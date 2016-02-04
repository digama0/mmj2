//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Statementizer.java  0.08 1/01/2012
 *
 * Dec-22-2006
 * --> added charNbr to MMIOException
 * --> made areLabelCharsValid(String s) static and created a
 *    static boolean isLabelOnProhibitedList(String s) for
 *    use in mmj.pa.ProofWorksheet.java.
 *
 * Apr-01-2006 -- Version 0.04:
 * --> Added compressed proof capability
 *
 * Nov-01-2006 -- Version 0.05:
 * --> Modified to include immediately preceding Metamath comment
 *    text, if such a $( comment command exists, to the
 *    SrcStmt.comment field of a Theorem's SrcStmt. This
 *    will be available for display on the mmj2 Proof Assistant.
 *
 * Aug-01-2007 -- Version 0.06:
 * --> Made isValidMathSymbol() static and public for use in
 *    mmj.lang.WorkVarManager.
 *
 * Aug-01-2008 -- Version 0.07:
 * --> Modified getAxiomaticAssrtSrcStmt() to load the previous
 *    comment into the SrcStmt of axioms (previously only
 *    theorem comments were hoovered up.)
 * --> Added static getTitleIfApplicable(), bypassWhitespace()
 *    and bypassNonWhitespace() methods for use in pulling
 *    Chapter and Section titles out of Metamath comments.
 *    The bypassXXX() modules are general and could be
 *    used in non-BookManager/Chapter/Section endeavors...
 * --> added a close(), getTokenizer() and getSource() functions.
 *
 * Version 0.08 - Nov-01-2011:
 *    - optimize getTitleIfApplicable()
 */

package mmj.mmio;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

import mmj.pa.ErrorCode;

/**
 * Load input MetaMath tokens for a single Metamath statement into a "work"
 * structure, {@code SrcStmt}.
 * <p>
 * (This is actually a somewhat inefficient intermediate step on the way to
 * loading the source into memory, but is done this way to simplify the main
 * code and to improve testability.)
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class Statementizer implements Closeable {

    private Tokenizer tokenizer = null;

    private int stmtNbr = 0;

    private final StringBuilder nextToken = new StringBuilder();

    private final int offset = 0;

    private String prevStmtComment = null;

    /**
     * Retrieves Chapter or Section title from a Comment String based on an
     * identifying pair of characters.
     *
     * @param s String containing Metamath Comment.
     * @param idString identifying string of Chapter or Section title.
     * @return Title of Chapter or Section, or null if the Comment does not
     *         match the search criteria.
     */
    public static String getTitleIfApplicable(final String s,
        final String idString)
    {
        int i = Statementizer.bypassWhitespace(s, 1);

        // optimization
        // int j = s.indexOf(idString, i);
        // if (i != j) {
        // return null;
        // }
        if (!s.startsWith(idString, i))
            return null;

        // ok, now we know we have a valid comment so
        // the output title will at least be a zero-length
        // string indicating that the title is blank
        // (a null would indicate that this comment line
        // is not a Chapter or Section.)
        String title = MMIOConstants.DEFAULT_TITLE;

        i = Statementizer.bypassNonWhitespace(s, i);
        i = Statementizer.bypassWhitespace(s, i);

        int j = s.indexOf('\n', i);
        if (j == -1) {
            j = s.indexOf('\r', i);
            if (j == -1) {
                j = s.indexOf(idString, i);
                if (j == -1)
                    j = s.length() - 1;
            }
        }

        j = s.indexOf(idString, i);
        if (j != -1)
            title = s.substring(i, j).trim();

        return title;
    }

    /**
     * Bypasses Metamath whitespace in a String.
     *
     * @param s String containing Metamath characters.
     * @param i index within String marking beginning of scan for whitespace.
     * @return index of first non-whitespace character or position beyond the
     *         end of the input String.
     */
    public static int bypassWhitespace(final String s, int i) {
        int x;
        final int n = s.length();
        while (i < n) {
            x = s.charAt(i) & 0x00ff;

            if ((MMIOConstants.VALID_CHAR_ARRAY[x]
                & MMIOConstants.WHITE_SPACE) != 0)
            {

                i++;
                continue;
            }
            break;
        }
        return i;
    }

    /**
     * Bypasses Metamath non-whitespace in a String.
     *
     * @param s String containing Metamath characters.
     * @param i index within String marking beginning of scan for
     *            non-whitespace.
     * @return index of first whitespace character or position beyond the end of
     *         the input String.
     */
    public static int bypassNonWhitespace(final String s, int i) {
        int x;

        final int n = s.length();
        while (i < n) {
            x = s.charAt(i) & 0x00ff;

            if ((MMIOConstants.VALID_CHAR_ARRAY[x]
                & MMIOConstants.WHITE_SPACE) != 0)
                break;
            i++;
            continue;
        }
        return i;
    }

    /**
     * Checks to see if a String is in the list of prohibited Metamath labels.
     * <p>
     * See Metamath.pdf specification for the source list.
     *
     * @param s String to check against the Prohibited Label list.
     * @return true if string is on the Prohibited Label list, otherwise false.
     */
    public static boolean isLabelOnProhibitedList(final String s) {
        return MMIOConstants.PROHIBITED_LABELS.contains(s.toLowerCase());
    }

    /**
     * Checks to see if a String is a valid Metamath label name.
     *
     * @param s String to check
     * @return true if string is valid
     */
    public static boolean isValidLabel(final String s) {
        return areLabelCharsValid(s) && !isLabelOnProhibitedList(s);
    }

    /**
     * Checks to see if each character in a String is a valid Metamath
     * character.
     * <p>
     * See Metamath.pdf specification for the source list.
     *
     * @param s String of characters to check for validity.
     * @return true if every character in the input String is a valid Metamath
     *         character.
     */
    public static boolean areLabelCharsValid(final String s) {
        return s.chars().allMatch(c -> (MMIOConstants.VALID_CHAR_ARRAY[c]
            & MMIOConstants.LABEL) != 0);
    }

    /**
     * Checks to see whether or not a String contains only symbols defined in
     * Metamath as valid math symbols.
     *
     * @param s input token, should be pre-trimmed.
     * @return true if input string contains only valid Metamath math symbol
     *         characters; otherwise false.
     */
    public static boolean isValidMathSymbol(final String s) {
        return s.chars().allMatch(c -> (MMIOConstants.VALID_CHAR_ARRAY[c]
            & MMIOConstants.MATH_SYMBOL) != 0);
    }

    /**
     * Construct a Statementizer from a Tokenizer.
     *
     * @param t input {@code Tokenizer} stream.
     */
    public Statementizer(final Tokenizer t) {
        tokenizer = t;
    }

    @Override
    public void close() throws IOException {
        if (tokenizer != null)
            tokenizer.close();
    }

    /**
     * Return number of last statement parsed.
     * <p>
     * Statement number is simply a counter of the {@code SrcStmt} processed so
     * far. It is not the same as <code>mmj.lang.MObj.seq</i>.
     *
     * @return stmtNbr
     */
    public int getStmtNbr() {
        return stmtNbr;
    }

    /**
     * Attempts to bypass the current statement in the input Metamath file.
     * <p>
     * Bypass is intended to give the next next invocation of {@code getStmt()}
     * a good chance of finding a complete statement to parse, thus avoiding
     * multiple error messages caused by a single error.
     *
     * @throws IOException if I/O error
     */
    public void bypassErrorStmt() throws IOException {
        while (true) {
            if (nextToken.indexOf(MMIOConstants.MM_END_COMMENT_KEYWORD) >= 0
                || nextToken.indexOf(MMIOConstants.MM_END_STMT_KEYWORD) >= 0
                || nextToken.indexOf(MMIOConstants.MM_END_FILE_KEYWORD) >= 0)
                break;
            if (getNextToken() <= 0)
                break;
        }

        prevStmtComment = null;
    }

    /**
     * Sets statement number of the last statement parsed.
     * <p>
     * This may come in handy in conjunction with included files -- construct a
     * new Statementizer for the included file, set the stmtNbr, process, then
     * continue processing with the old Statementizer... after setting its
     * stmtNbr. Or...just use the {@code setTokenizer} method to switch
     * Tokenizers! Easy.
     *
     * @param s Metamath statement (sequence) number.
     * @throws IllegalArgumentException if input statement number is less than
     *             zero.
     */
    public void setStmtNbr(final int s) throws IllegalArgumentException {
        if (s < 0)
            throw new IllegalArgumentException(
                new MMIOException(MMIOConstants.ERRMSG_SET_STMT_NBR_LT_0, s));
        stmtNbr = s;
    }

    /**
     * Switches the Tokenizer reader in use.
     * <p>
     * Intended for use with included MetaMath files ({@code $[ xx.mm $]}
     * command.)
     *
     * @param t input {@code Tokenizer}.
     * @return previous Tokenizer in use.
     * @throws IllegalArgumentException if input Tokenizer is null.
     */
    public Tokenizer setTokenizer(final Tokenizer t)
        throws IllegalArgumentException
    {
        if (t == null)
            throw new IllegalArgumentException(
                new MMIOException(MMIOConstants.ERRMSG_SET_TOKENIZER_NULL));
        final Tokenizer prev = tokenizer;
        tokenizer = t;
        return prev;
    }

    /**
     * Returns the current Source information from the Tokenizer.
     *
     * @return the Source info from the Tokenizer in use.
     */
    public String getSourceId() {
        return tokenizer.getSourceId();
    }

    /**
     * Returns the current Tokenizer.
     *
     * @return the Tokenizer in use.
     */
    public Tokenizer getTokenizer() {
        return tokenizer;
    }

    /**
     * Return next MetaMath SrcStmt.
     *
     * @return next MetaMath {@code SrcStmt} or null if EOF.
     * @throws IOException if I/O error
     * @throws MMIOException if invalid SrcStmt read.
     */
    public SrcStmt getStmt() throws MMIOException, IOException {

        SrcStmt x = null;

        while (x == null && getNextToken() > 0) {
            x = new SrcStmt(++stmtNbr, nextToken.toString());
            if (nextToken.charAt(0) == MMIOConstants.MM_KEYWORD_1ST_CHAR)
                switch (x.keyword) {
                    case MMIOConstants.MM_BEGIN_COMMENT_KEYWORD:
                        getComment(x);
                        continue;

                    case MMIOConstants.MM_BEGIN_FILE_KEYWORD:
                        getIncludeFileName(x);
                        continue;

                    case MMIOConstants.MM_BEGIN_SCOPE_KEYWORD:
                    case MMIOConstants.MM_END_SCOPE_KEYWORD:
                        // we're done, outtahere...no other fields in stmt.
                        prevStmtComment = null;
                        break;

                    case MMIOConstants.MM_CNST_KEYWORD:
                        getCnstSrcStmt(x);
                        break;

                    case MMIOConstants.MM_VAR_KEYWORD:
                        getVarSrcStmt(x);
                        break;

                    case MMIOConstants.MM_DJ_VAR_KEYWORD:
                        getDjVarSrcStmt(x);
                        break;

                    case MMIOConstants.MM_LOG_HYP_KEYWORD:
                    case MMIOConstants.MM_VAR_HYP_KEYWORD:
                    case MMIOConstants.MM_AXIOMATIC_ASSRT_KEYWORD:
                    case MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD:
                        raiseParseException(MMIOConstants.ERRMSG_MISSING_LABEL,
                            x.keyword);
                        break;
                    case MMIOConstants.MM_END_COMMENT_KEYWORD:
                        raiseParseException(
                            MMIOConstants.ERRMSG_MISSING_START_COMMENT);
                        break;
                    default:
                        raiseParseException(MMIOConstants.ERRMSG_INV_KEYWORD,
                            x.keyword);
                        break;
                }
            else { // 1st token of statement MUST be a label
                x.label = validateNextTokenLabel();
                x.column = (int)tokenizer.getCurrentColumnNbr()
                    - x.label.length() + 1;
                if (getNextNonCommentTokenLen() <= 0)
                    raiseParseException(MMIOConstants.ERRMSG_EOF_AFTER_LABEL,
                        x.label);
                if (nextToken.charAt(0) != MMIOConstants.MM_KEYWORD_1ST_CHAR)
                    raiseParseException(
                        MMIOConstants.ERRMSG_MISSING_KEYWORD_AFTER_LABEL,
                        x.label);
                switch (x.keyword = nextToken.toString()) {
                    case MMIOConstants.MM_LOG_HYP_KEYWORD:
                        getLogHypSrcStmt(x);
                        break;
                    case MMIOConstants.MM_VAR_HYP_KEYWORD:
                        getVarHypSrcStmt(x);
                        break;
                    case MMIOConstants.MM_AXIOMATIC_ASSRT_KEYWORD:
                        getAxiomaticAssrtSrcStmt(x);
                        break;
                    case MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD:
                        x.proofList = new ArrayList<>(100);
                        getProvableAssrtSrcStmt(x);
                        // x.proofList.trimToSize();
                        break;
                    case MMIOConstants.MM_CNST_KEYWORD:
                    case MMIOConstants.MM_VAR_KEYWORD:
                    case MMIOConstants.MM_DJ_VAR_KEYWORD:
                    case MMIOConstants.MM_BEGIN_SCOPE_KEYWORD:
                    case MMIOConstants.MM_END_SCOPE_KEYWORD:
                    case MMIOConstants.MM_BEGIN_COMMENT_KEYWORD:
                    case MMIOConstants.MM_END_COMMENT_KEYWORD:
                        raiseParseException(
                            MMIOConstants.ERRMSG_MISLABELLED_KEYWORD, x.label,
                            x.keyword);
                        break;
                    default:
                        raiseParseException(MMIOConstants.ERRMSG_INV_KEYWORD,
                            x.keyword);
                        break;
                }
            }
        }
        return x;
    }

    /*
     * Return length of next token that is not part of a comment.
     * <p>
     * This was added after the fact to ignore comments
     * embedded inside of other statements. Statementizer
     * is not designed to deal with intertwined statements,
     * except of course for ${ and $} embedding.
     * <p>
     * Note the tricky double loop: this handles back-to-back
     * embedded comments.
     *
     * @return -1 if EOF or length of next non-comment token.
     */
    private int getNextNonCommentTokenLen() throws IOException {

        int xLen = getNextToken();

        commentStart: while (true)
            if (nextToken.toString()
                .equals(MMIOConstants.MM_BEGIN_COMMENT_KEYWORD))
                while (true) {
                    if ((xLen = getNextToken()) <= 0)
                        return xLen;
                    if (nextToken.toString()
                        .equals(MMIOConstants.MM_END_COMMENT_KEYWORD))
                    {
                        xLen = getNextToken();
                        continue commentStart;
                    }
                }
            else
                return xLen;
    }

    private int getNextToken() throws IOException {
        nextToken.setLength(0);
        return tokenizer.getToken(nextToken, offset);
    }

    private String validateNextTokenLabel() throws MMIOException, IOException {
        final String s = nextToken.toString();
        if (!isValidLabel(s))
            raiseParseException(MMIOConstants.ERRMSG_INV_LABEL, s);
        return s;
    }

    private void getCnstSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {
        prevStmtComment = null;
        loadSymList(x);
        if (x.symList.isEmpty())
            raiseParseException(MMIOConstants.ERRMSG_EMPTY_CNST_STMT);
        dupCheckSymList(x);
    }

    private void getVarSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {
        prevStmtComment = null;
        loadSymList(x);
        if (x.symList.isEmpty())
            raiseParseException(MMIOConstants.ERRMSG_EMPTY_VAR_STMT);
        dupCheckSymList(x);
    }

    private void getDjVarSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {
        prevStmtComment = null;
        loadSymList(x);
        if (x.symList.size() < 2)
            raiseParseException(MMIOConstants.ERRMSG_LESS_THAN_2_DJVARS);
        dupCheckSymList(x);
    }

    private void getVarHypSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {
        prevStmtComment = null;
        getStmtType(x);
        loadSymList(x);
        if (x.symList.size() != 1)
            raiseParseException(MMIOConstants.ERRMSG_VAR_HYP_NE_2_TOKENS);
        if (x.typ.equals(x.symList.get(0)))
            raiseParseException(MMIOConstants.ERRMSG_STMT_HAS_DUP_TOKENS,
                x.keyword);
    }

    private void getLogHypSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {
        prevStmtComment = null;
        getStmtType(x);
        loadSymList(x);
    }

    private void getAxiomaticAssrtSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {
        x.comment = prevStmtComment;
        prevStmtComment = null;
        getStmtType(x);
        loadSymList(x);
    }

    private void getProvableAssrtSrcStmt(final SrcStmt x)
        throws MMIOException, IOException
    {

        x.comment = prevStmtComment;
        prevStmtComment = null;

        getStmtType(x);
        loadProofSymList(x);
        loadProofList(x);
        if (x.proofList.size() < 1 && x.proofBlockList == null)
            raiseParseException(MMIOConstants.ERRMSG_PROOF_IS_EMPTY);
    }

    private void getStmtType(final SrcStmt x)
        throws MMIOException, IOException
    {
        String s;
        if (getNextNonCommentTokenLen() <= 0)
            raiseParseException(MMIOConstants.ERRMSG_STMT_PREMATURE_EOF,
                x.keyword);
        s = nextToken.toString();
        if (!isValidMathSymbol(s))
            if (s.equals(MMIOConstants.MM_END_STMT_KEYWORD))
                raiseParseException(MMIOConstants.ERRMSG_STMT_MISSING_TYPE,
                    x.keyword);
            else
                raiseParseException(MMIOConstants.ERRMSG_INV_CHAR_IN_MATH_SYM,
                    s);
        x.typ = s;
    }

    private void loadSymList(final SrcStmt x)
        throws MMIOException, IOException
    {
        String s;
        x.symList = new ArrayList<>(40);
        while (getNextNonCommentTokenLen() > 0) {
            s = nextToken.toString();
            if (isValidMathSymbol(s))
                x.symList.add(s);
            else if (s.equals(MMIOConstants.MM_END_STMT_KEYWORD))
                break;
            else
                raiseParseException(MMIOConstants.ERRMSG_INV_CHAR_IN_MATH_SYM,
                    s);
        }
        if (nextToken.length() <= 0)
            raiseParseException(MMIOConstants.ERRMSG_STMT_PREMATURE_EOF,
                x.keyword);

    }

    private void dupCheckSymList(final SrcStmt x) throws MMIOException {
        final int n = x.symList.size();
        String s;
        for (int i = 0; i < n - 1; i++) {
            s = x.symList.get(i);
            for (int j = i + 1; j < n; j++)
                if (s.equals(x.symList.get(j)))
                    raiseParseException(
                        MMIOConstants.ERRMSG_STMT_HAS_DUP_TOKENS, x.keyword);
        }
    }

    private void loadProofSymList(final SrcStmt x)
        throws MMIOException, IOException
    {
        String s;
        x.symList = new ArrayList<>(40);
        while (getNextNonCommentTokenLen() > 0) {
            s = nextToken.toString();
            if (isValidMathSymbol(s))
                x.symList.add(s);
            else if (s.equals(MMIOConstants.MM_START_PROOF_KEYWORD))
                break;
            else if (s.equals(MMIOConstants.MM_END_STMT_KEYWORD))
                raiseParseException(MMIOConstants.ERRMSG_PROOF_MISSING);
            else
                raiseParseException(MMIOConstants.ERRMSG_INV_CHAR_IN_MATH_SYM,
                    s);
        }
        if (nextToken.length() <= 0)
            raiseParseException(MMIOConstants.ERRMSG_STMT_PREMATURE_EOF,
                x.keyword);

    }

    private void loadProofList(final SrcStmt x)
        throws MMIOException, IOException
    {
        String s;
        if (getNextNonCommentTokenLen() > 0)
            if (nextToken.length() == 1 && nextToken
                .charAt(0) == MMIOConstants.MM_BEGIN_COMPRESSED_PROOF_LIST_CHAR)
                loadCompressedProof(x);
            else
                do {
                    s = nextToken.toString();
                    if (isValidProofStep(s))
                        x.proofList.add(s);
                    else if (s.equals(MMIOConstants.MM_END_STMT_KEYWORD))
                        break;
                    else
                        raiseParseException(
                            MMIOConstants.ERRMSG_INV_CHAR_IN_PROOF_STEP, s);
                } while (getNextNonCommentTokenLen() > 0);

        if (nextToken.length() <= 0)
            raiseParseException(MMIOConstants.ERRMSG_STMT_PREMATURE_EOF,
                x.keyword);
    }

    private void loadCompressedProof(final SrcStmt x)
        throws MMIOException, IOException
    {

        x.proofBlockList = new BlockList();

        String s;

        while (getNextNonCommentTokenLen() > 0) {
            if (nextToken.length() == 1 && nextToken
                .charAt(0) == MMIOConstants.MM_END_COMPRESSED_PROOF_LIST_CHAR)
            {
                loadCompressedProofBlockList(x);
                break;
            }
            s = nextToken.toString();
            if (isValidProofStep(s)) {
                x.proofList.add(s);
                continue;
            }
            raiseParseException(MMIOConstants.ERRMSG_INV_CHAR_IN_PROOF_STEP, s);
        }
    }

    /**
     * Note: the compressed blocks are not validated here even though everything
     * else is normally validated as much as possible as soon as possible. Here,
     * we could edit for A->Z or ? but to save time we defer to
     * mmj.lang.ProofCompression.java which does thorough character, by
     * character validation.
     * <p>
     * It is necessary to check for "$." however but in the event that the "$."
     * is missing the program would keep chunking along, until the end of the
     * *next* statement or EOF.
     *
     * @param x the statement to load from
     * @throws MMIOException if an error occurs
     * @throws IOException if an error occurs
     */
    private void loadCompressedProofBlockList(final SrcStmt x)
        throws MMIOException, IOException
    {
        while (getNextNonCommentTokenLen() > 0) {
            final String s = nextToken.toString();
            if (s.equals(MMIOConstants.MM_END_STMT_KEYWORD))
                break;

            x.proofBlockList.addBlock(s);
        }

        if (x.proofBlockList.isEmpty())
            raiseParseException(MMIOConstants.ERRMSG_COMPRESSED_PROOF_IS_EMPTY);
    }

    private static boolean isValidProofStep(final String s) {
        return s.equals(MMIOConstants.MISSING_PROOF_STEP)
            || areLabelCharsValid(s);
    }

    private void getComment(final SrcStmt x) throws MMIOException, IOException {

        prevStmtComment = null;

        final StringBuilder s = new StringBuilder();
        String workToken;
        while (true) {
            nextToken.setLength(0);
            if (tokenizer.getWhiteSpace(nextToken, offset) > 0)
                s.append(nextToken);
            if (getNextToken() > 0) {
                workToken = nextToken.toString();
                if (workToken.equals(MMIOConstants.MM_END_COMMENT_KEYWORD)) {
                    x.comment = prevStmtComment = s.toString();
                    break;
                }
                s.append(workToken);
                /**
                 * must not contain embedded $( or $) character sequences
                 */
                if (workToken.indexOf(MMIOConstants.MM_END_COMMENT_KEYWORD) < 0
                    && workToken
                        .indexOf(MMIOConstants.MM_BEGIN_COMMENT_KEYWORD) < 0)
                    continue;
                raiseParseException(MMIOConstants.ERRMSG_INV_COMMENT_CHAR_STR,
                    workToken);
            }
            else
                raiseParseException(MMIOConstants.ERRMSG_PREMATURE_COMMENT_EOF);
        }
    }

    private void getIncludeFileName(final SrcStmt x)
        throws MMIOException, IOException
    {
        prevStmtComment = null;
        if (getNextNonCommentTokenLen() <= 0)
            raiseParseException(
                MMIOConstants.ERRMSG_PREMATURE_INCLUDE_STMT_EOF);
        x.includeFileName = nextToken.toString();
        if (!isValidFileName(x.includeFileName))
            raiseParseException(MMIOConstants.ERRMSG_INV_INCLUDE_FILE_NAME,
                x.includeFileName);
        if (getNextNonCommentTokenLen() <= 0)
            raiseParseException(
                MMIOConstants.ERRMSG_PREMATURE_INCLUDE_STMT_EOF);
        if (!nextToken.toString().equals(MMIOConstants.MM_END_FILE_KEYWORD))
            raiseParseException(MMIOConstants.ERRMSG_INV_INCLUDE_FILE_NAME);
    }

    private boolean isValidFileName(final String s) {
        for (int i = 0; i < s.length(); i++)
            if ((MMIOConstants.VALID_CHAR_ARRAY[s.charAt(i)]
                & MMIOConstants.FILE_NAME) == 0)
                return false;
        return true;
    }

    private void raiseParseException(final ErrorCode code, final Object... args)
        throws MMIOException
    {
        throw tokenizer.addContext(new MMIOException(code, args));
    }
}
