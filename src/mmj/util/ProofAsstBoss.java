//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ProofAsstBoss.java  0.12 11/01/2011
 *
 * Version 0.02:
 *     - New RunParms to support Proof Assistant "Derive" feature:
 *         - MaxUnifyAlternates
 *         - Dummy Var Prefix
 *
 * Sep-03-2006:
 *     -->Add TMFF stuff
 *
 * Version 0.04 - 06/01/2007
 *     -->Modify to not validate Font Family Name
 *     -->Added ProofAsstDjVarsSoftErrors RunParm
 *
 * Version 0.05 - 08/01/2007
 *     -->Added code to get/init workVarManager instance
 *        and add it to ProofAsstPreferences before
 *        returning a ProofAsst instance in getProofAsst().
 *     -->remove Dummy Var processing (deprecate RunParm too).
 *     -->Added "AsciiRetest" option to BatchTest RunParm
 *
 * Version 0.06 - 11/01/2007
 *     - Add "ProofAsstTextRows"            RunParm
 *     - Add "ProofAsstErrorMessageRows"    RunParm
 *     - Add "ProofAsstErrorMessageColumns" RunParm
 *     - Add "ProofAsstTextAtTop"           RunParm
 *
 * Version 0.07 - 02/01/2008
 *     - Add "ProofAsstIncompleteStepCursor"        RunParm
 *     - Add "ProofAsstOutputCursorInstrumentation" RunParm
 *     - Add "ProofAsstAutoReformat"                RunParm
 *
 * Version 0.08 - 03/01/2008
 *     - Add "StepSelectorMaxResults"               RunParm
 *     - Add "StepSelectorShowSubstitutions"        RunParm
 *     - Add "StepSelectorDialogPaneWidth"          RunParm
 *     - Add "StepSelectorDialogPaneHeight"         RunParm
 *     - Remove Unify+Get Hints feature, deprecate
 *       hint-related RunParms
 *     - Add "StepSelectorBatchTest"                RunParm
 *     - Add "PreprocessRequestBatchTest"           RunParm
 *
 * Version 0.09 - 08/01/2008
 *     - Clear ProofAsstPreferences when LoadFile RunParm
 *       encountered. It can't be hanging around after that.
 *     - changed editProofAsstImportFileRunParm() from
 *       protected access to public for use by TheoremLoaderBoss.
 *     - Add ProofAsstAssrtListFreespace RunParm allowing
 *       user to set to 0 thru 1000.
 *
 * Version 0.10 - Nov-01-2011
 *     -->Modified for mmj2 Paths Enhancement
 *        --> added mmj2Path arg to editProofAsstProofFolder() call
 *     -->Added code for MMJ2FailPopupWindow
 *
 * Version 0.11 - Aug-01-2013:
 *     - Add "ProofAsstProofFormat"                 RunParm
 *
 * Version 0.12 - Aug-11-2013:
 *     - Add "ProofAsstLookAndFeel"                 RunParm
 */

package mmj.util;

import static mmj.util.UtilConstants.*;

import java.awt.Color;
import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import mmj.lang.*;
import mmj.pa.*;
import mmj.pa.MacroManager.ExecutionMode;
import mmj.tl.TheoremLoader;
import mmj.verify.*;

/**
 * Responsible for building and triggering ProofAsst.
 * <ul>
 * <li>Remember that Messages, LogicalSystem and other objects may have changed.
 * Don't worry about whether or not file is loaded, the LogicalSystemBoss will
 * throw an exception if attempt is made to retrieve LogicalSystem if it is not
 * loaded and error free.
 * <li>If clear, RunParm values to null, etc.
 * </ul>
 */
@SuppressWarnings("deprecation")
public class ProofAsstBoss extends Boss {

    private ProofAsst proofAsst;

    private ProofAsstPreferences proofAsstPreferences;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public ProofAsstBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        final BooleanSupplier clear = () -> {
            proofAsst = null;
            proofAsstPreferences = null;
            return false; // not "consumed"
        };
        putCommand(RUNPARM_CLEAR, clear);
        putCommand(RUNPARM_LOAD_FILE, clear);

        putCommand(RUNPARM_PROOF_ASST_LOOK_AND_FEEL,
            this::editProofAsstLookAndFeel);

        putCommand(RUNPARM_PROOF_ASST_DJ_VARS_SOFT_ERRORS,
            this::editProofAsstDjVarsSoftErrors);

