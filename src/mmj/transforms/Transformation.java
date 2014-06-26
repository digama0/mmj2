package mmj.transforms;

import mmj.lang.Formula;
import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/**
 * The transformation of {@link Transformation#originalNode} to any equal
 * node.
 */
public abstract class Transformation {
    /** The original node */
    public final ParseNode originalNode;

    protected final TransformationManager trManager;

    // it is only the copy of the pointer
    protected final EquivalenceInfo eqInfo;

    public Transformation(final TransformationManager trManager,
        final ParseNode originalNode)
    {
        this.originalNode = originalNode;
        this.trManager = trManager;
        eqInfo = trManager.eqInfo;
    }

    /**
     * @param info the information about previous statements
     * @return the canonical node for {@link Transformation#originalNode}
     */
    abstract public ParseNode getCanonicalNode(WorksheetInfo info);

    /**
     * This function should construct derivation step sequence from this
     * {@link Transformation#originalNode} to target's
     * {@link Transformation#originalNode}
     * 
     * @param target the target transformation
     * @param info the information about work sheet
     * @return the proof step which confirms that this
     *         {@link Transformation#originalNode} is equal to target
     *         {@link Transformation#originalNode}. Could returns null it
     *         this and target are equal.
     */
    public abstract ProofStepStmt transformMeToTarget(
        final Transformation target, final WorksheetInfo info);

    /**
     * This function checks maybe we should not do anything! We should
     * perform transformations if this function returns derivStep.
     * 
     * @param target the target transformation
     * @param info the information about work sheet
     * @return null (if target equals to this), statement (if it is already
     *         exists) or info.derivStep if we should perform some
     *         transformations!
     */
    protected ProofStepStmt checkTransformationNecessary(
        final Transformation target, final WorksheetInfo info)
    {
        if (originalNode.isDeepDup(target.originalNode))
            return null; // nothing to transform!

        // Create node g(A, B, C) = g(A', B', C')
        final ParseNode stepNode = eqInfo.createEqNode(originalNode,
            target.originalNode);

        final ProofStepStmt stepTr = info.getProofStepStmt(stepNode);

        if (stepTr != null)
            return stepTr;

        return info.derivStep; // there is more complex case!
    }

    // use it only for debug!
    @Override
    public String toString() {
        final Formula f = trManager.getFormula(originalNode);
        return f.toString();
    }
}