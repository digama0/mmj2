//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  ParseTree.java  0.06 03/01/2008
 *
 *  ==> 6-Jan-2006: added maxDepth for Proof Assistant.
 *
 *  Version 0.04 -- 04/01/2006
 *      --> Added  deepCloneApplyingAssrtSubst() for ProofWorksheet
 *          (on behalf of mmj.pa.ProofUnifier).
 *
 *  Version 0.05 -- 08/01/2007
 *      --> Modified deepCloneApplyingAssrtSubst() to maintain
 *          workVarList instead of keeping count of "dummy"
 *          objects inserted.
 *
 *  Version 0.06 -- 03/01/2008
 *      --> Clone deepCloneApplyingAssrtSubst() so that
 *          argument "workVarList" is not needed.
 */

package mmj.lang;
import java.util.*;

/**
 *  A simple tree structure to hold a ParseNode root.
 */
public class ParseTree {

    private ParseNode root;

    /**
     *  Max depth used in Proof Assistant to determine
     *  if a candiate Assrt is unsuitable for unification:
     *  if the candidate Assrt's maximum depth is greater
     *  than the parse tree of the statement to be unified
     *  with it, then we know that unification is impossible.
     *
     *  The number is computed dynamically if and when
     *  someone asks for it (see below in setMaxDepth(),
     *  getMaxDepth() and calcMaxDepth()).
     */
    private int maxDepth          = -1;

    /**
     *  Computed value for Parse Tree containing no
     *  VarHyps in Levels one or two consisting of
     *  the level one and level two statement labels
     *  concatenated with intervening space characters.
     *
     *  levelOneTwo is used by Proof Assistant to
     *  reject unsuitable unification candidates
     *  as quickly as possible. The heuristic is
     *  that a proof step parse tree formula must
     *  overlay a candidate assertion's proof tree,
     *  and must match, except where the candidate
     *  assertion has a VarHyp (the VarHyp's provide
     *  the points where substitution takes place,
     *  a sub-tree from the proof step is substituted
     *  for the VarHyp!)
     *
     *  The value is computed dynamically if and when
     *  someone asks for it (see below in setLevelOneTwo(),
     *  and resetLevelOneTwo() and calcMaxDepth()).
     */
    private String levelOneTwo    = null;

    /**
     *  Constructor - default, creates empty ParseTree.
     */
    public ParseTree() {
    }

    /**
     *  Constructor - creates ParseTree using a ParseNode root.
     */
    public ParseTree(ParseNode root) {
        this.root = root;
    }

    /**
     *  Return root of tree.
     *
     *  @return ParseNode root of tree.
     */
    public ParseNode getRoot() {
        return root;
    }

    /**
     *  Set root of tree.
     *
     *  @param root of tree.
     */
    public void setRoot(ParseNode root) {
        this.root = root;
        resetMaxDepth();
        resetLevelOneTwo();
    }

    /**
     *  Check ParseTree array for duplicates.
     *  <p>
     *  No longer used, except in BottomUpParser.
     *
     *  @param parseCount number of trees in array.
     *  @param parseTreeArray array of trees.
     *
     *  @return true if duplicate found, else false.
     */
    public boolean isDup(int         parseCount,
                         ParseTree[] parseTreeArray) {
        //search in reverse, perhaps most recent more likely dup?
        for (--parseCount; parseCount >= 0; parseCount--) {
            if (isDup(parseTreeArray[parseCount])) {
                return true;
            }
        }
        return false;
    }

    /**
     *  Check another ParseTree for duplication.
     *  <p>
     *  No longer used, except in BottomUpParser.
     *
     *  @param parseTree tree to compare.
     *
     *  @return true if duplicate, else false.
     */
    public boolean isDup(ParseTree parseTree) {

        boolean deepDup = false;

        if (root == null) {
            if (parseTree.root == null) {
                deepDup = true;
            }
        }
        else {
            deepDup = root.isDeepDup(parseTree.root);
        }
        return deepDup;
    }

    /**
     *  (Deep) Clone a ParseTree.
     *
     *  @return true if duplicate, else false.
     */
    public ParseTree deepClone() {
        return new ParseTree(root.deepClone());
    }

