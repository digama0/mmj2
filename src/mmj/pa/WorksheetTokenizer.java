package mmj.pa;

import java.io.IOException;

import mmj.mmio.Tokenizer;
import mmj.pa.HighlightedDocument.DocumentReader;

public class WorksheetTokenizer {
    private int offset;
    private long lastLine;
    private Tokenizer tokenizer;
    private StringBuilder token;
    private boolean inComment;

    /**
     * Returns the next token.
     * 
     * @return the next token
     * @throws IOException when an error occurred
     */
    public Token getNextToken() throws IOException {
        token.setLength(0);
        if (tokenizer.getToken(token, 0) == -1)
            return null;
        final String s = token.toString();

        final Token t = new Token();
        t.length = token.length();
        t.begin = offset + (int)tokenizer.getCurrentCharNbr() - t.length;
        final boolean firstOfLine = tokenizer.getCurrentLineNbr() != lastLine;
        boolean lineBeginning = false;
        if (firstOfLine) {
            lastLine = tokenizer.getCurrentLineNbr();
            lineBeginning = tokenizer.getCurrentColumnNbr() == t.length;
            if (lineBeginning) {
                t.initialState = true;
                inComment = s.startsWith("*");
            }
        }

        if (inComment)
            t.type = PaConstants.PROOF_ASST_STYLE_COMMENT;
        else if (lineBeginning && s.matches("[^$ ].*"))
            t.type = PaConstants.PROOF_ASST_STYLE_SHR;
        else if (s.matches("[a-z]"))
            t.type = PaConstants.PROOF_ASST_STYLE_SET;
        else if (s.matches("[A-Z]"))
            t.type = PaConstants.PROOF_ASST_STYLE_CLASS;
        else if (s.matches("ph|ps|ch|th|ta|et|ze|si|rh|mu|la|ka"))
            t.type = PaConstants.PROOF_ASST_STYLE_WFF;
        else if (s.matches("&[CWS]\\d+"))
            t.type = PaConstants.PROOF_ASST_STYLE_WORKVAR;
        else
            t.type = PaConstants.PROOF_ASST_STYLE_DEFAULT;
        return t;
    }
    /**
     * Closes the current input stream, and resets the scanner to read from a
     * new input stream. All internal variables are reset, the old input stream
     * cannot be reused (content of the internal buffer is discarded and lost).
     * The lexical state is set to the initial state. Subsequent tokens read
     * from the lexer will start with the line, char, and column values given
     * here.
     * 
     * @param reader The new input.
     * @param ch The position (relative to the start of the stream) of the first
     *            token.
     * @throws IOException If an error occurs
     */
    public void reset(final DocumentReader reader, final int ch)
        throws IOException
    {
        tokenizer = new Tokenizer(reader, "");
        offset = ch;
        token = new StringBuilder();
        inComment = false;
        lastLine = 0;
    }

    public WorksheetTokenizer(final DocumentReader reader) {
        try {
            reset(reader, 0);
        } catch (final IOException e) {
            throw new RuntimeException("IOException in WorksheetTokenizer");
        }
    }

    public static class Token {
        public int begin;
        public int length;
        public String type;
        public boolean initialState;
    }
}
