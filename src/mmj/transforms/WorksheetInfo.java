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
    final ProofWorksheet proofWorksheet;
    final DerivationStep derivStep;

    final List<DerivationStep> newSteps = new ArrayList<DerivationStep>();

    private final VerifyProofs verifyProofs;
    private final Cnst provableLogicStmtTyp;

    public WorksheetInfo(final ProofWorksheet proofWorksheet,
        final DerivationStep derivStep, final VerifyProofs verifyProofs,
        final Cnst provableLogicStmtTyp)
    {
        super();
        this.proofWorksheet = proofWorksheet;
        this.derivStep = derivStep;
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;
    }

    public ProofStepStmt getProofStepStmt(final ParseNode stepNode) {
        final ProofStepStmt stepTr = getOrCreateProofStepStmt(stepNode, null,
            null);
        return stepTr;
    }

    ProofStepStmt getOrCreateProofStepStmt(final ParseNode root,
        final ProofStepStmt[] hyps, final Assrt assrt)
    {
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

        assert assrt.getLogHypArray().length == hyps.length;

        final String[] steps = new String[hyps.length];
        for (int i = 0; i < hyps.length; i++)
            steps[i] = hyps[i].getStep();

        final DerivationStep d = proofWorksheet.addDerivStep(derivStep, hyps,
            steps, assrt.getLabel(), generatedFormula, tree,
            Collections.<WorkVar> emptyList());
        d.setRef(assrt);
        // d.unificationStatus = PaConstants.UNIFICATION_STATUS_UNIFIED;
        newSteps.add(d);
        return d;
    }
}