    /**
     *  (Deep) Clone a ParseTree while substituting a given node
     *  with another.
     *
     *  @param matchNode node to replace.
     *  @param substNode new node to substitute.
     *
     *  @return new ParseTree.
     */
    public ParseTree deepCloneWNodeSub(ParseNode matchNode,
                                       ParseNode substNode) {

        return new ParseTree(root.deepCloneWNodeSub(matchNode,
                                                    substNode));
    }

    /**
     *  (Deep) Clone a ParseTree while substituting a set of
     *  VarHyp substitutions specified by a parallel Hyp array
     *  and keeping count of the number of dummy VarHyp
     *  substitutions.
     *  <p>
     *  This function is a helper for mmj.pa.ProofUnifier.
     *
     *  @param assrtHypArray parallel array for assrtSubst
     *  @param assrtSubst array of ParseNode sub-tree roots
     *                    specifying hyp substitutions.
     *  @param workVarList arrayList of WorkVar updated to contain
     *                       set of Work Vars used in the subtree.
     *                       substituted into the output.
     *
     *  @return new ParseTree.
     */
    public ParseTree deepCloneApplyingAssrtSubst(
                                    Hyp[]       assrtHypArray,
                                    ParseNode[] assrtSubst,
                                    ArrayList   workVarList) {
        return new ParseTree(
                    root.deepCloneApplyingAssrtSubst(
                        assrtHypArray,
                        assrtSubst,
                        workVarList));
    }

    /**
     *  (Deep) Clone a ParseTree while substituting a set of
     *  VarHyp substitutions specified by a parallel Hyp array.
     *  <p>
     *
     *  @param assrtHypArray parallel array for assrtSubst
     *  @param assrtSubst array of ParseNode sub-tree roots
     *                    specifying hyp substitutions.
     *
     *  @return new ParseTree.
     */
    public ParseTree deepCloneApplyingAssrtSubst(
                                    Hyp[]       assrtHypArray,
                                    ParseNode[] assrtSubst) {
        return new ParseTree(
                    root.deepCloneApplyingAssrtSubst(
                        assrtHypArray,
                        assrtSubst));
    }


    /**
     *  (Deep) Clone a ParseTree while substituting a set of
     *  Work Var updates.
     *  <p>
     *  This function is a helper for mmj.pa.ProofUnifier.
     *
     *
     *  @return new ParseTree.
     */
    public ParseTree deepCloneApplyingWorkVarUpdates() {
        return new ParseTree(
                    root.deepCloneApplyingWorkVarUpdates());
    }






    /**
     *  Finds first VarHyp within parseTree.root.child[i].
     *  <p>
     *  This feat is useful for GrammarRule parse trees, which
     *  contain at most one VarHyp per root.child[i].
     *
     *  @param childIndex index into ParseNode.child array.
     *
     *  @return first VarHyp ParseNode at/under child[i].
     */
    public ParseNode findChildVarHypNode(int childIndex) {
        ParseNode[] child = root.getChild();
        return child[childIndex].findFirstVarHypNode();
    }


    /**
     *  Build a ParseTree from an RPN Stmt array.
     *  <p>
     *  Note: A Metamath proof may contain a "?" to indicate a
     *        missing step. Missing steps are stored in
     *        mmj.lang.Theorem.proof[] as a "null" stmt reference.
     *        --> if rpn[i] == null, throw IllegalArgumentException
     *
     *  @param rpn Stmt array, may be parse or proof RPN.
     *
     *  @return ParseTree if rpn is valid.
     *
     *  @throws LangException if RPN is invalid (no plan at
     *          this moment to try to squeeze structure out of an
     *          incomplete or invalid RPN/Proof):
     */
    public static ParseTree convertRPNtoParseTree(Stmt[] rpn) {

        int       stmtPosInRPN = rpn.length - 1;

        ParseTree outParseTree = new ParseTree(new ParseNode());

        stmtPosInRPN = outParseTree.root.loadParseNodeFromRPN(
                                            rpn,
                                            stmtPosInRPN);
        if (stmtPosInRPN != 0) {
            throw new IllegalArgumentException(
                LangConstants.ERRMSG_RPN_CONV_TO_TREE_FAILURE);
        }

        return outParseTree;
    }

