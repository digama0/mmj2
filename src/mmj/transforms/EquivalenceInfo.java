//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

/**
 * This class is used for equivalence transformations.
 * <p>
 * Note: Now there is a restriction: the library has to define only one
 * equivalence operator for every type. In set.mm we have 2 types: wff, class.
 */
public class EquivalenceInfo extends DBInfo {

    /** The map from type to corresponding equivalence operators */
    private final Map<Cnst, Stmt> eqMap = new HashMap<>();

    /**
     * The list of commutative rules for equivalence operators: A = B => B = A
     */
    private final Map<Stmt, Assrt> eqCommutatives = new HashMap<>();

    /**
     * The list of transitive rules for equivalence operators:
     * <p>
     * A = B & B = C => A = C
     */
    private final Map<Stmt, Assrt> eqTransitivies = new HashMap<>();

    /** The indicator that we collected rules in deduction form */
    private boolean fillDeductRules = false;

    /**
     * The map from implication operator to the list of commutative deduction
     * rules for equivalence operators:
     * <p>
     * p -> A = B => p -> B = A
     */
    private final Map<Stmt, Map<Stmt, Assrt>> eqDeductCom = new HashMap<>();

    /**
     * The map from implication operator to the list of transitive deduction
     * rules for equivalence operators:
     * <p>
     * p -> A = B & p -> B = C => p -> A = C
     */
    private final Map<Stmt, Map<Stmt, Assrt>> eqDeductTrans = new HashMap<>();

    // ------------------------------------------------------------------------
    // ------------------------Initialization----------------------------------
    // ------------------------------------------------------------------------

    public EquivalenceInfo(final List<Assrt> assrtList, final TrOutput output,
        final boolean dbg)
    {
        super(output, dbg);

        for (final Assrt assrt : assrtList)
            findEquivalenceCommutativeRules(assrt);

        for (final Assrt assrt : assrtList)
            findEquivalenceTransitiveRules(assrt);

        filterOnlyEqRules();
    }

    public void fillDeductRules(final List<Assrt> assrtList,
        final ImplicationInfo implInfo)
    {
        assert !fillDeductRules;
        fillDeductRules = true;

        for (final Assrt assrt : assrtList)
            findEquivalenceCommutativeDeductionRules(assrt, implInfo);

        for (final Assrt assrt : assrtList)
            findEquivalenceTransitiveDeductionRules(assrt, implInfo);

        final Collection<Stmt> implOps = implInfo.getImplForPrefixOperators();
        for (final Stmt op : implOps) {
            final Map<Stmt, Assrt> dedCom = eqDeductCom.get(op);
            final Map<Stmt, Assrt> dedTrans = eqDeductTrans.get(op);
            for (final Stmt eqOp : eqCommutatives.keySet()) {
                if (!dedCom.containsKey(eqOp))
                    output.errorMessage(
                        TrConstants.ERRMSG_MISSING_EQUAL_COMMUT_DEDUCT_RULE, op,
                        eqOp);
                if (!dedTrans.containsKey(eqOp))
                    output.errorMessage(
                        TrConstants.ERRMSG_MISSING_EQUAL_TRANSIT_DEDUCT_RULE,
                        op, eqOp);
            }
        }
    }

    /**
     * Find commutative equivalence rules, like A = B => B = A
     * <p>
     *
     * @param assrt the candidate
     */
    private void findEquivalenceCommutativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        final ParseTree hypTree = logHyps[0].getExprParseTree();

        if (varHypArray.length != 2)
            return;

        if (hypTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getMaxDepth() != 2)
            return;

        final ParseNode root = assrtTree.getRoot();
        if (root.child.length != 2)
            return;

        final Stmt stmt = root.stmt;

        final ParseNode hypRoot = hypTree.getRoot();
        if (hypRoot.stmt != stmt)
            return;

        if (hypRoot.child[0].stmt != root.child[1].stmt)
            return;

        if (hypRoot.child[1].stmt != root.child[0].stmt)
            return;

        if (eqCommutatives.containsKey(stmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_EQUIV_COMM_ASSRTS, assrt,
            assrt.getFormula());

        eqCommutatives.put(stmt, assrt);
    }

