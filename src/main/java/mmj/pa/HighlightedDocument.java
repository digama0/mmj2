package mmj.pa;

import java.awt.Color;
import java.awt.Point;
import java.io.Reader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

public class HighlightedDocument extends DefaultStyledDocument {
    private DocumentReader reader;
    private final ColorThread colorer;
    private final WorksheetTokenizer tokenizer;
    private boolean programmatic;
    private boolean changed;
    private int lastCaret;
    private final JTextPane textPane;

    public HighlightedDocument(final ProofAsst proofAsst,
        final ProofAsstPreferences prefs)
    {
        programmatic = changed = false;
        if (prefs.highlightingEnabled.get()) {
            colorer = new ColorThread(this, prefs);
            reader = new DocumentReader();
            tokenizer = new WorksheetTokenizer(proofAsst, reader);
        }
        else {
            colorer = null;
            reader = null;
            tokenizer = null;
        }

        final UIDefaults defs = UIManager.getDefaults();
        final Color bg = prefs.backgroundColor.get();
        defs.put("TextPane[Enabled].backgroundPainter", bg);
        defs.put("TextPane.background", bg);
        defs.put("TextPane.inactiveBackground", bg);
        if (prefs.tmffPreferences.lineWrap.get())
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

        textPane.setForeground(prefs.foregroundColor.get());
        textPane.setCaretColor(prefs.foregroundColor.get());
        final SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setLineSpacing(style, prefs.lineSpacing.get());
        setParagraphAttributes(0, getLength(), style, false);
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

    /** Intercept inserts and removes to color them. */
    @Override
    public void insertString(final int offs, final String str,
        final AttributeSet a) throws BadLocationException
    {
        changed |= !programmatic;
        lastCaret = textPane.getCaretPosition();
        synchronized (this) {
            super.insertString(offs, str, a);
            if (reader != null)
                reader.update(offs, str.length());
        }
    }

    @Override
    public void remove(final int offs, final int len)
        throws BadLocationException
    {
        changed |= !programmatic;
        lastCaret = textPane.getCaretPosition();
        if (len > 0) {
            synchronized (this) {
                super.remove(offs, len);
                if (reader != null)
                    reader.update(offs, -len);
            }
        }
    }

    @Override
    public void getText(final int offset, int length, final Segment txt)
        throws BadLocationException
    {
        if (length < 0)
            length = 0;
        super.getText(offset, length, txt);
    }

    @Override
    protected void fireInsertUpdate(final DocumentEvent e) {
        super.fireInsertUpdate(e);
        color(e.getOffset(), e.getLength());
    }

    @Override
    protected void fireRemoveUpdate(final DocumentEvent e) {
        super.fireRemoveUpdate(e);
        color(e.getOffset(), -e.getLength());
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
                while (end > begin && end2 > begin && current
                    .charAt(end - 1) == replacement.charAt(end2 - 1))
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
            colorer.block(blockUntil == null ? Integer.MAX_VALUE
                : textPane.viewToModel(blockUntil));
        programmatic = false;
        if (reset)
            clearChanged();
    }
    public int getLineStartOffset(final int row) {
        return getDefaultRootElement().getElement(row).getStartOffset();
    }

    public int getLineOfOffset(final int i) {
        return getDefaultRootElement().getElementIndex(i);
    }

    public int getLineCount() {
        return getDefaultRootElement().getElementCount();
    }

    public class DocumentReader extends Reader implements Readable {
        /**
         * Updates the reader to reflect a change in the underlying model.
         *
         * @param pos the location of the insert/delete
         * @param adjustment the number of characters added/deleted
         */
        public void update(final int pos, final int adjustment) {
            if (pos < position)
                if (position < pos - adjustment)
                    position = pos;
                else
                    position += adjustment;
        }

        /**
         * Current position in the document. Incremented whenever a character is
         * read.
         */
        private int position = 0;

        /** Saved position used in the mark and reset methods. */
        private int mark = -1;

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
            if (position < getLength())
                try {
                    final char c = getText(position, 1).charAt(0);
                    position++;
                    return c;
                } catch (final BadLocationException x) {
                    return -1;
                }
            else
                return -1;
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
        public int read(final char[] cbuf, final int off, final int len) {
            if (position < getLength()) {
                int length = len;
                if (position + length >= getLength())
                    length = getLength() - position;
                if (off + length >= cbuf.length)
                    length = cbuf.length - off;
                try {
                    final String s = getText(position, length);
                    position += length;
                    for (int i = 0; i < length; i++)
                        cbuf[off + i] = s.charAt(i);
                    return length;
                } catch (final BadLocationException x) {
                    return -1;
                }
            }
            else
                return -1;
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
            if (mark == -1)
                position = 0;
            else
                position = mark;
            mark = -1;
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
            if (position + n <= getLength()) {
                position += n;
                return n;
            }
            else {
                final long oldPos = position;
                position = getLength();
                return getLength() - oldPos;
            }
        }

        /**
         * Seek to the given position in the document.
         *
         * @param n the offset to which to seek.
         */
        public void seek(final long n) {
            position = n <= getLength() ? (int)n : getLength();
        }

        public int getPosition() {
            return position;
        }
    }
}