        putCommand(RUNPARM_PROOF_ASST_PROOF_FORMAT,
            this::editProofAsstProofFormat);

        putCommand(RUNPARM_PROOF_ASST_INCOMPLETE_STEP_CURSOR,
            this::editProofAsstIncompleteStepCursor);

        putCommand(RUNPARM_PROOF_ASST_HIGHLIGHTING_ENABLED,
            this::editProofAsstHighlightingEnabled);

        putCommand(RUNPARM_PROOF_ASST_HIGHLIGHTING_STYLE,
            this::editProofAsstHighlightingStyle);

        putCommand(RUNPARM_PROOF_ASST_FOREGROUND_COLOR_RGB,
            this::editProofAsstForegroundColorRGB);

        putCommand(RUNPARM_PROOF_ASST_BACKGROUND_COLOR_RGB,
            this::editProofAsstBackgroundColorRGB);

        putCommand(RUNPARM_PROOF_ASST_FONT_SIZE, this::editProofAsstFontSize);

        putCommand(RUNPARM_PROOF_ASST_FONT_FAMILY,
            this::editProofAsstFontFamily);

        putCommand(RUNPARM_PROOF_ASST_FONT_BOLD, this::editProofAsstFontBold);

        putCommand(RUNPARM_PROOF_ASST_LINE_SPACING,
            this::editProofAsstLineSpacing);

        putCommand(RUNPARM_PROOF_ASST_TEXT_COLUMNS,
            this::editProofAsstTextColumns);

        putCommand(RUNPARM_PROOF_ASST_TEXT_ROWS, this::editProofAsstTextRows);

        putCommand(RUNPARM_PROOF_ASST_ERROR_MESSAGE_ROWS,
            this::editProofAsstErrorMessageRows);

        putCommand(RUNPARM_PROOF_ASST_ERROR_MESSAGE_COLUMNS,
            this::editProofAsstErrorMessageColumns);

        putCommand(RUNPARM_PROOF_ASST_MAXIMIZED, this::editProofAsstMaximized);

        putCommand(RUNPARM_PROOF_ASST_TEXT_AT_TOP,
            this::editProofAsstTextAtTop);

//->lineWrap disabled because couldn't get Java Swing to work with it
//        putCommand(RUNPARM_PROOF_ASST_LINE_WRAP,
//            this::editProofAsstLineWrap);

        putCommand(RUNPARM_PROOF_ASST_FORMULA_LEFT_COL,
            this::editProofAsstFormulaLeftCol);

        putCommand(RUNPARM_PROOF_ASST_FORMULA_RIGHT_COL,
            this::editProofAsstFormulaRightCol);

        putCommand(RUNPARM_PROOF_ASST_RPN_PROOF_LEFT_COL,
            this::editProofAsstRPNProofLeftCol);

        putCommand(RUNPARM_PROOF_ASST_RPN_PROOF_RIGHT_COL,
            this::editProofAsstRPNProofRightCol);

        final Runnable NULL = null;
        putCommand(RUNPARM_PROOF_ASST_MAX_UNIFY_ALTERNATES, NULL);

        putCommand(RUNPARM_PROOF_ASST_MAX_UNIFY_HINTS, NULL);

        putCommand(RUNPARM_PROOF_ASST_UNIFY_HINTS_IN_BATCH, NULL);

        putCommand(RUNPARM_STEP_SELECTOR_MAX_RESULTS,
            this::editStepSelectorMaxResults);

        putCommand(RUNPARM_STEP_SELECTOR_SHOW_SUBSTITUTIONS,
            this::editStepSelectorShowSubstitutions);

        putCommand(RUNPARM_STEP_SELECTOR_DIALOG_PANE_WIDTH,
            this::editStepSelectorDialogPaneWidth);

        putCommand(RUNPARM_STEP_SELECTOR_DIALOG_PANE_HEIGHT,
            this::editStepSelectorDialogPaneHeight);

        putCommand(RUNPARM_PROOF_ASST_ASSRT_LIST_FREESPACE,
            this::editProofAsstAssrtListFreespace);

        putCommand(RUNPARM_PROOF_ASST_OUTPUT_CURSOR_INSTRUMENTATION,
            this::editProofAsstOutputCursorInstrumentation);

        putCommand(RUNPARM_PROOF_ASST_AUTO_REFORMAT,
            this::editProofAsstAutoReformat);

        putCommand(RUNPARM_PROOF_ASST_UNDO_REDO_ENABLED,
            this::editProofAsstUndoRedoEnabled);

