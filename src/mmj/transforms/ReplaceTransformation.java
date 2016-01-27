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
        if (info.implStatement == originalNode.stmt) {
            assert originalNode.child.length == 2;
            if (originalNode.child[0].isDeepDup(info.implPrefix)
                && target.originalNode.child[0].isDeepDup(info.implPrefix))
                couldSimplifyGenStep = true;
        }

        final boolean[] replAsserts = replInfo
            .getPossibleReplaces(originalNode.stmt, info);

        assert replAsserts != null;

        for (int i = 0; i < originalNode.child.length; i++) {
            if (!replAsserts[i])
                continue;
            // We replaced previous children and now we should transform ith
            // child B

            final ParseNode child = originalNode.child[i];
            final ParseNode targetChild = target.originalNode.child[i];

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
            // "ph -> ( child <-> child') " and this node has form "ph -> child"
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
            assert eqInfo.isEquivalence(childTrRoot.stmt);
            assert childTrRoot.child.length == 2;
            assert childTrRoot.child[0].isDeepDup(originalNode.child[i]);
            assert childTrRoot.child[1].isDeepDup(trgt.originalNode.child[i]);

            // Create statement d:childTrStmt:replAssert
            // |- g(A', B, C) = g(A', B', C)
            final GenProofStepStmt stepTr = replInfo.createReplaceStep(info,
                resNode, i, trgt.originalNode.child[i], childTrStmt);
            final ParseNode r = stepTr.getCore();
            resNode = r.child[1];

            // resStmt now have the form g(A, B, C) = g(A', B, C)
            // So create transitive:
            // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
            // => g(A, B, C) = g(A', B', C)
            // Create statement d:resStmt,stepTr:transitive
            // |- g(A, B, C) = g(A', B', C)
            resStmt = eqInfo.getTransitiveStep(info, resStmt, stepTr);
        }

        assert resStmt != null;
        final ParseNode r = resStmt.getCore();

        assert r.child[0].isDeepDup(originalNode);
        final ParseNode r1 = resStmt.getCore();
        assert r1.child[1].isDeepDup(target.originalNode);

        return resStmt;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final boolean[] replAsserts = replInfo
            .getPossibleReplaces(originalNode.stmt, info);
        assert replAsserts != null;

        final ParseNode resNode = originalNode.shallowClone();

        for (int i = 0; i < resNode.child.length; i++) {
            if (!replAsserts[i])
                continue;
            resNode.child[i] = trManager.getCanonicalForm(originalNode.child[i],
                info);
        }

        return resNode;
    }
}
