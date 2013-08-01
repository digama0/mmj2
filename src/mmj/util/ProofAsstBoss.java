//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  ProofAsstBoss.java  0.10 11/01/2011
 *
 *  Version 0.02:
 *      - New RunParms to support Proof Assistant "Derive" feature:
 *          - MaxUnifyAlternates
 *          - Dummy Var Prefix
 *
 *  Sep-03-2006:
 *      -->Add TMFF stuff
 *
 *  Varsion 0.04 - 06/01/2007
 *      -->Modify to not validate Font Family Name
 *      -->Added ProofAsstDjVarsSoftErrors RunParm
 *
 *  Varsion 0.05 - 08/01/2007
 *      -->Added code to get/init workVarManager instance
 *         and add it to ProofAsstPreferences before
 *         returning a ProofAsst instance in getProofAsst().
 *      -->remove Dummy Var processing (deprecate RunParm too).
 *      -->Added "AsciiRetest" option to BatchTest RunParm
 *
 *
 *  Varsion 0.06 - 11/01/2007
 *      - Add "ProofAsstTextRows"            RunParm
 *      - Add "ProofAsstErrorMessageRows"    RunParm
 *      - Add "ProofAsstErrorMessageColumns" RunParm
 *      - Add "ProofAsstTextAtTop"           RunParm
 *
 *
 *  Varsion 0.07 - 02/01/2008
 *      - Add "ProofAsstIncompleteStepCursor"        RunParm
 *      - Add "ProofAsstOutputCursorInstrumentation" RunParm
 *      - Add "ProofAsstAutoReformat"                RunParm
 *
 *  Varsion 0.08 - 03/01/2008
 *      - Add "StepSelectorMaxResults"               RunParm
 *      - Add "StepSelectorShowSubstitutions"        RunParm
 *      - Add "StepSelectorDialogPaneWidth"          RunParm
 *      - Add "StepSelectorDialogPaneHeight"         RunParm
 *      - Remove Unify+Get Hints feature, deprecate
 *        hint-related RunParms
 *      - Add "StepSelectorBatchTest"                RunParm
 *      - Add "PreprocessRequestBatchTest"           RunParm
 *
 *  Varsion 0.09 - 08/01/2008
 *      - Clear ProofAsstPreferences when LoadFile RunParm
 *        encountered. It can't be hanging around after that.
 *      - changed editProofAsstImportFileRunParm() from
 *        protected access to public for use by TheoremLoaderBoss.
 *      - Add ProofAsstAssrtListFreespace RunParm allowing
 *        user to set to 0 thru 1000.
 *
 *  Version 0.10 - Nov-01-2011
 *      -->Modified for mmj2 Paths Enhancement
 *         --> added mmj2Path arg to editProofAsstProofFolder() call
 *      -->Added code for MMJ2FailPopupWindow

 */

package mmj.util;

import java.awt.Color;
import java.io.*;
import java.util.ArrayList;
import java.util.Map;

import mmj.lang.*;
import mmj.mmio.MMIOException;
import mmj.pa.*;
import mmj.tl.TheoremLoader;
import mmj.verify.Grammar;
import mmj.verify.VerifyProofs;

/**
 *  Responsible for building and triggering ProofAsst.
 *  <ul>
 *  <li>Remember that Messages, LogicalSystem
 *      and other objects may have changed. Don't worry
 *      about whether or not file is loaded, the
 *      LogicalSystemBoss will throw an exception if
 *      attempt is made to retrieve LogicalSystem if
 *      it is not loaded and error free.
 *  <li>If clear, RunParm values to null, etc.
 *  </ul>
 *
 */
public class ProofAsstBoss extends Boss {

    private ProofAsst proofAsst;

    private ProofAsstPreferences proofAsstPreferences;