        putCommand(RUNPARM_PROOF_ASST_DUMMY_VAR_PREFIX, NULL);

        putCommand(RUNPARM_PROOF_ASST_DEFAULT_FILE_NAME_SUFFIX,
            this::editProofAsstDefaultFileNameSuffix);

        putCommand(RUNPARM_PROOF_ASST_PROOF_FOLDER,
            this::editProofAsstProofFolder);

        putCommand(RUNPARM_RECHECK_PROOF_ASST_USING_PROOF_VERIFIER,
            this::editRecheckProofAsstUsingProofVerifier);

        putCommand(RUNPARM_PROOF_ASST_UNIFY_SEARCH_EXCLUDE,
            this::editProofAsstUnifySearchExclude);

        putCommand(RUNPARM_PROOF_ASST_EXPORT_TO_FILE,
            this::doProofAsstExportToFile);

        putCommand(RUNPARM_PROOF_ASST_BATCH_TEST, this::doProofAsstBatchTest);

        putCommand(RUNPARM_PROOF_ASST_OPTIMIZE_THEOREM_SEARCH,
            this::doProofAsstTheoremSearchOptimization);

        putCommand(RUNPARM_PROOF_ASST_AUTOCOMPLETE_ENABLED,
            this::doProofAsstAutocompleteEnabled);

        putCommand(RUNPARM_PROOF_ASST_USE_AUTOTRANSFORMATIONS,
            this::doProofAsstUseAutotransformations);

        putCommand(RUNPARM_PROOF_ASST_DERIVE_AUTOCOMPLETE,
            this::doProofAsstDeriveAutocomplete);

        putCommand(RUNPARM_STEP_SELECTOR_BATCH_TEST,
            this::doStepSelectorBatchTest);

        putCommand(RUNPARM_PREPROCESS_REQUEST_BATCH_TEST,
            this::doPreprocessRequestBatchTest);

        putCommand(RUNPARM_PROOF_ASST_STARTUP_PROOF_WORKSHEET,
            this::editProofAsstStartupProofWorksheet);

        putCommand(RUNPARM_SET_MM_DEFINITIONS_CHECK,
            this::doSetMMDefinitionsCheck);

