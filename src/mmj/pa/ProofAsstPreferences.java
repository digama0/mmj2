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

import static mmj.pa.SessionStore.setIntBound;

import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;

import mmj.lang.Assrt;
import mmj.lang.WorkVarManager;
import mmj.pa.PaConstants.*;
import mmj.search.SearchMgr;
import mmj.tmff.TMFFPreferences;
import mmj.verify.HypsOrder;
import mmj.verify.VerifyProofs;

/**
 * Holds user settings/preferences used by the Proof Assistant.
 */
public class ProofAsstPreferences {
    private static final String PFX = "ProofAsst.";

    private final SessionStore store;
    public TMFFPreferences tmffPreferences;

    /**
     * Proof folder used for storing proof text areas in ProofAsstGUI.
     */
    public Setting<File> proofFolder;

    /**
     * Startup Proof Worksheet File to be displayed when the ProofAsstGUI is
     * first displayed.
     */
    public Setting<File> startupProofWorksheetFile;

    /** Default file name suffix, such as ".txt" or ".mmp". */
    public Setting<String> defaultFileNameSuffix;

    /**
     * Font size used in ProofAsstGUI.
     * <p>
     * NOTE: presently, font size is set in ProofAsstBoss as part of the
     * start-up of ProofAsstGUI, based on a RunParm. Then during operation of
     * ProofAsstGUI the user can increase or decrease the font size used, and
     * those settings propagate to these ProofAsstPreferences (but are not
     * stored externally for use in the next session -- permanent setting should
     * be made in the RunParm file.)
     */
    public Setting<Integer> fontSize;

    /**
     * Font Family Name used in ProofAsstGUI.
     * <p>
     * Note: Proof Assistant formatting of formulas (via TMFF) REQUIRES a
     * fixed-width font for symbol alignment! A proportional or variable-width
     * font can be used but symbol alignments may be off.
     * <p>
     * Note: The default is "Monospaced", which works just fine...
     */
    public Setting<String> fontFamily;

    /** Font Bold style parameter used in ProofAsstGUI. */
    public Setting<Boolean> fontBold;

    /** Line spacing used in ProofAsstGUI. */
    public Setting<Float> lineSpacing;

    /** Number of error message rows on the ProofAsstGUI. */
    public Setting<Integer> errorMessageRows;
    /** Number of error message columns on the ProofAsstGUI. */
    public Setting<Integer> errorMessageColumns;
    /** Proof Maximized option for ProofAsstGUI. */
    public Setting<Boolean> maximized;
    /** Proof Text At Top option for ProofAsstGUI. */
    public Setting<Boolean> textAtTop;
    /** Proof GUI on-screen location. */
    public Setting<Rectangle> bounds;

    /**
     * Get left column number for RPN statement labels when creating
     * ProofAsstWorksheet.GeneratedProofStmt
     */
    public Setting<Integer> rpnProofLeftCol;
    /**
     * Set right column number for RPN statement labels when creating
     * ProofAsstWorksheet.GeneratedProofStmt
     */
    public Setting<Integer> rpnProofRightCol;

    /**
     * On/off indicator instructing Proof Assistant to double-check every proof
     * steps generated proof tree using the Proof Engine ({@link VerifyProofs}).
     */
    public Setting<Boolean> recheckProofAsstUsingProofVerifier;

    /**
     * On/off indicator instructing the Proof Assistant Export to use unified or
     * "un-unified" format for exported proofs.
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     */
    public Setting<Boolean> exportFormatUnified;

    /**
     * Set the order in which the Proof Assistant Export should output proof
     * step logical hypotheses (a testing feature for Proof Assistant.)
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     */
    public Setting<HypsOrder> exportHypsOrder;

    /**
     * On/off indicator instructing the Proof Assistant Export to output blank
     * formulas -- or not -- for non-qed derivation steps (not logical hyps).
     * <p>
     * Note: this applies to exported proofs written via ProofAsst.exportToFile,
     * which is triggered via BatchMMJ2 "RunParm ProofAsstExportToFile" as well
     * as the "ProofAsstBatchTest" (the latter when no input file is specified
     * and an "export to memory" is implicitly requested.)
     */
    public Setting<Boolean> exportDeriveFormulas;

