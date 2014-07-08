package mmj.transforms;

import java.util.*;

import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/** Associative and commutative transformations */
public class AssocComTransformation extends Transformation {
    private final AssocTree structure;
    private final GeneralizedStmt genStmt;

    public final CommutativeInfo comInfo;
    private final AssociativeInfo assocInfo;
    private final ReplaceInfo replInfo;

    public AssocComTransformation(final TransformationManager trManager,
        final ParseNode originalNode, final AssocTree structure,
        final GeneralizedStmt genStmt)
    {
        super(trManager, originalNode);
        this.genStmt = genStmt;
        this.structure = structure;

        comInfo = trManager.comInfo;
        assocInfo = trManager.assocInfo;
        replInfo = trManager.replInfo;
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        final CanonicalOperandHelper helper = new CanonicalOperandHelper(
            originalNode, genStmt);

        helper.collectOperandList(structure);
        helper.convertOperandsToCanonical(trManager, info);

        Collections.sort(helper.operandList, new Comparator<ParseNode>() {
            public int compare(final ParseNode o1, final ParseNode o2) {
                return CommutativeInfo.compareNodes(o1, o2);
            }
        });

        final ParseNode res = helper.constructCanonical();
        return res;
    }

    private static class LeafIndex {
        private final int num;

        public LeafIndex(final int num) {
            super();
            this.num = num;
        }
    }

    /** Associative-commutative tree */
    private static class ACTree {
        private ParseNode node;
        private boolean validParseNode = true;
        private ACTree parent;
        private final ACTree[] children;
        private final LeafIndex[] leafs;
        private int height;

        // Not leaf constructor
        public ACTree(final ParseNode node, final int from, final ACTree left,
            final ACTree right)
        {
            super();
            this.node = node;
            children = from == 0 ? new ACTree[]{left, right} : new ACTree[]{
                    right, left};
            children[0].parent = this;
            children[1].parent = this;

            leafs = new LeafIndex[children[0].leafs.length
                + children[1].leafs.length];

            int k = 0;
            for (int n = 0; n < 2; n++)
                for (int i = 0; i < children[n].leafs.length; i++)
                    leafs[k++] = children[n].leafs[i];
            Arrays.sort(leafs, new Comparator<LeafIndex>() {
                public int compare(final LeafIndex o1, final LeafIndex o2) {
                    return Integer.compare(o1.num, o2.num);
                }
            });

            updateHeight();
        }

        // Leaf constructor
        public ACTree(final ParseNode node, final LeafIndex index) {
            super();
            this.node = node;
            children = null;
            parent = null;
            height = 1;
            leafs = new LeafIndex[]{index};
        }

        public void changeChild(final ACTree prevChild, final ACTree newChild) {
            validParseNode = false;
            assert prevChild.parent == this;
            children[prevChild.getParentChildNum()] = newChild;
            updateHeight();
        }

        public void swapChildren() {
            validParseNode = false;
            final ACTree x = children[0];
            children[0] = children[1];
            children[1] = x;
        }

        public void replaceNode(final ParseNode newNode) {
            assert validParseNode == false : "We should replace something "
                + "when we already know that this node is invalid!";
            node = newNode;
            validParseNode = true;
            if (parent != null)
                parent.validParseNode = false;
        }

        public void updateHeight() {
            height = Math.max(children[0].height, children[1].height);
            if (parent != null)
                parent.updateHeight();
        }

        public int leafsLength() {
            return leafs.length;
        }

        public LeafIndex leafIndex() {
            assert leafs.length == 0;
            return leafs[0];
        }

        public int getParentChildNum() {
            assert !isRoot();
            if (parent.children[0] == this)
                return 0;
            assert parent.children[1] == this;
            return 1;
        }

        public ACTree getBrother() {
            return parent.children[(getParentChildNum() + 1) % 2];
        }

        public boolean isSideChild() {
            assert hasGrandParant();
            return getParentChildNum() == parent.getParentChildNum();
        }

        public boolean isTopLvlGrandChild() {
            return parent != null && parent.parent != null
                && parent.parent.parent == null;
        }