    private void findEquivalenceTransitiveDeductionRules(final Assrt assrt,
        final ImplicationInfo implInfo)
    {
        final VarHyp[] mandVarHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 2)
            return;

        final ParseTree hyp1Tree = logHyps[0].getExprParseTree();
        final ParseTree hyp2Tree = logHyps[1].getExprParseTree();

        if (mandVarHypArray.length != 3)
            return;

        if (hyp1Tree.getMaxDepth() != 3 || hyp2Tree.getMaxDepth() != 3)
            return;

        if (assrtTree.getMaxDepth() != 3)
            return;

        final ParseNode root = assrtTree.getRoot();
        if (root.child.length != 2)
            return;

        final Stmt implOp = root.stmt;
        if (!implInfo.isImplForPrefixOperator(implOp))
            return;

        final ParseNode hyp1Root = hyp1Tree.getRoot();
        final ParseNode hyp2Root = hyp2Tree.getRoot();

        if (hyp1Root.stmt != implOp)
            return;
        if (hyp2Root.stmt != implOp)
            return;

        final ParseNode prefix = root.child[0];
        if (prefix.stmt != mandVarHypArray[0])
            return;

        final ParseNode hyp1Prefix = hyp1Root.child[0];
        if (hyp1Prefix.stmt != mandVarHypArray[0])
            return;
        final ParseNode hyp2Prefix = hyp2Root.child[0];
        if (hyp2Prefix.stmt != mandVarHypArray[0])
            return;

        final ParseNode core = root.child[1];
        final ParseNode hyp1Core = hyp1Root.child[1];
        final ParseNode hyp2Core = hyp2Root.child[1];

        final Stmt stmt = core.stmt;

        if (!isEquivalence(stmt))
            return;

        if (hyp1Core.stmt != stmt)
            return;
        if (hyp2Core.stmt != stmt)
            return;

        // check for 'A' in 'A = B & B = C => A = C'
        if (hyp1Core.child[0].stmt != core.child[0].stmt)
            return;

        // check for 'B' in 'A = B & B = C'
        if (hyp1Core.child[1].stmt != hyp2Core.child[0].stmt)
            return;

        // check for 'C' in 'A = B & B = C => A = C'
        if (hyp2Core.child[1].stmt != core.child[1].stmt)
            return;

        Map<Stmt, Assrt> eqTransMap = eqDeductTrans.get(implOp);
        if (eqTransMap == null) {
            eqTransMap = new HashMap<>();
            eqDeductTrans.put(implOp, eqTransMap);
        }

        if (eqTransMap.containsKey(stmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_EQUIV_TRANS_DED_ASSRTS, assrt,
            assrt.getFormula());

        eqTransMap.put(stmt, assrt);
    }

    /**
     * Find transitive equivalence rules, like A = B & B = C => A = C
     * <p>
     *
     * @param assrt the candidate
     */
    private void findEquivalenceTransitiveRules(final Assrt assrt) {
        final VarHyp[] mandVarHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 2)
            return;

        final ParseTree hyp1Tree = logHyps[0].getExprParseTree();
        final ParseTree hyp2Tree = logHyps[1].getExprParseTree();

        if (mandVarHypArray.length != 2)
            return;

        if (hyp1Tree.getMaxDepth() != 2 || hyp2Tree.getMaxDepth() != 2)
            return;

        if (assrtTree.getMaxDepth() != 2)
            return;

        final ParseNode root = assrtTree.getRoot();
        if (root.child.length != 2)
            return;

        final Stmt stmt = root.stmt;

        final ParseNode hyp1Root = hyp1Tree.getRoot();
        final ParseNode hyp2Root = hyp2Tree.getRoot();

        if (hyp1Root.stmt != stmt)
            return;

        if (hyp2Root.stmt != stmt)
            return;

        // check for 'A' in 'A = B & B = C => A = C'
        if (hyp1Root.child[0].stmt != root.child[0].stmt)
            return;

        // check for 'B' in 'A = B & B = C'
        if (hyp1Root.child[1].stmt != hyp2Root.child[0].stmt)
            return;

        // check for 'C' in 'A = B & B = C => A = C'
        if (hyp2Root.child[1].stmt != root.child[1].stmt)
            return;

