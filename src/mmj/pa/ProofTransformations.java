//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.pa;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mmj.lang.LogHyp;
import mmj.lang.Messages;
import mmj.lang.ParseNode;
import mmj.lang.ParseTree;
import mmj.lang.Stmt;
import mmj.lang.Theorem;
import mmj.lang.VarHyp;

/**
 * This contains information for possible automatic transformations.
 */
public class ProofTransformations {
    /** The list of binary equivalence operators */
    private Map<Stmt, Theorem> eqOperators;

    /** The list of binary equivalence operators */
    private Map<Stmt, Theorem> comOp;

    /** The list of statements with possible variable replace */
    private Map<Stmt, Theorem[]> replaceOp;

    /** Empty default constructor */
    public ProofTransformations() {}

    // ----------------------------

    public void prepareAutomaticTransformations(
        final List<Theorem> theoremList, final Messages messages)
    {
        findEquivalenceRules(theoremList, messages);
        findCommutativeRules(theoremList, messages);
        findAssociativeRules(theoremList, messages);
        findReplaceRules/**/(theoremList, messages);
    }

    private void findEquivalenceRules(final List<Theorem> theoremList,
        final Messages messages)
    {
        eqOperators = new HashMap<Stmt, Theorem>();

        // Try to find equivalence rules, like A = B <=> B = A
        // TODO: find implication rules also
        for (final Theorem theorem : theoremList) {
            final VarHyp[] varHypArray = theorem.getMandVarHypArray();
            final LogHyp[] logHyps = theorem.getLogHypArray();
            final ParseTree theoremTree = theorem.getExprParseTree();
            if (logHyps.length == 1) {
                final ParseTree hypTree = logHyps[0].getExprParseTree();

                if (varHypArray.length == 2
                    && hypTree.getMaxDepth() == 2
                    && theoremTree.getMaxDepth() == 2
                    && theoremTree.getRoot().getChild().length == 2
                    && hypTree.getRoot().getStmt() == theoremTree.getRoot()
                        .getStmt()
                    && hypTree.getRoot().getChild()[0].getStmt() == theoremTree
                        .getRoot().getChild()[1].getStmt()
                    && hypTree.getRoot().getChild()[1].getStmt() == theoremTree
                        .getRoot().getChild()[0].getStmt())
                {
                    messages.accumInfoMessage("I-DBG Equivalence theorems: %s",
                        theorem.toString());
                    eqOperators.put(theoremTree.getRoot().getStmt(), theorem);
                }
            }
        }
    }

    private void findCommutativeRules(final List<Theorem> theoremList,
        final Messages messages)
    {
        comOp = new HashMap<Stmt, Theorem>();

        // Find commutative rules
        for (final Theorem theorem : theoremList) {
            final VarHyp[] varHypArray = theorem.getMandVarHypArray();
            // final LogHyp[] logHyps = theorem.getLogHypArray();
            final ParseTree theoremTree = theorem.getExprParseTree();

            // if (theorem.toString().equals("addcomi"))
            // theorem.toString();

            // TODO: adds the support of theorems like addcomi

            // Find now commutative rules, like A + B = B + A
            if (/*logHyps.length == 0 &&*/varHypArray.length == 2
                && theoremTree.getMaxDepth() == 3
                && eqOperators.get(theoremTree.getRoot().getStmt()) != null)
            {
                final ParseNode[] subTrees = theoremTree.getRoot().getChild();

                // it is the equivalence rule
                assert subTrees.length == 2;

                if (subTrees[0].getStmt() == subTrees[1].getStmt()
                    && subTrees[0].getChild().length == 2
                    && subTrees[0].getChild()[0].getStmt() == subTrees[1]
                        .getChild()[1].getStmt()
                    && subTrees[0].getChild()[1].getStmt() == subTrees[1]
                        .getChild()[0].getStmt())
                {
                    messages.accumInfoMessage(
                        "I-DBG commutative theorems: %s: %s",
                        theorem.toString(), theorem.getFormula().toString());
                    comOp.put(theoremTree.getRoot().getStmt(), theorem);
                }
            }
        }
    }

