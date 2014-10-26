package mmj.transforms;

import java.util.List;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;
import mmj.transforms.ClosureInfo.ResultClosureInfo;

/**
 * The information about associative operations.
 * <p>
 * Note: the current implementation contains some stub implementations!
 */
public class AssociativeInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    private final ConjunctionInfo conjInfo;
    private final ImplicationInfo implInfo;

    /** The information about closure rules */
    private final ClosureInfo clInfo;

    /** The information about replace rules */
    private final ReplaceInfo replInfo;

    private class AssocRuleMap extends AssocComComplexRuleMap<Assrt[]> {
        @Override
        public GeneralizedStmt detectGenStmtCore(final WorksheetInfo info,
            final ParseNode node, final PropertyTemplate template,
            final ConstSubst constSubst, final int[] varIndexes)
        {
            final Stmt stmt = node.getStmt();
            final boolean res = isAssociativeWithProp(node, template,
                constSubst, info);
            if (res)
                return new GeneralizedStmt(constSubst, template, varIndexes,
                    stmt);
            return null;
        }
    };

    /**
     * The collection of associative operators:
     * <p>
     * ( ( A + B ) + C ) = ( A + ( B + C ) )
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> map : possible properties ( _ e. CC in
     * the example) -> assert array. There could be many properties (
     * {" _ e. CC" , "_ e. RR" } for example ).
     * <p>
     * The result array has 2 elements:
     * <ul>
     * <li>f(f(a, b), c) = f(a, f(b, c))
     * <li>f(a, f(b, c)) = f(f(a, b), c)
     * </ul>
     * Usually, one element is null.
     */
    private final AssocRuleMap assocOp = new AssocRuleMap();

    private final AssocRuleMap implAssocOp = new AssocRuleMap();

    // ------------------------------------------------------------------------
    // ------------------------Initialization----------------------------------
    // ------------------------------------------------------------------------

    public AssociativeInfo(final EquivalenceInfo eqInfo,
        final ClosureInfo clInfo, final ReplaceInfo replInfo,
        final ConjunctionInfo conjInfo, final ImplicationInfo implInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        this.clInfo = clInfo;
        this.replInfo = replInfo;
        this.conjInfo = conjInfo;
        this.implInfo = implInfo;

        for (final Assrt assrt : assrtList) {
            findAssociativeRules(assrt);
            findImplAssociativeRules(assrt);
        }
    }

    /**
     * Filters associative rules, like (A + B) + C = A + (B + C)
     *
     * @param assrt the candidate
     */
    private void findAssociativeRules(final Assrt assrt) {
        // Debug statements: coass, addassi
        final ParseNode root = assrt.getExprParseTree().getRoot();

        final PropertyTemplate template = TrUtil
            .getTransformOperationTemplate(assrt);

        if (template == null)
            return;

        associativeSearchCore(assrt, root, template, assocOp);
    }

    private void findImplAssociativeRules(final Assrt assrt) {
        final ResultClosureInfo res = clInfo.extractImplClosureInfo(assrt);
        if (res == null)
            return;

        final ParseNode root = res.main;
        final PropertyTemplate template = res.template;

        associativeSearchCore(assrt, root, template, implAssocOp);
    }

    private void associativeSearchCore(final Assrt assrt, final ParseNode root,
        final PropertyTemplate template, final AssocRuleMap resMap)
    {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();

        if (varHypArray.length != 3)
            return;

        if (!eqInfo.isEquivalence(root.getStmt()))
            return;

        final ParseNode[] subTrees = root.getChild();

        // it must be the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ConstSubst constSubst = ConstSubst.createFromNode(subTrees[0]);

        assert constSubst != null;

        final int[] varPlace = constSubst.getVarPlace();

        // the statement contains more that 2 variables
        if (varPlace.length != 2)
            return;

        if (!constSubst.isTheSameConstMap(subTrees[1]))
            return;

        if (!template.isEmpty())
            if (!clInfo.hasClosureAssert(stmt, constSubst, template))
                return;

        final ParseNode[] leftChildren = subTrees[0].getChild();
        final ParseNode[] rightChildren = subTrees[1].getChild();

        // we need to find one of the 2 patterns:
        // 0) f(f(a, b), c) = f(a, f(b, c))
        // 1) f(a, f(b, c)) = f(f(a, b), c)
        for (int i = 0; i < 2; i++) {
            final int k = varPlace[i];
            final int n = varPlace[(i + 1) % 2];

            final VarHyp kVar = varHypArray[k * 2];
            final VarHyp nVar = varHypArray[n * 2];

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

            if (leftChildren[n].getStmt() != nVar)
                continue;

            if (leftChildren[n].getStmt() != rightChildren[n].getChild()[n]
                .getStmt())
                continue;

            if (leftChildren[k].getChild()[k].getStmt() != kVar)
                continue;

            if (leftChildren[k].getChild()[k].getStmt() != rightChildren[k]
                .getStmt())
                continue;

            if (leftChildren[k].getChild()[n].getStmt() != varHypArray[1])
                continue;

            if (leftChildren[k].getChild()[n].getStmt() != rightChildren[n]
                .getChild()[k].getStmt())
                continue;

            if (!replInfo.isFullReplaceStatement(stmt)) {
                output.dbgMessage(dbg, "I-TR-DBG found associative assrts "
                    + "but it has problems with replace: %s: %s", assrt,
                    assrt.getFormula());
                return;
            }

            final Assrt[] assoc = resMap.addData(stmt, constSubst, template,
                new Assrt[2]);

            if (assoc[i] != null)
                continue;

            output.dbgMessage(dbg, "I-TR-DBG associative assrts: %d. %s: %s",
                i, assrt, assrt.getFormula());
            assoc[i] = assrt;
            return;
        }
    }

    // ------------------------------------------------------------------------
    // ----------------------------Detection-----------------------------------
    // ------------------------------------------------------------------------

    public static boolean isAssociativeWithProp(final ParseNode node,
        final GeneralizedStmt assocProp, final WorksheetInfo info)
    {
        if (assocProp.stmt != node.getStmt())
            return false;
        return isAssociativeWithProp(node, assocProp.template,
            assocProp.constSubst, info);
    }

    private static boolean isAssociativeWithProp(final ParseNode node,
        final PropertyTemplate template, final ConstSubst constSubst,
        final WorksheetInfo info)
    {
        assert node.getChild().length == constSubst.constMap.length;

        if (template.isEmpty())
            return true;

        int varNum = 0;
        for (int i = 0; i < constSubst.constMap.length; i++) {
            final ParseNode child = node.getChild()[i];
            if (constSubst.constMap[i] == null) { // variable here
                varNum++;
                if (child.getStmt() == node.getStmt()
                    && isAssociativeWithProp(child, template, constSubst, info))
                    continue;

                if (!info.trManager.clInfo.getClosurePossibility(info, child,
                    template, true).hasClosure())
                    return false;
            }
            else if (!constSubst.constMap[i].isDeepDup(child)) // check constant
                return false;
        }

        assert varNum == 2;
        return true;
    }

    /**
     * This function searches generalized statement for node which is considered
     * to be the root of some associative actions
     *
     * @param node the input node
     * @param info the work sheet info
     * @return the found generalized statement or null
     */
    public GeneralizedStmt getGenStmtForAssocNode(final ParseNode node,
        final WorksheetInfo info)
    {
        final GeneralizedStmt res = assocOp.detectGenStmt(node, info);
        if (res != null)
            return res;
        return implAssocOp.detectGenStmt(node, info);
    }

    // ------------------------------------------------------------------------
    // ----------------------------Transformations-----------------------------
    // ------------------------------------------------------------------------

    /**
     * Creates f(f(a, b), c) = f(a, f(b, c)) or f(a, f(b, c)) = f(f(a, b), c)
     * statement.
     *
     * @param info the work sheet info
     * @param assocProp the generalized associative statement
     * @param from 0 if we should use f(f(a, b), c) = f(a, f(b, c)) form and 1
     *            if we should use f(a, f(b, c)) = f(f(a, b), c)
     * @param firstNode the first node (f(f(a, b), c) or f(a, f(b, c)))
     * @param secondNode the second node (f(a, f(b, c)) or f(f(a, b), c))
     * @return the created step
     */
    public ProofStepStmt createAssociativeStep(final WorksheetInfo info,
        final GeneralizedStmt assocProp, final int from,
        final ParseNode firstNode, final ParseNode secondNode)
    {
        Assrt[] assocTr = getAssocOp(assocProp, assocOp);
        final boolean implForm;
        if (assocTr == null) {
            implForm = true;
            assocTr = getAssocOp(assocProp, implAssocOp);
        }
        else
            implForm = false;

        assert assocTr != null;

        final boolean revert;
        final Assrt assocAssrt;
        final ParseNode left;
        final ParseNode right;
        if (assocTr[from] != null) {
            assocAssrt = assocTr[from];
            revert = false;
            left = firstNode;
            right = secondNode;
        }
        else {
            final int other = (from + 1) % 2;
            assocAssrt = assocTr[other];
            revert = true;
            left = secondNode;
            right = firstNode;
        }
        assert assocAssrt != null;

        final Stmt equalStmt;

        if (!implForm)
            equalStmt = assocAssrt.getExprParseTree().getRoot().getStmt();
        else {
            final ParseNode assrtRoot = assocAssrt.getExprParseTree().getRoot();
            equalStmt = assrtRoot.getChild()[1].getStmt();
        }

        // Create node f(f(a, b), c) = f(a, f(b, c))
        final ParseNode stepNode = TrUtil.createBinaryNode(equalStmt, left,
            right);

        final boolean firstForm = assocTr[0] != null;

        ProofStepStmt res = closurePropertyAssociative(info, assocProp,
            assocAssrt, firstForm, stepNode, implForm);

        if (revert)
            res = eqInfo.createReverse(info, res);

        return res;
    }

    // First form: f(f(a, b), c) = f(a, f(b, c))
    // Second form: f(a, f(b, c)) = f(f(a, b), c)
    private ProofStepStmt closurePropertyAssociative(final WorksheetInfo info,
        final GeneralizedStmt assocProp, final Assrt assocAssrt,
        final boolean firstForm, final ParseNode stepNode,
        final boolean implForm)
    {
        final ProofStepStmt[] hyps;
        if (!assocProp.template.isEmpty()) {

            hyps = new ProofStepStmt[3];
            final int n0 = assocProp.varIndexes[0];
            final int n1 = assocProp.varIndexes[1];
            // Side should has form f(f(a, b), c)
            final ParseNode side;
            if (firstForm)
                side = stepNode.getChild()[0];
            else
                side = stepNode.getChild()[1];
            final ParseNode[] in = new ParseNode[3];
            in[0] = side.getChild()[n0].getChild()[n0];
            in[1] = side.getChild()[n0].getChild()[n1];
            in[2] = side.getChild()[n1];

            for (int i = 0; i < 3; i++)
                hyps[i] = clInfo.closureProperty(info, assocProp.template,
                    in[i]);
        }
        else {
            assert !implForm;
            hyps = new ProofStepStmt[]{};
        }

        final ProofStepStmt res;
        if (!implForm)
            res = info.getOrCreateProofStepStmt(stepNode, hyps, assocAssrt);
        else {
            final ParseNode assrtRoot = assocAssrt.getExprParseTree().getRoot();
            final ParseNode hypsPartPattern = assrtRoot.getChild()[0];
            // Precondition f(A) /\ f(B) /\ f(C) step
            // (or ph->(f(A) /\ f(B) /\ f(C)))
            final ProofStepStmt implHyp = conjInfo.concatenateInTheSamePattern(
                hyps, hypsPartPattern, info);

            res = implInfo.applyImplicationRule(info, implHyp, stepNode,
                assocAssrt);
        }

        return res;
    }

    // ------------------------------------------------------------------------
    // ------------------------------Getters-----------------------------------
    // ------------------------------------------------------------------------

    private Assrt[] getAssocOp(final GeneralizedStmt genStmt,
        final AssocRuleMap map)
    {
        return map.getData(genStmt.stmt, genStmt.constSubst, genStmt.template);
    }
}
