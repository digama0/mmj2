//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TMFFBoss.java  0.02 11/01/2007
 *
 * Version 0.01:
 *     - New for TMFF project.
 *
 * Nov-01-2007 Version 0.02
 *     - modify doRunParmCommand, adding new RunParm calls:
 *           editTMFFAltFormat()
 *           editTMFFUseIndent()
 *           editTMFFAltIndent()
 */

package mmj.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import mmj.lang.VerifyException;
import mmj.mmio.MMIOException;
import mmj.tmff.*;
import mmj.verify.Grammar;

/**
 * Responsible for building a TMFFPreferences object, ensuring its integrity,
 * and providing references to it upon demand.
 * <p>
 * TMFF is a sub-system used primarily in Proof Assistant but also in Dump, and
 * wherever formulas are formatted. Therefore, it is not invoked directly by
 * RunParms, but by services which use its services.
 * <p>
 * A reference to TMFFPreferences is stored *inside* TMFFPreferences, primarily
 * for convenience in passing parameters to its main customer :) However,
 * TMFFPreferences can live outside of TMFFPreferences and this code needs to
 * maintain situational awareness of the state of the system. Specifically, TMFF
 * relies upon .mm statements being parsed before TMFF formatting. Also, in the
 * future it may be that an enhancement to TMFF which uses TMFFScheme assignents
 * at the level of individual Syntax Axioms will be added. This would quite
 * possibly involve storing information about the TMFFSchemes in an array -- one
 * element for each TMFFFormat -- inside mmj.lang.Axiom so that TMFF can
 * directly access the correct TMFFMethod without doing a lookup. In that event
 * the loading and parsing of a new .mm file would be of critical interest as it
 * would require re-application of updates to the Syntax mmj.lang.Axiom objects.
 * (Programmatically this could be handled by the GrammarBoss notifying the
 * TMFFBoss -- who would do nothing if TMFFPreferences is not already in
 * existence, but if it is, then it would re-apply any syntax axiom-related
 * updates.)
 * <p>
 * Other notes:
 * <ul>
 * <li>Remember that Messages, LogicalSystem and other objects may have changed.
 * Don't worry about whether or not file is loaded, the LogicalSystemBoss will
 * throw an exception if attempt is made to retrieve LogicalSystem if it is not
 * loaded and error free.
 * <li>If clear, RunParm values to null, etc.
 * </ul>
 */
public class TMFFBoss extends Boss {

    private TMFFPreferences tmffPreferences = null;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public TMFFBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     * Executes a single command from the RunParmFile.
     *
     * @param runParm the RunParmFile line to execute.
     * @return boolean "consumed" indicating that the input runParm should not
     *         be processed again.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException
    {

        if (UtilConstants.RUNPARM_CLEAR.matches(runParm)) {
            tmffPreferences = null;
            return false; // not "consumed"
        }

        if (UtilConstants.RUNPARM_TMFF_DEFINE_SCHEME.matches(runParm)) {
            editTMFFDefineScheme(runParm);
            return true;
        }

        if (UtilConstants.RUNPARM_TMFF_DEFINE_FORMAT.matches(runParm)) {
            editTMFFDefineFormat(runParm);
            return true;
        }

        if (UtilConstants.RUNPARM_TMFF_USE_FORMAT.matches(runParm)) {
            editTMFFUseFormat(runParm);
            return true;
        }

        if (UtilConstants.RUNPARM_TMFF_ALT_FORMAT.matches(runParm)) {
            editTMFFAltFormat(runParm);
            return true;
        }

        if (UtilConstants.RUNPARM_TMFF_USE_INDENT.matches(runParm)) {
            editTMFFUseIndent(runParm);
            return true;
        }

        if (UtilConstants.RUNPARM_TMFF_ALT_INDENT.matches(runParm)) {
            editTMFFAltIndent(runParm);
            return true;
        }

        return false;
    }

