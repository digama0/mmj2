//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * LogicalSystemBoss.java  0.05 1/01/2012
 *
 * Version 0.04 08/01/2008
 * --> Moved processing of ProvableLogicStmtType and
 *                         LogicStmtType RunParms
 *     to here from GrammarBoss.
 * --> Added BookManager parms.
 * --> Added SeqAssigner parms.
 *
 * Version 0.05 - Nov-01-2011:
 *     - Added GMFFManager stuff
 */

package mmj.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import mmj.gmff.GMFFManager;
import mmj.lang.*;
import mmj.mmio.*;
import mmj.verify.GrammarConstants;

/**
 * Responsible for building, loading, maintaining and fetching LogicalSystem,
 * and for executing RunParms involving it.
 * <ul>
 * <li>If non-executable parm, validate, store and "consume"
 * <li>If loadfile parm, validate, store values, load, get error status,
 * print-and-clear messages, and "consume". Remember that Messages object may
 * have changed since Systemizer was created, so update as needed!
 * <li>If clear, set logicalSystem and systemizer to null;
 * <li>Provide getter method for logical system and getIsLoaded method --> if
 * get of logical system attempted before load then throw exception.
 * </ul>
 */
public class LogicalSystemBoss extends Boss {

    protected String provableLogicStmtTypeParm;
    protected String logicStmtTypeParm;

    protected GMFFManager gmffManager;

    protected boolean bookManagerEnabledParm;
    protected BookManager bookManager;

    protected int seqAssignerIntervalSizeParm;
    protected int seqAssignerIntervalTblInitialSizeParm;
    protected SeqAssigner seqAssigner;

    protected int symTblInitialSizeParm;
    protected int stmtTblInitialSizeParm;
    protected int loadEndpointStmtNbrParm;
    protected String loadEndpointStmtLabelParm;

    protected boolean loadComments;
    protected boolean loadProofs;

    protected LogicalSystem logicalSystem;

    protected Systemizer systemizer;

    protected boolean logicalSystemLoaded;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public LogicalSystemBoss(final BatchFramework batchFramework) {
        super(batchFramework);
        initStateVariables();
    }

