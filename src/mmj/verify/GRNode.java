/*
  File: GRNode.java cloned from RBCell.java

  Originally written by Doug Lea and released into the public domain.
  Thanks for the assistance and support of Sun Microsystems Labs,
  Agorics Inc, Loral, and everyone contributing, testing, and using
  this code.

  History:
  Date     Who                What
  24Sep95  dl@cs.oswego.edu   Create from collections.java  working file
  27sep97  dl@cs.oswego.edu   Kill Cell dependency; simplify code

  http://gee.cs.oswego.edu/dl/classes/collections/RBCell.java
*/

//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  GRNode.java  0.02 08/28/2005
 */

package mmj.verify;
import mmj.lang.*;
import java.util.*;


/*
 * GRNode implements basic capabilities of Red-Black trees,
 * an efficient kind of balanced binary tree. The particular
 * algorithms used are adaptations of those in Corman,
 * Lieserson, and Rivest's <EM>Introduction to Algorithms</EM>.
 * This class was inspired by (and code cross-checked with) a
 * similar class by Chuck McManis. The implementations of
 * rebalancings during insertion and deletion are
 * a little trickier than those versions:
 * Most standard accounts use
 * a special dummy `nil' node for such purposes, but that doesn't
 * work well in a possibly concurrent environment.
 * <P>
 * It is a pure implementation class. For harnesses, see:
 * @see RBTree
 * @author Doug Lea
 *
 * <P> For an introduction to this package see <A HREF="index.html"> Overview </A>.
 *
 */


