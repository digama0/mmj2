package mmj.transforms;

import mmj.lang.*;

public class TrUtil {
    // Do not create objects with this type!
    private TrUtil() {}

    public static boolean isVarNode(final ParseNode node) {
        return isVarStmt(node.getStmt());
    }

    public static boolean isVarStmt(final Stmt stmt) {
        return stmt instanceof VarHyp;
    }

    public static boolean isConstNode(final ParseNode node) {
        if (isVarNode(node))
            return false;

        for (final ParseNode child : node.getChild())
            if (!isConstNode(child))
                return false;

        return true;
    }

    /**
     * Creates classical binary node
     * 
     * @param stmt the statement
     * @param left the first (left) node
     * @param right the second (right) node
     * @return the created node
     */
    public static ParseNode createBinaryNode(final Stmt stmt,
        final ParseNode left, final ParseNode right)
    {
        final ParseNode eqRoot = new ParseNode(stmt);
        final ParseNode[] children = {left, right};
        eqRoot.setChild(children);
        return eqRoot;
    }

    public static ParseNode[] collectConstSubst(final ParseNode originalNode) {
        final ParseNode[] constMap = new ParseNode[originalNode.getChild().length];

        for (int i = 0; i < constMap.length; i++) {
            final ParseNode child = originalNode.getChild()[i];
            if (isConstNode(child))
                constMap[i] = child;
        }

        return constMap;
    }

    /**
     * Creates node for generalized statement.
     * 
     * @param genStmt the statement
     * @param left the first (left) node
     * @param right the second (right) node
     * @return the created node
     */
    public static ParseNode createGenBinaryNode(final GeneralizedStmt genStmt,
        final ParseNode left, final ParseNode right)
    {
        final ParseNode eqRoot = new ParseNode(genStmt.stmt);
        final int len = genStmt.constSubst.constMap.length;
        final ParseNode[] vars = {left, right};
        final ParseNode[] children = new ParseNode[len];
        for (int i = 0; i < len; i++) {
            final ParseNode c = genStmt.constSubst.constMap[i];
            if (c != null)
                children[i] = c.deepClone();
        }
        for (int i = 0; i < 2; i++)
            children[genStmt.varIndexes[i]] = vars[i];

        eqRoot.setChild(children);
        return eqRoot;
    }

    public static ParseNode createAssocBinaryNode(final int from,
        final GeneralizedStmt assocProp, final ParseNode left,
        final ParseNode right)
    {
        if (from == 0)
            return createGenBinaryNode(assocProp, left, right);
        else
            return createGenBinaryNode(assocProp, right, left);
    }

}
