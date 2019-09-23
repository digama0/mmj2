//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ProofAsst.java  0.11 11/01/2011
 *
 * Version 0.03
 *     - fix vol test bug 3/27 (no qed step null pointer)
 *     - update misc. comments
 *     - add Proof Assistant 'Derive' Feature
 *
 * Sep-09-2006 - Version 0.04 - TMFF project
 *     - modified to support ProofAsstGUI File/GetProof
 *
 * Jun-01-2007 - Version 0.05 -
 *     - various changes to get rid of ProofWorkStmt.status
 *     - add call to proofWorksheet.addGeneratedDjVarsStmts()
 *
 * Aug-01-2007 - Version 0.06 -
 *     - Added asciiRetest option to importFrom*
 *
 * Nov-01-2007 - Version 0.07 -
 *     - Position cursor to *last* incomplete statement
 *       instead of first.
 *     - Call proofWorksheet.posCursorAtLastIncompleteOrQedStmt()
 *       when RPN proof generated successfully, and output an
 *       info message about the success. The old code positioned
 *       the cursor (intentionally) to the end of the RPN proof.
 *       And, output I-PA-0119, ERRMSG_PA_RPN_PROOF_GENERATED_1/2.
 *
 * Feb-01-2008 - Version 0.08 -
 *     - modify tmffReformat() to accept boolean argument,
 *       "inputCursorStep" which requests reformatting of
 *       just one proof step, the step underneath the cursor
 *       when the request was made.
 *     - Use new proofAsstPreferences.getIncompleteStmtCursorFirst()
 *       and     proofAsstPreferences.getIncompleteStmtCursorLast()
 *       to control cursor positioning when there are no
 *       unification errors.
 *     - "re-added back" posCursorAtFirstIncompleteOrQedStmt();
 *
 * Mar-01-2008 - Version 0.09 -
 *     - Misc. tidy up.
 *     - Add StepSelector Dialog/Search
 *     - Add PreprocessRequest option on Unify to
 *       implement Unify menu item Unify+Rederive Formulas
 *     - Remove Hints feature
 *
 * Aug-01-2008 - Version 0.10 -
 *     - Add TheoremLoader to constructor and various other
 *       items related to TheoremLoader.
 *
 * Version 0.11 - Nov-01-2011:
 *     - Added:
 *         exportViaGMFF() for use by ProofAsstGUI
 *       and
 *         getSortedSkipSeqTheoremIterable(String startTheoremLabel)
 *         getSortedTheoremIterable(int lowestMObjSeq)
 *         exportOneTheorem(String theoremLabel)
 *         exportOneTheorem(Theorem theorem)
 *       for use by GMFFManager.exportTheorem
 *     - Modified volumeTestOutputRoutine to use theorem to
 *       get label, not proof worksheet (because in some
 *       cases the proof worksheet may be invalid.)
 *     - Rewrote incompleteStepCursorPositioning() to
 *       fix "AsIs" cursor positioning bug.
 */

package mmj.pa;

import static mmj.pa.ProofWorksheet.addLabelContext;

import java.io.*;
import java.util.*;

import mmj.gmff.GMFFException;
import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.mmio.MMIOConstants.LineColumnContext;
import mmj.mmio.MMIOException;
import mmj.pa.MacroManager.CallbackType;
import mmj.pa.PaConstants.*;
import mmj.pa.StepRequest.StepRequestType;
import mmj.tl.*;
import mmj.transforms.TransformationManager;
import mmj.util.OutputBoss;
import mmj.util.StopWatch;
import mmj.verify.*;
import mmj.verify.GrammarConstants.LabelContext;

/**
 * The {@code ProofAsst}, along with the rest of the {@code mmj.pa} package
 * provides a graphical user interface (GUI) facility for developing Metamath
 * proofs. {@code ProofAsst} is the go-to guy, essentially a control module that
 * knows where to go to get things done. It is invoked by
 * mmj.util.ProofAsstBoss.java, and invokes mmj.pa.ProofAsstGUI, among others.
 * Nomenclature: a proof-in-progress is implemented by the
 * mmj.pa.ProofWorksheet.java class. A large quantity of information and useful
 * stuff is contained in mmj.pa.PaConstants.java.
 */
public class ProofAsst implements TheoremLoaderCommitListener {

    private boolean initializedOK;

    // global variables stored here for convenience
    private ProofAsstGUI proofAsstGUI;
    private final ProofAsstPreferences proofAsstPreferences;
    public final ProofUnifier proofUnifier;
    private final LogicalSystem logicalSystem;
    private final Grammar grammar;
    private final VerifyProofs verifyProofs;
    private Messages messages;
    private final TheoremLoader theoremLoader;
    public final MacroManager macroManager;

    // -----------------------------------------------------------------
    // -------------------------LOCAL CLASSES---------------------------
    // -----------------------------------------------------------------

    /** Information about theorem unification. */
    private static class TheoremTestResult {
        public final StopWatch stopWatch;
        public final ProofWorksheet proofWorksheet;
        public final Theorem theorem;

        public TheoremTestResult(final StopWatch stopWatch,
            final ProofWorksheet proofWorksheet, final Theorem theorem)
        {
            this.stopWatch = stopWatch;
            this.proofWorksheet = proofWorksheet;
            this.theorem = theorem;
        }

        // define it for debug reasons
        @Override
        public String toString() {
            return theorem.toString() + ":" + stopWatch.getElapsedTimeInStr();
        }
    }

    /** Statistic information about theorem unification tests. */
    private static class VolumeTestStats {
        public int nbrTestTheoremsProcessed = 0;
        public int nbrTestNotProvedPerfectly = 0;
        public int nbrTestProvedDifferently = 0;
    }

    // -----------------------------------------------------------------
    // ----------------------------METHODS------------------------------
    // -----------------------------------------------------------------

    /**
     * Constructor.
     *
     * @param proofAsstPreferences variable settings
     * @param logicalSystem the loaded Metamath data
     * @param grammar the mmj.verify.Grammar object
     * @param verifyProofs the mmj.verify.VerifyProofs object
     * @param theoremLoader the mmj.tl.TheoremLoader object
     * @param macroManager the mmj.pa.MacroManager object
     */
    public ProofAsst(final ProofAsstPreferences proofAsstPreferences,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final VerifyProofs verifyProofs, final TheoremLoader theoremLoader,
        final MacroManager macroManager)
    {

        this.proofAsstPreferences = proofAsstPreferences;
        this.logicalSystem = logicalSystem;
        this.grammar = grammar;
        this.verifyProofs = verifyProofs;
        this.theoremLoader = theoremLoader;
        this.macroManager = macroManager;

        proofUnifier = new ProofUnifier(this);

        messages = null;

        initializedOK = false;

    }

    /**
     * Triggers the Proof Assistant GUI.
     *
     * @param m the mmj.lang.Messages object used to store error and
     *            informational messages.
     */
    public void doGUI(final Messages m) {
        messages = m;

        if (!getInitializedOK())
            initializeLookupTables(m);

        proofAsstGUI = new ProofAsstGUI(this, proofAsstPreferences,
            theoremLoader);

        if (macroManager != null)
            macroManager.runCallback(CallbackType.BUILD_GUI);

        proofAsstGUI.showMainFrame();
    }

    /**
     * @return initializedOK flag.
     */
    public boolean getInitializedOK() {
        return initializedOK;
    }

    public LogicalSystem getLogicalSystem() {
        return logicalSystem;
    }

    public Messages getMessages() {
        return messages;
    }

    public ProofAsstGUI getProofAsstGUI() {
        return proofAsstGUI;
    }

    public ProofAsstPreferences getPreferences() {
        return proofAsstPreferences;
    }

    public Grammar getGrammar() {
        return grammar;
    }

    public VerifyProofs getVerifyProofs() {
        return verifyProofs;
    }

    public List<Assrt> sortAssrtListForSearch(final List<Assrt> list) {
        final List<Assrt> sorted = new ArrayList<>(list);
        Collections.sort(sorted, Assrt.NBR_LOG_HYP_SEQ);
        return sorted;
    }