        putCommand(RUNPARM_RUN_PROOF_ASST_GUI, this::doRunProofAsstGUI);
    }

    /**
     * Fetch a ProofAsst object.
     *
     * @return ProofAsst object, ready to go, or null;.
     */
    public ProofAsst getProofAsst() {

        if (proofAsst != null)
            return proofAsst;

        final Messages messages = batchFramework.outputBoss.getMessages();

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final VerifyProofs verifyProofs = batchFramework.verifyProofBoss
            .getVerifyProofs();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        if (grammar.getGrammarInitialized() && batchFramework.grammarBoss
            .getAllStatementsParsedSuccessfully())
        {

            final ProofAsstPreferences proofAsstPreferences = getProofAsstPreferences();

            final WorkVarManager workVarManager = batchFramework.workVarBoss
                .getWorkVarManager();

            if (!workVarManager.areWorkVarsDeclared())
                try {
                    workVarManager.declareWorkVars(grammar, logicalSystem);
                } catch (final VerifyException e) {
                    messages.accumException(e);
                }

            proofAsstPreferences.setWorkVarManager(workVarManager);

            final TheoremLoader theoremLoader = batchFramework.theoremLoaderBoss
                .getTheoremLoader();

            final MacroManager macroManager = batchFramework.macroBoss
                .getMacroManager(false);

            proofAsst = new ProofAsst(proofAsstPreferences, logicalSystem,
                grammar, verifyProofs, theoremLoader, macroManager);

            if (!proofAsst.getInitializedOK())
                proofAsst.initializeLookupTables(messages);

            logicalSystem.accumTheoremLoaderCommitListener(proofAsst);

        }
        else {
            proofAsst = null;
            messages.accumMessage(ERRMSG_PA_REQUIRES_GRAMMAR_INIT);
        }

        batchFramework.outputBoss.printAndClearMessages();

        return proofAsst;
    }

    /**
     * edit ProofAsstLookAndFeel RunParm.
     */
    protected void editProofAsstLookAndFeel() {
        final LookAndFeelInfo[] lafs = UIManager.getInstalledLookAndFeels();
        final String[] names = new String[lafs.length];
        for (int i = 0; i < lafs.length; i++)
            names[i] = lafs[i].getName();
        try {
            for (final LookAndFeelInfo info : lafs)
                if (get(1).equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    return;
                }
        } catch (final Exception e) {
            throw error(PaConstants.ERRMSG_SET_LOOK_AND_FEEL, get(1),
                Arrays.toString(names));
        }
        throw error(PaConstants.ERRMSG_LOOK_AND_FEEL_MISSING, get(1),
            Arrays.toString(names));
    }
    /**
     * edit ProofAsstDjVarsSoftErrors RunParm.
     */
    protected void editProofAsstDjVarsSoftErrors() {
        getProofAsstPreferences().djVarsSoftErrors.setSerial(get(1));
    }
    /**
     * edit ProofAsstProofFormat RunParm.
     */
    protected void editProofAsstProofFormat() {
        getProofAsstPreferences().proofFormat.setSerial(get(1));
    }

    /**
     * edit ProofAsstIncompleteStepCursor RunParm.
     */
    protected void editProofAsstIncompleteStepCursor() {
        getProofAsstPreferences().incompleteStepCursor.setSerial(get(1));
    }

    /**
     * Validate ProofAsstHighlightingEnabled RunParm.
     */
    protected void editProofAsstHighlightingEnabled() {
        getProofAsstPreferences().highlightingEnabled.set(getYesNo(1));
    }

    /**
     * Validate ProofAsstHighlightingEnabled RunParm.
     */
    protected void editProofAsstHighlightingStyle() {
        require(4);
        Color color = null;
        Boolean bold = null, italic = null;
        if (!get(2).equalsIgnoreCase(RUNPARM_OPTION_INHERIT)) {
            if (!runParm.values[1].matches("[0-9A-Fa-f]{6}"))
                throw error(ERRMSG_RUNPARM_RGB_FORMAT, runParm.values[1]);
            color = new Color(Integer.parseInt(get(2), 16));
        }
        if (!get(3).equalsIgnoreCase(RUNPARM_OPTION_INHERIT))
            bold = getYesNo(3);
        if (!get(4).equalsIgnoreCase(RUNPARM_OPTION_INHERIT))
            italic = getYesNo(4);

        try {
            getProofAsstPreferences().setHighlightingStyle(get(1), color, bold,
                italic);
        } catch (final IllegalArgumentException e) {
            throw error(ERRMSG_RUNPARM_PA_STYLE_UNKNOWN, e, e.getMessage());
        }
    }
    /**
     * Validate ProofAsstForegroundColorRGB RunParm.
     */
    protected void editProofAsstForegroundColorRGB() {
        getProofAsstPreferences().foregroundColor.set(getColor());
    }

    /**
     * Validate ProofAsstBackgroundColorRGB RunParm.
     */
    protected void editProofAsstBackgroundColorRGB() {
        getProofAsstPreferences().backgroundColor.set(getColor());
    }

    /**
     * Validate ProofAsstFontSize RunParm.
     */
    protected void editProofAsstFontSize() {
        getProofAsstPreferences().fontSize.set(getInt(1));
    }

    /**
     * Validate ProofAsstFontFamily RunParm.
     */
    protected void editProofAsstFontFamily() {
        getProofAsstPreferences().fontFamily.set(get(1));
    }

    /**
     * Validate ProofAsstFontBold RunParm.
     */
    protected void editProofAsstFontBold() {
        getProofAsstPreferences().fontBold.set(getYesNo(1));
    }

    /**
     * Validate ProofAsstLineSpacing RunParm.
     */
    protected void editProofAsstLineSpacing() {
        try {
            getProofAsstPreferences().lineSpacing.set(Float.valueOf(get(1)));
        } catch (final NumberFormatException e) {
            throw error(e, ERRMSG_RUNPARM_FLOAT_FORMAT_ERROR, e.getMessage());
        }
    }
    /**
     * Validate ProofAsstTextColumns
     */
    protected void editProofAsstTextColumns() {
        getProofAsstPreferences().tmffPreferences.textColumns.set(getInt(1));
    }

    /**
     * Validate ProofAsstTextRows
     */
    protected void editProofAsstTextRows() {
        getProofAsstPreferences().tmffPreferences.textRows.set(getInt(1));
    }

    /**
     * Validate ProofAsstErrorMessageRows
     */
    protected void editProofAsstErrorMessageRows() {
        getProofAsstPreferences().errorMessageRows.set(getInt(1));
    }

    /**
     * Validate ProofAsstErrorMessageColumns
     */
    protected void editProofAsstErrorMessageColumns() {
        getProofAsstPreferences().errorMessageColumns.set(getInt(1));
    }

    /**
     * Validate ProofAsstMaximized RunParm.
     */
    protected void editProofAsstMaximized() {
        getProofAsstPreferences().maximized.set(getYesNo(1));
    }

    /**
     * Validate ProofAsstTextAtTop RunParm.
     */
    protected void editProofAsstTextAtTop() {
        getProofAsstPreferences().textAtTop.set(getYesNo(1));
    }

    /**
     * Validate ProofAsstFormulaLeftCol.
     */
    protected void editProofAsstFormulaLeftCol() {
        getProofAsstPreferences().tmffPreferences.formulaLeftCol.set(getInt(1));
    }

    /**
     * Validate ProofAsstFormulaRightCol.
     */
    protected void editProofAsstFormulaRightCol() {
        getProofAsstPreferences().tmffPreferences.formulaRightCol
            .set(getInt(1));
    }

    /**
     * Validate ProofAsstRPNProofLeftCol.
     */
    protected void editProofAsstRPNProofLeftCol() {
        getProofAsstPreferences().rpnProofLeftCol.set(getInt(1));
    }

    /**
     * Validate ProofAsstRPNProofRightCol.
     */
    protected void editProofAsstRPNProofRightCol() {
        getProofAsstPreferences().rpnProofRightCol.set(getInt(1));
    }

    /**
     * Validate StepSelectorMaxResults
     * <p>
     * Must be positive integer.
     */
    protected void editStepSelectorMaxResults() {
        getProofAsstPreferences().stepSelectorMaxResults.set(getPosInt(1));
    }

    /**
     * Validate StepSelectorShowSubstitutions
     * <p>
     * Must be yes, no, on, off, true, false.
     */
    protected void editStepSelectorShowSubstitutions() {
        getProofAsstPreferences().stepSelectorShowSubstitutions
            .setString(get(1));
    }

    /**
     * Validate StepSelectorDialogPaneWidth.
     */
    protected void editStepSelectorDialogPaneWidth() {
        getProofAsstPreferences().stepSelectorDialogPaneWidth.set(getPosInt(1));
    }

    /**
     * Validate StepSelectorDialogPaneHeight.
     */
    protected void editStepSelectorDialogPaneHeight() {
        getProofAsstPreferences().stepSelectorDialogPaneHeight
            .set(getPosInt(1));
    }

    /**
     * Validate ProofAsstAssrtListFreespace.
     */
    protected void editProofAsstAssrtListFreespace() {
        getProofAsstPreferences().assrtListFreespace.set(getNonnegInt(1));
    }

    /**
     * Validate Proof Assistant Default File Name Suffix
     */
    protected void editProofAsstDefaultFileNameSuffix() {
        getProofAsstPreferences().defaultFileNameSuffix
            .set(getFileNameSuffix(1));

    }

    /**
     * Validate Proof Assistant Proof Folder Runparm.
     */
    protected void editProofAsstProofFolder() {
        getProofAsstPreferences().proofFolder
            .set(getExistingFolder(batchFramework.paths.getMMJ2Path(), 1));
    }

    /**
     * Validate Proof Assistant Startup Proof Worksheet Runparm.
     */
    protected void editProofAsstStartupProofWorksheet() {
        getProofAsstPreferences().startupProofWorksheetFile
            .set(getExistingFile(batchFramework.paths.getMMJ2Path(), 1));
    }

    /**
     * Validate ProofAsstOutputCursorInstrumentation RunParm.
     */
    protected void editProofAsstOutputCursorInstrumentation() {
        getProofAsstPreferences().outputCursorInstrumentation.set(getYesNo(1));
    }

    /**
     * Validate ProofAsstAutoReformat RunParm.
     */
    protected void editProofAsstAutoReformat() {
        getProofAsstPreferences().autoReformat.set(getYesNo(1));
    }

    /**
     * Validate ProofAsstUndoRedoEnabled RunParm.
     */
    protected void editProofAsstUndoRedoEnabled() {
        getProofAsstPreferences().undoRedoEnabled.set(getYesNo(1));
    }

    /**
     * Validate RecheckProofAsstUsingProofVerifier
     */
    protected void editRecheckProofAsstUsingProofVerifier() {
        getProofAsstPreferences().recheckProofAsstUsingProofVerifier
            .set(getYesNo(1));
    }

    /**
     * Validate ProofAsstUnifySearchExclude
     */
    protected void editProofAsstUnifySearchExclude() {
        final Grammar grammar = batchFramework.grammarBoss.getGrammar();
        final Cnst provableLogicStmtTyp = grammar
            .getProvableLogicStmtTypArray()[0];

        final Map<String, Stmt> stmtTbl = batchFramework.logicalSystemBoss
            .getLogicalSystem().getStmtTbl();

        getProofAsstPreferences().unifySearchExclude.set(Arrays
            .stream(runParm.values).map(value -> stmtTbl.get(value.trim()))
            .filter(stmt -> stmt instanceof Assrt
                && stmt.getFormula().getTyp() == provableLogicStmtTyp)
            .map(stmt -> stmt.getLabel()).collect(Collectors.toSet()));
    }

    /**
     * Exports currently loaded theorem proofs to an export file.
     */
    public void doProofAsstExportToFile() {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();

        try (Writer exportWriter = getExportFile()) {
            if (exportWriter == null)
                return;

            final int selectorCount = getSelectorCount(1);

            Theorem selectorTheorem = null;
            if (selectorCount == 0 && (selectorTheorem = getSelectorTheorem(1,
                batchFramework.logicalSystemBoss.getLogicalSystem()
                    .getStmtTbl())) == null)
                throw error(ERRMSG_SELECTOR_MISSING, 1);

            final OutputBoss outputBoss = getPrintParm(6)
                ? batchFramework.outputBoss : null;

            proofAsst.exportToFile(exportWriter, messages, selectorCount,
                selectorTheorem, outputBoss);
        } catch (final IOException e) {
            throw error(e, ERRMSG_MISC_IO_ERROR, e.getMessage());
        }

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     * Reads and unifies theorem proofs in test mode.
     */
    public void doProofAsstBatchTest() {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();

        require(1);

        final boolean exportFormatUnified = getExportFormatUnified(3);
        getProofAsstPreferences().exportFormatUnified.set(exportFormatUnified);

        final HypsOrder exportHypsOrder = getHypsOrder(4);
        getProofAsstPreferences().exportHypsOrder.set(exportHypsOrder);

        OutputBoss outputBoss = null;
        if (getPrintParm(5))
            outputBoss = batchFramework.outputBoss;

        final boolean exportDeriveFormulas = getDeriveFormulas(6);
        getProofAsstPreferences().exportDeriveFormulas
            .set(exportDeriveFormulas);

        final boolean importCompareDJs = getCompareDJs(7);
        getProofAsstPreferences().importCompareDJs.set(importCompareDJs);

        final boolean importUpdateDJs = getUpdateDJs(8);
        getProofAsstPreferences().importUpdateDJs.set(importUpdateDJs);

        final boolean asciiRetest = getAsciiRetest(9);

        final int selectorCount = getSelectorCount(1);

        Theorem selectorTheorem = null;
        if (selectorCount == 0 && (selectorTheorem = getSelectorTheorem(1,
            batchFramework.logicalSystemBoss.getLogicalSystem()
                .getStmtTbl())) == null)
            throw error(ERRMSG_SELECTOR_MISSING, 1);

        try (Reader importReader = getImportFile(2)) {

            if (importReader == null)
                proofAsst.importFromMemoryAndUnify(messages, selectorCount,
                    selectorTheorem, outputBoss, asciiRetest);
            else
                proofAsst.importFromFileAndUnify(importReader, messages,
                    selectorCount, selectorTheorem, outputBoss, asciiRetest);
        } catch (final IOException e) {
            throw error(e, ERRMSG_MISC_IO_ERROR, e.getMessage());
        }

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     * Perform the optimizations for theorem search during "parallel"
     * unification
     */
    public void doProofAsstTheoremSearchOptimization() {
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;
        proofAsst.optimizeTheoremSearch();
    }

    /**
     * Perform the initialization of auto-transformation component.
     */
    public void doProofAsstUseAutotransformations() {
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        require(3);
        proofAsst.initAutotransformations(getYesNo(1), getYesNo(2),
            getYesNo(3));
    }

    /**
     * If this option is set then the proof assistant will support autocomplete
     * derivation steps
     */
    public void doProofAsstAutocompleteEnabled() {
        getProofAsstPreferences().autocomplete.set(getYesNo(1));
    }

    /**
     * If this option is set then the proof assistant will support autocomplete
     * derivation steps
     */
    public void doProofAsstDeriveAutocomplete() {
        getProofAsstPreferences().deriveAutocomplete.set(getYesNo(1));
    }

    /**
     * Exercises the StepSelectorSearch code.
     */
    public void doStepSelectorBatchTest() {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final Messages messages = batchFramework.outputBoss.getMessages();
        batchFramework.outputBoss.printAndClearMessages();

        final OutputBoss outputBoss = batchFramework.outputBoss;

        require(3);

        // 1st option
        try (Reader importReader = getImportFile(1)) {
            proofAsst.stepSelectorBatchTest(importReader, messages, outputBoss,
                getInt(2), getInt(3));
        } catch (final IOException e) {}

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     * Exercises the PreprocessRequest code.
     */
    public void doPreprocessRequestBatchTest() {

        // ensures that file loaded and grammar validated
        // successfully, prints error message if not.
        final ProofAsst proofAsst = getProofAsst();
        if (proofAsst == null)
            return;

        final OutputBoss outputBoss = batchFramework.outputBoss;

        final Messages messages = outputBoss.getMessages();
        outputBoss.printAndClearMessages();

        require(2);

        try (Reader r = getImportFile(1)) {
            if (r == null)
                return;
            final StringWriter w = new StringWriter();
            int c = 0;
            while ((c = r.read()) != -1)
                w.write(c);
            final String proofText = w.toString();

            if (!get(2)
                .equalsIgnoreCase(RUNPARM_OPTION_ERASE_AND_REDERIVE_FORMULAS))
                throw error(ERRMSG_PREPROCESS_OPTION_UNRECOG, get(2));
            final PreprocessRequest preprocessRequest = new EraseWffsPreprocessRequest();

            proofAsst.preprocessRequestBatchTest(proofText, messages,
                outputBoss, preprocessRequest);
        } catch (final IOException e) {
            throw error(e, ERRMSG_MISC_IO_ERROR, e.getMessage());
        }

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     * Run the set.mm definition check.
     *
     * @deprecated Use {@link MacroManager#runMacro(ExecutionMode, String[])}
     *             with macro {@code definitionCheck}.
     */
    @Deprecated
    public void doSetMMDefinitionsCheck() {
        final MacroManager macroManager = batchFramework.macroBoss
            .getMacroManager(true);
        final String[] args = new String[runParm.values.length + 1];
        args[0] = "definitionCheck";
        System.arraycopy(runParm.values, 0, args, 1, runParm.values.length);
        macroManager.runMacro(ExecutionMode.RUNPARM, args);
    }

    /**
     * Validate input ProofAsst Import File RunParm option and returns Buffered
     * File Reader object.
     *
     * @param valueFieldNbr option number of file name
     * @return BufferedReader file object.
     * @throws IllegalArgumentException if an error occurred
     */
    public Reader getImportFile(final int valueFieldNbr) {
        final String fileNameParm = opt(valueFieldNbr);
        if (fileNameParm == null)
            return null;
        return buildBufferedFileReader(fileNameParm,
            getProofAsstPreferences().proofFolder.get());
    }

    /**
     * Validate output ProofAsst Export File RunParm options and returns
     * FileWriter object
     *
     * @return BufferedWriter file object.
     * @throws IllegalArgumentException if an error occurred
     */
    protected BufferedWriter getExportFile() {

        require(7);
        getProofAsstPreferences().exportFormatUnified
            .set(getExportFormatUnified(4));

        getProofAsstPreferences().exportHypsOrder.set(getHypsOrder(5));

        getProofAsstPreferences().exportDeriveFormulas
            .set(getDeriveFormulas(7));

        return buildBufferedFileWriter(getFileName(2), getFileUsage(3),
            getProofAsstPreferences().proofFolder.get());
    }

    /**
     * Validate Proof Assistant Export Format Parm ("unified" or "un-unified").
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean unified or un-unified proof format parm
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getExportFormatUnified(final int valueFieldNbr) {
        return getBoolean(valueFieldNbr,
            PaConstants.PROOF_ASST_EXPORT_FORMAT_UNIFIED_DEFAULT,
            RUNPARM_OPTION_PROOF_ASST_EXPORT_UNIFIED,
            RUNPARM_OPTION_PROOF_ASST_EXPORT_UN_UNIFIED);
    }

    /**
     * Validate Proof Assistant Export Hyps Order Parm ("Correct", "Randomized",
     * "Reverse" ,and others (see {@link HypsOrder}).
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return the order
     * @throws IllegalArgumentException if an error occurred
     */
    protected HypsOrder getHypsOrder(final int valueFieldNbr) {
        final String exportHypsRandomizedParm = opt(valueFieldNbr);
        try {
            return getEnum(valueFieldNbr,
                PaConstants.PROOF_ASST_EXPORT_HYPS_ORDER_DEFAULT,
                new MMJException(ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG,
                    valueFieldNbr, HypsOrder.Correct,
                    RUNPARM_OPTION_PROOF_ASST_NOT_RANDOMIZED,
                    HypsOrder.Randomized, HypsOrder.Reverse,
                    HypsOrder.HalfReverse, HypsOrder.Autocomplete,
                    HypsOrder.SomeOrder));
        } catch (final IllegalArgumentException e) {
            // deprecated old version
            if (exportHypsRandomizedParm
                .equalsIgnoreCase(RUNPARM_OPTION_PROOF_ASST_NOT_RANDOMIZED))
                return HypsOrder.Correct;
            throw e;
        }
    }

    /**
     * Validate Proof Assistant Export DeriveFormulas Parm ("DeriveFormulas" or
     * "NoDeriveFormulas" or "").
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean DeriveFormulas or NoDeriveFormulas parm
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getDeriveFormulas(final int valueFieldNbr) {
        return getBoolean(valueFieldNbr,
            PaConstants.PROOF_ASST_EXPORT_DERIVE_FORMULAS_DEFAULT,
            RUNPARM_OPTION_PROOF_ASST_DERIVE_FORMULAS);
    }

    /**
     * Validate Proof Assistant Import CompareDJs Parm ("CompareDJs" or
     * "NoCompareDJs" or "").
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean CompareDJs or NoCompareDJs parm
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getCompareDJs(final int valueFieldNbr) {
        return getBoolean(valueFieldNbr,
            PaConstants.PROOF_ASST_IMPORT_COMPARE_DJS_DEFAULT,
            RUNPARM_OPTION_PROOF_ASST_COMPARE_DJS);
    }

    /**
     * Validate Proof Assistant Import UpdateDJs Parm ("UpdateDJs" or
     * "NoUpdateDJs" or "").
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean UpdateDJs or NoUpdateDJs parm
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getUpdateDJs(final int valueFieldNbr) {
        return getBoolean(valueFieldNbr,
            PaConstants.PROOF_ASST_IMPORT_UPDATE_DJS_DEFAULT,
            RUNPARM_OPTION_PROOF_ASST_UPDATE_DJS);
    }

    /**
     * Validate Proof Assistant AsciiRetest Parm.
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean AsciiRetest or NoAsciiRetest parm
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getAsciiRetest(final int valueFieldNbr) {
        return getBoolean(valueFieldNbr,
            PaConstants.PROOF_ASST_ASCII_RETEST_DEFAULT,
            RUNPARM_OPTION_ASCII_RETEST);
    }

    /**
     * Validate Proof Assistant Export Print Parm ("Print" or "NoPrint").
     *
     * @param valueFieldNbr number of field in RunParm line.
     * @return boolean Print or NoPrint of Proof Worksheets
     * @throws IllegalArgumentException if an error occurred
     */
    protected boolean getPrintParm(final int valueFieldNbr) {
        return getBoolean(valueFieldNbr, PaConstants.PROOF_ASST_PRINT_DEFAULT,
            RUNPARM_OPTION_PROOF_ASST_PRINT);
    }

    /**
     * Executes the RunProofAsstGUI command, prints any messages, etc.
     *
     * @throws IllegalArgumentException if an error occurred
     */
    public void doRunProofAsstGUI() {

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

        batchFramework.storeBoss.autoload();

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
     * Fetches a reference to the ProofAsstPreferences, first initializing it if
     * necessary.
     * <p>
     * Note: must re-initialize the TMFFPreferences reference in
     * ProofAsstPreferences because TMFFBoss controls which instance of
     * TMFFPreferences is active!!!
     *
     * @return ProofAsstPreferences object ready to go.
     */
    public ProofAsstPreferences getProofAsstPreferences() {

        if (proofAsstPreferences == null) {
            proofAsstPreferences = new ProofAsstPreferences(
                batchFramework.storeBoss.getStore());
            proofAsstPreferences.tmffPreferences = batchFramework.tmffBoss
                .getTMFFPreferences();
        }

        return proofAsstPreferences;
    }
}
