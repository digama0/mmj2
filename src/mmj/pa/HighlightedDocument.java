package mmj.pa;

import java.awt.Color;
import java.awt.Point;
import java.io.Reader;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.DocumentEvent.EventType;
import javax.swing.text.*;

import mmj.pa.WorksheetTokenizer.Token;

public class HighlightedDocument extends DefaultStyledDocument {
    DocumentReader reader;
    private final ColorThread colorer;
    private final WorksheetTokenizer tokenizer;
    private boolean programmatic;
    private boolean changed;
    private int lastCaret;
    private final JTextPane textPane;

    public HighlightedDocument(final ProofAsst proofAsst,
        final ProofAsstPreferences prefs)
    {
        super(new GapContent(DefaultStyledDocument.BUFFER_SIZE_DEFAULT), prefs
            .getStyleContext());

        programmatic = changed = false;
        if (prefs.getHighlightingEnabled()) {
            colorer = new ColorThread(this);
            reader = new DocumentReader();
            tokenizer = new WorksheetTokenizer(proofAsst, reader);
        }
        else {
            colorer = null;
            reader = null;
            tokenizer = null;
        }

        final UIDefaults defs = UIManager.getDefaults();
        final Color bg = prefs.getBackgroundColor();
        defs.put("TextPane[Enabled].backgroundPainter", bg);
        defs.put("TextPane.background", bg);
        defs.put("TextPane.inactiveBackground", bg);
        if (prefs.getLineWrap())
            textPane = new JTextPane(this);
        else
            textPane = new JTextPane(this) {
                @Override
                public boolean getScrollableTracksViewportWidth() {
                    return getUI().getPreferredSize(this).width <= getParent()
                        .getSize().width;
                }
            };
        textPane.putClientProperty("Nimbus.Overrides", defs);

        textPane.setForeground(prefs.getForegroundColor());
        textPane.setCaretColor(prefs.getForegroundColor());
        writeLock();
        final SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(style, prefs.getLineSpacing());
        ((MutableAttributeSet)getDefaultRootElement().getAttributes())
            .addAttributes(style);
        writeUnlock();
    }
    /**
     * Color or recolor the entire document
     */
    public void colorAll() {
        color(0, getLength());
    }

    /**
     * Color a section of the document. The actual coloring will start somewhere
     * before the requested position and continue as long as needed.
     * 
     * @param position the starting point for the coloring.
     * @param adjustment amount of text inserted or removed at the starting
     *            point.
     */
    public void color(final int position, final int adjustment) {
        if (colorer != null)
            colorer.color(position, adjustment);
    }

    @Override
    protected void insertUpdate(final DefaultDocumentEvent chng,
        final AttributeSet attr)
    {
        changed |= !programmatic;
        lastCaret = textPane.getCaretPosition();
        /*
                final int offset = chng.getOffset();
                final int length = chng.getLength();
                if (attr == null)
                    attr = SimpleAttributeSet.EMPTY;

                try {
                    final Segment s = new Segment();
                    getText(offset, length, s);

                    for (char c = s.first(); c != Segment.DONE; c = s.next())
                        if (c == '\n') {
                            final int pos = s.getIndex() + offset + 1;
                            final BranchElement g = (BranchElement)getGroupElement(pos);
                            final int pIndex = g.getElementIndex(pos);
                            final BranchElement p = (BranchElement)g.getElement(pIndex);
                            final int lIndex = p.getElementIndex(pos);
                            final Element l = p.getElement(lIndex);

                            Element[] buf = new Element[p.getChildCount() - lIndex];
                            buf[0] = new LeafElement(p, l.getAttributes(), pos,
                                l.getEndOffset());
                            for (int i = 1; i < buf.length; i++)
                                buf[i] = p.getElement(lIndex + i);
                            final BranchElement pRight = new BranchElement(g,
                                p.getAttributes());
                            pRight.replace(0, 0, buf);

                            buf = new Element[]{new LeafElement(p, l.getAttributes(),
                                l.getStartOffset(), pos)};
                            replace(chng, p, lIndex, p.getChildCount() - lIndex, buf);

                            buf = new Element[]{pRight};
                            replace(chng, g, pIndex + 1, 0, buf);

                            root.dump(System.err, 0);
                        }
                } catch (final BadLocationException e) {}*/
        super.insertUpdate(chng, attr);
    }

    @Override
    public void remove(final int offs, final int len)
        throws BadLocationException
    {
        changed |= !programmatic;
        lastCaret = textPane.getCaretPosition();
        if (len > 0)
            super.remove(offs, len);
    }

    @Override
    protected void fireInsertUpdate(final DocumentEvent e) {
        // ((AbstractElement)getDefaultRootElement()).dump(System.out, 0);
        super.fireInsertUpdate(e);
        color(e.getOffset(), e.getLength());
    }