    /**
     * Initialized Unification lookup tables, etc. for Unification.
     *
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @return initializedOK flag.
     */
    public boolean initializeLookupTables(final Messages messages) {
        this.messages = messages;
        initializedOK = proofUnifier.initializeLookupTables(messages);
        proofAsstPreferences.getSearchMgr().initOtherEnvAreas(this,
            logicalSystem, grammar, verifyProofs, messages);
        logicalSystem.bookManager.getDirectSectionDependencies(logicalSystem);
        initAutotransformations(true, false, true);
        return initializedOK;
    }

    public List<Assrt> getSortedAssrtSearchList() {
        return proofUnifier.getSortedAssrtSearchList();
    }

    /**
     * Applies a set of updates from the TheoremLoader as specified in the
     * mmtTheoremSet object to the ProofAsst local caches of data.
     *
     * @param mmtTheoremSet MMTTheoremSet object containing the adds and updates
     *            already made to theorems in the LogicalSystem.
     */
    public void commit(final MMTTheoremSet mmtTheoremSet) {
        if (!getInitializedOK())
            return; // the stmtTbl data has not been stored yet

        final List<Theorem> listOfAssrtAddsSortedBySeq = mmtTheoremSet
            .buildSortedAssrtListOfAdds(MObj.SEQ);

        if (listOfAssrtAddsSortedBySeq.isEmpty())
            return;

        proofUnifier
            .mergeListOfAssrtAddsSortedBySeq(listOfAssrtAddsSortedBySeq);
    }

    /**
     * Verifies all proofs in the Logical System
     * <p>
     * This is here because the Proof Assistant GUI doesn't know how to do
     * anything...
     *
     * @return Messages object.
     */
    public Messages verifyAllProofs() {
        verifyProofs.verifyAllProofs(messages, logicalSystem.getStmtTbl());
        return messages;
    }

    /**
     * Invokes TheoremLoader to load all theorems in the MMT Folder.
     * <p>
     * This is here because the Proof Assistant GUI doesn't know how to do
     * anything...
     *
     * @return Messages object.
     */
    public Messages loadTheoremsFromMMTFolder() {
        try {
            theoremLoader.loadTheoremsFromMMTFolder(logicalSystem, messages);
        } catch (final TheoremLoaderException e) {
            messages.accumException(e);
        }
        return messages;
    }

    /**
     * Invokes GMFF to export the current Proof Worksheet.
     *
     * @param proofText Proof Worksheet text string.
     * @return Messages object.
     */
    public Messages exportViaGMFF(final String proofText) {

        try {
            for (final GMFFException confirm : logicalSystem.getGMFFManager()
                .exportProofWorksheet(proofText, null))
                messages.accumException(confirm);
        } catch (final GMFFException e) {
            messages.accumException(new ProofAsstException(e,
                PaConstants.ERRMSG_PA_GUI_EXPORT_VIA_GMFF_FAILED,
                e.getMessage()));
        }
        return messages;
    }

    /**
     * Invokes TheoremLoader to extract a theorem to the MMT Folder.
     * <p>
     * This is here because the Proof Assistant GUI doesn't know how to do
     * anything...
     *
     * @param theorem the theorem to extract
     * @return Messages object.
     */
    public Messages extractTheoremToMMTFolder(final Theorem theorem) {
        try {
            theoremLoader.extractTheoremToMMTFolder(theorem, logicalSystem,
                messages);
        } catch (final TheoremLoaderException e) {
            messages.accumException(e);
        }
        return messages;
    }

    /**
     * Builds new ProofWorksheet for a theorem.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * Note that the output ProofWorksheet is skeletal and is destined for a
     * straight-shot, output to the GUI screen. The ProofAsst and its components
     * retain no memory of a ProofWorksheet between screen actions. Each time
     * the user requests a new action the text is scraped off the screen and
     * built into a new ProofWorksheet object!
     * <p>
     * Notice also that this function just invokes a ProofWorksheet constructor.
     * Why? Because the ProofAsstGUI.java program has no access to or knowledge
     * of LogicalSystem, Grammar, etc. The only external knowledge it has is
     * ProofAsstPreferences.
     *
     * @param newTheoremLabel the name of the new proof
     * @return ProofWorksheet initialized skeleton proof
     */
    public ProofWorksheet startNewProof(final String newTheoremLabel) {

        final ProofWorksheet w = new ProofWorksheet(newTheoremLabel,
            proofAsstPreferences, logicalSystem, grammar, messages);
        w.outputCursorInstrumentationIfEnabled();
        return w;
    }

