//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/*
 *  ParseNode.java  0.09 03/01/2008
 *
 *  Sep-30-2005: use getMandHypArrayLength() instead of
 *               getMandHypArray().
 *
 *  Jan-6-2006:  - added calcMaxDepth() for Proof Assistant.
 *               - added new non-recursive isDeepDup() and
 *                  unifyWithSubtree().
 *  Mar-5-2006:  - misc. comments.
 *               - unifyWithSubtree() has hardcoded msg needs
 *                 fixin'.
 *  Aug-27-2006: - added renderParsedSubExpr() for TMFF project
 *
 *  Jun-01-2007: - version 0.06
 *      --> efficiency and logic tweaks in
 *          mmj.lang.ParseNode.java#unifyWithSubtree() and
 *          mmj.lang.ParseNode.java#isDeepDup()
 *
 *  Aug-1-2007  - version 0.07
 *      --> misc. Work Var Enhancements:
 *          -- added cloneNodeToUseWorkVars()
 *          -- changed "stmt" and "child" to public
 *             because the inconvenience is intolerable.
 *          -- Modified deepCloneApplyingAssrtSubst() to maintain
 *             workVarList instead of keeping count of "dummy"
 *             objects inserted.
 *
 *  Feb-1-2008  - version 0.08
 *          -- Add convertToRPN() version of converting
 *             just a sub-tree (not an entire tree)
 *             to RPN.
 *
 *  Apr-1-2008 -- version 0.09
 *          -- Clone deepCloneApplyingAssrtSubst()
 *             so that input variable "workVarList" is
 *             optional, and if null will not be used.
 */

package mmj.lang;
import  java.util.ArrayList;

/**
 *  Parse Node is an n-way tree node for Metamath Stmt trees.
 *  <p>
 *  ParseNode is dual use, and provides the tree node structure for
 *  grammatical/syntax parses -- these parse trees can be
 *  readily converted to Reverse Polish Notation (see Stmt.exprRPN)
 *  -- and for proof trees, which are normally stored in Metamath
 *  .mm files in RPN format but can easily be converted to
 *  ParseTrees.
 *  <p>
 *  The difference between
 *  proof and grammatical parse trees is content
 *  and purpose, there is no structural difference.
 *  <p>
 *  Proof step Stmt references are not restricted to Syntax
 *  Axiom and Variable Hypotheses, and can contain references
 *  to Logical Hypotheses, Logical Axioms and other
 *  Theorems. Logical Axioms and Theorems have
 *  Frames (see MandFrame and OptFrame) whose hypArrays
 *  contain Variable Hypotheses AND Logical Hypotheses,
 *  therefore there may be more children nodes under an
 *  Assertion's ParseNode than there are under a Syntax
 *  Axiom's ParseNode.
 *
 *  Both proof parse trees and grammatical parse trees
 *  are required to reproduce/generate the original
 *  Statement's Formula's Expression (the 2nd through
 *  nth Symbols of the Formula), but a Proof is, in
 *  addition required to produce the entire Formula,
 *  which includes the Type Code and the Expression.
 *  <p>
 *  Thus, the difference between a proof and a grammatical
 *  parse is that a proof derives a theorem's Formula
 *  from Axioms with the same Type Code as the statement
 *  being proved; a grammatical parse is not under this
 *  obligation (it must generate some Type Code however!)
 *  <p>
 *  Also:
 *  <ul>
 *  <li>Grammatical parse trees contain Stmt references
 *  to either Syntax Axioms or Variable Hypotheses. The
 *  children of a Syntax Axiom (which is an Assrt) are
 *  either VarHyp's or Syntax Axioms. The number of
 *  children of a Syntax Axiom node is equal to the
 *  size of the Syntax Axiom's Assrt.varHypArray -- which
 *  for Syntax Axioms is the same as the Syntax Axiom's
 *  Assrt.MandFrame.hypArray varHyp, because a Syntax
 *  Axiom has no Logical Hypotheses.</li>
 *
 *  <li>Proof Trees contain Stmt references to Assertions
 *  (Assrt, which includes Axiom and Theorem),
 *  and Hypotheses (Hyp, which includes VarHyp and LogHyp.)
 *  The children of an Assertion Proof Tree Node correspond
 *  to the Assrt.MandFrame.hypArray, one to one in database
 *  sequence. The children of a Hypothesis Proof Tree node
 *  depend on whether the Hypothesis is a Logical Hypothesis
 *  (LogHyp) or a Variable Hypothesis (VarHyp); if a LogHyp,
 *  then the children correspond to the LogHyp.varHypArray,
 *  while if a VarHyp there is only one child (even a VarHyp
 *  node represents an Expression for substitution in the
 *  proof and may have children of its own -- for example,
 *  a VarHyp sub-node of a ProofTree may actually contain
 *  a Stmt reference to "wi" where the "wi" statement has its
 *  own VarHyp's.)</li>
 *
 */