        public boolean hasGrandParant() {
            return parent != null && parent.parent != null;
        }

        public boolean isRoot() {
            return parent == null;
        }

        public ACTree getRoot() {
            if (parent != null)
                return parent.getRoot();
            else
                return this;
        }

        public boolean checkValidity(final GeneralizedStmt genStmt) {
            if (!validParseNode)
                return false;

            if (children == null)
                return true;

            for (int i = 0; i < 2; i++)
                if (children[i].node != node.getChild()[genStmt.varIndexes[i]])
                    return false;

            return true;
        }
    }

    private ProofStepStmt performAssociativeTransformation(final ACTree what,
        final WorksheetInfo info)
    {
        // @formatter:off
        // 'what' - it is 'a' node or 'd' node
        //         |g                  |r                +
        //        / \                 / \                +
        //       /   \               /   \               +
        //     e|     |f           a|     |t             +
        //     / \          ==>          / \             +
        //    /   \                     /   \            +
        //  a|     |d                 d|     |f          +
        // @formatter:on
        final boolean isSideWhat = what.isSideChild();

        final ACTree aTree = isSideWhat ? what : what.getBrother();
        final ACTree dTree = isSideWhat ? what.getBrother() : what;
        final ACTree eTree = what.parent;
        final ACTree fTree = eTree.getBrother();
        final ACTree gTree = eTree.parent;

        final ACTree gParent = gTree.parent;

        final ParseNode gNode = gTree.node;
        final ParseNode dNode = dTree.node;
        final ParseNode aNode = aTree.node;
        final ParseNode fNode = fTree.node;

        final int from = dTree.getParentChildNum();

        final ParseNode tNode = TrUtil.createAssocBinaryNode(from, genStmt,
            dNode, fNode);
        final ParseNode rNode = TrUtil.createAssocBinaryNode(from, genStmt,
            aNode, tNode);

        final ACTree tTree = new ACTree(tNode, from, dTree, fTree);
        final ACTree rTree = new ACTree(tNode, from, aTree, tTree);
        if (gParent != null)
            gParent.changeChild(gTree, rTree);

        final ProofStepStmt assocStep = assocInfo.createAssociativeStep(info,
            genStmt, from, gNode, rNode);
        return assocStep;
    }

    private ProofStepStmt createCommutativeStep(final ACTree what,
        final WorksheetInfo info)
    {
        // @formatter:off
        // Swap 'd = what' and 'a' nodes: 
        //     e|                    e'|                 +
        //     / \          ==>       / \                +
        //    /   \                  /   \               +
        //  a|     |d              a|     |d             +
        // @formatter:on

        final ACTree dTree = what;
        final ACTree aTree = dTree.getBrother();
        final ACTree eTree = dTree.parent;

        final int from = dTree.getParentChildNum();

        final ParseNode dNode = dTree.node;
        final ParseNode aNode = aTree.node;
        final ParseNode eNode = eTree.node;
        final ParseNode newENode = TrUtil.createAssocBinaryNode(from, genStmt,
            aNode, dNode);

        eTree.swapChildren();

        final ProofStepStmt comStep = comInfo.createCommutativeStep(info,
            genStmt, eNode, newENode);

        eTree.replaceNode(newENode);

        return comStep;
    }

    private ProofStepStmt performCommutativeTransformation(final ACTree what,
        final WorksheetInfo info)
    {
        // @formatter:off
        // Swap 'd = what' and 'a' nodes: 
        //      |g                     |g'               +
        //      |                      |                 +
        //      |                      |                 +
        //     e|                    e'|                 +
        //     / \          ==>       / \                +
        //    /   \                  /   \               +
        //  a|     |d              a|     |d             +
        // @formatter:on

        final ACTree dTree = what;
        final ACTree eTree = dTree.parent;
        final ACTree gTree = eTree.parent;

        final ParseNode gNode = gTree.node;

        final ProofStepStmt comStep = createCommutativeStep(what, info);

        final ParseNode newENode = getRightPart(comStep);

        final ProofStepStmt replStep = replInfo.createReplaceStep(info, gNode,
            genStmt.varIndexes[eTree.getParentChildNum()], newENode, comStep);

        final ParseNode newGNode = getRightPart(replStep);
        gTree.replaceNode(newGNode);

        return replStep;
    }

