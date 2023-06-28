//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.Formula;
import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/**
 * The transformation of {@link Transformation#originalNode} to any equal node.
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
     *         {@link Transformation#originalNode}. Could returns null it this
     *         and target are equal.
     */
    public abstract GenProofStepStmt transformMeToTarget(
        final Transformation target, final WorksheetInfo info);

    /**
     * It is a possible result for {@link #checkTransformationNecessary}
     * function. This marker indicates that we should not do anything to get
     * target transformation.
     */
    // TODO: Some old code compares with "null". Change it to
    // NOTHING_TO_TRANSFORM
    public static final GenProofStepStmt NOTHING_TO_TRANSFORM = null;

    /**
     * It is a possible result for {@link #checkTransformationNecessary}
     * function. This marker indicates that it is not simple case and we should
     * perform more complex transformation.
     */
    public static final GenProofStepStmt MORE_COMPLEX_TRANSFORMATION = new GenProofStepStmt(
        null, null);

    /**
     * This function checks maybe we should not do anything! We should perform
     * transformations if this function returns
     * {@link #MORE_COMPLEX_TRANSFORMATION}.
     *
     * @param target the target transformation
     * @param info the information about work sheet
     * @return {@link #NOTHING_TO_TRANSFORM} (if target equals to this), general
     *         statement (if it is already exists) or
     *         {@link #MORE_COMPLEX_TRANSFORMATION} if we should perform some
     *         transformations!
     */
    protected final GenProofStepStmt checkTransformationNecessary(
        final Transformation target, final WorksheetInfo info)
    {
        if (originalNode.isDeepDup(target.originalNode))
            return NOTHING_TO_TRANSFORM;

        // Create node g(A, B, C) = g(A', B', C')
        final ParseNode stepNode = eqInfo.createEqNode(originalNode,
            target.originalNode);

        final ProofStepStmt stepTr = info.getProofStepStmt(stepNode);

        if (stepTr != null)
            return new GenProofStepStmt(stepTr, null);

        if (info.hasImplPrefix()) {
            // Create node "prefix -> ( g(A, B, C) = g(A', B', C') )"
            final ParseNode implNode = info.applyImplPrefix(stepNode);

            final ProofStepStmt implTr = info.getProofStepStmt(implNode);

            if (implTr != null)
                return new GenProofStepStmt(implTr, info.implPrefix);
        }

        return MORE_COMPLEX_TRANSFORMATION; // there is more complex case!
    }

    // use it only for debug!
    @Override
    public String toString() {
        final Formula f = trManager.getFormula(originalNode);
        return f.toString();
    }
}
