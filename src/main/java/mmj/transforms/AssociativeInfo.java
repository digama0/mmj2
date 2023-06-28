//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.List;

import mmj.lang.*;
import mmj.transforms.ClosureInfo.ResultClosureInfo;

/**
 * The information about associative operations.
 * <p>
 * Note: the current implementation contains some stub implementations!
 */
public class AssociativeInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

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
            final Stmt stmt = node.stmt;
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
     * the example) -> assert array. There could be many properties ( {
     * " _ e. CC" , "_ e. RR" } for example ).
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
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        this.clInfo = clInfo;
        this.replInfo = replInfo;

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

        if (!eqInfo.isEquivalence(root.stmt))
            return;

        final ParseNode[] subTrees = root.child;

        // it must be the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].stmt != subTrees[1].stmt)
            return;

        final Stmt stmt = subTrees[0].stmt;

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

        final ParseNode[] leftChildren = subTrees[0].child;
        final ParseNode[] rightChildren = subTrees[1].child;

        // we need to find one of the 2 patterns:
        // 0) f(f(a, b), c) = f(a, f(b, c))
        // 1) f(a, f(b, c)) = f(f(a, b), c)
        for (int i = 0; i < 2; i++) {
            final int k = varPlace[i];
            final int n = varPlace[(i + 1) % 2];

            final VarHyp kVar = varHypArray[k * 2];
            final VarHyp nVar = varHypArray[n * 2];

            if (leftChildren[k].stmt != stmt)
                continue;
            if (!constSubst.isTheSameConstMap(leftChildren[k]))
                continue;

            if (rightChildren[n].stmt != stmt)
                continue;

            if (!constSubst.isTheSameConstMap(rightChildren[n]))
                continue;

            if (!TrUtil.isVarNode(leftChildren[n]))
                continue;

            if (leftChildren[n].stmt != nVar)
                continue;

            if (leftChildren[n].stmt != rightChildren[n].child[n].stmt)
                continue;

            if (leftChildren[k].child[k].stmt != kVar)
                continue;

            if (leftChildren[k].child[k].stmt != rightChildren[k].stmt)
                continue;

            if (leftChildren[k].child[n].stmt != varHypArray[1])
                continue;

            if (leftChildren[k].child[n].stmt != rightChildren[n].child[k].stmt)
                continue;

            if (!replInfo.isFullReplaceStatement(stmt)) {
                output.dbgMessage(dbg, TrConstants.ERRMSG_ASSOC_REPLACE_FAIL,
                    assrt, assrt.getFormula());
                return;
            }

            final Assrt[] assoc = resMap.addData(stmt, constSubst, template,
                new Assrt[2]);

            if (assoc[i] != null)
                continue;

            output.dbgMessage(dbg, TrConstants.ERRMSG_ASSOC_ASSRTS, i, assrt,
                assrt.getFormula());
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
        if (assocProp.stmt != node.stmt)
            return false;
        return isAssociativeWithProp(node, assocProp.template,
            assocProp.constSubst, info);
    }

    private static boolean isAssociativeWithProp(final ParseNode node,
        final PropertyTemplate template, final ConstSubst constSubst,
        final WorksheetInfo info)
    {
        assert node.child.length == constSubst.constMap.length;

        if (template.isEmpty())
            return true;

        int varNum = 0;
        for (int i = 0; i < constSubst.constMap.length; i++) {
            final ParseNode child = node.child[i];
            if (constSubst.constMap[i] == null) { // variable here
                varNum++;
                if (child.stmt == node.stmt
                    && isAssociativeWithProp(child, template, constSubst, info))
                    continue;

                if (!info.trManager.clInfo.getClosurePossibility(info, child,
                    template, true).hasClosure)
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
            if (!info.hasImplPrefix() || res.template.isEmpty())
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
    public GenProofStepStmt createAssociativeStep(final WorksheetInfo info,
        final GeneralizedStmt assocProp, final int from,
        final ParseNode firstNode, final ParseNode secondNode)
    {
        Assrt[] assocTr = null;
        boolean implForm = false;

        if (!info.hasImplPrefix() || assocProp.template.isEmpty()) {
            assocTr = getAssocOp(assocProp, assocOp);
            if (assocTr != null)
                implForm = false;
        }

        if (assocTr == null) {
            implForm = true;
            assocTr = getAssocOp(assocProp, implAssocOp);
        }

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

        final boolean firstForm = assocTr[0] != null;

        GenProofStepStmt res = closurePropertyAssociative(info, assocProp,
            assocAssrt, firstForm, left, right, implForm);

        if (revert)
            res = eqInfo.createReverseStep(info, res);

        return res;
    }

    // First form: f(f(a, b), c) = f(a, f(b, c))
    // Second form: f(a, f(b, c)) = f(f(a, b), c)
    private GenProofStepStmt closurePropertyAssociative(
        final WorksheetInfo info, final GeneralizedStmt assocProp,
        final Assrt assocAssrt, final boolean firstForm, final ParseNode left,
        final ParseNode right, final boolean implForm)
    {
        final GenProofStepStmt[] hyps;
        if (!assocProp.template.isEmpty()) {

            hyps = new GenProofStepStmt[3];
            final int n0 = assocProp.varIndexes[0];
            final int n1 = assocProp.varIndexes[1];
            // Side should has form f(f(a, b), c)
            final ParseNode side;
            if (firstForm)
                side = left;
            else
                side = right;
            final ParseNode[] in = new ParseNode[3];
            in[0] = side.child[n0].child[n0];
            in[1] = side.child[n0].child[n1];
            in[2] = side.child[n1];

            for (int i = 0; i < 3; i++)
                hyps[i] = clInfo.closureProperty(info, assocProp.template,
                    in[i], false, true);
        }
        else {
            assert !implForm;
            hyps = new GenProofStepStmt[]{};
        }

        return TrUtil.applyClosureProperties(implForm, hyps, info, assocAssrt,
            left, right);
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