/**
 *  GRNode -- see RBCell(dot)java by Doug Lea, public domain;
 *  clone that puppy, and added Up/Down fields
 *  to create hierarchical forest of Grammar Rule Trees.
 *  <p>
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
public class GRNode {
    static final boolean RED   = false;
    static final boolean BLACK = true;

    /*
     * The element held in the node
     */
    protected Cnst        elementCnst_;

    /*
     * My stuff follows...the happy little monkey modifies
     * a work of art :)
     */

    /*
     * NotationRule -- null if this GRNode is not a leaf node for
     *           a Grammar Rule. Note that GrammarRule has a
     *           reference back to this leaf node (e.g., delete a
     *           leaf node and the grammar rule must be deleted
     *           also!)
     */
    protected NotationRule elementNotationRule_;


    protected GRNode      elementUpLevel_;
    protected GRNode      elementDownLevelRoot_;

    /*
     * The node color (RED, BLACK)
     */
    protected boolean color_ = BLACK;

    /*
     * Pointer to left child
     */
    protected GRNode  left_ = null;

    /*
     * Pointer to right child
     */
    protected GRNode  right_ = null;

    /*
     * Pointer to parent (null if root)
     */
    protected GRNode  parent_ = null;

    /*
     * Make a new cell with given element, null links, and BLACK
     * color. Normally only called to establish a new root.
     */
    public GRNode(Cnst elementCnst) {
        elementCnst_ = elementCnst;
    }

    /*
     * Make a new cell with given element, null links, and BLACK color.
     * Normally only called to establish a new root.
     */
    public GRNode(Cnst         elementCnst,
                  NotationRule elementNotationRule,
                  GRNode       elementUpLevel,
                  GRNode       elementDownLevelRoot) {
        elementCnst_            = elementCnst;
        elementNotationRule_    = elementNotationRule;
        elementUpLevel_         = elementUpLevel;
        elementDownLevelRoot_   = elementDownLevelRoot;
    }

    /*
     *  loadRuleCollection --
     *  Add each GrammarRule in the current subtree to
     *  the ruleCollection. Order of adds is lowest
     *  within the current tree, and shortest rules first.
     *
     */
    public void loadRuleCollection(Collection ruleCollection) {
        GRNode x = leftmost();
        do {
            if (x.elementNotationRule_ != null) {
                ruleCollection.add(x.elementNotationRule_);
            }
            if (x.elementDownLevelRoot_ != null) {
                x.elementDownLevelRoot_.loadRuleCollection(
                    ruleCollection);
            }
        } while ((x = x.successor()) != null);
        return;
    }

    /*
     * return the element value
     */
    public final Cnst elementCnst() {
        return elementCnst_;
    }
    public final NotationRule elementNotationRule() {
        return elementNotationRule_;
    }
    public final GRNode elementUpLevel() {
        return elementUpLevel_;
    }
    public final GRNode elementDownLevelRoot() {
        return elementDownLevelRoot_;
    }


    /*
     * set the element value
     */
    public final void  elementCnst(Cnst v) {
        elementCnst_ = v;
    }
    public final void  elementNotationRule(NotationRule v) {
        elementNotationRule_ = v;
    }
    public final void  elementUpLevel(GRNode v) {
        elementUpLevel_ = v;
    }
    public final void  elementDownLevelRoot(GRNode v) {
        elementDownLevelRoot_ = v;
    }

    /*
     * Return left child (or null)
     */
    public final GRNode left() {
        return left_;
    }

    /*
     * Return right child (or null)
     */
    public final GRNode right() {
        return right_;
    }

    /*
     * Return parent (or null)
     */
    public final GRNode parent() {
        return parent_;
    }


    /*
     * Return color of node p, or BLACK if p is null
     */
    static boolean colorOf(GRNode p) {
        return (p == null)?
            BLACK :
            p.color_;
    }

    /*
     * return parent of node p, or null if p is null
     */
    static GRNode parentOf(GRNode p) {
        return (p == null)?
            null:
            p.parent_;
    }

    /*
     * Set the color of node p, or do nothing if p is null
     */
    static void setColor(GRNode p,
                         boolean c) {
        if (p != null) {
            p.color_ = c;
        }
    }

    /*
     * return left child of node p, or null if p is null
     */
    static GRNode leftOf(GRNode p) {
        return (p == null)?
            null:
            p.left_;
    }

    /*
     * return right child of node p, or null if p is null
     */
    static GRNode rightOf(GRNode p) {
        return (p == null)?
            null:
            p.right_;
    }


    /*
     * Copy all content fields from another node
     * Override this if you add any other fields in subclasses.
     *
     */
    protected void copyContents(GRNode t) {
        elementCnst_          = t.elementCnst_;
        elementNotationRule_  = t.elementNotationRule_;
        elementUpLevel_       = t.elementUpLevel_;
        elementDownLevelRoot_ = t.elementDownLevelRoot_;
    }


    /*
     * Return the minimum element of the current (sub)tree
     */
    public final GRNode leftmost() {
        GRNode p = this;
        for ( ;  p.left_ != null; p = p.left_) {
        }
        return p;
    }

    /*
     * Return the maximum element of the current (sub)tree
     */
    public final GRNode rightmost() {
        GRNode p = this;
        for ( ; p.right_ != null; p = p.right_) {
        }
        return p;
    }

    /*
     * Return the root (parentless node) of the tree
     */
    public final GRNode root() {
        GRNode p = this;
        for ( ; p.parent_ != null; p = p.parent_) {
        }
        return p;
    }

    /*
     * Return true if node is a root (i.e., has a null parent)
     */
    public final boolean isRoot() {
        return parent_ == null;
    }


    /*
     * Return the inorder successor, or null if no such
     */
    public final GRNode successor() {
        if (right_ != null) {
            return right_.leftmost();
        }
        else {
          GRNode p  = parent_;
          GRNode ch = this;
          while (p  != null   &&
                 ch == p.right_) {
              ch = p;
              p  = p.parent_;
          }
          return p;
        }
    }

    /*
     * Return the inorder predecessor, or null if no such
     */
    public final GRNode predecessor() {
        if (left_ != null) {
            return left_.rightmost();
        }
        else {
            GRNode p  = parent_;
            GRNode ch = this;
            while (p  != null  &&
                   ch == p.left_) {
                ch = p;
                p  = p.parent_;
            }
            return p;
        }
    }

    /*
     * Return the number of nodes in the subtree
     */
    public final int size() {
        int c = 1;
        if (left_ != null) {
            c += left_.size();
        }
        if (right_ != null) {
            c += right_.size();
        }
        return c;
    }


    /*
     * Return node of current subtree containing element as element(),
     * if it exists, else null.
     */
    public GRNode find(Cnst elementCnst) {
        GRNode t = this;
        int diff;
        for (;;) {
          if ((diff = t.elementCnst_.compareTo(elementCnst)) == 0) {
              return t;
          }
          else {
              if (diff < 0) {
                  t = t.left_;
              }
              else {
                  t = t.right_;
              }
          }
          if (t == null) return null;
        }
    }


    /*
     * Insert cell as the left child of current node, and then
     * rebalance the tree it is in.
     * @param cell the cell to add
     * @param root, the root of the current tree
     * @return the new root of the current tree. (Rebalancing
     * can change the root!)
     */
    public GRNode insertLeft(GRNode cell,
                             GRNode root) {
        left_        = cell;
        cell.parent_ = this;
        return cell.fixAfterInsertion(root);
    }

    /*
     * Insert cell as the right child of current node, and then
     * rebalance the tree it is in.
     * @param cell the cell to add
     * @param root, the root of the current tree
     * @return the new root of the current tree. (Rebalancing
     * can change the root!)
     */
    public GRNode insertRight(GRNode cell,
                              GRNode root) {
        right_       = cell;
        cell.parent_ = this;
        return cell.fixAfterInsertion(root);
    }


    /*
     *  From CLR
     */
    protected final GRNode rotateLeft(GRNode root) {
        GRNode r = right_;

        right_   = r.left_;
        if (r.left_ != null) {
            r.left_.parent_ = this;
        }

        r.parent_ = parent_;
        if (parent_ == null) {
            root = r;
        }
        else {
            if (parent_.left_ == this) {
                parent_.left_ = r;
            }
            else {
                parent_.right_ = r;
            }
        }
        r.left_ = this;
        parent_ = r;
        return root;
     }

    /*
     *  From CLR
     */
    protected final GRNode rotateRight(GRNode root) {
        GRNode l = left_;

        left_ = l.right_;
        if (l.right_ != null) {
            l.right_.parent_ = this;
        }

        l.parent_ = parent_;
        if (parent_ == null) {
            root = l;
        }
        else {
            if (parent_.right_ == this) {
                parent_.right_ = l;
            }
            else {
                parent_.left_ = l;
            }
        }
        l.right_ = this;
        parent_ = l;
        return root;
    }


    /*
     *  From CLR
     */
    protected final GRNode fixAfterInsertion(GRNode root) {
        color_   = RED;
        GRNode x = this;
        GRNode y;

        while (x                != null &&
               x                != root &&
               x.parent_.color_ == RED) {

            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {

                y = rightOf(parentOf(parentOf(x)));

                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        root = x.rotateLeft(root);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    if (parentOf(parentOf(x)) != null) {
                        root =
                            parentOf(parentOf(x)).rotateRight(root);
                    }
                }
            }

            else {

                y = leftOf(parentOf(parentOf(x)));

                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                }
                else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        root = x.rotateRight(root);
                    }
                    setColor(parentOf(x),  BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    if (parentOf(parentOf(x)) != null) {
                        root = parentOf(parentOf(x)).rotateLeft(root);
                    }
                }
            }
        }
        root.color_ = BLACK;
        return root;
    }
}
