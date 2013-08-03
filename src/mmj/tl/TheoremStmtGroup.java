//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TheoremStmtGroup.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     --> new.
 */

package mmj.tl;

import java.io.IOException;
import java.util.*;

import mmj.lang.*;
import mmj.mmio.*;
import mmj.pa.PaConstants;

/**
 * TheoremStmtGroup represents the contents of a MMTTheoremFile as well as state
 * variables pertaining to work performed loading the theorem into the
 * LogicalSystem.
 * <p>
 * Note: TheoremStmtGroup refers to the fact that a MMTTheoremFile consists of a
 * group of Metamath .mm statements.
 */
public class TheoremStmtGroup {

    private final MMTTheoremFile mmtTheoremFile;

    private final String theoremLabel;

    /* initially loaded data from input mmtTheoremFile */
    private SrcStmt beginScopeSrcStmt;
    private final List<SrcStmt> dvSrcStmtList;
    private final List<SrcStmt> logHypSrcStmtList;
    private SrcStmt theoremSrcStmt;
    private SrcStmt endScopeSrcStmt;

    /* derived or computed items follow: */

    private MObj<?> maxExistingMObjRef;

    private int insertSectionNbr; // for BookManager.

    private boolean isTheoremNew;

    private final LogHyp[] logHypArray;
    private Theorem theorem;

    /* store previous values for backout */
    private Stmt[] oldProof;
    private DjVars[] oldDjVarsArray;
    private DjVars[] oldOptDjVarsArray;

    /* derived based on relational edit/analysis
       of the MMTTheoremSet */
    private boolean isProofIncomplete;

    private boolean mustAppend;

    private final List<TheoremStmtGroup> theoremStmtGroupUsedList;
    private final List<TheoremStmtGroup> usedByTheoremStmtGroupList;

    /* derived based on updating access of LogicalSystem */
    private boolean wasTheoremUpdated;

    private final boolean[] wasLogHypInserted;
    private boolean wasTheoremInserted;

    private final boolean[] wasLogHypAppended;
    private boolean wasTheoremAppended;

    private final int[] assignedLogHypSeq;
    private int assignedTheoremSeq;

    /**
     * Constructor for TheoremStmtGroup.
     * <p>
     * The constructor loads the input file into the TheoremStmtGroup and
     * performs data validation of the input data.
     * 
     * @param mmtTheoremFile MMTTheoremFile to be read.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param tlPreferences TlPreferences object.
     * @throws TheoremLoaderException is thrown if there are data errors in the
     *             input MMTTheoremFile.
     */
    public TheoremStmtGroup(final MMTTheoremFile mmtTheoremFile,
        final LogicalSystem logicalSystem, final Messages messages,
        final TlPreferences tlPreferences) throws TheoremLoaderException
    {

        this.mmtTheoremFile = mmtTheoremFile;

        dvSrcStmtList = new ArrayList<SrcStmt>(
            TlConstants.DEFAULT_DV_SRC_STMT_LIST_SIZE);

        logHypSrcStmtList = new ArrayList<SrcStmt>(
            TlConstants.DEFAULT_LOG_HYP_SRC_STMT_LIST_SIZE);

        final Statementizer statementizer = mmtTheoremFile
            .constructStatementizer();

        loadParsedInputIntoStmtGroup(statementizer);

        theoremLabel = theoremSrcStmt.label;

        final int n = logHypSrcStmtList.size();
        logHypArray = new LogHyp[n];
        wasLogHypInserted = new boolean[n];
        wasLogHypAppended = new boolean[n];
        assignedLogHypSeq = new int[n];

        theoremStmtGroupUsedList = new LinkedList<TheoremStmtGroup>();
        usedByTheoremStmtGroupList = new LinkedList<TheoremStmtGroup>();

        validateStmtGroupData(logicalSystem, messages, tlPreferences);

    }

    /**
     * Initializes the mustAppend flag for the TheoremStmtGroup.
     * <p>
     * The initial (default) setting of mustAppend is false unless the theorem
     * is new and has an incomplete proof.
     */
    public void initializeMustAppend() {

        mustAppend = false;

        if (getIsTheoremNew())
            if (isProofIncomplete)
                mustAppend = true;
    }

