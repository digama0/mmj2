//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;
import java.util.function.Predicate;

import mmj.lang.*;
import mmj.pa.*;
import mmj.pa.MacroManager.CallbackType;
import mmj.transforms.ImplicationInfo.ExtractImplResult;
import mmj.transforms.Prover.AssrtProver;
import mmj.transforms.Prover.ProverResult;
import mmj.transforms.Provers.UseWhenPossible;
import mmj.util.TopologicalSorter;
import mmj.verify.VerifyException;
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
    /**
     * This constant indicates whether we should try to indicate implication
     * prefix and perform auto-transformations with it.
     */
    public final boolean supportImplicationPrefix;

    public final boolean dbg;

    public final TrOutput output;

    /** It is necessary for formula construction */
    public final VerifyProofs verifyProofs;

    /** The symbol like |- in set.mm */
    public final Cnst provableLogicStmtTyp;

    /** The information about equivalence rules */
    public final EquivalenceInfo eqInfo;

    public final ImplicationInfo implInfo;

    public final ConjunctionInfo conjInfo;

    /** The information about replace rules */
    public final ReplaceInfo replInfo;

    /** The information about closure rules */
    public final ClosureInfo clInfo;

    public final AssociativeInfo assocInfo;

    public final CommutativeInfo comInfo;

    public final List<Prover> provers;

    public final ProofAsst proofAsst;

    /**
     * Note: Here will be performed a lot of work during the construction of
     * this class!
     *
     * @param assrtList the list all library asserts
     * @param provableLogicStmtTyp this constant indicates "provable logic
     *            statement type"
     * @param messages the message manager
     * @param verifyProofs the proof verification is needed for some actions
     * @param proofAsst The proof asst
     * @param supportPrefix when it is true auto-transformation component will
     *            try to use implication prefix in transformations
     * @param debugOutput when it is true auto-transformation component will
     *            produce a lot of debug output
     */
    public TransformationManager(final ProofAsst proofAsst,
        final List<Assrt> assrtList, final Cnst provableLogicStmtTyp,
        final Messages messages, final VerifyProofs verifyProofs,
        final boolean supportPrefix, final boolean debugOutput)
    {
        this.proofAsst = proofAsst;
        output = new TrOutput(messages);
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;
        dbg = debugOutput;
        supportImplicationPrefix = supportPrefix;

        eqInfo = new EquivalenceInfo(assrtList, output, dbg);

        implInfo = new ImplicationInfo(eqInfo, assrtList, output, dbg);

        eqInfo.fillDeductRules(assrtList, implInfo);

        conjInfo = new ConjunctionInfo(implInfo, assrtList, output, dbg);

        clInfo = new ClosureInfo(implInfo, conjInfo, assrtList, output, dbg);

        replInfo = new ReplaceInfo(eqInfo, implInfo, assrtList, output, dbg);

        assocInfo = new AssociativeInfo(eqInfo, clInfo, replInfo, assrtList,
            output, dbg);

        comInfo = new CommutativeInfo(eqInfo, clInfo, assrtList, output, dbg);

        provers = new ArrayList<>();

        setUpProvers(assrtList);
    }

    private void setUpProvers(final List<Assrt> assrtList) {
        if (proofAsst.macroManager != null) {
            proofAsst.macroManager.set("trManager", this);
            proofAsst.macroManager.set("assrtList", assrtList);
            proofAsst.macroManager
                .runCallback(CallbackType.TRANSFORMATION_SET_UP);
        }
        else
            buildUWPProvers(assrtList, (assrt) -> true);
    }

    /**
     * Create {@link UseWhenPossible} provers for eligible assrts in the
     * database.
     *
     * @param assrtList The list of available assrts, sorted by number of
     *            loghyps
     * @param isLegal A filter for the assrtList: any illegal assrts will not be
     *            used to build provers
     */
    public void buildUWPProvers(final List<Assrt> assrtList,
        final Predicate<Assrt> isLegal)
    {
        final List<AssrtProver> extraProvers = new ArrayList<>();
        assrtLoop: for (final Assrt assrt : assrtList)
            if (!isLegal.test(assrt))
                continue;
            else if (assrt.getLogHypArrayLength() == 0)
                provers.add(new Provers.UseWhenPossible(assrt));
            else if (assrt
                .getMandHypArrayLength() == assrt.getLogHypArrayLength()
                    + assrt.getMandVarHypArray().length)
            {
                final int len = assrt.getFormula().getCnt();
                for (final Hyp hyp : assrt.getMandFrame().hypArray)
                    if (hyp instanceof LogHyp
                        && hyp.getFormula().getCnt() >= len)
                        continue assrtLoop;
                extraProvers.add(new Provers.UseWhenPossible(assrt));
            }
        final Map<Assrt, List<AssrtProver>> map = new HashMap<>();
        for (final AssrtProver p : extraProvers)
            for (final AssrtProver p2 : extraProvers)
                if (p2 != p && p.prove(null,
                    p2.assrt.getExprParseTree().getRoot()) != null)
                    map.computeIfAbsent(p.assrt, k -> new ArrayList<>())
                        .add(p2);

        final TopologicalSorter<AssrtProver> sorter = new TopologicalSorter<>(
            extraProvers,
            p -> map.getOrDefault(p.assrt, Collections.emptyList()), true);
        sorter.sort();

        if (!sorter.getWithLoops().isEmpty()) {
            final StringBuilder sb = new StringBuilder();
            for (final AssrtProver p : sorter.getWithLoops())
                sb.append(p.assrt + " > " + map.get(p.assrt));
            output.dbgMessage(dbg, TrConstants.ERRMSG_LOOP_IN_TRANSFORMATIONS,
                sb);
        }
        for (final AssrtProver p : sorter.getSorted())
            if (!sorter.getWithLoops().contains(p))
                provers.add(p);
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
        final Stmt stmt = node.stmt;

        final boolean[] replAsserts = replInfo.getPossibleReplaces(stmt, info);

        boolean isCom = false;
        final GeneralizedStmt comProp = comInfo.getGenStmtForComNode(node,
            info);
        if (comProp != null)
            isCom = true;

        boolean isAssoc = false;
        final GeneralizedStmt assocProp = assocInfo.getGenStmtForAssocNode(node,
            info);
        if (assocProp != null)
            isAssoc = true;

        boolean isAssocCom = false;
        if (isAssoc)
            isAssocCom = comInfo.isComOp(assocProp, info);

        final boolean subTreesCouldBeRepl = replAsserts != null;

        if (!subTreesCouldBeRepl)
            return new IdentityTransformation(this, node);

        if (isAssocCom)
            return new AssocComTransformation(this, node,
                AssocTree.createAssocTree(node, assocProp, info), assocProp);
        else if (isCom)
            return new CommutativeTransformation(this, node, comProp);

        else if (isAssoc)
            return new AssociativeTransformation(this, node,
                AssocTree.createAssocTree(node, assocProp, info), assocProp);
        else if (subTreesCouldBeRepl)
            return new ReplaceTransformation(this, node);

        throw new IllegalStateException(new VerifyException(
            TrConstants.ERRMSG_ILLEGAL_STATE_IN_CREATE_TRANSFORMATION));
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

        final GenProofStepStmt res = tr.transformMeToTarget(dsTr, info);

        // we have to remove prefix on previous steps
        assert !res.hasPrefix();

        final ProofStepStmt eqResult = res.getSimpleStep();

        final boolean isNormalOrder = TrUtil
            .isVarNode(impl.getLogHypArray()[0].getExprParseTree().getRoot());

        final ProofStepStmt[] hypDerivArray = isNormalOrder
            ? new ProofStepStmt[]{source, eqResult}
            : new ProofStepStmt[]{eqResult, source};

        info.finishDerivationStep(hypDerivArray, impl);
    }

    /**
     * Tries to unify the derivation step by some automatic transformations
     *
     * @param info The context
     * @param reverseTransformations True if we should try the reverse
     *            transformation module
     * @return true if it founds possible unification
     */
    private List<DerivationStep> tryToFindTransformationsCore(
        final WorksheetInfo info, final boolean reverseTransformations)
    {
        if (supportImplicationPrefix) {
            // If derivation step has form "prefix -> core", then this prefix
            // could be used in transformations
            final ExtractImplResult extrImplRes = implInfo
                .extractPrefixAndGetImplPart(info);
            if (extrImplRes != null)
                if (TrUtil.isVarNode(extrImplRes.implPrefix))
                    // Now we support only simple "one-variable" prefixes
                    info.setImplicationPrefix(extrImplRes.implPrefix,
                        extrImplRes.implStatement);
        }

        final ParseNode derivRoot = info.derivStep.formulaParseTree.getRoot();
        if (reverseTransformations
            && findReverseTransformations(info, derivRoot, true) != null)
            return info.newSteps;

        final Cnst derivType = derivRoot.stmt.getTyp();
        final Assrt implAssrt = implInfo.getEqImplication(derivType);
        if (implAssrt == null)
            return null;

        final Transformation dsTr = createTransformation(derivRoot, info);
        // Get canonical form for destination statement
        final ParseNode dsCanonicalForm = dsTr.getCanonicalNode(info);

        output.dbgMessage(dbg, TrConstants.ERRMSG_CANONICAL_FORM,
            info.derivStep, getFormula(dsCanonicalForm));

        for (final ProofWorkStmt proofWorkStmtObject : info.proofWorksheet
            .getProofWorkStmtList())
        {
            if (proofWorkStmtObject == info.derivStep)
                break;

            if (!(proofWorkStmtObject instanceof ProofStepStmt))
                continue;

            final ProofStepStmt candidate = (ProofStepStmt)proofWorkStmtObject;

            if (candidate.formulaParseTree == null)
                continue;

            final ParseNode candCanon = getCanonicalForm(
                candidate.formulaParseTree.getRoot(), info);
            output.dbgMessage(dbg, TrConstants.ERRMSG_CANONICAL_FORM, candidate,
                getFormula(candCanon));

            // Compare canonical forms for destination and for candidate
            if (dsCanonicalForm.isDeepDup(candCanon)) {
                output.dbgMessage(dbg,
                    TrConstants.ERRMSG_CANONICAL_CORRESPONDENCE, candidate,
                    info.derivStep);
                performTransformation(info, candidate, implAssrt);

                return info.newSteps;
            }
        }

        // Maybe it is closure assertion? Then we could automatically prove it!
        // TODO: Now the used algorithm could consume a lot of time for the
        // search!
        if (clInfo.performClosureTransformation(info))
            return info.newSteps;

        return null;
    }

    public ProofStepStmt findReverseTransformations(final WorksheetInfo info,
        final ParseNode root, final boolean finish)
    {
        final ProofStepStmt stmt = info.getProofStepStmt(root);
        if (stmt != null) {
            if (finish)
                info.derivStep.setLocalRef(stmt);
            return stmt;
        }
        for (final Prover p : provers) {
            final ProverResult result = p.prove(info, root);
            if (result == null
                || result.assrt.getSeq() >= info.proofWorksheet.getMaxSeq())
                continue;
            final ProofStepStmt[] hyps = new ProofStepStmt[result.subst.length];
            for (int i = 0; i < hyps.length; i++)
                hyps[i] = findReverseTransformations(info, result.subst[i],
                    false);
            if (finish) {
                info.finishDerivationStep(hyps, result.assrt);
                return info.derivStep;
            }
            else
                return info.createProofStepStmt(root, hyps, result.assrt);
        }
        if (finish)
            return null;
        final DerivationStep badStep = info.giveUpProofStepStmt(root);
        final List<DerivationStep> steps = tryToFindTransformationsCore(
            new WorksheetInfo(info.proofWorksheet, badStep, this), false);
        if (steps != null)
            info.newSteps.addAll(steps);
        return badStep;
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
        if (derivStep.getFormula() == null)
            return null;
        final WorksheetInfo info = new WorksheetInfo(proofWorksheet, derivStep,
            this);
        return tryToFindTransformationsCore(info, true);
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
        final Formula generatedFormula = verifyProofs
            .convertRPNToFormula(tree.convertToRPN(), "tree");
        return generatedFormula;
    }
}
