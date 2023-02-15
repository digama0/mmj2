//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;

public class ParseNodeHashElem {
    /** node could be null */
    public final ParseNode node;

    private final int cachedHash;

    public ParseNodeHashElem(final ParseNode node) {
        this.node = node;
        if (node != null)
            cachedHash = node.deepHashCode();
        else
            cachedHash = 0;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ParseNodeHashElem))
            return false;
        final ParseNodeHashElem that = (ParseNodeHashElem)obj;
        if (node == that.node)
            return true;

        if (cachedHash != that.cachedHash)
            return false;

        if (node == null || that.node == null)
            return false;
        return node.isDeepDup(that.node);
    }

    @Override
    public int hashCode() {
        return cachedHash;
    }

    public boolean isEmpty() {
        return node == null;
    }

    @Override
    public String toString() {
        if (node == null)
            return "Empty-node";

        return "node { " + node.toString() + " }";
    }
}