    /**
     * Validates the labels used in the TheoremStmtGroup proof.
     * <p>
     * During processing the highest sequence number referred to is updated,
     * <p>
     * Also, isProofIncomplete is set to true if a "?" is found in the proof.
     * <p>
     * And, two key lists about which theorems are used are updated:
     * {@code theoremStmtGroupUsedList} and {@code usedByTheoremStmtGroupList}.
     * These lists are the basis for determining the order in which theorems are
     * loaded into the Logical System.
     * 
     * @param logicalSystem LogicalSystem object.
     * @param theoremStmtGroupTbl the MMTTheoremSet Map containing the MMT
     *            Theorems in the set.
     * @throws TheoremLoaderException is thrown if there are data errors in the
     *             input MMTTheoremFile.
     */
    public void validateTheoremSrcStmtProofLabels(
        final LogicalSystem logicalSystem,
        final Map<String, TheoremStmtGroup> theoremStmtGroupTbl)
        throws TheoremLoaderException
    {

        final Map<String, Stmt> stmtTbl = logicalSystem.getStmtTbl();

        for (final String stepLabel : theoremSrcStmt.proofList) {
            if (stepLabel.compareTo(PaConstants.DEFAULT_STMT_LABEL) == 0) {

                isProofIncomplete = true;
                continue;
            }

            TheoremStmtGroup g = null;
            final Stmt ref = stmtTbl.get(stepLabel);
            if (ref == null || ref instanceof Theorem) {
                g = theoremStmtGroupTbl.get(stepLabel);
                if (g != null) {
                    accumInList(theoremStmtGroupUsedList, g);
                    g.accumInList(g.usedByTheoremStmtGroupList, this);
                }
            }

            if (ref == null) {
                if (g == null && !isLabelInLogHypList(stepLabel))
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_PROOF_LABEL_ERR_1 + stepLabel
                            + TlConstants.ERRMSG_PROOF_LABEL_ERR_2
                            + theoremLabel
                            + TlConstants.ERRMSG_PROOF_LABEL_ERR_3
                            + mmtTheoremFile.getSourceFileName());
            }
            else {
                updateMaxExistingMObjRef(ref);

                if (!getIsTheoremNew() &&

                maxExistingMObjRef.getSeq() >= theorem.getSeq())
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_1
                            + theoremLabel
                            + TlConstants.ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_2
                            + theorem.getSeq()
                            + TlConstants.ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_3
                            + ref.getLabel()
                            + TlConstants.ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_4
                            + ref.getSeq()
                            + TlConstants.ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_5
                            + mmtTheoremFile.getSourceFileName());
            }
        }
    }

    /**
     * Queues the theorem into either the ready list or the waiting list.
     * <p>
     * If the theorem's theoremStmtGroupUsedList is empty then it is ready to
     * update because it doesn't refer to any other theorems in the
     * MMTTheoremSet. Otherwise it goes into the waiting list. (When a theorem
     * is stored into the LogicalSystem it is removed from the
     * theoremStmtGroupUsedList of each theorem which refers to it.)
     * 
     * @param readyQueue queue of MMTTheorems ready for updating into the
     *            LogicalSystem.
     * @param waitingList list of MMTTheorems which are not yet ready to update
     *            into the LogicalSystem.
     */
    public void queueForUpdates(final Queue<TheoremStmtGroup> readyQueue,
        final List<TheoremStmtGroup> waitingList)
    {

        if (theoremStmtGroupUsedList.isEmpty())
            readyQueue.add(this);
        else
            waitingList.add(this);
    }

    /**
     * Requeues every MMT Theorem which uses this theorem.
     * <p>
     * When a theorem is stored into the LogicalSystem it is removed from the
     * theoremStmtGroupUsedList of each theorem which refers to it. To determine
     * which theorems need requeueing, the updated theorem's
     * usedByTheoremStmtGroup list is read and each theorem in that list is
     * requeued.
     * 
     * @param readyQueue queue of MMTTheorems ready for updating into the
     *            LogicalSystem.
     * @param waitingList list of MMTTheorems which are not yet ready to update
     *            into the LogicalSystem.
     * @throws TheoremLoaderException if a data error is discovered resulting
     *             from the theorem update.
     */
    public void queueDependentsForUpdate(
        final Deque<TheoremStmtGroup> readyQueue,
        final List<TheoremStmtGroup> waitingList) throws TheoremLoaderException
    {
        for (final TheoremStmtGroup g : usedByTheoremStmtGroupList)
            g.reQueueAfterUsedTheoremUpdated(this, readyQueue, waitingList);
    }

    /**
     * Adds or updates the LogicalSystem with the MMT Theorem and if the Logical
     * System has a Proof Verifier it runs the Metamath Proof Verification
     * algorithm.
     * <p>
     * Note: a proof verification error does not trigger a
     * TheoremLoaderException, which would thus halt the update of the entire
     * MMTTheoremSet. Instead, any proof verification errors are stored in the
     * Messages object for later display.
     * <p>
     * When a theorem is stored into the LogicalSystem it is removed from the
     * theoremStmtGroupUsedList of each theorem which refers to it. To determine
     * which theorems need requeueing, the updated theorem's
     * usedByTheoremStmtGroup list is read and each theorem in that list is
     * requeued.
     * 
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param tlPreferences TlPreferences object.
     * @throws TheoremLoaderException if a data error is discovered.
     * @throws LangException if a data error is discovered.
     */
    public void updateLogicalSystem(final LogicalSystem logicalSystem,
        final Messages messages, final TlPreferences tlPreferences)
        throws TheoremLoaderException, LangException
    {
        if (getIsTheoremNew())
            addTheoremToLogicalSystem(logicalSystem, messages, tlPreferences);
        else
            updateTheoremInLogicalSystem(logicalSystem, messages, tlPreferences);

        if (!isProofIncomplete) {
            final ProofVerifier proofVerifier = logicalSystem
                .getProofVerifier();
            if (proofVerifier != null) {
                final String s = proofVerifier.verifyOneProof(theorem);
                if (s != null)
                    // don't halt the update over a proof error
                    messages.accumErrorMessage(s);
            }
        }
    }

    /**
     * Backs out the updates made into the Logical System.
     * <p>
     * If the theorem is new, it and its logical hypotheses are removed from the
     * Logical System's statement table.
     * <p>
     * If the theorem was updated then the previous value of the theorem's proof
     * and its $d restrictions are restored.
     * 
     * @param stmtTbl the LogicalSystem object's stmtTbl.
     */
    public void reverseStmtTblUpdates(final Map<String, Stmt> stmtTbl) {
        if (getIsTheoremNew()) {
            for (final LogHyp element : logHypArray)
                if (element != null)
                    stmtTbl.remove(element.getLabel());
            if (theorem != null)
                stmtTbl.remove(theorem.getLabel());
        }
        else if (wasTheoremUpdated)
            theorem.proofUpdates(oldProof, oldDjVarsArray, oldOptDjVarsArray);
    }

    /**
     * Gets the isTheoremNew flag.
     * 
     * @return isTheoremNew flag.
     */
    public boolean getIsTheoremNew() {
        return isTheoremNew;
    }

    /**
     * Gets the theorem label.
     * 
     * @return theorem label.
     */
    public String getTheoremLabel() {
        return theoremLabel;
    }

    /**
     * Returns the MMTTheoremFile absolute pathname.
     * 
     * @return MMTTheoremFile absolute pathname.
     */
    public String getSourceFileName() {
        return mmtTheoremFile.getSourceFileName();
    }

    /**
     * Gets the wasTheoremUpdated flag.
     * 
     * @return wasTheoremUpdated flag.
     */
    public boolean getWasTheoremUpdated() {
        return wasTheoremUpdated;
    }

    /**
     * Gets the wasTheoremInserted flag.
     * 
     * @return wasTheoremInserted flag.
     */
    public boolean getWasTheoremInserted() {
        return wasTheoremInserted;
    }

    /**
     * Gets the wasTheoremAppended flag.
     * 
     * @return wasTheoremAppended flag.
     */
    public boolean getWasTheoremAppended() {
        return wasTheoremAppended;
    }

    /**
     * Gets the Theorem object or null if the theorem is new and has not yet
     * been stored in the Logical System.
     * 
     * @return Theorem object or null.
     */
    public Theorem getTheorem() {
        return theorem;
    }

    /**
     * Gets the wasLogHypInserted flag array.
     * 
     * @return wasLogHypInserted flag array.
     */
    public boolean[] getWasLogHypInsertedArray() {
        return wasLogHypInserted;
    }

    /**
     * Gets the wasLogHypAppended flag array.
     * 
     * @return wasLogHypAppended flag array.
     */
    public boolean[] getWasLogHypAppendedArray() {
        return wasLogHypAppended;
    }

    /**
     * Gets the LogHyp array which may contain nulls if the Theorem is new and
     * has not yet been stored in the Logical System.
     * 
     * @return LogHyp array.
     */
    public LogHyp[] getLogHypArray() {
        return logHypArray;
    }

    /**
     * Gets the BookManager insertSectionNbr for the theorem.
     * 
     * @return BookManager insertSectionNbr for the theorem.
     */
    public int getInsertSectionNbr() {
        return insertSectionNbr;
    }

    /**
     * Returns the assigned sequence number array for new Logical Hypotheses.
     * 
     * @return assigned seq numbers of new Logical Hypotheses.
     */
    public int[] getAssignedLogHypSeq() {
        return assignedLogHypSeq;
    }

    /**
     * Returns the assigned sequence number for a new Theorem.
     * 
     * @return assigned seq numbers of a new Theorem.
     */
    public int getAssignedTheoremSeq() {
        return assignedTheoremSeq;
    }

    /**
     * Converts TheoremStmtGroup to String.
     * 
     * @return returns TheoremStmtGroup string;
     */
    @Override
    public String toString() {
        return theoremLabel.toString();
    }

    /*
     * Computes hashcode for this TheoremStmtGroup
     *
     * @return hashcode for the TheoremStmtGroup
     */
    @Override
    public int hashCode() {
        return theoremLabel.hashCode();
    }

    /**
     * Compares TheoremStmtGroup object based on the label.
     * 
     * @param obj TheoremStmtGroup object to compare to this TheoremStmtGroup
     * @return returns negative, zero, or a positive int if this
     *         TheoremStmtGroup object is less than, equal to or greater than
     *         the input parameter obj.
     */
    public int compareTo(final Object obj) {
        return theoremLabel.compareTo(((TheoremStmtGroup)obj).theoremLabel);
    }

    /*
     * Compare for equality with another TheoremStmtGroup.
     * <p>
     * Equal if and only if the TheoremStmtGroup labels
     * are equal and the obj to be compared to this object
     * is not null and is a TheoremStmtGroup as well.
     *
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj ? true : !(obj instanceof TheoremStmtGroup) ? false
            : theoremLabel.equals(((TheoremStmtGroup)obj).theoremLabel);
    }

    /**
     * SEQ sequences by TheoremStmtGroup.theorem.getSeq().
     */
    static public final Comparator<TheoremStmtGroup> SEQ = new Comparator<TheoremStmtGroup>()
    {
        public int compare(final TheoremStmtGroup o1, final TheoremStmtGroup o2)
        {
            return o1.theorem.getSeq() - o2.theorem.getSeq();
        }
    };

    /**
     * NBR_LOG_HYP_SEQ sequences by number of LogHyps and Seq.
     */
    static public final Comparator<TheoremStmtGroup> NBR_LOG_HYP_SEQ = new Comparator<TheoremStmtGroup>()
    {

        public int compare(final TheoremStmtGroup o1, final TheoremStmtGroup o2)
        {
            int n = o1.theorem.getLogHypArrayLength()
                - o2.theorem.getLogHypArrayLength();

            if (n == 0)
                n =

                o1.theorem.getSeq() - o2.theorem.getSeq();

            return n;
        }
    };

    //
    // =======================================================
    // * Validation before beginning updates
    // =======================================================
    //

    private void reQueueAfterUsedTheoremUpdated(
        final TheoremStmtGroup usedTheoremStmtGroup,
        final Deque<TheoremStmtGroup> readyQueue,
        final List<TheoremStmtGroup> waitingList) throws TheoremLoaderException
    {

        updateMaxExistingMObjRef(usedTheoremStmtGroup.theorem);

        if (getIsTheoremNew()) {

            if (usedTheoremStmtGroup.wasTheoremAppended)
                mustAppend = true;
        }
        else if (maxExistingMObjRef.getSeq() >= theorem.getSeq())
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_1 + theoremLabel
                    + TlConstants.ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_2
                    + theorem.getSeq()
                    + TlConstants.ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_3
                    + usedTheoremStmtGroup.theoremLabel
                    + TlConstants.ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_4
                    + usedTheoremStmtGroup.theorem.getSeq()
                    + TlConstants.ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_5
                    + mmtTheoremFile.getSourceFileName());

        theoremStmtGroupUsedList.remove(usedTheoremStmtGroup);
        if (theoremStmtGroupUsedList.isEmpty()) {
            readyQueue.add(this);
            waitingList.remove(this);
        }
    }

    private void loadParsedInputIntoStmtGroup(final Statementizer statementizer)
        throws TheoremLoaderException
    {

        SrcStmt currSrcStmt;
        char c;
        try {
            while ((currSrcStmt = statementizer.getStmt()) != null) {

                c = currSrcStmt.keyword.charAt(1);

                if (c == MMIOConstants.MM_BEGIN_COMMENT_KEYWORD_CHAR)
                    continue;

                if (c == MMIOConstants.MM_AXIOMATIC_ASSRT_KEYWORD_CHAR
                    || c == MMIOConstants.MM_VAR_HYP_KEYWORD_CHAR
                    || c == MMIOConstants.MM_VAR_KEYWORD_CHAR
                    || c == MMIOConstants.MM_CNST_KEYWORD_CHAR
                    || c == MMIOConstants.MM_BEGIN_FILE_KEYWORD_CHAR)
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_MMT_THEOREM_FILE_BAD_KEYWORD_1
                            + mmtTheoremFile.getSourceFileName()
                            + TlConstants.ERRMSG_MMT_THEOREM_FILE_BAD_KEYWORD_2
                            + c);

                if (c == MMIOConstants.MM_BEGIN_SCOPE_KEYWORD_CHAR) {

                    if (currSrcStmt.seq > 1)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_BEGIN_SCOPE_MUST_BE_FIRST_1
                                + mmtTheoremFile.getSourceFileName());

                    beginScopeSrcStmt = currSrcStmt;
                    continue;
                }

                if (endScopeSrcStmt != null)
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_END_SCOPE_MUST_BE_LAST_1
                            + mmtTheoremFile.getSourceFileName());

                if (c == MMIOConstants.MM_END_SCOPE_KEYWORD_CHAR) {

                    if (beginScopeSrcStmt == null)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_BEGIN_SCOPE_MISSING_1_1
                                + mmtTheoremFile.getSourceFileName());

                    endScopeSrcStmt = currSrcStmt;
                    continue;
                }

                if (c == MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD_CHAR) {

                    if (theoremSrcStmt != null)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_EXTRA_THEOREM_STMT_1
                                + mmtTheoremFile.getSourceFileName());

                    if (mmtTheoremFile.getLabel().compareTo(currSrcStmt.label) != 0)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_THEOREM_LABEL_MISMATCH_1
                                + mmtTheoremFile.getLabel()
                                + TlConstants.ERRMSG_THEOREM_LABEL_MISMATCH_2
                                + mmtTheoremFile.getSourceFileName());

                    if (isLabelInLogHypList(currSrcStmt.label))
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_THEOREM_LABEL_HYP_DUP_1
                                + currSrcStmt.label
                                + TlConstants.ERRMSG_THEOREM_LABEL_HYP_DUP_2
                                + mmtTheoremFile.getSourceFileName());

                    if (currSrcStmt.proofBlockList != null)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_THEOREM_PROOF_COMPRESSED_1
                                + mmtTheoremFile.getSourceFileName());

                    theoremSrcStmt = currSrcStmt;
                    continue;
                }

                if (c == MMIOConstants.MM_LOG_HYP_KEYWORD_CHAR) {

                    if (theoremSrcStmt != null)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_THEOREM_LOG_HYP_SEQ_ERR_1
                                + mmtTheoremFile.getSourceFileName());

                    if (isLabelInLogHypList(currSrcStmt.label))
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_LOG_HYP_LABEL_HYP_DUP_1
                                + currSrcStmt.label
                                + TlConstants.ERRMSG_LOG_HYP_LABEL_HYP_DUP_2
                                + mmtTheoremFile.getSourceFileName());

                    logHypSrcStmtList.add(currSrcStmt);
                    continue;
                }

                if (c == MMIOConstants.MM_DJ_VAR_KEYWORD_CHAR) {

                    if (theoremSrcStmt != null)
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_THEOREM_DV_SEQ_ERR_1
                                + mmtTheoremFile.getSourceFileName());

                    dvSrcStmtList.add(currSrcStmt);
                    continue;
                }

                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_MMT_THEOREM_FILE_BOGUS_KEYWORD_1
                        + mmtTheoremFile.getSourceFileName()
                        + TlConstants.ERRMSG_MMT_THEOREM_FILE_BOGUS_KEYWORD_2
                        + c);

            }

            if (theoremSrcStmt == null)
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_THEOREM_FILE_THEOREM_MISSING_1
                        + mmtTheoremFile.getSourceFileName());

            if (endScopeSrcStmt == null && beginScopeSrcStmt != null)
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_END_SCOPE_MISSING_2_1
                        + mmtTheoremFile.getSourceFileName());

            if (beginScopeSrcStmt == null)
                if (!logHypSrcStmtList.isEmpty() || !dvSrcStmtList.isEmpty())
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_BEGIN_END_SCOPE_PAIR_MISSING_3_1
                            + mmtTheoremFile.getSourceFileName());
        } catch (final MMIOException e) {
            throw new TheoremLoaderException(e.getMessage());
        } catch (final IOException e) {
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_THEOREM_FILE_IO_ERROR_1
                    + mmtTheoremFile.getSourceFileName()
                    + TlConstants.ERRMSG_MMT_THEOREM_FILE_IO_ERROR_2
                    + e.getMessage());
        } finally {
            statementizer.close();
        }
    }

    private boolean isLabelInLogHypList(final String label) {
        for (final SrcStmt s : logHypSrcStmtList)
            if (s.label.compareTo(label) == 0)
                return true;
        return false;
    }

    /**
     * Compute a variety of facts about the SrcStmt objects read in from the
     * MMTTheoremFile and check for validity of the data using the
     * LogicalSystem.
     * 
     * @param logicalSystem the LogicalSystem
     * @param messages the error messages object
     * @param tlPreferences the Theorem Loader settings
     * @throws TheoremLoaderException if an error occurred
     */
    private void validateStmtGroupData(final LogicalSystem logicalSystem,
        final Messages messages, final TlPreferences tlPreferences)
        throws TheoremLoaderException
    {

        final Map<String, Sym> symTbl = logicalSystem.getSymTbl();
        final Map<String, Stmt> stmtTbl = logicalSystem.getStmtTbl();

        for (int i = 0; i < dvSrcStmtList.size(); i++)
            validateDvSrcStmt(dvSrcStmtList.get(i), symTbl);

        for (int i = 0; i < logHypSrcStmtList.size(); i++)
            validateLogHypSrcStmt(tlPreferences, logHypSrcStmtList.get(i),
                symTbl, stmtTbl, i);

        validateTheoremSrcStmt(tlPreferences, symTbl, stmtTbl);
    }

    private void validateDvSrcStmt(final SrcStmt dvSrcStmt,
        final Map<String, Sym> symTbl) throws TheoremLoaderException
    {

        checkVarMObjRef(dvSrcStmt, symTbl);
    }

    private void validateLogHypSrcStmt(final TlPreferences tlPreferences,
        final SrcStmt logHypSrcStmt, final Map<String, Sym> symTbl,
        final Map<String, Stmt> stmtTbl, final int logHypIndex)
        throws TheoremLoaderException
    {

        checkSymMObjRef(logHypSrcStmt, symTbl);

        validateMMTTypeCd(tlPreferences, logHypSrcStmt);

        final Stmt h = stmtTbl.get(logHypSrcStmt.label);

        if (h == null) {
            // is new?
        }
        else {
            if (!(h instanceof LogHyp))
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_LOG_HYP_STMT_MISMATCH_1
                        + logHypSrcStmt.label
                        + TlConstants.ERRMSG_LOG_HYP_STMT_MISMATCH_2
                        + logHypSrcStmt.seq
                        + TlConstants.ERRMSG_LOG_HYP_STMT_MISMATCH_3
                        + mmtTheoremFile.getSourceFileName());

            if (!h.getFormula().srcStmtEquals(logHypSrcStmt))
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_LOG_HYP_FORMULA_MISMATCH_1
                        + logHypSrcStmt.label
                        + TlConstants.ERRMSG_LOG_HYP_FORMULA_MISMATCH_2
                        + logHypSrcStmt.seq
                        + TlConstants.ERRMSG_LOG_HYP_FORMULA_MISMATCH_3
                        + mmtTheoremFile.getSourceFileName());
            updateMaxExistingMObjRef(h);
            logHypArray[logHypIndex] = (LogHyp)h;
        }
    }

    private void validateTheoremSrcStmt(final TlPreferences tlPreferences,
        final Map<String, Sym> symTbl, final Map<String, Stmt> stmtTbl)
        throws TheoremLoaderException
    {

        checkSymMObjRef(theoremSrcStmt, symTbl);

        validateMMTTypeCd(tlPreferences, theoremSrcStmt);

        final Stmt t = stmtTbl.get(theoremLabel);

        if (t == null) {
            isTheoremNew = true;
            for (final LogHyp element : logHypArray)
                if (element != null)
                    throw new TheoremLoaderException(
                        TlConstants.ERRMSG_NEW_THEOREM_OLD_LOG_HYP_1
                            + theoremLabel
                            + TlConstants.ERRMSG_NEW_THEOREM_OLD_LOG_HYP_2
                            + theoremSrcStmt.seq
                            + TlConstants.ERRMSG_NEW_THEOREM_OLD_LOG_HYP_3
                            + mmtTheoremFile.getSourceFileName());
        }
        else {
            if (!(t instanceof Theorem))
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_THEOREM_STMT_MISMATCH_1 + theoremLabel
                        + TlConstants.ERRMSG_THEOREM_STMT_MISMATCH_2
                        + theoremSrcStmt.seq
                        + TlConstants.ERRMSG_THEOREM_STMT_MISMATCH_3
                        + mmtTheoremFile.getSourceFileName());

            if (!t.getFormula().srcStmtEquals(theoremSrcStmt))
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_THEOREM_FORMULA_MISMATCH_1
                        + theoremLabel
                        + TlConstants.ERRMSG_THEOREM_FORMULA_MISMATCH_2
                        + theoremSrcStmt.seq
                        + TlConstants.ERRMSG_THEOREM_FORMULA_MISMATCH_3
                        + mmtTheoremFile.getSourceFileName());

            theorem = (Theorem)t;

            checkInputLogHypsAgainstTheorem(theorem);

            oldProof = theorem.getProof();
            oldDjVarsArray = theorem.getMandFrame().djVarsArray;
            oldOptDjVarsArray = theorem.getOptFrame().optDjVarsArray;
        }
    }

    private void validateMMTTypeCd(final TlPreferences tlPreferences,
        final SrcStmt currSrcStmt) throws TheoremLoaderException
    {
        if (currSrcStmt.typ != null
            && currSrcStmt.typ.equals(tlPreferences
                .getProvableLogicStmtTypeParm()))
            return;

        throw new TheoremLoaderException(
            TlConstants.ERRMSG_MMT_TYP_CD_NOT_VALID_1 + currSrcStmt.typ
                + TlConstants.ERRMSG_MMT_TYP_CD_NOT_VALID_2 + currSrcStmt.label
                + TlConstants.ERRMSG_MMT_TYP_CD_NOT_VALID_3 + currSrcStmt.seq
                + TlConstants.ERRMSG_MMT_TYP_CD_NOT_VALID_4
                + mmtTheoremFile.getSourceFileName());
    }

    private void checkInputLogHypsAgainstTheorem(final Theorem theorem)
        throws TheoremLoaderException
    {

        boolean match = true;

        final LogHyp[] theoremLogHypArray = theorem.getLogHypArray();

        if (theoremLogHypArray.length == logHypArray.length)
            loopI: for (int i = 0; i < theoremLogHypArray.length; i++) {

                for (int j = 0; j < logHypArray.length; j++)
                    if (logHypArray[j] == theoremLogHypArray[i])
                        continue loopI;
                match = false;
                break loopI;
            }
        else
            match = false;

        if (match == false)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_LOG_HYPS_DONT_MATCH_1 + theoremLabel
                    + TlConstants.ERRMSG_LOG_HYPS_DONT_MATCH_2
                    + theoremSrcStmt.seq
                    + TlConstants.ERRMSG_LOG_HYPS_DONT_MATCH_3
                    + mmtTheoremFile.getSourceFileName());
    }

    private void checkSymMObjRef(final SrcStmt x, final Map<String, Sym> symTbl)
        throws TheoremLoaderException
    {
        if (x.typ != null) {
            final Sym sym = symTbl.get(x.typ);
            if (sym == null)
                generateSymNotFndError(x, x.typ);
            else
                updateMaxExistingMObjRef(sym);
        }

        for (final String s : x.symList) {
            final Sym sym = symTbl.get(s);
            if (sym == null)
                generateSymNotFndError(x, s);
            else
                updateMaxExistingMObjRef(sym);
        }
    }

    private void checkVarMObjRef(final SrcStmt x, final Map<String, Sym> symTbl)
        throws TheoremLoaderException
    {
        for (final String s : x.symList) {
            final Sym sym = symTbl.get(s);
            if (sym == null)
                generateSymNotFndError(x, s);
            else
                updateMaxExistingMObjRef(sym);
            if (!sym.isVar())
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_DJ_VAR_SYM_NOT_A_VAR_1 + s
                        + TlConstants.ERRMSG_DJ_VAR_SYM_NOT_A_VAR_2 + x.seq
                        + TlConstants.ERRMSG_DJ_VAR_SYM_NOT_A_VAR_3
                        + mmtTheoremFile.getSourceFileName());
        }
    }
    private void generateSymNotFndError(final SrcStmt x, final String id)
        throws TheoremLoaderException
    {

        throw new TheoremLoaderException(
            TlConstants.ERRMSG_SRC_STMT_SYM_NOTFND_1 + id
                + TlConstants.ERRMSG_SRC_STMT_SYM_NOTFND_2 + x.seq
                + TlConstants.ERRMSG_SRC_STMT_SYM_NOTFND_3
                + mmtTheoremFile.getSourceFileName());
    }

    //
    // =======================================================
    // * Updates
    // =======================================================
    //

    private void updateTheoremInLogicalSystem(
        final LogicalSystem logicalSystem, final Messages messages,
        final TlPreferences tlPreferences) throws TheoremLoaderException,
        LangException
    {

        wasTheoremUpdated = true;

        theorem.setProof(logicalSystem.getStmtTbl(), theoremSrcStmt.proofList);

        if (tlPreferences.getDjVarsNoUpdate()) {}
        else {

            final List<DjVars> mandDjVarsUpdateList = new LinkedList<DjVars>();
            final List<DjVars> optDjVarsUpdateList = new LinkedList<DjVars>();
            buildMandAndOptDjVarsUpdateLists(logicalSystem.getSymTbl(),
                mandDjVarsUpdateList, optDjVarsUpdateList);

            if (tlPreferences.getDjVarsMerge())
                theorem.mergeDjVars(mandDjVarsUpdateList, optDjVarsUpdateList);
            else if (tlPreferences.getDjVarsReplace())
                theorem
                    .replaceDjVars(mandDjVarsUpdateList, optDjVarsUpdateList);
        }

        if (tlPreferences.getAuditMessages()) {
            final StringBuilder sb = new StringBuilder();
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_UPD_1);
            sb.append(theorem.getLabel());
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_UPD_2);
            sb.append(theorem.getSeq());
