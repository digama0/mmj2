//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;
import mmj.lang.Stmt;

/** This tree represents associative structure */
public class AssocTree {
    public final int size;
    public final AssocTree[] subTrees;

    public AssocTree() {
        size = 1;
        subTrees = null;
    }

    public AssocTree(final AssocTree[] subTrees) {
        this.subTrees = subTrees;
        assert subTrees.length == 2;
        size = subTrees[0].size + subTrees[1].size;
    }

    public AssocTree(final int from, final AssocTree left, final AssocTree right)
    {
        assert from == 0 || from == 1;
        subTrees = from == 0 ? new AssocTree[]{left, right} : new AssocTree[]{
                right, left};
        size = left.size + right.size;
    }

    public AssocTree duplicate() {
        if (subTrees != null)
            return new AssocTree(new AssocTree[]{subTrees[0].duplicate(),
                    subTrees[1].duplicate()});
        else
            return new AssocTree();
    }

    @Override
    public String toString() {
        String res = "";
        res += size;

        if (subTrees != null)
            res += "[" + subTrees[0].toString() + "," + subTrees[1].toString()
                + "]";

        return res;
    }

    /**
     * This function calculates associative structure.
     *
     * @param curNode the input node
     * @param assocProp the property of associative node
     * @param info the work sheet info
     * @return the tree structure
     */
    public static AssocTree createAssocTree(final ParseNode curNode,
        final GeneralizedStmt assocProp, final WorksheetInfo info)
    {
        final Stmt stmt = curNode.stmt;
        final ParseNode[] childrent = curNode.child;

        final Stmt assocStmt = assocProp.stmt;

        if (stmt != assocStmt)
            return new AssocTree();

        final AssocTree[] subTrees = new AssocTree[2];

        for (int i = 0; i < 2; i++) {
            final ParseNode child = childrent[assocProp.varIndexes[i]];
            if (AssociativeInfo.isAssociativeWithProp(child, assocProp, info))
                subTrees[i] = createAssocTree(child, assocProp, info);
            else
                subTrees[i] = new AssocTree();
        }

        return new AssocTree(subTrees);
    }
}
