package mmj.pa;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;

import mmj.pa.HighlightedDocument.DocumentReader;
import mmj.pa.WorksheetTokenizer.Token;

/**
 * Run the Syntax Highlighting as a separate thread. Things that need to be
 * colored are messaged to the thread and put in a list.
 */
class ColorThread extends Thread {
    /**
     * A simple wrapper representing something that needs to be colored. Placed
     * into an object so that it can be stored in a Vector.
     */
    private static class RecolorEvent {
        public int position;
        public int adjustment;

        public RecolorEvent(final int position, final int adjustment) {
            this.position = position;
            this.adjustment = adjustment;
        }
    }

    /**
     * Stores the document we are coloring. We use a WeakReference so that the
     * document is eligible for garbage collection when it is no longer being
     * used. At that point, this thread will shut down itself.
     */
    private final WeakReference<HighlightedDocument> document;

    private final ProofAsstPreferences preferences;

    /**
     * Keep a list of places in the file that it is safe to restart the
     * highlighting. This happens whenever the lexer reports that it has
     * returned to its initial state. Since this list needs to be sorted and we
     * need to be able to retrieve ranges from it, it is stored in a balanced
     * tree.
     */
    private final TreeSet<DocPosition> iniPositions = new TreeSet<>();

    /**
     * As we go through and remove invalid positions we will also be finding new
     * valid positions. Since the position list cannot be deleted from and
     * written to at the same time, we will keep a list of the new positions and
     * simply add it to the list of positions once all the old positions have
     * been removed.
     */
    private final HashSet<DocPosition> newPositions = new HashSet<>();

    /**
     * Vector that stores the communication between the two threads.
     */
    private volatile Deque<RecolorEvent> events = new ArrayDeque<>();

    private final Object docLock = new Object();

    /**
     * The amount of change that has occurred before the place in the document
     * that we are currently highlighting (lastPosition).
     */
    private volatile int change = 0;

    /**
     * The last position colored
     */
    private volatile int lastPosition = -1;

    private volatile int blockCutoff = -2;

    /**
     * Creates the coloring thread for the given document.
     *
     * @param doc The document to be colored.
     * @param prefs the preferences class
     */
    public ColorThread(final HighlightedDocument doc,
        final ProofAsstPreferences prefs)
    {
        super("ColorThread");
        document = new WeakReference<>(doc);
        preferences = prefs;
        start();
    }

    /**
     * Tell the Syntax Highlighting thread to take another look at this section
     * of the document. It will process this as a FIFO. This method should be
     * done inside a docLock.
     *
     * @param position The location of the change
     * @param adjustment The amount of text added
     */
    public void color(final int position, final int adjustment) {
        // figure out if this adjustment effects the current run.
        // if it does, then adjust the place in the document
        // that gets highlighted.
        if (position < lastPosition)
            if (lastPosition < position - adjustment)
                change -= lastPosition - position;
            else
                change += adjustment;
        synchronized (events) {
            if (!events.isEmpty()) {
                // check whether to coalesce with current last element
                final RecolorEvent curLast = events.getLast();
                if (adjustment < 0 && curLast.adjustment < 0) {
                    // both are removals
                    if (position == curLast.position) {
                        curLast.adjustment += adjustment;
                        return;
                    }
                }
                else if (adjustment >= 0 && curLast.adjustment >= 0)
                    // both are insertions
                    if (position == curLast.position + curLast.adjustment)
                    {
                        curLast.adjustment += adjustment;
                        return;
                    }
                    else if (curLast.position == position + adjustment) {
                        curLast.position = position;
                        curLast.adjustment += adjustment;
                        return;
                    }
            }
            events.add(new RecolorEvent(position, adjustment));
            events.notifyAll();
        }
    }

    public void block(final int blockUntil) {
        synchronized (this) {
            blockCutoff = blockUntil;
            while (!events.isEmpty() && lastPosition < blockUntil)
                try {
                    wait();
                } catch (final InterruptedException e) {}
            blockCutoff = -2;
        }
    }

