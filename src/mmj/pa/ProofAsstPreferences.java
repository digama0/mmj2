//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofAsstPreferences.java  0.09 08/01/2008
 *
 * Version 0.02:
 *     - added new items for the Proof Assistant "Derive"
 *       feature:
 *             - maxUnifyAlternates
 *             - dummy VarPrefix
 *
 * Version 0.03
 *     - moved left/right column info to TMFFPreferences
 *       and stowed instance of TMFFPreferences here.
 *       TMFF will be treated as a necessary sub-system
 *       for Proof Assistant, and its preferences are our
 *       preferences :)
 *     - other misc. preference items added, such as color.
 *
 * Version 0.04, 06/01/2007
 *     - added setDjVarsSoftErrorsOption, etc.
 *
 * Version 0.05, 08/01/2007
 *     - added get/setWorkVarManager(), etc.
 *     - removed dummy var stuff.
 *
 * Varsion 0.06 - 11/01/2007
 *     - Add "ProofAsstErrorMessageRows"    RunParm
 *     - Add "ProofAsstErrorMessageColumns" RunParm
 *     - Add "ProofAsstTextAtTop"           RunParm
 *
 * Varsion 0.07 - 02/01/2008
 *     - Add "ProofAsstIncompleteStepCursor"        RunParm
 *     - Add "ProofAsstOutputCursorInstrumentation" RunParm
 *     - Add "ProofAsstAutoReformat"                RunParm
 *
 * Varsion 0.08 - 03/01/2008
 *     - Add "StepSelectorMaxResults"               RunParm
 *     - Add "StepSelectorShowSubstitutions"        RunParm
 *     - Remove Hints feature
 *     - Remove "ProofAsstMaxUnifyAlternates"       RunParm
 *
 * Varsion 0.09 - 08/01/2008
 *     - Add "ProofAsstAssrtListFreespace"          RunParm
 */

package mmj.pa;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

import mmj.lang.Assrt;
import mmj.lang.LangException;
import mmj.lang.WorkVarManager;
import mmj.search.SearchMgr;
import mmj.tmff.TMFFPreferences;
import mmj.util.UtilConstants;
import mmj.verify.HypsOrder;

/**
 * Holds user settings/preferences used by the Proof Assistant.
 */
public class ProofAsstPreferences {

    private File proofFolder;

    private File startupProofWorksheetFile;

    private String defaultFileNameSuffix;

    private int fontSize;

    private String fontFamily;

    private boolean fontBold;

    private float lineSpacing;

    private int errorMessageRows;
    private int errorMessageColumns;
    private boolean maximized;
    private boolean textAtTop;

    private TMFFPreferences tmffPreferences;

    private int rpnProofLeftCol;

    private int rpnProofRightCol;

    // using VerifyProofs engine.
    private boolean recheckProofAsstUsingProofVerifier;

    private boolean exportFormatUnified;

    // randomizes LogHyps on output proof steps as a test of
    // proof assistant!
    private HypsOrder exportHypsOrder;

    private boolean exportDeriveFormulas;

    private boolean importCompareDJs;
    private boolean importUpdateDJs;

    private Assrt[] unifySearchExclude;

    private int stepSelectorMaxResults;
    private boolean stepSelectorShowSubstitutions;
    private int stepSelectorDialogPaneWidth;
    private int stepSelectorDialogPaneHeight;

    private int assrtListFreespace;

    private boolean outputCursorInstrumentation;
    private boolean autoReformat;

    private boolean undoRedoEnabled;

    private boolean highlightingEnabled;

    private Map<String, SimpleAttributeSet> highlighting;

    private Color foregroundColor;

    private Color backgroundColor;

    private boolean djVarsSoftErrorsIgnore;
    private boolean djVarsSoftErrorsReport;
    private boolean djVarsSoftErrorsGenerate;
    private boolean djVarsSoftErrorsGenerateNew;
    private boolean djVarsSoftErrorsGenerateRepl;
    private boolean djVarsSoftErrorsGenerateDiffs;

    private boolean proofFormatPacked;
    private boolean proofFormatCompressed;

    private WorkVarManager workVarManager;

    private StepUnifier stepUnifier;

    private String incompleteStepCursor;
    private boolean incompleteStepCursorFirst;
    private boolean incompleteStepCursorLast;

    private SearchMgr searchMgr;