    /**
     * Returns true if LogicalSystem loaded successfully.
     * 
     * @return true if LogicalSystem loaded successfully.
     */
    public boolean getLogicalSystemLoaded() {
        return logicalSystemLoaded;
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     */
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException
    {

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR) == 0)
        {
            initStateVariables();
            return false; // not "consumed"
        }
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_SYM_TBL_INITIAL_SIZE) == 0)
        {
            editSymTblInitialSize(runParm);
            return true; // "consumed"
        }
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STMT_TBL_INITIAL_SIZE) == 0)
        {
            editStmtTblInitialSize(runParm);
            return true; // "consumed"
        }
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_ENDPOINT_STMT_LABEL) == 0)
        {
            editLoadEndpointStmtLabel(runParm);
            return true; // "consumed"
        }
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_ENDPOINT_STMT_NBR) == 0)
        {
            editLoadEndpointStmtNbr(runParm);
            return true; // "consumed"
        }
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_COMMENTS) == 0)
        {
            editLoadComments(runParm);
            return true; // "consumed"
        }
        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_PROOFS) == 0)
        {
            editLoadProofs(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROVABLE_LOGIC_STMT_TYPE) == 0)
        {
            editProvableLogicStmtType(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_LOGIC_STMT_TYPE) == 0)
        {
            editLogicStmtType(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_BOOK_MANAGER_ENABLED) == 0)
        {
            editBookManagerEnabled(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_SEQ_ASSIGNER_INTERVAL_SIZE) == 0)
        {
            editSeqAssignerIntervalSize(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE) == 0)
        {
            editSeqAssignerIntervalTblInitialSize(runParm);
            return true; // "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE) == 0)
        {
            doLoadFile(runParm);
            return true; // "consumed"
        }
        return false;
    }

    private void initStateVariables() {
        logicalSystemLoaded = false;

        symTblInitialSizeParm = 0;
        stmtTblInitialSizeParm = 0;
        loadEndpointStmtNbrParm = 0;
        loadEndpointStmtLabelParm = null;
        logicalSystem = null;
        systemizer = null;

        loadComments = MMIOConstants.LOAD_COMMENTS_DEFAULT;
        loadProofs = MMIOConstants.LOAD_PROOFS_DEFAULT;

        provableLogicStmtTypeParm = GrammarConstants.DEFAULT_PROVABLE_LOGIC_STMT_TYP_CODES[0];

        logicStmtTypeParm = GrammarConstants.DEFAULT_LOGIC_STMT_TYP_CODES[0];

        gmffManager = null;

        bookManagerEnabledParm = LangConstants.BOOK_MANAGER_ENABLED_DEFAULT;
        bookManager = null;

        seqAssignerIntervalSizeParm = LangConstants.SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT;

        seqAssignerIntervalTblInitialSizeParm = LangConstants.SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_DEFAULT;

        seqAssigner = null;

    }

    /**
     * Get reference to LogicalSystem.
     * <p>
     * If LogicalSystem has not been successfully loaded with a .mm file -- and
     * no load errors -- then throw an exception. Either the RunParmFile lines
     * are misordered or the LoadFile command is missing, or the Metamath file
     * has errors, or?
     * 
     * @return LogicalSystem object reference.
     */
    public LogicalSystem getLogicalSystem() {
        if (!logicalSystemLoaded)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_MM_FILE_NOT_LOADED_1
                    + UtilConstants.RUNPARM_LOAD_FILE
                    + UtilConstants.ERRMSG_MM_FILE_NOT_LOADED_2
                    + UtilConstants.RUNPARM_LOAD_FILE
                    + UtilConstants.ERRMSG_MM_FILE_NOT_LOADED_3);
        return logicalSystem;
    }

    /**
     * Execute the LoadFile command: validates RunParm, loads the Metamath file,
     * prints any error messages and keeps a reference to the loaded
     * LogicalSystem for future reference.
     * <p>
     * Note: Systemizer does not (yet) have a Tokenizer setter method or
     * constructor. This would be needed to enable use of non-ASCII codesets
     * (there is only one Tokenizer at present and it hardcodes character values
     * based on the Metamath.pdf specification.) To make this change it would be
     * necessary to create a Tokenizer interface.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws FileNotFoundException if an error occurred
     * @throws IOException if an error occurred
     */
    public void doLoadFile(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException
    {

        logicalSystemLoaded = false;

        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_LOAD_FILE, 1);

        final Messages messages = batchFramework.outputBoss.getMessages();

        if (logicalSystem == null) {

            if (gmffManager == null)
                gmffManager = new GMFFManager(
                    batchFramework.paths.getMMJ2Path(), messages);

            if (bookManager == null)
                bookManager = new BookManager(bookManagerEnabledParm,
                    provableLogicStmtTypeParm);

            if (seqAssigner == null)
                seqAssigner = new SeqAssigner(seqAssignerIntervalSizeParm,
                    seqAssignerIntervalTblInitialSizeParm);

            int i = symTblInitialSizeParm;
            if (i <= 0)
                i = LangConstants.SYM_TBL_INITIAL_SIZE_DEFAULT;
            int j = symTblInitialSizeParm;
            if (j <= 0)
                j = LangConstants.STMT_TBL_INITIAL_SIZE_DEFAULT;

            logicalSystem = new LogicalSystem(provableLogicStmtTypeParm,
                logicStmtTypeParm, gmffManager, bookManager, seqAssigner, i, j,
                null, // use null to override default
                null); // use null to override default
        }
        else {
            gmffManager.forceReinitialization();
            // precautionary, added for 08/01/2008 release
            logicalSystem.setSyntaxVerifier(null);
            logicalSystem.setProofVerifier(null);
            logicalSystem.clearTheoremLoaderCommitListenerList();
        }

        if (systemizer == null)
            systemizer = new Systemizer(messages, logicalSystem);
        else {
            systemizer.setSystemLoader(logicalSystem);
            systemizer.setMessages(messages);
        }

        systemizer.setLimitLoadEndpointStmtNbr(loadEndpointStmtNbrParm);
        systemizer.setLimitLoadEndpointStmtLabel(loadEndpointStmtLabelParm);
        systemizer.setLoadComments(loadComments);
        systemizer.setLoadProofs(loadProofs);

        systemizer.load(batchFramework.paths.getMetamathPath(),
            runParm.values[0].trim());

        if (messages.getErrorMessageCnt() == 0)
            logicalSystemLoaded = true;

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     * Returns the current value of the LoadProofs RunParm or its default
     * setting.
     * 
     * @return LoadProofs RunParm value (or its default).
     */
    public boolean getLoadProofs() {
        return loadProofs;
    }

    /**
     * Validate Symbol Table Initial Size Parameter.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSymTblInitialSize(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        symTblInitialSizeParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_SYM_TBL_INITIAL_SIZE, 1);
    }

    /**
     * Validate Load Endpoint Statement Number Parameter.
     * <p>
     * Must be a positive integer.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadEndpointStmtNbr(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        loadEndpointStmtNbrParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_LOAD_ENDPOINT_STMT_NBR, 1);
    }

    /**
     * Validate Load Endpoint Statement Label Parameter.
     * <p>
     * Must not be blank.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadEndpointStmtLabel(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_LOAD_ENDPOINT_STMT_LABEL, 1);
        loadEndpointStmtLabelParm = runParm.values[0].trim();
        if (loadEndpointStmtLabelParm.equals(""))
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_LOAD_ENDPOINT_LABEL_BLANK);
    }

    /**
     * Validate Load Comments Parameter.
     * <p>
     * Must equal yes or no.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadComments(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        loadComments = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_LOAD_COMMENTS, 1);
    }

    /**
     * Validate Load Proofs Parameter.
     * <p>
     * Must equal yes or no.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadProofs(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        loadProofs = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_LOAD_PROOFS, 1);
    }

    /**
     * Validate Statement Table Initial Size Parameter.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editStmtTblInitialSize(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        symTblInitialSizeParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_STMT_TBL_INITIAL_SIZE, 1);
    }

    /**
     * Validate Provable Logic Statement Type Runparm.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editProvableLogicStmtType(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PROVABLE_LOGIC_STMT_TYPE, 1);

        provableLogicStmtTypeParm = runParm.values[0].trim();

        if (provableLogicStmtTypeParm.length() < 1
            || provableLogicStmtTypeParm.indexOf(' ') != -1)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_PROVABLE_TYP_CD_BOGUS_1);

    }

    /**
     * Validate Logic Statement Type Runparm.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLogicStmtType(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_LOGIC_STMT_TYPE,
            1);
        logicStmtTypeParm = runParm.values[0].trim();

        if (logicStmtTypeParm.length() < 1
            || logicStmtTypeParm.indexOf(' ') != -1)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_LOGIC_TYP_CD_BOGUS_1);
    }

    /**
     * Validate Book Manager Enabled Parameter.
     * <p>
     * Must equal yes or no.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editBookManagerEnabled(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        bookManagerEnabledParm = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_BOOK_MANAGER_ENABLED, 1);

        if (bookManager != null)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_BOOK_MANAGER_ALREADY_EXISTS_1);

    }

    /**
     * Validate SeqAssigner Interval Size Parameter.
     * <p>
     * Must be a positive integer within a given range.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSeqAssignerIntervalSize(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        seqAssignerIntervalSizeParm = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_SEQ_ASSIGNER_INTERVAL_SIZE, 1);
        SeqAssigner.validateIntervalSize(seqAssignerIntervalSizeParm);
    }

    /**
     * Validate SeqAssigner Interval Table Initial Size Parameter.
     * <p>
     * Must be a positive integer within a given range.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSeqAssignerIntervalTblInitialSize(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {
        seqAssignerIntervalTblInitialSizeParm = editRunParmValueReqPosInt(
            runParm,
            UtilConstants.RUNPARM_SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE, 1);
        SeqAssigner
            .validateIntervalTblInitialSize(seqAssignerIntervalTblInitialSizeParm);
    }
}