    /**
     * TMFFDefineScheme RunParm validation and loading.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTMFFDefineScheme(final RunParmArrayEntry runParm) {

        final TMFFPreferences p = getTMFFPreferences();

        try {
            final TMFFScheme tmffScheme = new TMFFScheme(runParm.values);

            if (!p.addDefinedScheme(tmffScheme))
                p.updateDefinedScheme(tmffScheme);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_DEFINE_SCHEME_ERR_1
                    + e.getMessage());
        }
    }

    /**
     * TMFFDefineFormat RunParm validation and loading.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTMFFDefineFormat(final RunParmArrayEntry runParm) {

        final TMFFPreferences p = getTMFFPreferences();

        try {
            final TMFFFormat tmffFormat = new TMFFFormat(runParm.values, p);

            p.updateDefinedFormat(tmffFormat);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_DEFINE_FORMAT_ERR_1
                    + e.getMessage());
        }
    }

    /**
     * TMFFUseFormat RunParm validation and loading. Checks for valid format
     * number *and* if a format number other than 0 (unformatted) is requested,
     * checks to see that TMFF can be run (i.e. that the Grammar has been
     * initialized and all input statements have been parsed).
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTMFFUseFormat(final RunParmArrayEntry runParm) {

        try {
            getTMFFPreferences().setCurrFormatNbr(runParm.values);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_USE_FORMAT_ERR_1 + e.getMessage());
        }

        if (getTMFFPreferences()
            .getCurrFormatNbr() != TMFFConstants.TMFF_UNFORMATTED_FORMAT_NBR_0)
            checkTMFFCanBeRunNow();
    }

    /**
     * TMFFAltFormat RunParm validation and loading. Checks for valid format
     * number *and* if a format number other than 0 (unformatted) is requested,
     * checks to see that TMFF can be run (i.e. that the Grammar has been
     * initialized and all input statements have been parsed).
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTMFFAltFormat(final RunParmArrayEntry runParm) {

        try {
            getTMFFPreferences().setAltFormatNbr(runParm.values);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_ALT_FORMAT_ERR_1 + e.getMessage());
        }
    }

    /**
     * TMFFUseIndent RunParm validation and loading. Checks for valid format
     * number *and* if a format number other than 0 (unformatted) is requested,
     * checks to see that TMFF can be run (i.e. that the Grammar has been
     * initialized and all input statements have been parsed).
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTMFFUseIndent(final RunParmArrayEntry runParm) {

        try {
            getTMFFPreferences().setUseIndent(runParm.values);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_USE_INDENT_ERR_1 + e.getMessage());
        }
    }

    /**
     * TMFFAltIndent RunParm validation and loading. Checks for valid format
     * number *and* if a format number other than 0 (unformatted) is requested,
     * checks to see that TMFF can be run (i.e. that the Grammar has been
     * initialized and all input statements have been parsed).
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editTMFFAltIndent(final RunParmArrayEntry runParm) {

        try {
            getTMFFPreferences().setAltIndent(runParm.values);
        } catch (final IllegalArgumentException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_RUNPARM_ALT_INDENT_ERR_1 + e.getMessage());
        }
    }

    /**
     * Fetches a reference to the TMFFPreferences, first initializing it if
     * necessary.
     *
     * @return TMFFPreferences object ready to go.
     */
    public TMFFPreferences getTMFFPreferences() {

        if (tmffPreferences == null)
            tmffPreferences = buildTMFFPreferences();
        return tmffPreferences;
    }

    /**
     * Construct TMFFPreferences object from scratch.
     *
     * @return TMFFPreferences object ready to go.
     */
    protected TMFFPreferences buildTMFFPreferences() {

        return new TMFFPreferences();
    }

    /**
     * Check that TMFF can be run now. Requires that all .mm statements have
     * been previously parsed -- grammatically validated -- otherwise it refuses
     * to do its job...
     * <p>
     * In fact, if the grammar is not pre-initialized when the instruction
     * pointer reaches this code, a Exception is thrown. The rationale is that
     * ProofAsstBoss will check grammar initialization before invoking this
     * anyway, and we'll just make the user fix the problem for other
     * invocations before doing anything else. This will save having to check if
     * 'tmffPreferences == null' everywhere else. Ugly, but...
     */
    protected void checkTMFFCanBeRunNow() {

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        if (grammar.getGrammarInitialized() && batchFramework.grammarBoss
            .getAllStatementsParsedSuccessfully())
        {
            // ok, fine.
        }
        else
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_TMFF_REQUIRES_GRAMMAR_INIT);
    }
}