    /**
     * Default constructor.
     */
    public ProofAsstPreferences() {
        proofFolder = null;
        startupProofWorksheetFile = null;

        defaultFileNameSuffix = PaConstants.PA_GUI_DEFAULT_FILE_NAME_SUFFIX;

        fontSize = PaConstants.PROOF_ASST_FONT_SIZE_DEFAULT;
        fontBold = PaConstants.PROOF_ASST_FONT_BOLD_DEFAULT;
        fontFamily = PaConstants.PROOF_ASST_FONT_FAMILY_DEFAULT;
        lineSpacing = PaConstants.PROOF_ASST_LINE_SPACING_DEFAULT;

        errorMessageRows = PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_DEFAULT;
        errorMessageColumns = PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_DEFAULT;
        maximized = PaConstants.PROOF_ASST_MAXIMIZED_DEFAULT;
        textAtTop = PaConstants.PROOF_ASST_TEXT_AT_TOP_DEFAULT;

        tmffPreferences = new TMFFPreferences();

        rpnProofLeftCol = PaConstants.PROOF_ASST_RPN_PROOF_LEFT_COL_AUTO;
        rpnProofRightCol = PaConstants.PROOF_ASST_RPN_PROOF_RIGHT_COL_DEFAULT;

        recheckProofAsstUsingProofVerifier = PaConstants.RECHECK_PROOF_ASST_USING_PROOF_VERIFIER_DEFAULT;

        exportFormatUnified = PaConstants.PROOF_ASST_EXPORT_FORMAT_UNIFIED_DEFAULT;

        exportHypsOrder = PaConstants.PROOF_ASST_EXPORT_HYPS_ORDER_DEFAULT;

        exportDeriveFormulas = PaConstants.PROOF_ASST_EXPORT_DERIVE_FORMULAS_DEFAULT;

        importCompareDJs = PaConstants.PROOF_ASST_IMPORT_COMPARE_DJS_DEFAULT;

        importUpdateDJs = PaConstants.PROOF_ASST_IMPORT_UPDATE_DJS_DEFAULT;

        unifySearchExclude = new Assrt[0];

        stepSelectorMaxResults = PaConstants.STEP_SELECTOR_MAX_RESULTS_DEFAULT;

        stepSelectorShowSubstitutions = PaConstants.STEP_SELECTOR_SHOW_SUBSTITUTIONS_DEFAULT;

        stepSelectorDialogPaneWidth = PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_DEFAULT;

        stepSelectorDialogPaneHeight = PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_DEFAULT;

        assrtListFreespace = PaConstants.ASSRT_LIST_FREESPACE_DEFAULT;

        outputCursorInstrumentation = PaConstants.OUTPUT_CURSOR_INSTRUMENTATION_DEFAULT;

        autoReformat = PaConstants.AUTO_REFORMAT_DEFAULT;

        undoRedoEnabled = PaConstants.UNDO_REDO_ENABLED_DEFAULT;

        if (highlightingEnabled = PaConstants.HIGHLIGHTING_ENABLED_DEFAULT) {
            highlighting = new HashMap<String, SimpleAttributeSet>();
            PaConstants.doStyleDefaults(highlighting);
        }
        else
            highlighting = null;

        foregroundColor = PaConstants.DEFAULT_FOREGROUND_COLOR;

        backgroundColor = PaConstants.DEFAULT_BACKGROUND_COLOR;

        setDjVarsSoftErrorsOption(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_DEFAULT);

        setProofFormatOption(PaConstants.PROOF_ASST_PROOF_COMPRESSED);

        setIncompleteStepCursor(PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_DEFAULT);

        // Note: this default constructor is available for test
        // of ProofAsstGUI in batch mode -- but is
        // mainly used by mmj.util.ProofAsstBoss, which
        // is responsible for loading workVarManager!
        // "null" is not a valid default and would
        // eventually result in an exception if not
        // updated with an actual WorkVarmanager. We
        // are *not* invoking the default WorkVarManager()
        // constructor here because, in all likelyhood,
        // all of its work would need to be thrown away
        // and redone with the correct WorkVar settings.
        workVarManager = null;
        stepUnifier = null;

        setSearchMgr(null);
    }
    /**
     * Set proof folder used for storing proof text areas in ProofAsstGUI.
     * 
     * @param proofFolder proof folder used for storing proof text areas
     */
    public void setProofFolder(final File proofFolder) {
        this.proofFolder = proofFolder;
    }

    /**
     * Get proof folder used for storing proof text areas in ProofAsstGUI.
     * 
     * @return proofFolder proof folder used for storing proof text areas
     */
    public File getProofFolder() {
        return proofFolder;
    }

    /**
     * Set startup Proof Worksheet File to be displayed when the ProofAsstGUI is
     * first displayed. in ProofAsstGUI.
     * 
     * @param startupProofWorksheetFile File object or null.
     */
    public void setStartupProofWorksheetFile(
        final File startupProofWorksheetFile)
    {
        this.startupProofWorksheetFile = startupProofWorksheetFile;
    }

    /**
     * Get startup Proof Worksheet File to be displayed when the ProofAsstGUI is
     * first displayed.
     * 
     * @return startupProofWorksheetFile File object or null.
     */
    public File getStartupProofWorksheetFile() {
        return startupProofWorksheetFile;
    }

    /**
     * Set default file name suffix.
     * 
     * @param defaultFileNameSuffix such as ".txt" or ".mmp"
     */
    public void setDefaultFileNameSuffix(final String defaultFileNameSuffix) {
        this.defaultFileNameSuffix = defaultFileNameSuffix;
    }

    /**
     * Get default file name suffix.
     * 
     * @return defaultFileNameSuffix such as ".txt" or ".mmp"
     */
    public String getDefaultFileNameSuffix() {
        return defaultFileNameSuffix;
    }

    /**
     * Set Font Family Name used in ProofAsstGUI.
     * <p>
     * Note: Proof Assistant formatting of formulas (via TMFF) REQUIRES a
     * fixed-width font for symbol alignment! A proportional or variable-width
     * font can be used but symbol alignments may be off.
     * <p>
     * Note: The default is "Monospaced", which works just fine...
     * 
     * @param fontFamily for ProofAsstGUI
     */
    public synchronized void setFontFamily(final String fontFamily) {
        this.fontFamily = fontFamily;
    }

    /**
     * Get Font Family Name used in ProofAsstGUI.
     * 
     * @return font family name used in ProofAsstGUI.
     */
    public synchronized String getFontFamily() {
        return fontFamily;
    }

    /**
     * Set Font style to bold or regular.
     * <p>
     * Note: The default is "Bold", which seems excellent to me.
     * 
     * @param fontBold yes or no parameter.
     */
    public synchronized void setFontBold(final boolean fontBold) {
        this.fontBold = fontBold;
    }

    /**
     * Get Font Bold style parameter used in ProofAsstGUI.
     * 
     * @return fontBold yes or no.
     */
    public synchronized boolean getFontBold() {
        return fontBold;
    }

