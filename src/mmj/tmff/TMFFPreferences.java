//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  TMFFPreferences.java  0.04 11/01/2011
 *
 *  Version 0.01:
 *      - New.
 *
 *  Nov-01-2007 Version 0.02
 *      - Add altFormatNbr, useIndent, altIndent,
 *        textRows, ...
 *        plus various getter/setter functions.
 *      - Add toggleAltFormatAndIndentParms() for use by
 *        ProofAsstGUI in the "Edit/Reformat Proof - Swap Alt"
 *        menu item.
 *      - Modify the various renderFormula() methods to
 *        include a proofLevel parameter. They in turn,
 *        will invoke the new  method
 *            TMFFSP.setLeftmostColNbr(int formulaLeftCol,
 *                                     int useIndent,
 *                                     int proofLevel)
 *        to adjust the TMFFSP.leftmostColNbr prior to
 *        formula rendering.
 *
 *  Feb-01-2008 Version 0.03
 *      - Remove old, commented-out code.
 *
 *  Nov-01-2011 Version 0.04:  comment update.
 *      - Use TMFF_CURR_FORMAT_NBR_DEFAULT (13) instead
 *        of defaulting to 0.
 */

package mmj.tmff;

import java.util.HashMap;
import java.util.Iterator;
import mmj.pa.PaConstants;
import mmj.lang.ParseNode;
import mmj.lang.ParseTree;
import mmj.lang.Formula;

/**
 *  Holds user settings/preferences used by the
 *  Text Mode Formula Formatting (TMFF) code and
 *  provides helper routines to invoke
 *  formula rendering methods.
 *  <p>
 *  NOTE: this holds a couple of elements that were
 *        originally part of Proof Assistant's
 *        preferences, but now an instance of
 *        TMFFPreferences is stored inside of
 *        ProofAsstPreferences.
 *
 */
public class TMFFPreferences {

    private   int            formulaLeftCol;

    private   int            formulaRightCol;

    private   int            textColumns;

    private   int            textRows;

    private   boolean        lineWrap;

    private   int            currFormatNbr;

    private   int            altFormatNbr;
    private   int            useIndent;
    private   int            altIndent;

    /*
     * See toggleAltFormatAndIndentParms() for info on how
     * these are used.
     */
    private   boolean        inAltFormatNow;
    private   int            prevFormatNbr;
    private   int            prevIndent;


    /*
     *  The TMFFSchemes defined for use. We'll use the HashMap
     *  to check for duplicates and process updates (via
     *  RunParms in BatchMMJ2.)
     */
    private   HashMap        tmffSchemeMap;


    /*
     *  The (4) Formats available for use: 0, 1, 2, 3
     *  (the number is subject to change, see TMFFConstants.java).
     *  Number 0 is the default Format, which is coded to
     *  output the "old" way: unformatted.
     */
    private   TMFFFormat[]   tmffFormatArray;

    /*
     *  These output the "old" way: unformatted strings.
     */
    private   TMFFScheme     tmffUnformattedScheme;
    private   TMFFFormat     tmffUnformattedFormat;

    /**
     *  Default constructor for TMFFPreferences.
     */
    public TMFFPreferences() {
        loadPreferenceDefaults();
    }

