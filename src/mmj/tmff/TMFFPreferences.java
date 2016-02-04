//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * TMFFPreferences.java  0.04 11/01/2011
 *
 * Version 0.01:
 *     - New.
 *
 * Nov-01-2007 Version 0.02
 *     - Add altFormatNbr, useIndent, altIndent,
 *       textRows, ...
 *       plus various getter/setter functions.
 *     - Add toggleAltFormatAndIndentParms() for use by
 *       ProofAsstGUI in the "Edit/Reformat Proof - Swap Alt"
 *       menu item.
 *     - Modify the various renderFormula() methods to
 *       include a proofLevel parameter. They in turn,
 *       will invoke the new  method
 *           TMFFSP.setLeftmostColNbr(int formulaLeftCol,
 *                                    int useIndent,
 *                                    int proofLevel)
 *       to adjust the TMFFSP.leftmostColNbr prior to
 *       formula rendering.
 *
 * Feb-01-2008 Version 0.03
 *     - Remove old, commented-out code.
 *
 * Nov-01-2011 Version 0.04:  comment update.
 *     - Use TMFF_CURR_FORMAT_NBR_DEFAULT (13) instead
 *       of defaulting to 0.
 */

package mmj.tmff;

import static mmj.pa.SessionStore.setIntBound;
import static mmj.tmff.TMFFConstants.*;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import mmj.lang.*;
import mmj.pa.*;

/**
 * Holds user settings/preferences used by the Text Mode Formula Formatting
 * (TMFF) code and provides helper routines to invoke formula rendering methods.
 * <p>
 * NOTE: this holds a couple of elements that were originally part of Proof
 * Assistant's preferences, but now an instance of TMFFPreferences is stored
 * inside of ProofAsstPreferences.
 */
public class TMFFPreferences {
    private static final String PFX = "TMFF.";

    /** Formula left column used in formatting proof text areas. */
    public Setting<Integer> formulaLeftCol;

    /** Formula right column used in formatting proof text areas. */
    public Setting<Integer> formulaRightCol;

    /**
     * Number of text columns used to display formulas.
     * <p>
     * This number is used to line wrapping and basically corresponds to the
     * window used to display formulas.
     * <p>
     * A formula can be longer than this number, and the Frame should scroll --
     * assuming that lineWrap is off and there are no NewLines.
     */
    public Setting<Integer> textColumns;

    /** Number of text rows used to display formulas. */
    public Setting<Integer> textRows;

    /**
     * Line wrap on or off.
     * <p>
     * If line wrap is on then Newlines (carriage returns) will not be used to
     * split formulas. Instead, space characters will be written to fill out the
     * remaining text columns on the line.
     */
    public Setting<Boolean> lineWrap;

    public Setting<Integer> currFormatNbr;

    public Setting<Integer> altFormatNbr;
    public Setting<Integer> useIndent;
    public Setting<Integer> altIndent;

    /*
     * See toggleAltFormatAndIndentParms() for info on how
     * these are used.
     */
    public Setting<Boolean> inAltFormatNow;
    public Setting<Integer> prevFormatNbr;
    public Setting<Integer> prevIndent;

    /*
     * The TMFFSchemes defined for use. We'll use the HashMap
     * to check for duplicates and process updates (via
     * RunParms in BatchMMJ2.)
     */
    private final Map<String, TMFFScheme> tmffSchemeMap;

    /*
     * The (4) Formats available for use: 0, 1, 2, 3
     * (the number is subject to change, see java).
     * Number 0 is the default Format, which is coded to
     * output the "old" way: unformatted.
     */
    private final TMFFFormat[] tmffFormatArray;

    /*
     * These output the "old" way: unformatted strings.
     */
    private final TMFFScheme tmffUnformattedScheme;
    private final TMFFFormat tmffUnformattedFormat;

