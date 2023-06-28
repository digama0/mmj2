//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

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

import static mmj.util.UtilConstants.*;

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

        putCommand(RUNPARM_CLEAR, () -> {
            initStateVariables();
            return false; // not "consumed"
        });

        putCommand(RUNPARM_SYM_TBL_INITIAL_SIZE, this::editSymTblInitialSize);
        putCommand(RUNPARM_STMT_TBL_INITIAL_SIZE, this::editStmtTblInitialSize);
        putCommand(RUNPARM_LOAD_ENDPOINT_STMT_LABEL,
            this::editLoadEndpointStmtLabel);
        putCommand(RUNPARM_LOAD_ENDPOINT_STMT_NBR,
            this::editLoadEndpointStmtNbr);
        putCommand(RUNPARM_LOAD_COMMENTS, this::editLoadComments);
        putCommand(RUNPARM_LOAD_PROOFS, this::editLoadProofs);

        putCommand(RUNPARM_PROVABLE_LOGIC_STMT_TYPE,
            this::editProvableLogicStmtType);

        putCommand(RUNPARM_LOGIC_STMT_TYPE, this::editLogicStmtType);

        putCommand(RUNPARM_BOOK_MANAGER_ENABLED, this::editBookManagerEnabled);

        putCommand(RUNPARM_SEQ_ASSIGNER_INTERVAL_SIZE,
            this::editSeqAssignerIntervalSize);

        putCommand(RUNPARM_SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE,
            this::editSeqAssignerIntervalTblInitialSize);

        putCommand(RUNPARM_LOAD_FILE, this::doLoadFile);
    }

    /**
     * Returns true if LogicalSystem loaded successfully.
     *
     * @return true if LogicalSystem loaded successfully.
     */
    public boolean getLogicalSystemLoaded() {
        return logicalSystemLoaded;
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
        if (logicalSystemLoaded)
            return logicalSystem;
        throw error(ERRMSG_MM_FILE_NOT_LOADED, RUNPARM_LOAD_FILE);
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
     */
    public void doLoadFile() {

        logicalSystemLoaded = false;

        require(1);

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
            systemizer = new Systemizer();
        systemizer.init(messages, logicalSystem, loadEndpointStmtNbrParm,
            loadEndpointStmtLabelParm, loadComments, loadProofs);

        try {
            systemizer.load(batchFramework.paths.getMetamathPath(), get(1));
        } catch (final MMIOException e) {
            throw error(e);
        }

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
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSymTblInitialSize() {
        symTblInitialSizeParm = getPosInt(1);
    }

    /**
     * Validate Load Endpoint Statement Number Parameter.
     * <p>
     * Must be a positive integer.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadEndpointStmtNbr() {
        loadEndpointStmtNbrParm = getPosInt(1);
    }

    /**
     * Validate Load Endpoint Statement Label Parameter.
     * <p>
     * Must not be blank.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadEndpointStmtLabel() {
        loadEndpointStmtLabelParm = getNonBlank(1,
            ERRMSG_LOAD_ENDPOINT_LABEL_BLANK);
    }

    /**
     * Validate Load Comments Parameter.
     * <p>
     * Must equal yes or no.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadComments() {
        loadComments = getYesNo(1);
    }

    /**
     * Validate Load Proofs Parameter.
     * <p>
     * Must equal yes or no.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editLoadProofs() {
        loadProofs = getYesNo(1);
    }

    /**
     * Validate Statement Table Initial Size Parameter.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editStmtTblInitialSize() {
        symTblInitialSizeParm = getPosInt(1);
    }

    /**
     * Validate Provable Logic Statement Type Runparm.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editProvableLogicStmtType() {
        provableLogicStmtTypeParm = get(1);

        if (provableLogicStmtTypeParm.isEmpty()
            || provableLogicStmtTypeParm.indexOf(' ') != -1)
            throw error(ERRMSG_PROVABLE_TYP_CD_BOGUS);

    }

    /**
     * Validate Logic Statement Type Runparm.
     */
    protected void editLogicStmtType() {
        logicStmtTypeParm = get(1);

        if (logicStmtTypeParm.isEmpty() || logicStmtTypeParm.indexOf(' ') != -1)
            throw error(ERRMSG_LOGIC_TYP_CD_BOGUS);
    }

    /**
     * Validate Book Manager Enabled Parameter.
     * <p>
     * Must equal yes or no.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editBookManagerEnabled() {

        bookManagerEnabledParm = getYesNo(1);

        if (bookManager != null)
            throw error(ERRMSG_BOOK_MANAGER_ALREADY_EXISTS, runParm.name,
                RUNPARM_LOAD_FILE);

    }

    /**
     * Validate SeqAssigner Interval Size Parameter.
     * <p>
     * Must be a positive integer within a given range.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSeqAssignerIntervalSize() {
        seqAssignerIntervalSizeParm = getPosInt(1);
        SeqAssigner.validateIntervalSize(seqAssignerIntervalSizeParm);
    }

    /**
     * Validate SeqAssigner Interval Table Initial Size Parameter.
     * <p>
     * Must be a positive integer within a given range.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editSeqAssignerIntervalTblInitialSize() {
        seqAssignerIntervalTblInitialSizeParm = getPosInt(1);
        SeqAssigner.validateIntervalTblInitialSize(
            seqAssignerIntervalTblInitialSizeParm);
    }
}