public class ParseNode {

    /**
     *  Stmt object reference, either a Hyp or an Assrt.
     */
    public         Stmt            stmt;

    /**
     *  Child node array of length 0 to n.
     */
    public         ParseNode[]     child;

    /**
     *  Default constructor.
     */
    public ParseNode() {
    }

    /**
     *  Construct with a Stmt.
     *
     *  @param stmt a proof step.
     */
    public ParseNode(Stmt stmt) {
        this.stmt = stmt;
        child     = null;
    }

    /**
     *  Construct using a VarHyp.
     *
     *  @param varHyp a proof or parse step.
     */
    public ParseNode(VarHyp varHyp) {
        this.stmt = varHyp;
        this.child = new ParseNode[0];
    }

    /**
     *  Return ParseNode's Stmt.
     *
     *  @return stmt (could be null depending on the context).
     */
    public Stmt getStmt() {
        return stmt;
    }

    /**
     *  Set ParseNode's Stmt.
     *
     *  @param stmt a proof or parse step.
     */
    public void setStmt(Stmt stmt) {
        this.stmt = stmt;
    }


    /**
     *  Return ParseNode's Child ParseNode array.
     *
     *  @return child ParseNode array.
     */
    public ParseNode[] getChild() {
        return child;
    }

    /**
     *  Return ParseNode's Child ParseNode array.
     *
     *  @param child ParseNode array.
     */
    public void setChild(ParseNode[] child) {
        this.child = child;
    }

