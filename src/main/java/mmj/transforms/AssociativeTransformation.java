//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;

/** Only associative transformations */
class AssociativeTransformation extends Transformation {
    private final AssocTree structure;
    private final GeneralizedStmt genStmt;

    private final ReplaceInfo replInfo;
    private final AssociativeInfo assocInfo;

    public AssociativeTransformation(final TransformationManager trManager,
        final ParseNode originalNode, final AssocTree structure,
        final GeneralizedStmt assocProp)
    {
        super(trManager, originalNode);
        this.structure = structure;
        genStmt = assocProp;

        replInfo = trManager.replInfo;
        assocInfo = trManager.assocInfo;
    }

    @Override
    public GenProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof AssociativeTransformation;
        final AssociativeTransformation trgt = (AssociativeTransformation)target;

        assert trgt.structure.size == structure.size;

        final GenProofStepStmt simpleRes = checkTransformationNecessary(target,
            info);
        if (simpleRes != MORE_COMPLEX_TRANSFORMATION)
            return simpleRes;

        final int from;
        final int to;
        if (structure.subTrees[genStmt.varIndexes[0]].size > trgt.structure.subTrees[genStmt.varIndexes[0]].size)
        {
            from = genStmt.varIndexes[0];
            to = genStmt.varIndexes[1];
        }
        else {
            from = genStmt.varIndexes[1];
            to = genStmt.varIndexes[0];
        }

        final int toSize = trgt.structure.subTrees[to].size;

        AssocTree gAssT = structure.duplicate(); // g node
        ParseNode gNode = originalNode;

        // result transformation statement
        GenProofStepStmt resStmt = null;

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

                final ParseNode eNode = gNode.child[from];
                final ParseNode fNode = gNode.child[to];
                final ParseNode aNode = eNode.child[from];
                final ParseNode dNode = eNode.child[to];

                final ParseNode prevNode = gNode;

                gNode = TrUtil.createAssocBinaryNode(from, genStmt, aNode,
                    TrUtil.createAssocBinaryNode(from, genStmt, dNode, fNode));

                // transform to normal direction => use 'from'
                final GenProofStepStmt assocTr = assocInfo
                    .createAssociativeStep(info, genStmt, from, prevNode, gNode);

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

                /*--*/ParseNode eNode = gNode.child[from];
                final ParseNode fNode = gNode.child[to];
                final ParseNode aNode = eNode.child[from];
                final ParseNode dNode = eNode.child[to];
                final ParseNode bNode = dNode.child[from];
                final ParseNode cNode = dNode.child[to];

                final ParseNode prevENode = eNode;
                eNode = TrUtil.createAssocBinaryNode(from, genStmt,
                    TrUtil.createAssocBinaryNode(from, genStmt, aNode, bNode),
                    cNode);

                // transform to other direction => use 'to'
                final GenProofStepStmt assocTr = assocInfo
                    .createAssociativeStep(info, genStmt, to, prevENode, eNode);

                final ParseNode prevGNode = gNode;
                gNode = TrUtil.createAssocBinaryNode(from, genStmt, eNode,
                    fNode);

                final GenProofStepStmt replTr = replInfo.createReplaceStep(
                    info, prevGNode, from, eNode, assocTr);

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

        final GenProofStepStmt replTrStep = replaceMe.transformMeToTarget(
            replaceTarget, info);

        if (replTrStep != null)
            resStmt = eqInfo.getTransitiveStep(info, resStmt, replTrStep);

        return resStmt;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final CanonicalOperandHelper helper = new CanonicalOperandHelper(
            originalNode, genStmt);
        helper.collectOperandList(structure);
        helper.convertOperandsToCanonical(trManager, info);
        return helper.constructCanonical();
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + structure;
    }
}
