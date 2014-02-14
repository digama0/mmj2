package mmj.pa;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import javax.swing.text.BadLocationException;
import javax.swing.text.Position;

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

    /**
     * Keep a list of places in the file that it is safe to restart the
     * highlighting. This happens whenever the lexer reports that it has
     * returned to its initial state. Since this list needs to be sorted and we
     * need to be able to retrieve ranges from it, it is stored in a balanced
     * tree.
     */
    private final TreeSet<DocPosition> iniPositions = new TreeSet<DocPosition>();

    /**
     * As we go through and remove invalid positions we will also be finding new
     * valid positions. Since the position list cannot be deleted from and
     * written to at the same time, we will keep a list of the new positions and
     * simply add it to the list of positions once all the old positions have
     * been removed.
     */
    private final HashSet<DocPosition> newPositions = new HashSet<DocPosition>();

    /**
     * Vector that stores the communication between the two threads.
     */
    private final BlockingDeque<RecolorEvent> events = new LinkedBlockingDeque<RecolorEvent>();

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
     */
    public ColorThread(final HighlightedDocument doc) {
        super("ColorThread");
        document = new WeakReference<HighlightedDocument>(doc);
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
        if (!events.isEmpty())
            synchronized (events) {
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
                    if (position == curLast.position + curLast.adjustment) {
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
                    re = events.take();
                }
                processEvent(re.position, re.adjustment);
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

        DocPosition startRequest, endRequest;
        try {
            startRequest = new DocPosition(doc, position);
            endRequest = new DocPosition(doc, position + Math.abs(adjustment));
        } catch (final BadLocationException e) {
            e.printStackTrace();
            return;
        }
        DocPosition dp;
        DocPosition dpEnd = null;

        // find the starting position. We must start at least one
        // token before the current position
        DocPosition dpStart = iniPositions.lower(new DocPosition(position));
        if (dpStart == null)
            try {
                dpStart = new DocPosition(doc, 0);
            } catch (final BadLocationException e1) {
                // Shouldn't happen
            }

        // if stuff was removed, take any removed positions off the list.
        if (adjustment <= 0)
            iniPositions.subSet(startRequest, endRequest).clear();

        // adjust the positions of everything after the
        // insertion/removal.
        for (final DocPosition pos : iniPositions.tailSet(startRequest))
            pos.getOffset();

        // now go through and highlight as much as needed
        dp = iniPositions.ceiling(dpStart);
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
                syntaxLexer.reset(reader, dpStart.getOffset());
                // we will highlight tokens until we reach a good stopping
                // place.
                // the first obvious stopping place is the end of the document.
                // the lexer will return null at the end of the document and we
                // need to stop there.
                t = syntaxLexer.getNextToken();
            }
            newPositions.add(dpStart);
            final ArrayList<Token> line = new ArrayList<Token>();
            while (!done && t != null) {
                try {
                    dpEnd = new DocPosition(doc, t.begin);
                } catch (final BadLocationException e) {}
                if (t.length <= 0 || t.type == null || t.begin < 0)
                    new IllegalStateException(PaConstants.ERRMSG_TOKENIZER_FAIL)
                        .printStackTrace();
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
                    while (dp != null && dp.getOffset() <= t.begin)
                        if (dp.getOffset() == t.begin
                            && dp.compareTo(endRequest) >= 0)
                        {
                            // we have found a state that is the
                            // same
                            done = true;
                            dp = null;
                        }
                        else
                            // didn't find it, try again.
                            dp = iniPositions.higher(dp);
                    // so that we can do this check next time,
                    // record all the
                    // initial states from this time.
                    if (dpEnd != null)
                        newPositions.add(dpEnd);
                    writeTokens(doc, line, t.begin);
                    synchronized (this) {
                        notifyAll();
                    }
                }
                line.add(t);
                synchronized (docLock) {
                    t = syntaxLexer.getNextToken();
                }
            }
            if (t == null)
                writeTokens(doc, line, doc.getLength());

            // remove all the old initial positions from the place
            // where
            // we started doing the highlighting right up through
            // the last
            // bit of text we touched.
            iniPositions.subSet(dpStart, dpEnd).clear();

            // Remove all the positions that are after the end of
            // the file.:
            iniPositions.tailSet(new DocPosition(doc.getLength())).clear();

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

    private void writeTokens(final HighlightedDocument doc,
        final ArrayList<Token> tokens, final int lineEnd)
    {
        doc.writeTokens(tokens, change, lineEnd);
        if (lastPosition < blockCutoff && lineEnd >= blockCutoff)
            synchronized (this) {
                notifyAll();
            }
        lastPosition = lineEnd;
    }

    /**
     * A wrapper for a position in a document appropriate for storing in a
     * collection.
     */
    private static class DocPosition implements Comparable<DocPosition> {
        /** The position of a "lightweight" {@code DocPosition} */
        private int offset;
        /** The position of a "heavyweight" {@code DocPosition} */
        private final Position position;

        /**
         * Get the position represented by this DocPosition
         * 
         * @return the position
         */
        int getOffset() {
            return position == null ? offset : (offset = position.getOffset());
        }

        /**
         * Get the position represented by this DocPosition
         * 
         * @return the position
         */
        Position getPosition() {
            return position;
        }

        /**
         * Construct a DocPosition from the given offset into the document.
         * 
         * @param offs The position this DocPosition will represent
         */
        public DocPosition(final int offs) {
            offset = offs;
            position = null;
        }

        /**
         * Construct a DocPosition from the given Position object.
         * 
         * @param doc The document from which to base the position
         * @param offs The position this DocPosition will represent
         * @throws BadLocationException for an incorrect offset
         */
        public DocPosition(final HighlightedDocument doc, final int offs)
            throws BadLocationException
        {
            position = doc.createPosition(offset = offs);
        }

        /**
         * Two DocPositions are equal iff they have the same internal position.
         * 
         * @return if this DocPosition represents the same position as another.
         */
        @Override
        public boolean equals(final Object obj) {
            return compareTo((DocPosition)obj) == 0;
        }

        /**
         * A string representation useful for debugging.
         * 
         * @return A string representing the position.
         */
        @Override
        public String toString() {
            return "" + getOffset();
        }

        public int compareTo(final DocPosition other) {
            return offset - other.offset;
        }
    }
}