    @Override
    protected void fireRemoveUpdate(final DocumentEvent e) {
        super.fireRemoveUpdate(e);
        color(e.getOffset(), -e.getLength());
    }

    public void writeTokens(final List<Token> tokens, final int change,
        final int lineEnd)
    {
        if (tokens.isEmpty())
            return;
        final AbstractElement root = (AbstractElement)getDefaultRootElement();
        int index = tokens.get(0).begin + change;
        final int lastIndex = (lineEnd == root.getEndOffset() ? 1 : 0)
            + root.getElementIndex(lineEnd);
        try {
            writeLock();
            for (int pIndex = root.getElementIndex(index); pIndex < lastIndex; pIndex++)
            {
                final BranchElement p = (BranchElement)root.getElement(pIndex);
                final List<Element> content = new ArrayList<Element>();
                final Iterator<Token> it = tokens.iterator();
                while (it.hasNext()) {
                    final Token t = it.next();
                    it.remove();
                    if (t.type.equals(PaConstants.PROOF_ASST_STYLE_DEFAULT))
                        continue;
                    if (p.getEndOffset() <= t.begin + change)
                        break;
                    if (index < t.begin + change)
                        content.add(new LeafElement(p, null, index,
                            index = t.begin + change));
                    content.add(new LeafElement(p, getStyle(t.type), index,
                        index += t.length));
                }
                content.add(new LeafElement(p, null, index, index = p
                    .getEndOffset()));
                final Element[] removed = new Element[p.getElementCount()];
                for (int i = 0; i < removed.length; i++)
                    removed[i] = p.getElement(i);
                final DefaultDocumentEvent changes = new DefaultDocumentEvent(
                    p.getStartOffset(), index, EventType.CHANGE)
                {
                    @Override
                    public boolean isSignificant() {
                        return false;
                    }
                };

                final Element[] added = content.toArray(new Element[content
                    .size()]);
                p.replace(0, removed.length, added);
                changes.addEdit(new ElementEdit(p, 0, removed, added));
                changes.end();
                fireChangedUpdate(changes);
                fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
            }
        } finally {
            writeUnlock();
        }

        // root.dump(System.out, 0);

        /*
        try {
            writeLock();
            for (final Iterator<Token> i = tokens.iterator(); i.hasNext();) {
                final Token t = i.next();
                i.remove();
                // this is the actual command that colors the stuff.
                // Color stuff with the description of the styles
                // stored in tokenStyles.
                final int end = t.begin + t.length;
                if (end <= getLength()) {
                    // record the position of the last bit of
                    // text that we colored
                    final int offset = t.begin + change;

                    final DefaultDocumentEvent changes = new DefaultDocumentEvent(
                        offset, t.length, EventType.CHANGE)
                    {
                        @Override
                        public boolean isSignificant() {
                            return false;
                        }
                    };
                    // split elements that need it
                    buffer.change(offset, t.length, changes);

                    final AttributeSet s = getStyle(t.type);

                    // PENDING(prinz) - this isn't a very efficient way to
                    // iterate
                    int lastEnd;
                    for (int pos = offset; pos < offset + t.length; pos = lastEnd)
                    {
                        final Element run = getCharacterElement(pos);
                        lastEnd = run.getEndOffset();
                        if (pos == lastEnd)
                            // offset + length beyond length of document,
                            // bail.
                            break;
                        final MutableAttributeSet attr = (MutableAttributeSet)run
                            .getAttributes();
                        changes
                            .addEdit(new AttributeUndoableEdit(run, s, true));
                        attr.removeAttributes(attr);
                        if (t.type != PaConstants.PROOF_ASST_STYLE_DEFAULT)
                            attr.addAttributes(s);
                    }
                    changes.end();
                    fireChangedUpdate(changes);
                    fireUndoableEditUpdate(new UndoableEditEvent(this, changes));

                    // ((AbstractElement)getDefaultRootElement()).dump(
                    // System.err, 0);
                }
            }
        } finally {
            writeUnlock();
        }*/
    }
    public DocumentReader getDocumentReader() {
        return reader;
    }

    public WorksheetTokenizer getTokenizer() {
        return tokenizer;
    }

    public JTextPane getTextPane() {
        return textPane;
    }

    public int getLastCaretPosition() {
        return lastCaret;
    }

    public boolean isProgrammatic() {
        return programmatic;
    }

    public boolean isChanged() {
        return changed;
    }

    public void clearChanged() {
        changed = false;
    }