    /**
     *  Unify an input parse subtree (expression) with
     *  this subtree and return an array of substitions
     *  if successful, or null.
     *
     *  Using fixed size work stacks here, set to 1000
     *  in Proof Assistant. Hard to imagine exceeding
     *  the size so we'll accept the possibility of
     *  array out-of-bounds exception and just recompile
     *  if needed (for now...
     *  see mmj.pa.PaConstants.UNIFIER_NODE_STACK_SIZE ).
     *
     *  @param subtreeRoot root of parse subtree to unify with this
     *  @param varHypArray the VarHyp's in this subtree
     *  @param unifyNodeStack fixed size work stack
     *  @param compareNodeStack fixed size work stack
     *
     *  @return array of subtrees that represent substitutions
     *          for the corresponding VarHyps in the input
     *          varHypArray (may contain nulls).
     */
    public ParseNode[] unifyWithSubtree(
                            ParseNode   subtreeRoot,
                            VarHyp[]    varHypArray,
                            ParseNode[] unifyNodeStack,
                            ParseNode[] compareNodeStack) {

        unifyNodeStack[0]         = this;
        unifyNodeStack[1]         = subtreeRoot;

        int         stackCnt      = 2;

        ParseNode[] substArray    =
            new ParseNode[varHypArray.length];

        ParseNode   myNode;
        ParseNode   subtreeNode;

        stackLoop: while (stackCnt > 0) {
            subtreeNode           = unifyNodeStack[--stackCnt];
            myNode                = unifyNodeStack[--stackCnt];

            if (myNode.stmt != subtreeNode.stmt) {
                if (!myNode.stmt.isVarHyp() ||
                    myNode.stmt.getTyp() !=
                    subtreeNode.stmt.getTyp()) {
                    //mismatch and/or myNode not VarHyp
                    return null;
                }
                //ok, subst syntax node/VarHyp for VarHyp if Typ equal
            }
            else {
                if (myNode.stmt.isVarHyp()) {
                    //ok, a vacuous, subst VarHyp x for Varhyp x!
                }
                else { //ok, matching syntax nodes
                    for (int i = (myNode.child.length - 1);
                         i >= 0;
                         i--) {
                        unifyNodeStack[stackCnt++]
                                      = myNode.child[i];
                        unifyNodeStack[stackCnt++]
                                      = subtreeNode.child[i];
                    }
                    continue stackLoop;
                }
            }

            //ok, accum the subst while checking for (erroneous)
            //two different subst values for a single VarHyp
            for (int i = 0; i < varHypArray.length; i++) {
                if (varHypArray[i] == myNode.stmt) {
                    if (substArray[i] == null) {
                        substArray[i]
                                  = subtreeNode;
                    }
                    else {
                        if (!substArray[i].isDeepDup(
                                            subtreeNode,
                                            compareNodeStack)) {
                            return null; //bad subst, 2 diff values
                        }
                    }
                    continue stackLoop; //next!!!
                }
            }

            //if we're here then we messed up!
            StringBuffer testSB = new StringBuffer();
            for (int i = 0; i < varHypArray.length; i++) {
                testSB.append(varHypArray[i].getLabel());
                testSB.append(' ');
            }
            throw new IllegalArgumentException(
                LangConstants.ERRMSG_UNIFY_SUBST_HYP_NOTFND_1
                + myNode.stmt.getLabel()
                + LangConstants.ERRMSG_UNIFY_SUBST_HYP_NOTFND_2
                + testSB.toString());
        }
        return substArray;
    }

    /**
     *  Determine if sub-tree is a duplicate of this sub-tree.
     *
     *  This is the non-recursive version of isDeepDup(),
     *  which can be used equivalently instead.
     *
     *  @param that ParseNode to compare to this ParseNode.
     *  @param compareNodeStack fixed length work stack for
     *                          ParseNodes.
     *
     *  @return true if the sub-trees have identical contents.
     */
    public boolean isDeepDup(ParseNode   that,
                             ParseNode[] compareNodeStack) {

        ParseNode myNode          = this;
        ParseNode thatNode        = that;

        int stackCnt              = 0;
        int i;
        while (thatNode             != null                  &&
               myNode.stmt          == thatNode.stmt         &&
               myNode.child.length  == thatNode.child.length) {

            i                     = myNode.child.length - 1;
            if (i < 0) {
                if (stackCnt <= 0) {
                    return true;
                }
                thatNode          =
                    compareNodeStack[--stackCnt];
                myNode            =
                    compareNodeStack[--stackCnt];
            }
            else {
                while (i > 0) {
                    compareNodeStack[stackCnt++]
                                  = myNode.child[i];
                    compareNodeStack[stackCnt++]
                                  = thatNode.child[i];
                    --i;
                }
                thatNode          = thatNode.child[0];
                myNode            = myNode.child[0];
            }
        }
        return false;
    }