    private static ParseNode getRightPart(final ProofStepStmt step) {
        assert step.formulaParseTree.getRoot().getChild().length == 2;
        return step.formulaParseTree.getRoot().getChild()[1];
    }

    private ProofStepStmt move(ACTree what, final WorksheetInfo info) {
        assert what != null;
        assert what.parent != null;
        assert what.parent.parent != null;

        ProofStepStmt result = null;
        while (true) {
            assert what.validParseNode;
            assert what.parent.validParseNode;
            assert what.parent.parent.validParseNode;

            if (what.isTopLvlGrandChild()) {
                if (what.isSideChild()) {
                    // @formatter:off
                    // Swap 'd = what' and 'a' nodes
                    //         |g                     |g'            +
                    //        / \                    / \             +
                    //       /   \                  /   \            +
                    //     e|     |f             e'|     |f          +
                    //     / \          ==>       / \                +
                    //    /   \                  /   \               +
                    //  d|     |a              a|     |d             +
                    // @formatter:on
                    final ProofStepStmt replStep = performCommutativeTransformation(
                        what, info);
                    result = eqInfo.getTransitiveStep(info, result, replStep);
                }

                // @formatter:off
                // Move 'd = what' node to the other side
                //         |g                  |r                +
                //        / \                 / \                +
                //       /   \               /   \               +
                //     e|     |f           a|     |t             +
                //     / \          ==>          / \             +
                //    /   \                     /   \            +
                //  a|     |d                 d|     |f          +
                // @formatter:on
                final ProofStepStmt assocStep = performAssociativeTransformation(
                    what, info);

                result = eqInfo.getTransitiveStep(info, result, assocStep);
                return result;
            }
            else {
                final ACTree gTree = what.parent.parent;
                final ACTree pTree = what.parent.parent.parent;
                final ParseNode pNode = what.parent.parent.parent.node;
                if (!what.isSideChild()) {
                    // @formatter:off
                    // Level up the equivalence
                    //         |g                     |g'            +
                    //        / \                    / \             +
                    //       /   \                  /   \            +
                    //     e|     |f             e'|     |f          +
                    //     / \          ==>       / \                +
                    //    /   \                  /   \               +
                    //  a|     |d              d|     |a             +
                    // @formatter:on
                    final ProofStepStmt replStep = performCommutativeTransformation(
                        what, info);
                    result = eqInfo.getTransitiveStep(info, result, replStep);
                }

                // @formatter:off
                // Up 'd = what' node
                //         |p                  |p'               +
                //         |                   |                 +
                //         |g                  |r                +
                //        / \                 / \                +
                //       /   \               /   \               +
                //     e|     |f           d|     |t             +
                //     / \          ==>          / \             +
                //    /   \                     /   \            +
                //  d|     |a                 a|     |f          +
                // @formatter:on

                ProofStepStmt assocStep = performAssociativeTransformation(
                    what, info);

                assocStep = eqInfo.getTransitiveStep(info, result, assocStep);

                assert !pTree.validParseNode;

                final ParseNode newGNode = getRightPart(assocStep);
                final ProofStepStmt replGNode = replInfo.createReplaceStep(
                    info, pNode, genStmt.varIndexes[gTree.getParentChildNum()],
                    newGNode, assocStep);

                final ParseNode newPNode = getRightPart(replGNode);
                pTree.replaceNode(newPNode);

                what = what.parent;
                result = replGNode;
            }
        }
    }

    private ACTree[] getLvl(final ACTree input, final int height) {
        ACTree[] curLvl = new ACTree[]{input};
        int curHeight = input.height;

        while (curHeight > height) {
            curHeight--;
            final ArrayList<ACTree> lst = new ArrayList<ACTree>();

            for (final ACTree tree : curLvl)
                for (final ACTree child : tree.children)
                    if (child.height == curHeight)
                        lst.add(child);

            curLvl = (ACTree[])lst.toArray();
        }
        return curLvl;
    }