    /**
     *  Convert ParseTree to RPN Stmt array.
     *  <p>
     *  Notes on algorithm:
     *  <ol>
     *  <li>We don't know the number of nodes
     *  in the tree in advance, so to generate a Java array
     *  of the proper size, we can either count, allocate and
     *  load, or load into a resizeable list structure then
     *  allocate and copy when the size is known. Pre-counting
     *  means traversing all of the nodes twice and then loading
     *  the array in reverse order (the top node must be at the
     *  end of the list). BUT there is overhead associated with
     *  resizeable lists and with the copy operation. Overall,
     *  the difference between the two methods is probably not
     *  much. So let's try pre-counting since the count logic
     *  can be separated and reused.
     *  <li>we also don't know whether the ParseTree is a Syntax
     *  Parse Tree or a Proof Parse Tree, or even whether or not
     *  the ParseTree is "valid". That is not the concern of this
     *  routine, however.
     *  </ol>
     *
     *  @return RPN Stmt array.
     */
    public Stmt[] convertToRPN() {
        Stmt[] outRPN = new Stmt[countParseNodes()];
        if (root == null) {
            return outRPN;
        }

        int    dest   = outRPN.length - 1;

        dest          = root.convertToRPN(outRPN,
                                          dest);
        if (dest != -1) {
            throw new IllegalStateException(
                LangConstants.ERRMSG_TREE_CONV_TO_RPN_FAILURE
                + (dest * -1));
        }
        return outRPN;
    }



    /**
     *  Count parse nodes in a ParseTree.
     *  <p>
     *  If root is null, count = zero.
     *
     *  @return number of parse nodes in ParseTree.
     */
    public int countParseNodes() {

        if (root == null) {
            return 0;
        }
        return root.countParseNodes();
    }

    /**
     *  Gets the maximum depth of the tree after
     *  calculating it if the answer is not already
     *  known.
     *
     *  @return maximum depth of the parse tree
     */
    public int getMaxDepth() {
        if (maxDepth == -1) {
            setMaxDepth(root.calcMaxDepth());
        }
        return maxDepth;
    }

    /**
     *  Sets the maximum depth of the tree.
     *
     *  @return maximum depth of the parse tree
     */
    private void setMaxDepth(int maxDepth) {
        this.maxDepth             = maxDepth;
    }

    /**
     *  Resets the maximum depth of the tree to the
     *  initial value.
     */
    public void resetMaxDepth() {
        maxDepth                  = -1;
    }

    /**
     *  Computes the levelOneTwo string (key) value
     *  after calculating it if the answer is not
     *  already known.
     *
     *  @return levelOneTwo key string.
     */
    public String getLevelOneTwo() {
        if (levelOneTwo != null) {
            return levelOneTwo;
        }
        Stmt stmt                 = root.getStmt();
        StringBuffer answer       = new StringBuffer();
        if (!stmt.isVarHyp()) {
            answer.append(stmt.getLabel());
            answer.append(' ');
            ParseNode[] child     = root.getChild();
            if (child.length > 0) {
                int i             = 0;
                while (true) {
                    stmt          = child[i].getStmt();
                    if (stmt.isVarHyp()) {
                        setLevelOneTwo("");
                        return levelOneTwo;
                    }
                    answer.append(stmt.getLabel());
                    if (++i < child.length) {
                        answer.append(' ');
                        continue;
                    }
                    break;
                }
                setLevelOneTwo(answer.toString());
            }
            else {
                setLevelOneTwo("");
            }
        }
        else {
            setLevelOneTwo("");
        }
        return levelOneTwo;
    }

    /**
     *  Sets the levelOneTwo string key value.
     *
     *  @param levelOneTwo string key.
     */
    private void setLevelOneTwo(String levelOneTwo) {
        this.levelOneTwo          = levelOneTwo;
    }

    /**
     *  Resets the levelOneTwo string key value to the
     *  default value;
     */
    public void resetLevelOneTwo() {
        levelOneTwo               = null;
    }

    /**
     *  Converts a ParseTree to a String consisting of Stmt
     *  labels in RPN order.
     *  <p>
     *  Note: this can fail if the ParseTree is invalid. Intended
     *        for testing/diagnostic use.
     *
     *  @return String containing ParseTree statement labels in RPN
     *          order.
     */
    public String toString() {
        Stmt[] rpn = convertToRPN();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rpn.length; i++) {
            sb.append(rpn[i].getLabel());
            sb.append(" ");
        }
        return new String(sb);
    }
}
