//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.HashSet;
import java.util.Set;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;
import mmj.transforms.ClosureInfo.TemplDetectRes;

public class TrUtil {
    // Do not create objects with this type!
    private TrUtil() {}

    public static boolean isVarNode(final ParseNode node) {
        return isVarStmt(node.stmt);
    }

    public static boolean isVarStmt(final Stmt stmt) {
        return stmt instanceof VarHyp;
    }

    public static boolean isConstNode(final ParseNode node) {
        if (isVarNode(node))
            return false;

        for (final ParseNode child : node.child)
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
        assert left != null;
        assert right != null;
        assert stmt != null;
        return new ParseNode(stmt, left, right);
    }

    public static ParseNode[] collectConstSubst(final ParseNode originalNode) {
        final ParseNode[] constMap = new ParseNode[originalNode.child.length];

        for (int i = 0; i < constMap.length; i++) {
            final ParseNode child = originalNode.child[i];
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

        return new ParseNode(genStmt.stmt, children);
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
        return getHypToVarMap(assrt, null);
    }

    // Could return empty array with length 0
    public static VarHyp[] getHypToVarMap(final Assrt assrt,
        final VarHyp paramVar)
    {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();

        final VarHyp[] hypToVarHypMap = new VarHyp[logHyps.length];
        if (paramVar == null) {
            if (logHyps.length != varHypArray.length)
                return null;
        }
        else if (1 + logHyps.length != varHypArray.length)
            return null;

        for (int i = 0; i < logHyps.length; i++) {
            final VarHyp[] varsi = logHyps[i].getMandVarHypArray();
            final VarHyp vari;
            if (paramVar == null) {
                if (varsi.length != 1)
                    return null;
                vari = varsi[0];
            }
            else {
                if (varsi.length != 2)
                    return null;
                if (paramVar == varsi[0])
                    vari = varsi[1];
                else if (paramVar == varsi[1])
                    vari = varsi[0];
                else
                    return null;
            }

            hypToVarHypMap[i] = vari;
        }

        return hypToVarHypMap;
    }

    public static void findVarsInParseNode(final ParseNode input,
        final Set<VarHyp> res)
    {
        if (input.stmt instanceof VarHyp)
            res.add((VarHyp)input.stmt);
        else
            for (final ParseNode child : input.child)
                findVarsInParseNode(child, res);
    }

    // Returns the only one variable or null
    public static VarHyp findOneVarInParseNode(final ParseNode input) {
        final Set<VarHyp> res = new HashSet<>();
        findVarsInParseNode(input, res);
        if (res.size() != 1)
            return null;
        else
            return res.iterator().next();
    }

    public static PropertyTemplate getTransformOperationTemplate(
        final Assrt assrt)
    {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();

        final TemplDetectRes tDetectRes = ClosureInfo
            .getTemplateAndVarHyps(assrt);

        if (tDetectRes == null)
            return null;

        final PropertyTemplate template = tDetectRes.template;

        assert template != null;

        // Preserve the same order for variable hypotheses and for the
        // commutative rule
        if (!template.isEmpty()) {
            if (varHypArray.length != tDetectRes.hypToVarHypMap.length)
                return null;

            for (int i = 0; i < varHypArray.length; i++)
                if (varHypArray[i] != tDetectRes.hypToVarHypMap[i])
                    return null;
        }

        return template;
    }

    /**
     * This function converts the array of generalized steps into array of
     * simple proof steps. Be sure that every general step is actually a simple
     * one.
     *
     * @param genSteps steps in generalized form
     * @return simplified steps
     */
    static public ProofStepStmt[] convertGenToSimpleProofSteps(
        final GenProofStepStmt[] genSteps)
    {
        final ProofStepStmt[] simpleSteps = new ProofStepStmt[genSteps.length];

        for (int i = 0; i < genSteps.length; i++)
            simpleSteps[i] = genSteps[i].getSimpleStep();
        return simpleSteps;
    }

    public static GenProofStepStmt applyClosureProperties(
        final boolean implForm, final GenProofStepStmt[] hyps,
        final WorksheetInfo info, final Assrt assrt, final ParseNode left,
        final ParseNode right)
    {
        final ImplicationInfo implInfo = info.trManager.implInfo;
        final ConjunctionInfo conjInfo = info.trManager.conjInfo;

        final Stmt equalStmt;

        if (!implForm)
            equalStmt = assrt.getExprParseTree().getRoot().stmt;
        else {
            final ParseNode assrtRoot = assrt.getExprParseTree().getRoot();
            equalStmt = assrtRoot.child[1].stmt;
        }

        // Create node f(f(a, b), c) = f(a, f(b, c))
        final ParseNode stepNode = createBinaryNode(equalStmt, left, right);

        if (!implForm) {
            if (hyps.length != 0)
                assert !info.hasImplPrefix();

            ProofStepStmt res = info.getProofStepStmt(stepNode);
            if (res == null)
                res = info.createProofStepStmt(stepNode,
                    convertGenToSimpleProofSteps(hyps), assrt);

            return new GenProofStepStmt(res, null);
        }
        else {
            final ParseNode assrtRoot = assrt.getExprParseTree().getRoot();
            final ParseNode hypsPartPattern = assrtRoot.child[0];

            final GenProofStepStmt hypGenStep = conjInfo
                .concatenateInTheSamePattern(hyps, hypsPartPattern, info);

            return implInfo.applyHyp(info, hypGenStep, stepNode, assrt);
        }
    }
}
