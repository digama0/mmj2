package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.verify.VerifyProofs;

public class DataBaseInfo {

    /** This field is true if this object was initialized */
    private boolean isInit = false;

    private final boolean dbg = true;

    protected TrOutput output;

    /** It is necessary for formula construction */
    VerifyProofs verifyProofs;

    /** The information about equivalence rules */
    protected EquivalenceInfo eqInfo = new EquivalenceInfo();

    protected ImplicationInfo implInfo = new ImplicationInfo();

    protected ReplaceInfo replInfo = new ReplaceInfo();

    protected ClosureInfo clInfo = new ClosureInfo();

    /** The list of commutative operators */
    protected Map<Stmt, Assrt> comOp;

    /**
     * The map: associative operators -> array of 2 elements:
     * <ul>
     * <li>f(f(a, b), c) = f(a, f(b, c))
     * <li>f(a, f(b, c)) = f(f(a, b), c)
     * </ul>
     */
    protected Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>> assocOp;

    /** The symbol like |- in set.mm */
    protected Cnst provableLogicStmtTyp;

    /** Empty default constructor */
    public DataBaseInfo() {}

    // ----------------------------

    public void prepareAutomaticTransformations(final List<Assrt> assrtList,
        final Cnst provableLogicStmtTyp, final Messages messages,
        final VerifyProofs verifyProofs)
    {
        isInit = true;
        output = new TrOutput(messages);
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        eqInfo.initMe(assrtList, output, dbg);

        clInfo.initMe(assrtList, output, dbg);

        implInfo.initMe(eqInfo, assrtList, output, dbg);

        replInfo.initMe(eqInfo, assrtList, output, dbg);

        comOp = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findCommutativeRules(assrt);

        assocOp = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>>();
        for (final Assrt assrt : assrtList)
            findAssociativeRules(assrt);
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

    // -----------------------------------------------------------
    // -----------------------------------------------------------
    // -----------------------------------------------------------

    /**
     * Filters associative rules, like (A + B) + C = A + (B + C)
     * 
     * @param assrt the candidate
     */
    protected void findAssociativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        // if (assrt.getLabel().equals("addassi"))
        // assrt.toString();

        final LogHyp[] logHyps = assrt.getLogHypArray();

        ParseNode templNode = null;
        if (logHyps.length != 0) {
            templNode = ClosureInfo.createTemplateNodeFromHyp(assrt);
            if (templNode == null)
                return;
        }

        if (varHypArray.length != 3)
            return;

        // if (assrtTree.getMaxDepth() != 4)
        // return;

        if (!eqInfo.isEquivalence(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it must be the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ParseNode[] leftChildren = subTrees[0].getChild();
        final ParseNode[] rightChildren = subTrees[1].getChild();

        final int[] varPlace = new int[leftChildren.length];
        for (int i = 0; i < varPlace.length; i++)
            varPlace[i] = -1;
        int curVarNum = 0;
        final ParseNode[] constMap = new ParseNode[leftChildren.length];
        for (int i = 0; i < leftChildren.length; i++)
            if (TrUtil.isConstNode(leftChildren[i]))
                constMap[i] = leftChildren[i];
            else
                varPlace[curVarNum++] = i;

        // the statement contains more that 2 variables
        if (curVarNum != 2)
            return;

        final ConstSubst constSubst = new ConstSubst(constMap);

        if (!constSubst.isTheSameConstMap(subTrees[1]))
            return;

        final PropertyTemplate template = new PropertyTemplate(templNode);
        if (!template.isEmpty())
            if (clInfo.getClosureAssert(stmt, constSubst, template) == null)
                return;

        // we need to find one of the 2 patterns:
        // 0) f(f(a, b), c) = f(a, f(b, c))
        // 1) f(a, f(b, c)) = f(f(a, b), c)
        for (int i = 0; i < 2; i++) {
            final int k = varPlace[i];
            final int n = varPlace[(i + 1) % 2];
            if (leftChildren[k].getStmt() != stmt)
                continue;
            if (!constSubst.isTheSameConstMap(leftChildren[k]))
                continue;

            if (rightChildren[n].getStmt() != stmt)
                continue;

            if (!constSubst.isTheSameConstMap(rightChildren[n]))
                continue;

            if (!TrUtil.isVarNode(leftChildren[n]))
                continue;

            if (leftChildren[n].getStmt() != rightChildren[n].getChild()[n]
                .getStmt())
                continue;

            if (leftChildren[k].getChild()[k].getStmt() != rightChildren[k]
                .getStmt())
                continue;

            if (leftChildren[k].getChild()[n].getStmt() != rightChildren[n]
                .getChild()[k].getStmt())
                continue;

            if (!replInfo.isFullReplaceStatement(stmt)) {
                output.dbgMessage(dbg, "I-DBG found commutative assrts "
                    + "but it has problems with replace: %s: %s", assrt,
                    assrt.getFormula());
                return;
            }

            if (!constSubst.isEmpty())
                output.dbgMessage(dbg,
                    "I-DBG temporary associative assrts: %d. %s: %s", i, assrt,
                    assrt.getFormula());
            // return;

            Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap = assocOp
                .get(stmt);

            if (constSubstMap == null) {
                constSubstMap = new HashMap<ConstSubst, Map<PropertyTemplate, Assrt[]>>();
                assocOp.put(stmt, constSubstMap);
            }

            Map<PropertyTemplate, Assrt[]> propertyMap = constSubstMap
                .get(constSubst);
            if (propertyMap == null) {
                propertyMap = new HashMap<PropertyTemplate, Assrt[]>();
                constSubstMap.put(constSubst, propertyMap);
            }

            Assrt[] assoc = propertyMap.get(template);

            if (assoc == null) {
                assoc = new Assrt[2];
                propertyMap.put(template, assoc);
            }
            /*
            Assrt[] assoc = assocOp.get(stmt);

            if (assoc == null) {
                assoc = new Assrt[2];
                assocOp.put(stmt, assoc);
            }
            */

            if (assoc[i] != null)
                continue;

            output.dbgMessage(dbg, "I-DBG associative assrts: %d. %s: %s", i,
                assrt, assrt.getFormula());
            assoc[i] = assrt;
            return;
        }
    }

    public boolean isInit() {
        return isInit;
    }
}
