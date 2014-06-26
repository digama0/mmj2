package mmj.transforms;

import java.util.*;

import mmj.lang.*;

public class CommutativeInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    /** The list of commutative operators */
    protected Map<Stmt, Assrt> comOp;

    public CommutativeInfo(final EquivalenceInfo eqInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;

        comOp = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findCommutativeRules(assrt);
    }

    /**
     * Filters commutative rules, like A + B = B + A
     * 
     * @param assrt the candidate
     */
    protected void findCommutativeRules(final Assrt assrt) {
        // TODO: adds the support of assrts like addcomi
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 0)
            return;

        if (varHypArray.length != 2)
            return;

        if (assrtTree.getMaxDepth() != 3)
            return;

        if (!eqInfo.isEquivalence(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it is the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        if (subTrees[0].getChild().length != 2)
            return;

        if (subTrees[0].getChild()[0].getStmt() != subTrees[1].getChild()[1]
            .getStmt())
            return;

        if (subTrees[0].getChild()[1].getStmt() != subTrees[1].getChild()[0]
            .getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        if (comOp.containsKey(stmt))
            return;

        output.dbgMessage(dbg, "I-DBG commutative assrts: %s: %s", assrt,
            assrt.getFormula());
        comOp.put(stmt, assrt);
    }
}
