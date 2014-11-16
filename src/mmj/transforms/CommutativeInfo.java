package mmj.transforms;

import java.util.List;

import mmj.lang.*;
import mmj.transforms.ClosureInfo.ResultClosureInfo;

/**
 * The information about commutative operations.
 * <p>
 * Note: the current implementation contains some stub implementations!
 */
public class CommutativeInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    private final ClosureInfo clInfo;

    private class CommRuleMap extends AssocComComplexRuleMap<Assrt> {
        @Override
        public GeneralizedStmt detectGenStmtCore(final WorksheetInfo info,
            final ParseNode node, final PropertyTemplate template,
            final ConstSubst constSubst, final int[] varIndexes)
        {
            final Stmt stmt = node.getStmt();
            final GeneralizedStmt genStmt = new GeneralizedStmt(constSubst,
                template, varIndexes, stmt);
            final boolean res = isCommutativeWithProp(node, info, genStmt);
            if (res)
                return genStmt;
            return null;
        }
    };

    /**
     * The list of commutative operators: ( A + B ) = ( B + A ). Or more complex
     * cases: "A e. CC & B e. CC => ( A + B ) = ( B + A )"
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> map : possible properties ( _ e. CC in
     * the example) -> assert. There could be many properties ( {" _ e. CC" ,
     * "_ e. RR" } for example ).
     */
    private final CommRuleMap comOp = new CommRuleMap();

    // TODO: implement the usage of it!
    private final CommRuleMap implComOp = new CommRuleMap();

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

        for (final Assrt assrt : assrtList) {
            findCommutativeRules(assrt);
            findImplCommutativeRules(assrt);
        }
    }

    /**
     * Filters commutative rules, like A + B = B + A
     *
     * @param assrt the candidate
     */
    protected void findCommutativeRules(final Assrt assrt) {
        // Debug statements: prcom, addcomi
        final ParseNode root = assrt.getExprParseTree().getRoot();

        final PropertyTemplate template = TrUtil
            .getTransformOperationTemplate(assrt);

        if (template == null)
            return;

        commutativeSearchCore(assrt, root, template, comOp);
    }

    private void findImplCommutativeRules(final Assrt assrt) {
        // Debug statements: gcdcom

        final ResultClosureInfo res = clInfo.extractImplClosureInfo(assrt);
        if (res == null)
            return;

        final ParseNode root = res.main;
        final PropertyTemplate template = res.template;
        commutativeSearchCore(assrt, root, template, implComOp);
    }

    private void commutativeSearchCore(final Assrt assrt, final ParseNode root,
        final PropertyTemplate template, final CommRuleMap resMap)
    {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();

        if (varHypArray.length != 2)
            return;

        if (!eqInfo.isEquivalence(root.getStmt()))
            return;

        final ParseNode[] subTrees = root.getChild();

        // it is the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ConstSubst constSubst = ConstSubst.createFromNode(subTrees[0]);
        if (constSubst == null)
            return;

        final int[] varPlace = constSubst.getVarPlace();

        // the statement contains more that 2 variables
        if (varPlace.length != 2)
            return;

        if (!constSubst.isTheSameConstMap(subTrees[1]))
            return;

        // It is unnecessary:
        // if (!template.isEmpty())
        // if (clInfo.getClosureAssert(stmt, constSubst, template) == null)
        // return;

        final ParseNode[] leftChildren = subTrees[0].getChild();
        final ParseNode[] rightChildren = subTrees[1].getChild();

        final int k0 = varPlace[0];
        final int k1 = varPlace[1];

        if (leftChildren[k0].getStmt() != varHypArray[0])
            return;

        if (leftChildren[k1].getStmt() != varHypArray[1])
            return;

        if (leftChildren[k0].getStmt() != rightChildren[k1].getStmt())
            return;

        if (leftChildren[k1].getStmt() != rightChildren[k0].getStmt())
            return;

        final Assrt com = resMap.addData(stmt, constSubst, template, assrt);

        if (com != assrt)
            return;

        output.dbgMessage(dbg, "I-TR-DBG commutative assrts: %s: %s", assrt,
            assrt.getFormula());
        // propertyMap.put(template, assrt);
    }
    // ------------------------------------------------------------------------
    // ----------------------------Detection-----------------------------------
    // ------------------------------------------------------------------------

    /**
     * @param first The one operand
     * @param second The other operand
     * @return -1(less), 0(equal),1(greater)
     */
    public static int compareNodes(final ParseNode first, final ParseNode second)
    {
        if (first.getStmt() == second.getStmt()) {
            final int len = first.getChild().length;
            for (int i = 0; i < len; i++) {
                final int res = compareNodes(first.getChild()[i],
                    second.getChild()[i]);
                if (res != 0)
                    return res;
            }

            return 0;
        }
        return first.getStmt().getSeq() < second.getStmt().getSeq() ? -1 : 1;
    }

    /**
     * This function searches generalized statement for node which is considered
     * to be the root of some commutative action
     *
     * @param node the input node
     * @param info the work sheet info
     * @return the found generalized statement or null
     */
    public GeneralizedStmt getGenStmtForComNode(final ParseNode node,
        final WorksheetInfo info)
    {
        final GeneralizedStmt res = comOp.detectGenStmt(node, info);
        if (res != null)
            if (!TransformationManager.SEARCH_PREFIX || res.template.isEmpty())
                return res;
        return implComOp.detectGenStmt(node, info);
    }

    private static boolean isCommutativeWithProp(final ParseNode node,
        final WorksheetInfo info, final GeneralizedStmt genStmt)
    {
        if (genStmt.template.isEmpty())
            return true;
        for (int i = 0; i < 2; i++) {
            final ParseNode child = node.getChild()[genStmt.varIndexes[i]];

            if (!info.trManager.clInfo.getClosurePossibility(info, child,
                genStmt.template, true).hasClosure())
                return false;
        }

        return true;
    }

    // ------------------------------------------------------------------------
    // ----------------------------Transformations-----------------------------
    // ------------------------------------------------------------------------

    /**
     * Creates f(a, b) = f(b, a) statement.
     *
     * @param info the work sheet info
     * @param comProp the generalized associative statement
     * @param source the first node f(a, b)
     * @param target the second node f(b, a)
     * @return the created step
     */
    public GenProofStepStmt createCommutativeStep(final WorksheetInfo info,
        final GeneralizedStmt comProp, final ParseNode source,
        final ParseNode target)
    {
        final GenProofStepStmt[] hyps;
        if (!comProp.template.isEmpty()) {
            hyps = new GenProofStepStmt[2];
            final int n0 = comProp.varIndexes[0];
            final int n1 = comProp.varIndexes[1];
            final ParseNode side = source;
            final ParseNode[] in = new ParseNode[2];
            in[0] = side.getChild()[n0];
            in[1] = side.getChild()[n1];

            for (int i = 0; i < 2; i++)
                hyps[i] = clInfo.closureProperty(info, comProp.template, in[i],
                    false, true);
        }
        else
            hyps = new GenProofStepStmt[]{};

        Assrt assrt = null;
        boolean implForm = false;

        if (!TransformationManager.SEARCH_PREFIX || comProp.template.isEmpty())
        {
            assrt = getComOp(comProp, comOp);
            if (assrt != null)
                implForm = false;
        }

        if (assrt == null) {
            implForm = true;
            assrt = getComOp(comProp, implComOp);
        }

        return TrUtil.applyClosureProperties(implForm, hyps, info, assrt,
            source, target);
    }

    // ------------------------------------------------------------------------
    // ------------------------------Getters-----------------------------------
    // ------------------------------------------------------------------------

    public boolean isComOp(final GeneralizedStmt genStmt) {

        if (getComOp(genStmt, comOp) != null)
            if (!TransformationManager.SEARCH_PREFIX
                || genStmt.template.isEmpty())
                return true;

        return getComOp(genStmt, implComOp) != null;
    }

    public Assrt getComOp(final GeneralizedStmt genStmt,
        final CommRuleMap resMap)
    {
        // return getComOp(genStmt.stmt, genStmt.constSubst, genStmt.template);
        return resMap.getData(genStmt.stmt, genStmt.constSubst,
            genStmt.template);
    }
}
