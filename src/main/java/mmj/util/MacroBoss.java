//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MacroBoss.java  0.01 11/13/2015
 *
 * Version 0.01: 11/13/2015
 *     - New.
 */

package mmj.util;

import static mmj.util.UtilConstants.*;

import mmj.pa.MacroManager;
import mmj.pa.MacroManager.ExecutionMode;

/**
 * Manages access to the WorkVarManager resource and processes Work Var
 * RunParms.
 * <p>
 * Even though WorkVar and related classes are defined in the mmj.lang package,
 * WorkVarBoss is coded as a separate "boss" so that Work Vars can be treated as
 * a separate set of resources, separate from Logical System, Proof Assistant,
 * etc. Initially though, WorkVars and friends are to be used with Proof
 * Assistant -- and nowhere else.
 */
public class MacroBoss extends Boss {

    private MacroManager macroManager;

    private boolean macroEnabled = UtilConstants.RUNPARM_MACRO_ENABLED_DEFAULT;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public MacroBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        putCommand(RUNPARM_CLEAR, () -> {
            macroManager = null;
            macroEnabled = UtilConstants.RUNPARM_MACRO_ENABLED_DEFAULT;
            return false; // not "consumed"
        });

        putCommand(RUNPARM_MACRO_ENABLED, this::setMacroEnabled);

        putCommand(RUNPARM_MACRO_FOLDER, this::setMacroFolder);

        putCommand(RUNPARM_MACRO_LANGUAGE, this::setMacroLanguage);

        putCommand(RUNPARM_RUN_MACRO_INIT, this::runMacroInit);

        putCommand(RUNPARM_RUN_MACRO, this::runMacro);
    }

    /**
     * Validate Macro Folder RunParm.
     */
    protected void setMacroEnabled() {
        if (!(macroEnabled = getYesNo(1)))
            macroManager = null;
    }

    /**
     * Validate Macro Folder RunParm.
     */
    protected void setMacroFolder() {
        getMacroManager(true).macroFolder
            .set(getExistingFolder(batchFramework.paths.getMMJ2Path(), 1));
    }

    /**
     * Validate Macro Language RunParm.
     */
    protected void setMacroLanguage() {
        require(2);
        if (getMacroManager(true).macroLanguage.set(get(1)))
            getMacroManager(true).macroExtension.set(get(2));
    }

    /**
     * Validate RunMacro RunParm.
     */
    protected void runMacroInit() {
        getMacroManager(true).getEngine(get(1));
    }

    /**
     * Validate RunMacro RunParm.
     */
    protected void runMacro() {
        require(1);
        getMacroManager(true).runMacro(ExecutionMode.RUNPARM, runParm.values);
    }

    /**
     * Fetches a reference to the MacroManager, first initializing it if
     * necessary.
     *
     * @param force True to ignore the macro enabled setting
     * @return MacroManager object ready to go.
     */
    public MacroManager getMacroManager(final boolean force) {

        if ((macroEnabled |= force) && macroManager == null)
            macroManager = new MacroManager(batchFramework.storeBoss.getStore(),
                batchFramework.proofAsstBoss::getProofAsst);

        return macroManager;
    }
}
