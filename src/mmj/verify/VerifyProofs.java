//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * VerifyProofs.java 0.09 11/01/2011
 *
 * 15-Jan-2006
 *             --> added verifyDerivStepProof(),
 *                   and verifyDerivStepDjVars() for Proof Assistant.
 *                 see mmj.pa.ProofUnifier.java.
 *             --> added getProofDerivationSteps() for Proof Asst.
 *                 see mmj.pa.ProofAsst.java.
 *             --> added optimization to main loop to not call
 *                 checkDjVars unless stepFrame.djVarsArray.length
 *                 > 0. The checkDjVars routine does a LOT of
 *                 looping around before it even checks to see if
 *                 there are any DjVars!
 *
 * 01-April-2006: Version 0.04:
 *    --> Generate formula from RPN for Derive Feature of
 *        Proof Assistant.
 *
 * 01-November-2006: Version 0.05:
 *    -->  Fixed bug: a proof of "wph wps ax-mp" resulted in
 *         ArrayIndexOutOfBoundsException in FindUniqueSubstMapping()
 *         so message ERRMSG_STACK_MISMATCH_STEP, E-PR-0021, was
 *         added to detect this underflow condition and generate
 *         a cryptic but helpful message :)
 *
 * 01-June-2007: Version 0.06
 *    -->  Added proofDjVarsSoftErrorsIgnore and
 *         proofSoftDjVarsErrorList for use by ProofAsst
 *         (specifically, mmj.pa.ProofUnifier.java).
 *         In checkDjVars(), "soft" Dj Vars errors -- those
 *         caused by missing $d statements in the theorem
 *         being proved can now be reported (as usual) or
 *         ignored, or can be returned as an ArrayList
 *         of DjVars.java objects that need to be added
 *         to the theorem being proved.
 *
 * 01-Aug-2007: Version 0.07
 *    -->  Don't report "soft" Dj errors involving Work Vars
 *
 * Nov-01-2007 Version 0.08
 * - add call to ProofDerivationStepEntry.computeProofLevels()
 *   in VerifyProofs.getProofDerivationSteps() so that
 *   proof level is always available on exported proofs
 *   even if TMFF UseIndent is zero.
 *
 * Version 0.09 - Nov-01-2011:
 *     _ Bugfix in loadProofDerivStepList():
 *
 *       GMFFExportTheorem export of dummylink came out as follows
 *        h1::dummylink.1     |- ph
 *        hqed::dummylink.2   |- ps
 */

package mmj.verify;

import java.util.*;

import mmj.lang.*;