        if (eqTransitivies.containsKey(stmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_EQUIV_TRANS_ASSRTS, assrt,
            assrt.getFormula());

        eqTransitivies.put(stmt, assrt);
    }

    /**
     * We found candidates for equivalence from commutative and transitive
     * sides. Now compare results and remove unsuitable!
     */
    private void filterOnlyEqRules() {
        while (true) {
            boolean changed = false;

            for (final Stmt eq : eqTransitivies.keySet())
                if (!eqCommutatives.containsKey(eq)) {
                    eqTransitivies.remove(eq);
                    changed = true;
                    break;
                }

            for (final Stmt eq : eqCommutatives.keySet())
                if (!eqTransitivies.containsKey(eq)) {
                    eqCommutatives.remove(eq);
                    changed = true;
                    break;
                }

            if (!changed)
                break;
        }

        // Debug output:
        for (final Stmt eq : eqTransitivies.keySet())
            output.dbgMessage(dbg, TrConstants.ERRMSG_EQUIV_RULES, eq,
                eqCommutatives.get(eq).getFormula(),
                eqTransitivies.get(eq).getFormula());

        for (final Stmt eq : eqCommutatives.keySet()) {
            final Assrt assrt = eqCommutatives.get(eq);

            final ParseTree assrtTree = assrt.getExprParseTree();
            final Cnst type = assrtTree.getRoot().child[0].stmt.getTyp();

            if (eqMap.containsKey(type)) {
                output.errorMessage(
                    TrConstants.ERRMSG_MORE_THEN_ONE_EQUALITY_OPERATOR, eq,
                    eqMap.get(type), type);
                continue;
            }

            eqMap.put(type, eq);

            output.dbgMessage(dbg, TrConstants.ERRMSG_TYPE_EQUIV, type, eq);
        }
    }
    // can be tested on eqcomd
    private void findEquivalenceCommutativeDeductionRules(final Assrt assrt,
        final ImplicationInfo implInfo)
    {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        final ParseTree hypTree = logHyps[0].getExprParseTree();

        if (varHypArray.length != 3)
            return;

        if (hypTree.getMaxDepth() != 3)
            return;

        if (assrtTree.getMaxDepth() != 3)
            return;

        final ParseNode root = assrtTree.getRoot();

        if (root.child.length != 2)
            return;

        final Stmt implOp = root.stmt;
        if (!implInfo.isImplForPrefixOperator(implOp))
            return;

        final ParseNode hypRoot = hypTree.getRoot();
        if (hypRoot.stmt != implOp)
            return;

        final ParseNode prefix = root.child[0];
        if (prefix.stmt != varHypArray[0])
            return;

        final ParseNode hypPrefix = hypRoot.child[0];
        if (hypPrefix.stmt != varHypArray[0])
            return;

        final ParseNode core = root.child[1];
        final ParseNode hypCore = hypRoot.child[1];

        final Stmt stmt = core.stmt;

        if (!isEquivalence(stmt))
            return;

        if (hypCore.stmt != stmt)
            return;

        assert core.child.length == 2;

        if (hypCore.child[0].stmt != core.child[1].stmt)
            return;

        if (hypCore.child[1].stmt != core.child[0].stmt)
            return;

        Map<Stmt, Assrt> eqComMap = eqDeductCom.get(implOp);
        if (eqComMap == null) {
            eqComMap = new HashMap<>();
            eqDeductCom.put(implOp, eqComMap);
        }

        if (eqComMap.containsKey(stmt))
            return;

        output.dbgMessage(dbg, TrConstants.ERRMSG_EQUIV_COMM_DED_ASSRTS, assrt,
            assrt.getFormula());

        eqComMap.put(stmt, assrt);
    }

    // ------------------------------------------------------------------------
    // ------------------------Transformations---------------------------------
    // ------------------------------------------------------------------------

    /**
     * Creates equivalence node (e.g. a = b )
     *
     * @param left the left node
     * @param right the right node
     * @return the equivalence node
     */
    public ParseNode createEqNode(final ParseNode left, final ParseNode right) {
        final Cnst type = left.stmt.getTyp();
        assert type == right.stmt.getTyp();
        final Stmt equalStmt = getEqStmt(type);
        assert equalStmt != null;

        final ParseNode res = TrUtil.createBinaryNode(equalStmt, left, right);
        return res;
    }

