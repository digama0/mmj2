package mmj.transforms;

import mmj.lang.Assrt;
import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/** Only commutative transformations */
public class CommutativeTransformation extends Transformation {
    private final GeneralizedStmt comProp;

    public final CommutativeInfo comInfo;

    public CommutativeTransformation(final TransformationManager trManager,
        final ParseNode originalNode, final GeneralizedStmt comProp)
    {
        super(trManager, originalNode);
        this.comProp = comProp;
        comInfo = trManager.comInfo;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final ParseNode[] canonical = new ParseNode[2];
        for (int i = 0; i < 2; i++) {
            final ParseNode child = originalNode.getChild()[comProp.varIndexes[i]];
            canonical[i] = trManager.getCanonicalForm(child, info);
        }

        ParseNode first = canonical[0];
        ParseNode second = canonical[1];

        final int res = CommutativeInfo.compareNodes(first, second);

        if (res < 0) {
            first = canonical[1];
            second = canonical[0];
        }

        return TrUtil.createGenBinaryNode(comProp, first, second);
    }

    @Override
    public ProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof CommutativeTransformation;

        final ParseNode canonicalMe = trManager.getCanonicalForm(
            originalNode.getChild()[comProp.varIndexes[0]], info);
        final ParseNode canonicalTrgt = trManager.getCanonicalForm(
            target.originalNode.getChild()[comProp.varIndexes[0]], info);

        final ProofStepStmt reverseStep;
        final ParseNode myNode;
        if (!canonicalMe.isDeepDup(canonicalTrgt)) {
            final ParseNode left = originalNode.getChild()[comProp.varIndexes[0]];
            final ParseNode right = originalNode.getChild()[comProp.varIndexes[1]];

            myNode = TrUtil.createGenBinaryNode(comProp, right, left);

            final Assrt comAssrt = comInfo.getComOp(comProp);

            reverseStep = comInfo.closurePropertyCommutative(info, comProp,
                comAssrt, myNode);
        }
        else {
            myNode = originalNode;
            reverseStep = null;
        }

        final Transformation replaceMe = new ReplaceTransformation(trManager,
            myNode);
        final Transformation replaceTarget = new ReplaceTransformation(
            trManager, target.originalNode);
        final ProofStepStmt replTrStep = replaceMe.transformMeToTarget(
            replaceTarget, info);

        final ProofStepStmt res;
        if (reverseStep != null)
            res = eqInfo.getTransitiveStep(info, reverseStep, replTrStep);
        else
            res = replTrStep;

        // TODO Auto-generated method stub
        return res;
    }
}