    /**
     * The colorer runs forever and may sleep for long periods of time. It
     * should be interrupted every time there is something for it to do.
     */
    @Override
    public void run() {
        while (document.get() != null)
            try {
                final RecolorEvent re;
                synchronized (events) {
                    // get the next event to process - stalling until the
                    // event becomes available
                    while (events.isEmpty() && document.get() != null) {
                        // stop waiting after a second in case document
                        // has been cleared.
                        synchronized (this) {
                            notifyAll();
                        }
                        events.wait(1000);
                    }
                    re = events.removeFirst();
                }
                processEvent(re.position, re.adjustment);
                Thread.sleep(100);
            } catch (final InterruptedException e) {}
    }

    private void processEvent(final int position, final int adjustment) {
        final HighlightedDocument doc = document.get();
        if (doc == null)
            return;

        // slurp everything up into local variables in case another
        // thread changes them during coloring process
        final DocumentReader reader = doc.getDocumentReader();
        final WorksheetTokenizer syntaxLexer = doc.getTokenizer();

        SortedSet<DocPosition> workingSet;
        Iterator<DocPosition> workingIt;
        final DocPosition startRequest = new DocPosition(position);
        final DocPosition endRequest = new DocPosition(
            position + Math.abs(adjustment));
        DocPosition dp;
        DocPosition dpStart = null;
        DocPosition dpEnd = null;

        // find the starting position. We must start at least one
        // token before the current position
        try {
            // all the good positions before
            workingSet = iniPositions.headSet(startRequest);
            // the last of the stuff before
            dpStart = workingSet.last();
        } catch (final NoSuchElementException x) {
            // if there were no good positions before the requested
            // start,
            // we can always start at the very beginning.
            dpStart = new DocPosition(0);
        }

        // if stuff was removed, take any removed positions off the
        // list.
        if (adjustment < 0) {
            workingSet = iniPositions.subSet(startRequest, endRequest);
            workingIt = workingSet.iterator();
            while (workingIt.hasNext()) {
                workingIt.next();
                workingIt.remove();
            }
        }

        // adjust the positions of everything after the
        // insertion/removal.
        for (final DocPosition pos : iniPositions.tailSet(startRequest))
            pos.adjustPosition(adjustment);

        // now go through and highlight as much as needed
        workingSet = iniPositions.tailSet(dpStart);
        workingIt = workingSet.iterator();
        dp = null;
        if (workingIt.hasNext())
            dp = workingIt.next();
        try {
            Token t;
            boolean done = false;
            dpEnd = dpStart;
            synchronized (docLock) {
                // After the lexer has been set up, scroll the reader so that it
                // is in the correct spot as well.
                reader.seek(dpStart.getPosition());
                // we are playing some games with the lexer for efficiency.
                // we could just create a new lexer each time here, but instead,
                // we will just reset it so that it thinks it is starting at the
                // beginning of the document but reporting a funny start
                // position. Reseting the lexer causes the close() method on the
                // reader to be called but because the close() method has no
                // effect on the DocumentReader, we can do this.
                syntaxLexer.reset(reader, dpStart.getPosition());
                // we will highlight tokens until we reach a good stopping
                // place.
                // the first obvious stopping place is the end of the document.
                // the lexer will return null at the end of the document and we
                // need to stop there.
                t = syntaxLexer.getNextToken();
            }
            newPositions.add(dpStart);
            while (!done && t != null) {
                // this is the actual command that colors the stuff.
                // Color stuff with the description of the styles
                // stored in tokenStyles.
                final int end = t.begin + t.length;
                if (end <= doc.getLength()) {
                    if (t.length < 0 || t.type == null || t.begin < 0)
                        new ProofAsstException(
                            PaConstants.ERRMSG_TOKENIZER_FAIL)
                                .printStackTrace();
                    else
                        try {
                            doc.setCharacterAttributes(t.begin + change,
                                t.length,
                                preferences.getHighlightingStyle(t.type), true);
                        } catch (final RuntimeException e) {
                            System.err.println("Ignoring exception:");
                            e.printStackTrace();
                        }
                    // record the position of the last bit of
                    // text that we colored
                    dpEnd = new DocPosition(t.begin);
                }
                if (lastPosition < blockCutoff && end + change >= blockCutoff)
                    synchronized (this) {
                        notifyAll();
                    }
                lastPosition = end + change;
                // The other more complicated reason for doing no
                // more highlighting
                // is that all the colors are the same from here on
                // out anyway.
                // We can detect this by seeing if the place that
                // the lexer returned
                // to the initial state last time we highlighted is
                // the same as the
                // place that returned to the initial state this
                // time.
                // As long as that place is after the last changed
                // text, everything
                // from there on is fine already.
                if (t.initialState) {
                    // look at all the positions from last time that
                    // are less than or
                    // equal to the current position
                    while (dp != null && dp.getPosition() <= t.begin)
                        if (dp.getPosition() == t.begin
                            && dp.getPosition() >= endRequest.getPosition())
                        {
                            // we have found a state that is the
                            // same
                            done = true;
                            dp = null;
                        }
                        else if (workingIt.hasNext())
                            // didn't find it, try again.
                            dp = workingIt.next();
                        else
                            // didn't find it, and there is no more
                            // info from last
                            // time. This means that we will just
                            // continue
                            // until the end of the document.
                            dp = null;
                    // so that we can do this check next time,
                    // record all the
                    // initial states from this time.
                    newPositions.add(dpEnd);
                }
                synchronized (docLock) {
                    t = syntaxLexer.getNextToken();
                }
            }

            // remove all the old initial positions from the place
            // where
            // we started doing the highlighting right up through
            // the last
            // bit of text we touched.
            workingIt = iniPositions.subSet(dpStart, dpEnd).iterator();
            while (workingIt.hasNext()) {
                workingIt.next();
                workingIt.remove();
            }

            // Remove all the positions that are after the end of
            // the file.:
            workingIt = iniPositions.tailSet(new DocPosition(doc.getLength()))
                .iterator();
            while (workingIt.hasNext()) {
                workingIt.next();
                workingIt.remove();
            }

            // and put the new initial positions that we have found
            // on the list.
            iniPositions.addAll(newPositions);
            newPositions.clear();
        } catch (final IOException x) {}
        synchronized (docLock) {
            lastPosition = -1;
            change = 0;
        }
    }

