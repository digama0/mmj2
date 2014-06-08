//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.pa;

import java.util.*;

import mmj.lang.*;
import mmj.verify.VerifyProofs;

/**
 * This contains information for possible automatic transformations.
 * <p>
 * Canonical form for the parse node is a parse node with sorted
 * commutative/associative transformations.
 * <p>
 * The functions in this class are separated into 5 parts:
 * <ul>
 * <li>Data structures initialization
 * <li>Canonical form construction
 * <li>Statements unification (using canonical forms)
 * <li>Canonical form comparison
 * <li>Auxiliary functions
 * </ul>
 */
public class ProofTransformations {
    /** This field is true if this object was initialized */
    private boolean isInit = false;

    Messages messages; // for debug reasons

    /** It is necessary for formula construction */
    VerifyProofs verifyProofs;

    /** The map from type to corresponding equivalence operators */
    private Map<Cnst, Stmt> eqMap;

    /** The list of equivalence operators: A = B => B = A */
    private Map<Stmt, Assrt> eqOperators;

    /** The list of transitive rules for equivalence operators */
    private Map<Stmt, Assrt> eqTransitivies;

    /** The list of commutative operators */
    private Map<Stmt, Assrt> comOp;

    /** The list of statements with possible variable replace */
    private Map<Stmt, Assrt[]> replaceOp;

    /**
     * The map: associative operators -> array of 2 elements:
     * <ul>
     * <li>f(f(a, b), c) = f(a, f(b, c))
     * <li>f(a, f(b, c)) = f(f(a, b), c)
     * </ul>
     */
    private Map<Stmt, Assrt[]> assocOp;

    /** The symbol like |- in set.mm */
    private Cnst provableLogicStmtTyp;

    /** Empty default constructor */
    public ProofTransformations() {}

    // ----------------------------

    public void prepareAutomaticTransformations(final List<Assrt> assrtList,
        final Cnst provableLogicStmtTyp, final Messages messages,
        final VerifyProofs verifyProofs)
    {
        isInit = true;
        this.messages = messages;
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        eqOperators = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findEquivalenceCommutativeRules(assrt);

        eqTransitivies = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findEquivalenceTransitiveRules(assrt);

        filterOnlyEqRules();

        comOp = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findCommutativeRules(assrt);

        assocOp = new HashMap<Stmt, Assrt[]>();
        for (final Assrt assrt : assrtList)
            findAssociativeRules(assrt);

        replaceOp = new HashMap<Stmt, Assrt[]>();
        for (final Assrt assrt : assrtList)
            findReplaceRules(assrt);
    }

    /**
     * Find commutative equivalence rules, like A = B => B = A
     * <p>
     * TODO: Find rules in implication form also
     * 
     * @param assrt the candidate
     */
    private void findEquivalenceCommutativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        final ParseTree hypTree = logHyps[0].getExprParseTree();

        if (varHypArray.length != 2)
            return;

        if (hypTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getRoot().getChild().length != 2)
            return;

        final Stmt stmt = assrtTree.getRoot().getStmt();

        if (hypTree.getRoot().getStmt() != stmt)
            return;

        if (hypTree.getRoot().getChild()[0].getStmt() != assrtTree.getRoot()
            .getChild()[1].getStmt())
            return;

        if (hypTree.getRoot().getChild()[1].getStmt() != assrtTree.getRoot()
            .getChild()[0].getStmt())
            return;

        messages.accumInfoMessage(
            "I-DBG Equivalence commutative assrt: %s: %s", assrt.toString(),
            assrt.getFormula().toString());

