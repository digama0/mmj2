//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;

/** Only commutative transformations */
public class CommutativeTransformation extends Transformation {
    private final GeneralizedStmt genStmt;

    public final CommutativeInfo comInfo;

    public CommutativeTransformation(final TransformationManager trManager,
        final ParseNode originalNode, final GeneralizedStmt comProp)
    {
        super(trManager, originalNode);
        genStmt = comProp;
        comInfo = trManager.comInfo;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final ParseNode[] canonical = new ParseNode[2];
        for (int i = 0; i < 2; i++) {
            final ParseNode child = originalNode.child[genStmt.varIndexes[i]];
            canonical[i] = trManager.getCanonicalForm(child, info);
        }

        ParseNode first = canonical[0];
        ParseNode second = canonical[1];

        final int res = CommutativeInfo.compareNodes(first, second);

        if (res < 0) {
            first = canonical[1];
            second = canonical[0];
        }

        return TrUtil.createGenBinaryNode(genStmt, first, second);
    }

    @Override
    public GenProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof CommutativeTransformation;

        final GenProofStepStmt simpleRes = checkTransformationNecessary(target,
            info);
        if (simpleRes != MORE_COMPLEX_TRANSFORMATION)
            return simpleRes;

        final ParseNode canonicalMe = trManager.getCanonicalForm(
            originalNode.child[genStmt.varIndexes[0]], info);
        final ParseNode canonicalTrgt = trManager.getCanonicalForm(
            target.originalNode.child[genStmt.varIndexes[0]], info);

        final GenProofStepStmt reverseStep;
        final ParseNode myNode;
        if (!canonicalMe.isDeepDup(canonicalTrgt)) {
            final ParseNode left = originalNode.child[genStmt.varIndexes[0]];
            final ParseNode right = originalNode.child[genStmt.varIndexes[1]];

            myNode = TrUtil.createGenBinaryNode(genStmt, right, left);

            reverseStep = comInfo.createCommutativeStep(info, genStmt,
                originalNode, myNode);
            /*
            final Assrt comAssrt = comInfo.getComOp(genStmt);

            final Stmt equalStmt = comAssrt.getExprParseTree().getRoot()
                .getStmt();

            // Create node f(a, b) = f(b, a)
            final ParseNode stepNode = TrUtil.createBinaryNode(equalStmt,
                originalNode, myNode);
            reverseStep = comInfo.closurePropertyCommutative(info, genStmt,
                comAssrt, stepNode);
            */
        }
        else {
            myNode = originalNode;
            reverseStep = null;
        }

        final Transformation replaceMe = new ReplaceTransformation(trManager,
            myNode);
        final Transformation replaceTarget = new ReplaceTransformation(
            trManager, target.originalNode);
        final GenProofStepStmt replTrStep = replaceMe.transformMeToTarget(
            replaceTarget, info);

        final GenProofStepStmt res;
        if (reverseStep != null && replTrStep != null)
            res = eqInfo.getTransitiveStep(info, reverseStep, replTrStep);
        else if (reverseStep != null)
            res = reverseStep;
        else
            res = replTrStep;

        return res;
    }
}