    /**
     * A wrapper for a position in a document appropriate for storing in a
     * collection.
     */
    private class DocPosition implements Comparable<DocPosition> {
        /**
         * The actual position
         */
        private int position;

        /**
         * Get the position represented by this DocPosition
         *
         * @return the position
         */
        int getPosition() {
            return position;
        }

        /**
         * Construct a DocPosition from the given offset into the document.
         *
         * @param position The position this DocObject will represent
         */
        public DocPosition(final int position) {
            this.position = position;
        }

        /**
         * Adjust this position. This is useful in cases that an amount of text
         * is inserted or removed before this position.
         *
         * @param adjustment amount (either positive or negative) to adjust this
         *            position.
         * @return the DocPosition, adjusted properly.
         */
        public DocPosition adjustPosition(final int adjustment) {
            position += adjustment;
            return this;
        }

        /**
         * Two DocPositions are equal iff they have the same internal position.
         *
         * @return if this DocPosition represents the same position as another.
         */
        @Override
        public boolean equals(final Object obj) {
            return obj instanceof DocPosition
                && position == ((DocPosition)obj).position;
        }
        /**
         * A string representation useful for debugging.
         *
         * @return A string representing the position.
         */
        @Override
        public String toString() {
            return "" + position;
        }

        public int compareTo(final DocPosition other) {
            return position - other.position;
        }
    }
}
