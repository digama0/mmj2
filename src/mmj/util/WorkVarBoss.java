//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  WorkVarBoss.java  0.01 08/01/2007
 *
 *  Version 0.01: 08/01/2007
 *      - New.
 */

package mmj.util;

import mmj.lang.*;
import mmj.verify.Grammar;

/**
 *  Manages access to the WorkVarManager resource and
 *  processes Work Var RunParms.
 *  <p>
 *  Even though WorkVar and related classes are defined
 *  in the mmj.lang package, WorkVarBoss is coded as
 *  a separate "boss" so that Work Vars can be treated
 *  as a separate set of resources, separate from
 *  Logical System, Proof Assistant, etc. Initially
 *  though, WorkVars and friends are to be used with
 *  Proof Assistant -- and nowhere else.
 *
 */
public class WorkVarBoss extends Boss {

    private WorkVarManager workVarManager;

    /**
     *  Constructor with BatchFramework for access to environment.
     *
     *  @param batchFramework for access to environment.
     */
    public WorkVarBoss(final BatchFramework batchFramework) {
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
        throws IllegalArgumentException, VerifyException
    {

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR) == 0)
        {
            workVarManager = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE) == 0)
        {
            workVarManager = null;
            return false; // not "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_DEFINE_WORK_VAR_TYPE) == 0)
        {
            editDefineWorkVarType(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_DECLARE_WORK_VARS) == 0)
        {
            editDeclareWorkVars(runParm);
            return true;
        }

        return false;
    }

    /**
     *  Fetch a WorkVarManager object.
     *  <p>
     *  Requires that a LogicalSystem be loaded with a .mm
     *  file and that an initialized Grammar object be
     *  available.
     *  <p>
     *  @return WorkVarManager object, ready to go, or null.
     */
    public WorkVarManager getWorkVarManager() {

        if (workVarManager != null)
            return workVarManager;

        final Messages messages = batchFramework.outputBoss.getMessages();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        if (grammar.getGrammarInitialized())
            workVarManager = new WorkVarManager(grammar);
        else
            messages
                .accumErrorMessage(UtilConstants.ERRMSG_WV_MGR_REQUIRES_GRAMMAR_INIT);

        batchFramework.outputBoss.printAndClearMessages();

        return workVarManager;
    }

    /**
     *  Validate DefineWorkVarType RunParm.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editDefineWorkVarType(final RunParmArrayEntry runParm)
        throws VerifyException, IllegalArgumentException
    {

        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_DEFINE_WORK_VAR_TYPE, 3);

        final int nbrWorkVars = editRunParmValueReqPosInt(runParm,
            UtilConstants.RUNPARM_DEFINE_WORK_VAR_TYPE, 3);

        getWorkVarManager().defineWorkVarType(
            batchFramework.grammarBoss.getGrammar(), runParm.values[0].trim(),
            runParm.values[1].trim(), nbrWorkVars);
    }

    /**
     *  Create and initialize the Work Vars to be used.
     *
     *  @param runParm run parm parsed into RunParmArrayEntry object
     */
    protected void editDeclareWorkVars(final RunParmArrayEntry runParm)
        throws VerifyException, IllegalArgumentException
    {

        getWorkVarManager().declareWorkVars(
            batchFramework.grammarBoss.getGrammar(),
            batchFramework.logicalSystemBoss.getLogicalSystem());
    }
}
