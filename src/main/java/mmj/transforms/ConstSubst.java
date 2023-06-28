//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;

public class ConstSubst {
    /** This array contains null elements. */
    public final ParseNode[] constMap;
    public final int hash;

    public ConstSubst(final ParseNode[] constMap) {
        this.constMap = constMap;
        hash = calcHashCode();
    }

    /**
     * This function should be used only during "info"'s initialization. Do not
     * use it during unification search! It creates constant substitution for
     * all constant nodes.
     *
     * @param node the input node
     * @return constructed result
     */
    public static ConstSubst createFromNode(final ParseNode node) {
        final ParseNode[] children = node.child;
        final ParseNode[] constMap = new ParseNode[children.length];
        for (int i = 0; i < children.length; i++)
            if (TrUtil.isConstNode(children[i]))
                constMap[i] = children[i];
        return new ConstSubst(constMap);
    }

    public boolean isEmpty() {
        for (final ParseNode node : constMap)
            if (node != null)
                return false;
        return true;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ConstSubst))
            return false;
        final ConstSubst that = (ConstSubst)obj;

        assert constMap.length == that.constMap.length;
        for (int i = 0; i < constMap.length; i++)
            if (constMap[i] != that.constMap[i]) {
                if (constMap[i] == null || that.constMap[i] == null)
                    return false;
                if (!constMap[i].isDeepDup(that.constMap[i]))
                    return false;
            }
        return true;
    }

    protected int calcHashCode() {
        int hash = 0;
        for (final ParseNode node : constMap)
            if (node != null)
                hash ^= node.deepHashCode();
        return hash;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    /**
     * @param candidate the candidate
     * @return true if the candidate has exactly the same constant map
     */
    public boolean isTheSameConstMap(final ParseNode candidate) {
        if (candidate.child.length != constMap.length)
            return false;

        for (int i = 0; i < constMap.length; i++) {
            final ParseNode child = candidate.child[i];
            if (TrUtil.isConstNode(child)) {
                if (constMap[i] == null)
                    return false;
                else if (!constMap[i].isDeepDup(child))
                    return false;
            }
            else if (constMap[i] != null)
                return false;

        }
        return true;
    }

    public int[] getVarPlace() {
        int curVarNum = 0;
        final int[] varPlace = new int[constMap.length];
        for (int i = 0; i < varPlace.length; i++)
            if (constMap[i] != null)
                varPlace[i] = -1;
            else
                varPlace[curVarNum++] = i;
        final int[] res = new int[curVarNum];
        for (int i = 0; i < curVarNum; i++)
            res[i] = varPlace[i];

        return res;
    }

    @Override
    public String toString() {
        String res = "[";
        for (int i = 0; i < constMap.length; i++) {
            if (i != 0)
                res += ", ";
            res += constMap[i];
        }
        res += "]";
        return res;
    }
}
