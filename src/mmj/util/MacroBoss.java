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

import mmj.lang.VerifyException;
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

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public MacroBoss(final BatchFramework batchFramework) {
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
        throws IllegalArgumentException, VerifyException
    {

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()) == 0)
        {
            macroManager = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_MACRO_FOLDER.name()) == 0)
        {
            setMacroFolder(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_MACRO_LANGUAGE.name()) == 0)
        {
            setMacroLanguage(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_RUN_MACRO_INIT.name()) == 0)
        {
            runMacroInit(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_RUN_MACRO.name()) == 0)
        {
            runMacro(runParm);
            return true;
        }

        return false;
    }

    /**
     * Validate Macro Folder RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws IllegalArgumentException if an error occurred
     */
    protected void setMacroFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        getMacroManager().setMacroFolder(
            editExistingFolderRunParm(batchFramework.paths.getMMJ2Path(),
                runParm, UtilConstants.RUNPARM_MACRO_FOLDER.name(), 1));
    }

    /**
     * Validate Macro Language RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws IllegalArgumentException if an error occurred
     */
    protected void setMacroLanguage(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_MACRO_LANGUAGE.name(), 2);
        getMacroManager().setMacroLanguage(runParm.values[0],
            runParm.values[1]);
    }

    /**
     * Validate RunMacro RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     * @throws IllegalArgumentException if an error occurred
     */
    protected void runMacroInit(final RunParmArrayEntry runParm)
        throws VerifyException, IllegalArgumentException
    {
        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_RUN_MACRO.name(),
            1);
        getMacroManager().getEngine(runParm.values[0]);
    }

    /**
     * Validate RunMacro RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     * @throws IllegalArgumentException if an error occurred
     */
    protected void runMacro(final RunParmArrayEntry runParm)
        throws VerifyException, IllegalArgumentException
    {
        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_RUN_MACRO.name(),
            1);
        getMacroManager().runMacro(ExecutionMode.RUNPARM, runParm.values);
    }

    /**
     * Fetches a reference to the MacroManager, first initializing it if
     * necessary.
     *
     * @return MacroManager object ready to go.
     */
    public MacroManager getMacroManager() {

        if (macroManager == null)
            macroManager = new MacroManager(batchFramework);

        return macroManager;
    }
}
