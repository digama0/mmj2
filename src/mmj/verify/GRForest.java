/*
  File: GRForest.java cloned from RBTree.java

  Originally written by Doug Lea and released into the public domain.
  Thanks for the assistance and support of Sun Microsystems Labs, Agorics
  Inc, Loral, and everyone contributing, testing, and using this code.

  History:
  Date     Who                What
  24Sep95  dl@cs.oswego.edu   Create from collections.java  working file
  13Oct95  dl                 Changed protection statuses

  http://gee.cs.oswego.edu/dl/classes/collections/RBTree.java
*/
//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * @(#)GRForest.java  0.02 08/28/2005
 */

package mmj.verify;

import mmj.lang.*;
import java.util.*;



/**
 *
 * RedBlack trees.
 * @author Doug Lea
 * @version 0.93
 *
 * <P> For an introduction to this package see <A HREF="index.html"> Overview </A>.
 */

/**
 *  GRForest is a collection of static functions that
 *  operate on GRNode Trees.
 *  <p>
 *  Each tree's root is stored in mmj.lang.Cnst.gRRoot(dot).
 *  <p>
 *  A tree contains every Notation Grammar Rule that begins
 *  with the gRRoot's Cnst. Each leaf node has a reference (pointer)
 *  to a sub-tree, and so on. This enables all Rules whose
 *  expressions begin with "(" to share a common root.
 *  <p>
 *  The purpose of GRForest and GRNode was to provide a
 *  searchable repository of NotationRule information,
 *  and it is used for that purpose in mmj.verify.BottomUpParser.java.
 *  However, the Bottom Up algorithm is to inefficient for
 *  Metamath's set.mm and has been "deprecated" (to put it
 *  politely), in favor of mmj.verify.EarleyParser.java.
 *
 *  So, GRForest and GRNode are teetering on obsolescence
 *  and could be removed from without too much trouble.
 *  The only use for them still is checking for duplicate
 *  Rule expressions, and there is obviously a cheaper
 *  way to do that than with these things...(I'm guessing
 *  that since each GrammarRule carries its own expression
 *  that the hash code for each rule could be pre-computed
 *  and even stored, if desired, and then used as the
 *  key to a HashSet. Bada bing, bada boom.)
 *
 *  Therefore, I am not going to document these any further.
 *
 */
public class GRForest {

    /*
     *  findCnstSubSeq -- searches GrNode tree/subtree down
     *  through the "forest" of trees. Each element of the
     *  input Cnst array represents a level of the forest,
     *  with ruleFormatExpr[0] in the top tree -- the matching
     *  GRNode contains a link down to the root of the tree
     *  in the level below, which represents the Cnst values
     *  in a ruleFormatExpr[1] that begin with the parent level's
     *  Cnst. In other words, each tree node can point to a tree
     *  below it -- as well as having left/right/parent/child
     *  pointers to GRNodes within the current level's tree.
     */
    public static GRNode findCnstSubSeq(GRNode root,
                                        Cnst[] ruleFormatExpr,
                                        int    seqNext,
                                        int    seqLast) {
        GRNode match = null;
        while (true) {
            if (root == null ||
               (match = root.find(ruleFormatExpr[seqNext])) == null) {
                return null;
            }
            if (++seqNext > seqLast) {
                return match;
            }
            root = match.elementDownLevelRoot();
        }
    }

    /*
     *  findLen1CnstNotationRule -- searches GrNode tree for a
     *  length of 1 Notation Rule matching the input Cnst.
     */
    public static NotationRule findLen1CnstNotationRule(GRNode root,
                                                        Cnst   cnst) {
        if (root == null) {
            return null;
        }

        GRNode match = root.find(cnst);
        if (match == null) {
            return null;
        }

        return match.elementNotationRule();
    }


