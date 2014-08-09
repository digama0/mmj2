package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

// TODO: maybe we should remove it
public class ConjunctionInfo extends DBInfo {

    /** The list of operations which have form A & B & ... => f(A, B, ...) */
    private final Map<Stmt, Assrt> gatheringOp = new HashMap<Stmt, Assrt>();

    /**
     * Map for and operations:
     * <p>
     * f --> { f(A, B, ...) => A , f(A, B, ...) => B, ... }
     */
    private final Map<Stmt, Assrt[]> andPart = new HashMap<Stmt, Assrt[]>();

    public ConjunctionInfo(final List<Assrt> assrtList, final TrOutput output,
        final boolean dbg)
    {
        super(output, dbg);

        for (final Assrt assrt : assrtList)
            findGatheringRules(assrt);

        for (final Assrt assrt : assrtList)
            findPartRules(assrt);

        filterAndOperations();
    }

    private void findGatheringRules(final Assrt assrt) {
        // if (assrt.getLabel().equals("pm3.2i"))
        // assrt.toString();

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

        if (root.getChild().length != vars.length)
            return;

        for (int i = 0; i < root.getChild().length; i++) {
            final ParseNode child = root.getChild()[i];
            if (child.getStmt() != vars[i])
                return;
        }
        final Stmt stmt = root.getStmt();

        if (gatheringOp.containsKey(stmt))
            return;

        output.dbgMessage(dbg, "I-TR-DBG gathering assrt %s : %s", assrt,
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
        final Stmt stmt = logRoot.getStmt();

        if (!gatheringOp.containsKey(stmt))
            return;

        int num = -1;
        final ParseNode[] ars = logRoot.getChild();
        for (int i = 0; i < ars.length; i++) {
            final ParseNode arg = ars[i];
            if (!TrUtil.isVarNode(arg))
                return;
            if (arg.getStmt() == root.getStmt())
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

        output.dbgMessage(dbg,
            "I-TR-DBG part rule #%d for gathering assrt %s : %s", num, assrt,
            assrt.getFormula());

        rules[num] = assrt;
    }

    private void filterAndOperations() {
        final Set<Stmt> needRemove = new HashSet<Stmt>();

        for (final Entry<Stmt, Assrt[]> elem : andPart.entrySet())
            for (final Assrt assrt : elem.getValue())
                if (assrt == null)
                    needRemove.add(elem.getKey());

        for (final Stmt stmt : needRemove) {
            andPart.remove(stmt);

            output.dbgMessage(dbg,
                "I-TR-DBG statement %s is not and operation", stmt);
        }
    }

    private void separateByAndInternal(final ParseNode input,
        final List<ParseNode> res)
    {
        if (!isAndOperation(input.getStmt())) {
            res.add(input);
            return;
        }

        for (final ParseNode child : input.getChild())
            separateByAndInternal(child, res);
    }

    public List<ParseNode> separateByAnd(final ParseNode input) {
        final List<ParseNode> res = new ArrayList<ParseNode>();
        separateByAndInternal(input, res);
        return res;
    }

    private static class IndexCounter {
        public int idx = 0;
        public final ProofStepStmt[] hyps;

        public IndexCounter(final ProofStepStmt[] hyps) {
            this.hyps = hyps;
        }
    }

    private ProofStepStmt conctinateInTheSamePatternInternal(
        final ParseNode andPattern, final IndexCounter counter,
        final WorksheetInfo info)
    {
        final Stmt stmt = andPattern.getStmt();
        final int length = andPattern.getChild().length;
        if (!isAndOperation(stmt))
            return counter.hyps[counter.idx++];

        // .formulaParseTree.getRoot()
        final ParseNode[] children = new ParseNode[length];
        final ParseNode resNode = new ParseNode(stmt, children);

        final ProofStepStmt hyps[] = new ProofStepStmt[length];

        for (int i = 0; i < length; i++) {
            final ParseNode andChild = andPattern.getChild()[i];
            final ProofStepStmt chidStep = conctinateInTheSamePatternInternal(
                andChild, counter, info);
            children[i] = chidStep.formulaParseTree.getRoot();
            hyps[i] = chidStep;
        }

        final Assrt assrt = gatheringOp.get(stmt);

        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(resNode,
            hyps, assrt);

        return stepTr;

    }
    public ProofStepStmt conctinateInTheSamePattern(final ProofStepStmt[] hyps,
        final ParseNode andPattern, final WorksheetInfo info)
    {
        final IndexCounter counter = new IndexCounter(hyps);
        final ProofStepStmt res = conctinateInTheSamePatternInternal(
            andPattern, counter, info);
        assert counter.hyps.length == counter.idx;
        return res;
    }

    public boolean isAndOperation(final Stmt stmt) {
        return andPart.containsKey(stmt);
    }
}