    /**
     *  Loads the hardcoded default TMFFPreferences values.
     *  <p>
     *  This method is provided as a public service so
     *  that, in theory, the user can experiment or alter
     *  maxDepth in various Formats using
     *  <code>updateMaxDepthAcrossMethods()</code>
     *  and then reload the original Format values -- which
     *  will also wipe out any RunParm Preferences input
     *  for the run.
     */
    public void loadPreferenceDefaults() {
        formulaLeftCol            =
            PaConstants.PROOF_ASST_FORMULA_LEFT_COL_DEFAULT;
        formulaRightCol           =
            PaConstants.PROOF_ASST_FORMULA_RIGHT_COL_DEFAULT;
        textColumns               =
            PaConstants.PROOF_ASST_TEXT_COLUMNS_DEFAULT;
        textRows                  =
            PaConstants.PROOF_ASST_TEXT_ROWS_DEFAULT;

        lineWrap                  =
            PaConstants.PROOF_ASST_LINE_WRAP_DEFAULT;

        currFormatNbr             =
            TMFFConstants.TMFF_CURR_FORMAT_NBR_DEFAULT;
        altFormatNbr              =
            TMFFConstants.TMFF_ALT_FORMAT_NBR_DEFAULT;

        useIndent                 =
            TMFFConstants.TMFF_USE_INDENT_DEFAULT;
        altIndent                 =
            TMFFConstants.TMFF_ALT_INDENT_DEFAULT;

        inAltFormatNow            = false;
        prevFormatNbr             =
            TMFFConstants.TMFF_CURR_FORMAT_NBR_DEFAULT;
        prevIndent                =
            TMFFConstants.TMFF_USE_INDENT_DEFAULT;

        tmffFormatArray           =
            new TMFFFormat[
                TMFFConstants.TMFF_MAX_FORMAT_NBR + 1];

        tmffSchemeMap             = new HashMap();

        /*
         *  Load default Schemes
         */

        TMFFScheme s;

        for (int i = 0;
             i < TMFFConstants.
                     TMFF_DEFAULT_DEFINE_SCHEME_PARAMS.
                        length;
             i++) {
            s                     =
                new TMFFScheme(
                    TMFFConstants.
                        TMFF_DEFAULT_DEFINE_SCHEME_PARAMS[i]);
            putToSchemeMap(s);
        }

        /*
         *  Load default Formats
         */

        TMFFFormat f;

        for (int i = 0;
             i < TMFFConstants.
                     TMFF_DEFAULT_DEFINE_FORMAT_PARAMS.
                         length;
             i++) {
            s                     =
                getDefinedScheme(
                    TMFFConstants.
                        TMFF_DEFAULT_DEFINE_FORMAT_PARAMS[i][1]);
            if (s == null) {
                throw new IllegalArgumentException(
                    TMFFConstants.ERRMSG_FORMAT_SCHEME_NAME_NOTFND_1
                    + TMFFConstants.
                        TMFF_DEFAULT_DEFINE_FORMAT_PARAMS[i][1]);
            }
            f                     =
                new TMFFFormat(
                    TMFFConstants.
                        TMFF_DEFAULT_DEFINE_FORMAT_PARAMS[i][0],
                    s);

            tmffFormatArray[f.getFormatNbr()]
                                  = f;
        }

        /*
         *  Load default Format 0 holders
         */
        tmffUnformattedFormat     =
            tmffFormatArray[
                TMFFConstants.TMFF_UNFORMATTED_FORMAT_NBR_0];
        tmffUnformattedScheme     =
            tmffUnformattedFormat.getFormatScheme();

    }

    /**
     *  Informs the caller whether or not TMFF is enabled.
     *  <p>
     *  In practice "enabled" means using Format 1, 2 or 3 --
     *  and "disabled" means using Format 0.
     *  <p>
     *  Although TMFF's renderFormula can be invoked even if
     *  TMFF is disabled, in ProofWorksheet considerable work
     *  is needed to parse formulas from Derivation Proof Steps
     *  for exported proofs. Therefore, if TMFF is disabled
     *  the grammatical parsing process can be skipped.
     *  <p>
     *  @return true if TMFF is enabled, else false.
     */
    public boolean isTMFFEnabled() {
        if (getCurrFormatNbr() ==
            TMFFConstants.TMFF_UNFORMATTED_FORMAT_NBR_0) {
            return false;
        }
        else {
            return true;
        }
    }


