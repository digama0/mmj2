package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

public class ReplaceInfo extends DBInfo {
    /** The information about equivalence rules */
    private final EquivalenceInfo eqInfo;

    private final ImplicationInfo implInfo;

    /** The list of statements with possible variable replace */
    private final Map<Stmt, Assrt[]> replaceOp = new HashMap<Stmt, Assrt[]>();

    /** The list of statements with possible variable replace */
    private final Map<Stmt, Assrt[]> implReplaceOp = new HashMap<Stmt, Assrt[]>();

    // ------------------------------------------------------------------------
    // ------------------------Initialization----------------------------------
    // ------------------------------------------------------------------------

    public ReplaceInfo(final EquivalenceInfo eqInfo,
        final ImplicationInfo implInfo, final List<Assrt> assrtList,
        final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.eqInfo = eqInfo;
        this.implInfo = implInfo;

        for (final Assrt assrt : assrtList) {
            findReplaceRules(assrt);
            findImplReplaceRules(assrt);
        }

        if (dbg) {
            dbgCompare(replaceOp, implReplaceOp, "replaceOp");
            dbgCompare(implReplaceOp, replaceOp, "implReplaceOp");
        }
    }

    private void dbgCompare(final Map<Stmt, Assrt[]> resMap1,
        final Map<Stmt, Assrt[]> resMap2, final String mapName1)
    {
        for (final Entry<Stmt, Assrt[]> elem : resMap1.entrySet()) {
            final Assrt[] assrts = elem.getValue();
            final Stmt stmt = elem.getKey();

            final Assrt[] otherAssrts = resMap2.get(stmt);

            if (otherAssrts == null) {
                output
                    .dbgMessage(
                        dbg,
                        "I-TR-DBG Replace assrts: unique %s assert collection for %s",
                        mapName1, stmt.toString());
                continue;
            }

            assert assrts.length == otherAssrts.length;

            for (int i = 0; i < assrts.length; i++)
                if (assrts[i] != null && otherAssrts[i] == null)
                    output
                        .dbgMessage(
                            dbg,
                            "I-TR-DBG Replace assrts: unique %s assert for %s in position %d",
                            mapName1, stmt.toString(), i);
        }
    }

    /**
     * Filters replace rules, like A = B => g(A) = g(B)
     * 
     * @param assrt the candidate
     */
    private void findReplaceRules(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 3)
            return;

        final ParseNode replRoot = assrtTree.getRoot();
        final ParseNode hypNode = logHyps[0].getExprParseTree().getRoot();