    /**
     * Default constructor for TMFFPreferences.
     *
     * @param store The session store
     */
    public TMFFPreferences(final SessionStore store) {
        formulaLeftCol = setIntBound(
            store.addSetting(PFX + "formulaLeftCol",
                PaConstants.PROOF_ASST_FORMULA_LEFT_COL_DEFAULT),
            () -> PaConstants.PROOF_ASST_FORMULA_LEFT_COL_MIN,
            () -> formulaRightCol.get() - 1);
        formulaRightCol = setIntBound(
            store.addSetting(PFX + "formulaRightCol",
                PaConstants.PROOF_ASST_FORMULA_RIGHT_COL_DEFAULT),
            () -> formulaLeftCol.get() + 1,
            () -> PaConstants.PROOF_ASST_FORMULA_RIGHT_COL_MAX);

        textColumns = setIntBound(
            store.addSetting(PFX + "textColumns",
                PaConstants.PROOF_ASST_TEXT_COLUMNS_DEFAULT),
            PaConstants.PROOF_ASST_TEXT_COLUMNS_MIN,
            PaConstants.PROOF_ASST_TEXT_COLUMNS_MAX);
        textRows = setIntBound(
            store.addSetting(PFX + "textRows",
                PaConstants.PROOF_ASST_TEXT_ROWS_DEFAULT),
            PaConstants.PROOF_ASST_TEXT_ROWS_MIN,
            PaConstants.PROOF_ASST_TEXT_ROWS_MAX);

        lineWrap = store.addSetting(PFX + "lineWrap",
            PaConstants.PROOF_ASST_LINE_WRAP_DEFAULT);

        currFormatNbr = setIntBound(store.addSetting(PFX + "currFormatNbr",
            TMFF_CURR_FORMAT_NBR_DEFAULT), 0, TMFF_MAX_FORMAT_NBR);
        altFormatNbr = setIntBound(
            store.addSetting(PFX + "altFormatNbr", TMFF_ALT_FORMAT_NBR_DEFAULT),
            0, TMFF_MAX_FORMAT_NBR);

        useIndent = setIntBound(
            store.addSetting(PFX + "useIndent", TMFF_USE_INDENT_DEFAULT), 0,
            TMFF_MAX_INDENT);
        altIndent = setIntBound(
            store.addSetting(PFX + "altIndent", TMFF_ALT_INDENT_DEFAULT), 0,
            TMFF_MAX_INDENT);

        inAltFormatNow = store.addSetting(PFX + "inAltFormatNow", false);
        prevFormatNbr = store.addSetting(PFX + "prevFormatNbr",
            TMFF_CURR_FORMAT_NBR_DEFAULT);
        prevIndent = store.addSetting(PFX + "prevIndent",
            TMFF_USE_INDENT_DEFAULT);

        tmffFormatArray = new TMFFFormat[TMFF_MAX_FORMAT_NBR + 1];

        tmffSchemeMap = new HashMap<>();

        /*
         * Load default Schemes
         */

        for (final String[] element : TMFF_DEFAULT_DEFINE_SCHEME_PARAMS)
            putToSchemeMap(new TMFFScheme(element));

        /*
         * Load default Formats
         */

        final Consumer<String[]> read = element -> {
            final TMFFScheme s = getDefinedScheme(element[1]);
            if (s == null)
                throw new IllegalArgumentException(ErrorCode
                    .format(ERRMSG_FORMAT_SCHEME_NAME_NOTFND, element[1]));
            final TMFFFormat f = new TMFFFormat(element[0], s);

            tmffFormatArray[f.getFormatNbr()] = f;
        };
        Arrays.stream(TMFF_DEFAULT_DEFINE_FORMAT_PARAMS).forEach(read);

        /* Load default Format 0 holders */
        tmffUnformattedFormat = tmffFormatArray[TMFF_UNFORMATTED_FORMAT_NBR_0];
        tmffUnformattedScheme = tmffUnformattedFormat.getFormatScheme();

        // The ~ is so that it sorts at the end with other big keys
        store.addSerializable("~" + PFX + "formatArray",
            (final JSONObject o) -> o.entrySet().parallelStream()
                .map(e -> new String[]{e.getKey().toString(),
                        (String)e.getValue()})
                .forEach(read),
            () -> Arrays.stream(tmffFormatArray).filter(f -> f != null)
                .collect(Collectors.toMap(f -> f.getFormatNbr() + "",
                    f -> f.getFormatScheme().getSchemeName(), (a, b) -> a,
                    JSONObject::new)));

        store.addSerializable("~" + PFX + "schemeMap", (final JSONObject o) -> {
            for (final Entry<String, Object> e : o.entrySet()) {
                final List<Object> a = new ArrayList<>(
                    (JSONArray)e.getValue());
                a.add(0, e.getKey());
                putToSchemeMap(new TMFFScheme(
                    a.stream().map(Object::toString).toArray(String[]::new)));
            }
        } , () -> tmffSchemeMap.values().parallelStream()
            .collect(Collectors.toMap(s -> s.getSchemeName(),
                s -> s.getTMFFMethod().asArray(), (a, b) -> a,
                JSONObject::new)));

    }