//          sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_UPD_3);
//          sb.append(theorem.getChapterNbr());
//          sb.append(".");
//          sb.append(theorem.getSectionNbr());
//          sb.append(".");
//          sb.append(theorem.getSectionMObjNbr());
            messages.accumInfoMessage(sb.toString());
        }
    }

    private void addTheoremToLogicalSystem(final LogicalSystem logicalSystem,
        final Messages messages, final TlPreferences tlPreferences)
        throws TheoremLoaderException, LangException
    {

        SrcStmt currSrcStmt;
        final SeqAssigner seqAssigner = logicalSystem.getSeqAssigner();

        logicalSystem.beginScope();

        insertSectionNbr = -1; // for BookManager.

        for (int i = 0; i < logHypArray.length; i++) {
            currSrcStmt = logHypSrcStmtList.get(i);

            final LogHyp s = (LogHyp)logicalSystem.getStmtTbl().get(
                currSrcStmt.label);
            if (s != null)
                // another new mmt theorem already added it, so...
                throw new TheoremLoaderException(
                    TlConstants.ERRMSG_HYP_ADDED_TWICE_ERR_1 + theoremLabel
                        + TlConstants.ERRMSG_HYP_ADDED_TWICE_ERR_2
                        + currSrcStmt.label
                        + TlConstants.ERRMSG_HYP_ADDED_TWICE_ERR_3
                        + mmtTheoremFile.getSourceFileName());

            assignedLogHypSeq[i] = -1;
            if (!mustAppend && maxExistingMObjRef != null)
                assignedLogHypSeq[i] = seqAssigner
                    .nextInsertSeq(maxExistingMObjRef.getSeq());

            if (assignedLogHypSeq[i] == -1) {

                assignedLogHypSeq[i] = seqAssigner.nextSeq();

                wasLogHypAppended[i] = true;
            }
            else {
                wasLogHypInserted[i] = true;

                // save this info for Book Manager!
                if (insertSectionNbr == -1)
                    insertSectionNbr = maxExistingMObjRef.getSectionNbr();
            }

            logHypArray[i] = loadLogHyp(logicalSystem, assignedLogHypSeq[i],
                currSrcStmt);

            if (tlPreferences.getAuditMessages())
                messages.accumInfoMessage(buildAddAuditMessage(logHypArray[i],
                    wasLogHypAppended[i]));

            updateMaxExistingMObjRef(logHypArray[i]);

            loadStmtParseTree(messages, logicalSystem, logHypArray[i],
                currSrcStmt);
        }

        assignedTheoremSeq = -1;
        if (!mustAppend && maxExistingMObjRef != null)
            assignedTheoremSeq = seqAssigner.nextInsertSeq(maxExistingMObjRef
                .getSeq());

        if (assignedTheoremSeq == -1) {

            assignedTheoremSeq = seqAssigner.nextSeq();
            wasTheoremAppended = true;
        }
        else {
            wasTheoremInserted = true;
            // save this info for Book Manager!
            if (insertSectionNbr == -1)
                insertSectionNbr = maxExistingMObjRef.getSectionNbr();
        }

        theorem = loadTheorem(logicalSystem, assignedTheoremSeq);

        logicalSystem.endScope();

        loadStmtParseTree(messages, logicalSystem, theorem, theoremSrcStmt);

        if (!dvSrcStmtList.isEmpty()) {

            final List<DjVars> mandDjVarsUpdateList = new LinkedList<DjVars>();
            final List<DjVars> optDjVarsUpdateList = new LinkedList<DjVars>();
            buildMandAndOptDjVarsUpdateLists(logicalSystem.getSymTbl(),
                mandDjVarsUpdateList, optDjVarsUpdateList);

            theorem.replaceDjVars(mandDjVarsUpdateList, optDjVarsUpdateList);
        }

        if (tlPreferences.getAuditMessages())
            messages.accumInfoMessage(buildAddAuditMessage(theorem,
                wasTheoremAppended));
    }

    private String buildAddAuditMessage(final Stmt stmt, final boolean appended)
    {
        final StringBuilder sb = new StringBuilder();
        sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_1);
        if (stmt instanceof Theorem)
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_2a);
        else
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_2b);
        sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_3);
        sb.append(stmt.getLabel());
        sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_4);
        sb.append(stmt.getSeq());
        if (appended)
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_5a);
        else
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_5b);
        sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_6);
        if (maxExistingMObjRef == null) {
            sb.append(" ");
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_7);
            sb.append(" ");
        }
        else {
            if (maxExistingMObjRef instanceof Sym)
                sb.append(((Sym)maxExistingMObjRef).getId());
            else
                sb.append(((Stmt)maxExistingMObjRef).getLabel());
            sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_7);
            sb.append(maxExistingMObjRef.getSeq());
        }
