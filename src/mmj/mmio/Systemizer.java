//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Systemizer.java  0.08 11/01/2011
 *
 * Sep-25-2005
 *     -> do finalizeEOF even if error messages found so that
 *        final End Scope can be checked (LogicalSystem
 *        already performs the error message count checking.)
 * Dec-10-2005
 *     -> Add LoadLimit inner class and associated methods
 *        for setting load limits:
 *
 *            setLimitLoadEndpointStmtNbr(int stmtNbr)
 *            setLimitLoadEndpointStmtLabel(int stmtLabel)
 *
 *        Call these after construction of Systemizer but
 *        before loading file(s).
 *
 *        (NOTE: There is no way to reset the LoadLimit after
 *        use -- a reset could be added but there appears
 *        to be no use for it.)
 * Dec-22-2005
 *     -> Added character number (offset + 1) to
 *        MMIOException
 *
 * Apr-1-2006: Version 0.05:
 *     -> Added compressed proof code.
 *
 * Nov-1-2006: Version 0.06:
 *     -> Added logic to load input comments and
 *        to store Theorem description.
 *     -> Added logic to *not* load proofs
 *
 * Aug-01-2008: Version 0.07:
 *     -> Modified loadAxiom() to load the MObj.description
 *        using the curr SrcStmt's comment, if LoadComments
 *        is "on" (previously only theorem comments were
 *        hoovered up.)
 *
 * Nov-01-2011: Version 0.08:
 *     -> Modified loadComment() to grab $t comments for GMFF.
 *     -> Modified for mmj2 Paths Enhancement:
 *        -> add "path" argument to all load() methods.
 */

package mmj.mmio;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import mmj.lang.*;