    /**
     * Informs the caller whether or not TMFF is enabled.
     * <p>
     * In practice "enabled" means using Format 1, 2 or 3 -- and "disabled"
     * means using Format 0.
     * <p>
     * Although TMFF's renderFormula can be invoked even if TMFF is disabled, in
     * ProofWorksheet considerable work is needed to parse formulas from
     * Derivation Proof Steps for exported proofs. Therefore, if TMFF is
     * disabled the grammatical parsing process can be skipped.
     *
     * @return true if TMFF is enabled, else false.
     */
    public boolean isTMFFEnabled() {
        if (currFormatNbr.get() == TMFF_UNFORMATTED_FORMAT_NBR_0)
            return false;
        else
            return true;
    }

    /**
     * Formats a formula and outputs it to a StringBuilder using the given
     * ParseTree, Formula and TMFFStateParams instance.
     * <p>
     * This is intended to be the main way of invoking TMFF formatting.
     *
     * @param tmffSP TMFFStateParams initialized, ready for use.
     * @param parseTree of the Formula to be rendered. If left null, the formula
     *            will be output in unformatted mode.
     * @param formula to be formatted and output to sb.
     * @param proofLevel level number of formula in proof.
     * @return number of lines rendered or -1 if an error was encountered and
     *         the formula could not be formatted.
     */
    public int renderFormula(final TMFFStateParams tmffSP,
        final ParseTree parseTree, final Formula formula,
        final int proofLevel)
    {

        int nbrLines = -1;

        if (parseTree != null) {

            final int savedLength = tmffSP.sb.length();
            final int savedColNbr = tmffSP.prevColNbr;
            final int savedLeftmostColNbr = tmffSP.leftmostColNbr;

            tmffSP.setLeftmostColNbr(formulaLeftCol.get(), useIndent.get(),
                proofLevel);
            nbrLines = getCurrFormat().getFormatScheme().getTMFFMethod()
                .renderFormula(tmffSP, parseTree, formula);

            tmffSP.leftmostColNbr = savedLeftmostColNbr;

            if (nbrLines > 0)
                return nbrLines;

            tmffSP.sb.setLength(savedLength); // clean up mess.
            tmffSP.prevColNbr = savedColNbr; // clean up mess
        }

        // Note: TMFFUnformatted overrides renderFormula
        nbrLines = getTMFFUnformattedScheme().getTMFFMethod().renderFormula(
            tmffSP, null, // parse tree not needed!
            formula);
        if (nbrLines < 1)
            throw new IllegalArgumentException(ERRMSG_RENDER_FORMULA_ERROR_1);

        return nbrLines;

    }

