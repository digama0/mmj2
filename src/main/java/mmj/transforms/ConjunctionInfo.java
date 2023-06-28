//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

public class ConjunctionInfo extends DBInfo {

    private final ImplicationInfo implInfo;

    /** The list of operations which have form A & B & ... => f(A, B, ...) */
    private final Map<Stmt, Assrt> gatheringOp = new HashMap<>();

    /**
     * The list of operations which have form
     * "p->A & p->B & ... => p->f(A, B, ...)".
     * <p>
     * It is map: implication operator -> gathering operator -> assertion
     */
    private final Map<Stmt, Map<Stmt, Assrt>> implGatheringOp = new HashMap<>();

    /**
     * Map for and operations:
     * <p>
     * f --> { f(A, B, ...) => A , f(A, B, ...) => B, ... }
     */
    private final Map<Stmt, Assrt[]> andPart = new HashMap<>();

    public ConjunctionInfo(final ImplicationInfo implInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.implInfo = implInfo;

        for (final Assrt assrt : assrtList)
            findGatheringRules(assrt);

        for (final Assrt assrt : assrtList)
            findPartRules(assrt);

        for (final Assrt assrt : assrtList)
            findGatheringImplRules(assrt);

        filterAndOperations();
    }

    private void findGatheringImplRules(final Assrt assrt) {
        // if (assrt.getLabel().equals("jca"))
        // assrt.toString();

        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length < 2)
            return;
        final ParseNode root = assrtTree.getRoot();
        final Stmt implStmt = root.stmt;

        if (!implInfo.isImplOperator(implStmt))
            return;

        assert root.child.length == 2;

        final ParseNode prefix = root.child[0];

        final ParseNode core = root.child[1];

        final Stmt stmt = core.stmt;

        if (!gatheringOp.containsKey(stmt))
            return;

        if (!TrUtil.isVarNode(prefix))
            return;

        final VarHyp prefixVar = (VarHyp)prefix.stmt;

        final VarHyp[] vars = TrUtil.getHypToVarMap(assrt, prefixVar);

        if (vars == null)
            return;
        assert vars.length >= 2;

        for (int i = 0; i < vars.length; i++) {
            final ParseNode logRoot = logHyps[i].getExprParseTree().getRoot();
            if (logRoot.stmt != implStmt)
                return;
            if (logRoot.child[0].stmt != prefixVar)
                return;
            if (!TrUtil.isVarNode(logRoot.child[1]))
                return;
        }

        if (core.child.length != vars.length)
            return;

        for (int i = 0; i < core.child.length; i++) {
            final ParseNode child = core.child[i];
            if (child.stmt != vars[i])
                return;
        }

        Map<Stmt, Assrt> gathMap = implGatheringOp.get(implStmt);

        if (gathMap == null) {
            gathMap = new HashMap<>();
            implGatheringOp.put(implStmt, gathMap);
        }

        if (gathMap.containsKey(stmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_IMPL_GATHER_ASSRTS, assrt,
            assrt.getFormula());

        gathMap.put(stmt, assrt);
    }

    private void findGatheringRules(final Assrt assrt) {
        // Debug: pm3.2i

        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length < 2)
            return;
        final VarHyp[] vars = TrUtil.getHypToVarMap(assrt);

        if (vars == null)
            return;
        assert vars.length >= 2;

        for (int i = 0; i < vars.length; i++)
            if (!TrUtil.isVarNode(logHyps[i].getExprParseTree().getRoot()))
                return;

        final ParseNode root = assrtTree.getRoot();

        if (root.child.length != vars.length)
            return;

        for (int i = 0; i < root.child.length; i++) {
            final ParseNode child = root.child[i];
            if (child.stmt != vars[i])
                return;
        }
        final Stmt stmt = root.stmt;

        if (gatheringOp.containsKey(stmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_GATHER_ASSRTS, assrt,
            assrt.getFormula());

        gatheringOp.put(stmt, assrt);
    }

    private void findPartRules(final Assrt assrt) {
        // if (assrt.getLabel().equals("pm3.2i"))
        // assrt.toString();

        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 1)
            return;

        final ParseNode root = assrtTree.getRoot();

        if (!TrUtil.isVarNode(root))
            return;

        final ParseNode logRoot = logHyps[0].getExprParseTree().getRoot();
        final Stmt stmt = logRoot.stmt;

        if (!gatheringOp.containsKey(stmt))
            return;

