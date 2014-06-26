//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.*;

/**
 * This contains information for possible automatic transformations.
 * <p>
 * Canonical form for the parse node is a parse node with sorted
 * commutative/associative transformations.
 * <p>
 * The functions in this class are separated into 4 parts:
 * <ul>
 * <li>Data structures initialization
 * <li>Transformations
 * <li>Canonical form comparison
 * <li>Auxiliary functions
 * </ul>
 */
public class ProofTransformations extends DataBaseInfo {

    public ProofTransformations() {}

    private final boolean dbg = true;

    /**
     * The transformation of {@link Transformation#originalNode} to any equal
     * node.
     */
    private abstract class Transformation {
        /** The original node */
        public final ParseNode originalNode;

        public Transformation(final ParseNode originalNode) {
            this.originalNode = originalNode;
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
         *         {@link Transformation#originalNode}. Could returns null it
         *         this and target are equal.
         */
        public abstract ProofStepStmt transformMeToTarget(
            final Transformation target, final WorksheetInfo info);

        /**
         * This function checks maybe we should not do anything! We should
         * perform transformations if this function returns derivStep.
         * 
         * @param target the target transformation
         * @param info the information about work sheet
         * @return null (if target equals to this), statement (if it is already
         *         exists) or info.derivStep if we should perform some
         *         transformations!
         */
        protected ProofStepStmt checkTransformationNecessary(
            final Transformation target, final WorksheetInfo info)
        {
            if (originalNode.isDeepDup(target.originalNode))
                return null; // nothing to transform!

            // Create node g(A, B, C) = g(A', B', C')
            final ParseNode stepNode = eqInfo.createEqNode(originalNode,
                target.originalNode);

            final ProofStepStmt stepTr = info.getProofStepStmt(stepNode);

            if (stepTr != null)
                return stepTr;

            return info.derivStep; // there is more complex case!
        }

        // use it only for debug!
        @Override
        public String toString() {
            final Formula f = getFormula(originalNode);
            return f.toString();
        }
    }

    /** No transformation at all =) */
    private class IdentityTransformation extends Transformation {

        public IdentityTransformation(final ParseNode originalNode) {
            super(originalNode);
        }

        @Override
        public ParseNode getCanonicalNode(final WorksheetInfo info) {
            return originalNode;
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final WorksheetInfo info)
        {
            assert target.originalNode.isDeepDup(originalNode);
            return null; // nothing to do
        }
    }

    /**
     * The replace transformation: we could transform children of corresponding
     * node and replace them with its canonical form (or vice versa).
     */
    private class ReplaceTransformation extends Transformation {
        public ReplaceTransformation(final ParseNode originalNode) {
            super(originalNode);
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final WorksheetInfo info)
        {
            assert target instanceof ReplaceTransformation;
            final ReplaceTransformation trgt = (ReplaceTransformation)target;

            final ProofStepStmt simpleRes = checkTransformationNecessary(
                target, info);
            if (simpleRes != info.derivStep)
                return simpleRes;

            // result transformation statement
            ProofStepStmt resStmt = null;

            // the current transformation result
            ParseNode resNode = originalNode;

            final Assrt[] replAsserts = replInfo.getReplaceAsserts(originalNode
                .getStmt());

            for (int i = 0; i < originalNode.getChild().length; i++) {
                if (replAsserts[i] == null)
                    continue;
                // We replaced previous children and now we should transform ith
                // child B

                final ParseNode child = originalNode.getChild()[i];
                final ParseNode targetChild = target.originalNode.getChild()[i];

                assert replAsserts[i].getLogHypArrayLength() == 1;

                // get the root symbol from g(A', B, C) = g(A', B', C)
                // in set.mm it will be = or <->
                final Stmt equalStmt = replAsserts[i].getExprParseTree()
                    .getRoot().getStmt();

                // the symbol should be equivalence operator
                assert eqInfo.isEquivalence(equalStmt);

                // transform ith child
                // the result should be some B = B' statement
                final ProofStepStmt childTrStmt = createTransformation(child,
                    info).transformMeToTarget(
                    createTransformation(targetChild, info), info);
                if (childTrStmt == null)
                    continue; // noting to do

                // get the node B = B'
                final ParseNode childTrRoot = childTrStmt.formulaParseTree
                    .getRoot();

                // transformation should be transformation:
                // check result childTrStmt statement
                assert eqInfo.isEquivalence(childTrRoot.getStmt());
                assert childTrRoot.getChild().length == 2;
                assert childTrRoot.getChild()[0].isDeepDup(originalNode
                    .getChild()[i]);
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
            final Assrt[] replAsserts = replInfo.getReplaceAsserts(originalNode
                .getStmt());
            final ParseNode resNode = originalNode.cloneWithoutChildren();

            for (int i = 0; i < resNode.getChild().length; i++) {
                if (replAsserts[i] == null)
                    continue;
                resNode.getChild()[i] = getCanonicalForm(
                    originalNode.getChild()[i], info);
            }

            return resNode;
        }
    }

    private static class AssocTree {
        final int size;
        final AssocTree[] subTrees;

        AssocTree() {
            size = 1;
            subTrees = null;
        }

        AssocTree(final AssocTree left, final AssocTree right) {
            subTrees = new AssocTree[]{left, right};
            size = left.size + right.size;
        }

        AssocTree(final AssocTree[] subTrees) {
            this.subTrees = subTrees;
            assert subTrees.length == 2;
            size = subTrees[0].size + subTrees[1].size;
        }

        AssocTree(final int from, final AssocTree left, final AssocTree right) {
            assert from == 0 || from == 1;
            subTrees = from == 0 ? new AssocTree[]{left, right}
                : new AssocTree[]{right, left};
            size = left.size + right.size;
        }

        AssocTree duplicate() {
            if (subTrees != null)
                return new AssocTree(subTrees[0].duplicate(),
                    subTrees[1].duplicate());
            else
                return new AssocTree();
        }

        @Override
        public String toString() {
            String res = "";
            res += size;

            if (subTrees != null)
                res += "[" + subTrees[0].toString() + ","
                    + subTrees[1].toString() + "]";

            return res;
        }
    }

    private AssocTree createAssocTree(final ParseNode curNode,
        final GeneralizedStmt assocProp, final WorksheetInfo info)
    {
        final Stmt stmt = curNode.getStmt();
        final ParseNode[] childrent = curNode.getChild();

        final Stmt assocStmt = assocProp.stmt;

        if (stmt != assocStmt)
            return new AssocTree();

        final AssocTree[] subTrees = new AssocTree[2];

        for (int i = 0; i < 2; i++) {
            final ParseNode child = childrent[assocProp.varIndexes[i]];
            if (isAssociativeWithProp(child, assocProp, info))
                subTrees[i] = createAssocTree(child, assocProp, info);
            else
                subTrees[i] = new AssocTree();
        }

        return new AssocTree(subTrees);
    }

    /** Only associative transformations */
    private class AssociativeTransformation extends Transformation {
        final AssocTree structure;
        final GeneralizedStmt assocProp;

        public AssociativeTransformation(final ParseNode originalNode,
            final AssocTree structure, final GeneralizedStmt assocProp)
        {
            super(originalNode);
            this.structure = structure;
            this.assocProp = assocProp;
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final WorksheetInfo info)
        {
            assert target instanceof AssociativeTransformation;
            final AssociativeTransformation trgt = (AssociativeTransformation)target;

            assert trgt.structure.size == structure.size;

            final ProofStepStmt simpleRes = checkTransformationNecessary(
                target, info);
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

                    gAssT = new AssocTree(from, aAssT, new AssocTree(from,
                        dAssT, fAssT));

                    final ParseNode eNode = gNode.getChild()[from];
                    final ParseNode fNode = gNode.getChild()[to];
                    final ParseNode aNode = eNode.getChild()[from];
                    final ParseNode dNode = eNode.getChild()[to];

                    final ParseNode prevNode = gNode;

                    gNode = createAssocBinaryNode(from, assocProp, aNode,
                        createAssocBinaryNode(from, assocProp, dNode, fNode));

                    // transform to normal direction => 'from'
                    final ProofStepStmt assocTr = createAssociativeStep(info,
                        assocProp, from, prevNode, gNode);

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

                    gAssT.subTrees[from] = new AssocTree(from, new AssocTree(
                        from, aAssT, bAssT), cAssT);

                    /*--*/ParseNode eNode = gNode.getChild()[from];
                    final ParseNode fNode = gNode.getChild()[to];
                    final ParseNode aNode = eNode.getChild()[from];
                    final ParseNode dNode = eNode.getChild()[to];
                    final ParseNode bNode = dNode.getChild()[from];
                    final ParseNode cNode = dNode.getChild()[to];

                    final ParseNode prevENode = eNode;
                    eNode = createAssocBinaryNode(from, assocProp,
                        createAssocBinaryNode(from, assocProp, aNode, bNode),
                        cNode);

                    // transform to other direction => 'to'
                    final ProofStepStmt assocTr = createAssociativeStep(info,
                        assocProp, to, prevENode, eNode);

                    final ParseNode prevGNode = gNode;
                    gNode = createAssocBinaryNode(from, assocProp, eNode, fNode);

                    final ProofStepStmt replTr = replInfo.createReplaceStep(
                        info, prevGNode, from, eNode, assocTr);

                    resStmt = eqInfo.getTransitiveStep(info, resStmt, replTr);
                }
                else
                    break;
            }

            assert gAssT.subTrees[0].size == trgt.structure.subTrees[0].size;
            assert gAssT.subTrees[1].size == trgt.structure.subTrees[1].size;

            final Transformation replaceMe = new ReplaceTransformation(gNode);
            final Transformation replaceTarget = new ReplaceTransformation(
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
                final ParseNode leaf = getCanonicalForm(cur, info);
                if (left == null)
                    return leaf;
                else
                    return createBinaryNode(assocProp, left, leaf);
            }

            for (int i = 0; i < 2; i++)
                left = constructCanonicalForm(left, cur.getChild()[i], info);

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

    private ProofStepStmt createAssociativeStep(final WorksheetInfo info,
        final GeneralizedStmt assocProp, final int from,
        final ParseNode prevNode, final ParseNode newNode)
    {
        final Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap = assocOp
            .get(prevNode.getStmt());

        assert constSubstMap != null;

        final Map<PropertyTemplate, Assrt[]> propertyMap = constSubstMap
            .get(assocProp.constSubst);

        assert propertyMap != null;

        final Assrt[] assocTr = propertyMap.get(assocProp.template);
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

    private static ParseNode createAssocBinaryNode(final int from,
        final GeneralizedStmt assocProp, final ParseNode left,
        final ParseNode right)
    {
        if (from == 0)
            return createBinaryNode(assocProp, left, right);
        else
            return createBinaryNode(assocProp, right, left);
    }

    private static ParseNode createBinaryNode(final GeneralizedStmt genStmt,
        final ParseNode left, final ParseNode right)
    {
        final ParseNode eqRoot = new ParseNode(genStmt.stmt);
        final int len = genStmt.constSubst.constMap.length;
        final ParseNode[] vars = {left, right};
        final ParseNode[] children = new ParseNode[len];
        for (int i = 0; i < len; i++) {
            final ParseNode c = genStmt.constSubst.constMap[i];
            if (c != null)
                children[i] = c.deepClone();
        }
        for (int i = 0; i < 2; i++)
            children[genStmt.varIndexes[i]] = vars[i];

        eqRoot.setChild(children);
        return eqRoot;
    }

    private ParseNode[] collectConstSubst(final ParseNode originalNode) {
        final ParseNode[] constMap = new ParseNode[originalNode.getChild().length];

        for (int i = 0; i < constMap.length; i++) {
            final ParseNode child = originalNode.getChild()[i];
            if (TrUtil.isConstNode(child))
                constMap[i] = child;
        }

        return constMap;
    }

    private boolean isAssociativeWithProp(final ParseNode originalNode,
        final GeneralizedStmt assocProp, final WorksheetInfo info)
    {
        if (assocProp.stmt != originalNode.getStmt())
            return false;
        return isAssociativeWithProp(originalNode, assocProp.template,
            assocProp.constSubst, info);
    }

    private boolean isAssociativeWithProp(final ParseNode originalNode,
        final PropertyTemplate template, final ConstSubst constSubst,
        final WorksheetInfo info)
    {
        if (originalNode.getChild().length != constSubst.constMap.length)
            assert originalNode.getChild().length == constSubst.constMap.length;

        if (template.isEmpty())
            return true;

        int varNum = 0;
        for (int i = 0; i < constSubst.constMap.length; i++) {
            final ParseNode child = originalNode.getChild()[i];
            if (constSubst.constMap[i] == null) { // variable here
                varNum++;
                final ParseNode substProp = template.subst(child);
                final ProofStepStmt stmt = info.getProofStepStmt(substProp);
                if (stmt == null)
                    if (child.getStmt() != originalNode.getStmt()
                        || !isAssociativeWithProp(child, template, constSubst,
                            info))
                        return false;
            }
            else if (!constSubst.constMap[i].isDeepDup(child)) // check constant
                return false;
        }

        assert varNum == 2;
        return true;
    }

    private GeneralizedStmt isAssociative(final ParseNode originalNode,
        final Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap,
        final WorksheetInfo info)
    {
        final Stmt stmt = originalNode.getStmt();
        final ParseNode[] constMap = collectConstSubst(originalNode);

        final int[] varIndexes = new int[2];

        for (final Entry<ConstSubst, Map<PropertyTemplate, Assrt[]>> elem : constSubstMap
            .entrySet())
        {
            final ConstSubst constSubst = elem.getKey();
            int curVar = 0;

            boolean ok = true;
            for (int i = 0; i < constSubst.constMap.length; i++)
                if (constSubst.constMap[i] != null) {
                    if (constMap[i] == null
                        || !constSubst.constMap[i].isDeepDup(constMap[i]))
                    {
                        ok = false;
                        break;
                    }
                }
                else {
                    assert curVar < 2;
                    varIndexes[curVar++] = i;
                }

            if (!ok)
                continue;

            final Map<PropertyTemplate, Assrt[]> propertyMap = elem.getValue();

            for (final Entry<PropertyTemplate, Assrt[]> propElem : propertyMap
                .entrySet())
            {
                final PropertyTemplate template = propElem.getKey();
                final boolean res = isAssociativeWithProp(originalNode,
                    template, constSubst, info);
                if (res)
                    return new GeneralizedStmt(constSubst, template,
                        varIndexes, stmt);
            }
        }

        return null;
    }

    /**
     * The main function to create transformation.
     * 
     * @param originalNode the source node
     * @param info the information about previous steps
     * @return the transformation
     */
    private Transformation createTransformation(final ParseNode originalNode,
        final WorksheetInfo info)
    {
        final Stmt stmt = originalNode.getStmt();

        final Assrt[] replAsserts = replInfo.getReplaceAsserts(stmt);

        final Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap = assocOp
            .get(stmt);

        boolean isAssoc = false;
        GeneralizedStmt assocProp = null;
        if (constSubstMap != null) {
            assocProp = isAssociative(originalNode, constSubstMap, info);
            if (assocProp != null)
                isAssoc = true;
        }

        final boolean subTreesCouldBeRepl = replAsserts != null;

        if (!subTreesCouldBeRepl)
            return new IdentityTransformation(originalNode);

        if (isAssoc)
            return new AssociativeTransformation(originalNode, createAssocTree(
                originalNode, assocProp, info), assocProp);
        else if (subTreesCouldBeRepl)
            return new ReplaceTransformation(originalNode);

        // TODO: make the string constant!
        throw new IllegalStateException(
            "Error in createTransformation() algorithm");
    }

    private ParseNode getCanonicalForm(final ParseNode originalNode,
        final WorksheetInfo info)
    {
        return createTransformation(originalNode, info).getCanonicalNode(info);
    }

    /**
     * @param first The one operand
     * @param second The other operand
     * @return -1(less), 0(equal),1(greater)
     */
    @SuppressWarnings("unused")
    private static int compareNodes(final ParseNode first,
        final ParseNode second)
    {
        if (first.getStmt() == second.getStmt()) {
            final int len = first.getChild().length;
            for (int i = 0; i < len; i++) {
                final int res = compareNodes(first.getChild()[i],
                    second.getChild()[i]);
                if (res != 0)
                    return res;
            }

            return 0;
        }
        return first.getStmt().getSeq() < second.getStmt().getSeq() ? -1 : 1;
    }

    // -------------

    /**
     * This function is needed for debug
     * 
     * @param proofStmt the proof statement
     * @return the corresponding canonical formula
     */
    private Formula getCanonicalFormula(final ProofStepStmt proofStmt) {
        return getFormula(proofStmt.getCanonicalForm());
    }

    /**
     * This function is needed for debug
     * 
     * @param node the input node
     * @return the corresponding formula
     */
    private Formula getFormula(final ParseNode node) {
        final ParseTree tree = new ParseTree(node);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        return generatedFormula;
    }

    // ---------------------

    private void performTransformation(final WorksheetInfo info,
        final ProofStepStmt source, final Assrt impl)
    {
        final Transformation dsTr = createTransformation(
            info.derivStep.formulaParseTree.getRoot(), info);
        final Transformation tr = createTransformation(
            source.formulaParseTree.getRoot(), info);

        final ProofStepStmt eqResult = tr.transformMeToTarget(dsTr, info);
        eqResult.toString();

        final boolean isNormalOrder = TrUtil.isVarNode(impl.getLogHypArray()[0]
            .getExprParseTree().getRoot());

        final ProofStepStmt[] hypDerivArray = isNormalOrder ? new ProofStepStmt[]{
                source, eqResult}
            : new ProofStepStmt[]{eqResult, source};

        final String[] hypStep = new String[hypDerivArray.length];
        for (int i = 0; i < hypStep.length; i++)
            hypStep[i] = hypDerivArray[i].getStep();

        info.derivStep.setRef(impl);
        info.derivStep.setRefLabel(impl.getLabel());
        info.derivStep.setHypList(hypDerivArray);
        info.derivStep.setHypStepList(hypStep);
        info.derivStep.setAutoStep(false);

        return;
    }

    /**
     * Tries to unify the derivation step by some automatic transformations
     * 
     * @param proofWorksheet the proof work sheet
     * @param derivStep the derivation step
     * @return true if it founds possible unification
     */
    private List<DerivationStep> tryToFindTransformationsCore(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
    {
        if (!isInit())
            return null;
        final WorksheetInfo info = new WorksheetInfo(proofWorksheet, derivStep,
            verifyProofs, provableLogicStmtTyp);

        final Cnst derivType = info.derivStep.formulaParseTree.getRoot()
            .getStmt().getTyp();
        final Assrt implAssrt = implInfo.getEqImplication(derivType);
        if (implAssrt == null)
            return null;

        final Transformation dsTr = createTransformation(
            derivStep.formulaParseTree.getRoot(), info);
        final ParseNode dsCanonicalForm = dsTr.getCanonicalNode(info);
        derivStep.setCanonicalForm(dsCanonicalForm);

        output.dbgMessage(dbg, "I-DBG Step %s has canonical form: %s",
            derivStep, getCanonicalFormula(derivStep));

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject == derivStep)
                break;

            if (!(proofWorkStmtObject instanceof ProofStepStmt))
                continue;

            final ProofStepStmt candidate = (ProofStepStmt)proofWorkStmtObject;

            if (candidate.getCanonicalForm() == null) {
                candidate.setCanonicalForm(getCanonicalForm(
                    candidate.formulaParseTree.getRoot(), info));

                output.dbgMessage(dbg, "I-DBG Step %s has canonical form: %s",
                    candidate, getCanonicalFormula(candidate));
            }

            if (derivStep.getCanonicalForm().isDeepDup(
                candidate.getCanonicalForm()))
            {
                output.dbgMessage(dbg,
                    "I-DBG found canonical forms correspondance: %s and %s",
                    candidate, derivStep);
                performTransformation(info, candidate, implAssrt);

                // confirm unification for derivStep also!
                info.newSteps.add(derivStep);
                return info.newSteps;
            }
        }
        return null;
    }

    public List<DerivationStep> tryToFindTransformations(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
    {
        try {
            return tryToFindTransformationsCore(proofWorksheet, derivStep);
        } catch (final Exception e) {
            // TODO: make string error constant!
            output.errorMessage("E- autotramsformation problem:", e.toString());
            e.printStackTrace();
            return null;
        }
    }

    // ------------Auxiliary functions--------------
}