    /**
     * Builds new ProofWorksheet for the next theorem after the current theorem
     * sequence number on the ProofAsstGUI.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * This function is provided for students who wish to work their way through
     * Metamath databases such as set.mm and prove each theorem. It is like
     * Forward-GetProof except that it returns a skeletal Proof Worksheet, and
     * thus helps the student by not revealing the contents of the existing
     * proof in the Metamath database.
     * <p>
     * Note that the output ProofWorksheet is skeletal and is destined for a
     * straight-shot, output to the GUI screen. The ProofAsst and its components
     * retain no memory of a ProofWorksheet between screen actions. Each time
     * the user requests a new action the text is scraped off the screen and
     * built into a new ProofWorksheet object!
     * <p>
     * Notice also that this function just invokes a ProofWorksheet constructor.
     * Why? Because the ProofAsstGUI.java program has no access to or knowledge
     * of LogicalSystem, Grammar, etc. The only external knowledge it has is
     * ProofAsstPreferences.
     *
     * @param currProofMaxSeq sequence number of current proof on ProofAsstGUI
     *            screen.
     * @return ProofWorksheet initialized skeleton proof
     */
    public ProofWorksheet startNewNextProof(final int currProofMaxSeq) {

        ProofWorksheet w;

        final Theorem theorem = getTheoremForward(currProofMaxSeq, false); // not
                                                                           // a
                                                                           // retry
                                                                           // :)

        if (theorem == null) {
            messages.accumMessage(PaConstants.ERRMSG_PA_FWD_BACK_SEARCH_NOTFND,
                "forward");

            w = new ProofWorksheet(proofAsstPreferences, messages, // oh yeah,
                                                                   // we got 'em
                true, // structuralErrors
                ProofAsstCursor.makeProofStartCursor());
        }
        else
            w = new ProofWorksheet(theorem.getLabel(), proofAsstPreferences,
                logicalSystem, grammar, messages);

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Fetches a Theorem using an input Label String.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     *
     * @param theoremLabel label of Theorem to retrieve from statement table.
     * @return Theorem or null if Label not found or is not a Theorem Stmt
     *         label.
     */
    public Theorem getTheorem(final String theoremLabel) {

        final Stmt stmt = logicalSystem.getStmtTbl().get(theoremLabel.trim());
        if (stmt != null && stmt instanceof Theorem)
            return (Theorem)stmt;
        return null;
    }

    public Stmt getStmt(final String s) {
        return logicalSystem.getStmtTbl().get(s.trim());
    }

    /**
     * Builds ProofWorksheet for an existing theorem.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     *
     * @param oldTheorem theorem to get
     * @param exportFormatUnified true means include step Ref labels
     * @param hypsOrder the order of step Hyps
     * @return ProofWorksheet initialized.
     */
    public ProofWorksheet getExistingProof(final Theorem oldTheorem,
        final boolean exportFormatUnified, final HypsOrder hypsOrder)
    {

        ProofWorksheet w = getExportedProofWorksheet(oldTheorem,
            exportFormatUnified, hypsOrder, false); // deriveFormulas

        final ProofAsstCursor cursor = ProofAsstCursor.makeProofStartCursor();

        if (w == null)
            w = new ProofWorksheet(oldTheorem.getLabel(), proofAsstPreferences,
                logicalSystem, grammar, messages);
        else
            w.setProofCursor(cursor);

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Builds ProofWorksheet for the next theorem after a given MObj sequence
     * number.
     * <p>
     * The search wraps to the start if the end is reached.
     * <p>
     * Note: The search list excludes any Theorems excluded by the user from
     * Proof Unification (see RunParm ProofAsstUnifySearchExclude). The
     * exclusion is made for technical reasons (expediency) -- if you don't like
     * it a whole lot we can change it.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     *
     * @param currProofMaxSeq sequence number of ProofWorksheet from
     *            ProofAsstGUI currProofMaxSeq field.
     * @param exportFormatUnified true means include step Ref labels
     * @param hypsOrder the order of step Hyps
     * @return ProofWorksheet initialized.
     */
    public ProofWorksheet getNextProof(final int currProofMaxSeq,
        final boolean exportFormatUnified, final HypsOrder hypsOrder)
    {

        ProofWorksheet w;

        final ProofAsstCursor cursor = ProofAsstCursor.makeProofStartCursor();

        // not a retry :)
        final Theorem theorem = getTheoremForward(currProofMaxSeq, false);
        if (theorem == null) {
            messages.accumMessage(PaConstants.ERRMSG_PA_FWD_BACK_SEARCH_NOTFND,
                "forward");
            w = new ProofWorksheet(proofAsstPreferences,
                /* oh yeah, we got 'em */messages, /* structuralErrors=*/true,
                cursor);
        }
        else {
            w = getExportedProofWorksheet(theorem, exportFormatUnified,
                hypsOrder, /* deriveFormulas=*/false);

            if (w == null)
                w = new ProofWorksheet(theorem.getLabel(), proofAsstPreferences,
                    logicalSystem, grammar, messages);
            else
                w.setProofCursor(cursor);
        }

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Builds ProofWorksheet for the first theorem before a given MObj sequence
     * number.
     * <p>
     * The search wraps to the end if the start is reached.
     * <p>
     * Note: The search list excludes any Theorems excluded by the user from
     * Proof Unification (see RunParm ProofAsstUnifySearchExclude). The
     * exclusion is made for technical reasons (expediency) -- if you don't like
     * it a whole lot we can change it.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     *
     * @param currProofMaxSeq sequence number of ProofWorksheet from
     *            ProofAsstGUI currProofMaxSeq field.
     * @param exportFormatUnified true means include step Ref labels
     * @param hypsOrder the order of step Hyps
     * @return ProofWorksheet initialized.
     */
    public ProofWorksheet getPreviousProof(final int currProofMaxSeq,
        final boolean exportFormatUnified, final HypsOrder hypsOrder)
    {

        ProofWorksheet w;

        final ProofAsstCursor cursor = ProofAsstCursor.makeProofStartCursor();

        final Theorem theorem = getTheoremBackward(currProofMaxSeq, false); // not
                                                                            // a
                                                                            // retry
                                                                            // :)

        if (theorem == null) {
            messages.accumMessage(PaConstants.ERRMSG_PA_FWD_BACK_SEARCH_NOTFND,
                "backward");
            w = new ProofWorksheet(proofAsstPreferences, messages, // oh yeah,
                                                                   // we got 'em
                true, // structuralErrors
                cursor);
        }
        else {
            w = getExportedProofWorksheet(theorem, exportFormatUnified,
                hypsOrder, false); // deriveFormulas

            if (w == null)
                w = new ProofWorksheet(theorem.getLabel(), proofAsstPreferences,
                    logicalSystem, grammar, messages);
            else
                w.setProofCursor(cursor);
        }

        w.outputCursorInstrumentationIfEnabled();

        return w;
    }

    /**
     * Attempts Unification for a proof contained in a String proof text area.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * The ProofWorksheetParser class is used to parse the input proof text. The
     * reason for using this intermediary is that the system is designed to be
     * able to read a file of proof texts (which is a feature designed for
     * testing purposes, but still available to a user via the BatchMMJ2
     * facility.)
     *
     * @param proofText proof text from ProofAsstGUI screen, or any String
     *            conforming to the formatting rules of ProofAsst.
     * @param renumReq renumbering of proof steps requested
     * @param noConvertWV true if we should not replace work vars with dummy
     *            vars in derivation steps
     * @param preprocessRequest if not null specifies an editing operation to be
     *            applied to the proof text before other processing.
     * @param inputCursorPos caret offset plus one of input or -1 if caret pos
     *            unavailable to caller.
     * @param stepRequest may be null, or StepSelector Search or Choice request
     *            and will be loaded into the ProofWorksheet.
     * @param tlRequest may be null or a TLRequest.
     * @param printOkMessages whether we could print any messages except errors?
     * @return ProofWorksheet unified.
     */
    public ProofWorksheet unify(final boolean renumReq,
        final boolean noConvertWV, final String proofText,
        final PreprocessRequest preprocessRequest,
        final StepRequest stepRequest, final TLRequest tlRequest,
        final int inputCursorPos, final boolean printOkMessages)
    {

        String proofTextEdited;
        if (preprocessRequest == null)
            proofTextEdited = proofText;
        else
            try {
                proofTextEdited = preprocessRequest.doIt(proofText);
            } catch (final ProofAsstException e) {
                messages.accumException(e);
                return updateWorksheetWithException(null, null);
            }

        if (macroManager != null) {
            macroManager.set("proofText", proofTextEdited);
            macroManager.runCallback(CallbackType.PREPROCESS);
            proofTextEdited = (String)macroManager.get("proofText");
        }

        final boolean[] errorFound = new boolean[1];
        final ProofWorksheet proofWorksheet = getParsedProofWorksheet(
            proofTextEdited, errorFound, inputCursorPos, stepRequest);

        if (!errorFound[0]) {

            if (renumReq)
                proofWorksheet.renumberProofSteps(
                    PaConstants.PROOF_STEP_RENUMBER_START,
                    PaConstants.PROOF_STEP_RENUMBER_INTERVAL);

            proofWorksheet.runCallback(CallbackType.AFTER_RENUMBER);

            unifyProofWorksheet(proofWorksheet, noConvertWV, printOkMessages);
        }

        if (tlRequest != null && proofWorksheet.getGeneratedProofStmt() != null)
            try {
                tlRequest.doIt(theoremLoader, proofWorksheet, logicalSystem,
                    messages, this);
            } catch (final TheoremLoaderException e) {
                messages.accumErrorMessage(e.getMessage());
            }

        proofWorksheet.outputCursorInstrumentationIfEnabled();

        proofWorksheet.runCallback(CallbackType.AFTER_UNIFY);

        return proofWorksheet;

    }

    /**
     * Reformats a ProofWorksheet using TMFF.
     * <p>
     * Note: this method is called by ProofAsstGUI.java!
     * <p>
     * Reformatting is not attempted if the ProofWorksheet has structural errors
     * (returned from the parser).
     *
     * @param inputCursorStep set to true to reformat just the proof step
     *            underneath the cursor.
     * @param proofText proof text from ProofAsstGUI screen, or any String
     *            conforming to the formatting rules of ProofAsst.
     * @param inputCursorPos caret offset plus one of input or -1 if caret pos
     *            unavailable to caller.
     * @return ProofWorksheet reformatted, or not, if errors.
     */
    public ProofWorksheet tmffReformat(final boolean inputCursorStep,
        final String proofText, final int inputCursorPos)
    {

        final boolean[] errorFound = new boolean[1];

        final ProofWorksheet proofWorksheet = getParsedProofWorksheet(proofText,
            errorFound, inputCursorPos, null);

        if (errorFound[0] == false) {
            proofWorksheet.setProofCursor(proofWorksheet.proofInputCursor);
            proofWorksheet.tmffReformat(inputCursorStep);
        }

        proofWorksheet.outputCursorInstrumentationIfEnabled();

        return proofWorksheet;

    }

    /**
     * Import Theorem proofs from memory and unifies.
     * <p>
     * This is a simulation routine for testing purposes.
     *
     * @param messages Messages object for output messages.
     * @param selectorCount use to restrict the number of theorems present.
     * @param selectorTheorem just process one theorem, ignore selectorCount.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param asciiRetest instructs program to re-unify the output Proof
     *            Worksheet text after unification.
     */
    public void importFromMemoryAndUnify(final Messages messages,
        final int selectorCount, final Theorem selectorTheorem,
        final OutputBoss outputBoss, final boolean asciiRetest)
    {
        this.messages = messages;

        if (selectorTheorem != null)
            importFromMemoryAndUnifyOneTheorem(selectorTheorem, outputBoss,
                asciiRetest);
        else
            importFromMemoryAndUnifyManyTheorems(selectorCount, outputBoss,
                asciiRetest);
    }

    /**
     * Imports one theorem proof from memory and unifies.
     * <p>
     * This is a simulation routine for testing purposes.
     *
     * @param selectorTheorem process selected theorem.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param asciiRetest instructs program to re-unify the output Proof
     *            Worksheet text after unification.
     */
    public void importFromMemoryAndUnifyOneTheorem(
        final Theorem selectorTheorem, final OutputBoss outputBoss,
        final boolean asciiRetest)
    {
        assert selectorTheorem != null;

        final boolean unifiedFormat = proofAsstPreferences.exportFormatUnified
            .get();
        final HypsOrder hypsOrder = proofAsstPreferences.exportHypsOrder.get();
        final boolean deriveFormulas = proofAsstPreferences.exportDeriveFormulas
            .get();
        final boolean verifierRecheck = proofAsstPreferences.recheckProofAsstUsingProofVerifier
            .get();

        final String proofText = exportOneTheorem(null, // to memory not writer
            selectorTheorem, unifiedFormat, hypsOrder, deriveFormulas);
        if (proofText != null) {
            if (asciiRetest)
                proofAsstPreferences.recheckProofAsstUsingProofVerifier
                    .set(false);

            final StopWatch testStopWatch = new StopWatch(true);
            final ProofWorksheet proofWorksheet = unify(false, // no renum
                true, // don't convert work vars
                proofText, null, // no preprocess
                null, // no step request
                null, // no TL request
                -1, // inputCursorPos
                true); // printOkMessages
            testStopWatch.stop();

            if (asciiRetest)
                proofAsstPreferences.recheckProofAsstUsingProofVerifier
                    .set(verifierRecheck);

            final TheoremTestResult result = new TheoremTestResult(
                testStopWatch, proofWorksheet, selectorTheorem);

            volumeTestOutputRoutine(result, null, true);

            final String updatedProofText = proofWorksheet.getOutputProofText();

            // retest
            if (updatedProofText != null && asciiRetest)
                unify(false, // no renum
                    true, // don't convert work vars
                    updatedProofText, null, // no preprocess request
                    null, // no step request
                    null, // no TL request
                    -1, // inputCursorPos
                    true); // printOkMessages

            if (updatedProofText != null) {
                printProof(outputBoss, proofWorksheet, updatedProofText);
                checkAndCompareUpdateDJs(proofWorksheet);
            }
        }
    }

    /**
     * Import Theorem proofs from memory and unifies.
     * <p>
     * This is a simulation routine for testing purposes.
     *
     * @param selectorCount use to restrict the number of theorems present.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param asciiRetest instructs program to re-unify the output Proof
     *            Worksheet text after unification.
     */
    public void importFromMemoryAndUnifyManyTheorems(final int selectorCount,
        final OutputBoss outputBoss, final boolean asciiRetest)
    {
        final boolean unifiedFormat = proofAsstPreferences.exportFormatUnified
            .get();
        final HypsOrder hypsOrder = proofAsstPreferences.exportHypsOrder.get();
        final boolean deriveFormulas = proofAsstPreferences.exportDeriveFormulas
            .get();
        final boolean verifierRecheck = proofAsstPreferences.recheckProofAsstUsingProofVerifier
            .get();

        final VolumeTestStats stats = new VolumeTestStats();

        final List<Theorem> theoremList = getSortedTheoremList(0);

        final int numberToProcess = Math.min(selectorCount, theoremList.size());
        int numberProcessed = 0;

        final boolean smallTest = numberToProcess < PaConstants.PA_TESTMSG_THEOREM_NUMBER_THRESHOLD;

        final TheoremTestResult[] timeTop = smallTest ? null
            : new TheoremTestResult[PaConstants.PA_TESTMSG_THEOREM_TIME_TOP_NUMBER];

        final StopWatch wholeTestSuiteTime = new StopWatch(true);
        for (final Theorem theorem : theoremList) {
            if (numberProcessed >= numberToProcess
                || messages.maxErrorMessagesReached())
                break;

            // This whole function is needed for debug and regression tests.
            // The biggest test is set.mm which consumes a lot of time.
            // So, I think, it will be good to watch the progress dynamically.
            if (outputBoss != null)
                try {
                    outputBoss.printException(new ProofAsstException(
                        PaConstants.ERRMSG_PA_TESTMSG_PROGRESS,
                        numberProcessed + 1, numberToProcess,
                        theorem.getLabel()));
                } catch (final IOException e) {}

            stats.nbrTestTheoremsProcessed++;
            final String proofText = exportOneTheorem(null, theorem,
                unifiedFormat, hypsOrder, deriveFormulas);
            if (proofText != null) {
                if (asciiRetest)
                    proofAsstPreferences.recheckProofAsstUsingProofVerifier
                        .set(false);
                // for Volume Testing

                final StopWatch testStopWatch = new StopWatch(true);
                final ProofWorksheet proofWorksheet = unify(false, // no renum
                    true, // don't convert work vars
                    proofText, null, // no preprocess
                    null, // no step request
                    null, // no TL request
                    -1, // inputCursorPos
                    smallTest); // printOkMessages
                testStopWatch.stop();

                if (asciiRetest)
                    proofAsstPreferences.recheckProofAsstUsingProofVerifier
                        .set(verifierRecheck);

                final TheoremTestResult result = new TheoremTestResult(
                    testStopWatch, proofWorksheet, theorem);

                addResultToVolumeTestTimeTop(timeTop, result);

                volumeTestOutputRoutine(result, stats, smallTest);
                final String updatedProofText = proofWorksheet
                    .getOutputProofText();

                // retest
                if (updatedProofText != null && asciiRetest)
                    unify(false, // no renum
                        true, // don't convert work vars
                        updatedProofText, null, // no preprocess request
                        null, // no step request
                        null, // no TL request
                        -1, // inputCursorPos
                        smallTest); // printOkMessages

                if (updatedProofText != null) {
                    printProof(outputBoss, proofWorksheet, updatedProofText);
                    checkAndCompareUpdateDJs(proofWorksheet);
                }
            }
            numberProcessed++;
        }
        System.err.println(); // for debug reasons
        wholeTestSuiteTime.stop();

        printVolumeTestStats(stats, wholeTestSuiteTime, timeTop);
    }

    /**
     * Perform the optimizations for theorem search during "parallel"
     * unification
     */
    public void optimizeTheoremSearch() {
        final List<Theorem> theoremList = getSortedTheoremList(0);

        final Map<Cnst, Integer> frequency = new HashMap<>();

        final Set<Formula> formulaList = new LinkedHashSet<>();

        for (final Theorem theorem : theoremList) {
            formulaList.add(theorem.getFormula());
            for (final LogHyp logHyp : theorem.getLogHypArray())
                formulaList.add(logHyp.getFormula());
        }

        for (final Formula formula : formulaList)
            formula.collectConstFrequenceAndInitConstList(frequency);

        final Comparator<Cnst> comp = new Comparator<Cnst>() {
            public int compare(final Cnst o1, final Cnst o2) {
                final Integer i1 = frequency.get(o1);
                final Integer i2 = frequency.get(o2);

                if (i1 == i2)
                    return o1.getSeq() - o2.getSeq();

                if (i1 == null)
                    return 1;
                if (i2 == null)
                    return -1;

                if (i1.intValue() != i2.intValue())
                    return i1 - i2;
                else
                    return o1.getSeq() - o2.getSeq();
            }
        };

        for (final Formula formula : formulaList)
            formula.sortConstList(comp);
    }

    /**
     * This function initialize auto-transformation component.
     *
     * @param enabled Set to false to de-initialize an already loaded
     *            transformation manager
     * @param debugOutput when it is true auto-transformation component will
     *            produce a lot of debug output
     * @param supportPrefix when it is true auto-transformation component will
     *            try to use implication prefix in transformations
     */
    public void initAutotransformations(final boolean enabled,
        final boolean debugOutput, final boolean supportPrefix)
    {
        final TransformationManager trManager = enabled
            ? new TransformationManager(this, getSortedAssrtSearchList(),
                getProvableLogicStmtTyp(), messages, verifyProofs,
                supportPrefix, debugOutput)
            : null;
        proofUnifier.setTransformationManager(trManager);
    }

    /**
     * Import Theorem proofs from a given Reader.
     *
     * @param importReader source of proofs
     * @param messages Messages object for output messages.
     * @param numberToProcess use to restrict the number of theorems present.
     * @param selectorTheorem just process one theorem, ignore numberToProcess.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param asciiRetest instructs program to re-unify the output Proof
     *            Worksheet text after unification.
     */
    public void importFromFileAndUnify(final Reader importReader, // already
                                                                  // open
        final Messages messages, final int numberToProcess,
        final Theorem selectorTheorem, final OutputBoss outputBoss,
        final boolean asciiRetest)
    {

        this.messages = messages;

        final boolean verifierRecheck = proofAsstPreferences.recheckProofAsstUsingProofVerifier
            .get();

        ProofWorksheet proofWorksheet = null;
        String updatedProofText;

        String theoremLabel;

        int numberProcessed = 0;

        try (
            ProofWorksheetParser proofWorksheetParser = new ProofWorksheetParser(
                importReader, PaConstants.PROOF_TEXT_READER_CAPTION,
                proofAsstPreferences, logicalSystem, grammar, messages,
                macroManager))
        {

            while (true) {

                proofWorksheet = proofWorksheetParser.next();

                theoremLabel = proofWorksheet.getTheoremLabel();

                if (proofWorksheet.hasStructuralErrors()) {
                    messages.accumException(addLabelContext(proofWorksheet,
                        PaConstants.ERRMSG_PA_IMPORT_STRUCT_ERROR));
                    break;
                }

                if (messages.maxErrorMessagesReached())
                    break;

                if (selectorTheorem != null)
                    if (selectorTheorem.getLabel().equals(theoremLabel)) {

                        if (asciiRetest)
                            proofAsstPreferences.recheckProofAsstUsingProofVerifier
                                .set(false);

                        unifyProofWorksheet(proofWorksheet, false, true);

                        if (asciiRetest)
                            proofAsstPreferences.recheckProofAsstUsingProofVerifier
                                .set(verifierRecheck);

                        updatedProofText = proofWorksheet.getOutputProofText();

                        // retest
                        if (updatedProofText != null && asciiRetest)
                            unify(false, // no renum
                                false, // convert work vars
                                updatedProofText, null, // no preprocess request
                                null, // no step request
                                null, // no TL request
                                -1, // inputCursorPos
                                true); // printOkMessages

                        if (updatedProofText != null) {
                            printProof(outputBoss, proofWorksheet,
                                updatedProofText);
                            checkAndCompareUpdateDJs(proofWorksheet);
                        }
                        break;
                    }
                    else {
                        if (!proofWorksheetParser.hasNext())
                            break;
                        continue;
                    }

                if (numberProcessed >= numberToProcess)
                    break;

                if (asciiRetest)
                    proofAsstPreferences.recheckProofAsstUsingProofVerifier
                        .set(false);

                unifyProofWorksheet(proofWorksheet, false, true);

                if (asciiRetest)
                    proofAsstPreferences.recheckProofAsstUsingProofVerifier
                        .set(verifierRecheck);

                updatedProofText = proofWorksheet.getOutputProofText();

                // retest
                if (updatedProofText != null && asciiRetest)
                    unify(false, // no renum
                        false, // convert work vars
                        updatedProofText, null, // no preprocess request
                        null, // no step request
                        null, // no TL request
                        -1, // inputCursorPos
                        true); // printOkMessages

                if (updatedProofText != null) {
                    printProof(outputBoss, proofWorksheet, updatedProofText);
                    checkAndCompareUpdateDJs(proofWorksheet);
                }
                numberProcessed++;
                if (!proofWorksheetParser.hasNext())
                    break;
            }
        }

        catch (final ProofAsstException e) {
            messages.accumException(
                addLabelContext(proofWorksheet, new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_IMPORT_ERROR, e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, e);
        } catch (final MMIOException e) {
            messages.accumException(
                addLabelContext(proofWorksheet, new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_IMPORT_IO_ERROR, e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, e);
        } catch (final Exception e) {
            e.printStackTrace();
            messages.accumException(addLabelContext(proofWorksheet,
                new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_IMPORT_SEVERE_ERROR,
                    e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, null);
        }
    }

    /**
     * Exercises the PreprocessRequest code for one proof.
     *
     * @param proofText one Proof Text Area in a String.
     * @param messages Messages object for output messages.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param preprocessRequest to apply before unification
     */
    public void preprocessRequestBatchTest(final String proofText,
        final Messages messages, final OutputBoss outputBoss,
        final PreprocessRequest preprocessRequest)
    {

        this.messages = messages;

        String updatedProofText = null;

        if (proofText != null)
            printProof(outputBoss, (String)null, proofText);

        final ProofWorksheet proofWorksheet = unify(false, // no renum
            false, // convert work vars
            proofText, preprocessRequest, null, // no step request
            null, // no TL request
            -1, // inputCursorPos
            true); // printOkMessages

        updatedProofText = proofWorksheet.getOutputProofText();

        if (updatedProofText != null)
            printProof(outputBoss, proofWorksheet, updatedProofText);

    }

    /**
     * Exercises the StepSelectorSearch for one proof.
     *
     * @param importReader source of proofs
     * @param messages Messages object for output messages.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     * @param cursorPos offset of input cursor.
     * @param selectionNumber choice from StepSelectorResults
     */
    public void stepSelectorBatchTest(final Reader importReader, // already open
        final Messages messages, final OutputBoss outputBoss,
        final int cursorPos, final int selectionNumber)
    {

        this.messages = messages;

        ProofWorksheet proofWorksheet = null;

        String origProofText = null;
        String updatedProofText = null;
        try (
            ProofWorksheetParser proofWorksheetParser = new ProofWorksheetParser(
                importReader, PaConstants.PROOF_TEXT_READER_CAPTION,
                proofAsstPreferences, logicalSystem, grammar, messages,
                macroManager))
        {

            proofWorksheet = proofWorksheetParser.next(cursorPos + 1,
                new StepRequest(StepRequestType.SelectorSearch));

            final String theoremLabel = proofWorksheet.getTheoremLabel();

            if (!proofWorksheet.hasStructuralErrors()) {
                origProofText = proofWorksheet.getOutputProofText();
                unifyProofWorksheet(proofWorksheet, false, true);
            }
            if (proofWorksheet.hasStructuralErrors()) {
                messages.accumException(addLabelContext(proofWorksheet,
                    PaConstants.ERRMSG_PA_IMPORT_STRUCT_ERROR));
                return;
            }
            if (proofWorksheet.stepSelectorResults == null) {
                messages.accumException(addLabelContext(proofWorksheet,
                    PaConstants.ERRMSG_STEP_SELECTOR_BATCH_TEST_NO_RESULTS));
                return;
            }

            final StepSelectorResults results = proofWorksheet.stepSelectorResults;

            printStepSelectorResults(outputBoss, theoremLabel, results);

            if (selectionNumber < 0
                || selectionNumber >= results.selectionArray.length)
            {
                messages.accumException(addLabelContext(proofWorksheet,
                    PaConstants.ERRMSG_STEP_SELECTOR_BATCH_TEST_INV_CHOICE,
                    selectionNumber, results.selectionArray.length - 1));
                return;
            }

            final String step = results.step;
            final Assrt assrt = results.refArray[selectionNumber];
            final String selection = results.selectionArray[selectionNumber];
            messages.accumException(addLabelContext(proofWorksheet,
                PaConstants.ERRMSG_STEP_SELECTOR_BATCH_TEST_CHOICE, step,
                selectionNumber, assrt.getLabel(), selection));

            final StepRequest stepRequestChoice = new StepRequest(
                StepRequestType.SelectorChoice, results.step,
                results.refArray[selectionNumber]);

            proofWorksheet = unify(false, // no renumReq,
                false, // convert work vars
                origProofText, null, // no preprocessRequest,
                stepRequestChoice, null, // no TL request
                cursorPos + 1, true); // printOkMessages

            updatedProofText = proofWorksheet.getOutputProofText();
            if (updatedProofText != null)
                printProof(outputBoss, proofWorksheet, updatedProofText);
        } catch (final ProofAsstException e) {
            messages.accumException(
                addLabelContext(proofWorksheet, new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_IMPORT_ERROR, e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, e);
        } catch (final MMIOException e) {
            messages.accumException(
                addLabelContext(proofWorksheet, new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_IMPORT_IO_ERROR, e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, e);
        } catch (final Exception e) {
            e.printStackTrace();
            messages.accumException(addLabelContext(proofWorksheet,
                new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_IMPORT_SEVERE_ERROR,
                    e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, null);
        }
    }

    /**
     * Export Theorem proofs to a given Writer.
     * <p>
     * Uses ProofAsstPreferences.getExportFormatUnified() to determine whether
     * output proof derivation steps contain Ref statement labels (if "unified"
     * then yes, add labels.)
     * <p>
     * An incomplete input proof generates an incomplete output proof as well as
     * an error message.
     *
     * @param exportWriter destination for output proofs.
     * @param messages Messages object for output messages.
     * @param numberToExport use to restrict the number of theorems present.
     * @param selectorTheorem just process one theorem, ignore numberToExport.
     * @param outputBoss mmj.util.OutputBoss object, if not null means, please
     *            print the proof test.
     */
    public void exportToFile(final Writer exportWriter, // already open
        final Messages messages, final int numberToExport,
        final Theorem selectorTheorem, final OutputBoss outputBoss)
    {

        final boolean exportFormatUnified = proofAsstPreferences.exportFormatUnified
            .get();

        final HypsOrder hypsOrder = proofAsstPreferences.exportHypsOrder.get();

        final boolean deriveFormulas = proofAsstPreferences.exportDeriveFormulas
            .get();

        this.messages = messages;

        if (selectorTheorem != null) {
            final String proofText = exportOneTheorem(exportWriter,
                selectorTheorem, exportFormatUnified, hypsOrder,
                deriveFormulas);
            if (proofText != null)
                printProof(outputBoss, selectorTheorem.getLabel(), proofText);
            return;
        }

        int numberExported = 0;
        for (final Theorem theorem : getSortedTheoremList(0)) {
            if (numberExported >= numberToExport)
                break;
            final String proofText = exportOneTheorem(exportWriter, theorem,
                exportFormatUnified, hypsOrder, deriveFormulas);
            if (proofText != null)
                printProof(outputBoss, theorem.getLabel(), proofText);
            numberExported++;
        }
    }

    // Note: could do binary lookup for sequence number
    // within ArrayList which happens to be sorted.
    private Theorem getTheoremBackward(final int currProofMaxSeq,
        final boolean isRetry)
    {
        final List<Assrt> searchList = proofUnifier
            .getUnifySearchListByMObjSeq();

        for (final ListIterator<Assrt> li = searchList
            .listIterator(searchList.size()); li.hasPrevious();)
        {
            final Assrt assrt = li.previous();
            if (assrt.getSeq() < currProofMaxSeq && assrt instanceof Theorem)
                return (Theorem)assrt;
        }
        if (isRetry)
            return null;
        return getTheoremBackward(Integer.MAX_VALUE, true);
    }

    // Note: could do binary lookup for sequence number
    // within ArrayList which happens to be sorted.
    private Theorem getTheoremForward(final int currProofMaxSeq,
        final boolean isRetry)
    {

        if (currProofMaxSeq != Integer.MAX_VALUE) {

            final List<Assrt> searchList = proofUnifier
                .getUnifySearchListByMObjSeq();

            for (final Assrt assrt : searchList)
                if (assrt.getSeq() > currProofMaxSeq
                    && assrt instanceof Theorem)
                    return (Theorem)assrt;
        }
        if (isRetry)
            return null;

        return getTheoremForward(Integer.MIN_VALUE, true);
    }

    /**
     * Parses a Proof Worksheet text area and returns a Proof Worksheet plus an
     * error flag.
     * <p>
     * Note that the ProofWorksheetParser invokes the logic to perform parsing
     * and "structural" edits so that other logic such as ProofUnifier have a
     * clean ProofWorksheet. A "structural error" means an error like a formula
     * with a syntax error, or a Hyp referring to a non-existent step, etc.
     *
     * @param proofText proof text from ProofAsstGUI screen, or any String
     *            conforming to the formatting rules of ProofAsst.
     * @param errorFound boolean array of 1 element that is output as true if an
     *            error was found.
     * @param inputCursorPos caret offset plus one of input or -1 if caret pos
     *            unavailable to caller.
     * @param stepRequest may be null, or StepSelector Search or Choice request
     *            and will be loaded into the ProofWorksheet.
     * @return ProofWorksheet unified.
     */
    private ProofWorksheet getParsedProofWorksheet(final String proofText,
        final boolean[] errorFound, final int inputCursorPos,
        final StepRequest stepRequest)
    {

        ProofWorksheet proofWorksheet = null;
        errorFound[0] = true;
        try (
            ProofWorksheetParser proofWorksheetParser = new ProofWorksheetParser(
                proofText, "Proof Text", proofAsstPreferences, logicalSystem,
                grammar, messages, macroManager))
        {

            proofWorksheet = proofWorksheetParser.next(inputCursorPos,
                stepRequest);

            errorFound[0] = false;

        } catch (final ProofAsstException e) {
            messages.accumException(addLabelContext(proofWorksheet, e));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, e);
        } catch (final MMIOException e) {
            messages.accumException(addLabelContext(proofWorksheet, e));
        } catch (final IOException e) {
            e.printStackTrace();
            messages.accumException(
                addLabelContext(proofWorksheet, new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_UNIFY_SEVERE_ERROR, e.getMessage())));
            proofWorksheet = updateWorksheetWithException(proofWorksheet, null);
        } finally {
            if (macroManager != null)
                macroManager.runCallback(CallbackType.PARSE_FAILED);
        }
        return proofWorksheet;
    }

    /**
     * This function compares {@code candidate} time information with the
     * content of {@code timeTop} array and updates {@code timeTop} if it is
     * needed.
     *
     * @param timeTop The array with already collected information about longest
     *            theorem unifications.
     * @param candidate The information about some theorem test.
     */
    private void addResultToVolumeTestTimeTop(final TheoremTestResult[] timeTop,
        final TheoremTestResult candidate)
    {
        if (timeTop == null)
            return;

        // simple comparator: could compare null objects also!
        final Comparator<TheoremTestResult> comp = new Comparator<TheoremTestResult>() {
            public int compare(final TheoremTestResult left,
                final TheoremTestResult right)
            {
                if (left == right)
                    return 0;

                if (left == null)
                    return 1;

                if (right == null)
                    return -1;

                return -StopWatch.compare(left.stopWatch, right.stopWatch);
            }
        };

        // find insert position
        int pos = Arrays.binarySearch(timeTop, candidate, comp);
        if (pos < 0)
            pos = -(pos + 1);

        if (pos < timeTop.length) {
            // we need to add this candidate to timeTop array

            // shift array
            System.arraycopy(timeTop, pos, timeTop, pos + 1,
                timeTop.length - pos - 1);

            // add alement
            timeTop[pos] = candidate;
        }
    }

    private void printVolumeTestStats(final VolumeTestStats stats,
        final StopWatch wholeTestTime, final TheoremTestResult[] timeTop)
    {
        messages.accumMessage(PaConstants.ERRMSG_PA_TESTMSG_03,
            stats.nbrTestTheoremsProcessed, stats.nbrTestNotProvedPerfectly,
            stats.nbrTestProvedDifferently,
            wholeTestTime.getElapsedTimeInStr());

        if (timeTop != null) {
            messages.accumMessage(PaConstants.ERRMSG_PA_TIME_TOP_HEADER);

            for (final TheoremTestResult result : timeTop)
                volumeTestOutputRoutine(result, null, true);
        }
    }

    private void volumeTestOutputRoutine(final TheoremTestResult result,
        final VolumeTestStats stats, final boolean printOkTheorems)
    {

        final DerivationStep q = result.proofWorksheet.getQedStep();

        if (q == null) {
            if (stats != null)
                stats.nbrTestNotProvedPerfectly++;
            messages.accumMessage(PaConstants.ERRMSG_PA_TESTMSG_01,
                result.theorem.getLabel(),
                result.stopWatch.getElapsedTimeInStr(), 9999999,
                "No qed step found!");
            return;
        }

        // this 's' is for batch testing. the numbers
        // are for backward compatibility to the old
        // ProofWorkStmt.status values.
        final int PROVED_PERFECTLY = 8;
        int s;
        if (q.getProofTree() == null)
            s = 4; // arbitrary
        else if (q.djVarsErrorStatus == DjVarsErrorStatus.None) {
            if (!q.verifyProofError)
                s = PROVED_PERFECTLY; // proved perfectly
            else {
                s = 10; // verify proof err
                if (stats != null)
                    stats.nbrTestNotProvedPerfectly++;
            }
        }
        else {
            s = 9; // dj vars error
            if (stats != null)
                stats.nbrTestNotProvedPerfectly++;
        }

        if (printOkTheorems || s != PROVED_PERFECTLY)
            messages.accumMessage(PaConstants.ERRMSG_PA_TESTMSG_01,
                result.theorem.getLabel(),
                result.stopWatch.getElapsedTimeInStr(), s,
                PaConstants.STATUS_DESC[s]);

        if (q != null) {
            RPNStep[] newProof;
            if (q.getProofTree() == null)
                newProof = new RPNStep[0];
            else
                newProof = q.getProofTree().convertToRPNExpanded();
            final RPNStep[] oldProof = new ParseTree(
                result.proofWorksheet.getTheorem().getProof())
                    .convertToRPNExpanded();

            boolean differenceFound = false;
            String oldStmtDiff = "";
            String newStmtDiff = "";

            if (oldProof == null) {
                if (newProof.length == 0) {
                    // equal
                }
                else {
                    differenceFound = true;
                    newStmtDiff = newProof[0].stmt.getLabel();
                }
            }
            else {
                int i = 0;
                while (true) {
                    if (i < oldProof.length && i < newProof.length) {
                        if (newProof[i].stmt == oldProof[i].stmt) {
                            i++;
                            continue;
                        }
                        oldStmtDiff = oldProof[i].stmt.getLabel();
                        newStmtDiff = newProof[i].stmt.getLabel();
                    }
                    else if (i < oldProof.length)
                        oldStmtDiff = oldProof[i].stmt.getLabel();
                    else if (i < newProof.length)
                        newStmtDiff = newProof[i].stmt.getLabel();
                    else
                        break; // no differences
                    differenceFound = true;
                    break;
                }
            }

            if (differenceFound && printOkTheorems) {
                if (stats != null)
                    stats.nbrTestProvedDifferently++;
                messages.accumMessage(PaConstants.ERRMSG_PA_TESTMSG_02,
                    result.theorem.getLabel(), oldStmtDiff, newStmtDiff);
            }

        }

        // end Volume Testing code.
    }

    private void printStepSelectorResults(final OutputBoss outputBoss,
        final String theoremLabel, final StepSelectorResults results)
    {

        if (outputBoss == null || results == null
            || !PaConstants.ERRMSG_STEP_SELECTOR_RESULTS_PRINT.enabled())
            return;

        try {
            outputBoss.printException(new ProofAsstException(
                PaConstants.ERRMSG_STEP_SELECTOR_RESULTS_PRINT, theoremLabel,
                results.step));
            String label;
            for (int i = 0; i < results.refArray.length; i++) {
                if (results.refArray[i] == null)
                    label = " ";
                else
                    label = results.refArray[i].getLabel();
                outputBoss.sysOutPrint(" " + i + " " + label + " "
                    + results.selectionArray[i] + "\n");
            }
        } catch (final IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(new ProofAsstException(
                PaConstants.ERRMSG_PA_PRINT_IO_ERROR, e.getMessage())
                    .addContext(new TheoremContext(theoremLabel)));
        }

    }

    public String exportOneTheorem(final String theoremLabel) {

        final Theorem theorem = getTheorem(theoremLabel);
        if (theorem == null)
            throw new IllegalArgumentException(new ProofAsstException(
                PaConstants.ERRMSG_PA_GET_THEOREM_NOT_FOUND, theoremLabel));
        return exportOneTheorem(theorem);
    }

    public String exportOneTheorem(final Theorem theorem) {
        return exportOneTheorem(null, // no export writer
            theorem, true, // exportFormatUnified
            HypsOrder.Correct, // hypsRandomized
            false); // deriveFormulas
    }

    private String exportOneTheorem(final Writer exportWriter, // already open
        final Theorem theorem, final boolean exportFormatUnified,
        final HypsOrder hypsOrder, final boolean deriveFormulas)
    {

        final ProofWorksheet proofWorksheet = getExportedProofWorksheet(theorem,
            exportFormatUnified, hypsOrder, deriveFormulas);

        if (proofWorksheet == null)
            return null;

        final String proofText = proofWorksheet.getOutputProofText();

        if (proofText == null)
            throw new IllegalArgumentException(addLabelContext(proofWorksheet,
                PaConstants.ERRMSG_PA_EXPORT_STRUCT_ERROR));

        if (exportWriter != null)
            try {
                exportWriter.write(proofText);
                exportWriter.write('\n');
            } catch (final IOException e) {
                throw new IllegalArgumentException(
                    addLabelContext(proofWorksheet,
                        new ProofAsstException(e,
                            PaConstants.ERRMSG_PA_EXPORT_IO_ERROR,
                            e.getMessage())));
            }
        return proofText;
    }

    private ProofWorksheet getExportedProofWorksheet(final Theorem theorem,
        final boolean exportFormatUnified, final HypsOrder hypsOrder,
        final boolean deriveFormulas)
    {

        ProofWorksheet proofWorksheet = null;
        List<ProofDerivationStepEntry> proofDerivationStepList;

        try {
            proofDerivationStepList = verifyProofs.getProofDerivationSteps(
                theorem, exportFormatUnified, hypsOrder,
                getProvableLogicStmtTyp());

            proofWorksheet = new ProofWorksheet(theorem,
                proofDerivationStepList, deriveFormulas, proofAsstPreferences,
                logicalSystem, grammar, messages);
        } catch (final VerifyException e) {
            messages.accumException(TheoremContext
                .addTheoremContext(theorem.getLabel(), new ProofAsstException(e,
                    PaConstants.ERRMSG_PA_EXPORT_PV_ERROR, e.getMessage())));
        }

        return proofWorksheet;
    }

    private void unifyProofWorksheet(final ProofWorksheet proofWorksheet,
        final boolean noConvertWV, final boolean printOkMessages)
    {

        if (proofWorksheet.getNbrDerivStepsReadyForUnify() > 0
            || proofWorksheet.stepRequest != null
                && (proofWorksheet.stepRequest.type.simple
                || proofWorksheet.stepRequest.type == StepRequestType.GeneralSearch))
        {

            try {
                proofUnifier.unifyAllProofDerivationSteps(proofWorksheet,
                    messages, noConvertWV);
            } catch (final VerifyException e) {
                // this is a particularly severe situation
                // caused by a shortage of allocatable
                // work variables -- the user will need
                // to restart mmj2 after updating RunParm.txt
                // with more Work Variables (assuming there
                // is no bug...)
                messages.accumException(e);
                proofWorksheet.setStructuralErrors(true);
                return;
            }

            final ProofFormat format = proofAsstPreferences.proofFormat.get();
            final RPNStep[] rpnProof = format == ProofFormat.Normal
                ? proofWorksheet.getQedStepProofRPN()
                : proofWorksheet.getQedStepSquishedRPN();

            if (rpnProof == null)
                proofUnifier.reportUnificationFailures();
            else {
                if (format == ProofFormat.Compressed) {
                    final StringBuilder letters = new StringBuilder();

                    final List<Hyp> mandHypList = new ArrayList<>();
                    final List<VarHyp> optHypList = new ArrayList<>();
                    ProofUnifier.separateMandAndOptFrame(proofWorksheet,
                        proofWorksheet.getQedStep(), mandHypList, optHypList,
                        true);

                    final int width = proofWorksheet.proofAsstPreferences.rpnProofRightCol
                        .get() - proofWorksheet.getRPNProofLeftCol() + 1;
                    final List<Stmt> parenList = logicalSystem
                        .getProofCompression()
                        .compress(proofWorksheet.getTheoremLabel(), width,
                            mandHypList, optHypList, rpnProof, letters);

                    proofWorksheet.addGeneratedProofStmt(parenList,
                        letters.toString());
                }
                else
                    proofWorksheet.addGeneratedProofStmt(rpnProof);
                if (proofAsstPreferences.djVarsSoftErrors.get().generate
                    && proofWorksheet.proofSoftDjVarsErrorList != null
                    && !proofWorksheet.proofSoftDjVarsErrorList.isEmpty())
                    proofWorksheet.generateAndAddDjVarsStmts();

                if (printOkMessages)
                    messages.accumException(addLabelContext(proofWorksheet,
                        PaConstants.ERRMSG_PA_RPN_PROOF_GENERATED));
            }
        }
        else
            messages.accumException(addLabelContext(proofWorksheet,
                PaConstants.ERRMSG_PA_NOTHING_TO_UNIFY));
        proofWorksheet.incompleteStepCursorPositioning();
    }

    private ProofWorksheet updateWorksheetWithException(final ProofWorksheet w,
        final MMJException e)
    {
        ProofWorksheet out;
        final LineColumnContext c = e == null ? null
            : e.getContext(LineColumnContext.class);

        final ProofAsstCursor proofCursor = c == null ? new ProofAsstCursor()
            : new ProofAsstCursor((int)c.charNbr, (int)c.lineNbr,
                (int)c.columnNbr);
        if (w == null)
            out = new ProofWorksheet(proofAsstPreferences, messages, true, // structural
                                                                           // err!
                proofCursor);
        else {
            out = w;
            if (!w.proofCursor.cursorIsSet)
                out.setProofCursor(proofCursor);
        }

        return out;
    }

    private void printProof(final OutputBoss outputBoss, final ProofWorksheet w,
        final String proofText)
    {
        printProof(outputBoss, w == null ? null : w.getTheoremLabel(),
            proofText);
    }

    private void printProof(final OutputBoss outputBoss, final String label,
        final String proofText)
    {
        if (outputBoss == null || proofText == null)
            return;

        try {
            outputBoss.sysOutPrint(proofText);
            outputBoss.sysOutPrint("\n");
        } catch (final IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                new ProofAsstException(e, PaConstants.ERRMSG_PA_PRINT_IO_ERROR,
                    e.getMessage()).addContext(
                        label == null ? null : new LabelContext(label)));
        }
    }

    public Iterable<Theorem> getSortedSkipSeqTheoremIterable(
        final String startTheoremLabel) throws ProofAsstException
    {
        int lowestMObjSeq = 0;
        if (startTheoremLabel != null) {
            final Theorem theorem = getTheorem(startTheoremLabel);
            if (theorem == null)
                throw new ProofAsstException(
                    PaConstants.ERRMSG_PA_START_THEOREM_NOT_FOUND,
                    startTheoremLabel);
            lowestMObjSeq = theorem.getSeq();
        }

        return getSortedTheoremList(lowestMObjSeq);
    }

    private List<Theorem> getSortedTheoremList(final int lowestMObjSeq) {

        final ArrayList<Theorem> sortedTheoremList = new ArrayList<>(
            logicalSystem.getStmtTbl().size());

        for (final Stmt stmt : logicalSystem.getStmtTbl().values())
            if (stmt.getSeq() >= lowestMObjSeq && stmt instanceof Theorem
                && stmt.getFormula().getTyp() == getProvableLogicStmtTyp() &&
                // don't process these during batch testing.
                // for one thing, "dummylink" generates an
                // exception because its proof is invalid
                // for the mmj2 Proof Assistant.
                !((Assrt)stmt).isExcluded())
                sortedTheoremList.add((Theorem)stmt);

        Collections.sort(sortedTheoremList, MObj.SEQ);

        return sortedTheoremList;
    }

    private Cnst getProvableLogicStmtTyp() {
        return grammar.getProvableLogicStmtTypArray()[0];
    }

    private void checkAndCompareUpdateDJs(final ProofWorksheet proofWorksheet) {
        if (proofWorksheet.getQedStepProofRPN() == null
            || proofWorksheet.newTheorem
            || !proofAsstPreferences.djVarsSoftErrors.get().generate
            || proofWorksheet.proofSoftDjVarsErrorList == null
            || proofWorksheet.proofSoftDjVarsErrorList.isEmpty())
            return;

        if (proofAsstPreferences.importCompareDJs.get())
            importCompareDJs(proofWorksheet);

        if (proofAsstPreferences.importUpdateDJs.get())
            importUpdateDJs(proofWorksheet);

    }

    /**
     * OK, at this point the proof is complete and the Proof Worksheet's
     * "comboFrame" has been updated with newly computed DjVars (if
     * GenerateDifferences was used, the differences have been merged with the
     * original, so in every case, comboFrame has a complete set of DjVars.)
     * <p>
     * In addition, this code is only executed *if* DjVars were generated during
     * processing of the ProofWorksheet (if GenerateDifferences or
     * GenerateReplacements were used then there must have been at least one
     * "soft" DjVars error.)
     * <p>
     * In theory we could report Superfluous and Omitted DjVars for the
     * theorem's Mandatory and Optional Frames. However, according to Norm, the
     * interesting thing to learn about is Superfluous Mandatory DjVars
     * restrictions in the theorem.
     * <p>
     * This code does not need to be efficient, as it is used only for testing
     * purposes, so finding Superfluous Mandatory DjVars just means looking up
     * each mandFrame.djVarsArray[i] in the ProofWorksheet's
     * comboFrame.djVarsArray (which happens to be sorted); if not found, then
     * the mandFrame.djVarsArray[i] element is Superflous...
     *
     * @param proofWorksheet the owner ProofWorksheet
     */
    private void importCompareDJs(final ProofWorksheet proofWorksheet) {
        final List<DjVars> superfluous = new ArrayList<>();
        final ScopeFrame mandFrame = proofWorksheet.theorem.getMandFrame();
        int compare;
        loopI: for (int i = 0; i < mandFrame.djVarsArray.length; i++) {
            for (int j = 0; j < proofWorksheet.comboFrame.djVarsArray.length; j++) {
                compare = mandFrame.djVarsArray[i]
                    .compareTo(proofWorksheet.comboFrame.djVarsArray[j]);
                if (compare > 0)
                    continue;
                if (compare == 0)
                    continue loopI;
                superfluous.add(mandFrame.djVarsArray[i]);
                continue loopI; // not found
            }

            superfluous.add(mandFrame.djVarsArray[i]);
            continue;
        }

        if (!superfluous.isEmpty())
            messages.accumException(addLabelContext(proofWorksheet,
                PaConstants.ERRMSG_SUPERFLUOUS_MANDFRAME_DJVARS, superfluous));
    }

    private void importUpdateDJs(final ProofWorksheet proofWorksheet) {
        final List<DjVars> list = new ArrayList<>();

        final ScopeFrame mandFrame = proofWorksheet.theorem.getMandFrame();

        for (final DjVars element : proofWorksheet.comboFrame.djVarsArray)
            if (mandFrame.areBothDjVarsInHypArray(element))
                list.add(element);
        final DjVars[] djArray = new DjVars[list.size()];
        for (int i = 0; i < djArray.length; i++)
            djArray[i] = list.get(i);
        mandFrame.djVarsArray = djArray;
    }
}