//      sb.append(TlConstants.ERRMSG_AUDIT_MSG_THEOREM_ADD_8);
//      sb.append(stmt.getChapterNbr());
//      sb.append(".");
//      sb.append(stmt.getSectionNbr());
//      sb.append(".");
//      sb.append(stmt.getSectionMObjNbr());
        return sb.toString();
    }

    private void buildMandAndOptDjVarsUpdateLists(
        final Map<String, Sym> symTbl, final List<DjVars> mandDjVarsUpdateList,
        final List<DjVars> optDjVarsUpdateList) throws LangException
    {
        final List<List<String>> inputDjVarsStmtList = new ArrayList<List<String>>();
        for (final SrcStmt currSrcStmt : dvSrcStmtList)
            // note: DjVar Vars used in an existing theorem must
            // be defined in the mandatory or optional
            // frame of the theorem.
            inputDjVarsStmtList.add(currSrcStmt.symList);

        theorem.loadMandAndOptDjVarsUpdateLists(symTbl, inputDjVarsStmtList,
            mandDjVarsUpdateList, optDjVarsUpdateList);
    }

    // Note: In a rare case, the formula is parseable
    // parse error but the proof is complete and
    // invalid so that the theorem is inserted
    // at a location which prevents parsing (the
    // insert seq number is lower than the syntax
    // axioms used). The solution for this is to
    // not put a totally bogus proof in a theorem --
    // if the proof is unknown, use a "?" to
    // signify that the proof is incomplete.
    private void loadStmtParseTree(final Messages messages,
        final LogicalSystem logicalSystem, final Stmt stmt,
        final SrcStmt currSrcStmt) throws TheoremLoaderException
    {

        final SyntaxVerifier syntaxVerifier = logicalSystem.getSyntaxVerifier();

        if (syntaxVerifier == null)
            return; // user loading all before grammar checking

        final ParseTree parseTree = syntaxVerifier.parseOneStmt(messages,
            logicalSystem.getSymTbl(), logicalSystem.getStmtTbl(), stmt);
        if (parseTree == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_MMT_STMT_PARSE_ERR_1 + currSrcStmt.label
                    + TlConstants.ERRMSG_MMT_STMT_PARSE_ERR_2 + currSrcStmt.seq
                    + TlConstants.ERRMSG_MMT_STMT_PARSE_ERR_3
                    + mmtTheoremFile.getSourceFileName());
        stmt.setExprParseTree(parseTree);
    }

    private Theorem loadTheorem(final LogicalSystem logicalSystem, final int seq)
        throws LangException
    {

        final Theorem theorem = logicalSystem.addTheoremForTheoremLoader(seq,
            theoremLabel, theoremSrcStmt.typ, theoremSrcStmt.symList,
            theoremSrcStmt.proofList);

        if (theoremSrcStmt.comment != null)
            theorem.setDescription(theoremSrcStmt.comment);

        return theorem;
    }

    /**
     * Sends logical hypothesis to LogicalSystem.
     * 
     * @param logicalSystem the LogicalSystem
     * @param seq preassigned MObj seq number
     * @param currSrcStmt the logical hypothesis
     * @return newly constructed LogHyp added to LogicalSystem.
     * @throws LangException if duplicate label, undefined vars, etc.
     */
    private LogHyp loadLogHyp(final LogicalSystem logicalSystem, final int seq,
        final SrcStmt currSrcStmt) throws LangException
    {

        return logicalSystem.addLogHypForTheoremLoader(seq, currSrcStmt.label,
            currSrcStmt.typ, currSrcStmt.symList);
    }

    //
    // =======================================================
    // * General Stuff
    // =======================================================
    //

    private void accumInList(final List<TheoremStmtGroup> list,
        final TheoremStmtGroup tsg)
    {
        if (!list.contains(tsg))
            list.add(tsg);
    }

    private void updateMaxExistingMObjRef(final MObj<?> mObj) {

        if (maxExistingMObjRef == null
            || mObj.getSeq() > maxExistingMObjRef.getSeq())
            maxExistingMObjRef = mObj;
    }
}