    /**
     * On/off indicator instructing the Proof Assistant Batch Test Import to
     * compare generated Dj Vars with the originals.
     */
    public Setting<Boolean> importCompareDJs;
    /**
     * On/off indicator instructing the Proof Assistant Batch Test Import to
     * update the originals that are stored in memory (does not update the .mm
     * file though.)
     */
    public Setting<Boolean> importUpdateDJs;

    /**
     * Array of assertions that will be excluded from the proof unification
     * search process.
     * <p>
     * This feature is primarily needed for redundant theorems that are carried
     * in a Metamath database because they have a different proof (other
     * possibilities exist.)
     */
    public Setting<Set<String>> unifySearchExclude;

    public Setting<Integer> stepSelectorMaxResults;
    public Setting<Boolean> stepSelectorShowSubstitutions;
    public Setting<Integer> stepSelectorDialogPaneWidth;
    public Setting<Integer> stepSelectorDialogPaneHeight;

    public Setting<Integer> assrtListFreespace;

    /**
     * Boolean value enabling or disabling "instrumentation" of the OutputCursor
     * for regression testing.
     */
    public Setting<Boolean> outputCursorInstrumentation;
    /**
     * Boolean value enabling or disabling AutoReformat of proof step formulas
     * after Work Variables are resolved.
     */
    public Setting<Boolean> autoReformat;

    /**
     * Boolean value enabling or disabling use of Undo/Redo Menu Items on the
     * Proof Assistant GUI.
     */
    public Setting<Boolean> undoRedoEnabled;

    /** Syntax highlighting for Proof Asst GUI. */
    public Setting<Boolean> highlightingEnabled;

    public Map<String, SimpleAttributeSet> highlighting = null;

    /** Foreground color for Proof Asst GUI. */
    public Setting<Color> foregroundColor;
    /** Background color for Proof Asst GUI. */
    public Setting<Color> backgroundColor;

    public Setting<DjVarsSoftErrors> djVarsSoftErrors;

    public Setting<ProofFormat> proofFormat;

    public Setting<IncompleteStepCursor> incompleteStepCursor;

    public Setting<Boolean> autocomplete;
    public Setting<Boolean> deriveAutocomplete;

    public Setting<String> proofTheoremLabel;

    private WorkVarManager workVarManager;

    private StepUnifier stepUnifier;

    private SearchMgr searchMgr;