    /**
     *  Constructor with BatchFramework for access to environment.
     *
     *  @param batchFramework for access to environment.
     */
    public ProofAsstBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     *  Executes a single command from the RunParmFile.
     *
     *  @param runParm the RunParmFile line to execute.
     *
     *  @return      boolean "consumed" indicating that the
     *           input runParm should not be processed
     *           again.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException
    {

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR) == 0)
        {
            proofAsst = null;
            proofAsstPreferences = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE) == 0)
        {
            proofAsst = null;
            proofAsstPreferences = null;
            return false; // not "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_DJ_VARS_SOFT_ERRORS) == 0)
        {
            editProofAsstDjVarsSoftErrors(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_PROOF_FORMAT) == 0)
        {
            editProofAsstProofFormat(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_INCOMPLETE_STEP_CURSOR) == 0)
        {
            editProofAsstIncompleteStepCursor(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_FOREGROUND_COLOR_RGB) == 0)
        {
            editProofAsstForegroundColorRGB(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_BACKGROUND_COLOR_RGB) == 0)
        {
            editProofAsstBackgroundColorRGB(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_FONT_SIZE) == 0)
        {
            editProofAsstFontSize(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_FONT_FAMILY) == 0)
        {
            editProofAsstFontFamily(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_FONT_BOLD) == 0)
        {
            editProofAsstFontBold(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_TEXT_COLUMNS) == 0)
        {
            editProofAsstTextColumns(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_TEXT_ROWS) == 0)
        {
            editProofAsstTextRows(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_ERROR_MESSAGE_ROWS) == 0)
        {
            editProofAsstErrorMessageRows(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_ERROR_MESSAGE_COLUMNS) == 0)
        {
            editProofAsstErrorMessageColumns(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_TEXT_AT_TOP) == 0)
        {
            editProofAsstTextAtTop(runParm);
            return true;
        }

//->lineWrap disabled because couldn't get Java Swing to work with it
//      if (runParm.name.compareToIgnoreCase(
//              UtilConstants.RUNPARM_PROOF_ASST_LINE_WRAP)
//          == 0) {
//          editProofAsstLineWrap(runParm);
//          return true;
//      }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_FORMULA_LEFT_COL) == 0)
        {
            editProofAsstFormulaLeftCol(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_FORMULA_RIGHT_COL) == 0)
        {
            editProofAsstFormulaRightCol(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_RPN_PROOF_LEFT_COL) == 0)
        {
            editProofAsstRPNProofLeftCol(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_RPN_PROOF_RIGHT_COL) == 0)
        {
            editProofAsstRPNProofRightCol(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_MAX_UNIFY_ALTERNATES) == 0)
            // DEPRECATED
            return true;

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_MAX_UNIFY_HINTS) == 0)
            // DEPRECATED
            return true;

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_UNIFY_HINTS_IN_BATCH) == 0)
            // DEPRECATED
            return true;

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STEP_SELECTOR_MAX_RESULTS) == 0)
        {
            editStepSelectorMaxResults(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STEP_SELECTOR_SHOW_SUBSTITUTIONS) == 0)
        {
            editStepSelectorShowSubstitutions(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STEP_SELECTOR_DIALOG_PANE_WIDTH) == 0)
        {
            editStepSelectorDialogPaneWidth(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STEP_SELECTOR_DIALOG_PANE_HEIGHT) == 0)
        {
            editStepSelectorDialogPaneHeight(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_ASSRT_LIST_FREESPACE) == 0)
        {
            editProofAsstAssrtListFreespace(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_OUTPUT_CURSOR_INSTRUMENTATION) == 0)
        {
            editProofAsstOutputCursorInstrumentation(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_AUTO_REFORMAT) == 0)
        {
            editProofAsstAutoReformat(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_UNDO_REDO_ENABLED) == 0)
        {
            editProofAsstUndoRedoEnabled(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_DUMMY_VAR_PREFIX) == 0)
            // deprecated.
            return true;

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_DEFAULT_FILE_NAME_SUFFIX) == 0)
        {
            editProofAsstDefaultFileNameSuffix(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_PROOF_FOLDER) == 0)
        {
            editProofAsstProofFolder(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_RECHECK_PROOF_ASST_USING_PROOF_VERIFIER) == 0)
        {
            editRecheckProofAsstUsingProofVerifier(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_UNIFY_SEARCH_EXCLUDE) == 0)
        {
            editProofAsstUnifySearchExclude(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE) == 0)
        {
            doProofAsstExportToFile(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST) == 0)
        {
            doProofAsstBatchTest(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STEP_SELECTOR_BATCH_TEST) == 0)
        {
            doStepSelectorBatchTest(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PREPROCESS_REQUEST_BATCH_TEST) == 0)
        {
            doPreprocessRequestBatchTest(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PROOF_ASST_STARTUP_PROOF_WORKSHEET) == 0)
        {
            editProofAsstStartupProofWorksheet(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_RUN_PROOF_ASST_GUI) == 0)
        {
            doRunProofAsstGUI(runParm);
            return true; // "consumed"
        }

        return false;
    }

    /**
     *  Fetch a ProofAsst object.
     *  <p>
     *
     *  @return ProofAsst object, ready to go, or null;.
     */
    public ProofAsst getProofAsst() throws VerifyException {

        if (proofAsst != null)
            return proofAsst;

        final Messages messages = batchFramework.outputBoss.getMessages();

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final VerifyProofs verifyProofs = batchFramework.verifyProofBoss
            .getVerifyProofs();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        if (grammar.getGrammarInitialized()
            && batchFramework.grammarBoss.getAllStatementsParsedSuccessfully())
        {

            final ProofAsstPreferences proofAsstPreferences = getProofAsstPreferences();

            final WorkVarManager workVarManager = batchFramework.workVarBoss
                .getWorkVarManager();

            if (!workVarManager.areWorkVarsDeclared())
                workVarManager.declareWorkVars(grammar, logicalSystem);

            proofAsstPreferences.setWorkVarManager(workVarManager);

            final TheoremLoader theoremLoader = batchFramework.theoremLoaderBoss
                .getTheoremLoader();

            proofAsst = new ProofAsst(proofAsstPreferences, logicalSystem,
                grammar, verifyProofs, theoremLoader);

            if (!proofAsst.getInitializedOK())
                proofAsst.initializeLookupTables(messages);

            logicalSystem.accumTheoremLoaderCommitListener(proofAsst);

        }
        else {
            proofAsst = null;
            messages
                .accumErrorMessage(UtilConstants.ERRMSG_PA_REQUIRES_GRAMMAR_INIT);
        }

        batchFramework.outputBoss.printAndClearMessages();

        return proofAsst;
    }

    /**
     *  edit ProofAsstDjVarsSoftErrors RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstDjVarsSoftErrors(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_DJ_VARS_SOFT_ERRORS, 1);
        if (getProofAsstPreferences().setDjVarsSoftErrorsOption(
            runParm.values[0].trim()))
            return; // ok, valid!

        throw new IllegalArgumentException(
            PaConstants.ERRMSG_INVALID_SOFT_DJ_VARS_ERROR_OPTION_1
                + runParm.values[0]
                + PaConstants.ERRMSG_INVALID_SOFT_DJ_VARS_ERROR_OPTION_2);
    }

    /**
     * edit ProofAsstProofFormat RunParm.
     * @param runParm
     *        run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstProofFormat(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_PROOF_FORMAT, 1);
        if (getProofAsstPreferences().setProofFormatOption(
            runParm.values[0].trim()))
            return; // ok, valid!

        throw new IllegalArgumentException(
            PaConstants.ERRMSG_INVALID_PROOF_FORMAT_1 + runParm.values[0]
                + PaConstants.ERRMSG_INVALID_PROOF_FORMAT_2);
    }

    /**
     *  edit ProofAsstIncompleteStepCursor RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstIncompleteStepCursor(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_INCOMPLETE_STEP_CURSOR, 1);
        if (getProofAsstPreferences().setIncompleteStepCursor(
            runParm.values[0].trim()))
            return; // ok, valid!

        throw new IllegalArgumentException(
            PaConstants.ERRMSG_INVALID_INCOMPLETE_STEP_CURSOR_1
                + runParm.values[0]
                + PaConstants.ERRMSG_INVALID_INCOMPLETE_STEP_CURSOR_2);
    }

    /**
     *  Validate ProofAsstForegroundColorRGB RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstForegroundColorRGB(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final Color color = editRunParmValueReqRGBColor(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_FOREGROUND_COLOR_RGB);
        getProofAsstPreferences().setForegroundColor(color);
    }

    /**
     *  Validate ProofAsstBackgroundColorRGB RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstBackgroundColorRGB(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final Color color = editRunParmValueReqRGBColor(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_BACKGROUND_COLOR_RGB);
        getProofAsstPreferences().setBackgroundColor(color);
    }

    /**
     *  Validate ProofAsstFontSize RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstFontSize(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int fontSize = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_FONT_SIZE, 1);
        if (fontSize < PaConstants.PROOF_ASST_FONT_SIZE_MIN
            || fontSize > PaConstants.PROOF_ASST_FONT_SIZE_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_FONT_SZ_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_FONT_SIZE_MIN
                    + UtilConstants.ERRMSG_RUNPARM_FONT_SZ_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_FONT_SIZE_MAX);
        getProofAsstPreferences().setFontSize(fontSize);
    }

    /**
     *  Validate ProofAsstFontFamily RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstFontFamily(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_FONT_FAMILY, 1);
//      try {
//          String familyName =
//              getProofAsstPreferences().
//                  validateFontFamily(
//                      runParm.values[0]);
        final String familyName = runParm.values[0].trim();

        getProofAsstPreferences().setFontFamily(familyName);
//      }
//      catch (ProofAsstException e) {
//          throw new IllegalArgumentException(e
//              + UtilConstants.PROOF_ASST_FONT_FAMILY_LIST_CAPTION
//              + getProofAsstPreferences().getFontListString());
//      }
    }

    /**
     *  Validate ProofAsstFontBold RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstFontBold(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final boolean boldFont = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_FONT_BOLD, 1);

        getProofAsstPreferences().setFontBold(boldFont);
    }

//  /**
//   *  Validate ProofAsstLineWrap
//   *
//   *  @param runParm run parm parsed into RunParmArrayEntry object
//   */
//  protected void editProofAsstLineWrap(
//                      RunParmArrayEntry runParm)
//                          throws IllegalArgumentException {
//
//      boolean lineWrap          =
//          editOnOffRunParm(
//              runParm,
//              UtilConstants.
//                  RUNPARM_PROOF_ASST_LINE_WRAP,
//              1);
//      getProofAsstPreferences().setLineWrap(lineWrap);
//  }

    /**
     *  Validate ProofAsstTextColumns
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstTextColumns(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int textColumns = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_TEXT_COLUMNS, 1);
        if (textColumns < PaConstants.PROOF_ASST_TEXT_COLUMNS_MIN
            || textColumns > PaConstants.PROOF_ASST_TEXT_COLUMNS_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_TEXT_COL_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_TEXT_COLUMNS_MIN
                    + UtilConstants.ERRMSG_RUNPARM_PA_TEXT_COL_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_TEXT_COLUMNS_MAX);
        getProofAsstPreferences().setTextColumns(textColumns);
    }

    /**
     *  Validate ProofAsstTextRows
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstTextRows(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int textRows = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_TEXT_ROWS, 1);
        if (textRows < PaConstants.PROOF_ASST_TEXT_ROWS_MIN
            || textRows > PaConstants.PROOF_ASST_TEXT_ROWS_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_TEXT_ROW_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_TEXT_ROWS_MIN
                    + UtilConstants.ERRMSG_RUNPARM_PA_TEXT_ROW_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_TEXT_ROWS_MAX);
        getProofAsstPreferences().setTextRows(textRows);
    }

    /**
     *  Validate ProofAsstErrorMessageRows
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstErrorMessageRows(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int textRows = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_ERROR_MESSAGE_ROWS, 1);
        if (textRows < PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_MIN
            || textRows > PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_ERR_MSG_ROW_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_MIN
                    + UtilConstants.ERRMSG_RUNPARM_PA_ERR_MSG_ROW_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_MAX);
        getProofAsstPreferences().setErrorMessageRows(textRows);
    }

    /**
     *  Validate ProofAsstErrorMessageColumns
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstErrorMessageColumns(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final int textColumns = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_ERROR_MESSAGE_COLUMNS, 1);
        if (textColumns < PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_MIN
            || textColumns > PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_ERR_MSG_COL_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_MIN
                    + UtilConstants.ERRMSG_RUNPARM_PA_ERR_MSG_COL_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_MAX);
        getProofAsstPreferences().setErrorMessageColumns(textColumns);
    }

    /**
     *  Validate ProofAsstTextAtTop RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstTextAtTop(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final boolean enabled = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_TEXT_AT_TOP, 1);
        getProofAsstPreferences().setTextAtTop(enabled);
    }

    /**
     *  Validate ProofAsstFormulaLeftCol.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstFormulaLeftCol(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int formulaLeftCol = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_FORMULA_LEFT_COL, 1);
        if (formulaLeftCol < PaConstants.PROOF_ASST_FORMULA_LEFT_COL_MIN
            || formulaLeftCol > getProofAsstPreferences().getFormulaRightCol() - 1)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_FLC_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_FORMULA_LEFT_COL_MIN
                    + UtilConstants.ERRMSG_RUNPARM_PA_FLC_RANGE_ERR_2
                    + (getProofAsstPreferences().getFormulaRightCol() - 1));
        getProofAsstPreferences().setFormulaLeftCol(formulaLeftCol);
    }

    /**
     *  Validate ProofAsstFormulaRightCol.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstFormulaRightCol(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int formulaRightCol = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_FORMULA_RIGHT_COL, 1);
        if (formulaRightCol < getProofAsstPreferences().getFormulaLeftCol() + 1
            || formulaRightCol > PaConstants.PROOF_ASST_FORMULA_RIGHT_COL_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_FLC_RANGE_ERR_1
                    + (getProofAsstPreferences().getFormulaLeftCol() + 1)
                    + UtilConstants.ERRMSG_RUNPARM_PA_FLC_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_FORMULA_RIGHT_COL_MAX);
        getProofAsstPreferences().setFormulaRightCol(formulaRightCol);
    }

    /**
     *  Validate ProofAsstRPNProofLeftCol.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstRPNProofLeftCol(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int rpnProofLeftCol = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_RPN_PROOF_LEFT_COL, 1);
        if (rpnProofLeftCol < PaConstants.PROOF_ASST_RPN_PROOF_LEFT_COL_MIN
            || rpnProofLeftCol > getProofAsstPreferences()
                .getRPNProofRightCol() - 1)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_RLC_RANGE_ERR_1
                    + PaConstants.PROOF_ASST_RPN_PROOF_LEFT_COL_MIN
                    + UtilConstants.ERRMSG_RUNPARM_PA_RLC_RANGE_ERR_2
                    + (getProofAsstPreferences().getRPNProofRightCol() - 1));
        getProofAsstPreferences().setRPNProofLeftCol(rpnProofLeftCol);
    }

    /**
     *  Validate ProofAsstRPNProofRightCol.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstRPNProofRightCol(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int rpnProofRightCol = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_RPN_PROOF_RIGHT_COL, 1);
        if (rpnProofRightCol < getProofAsstPreferences().getRPNProofLeftCol() + 1
            || rpnProofRightCol > PaConstants.PROOF_ASST_RPN_PROOF_RIGHT_COL_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PA_RLC_RANGE_ERR_1
                    + (getProofAsstPreferences().getRPNProofLeftCol() + 1)
                    + UtilConstants.ERRMSG_RUNPARM_PA_RLC_RANGE_ERR_2
                    + PaConstants.PROOF_ASST_RPN_PROOF_RIGHT_COL_MAX);
        getProofAsstPreferences().setRPNProofRightCol(rpnProofRightCol);
    }

    /**
     *  Validate StepSelectorMaxResults
     *  <p>
     *  Must be positive integer.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editStepSelectorMaxResults(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final int stepSelectorMaxResults = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_STEP_SELECTOR_MAX_RESULTS, 1);

        final ProofAsstPreferences p = getProofAsstPreferences();
        p.setStepSelectorMaxResults(p.validateStepSelectorMaxResults(Integer
            .toString(stepSelectorMaxResults)));
    }

    /**
     *  Validate StepSelectorShowSubstitutions
     *  <p>
     *  Must be yes, no, on, off, true, false.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editStepSelectorShowSubstitutions(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final int valueFieldNbr = 1;
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_STEP_SELECTOR_SHOW_SUBSTITUTIONS,
            valueFieldNbr);

        final ProofAsstPreferences p = getProofAsstPreferences();
        p.setStepSelectorShowSubstitutions(p
            .validateStepSelectorShowSubstitutions(runParm.values[valueFieldNbr - 1]));
    }

    /**
     *  Validate StepSelectorDialogPaneWidth.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editStepSelectorDialogPaneWidth(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final int stepSelectorDialogPaneWidth = editRunParmValueReqPosInt(
            runParm, UtilConstants.RUNPARM_STEP_SELECTOR_DIALOG_PANE_WIDTH, 1);
        if (stepSelectorDialogPaneWidth < PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_MIN
            || stepSelectorDialogPaneWidth > PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_SS_DLG_PANE_WIDTH_ERR_1
                    + PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_MIN
                    + UtilConstants.ERRMSG_RUNPARM_SS_DLG_PANE_WIDTH_ERR_2
                    + PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_MAX);
        getProofAsstPreferences().setStepSelectorDialogPaneWidth(
            stepSelectorDialogPaneWidth);
    }

    /**
     *  Validate StepSelectorDialogPaneHeight.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editStepSelectorDialogPaneHeight(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final int stepSelectorDialogPaneHeight = editRunParmValueReqPosInt(
            runParm, UtilConstants.RUNPARM_STEP_SELECTOR_DIALOG_PANE_HEIGHT, 1);
        if (stepSelectorDialogPaneHeight < PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_MIN
            || stepSelectorDialogPaneHeight > PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_SS_DLG_PANE_HEIGHT_ERR_1
                    + PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_MIN
                    + UtilConstants.ERRMSG_RUNPARM_SS_DLG_PANE_HEIGHT_ERR_2
                    + PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_MAX);
        getProofAsstPreferences().setStepSelectorDialogPaneHeight(
            stepSelectorDialogPaneHeight);
    }

    /**
     *  Validate ProofAsstAssrtListFreespace.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstAssrtListFreespace(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final int proofAsstAssrtListFreespace = editRunParmValueReqNonNegativeInt(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_ASSRT_LIST_FREESPACE, 1);
        if (proofAsstAssrtListFreespace > PaConstants.ASSRT_LIST_FREESPACE_MAX)
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_PROOF_ASST_FREESPACE_ERR_1
                    + PaConstants.ASSRT_LIST_FREESPACE_MAX);
        getProofAsstPreferences().setAssrtListFreespace(
            proofAsstAssrtListFreespace);
    }

    /**
     *  Validate Proof Assistant Default File Name Suffix
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstDefaultFileNameSuffix(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final String defaultFileNameSuffix = editProofWorksheetFileNameSuffix(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_DEFAULT_FILE_NAME_SUFFIX,
            1); // field nbr
        getProofAsstPreferences().setDefaultFileNameSuffix(
            defaultFileNameSuffix);

    }

    /**
     *  Validate Proof Assistant Proof Folder Runparm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstProofFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final File proofFolder = editExistingFolderRunParm(
            batchFramework.paths.getMMJ2Path(), runParm,
            UtilConstants.RUNPARM_PROOF_ASST_PROOF_FOLDER, 1); // field nbr of
                                                               // folder
        getProofAsstPreferences().setProofFolder(proofFolder);

    }

    /**
     *  Validate Proof Assistant Startup Proof Worksheet Runparm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstStartupProofWorksheet(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final File startupProofWorksheetFile = editExistingFileRunParm(
            batchFramework.paths.getMMJ2Path(), runParm,
            UtilConstants.RUNPARM_PROOF_ASST_STARTUP_PROOF_WORKSHEET, 1); // field
                                                                          // nbr
                                                                          // of
                                                                          // folder

        getProofAsstPreferences().setStartupProofWorksheetFile(
            startupProofWorksheetFile);
    }

    /**
     *  Validate ProofAsstOutputCursorInstrumentation RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstOutputCursorInstrumentation(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final boolean instrumentation = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_OUTPUT_CURSOR_INSTRUMENTATION, 1);
        getProofAsstPreferences().setOutputCursorInstrumentation(
            instrumentation);
    }

    /**
     *  Validate ProofAsstAutoReformat RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstAutoReformat(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final boolean autoReformat = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_AUTO_REFORMAT, 1);
        getProofAsstPreferences().setAutoReformat(autoReformat);
    }

    /**
     *  Validate ProofAsstUndoRedoEnabled RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstUndoRedoEnabled(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {

        final boolean enabled = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_UNDO_REDO_ENABLED, 1);
        getProofAsstPreferences().setUndoRedoEnabled(enabled);
    }

    /**
     *  Validate RecheckProofAsstUsingProofVerifier
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editRecheckProofAsstUsingProofVerifier(
        final RunParmArrayEntry runParm) throws IllegalArgumentException
    {

        final boolean recheck = editYesNoRunParm(runParm,
            UtilConstants.RUNPARM_RECHECK_PROOF_ASST_USING_PROOF_VERIFIER, 1);
        getProofAsstPreferences()
            .setRecheckProofAsstUsingProofVerifier(recheck);
    }

    /**
     *  Validate ProofAsstUnifySearchExclude
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editProofAsstUnifySearchExclude(
        final RunParmArrayEntry runParm)
    {

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();
        final Cnst provableLogicStmtTyp = grammar
            .getProvableLogicStmtTypArray()[0];

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Map stmtTbl = logicalSystem.getStmtTbl();
        Stmt stmt;

        final ArrayList excludedList = new ArrayList(runParm.values.length + 1);

        Object mapValue;
        String label;

        for (final String value : runParm.values) {
            label = value.trim();
            mapValue = stmtTbl.get(label);
            if (mapValue != null) {
                stmt = (Stmt)mapValue;
                if (stmt.isAssrt()
                    && stmt.getFormula().getTyp() == provableLogicStmtTyp)
                    excludedList.add(stmt);

            }
        }
        final Assrt[] excludedArray = new Assrt[excludedList.size()];
        for (int i = 0; i < excludedArray.length; i++)
            excludedArray[i] = (Assrt)excludedList.get(i);
        // //test code
        // System.out.println(
        // "TestCode: excludedArray["
        // + i
        // + "] = "
        // + excludedArray[i].getLabel());
        getProofAsstPreferences().setUnifySearchExclude(excludedArray);
    }

    /**
     *  Exports currently loaded theorem proofs to an export
     *  file.
     *
     *  @param runParm RunParmFile line.
     */
    public void doProofAsstExportToFile(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();

        final Writer exportWriter = editProofAsstExportFileRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE);

        Boolean selectorAll = null;
        Integer selectorCount = null;
        Theorem selectorTheorem = null;

        selectorAll = getSelectorAllRunParmOption(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE, 1);
        if (selectorAll == null) {
            selectorCount = getSelectorCountRunParmOption(runParm,
                UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE, 1);
            if (selectorCount == null) {
                final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                    .getLogicalSystem();

                selectorTheorem = getSelectorTheoremRunParmOption(runParm,
                    UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE, 1,
                    logicalSystem.getStmtTbl());
                if (selectorTheorem == null)
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_SELECTOR_MISSING_1
                            + UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE
                            + UtilConstants.ERRMSG_SELECTOR_MISSING_2 + "1"
                            + UtilConstants.ERRMSG_SELECTOR_MISSING_3);
            }
        }

        OutputBoss outputBoss = null;
        if (editProofAsstPrintParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE, 6))
            outputBoss = batchFramework.outputBoss;

        if (exportWriter != null) {
            proofAsst.exportToFile(exportWriter, messages, selectorAll,
                selectorCount, selectorTheorem, outputBoss);
            exportWriter.close();
        }

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     *  Reads and unifies theorem proofs in test mode.
     *
     *  @param runParm RunParmFile line.
     */
    public void doProofAsstBatchTest(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();

        Boolean selectorAll = null;
        Integer selectorCount = null;
        Theorem selectorTheorem = null;

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 1);

        final boolean exportFormatUnified = editProofAsstExportFormatUnifiedParm(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 3);
        getProofAsstPreferences().setExportFormatUnified(exportFormatUnified);

        final boolean exportHypsRandomized = editProofAsstExportHypsRandomizedParm(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 4);
        getProofAsstPreferences().setExportHypsRandomized(exportHypsRandomized);

        OutputBoss outputBoss = null;
        if (editProofAsstPrintParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 5))
            outputBoss = batchFramework.outputBoss;

        final boolean exportDeriveFormulas = editProofAsstExportDeriveFormulasParm(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 6);
        getProofAsstPreferences().setExportDeriveFormulas(exportDeriveFormulas);

        final boolean importCompareDJs = editProofAsstImportCompareDJsParm(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 7);
        getProofAsstPreferences().setImportCompareDJs(importCompareDJs);

        final boolean importUpdateDJs = editProofAsstImportUpdateDJsParm(
            runParm, UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 8);
        getProofAsstPreferences().setImportUpdateDJs(importUpdateDJs);

        final boolean asciiRetest = editProofAsstAsciiRetestParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 9);

        selectorAll = getSelectorAllRunParmOption(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 1);
        if (selectorAll == null) {
            selectorCount = getSelectorCountRunParmOption(runParm,
                UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 1);
            if (selectorCount == null) {
                final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
                    .getLogicalSystem();

                selectorTheorem = getSelectorTheoremRunParmOption(runParm,
                    UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 1,
                    logicalSystem.getStmtTbl());
                if (selectorTheorem == null)
                    throw new IllegalArgumentException(
                        UtilConstants.ERRMSG_SELECTOR_MISSING_1
                            + UtilConstants.RUNPARM_PROOF_ASST_EXPORT_TO_FILE
                            + UtilConstants.ERRMSG_SELECTOR_MISSING_2 + "1"
                            + UtilConstants.ERRMSG_SELECTOR_MISSING_3);
            }
        }

        final Reader importReader = editProofAsstImportFileRunParm(runParm,
            UtilConstants.RUNPARM_PROOF_ASST_BATCH_TEST, 2);

        if (importReader == null)
            proofAsst.importFromMemoryAndUnify(messages, selectorAll,
                selectorCount, selectorTheorem, outputBoss, asciiRetest);
        else {
            proofAsst.importFromFileAndUnify(importReader, messages,
                selectorAll, selectorCount, selectorTheorem, outputBoss,
                asciiRetest);
            importReader.close();
        }

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     *  Exercises the StepSelectorSearch code.
     *
     *  @param runParm RunParmFile line.
     */
    public void doStepSelectorBatchTest(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();
        batchFramework.outputBoss.printAndClearMessages();

        final OutputBoss outputBoss = batchFramework.outputBoss;

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_STEP_SELECTOR_BATCH_TEST, 3);

        // 1st option
        final Reader importReader = editProofAsstImportFileRunParm(runParm,
            UtilConstants.RUNPARM_STEP_SELECTOR_BATCH_TEST, 1);

        // 2st option
        final int cursorPos = editRunParmValueInteger(runParm.values[1].trim(),
            UtilConstants.RUNPARM_STEP_SELECTOR_BATCH_TEST).intValue();

        // 3rd option
        final int selectionNumber = editRunParmValueInteger(
            runParm.values[2].trim(),
            UtilConstants.RUNPARM_STEP_SELECTOR_BATCH_TEST).intValue();

        proofAsst.stepSelectorBatchTest(importReader, messages, outputBoss,
            cursorPos, selectionNumber);

        importReader.close();

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     *  Exercises the PreprocessRequest code.
     *
     *  @param runParm RunParmFile line.
     */
    public void doPreprocessRequestBatchTest(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();
        batchFramework.outputBoss.printAndClearMessages();

        final OutputBoss outputBoss = batchFramework.outputBoss;

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PREPROCESS_REQUEST_BATCH_TEST, 2);

        // 1st option
        final Reader r = editProofAsstImportFileRunParm(runParm,
            UtilConstants.RUNPARM_PREPROCESS_REQUEST_BATCH_TEST, 1);
        final StringWriter w = new StringWriter();
        int c = 0;
        while ((c = r.read()) != -1)
            w.write(c);
        final String proofText = w.toString();
        r.close();

        // 2st option
        final PreprocessRequest preprocessRequest = editPreprocessRequestOption(
            runParm.values[1].trim(),
            UtilConstants.RUNPARM_PREPROCESS_REQUEST_BATCH_TEST);

        proofAsst.preprocessRequestBatchTest(proofText, messages, outputBoss,
            preprocessRequest);

        batchFramework.outputBoss.printAndClearMessages();
    }

    private PreprocessRequest editPreprocessRequestOption(final String s,
        final String valueCaption) throws IllegalArgumentException
    {
        if (s
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_ERASE_AND_REDERIVE_FORMULAS) == 0)
            return new EraseWffsPreprocessRequest();

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_PREPROCESS_OPTION_UNRECOG_1 + s);
    }

    /**
     *  Validate input ProofAsst Import File RunParm option
     *  and returns Buffered File Reader object.
     *
     *  @param runParm RunParmFile line parsed into RunParmArrayEntry.
     *  @param valueCaption name of RunParm, for error message output.
     *  @param valueFieldNbr option number of file name
     */
    public Reader editProofAsstImportFileRunParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return null;

        final String fileNameParm = runParm.values[valueFieldNbr - 1].trim();
        if (fileNameParm.length() == 0)
            return null;

        return doConstructBufferedFileReader(valueCaption, fileNameParm,
            getProofAsstPreferences().getProofFolder());
    }

    /**
     *  Validate output ProofAsst Export File RunParm options
     *  and returns FileWriter object
     *
     *  @param runParm RunParmFile line parsed into RunParmArrayEntry.
     *  @param valueCaption name of RunParm, for error message output.
     */
    protected BufferedWriter editProofAsstExportFileRunParm(
        final RunParmArrayEntry runParm, final String valueCaption)
        throws IllegalArgumentException
    {

        final String fileNameParm = editFileNameParm(runParm, valueCaption, 2);

        final String fileUsageParm = editFileUsageParm(runParm, valueCaption, 3);

        final boolean exportFormatUnified = editProofAsstExportFormatUnifiedParm(
            runParm, valueCaption, 4);
        getProofAsstPreferences().setExportFormatUnified(exportFormatUnified);

        final boolean exportHypsRandomized = editProofAsstExportHypsRandomizedParm(
            runParm, valueCaption, 5);
        getProofAsstPreferences().setExportHypsRandomized(exportHypsRandomized);

        final boolean exportDeriveFormulas = editProofAsstExportDeriveFormulasParm(
            runParm, valueCaption, 7);
        getProofAsstPreferences().setExportDeriveFormulas(exportDeriveFormulas);

        return doConstructBufferedFileWriter(valueCaption, fileNameParm,
            fileUsageParm, getProofAsstPreferences().getProofFolder());
    }

    /**
     *  Validate Proof Assistant Export Format Parm ("unified"
     *  or "un-unified").
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean unified or un-unified proof format parm
     */
    protected boolean editProofAsstExportFormatUnifiedParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_EXPORT_FORMAT_UNIFIED_DEFAULT;

        final String exportFormatUnifiedParm = runParm.values[valueFieldNbr - 1]
            .trim();
        if (exportFormatUnifiedParm.length() == 0)
            return PaConstants.PROOF_ASST_EXPORT_FORMAT_UNIFIED_DEFAULT;

        if (exportFormatUnifiedParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_EXPORT_UNIFIED) == 0)
            return true;

        if (exportFormatUnifiedParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_EXPORT_UN_UNIFIED) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_1 + valueCaption
                + UtilConstants.ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_EXPORT_UNIFIED
                + UtilConstants.ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_EXPORT_UN_UNIFIED
                + UtilConstants.ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_5
                + exportFormatUnifiedParm
                + UtilConstants.ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_6);
    }

    /**
     *  Validate Proof Assistant Export Hyps Randomized Parm
     *  ("Randomized" or "NotRandomized").
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean Randomized or NotRandomized proof format parm
     */
    protected boolean editProofAsstExportHypsRandomizedParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_EXPORT_HYPS_RANDOMIZED_DEFAULT;

        final String exportHypsRandomizedParm = runParm.values[valueFieldNbr - 1]
            .trim();
        if (exportHypsRandomizedParm.length() == 0)
            return PaConstants.PROOF_ASST_EXPORT_HYPS_RANDOMIZED_DEFAULT;

        if (exportHypsRandomizedParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_RANDOMIZED) == 0)
            return true;

        if (exportHypsRandomizedParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_NOT_RANDOMIZED) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_1
                + valueCaption
                + UtilConstants.ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_RANDOMIZED
                + UtilConstants.ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_NOT_RANDOMIZED
                + UtilConstants.ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_5
                + exportHypsRandomizedParm
                + UtilConstants.ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_6);
    }

    /**
     *  Validate Proof Assistant Export DeriveFormulas Parm
     *  ("DeriveFormulas" or "NoDeriveFormulas" or "").
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean DeriveFormulas or NoDeriveFormulas parm
     */
    protected boolean editProofAsstExportDeriveFormulasParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_EXPORT_DERIVE_FORMULAS_DEFAULT;

        final String exportDeriveFormulasParm = runParm.values[valueFieldNbr - 1]
            .trim();
        if (exportDeriveFormulasParm.length() == 0)
            return PaConstants.PROOF_ASST_EXPORT_DERIVE_FORMULAS_DEFAULT;

        if (exportDeriveFormulasParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_DERIVE_FORMULAS) == 0)
            return true;

        if (exportDeriveFormulasParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_DERIVE_FORMULAS) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_1
                + valueCaption
                + UtilConstants.ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_DERIVE_FORMULAS
                + UtilConstants.ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_DERIVE_FORMULAS
                + UtilConstants.ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_5
                + exportDeriveFormulasParm
                + UtilConstants.ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_6);
    }

    /**
     *  Validate Proof Assistant Import CompareDJs Parm
     *  ("CompareDJs" or "NoCompareDJs" or "").
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean CompareDJs or NoCompareDJs parm
     */
    protected boolean editProofAsstImportCompareDJsParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_IMPORT_COMPARE_DJS_DEFAULT;

        final String importCompareDJsParm = runParm.values[valueFieldNbr - 1]
            .trim();
        if (importCompareDJsParm.length() == 0)
            return PaConstants.PROOF_ASST_IMPORT_COMPARE_DJS_DEFAULT;

        if (importCompareDJsParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_COMPARE_DJS) == 0)
            return true;

        if (importCompareDJsParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_COMPARE_DJS) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_1
                + valueCaption
                + UtilConstants.ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_COMPARE_DJS
                + UtilConstants.ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_COMPARE_DJS
                + UtilConstants.ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_5
                + importCompareDJsParm
                + UtilConstants.ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_6);
    }

    /**
     *  Validate Proof Assistant Import UpdateDJs Parm
     *  ("UpdateDJs" or "NoUpdateDJs" or "").
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean UpdateDJs or NoUpdateDJs parm
     */
    protected boolean editProofAsstImportUpdateDJsParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_IMPORT_UPDATE_DJS_DEFAULT;

        final String importUpdateDJsParm = runParm.values[valueFieldNbr - 1]
            .trim();
        if (importUpdateDJsParm.length() == 0)
            return PaConstants.PROOF_ASST_IMPORT_UPDATE_DJS_DEFAULT;

        if (importUpdateDJsParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_UPDATE_DJS) == 0)
            return true;

        if (importUpdateDJsParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_UPDATE_DJS) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_1
                + valueCaption
                + UtilConstants.ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_UPDATE_DJS
                + UtilConstants.ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_UPDATE_DJS
                + UtilConstants.ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_5
                + importUpdateDJsParm
                + UtilConstants.ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_6);

    }

    /**
     *  Validate Proof Assistant AsciiRetest Parm.
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean AsciiRetest or NoAsciiRetest parm
     */
    protected boolean editProofAsstAsciiRetestParm(
        final RunParmArrayEntry runParm, final String valueCaption,
        final int valueFieldNbr) throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_ASCII_RETEST_DEFAULT;

        final String asciiRetestParm = runParm.values[valueFieldNbr - 1].trim();
        if (asciiRetestParm.length() == 0)
            return PaConstants.PROOF_ASST_ASCII_RETEST_DEFAULT;

        if (asciiRetestParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_ASCII_RETEST) == 0)
            return true;

        if (asciiRetestParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_NO_ASCII_RETEST) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_ASCII_RETEST_PARM_UNRECOG_1 + valueCaption
                + UtilConstants.ERRMSG_ASCII_RETEST_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_ASCII_RETEST_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_ASCII_RETEST
                + UtilConstants.ERRMSG_ASCII_RETEST_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_NO_ASCII_RETEST
                + UtilConstants.ERRMSG_ASCII_RETEST_PARM_UNRECOG_5
                + asciiRetestParm
                + UtilConstants.ERRMSG_ASCII_RETEST_PARM_UNRECOG_6);
    }

    /**
     *  Validate Proof Assistant Export Print Parm
     *  ("Print" or "NoPrint").
     *
     *  @param runParm        RunParmFile line.
     *  @param valueCaption   name of RunParm, for error message
     *                        output.
     *  @param valueFieldNbr  number of field in RunParm line.
     *
     *  @return boolean Print or NoPrint of Proof Worksheets
     */
    protected boolean editProofAsstPrintParm(final RunParmArrayEntry runParm,
        final String valueCaption, final int valueFieldNbr)
        throws IllegalArgumentException
    {

        if (runParm.values.length < valueFieldNbr)
            return PaConstants.PROOF_ASST_PRINT_DEFAULT;

        final String printParm = runParm.values[valueFieldNbr - 1].trim();
        if (printParm.length() == 0)
            return PaConstants.PROOF_ASST_PRINT_DEFAULT;

        if (printParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_PRINT) == 0)
            return true;

        if (printParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_PRINT) == 0)
            return false;

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_EXPORT_PRINT_PARM_UNRECOG_1 + valueCaption
                + UtilConstants.ERRMSG_EXPORT_PRINT_PARM_UNRECOG_2
                + valueFieldNbr
                + UtilConstants.ERRMSG_EXPORT_PRINT_PARM_UNRECOG_3
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_PRINT
                + UtilConstants.ERRMSG_EXPORT_PRINT_PARM_UNRECOG_4
                + UtilConstants.RUNPARM_OPTION_PROOF_ASST_NO_PRINT
                + UtilConstants.ERRMSG_EXPORT_PRINT_PARM_UNRECOG_5 + printParm
                + UtilConstants.ERRMSG_EXPORT_PRINT_PARM_UNRECOG_6);
    }

    /**
     *  Executes the RunProofAsstGUI command, prints any messages,
     *  etc.
     *
     *  @param runParm RunParmFile line.
     */
    public void doRunProofAsstGUI(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();

        // do not init lookup tables unless actually
        // planning to use them!
        if (!proofAsst.getInitializedOK())
            proofAsst.initializeLookupTables(messages);

        // start GUI w/clear Messages area; GUI will
        // use Messages but display on error screen (frame).
        batchFramework.outputBoss.printAndClearMessages();

        // turn off "startupMode" in MMJ2FailPopupWindow
        // so that it only appears for "Fail" type messages
        // from this point onward.
        batchFramework.mmj2FailPopupWindow.terminateStartupMode();

        proofAsst.doGUI(messages);

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     *  Fetches a reference to the ProofAsstPreferences,
     *  first initializing it if necessary.
     *
     *  Note: must re-initialize the TMFFPreferences
     *        reference in ProofAsstPreferences because
     *        TMFFBoss controls which instance of
     *        TMFFPreferences is active!!!
     *
     *  @return ProofAsstPreferences object ready to go.
     */
    public ProofAsstPreferences getProofAsstPreferences() {

        if (proofAsstPreferences == null) {
            proofAsstPreferences = new ProofAsstPreferences();
            proofAsstPreferences.setTMFFPreferences(batchFramework.tmffBoss
                .getTMFFPreferences());
        }

        return proofAsstPreferences;
    }
}
