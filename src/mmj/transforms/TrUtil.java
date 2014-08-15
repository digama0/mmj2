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
        final GeneralizedStmt genStmt, final ParseNode left,
        final ParseNode right)
    {
        if (from == 0)
            return createGenBinaryNode(genStmt, left, right);
        else
            return createGenBinaryNode(genStmt, right, left);
    }

    public static int[] checkConstSubstAndGetVarPositions(
        final ConstSubst constSubst, final ParseNode[] constMap)
    {
        int size = 0;
        for (final ParseNode element : constSubst.constMap)
            if (element == null)
                size++;

        int curVar = 0;
        final int[] varIndexes = new int[size];
        for (int i = 0; i < constSubst.constMap.length; i++)
            if (constSubst.constMap[i] != null) {
                if (constMap[i] == null
                    || !constSubst.constMap[i].isDeepDup(constMap[i]))
                    return null;
            }
            else {
                assert curVar < size;
                varIndexes[curVar++] = i;
            }

        return varIndexes;
    }

    // Could return empty array with length 0
    public static VarHyp[] getHypToVarMap(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();

        final VarHyp[] hypToVarHypMap = new VarHyp[logHyps.length];
        if (logHyps.length != varHypArray.length)
            return null;

        for (int i = 0; i < logHyps.length; i++) {
            final VarHyp[] varsi = logHyps[i].getMandVarHypArray();
            if (varsi.length != 1)
                return null;
            final VarHyp vari = varsi[0];

            hypToVarHypMap[i] = vari;
        }

        return hypToVarHypMap;
    }

}