        int num = -1;
        final ParseNode[] ars = logRoot.child;
        for (int i = 0; i < ars.length; i++) {
            final ParseNode arg = ars[i];
            if (!TrUtil.isVarNode(arg))
                return;
            if (arg.stmt == root.stmt)
                num = i;
        }

        if (num == -1)
            return;

        Assrt[] rules = andPart.get(stmt);
        if (rules == null) {
            rules = new Assrt[ars.length];
            andPart.put(stmt, rules);
        }

        if (rules[num] != null)
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_GATHER_PART, num, assrt,
            assrt.getFormula());

        rules[num] = assrt;
    }

    private void filterAndOperations() {
        final Set<Stmt> needRemove = new HashSet<>();

        for (final Entry<Stmt, Assrt[]> elem : andPart.entrySet())
            for (final Assrt assrt : elem.getValue())
                if (assrt == null)
                    needRemove.add(elem.getKey());

        for (final Stmt stmt : needRemove) {
            andPart.remove(stmt);

            output.dbgMessage(dbg, TrConstants.ERRMSG_NOT_AND_OP, stmt);
        }
    }

    private void separateByAndInternal(final ParseNode input,
        final List<ParseNode> res)
    {
        if (!isAndOperation(input.stmt)) {
            res.add(input);
            return;
        }

        for (final ParseNode child : input.child)
            separateByAndInternal(child, res);
    }

    public List<ParseNode> separateByAnd(final ParseNode input) {
        final List<ParseNode> res = new ArrayList<>();
        separateByAndInternal(input, res);
        return res;
    }

    private static class IndexCounter {
        public int idx = 0;
        public final GenProofStepStmt[] hyps;

        public IndexCounter(final GenProofStepStmt[] hyps) {
            this.hyps = hyps;
        }
    }

    private GenProofStepStmt concatenateInTheSamePatternInternal(
        final ParseNode andPattern, final IndexCounter counter,
        final WorksheetInfo info)
    {
        final Stmt stmt = andPattern.stmt;
        final int length = andPattern.child.length;
        if (!isAndOperation(stmt))
            return counter.hyps[counter.idx++];

        // .formulaParseTree.getRoot()
        final ParseNode[] children = new ParseNode[length];
        ParseNode resNode = new ParseNode(stmt, children);

        final GenProofStepStmt genHyps[] = new GenProofStepStmt[length];

        boolean hasPrefix = false;

        for (int i = 0; i < length; i++) {
            final ParseNode andChild = andPattern.child[i];
            final GenProofStepStmt chidStep = concatenateInTheSamePatternInternal(
                andChild, counter, info);
            children[i] = chidStep.getCore();
            genHyps[i] = chidStep;
            hasPrefix = hasPrefix || chidStep.hasPrefix();
        }

        final ProofStepStmt hyps[] = new ProofStepStmt[length];

        if (!hasPrefix) {
            for (int i = 0; i < length; i++)
                hyps[i] = genHyps[i].getSimpleStep();
            final Assrt assrt = gatheringOp.get(stmt);
            final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(resNode,
                hyps, assrt);

            return new GenProofStepStmt(stepTr, null);
        }

        for (int i = 0; i < length; i++)
            if (genHyps[i].hasPrefix())
                hyps[i] = genHyps[i].getImplicationStep();
            else {
                final ProofStepStmt hypStep = implInfo.applyStubRule(info,
                    genHyps[i].getSimpleStep());
                hyps[i] = hypStep;
            }

        final Map<Stmt, Assrt> gMap = implGatheringOp.get(info.implStatement);
        assert gMap != null;
        final Assrt assrt = gMap.get(stmt);
        assert assrt != null;

        resNode = TrUtil.createBinaryNode(info.implStatement, info.implPrefix,
            resNode);

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(resNode,
            hyps, assrt);

        return new GenProofStepStmt(stepTr, info.implPrefix);

    }

    /**
     * Concatenates the list of hypotheses into one hypothesis. The 'andPattern'
     * is used for construction of this one hypothesis.
     *
     * @param hyps input hypotheses list
     * @param andPattern template
     * @param info the work sheet info
     * @return result one hypothesis
     */
    public GenProofStepStmt concatenateInTheSamePattern(
        final GenProofStepStmt[] hyps, final ParseNode andPattern,
        final WorksheetInfo info)
    {
        final IndexCounter counter = new IndexCounter(hyps);
        final GenProofStepStmt res = concatenateInTheSamePatternInternal(
            andPattern, counter, info);
        assert counter.hyps.length == counter.idx;
        return res;
    }

    public boolean isAndOperation(final Stmt stmt) {
        return andPart.containsKey(stmt);
    }
}