    /**
     * Default constructor.
     */
    public ProofAsstPreferences() {
        this(new SessionStore());
    }
    /**
     * Constructor with pre-constructed storage manager.
     *
     * @param store The storage manager
     */
    public ProofAsstPreferences(final SessionStore store) {
        this.store = store;
        tmffPreferences = new TMFFPreferences(store);

        proofFolder = store.addFileSetting(PFX + "proofFolder", null);
        startupProofWorksheetFile = store
            .addFileSetting(PFX + "startupProofWorksheetFile", null);

        defaultFileNameSuffix = store.addSetting(PFX + "defaultFileNameSuffix",
            PaConstants.PA_GUI_DEFAULT_FILE_NAME_SUFFIX);

        fontSize = setIntBound(
            store.addSetting(PFX + "fontSize",
                PaConstants.PROOF_ASST_FONT_SIZE_DEFAULT),
            PaConstants.PROOF_ASST_FONT_SIZE_MIN,
            PaConstants.PROOF_ASST_FONT_SIZE_MAX);
        fontFamily = store.addSetting(PFX + "fontFamily",
            PaConstants.PROOF_ASST_FONT_FAMILY_DEFAULT);
        fontFamily.addListener((o,
            value) -> Arrays.asList(GraphicsEnvironment
                .getLocalGraphicsEnvironment().getAvailableFontFamilyNames())
            .contains(value));
        fontBold = store.addSetting(PFX + "fontBold",
            PaConstants.PROOF_ASST_FONT_BOLD_DEFAULT);
        lineSpacing = store.addSetting(PFX + "lineSpacing",
            PaConstants.PROOF_ASST_LINE_SPACING_DEFAULT);

        errorMessageRows = setIntBound(
            store.addSetting(PFX + "errorMessageRows",
                PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_DEFAULT),
            PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_MIN,
            PaConstants.PROOF_ASST_ERROR_MESSAGE_ROWS_MAX);
        errorMessageColumns = setIntBound(
            store.addSetting(PFX + "errorMessageColumns",
                PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_DEFAULT),
            PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_MIN,
            PaConstants.PROOF_ASST_ERROR_MESSAGE_COLUMNS_MAX);
        maximized = store.addSetting(PFX + "maximized",
            PaConstants.PROOF_ASST_MAXIMIZED_DEFAULT);
        textAtTop = store.addSetting(PFX + "lineSpacing",
            PaConstants.PROOF_ASST_TEXT_AT_TOP_DEFAULT);
        bounds = store.new NullSetting<>(PFX + "bounds",
            Serializer.RECT_SERIALIZER);

        rpnProofLeftCol = setIntBound(
            store.addSetting(PFX + "rpnProofLeftCol",
                PaConstants.PROOF_ASST_RPN_PROOF_LEFT_COL_AUTO),
            () -> PaConstants.PROOF_ASST_RPN_PROOF_LEFT_COL_MIN,
            () -> rpnProofRightCol.get() - 1);
        rpnProofRightCol = setIntBound(
            store.addSetting(PFX + "rpnProofRightCol",
                PaConstants.PROOF_ASST_RPN_PROOF_RIGHT_COL_DEFAULT),
            () -> rpnProofLeftCol.get() + 1,
            () -> PaConstants.PROOF_ASST_RPN_PROOF_RIGHT_COL_MAX);
        rpnProofLeftCol = store.addSetting(PFX + "rpnProofLeftCol",
            PaConstants.PROOF_ASST_RPN_PROOF_LEFT_COL_AUTO);
        rpnProofRightCol = store.addSetting(PFX + "rpnProofRightCol",
            PaConstants.PROOF_ASST_RPN_PROOF_RIGHT_COL_DEFAULT);

        recheckProofAsstUsingProofVerifier = store.addSetting(
            PFX + "recheckProofAsstUsingProofVerifier",
            PaConstants.RECHECK_PROOF_ASST_USING_PROOF_VERIFIER_DEFAULT);

        exportFormatUnified = store.addSetting(PFX + "exportFormatUnified",
            PaConstants.PROOF_ASST_EXPORT_FORMAT_UNIFIED_DEFAULT);

        exportHypsOrder = store.addSetting(PFX + "exportHypsOrder",
            PaConstants.PROOF_ASST_EXPORT_HYPS_ORDER_DEFAULT);

        exportDeriveFormulas = store.addSetting(PFX + "exportDeriveFormulas",
            PaConstants.PROOF_ASST_EXPORT_DERIVE_FORMULAS_DEFAULT);

        importCompareDJs = store.addSetting(PFX + "importCompareDJs",
            PaConstants.PROOF_ASST_IMPORT_COMPARE_DJS_DEFAULT);
        importUpdateDJs = store.addSetting(PFX + "importUpdateDJs",
            PaConstants.PROOF_ASST_IMPORT_UPDATE_DJS_DEFAULT);

        // The ~ is so that it sorts at the end with other big keys
        unifySearchExclude = store.addSetting("~" + PFX + "unifySearchExclude",
            new HashSet<>(), Serializer.<String> identity().set());

        stepSelectorMaxResults = setIntBound(
            store.addSetting(PFX + "stepSelectorMaxResults",
                PaConstants.STEP_SELECTOR_MAX_RESULTS_DEFAULT),
            1, PaConstants.STEP_SELECTOR_MAX_RESULTS_MAXIMUM);
        stepSelectorShowSubstitutions = store.addSetting(
            PFX + "stepSelectorShowSubstitutions",
            PaConstants.STEP_SELECTOR_SHOW_SUBSTITUTIONS_DEFAULT);

        stepSelectorDialogPaneWidth = setIntBound(
            store.addSetting(PFX + "stepSelectorDialogPaneWidth",
                PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_DEFAULT),
            PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_MIN,
            PaConstants.STEP_SELECTOR_DIALOG_PANE_WIDTH_MAX);
        stepSelectorDialogPaneHeight = setIntBound(
            store.addSetting(PFX + "stepSelectorDialogPaneHeight",
                PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_DEFAULT),
            PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_MIN,
            PaConstants.STEP_SELECTOR_DIALOG_PANE_HEIGHT_MAX);

        assrtListFreespace = setIntBound(
            store.addSetting(PFX + "assrtListFreespace",
                PaConstants.ASSRT_LIST_FREESPACE_DEFAULT),
            0, PaConstants.ASSRT_LIST_FREESPACE_MAX);

        outputCursorInstrumentation = store.addSetting(
            PFX + "outputCursorInstrumentation",
            PaConstants.OUTPUT_CURSOR_INSTRUMENTATION_DEFAULT);
        autoReformat = store.addSetting(PFX + "autoReformat",
            PaConstants.AUTO_REFORMAT_DEFAULT);

        undoRedoEnabled = store.addSetting(PFX + "undoRedoEnabled",
            PaConstants.UNDO_REDO_ENABLED_DEFAULT);
        highlightingEnabled = store.addSetting(PFX + "highlightingEnabled",
            PaConstants.HIGHLIGHTING_ENABLED_DEFAULT);

        if (highlightingEnabled.get())
            PaConstants.doStyleDefaults(highlighting = new HashMap<>());

        foregroundColor = store.addSetting(PFX + "foregroundColor",
            PaConstants.DEFAULT_FOREGROUND_COLOR);
        backgroundColor = store.addSetting(PFX + "backgroundColor",
            PaConstants.DEFAULT_BACKGROUND_COLOR);

        djVarsSoftErrors = store.addSetting(PFX + "djVarsSoftErrors",
            PaConstants.PROOF_ASST_DJ_VARS_SOFT_ERRORS_DEFAULT);

        proofFormat = store.addSetting(PFX + "proofFormat",
            ProofFormat.Compressed);

        incompleteStepCursor = store.addSetting(PFX + "incompleteStepCursor",
            IncompleteStepCursor.AsIs);

        autocomplete = store.addSetting(PFX + "autocomplete",
            PaConstants.AUTOCOMPLETE_ENABLED_DEFAULT);
        deriveAutocomplete = store.addSetting(PFX + "deriveAutocomplete",
            PaConstants.DERIVE_AUTOCOMPLETE_DEFAULT);
        autocomplete.addListener((o, value) -> deriveAutocomplete
            .set(value && deriveAutocomplete.get()));
        deriveAutocomplete.addListener(
            (o, value) -> autocomplete.set(value || autocomplete.get()));

        proofTheoremLabel = store.new NullSetting<>(PFX + "proofTheoremLabel",
            "", Serializer.identity());

        // Note: this default constructor is available for test
        // of ProofAsstGUI in batch mode -- but is
        // mainly used by mmj.util.ProofAsstBoss, which
        // is responsible for loading workVarManager!
        // "null" is not a valid default and would
        // eventually result in an exception if not
        // updated with an actual WorkVarmanager. We
        // are *not* invoking the default WorkVarManager()
        // constructor here because, in all likelihood,
        // all of its work would need to be thrown away
        // and redone with the correct WorkVar settings.
        workVarManager = null;
        stepUnifier = null;

        setSearchMgr(null);
    }