    /*
     *  addNotationRule --
     *  <ol>
     *  <li>get root, blow up if helpful to consistency;</li>
     *  <li>find last element of input expr that has a
     *      node under the forest root;</li>
     *  <li>if last element of input expr is the
     *      final element of the array and it already has
     *      a NotationRule return the found node -- but if
     *      the NotationRule is null, update the rule with
     *      the input rule and return the found node.</li>
     *  <li>prepare a linked chain of all nodes that do
     *      not already exist and splice them into the forest,
     *      returning the last node to the caller.</li>
     *  </ol>
     *
     *  @return tailNode of chain -- if returned node's
     *          NotationRule differs from input, then
     *          the input is a duplicate (error); otherwise
     *          the returned node is new and good.
     */
    public static GRNode addNotationRule(Cnst[] ruleFormatExpr,
                                         NotationRule notationRule)
                        throws IllegalArgumentException {
        int                 prevLevel           = -1;
        int                 currLevel           = 0;
        GRNode              match               = null;
        GRNode              currLevelRoot       = null;
        GRNode              revCurrLevelRoot    = null;
        GRNode              prevLevelRoot       = null;
        GRNode              foundLevelNode      = null;

        if (ruleFormatExpr.length > 0) {
            currLevelRoot = ruleFormatExpr[currLevel].getGRRoot();
        }
        else {
            throw new IllegalArgumentException(
                GrammarConstants.ERRMSG_GRFOREST_EXPR_LENGTH_ZERO);
        }
        if (notationRule == null) {
            throw new IllegalArgumentException(
                GrammarConstants.ERRMSG_GRFOREST_RULE_NULL);
        }

        while (true) {
            if (currLevelRoot == null
                ||
               (foundLevelNode = currLevelRoot.find(
                                          ruleFormatExpr[currLevel]))
                    == null) {
                break;
            }
            match           = foundLevelNode;
            prevLevel       = currLevel;
            prevLevelRoot   = currLevelRoot;
            if (++currLevel >= ruleFormatExpr.length) {
                break;
            }
            currLevelRoot   = match.elementDownLevelRoot();
        }

        if (currLevel >= ruleFormatExpr.length) {
            NotationRule existingRule;
            if ((existingRule = match.elementNotationRule())
                 != null) {
                if (existingRule == notationRule) {
                    throw new IllegalArgumentException(
                        GrammarConstants.ERRMSG_GRFOREST_RULE_DUP);
                }
                return match;
            }
            match.elementNotationRule(notationRule);
            notationRule.setGRTail(match);
            return match;
        }

        /**
         * Situation: NO complete match found, SO we have either
         *            a partial match, on at least one node, or
         *            a totally null match. We'll need to
         *            compute the number of new nodes, create
         *            them and then splice them into the forest.
         *            Plus, we may have to update Cnst.gRRoot.
         */

        revCurrLevelRoot = GRForest.addToTree(
                                        ruleFormatExpr[currLevel],
                                        currLevelRoot);
        if (prevLevelRoot == null) {
            ruleFormatExpr[0].setGRRoot(revCurrLevelRoot);
        }
        else {
            match.elementDownLevelRoot(revCurrLevelRoot);
        }

        int nbrAddNodes = ruleFormatExpr.length;
        if (prevLevel != -1) {
            nbrAddNodes -= (prevLevel + 1);
        }
        GRNode[] addNodeArray = new GRNode[nbrAddNodes];

        addNodeArray[0] = revCurrLevelRoot.find(
                                          ruleFormatExpr[currLevel]);
        if (addNodeArray[0] == null) {
            throw new IllegalArgumentException(
                GrammarConstants.ERRMSG_GRFOREST_NODE_LOST);
        }

        addNodeArray[0].elementUpLevel(match);

        int src = currLevel + 1;
        int dest;
        for (dest = 1; dest < nbrAddNodes; dest++, src++) {
            addNodeArray[dest] = new GRNode(ruleFormatExpr[src]);
            addNodeArray[dest].elementUpLevel(
                                            addNodeArray[dest - 1]);
            addNodeArray[dest - 1].elementDownLevelRoot(
                                            addNodeArray[dest]);
        }

        dest = nbrAddNodes - 1;
        notationRule.setGRTail(addNodeArray[dest]);
        addNodeArray[dest].elementNotationRule(notationRule);

        return addNodeArray[dest];
    }


    protected static GRNode addToTree(Cnst    elementCnst,
                                      GRNode  oldRoot)
                                    throws IllegalArgumentException {
        if (oldRoot == null) {
            return new GRNode(elementCnst);
        }

        GRNode t = oldRoot; //insert oper can change the root!
        int    diff;
        while (true) {
            if ((diff = t.elementCnst().compareTo(elementCnst)) == 0) {
                throw new IllegalArgumentException(
                    "adding new node but already exists in tree!");
            }
            if (diff < 0) {
                if (t.left() != null) {
                    t = t.left();
                }
                else {
                    return t.insertLeft(new GRNode(elementCnst),
                                        oldRoot);
                }
            }
            else {
                if (t.right() != null) {
                    t = t.right();
                }
                else {
                    return t.insertRight(new GRNode(elementCnst),
                                         oldRoot);
                }
            }
        }
    }

    public static Collection getRuleCollection(GRNode root) {
        ArrayList ruleCollection = new ArrayList();
        if (root != null) {
            root.loadRuleCollection(ruleCollection);
        }
        return ruleCollection;
    }
}
