package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;

public class ReplaceInfo extends DBInfo {
    /** The information about equivalence rules */
    private EquivalenceInfo eqInfo;

    /** The list of statements with possible variable replace */
    protected Map<Stmt, Assrt[]> replaceOp;

    public void initMe(final EquivalenceInfo eqInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super.initMe(output, dbg);
        this.eqInfo = eqInfo;

        replaceOp = new HashMap<Stmt, Assrt[]>();
        for (final Assrt assrt : assrtList)
            findReplaceRules(assrt);
    }
    /**
     * Filters replace rules, like A = B => g(A) = g(B)
     * 
     * @param assrt the candidate
     */
    private void findReplaceRules(final Assrt assrt) {
        assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 3)
            return;

        if (eqInfo.getEqCommutative(assrtTree.getRoot().getStmt()) == null)
            return;

        final LogHyp testHyp = logHyps[0];

        final ParseTree hypTree = testHyp.getExprParseTree();

        if (eqInfo.getEqCommutative(hypTree.getRoot().getStmt()) == null)
            return;

        final ParseNode[] hypSubTrees = hypTree.getRoot().getChild();

        assert hypSubTrees.length == 2 : "It should be the equivalence rule!";

        if (!TrUtil.isVarNode(hypSubTrees[0])
            || !TrUtil.isVarNode(hypSubTrees[1]))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

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

        Assrt[] repl = replaceOp.get(stmt);

        if (repl == null) {
            repl = new Assrt[subTrees[0].getChild().length];
            replaceOp.put(stmt, repl);
        }

        // it is the first such assrt;
        if (repl[replPos] != null)
            return;

        repl[replPos] = assrt;

        output.dbgMessage(dbg, "I-DBG Replace assrts: %s: %s", assrt,
            assrt.getFormula());
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
        final Assrt[] replAsserts = getReplaceAsserts(prevVersion.getStmt());
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

    // Map<Stmt, Assrt[]> replaceOp;
    public Assrt[] getReplaceAsserts(final Stmt stmt) {
        return replaceOp.get(stmt);
    }

}
