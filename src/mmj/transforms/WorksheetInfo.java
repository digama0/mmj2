//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.pa.*;
import mmj.verify.VerifyProofs;

/**
 * Information about work sheet.
 * <p>
 * This class has local package visibility.
 */
/*local*/class WorksheetInfo {
    private final boolean finished = false;

    public final TrOutput output;
    public final boolean dbg;

    public final ProofWorksheet proofWorksheet;
    public final DerivationStep derivStep;

    public final List<DerivationStep> newSteps = new ArrayList<>();

    public final TransformationManager trManager;

    /**
     * This field could be null. If it is not null, then we could use this
     * prefix in hypothesis. For example, if the derivations step is
     * "( ph -> ( A + B ) = C )", then we could search not only "A e. CC", but
     * also "( ph -> A e. CC )"
     */
    public ParseNode implPrefix = null;
    public Stmt implStatement = null;

    private final VerifyProofs verifyProofs;
    private final Cnst provableLogicStmtTyp;

    public WorksheetInfo(final ProofWorksheet proofWorksheet,
        final DerivationStep derivStep, final TransformationManager trManager)
    {
        super();
        this.proofWorksheet = proofWorksheet;
        this.derivStep = derivStep;
        this.trManager = trManager;
        output = trManager.output;
        dbg = trManager.dbg;
        verifyProofs = trManager.verifyProofs;
        provableLogicStmtTyp = trManager.provableLogicStmtTyp;
    }

    /**
     * @param root searched expression
     * @return an existed step with root equals to "root" or null
     */
    public ProofStepStmt getProofStepStmt(final ParseNode root) {
        assert !finished;
        return getOrCreateProofStepStmt(root, null, null);
    }

    /**
     * @param root searched expression
     * @param hyps hypotheses needed for result step construction (could be null
     *            if we want existed step)
     * @param assrt assert needed for result step construction (could be null if
     *            we want existed step)
     * @return an existed step with root equals to stepNode or new step
     */
    public ProofStepStmt getOrCreateProofStepStmt(final ParseNode root,
        final ProofStepStmt[] hyps, final Assrt assrt)
    {
        return getOrCreateProofStepStmt(root, hyps, assrt, true);
    }

    /**
     * @param root searched expression
     * @param hyps hypotheses needed for result step construction (could be null
     *            if we want existed step)
     * @param assrt assert needed for result step construction (could be null if
     *            we want existed step)
     * @param get True to search for matching steps
     * @return an existed step with root equals to stepNode or new step
     */
    public ProofStepStmt getOrCreateProofStepStmt(final ParseNode root,
        final ProofStepStmt[] hyps, final Assrt assrt, final boolean get)
    {
        assert !finished;
        final ParseTree tree = new ParseTree(root);
        final Formula generatedFormula = verifyProofs
            .convertRPNToFormula(tree.convertToRPN(), "tree"); // TODO: use
                                                               // constant
        generatedFormula.setTyp(provableLogicStmtTyp);

        if (get) {
            final ProofStepStmt findMatchingStepFormula = proofWorksheet
                .findMatchingStepFormula(generatedFormula, derivStep);

            if (findMatchingStepFormula != null)
                return findMatchingStepFormula;
        }

        if (hyps == null || assrt == null)
            return null;

        assert assrt.getLogHypArray().length == hyps.length;

        final String[] steps = new String[hyps.length];
        for (int i = 0; i < hyps.length; i++)
            steps[i] = hyps[i].getStep();

        final DerivationStep d = proofWorksheet.addDerivStep(derivStep, hyps,
            steps, assrt.getLabel(), generatedFormula, tree,
            Collections.<WorkVar> emptyList());
        d.setRef(assrt);
        newSteps.add(d);

        if (dbg) {
            final String str = getDebugString(d, hyps);
            output.dbgMessage(dbg, TrConstants.ERRMSG_EMITTED_STEP, str);
        }

        return d;
    }

    /**
     * @param root searched expression
     * @param hyps hypotheses needed for result step construction (could be null
     *            if we want existed step)
     * @param assrt assert needed for result step construction (could be null if
     *            we want existed step)
     * @return an existed step with root equals to stepNode or new step
     */
    public DerivationStep createProofStepStmt(final ParseNode root,
        final ProofStepStmt[] hyps, final Assrt assrt)
    {
        assert !finished;
        return (DerivationStep)getOrCreateProofStepStmt(root, hyps, assrt,
            false);
    }

    public DerivationStep giveUpProofStepStmt(final ParseNode root) {
        final ParseTree tree = new ParseTree(root);
        final Formula generatedFormula = verifyProofs
            .convertRPNToFormula(tree.convertToRPN(), "tree"); // TODO: use
                                                               // constant
        generatedFormula.setTyp(provableLogicStmtTyp);

        final ProofStepStmt[] hyps = new ProofStepStmt[0];
        final DerivationStep d = proofWorksheet.addDerivStep(derivStep, hyps,
            new String[0], null, generatedFormula, tree,
            Collections.<WorkVar> emptyList());
        newSteps.add(d);

        if (dbg) {
            final String str = getDebugString(d, hyps);
            output.dbgMessage(dbg, TrConstants.ERRMSG_EMITTED_STEP, str);
        }

        return d;
    }

    public void finishDerivationStep(final ProofStepStmt[] hyps,
        final Assrt assrt)
    {
        assert !finished;

        final String[] steps = new String[hyps.length];
        for (int i = 0; i < steps.length; i++)
            steps[i] = hyps[i].getStep();

        derivStep.setRef(assrt);
        derivStep.setRefLabel(assrt.getLabel());
        derivStep.setHypList(hyps);
        derivStep.setHypStepList(steps);
        derivStep.setAutoStep(false);
        // confirm unification for derivStep also!
        newSteps.add(derivStep);

        if (dbg) {
            final String str = getDebugString(derivStep, hyps);
            output.dbgMessage(dbg, TrConstants.ERRMSG_FINISHED_STEP, str);
        }
    }

    private String getDebugString(final DerivationStep d,
        final ProofStepStmt[] hyps)
    {
        String str = "";
        for (int i = 0; i < hyps.length; i++) {
            if (i != 0)
                str += " & ";
            str += hyps[i].toString();
        }

        str += " => " + d;
        return str;
    }

    public ParseNode applyImplPrefix(final ParseNode core) {
        return TrUtil.createBinaryNode(implStatement, implPrefix, core);
    }

    public boolean hasImplPrefix() {
        return implPrefix != null;
    }

    // ----------------------------------------------

    public static class SubstParam {
        final ProofStepStmt[] hypDerivArray;
        final Assrt assrt;

        public SubstParam(final ProofStepStmt[] hypDerivArray,
            final Assrt assrt)
        {
            this.hypDerivArray = hypDerivArray;
            this.assrt = assrt;
        }
    }

    public void setImplicationPrefix(final ParseNode implPrefix,
        final Stmt implStatement)
    {
        this.implPrefix = implPrefix;
        this.implStatement = implStatement;
    }
}
