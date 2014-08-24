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

    public final List<DerivationStep> newSteps = new ArrayList<DerivationStep>();

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

    private int debugCounter = 0;

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

    public ProofStepStmt getProofStepStmt(final ParseNode stepNode) {
        assert !finished;
        final ProofStepStmt stepTr = getOrCreateProofStepStmt(stepNode, null,
            null);
        return stepTr;
    }

    public ProofStepStmt getOrCreateProofStepStmt(final ParseNode root,
        final ProofStepStmt[] hyps, final Assrt assrt)
    {
        assert !finished;
        final ParseTree tree = new ParseTree(root);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        generatedFormula.setTyp(provableLogicStmtTyp);

        final ProofStepStmt findMatchingStepFormula = proofWorksheet
            .findMatchingStepFormula(generatedFormula, derivStep);

        if (findMatchingStepFormula != null)
            return findMatchingStepFormula;

        if (hyps == null || assrt == null)
            return null;

        debugCounter++;

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
            output.dbgMessage(dbg, "I-TR-DBG Emminted step: " + str);
        }

        return d;
    }

    public void finishDerivationStep(final ProofStepStmt[] hyps,
        final Assrt assrt)
    {
        assert !finished;
        debugCounter++;

        if (debugCounter == 3)
            toString();

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
            output.dbgMessage(dbg, "I-TR-DBG Finished step: " + str);
        }
    }

    private String getDebugString(final DerivationStep d,
        final ProofStepStmt[] hyps)
    {
        String str = "(+" + debugCounter + "+): ";
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

        public SubstParam(final ProofStepStmt[] hypDerivArray, final Assrt assrt)
        {
            this.hypDerivArray = hypDerivArray;
            this.assrt = assrt;
        }
    }

    public static class GenProofStepStmt {
        public final ProofStepStmt step;
        public final ParseNode prefix;

        public GenProofStepStmt(final ProofStepStmt step, final ParseNode prefix)
        {
            super();
            this.step = step;
            this.prefix = prefix;
        }

        public boolean hasPrefix() {
            return prefix != null;
        }

        public ParseNode getCore() {
            final ParseNode root = step.formulaParseTree.getRoot();
            if (prefix == null)
                return root;
            else {
                assert root.getChild().length == 2;
                return root.getChild()[1];
            }
        }
    }
}