    /**
     *  Formats a formula and outputs it to a StringBuffer
     *  using the given ParseTree, Formula and
     *  TMFFStateParams instance.
     *  <p>
     *  This is intended to be the main way of invoking
     *  TMFF formatting.
     *  <p>
     *  @param tmffSP TMFFStateParams initialized, ready for use.
     *
     *  @param parseTree of the Formula to be rendered.
     *                   If left null, the formula will be
     *                   output in unformatted mode.
     *
     *  @param formula to be formatted and output to sb.
     *
     *  @param proofLevel level number of formula in proof.
     *
     *  @return number of lines rendered or -1 if an error
     *                  was encountered and the formula could
     *                  not be formatted.
     */
    public int renderFormula(TMFFStateParams tmffSP,
                             ParseTree       parseTree,
                             Formula         formula,
                             int             proofLevel) {


        int nbrLines              = -1;

        if (parseTree != null) {

            int savedLength       = tmffSP.sb.length();
            int savedColNbr       = tmffSP.prevColNbr;
            int savedLeftmostColNbr
                                  = tmffSP.getLeftmostColNbr();

            tmffSP.setLeftmostColNbr(getFormulaLeftCol(),
                                     getUseIndent(),
                                     proofLevel);
            nbrLines              =
                getCurrFormat().
                    getFormatScheme().
                        getTMFFMethod().
                            renderFormula(tmffSP,
                                          parseTree,
                                          formula);

            tmffSP.setLeftmostColNbr(savedLeftmostColNbr);

            if (nbrLines > 0) {
                return nbrLines;
            }

            tmffSP.sb.setLength(savedLength);    //clean up mess.
            tmffSP.prevColNbr     = savedColNbr; //clean up mess
        }


        // Note: TMFFUnformatted overrides renderFormula
        nbrLines                  =
            getTMFFUnformattedScheme().
                getTMFFMethod().
                    renderFormula(tmffSP,
                                  null, //parse tree not needed!
                                  formula);
        if (nbrLines < 1) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_RENDER_FORMULA_ERROR_1);
        }

        return nbrLines;

    }


    /**
     *  Formats a formula and outputs it to a StringBuffer
     *  using the given ParseTree, Formula and
     *  TMFFStateParams instance.
     *  <p>
     *  This is the *old* method for which proof level is
     *  not used. It is used when the caller wishes to
     *  set Formula Left Column independently in tmffSP.
     *  <p>
     *  @param tmffSP TMFFStateParams initialized, ready for use.
     *
     *  @param parseTree of the Formula to be rendered.
     *                   If left null, the formula will be
     *                   output in unformatted mode.
     *
     *  @param formula to be formatted and output to sb.
     *
     *  @return number of lines rendered or -1 if an error
     *                  was encountered and the formula could
     *                  not be formatted.
     */
    public int renderFormula(TMFFStateParams tmffSP,
                             ParseTree       parseTree,
                             Formula         formula) {


        int nbrLines              = -1;

        if (parseTree != null) {

            int savedLength       = tmffSP.sb.length();
            int savedColNbr       = tmffSP.prevColNbr;

            nbrLines              =
                getCurrFormat().
                    getFormatScheme().
                        getTMFFMethod().
                            renderFormula(tmffSP,
                                          parseTree,
                                          formula);
            if (nbrLines > 0) {
                return nbrLines;
            }

            tmffSP.sb.setLength(savedLength);    //clean up mess.
            tmffSP.prevColNbr     = savedColNbr; //clean up mess
        }

        // Note: TMFFUnformatted overrides renderFormula
        nbrLines                  =
            getTMFFUnformattedScheme().
                getTMFFMethod().
                    renderFormula(tmffSP,
                                  null, //parse tree not needed!
                                  formula);
        if (nbrLines < 1) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_RENDER_FORMULA_ERROR_1);
        }

        return nbrLines;

    }


    /**
     *  Returns the TMFFFormat presently in use.
     *
     *  @return TMFFFormat in use now, which may be the
     *          default format "Unformatted".
     */
    public TMFFFormat getCurrFormat() {
        return tmffFormatArray[getCurrFormatNbr()];
    }

    /**
     *  Returns the default Format.
     *
     *  @return default TMFFFormat.
     */
    public TMFFFormat getTMFFUnformattedFormat() {
        return tmffUnformattedFormat;
    }


    /**
     *  Returns the default Scheme
     *
     *  @return default TMFFFormat.
     */
    public TMFFScheme getTMFFUnformattedScheme() {
        return tmffUnformattedScheme;
    }


    /**
     *  Set formula left column used in formatting proof
     *  text areas.
     *
     *  @param formulaLeftCol formula LeftCol used for formatting
     *                     formula text areas
     */
    public void setFormulaLeftCol(int formulaLeftCol) {
        this.formulaLeftCol       = formulaLeftCol;
    }

    /**
     *  Get formula left column used in formatting proof
     *  text areas.
     *
     *  @return formulaLeftCol formula LeftCol used for
     *                      formatting formula text areas
     */
    public int getFormulaLeftCol() {
        return formulaLeftCol;
    }

    /**
     *  Set formula right column used in formatting proof
     *  text areas.
     *
     *  @param formulaRightCol formula RightCol used for
     *                     formatting formula text areas
     */
    public void setFormulaRightCol(int formulaRightCol) {
        this.formulaRightCol      = formulaRightCol;
    }

    /**
     *  Get formula right column used in formatting proof
     *  text areas.
     *
     *  @return formulaRightCol formula RightCol used for
     *                      formatting formula text areas
     */
    public int getFormulaRightCol() {
        return formulaRightCol;
    }

    /**
     *  Set number of text columns used to display formulas.
     *  <p>
     *  This number is used to line wrapping and basically
     *  corresponds to the window used to display formulas.
     *  <p>
     *  A formula can be longer than this number, and
     *  the Frame should scroll -- assuming that lineWrap
     *  is off and there are no NewLines.
     *  <p>
     *  @param textColumns number of text columns.
     */
    public void setTextColumns(int textColumns) {
        this.textColumns          = textColumns;
    }

    /**
     *  Get number of text columns used to display formulas.
     *
     *  @return textColumns number of text columns used to display
     *                      formulas.
     */
    public int getTextColumns() {
        return textColumns;
    }

    /**
     *  Set number of text rows used to display formulas.
     *  <p>
     *  @param textRows number of text rows.
     */
    public void setTextRows(int textRows) {
        this.textRows          = textRows;
    }

    /**
     *  Get number of text rows used to display formulas.
     *
     *  @return textRows number of text rows used to display
     *                      formulas.
     */
    public int getTextRows() {
        return textRows;
    }

    /**
     *  Set line wrap on or off.
     *  <p>
     *  If line wrap is on then Newlines (carraige returns)
     *  will not be used to split formulas. Instead, space
     *  characters will be written to fill out the remaining
     *  text columns on the line.
     *  <p>
     *  @param lineWrap setting, on or off.
     */
    public void setLineWrap(boolean lineWrap) {
        this.lineWrap             = lineWrap;
    }

    /**
     *  Get the current lineWrap setting.
     *
     *  @return lineWrap setting.
     */
    public boolean getLineWrap() {
        return lineWrap;
    }

    /**
     *  Set current Format number using user parameters.
     *  <p>
     *  @param param String array containing current Format
     *               number in array element 0.
     */
    public void setCurrFormatNbr(String[] param) {

        if (param.length < 1   ||
            param[0] == null   ||
            param[0].length() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_FORMAT_NBR_MISSING2_1);
        }

        try {
            setCurrFormatNbr(
                Integer.parseInt(param[0].trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_PREF_FORMAT_NBR_1
                + param[0].trim()
                + TMFFConstants.ERRMSG_BAD_PREF_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }
    }

    /**
     *  Set Alternate Format number using user parameters.
     *
     *  @param param String array containing alternate Format
     *               number in array element 0.
     */
    public void setAltFormatNbr(String[] param) {

        if (param.length < 1   ||
            param[0] == null   ||
            param[0].length() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_ALT_FORMAT_NBR_MISSING2_1);
        }

        try {
            setAltFormatNbr(
                Integer.parseInt(param[0].trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_ALT_FORMAT_NBR_1
                + param[0].trim()
                + TMFFConstants.ERRMSG_BAD_ALT_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }
    }

    /**
     *  Set Use Indent amount using user parameters.
     *
     *  @param param String array containing alternate Indent
     *               amount in array element 0.
     */
    public void setUseIndent(String[] param) {

        if (param.length < 1   ||
            param[0] == null   ||
            param[0].length() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_USE_INDENT_MISSING2_1);
        }

        try {
            setUseIndent(
                Integer.parseInt(param[0].trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_USE_INDENT_1
                + param[0].trim()
                + TMFFConstants.ERRMSG_BAD_USE_INDENT_2
                + TMFFConstants.TMFF_MAX_INDENT);
        }
    }

    /**
     *  Set Alt Indent amount using user parameters.
     *
     *  @param param String array containing alternate Indent
     *               amount in array element 0.
     */
    public void setAltIndent(String[] param) {

        if (param.length < 1   ||
            param[0] == null   ||
            param[0].length() == 0) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_ALT_INDENT_MISSING2_1);
        }

        try {
            setAltIndent(
                Integer.parseInt(param[0].trim()));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_ALT_INDENT_1
                + param[0].trim()
                + TMFFConstants.ERRMSG_BAD_ALT_INDENT_2
                + TMFFConstants.TMFF_MAX_INDENT);
        }
    }

    /**
     *  A slightly redundant routine to validate an input
     *  indent amount.
     *  <p>
     *  This routine is used by ProofAsstGUI.
     *  <p>
     *  @param s Indent amountString.
     *  @return Indent amount if input is valid.
     *  @throws TMFFException if input is invalid.
     */
    public int validateIndentString(String s)
                                        throws TMFFException {
        if (s != null) {
            s                     = s.trim();
            if (s.length() > 0) {
                try {
                    int n         = Integer.parseInt(s);
                    if (n >= 0 &&
                        n <= TMFFConstants.TMFF_MAX_INDENT) {
                        return n;
                    }
                }
                catch (NumberFormatException e) {
                }
            }
        }
        throw new TMFFException(
            TMFFConstants.ERRMSG_ERR_INDENT_INPUT_1
            + Integer.toString(TMFFConstants.TMFF_MAX_INDENT));
    }


    /**
     *  A slightly redundant routine to validate an input
     *  format number.
     *  <p>
     *  This routine is used by ProofAsstGUI.
     *  <p>
     *  @param s Format Number String.
     *  @return Format Number if input is valid.
     *  @throws TMFFException if input is invalid.
     */
    public int validateFormatNbrString(String s)
                                        throws TMFFException {
        if (s != null) {
            s                     = s.trim();
            if (s.length() > 0) {
                try {
                    int n         = Integer.parseInt(s);
                    if (n >= 0 &&
                        n <= TMFFConstants.TMFF_MAX_FORMAT_NBR) {
                        return n;
                    }
                }
                catch (NumberFormatException e) {
                }
            }
        }
        throw new TMFFException(
            TMFFConstants.ERRMSG_ERR_FORMAT_NBR_INPUT_1
            + Integer.toString(TMFFConstants.TMFF_MAX_FORMAT_NBR));
    }

    /**
     *  A simple routine to build a list of all defined Formats.
     *  <p>
     *  The display is returned as a String consisting of lines
     *  terminated with newline characters, where each line
     *  consists of Format Number + " - " + Scheme Name.
     *  <p>
     *  This routine is used by ProofAsstGUI.
     *  <p>
     *  @return Format List String
     */
    public String getFormatListString() {

        StringBuffer sb           = new StringBuffer();

        for (int i = 0; i < tmffFormatArray.length; i++) {
            sb.append(
                Integer.toString(
                    tmffFormatArray[i].
                        getFormatNbr()));
            sb.append(" - ");
            sb.append(
                tmffFormatArray[i].
                    getFormatScheme().
                        getSchemeName());
            sb.append('\n');
        }

        return sb.toString();
    }

    /**
     *  Set current Format number.
     *  <p>
     *  @param currFormatNbr 0 thru max number.
     */
    public void setCurrFormatNbr(int currFormatNbr) {
        if (currFormatNbr < 0
            ||
            currFormatNbr >
            TMFFConstants.TMFF_MAX_FORMAT_NBR) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_PREF_FORMAT_NBR_1
                + currFormatNbr
                + TMFFConstants.ERRMSG_BAD_PREF_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }
        this.currFormatNbr        = currFormatNbr;
    }

    /**
     *  Get current Format Number.
     *
     *  @return TMFF format number in use.
     */
    public int getCurrFormatNbr() {
        return currFormatNbr;
    }

    /**
     *  Toggles the alternate and current format and indent
     *  parameters when the ProofAsstGUI Reformat Proof
     *  Swap Alt menu item is selected.
     *  <P>
     *  What that will accomplish is that the "prev"
     *  items are set only when you go *int* alt mode
     *  and are restored when you come out of alt mode.
     *  So if the user is in alt mode and then changes
     *  the format nbr or indent amount -- reformatting --
     *  and then selects "...Swap Alt", the original
     *  format nbr and indent amount *before* going
     *  into alt mode are restored...which is a good
     *  thing since it lets the user play around and
     *  then return to a good setting, while leaving
     *  the alt format and indent settings unchanged
     *  (they are only changed by RunParm.)
     */
    public void toggleAltFormatAndIndentParms() {

        if (inAltFormatNow) {
            setCurrFormatNbr(prevFormatNbr);
            setUseIndent(prevIndent);
            inAltFormatNow        = false;
        }
        else {
            prevFormatNbr         = getCurrFormatNbr();
            prevIndent            = getUseIndent();
            setCurrFormatNbr(altFormatNbr);
            setUseIndent(altIndent);
            inAltFormatNow        = true;
        }
    }

    /**
     *  Set alternate Format number.
     *  <p>
     *  @param altFormatNbr 0 thru max number.
     */
    public void setAltFormatNbr(int altFormatNbr) {
        if (altFormatNbr < 0
            ||
            altFormatNbr >
            TMFFConstants.TMFF_MAX_FORMAT_NBR) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_ALT_FORMAT_NBR_1
                + altFormatNbr
                + TMFFConstants.ERRMSG_BAD_ALT_FORMAT_NBR_2
                + TMFFConstants.TMFF_MAX_FORMAT_NBR);
        }
        this.altFormatNbr        = altFormatNbr;
    }

    /**
     *  Get alternate Format Number.
     *
     *  @return TMFF alt format number in use.
     */
    public int getAltFormatNbr() {
        return altFormatNbr;
    }

    /**
     *  Set Use Indent number.
     *  <p>
     *  @param useIndent 0 thru max number.
     */
    public void setUseIndent(int useIndent) {
        if (useIndent < 0
            ||
            useIndent >
            TMFFConstants.TMFF_MAX_INDENT) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_USE_INDENT_1
                + useIndent
                + TMFFConstants.ERRMSG_BAD_USE_INDENT_2
                + TMFFConstants.TMFF_MAX_INDENT);
        }
        this.useIndent        = useIndent;
    }

    /**
     *  Get Use Indent Amount.
     *
     *  @return TMFF Use Indent amount in use.
     */
    public int getUseIndent() {
        return useIndent;
    }

    /**
     *  Set Alt Indent amount.
     *  <p>
     *  @param altIndent 0 thru max number.
     */
    public void setAltIndent(int altIndent) {
        if (altIndent < 0
            ||
            altIndent >
            TMFFConstants.TMFF_MAX_INDENT) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_BAD_ALT_INDENT_1
                + altIndent
                + TMFFConstants.ERRMSG_BAD_ALT_INDENT_2
                + TMFFConstants.TMFF_MAX_INDENT);
        }
        this.altIndent        = altIndent;
    }

    /**
     *  Get Alt Indent Amount.
     *
     *  @return TMFF alternate indent amount number in use.
     */
    public int getAltIndent() {
        return altIndent;
    }

    /**
     *  Add newly defined Scheme to Preferences data.
     *
     *  @param s TMFFScheme to be added to the TMFFPreferences data.
     *
     *  @return TMFF format number in use.
     */
    public boolean addDefinedScheme(TMFFScheme s) {
        if (getDefinedScheme(s.getSchemeName()) == null) {
            putToSchemeMap(s);
            return true;
        }
        return false;
    }

    /**
     *  Update existing Format definition.
     *  <p>
     *  @param newFormat to store into the Preferences data.
     */
    public void updateDefinedFormat(TMFFFormat newFormat) {
        tmffFormatArray[newFormat.getFormatNbr()]
                                  = newFormat;
    }

    /**
     *  Update existing Scheme definition.
     *  <p>
     *  Updates each Format that uses the existing Scheme
     *  Name to point to the new object. (It has to do a
     *  replace because the object type could theoretically
     *  be different -- for example, "TMFFFlat" instead
     *  of "TMFFAlignColumn".)
     *
     *  @param newScheme to update into the Preferences data.
     */
    public void updateDefinedScheme(TMFFScheme newScheme) {

        TMFFScheme oldScheme      =
            getDefinedScheme(newScheme.getSchemeName());
        if (oldScheme == null) {
            throw new IllegalArgumentException(
                TMFFConstants.ERRMSG_UPDATE_SCHEME_NOTFND_BUG_1
                + newScheme.getSchemeName());
        }

        for (int i = 0; i < tmffFormatArray.length; i++) {
            if (tmffFormatArray[i].
                    getFormatScheme().
                        getSchemeName().
                            compareToIgnoreCase(
                                oldScheme.getSchemeName())
                == 0) {
                tmffFormatArray[i].setFormatScheme(newScheme);
            }
        }

        putToSchemeMap(newScheme);
    }


    /**
     *  Get already defined Scheme from Preferences data.
     *
     *  @param definedSchemeName to be looked up.
     *
     *  @return TMFF format number in use.
     */
    public TMFFScheme getDefinedScheme(String definedSchemeName) {
        return getFromSchemeMap(definedSchemeName);
    }

    private void putToSchemeMap(TMFFScheme s) {
        tmffSchemeMap.put(s.getSchemeName().toLowerCase(),
                          s);
    }

    private TMFFScheme getFromSchemeMap(String k) {
        return (TMFFScheme) tmffSchemeMap.get(k.toLowerCase());
    }

    /**
     *  Updates maxDepth for all TMFFMethods that allow
     *  updates.
     *  <p>
     *  As of the initial release, only TMFFAlignColumn
     *  uses maxDepth. The methods TMFFFlat and
     *  TMFFUnformatted have maxDepth = Integer.MAX_VALUE
     *  which results in no maxDepth line breaks from
     *  happening -- therefore, they do not allow updates
     *  after initial construction of the method.
     *
     *  @param maxDepth parameter.
     *
     *  @return boolean - returns false if maxDepth invalid
     *                    (i.e. not a positive integer).
     */
    public boolean updateMaxDepthAcrossMethods(int maxDepth) {

        try {
            TMFFMethod.validateMaxDepth(maxDepth);
        }
        catch (IllegalArgumentException e) {
            return false;
        }

        Iterator   i              =
            tmffSchemeMap.
                values().
                    iterator();

        while (i.hasNext()) {
            ((TMFFScheme)i.next()).
                getTMFFMethod().
                    updateMaxDepth(
                        maxDepth);
        }

        return true;
    }

}
