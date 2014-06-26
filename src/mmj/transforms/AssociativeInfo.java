package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

public class AssociativeInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    /** The information about closure rules */
    private final ClosureInfo clInfo;

    /** The information about replace rules */
    private final ReplaceInfo replInfo;
    /**
     * The map: associative operators -> array of 2 elements:
     * <ul>
     * <li>f(f(a, b), c) = f(a, f(b, c))
     * <li>f(a, f(b, c)) = f(f(a, b), c)
     * </ul>
     */
    protected Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>> assocOp;

    public AssociativeInfo(final EquivalenceInfo eqInfo,
        final ClosureInfo clInfo, final ReplaceInfo replInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        this.clInfo = clInfo;
        this.replInfo = replInfo;

        assocOp = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>>();
        for (final Assrt assrt : assrtList)
            findAssociativeRules(assrt);
    }
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

    public static boolean isAssociativeWithProp(final ParseNode originalNode,
        final GeneralizedStmt assocProp, final WorksheetInfo info)
    {
        if (assocProp.stmt != originalNode.getStmt())
            return false;
        return isAssociativeWithProp(originalNode, assocProp.template,
            assocProp.constSubst, info);
    }

    private static boolean isAssociativeWithProp(final ParseNode originalNode,
        final PropertyTemplate template, final ConstSubst constSubst,
        final WorksheetInfo info)
    {
        if (originalNode.getChild().length != constSubst.constMap.length)
            assert originalNode.getChild().length == constSubst.constMap.length;

        if (template.isEmpty())
            return true;

        int varNum = 0;
        for (int i = 0; i < constSubst.constMap.length; i++) {
            final ParseNode child = originalNode.getChild()[i];
            if (constSubst.constMap[i] == null) { // variable here
                varNum++;
                final ParseNode substProp = template.subst(child);
                final ProofStepStmt stmt = info.getProofStepStmt(substProp);
                if (stmt == null)
                    if (child.getStmt() != originalNode.getStmt()
                        || !isAssociativeWithProp(child, template, constSubst,
                            info))
                        return false;
            }
            else if (!constSubst.constMap[i].isDeepDup(child)) // check constant
                return false;
        }

        assert varNum == 2;
        return true;
    }

    public GeneralizedStmt getGenStmtForAssocNode(final ParseNode node,
        final WorksheetInfo info)
    {
        final Stmt stmt = node.getStmt();
        final Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap = assocOp
            .get(stmt);

        if (constSubstMap == null)
            return null;

        final ParseNode[] constMap = TrUtil.collectConstSubst(node);

        final int[] varIndexes = new int[2];

        for (final Entry<ConstSubst, Map<PropertyTemplate, Assrt[]>> elem : constSubstMap
            .entrySet())
        {
            final ConstSubst constSubst = elem.getKey();
            int curVar = 0;

            boolean ok = true;
            for (int i = 0; i < constSubst.constMap.length; i++)
                if (constSubst.constMap[i] != null) {
                    if (constMap[i] == null
                        || !constSubst.constMap[i].isDeepDup(constMap[i]))
                    {
                        ok = false;
                        break;
                    }
                }
                else {
                    assert curVar < 2;
                    varIndexes[curVar++] = i;
                }

            if (!ok)
                continue;

            final Map<PropertyTemplate, Assrt[]> propertyMap = elem.getValue();

            for (final Entry<PropertyTemplate, Assrt[]> propElem : propertyMap
                .entrySet())
            {
                final PropertyTemplate template = propElem.getKey();
                final boolean res = isAssociativeWithProp(node, template,
                    constSubst, info);
                if (res)
                    return new GeneralizedStmt(constSubst, template,
                        varIndexes, stmt);
            }
        }

        return null;
    }

    public Assrt[] getAssocOp(final GeneralizedStmt genStmt) {
        return getAssocOp(genStmt.stmt, genStmt.constSubst, genStmt.template);
    }

    public Assrt[] getAssocOp(final Stmt stmt, final ConstSubst constSubst,
        final PropertyTemplate template)
    {
        final Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap = assocOp
            .get(stmt);

        if (constSubstMap == null)
            return null;

        final Map<PropertyTemplate, Assrt[]> propertyMap = constSubstMap
            .get(constSubst);

        if (propertyMap == null)
            return null;

        final Assrt[] assocTr = propertyMap.get(template);
        return assocTr;
    }
}