    private void findReplaceRules(final List<Theorem> theoremList,
        final Messages messages)
    {
        replaceOp = new HashMap<Stmt, Theorem[]>();

        mainLoop: for (final Theorem theorem : theoremList) {
            theorem.getMandVarHypArray();
            final LogHyp[] logHyps = theorem.getLogHypArray();
            final ParseTree theoremTree = theorem.getExprParseTree();

            // TODO: logHyps could contains other hypotheses
            if (logHyps.length != 1)
                continue;

            // Maybe depth restriction could be weaken
            if (theoremTree.getMaxDepth() != 3)
                continue;

            if (eqOperators.get(theoremTree.getRoot().getStmt()) == null)
                continue;

            final LogHyp testHyp = logHyps[0];

            final ParseTree hypTree = testHyp.getExprParseTree();

            if (eqOperators.get(hypTree.getRoot().getStmt()) == null)
                continue;

            final ParseNode[] hypSubTrees = hypTree.getRoot().getChild();

            assert hypSubTrees.length == 2 : "it is the equivalence rule";

            if (!isVarNode(hypSubTrees[0]) || !isVarNode(hypSubTrees[1]))
                continue;

            final ParseNode[] subTrees = theoremTree.getRoot().getChild();

            assert subTrees.length == 2 : "it is the equivalence rule";

            if (subTrees[0].getStmt() != subTrees[1].getStmt())
                continue;

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
                        continue mainLoop;

                    // We found the replace
                    replPos = i;

                    // Check that it is actually the swap of two variables
                    for (int k = 0; k < 2; k++) {
                        final int m = (k + 1) % 2; // the other index
                        if (leftChild[i].getStmt() == hypSubTrees[k].getStmt()
                            && rightChild[i].getStmt() == hypSubTrees[m]
                                .getStmt())
                            continue replaceCheck;
                    }

                    continue mainLoop;
                }

            Theorem[] repl = replaceOp.get(stmt);

            if (repl == null) {
                repl = new Theorem[subTrees[0].getChild().length];
                replaceOp.put(stmt, repl);
            }

            // it is the first such theorem;
            if (repl[replPos] == null)
                repl[replPos] = theorem;

            messages.accumInfoMessage("I-DBG Replace theorems: %s: %s",
                theorem.toString(), theorem.getFormula().toString());

            // TODO: find rules in the form of implication!
        }
    }
    private void findAssociativeRules(final List<Theorem> theoremList,
        final Messages messages)
    {
        final Set<Stmt> assocOp = new HashSet<Stmt>();

        // Find now associative rules, like (A + B) + C = A + (B + C)
        for (final Theorem theorem : theoremList) {
            final VarHyp[] varHypArray = theorem.getMandVarHypArray();
            // final LogHyp[] logHyps = theorem.getLogHypArray();
            final ParseTree theoremTree = theorem.getExprParseTree();

            // if (theorem.toString().equals("addcomi"))
            // theorem.toString();

            // TODO: adds the support of theorems like addcomi

            if (/*logHyps.length == 0 &&*/varHypArray.length == 3
                && theoremTree.getMaxDepth() == 4
                && eqOperators.get(theoremTree.getRoot().getStmt()) != null)
            {
                final ParseNode[] subTrees = theoremTree.getRoot().getChild();

                // it is the equivalence rule
                assert subTrees.length == 2;

                if (subTrees[0].getStmt() == subTrees[1].getStmt()) {
                    final Stmt f = subTrees[0].getStmt();
                    // we need to find one of the 2 templates:
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

                        if (leftChild[j].getStmt() != rightChild[j].getChild()[j]
                            .getStmt())
                            continue;

                        if (leftChild[i].getChild()[i].getStmt() != rightChild[i]
                            .getStmt())
                            continue;

                        if (leftChild[i].getChild()[j].getStmt() != rightChild[j]
                            .getChild()[i].getStmt())
                            continue;

                        messages
                            .accumInfoMessage(
                                "I-DBG associative theorems: %s: %s", theorem
                                    .toString(), theorem.getFormula()
                                    .toString());
                        assocOp.add(theoremTree.getRoot().getStmt());
                    }
                }
            }
        }
    }

    private static boolean isVarNode(final ParseNode node) {
        return isVarStmt(node.getStmt());
    }

    private static boolean isVarStmt(final Stmt stmt) {
        return stmt instanceof VarHyp;
    }
}
