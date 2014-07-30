package mmj.transforms;

import java.util.List;

import mmj.lang.*;
import mmj.pa.*;
import mmj.verify.VerifyProofs;

/**
 * This contains information for possible automatic transformations.
 * <p>
 * Canonical form for the parse node is a parse node with sorted
 * commutative/associative transformations.
 * <p>
 * The functions in this class are separated into 4 parts:
 * <ul>
 * <li>Data structures initialization
 * <li>Transformations
 * <li>Canonical form comparison
 * <li>Auxiliary functions
 * </ul>
 */
public class TransformationManager {

    private final boolean dbg = true;

    protected TrOutput output;

    /** It is necessary for formula construction */
    private final VerifyProofs verifyProofs;

    /** The information about equivalence rules */
    public final EquivalenceInfo eqInfo;

    public final ImplicationInfo implInfo;

    /** The information about replace rules */
    public final ReplaceInfo replInfo;

    /** The information about closure rules */
    public final ClosureInfo clInfo;

    public final AssociativeInfo assocInfo;

    public final CommutativeInfo comInfo;

    /** The symbol like |- in set.mm */
    protected Cnst provableLogicStmtTyp;

    /**
     * Note: Here will be performed a lot of work during the construction of
     * this class!
     * 
     * @param assrtList the list all library asserts
     * @param provableLogicStmtTyp this constant indicates
     *            "provable logic statement type"
     * @param messages the message manager
     * @param verifyProofs the proof verification is needed for some actions
     */
    public TransformationManager(final List<Assrt> assrtList,
        final Cnst provableLogicStmtTyp, final Messages messages,
        final VerifyProofs verifyProofs)
    {
        output = new TrOutput(messages);
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        eqInfo = new EquivalenceInfo(assrtList, output, dbg);

        clInfo = new ClosureInfo(assrtList, output, dbg);

        implInfo = new ImplicationInfo(eqInfo, assrtList, output, dbg);

        replInfo = new ReplaceInfo(eqInfo, assrtList, output, dbg);

        assocInfo = new AssociativeInfo(eqInfo, clInfo, replInfo, assrtList,
            output, dbg);

        comInfo = new CommutativeInfo(eqInfo, clInfo, assrtList, output, dbg);
    }

    // ----------------------------

    // ------------------------------------------------------------------------
    // ---------------------Public transformation functions--------------------
    // ------------------------------------------------------------------------

    /**
     * The main function to create transformation.
     * 
     * @param node the source node
     * @param info the information about previous steps
     * @return the transformation
     */
    public Transformation createTransformation(final ParseNode node,
        final WorksheetInfo info)
    {
        final Stmt stmt = node.getStmt();

        final Assrt[] replAsserts = replInfo.getReplaceAsserts(stmt);

        boolean isCom = false;
        final GeneralizedStmt comProp = comInfo
            .getGenStmtForComNode(node, info);
        if (comProp != null)
            isCom = true;

        boolean isAssoc = false;
        final GeneralizedStmt assocProp = assocInfo.getGenStmtForAssocNode(
            node, info);
        if (assocProp != null)
            isAssoc = true;

        boolean isAssocCom = false;
        if (isAssoc) {
            final Assrt comAssocAssrt = comInfo.getComOp(assocProp);
            if (comAssocAssrt != null)
                isAssocCom = true;
        }

        final boolean subTreesCouldBeRepl = replAsserts != null;

        if (!subTreesCouldBeRepl)
            return new IdentityTransformation(this, node);

        if (isAssocCom)
            // TODO: check the property!
            return new AssocComTransformation(this, node,
                AssocTree.createAssocTree(node, assocProp, info), assocProp);
        else if (isCom)
            return new CommutativeTransformation(this, node, comProp);

        else if (isAssoc)
            return new AssociativeTransformation(this, node,
                AssocTree.createAssocTree(node, assocProp, info), assocProp);
        else if (subTreesCouldBeRepl)
            return new ReplaceTransformation(this, node);

        // TODO: make the string constant!
        throw new IllegalStateException(
            "Error in createTransformation() algorithm");
    }

    public ParseNode getCanonicalForm(final ParseNode originalNode,
        final WorksheetInfo info)
    {
        return createTransformation(originalNode, info).getCanonicalNode(info);
    }

    // ------------------------------------------------------------------------
    // --------------------------Entry point part------------------------------
    // ------------------------------------------------------------------------

