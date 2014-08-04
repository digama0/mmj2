package mmj.transforms;

import mmj.lang.ParseNode;
import mmj.lang.Stmt;
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
    public ProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof ReplaceTransformation;
        final ReplaceTransformation trgt = (ReplaceTransformation)target;

        final ProofStepStmt simpleRes = checkTransformationNecessary(target,
            info);
        if (simpleRes != info.derivStep)
            return simpleRes;

        // result transformation statement
        ProofStepStmt resStmt = null;

        // the current transformation result
        ParseNode resNode = originalNode;

        final boolean[] replAsserts = replInfo.getPossibleReplaces(originalNode
            .getStmt());

        final Stmt equalStmt = eqInfo
            .getEqStmt(originalNode.getStmt().getTyp());

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

            // the symbol should be equivalence operator
            assert eqInfo.isEquivalence(equalStmt);

            // transform ith child
            // the result should be some B = B' statement
            final ProofStepStmt childTrStmt = trManager.createTransformation(
                child, info).transformMeToTarget(
                trManager.createTransformation(targetChild, info), info);
            if (childTrStmt == null)
                continue; // noting to do

            // get the node B = B'
            final ParseNode childTrRoot = childTrStmt.formulaParseTree
                .getRoot();

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
            final ProofStepStmt stepTr = replInfo.createReplaceStep(info,
                resNode, i, trgt.originalNode.getChild()[i], childTrStmt);
            resNode = stepTr.formulaParseTree.getRoot().getChild()[1];

            // resStmt now have the form g(A, B, C) = g(A', B, C)
            // So create transitive:
            // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
            // => g(A, B, C) = g(A', B', C)
            // Create statement d:resStmt,stepTr:transitive
            // |- g(A, B, C) = g(A', B', C)
            resStmt = eqInfo.getTransitiveStep(info, resStmt, stepTr);
        }

        assert resStmt != null;

        assert resStmt.formulaParseTree.getRoot().getChild()[0]
            .isDeepDup(originalNode);
        assert resStmt.formulaParseTree.getRoot().getChild()[1]
            .isDeepDup(target.originalNode);

        return resStmt;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final boolean[] replAsserts = replInfo.getPossibleReplaces(originalNode
            .getStmt());
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
