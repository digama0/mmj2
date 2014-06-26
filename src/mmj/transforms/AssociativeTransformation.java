package mmj.transforms;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

/** Only associative transformations */
class AssociativeTransformation extends Transformation {
    final AssocTree structure;
    final GeneralizedStmt assocProp;

    private final ReplaceInfo replInfo;
    private final AssociativeInfo assocInfo;
    public final ClosureInfo clInfo;

    public AssociativeTransformation(final TransformationManager trManager,
        final ParseNode originalNode, final AssocTree structure,
        final GeneralizedStmt assocProp)
    {
        super(trManager, originalNode);
        this.structure = structure;
        this.assocProp = assocProp;

        replInfo = trManager.replInfo;
        assocInfo = trManager.assocInfo;
        clInfo = trManager.clInfo;
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
                final ProofStepStmt assocTr = createAssociativeStep(info, from,
                    prevNode, gNode);

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
                final ProofStepStmt assocTr = createAssociativeStep(info, to,
                    prevENode, eNode);

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
        final Transformation replaceTarget = new ReplaceTransformation(trManager,
            target.originalNode);

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
                return TrUtil.createBinaryNode(assocProp, left, leaf);
        }

        for (int i = 0; i < 2; i++)
            left = constructCanonicalForm(left, cur.getChild()[i], info);

        return left;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        return constructCanonicalForm(null, originalNode, info);
    }

    private ProofStepStmt createAssociativeStep(final WorksheetInfo info,
        final int from, final ParseNode prevNode, final ParseNode newNode)
    {
        final Assrt[] assocTr = assocInfo.getAssocOp(assocProp);
        assert assocTr != null;

        final boolean revert;
        final Assrt assocAssrt;
        final boolean firstForm;
        final ParseNode left;
        final ParseNode right;
        if (assocTr[from] != null) {
            assocAssrt = assocTr[from];
            revert = false;
            firstForm = true;
            left = prevNode;
            right = newNode;
        }
        else {
            final int other = (from + 1) % 2;
            assocAssrt = assocTr[other];
            revert = true;
            firstForm = false;
            left = newNode;
            right = prevNode;
        }
        assert assocAssrt != null;

        final Stmt equalStmt = assocAssrt.getExprParseTree().getRoot()
            .getStmt();

        // Create node f(f(a, b), c) = f(a, f(b, c))
        final ParseNode stepNode = TrUtil.createBinaryNode(equalStmt, left,
            right);

        ProofStepStmt res = closurePropertyAssociative(info, assocProp,
            assocAssrt, firstForm, stepNode);

        if (revert)
            res = eqInfo.createReverse(info, res);

        return res;
    }
    private ProofStepStmt closureProperty(final WorksheetInfo info,
        final GeneralizedStmt assocProp, final ParseNode node)
    {
        // PropertyTemplate template = assocProp.template;
        final Assrt assrt = clInfo.getClosureAssert(assocProp);

        assert assrt != null;

        final ParseNode stepNode = assocProp.template.subst(node);

        ProofStepStmt res = info.getProofStepStmt(stepNode);
        if (res != null)
            return res;

        assert assocProp.varIndexes.length == assrt.getLogHypArrayLength();
        final ProofStepStmt[] hyps = new ProofStepStmt[assocProp.varIndexes.length];
        for (final int n : assocProp.varIndexes) {
            final ParseNode child = node.getChild()[n];

            hyps[n] = closureProperty(info, assocProp, child);
        }
        res = info.getOrCreateProofStepStmt(stepNode, hyps, assrt);
        res.toString();

        return res;
    }

    // First form: f(f(a, b), c) = f(a, f(b, c))
    // Second form: f(a, f(b, c)) = f(f(a, b), c)
    private ProofStepStmt closurePropertyAssociative(final WorksheetInfo info,
        final GeneralizedStmt assocProp, final Assrt assocAssrt,
        final boolean firstForm, final ParseNode stepNode)
    {
        final ProofStepStmt[] hyps;
        if (!assocProp.template.isEmpty()) {

            hyps = new ProofStepStmt[3];
            final int n0 = assocProp.varIndexes[0];
            final int n1 = assocProp.varIndexes[1];
            final ParseNode side;
            if (firstForm)
                side = stepNode.getChild()[0];
            else
                side = stepNode.getChild()[1];
            final ParseNode[] in = new ParseNode[3];
            in[0] = side.getChild()[n0].getChild()[n0];
            in[1] = side.getChild()[n0].getChild()[n1];
            in[2] = side.getChild()[n1];

            for (int i = 0; i < 3; i++)
                hyps[i] = closureProperty(info, assocProp, in[i]);
        }
        else
            hyps = new ProofStepStmt[]{};

        final ProofStepStmt res = info.getOrCreateProofStepStmt(stepNode, hyps,
            assocAssrt);

        return res;
    }

    @Override
    public String toString() {
        return super.toString() + "\n" + structure;
    }
}