    private void performTransformation(final WorksheetInfo info,
        final ProofStepStmt source, final Assrt impl)
    {
        final Transformation dsTr = createTransformation(
            info.derivStep.formulaParseTree.getRoot(), info);
        final Transformation tr = createTransformation(
            source.formulaParseTree.getRoot(), info);

        final ProofStepStmt eqResult = tr.transformMeToTarget(dsTr, info);
        eqResult.toString();

        final boolean isNormalOrder = TrUtil.isVarNode(impl.getLogHypArray()[0]
            .getExprParseTree().getRoot());

        final ProofStepStmt[] hypDerivArray = isNormalOrder ? new ProofStepStmt[]{
                source, eqResult}
            : new ProofStepStmt[]{eqResult, source};

        final String[] hypStep = new String[hypDerivArray.length];
        for (int i = 0; i < hypStep.length; i++)
            hypStep[i] = hypDerivArray[i].getStep();

        info.derivStep.setRef(impl);
        info.derivStep.setRefLabel(impl.getLabel());
        info.derivStep.setHypList(hypDerivArray);
        info.derivStep.setHypStepList(hypStep);
        info.derivStep.setAutoStep(false);

        return;
    }

    /**
     * Tries to unify the derivation step by some automatic transformations
     * 
     * @param proofWorksheet the proof work sheet
     * @param derivStep the derivation step
     * @return true if it founds possible unification
     */
    private List<DerivationStep> tryToFindTransformationsCore(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
    {
        final WorksheetInfo info = new WorksheetInfo(proofWorksheet, derivStep,
            verifyProofs, provableLogicStmtTyp);

        final Cnst derivType = info.derivStep.formulaParseTree.getRoot()
            .getStmt().getTyp();
        final Assrt implAssrt = implInfo.getEqImplication(derivType);
        if (implAssrt == null)
            return null;

        final Transformation dsTr = createTransformation(
            derivStep.formulaParseTree.getRoot(), info);
        final ParseNode dsCanonicalForm = dsTr.getCanonicalNode(info);
        derivStep.setCanonicalForm(dsCanonicalForm);

        output.dbgMessage(dbg, "I-TR-DBG Step %s has canonical form: %s",
            derivStep, getCanonicalFormula(derivStep));

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject == derivStep)
                break;

            if (!(proofWorkStmtObject instanceof ProofStepStmt))
                continue;

            final ProofStepStmt candidate = (ProofStepStmt)proofWorkStmtObject;

            if (candidate.getCanonicalForm() == null) {
                candidate.setCanonicalForm(getCanonicalForm(
                    candidate.formulaParseTree.getRoot(), info));

                output.dbgMessage(dbg, "I-TR-DBG Step %s has canonical form: %s",
                    candidate, getCanonicalFormula(candidate));
            }

            if (derivStep.getCanonicalForm().isDeepDup(
                candidate.getCanonicalForm()))
            {
                output.dbgMessage(dbg,
                    "I-TR-DBG found canonical forms correspondance: %s and %s",
                    candidate, derivStep);
                performTransformation(info, candidate, implAssrt);

                // confirm unification for derivStep also!
                info.newSteps.add(derivStep);
                return info.newSteps;
            }
        }
        return null;
    }

    /**
     * The main entry point transformation function. This function tries to find
     * the transformation which leads to the derivation step from earlier steps.
     * 
     * @param proofWorksheet the proof work sheet
     * @param derivStep the
     * @return the list of generated steps (and also derivStep) or null if the
     *         transformation was not found.
     */
    public List<DerivationStep> tryToFindTransformations(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
    {
        try {
            return tryToFindTransformationsCore(proofWorksheet, derivStep);
        } catch (final Throwable e) {
            // TODO: make string error constant!
            if (dbg)
                e.printStackTrace();

            output.errorMessage(TrConstants.ERRMSG_UNEXPECTED_EXCEPTION,
                e.toString());
            return null;
        }
    }
    // ------------------------------------------------------------------------
    // ------------------------Debug functions---------------------------------
    // ------------------------------------------------------------------------

    /**
     * This function is needed for debug
     * 
     * @param node the input node
     * @return the corresponding formula
     */
    protected Formula getFormula(final ParseNode node) {
        final ParseTree tree = new ParseTree(node);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        return generatedFormula;
    }

    /**
     * This function is needed for debug
     * 
     * @param proofStmt the proof statement
     * @return the corresponding canonical formula
     */
    protected Formula getCanonicalFormula(final ProofStepStmt proofStmt) {
        return getFormula(proofStmt.getCanonicalForm());
    }
}