    /**
     * Set font size used in ProofAsstGUI.
     * <p>
     * NOTE: presently, font size is set in ProofAsstBoss as part of the
     * start-up of ProofAsstGUI, based on a RunParm. Then during operation of
     * ProofAsstGUI the user can increase or decrease the font size used, and
     * those settings propagate to these ProofAsstPreferences (but are not
     * stored externally for use in the next session -- permanent setting should
     * be made in the RunParm file.)
     * 
     * @param fontSize font size for ProofAsstGUI
     */
    public synchronized void setFontSize(final int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * Get font size used in ProofAsstGUI.
     * 
     * @return fontSize font size for ProofAsstGUI.
     */
    public synchronized int getFontSize() {
        return fontSize;
    }

    /**
     * Set line spacing used in ProofAsstGUI.
     * 
     * @param lineSpacing line spacing for ProofAsstGUI
     */
    public synchronized void setLineSpacing(final float lineSpacing) {
        this.lineSpacing = lineSpacing;
    }

    /**
     * Get font size used in ProofAsstGUI.
     * 
     * @return fontSize font size for ProofAsstGUI.
     */
    public synchronized float getLineSpacing() {
        return lineSpacing;
    }

    /**
     * Set line wrap on or off.
     * <p>
     * If line wrap is on then Newlines (carraige returns) will not be used to
     * split formulas. Instead, space characters will be written to fill out the
     * remaining text columns on the line.
     * 
     * @param lineWrap setting, on or off.
     */
    public void setLineWrap(final boolean lineWrap) {
        tmffPreferences.setLineWrap(lineWrap);
    }

    /**
     * Get the current lineWrap setting.
     * 
     * @return lineWrap setting.
     */
    public boolean getLineWrap() {
        return tmffPreferences.getLineWrap();
    }

    /**
     * Set number of text columns used to display formulas.
     * <p>
     * This number is used to line wrapping and basically corresponds to the
     * window used to display formulas.
     * <p>
     * A formula can be longer than this number, and the Frame should scroll --
     * assuming that lineWrap is off and there are no NewLines.
     * 
     * @param textColumns number of text columns.
     */
    public void setTextColumns(final int textColumns) {
        tmffPreferences.setTextColumns(textColumns);
    }

    /**
     * Get number of text columns used to display formulas.
     * <p>
     * This number is used to line wrapping and basically corresponds to the
     * window used to display formulas.
     * <p>
     * A formula can be longer than this number, and the Frame should scroll --
     * assuming that lineWrap is off and there are no NewLines.
     * 
     * @return number of text columns used to display formulas.
     */
    public int getTextColumns() {
        return tmffPreferences.getTextColumns();
    }

    /**
     * Set number of text rows used to display formulas.
     * 
     * @param textRows number of text rows.
     */
    public void setTextRows(final int textRows) {
        tmffPreferences.setTextRows(textRows);
    }

    /**
     * Get number of text rows used to display formulas.
     * 
     * @return number of text rows used to display formulas.
     */
    public int getTextRows() {
        return tmffPreferences.getTextRows();
    }

    /**
     * Set number of error message rows on the ProofAsstGUI.
     * 
     * @param errorMessageRows number of error message rows.
     */
    public void setErrorMessageRows(final int errorMessageRows) {
        this.errorMessageRows = errorMessageRows;
    }

    /**
     * Get number of error message rows on the ProofAsstGUI.
     * 
     * @return number of error message rows
     */
    public int getErrorMessageRows() {
        return errorMessageRows;
    }

    /**
     * Set number of error message columns on the ProofAsstGUI.
     * 
     * @param errorMessageColumns number of error message columns.
     */
    public void setErrorMessageColumns(final int errorMessageColumns) {
        this.errorMessageColumns = errorMessageColumns;
    }

    /**
     * Get number of error message columns on the ProofAsstGUI.
     * 
     * @return number of of error message columns
     */
    public int getErrorMessageColumns() {
        return errorMessageColumns;
    }

    /**
     * Set Proof Maximized option for ProofAsstGUI.
     * 
     * @param maximized true to maximize the view on startup
     */
    public void setMaximized(final boolean maximized) {
        this.maximized = maximized;
    }

    /**
     * Get Proof Maximized option for ProofAsstGUI.
     * 
     * @return the value
     */
    public boolean getMaximized() {
        return maximized;
    }

    /**
     * Set Proof Text At Top option for ProofAsstGUI.
     * 
     * @param textAtTop true to put the main view at the top and messages below.
     */
    public void setTextAtTop(final boolean textAtTop) {
        this.textAtTop = textAtTop;
    }

    /**
     * Get Proof Text At Top option for ProofAsstGUI.
     * 
     * @return the value
     */
    public boolean getTextAtTop() {
        return textAtTop;
    }

    /**
     * Set formula left column used in formatting proof text areas.
     * 
     * @param formulaLeftCol formula LeftCol used for formatting formula text
     *            areas
     */
    public void setFormulaLeftCol(final int formulaLeftCol) {
        tmffPreferences.setFormulaLeftCol(formulaLeftCol);
    }

    /**
     * Get formula left column used in formatting proof text areas.
     * 
     * @return formulaLeftCol formula LeftCol used for formatting formula text
     *         areas
     */
    public int getFormulaLeftCol() {
        return tmffPreferences.getFormulaLeftCol();
    }

    /**
     * Set formula right column used in formatting proof text areas.
     * 
     * @param formulaRightCol formula RightCol used for formatting formula text
     *            areas
     */
    public void setFormulaRightCol(final int formulaRightCol) {
        tmffPreferences.setFormulaRightCol(formulaRightCol);
    }

    /**
     * Get formula right column used in formatting proof text areas.
     * 
     * @return formulaRightCol formula RightCol used for formatting formula text
     *         areas
     */
    public int getFormulaRightCol() {
        return tmffPreferences.getFormulaRightCol();
    }

    /**
     * Set left column number for RPN statement labels when creating
     * ProofAsstWorksheet.GeneratedProofStmt
     * 
     * @param rpnProofLeftCol left column for RPN label
     */
    public void setRPNProofLeftCol(final int rpnProofLeftCol) {
        this.rpnProofLeftCol = rpnProofLeftCol;
    }

    /**
     * Get left column number for RPN statement labels when creating
     * ProofAsstWorksheet.GeneratedProofStmt
     * 
     * @return rpnProofLeftCol left column or RPN label
     */
    public int getRPNProofLeftCol() {
        return rpnProofLeftCol;
    }

    /**
     * Set right column number for RPN statement labels when creating
     * ProofAsstWorksheet.GeneratedProofStmt
     * 
     * @param rpnProofRightCol right column for RPN label
     */
    public void setRPNProofRightCol(final int rpnProofRightCol) {
        this.rpnProofRightCol = rpnProofRightCol;
    }

    /**
     * Get right column number for RPN statement labels when creating
     * ProofAsstWorksheet.GeneratedProofStmt
     * 
     * @return rpnProofRightCol right column or RPN label
     */
    public int getRPNProofRightCol() {
        return rpnProofRightCol;
    }

    /**
     * Set on/off indicator instructing Proof Assistant to double-check every
     * proof steps generated proof tree using the Proof Engine
     * (mmj.verify.VerifyProofs.java).
     * 
     * @param value the new value
     */
    public void setRecheckProofAsstUsingProofVerifier(final boolean value) {
        recheckProofAsstUsingProofVerifier = value;
    }

    /**
     * Get on/off indicator instructing Proof Assistant to double-check every
     * proof steps generated proof tree using the Proof Engine
     * (mmj.verify.VerifyProofs.java).
     * 
     * @return recheckProofAsstUsingProofVerifier
     */
    public boolean getRecheckProofAsstUsingProofVerifier() {
        return recheckProofAsstUsingProofVerifier;
    }

    /**
     * Set on/off indicator instructing the Proof Assistant Export to use
     * unified or "un-unified" format for exported proofs.
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     * 
     * @param exportFormatUnified yes/no.
     */
    public void setExportFormatUnified(final boolean exportFormatUnified) {
        this.exportFormatUnified = exportFormatUnified;
    }

    /**
     * Get on/off indicator instructing the Proof Assistant Export to use
     * unified or "un-unified" format for exported proofs.
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     * 
     * @return exportFormatUnified yes/no.
     */
    public boolean getExportFormatUnified() {
        return exportFormatUnified;
    }

    /**
     * Set the order in which the Proof Assistant Export should output proof
     * step logical hypotheses (a testing feature for Proof Assistant.)
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     * 
     * @param exportHypsOrder the order.
     */
    public void setExportHypsOrder(final HypsOrder exportHypsOrder) {
        this.exportHypsOrder = exportHypsOrder;
    }

    /**
     * Get the order in which the Proof Assistant Export should output proof
     * step logical hypotheses (a testing feature for Proof Assistant.)
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     * 
     * @return ExportHypsRandomized
     */
    public HypsOrder getExportHypsOrder() {
        return exportHypsOrder;
    }

    /**
     * Set on/off indicator instructing the Proof Assistant Export to output
     * blank formulas -- or not -- for non-qed derivation steps (not logical
     * hyps).
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     * 
     * @param exportDeriveFormulas yes/no.
     */
    public void setExportDeriveFormulas(final boolean exportDeriveFormulas) {
        this.exportDeriveFormulas = exportDeriveFormulas;
    }

    /**
     * Get on/off indicator instructing the Proof Assistant Export to output
     * blank formulas -- or not -- for non-qed derivation steps (not logical
     * hyps).
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     * 
     * @return exportDeriveFormulas
     */
    public boolean getExportDeriveFormulas() {
        return exportDeriveFormulas;
    }

    /**
     * Set on/off indicator instructing the Proof Assistant Batch Test Import to
     * compare generated Dj Vars with the originals.
     * 
     * @param importCompareDJs yes/no.
     */
    public void setImportCompareDJs(final boolean importCompareDJs) {
        this.importCompareDJs = importCompareDJs;
    }

    /**
     * Set on/off indicator instructing the Proof Assistant Batch Test Import to
     * compare generated Dj Vars with the originals.
     * 
     * @return importCompareDJs
     */
    public boolean getImportCompareDJs() {
        return importCompareDJs;
    }

    /**
     * Set on/off indicator instructing the Proof Assistant Batch Test Import to
     * update the originals that are stored in memory (does not update the .mm
     * file though.)
     * 
     * @param importUpdateDJs yes/no.
     */
    public void setImportUpdateDJs(final boolean importUpdateDJs) {
        this.importUpdateDJs = importUpdateDJs;
    }

    /**
     * Set on/off indicator instructing the Proof Assistant Batch Test Import to
     * update the originals that are stored in memory (does not update the .mm
     * file though.)
     * 
     * @return importUpdateDJs
     */
    public boolean getImportUpdateDJs() {
        return importUpdateDJs;
    }

    /**
     * Set array of assertions that will be excluded from the proof unification
     * search process.
     * <p>
     * This feature is primarily needed for redundant theorems that are carried
     * in a Metamath database because they have a different proof (other
     * possibilities exist.)
     * 
     * @param unifySearchExclude array
     */
    public void setUnifySearchExclude(final Assrt[] unifySearchExclude) {
        this.unifySearchExclude = unifySearchExclude;
    }

    /**
     * Get array of assertions that will be excluded from the proof unification
     * search process.
     * <p>
     * This feature is primarily needed for redundant theorems that are carried
     * in a Metamath database because they have a different proof (other
     * possibilities exist.)
     * 
     * @return unifySearchExclude array
     */
    public Assrt[] getUnifySearchExclude() {
        return unifySearchExclude;
    }

    /**
     * Search array of assertions to see if a given assertion should be excluded
     * from the unification search process.
     * <p>
     * Assuming that the number of exclusions is small, we're using an array. If
     * the number were very large a hash table could be used, but the array is
     * searched only during the first pass through the LogicalSystem Statement
     * Table (see ProofUnifier.java).
     * 
     * @param assrt the given assertion
     * @return true if assertion should be excluded
     */
    public boolean checkUnifySearchExclude(final Assrt assrt) {
        for (final Assrt element : unifySearchExclude)
            if (assrt == element)
                return true;
        return false;
    }

    /**
     * Sets boolean value enabling or disabling "instrumentation" of the
     * OutputCursor for regression testing.
     * 
     * @param outputCursorInstrumentation true or false.
     */
    public void setOutputCursorInstrumentation(
        final boolean outputCursorInstrumentation)
    {
        this.outputCursorInstrumentation = outputCursorInstrumentation;
    }

    /**
     * Gets boolean value enabling or disabling "instrumentation" of the
     * OutputCursor for regression testing.
     * 
     * @return outputCursorInstrumentation true or false.
     */
    public boolean getOutputCursorInstrumentation() {
        return outputCursorInstrumentation;
    }

    /**
     * Sets boolean value enabling or disabling AutoReformat of proof step
     * formulas after Work Variables are resolved.
     * 
     * @param autoReformat true or false.
     */
    public void setAutoReformat(final boolean autoReformat) {
        this.autoReformat = autoReformat;
    }

    /**
     * Gets boolean value enabling or disabling AutoReformat of proof step
     * formulas after Work Variables are resolved.
     * 
     * @return autoReformat true or false.
     */
    public boolean getAutoReformat() {
        return autoReformat;
    }

    /**
     * Sets boolean value enabling or disabling use of Undo/Redo Menu Items on
     * the Proof Assistant GUI.
     * 
     * @param undoRedoEnabled true or false.
     */
    public void setUndoRedoEnabled(final boolean undoRedoEnabled) {
        this.undoRedoEnabled = undoRedoEnabled;
    }

    /**
     * Gets boolean value enabling or disabling use of Undo/Redo Menu Items on
     * the Proof Assistant GUI.
     * 
     * @return undoRedoEnabled true or false.
     */
    public boolean getUndoRedoEnabled() {
        return undoRedoEnabled;
    }

    /**
     * Sets syntax highlighting for Proof Asst GUI.
     * 
     * @param highlightingEnabled true or false
     */
    public void setHighlightingEnabled(final boolean highlightingEnabled) {
        this.highlightingEnabled = highlightingEnabled;
    }

    /**
     * Gets syntax highlighting for Proof Asst GUI.
     * 
     * @return highlightingEnabled true or false
     */
    public boolean getHighlightingEnabled() {
        return highlightingEnabled;
    }

    /**
     * Sets syntax highlighting for Proof Asst GUI.
     * 
     * @param key The name of one of the styles of the syntax highlighting
     * @param color the foreground color
     * @param bold true for bold, false for plain, null for inherit
     * @param italic true for italic, false for plain, null for inherit
     * @throws IllegalArgumentException if the
     */
    public void setHighlightingStyle(final String key, final Color color,
        final Boolean bold, final Boolean italic)
        throws IllegalArgumentException
    {
        final SimpleAttributeSet style = highlighting.get(key);
        if (style == null) {
            final List<String> list = new ArrayList<String>(
                highlighting.keySet());
            Collections.sort(list);
            throw new IllegalArgumentException(list.toString());
        }
        PaConstants.setStyle(style, color, bold, italic);
    }

    /**
     * Gets syntax highlighting for Proof Asst GUI.
     * 
     * @param key the token type
     * @return the style settings for the given token type
     */
    public AttributeSet getHighlightingStyle(final String key) {
        final AttributeSet style = highlighting.get(key);
        return style != null ? style : highlighting
            .get(PaConstants.PROOF_ASST_STYLE_ERROR);
    }
    /**
     * Sets foreground color for Proof Asst GUI.
     * 
     * @param foregroundColor Color object
     */
    public void setForegroundColor(final Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    /**
     * Gets foreground color for Proof Asst GUI.
     * 
     * @return foregroundColor Color object
     */
    public Color getForegroundColor() {
        return foregroundColor;
    }

    /**
     * Sets background color for Proof Asst GUI.
     * 
     * @param backgroundColor Color object
     */
    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * Gets background color for Proof Asst GUI.
     * 
     * @return backgroundColor Color object
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * Set TMFF Prefernces.
     * 
     * @param tmffPreferences instance of TMFFPreferences.
     */
    public void setTMFFPreferences(final TMFFPreferences tmffPreferences) {
        this.tmffPreferences = tmffPreferences;
    }

    /**
     * Get TMFF Preferences.
     * 
     * @return tmffPreferences instances.
     */
    public TMFFPreferences getTMFFPreferences() {
        return tmffPreferences;
    }

    /**
     * Set WorkVarManager
     * 
     * @param workVarManager instance of WorkVarManager.
     */
    public void setWorkVarManager(final WorkVarManager workVarManager) {
        this.workVarManager = workVarManager;
    }

    /**
     * Get WorkVarManager.
     * 
     * @return workVarManager instance.
     */
    public WorkVarManager getWorkVarManager() {
        return workVarManager;
    }

    /**
     * Set StepUnifier
     * 
     * @param stepUnifier instance of StepUnifier or null.
     */
    public void setStepUnifier(final StepUnifier stepUnifier) {
        this.stepUnifier = stepUnifier;
    }

    /**
     * Get StepUnifier.
     * 
     * @return stepUnifier instance.
     */
    public StepUnifier getStepUnifier() {
        return stepUnifier;
    }

    /**
     * Get StepUnifier Instance.
     * 
     * @return stepUnifier instance.
     */
    public StepUnifier getStepUnifierInstance() {
        StepUnifier s = getStepUnifier();
        if (s == null) {
            s = new StepUnifier(getWorkVarManager());
            setStepUnifier(s);
        }
        return s;
    }

    /**
     * A simple routine to build a list of all options for Soft Dj Vars error
     * handling.
     * <p>
     * This routine is used by ProofAsstPreferences.
     * 
     * @return Soft Dj Vars Error List
     */
    public String getSoftDjErrorOptionListString() {

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE.length; i++)
        {
            sb.append(i + 1);
            sb.append(" - ");
            sb.append(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE[i]);
            sb.append('\n');
        }

        sb.append('\n');

        return sb.toString();
    }

    /**
     * A simple routine to build a list of all defined Fonts Families.
     * <p>
     * This routine is used by ProofAsstPreferences.
     * 
     * @return Font Family List String
     */
    public String getFontListString() {

        final GraphicsEnvironment g = GraphicsEnvironment
            .getLocalGraphicsEnvironment();

        final Font[] f = g.getAllFonts();

        final Set<String> t = new TreeSet<String>();

        for (final Font element : f)
            t.add(element.getFamily());

        final StringBuilder sb = new StringBuilder();

        if (!t.isEmpty())
            loopA: for (int loopCnt = 1;; loopCnt++) {
                final int lineMax = loopCnt
                    * PaConstants.FONT_LIST_STARTING_LINE_LENGTH;
                sb.setLength(0);
                int lineCnt = 1;
                String delim = "";

                for (final String s : t) {
                    sb.append(delim).append(s);
                    if (sb.length() > lineMax * lineCnt) {
                        delim = "\n";
                        lineCnt++;
                        if (lineCnt > PaConstants.FONT_LIST_MAX_LINES)
                            continue loopA;
                    }
                    else
                        delim = ", ";
                }
                break;
            }
        else
            sb.append(" ");

        sb.append('\n');

        return sb.toString();
    }

    /**
     * A stupid routine to validate a Font Family Name.
     * <p>
     * This routine is used by ProofAsstPreferences.
     * 
     * @param familyName font family name, which must be available in
     *            GraphicsEnvironment.getAllFonts().
     * @return Family Name adjust for cap/lower variations.
     * @throws ProofAsstException if input familyName not installed in the
     *             system.
     */
    public String validateFontFamily(final String familyName)
        throws ProofAsstException
    {

        String n;
        if (familyName == null)
            n = " ";
        else
            n = familyName.trim();

        final Font[] f = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getAllFonts();

        for (final Font element : f)
            if (element.getFamily().compareToIgnoreCase(n) == 0)
                return element.getFamily();
        throw new ProofAsstException(
            PaConstants.ERRMSG_INVALID_FONT_FAMILY_NAME, familyName);
    }

    /**
     * A stupid routine to validate the entered number indicating a Dj Vars Soft
     * Error Option.
     * <p>
     * The entered number, minus 1, is looked up in
     * PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE using the option number
     * as an index.
     * <p>
     * This routine is used by ProofAsstPreferences.
     * 
     * @param option number corresponding to Dj Vars Soft Error Option name
     * @return Dj Vars Soft Error Option Name String
     * @throws ProofAsstException if input option number is out of range or is
     *             not a number.
     */
    public String validateDjVarsSoftErrorsOptionNbr(String option)
        throws ProofAsstException
    {

        int n = -1;
        if (option != null)
            try {
                n = Integer.parseInt(option);
            } catch (final NumberFormatException e) {}
        else
            option = "";

        if (n < 1
            || n > PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE.length)
            throw new ProofAsstException(
                PaConstants.ERRMSG_INVALID_SOFT_DJ_ERROR_OPTION_NBR, option);

        return PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE[n - 1];
    }

    public boolean getDjVarsSoftErrorsIgnore() {
        return djVarsSoftErrorsIgnore;
    }
    public boolean getDjVarsSoftErrorsReport() {
        return djVarsSoftErrorsReport;
    }
    public boolean getDjVarsSoftErrorsGenerate() {
        return djVarsSoftErrorsGenerate;
    }
    public boolean getDjVarsSoftErrorsGenerateNew() {
        return djVarsSoftErrorsGenerateNew;
    }
    public boolean getDjVarsSoftErrorsGenerateRepl() {
        return djVarsSoftErrorsGenerateRepl;
    }
    public boolean getDjVarsSoftErrorsGenerateDiffs() {
        return djVarsSoftErrorsGenerateDiffs;
    }
    public boolean getProofFormatCompressed() {
        return proofFormatCompressed;
    }
    public boolean getProofFormatPacked() {
        return proofFormatPacked;
    }

    public String getDjVarsSoftErrorsOptionNbr() {
        final String s = getDjVarsSoftErrorsOption();
        for (int i = 0; i < PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE.length; i++)
            if (s
                .compareTo(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_TABLE[i]) == 0)
                return Integer.toString(i + 1);
        throw new IllegalArgumentException("");
    }

    public String getDjVarsSoftErrorsOption() {
        if (djVarsSoftErrorsIgnore)
            return PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_IGNORE;
        if (djVarsSoftErrorsReport)
            return PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_REPORT;
        if (djVarsSoftErrorsGenerateNew)
            return PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_NEW;
        if (djVarsSoftErrorsGenerateRepl)
            return PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_REPLACEMENTS;
        if (djVarsSoftErrorsGenerateDiffs)
            return PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_DIFFERENCES;

        throw new IllegalArgumentException("");
    }

    public String getProofFormatOption() {
        if (proofFormatCompressed)
            return PaConstants.PROOF_ASST_PROOF_COMPRESSED;
        if (proofFormatPacked)
            return PaConstants.PROOF_ASST_PROOF_PACKED;

        return PaConstants.PROOF_ASST_PROOF_NORMAL;
    }

    public int getProofFormatNumber() {
        if (proofFormatCompressed)
            return 3;
        if (proofFormatPacked)
            return 2;

        return 1;
    }

    public boolean setDjVarsSoftErrorsOption(final String s) {
        if (s == null)
            return false; // error

        // Note: do not modify any settings unless
        // the input is valid -- therefore,
        // no default settings are made here
        // ...
        // [ ]
        //

        if (s
            .compareToIgnoreCase(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_REPLACEMENTS) == 0)
        {

            djVarsSoftErrorsIgnore = false;
            djVarsSoftErrorsReport = false;

            djVarsSoftErrorsGenerate = true;
            djVarsSoftErrorsGenerateNew = false;
            djVarsSoftErrorsGenerateRepl = true;
            djVarsSoftErrorsGenerateDiffs = false;
            return true; // no error
        }

        if (s
            .compareToIgnoreCase(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_NEW) == 0)
        {

            djVarsSoftErrorsIgnore = false;
            djVarsSoftErrorsReport = false;

            djVarsSoftErrorsGenerate = true;
            djVarsSoftErrorsGenerateNew = true;
            djVarsSoftErrorsGenerateRepl = false;
            djVarsSoftErrorsGenerateDiffs = false;
            return true; // no error
        }

        if (s
            .compareToIgnoreCase(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_GENERATE_DIFFERENCES) == 0)
        {

            djVarsSoftErrorsIgnore = false;
            djVarsSoftErrorsReport = false;

            djVarsSoftErrorsGenerate = true;
            djVarsSoftErrorsGenerateNew = false;
            djVarsSoftErrorsGenerateRepl = false;
            djVarsSoftErrorsGenerateDiffs = true;
            return true; // no error
        }

        if (s
            .compareToIgnoreCase(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_REPORT) == 0)
        {

            djVarsSoftErrorsIgnore = false;
            djVarsSoftErrorsReport = true;

            djVarsSoftErrorsGenerate = false;
            djVarsSoftErrorsGenerateNew = false;
            djVarsSoftErrorsGenerateRepl = false;
            djVarsSoftErrorsGenerateDiffs = false;

            return true; // no error
        }

        if (s
            .compareToIgnoreCase(PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_IGNORE) == 0)
        {

            djVarsSoftErrorsIgnore = true;
            djVarsSoftErrorsReport = false;

            djVarsSoftErrorsGenerate = false;
            djVarsSoftErrorsGenerateNew = false;
            djVarsSoftErrorsGenerateRepl = false;
            djVarsSoftErrorsGenerateDiffs = false;

            return true; // no error
        }

        return false;
    }

    public boolean setProofFormatOption(final String s) {
        if (s == null)
            return false; // error

        if (s.compareToIgnoreCase(PaConstants.PROOF_ASST_PROOF_NORMAL) == 0) {
            proofFormatPacked = false;
            proofFormatCompressed = false;
            return true; // no error
        }

        if (s.compareToIgnoreCase(PaConstants.PROOF_ASST_PROOF_PACKED) == 0) {
            proofFormatPacked = true;
            proofFormatCompressed = false;
            return true; // no error
        }

        if (s.compareToIgnoreCase(PaConstants.PROOF_ASST_PROOF_COMPRESSED) == 0)
        {
            proofFormatPacked = true;
            proofFormatCompressed = true;
            return true; // no error
        }

        return false;
    }

    /**
     * A stupid routine to validate the entered number indicating an Incomplete
     * Step Cursor Option.
     * <p>
     * The entered number, minus 1, is looked up in
     * PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE using the option
     * number as an index.
     * 
     * @param option number corresponding to Incomplete Step Cursor option name
     * @return Incomplete Step Cursor Option Name String
     * @throws ProofAsstException if input option number is out of range or is
     *             not a number.
     */
    public String validateIncompleteStepCursorOptionNbr(String option)
        throws ProofAsstException
    {

        int n = -1;
        if (option != null)
            try {
                n = Integer.parseInt(option);
            } catch (final NumberFormatException e) {}
        else
            option = "";

        if (n < 1
            || n > PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE.length)
            throw new ProofAsstException(
                PaConstants.ERRMSG_INVALID_INCOMPLETE_STEP_CURSOR_OPTION_NBR,
                option);

        return PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE[n - 1];
    }

    /**
     * Validates ProofAsstIncompleteStepCursor option and updates.
     * 
     * @param s either "First", "Last" or "AsIs".
     * @return true if valid otherwise false.
     */
    public boolean setIncompleteStepCursor(final String s) {
        final boolean first = PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_FIRST
            .equalsIgnoreCase(s), last = PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_LAST
            .equalsIgnoreCase(s), asis = PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_ASIS
            .equalsIgnoreCase(s);

        if (first || last || asis) {
            incompleteStepCursor = s;
            incompleteStepCursorFirst = first;
            incompleteStepCursorLast = last;
            return true; // no error
        }

        return false;
    }

    /**
     * Get incompleteStepCursor parameter.
     * 
     * @return incompleteStepCursor parameter.
     */
    public String getIncompleteStepCursor() {
        return incompleteStepCursor;
    }

    /**
     * Get incompleteStepCursorFirst parameter.
     * 
     * @return incompleteStepCursorFirst parameter.
     */
    public boolean getIncompleteStepCursorFirst() {
        return incompleteStepCursorFirst;
    }

    /**
     * Get incompleteStepCursorLast parameter.
     * 
     * @return incompleteStepCursorLast parameter.
     */
    public boolean getIncompleteStepCursorLast() {
        return incompleteStepCursorLast;
    }

    /**
     * Get current incompleteStepCursor option number.
     * 
     * @return incompleteStepCursor option number.
     */
    public String getIncompleteStepCursorOptionNbr() {
        final String s = getIncompleteStepCursor();
        for (int i = 0; i < PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE.length; i++)
            if (s
                .compareTo(PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE[i]) == 0)
                return Integer.toString(i + 1);
        throw new IllegalArgumentException("");
    }

    /**
     * A simple routine to build a list of all options for Incomplete Step
     * Cursor options.
     * 
     * @return Incomplete Step Cursor option list string.
     */
    public String getIncompleteStepCursorOptionListString() {

        final StringBuilder sb = new StringBuilder();

        for (int i = 0; i < PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE.length; i++)
        {
            sb.append(i + 1);
            sb.append(" - ");
            sb.append(PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_TABLE[i]);
            sb.append('\n');
        }

        sb.append('\n');

        return sb.toString();
    }

    /**
     * A stupid routine to validate StepSelectorMaxResults.
     * <p>
     * This routine is used by ProofAsstGUI.
     * 
     * @param maxResultsString integer max results for StepSelectorSearch.
     * @return maxResults number.
     * @throws IllegalArgumentException if an error occurred
     */
    public int validateStepSelectorMaxResults(final String maxResultsString)
        throws IllegalArgumentException
    {

        int n = -1;
        if (maxResultsString != null)
            try {
                n = Integer.parseInt(maxResultsString);
            } catch (final NumberFormatException e) {}

        if (n < 1 || n > PaConstants.STEP_SELECTOR_MAX_RESULTS_MAXIMUM)
            throw new IllegalArgumentException(
                LangException.format(
                    PaConstants.ERRMSG_INVALID_STEP_SELECTOR_MAX_RESULTS_NBR,
                    maxResultsString,
                    PaConstants.STEP_SELECTOR_MAX_RESULTS_MAXIMUM));

        return n;
    }

    /**
     * Sets maximum number of StepSelector Results.
     * 
     * @param stepSelectorMaxResults number
     */
    public void setStepSelectorMaxResults(final int stepSelectorMaxResults) {
        this.stepSelectorMaxResults = stepSelectorMaxResults;
    }

    /**
     * Gets maximum number of StepSelector Results.
     * 
     * @return stepSelectorMaxResults number
     */
    public int getStepSelectorMaxResults() {
        return stepSelectorMaxResults;
    }

    /**
     * A stupid routine to validate StepSelectorShowSubstitutions.
     * <p>
     * This routine is used by ProofAsstGUI.
     * 
     * @param showSubstitutionsString yes or no or true or false or on or off.
     * @return boolean true or false
     * @throws IllegalArgumentException if invalid value.
     */
    public boolean validateStepSelectorShowSubstitutions(
        final String showSubstitutionsString) throws IllegalArgumentException
    {
        String s;
        if (showSubstitutionsString != null) {
            s = showSubstitutionsString.trim().toLowerCase();
            if (s.equals(PaConstants.SYNONYM_TRUE_1)
                || s.equals(PaConstants.SYNONYM_TRUE_2)
                || s.equals(PaConstants.SYNONYM_TRUE_3))
                return true;
            if (s.equals(PaConstants.SYNONYM_FALSE_1)
                || s.equals(PaConstants.SYNONYM_FALSE_2)
                || s.equals(PaConstants.SYNONYM_FALSE_3))
                return false;
        }
        else
            s = " ";

        throw new IllegalArgumentException(LangException.format(
            PaConstants.ERRMSG_INVALID_BOOLEAN,
            UtilConstants.RUNPARM_STEP_SELECTOR_SHOW_SUBSTITUTIONS, s));
    }

    /**
     * Sets StepSelectorShowSubstitutions RunParm option.
     * 
     * @param stepSelectorShowSubstitutions option.
     */
    public void setStepSelectorShowSubstitutions(
        final boolean stepSelectorShowSubstitutions)
    {
        this.stepSelectorShowSubstitutions = stepSelectorShowSubstitutions;
    }

    /**
     * Gets StepSelectorShowSubstitutions RunParm option.
     * 
     * @return stepSelectorShowSubstitutions option
     */
    public boolean getStepSelectorShowSubstitutions() {
        return stepSelectorShowSubstitutions;
    }

    /**
     * Sets StepSelectorDialogPaneWidth RunParm option.
     * 
     * @param stepSelectorDialogPaneWidth option.
     */
    public void setStepSelectorDialogPaneWidth(
        final int stepSelectorDialogPaneWidth)
    {
        this.stepSelectorDialogPaneWidth = stepSelectorDialogPaneWidth;
    }

    /**
     * Gets StepSelectorDialogPaneWidth RunParm option.
     * 
     * @return stepSelectorDialogPaneWidth option
     */
    public int getStepSelectorDialogPaneWidth() {
        return stepSelectorDialogPaneWidth;
    }

    /**
     * Sets StepSelectorDialogPaneHeight RunParm option.
     * 
     * @param stepSelectorDialogPaneHeight option.
     */
    public void setStepSelectorDialogPaneHeight(
        final int stepSelectorDialogPaneHeight)
    {
        this.stepSelectorDialogPaneHeight = stepSelectorDialogPaneHeight;
    }

    /**
     * Gets StepSelectorDialogPaneHeight RunParm option.
     * 
     * @return stepSelectorDialogPaneHeight option
     */
    public int getStepSelectorDialogPaneHeight() {
        return stepSelectorDialogPaneHeight;
    }

    /**
     * Sets ProofAsstAssrtListFreespace RunParm option.
     * 
     * @param assrtListFreespace option.
     */
    public void setAssrtListFreespace(final int assrtListFreespace) {
        this.assrtListFreespace = assrtListFreespace;
    }

    /**
     * Gets ProofAsstAssrtListFreespace RunParm option.
     * 
     * @return proofAsstAssrtListFreespace option
     */
    public int getAssrtListFreespace() {
        return assrtListFreespace;
    }

    public SearchMgr getSearchMgr() {
        if (searchMgr == null)
            setSearchMgr(new SearchMgr(this));
        return searchMgr;
    }

    public void setSearchMgr(final SearchMgr searchmgr) {
        searchMgr = searchmgr;
    }
}
