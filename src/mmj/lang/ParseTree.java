//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ParseTree.java  0.06 03/01/2008
 *
 * ==> 6-Jan-2006: added maxDepth for Proof Assistant.
 *
 * Version 0.04 -- 04/01/2006
 *     --> Added  deepCloneApplyingAssrtSubst() for ProofWorksheet
 *         (on behalf of mmj.pa.ProofUnifier).
 *
 * Version 0.05 -- 08/01/2007
 *     --> Modified deepCloneApplyingAssrtSubst() to maintain
 *         workVarList instead of keeping count of "dummy"
 *         objects inserted.
 *
 * Version 0.06 -- 03/01/2008
 *     --> Clone deepCloneApplyingAssrtSubst() so that
 *         argument "workVarList" is not needed.
 */

package mmj.lang;

import java.util.*;

import mmj.pa.Serializer;

/**
 * A simple tree structure to hold a ParseNode root.
 */
public class ParseTree {

    private ParseNode root;

    /**
     * Max depth used in Proof Assistant to determine if a candiate Assrt is
     * unsuitable for unification: if the candidate Assrt's maximum depth is
     * greater than the parse tree of the statement to be unified with it, then
     * we know that unification is impossible. The number is computed
     * dynamically if and when someone asks for it (see below in setMaxDepth(),
     * getMaxDepth() and calcMaxDepth()).
     */
    private int maxDepth = -1;

    /**
     * Computed value for Parse Tree containing no VarHyps in Levels one or two
     * consisting of the level one and level two statement labels concatenated
     * with intervening space characters. levelOneTwo is used by Proof Assistant
     * to reject unsuitable unification candidates as quickly as possible. The
     * heuristic is that a proof step parse tree formula must overlay a
     * candidate assertion's proof tree, and must match, except where the
     * candidate assertion has a VarHyp (the VarHyp's provide the points where
     * substitution takes place, a sub-tree from the proof step is substituted
     * for the VarHyp!) The value is computed dynamically if and when someone
     * asks for it (see below in setLevelOneTwo(), and resetLevelOneTwo() and
     * calcMaxDepth()).
     */
    private String levelOneTwo = null;

    /**
     * Constructor - default, creates empty ParseTree.
     */
    public ParseTree() {}

    /**
     * Constructor - creates ParseTree using a ParseNode root.
     *
     * @param root the root node
     */
    public ParseTree(final ParseNode root) {
        this.root = root;
    }

    /**
     * Return root of tree.
     *
     * @return ParseNode root of tree.
     */
    public ParseNode getRoot() {
        return root;
    }

    /**
     * Set root of tree.
     *
     * @param root of tree.
     */
    public void setRoot(final ParseNode root) {
        this.root = root;
        resetMaxDepth();
        resetLevelOneTwo();
    }

    /**
     * Check ParseTree array for duplicates.
     * <p>
     * No longer used, except in BottomUpParser.
     *
     * @param parseCount number of trees in array.
     * @param parseTreeArray array of trees.
     * @return true if duplicate found, else false.
     */
    public boolean isDup(int parseCount, final ParseTree[] parseTreeArray) {
        // search in reverse, perhaps most recent more likely dup?
        for (--parseCount; parseCount >= 0; parseCount--)
            if (isDup(parseTreeArray[parseCount]))
                return true;
        return false;
    }

    /**
     * Check another ParseTree for duplication.
     * <p>
     * No longer used, except in BottomUpParser.
     *
     * @param parseTree tree to compare.
     * @return true if duplicate, else false.
     */
    public boolean isDup(final ParseTree parseTree) {

        boolean deepDup = false;

        if (root == null) {
            if (parseTree.root == null)
                deepDup = true;
        }
        else
            deepDup = root.isDeepDup(parseTree.root);
        return deepDup;
    }

    /**
     * (Deep) Clone a ParseTree.
     *
     * @return true if duplicate, else false.
     */
    public ParseTree deepClone() {
        return new ParseTree(root.deepClone());
    }

    /**
     * (Deep) Clone a ParseTree while substituting a given node with another.
     *
     * @param matchNode node to replace.
     * @param substNode new node to substitute.
     * @return new ParseTree.
     */
    public ParseTree deepCloneWNodeSub(final ParseNode matchNode,
        final ParseNode substNode)
    {

        return new ParseTree(root.deepCloneWNodeSub(matchNode, substNode));
    }

    /**
     * (Deep) Clone a ParseTree while substituting a set of VarHyp substitutions
     * specified by a parallel Hyp array and keeping count of the number of
     * dummy VarHyp substitutions.
     * <p>
     * This function is a helper for mmj.pa.ProofUnifier.
     *
     * @param assrtHypArray parallel array for assrtSubst
     * @param assrtSubst array of ParseNode sub-tree roots specifying hyp
     *            substitutions.
     * @param workVarList arrayList of WorkVar updated to contain set of Work
     *            Vars used in the subtree. substituted into the output.
     * @return new ParseTree.
     */
    public ParseTree deepCloneApplyingAssrtSubst(final Hyp[] assrtHypArray,
        final ParseNode[] assrtSubst, final List<WorkVar> workVarList)
    {
        return new ParseTree(root.deepCloneApplyingAssrtSubst(assrtHypArray,
            assrtSubst, workVarList));
    }