    public void setTextProgrammatic(final String text, final Point blockUntil,
        final boolean smart, final boolean reset)
    {
        programmatic = true;
        try {
            final String replacement = text.replace("\r\n", "\n");
            final int length = getLength();
            if (smart) {
                final String current = getText(0, length);
                int begin = 0;
                while (begin < length
                    && current.charAt(begin) == replacement.charAt(begin))
                    begin++;
                int end = length, end2 = replacement.length();
                while (end > begin && end2 > begin
                    && current.charAt(end - 1) == replacement.charAt(end2 - 1))
                {
                    end--;
                    end2--;
                }
                if (end != begin)
                    remove(begin, end - begin);
                if (end2 != begin)
                    insertString(begin, replacement.substring(begin, end2),
                        new SimpleAttributeSet());
            }
            else {
                if (length != 0)
                    remove(0, length);
                if (!replacement.isEmpty())
                    insertString(0, replacement, new SimpleAttributeSet());
            }
        } catch (final BadLocationException e) {}
        if (colorer != null)
            colorer.block(blockUntil == null ? Integer.MAX_VALUE : textPane
                .viewToModel(blockUntil));
        programmatic = false;
        if (reset)
            clearChanged();
    }
    public int getLineStartOffset(final int row) {
        return getDefaultRootElement().getElement(row).getStartOffset();
    }

    public class DocumentReader extends Reader implements Readable {
        /**
         * Current position in the document. Incremented whenever a character is
         * read.
         */

        private Position position;
        {
            try {
                position = createPosition(0);
            } catch (final BadLocationException e) {}
        }

        private final Segment s = new Segment();

        /** Saved position used in the mark and reset methods. */
        private Position mark = null;

        /**
         * Has no effect. This reader can be used even after it has been closed.
         */
        @Override
        public void close() {}

        /**
         * Save a position for reset.
         * 
         * @param readAheadLimit ignored.
         */
        @Override
        public void mark(final int readAheadLimit) {
            mark = position;
        }

        /**
         * This reader supports mark and reset.
         * 
         * @return true
         */
        @Override
        public boolean markSupported() {
            return true;
        }

        /**
         * Read a single character.
         * 
         * @return the character or -1 if the end of the document has been
         *         reached.
         */
        @Override
        public int read() {
            final char[] buf = new char[1];
            return read(buf) == -1 ? -1 : buf[0];
        }
        /**
         * Read and fill the buffer. This method will always fill the buffer
         * unless the end of the document is reached.
         * 
         * @param cbuf the buffer to fill.
         * @return the number of characters read or -1 if no more characters are
         *         available in the document.
         */
        @Override
        public int read(final char[] cbuf) {
            return read(cbuf, 0, cbuf.length);
        }

        /**
         * Read and fill the buffer. This method will always fill the buffer
         * unless the end of the document is reached.
         * 
         * @param cbuf the buffer to fill.
         * @param off offset into the buffer to begin the fill.
         * @param len maximum number of characters to put in the buffer.
         * @return the number of characters read or -1 if no more characters are
         *         available in the document.
         */
        @Override
        public int read(final char[] cbuf, final int off, int len) {
            final int pos = getPosition();
            if (pos + len >= getLength())
                len = getLength() - pos;
            if (len == 0)
                return -1;
            try {
                getContent().getChars(pos, len, s);
                seek(pos + len);
            } catch (final BadLocationException e) {
                // Shouldn't happen
            }
            System.arraycopy(s.array, s.offset, cbuf, off, len);
            return len;
        }
        /**
         * @return true
         */
        @Override
        public boolean ready() {
            return true;
        }

        /**
         * Reset this reader to the last mark, or the beginning of the document
         * if a mark has not been set.
         */
        @Override
        public void reset() {
            if (mark == null)
                try {
                    seek(0);
                } catch (final BadLocationException e) {
                    // Shouldn't happen
                }
            else
                position = mark;
            mark = null;
        }

        /**
         * Skip characters of input. This method will always skip the maximum
         * number of characters unless the end of the file is reached.
         * 
         * @param n number of characters to skip.
         * @return the actual number of characters skipped.
         */
        @Override
        public long skip(final long n) {
            final int pos = getPosition();
            int end = (int)(pos + n);
            if (end > getLength())
                end = getLength();
            try {
                seek(end);
            } catch (final BadLocationException e) {
                // Shouldn't happen
            }
            return end - pos;
        }

        /**
         * Seek to the given position in the document.
         * 
         * @param n the offset to which to seek.
         * @throws BadLocationException for an invalid offset
         */
        public void seek(final int n) throws BadLocationException {
            position = getContent().createPosition(n);
        }

        /**
         * Seek to the given position in the document.
         * 
         * @param p the position to which to seek.
         */
        public void seek(final Position p) {
            position = p;
        }

        public int getPosition() {
            return position.getOffset();
        }
    }
}