    /**
     * Formats a formula and outputs it to a StringBuilder using the given
     * ParseTree, Formula and TMFFStateParams instance.
     * <p>
     * This is the *old* method for which proof level is not used. It is used
     * when the caller wishes to set Formula Left Column independently in
     * tmffSP.
     *
     * @param tmffSP TMFFStateParams initialized, ready for use.
     * @param parseTree of the Formula to be rendered. If left null, the formula
     *            will be output in unformatted mode.
     * @param formula to be formatted and output to sb.
     * @return number of lines rendered or -1 if an error was encountered and
     *         the formula could not be formatted.
     */
    public int renderFormula(final TMFFStateParams tmffSP,
        final ParseTree parseTree, final Formula formula)
    {

        int nbrLines = -1;

        if (parseTree != null) {

            final int savedLength = tmffSP.sb.length();
            final int savedColNbr = tmffSP.prevColNbr;

            nbrLines = getCurrFormat().getFormatScheme().getTMFFMethod()
                .renderFormula(tmffSP, parseTree, formula);
            if (nbrLines > 0)
                return nbrLines;

            tmffSP.sb.setLength(savedLength); // clean up mess.
            tmffSP.prevColNbr = savedColNbr; // clean up mess
        }

        // Note: TMFFUnformatted overrides renderFormula
        nbrLines = getTMFFUnformattedScheme().getTMFFMethod().renderFormula(
            tmffSP, null, // parse tree not needed!
            formula);
        if (nbrLines < 1)
            throw new IllegalArgumentException(ERRMSG_RENDER_FORMULA_ERROR_1);

        return nbrLines;

    }

    /**
     * Returns the TMFFFormat presently in use.
     *
     * @return TMFFFormat in use now, which may be the default format
     *         "Unformatted".
     */
    public TMFFFormat getCurrFormat() {
        return tmffFormatArray[currFormatNbr.get()];
    }

    /**
     * Returns the default Format.
     *
     * @return default TMFFFormat.
     */
    public TMFFFormat getTMFFUnformattedFormat() {
        return tmffUnformattedFormat;
    }

    /**
     * Returns the default Scheme
     *
     * @return default TMFFFormat.
     */
    public TMFFScheme getTMFFUnformattedScheme() {
        return tmffUnformattedScheme;
    }

    /**
     * A slightly redundant routine to validate an input indent amount.
     * <p>
     * This routine is used by ProofAsstGUI.
     *
     * @param s Indent amountString.
     * @return Indent amount if input is valid.
     * @throws TMFFException if input is invalid.
     */
    public int validateIndentString(String s) throws TMFFException {
        if (s != null) {
            s = s.trim();
            if (s.length() > 0)
                try {
                    final int n = Integer.parseInt(s);
                    if (n >= 0 && n <= TMFF_MAX_INDENT)
                        return n;
                } catch (final NumberFormatException e) {}
        }
        throw new TMFFException(
            ERRMSG_ERR_INDENT_INPUT_1 + Integer.toString(TMFF_MAX_INDENT));
    }

    /**
     * A simple routine to build a list of all defined Formats.
     * <p>
     * The display is returned as a String consisting of lines terminated with
     * newline characters, where each line consists of Format Number + " - " +
     * Scheme Name.
     * <p>
     * This routine is used by ProofAsstGUI.
     *
     * @return Format List String
     */
    public String getFormatListString() {
        return Arrays.stream(tmffFormatArray)
            .map(element -> element.getFormatNbr() + " - "
                + element.getFormatScheme().getSchemeName() + "\n")
            .collect(Collectors.joining());
    }