    /**
     *  Determine if sub-tree is a duplicate of this sub-tree.
     *
     *  @param that ParseNode to compare to this ParseNode.
     *
     *  @return true if the sub-trees have identical contents.
     */
    public boolean isDeepDup(ParseNode that) {
        if (that         == null      ||
            stmt         != that.stmt ||
            child.length != that.child.length) {
            return false;
        }
        for (int i = 0; i < child.length; i++) {
            if (!child[i].isDeepDup(that.child[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Clones a ParseNode while substituting for any child
     *  nodes that match the corresponding matchNode array
     *  node.
     *
     *  see GrammarRule.buildGrammaticalParseNode
     *  for an example. (this is kind of ugly to look at!)
     *
     *  @param matchNode ParseNode array of matching ParseNodes.
     *  @param expandedReseqParam ParseNodeHolder array of
     *          nodes to splice in to the original tree where
     *          a matchNode is found.
     *
     *  @return deep clone of original node with substituted
     *          child nodes.
     */
    public ParseNode deepCloneWithGrammarHypSubs(
                            ParseNode[]       matchNode,
                            ParseNodeHolder[] expandedReseqParam) {
        ParseNode out     = new ParseNode(stmt);
        out.child         = new ParseNode[expandedReseqParam.length];
        for (int i = 0; i < out.child.length; i++) {
            if (expandedReseqParam[i] == null) {
                out.child[i] = child[i].deepClone();
            }
            else {
                out.child[i] =
                    child[i].deepCloneWNodeSub(
                        matchNode[i],
                        expandedReseqParam[i].parseNode);
            }
        }
        return out;
    }

    /**
     *  Clones a sub-tree and splices in a substitution
     *  node when a given "matchNode" is found.
     *  <p>
     *  Basically, this is used to splice an expression's
     *  sub-tree into a VarHyp sub-tree. Note that the
     *  original tree is on top, so we're layering another
     *  formula on top, with substituted expressions
     *  replacing the formula's hypotheses.
     *
     *  @param matchNode ParseNode to look for and replace.
     *  @param substNode ParseNode to replace matchNode.
     *
     *  @return output ParseNode, unchanged or substituted.
     */
    public ParseNode deepCloneWNodeSub(ParseNode matchNode,
                                       ParseNode substNode) {
        if (this == matchNode) {
            return substNode;
        }
        ParseNode out = new ParseNode(stmt);
        if (child != null) {
            out.child = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++) {
                out.child[i] = child[i].deepCloneWNodeSub(matchNode,
                                                          substNode);
            }
        }
        return out;
    }


    /**
     *  Deep clone of a ParseNode sub-tree.
     *
     *  @return ParseNode sub-tree matching the original's
     *          content.
     */
    public ParseNode deepClone() {
        ParseNode out = new ParseNode();
        out.stmt      = stmt;
        out.child     = new ParseNode[child.length];
        for (int i = 0; i < child.length; i++) {
            out.child[i] = child[i].deepClone();
        }
        return out;
    }

    /**
     *  (Deep) Clone a ParseNode while substituting a set of
     *  VarHyp substitutions specified by a parallel Hyp array
     *  and keeping track of the Work Vars output.
     *  <p>
     *  This function is a helper for mmj.pa.ProofUnifier
     *  and its friend mmj.pa.ProofWorksheet.
     *
     *  @param assrtHypArray parallel array for assrtSubst
     *  @param assrtSubst array of ParseNode sub-tree roots
     *                    specifying hyp substitutions.
     *  @param workVarList arrayList of WorkVar updated to contain
     *                       set of Work Vars used in the subtree.
     *                       substituted into the output.
     *
     *  @return new ParseNode.
     */
    public ParseNode deepCloneApplyingAssrtSubst(
                                    Hyp[]       assrtHypArray,
                                    ParseNode[] assrtSubst,
                                    ArrayList   workVarList) {
        if (!stmt.isVarHyp()) {
            ParseNode out         = new ParseNode(stmt);
            out.child             = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++) {
                out.child[i]      =
                    child[i].deepCloneApplyingAssrtSubst(
                        assrtHypArray,
                        assrtSubst,
                        workVarList);
            }
            return out;
        }

        for (int i = 0; i < assrtHypArray.length; i++) {
            if (assrtHypArray[i] == stmt) {
                assrtSubst[i].
                    accumSetOfWorkVarsUsed(
                        workVarList);
                return assrtSubst[i];
            }
        }

        throw new IllegalArgumentException(
            LangConstants.ERRMSG_ASSRT_SUBST_HYP_NOTFND_1
            + stmt.getLabel());
    }

    /**
     *  (Deep) Clone a ParseNode while substituting a set of
     *  VarHyp substitutions specified by a parallel Hyp array.
     *
     *  @param assrtHypArray parallel array for assrtSubst
     *  @param assrtSubst array of ParseNode sub-tree roots
     *                    specifying hyp substitutions.
     *
     *  @return new ParseNode.
     */
    public ParseNode deepCloneApplyingAssrtSubst(
                                    Hyp[]       assrtHypArray,
                                    ParseNode[] assrtSubst) {
        if (!stmt.isVarHyp()) {
            ParseNode out         = new ParseNode(stmt);
            out.child             = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++) {
                out.child[i]      =
                    child[i].deepCloneApplyingAssrtSubst(
                        assrtHypArray,
                        assrtSubst);
            }
            return out;
        }

        for (int i = 0; i < assrtHypArray.length; i++) {
            if (assrtHypArray[i] == stmt) {
                return assrtSubst[i];
            }
        }

        throw new IllegalArgumentException(
            LangConstants.ERRMSG_ASSRT_SUBST_HYP_NOTFND_1
            + stmt.getLabel());
    }




    /**
     *  Finds *first* VarHyp node in a sub-tree.
     *  <p>
     *  This is useful mainly for GrammarRule parse trees which
     *  have at most one VarHyp per ParseTree.root.child[i].
     *
     *  @return first ParseNode among sub-trees of a ParseNode's
     *          children that is a VarHyp.
     */
    public ParseNode findFirstVarHypNode() {
        if (stmt.isVarHyp()) {
            return this;
        }
        ParseNode out;
        for (int i = 0; i < child.length; i++) {
            out = child[i].findFirstVarHypNode();
            if (out != null) {
                return out;
            }
        }
        return null;
    }

    /**
     *  Calculates the maximum depth of a parse sub-tree.
     *
     *  @return maximum depth of parse sub-tree
     */
    public int calcMaxDepth() {
        if (stmt.isWorkVarHyp()) {
            return 0;
        }
        int childMaxDepth         = 0;
        int childDepth;
        for (int i = 0; i < child.length; i++) {
            if ((childDepth       = child[i].calcMaxDepth())
                 > childMaxDepth) {
                childMaxDepth = childDepth;
            }
        }
        return childMaxDepth + 1;
    }


    /**
     *  Counts nodes in a ParseNode sub-tree.
     */
    public int countParseNodes() {
        int n = 1;
        if (child != null) {
            for (int i = 0; i < child.length; i++) {
                n += child[i].countParseNodes();
            }
        }
        return n;
    }

    /**
     *  Converts a sub-tree expression to Reverse Polish Notation.
     *  <p>
     *  Intended for creating the RPN for an expression
     *  rather than an entire formula (with ParseTree).
     *
     *  @return RPN Stmt array.
     */
    public Stmt[] convertToRPN() {

        Stmt[] outRPN = new Stmt[countParseNodes()];

        int    dest   = outRPN.length - 1;

        dest          = convertToRPN(outRPN,
                                     dest);
        if (dest != -1) {
            throw new IllegalStateException(
                LangConstants.ERRMSG_SUBTREE_CONV_TO_RPN_FAILURE
                + (dest * -1));
        }
        return outRPN;
    }

    /**
     *  Converts a sub-tree to Reverse Polish Notation.
     *  <p>
     *  Works in reverse order.
     *
     *  @param outRPN Stmt Array where RPN will be stored.
     *  @param dest location in output array to write the
     *         next Stmt reference.
     *
     *  @return dest of *next* output Stmt array item.
     */
    public int convertToRPN(Stmt[] outRPN,
                            int    dest) {
        outRPN[dest--] = stmt;

        if (child != null) {
            for (int i = (child.length - 1); i >= 0; i--) {
                dest = child[i].convertToRPN(outRPN,
                                             dest);
            }
        }

        return dest;
    }

    /**
     *  Builds a ParseNode sub-tree from a Stmt array in RPN format.
     *
     *  @param rpn Stmt array in RPN format.
     *  @param stmtPosInRPN location of next Stmt in array
     *
     *  @return location of next Stmt in array
     *
     *  @throws LangException if ParseTree cannot be built from the
     *          RPN (null statment or RPN incomplete.)
     */
    public int loadParseNodeFromRPN(Stmt[] rpn,
                                    int    stmtPosInRPN) {
        if ((stmt = rpn[stmtPosInRPN]) == null) {
            throw new IllegalArgumentException(
                LangConstants.ERRMSG_PARSED_RPN_INCOMPLETE +
                    stmtPosInRPN);
        }

        int nbrChildNodes = stmt.getMandHypArrayLength();

        child = new ParseNode[nbrChildNodes];

        for (int i = (nbrChildNodes - 1); i >= 0; i--) {
            if ((--stmtPosInRPN) < 0) {
                throw new IllegalArgumentException(
                    LangConstants.ERRMSG_RPN_INVALID_NOT_ENOUGH_STMTS
                    );
            }
            child[i]     = new ParseNode();
            stmtPosInRPN =
                child[i].loadParseNodeFromRPN(rpn,
                                              stmtPosInRPN);
        }
        return stmtPosInRPN;
    }

    /**
     *  Converts a parse sub-tree into a sub-expression
     *  which is output into a String Buffer.
     *  <p>
     *  Note: this will not work for a proof node! The
     *        ParseNode's stmt must be a VarHyp or
     *        a Syntax Axiom.
     *  <p>
     *  The output sub-expression is generated into
     *  text not to exceed the given maxLength. If the
     *  number of output characters exceeds maxLength
     *  output terminates after tidying StringBuffer.
     *  <p>
     *  The depth of the sub-tree is checked against
     *  the input maxDepth parameter, and if the depth
     *  exceeds this number, output terminates after
     *  tidying StringBuffer.
     *  <p>
     *  Depth is computed as 1 for each Notation Syntax
     *  Axiom Node. VarHyp nodes and Nulls Permitted,
     *  Type Conversion and NamedTypedConstant Syntax
     *  Axiom nodes are assigned depth = 0 for purposes of
     *  depth checking.
     *  <p>
     *  @param sb            StringBuffer already initialized
     *                       for appending characters.
     *
     *  @param maxDepth      maximum depth of Notation Syntax
     *                       axioms in sub-tree to be printed.
     *                       Set to Integer.MAX_VALUE to turn
     *                       off depth checking.
     *
     *  @param maxLength     maximum length of output
     *                       sub-expression.
     *                       Set to Integer.MAX_VALUE to turn
     *                       off depth checking.
     *
     *  @return length of sub-expression characters
     *          appended to the input StringBuffer --
     *          or -1 if maxDepth or maxLength exceeded.
     */
    public int renderParsedSubExpr(StringBuffer sb,
                                   int          maxDepth,
                                   int          maxLength) {

        int rollbackToLength      = sb.length();
        int outputSubExprLength   =
                stmt.renderParsedSubExpr(sb,
                                         maxDepth,
                                         maxLength,
                                         child);
        if (outputSubExprLength < 0) {
            sb.setLength(rollbackToLength);
        }

        return outputSubExprLength;
    }

    /**
     *  Deep clone of a ParseNode sub-tree converting
     *  Target Variables to Source Variables.
     *  <p>
     *  Note: this is used by UnifyWorkManager and only
     *        for "raw" substitutions which means that
     *        any variables in the tree being cloned are,
     *        by definition, target variables; we do not
     *        expect any of them to be work variables,
     *        and furthermore, we expect all of them
     *        to have assigned values.
     *
     *  @return ParseNode sub-tree converted to use WorkVarHyps.
     */
    public ParseNode cloneTargetToSourceVars() {

        ParseNode out             = new ParseNode();

        if (stmt.isVarHyp()) {
            ParseNode vHNode      = ((VarHyp)stmt).paSubst;
            if (vHNode == null) {
                throw new IllegalArgumentException(
                    LangConstants.
                        ERRMSG_NULL_TARGET_VAR_HYP_PA_SUBST_1);
            }
            out.stmt              = vHNode.stmt;
            out.child             = vHNode.child;
        }
        else {
            out.stmt              = stmt;
            out.child             = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++) {
                out.child[i]      =
                    child[i].cloneTargetToSourceVars();
            }
        }
        return out;
    }

    /**
     *      Check to see if or how the input searchWorkVarHyp
     *      occurs within the current ParseNode stmt and its
     *      subtree and any substitutions made to VarHyps via
     *      paSubst.
     *      <p>
     *      Note that this function is only called if the
     *      searchWorkVarHyp.paSubst == null (which
     *      means we are considering assigning a substitution).
     *      to it. AND note that it is called only if the
     *      currentNode.stmt.isWorkVarHyp().
     *      <p>
     *      The reason for this hokeyness is that set.mm
     *      contains loops of renames, such as &W1 := & W3 := &W1,
     *      etc. Not to mention &W1 := &W2 := &W3 := &W1.
     *      THESE are "ok" as long as the subtree depth
     *      remains 1 (e.g. not &W1 := ( &W2 -> &W1 ).
     *
     * @param  searchWorkVarHyp the object of the search.
     * @return occurs Type:
     *              0 = no occurrences,
     *              1 = occurs in error (invalid loop)
     *             -1 = valid rename
     */
    public int checkWorkVarHasOccursIn(
                            WorkVarHyp searchWorkVarHyp) {

        if (stmt.isWorkVarHyp()) {

            Stmt targetStmt       =
                checkWorkVarHasOccursInValidRename(
                    searchWorkVarHyp);

            if (targetStmt == null) {
                return LangConstants.WV_OCCURS_IN_NOT_AT_ALL;
            }

            if (targetStmt == searchWorkVarHyp) {
                return LangConstants.WV_OCCURS_IN_RENAME_LOOP;
            }

            // else...found a "->" or "ph" or other non-WorkVarHyp

        }
        if (hasOccursIn(searchWorkVarHyp)) {
            return LangConstants.WV_OCCURS_IN_ERROR;
        }

        else {
            return LangConstants.WV_OCCURS_IN_NOT_AT_ALL;
        }
    }

    private Stmt checkWorkVarHasOccursInValidRename(
                                     WorkVarHyp searchWorkVarHyp) {
        if (stmt == searchWorkVarHyp     //valid rename
                ||
            !stmt.isWorkVarHyp()) {      //bogus
            return stmt;
        }

        if (((VarHyp)stmt).paSubst == null) {
            return null;                 // found nothing
        }

        return ((VarHyp)stmt).
                    paSubst.
                        checkWorkVarHasOccursInValidRename(
                            searchWorkVarHyp);
    }

    /**
     *  Looks for an occurrence of a given WorkVarHyp within
     *  a subtree.
     *  <p>
     *  This is used in UnifyWorkManager to perform the
     *  "occurs in" check used by Robinson's unification
     *  algorithm.
     *  <p>
     *  @param searchStmt is what we are looking for.
     *  @return true iff input searchStmt found in subtree.
     */
    private boolean hasOccursIn(WorkVarHyp searchWorkVarHyp) {
        if (searchWorkVarHyp == stmt) {
            return true;
        }
        if (stmt.isWorkVarHyp()) {
            if (((VarHyp)stmt).paSubst != null) {
                return (((VarHyp)stmt).paSubst.
                            hasOccursIn(searchWorkVarHyp));
            }
            return false;
        }
        for (int i = 0; i < child.length; i++) {
            if (child[i].hasOccursIn(searchWorkVarHyp)) {
                return true;
            }
        }
        return false;
    }


    /**
     *  Returns true if subtree contains a WorkVar
     *  which has a non-null assigned substitution update.
     *  <p>
     *  @return true if subtree contains an updated WorkVar.
     */
    public boolean hasUpdatedWorkVar() {
        if (stmt.isVarHyp()) {
            if (stmt.isWorkVarHyp()) {
                if (((VarHyp)stmt).paSubst != null) {
                    return true;
                }
            }
        }
        else {
            for (int i = 0; i < child.length; i++) {
                if (child[i].hasUpdatedWorkVar()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     *  Clone subtree replacing any updated Work Vars
     *  with clones of their updating subtrees.
     *
     *  @return cloned subtree containing no Work Vars
     *          which have updates.
     */
    public ParseNode cloneResolvingUpdatedWorkVars() {

        if (stmt.isVarHyp()) {

            if (((VarHyp)stmt).isWorkVarHyp()) {

                if (((VarHyp)stmt).paSubst != null) {

                    return ((VarHyp)stmt).
                               paSubst.
                                   cloneResolvingUpdatedWorkVars();
                }
            }
            return  new ParseNode((VarHyp)stmt);
        }
        else {

            ParseNode out         = new ParseNode(stmt);
            out.child             = new ParseNode[child.length];
            for (int i = 0; i < child.length; i++) {

                out.child[i]      =
                    child[i].cloneResolvingUpdatedWorkVars();
            }
            return out;
        }
    }

    /**
     *  Updates an ArrayList to maintain a set of Work Vars
     *  used in a subtree.
     *
     *  @param workVarList ArrayList of WorkVar objects in subtree.
     */
    public void accumSetOfWorkVarsUsed(ArrayList workVarList) {
        if (stmt.isWorkVarHyp()) {
            Var v                 = ((VarHyp)stmt).getVar();
            for (int i = 0; i < workVarList.size(); i++) {
                if (workVarList.get(i) == v) {
                    return;
                }
            }
            workVarList.add(v);
        }
        else { // note: child.length == 0 for VarHyp nodes...
            for (int i = 0; i < child.length; i++) {
                child[i].
                    accumSetOfWorkVarsUsed(
                        workVarList);
            }
        }
    }

    /**
     *  Updates an ArrayList to maintain a set of Var Hyps
     *  used in a subtree.
     *
     *  @param varHypList ArrayList of VarHyp objects in subtree.
     */
    public void accumVarHypUsedListBySeq(ArrayList varHypList) {
        if (stmt.isVarHyp()) {
            ((VarHyp)stmt).
                accumVarHypListBySeq(
                    varHypList);
        }
        else { // note: child.length == 0 for VarHyp nodes...
            for (int i = 0; i < child.length; i++) {
                child[i].
                    accumVarHypUsedListBySeq(
                        varHypList);

            }
        }
    }

    /**
     *  Accumulate Var Hyps used in the subtree
     *  which are also in an input list of Var Hyps.
     *  <p>
     *  Lists are maintained without duplicates and are
     *  in ascending database sequence.
     *  <p>
     *  Note: in ProofUnifier this is used to accumulate
     *  a list of optional variables that are in use
     *  in a proof.
     *  <p>
     *  @param varHypList ArrayList of Var Hyps being sought
     *                 for accumulation.
     *  @param varHypInUseList ArrayList of Var Hyps accumulated
     *                 which are in the formula and are
     *                 in the input varList.
     */
    public void accumListVarHypUsedListBySeq(
                                ArrayList varHypList,
                                ArrayList varHypInUseList) {

        VarHyp vH;
        if (stmt.isVarHyp()) {
            vH                    = (VarHyp)stmt;
            if (vH.containedInVarHypListBySeq(varHypList)) {
                vH.accumVarHypListBySeq(varHypInUseList);
            }
        }
        else { // note: child.length == 0 for VarHyp nodes...
            for (int i = 0; i < child.length; i++) {
                child[i].
                    accumListVarHypUsedListBySeq(
                        varHypList,
                        varHypInUseList);
            }
        }
    }


    /**
     *  (Deep) Clone a ParseNode while applying updates to
     *  WorkVars.
     *  <p>
     *  This function is a helper for mmj.pa.ProofUnifier
     *  and its friend mmj.pa.ProofWorksheet.
     *
     *  @return new ParseNode subtree.
     */
    public ParseNode deepCloneApplyingWorkVarUpdates() {

        if (stmt.isWorkVarHyp() &&
            ((VarHyp)stmt).paSubst != null) {
            return ((VarHyp)stmt).paSubst;
        }

        ParseNode out         = new ParseNode(stmt);
        out.child             = new ParseNode[child.length];
        for (int i = 0; i < child.length; i++) {
            out.child[i]      =
                child[i].deepCloneApplyingWorkVarUpdates();
        }
        return out;
    }

}