    private int commonElements(final ACTree t1, final ACTree t2) {
        int i1 = 0;
        int i2 = 0;

        int common = 0;

        while (true) {
            while (t1.leafs[i1].num < t2.leafs[i2].num) {
                i1++;
                if (i1 >= t1.leafs.length)
                    return common;
            }
            while (t2.leafs[i2].num < t1.leafs[i1].num) {
                i2++;
                if (i2 >= t2.leafs.length)
                    return common;
            }

            while (t1.leafs[i1].num == t2.leafs[i2].num) {
                common++;
                i1++;
                if (i1 >= t1.leafs.length)
                    return common;
                i2++;
                if (i2 >= t2.leafs.length)
                    return common;
            }
        }
    }

    private static class CanonicalInfo {
        ParseNode node;
        ParseNode canonical;
        LeafIndex index;

        CanonicalInfo next; // for auxiliary information

        public CanonicalInfo(final ParseNode node, final ParseNode canonical) {
            super();
            this.node = node;
            this.canonical = canonical;
        }
    }

    private void constructCanonicalInfoList(final WorksheetInfo info,
        final AssocTree assocTree, final ParseNode curNode,
        final List<CanonicalInfo> resList)
    {
        final CanonicalInfo last = resList.isEmpty() ? null : resList
            .get(resList.size() - 1);

        if (assocTree.size == 1) {
            final ParseNode canonical = trManager.getCanonicalForm(curNode,
                info);
            final CanonicalInfo add = new CanonicalInfo(curNode, canonical);
            if (last != null)
                last.next = add;
            resList.add(add);
        }
        else
            for (int i = 0; i < 2; i++) {
                final ParseNode child = curNode.getChild()[genStmt.varIndexes[i]];
                constructCanonicalInfoList(info, assocTree.subTrees[i], child,
                    resList);
            }
    }

    private class TreeConstructor {
        CanonicalInfo curInfo;

        public TreeConstructor(final CanonicalInfo curInfo) {
            this.curInfo = curInfo;
        }

        public ACTree constructTree(final AssocTree assocTree,
            final ParseNode curNode)
        {
            if (assocTree.size == 1) {
                assert curInfo.node == curNode;
                final ACTree res = new ACTree(curNode, curInfo.index);
                curInfo = curInfo.next;
                return res;
            }
            final ACTree left = constructTree(assocTree.subTrees[0],
                curNode.getChild()[genStmt.varIndexes[0]]);
            final ACTree right = constructTree(assocTree.subTrees[1],
                curNode.getChild()[genStmt.varIndexes[1]]);

            return new ACTree(curNode, 0, left, right);
        }
    }

    private ACTree constructTree(final WorksheetInfo info,
        final ParseNode topNode, final AssocTree assocTree)
    {

        final List<CanonicalInfo> resList = new ArrayList<CanonicalInfo>(
            assocTree.size);
        constructCanonicalInfoList(info, assocTree, topNode, resList);

        final CanonicalInfo first = resList.get(0);

        Collections.sort(resList, new Comparator<CanonicalInfo>() {
            public int compare(final CanonicalInfo o1, final CanonicalInfo o2) {
                return CommutativeInfo.compareNodes(o1.canonical, o2.canonical);
            }
        });

        int curIdx = 0;
        for (int i = 0; i < resList.size(); i++) {
            final CanonicalInfo cur = resList.get(i);
            final CanonicalInfo next = i < resList.size() - 1 ? resList
                .get(i + 1) : null;

            cur.index = new LeafIndex(curIdx);

            if (next != null && !cur.canonical.isDeepDup(next.canonical))
                curIdx++;
        }

        return new TreeConstructor(first).constructTree(assocTree, topNode);
    }

