package mmj.pa;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;

import mmj.lang.*;
import mmj.mmio.Tokenizer;
import mmj.pa.HighlightedDocument.DocumentReader;

public class WorksheetTokenizer {
    private static final int HEADER = 0, COMMENT = 1, STEP = 2, DJVARS = 3,
        PROOF = 4, FOOTER = 5;

    private int offset, index, lineType;
    private long lastLine;
    private Tokenizer tokenizer;
    private StringBuilder token;
    private final Deque<Token> tokenQueue;
    private final LogicalSystem logSys;

    /**
     * Returns the next token.
     * 
     * @return the next token
     * @throws IOException when an error occurred
     */
    public Token getNextToken() throws IOException {
        if (!tokenQueue.isEmpty())
            return tokenQueue.pop();
        if (lineType == FOOTER)
            return null;
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
        }
        if (lineBeginning) {
            t.initialState = true;
            if (s.equals(PaConstants.HEADER_STMT_TOKEN)) {
                lineType = HEADER;
                index = 0;
                t.type = PaConstants.PROOF_ASST_STYLE_KEYWORD;
            }
            else if (s.equals(PaConstants.DISTINCT_VARIABLES_STMT_TOKEN)) {
                lineType = DJVARS;
                t.type = PaConstants.PROOF_ASST_STYLE_KEYWORD;
            }
            else if (s.equals(PaConstants.GENERATED_PROOF_STMT_TOKEN)) {
                lineType = PROOF;
                t.type = PaConstants.PROOF_ASST_STYLE_KEYWORD;
            }
            else if (s.equals(PaConstants.FOOTER_STMT_TOKEN)) {
                lineType = FOOTER;
                t.type = PaConstants.PROOF_ASST_STYLE_KEYWORD;
            }
            else if (s.startsWith(PaConstants.COMMENT_STMT_TOKEN_PREFIX)) {
                lineType = COMMENT;
                t.type = PaConstants.PROOF_ASST_STYLE_COMMENT;
            }
            else {
                lineType = STEP;
                parseStepHypRef(s, t);
            }
        }
        else if (lineType == COMMENT)
            t.type = PaConstants.PROOF_ASST_STYLE_COMMENT;
        else if (lineType == HEADER)
            parseHeader(s, t);
        else if (lineType == PROOF)
            t.type = s.equals(PaConstants.END_PROOF_STMT_TOKEN) ? PaConstants.PROOF_ASST_STYLE_KEYWORD
                : PaConstants.PROOF_ASST_STYLE_PROOF;
        else if (lineType == DJVARS)
            parseSym(s, t);
        else if (lineType == STEP)
            if (s.matches("&[CWS]\\d+"))
                t.type = PaConstants.PROOF_ASST_STYLE_WORKVAR;
            else
                parseSym(s, t);
        else
            t.type = PaConstants.PROOF_ASST_STYLE_ERROR;
        return t;
    }

    private void parseSym(final String s, final Token t) {
        if (logSys == null)
            t.type = PaConstants.PROOF_ASST_STYLE_DEFAULT;
        else {
            final Sym sym = logSys.getSymTbl().get(s);
            if (sym == null)
                t.type = PaConstants.PROOF_ASST_STYLE_ERROR;
            else if (sym instanceof Var)
                t.type = ((Var)sym).getActiveVarHyp().getTyp().getId();
            else
                t.type = PaConstants.PROOF_ASST_STYLE_DEFAULT;
        }
    }

    private void parseHeader(final String s, final Token t) {
        switch (index++) {
            case 0:
                t.type = s.equals(PaConstants.HEADER_MM_TOKEN) ? PaConstants.PROOF_ASST_STYLE_KEYWORD
                    : PaConstants.PROOF_ASST_STYLE_ERROR;
                break;
            case 1:
                t.type = s.equals(PaConstants.HEADER_PROOF_ASST_TOKEN) ? PaConstants.PROOF_ASST_STYLE_KEYWORD
                    : PaConstants.PROOF_ASST_STYLE_ERROR;
                break;
            case 2:
                String pref = PaConstants.HEADER_THEOREM_EQUAL_PREFIX;
                if (s.startsWith(pref) && s.length() > pref.length()) {
                    t.type = PaConstants.PROOF_ASST_STYLE_KEYWORD;
                    final Token t2 = new Token();
                    t2.begin = t.begin + (t.length = pref.length());
                    t2.length = s.length() - t.length;
                    t2.type = PaConstants.PROOF_ASST_STYLE_REF;
                    tokenQueue.add(t2);
                }
                else
                    t.type = PaConstants.PROOF_ASST_STYLE_ERROR;
                break;
            case 3:
                pref = PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX;
                if (s.startsWith(pref)) {
                    t.type = PaConstants.PROOF_ASST_STYLE_KEYWORD;
                    t.length = pref.length();
                    final String s2 = s.substring(pref.length());
                    if (!s2.isEmpty()) {
                        final Token t2 = new Token();
                        t2.begin = t.begin + t.length;
                        t2.length = s2.length();
                        if (s2.equals(PaConstants.DEFAULT_STMT_LABEL))
                            t2.type = PaConstants.PROOF_ASST_STYLE_SPECIAL_STEP;
                        else if (logSys == null
                            || logSys.getStmtTbl().get(s2) != null)
                            t2.type = PaConstants.PROOF_ASST_STYLE_REF;
                        else
                            t2.type = PaConstants.PROOF_ASST_STYLE_ERROR;
                        tokenQueue.add(t2);
                    }
                }
                else
                    t.type = PaConstants.PROOF_ASST_STYLE_ERROR;
                break;
            default:
                t.type = PaConstants.PROOF_ASST_STYLE_ERROR;
        }
    }
    private void parseStepHypRef(final String s, final Token t) {
        final String[] fields = s.split("" + PaConstants.FIELD_DELIMITER_COLON,
            4);
        t.length = fields[0].length();
        boolean isHyp = false;
        Token t2 = t;
        if (fields[0].startsWith(PaConstants.AUTO_STEP_PREFIX)) {
            t2 = new Token();
            t2.begin = t.begin + 1;
            t2.length = t.length - 1;
            t.length = 1;
            t.type = PaConstants.PROOF_ASST_STYLE_SPECIAL_STEP;
            fields[0] = fields[0].substring(1);
            tokenQueue.add(t2);
        }
        if (fields[0].equals(PaConstants.DEFAULT_STMT_LABEL)
            || fields[0].contains("" + PaConstants.FIELD_DELIMITER_COMMA)
            || fields[0].contains(PaConstants.AUTO_STEP_PREFIX))
            t2.type = PaConstants.PROOF_ASST_STYLE_ERROR;
        else if (fields[0].equalsIgnoreCase(PaConstants.QED_STEP_NBR))
            t2.type = PaConstants.PROOF_ASST_STYLE_SPECIAL_STEP;
        else if (fields[0].startsWith(PaConstants.HYP_STEP_PREFIX)) {
            t2.type = PaConstants.PROOF_ASST_STYLE_SPECIAL_STEP;
            isHyp = true;
        }
        else
            t2.type = PaConstants.PROOF_ASST_STYLE_STEP;
        index = t2.begin + t2.length;
        if (fields.length > 1) {
            if (fields.length == 2) {
                if (parseRef(fields[1], isHyp))
                    return;
                index -= tokenQueue.removeLast().length;
            }
            for (String hyp : fields[1].split(""
                + PaConstants.FIELD_DELIMITER_COMMA, -1))
            {
                t2 = new Token();
                t2.begin = index++;
                t2.length = 1;
                t2.type = PaConstants.PROOF_ASST_STYLE_DEFAULT;
                tokenQueue.add(t2);

                t2 = new Token();
                t2.begin = index;
                if (isHyp) {
                    hyp = fields[1];
                    t2.type = PaConstants.PROOF_ASST_STYLE_ERROR;
                }
                else
                    t2.type = hyp.equals(PaConstants.DEFAULT_STMT_LABEL) ? PaConstants.PROOF_ASST_STYLE_SPECIAL_STEP
                        : PaConstants.PROOF_ASST_STYLE_HYP;
                index += t2.length = hyp.length();
                if (t2.length > 0)
                    tokenQueue.add(t2);
                if (isHyp)
                    break;
            }
            if (fields.length > 2) {
                parseRef(fields[2], isHyp);
                if (fields.length > 3) {
                    t2 = new Token();
                    t2.begin = index;
                    t2.length = fields[3].length() + 1;
                    t2.type = PaConstants.PROOF_ASST_STYLE_ERROR;
                    tokenQueue.add(t2);
                }
            }
        }
    }

    private boolean parseRef(final String field, final boolean isHyp) {
        Token t2 = new Token();
        t2.begin = index++;
        t2.length = 1;
        t2.type = PaConstants.PROOF_ASST_STYLE_DEFAULT;
        tokenQueue.add(t2);

        if (!field.isEmpty()) {
            t2 = new Token();
            t2.begin = index;
            index += t2.length = field.length();
            if (field.charAt(0) == PaConstants.LOCAL_REF_ESCAPE_CHAR)
                t2.type = PaConstants.PROOF_ASST_STYLE_LOCREF;
            else if (isHyp)
                t2.type = PaConstants.PROOF_ASST_STYLE_REF;
            else
                t2.type = logSys == null
                    || logSys.getStmtTbl().get(field) instanceof Assrt ? PaConstants.PROOF_ASST_STYLE_REF
                    : PaConstants.PROOF_ASST_STYLE_ERROR;
            tokenQueue.add(t2);
            return !(PaConstants.PROOF_ASST_STYLE_ERROR.equals(t2.type));
        }
        return true;
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
        lineType = -1;
        lastLine = 0;
        tokenQueue.clear();
    }

    public WorksheetTokenizer(final ProofAsst proofAsst,
        final DocumentReader reader)
    {
        logSys = proofAsst == null ? null : proofAsst.getLogicalSystem();
        tokenQueue = new ArrayDeque<>();
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
        public boolean initialState = false;
    }
}
