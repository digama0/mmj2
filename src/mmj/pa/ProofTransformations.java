//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.pa;

import java.util.*;

import mmj.lang.*;

/**
 * This contains information for possible automatic transformations.
 */
public class ProofTransformations {
    /** The list of equivalence operators */
    private Map<Stmt, Theorem> eqOperators;

    /** The list of commutative operators */
    private Map<Stmt, Theorem> comOp;

    /** The list of statements with possible variable replace */
    private Map<Stmt, Theorem[]> replaceOp;

    /** The list of associative operators */
    private Set<Stmt> assocOp;

    /** Empty default constructor */
    public ProofTransformations() {}

    // ----------------------------

    public void prepareAutomaticTransformations(
        final List<Theorem> theoremList, final Messages messages)
    {
        eqOperators = new HashMap<Stmt, Theorem>();
        for (final Theorem theorem : theoremList)
            findEquivalenceRules(theorem, messages);

        comOp = new HashMap<Stmt, Theorem>();
        for (final Theorem theorem : theoremList)
            findCommutativeRules(theorem, messages);

        assocOp = new HashSet<Stmt>();
        for (final Theorem theorem : theoremList)
            findAssociativeRules(theorem, messages);

        replaceOp = new HashMap<Stmt, Theorem[]>();
        for (final Theorem theorem : theoremList)
            findReplaceRules(theorem, messages);
    }

    private void findEquivalenceRules(final Theorem theorem,
        final Messages messages)
    {
        // Try to find equivalence rules, like A = B <=> B = A
        // TODO: find implication rules also
        final VarHyp[] varHypArray = theorem.getMandVarHypArray();
        final LogHyp[] logHyps = theorem.getLogHypArray();
        final ParseTree theoremTree = theorem.getExprParseTree();

        if (logHyps.length != 1)
            return;

        final ParseTree hypTree = logHyps[0].getExprParseTree();

        if (varHypArray.length != 2)
            return;

        if (hypTree.getMaxDepth() != 2)
            return;

        if (theoremTree.getMaxDepth() != 2)
            return;

        if (theoremTree.getRoot().getChild().length != 2)
            return;

        if (hypTree.getRoot().getStmt() != theoremTree.getRoot().getStmt())
            return;

        if (hypTree.getRoot().getChild()[0].getStmt() != theoremTree.getRoot()
            .getChild()[1].getStmt())
            return;

        if (hypTree.getRoot().getChild()[1].getStmt() != theoremTree.getRoot()
            .getChild()[0].getStmt())
            return;

        messages.accumInfoMessage("I-DBG Equivalence theorems: %s",
            theorem.toString());
        eqOperators.put(theoremTree.getRoot().getStmt(), theorem);
    }