    private ProofStepStmt performSimpleTransformation(final WorksheetInfo info,
        final ACTree myTree, final ACTree tgtTree)
    {
        ProofStepStmt result = null;

        if (tgtTree.leafsLength() == 2)
            if (myTree.children[0].leafIndex() != tgtTree.children[0]
                .leafIndex())
                result = createCommutativeStep(myTree.children[0], info);

        final Transformation replaceMe = new ReplaceTransformation(trManager,
            myTree.node);
        final Transformation replaceTarget = new ReplaceTransformation(
            trManager, tgtTree.node);

        final ProofStepStmt replTrStep = replaceMe.transformMeToTarget(
            replaceTarget, info);

        if (replTrStep != null)
            result = eqInfo.getTransitiveStep(info, result, replTrStep);

        return result;
    }

    private ProofStepStmt performTreeTransformation(final WorksheetInfo info,
        ACTree myTree, final ACTree tgtTree)
    {
        // The number of leafs should be equal
        assert tgtTree.leafsLength() == myTree.leafsLength();
        // And the leafs themselves also should be equal
        assert commonElements(myTree, tgtTree) == myTree.leafsLength();

        // end of recursion:
        if (tgtTree.leafsLength() < 3)
            return performSimpleTransformation(info, myTree, tgtTree);

        // Choose the biggest target side first!
        // So we will move nodes from big side to the small side
        final int startSide;
        if (tgtTree.children[0].leafsLength() >= tgtTree.children[1]
            .leafsLength())
            startSide = 0;
        else
            startSide = 1;

        ProofStepStmt result = null;
        for (int sideI = 0; sideI < 2; sideI++) {
            final int side = (startSide + sideI) % 2;
            final ACTree tgtSide = tgtTree.children[side];
            ACTree mySide = myTree.children[side];

            int nElems = commonElements(mySide, tgtSide);

            // +1 because of integer arithmetic properties
            if (nElems < (tgtSide.leafsLength() + 1) / 2) {
                final ProofStepStmt swap = createCommutativeStep(mySide, info);
                myTree = mySide.getRoot();
                mySide = mySide.getBrother();

                result = eqInfo.getTransitiveStep(info, result, swap);

                nElems = commonElements(mySide, tgtSide);
            }

            if (nElems == mySide.leafsLength())
                continue;

            // The list of leafs which we should move to the other side
            // Initialize it as all leafs and then remove inappropriate
            final ACTree[] sideLeafs = getLvl(mySide, 1);

            // We could do the search more efficiently!
            // Calculate which leafs we should move to the other side!
            for (final LeafIndex leafIdx : tgtSide.leafs)
                for (int i = 0; i < sideLeafs.length; i++)
                    if (sideLeafs[i] != null)
                        if (leafIdx.num == sideLeafs[i].leafIndex().num) {
                            sideLeafs[i] = null;
                            break;
                        }

            // Move leafs to the other side!
            for (final ACTree leaf : sideLeafs)
                if (leaf != null) {
                    // The result statement says that the previous 'tree' is
                    // equal to the current 'tree':
                    final ProofStepStmt stmt = move(leaf, info);
                    result = eqInfo.getTransitiveStep(info, result, stmt);

                    myTree = leaf.getRoot();
                    assert myTree.checkValidity(genStmt);
                }
        }

        ParseNode node = myTree.node;
        for (int i = 0; i < 2; i++) {
            myTree.children[i].parent = null;
            tgtTree.children[i].parent = null;
            final ProofStepStmt childResult = performTreeTransformation(info,
                myTree.children[i], tgtTree.children[i]);

            if (childResult == null)
                continue; // all nodes already at the right place!

            final ParseNode newChild = getRightPart(childResult);
            final ProofStepStmt replChildStep = replInfo.createReplaceStep(
                info, node, genStmt.varIndexes[i], newChild, childResult);
            node = getRightPart(replChildStep);
            result = eqInfo.getTransitiveStep(info, result, replChildStep);
        }
        return result;
    }

    @Override
    public ProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target instanceof AssocComTransformation;
        final AssocComTransformation tgt = (AssocComTransformation)target;

        final ACTree myTree = constructTree(info, originalNode, structure);
        final ACTree tgtTree = constructTree(info, tgt.originalNode,
            tgt.structure);
        assert myTree.leafs != null;
        assert myTree.leafsLength() == structure.size;

        return performTreeTransformation(info, myTree, tgtTree);
    }

}
