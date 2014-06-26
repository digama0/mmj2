package mmj.transforms;

import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/** Only associative transformations */
class AssociativeTransformation extends Transformation {
    private final AssocTree structure;
    private final GeneralizedStmt assocProp;

    private final ReplaceInfo replInfo;
    private final AssociativeInfo assocInfo;

    public AssociativeTransformation(final TransformationManager trManager,
        final ParseNode originalNode, final AssocTree structure,
        final GeneralizedStmt assocProp)
    {
        super(trManager, originalNode);
        this.structure = structure;
        this.assocProp = assocProp;

        replInfo = trManager.replInfo;
        assocInfo = trManager.assocInfo;
    }

    @Override
    public ProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof AssociativeTransformation;
        final AssociativeTransformation trgt = (AssociativeTransformation)target;

        assert trgt.structure.size == structure.size;

        final ProofStepStmt simpleRes = checkTransformationNecessary(target,
            info);
        if (simpleRes != info.derivStep)
            return simpleRes;

        originalNode.getStmt();

        final int from;
        final int to;
        if (structure.subTrees[assocProp.varIndexes[0]].size > trgt.structure.subTrees[assocProp.varIndexes[0]].size)
        {
            from = assocProp.varIndexes[0];
            to = assocProp.varIndexes[1];
        }
        else {
            from = assocProp.varIndexes[1];
            to = assocProp.varIndexes[0];
        }

        final int toSize = trgt.structure.subTrees[to].size;

        AssocTree gAssT = structure.duplicate(); // g node
        ParseNode gNode = originalNode;

        // result transformation statement
        ProofStepStmt resStmt = null;

        while (true) {

            // move subtrees to the other side
            while (true) {
                // @formatter:off
                //         |g                  |                 +
                //        / \                 / \                +
                //       /   \               /   \               +
                //     e|     |f           a|     |              +
                //     / \          ==>          / \             +
                //    /   \                     /   \            +
                //  a|     |d                 d|     |f          +
                // @formatter:on

                final AssocTree eAssT = gAssT.subTrees[from];
                final AssocTree fAssT = gAssT.subTrees[to];

                if (fAssT.size >= toSize)
                    break;

                final AssocTree aAssT = eAssT.subTrees[from];
                final AssocTree dAssT = eAssT.subTrees[to];

                if (dAssT.size + fAssT.size > toSize)
                    break;

                gAssT = new AssocTree(from, aAssT, new AssocTree(from, dAssT,
                    fAssT));

                final ParseNode eNode = gNode.getChild()[from];
                final ParseNode fNode = gNode.getChild()[to];
                final ParseNode aNode = eNode.getChild()[from];
                final ParseNode dNode = eNode.getChild()[to];

                final ParseNode prevNode = gNode;

                gNode = TrUtil
                    .createAssocBinaryNode(from, assocProp, aNode, TrUtil
                        .createAssocBinaryNode(from, assocProp, dNode, fNode));

                // transform to normal direction => 'from'
                final ProofStepStmt assocTr = assocInfo.createAssociativeStep(
                    info, assocProp, from, prevNode, gNode);

                resStmt = eqInfo.getTransitiveStep(info, resStmt, assocTr);
            }

            final AssocTree eAssT = gAssT.subTrees[from];
            final AssocTree fAssT = gAssT.subTrees[to];

            if (fAssT.size != toSize) {
                final AssocTree aAssT = eAssT.subTrees[from];
                final AssocTree dAssT = eAssT.subTrees[to];
                final AssocTree bAssT = dAssT.subTrees[from];
                final AssocTree cAssT = dAssT.subTrees[to];

                // reconstruct 'from' part
                // @formatter:off
                //         |g                  |g            +
                //        / \                 / \            +
                //       /   \               /   \           +
                //     e|     |f          e'|     |f         +
                //     / \          ==>    / \               +
                //    /   \               /   \              +
                //  a|     |d            |     |c            +
                //        / \           / \                  +
                //       /   \         /   \                 +
                //     b|     |c     a|     |b               +
                // @formatter:on

                gAssT.subTrees[from] = new AssocTree(from, new AssocTree(from,
                    aAssT, bAssT), cAssT);

                /*--*/ParseNode eNode = gNode.getChild()[from];
                final ParseNode fNode = gNode.getChild()[to];
                final ParseNode aNode = eNode.getChild()[from];
                final ParseNode dNode = eNode.getChild()[to];
                final ParseNode bNode = dNode.getChild()[from];
                final ParseNode cNode = dNode.getChild()[to];

                final ParseNode prevENode = eNode;
                eNode = TrUtil
                    .createAssocBinaryNode(from, assocProp, TrUtil
                        .createAssocBinaryNode(from, assocProp, aNode, bNode),
                        cNode);

                // transform to other direction => 'to'
                final ProofStepStmt assocTr = assocInfo.createAssociativeStep(
                    info, assocProp, to, prevENode, eNode);

                final ParseNode prevGNode = gNode;
                gNode = TrUtil.createAssocBinaryNode(from, assocProp, eNode,
                    fNode);

                final ProofStepStmt replTr = replInfo.createReplaceStep(info,
                    prevGNode, from, eNode, assocTr);

                resStmt = eqInfo.getTransitiveStep(info, resStmt, replTr);
            }
            else
                break;
        }

        assert gAssT.subTrees[0].size == trgt.structure.subTrees[0].size;
        assert gAssT.subTrees[1].size == trgt.structure.subTrees[1].size;

        final Transformation replaceMe = new ReplaceTransformation(trManager,
            gNode);
        final Transformation replaceTarget = new ReplaceTransformation(
            trManager, target.originalNode);

        final ProofStepStmt replTrStep = replaceMe.transformMeToTarget(
            replaceTarget, info);

        if (replTrStep != null)
            resStmt = eqInfo.getTransitiveStep(info, resStmt, replTrStep);

        return resStmt;
    }

    private ParseNode constructCanonicalForm(ParseNode left,
        final ParseNode cur, final WorksheetInfo info)
    {
        if (cur.getStmt() != originalNode.getStmt()) {
            final ParseNode leaf = trManager.getCanonicalForm(cur, info);
            if (left == null)
                return leaf;
            else
                return TrUtil.createGenBinaryNode(assocProp, left, leaf);
        }

        for (int i = 0; i < 2; i++) {
            final ParseNode child = cur.getChild()[assocProp.varIndexes[i]];
            left = constructCanonicalForm(left, child, info);
        }

        return left;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        return constructCanonicalForm(null, originalNode, info);
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + structure;
    }
}