        coreCheck(assrt, replRoot, hypNode, replaceOp);
    }

    /**
     * Filters replace rules in implication form, like A = B -> g(A) = g(B)
     * 
     * @param assrt the candidate
     */
    private void findImplReplaceRules(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 0)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 4)
            return;

        final ParseNode root = assrtTree.getRoot();

        if (!implInfo.isImplOperator(root.getStmt()))
            return;

        final ParseNode replRoot = root.getChild()[1];
        final ParseNode hypNode = root.getChild()[0];

        coreCheck(assrt, replRoot, hypNode, implReplaceOp);
    }

    // the main search algorithm
    private void coreCheck(final Assrt assrt, final ParseNode replRoot,
        final ParseNode hypNode, final Map<Stmt, Assrt[]> resMap)
    {

        if (eqInfo.getEqCommutative(replRoot.getStmt()) == null)
            return;

        if (eqInfo.getEqCommutative(hypNode.getStmt()) == null)
            return;

        final ParseNode[] hypSubTrees = hypNode.getChild();

        assert hypSubTrees.length == 2 : "It should be the equivalence rule!";

        if (!TrUtil.isVarNode(hypSubTrees[0])
            || !TrUtil.isVarNode(hypSubTrees[1]))
            return;

        final ParseNode[] subTrees = replRoot.getChild();

        assert subTrees.length == 2 : "It should be the equivalence rule!";

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ParseNode[] leftChild = subTrees[0].getChild();
        final ParseNode[] rightChild = subTrees[1].getChild();

        // Fast compare, change if the depth of this assrt statement tree
        // could be more then 3
        int replPos = -1;
        replaceCheck: for (int i = 0; i < leftChild.length; i++)
            if (leftChild[i].getStmt() != rightChild[i].getStmt()) {
                // Another place for replace? It is strange!
                if (replPos != -1)
                    return;

                // We found the replace
                replPos = i;

                // Check that it is actually the swap of two variables
                for (int k = 0; k < 2; k++) {
                    final int m = (k + 1) % 2; // the other index
                    if (leftChild[i].getStmt() == hypSubTrees[k].getStmt()
                        && rightChild[i].getStmt() == hypSubTrees[m].getStmt())
                        continue replaceCheck;
                }

                return;
            }

        Assrt[] repl = resMap.get(stmt);

        if (repl == null) {
            repl = new Assrt[subTrees[0].getChild().length];
            resMap.put(stmt, repl);
        }

        // it is the first such assrt;
        if (repl[replPos] != null)
            return;

        repl[replPos] = assrt;

        output.dbgMessage(dbg, "I-TR-DBG Replace assrts for %s [%d]: %s: %s",
            stmt, replPos, assrt, assrt.getFormula());
    }

    /**
     * @param stmt the statement
     * @return true if all children in the statement could be replaced with
     *         equal children.
     */
    public boolean isFullReplaceStatement(final Stmt stmt) {
        final Assrt[] replAssrts = replaceOp.get(stmt);

        if (replAssrts == null)
            return false;

        for (final Assrt assrt : replAssrts)
            if (assrt == null)
                return false;

        return true;
    }

    // ------------------------------------------------------------------------
    // ------------------------Transformations---------------------------------
    // ------------------------------------------------------------------------

    /**
     * Creates replace step (e.g. g(A, B, C) <-> g(A, B', C)). Note: the
     * equivalence operators for 'g' and for 'B' could differ!
     * 
     * @param info the work sheet info
     * @param prevVersion the source node (e.g. g(A, B, C) )
     * @param i the position for replace (e.g. 1 (second))
     * @param newSubTree the new child (e.g. B')
     * @param childTrStmt the equivalence of children (e.g. B = B' )
     * @return the replace step
     */
    public ProofStepStmt createReplaceStep(final WorksheetInfo info,
        final ParseNode prevVersion, final int i, final ParseNode newSubTree,
        final ProofStepStmt childTrStmt)
    {
        Assrt[] replAsserts = replaceOp.get(prevVersion.getStmt());
        if (replAsserts != null && replAsserts[i] != null)
            return createReplaceStepHyp(info, prevVersion, i, newSubTree,
                childTrStmt, replAsserts);

        replAsserts = implReplaceOp.get(prevVersion.getStmt());
        assert replAsserts != null;
        assert replAsserts[i] != null;

        return createReplaceStepImpl(info, prevVersion, i, newSubTree,
            childTrStmt, replAsserts);
    }

    private ProofStepStmt createReplaceStepHyp(final WorksheetInfo info,
        final ParseNode prevVersion, final int i, final ParseNode newSubTree,
        final ProofStepStmt childTrStmt, final Assrt[] replAsserts)
    {
        assert replAsserts[i] != null;
        final Stmt equalStmt = replAsserts[i].getExprParseTree().getRoot()
            .getStmt();
        final ParseNode resNode = prevVersion.cloneWithoutChildren();

        // Fill the next child
        // So the new node has form g(A, B', C)
        resNode.getChild()[i] = newSubTree;

        // Create node g(A, B, C) = g(A, B', C)
        final ParseNode stepNode = TrUtil.createBinaryNode(equalStmt,
            prevVersion, resNode);

        // Create statement d:childTrStmt:replAssert
        // |- g(A, B, C) = g(A, B', C)
        final ProofStepStmt stepTr = info.getOrCreateProofStepStmt(stepNode,
            new ProofStepStmt[]{childTrStmt}, replAsserts[i]);
        return stepTr;
    }

    private ProofStepStmt createReplaceStepImpl(final WorksheetInfo info,
        final ParseNode prevVersion, final int i, final ParseNode newSubTree,
        final ProofStepStmt childTrStmt, final Assrt[] replAsserts)
    {
        assert replAsserts[i] != null;
        final ParseNode root = replAsserts[i].getExprParseTree().getRoot();
        final Stmt implStmt = root.getStmt();
        final Stmt equalStmt = root.getChild()[1].getStmt();
        final ParseNode resNode = prevVersion.cloneWithoutChildren();

        // maybe we should clone!
        final ParseNode hypNode = childTrStmt.formulaParseTree.getRoot();

        // Fill the next child
        // So the new node has form g(A, B', C)
        resNode.getChild()[i] = newSubTree;

        // Create node g(A, B, C) = g(A, B', C)
        final ParseNode eqNode = TrUtil.createBinaryNode(equalStmt,
            prevVersion, resNode);

        // Create node B = B' -> g(A, B, C) = g(A, B', C)
        final ParseNode stepNode = TrUtil.createBinaryNode(implStmt, hypNode,
            eqNode);

        // Create statement d:childTrStmt:replAssert
        // |- B = B' -> g(A, B, C) = g(A, B', C)
        final ProofStepStmt implStep = info.getOrCreateProofStepStmt(stepNode,
            new ProofStepStmt[]{}, replAsserts[i]);

        return implInfo.applyImplicationRule(info, childTrStmt, implStep,
            implStmt);
    }

    // ------------------------------------------------------------------------
    // ------------------------------Getters-----------------------------------
    // ------------------------------------------------------------------------

    public boolean[] getPossibleReplaces(final Stmt stmt) {
        final Assrt[][] assrts = new Assrt[][]{replaceOp.get(stmt),
                implReplaceOp.get(stmt)};

        boolean[] res = null;
        for (final Assrt[] assrt : assrts)
            if (assrt != null) {
                res = new boolean[assrt.length];
                for (int i = 0; i < assrt.length; i++)
                    if (assrt[i] != null)
                        res[i] = true;
            }
        return res;
    }
}
