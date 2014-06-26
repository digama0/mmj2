package mmj.transforms;

import java.util.*;

import mmj.lang.*;

public class CommutativeInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    /** The information about closure rules */
    private final ClosureInfo clInfo;

    /** The list of commutative operators */
    protected Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>> comOp;

    // protected Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>>
    // assocOp;

    // ------------------------------------------------------------------------
    // ------------------------Initialization----------------------------------
    // ------------------------------------------------------------------------

    public CommutativeInfo(final EquivalenceInfo eqInfo,
        final ClosureInfo clInfo, final List<Assrt> assrtList,
        final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        this.clInfo = clInfo;

        comOp = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>>();
        for (final Assrt assrt : assrtList)
            findCommutativeRules(assrt);
    }

    /**
     * Filters commutative rules, like A + B = B + A
     * 
     * @param assrt the candidate
     */
    protected void findCommutativeRules(final Assrt assrt) {
        // Debug statements: prcom, addcomi
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (assrt.getLabel().toString().equals("addcomi"))
            assrt.toString();

        final PropertyTemplate template = ClosureInfo
            .createTemplateFromHyp(assrt);

        if (template == null)
            return;

        if (varHypArray.length != 2)
            return;

        if (!eqInfo.isEquivalence(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it is the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ConstSubst constSubst = ConstSubst.createFromNode(subTrees[0]);

        final int[] varPlace = constSubst.getVarPlace();

        // the statement contains more that 2 variables
        if (varPlace.length != 2)
            return;

        if (!constSubst.isTheSameConstMap(subTrees[1]))
            return;

        if (!template.isEmpty())
            if (clInfo.getClosureAssert(stmt, constSubst, template) == null)
                return;

        final ParseNode[] leftChildren = subTrees[0].getChild();
        final ParseNode[] rightChildren = subTrees[1].getChild();

        final int k0 = varPlace[0];
        final int k1 = varPlace[1];

        if (leftChildren[k0].getStmt() != rightChildren[k1].getStmt())
            return;

        if (leftChildren[k1].getStmt() != rightChildren[k0].getStmt())
            return;

        Map<ConstSubst, Map<PropertyTemplate, Assrt>> constSubstMap = comOp
            .get(stmt);

        if (constSubstMap == null) {
            // TODO: linked hash map stub!
            constSubstMap = new LinkedHashMap<ConstSubst, Map<PropertyTemplate, Assrt>>();
            comOp.put(stmt, constSubstMap);
        }

        Map<PropertyTemplate, Assrt> propertyMap = constSubstMap
            .get(constSubst);
        if (propertyMap == null) {
            // TODO: linked hash map stub!
            propertyMap = new LinkedHashMap<PropertyTemplate, Assrt>();
            constSubstMap.put(constSubst, propertyMap);
        }

        final Assrt com = propertyMap.get(template);

        if (com != null)
            return;

        output.dbgMessage(dbg, "I-DBG commutative assrts: %s: %s", assrt,
            assrt.getFormula());
        propertyMap.put(template, assrt);
    }
}