        if (!eqOperators.containsKey(stmt))
            eqOperators.put(stmt, assrt);
    }

    /**
     * Find transitive equivalence rules, like A = B & B = C => A = C
     * <p>
     * TODO: Find rules in implication form also
     * 
     * @param assrt the candidate
     */
    private void findEquivalenceTransitiveRules(final Assrt assrt) {
        final VarHyp[] mandVarHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 2)
            return;

        final ParseTree hypTree1 = logHyps[0].getExprParseTree();
        final ParseTree hypTree2 = logHyps[1].getExprParseTree();

        if (mandVarHypArray.length != 2)
            return;

        if (hypTree1.getMaxDepth() != 2 || hypTree2.getMaxDepth() != 2)
            return;

        if (assrtTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getRoot().getChild().length != 2)
            return;

        final Stmt stmt = assrtTree.getRoot().getStmt();

        if (hypTree1.getRoot().getStmt() != stmt)
            return;

        if (hypTree2.getRoot().getStmt() != stmt)
            return;

        // check for 'A' in 'A = B & B = C => A = C'
        if (hypTree1.getRoot().getChild()[0].getStmt() != assrtTree.getRoot()
            .getChild()[0].getStmt())
            return;

        // check for 'B' in 'A = B & B = C'
        if (hypTree1.getRoot().getChild()[1].getStmt() != hypTree2.getRoot()
            .getChild()[0].getStmt())
            return;

        // check for 'C' in 'A = B & B = C => A = C'
        if (hypTree2.getRoot().getChild()[1].getStmt() != assrtTree.getRoot()
            .getChild()[1].getStmt())
            return;

        messages.accumInfoMessage("I-DBG Equivalence transitive assrt: %s: %s",
            assrt.toString(), assrt.getFormula().toString());
        if (!eqTransitivies.containsKey(stmt))
            eqTransitivies.put(stmt, assrt);
    }

    /**
     * We found candidates for equivalence from commutative and transitive
     * sides. Now compare results and remove unsuitable!
     */
    private void filterOnlyEqRules() {
        while (true) {
            boolean changed = false;

            for (final Stmt eq : eqTransitivies.keySet())
                if (!eqOperators.containsKey(eq)) {
                    eqTransitivies.remove(eq);
                    changed = true;
                    break;
                }

            for (final Stmt eq : eqOperators.keySet())
                if (!eqTransitivies.containsKey(eq)) {
                    eqOperators.remove(eq);
                    changed = true;
                    break;
                }

            if (!changed)
                break;
        }

        // Debug output:
        for (final Stmt eq : eqTransitivies.keySet())
            messages.accumInfoMessage("I-DBG Equivalence rules: %s: %s and %s",
                eq, eqOperators.get(eq).getFormula().toString(), eqTransitivies
                    .get(eq).getFormula().toString());

        // Create the reverse map:
        eqMap = new HashMap<Cnst, Stmt>();

        for (final Stmt eq : eqOperators.keySet()) {
            final Assrt assrt = eqOperators.get(eq);

            final ParseTree assrtTree = assrt.getExprParseTree();
            final Cnst type = assrtTree.getRoot().getChild()[0].getStmt()
                .getTyp();
            eqMap.put(type, eq);

            messages.accumInfoMessage("I-DBG Type equivalence map: %s: %s",
                type, eq);
        }
    }

    /**
     * Filters commutative rules, like A + B = B + A
     * 
     * @param assrt the candidate
     */
    private void findCommutativeRules(final Assrt assrt) {
        // TODO: adds the support of assrts like addcomi
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        // if (assrt.toString().equals("addcomi"))
        // assrt.toString();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 0)
            return;

        if (varHypArray.length != 2)
            return;

        if (varHypArray.length != 2)
            return;

        if (assrtTree.getMaxDepth() != 3)
            return;

        if (!eqOperators.containsKey(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it is the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        if (subTrees[0].getChild().length != 2)
            return;

        if (subTrees[0].getChild()[0].getStmt() != subTrees[1].getChild()[1]
            .getStmt())
            return;

        if (subTrees[0].getChild()[1].getStmt() != subTrees[1].getChild()[0]
            .getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        if (comOp.containsKey(stmt))
            return;

        messages.accumInfoMessage("I-DBG commutative assrts: %s: %s",
            assrt.toString(), assrt.getFormula().toString());
        comOp.put(stmt, assrt);
    }

    /**
     * Filters replace rules, like A = B => g(A) = g(B)
     * 
     * @param assrt the candidate
     */
    private void findReplaceRules(final Assrt assrt) {
        // TODO: find rules in the form of implication!
        // TODO: logHyps could contains other hypotheses
        assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 3)
            return;

        if (eqOperators.get(assrtTree.getRoot().getStmt()) == null)
            return;

        final LogHyp testHyp = logHyps[0];

        final ParseTree hypTree = testHyp.getExprParseTree();

        if (eqOperators.get(hypTree.getRoot().getStmt()) == null)
            return;

        final ParseNode[] hypSubTrees = hypTree.getRoot().getChild();

        assert hypSubTrees.length == 2 : "It should be the equivalence rule!";

        if (!isVarNode(hypSubTrees[0]) || !isVarNode(hypSubTrees[1]))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        assert subTrees.length == 2 : "It should be the equivalence rule!";

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ParseNode[] leftChild = subTrees[0].getChild();
        final ParseNode[] rightChild = subTrees[1].getChild();

        // Fast compare, change if the depth of this assrt statement tree
        // could be more then 3
        int replPos = -1;
        replaceCheck: for (int i = 0; i < leftChild.length; i++)
            if (leftChild[i].getStmt() != rightChild[i].getStmt()) {
                // Another place for replace? It is strange!
                if (replPos != -1)
                    return;

                // We found the replace
                replPos = i;

                // Check that it is actually the swap of two variables
                for (int k = 0; k < 2; k++) {
                    final int m = (k + 1) % 2; // the other index
                    if (leftChild[i].getStmt() == hypSubTrees[k].getStmt()
                        && rightChild[i].getStmt() == hypSubTrees[m].getStmt())
                        continue replaceCheck;
                }

                return;
            }

        Assrt[] repl = replaceOp.get(stmt);

        if (repl == null) {
            repl = new Assrt[subTrees[0].getChild().length];
            replaceOp.put(stmt, repl);
        }

        // it is the first such assrt;
        if (repl[replPos] != null)
            return;

        repl[replPos] = assrt;

        messages.accumInfoMessage("I-DBG Replace assrts: %s: %s",
            assrt.toString(), assrt.getFormula().toString());
    }

    /**
     * Filters associative rules, like (A + B) + C = A + (B + C)
     * 
     * @param assrt the candidate
     */
    private void findAssociativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        // TODO: remove this restriction!
        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 0)
            return;

        if (varHypArray.length != 3)
            return;

        if (assrtTree.getMaxDepth() != 4)
            return;

        if (!eqOperators.containsKey(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it must be the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt f = subTrees[0].getStmt();
        // we need to find one of the 2 patterns:
        // 0) f(f(a, b), c) = f(a, f(b, c))
        // 1) f(a, f(b, c)) = f(f(a, b), c)
        for (int i = 0; i < 2; i++) {
            final int j = (i + 1) % 2;
            final ParseNode[] leftChild = subTrees[0].getChild();
            final ParseNode[] rightChild = subTrees[1].getChild();
            if (leftChild[i].getStmt() != f)
                continue;

            if (rightChild[j].getStmt() != f)
                continue;

            if (!isVarNode(leftChild[j]))
                continue;

            if (leftChild[j].getStmt() != rightChild[j].getChild()[j].getStmt())
                continue;

            if (leftChild[i].getChild()[i].getStmt() != rightChild[i].getStmt())
                continue;

            if (leftChild[i].getChild()[j].getStmt() != rightChild[j]
                .getChild()[i].getStmt())
                continue;

            final Stmt stmt = subTrees[0].getStmt();

            Assrt[] assoc = assocOp.get(stmt);

            if (assoc == null) {
                assoc = new Assrt[2];
                assocOp.put(stmt, assoc);
            }

            if (assoc[i] != null)
                continue;

            messages.accumInfoMessage("I-DBG associative assrts: %d. %s: %s",
                i, assrt, assrt.getFormula());
            assoc[i] = assrt;
            return;
        }
    }

    // ----------------------------------------------

    public static class TransformationOld {
        public final ParseNode canonResult;
        public final ParseNode equivalence;
        public final Assrt assrt;
        public final TransformationOld[] subTransformations;

        public TransformationOld(final ParseNode canonResult,
            final ParseNode equivalence, final Assrt assrt, final int subTrSize)
        {
            this.canonResult = canonResult;
            this.equivalence = equivalence;
            this.assrt = assrt;
            subTransformations = new TransformationOld[subTrSize];
        }
    }

    /**
     * The transformation of {@link Transformation#originalNode} to any equal
     * node.
     */
    public abstract class Transformation {
        /** The original node */
        public final ParseNode originalNode;

        public Transformation(final ParseNode originalNode) {
            this.originalNode = originalNode;
        }

        /**
         * @return the canonical node for {@link Transformation#originalNode}
         */
        abstract public ParseNode getCanonicalForm();

        /**
         * This function should construct derivation step sequence from this
         * {@link Transformation#originalNode} to target's
         * {@link Transformation#originalNode}
         * 
         * @param target the target transformation
         * @param proofWorksheet the proof work sheet
         * @param derivStep 'this' derivation step
         * @return the proof step which confirms that this
         *         {@link Transformation#originalNode} is equal to target
         *         {@link Transformation#originalNode}. Could returns null it
         *         this and target are equal.
         */
        public abstract ProofStepStmt transformMeToTarget(
            final Transformation target, final ProofWorksheet proofWorksheet,
            final DerivationStep derivStep);

        /**
         * This function checks maybe we should not do anything! We should
         * perform transformations if this function returns derivStep.
         * 
         * @param target the target transformation
         * @param proofWorksheet the proof work sheet
         * @param derivStep 'this' derivation step
         * @return null (if target equals to this), statement (if it is already
         *         exists) or derivStep if we should perform some
         *         transformations!
         */
        protected ProofStepStmt checkTransformationNecessary(
            final Transformation target, final ProofWorksheet proofWorksheet,
            final DerivationStep derivStep)
        {

            if (originalNode.isDeepDup(target.originalNode))
                return null; // nothing to transform!

            final Stmt equalStmt = eqMap.get(originalNode.getStmt().getTyp());

            // Create node g(A, B, C) = g(A', B', C')
            final ParseNode stepNode = createBinaryNode(equalStmt,
                originalNode, target.originalNode);

            final ProofStepStmt stepTr = getOrCreateProofStepStmt(
                proofWorksheet, derivStep, stepNode, null, null);

            if (stepTr != null)
                return stepTr;

            return derivStep; // there is more complex case!
        }
    }

    /** No transformation at all =) */
    public class IdentityTransformation extends Transformation {

        public IdentityTransformation(final ParseNode originalNode) {
            super(originalNode);
        }

        @Override
        public ParseNode getCanonicalForm() {
            return originalNode;
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
        {
            assert target.originalNode.isDeepDup(originalNode);
            return null; // nothing to do
        }
    }

    /**
     * The replace transformation: we could transform children of corresponding
     * node and replace them with its canonical form (or vice versa).
     */
    public class ReplaceTransformation extends Transformation {
        public ReplaceTransformation(final ParseNode originalNode) {
            super(originalNode);
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
        {
            assert target instanceof ReplaceTransformation;
            final ReplaceTransformation trgt = (ReplaceTransformation)target;

            final ProofStepStmt simpleRes = checkTransformationNecessary(
                target, proofWorksheet, derivStep);
            if (simpleRes != derivStep)
                return simpleRes;

            // result transformation statement
            ProofStepStmt resStmt = null;

            // the current transformation result
            ParseNode resNode = originalNode;

            final Assrt[] replAsserts = replaceOp.get(originalNode.getStmt());

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
                assert eqOperators.containsKey(equalStmt);

                // transform ith child
                // the result should be some B = B' statement
                final ProofStepStmt childTrStmt = createTransformation(child)
                    .transformMeToTarget(createTransformation(targetChild),
                        proofWorksheet, derivStep);
                if (childTrStmt == null)
                    continue; // noting to do

                // get the node B = B'
                final ParseNode childTrRoot = childTrStmt.formulaParseTree
                    .getRoot();

                // transformation should be transformation:
                // check result childTrStmt statement
                assert eqOperators.containsKey(childTrRoot.getStmt());
                assert childTrRoot.getChild().length == 2;
                assert childTrRoot.getChild()[0].isDeepDup(originalNode
                    .getChild()[i]);
                assert childTrRoot.getChild()[1].isDeepDup(trgt.originalNode
                    .getChild()[i]);

                // Create statement d:childTrStmt:replAssert
                // |- g(A', B, C) = g(A', B', C)
                final ProofStepStmt stepTr = createReplaceStep(proofWorksheet,
                    derivStep, resNode, i, trgt.originalNode.getChild()[i],
                    childTrStmt);
                resNode = stepTr.formulaParseTree.getRoot().getChild()[1];

                // resStmt now have the form g(A, B, C) = g(A', B, C)
                // So create transitive:
                // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
                // => g(A, B, C) = g(A', B', C)
                // Create statement d:resStmt,stepTr:transitive
                // |- g(A, B, C) = g(A', B', C)
                resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                    resStmt, stepTr);
            }

            assert resStmt != null;

            assert resStmt.formulaParseTree.getRoot().getChild()[0]
                .isDeepDup(originalNode);
            assert resStmt.formulaParseTree.getRoot().getChild()[1]
                .isDeepDup(target.originalNode);

            return resStmt;
        }

        @Override
        public ParseNode getCanonicalForm() {
            final Assrt[] replAsserts = replaceOp.get(originalNode.getStmt());
            final ParseNode resNode = originalNode.cloneWithoutChildren();

            for (int i = 0; i < resNode.getChild().length; i++) {
                if (replAsserts[i] == null)
                    continue;
                resNode.getChild()[i] = createTransformation(
                    originalNode.getChild()[i]).getCanonicalForm();
            }

            return resNode;
        }
    }

    static class AssocTree {
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
    }

    static AssocTree createAssocTree(final ParseNode originalNode,
        final Stmt assocStmt)
    {
        final Stmt stmt = originalNode.getStmt();
        final ParseNode[] childrent = originalNode.getChild();
        if (stmt != assocStmt)
            return new AssocTree();
        return new AssocTree(createAssocTree(childrent[0], assocStmt),
            createAssocTree(childrent[1], assocStmt));
    }

    /** Only associative transformations */
    public class AssociativeTransformation extends Transformation {
        final AssocTree structure;

        public AssociativeTransformation(final ParseNode originalNode,
            final AssocTree structure)
        {
            super(originalNode);
            this.structure = structure;
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
        {
            assert target instanceof AssociativeTransformation;
            final AssociativeTransformation trgt = (AssociativeTransformation)target;

            assert trgt.structure.size == structure.size;
            final Stmt stmt = originalNode.getStmt();

            int from;
            if (structure.subTrees[0].size > trgt.structure.subTrees[0].size)
                from = 0;
            else
                from = 1;
            final int to = (from + 1) % 2;

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

                    gNode = createBinaryNode(stmt, aNode,
                        createBinaryNode(stmt, dNode, fNode));

                    // transform to normal direction => 'from'
                    final ProofStepStmt assocTr = createAssociativeStep(
                        proofWorksheet, derivStep, from, prevNode, gNode);

                    resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                        resStmt, assocTr);
                }

                final AssocTree eAssT = gAssT.subTrees[from];
                final AssocTree fAssT = gAssT.subTrees[to];
                final AssocTree aAssT = eAssT.subTrees[from];
                final AssocTree dAssT = eAssT.subTrees[to];
                final AssocTree bAssT = dAssT.subTrees[from];
                final AssocTree cAssT = dAssT.subTrees[to];

                if (fAssT.size != toSize) {
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
                    eNode = createBinaryNode(stmt,
                        createBinaryNode(stmt, aNode, bNode), cNode);

                    // transform to other direction => 'to'
                    final ProofStepStmt assocTr = createAssociativeStep(
                        proofWorksheet, derivStep, to, prevENode, eNode);

                    final ParseNode prevGNode = gNode;
                    gNode = createBinaryNode(stmt, eNode, fNode);

                    final ProofStepStmt replTr = createReplaceStep(
                        proofWorksheet, derivStep, prevGNode, from, eNode,
                        assocTr);

                    resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                        resStmt, replTr);
                }
                else
                    break;
            }

            assert gAssT.subTrees[0].size == trgt.structure.subTrees[0].size;
            assert gAssT.subTrees[1].size == trgt.structure.subTrees[1].size;

            final Transformation replaceTr = new ReplaceTransformation(gNode);

            final ProofStepStmt replTrStep = replaceTr.transformMeToTarget(
                target, proofWorksheet, derivStep);

            if (replTrStep != null)
                resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                    resStmt, replTrStep);

            return resStmt;
        }

        private ParseNode constructCanonicalForm(ParseNode left,
            final ParseNode cur)
        {
            if (cur.getStmt() != originalNode.getStmt())
                if (left == null)
                    return cur;
                else
                    return createBinaryNode(originalNode.getStmt(), left, cur);

            for (int i = 0; i < 2; i++)
                left = constructCanonicalForm(left, cur.getChild()[0]);

            return left;
        }

        @Override
        public ParseNode getCanonicalForm() {
            return constructCanonicalForm(null, originalNode);
        }
    }

    private ProofStepStmt createReplaceStep(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final ParseNode prevVersion, final int i, final ParseNode newSubTree,
        final ProofStepStmt childTrStmt)
    {
        final Stmt equalStmt = eqMap.get(prevVersion.getStmt().getTyp());
        final Assrt[] replAsserts = replaceOp.get(prevVersion.getStmt());
        final ParseNode resNode = prevVersion.cloneWithoutChildren();

        // Fill the next child
        // So the new node has form g(A', B', C)
        resNode.getChild()[i] = newSubTree;

        // Create node g(A', B, C) = g(A', B', C)
        final ParseNode stepNode = createBinaryNode(equalStmt, prevVersion,
            resNode);

        // Create statement d:childTrStmt:replAssert
        // |- g(A', B, C) = g(A', B', C)
        final ProofStepStmt stepTr = getOrCreateProofStepStmt(proofWorksheet,
            derivStep, stepNode, new ProofStepStmt[]{childTrStmt},
            replAsserts[i]);
        return stepTr;
    }

    private ProofStepStmt createAssociativeStep(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final int from, final ParseNode prevNode, final ParseNode newNode)
    {
        final Assrt[] assocTr = assocOp.get(prevNode.getStmt());

        final boolean revert;
        final Assrt assocAssrt;
        if (assocTr[from] != null) {
            assocAssrt = assocTr[from];
            revert = false;
        }
        else {
            final int other = (from + 1) % 2;
            assocAssrt = assocTr[other];
            revert = true;
        }
        assert assocAssrt != null;

        final Stmt equalStmt = assocAssrt.getExprParseTree().getRoot()
            .getStmt();

        // Create node f(f(a, b), c) = f(a, f(b, c))
        final ParseNode stepNode = !revert ? createBinaryNode(equalStmt,
            prevNode, newNode) : createBinaryNode(equalStmt, newNode, prevNode);

        ProofStepStmt res = getOrCreateProofStepStmt(proofWorksheet, derivStep,
            stepNode, new ProofStepStmt[]{}, assocAssrt);

        if (revert) {
            final Assrt eqComm = eqOperators.get(equalStmt);
            final ParseNode revNode = createBinaryNode(equalStmt, prevNode,
                newNode);

            res = getOrCreateProofStepStmt(proofWorksheet, derivStep, revNode,
                new ProofStepStmt[]{res}, eqComm);
        }

        return res;
    }

    private ProofStepStmt transitiveConnectStep(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final ProofStepStmt prevRes, final ProofStepStmt newRes)
    {
        if (prevRes == null)
            return newRes;

        final ParseNode prevRoot = prevRes.formulaParseTree.getRoot();
        final ParseNode newRoot = newRes.formulaParseTree.getRoot();
        final Stmt equalStmt = prevRoot.getStmt();
        final Assrt transitive = eqTransitivies.get(equalStmt);

        final ParseNode transitiveNode = createBinaryNode(equalStmt,
            prevRoot.getChild()[0], newRoot.getChild()[1]);

        // resStmt now have the form g(A, B, C) = g(A', B, C)
        // So create transitive:
        // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
        // => g(A, B, C) = g(A', B', C)
        // Create statement d:resStmt,stepTr:transitive
        // |- g(A, B, C) = g(A', B', C)
        final ProofStepStmt resStmt = getOrCreateProofStepStmt(proofWorksheet,
            derivStep, transitiveNode, new ProofStepStmt[]{prevRes, newRes},
            transitive);

        return resStmt;
    }

    private static ParseNode createBinaryNode(final Stmt stmt,
        final ParseNode left, final ParseNode right)
    {
        final ParseNode eqRoot = new ParseNode(stmt);
        final ParseNode[] eqChildren = {left, right};
        eqRoot.setChild(eqChildren);
        return eqRoot;
    }

    Transformation createTransformation(final ParseNode originalNode) {
        final Stmt stmt = originalNode.getStmt();
        final Formula f = getFormula(originalNode);// !!!delete it
        f.toString();// !!!delete it

        final Assrt[] replAsserts = replaceOp.get(stmt);
        // final Assrt comAssert = comOp.get(stmt);

        final boolean isAssoc = assocOp.containsKey(stmt);

        final boolean subTreesCouldBeRepl = replAsserts != null;
        // final boolean comOper = comAssert != null;

        if (!subTreesCouldBeRepl)
            return new IdentityTransformation(originalNode);

        if (isAssoc)
            return new AssociativeTransformation(originalNode, createAssocTree(
                originalNode, stmt));
        else if (subTreesCouldBeRepl)
            return new ReplaceTransformation(originalNode);

        // TODO: make the string constant!
        throw new IllegalStateException(
            "Error in createTransformation() algorithm");
    }

    public TransformationOld getCanonicalForm(final ParseNode origlNode) {
        final Stmt stmt = origlNode.getStmt();

        final Assrt[] replAsserts = replaceOp.get(stmt);
        final Assrt comAssert = comOp.get(stmt);

        final boolean subTreesCouldBeRepl = replAsserts != null;
        final boolean comOper = comAssert != null;

        if (!comOper && !subTreesCouldBeRepl)
            return null; // We could do nothing with this node!

        TransformationOld resTr = null;

        final int length = origlNode.getChild().length;
        final ParseNode[] origChildren = origlNode.getChild();
        ParseNode resNode = origlNode;

        final Cnst type = stmt.getTyp();
        type.toString();

        final Stmt eqOrigStmt = eqMap.get(type);
        final Assrt eqTrans = eqTransitivies.get(eqOrigStmt);

        if (subTreesCouldBeRepl)
            // Now we could reconstruct subtrees!
            for (int i = 0; i < length; i++) {
                final Assrt replAssert = replAsserts[i];
                if (replAssert == null)
                    // We can't transform this sub-tree
                    continue;

                // Get sub-node transformation:
                final TransformationOld subTr = getCanonicalForm(origChildren[i]);

                if (subTr == null)
                    // We should not do any transformations
                    continue;

                final ParseNode prevVersion = resNode;
                resNode = prevVersion.cloneWithoutChildren();

                // Fill the next child
                resNode.getChild()[i] = subTr.canonResult;

                // Construct the next step of this node transformation:
                final TransformationOld eqTr = getTransformationStep(
                    replAssert, prevVersion, resNode);

                assert replAssert.getLogHypArray().length == 1;

                eqTr.subTransformations[0] = subTr;

                if (resTr != null) {
                    // It is not the first transformation and we should include
                    // transitive equivalence!
                    final TransformationOld transTr = getTransformationStep(
                        eqTrans, origlNode, resNode);

                    assert eqTrans.getLogHypArray().length == 2;

                    transTr.subTransformations[0] = resTr;
                    transTr.subTransformations[1] = eqTr;
                    resTr = transTr;
                }
                else
                    resTr = eqTr;
            }

        if (comOper) {
            // This node is the commutative operation node
            assert length == 2;

            if (compareNodes(resNode.getChild()[0], resNode.getChild()[1]) > 0)
            {
                final ParseNode prevVersion = resNode;
                resNode = prevVersion.cloneWithoutChildren();

                // Swap it!
                final ParseNode tmp = resNode.getChild()[0];
                resNode.getChild()[0] = resNode.getChild()[1];
                resNode.getChild()[1] = tmp;

                // Construct the next step of this node transformation:
                final TransformationOld eqTr = getTransformationStep(comAssert,
                    prevVersion, resNode);

                assert comAssert.getLogHypArray().length == 0;

                if (resTr != null) {
                    // It is not the first transformation and we should include
                    // transitive equivalence!
                    final TransformationOld transTr = getTransformationStep(
                        eqTrans, origlNode, resNode);

                    assert eqTrans.getLogHypArray().length == 2;

                    transTr.subTransformations[0] = resTr;
                    transTr.subTransformations[1] = eqTr;
                    resTr = transTr;
                }
                else
                    resTr = eqTr;
            }
        }
        return resTr;
    }

    private static TransformationOld getTransformationStep(final Assrt assrt,
        final ParseNode left, final ParseNode right)
    {
        final Stmt stmt = assrt.getExprParseTree().getRoot().getStmt();
        final int lenght = assrt.getLogHypArray().length;
        final ParseNode eqRoot = new ParseNode(stmt);
        final ParseNode[] eqChildren = {left, right};
        eqRoot.setChild(eqChildren);
        final TransformationOld tr = new TransformationOld(right, eqRoot,
            assrt, lenght);
        return tr;
    }

    /**
     * @param first The one operand
     * @param second The other operand
     * @return -1(less), 0(equal),1(greater)
     */
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

    private ProofStepStmt getOrCreateProofStepStmt(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final ParseNode root, final ProofStepStmt[] hyps, final Assrt assrt)
    {
        final ParseTree tree = new ParseTree(root);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        generatedFormula.setTyp(provableLogicStmtTyp);

        final ProofStepStmt findMatchingStepFormula = proofWorksheet
            .findMatchingStepFormula(generatedFormula, derivStep);

        if (findMatchingStepFormula != null)
            return findMatchingStepFormula;

        if (hyps == null || assrt == null)
            return null;

        assert assrt.getLogHypArray().length == hyps.length;

        final String[] steps = new String[hyps.length];
        for (int i = 0; i < hyps.length; i++)
            steps[i] = hyps[i].getStep();

        final DerivationStep d = proofWorksheet.addDerivStep(derivStep, hyps,
            steps, assrt.getLabel(), generatedFormula, tree,
            Collections.<WorkVar> emptyList());
        return d;
    }

    private ProofStepStmt outputTransformations(final TransformationOld tr,
        final VerifyProofs verifyProofs, final Messages messages,
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
    {
        if (tr == null)
            return null;

        final int len = tr.subTransformations.length;
        final ProofStepStmt[] hyps = new ProofStepStmt[len];
        final String[] steps = new String[len];

        for (int i = 0; i < len; i++) {
            final ProofStepStmt prev = outputTransformations(
                tr.subTransformations[i], verifyProofs, messages,
                proofWorksheet, derivStep);
            hyps[i] = prev;
            steps[i] = prev.getStep();
        }

        final ParseTree tree = new ParseTree(tr.equivalence);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        generatedFormula.setTyp(provableLogicStmtTyp);

        final ProofStepStmt findMatchingStepFormula = proofWorksheet
            .findMatchingStepFormula(generatedFormula, derivStep);

        if (findMatchingStepFormula != null)
            return findMatchingStepFormula;

        final DerivationStep d = proofWorksheet.addDerivStep(derivStep, hyps,
            steps, tr.assrt.getLabel(), generatedFormula, tree,
            Collections.<WorkVar> emptyList());

        messages.accumInfoMessage("I-DBG Transformation (%s): %s", tr.assrt, d);
        return d;
    }

    // ---------------------

    /**
     * Tries to unify the derivation step by some automatic transformations
     * 
     * @param proofWorksheet the proof work sheet
     * @param derivStep the derivation step
     * @return true if it founds possible unification
     */
    public boolean tryToFindTransformations(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
    {
        if (!isInit)
            return false;

        final TransformationOld dsCanonicalForm = getCanonicalForm(derivStep.formulaParseTree
            .getRoot());
        derivStep.setCanonicalTransformation(dsCanonicalForm);

        messages.accumInfoMessage("I-DBG Step %s has canonical form: %s",
            derivStep, getCanonicalFormula(derivStep));

        outputTransformations(dsCanonicalForm, verifyProofs, messages,
            proofWorksheet, derivStep);

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject == derivStep)
                break;

            if (!(proofWorkStmtObject instanceof ProofStepStmt))
                continue;

            final ProofStepStmt candidate = (ProofStepStmt)proofWorkStmtObject;

            if (candidate.getCanonicalTransformation() == null)
                if (!candidate.isSameCanonicalForm()) {
                    final TransformationOld tr = getCanonicalForm(candidate.formulaParseTree
                        .getRoot());
                    candidate.setCanonicalTransformation(tr);

                    messages.accumInfoMessage(
                        "I-DBG Step %s has canonical form: %s", candidate,
                        getCanonicalFormula(candidate));
                }

            if (derivStep.getCanonicalForm().isDeepDup(
                candidate.getCanonicalForm()))
            {
                messages.accumInfoMessage(
                    "I-DBG found canonical forms correspondance: %s and %s",
                    candidate.toString(), derivStep.toString());
                return true;
            }
        }
        return false;
    }
    // ------------Auxiliary functions--------------

    private static boolean isVarNode(final ParseNode node) {
        return isVarStmt(node.getStmt());
    }

    private static boolean isVarStmt(final Stmt stmt) {
        return stmt instanceof VarHyp;
    }
}