    private void findCommutativeRules(final Theorem theorem,
        final Messages messages)
    {
        // TODO: adds the support of theorems like addcomi
        // Find commutative rules, like A + B = B + A

        final VarHyp[] varHypArray = theorem.getMandVarHypArray();
        final ParseTree theoremTree = theorem.getExprParseTree();

        // if (theorem.toString().equals("addcomi"))
        // theorem.toString();

        final LogHyp[] logHyps = theorem.getLogHypArray();
        if (logHyps.length != 0)
            return;

        if (varHypArray.length != 2)
            return;

        if (varHypArray.length != 2)
            return;

        if (theoremTree.getMaxDepth() != 3)
            return;

        if (!eqOperators.containsKey(theoremTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = theoremTree.getRoot().getChild();

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

        messages.accumInfoMessage("I-DBG commutative theorems: %s: %s",
            theorem.toString(), theorem.getFormula().toString());
        comOp.put(theoremTree.getRoot().getStmt(), theorem);
    }

    private void findReplaceRules(final Theorem theorem, final Messages messages)
    {

        theorem.getMandVarHypArray();
        final LogHyp[] logHyps = theorem.getLogHypArray();
        final ParseTree theoremTree = theorem.getExprParseTree();

        // TODO: find rules in the form of implication!
        // TODO: logHyps could contains other hypotheses
        if (logHyps.length != 1)
            return;

        // Maybe depth restriction could be weaken
        if (theoremTree.getMaxDepth() != 3)
            return;

        if (eqOperators.get(theoremTree.getRoot().getStmt()) == null)
            return;

        final LogHyp testHyp = logHyps[0];

        final ParseTree hypTree = testHyp.getExprParseTree();

        if (eqOperators.get(hypTree.getRoot().getStmt()) == null)
            return;

        final ParseNode[] hypSubTrees = hypTree.getRoot().getChild();

        assert hypSubTrees.length == 2 : "It should be the equivalence rule!";

        if (!isVarNode(hypSubTrees[0]) || !isVarNode(hypSubTrees[1]))
            return;

        final ParseNode[] subTrees = theoremTree.getRoot().getChild();

        assert subTrees.length == 2 : "It should be the equivalence rule!";

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ParseNode[] leftChild = subTrees[0].getChild();
        final ParseNode[] rightChild = subTrees[1].getChild();

        // Fast compare, change if the depth of this theorem statement tree
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

        Theorem[] repl = replaceOp.get(stmt);

        if (repl == null) {
            repl = new Theorem[subTrees[0].getChild().length];
            replaceOp.put(stmt, repl);
        }

        // it is the first such theorem;
        if (repl[replPos] != null)
            return;

        repl[replPos] = theorem;

        messages.accumInfoMessage("I-DBG Replace theorems: %s: %s",
            theorem.toString(), theorem.getFormula().toString());
    }

    private void findAssociativeRules(final Theorem theorem,
        final Messages messages)
    {
        // Find now associative rules, like (A + B) + C = A + (B + C)
        final VarHyp[] varHypArray = theorem.getMandVarHypArray();
        // final LogHyp[] logHyps = theorem.getLogHypArray();
        final ParseTree theoremTree = theorem.getExprParseTree();

        // if (theorem.toString().equals("addcomi"))
        // theorem.toString();

        // TODO: adds the support of theorems like addcomi

        if (varHypArray.length != 3)
            return;

        if (theoremTree.getMaxDepth() != 4)
            return;

        if (!eqOperators.containsKey(theoremTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = theoremTree.getRoot().getChild();

        // it is the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt f = subTrees[0].getStmt();
        // we need to find one of the 2 patterns:
        // 1) f(a, f(b, c)) = f(f(a, b), c)
        // 2) f(f(a, b), c) = f(a, f(b, c))
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

            messages.accumInfoMessage("I-DBG associative theorems: %s: %s",
                theorem.toString(), theorem.getFormula().toString());
            assocOp.add(theoremTree.getRoot().getStmt());
            return;
        }
    }

    // ----------------------------------------------

    public static class Transformation {
        final ParseNode canonResult;
        final ParseNode equivalence;
        Transformation next;

        public Transformation(final ParseNode canonResult,
            final ParseNode equivalence)
        {
            this.canonResult = canonResult;
            this.equivalence = equivalence;
            next = null;
        }

        public Transformation(final ParseNode canonResult,
            final ParseNode equivalence, final Transformation next)
        {
            this.canonResult = canonResult;
            this.equivalence = equivalence;
            this.next = next;
        }

        public void addToTheTail(final Transformation second) {
            // TODO: optimize it!
            Transformation last = this;
            while (last.next != null)
                last = last.next;

            last.next = second;
        }
    }

    // Concatenates transformations
    private static Transformation concatTrs(final Transformation first,
        final Transformation second)
    {
        assert first != null || second != null;
        if (first != null) {
            first.addToTheTail(second);
            return first;
        }
        return second;
    }

    public Transformation getCanonicalForm(final ParseNode node) {
        final Stmt stmt = node.getStmt();

        final Theorem[] replTheorems = replaceOp.get(stmt);
        final Theorem comTreorem = comOp.get(stmt);

        final boolean subTreesCouldBeRepl = replTheorems != null;
        final boolean comOper = comTreorem != null;

        if (!comOper && !subTreesCouldBeRepl)
            return null; // We could do nothing with this node!

        Transformation resTr = null;

        final int length = node.getChild().length;
        final ParseNode[] origChildren = node.getChild();
        ParseNode resNode = new ParseNode(stmt);

        if (subTreesCouldBeRepl)
            // Now we could reconstruct subtrees!
            for (int i = 0; i < length; i++) {
                final Theorem replTheorem = replTheorems[i];
                if (replTheorem == null)
                    // We can't transform this sub-tree
                    continue;

                // Get sub-node transformation:
                final Transformation subTr = getCanonicalForm(origChildren[i]);

                if (subTr == null)
                    // We should not do any transformations
                    continue;

                final ParseNode prevVersion = resNode;
                resNode = prevVersion.cloneWithoutChildren();

                // Fill the next child
                resNode.getChild()[i] = subTr.canonResult;

                // Construct the next step of this node transformation:
                final Stmt eqStmt = replTheorem.getExprParseTree().getRoot()
                    .getStmt();
                final ParseNode eqRoot = new ParseNode(eqStmt);
                final ParseNode[] eqChildren = {prevVersion, resNode};
                eqRoot.setChild(eqChildren);
                final Transformation eqTr = new Transformation(resNode, eqRoot);

                // Add subtree transformation:
                final Transformation trStep = concatTrs(eqTr, subTr);

                // Update the full transformation:
                resTr = concatTrs(resTr, trStep);

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
                final Stmt eqStmt = comTreorem.getExprParseTree().getRoot()
                    .getStmt();
                final ParseNode eqRoot = new ParseNode(eqStmt);
                final ParseNode[] eqChildren = {prevVersion, resNode};
                eqRoot.setChild(eqChildren);
                final Transformation eqTr = new Transformation(resNode, eqRoot);
                resTr = concatTrs(resTr, eqTr);
            }
        }
        return resTr;
    }
    /**
     * @param first The one operand
     * @param second The other operand
     * @return -1(less), 0(equal),1(greater)
     */
    private static int compareNodes(final ParseNode first,
        final ParseNode second)
    {

        return 0;
    }

    // ------------Additional functions--------------

    private static boolean isVarNode(final ParseNode node) {
        return isVarStmt(node.getStmt());
    }

    private static boolean isVarStmt(final Stmt stmt) {
        return stmt instanceof VarHyp;
    }
}