    /**
     * (Deep) Clone a ParseTree while substituting a set of VarHyp substitutions
     * specified by a parallel Hyp array.
     *
     * @param assrtHypArray parallel array for assrtSubst
     * @param assrtSubst array of ParseNode sub-tree roots specifying hyp
     *            substitutions.
     * @return new ParseTree.
     */
    public ParseTree deepCloneApplyingAssrtSubst(final Hyp[] assrtHypArray,
        final ParseNode[] assrtSubst)
    {
        return new ParseTree(
            root.deepCloneApplyingAssrtSubst(assrtHypArray, assrtSubst));
    }

    /**
     * (Deep) Clone a ParseTree while substituting a set of Work Var updates.
     * <p>
     * This function is a helper for mmj.pa.ProofUnifier.
     *
     * @return new ParseTree.
     */
    public ParseTree deepCloneApplyingWorkVarUpdates() {
        return new ParseTree(root.deepCloneApplyingWorkVarUpdates());
    }

    /**
     * Finds first VarHyp within parseTree.root.child[i].
     * <p>
     * This feat is useful for GrammarRule parse trees, which contain at most
     * one VarHyp per root.child[i].
     *
     * @param childIndex index into ParseNode.child array.
     * @return first VarHyp ParseNode at/under child[i].
     */
    public ParseNode findChildVarHypNode(final int childIndex) {
        return root.child[childIndex].findFirstVarHypNode();
    }