/**
 * Feed {@code SystemLoader} interface with {@code SrcStmt} objects from
 * {@code Statementizer}.
 * <p>
 * Notes:
 * <ul>
 * <li>Intercepts include statements and converts them into {@code SrcStmt}
 * objects -- transparently with respect to {@code SystemLoader}.
 * <li>Keeps track of end of file and notifies {@code SystemLoader} of that
 * condition.
 * <li>Has no concept of comment statements that are embedded inside other
 * statements -- if comments are ever to be used, this needs a redesign!
 * </ul>
 * 
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class Systemizer {

    private Tokenizer tokenizer;
    private Statementizer statementizer;

    private Messages messages;

    private SystemLoader systemLoader;

    private boolean eofReached = false;
    private List<IncludeFile> fileList = null;
    private List<String> filesAlreadyLoaded = null;

    private SrcStmt currSrcStmt = null;

    private LoadLimit loadLimit = null;

    private boolean loadComments = false;

    private boolean loadProofs = true;

    private List<String> defaultProofList = null;

    /**
     * Construct {@code Systemizer} from a {@code Messages} object and a
     * {@code SystemLoader} object.
     * 
     * @param messages -- repository of error and info messages that provides a
     *            limit on the number of errors output before processing is
     *            halted.
     * @param systemLoader -- a SystemLoader initialized with any customizing
     *            parameters and ready to be loaded with data.
     */
    public Systemizer(final Messages messages, final SystemLoader systemLoader)
    {

        this.messages = messages;
        this.systemLoader = systemLoader;

        filesAlreadyLoaded = new ArrayList<String>();
        tokenizer = null;
        statementizer = null;

        loadLimit = new LoadLimit();

        loadComments = MMIOConstants.LOAD_COMMENTS_DEFAULT;

        loadProofs = MMIOConstants.LOAD_PROOFS_DEFAULT;

        defaultProofList = new ArrayList<String>(1);
        defaultProofList.add(MMIOConstants.MISSING_PROOF_STEP);

    }

    /**
     * Get SystemLoader, as-is.
     * 
     * @return -- SystemLoader structure in use.
     */
    public SystemLoader getSystemLoader() {
        return systemLoader;
    }

    /**
     * Set SystemLoader
     * 
     * @param systemLoader {@code SystemLoader} can be input or changed after
     *            construction.
     */
    public void setSystemLoader(final SystemLoader systemLoader) {
        this.systemLoader = systemLoader;
    }

    /**
     * Get {@code Messages} object
     * 
     * @return {@code Messages} object
     */
    public Messages getMessages() {
        return messages;
    }

    /**
     * Set Messages object
     * 
     * @param messages {@code Messages} object can be set or changed after
     *            construction.
     */
    public void setMessages(final Messages messages) {
        this.messages = messages;
    }

    /**
     * Set LoadLimit Stmt Nbr parm
     * 
     * @param stmtNbr maximum number of Metamath statements to be loaded.
     */
    public void setLimitLoadEndpointStmtNbr(final int stmtNbr) {
        loadLimit.setLoadEndpointStmtNbr(stmtNbr);
    }

    /**
     * Set LoadLimit Stmt Label parm
     * 
     * @param stmtLabel last Metamath statement label to load.
     */
    public void setLimitLoadEndpointStmtLabel(final String stmtLabel) {
        loadLimit.setLoadEndpointStmtLabel(stmtLabel);
    }

    /**
     * Set loadComments boolean parm.
     * <p>
     * If loadComments is true then Metamath comments (at least, for Theorems)
     * will be loaded.
     * 
     * @param loadComments true/false load Metamath Comments.
     */
    public void setLoadComments(final boolean loadComments) {
        this.loadComments = loadComments;
    }

    /**
     * Set loadProofs boolean parm.
     * <p>
     * If loadProofs is true then Metamath proofs are loaded into LogicalSystem,
     * otherwise just a "?" proof is stored with each Theorem.
     * 
     * @param loadProofs true/false load Metamath Proofs in Theorem objects.
     */
    public void setLoadProofs(final boolean loadProofs) {
        this.loadProofs = loadProofs;
    }

    /**
     * Loads MetaMath source file via {@code SystemLoader}.
     * <p>
     * Note: multiple files can be loaded in serial fashion.
     * 
     * @param filePath -- File object holding directory path for readerIn. Used
     *            to look up Metamath include files. May be null, or absolute
     *            path, or relative.
     * @param readerIn -- may be StringReader or BufferedReader but
     *            PushbackReader and LineNumberReader are not helpful choices.)
     *            Will be closed at EOF.
     * @param sourceId -- caption such as filename or test ID. May be empty
     *            string if N/A. Used solely for diagnostic/testing messages.
     * @return {@code Messages} object, which can be tested to see if any error
     *         messages were generated
     * @see mmj.lang.Messages#getErrorMessageCnt() Messages.getErrorMessageCnt()
     * @throws IOException if I/O error
     */
    public Messages load(final File filePath, final Reader readerIn,
        final String sourceId) throws IOException
    {

        tokenizer = new Tokenizer(readerIn, sourceId);
        statementizer = new Statementizer(tokenizer);

        // init stack of include files
        fileList = new ArrayList<IncludeFile>();

        eofReached = false;
        getNextStmt();
        if (eofReached && messages.getErrorMessageCnt() == 0)
            handleParseErrorMessage(MMIOConstants.ERRMSG_INPUT_FILE_EMPTY);
        else {
            while (!eofReached && !messages.maxErrorMessagesReached()) {
                loadStmt(filePath);
                if (loadLimit.getEndpointReached()) {
                    finalizePrematureEOF();
                    tokenizer.close();
                    return messages;
                }
                getNextStmt();
            }
            if (eofReached == true)
                finalizeEOF();
        }
        tokenizer.close();
        return messages;
    }

    /**
     * Clone of Load function using fileNameIn instead of readerIn
     * 
     * @param filePath -- File object holding directory path for fileNameIn --
     *            and used to look up Metamath include files. May be null, or
     *            absolute path, or relative.
     * @param fileNameIn -- input .mm file name String.
     * @param sourceId -- test such as filename or test ID. May be empty string
     *            if N/A. Used solely for diagnostic/testing messages.
     * @return {@code Messages} object, which can be tested to see if any error
     *         messages were generated
     * @throws IOException if I/O error
     * @throws MMIOException if file requested has already been loaded.
     * @throws MMIOError if file requested does not exist.
     */
    public Messages load(final File filePath, final String fileNameIn,
        final String sourceId) throws MMIOException, IOException
    {
        Reader readerIn;
        File f = new File(fileNameIn);
        try {
            f = isInFilesAlreadyLoaded(filesAlreadyLoaded, filePath, fileNameIn);
            if (f == null)
                throw new MMIOException(MMIOConstants.ERRMSG_LOAD_REQ_FILE_DUP
                    + fileNameIn);

            readerIn = new BufferedReader(new InputStreamReader(
                new FileInputStream(f)), MMIOConstants.READER_BUFFER_SIZE);
        } catch (final FileNotFoundException e) {
            throw new MMIOError(MMIOConstants.ERRMSG_LOAD_REQ_FILE_NOTFND +
            // fileNameIn);
                f.getAbsolutePath());
        }

        return load(filePath, readerIn, sourceId);
    }

    /**
     * Clone of Load function using fileNameIn instead of readerIn.
     * 
     * @param filePath -- File object holding directory path for fileNameIn --
     *            and used to look up Metamath include files. May be null, or
     *            absolute path, or relative.
     * @param fileNameIn -- input .mm file name String.
     * @return {@code Messages} object, which can be tested to see if any error
     *         messages were generated
     * @throws IOException if I/O error
     * @throws MMIOException if file requested has already been loaded.
     * @throws MMIOError if file requested does not exist.
     */
    public Messages load(final File filePath, final String fileNameIn)
        throws MMIOException, IOException
    {
        return load(filePath, fileNameIn, fileNameIn);
    }

    // =========================================================

    /**
     * Get next SrcStmt from Statementizer. This is a weird little routine
     * because it must check to see if end of file (eof -- indicated by a null
     * SrcStmt from Statementizer) is simply the end of an include file, and if
     * so, pop the include file stack and go get the NEXT statement from the
     * parent file. Also, it must bypass statements with errors and keep
     * processing until the maximum number of parse errors is reached, or end of
     * file.
     * 
     * @throws IOException if an error occurred
     */
    private void getNextStmt() throws IOException {
        currSrcStmt = null;
        while (true)
            try {
                currSrcStmt = statementizer.getStmt();
                if (currSrcStmt == null) {
                    if (fileList.isEmpty()) {
                        eofReached = true;
                        return;
                    }
                    else
                        termIncludeFile();
                }
                else {
                    loadLimit.checkEndpointReached(currSrcStmt);
                    return;
                }
            } catch (final MMIOException e) {
                handleParseErrorMessage(e.getMessage());
                if (messages.maxErrorMessagesReached()) {
                    eofReached = true;
                    return;
                }
                else
                    statementizer.bypassErrorStmt();
            }
    }

    /**
     * Loads next SrcStmt from Statementizer into memory. The main quirk here is
     * that an unrecognized keyword indicates a programming error. None such
     * should be returned by Statementizer -- or else this routine's logic is
     * bogus!
     * 
     * @param filePath the path to the .mm file
     * @throws IOException if an error occurred
     */
    private void loadStmt(final File filePath) throws IOException {
        try {
            switch (currSrcStmt.keyword.charAt(1)) {

            // these case statements are sequenced by guesstimated
            // frequency of occurrence in www.metamath.org\set.mm

                case MMIOConstants.MM_BEGIN_COMMENT_KEYWORD_CHAR:
                    loadComment(currSrcStmt.comment);
                    break;

                case MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD_CHAR:
                    loadTheorem();
                    break;

                case MMIOConstants.MM_LOG_HYP_KEYWORD_CHAR:
                    loadLogHyp();
                    break;

                case MMIOConstants.MM_BEGIN_SCOPE_KEYWORD_CHAR:
                    loadBeginScope();
                    break;

                case MMIOConstants.MM_END_SCOPE_KEYWORD_CHAR:
                    loadEndScope();
                    break;

                case MMIOConstants.MM_AXIOMATIC_ASSRT_KEYWORD_CHAR:
                    loadAxiom();
                    break;

                case MMIOConstants.MM_VAR_HYP_KEYWORD_CHAR:
                    loadVarHyp();
                    break;

                case MMIOConstants.MM_VAR_KEYWORD_CHAR:
                    loadVar();
                    break;

                case MMIOConstants.MM_DJ_VAR_KEYWORD_CHAR:
                    loadDjVar();
                    break;

                case MMIOConstants.MM_CNST_KEYWORD_CHAR:
                    loadCnst();
                    break;

                case MMIOConstants.MM_BEGIN_FILE_KEYWORD_CHAR:
                    initIncludeFile(filePath);
                    break;

                default:
                    throw new IllegalArgumentException(
                        MMIOConstants.ERRMSG_INV_KEYWORD + currSrcStmt.keyword);
            }
        } catch (final MMIOException e) {
            handleParseErrorMessage(e.getMessage());
        } catch (final LangException e) {
            handleLangErrorMessage(e.getMessage());
        }
    }

    /**
     * Tell SystemLoader that a new level of scoping is starting (to group
     * logical hypotheses, etc. with assertions).
     */
    private void loadBeginScope() {
        systemLoader.beginScope();
    }

    /**
     * Sends each constant symbol to SystemLoader, which handles final
     * validations, etc.
     * 
     * @throws LangException if an error occurred
     */
    private void loadCnst() throws LangException {

        for (final String x : currSrcStmt.symList)
            systemLoader.addCnst(x);
    }

    /**
     * Sends each var symbol to SystemLoader, which handles final validations,
     * etc.
     * 
     * @throws LangException if an error occurred
     */
    private void loadVar() throws LangException {

        for (final String x : currSrcStmt.symList)
            systemLoader.addVar(x);
    }

    /**
     * Sends variable hypothesis to SystemLoader.
     * 
     * @throws LangException if an error occurred
     */
    private void loadVarHyp() throws LangException {

        // note: only one symbol in variable hypothesis, hence get(0);
        systemLoader.addVarHyp(currSrcStmt.label, currSrcStmt.typ,
            currSrcStmt.symList.get(0));

    }

    /**
     * Sends logical hypothesis to SystemLoader.
     * 
     * @throws LangException if an error occurred
     */
    private void loadLogHyp() throws LangException {

        systemLoader.addLogHyp(currSrcStmt.label, currSrcStmt.typ,
            currSrcStmt.symList);
    }

    /**
     * Sends axiom to SystemLoader.
     * 
     * @throws LangException if an error occurred
     */
    private void loadAxiom() throws LangException {

        final Axiom axiom = systemLoader.addAxiom(currSrcStmt.label,
            currSrcStmt.typ, currSrcStmt.symList);

        if (loadComments && currSrcStmt.comment != null)
            axiom.setDescription(currSrcStmt.comment);

    }

    /**
     * Sends theorem to SystemLoader.
     * <p>
     * If loadProofs false, add Theorem with just a "?" proof step.
     * <p>
     * Otherwise, If proofBlockList not null, invoke the variant of addTheorem
     * that handles compressed proofs.
     * <p>
     * Otherwise, add Theorem with uncompressed proof.
     * <p>
     * If comments are to be loaded and a description is available for the
     * Theorem, store the description in the new Theorem object.
     * 
     * @throws LangException if an error occurred
     */
    private void loadTheorem() throws LangException {

        Theorem theorem;
        if (loadProofs) {
            if (currSrcStmt.proofBlockList == null)
                theorem = systemLoader
                    .addTheorem(currSrcStmt.label, currSrcStmt.typ,
                        currSrcStmt.symList, currSrcStmt.proofList);
            else
                theorem = systemLoader.addTheorem(currSrcStmt.label,
                    currSrcStmt.typ, currSrcStmt.symList,
                    currSrcStmt.proofList, currSrcStmt.proofBlockList);
        }
        else
            theorem = systemLoader.addTheorem(currSrcStmt.label,
                currSrcStmt.typ, currSrcStmt.symList, defaultProofList);

        if (loadComments && currSrcStmt.comment != null)
            theorem.setDescription(currSrcStmt.comment);
    }

    /**
     * Sends End Scope command to SystemLoader.
     * 
     * @throws LangException if an error occurred
     */
    private void loadEndScope() throws LangException {
        systemLoader.endScope();
    }

    /**
     * Sends Dj Vars to SystemLoader.
     * 
     * @throws LangException if an error occurred
     */
    private void loadDjVar() throws LangException {

        final int iEnd = currSrcStmt.symList.size() - 1;
        String djVarI;

        final int jEnd = iEnd + 1;
        String djVarJ;

        for (int i = 0; i < iEnd; i++) {
            djVarI = currSrcStmt.symList.get(i);
            for (int j = i + 1; j < jEnd; j++) {
                djVarJ = currSrcStmt.symList.get(j);
                systemLoader.addDjVars(djVarI, djVarJ);
            }
        }
    }

    /**
     * As of we are just loading Chapter and Sections for BookManager and $t
     * typesetting definition comments for GMFFManager.
     * <p>
     * Note: per agreement with Norm, the "$t" token identifying typesetting
     * definitions in a comment is the first non-whitespace token after the "$("
     * in the comment.
     * 
     * @param comment the comment string
     */
    private void loadComment(final String comment) {

        // for GMFFManager, grab this $t comment!
        final int i = Statementizer.bypassWhitespace(comment, 1);
        if (comment.startsWith(MMIOConstants.TYPESETTING_COMMENT_ID_STRING, i))
        {
            systemLoader.cacheTypesettingCommentForGMFF(comment);
            return; // exit, this not the droid you looking for.
        }

        if (systemLoader.isBookManagerEnabled()) {
            String s = Statementizer.getTitleIfApplicable(comment,
                MMIOConstants.CHAPTER_ID_STRING);
            if (s != null)
                systemLoader.addNewChapter(s);
            else {
                s = Statementizer.getTitleIfApplicable(comment,
                    MMIOConstants.SECTION_ID_STRING);
                if (s != null)
                    systemLoader.addNewSection(s);
            }
        }
    }

    /**
     * Switches to the indicated include file, making sure to save the new
     * tokenizer reference for use in error reporting.
     * 
     * @param filePath the path to this .mm file (not the include)
     * @throws MMIOException if an error occurred
     * @throws IOException if an error occurred
     */
    private void initIncludeFile(final File filePath) throws MMIOException,
        IOException
    {

        File f = new File(currSrcStmt.includeFileName);
        try {
            f = isInFilesAlreadyLoaded(filesAlreadyLoaded, filePath,
                currSrcStmt.includeFileName);
            if (f == null)
                raiseParseException(MMIOConstants.ERRMSG_INCL_FILE_DUP
                    + currSrcStmt.includeFileName);
            tokenizer = IncludeFile.initIncludeFile(fileList, f,
                currSrcStmt.includeFileName, statementizer);
        } catch (final FileNotFoundException e) {
            raiseParseException(MMIOConstants.ERRMSG_INCL_FILE_NOTFND_1
                + f.getAbsolutePath() + MMIOConstants.ERRMSG_INCL_FILE_NOTFND_2);
        }
    }

    /**
     * Pops the {@code fileList} stack of include files and throws a hard error
     * if it is unable to switch back to the parent source file (should always
     * work...or there is a logic error...or someone deleted the parent file
     * while we were busy processing a nested include file.)
     * 
     * @throws IOException if an error occurred
     */
    private void termIncludeFile() throws IOException {
        try {
            tokenizer = IncludeFile.termIncludeFile(fileList, statementizer);
        } catch (final FileNotFoundException e) {
            throw new IllegalStateException(
                MMIOConstants.ERRMSG_INCLUDE_FILE_LIST_ERR);

        }
    }

    private File isInFilesAlreadyLoaded(final List<String> filesAlreadyLoaded,
        final File filePath, final String fileNameIn)
    {

        File f = new File(fileNameIn);
        if (filePath == null || f.isAbsolute()) {}
        else
            f = new File(filePath, fileNameIn);

        final String absPath = f.getAbsolutePath();

        for (int i = 0; i < filesAlreadyLoaded.size(); i++)
            if (filesAlreadyLoaded.get(i).equals(absPath))
                return null;
        filesAlreadyLoaded.add(absPath);

        return f;
    }

    private void finalizePrematureEOF() {
        try {
            while (true)
                if (fileList.isEmpty())
                    break;
                else
                    termIncludeFile();
        } catch (final IOException e) {
            handleParseErrorMessage(e.getMessage());
        }

        try {
            systemLoader.finalizeEOF(messages, true); // premature eof
        } catch (final LangException e) {
            handleLangEOFErrorMessage(e.getMessage());
        }
    }

    private void finalizeEOF() {
        try {
            systemLoader.finalizeEOF(messages, false); // !premature eof
        } catch (final LangException e) {
            handleLangEOFErrorMessage(e.getMessage());
        }
    }

    private void handleLangErrorMessage(final String eMessage) {
        messages
            .accumErrorMessage(eMessage + MMIOConstants.ERRMSG_TXT_SOURCE_ID
                + tokenizer.getSourceId() + MMIOConstants.ERRMSG_TXT_LINE
                + tokenizer.getCurrentLineNbr()
                + MMIOConstants.ERRMSG_TXT_COLUMN
                + tokenizer.getCurrentColumnNbr());

    }

    private void handleLangEOFErrorMessage(final String eMessage) {
        messages.accumErrorMessage(eMessage
            + MMIOConstants.ERRMSG_TXT_SOURCE_ID + MMIOConstants.EOF_ERRMSG
            + tokenizer.getSourceId());
    }

    private void handleParseErrorMessage(final String eMessage) {
        messages.accumErrorMessage(eMessage);
    }

    private void raiseParseException(final String errmsg) throws MMIOException {
        throw new MMIOException(tokenizer.getSourceId(),
            tokenizer.getCurrentLineNbr(), tokenizer.getCurrentColumnNbr(),
            tokenizer.getCurrentCharNbr(), errmsg);
    }

    private class LoadLimit {
        int loadEndpointStmtNbr;
        String loadEndpointStmtLabel;
        boolean endpointReached;

        public LoadLimit() {
            loadEndpointStmtNbr = 0;
            loadEndpointStmtLabel = null;
            endpointReached = false;
        }
        public void setLoadEndpointStmtNbr(final int loadEndpointStmtNbr) {
            this.loadEndpointStmtNbr = loadEndpointStmtNbr;
        }
        public void setLoadEndpointStmtLabel(final String loadEndpointStmtLabel)
        {
            this.loadEndpointStmtLabel = loadEndpointStmtLabel;
        }
        public boolean checkEndpointReached(final SrcStmt srcStmt) {
            if (loadEndpointStmtNbr > 0 && srcStmt.seq >= loadEndpointStmtNbr) {
                endpointReached = true;
                messages
                    .accumInfoMessage(MMIOConstants.ERRMSG_LOAD_LIMIT_STMT_NBR_REACHED
                        + loadEndpointStmtNbr);
            }
            if (loadEndpointStmtLabel != null && srcStmt.label != null
                && srcStmt.label.equals(loadEndpointStmtLabel))
            {
                endpointReached = true;
                messages
                    .accumInfoMessage(MMIOConstants.ERRMSG_LOAD_LIMIT_STMT_LABEL_REACHED
                        + loadEndpointStmtLabel);
            }
            return endpointReached;
        }
        public boolean getEndpointReached() {
            return endpointReached;
        }
    }
}
