//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * WorkVarBoss.java  0.01 08/01/2007
 *
 * Version 0.01: 08/01/2007
 *     - New.
 */

package mmj.util;

import static mmj.util.UtilConstants.*;

import java.util.function.BooleanSupplier;

import mmj.lang.Messages;
import mmj.lang.WorkVarManager;
import mmj.verify.Grammar;
import mmj.verify.VerifyException;

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
public class WorkVarBoss extends Boss {

    private WorkVarManager workVarManager;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public WorkVarBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        final BooleanSupplier clear = () -> {
            workVarManager = null;
            return false; // not "consumed"
        };

        putCommand(RUNPARM_CLEAR, clear);
        putCommand(RUNPARM_LOAD_FILE, clear);

        putCommand(RUNPARM_DEFINE_WORK_VAR_TYPE, this::editDefineWorkVarType);

        putCommand(RUNPARM_DECLARE_WORK_VARS, this::editDeclareWorkVars);
    }

    /**
     * Fetch a WorkVarManager object.
     * <p>
     * Requires that a LogicalSystem be loaded with a .mm file and that an
     * initialized Grammar object be available.
     *
     * @return WorkVarManager object, ready to go, or null.
     */
    public WorkVarManager getWorkVarManager() {

        if (workVarManager != null)
            return workVarManager;

        final Messages messages = batchFramework.outputBoss.getMessages();

        final Grammar grammar = batchFramework.grammarBoss.getGrammar();

        if (grammar.getGrammarInitialized())
            workVarManager = new WorkVarManager(grammar);
        else
            messages.accumMessage(ERRMSG_WV_MGR_REQUIRES_GRAMMAR_INIT);

        batchFramework.outputBoss.printAndClearMessages();

        return workVarManager;
    }

    /**
     * Validate DefineWorkVarType RunParm.
     */
    protected void editDefineWorkVarType() {
        require(3);
        try {
            getWorkVarManager().defineWorkVarType(
                batchFramework.grammarBoss.getGrammar(), get(1), get(2),
                getPosInt(3));
        } catch (final VerifyException e) {
            throw error(e);
        }
    }

    /**
     * Create and initialize the Work Vars to be used.
     */
    protected void editDeclareWorkVars() {
        try {
            getWorkVarManager().declareWorkVars(
                batchFramework.grammarBoss.getGrammar(),
                batchFramework.logicalSystemBoss.getLogicalSystem());
        } catch (final VerifyException e) {
            throw error(e);
        }
    }
}