    public SessionStore getStore() {
        return store;
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
        return unifySearchExclude.get().contains(assrt.getLabel());
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
            final List<String> list = new ArrayList<>(highlighting.keySet());
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
        return style != null ? style
            : highlighting.get(PaConstants.PROOF_ASST_STYLE_ERROR);
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

        final Set<String> t = new TreeSet<>();

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
     * @throws IllegalArgumentException if input familyName not installed in the
     *             system.
     */
    public String validateFontFamily(final String familyName) {
        return Arrays
            .stream(GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames())
            .filter(familyName::equalsIgnoreCase).findAny().orElseThrow(
                () -> new IllegalArgumentException(new ProofAsstException(
                    PaConstants.ERRMSG_INVALID_FONT_FAMILY_NAME, familyName)));
    }

    /**
     * A stupid routine to validate StepSelectorShowSubstitutions.
     * <p>
     * This routine is used by ProofAsstGUI.
     *
     * @param s yes or no or true or false or on or off.
     * @return boolean true or false
     * @throws IllegalArgumentException if invalid value.
     */
    public static boolean parseBoolean(String s)
        throws IllegalArgumentException
    {
        if (s != null) {
            s = s.trim().toLowerCase();
            if (s.equals(PaConstants.SYNONYM_TRUE_1)
                || s.equals(PaConstants.SYNONYM_TRUE_2)
                || s.equals(PaConstants.SYNONYM_TRUE_3))
                return true;
            if (s.equals(PaConstants.SYNONYM_FALSE_1)
                || s.equals(PaConstants.SYNONYM_FALSE_2)
                || s.equals(PaConstants.SYNONYM_FALSE_3))
                return false;
        }

        throw new IllegalArgumentException(
            new ProofAsstException(PaConstants.ERRMSG_INVALID_BOOLEAN, s));
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
