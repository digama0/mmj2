//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFBoss.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.util;

import static mmj.gmff.GMFFConstants.*;
import static mmj.util.UtilConstants.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

import mmj.gmff.*;
import mmj.pa.MMJException;
import mmj.pa.ProofAsst;

public class GMFFBoss extends Boss {

    protected GMFFManager gmffManager;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public GMFFBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        final BooleanSupplier clear = () -> {
            // don't need to do anything here because
            // LogicalSystemBoss executes
            // gmffManager.forceReinitialization() --
            // we will null out the cached gmffManager
            // just to be safe though.
            gmffManager = null;

            return false; // not "consumed"
        };
        putCommand(RUNPARM_CLEAR, clear);
        putCommand(RUNPARM_LOAD_FILE, clear);

        putCommand(RUNPARM_GMFF_INITIALIZE, this::doGMFFInitialize);
        putCommand(RUNPARM_GMFF_EXPORT_PARMS, this::doRunParmGMFFExportParms);
        putCommand(RUNPARM_GMFF_USER_EXPORT_CHOICE,
            this::doRunParmGMFFUserExportChoice);
        putCommand(RUNPARM_GMFF_USER_TEXT_ESCAPES,
            this::doRunParmGMFFUserTextEscapes);
        putCommand(RUNPARM_GMFF_PARSE_METAMATH_TYPESET_COMMENT,
            this::doGMFFParseMetamathTypesetComment);
        putCommand(RUNPARM_GMFF_EXPORT_FROM_FOLDER,
            this::doGMFFExportFromFolder);
        putCommand(RUNPARM_GMFF_EXPORT_THEOREM, this::doGMFFExportTheorem);
    }

    /**
     * Fetch a gmffManager object, building it if necessary from previously
     * input RunParms.
     * <p>
     * NOTE: The returned gmffManager is "ready to go" but may not have been
     * "initialized", which means gmffManager validation, etc. The reason that
     * gmffManager is not initialized here is that a previous attempt to
     * "initialize" may have failed due to gmffManager errors, so to re-do it
     * here would result in doubled-up error messages. The Initialize
     * gmffManager RunParm Command should be used prior to PrintSyntaxDetails if
     * a "load and print syntax" is desired.
     *
     * @return gmffManagerobject, ready to go.
     */
    public GMFFManager getGMFFManager() {

        if (gmffManager == null)
            gmffManager = batchFramework.logicalSystemBoss.getLogicalSystem()
                .getGMFFManager();
        return gmffManager;
    }

    /**
     * Executes GMFFManager initializeGMFF function and prints any messages,
     * etc.
     */
    public void doGMFFInitialize() {

        boolean printTypesettingDefinitionsReport = false;
        final String parmString = opt(1);
        if (parmString != null)
            if (parmString.equalsIgnoreCase(PRINT_TYPESETTING_DEFINITIONS))
                printTypesettingDefinitionsReport = true;
            else
                throw error(ERRMSG_GMFF_INITIALIZE_PARM_0_ERR,
                    PRINT_TYPESETTING_DEFINITIONS, parmString);

        try {
            getGMFFManager().gmffInitialize(printTypesettingDefinitionsReport);
        } catch (final GMFFException e) {
            accumException(e);
        }

        batchFramework.outputBoss.printAndClearMessages();

        if (!getGMFFManager().isGMFFInitialized())
            throw error(ERRMSG_GMFF_INITIALIZATION_ERROR);

        return;
    }

    public void doRunParmGMFFExportParms() {

        require(8);
        getGMFFManager().accumInputGMFFExportParms(new GMFFExportParms(get(1),
            get(2), get(3), get(4), get(5), get(6), get(7), get(8), opt(9)));

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doRunParmGMFFUserTextEscapes() {

        require(3);

        final List<EscapePair> list = new ArrayList<>(
            DEFAULT_USER_TEXT_ESCAPES.length);

        for (int i = 1; i < runParm.values.length; i++)
            list.add(new EscapePair(getInt(i++), opt(i)));

        getGMFFManager().accumInputGMFFUserTextEscapesList(
            new GMFFUserTextEscapes(get(1), list));

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doRunParmGMFFUserExportChoice() {
        getGMFFManager()
            .setInputGMFFUserExportChoice(new GMFFUserExportChoice(get(1)));

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doGMFFParseMetamathTypesetComment() {

        boolean runParmPrintOption = false;

        require(3);
        try {
            final String parmString = opt(4);
            if (parmString != null)
                if (parmString.equalsIgnoreCase(RUNPARM_PRINT_OPTION))
                    runParmPrintOption = true;
                else
                    throw error(ERRMSG_GMFF_PARSE_RUNPARM_PARM_4_ERR,
                        RUNPARM_PRINT_OPTION, parmString);

            getGMFFManager().parseMetamathTypesetComment(get(1), get(2), get(3),
                runParmPrintOption);
        } catch (final GMFFException e) {
            accumException(e);
        }

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doGMFFExportFromFolder() {

        try {
            require(4);
            getGMFFManager().exportFromFolder(get(1), get(2), get(3), get(4),
                opt(5));
        } catch (final GMFFException e) {
            accumException(MMJException.extract(e));
        }

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    public void doGMFFExportTheorem() {

        try {
            require(2);
            final ProofAsst proofAsst = batchFramework.proofAsstBoss
                .getProofAsst();
            if (proofAsst == null)
                throw error(ERRMSG_GMFF_PROOF_ASST_MISSING);

            getGMFFManager().exportTheorem(get(1), get(2), opt(3), proofAsst);
        } catch (final Exception e) {
            accumException(
                new MMJException(e, ERRMSG_GMFF_RUNPARM_ERROR, e.getMessage()));
        }

        batchFramework.outputBoss.printAndClearMessages();
        return;
    }
}