    /**
     * Build a ParseTree from an RPN Stmt array.
     * <p>
     * Note: A Metamath proof may contain a "?" to indicate a missing step.
     * Missing steps are stored in mmj.lang.Theorem.proof[] as a "null" stmt
     * reference. --> if rpn[i] == null, throw IllegalArgumentException
     *
     * @param rpn Stmt array, may be parse or proof RPN.
     * @throws IllegalArgumentException if ParseTree cannot be built from the
     *             RPN (null statment or RPN incomplete.)
     */
    public ParseTree(final RPNStep[] rpn) {
        final Deque<ParseNode> stack = new ArrayDeque<>();
        final List<ParseNode> backrefs = new ArrayList<>();
        for (final RPNStep s : rpn)
            if (s != null && s.stmt == null && s.backRef > 0)
                stack.push(backrefs.get(s.backRef - 1));
            else {
                int len = 0;
                Stmt stmt = null;
                if (s != null && (stmt = s.stmt) != null
                    && stmt instanceof Assrt)
                    len = stmt.getMandHypArrayLength();
                if (stack.size() < len)
                    throw new IllegalArgumentException(new LangException(
                        LangConstants.ERRMSG_RPN_INVALID_NOT_ENOUGH_STMTS));
                final ParseNode node = new ParseNode(stmt);
                node.child = new ParseNode[len];
                for (int i = len - 1; i >= 0; i--)
                    node.child[i] = stack.pop();
                stack.push(node);
                if (s != null && s.backRef < 0)
                    backrefs.add(node);
            }

        if (stack.size() != 1)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_RPN_CONV_TO_TREE_FAILURE));
        root = stack.pop();
    }
    /**
     * Convert ParseTree to RPN Stmt array.
     * <p>
     * Notes on algorithm:
     * <ol>
     * <li>We don't know the number of nodes in the tree in advance, so to
     * generate a Java array of the proper size, we can either count, allocate
     * and load, or load into a resizeable list structure then allocate and copy
     * when the size is known. Pre-counting means traversing all of the nodes
     * twice and then loading the array in reverse order (the top node must be
     * at the end of the list). BUT there is overhead associated with resizeable
     * lists and with the copy operation. Overall, the difference between the
     * two methods is probably not much. So let's try pre-counting since the
     * count logic can be separated and reused.
     * <li>we also don't know whether the ParseTree is a Syntax Parse Tree or a
     * Proof Parse Tree, or even whether or not the ParseTree is "valid". That
     * is not the concern of this routine, however.
     * </ol>
     *
     * @return RPN Stmt array.
     */
    public RPNStep[] convertToRPNExpanded() {
        final RPNStep[] outRPN = new RPNStep[countParseNodes(true)];
        if (root == null)
            return outRPN;

        final int dest = root.convertToRPNExpanded(outRPN, 0);
        if (dest != outRPN.length)
            throw new IllegalStateException(
                new LangException(LangConstants.ERRMSG_TREE_CONV_TO_RPN_FAILURE,
                    outRPN.length - dest));
        return outRPN;
    }
    /**
     * Compresses ("squishes") the tree to re-use repeated subtrees.
     *
     * @return this object
     */
    public ParseTree squishTree() {
        if (root != null)
            root.squishTree(new ArrayList<ParseNode>());
        return this;
    }

    public RPNStep[] convertToRPN() {
        return convertToRPN(true);
    }

    public RPNStep[] convertToRPN(final boolean pressLeaf) {
        if (root != null)
            return root.convertToRPN(pressLeaf);
        return new RPNStep[0];
    }

    /**
     * Stores backreference information in "packed" and "compressed" formats.
     * <p>
     * In packed format, proof steps come in three different flavors: unmarked
     * (for example {@code syl}) and marked (for example {@code 5:syl}), as well
     * as backreference steps (just written as numbers i.e. {@code 5}). In
     * compressed format the same rules apply, except it is harder to read.
     * Marked steps get a {@code Z} after the number, and backreferences are
     * numbers larger than the statement list. In our format, which most closely
     * resembles the packed format, unmarked steps have a valid {@code stmt} and
     * {@code backRef = 0}. Marked steps have a valid {@code stmt} and
     * {@code backRef < 0}: the value of {@code -backRef} is the 1-based index
     * of this marked step in a list of all marked steps (this is <i>not</i> the
     * RPN index).
     * <p>
     * For backreference steps, {@code backRef > 0} and {@code stmt} is
     * {@code null}. In this case, {@code backRef} is the index of the marked
     * step that is being referenced. (One other special case is {@code ?}
     * steps: in this case {@code stmt} is {@code null} and {@code backRef = 0}
     * .)
     */
    public static class RPNStep {
        public Stmt stmt;
        public int backRef;

        public RPNStep(final Stmt stmt) {
            this.stmt = stmt;
        }

        public RPNStep(final Map<String, Stmt> stmtTbl, final String str) {
            final String[] split = str.split(":", 2);
            if (split.length > 1) {
                stmt = stmtTbl.get(split[1]);
                backRef = -Integer.parseInt(split[0]);
            }
            else if (!str.equals("?")) {
                stmt = stmtTbl.get(str);
                if (stmt == null)
                    try {
                        backRef = Integer.parseInt(str);
                    } catch (final NumberFormatException e) {}
            }
        }

        @Override
        public String toString() {
            if (backRef < 0)
                return -backRef + ":" + stmt;
            else if (backRef == 0)
                return stmt == null ? "?" : stmt.toString();
            else
                return Integer.toString(backRef);
        }

        public static Serializer<RPNStep> serializer(
            final Map<String, Stmt> stmtTbl)
        {
            return Serializer.of(o -> new RPNStep(stmtTbl, (String)o),
                RPNStep::toString);
        }
    }

    /**
     * Count parse nodes in a ParseTree.
     * <p>
     * If root is null, count = zero.
     *
     * @param expanded true to count repeated subtrees multiple times, false to
     *            count them as size 1 stubs in subsequent occurrences
     * @return number of parse nodes in ParseTree.
     */
    public int countParseNodes(final boolean expanded) {

        if (root == null)
            return 0;
        return root.countParseNodes(expanded);
    }

    /**
     * Gets the maximum depth of the tree after calculating it if the answer is
     * not already known.
     *
     * @return maximum depth of the parse tree
     */
    public int getMaxDepth() {
        if (maxDepth == -1)
            setMaxDepth(root.calcMaxDepth());
        return maxDepth;
    }

    /**
     * Sets the maximum depth of the tree.
     *
     * @param maxDepth maximum depth of the parse tree
     */
    private void setMaxDepth(final int maxDepth) {
        this.maxDepth = maxDepth;
    }

    /**
     * Resets the maximum depth of the tree to the initial value.
     */
    public void resetMaxDepth() {
        maxDepth = -1;
    }

    /**
     * Computes the levelOneTwo string (key) value after calculating it if the
     * answer is not already known.
     *
     * @return levelOneTwo key string.
     */
    public String getLevelOneTwo() {
        if (levelOneTwo != null)
            return levelOneTwo;
        Stmt stmt = root.stmt;
        if (stmt instanceof VarHyp)
            levelOneTwo = "";
        else {
            String answer = stmt.getLabel();
            for (final ParseNode child : root.child) {
                stmt = child.stmt;
                if (stmt instanceof VarHyp)
                    return levelOneTwo = "";
                answer += " " + stmt.getLabel();
            }
            levelOneTwo = answer;
        }
        return levelOneTwo;
    }

    /**
     * Resets the levelOneTwo string key value to the default value;
     */
    public void resetLevelOneTwo() {
        levelOneTwo = null;
    }

    /**
     * Converts a ParseTree to a String consisting of Stmt labels in RPN order.
     * <p>
     * Note: this can fail if the ParseTree is invalid. Intended for
     * testing/diagnostic use.
     *
     * @return String containing ParseTree statement labels in RPN order.
     */
    @Override
    public String toString() {
        final RPNStep[] rpn = convertToRPN();
        final StringBuilder sb = new StringBuilder();
        for (final RPNStep element : rpn)
            sb.append(element).append(" ");
        return sb.toString();
    }

    public static Serializer<ParseTree> serializer(
        final Map<String, Stmt> stmtTbl)
    {
        final Serializer<RPNStep[]> ser = RPNStep.serializer(stmtTbl)
            .array(RPNStep[]::new);
        return Serializer.of(o -> new ParseTree(ser.deserialize(o)),
            tree -> ser.serialize(tree.convertToRPN()));
    }
}
