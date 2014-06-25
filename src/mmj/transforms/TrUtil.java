package mmj.transforms;

import mmj.lang.*;

public class TrUtil {
    // Do not create objects with this type!
    private TrUtil() {}

    public static boolean isVarNode(final ParseNode node) {
        return TrUtil.isVarStmt(node.getStmt());
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

    public static ParseNode createBinaryNode(final Stmt stmt,
        final ParseNode left, final ParseNode right)
    {
        final ParseNode eqRoot = new ParseNode(stmt);
        final ParseNode[] children = {left, right};
        eqRoot.setChild(children);
        return eqRoot;
    }

}