/**
 * VerifyProofs implements the proof verification process described in
 * Metamath(dot)pdf.
 * <p>
 * It also has a new feature, verifying a syntax RPN as if it were a proof (in a
 * sense, Stmt.exprRPN is a proof, a proof that Stmt.formula can be generated
 * using the grammar embodied in the Metamath file...) The purpose of this
 * feature is primarily to test the grammatical parser's output -- a
 * double-check of the results.
 * <p>
 * The code is optimized for batch processing of a large number of proofs, one
 * after the other.
 * <p>
 * The main "optimization" was to re-use arrays instead of allocating them for
 * each proof. The arrays are initially allocated at a size that fits set.mm,
 * which has some massive proofs. If an ArrayIndexOutOfBounds exception is
 * detected, the arrays are reallocated with a larger size (an upper limit halts
 * this process), and a "retry" is performed.
 * <p>
 * VerifyProofs uses class SubstMapEntry which is a simple data structure that
 * should probably be an inner class of VerifyProofs. Other clean-ups are
 * probably at hand for reworking this *thing*. One thing is sure, the error
 * messages provided by VerifyProofs are much less helpful than those from
 * metamath.exe -- but, on the other hand, since Proof Assistant is the best way
 * to create proofs, a proof error in an existing file should be rare (creating
 * a Metamath proof by hand is like writing assembler code.)
 * <p>
 * FYI, here is the basic verification algorithm, which leaves out the ugly
 * details of disjoint variable restrictions and substitutions:
 * <p>
 * 
 * <pre>
 *     1. init proof work stack, etc.
 *     2. loop through theorem proof array, for each proof[] step:
 *        - if null, exception ==> proof incomplete
 *        - if stmt.isHyp(), ==> push stmt.formula onto stack
 *        - else if stmt.mandFrame.hypArray.length = zero
 *              ==> push stmt.formula onto stack
 *                  (see ccau, for ex.)
 *        - else (assertion w/hyp):
 *             findVarSubstMap for assertion
 *             if notfnd ==> exception...various
 *             else,
 *                 - check DjVars restrictions
 *                 - pop 'n' entries from stack
 *                     ('n' = nbr of mand hyps)
 *                 - throw exception if stack underflow!
 *                 - push substituted assertion formula onto stack.
 *     3. if stack has more than one entry left,
 *            ==>exception, > 1 stack entry left, disproved!
 *     4. if stack entry not equal to stmt to be proved formula
 *            ==>exception, last entry not equal! disproved!
 *     5. ok! proved.
 * 
 * </pre>
 * 
 * @see <a href="../../mmjProofVerification.html"> More on Proof
 *      Verification</a>
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class VerifyProofs implements ProofVerifier {

    private int retryCnt = -1;

    // *******************************************
    // all following variables are work items used
    // within a single execution but are stored
    // globally to avoid having to pass around
    // a bazillion call paramaters.
    // *******************************************

    private int pStackCnt;
    private int pStackMax;
    private int pStackHighwater;
    private Formula[] pStack;

    // "work" expression/formula
    private int wExprCnt;
    private int wExprMax;
    private int wExprHighwater;
    private Sym[] wExpr;

    private int substCnt;
    private int substMax;
    private int substHighwater;
    private SubstMapEntry[] subst;

    private boolean isExprRPNVerify;
    private String proofStmtLabel;
    private Formula proofStmtFormula;
    private MandFrame proofStmtFrame;
    private OptFrame proofStmtOptFrame;
    private Stmt[] proof;

    private boolean proofDjVarsSoftErrorsIgnore;

    private List<DjVars> proofSoftDjVarsErrorList;

    private MandFrame dummyMandFrame = new MandFrame();
    private OptFrame dummyOptFrame = new OptFrame();

    private Assrt stepAssrt;
    private String stepLabel;
    private Formula stepFormula;
    private MandFrame stepFrame;
    private Formula stepSubstFormula;
    private int stepNbr;
    private String stepNbrOutputString;

    // *******************************************

    /**
     * Constructor - default.
     */
    public VerifyProofs() {

        // don't allocate the stacks until verification requested.
        retryCnt = -1;

        /**
         * Load dummy MandFrame and OptFrame objects for use in validating an
         * exprRPN using the VerifyProofs engine.
         */
        dummyMandFrame = new MandFrame();
        dummyMandFrame.hypArray = null; // <-load this for use...
        dummyMandFrame.djVarsArray = new DjVars[0];
        dummyOptFrame = new OptFrame();
        dummyOptFrame.optHypArray = new Hyp[0];
        dummyOptFrame.optDjVarsArray = new DjVars[0];

    }

    /**
     * Verify all proofs in Statement Table.
     * 
     * @param messages Messages object for output error messages.
     * @param stmtTbl Statement Table (map).
     */
    public void verifyAllProofs(final Messages messages,
        final Map<String, Stmt> stmtTbl)
    {
        for (final Stmt stmt : stmtTbl.values()) {
            if (messages.maxErrorMessagesReached())
                break;
            if (stmt instanceof Theorem) {
                final String errMsg = verifyOneProof((Theorem)stmt);
                if (errMsg != null)
                    messages.accumErrorMessage(errMsg);
            }
        }
    }

    /**
     * Verify a single proof.
     * 
     * @param theorem Theorem object reference.
     * @return String error message if error(s), or null.
     */
    public String verifyOneProof(final Theorem theorem) {

        String errMsg = null;
        boolean needToRetry = true;

        try {
            reInitArrays(0);
            while (needToRetry)
                try {
                    errMsg = null;
                    loadTheoremGlobalVerifyVars(theorem);
                    proofDjVarsSoftErrorsIgnore = false;
                    proofSoftDjVarsErrorList = null;

                    verifyProof();
                    needToRetry = false;
                } catch (final ArrayIndexOutOfBoundsException e) {
                    ++retryCnt;
                    reInitArrays(retryCnt);
                } catch (final VerifyException e) {
                    needToRetry = false;
                    errMsg = e.getMessage();
                }
        } catch (final VerifyException e) {
            errMsg = e.getMessage();
        }

        return errMsg;

    }

    /**
     * Verify all Statements' grammatical parse RPNs.
     * <p>
     * Note: even VarHyp and Syntax Axioms are assigned default RPN's, so this
     * should work -- unless there are errors in the Metamath file, or in the
     * grammar itself.
     * 
     * @param messages Messages object for output error messages.
     * @param stmtTbl Statement Table (map).
     */
    public void verifyAllExprRPNAsProofs(final Messages messages,
        final Map<String, Stmt> stmtTbl)
    {
        for (final Stmt stmt : stmtTbl.values()) {
            if (messages.maxErrorMessagesReached())
                break;
            final String errMsg = verifyExprRPNAsProof(stmt);
            if (errMsg != null)
                messages.accumErrorMessage(errMsg);
        }
    }

    /**
     * Verify grammatical parse RPN as if it were a proof.
     * <p>
     * Note: even VarHyp and Syntax Axioms are assigned default RPN's, so this
     * should work -- unless there are errors in the Metamath file, or in the
     * grammar itself.
     * 
     * @param exprRPNStmt Stmt with RPN to verify.
     * @return String error message if error(s), or null.
     */
    public String verifyExprRPNAsProof(final Stmt exprRPNStmt) {

        String errMsg = null;
        boolean needToRetry = true;

        try {
            reInitArrays(0);
            while (needToRetry)
                try {
                    errMsg = null;
                    loadExprRPNGlobalVerifyVars(exprRPNStmt);
                    proofDjVarsSoftErrorsIgnore = false;
                    proofSoftDjVarsErrorList = null;
                    verifyProof();
                    needToRetry = false;
                } catch (final ArrayIndexOutOfBoundsException e) {
                    ++retryCnt;
                    reInitArrays(retryCnt);
                } catch (final VerifyException e) {
                    needToRetry = false;
                    errMsg = ProofConstants.ERRMSG_RPN_VERIFY_AS_PROOF_FAILURE
                        + e.getMessage();
                }
        } catch (final VerifyException e) {
            errMsg = e.getMessage();
        }

        return errMsg;

    }

    /**
     * Verify a single proof derivation step.
     * <p>
     * Note: the "comboFrame" contains the mandatory and optional information as
     * a matter of convenience. The "optional" information is kept separate in
     * Metamath because it needs to be available in Proof Verifier (and
     * elsewhere), but must not be used when the Assertion is referenced in a
     * proof (especially not be pushed onto the stack!)
     * 
     * @param derivStepStmtLabel label + "Step" + step number
     * @param derivStepFormula formula of proof step
     * @param derivStepProofTree step proof tree.
     * @param derivStepComboFrame MandFrame for step.
     * @return String error message if error(s), or null.
     */
    public String verifyDerivStepProof(
        final String derivStepStmtLabel, // theorem
        final Formula derivStepFormula, final ParseTree derivStepProofTree,
        final MandFrame derivStepComboFrame)
    {

        isExprRPNVerify = false;

        proofStmtLabel = derivStepStmtLabel; // theorem
        proofStmtFormula = derivStepFormula;
        proof = derivStepProofTree.convertToRPN();
        proofStmtFrame = derivStepComboFrame;
        proofStmtOptFrame = dummyOptFrame;

        String errMsg = null;
        boolean needToRetry = true;

        try {
            reInitArrays(0);
            while (needToRetry)
                try {
                    errMsg = null;
                    proofDjVarsSoftErrorsIgnore = false;
                    proofSoftDjVarsErrorList = null;
                    verifyProof();
                    needToRetry = false;
                } catch (final ArrayIndexOutOfBoundsException e) {
                    ++retryCnt;
                    reInitArrays(retryCnt);
                } catch (final VerifyException e) {
                    needToRetry = false;
                    errMsg = ProofConstants.ERRMSG_DERIV_STEP_PROOF_FAILURE
                        + e.getMessage();
                }
        } catch (final VerifyException e) {
            errMsg = e.getMessage();
        }

        return errMsg;

    }

    /**
     * Verify that a single proof derivation step does not violate the Distinct
     * Variable Restrictions of the step's Ref assertion.
     * <p>
     * The main complication here is converting the parse subtrees that comprise
     * the variable substitutions into the form required by method
     * checkDjVars().
     * <p>
     * Note: the "comboFrame" contains the mandatory and optional information as
     * a matter of convenience. The "optional" information is kept separate in
     * Metamath because it needs to be available in Proof Verifier (and
     * elsewhere), but must not be used when the Assertion is referenced in a
     * proof (especially not be pushed onto the stack!)
     * 
     * @param derivStepNbr string, may equal "qed"
     * @param derivStepStmtLabel label + "Step" + step number
     * @param derivStepRef Assertion justifying the step
     * @param derivStepAssrtSubst array of substitution subtrees
     * @param derivStepComboFrame MandFrame for step.
     * @param djVarsSoftErrorsIgnore whether to ignore soft DjVars errors
     * @param djVarsSoftErrorsGenerateNew whether to generate missing DjVars
     *            restrictions
     * @param softDjVarsErrorList the output list of violated DjVars
     * @return String error message if error(s), or null.
     */
    public String verifyDerivStepDjVars(
        final String derivStepNbr,
        final String derivStepStmtLabel, // theorem
        final Assrt derivStepRef, final ParseNode[] derivStepAssrtSubst,
        final MandFrame derivStepComboFrame,
        final boolean djVarsSoftErrorsIgnore,
        final boolean djVarsSoftErrorsGenerateNew,
        final List<DjVars> softDjVarsErrorList)
    {

        stepNbrOutputString = derivStepNbr;
        stepAssrt = derivStepRef;
        stepLabel = stepAssrt.getLabel(); // ref label
        stepFrame = derivStepRef.getMandFrame();
        proofStmtLabel = derivStepStmtLabel; // theorem
        proofStmtFrame = derivStepComboFrame;
        proofStmtOptFrame = dummyOptFrame;

        proofDjVarsSoftErrorsIgnore = djVarsSoftErrorsIgnore;

        proofSoftDjVarsErrorList = softDjVarsErrorList;

        String errMsg = null;
        boolean needToRetry = true;

        try {
            reInitArrays(0);
            while (needToRetry)
                try {
                    errMsg = null;
                    loadDerivStepDjVarsSubst(derivStepAssrtSubst);
                    if (substCnt > 0) {
                        if (djVarsSoftErrorsGenerateNew)
                            // do not use existing $d's
                            proofStmtFrame = dummyMandFrame;
                        checkDjVars();
                    }
                    needToRetry = false;
                } catch (final ArrayIndexOutOfBoundsException e) {
                    ++retryCnt;
                    proofStmtFrame = derivStepComboFrame; // reset
                    reInitArrays(retryCnt);
                } catch (final VerifyException e) {
                    needToRetry = false;
                    errMsg = e.getMessage();
                }
        } catch (final VerifyException e) {
            errMsg = e.getMessage();
        }

        return errMsg;

    }

    /**
     * Builds and returns an ArrayList of proof steps for export via the Proof
     * Assistant.
     * 
     * @param theorem the theorem whose proof will be exported
     * @param exportFormatUnified set to true if proof step label is output on
     *            each step (else, it is only output on Logical Hypothesis
     *            steps.)
     * @param hypsRandomized set to true if proof step hyps should be rearranged
     *            in random order (a testing feature.)
     * @param provableLogicStmtTyp type code of proof derivation steps to
     *            return.
     * @return List of ProofDerivationStepEntry objects.
     * @throws VerifyException if the proof is invalid.
     */
    public List<ProofDerivationStepEntry> getProofDerivationSteps(
        final Theorem theorem, final boolean exportFormatUnified,
        final boolean hypsRandomized, final Cnst provableLogicStmtTyp)
        throws VerifyException
    {

        List<ProofDerivationStepEntry> derivStepList = null;

        final LogHyp[] theoremLogHypArray = theorem.getLogHypArray();
        final ProofDerivationStepEntry[] theoremHypStepArray = new ProofDerivationStepEntry[theoremLogHypArray.length];
        for (int i = 0; i < theoremLogHypArray.length; i++) {
            final ProofDerivationStepEntry e = new ProofDerivationStepEntry();
            e.isHyp = true;
            e.step = Integer.toString(i + 1);
            e.hypStep = new String[0];
            e.refLabel = theoremLogHypArray[i].getLabel();
            e.formula = theoremLogHypArray[i].getFormula();
            e.formulaParseTree = theoremLogHypArray[i].getExprParseTree();

            theoremHypStepArray[i] = e;
        }

        boolean needToRetry = true;
        reInitArrays(0);
        while (needToRetry)
            try {
                loadTheoremGlobalVerifyVars(theorem);
                derivStepList = new ArrayList<ProofDerivationStepEntry>();
                loadProofDerivStepList(theorem, derivStepList,
                    theoremHypStepArray, exportFormatUnified, hypsRandomized,
                    provableLogicStmtTyp);
                needToRetry = false;
            } catch (final IllegalArgumentException e) {}

        ProofDerivationStepEntry.computeProofLevels(derivStepList);

        return derivStepList;

    }

    /**
     * Loads an array list of ProofDerivationStepEntry objects for the
     * non-syntax assertion and the logical hypothesis steps of the proof.
     * <p>
     * Proof step labels (Ref) are output in the step entries if either
     * exportFormatUnified == true or if the step is a logical hypothesis step.
     * <p>
     * This is a clone of verifyProof()!!!!
     * 
     * @param theorem the theorem whose proof will be exported
     * @param derivStepList the list to load with ProofDerivationStepEntry
     *            objects
     * @param theoremHypStepArray the hypStepArray for the Theorem
     * @param exportFormatUnified set to true if proof step label is output on
     *            each step (else, it is only output on Logical Hypothesis
     *            steps.)
     * @param hypsRandomized set to true if proof step hyps should be rearranged
     *            in random order (a testing feature.)
     * @param provableLogicStmtTyp type code of proof derivation steps to
     *            return.
     * @throws VerifyException if an error occurred
     */
    private void loadProofDerivStepList(final Theorem theorem,
        final List<ProofDerivationStepEntry> derivStepList,
        final ProofDerivationStepEntry[] theoremHypStepArray,
        final boolean exportFormatUnified, final boolean hypsRandomized,
        final Cnst provableLogicStmtTyp) throws VerifyException
    {

        for (final ProofDerivationStepEntry element : theoremHypStepArray)
            derivStepList.add(element);

        final Stack<ProofDerivationStepEntry> undischargedStack = new Stack<ProofDerivationStepEntry>();

        nextStep: for (stepNbr = 0; stepNbr < proof.length; stepNbr++) {

            if (proof[stepNbr] == null)
                raiseVerifyException(Integer.toString(stepNbr + 1), " ",
                    ProofConstants.ERRMSG_PROOF_STEP_INCOMPLETE);

            stepFormula = proof[stepNbr].getFormula();
            stepLabel = proof[stepNbr].getLabel();
            if (proof[stepNbr].isHyp()) {
                pStack[pStackCnt++] = stepFormula;
                if (proof[stepNbr].isLogHyp()) {
                    int i = 0;
                    while (i < theoremHypStepArray.length) {
                        if (stepLabel.equals(theoremHypStepArray[i].refLabel)) {
                            undischargedStack.push(theoremHypStepArray[i]);
                            break;
                        }
                        ++i;
                    }
                    if (i >= theoremHypStepArray.length)
                        raiseVerifyException(
                            Integer.toString(stepNbr + 1),
                            stepLabel,
                            ProofConstants.ERRMSG_BOGUS_PROOF_LOGHYP_STMT_1
                                + proofStmtLabel
                                + ProofConstants.ERRMSG_BOGUS_PROOF_LOGHYP_STMT_2
                                + stepLabel
                                + ProofConstants.ERRMSG_BOGUS_PROOF_LOGHYP_STMT_3);
                }
                continue nextStep;
            }

            stepAssrt = (Assrt)proof[stepNbr];
            stepFrame = stepAssrt.getMandFrame();
            if (stepFrame.hypArray.length == 0) { // constant
                pStack[pStackCnt++] = stepFormula;
                if (stepFormula.getTyp() != provableLogicStmtTyp
                    || stepAssrt.isAxiom()
                    && ((Axiom)stepAssrt).getIsSyntaxAxiom())
                {
                    // ok, bypass syntax axioms and syntax theorems
                }
                else {
                    final ProofDerivationStepEntry e = new ProofDerivationStepEntry();
                    e.isHyp = false;
                    e.step = Integer.toString(derivStepList.size() + 1);
                    e.hypStep = new String[0];
                    if (exportFormatUnified)
                        e.refLabel = stepLabel;
                    e.formula = stepFormula;
                    derivStepList.add(e);
                    undischargedStack.push(e);
                }
                continue nextStep;
            }

            findUniqueSubstMapping();

            pStackCnt -= stepFrame.hypArray.length;
            if (pStackCnt < 0)
                raiseVerifyException(Integer.toString(stepNbr + 1), stepLabel,
                    ProofConstants.ERRMSG_PROOF_STACK_UNDERFLOW);

            stepSubstFormula = applySubstMapping(stepFormula);
            pStack[pStackCnt++] = stepSubstFormula;

            if (stepFormula.getTyp() != provableLogicStmtTyp
                || stepAssrt.isAxiom() && ((Axiom)stepAssrt).getIsSyntaxAxiom())
            {
                // ok, bypass syntax axioms and syntax theorems
            }
            else {
                final ProofDerivationStepEntry e = new ProofDerivationStepEntry();
                e.isHyp = false;
                e.step = Integer.toString(derivStepList.size() + 1);
                final int logHypCnt = stepAssrt.getLogHypArrayLength();
                e.hypStep = new String[logHypCnt];
                int i = logHypCnt;
                if (i > undischargedStack.size())
                    raiseVerifyException(Integer.toString(stepNbr + 1),
                        stepLabel,
                        ProofConstants.ERRMSG_LOGHYP_STACK_DEFICIENT_1
                            + proofStmtLabel
                            + ProofConstants.ERRMSG_LOGHYP_STACK_DEFICIENT_1);
                while (--i >= 0) {
                    final ProofDerivationStepEntry h = undischargedStack.pop();
                    e.hypStep[i] = h.step;
                }
                if (hypsRandomized)
                    doRandomizeHypSteps(e.hypStep);
                if (exportFormatUnified)
                    e.refLabel = stepLabel;
                e.formula = stepSubstFormula;
                derivStepList.add(e);
                undischargedStack.push(e);
            }
        }

        if (pStackCnt != 1)
            if (proof.length == 0)
                raiseVerifyException(Integer.toString(stepNbr), " ",
                    ProofConstants.ERRMSG_PROOF_HAS_ZERO_STEPS);
            else
                raiseVerifyException(Integer.toString(stepNbr), " ",
                    ProofConstants.ERRMSG_PROOF_STACK_GT_1_AT_END);

        if (!proofStmtFormula.equals(pStack[0]))
            raiseVerifyException(
                Integer.toString(stepNbr + 1),
                " ",
                ProofConstants.ERRMSG_FINAL_STACK_ENTRY_UNEQUAL2
                    + pStack[0].toString());

//      PATCH: Release 11/01/2011 bugfix
//      if (derivStepList.isEmpty()) {
        if (derivStepList.size() <= theoremHypStepArray.length)
            // END-PATCH
            raiseVerifyException(Integer.toString(stepNbr), " ",
                ProofConstants.ERRMSG_NO_DERIV_STEPS_CREATED_1 + proofStmtLabel
                    + ProofConstants.ERRMSG_NO_DERIV_STEPS_CREATED_2);
        else {
            final int qedIndex = derivStepList.size() - 1;
            final ProofDerivationStepEntry qedStep = derivStepList
                .get(qedIndex);
            qedStep.step = ProofConstants.QED_STEP_NBR;
            qedStep.formulaParseTree = theorem.getExprParseTree();
        }
    }

    private void doRandomizeHypSteps(final String[] hypStep) {
        if (hypStep.length < 2)
            return;

        String s;

        int swap;

        final Random random = new Random(System.nanoTime());

        for (int i = 0; i < hypStep.length; i++) {
            swap = random.nextInt(hypStep.length);
            s = hypStep[swap];
            hypStep[swap] = hypStep[i];
            hypStep[i] = s;
        }
    }

    /**
     * Converts a single derivation step's ParseNode subtree array into a
     * SubstMapEntry array.
     * <p>
     * The input array of ParseNode subtrees is in the same sequence as the
     * stepAssrt.MandFrame.hypArray -- it is a parallel array, in fact. So this
     * means that we have to travel through the input array selecting only those
     * array elements corresponding to variable hypotheses in the stepAssrt's
     * hypArray (ignoring LogHyp entries).
     * 
     * @param derivStepAssrtSubst the ParseNode subtree array
     */
    private void loadDerivStepDjVarsSubst(final ParseNode[] derivStepAssrtSubst)
    {

        final Hyp[] hypArray = stepFrame.hypArray;

        final Stack<ParseNode> nodeStack = new Stack<ParseNode>();

        ParseNode node;
        ParseNode[] child;

        substCnt = 0;
        for (int i = 0; i < hypArray.length; i++) {
            if (!hypArray[i].isVarHyp())
                continue;

            wExprCnt = 0;
            nodeStack.push(derivStepAssrtSubst[i]);
            while (!nodeStack.isEmpty()) {
                node = nodeStack.pop();
                if (node.getStmt().isVarHyp())
                    wExpr[wExprCnt++] = ((VarHyp)node.getStmt()).getVar();
                else {
                    child = node.getChild();
                    for (final ParseNode element : child)
                        nodeStack.push(element);
                }
            }
            if (wExprCnt == 0)
                continue;

            if (subst[substCnt] == null)
                subst[substCnt] = new SubstMapEntry();
            subst[substCnt].substFrom = ((VarHyp)stepFrame.hypArray[i])
                .getVar();

            final Sym[] s = new Sym[wExprCnt];
            for (int w = 0; w < wExprCnt; w++)
                s[w] = wExpr[w];
            subst[substCnt].substTo = s;
            ++substCnt;
        }
    }

    private void loadTheoremGlobalVerifyVars(final Theorem theoremToProve) {
        isExprRPNVerify = false;
        proofStmtLabel = theoremToProve.getLabel();
        proofStmtFormula = theoremToProve.getFormula();
        proof = theoremToProve.getProof();
        proofStmtFrame = theoremToProve.getMandFrame();
        proofStmtOptFrame = theoremToProve.getOptFrame();
    }

    private void loadExprRPNGlobalVerifyVars(final Stmt exprRPNStmt) {
        isExprRPNVerify = true;
        proofStmtLabel = exprRPNStmt.getLabel();
        proofStmtFormula = exprRPNStmt.getFormula();
        proof = exprRPNStmt.getExprRPN();

        if (proof == null)
            throw new IllegalArgumentException("Proof is null for Stmt ="
                + exprRPNStmt.getLabel());

        dummyMandFrame.hypArray = exprRPNStmt.getMandVarHypArray();
        proofStmtFrame = dummyMandFrame;
        proofStmtOptFrame = dummyOptFrame;
    }

    /**
     * <ol>
     * <li>init proof work stack, etc.
     * <li>loop through theorem proof array, for each proof[] step:
     * <ul>
     * <li>if null, exception ==> proof incomplete
     * <li>if stmt.isHyp(), ==> push stmt.formula onto stack
     * <li>else if stmt.mandFrame.hypArray.length = zero<br>
     * ==> push stmt.formula onto stack (see ccau, for ex.)
     * <li>else (assertion w/hyp):
     * <ul>
     * <li>findVarSubstMap for assertion
     * <li>if notfnd ==> exception...various
     * <li>else,
     * <ul>
     * <li>check DjVars restrictions
     * <li>pop 'n' entries from stack ('n' = nbr of mand hyps)
     * <li>throw exception if stack underflow!
     * <li>push substituted assertion formula onto stack.
     * </ul>
     * </ul>
     * </ul>
     * <li>if stack has more than one entry left, ==>exception, > 1 stack entry
     * left, disproved!
     * <li>if stack entry not equal to stmt to be proved formula ==>exception,
     * last entry not equal! disproved!
     * <li>ok! proved.
     * </ol>
     * <p>
     * NOTE: try to catch ArrayIndexOutOfBoundsException and reallocate arrays
     * with larger size... </pre>
     * 
     * @throws VerifyException if an error occurred
     */
    private void verifyProof() throws VerifyException {

        nextStep: for (stepNbr = 0; stepNbr < proof.length; stepNbr++) {

            if (proof[stepNbr] == null)
                raiseVerifyException(Integer.toString(stepNbr + 1), " ",
                    ProofConstants.ERRMSG_PROOF_STEP_INCOMPLETE);

            stepFormula = proof[stepNbr].getFormula();
            if (proof[stepNbr].isHyp()) {
                pStack[pStackCnt++] = stepFormula;
                continue nextStep;
            }

            stepAssrt = (Assrt)proof[stepNbr];
            stepFrame = stepAssrt.getMandFrame();
            if (stepFrame.hypArray.length == 0) {
                pStack[pStackCnt++] = stepFormula;
                continue nextStep;
            }

            stepLabel = stepAssrt.getLabel();

            findUniqueSubstMapping();

            /**
             * Optimization: don't go thru checkDjVars needlessly.
             */
            if (stepFrame.djVarsArray.length > 0) {
                stepNbrOutputString = Integer.toString(stepNbr + 1);
                checkDjVars();
            }

            pStackCnt -= stepFrame.hypArray.length;
            if (pStackCnt < 0)
                raiseVerifyException(Integer.toString(stepNbr + 1), stepLabel,
                    ProofConstants.ERRMSG_PROOF_STACK_UNDERFLOW);

            stepSubstFormula = applySubstMapping(stepFormula);
            pStack[pStackCnt++] = stepSubstFormula;

        }

        if (pStackCnt != 1)
            if (proof.length == 0)
                raiseVerifyException(Integer.toString(stepNbr), " ",
                    ProofConstants.ERRMSG_PROOF_HAS_ZERO_STEPS);
            else
                raiseVerifyException(Integer.toString(stepNbr), " ",
                    ProofConstants.ERRMSG_PROOF_STACK_GT_1_AT_END);

        if (!proofStmtFormula.equals(pStack[0]))
            if (isExprRPNVerify && proofStmtFormula.exprEquals(pStack[0])) {}
            else
                raiseVerifyException(
                    Integer.toString(stepNbr + 1),
                    " ",
                    ProofConstants.ERRMSG_FINAL_STACK_ENTRY_UNEQUAL
                        + pStack[0].toString());
    }

    /**
     * ok, some input, work and output areas in global (class) work areas...
     * 
     * <pre>
     * input: -  MandFrame stepFrame  -->  (frame for theorem referenced
     *              Hyp[]  hypArray;        in proof step, contains
     *                                      mandatory hypothesis array.)
     * 
     *        -  Formula[] pStack;         (the proof stack, which should
     *           int       pStackCnt       contain 'n' entries at the end
     *                                     that have types matching the
     *                                     hypArray entries -- they will
     *                                     be used to generate
     *                                     substitutions which force the
     *                                     hypArray entriesto match
     *                                     (and if not, error!))
     * 
     * output:-  SubstMapEntry[] subst --> contains output array of:
     * 
     *              Sym substFrom (variable from proof step mandatory
     *                             hypotheses)
     *              Sym[] substTo (expression/variable to substitute
     *                             FOR each occurrence of substFrom
     *                             in the proof step's mandatory
     *                             hypotheses and assertion.)
     * </pre>
     * 
     * @throws VerifyException if DjVars (restriction) violation found.
     */
    private void findUniqueSubstMapping() throws VerifyException {
        substCnt = stepFrame.hypArray.length;
        final int stackMatchBegin = pStackCnt - substCnt;

        if (stackMatchBegin < 0)
            raiseVerifyException(Integer.toString(stepNbr + 1), stepLabel,
                ProofConstants.ERRMSG_STACK_SIZE_MISMATCH_FOR_STEP_HYPS);

        // 1) scan stepFrame.hypArray, pulling out VarHyp's and
        // creating the subst array entries for them (subst
        // is parallel by index to hypArray and pStack, with
        // unused entries left null...initially.)
        nextHyp: for (int i = 0, pStackIndex = stackMatchBegin; i < substCnt; i++, pStackIndex++)
        {
            final Hyp hyp = stepFrame.hypArray[i];
            if (hyp.getTyp() != pStack[pStackIndex].getTyp())
                raiseVerifyException(
                    Integer.toString(stepNbr + 1),
                    stepLabel,
                    ProofConstants.ERRMSG_HYP_TYP_MISMATCH_STACK_TYP
                        + hyp.getTyp() + ProofConstants.ERRMSG_STACK_ITEM_TYP
                        + pStack[pStackIndex].getTyp());
            if (!hyp.isVarHyp()) {
                subst[i] = null;
                continue nextHyp;
            }

            if (subst[i] == null)
                subst[i] = new SubstMapEntry(((VarHyp)hyp).getVar(),
                    pStack[pStackIndex].getExpr());
            else {
                subst[i].substFrom = ((VarHyp)hyp).getVar();
                subst[i].substTo = pStack[pStackIndex].getExpr();
            }

        }

        // 2) now! go back through hypArray applying the generated
        // substitutions to the non-VarHyp formulas -- which are
        // then compared to the corresponding entries on pStack
        // to make sure the substitutions "work"...and that the
        // proofstep is therefore "legal".
        nextLogHyp: for (int i = 0, pStackIndex = stackMatchBegin; i < substCnt; i++, pStackIndex++)
        {
            final Hyp hyp = stepFrame.hypArray[i];
            if (hyp.isVarHyp())
                continue nextLogHyp;
            final Formula workFormula = applySubstMapping(hyp.getFormula());
            if (!workFormula.equals(pStack[pStackIndex]))
                raiseVerifyException(Integer.toString(stepNbr + 1), stepLabel,
                    ProofConstants.ERRMSG_STEP_LOG_HYP_SUBST_UNEQUAL
                        + ProofConstants.ERRMSG_STACK + pStack[pStackIndex]
                        + ProofConstants.ERRMSG_SUBST_HYP + workFormula);

        }
    }

    /**
     * ok, some input, work and output areas in global (class) work areas
     * <p>
     * input: SubstMapEntry[] subst (and substCnt)
     * <p>
     * work: Sym[] wExpr (and wExprCnt)
     * <p>
     * output: a new Formula
     * <p>
     * NOTE: DO NOT substitute for the Type constant at the beginning of the
     * formula.
     * 
     * @param f the formula
     * @return a new Formula
     */
    private Formula applySubstMapping(final Formula f) {
        SubstMapEntry substMapEntry;
        Sym fSym;
        final int fCnt = f.getCnt();
        final Sym[] fSymArray = f.getSym();
        wExpr[0] = fSymArray[0];
        wExprCnt = 1;

        nextFSym: for (int i = 1; i < fCnt; i++) {
            fSym = fSymArray[i];
            nextSubst: for (int j = 0; j < substCnt; j++) {
                if ((substMapEntry = subst[j]) == null)
                    // this wasn't a VarHyp subst entry
                    continue nextSubst;
                if (fSym == substMapEntry.substFrom) {
                    for (final Sym element : substMapEntry.substTo)
                        wExpr[wExprCnt++] = element;
                    continue nextFSym;
                }
            }
            wExpr[wExprCnt++] = fSym; // no subst, use orig sym!
        }

        return new Formula(wExprCnt, wExpr);
    }

    /**
     * ok, some input, work and output areas in global (class) work areas...
     * 
     * <pre>
     * input: -  MandFrame proofStmtFrame (frame for theorem to be proved)
     * 
     *        -  MandFrame stepFrame    (frame for theorem referenced
     *                                   in proof step)
     * 
     *        -  SubstMapEntry[] subst --> contains array of:
     * 
     *              Sym substFrom (variable from proof step mandatory
     *                             hypotheses)
     *              Sym[] substTo (expression/variable to substitute
     *                              FOR each occurrence of substFrom
     *                              in the proof step's mandatory
     *                              hypotheses and assertion.)
     * 
     *        -  stepNbrOutputString (already computed...)
     * </pre>
     * 
     * @throws VerifyException if DjVars (restriction) violation found!
     */
    private void checkDjVars() throws VerifyException {

        // 1) see if any pairs of variables in subst[].substFrom are
        // listed as DjVars in the proof step's stepFrame

        final int xMax = substCnt - 1;
        final int yMax = xMax + 1;

        for (int fromX = 0; fromX < xMax; fromX++)
            if (subst[fromX] != null)
                for (int fromY = fromX + 1; fromY < yMax; fromY++)
                    if (subst[fromY] != null)
                        if (MandFrame.isVarPairInDjArray(stepFrame,
                            (Var)subst[fromX].substFrom,
                            (Var)subst[fromY].substFrom))
                            checkSubstToVars(fromX, fromY);
    }

    /**
     * Well, ALLRIGHTY THEN! Let's continue...
     * <p>
     * but first, let's recall why we are down here in the dungeon...
     * <p>
     * <b>PURPOSE:</b>
     * <ol>
     * <li>make sure that the substitution "to" expressions do not have any
     * variables in common (because the substitution "from" variables have a
     * DjVars restriction in the theorem referenced by the proof step.)
     * <li>we must check that each possible pair of variables, taken one each
     * from substTo[fromX] and substTo[fromY], has a corresponding DjVars
     * restriction in the mandatory or optional frame of the theorem being
     * proved!
     * </ol>
     * ok, some input, work and output areas in global (class) work areas...
     * 
     * <pre>
     * input: -  MandFrame proofStmtFrame (frame for theorem to be proved)
     *           Opt       proofStmtOptFrame (frame for theorem to be
     *                     proved)
     * 
     *        -  SubstMapEntry[] subst --> contains array of:
     * 
     *              Sym substFrom (variable from proof step mandatory
     *                            hypotheses)
     * 
     *              Sym[] substTo (expression/variable to substitute FOR
     *                             each occurrence of substFrom in the
     *                             proof step's mandatory hypotheses and
     *                             assertion.)
     * 
     *        -  stepNbr and stepLabel, ready for diagnostic purposes!
     *        -  stepNbrOutputString (already computed...)
     * </pre>
     * 
     * @param x the index of the first SubstMapEntry of the pair
     * @param y the index of the second SubstMapEntry of the pair
     * @throws VerifyException if DjVars (restriction) violation found!
     */
    private void checkSubstToVars(final int x, final int y)
        throws VerifyException
    {
        Sym symI;
        Sym symJ;
        for (int i = 0; i < subst[x].substTo.length; i++) {
            symI = subst[x].substTo[i];
            if (symI.isVar())
                nextSymJ: for (int j = 0; j < subst[y].substTo.length; j++) {
                    symJ = subst[y].substTo[j];
                    if (!symJ.isVar())
                        continue nextSymJ;
                    if (symI == symJ)
                        raiseVerifyException(stepNbrOutputString, stepLabel,
                            ProofConstants.ERRMSG_SUBST_TO_VARS_MATCH + symI
                                + ProofConstants.ERRMSG_EQUALS_LITERAL + symJ);

                    // this is for the benefit of ProofAsst...
                    if (proofDjVarsSoftErrorsIgnore)
                        continue nextSymJ;

                    if (!MandFrame.isVarPairInDjArray(proofStmtFrame,
                        (Var)symI, (Var)symJ)
                        && !OptFrame.isVarPairInDjArray(proofStmtOptFrame,
                            (Var)symI, (Var)symJ)
                        && // don't report "soft" Dj WorkVar errors
                        !((Var)symI).getIsWorkVar()
                        && !((Var)symJ).getIsWorkVar())
                    {
                        // this is for the benefit of ProofAsst...
                        if (proofSoftDjVarsErrorList != null) {
                            try {
                                proofSoftDjVarsErrorList.add(new DjVars(
                                    (Var)symI, (Var)symJ));
                            } catch (final LangException e) {
                                throw new IllegalArgumentException(e);
                            }
                            continue nextSymJ;
                        }

                        raiseVerifyException(stepNbrOutputString, stepLabel,
                            ProofConstants.ERRMSG_SUBST_TO_VARS_NOT_DJ + symI
                                + ProofConstants.ERRMSG_AND_LITERAL + symJ);
                    }
                }
        }
    }

    public void raiseVerifyException(final String stepNbrIndexString,
        final String stepLabel, final String errmsg) throws VerifyException
    {
        throw new VerifyException(errmsg + ProofConstants.ERRMSG_THEOREM_LABEL
            + proofStmtLabel + ProofConstants.ERRMSG_THEOREM_STEP_NBR
            + stepNbrIndexString + ProofConstants.ERRMSG_THEOREM_STEP_LABEL
            + stepLabel);
    }

    // *
    // *******attempting to dynamically resize arrays as needed...
    // in the face of the unknowable.
    // *
    public int getPStackHighwater() {
        return pStackHighwater;
    }
    public int getPExprHighwater() {
        return wExprHighwater;
    }
    public int getSubstHighwater() {
        return substHighwater;
    }

    private void reInitArrays(final int retry) throws VerifyException {
        if (retryCnt == -1) {
            initArrays();
            return;
        }
        retryCnt = retry;

        if (pStackCnt > pStackHighwater)
            pStackHighwater = pStackCnt;
        if (wExprCnt > wExprHighwater)
            wExprHighwater = wExprCnt;
        if (substCnt > wExprHighwater)
            substHighwater = substCnt;

        if (retry == 0) {
            pStackCnt = 0;
            wExprCnt = 0;
            substCnt = 0;
            return;
        }

        if (pStackMax < ProofConstants.PROOF_PSTACK_HARD_FAILURE_LEN) {
            if (pStackMax < pStackCnt + 10) {
                pStackMax *= 2;
                if (pStackMax > ProofConstants.PROOF_PSTACK_HARD_FAILURE_LEN)
                    pStackMax = ProofConstants.PROOF_PSTACK_HARD_FAILURE_LEN;
                pStack = new Formula[pStackMax];
            }
            pStackCnt = 0;
        }
        else
            throw new VerifyException(
                ProofConstants.ERRMSG_PSTACK_ARRAY_OVERFLOW + pStackMax
                    + ProofConstants.ERRMSG_THEOREM_LABEL + proofStmtLabel);

        if (wExprMax < ProofConstants.PROOF_WEXPR_HARD_FAILURE_LEN) {
            if (wExprMax < wExprCnt + 10) {
                wExprMax *= 2;
                if (wExprMax > ProofConstants.PROOF_WEXPR_HARD_FAILURE_LEN)
                    wExprMax = ProofConstants.PROOF_WEXPR_HARD_FAILURE_LEN;
                wExpr = new Sym[wExprMax];
            }
            wExprCnt = 0;
        }
        else
            throw new VerifyException(
                ProofConstants.ERRMSG_WEXPR_ARRAY_OVERFLOW + wExprMax
                    + ProofConstants.ERRMSG_THEOREM_LABEL + proofStmtLabel);

        if (substMax < ProofConstants.PROOF_SUBST_HARD_FAILURE_LEN) {
            if (substMax < substCnt + 10) {
                substMax *= 2;
                if (substMax > ProofConstants.PROOF_SUBST_HARD_FAILURE_LEN)
                    substMax = ProofConstants.PROOF_SUBST_HARD_FAILURE_LEN;
                subst = new SubstMapEntry[substMax];
                for (int i = 0; i < substMax; i++)
                    subst[i] = new SubstMapEntry();
            }
            substCnt = 0;
        }
        else
            throw new VerifyException(
                ProofConstants.ERRMSG_SUBST_ARRAY_OVERFLOW + substMax
                    + ProofConstants.ERRMSG_THEOREM_LABEL + proofStmtLabel);

        if (retryCnt > ProofConstants.PROOF_ABSOLUTE_MAX_RETRIES)
            throw new IllegalArgumentException(
                ProofConstants.ERRMSG_PROOF_ABS_MAX_RETRY_EXCEEDED);
    }

    private void initArrays() {
        retryCnt = 0;

        pStackCnt = 0;
        pStackMax = ProofConstants.PROOF_PSTACK_INIT_LEN;
        pStackHighwater = 0;
        pStack = new Formula[pStackMax];

        wExprCnt = 0;
        wExprMax = ProofConstants.PROOF_WEXPR_INIT_LEN;
        wExprHighwater = 0;
        wExpr = new Sym[wExprMax];

        substCnt = 0;
        substMax = ProofConstants.PROOF_SUBST_INIT_LEN;
        substHighwater = 0;
        subst = new SubstMapEntry[substMax];
        for (int i = 0; i < substMax; i++)
            subst[i] = new SubstMapEntry();
    }

    /**
     * Generate Formula from RPN.
     * 
     * @param formulaRPN to be converted to a Formula
     * @param stepLabelForMessages used for abend message :)
     * @return Formula generated from RPN
     */
    public Formula convertRPNToFormula(final Stmt[] formulaRPN,
        final String stepLabelForMessages)
    {

        proof = formulaRPN;
        proofStmtLabel = stepLabelForMessages;

        String errMsg = null;
        boolean needToRetry = true;

        Formula out = null;
        try {
            reInitArrays(0);
            while (needToRetry)
                try {
                    errMsg = null;
                    out = generateFormulaFromRPN();
                    needToRetry = false;
                } catch (final ArrayIndexOutOfBoundsException e) {
                    ++retryCnt;
                    reInitArrays(retryCnt);
                } catch (final VerifyException e) {
                    needToRetry = false;
                    errMsg = e.getMessage();
                }
        } catch (final VerifyException e) {
            errMsg = e.getMessage();
        }

        if (errMsg != null)
            throw new IllegalArgumentException(
                ProofConstants.ERRMSG_RPN_TO_FORMULA_CONV_FAILURE + errMsg);

        return out;

    }

    private Formula generateFormulaFromRPN() throws VerifyException {

        nextStep: for (stepNbr = 0; stepNbr < proof.length; stepNbr++) {

            if (proof[stepNbr] == null)
                raiseVerifyException(Integer.toString(stepNbr + 1), " ",
                    ProofConstants.ERRMSG_PROOF_STEP_INCOMPLETE);

            stepFormula = proof[stepNbr].getFormula();
            if (proof[stepNbr].isHyp()) {
                pStack[pStackCnt++] = stepFormula;
                continue nextStep;
            }

            stepAssrt = (Assrt)proof[stepNbr];
            stepFrame = stepAssrt.getMandFrame();
            if (stepFrame.hypArray.length == 0) {
                pStack[pStackCnt++] = stepFormula;
                continue nextStep;
            }

            stepLabel = stepAssrt.getLabel();

            findUniqueSubstMapping();

            pStackCnt -= stepFrame.hypArray.length;
            if (pStackCnt < 0)
                raiseVerifyException(Integer.toString(stepNbr + 1), stepLabel,
                    ProofConstants.ERRMSG_PROOF_STACK_UNDERFLOW);

            stepSubstFormula = applySubstMapping(stepFormula);
            pStack[pStackCnt++] = stepSubstFormula;

        }

        if (pStackCnt != 1)
            if (proof.length == 0)
                raiseVerifyException(Integer.toString(stepNbr), " ",
                    ProofConstants.ERRMSG_PROOF_HAS_ZERO_STEPS);
            else
                raiseVerifyException(Integer.toString(stepNbr), " ",
                    ProofConstants.ERRMSG_PROOF_STACK_GT_1_AT_END);

        return new Formula(pStack[0].getCnt(), pStack[0].getSym());
    }

}