    /**
     * Creates reverse step for another equivalence step (e.g. b = a for a = b)
     *
     * @param info the work sheet info
     * @param source the source (e.g. a = b)
     * @return the reverse step
     */
    public GenProofStepStmt createReverseStep(final WorksheetInfo info,
        final GenProofStepStmt source)
    {
        /*
        if (!source.hasPrefix())
            return new GenProofStepStmt(createSimpleReverseStep(info,
                source.getSimpleStep()), null);
         */
        final ParseNode core = source.getCore();
        final Stmt equalStmt = core.stmt;
        final ParseNode left = core.child[0];
        final ParseNode right = core.child[1];

        ParseNode revNode = TrUtil.createBinaryNode(equalStmt, right, left);

        final Assrt eqComm;
        if (!source.hasPrefix())
            eqComm = getEqCommutative(equalStmt);
        else {
            revNode = TrUtil.createBinaryNode(info.implStatement,
                info.implPrefix, revNode);
            eqComm = getEqDeductCommutative(info.implStatement, equalStmt);
        }

        assert eqComm != null;
        final ProofStepStmt res = info.getOrCreateProofStepStmt(revNode,
            new ProofStepStmt[]{source.getAnyStep()}, eqComm);

        return new GenProofStepStmt(res, source.getPrefixOrNull());
    }
    /**
     * This function creates transitive inference for two steps (= is the
     * example of equivalence operator).
     *
     * @param info the work sheet info
     * @param first the first statement (e.g. a = b )
     * @param second the second statement (e.g. b = c )
     * @return the result statement (e.g. a = c )
     */
    public GenProofStepStmt getTransitiveStep(final WorksheetInfo info,
        final GenProofStepStmt first, final GenProofStepStmt second)
    {
        if (first == null)
            return second;

        final ParseNode firstCore = first.getCore();
        final ParseNode secondCore = second.getCore();
        final Stmt equalStmt = firstCore.stmt;

        assert equalStmt == secondCore.stmt;
        assert firstCore.child[1].isDeepDup(secondCore.child[0]);

        ParseNode resNode = TrUtil.createBinaryNode(equalStmt,
            firstCore.child[0], secondCore.child[1]);

        ParseNode prefix = null;
        ProofStepStmt firstStep = first.getAnyStep();
        ProofStepStmt secondStep = second.getAnyStep();
        final Assrt transitive;
        if (first.hasPrefix() || second.hasPrefix()) {
            prefix = info.implPrefix;
            if (!first.hasPrefix())
                firstStep = info.trManager.implInfo.applyStubRule(info,
                    firstStep);
            if (!second.hasPrefix())
                secondStep = info.trManager.implInfo.applyStubRule(info,
                    secondStep);

            transitive = getEqDeductTransitive(info.implStatement, equalStmt);
            resNode = TrUtil.createBinaryNode(info.implStatement,
                info.implPrefix, resNode);
        }
        else
            transitive = getEqTransitive(equalStmt);

        final ProofStepStmt resStmt = info.getOrCreateProofStepStmt(resNode,
            new ProofStepStmt[]{firstStep, secondStep}, transitive);

        return new GenProofStepStmt(resStmt, prefix);

    }

    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------

    public boolean isEquivalence(final Stmt stmt) {
        return eqMap.containsValue(stmt);
    }

    public Stmt getEqStmt(final Cnst type) {
        return eqMap.get(type);
    }

    private Assrt getEqCommutative(final Stmt stmt) {
        return eqCommutatives.get(stmt);
    }

    private Assrt getEqTransitive(final Stmt stmt) {
        return eqTransitivies.get(stmt);
    }

    private Assrt getEqDeductCommutative(final Stmt implOp, final Stmt eqOp) {
        final Map<Stmt, Assrt> dedCom = eqDeductCom.get(implOp);
        if (dedCom == null)
            return null;
        return dedCom.get(eqOp);
    }
    private Assrt getEqDeductTransitive(final Stmt implOp, final Stmt eqOp) {
        final Map<Stmt, Assrt> dedTrans = eqDeductTrans.get(implOp);
        if (dedTrans == null)
            return null;
        return dedTrans.get(eqOp);
    }
}