    /**
     * Toggles the alternate and current format and indent parameters when the
     * ProofAsstGUI Reformat Proof Swap Alt menu item is selected.
     * <P>
     * What that will accomplish is that the "prev" items are set only when you
     * go *int* alt mode and are restored when you come out of alt mode. So if
     * the user is in alt mode and then changes the format nbr or indent amount
     * -- reformatting -- and then selects "...Swap Alt", the original format
     * nbr and indent amount *before* going into alt mode are restored...which
     * is a good thing since it lets the user play around and then return to a
     * good setting, while leaving the alt format and indent settings unchanged
     * (they are only changed by RunParm.)
     */
    public void toggleAltFormatAndIndentParms() {

        if (inAltFormatNow.get()) {
            currFormatNbr.set(prevFormatNbr.get());
            useIndent.set(prevIndent.get());
            inAltFormatNow.set(false);
        }
        else {
            prevFormatNbr.set(currFormatNbr.get());
            prevIndent.set(useIndent.get());
            currFormatNbr.set(altFormatNbr.get());
            useIndent.set(altIndent.get());
            inAltFormatNow.set(true);
        }
    }

    /**
     * Add newly defined Scheme to Preferences data.
     *
     * @param s TMFFScheme to be added to the TMFFPreferences data.
     * @return TMFF format number in use.
     */
    public boolean addDefinedScheme(final TMFFScheme s) {
        if (getDefinedScheme(s.getSchemeName()) == null) {
            putToSchemeMap(s);
            return true;
        }
        return false;
    }

    /**
     * Update existing Format definition.
     *
     * @param newFormat to store into the Preferences data.
     */
    public void updateDefinedFormat(final TMFFFormat newFormat) {
        tmffFormatArray[newFormat.getFormatNbr()] = newFormat;
    }

    /**
     * Update existing Scheme definition.
     * <p>
     * Updates each Format that uses the existing Scheme Name to point to the
     * new object. (It has to do a replace because the object type could
     * theoretically be different -- for example, "TMFFFlat" instead of
     * "TMFFAlignColumn".)
     *
     * @param newScheme to update into the Preferences data.
     */
    public void updateDefinedScheme(final TMFFScheme newScheme) {

        final TMFFScheme oldScheme = getDefinedScheme(
            newScheme.getSchemeName());
        if (oldScheme == null)
            throw new IllegalArgumentException(
                ERRMSG_UPDATE_SCHEME_NOTFND_BUG_1 + newScheme.getSchemeName());

        for (final TMFFFormat element : tmffFormatArray)
            if (element.getFormatScheme().getSchemeName()
                .equalsIgnoreCase(oldScheme.getSchemeName()))
                element.setFormatScheme(newScheme);

        putToSchemeMap(newScheme);
    }

    /**
     * Get already defined Scheme from Preferences data.
     *
     * @param definedSchemeName to be looked up.
     * @return TMFF format number in use.
     */
    public TMFFScheme getDefinedScheme(final String definedSchemeName) {
        return getFromSchemeMap(definedSchemeName);
    }

    private void putToSchemeMap(final TMFFScheme s) {
        tmffSchemeMap.put(s.getSchemeName().toLowerCase(), s);
    }

    private TMFFScheme getFromSchemeMap(final String k) {
        return tmffSchemeMap.get(k.toLowerCase());
    }

    /**
     * Updates maxDepth for all TMFFMethods that allow updates.
     * <p>
     * As of the initial release, only TMFFAlignColumn uses maxDepth. The
     * methods TMFFFlat and TMFFUnformatted have maxDepth = Integer.MAX_VALUE
     * which results in no maxDepth line breaks from happening -- therefore,
     * they do not allow updates after initial construction of the method.
     *
     * @param maxDepth parameter.
     * @return boolean - returns false if maxDepth invalid (i.e. not a positive
     *         integer).
     */
    public boolean updateMaxDepthAcrossMethods(final int maxDepth) {

        try {
            TMFFMethod.validateMaxDepth(maxDepth);
        } catch (final IllegalArgumentException e) {
            return false;
        }

        for (final TMFFScheme s : tmffSchemeMap.values())
            s.getTMFFMethod().updateMaxDepth(maxDepth);

        return true;
    }

}
