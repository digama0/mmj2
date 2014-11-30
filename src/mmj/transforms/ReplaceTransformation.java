//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/**
 * The replace transformation: we could transform children of corresponding node
 * and replace them with its canonical form (or vice versa).
 */
public class ReplaceTransformation extends Transformation {
    private final ReplaceInfo replInfo;

    public ReplaceTransformation(final TransformationManager trManager,
        final ParseNode originalNode)
    {
        super(trManager, originalNode);
        replInfo = trManager.replInfo;
    }

    @Override
    public GenProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof ReplaceTransformation;
        final ReplaceTransformation trgt = (ReplaceTransformation)target;

        final GenProofStepStmt simpleRes = checkTransformationNecessary(target,
            info);
        if (simpleRes != MORE_COMPLEX_TRANSFORMATION)
            return simpleRes;

        // result transformation statement
        GenProofStepStmt resStmt = null;

        // the current transformation result
        ParseNode resNode = originalNode;

        boolean couldSimplifyGenStep = false;
        if (info.implStatement == originalNode.getStmt()) {
            assert originalNode.getChild().length == 2;
            if (originalNode.getChild()[0].isDeepDup(info.implPrefix)
                && target.originalNode.getChild()[0].isDeepDup(info.implPrefix))
                couldSimplifyGenStep = true;
        }

        final boolean[] replAsserts = replInfo.getPossibleReplaces(
            originalNode.getStmt(), info);

        assert replAsserts != null;

        for (int i = 0; i < originalNode.getChild().length; i++) {
            if (!replAsserts[i])
                continue;
            // We replaced previous children and now we should transform ith
            // child B

            final ParseNode child = originalNode.getChild()[i];
            final ParseNode targetChild = target.originalNode.getChild()[i];

            // get the root symbol from g(A', B, C) = g(A', B', C)
            // in set.mm it will be = or <->
            // replAsserts[i].getExprParseTree().getRoot()
            // .getStmt();

            // transform ith child
            // the result should be some B = B' statement
            final GenProofStepStmt childTrStmt = trManager
                .createTransformation(child, info).transformMeToTarget(
                    trManager.createTransformation(targetChild, info), info);
            if (childTrStmt == null)
                continue; // noting to do

            // If child transformation has implication prefix and has form
            // "ph -> ( child <-> chald') " and this node has form "ph -> child"
            // then we could apply distributive rule!
            if (couldSimplifyGenStep && i == 1 && childTrStmt.hasPrefix()) {
                final ProofStepStmt res = info.trManager.implInfo
                    .applyDisrtibutiveRule(info,
                        childTrStmt.getImplicationStep());
                return new GenProofStepStmt(res, null);
            }

            // get the node B = B'
            final ParseNode childTrRoot = childTrStmt.getCore();

            // transformation should be transformation:
            // check result childTrStmt statement
            assert eqInfo.isEquivalence(childTrRoot.getStmt());
            assert childTrRoot.getChild().length == 2;
            assert childTrRoot.getChild()[0]
                .isDeepDup(originalNode.getChild()[i]);
            assert childTrRoot.getChild()[1].isDeepDup(trgt.originalNode
                .getChild()[i]);

            // Create statement d:childTrStmt:replAssert
            // |- g(A', B, C) = g(A', B', C)
            final GenProofStepStmt stepTr = replInfo.createReplaceStep(info,
                resNode, i, trgt.originalNode.getChild()[i], childTrStmt);
            resNode = stepTr.getCore().getChild()[1];

            // resStmt now have the form g(A, B, C) = g(A', B, C)
            // So create transitive:
            // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
            // => g(A, B, C) = g(A', B', C)
            // Create statement d:resStmt,stepTr:transitive
            // |- g(A, B, C) = g(A', B', C)
            resStmt = eqInfo.getTransitiveStep(info, resStmt, stepTr);
        }

        assert resStmt != null;

        assert resStmt.getCore().getChild()[0].isDeepDup(originalNode);
        assert resStmt.getCore().getChild()[1].isDeepDup(target.originalNode);

        return resStmt;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final boolean[] replAsserts = replInfo.getPossibleReplaces(
            originalNode.getStmt(), info);
        assert replAsserts != null;

        final ParseNode resNode = originalNode.cloneWithoutChildren();

        for (int i = 0; i < resNode.getChild().length; i++) {
            if (!replAsserts[i])
                continue;
            resNode.getChild()[i] = trManager.getCanonicalForm(
                originalNode.getChild()[i], info);
        }

        return resNode;
    }
}
