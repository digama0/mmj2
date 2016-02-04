//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SeqAssigner.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     --> new!
 */

package mmj.lang;

import java.util.*;

import mmj.tl.*;

/**
 * The {@code SeqAssigner} generates sequence numbers for Metamath objects
 * (MObj) within the mmj2 Logical System.
 * <p>
 * SeqAssigner.java's job is assigning sequence numbers to Metamath objects
 * (MObj's) as they are loaded and inserted in the mmj2 Logical System. Sequence
 * numbers are assigned sequentially and provide the basis for the mechanism
 * which ensures that cyclic or forward references by Metamath objects are
 * rejected (as invalid).
 * <p>
 * The motivation for this new feature is assignment of sequence numbers for
 * theorems inserted by the new Theorem Loader enhancement. Previously all
 * Metamath objects were appended to the "end" of the Logical System and
 * sequence numbers were assigned from 10 by 10 up to the maximum of 2**31 - 1.
 * <p>
 * The Theorem Loader aims to "insert" theorems and logical hypotheses into the
 * sequence number "gaps" left over from the initial Metamath .mm file load(s)
 * (RunParm "LoadFile").
 * <p>
 * The Theorem Loader determines the Metamath object dependencies of objects to
 * be inserted and instructs SeqAssigner to assign in the gap after the
 * referenced object with the highest sequence number. The SeqAssigner
 * determines whether or not the "gap" is full and assigns the appropriate
 * sequence number for each new object. A full gap results in an "append"ed
 * sequence number, which may or may not be suitable -- if a new theorem is
 * referred to by an existing theorem, then appending the new theorem is not
 * acceptable (which results in an error and backout of all changes prior to
 * detection of the error.)
 * <p>
 * Associated with SeqAssigner.java is a new RunParm,
 *
 * <pre>
 *      SeqAssignerIntervalSize,9999
 * </pre>
 *
 * The default sequence number interval is 1000, thus allowing for 999 inserts
 * into every gap. It also provides the capability to load at least 1 million
 * Metamath objects (perhaps more) into the mmj2 Logical System. An interval
 * size of 100 would be suitable for almost every purpose -- the exception being
 * automated updates from an external system via the new "mmj2 Service" feature.
 */
public class SeqAssigner implements TheoremLoaderCommitListener {

    private int mObjCount;
    private int nbrIntervals;
    private final int intervalSize;

    private boolean checkpointInitialized;

    private int checkpointMObjCount;
    private int checkpointNbrIntervals;

    private final Map<Integer, BitSet> intervalTbl;

    /**
     * Validates Interval Size parameter.
     * <p>
     * Provided this function so that the same code can be used by
     * LogicalSystemBoss.
     *
     * @param n interval size for MObj.seq numbers.
     * @throws IllegalArgumentException if invalid interval size.
     */
    public static void validateIntervalSize(final int n) {

        if (n < LangConstants.SEQ_ASSIGNER_MIN_INTERVAL_SIZE
            || n > LangConstants.SEQ_ASSIGNER_MAX_INTERVAL_SIZE)
            throw new IllegalArgumentException(
                new LangException(LangConstants.ERRMSG_INTERVAL_SIZE_RANGE_ERR,
                    n, LangConstants.SEQ_ASSIGNER_MIN_INTERVAL_SIZE,
                    LangConstants.SEQ_ASSIGNER_MAX_INTERVAL_SIZE));
    }

    /**
     * Validates Interval Table Initial Size parameter.
     * <p>
     * Provided this function so that the same code can be used by
     * LogicalSystemBoss.
     *
     * @param n interval table initial size for MObj.seq numbers.
     */
    public static void validateIntervalTblInitialSize(final int n) {

        if (n < LangConstants.SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MIN
            || n > LangConstants.SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MAX)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_INTERVAL_TBL_SIZE_RANGE_ERR, n,
                LangConstants.SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MIN,
                LangConstants.SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_MAX));
    }
    /**
     * Construct with default set of parameters.
     */
    public SeqAssigner() {
        this(LangConstants.SEQ_ASSIGNER_INTERVAL_SIZE_DEFAULT,
            LangConstants.SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE_DEFAULT);
    }

    /**
     * Construct with full set of parameters.
     *
     * @param intervalSize numbering interval for MObj.seq numbers.
     * @param intervalTblInitialSize initial size of HashMap for recording
     *            insertions in the sequence number interval gaps.
     */
    public SeqAssigner(final int intervalSize,
        final int intervalTblInitialSize)
    {

        SeqAssigner.validateIntervalSize(intervalSize);
        this.intervalSize = intervalSize;

        SeqAssigner.validateIntervalTblInitialSize(intervalTblInitialSize);
        intervalTbl = new HashMap<>(intervalTblInitialSize);
    }

    /**
     * Constructs MObj.seq value for new object.
     * <p>
     * The return number is one of the "appended" sequence numbers, located
     * logically at the end of LogicalSystem.
     *
     * @return new MObj.seq number.
     * @throws IllegalArgumentException if the next available sequence number is
     *             beyond the number range available to a Java "int".
     */
    public int nextSeq() {

        mObjCount++;

        final long seq = ++nbrIntervals * intervalSize;
        if (seq + intervalSize < Integer.MAX_VALUE)
            return (int)seq;

        throw new IllegalArgumentException(new LangException(
            LangConstants.ERRMSG_SEQ_ASSIGNER_OUT_OF_NUMBERS, seq, mObjCount));
    }

    /**
     * Constructs MObj.seq value for a Metamath object to be inserted in the
     * number gap between two existing object.
     * <p>
     * The return number is one of the "inserted" sequence numbers, located
     * logically at the end of LogicalSystem.
     * <p>
     * The input locAfterSeq designates a an "interval" which contains the "gap"
     * of numbers (e.g. seq 3501 is in the 35 interval which has gap 3501 thru
     * 3599.) The inserted sequence number goes into this gap if the gap is not
     * already full. (A HashMap of BitSet is used to keep track of the intervals
     * and gaps, respectively.)
     * <p>
     * To conserve empty gap space, if the locAfterSeq is assigned to the last
     * interval in the system, then the next sequence number is not inserted,
     * but appended.
     * <p>
     * If the next sequence number is not inserted (meaning that it is to be
     * appended), then -1 is returned instead of the assigned sequence number.
     *
     * @param locAfterSeq see description
     * @return new MObj.seq number if number was inserted, else -1 indicating
     *         that it must be appended.
     * @throws IllegalArgumentException if the next available sequence number is
     *             beyond the number range available to a Java "int".
     */
    public int nextInsertSeq(final int locAfterSeq) {

        if (intervalSize == 1)
            return -1;

        final Integer intervalNumber = convertSeqToIntervalNumber(locAfterSeq);
        final int i = intervalNumber.intValue();

        if (i >= nbrIntervals || i == 0)
            // use append instead to conserve gap sequence numbers
            return -1;

        boolean bitSetFound = false;
        BitSet bitSet = getBitSet(intervalNumber);
        if (bitSet == null)
            bitSet = initBitSet();
        else
            bitSetFound = true;

        int bitNumber;
        try {
            if (bitSet.get(intervalSize - 1))
                // full!
                return -1;
            bitNumber = bitSet.nextClearBit(1);
        } catch (final IndexOutOfBoundsException e) {
            return -1; // gap full, must append!
        }

        bitSet.set(bitNumber, true);

        if (!bitSetFound)
            putBitSet(intervalNumber, bitSet);

        // overflow of 'int' not possible here because a higher
        // interval is already in use.
        final int seq = i * intervalSize + bitNumber;

        return seq;
    }

    /**
     * Bit of a misnomer as this function takes a checkpoint in case a rollback
     * is needed by TheoremLoader.
     *
     * @throws IllegalArgumentException if checkpointing is already on.
     */
    public void turnOnCheckpointing() {
        if (checkpointInitialized)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_SEQ_ASSIGNER_CHECKPOINT_STATE));
        checkpointMObjCount = mObjCount;
        checkpointNbrIntervals = nbrIntervals;
        checkpointInitialized = true;
    }

    /**
     * Bit of a misnomer as this function merely turns off checkpointing.
     *
     * @throws IllegalArgumentException if checkpointing is not already on.
     */
    public void commit(final MMTTheoremSet mmtTheoremSet) {

        if (!checkpointInitialized)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_SEQ_ASSIGNER_COMMIT_STATE));

        turnOffCheckpointing();
    }

    /**
     * Reverses all changes made to the SeqAssigner state variables since the
     * last checkpoing was taken.
     * <p>
     * Notice that only inserted sequence numbers are individually backed out.
     * Appended sequence numbers are backed out en masse by reverting to the
     * checkpointed value of "nbrIntervals" -- that is because there is no
     * BitSet created for an interval until there is an insert in the interval's
     * gap.
     * <p>
     * Audit messages are produced primarily so that the code is testable --
     * they provide "instrumentation".
     *
     * @param mmtTheoremSet TheoremLoader's set of adds and updates.
     * @param messages the Messages object for error logging
     * @param auditMessages true to write audit messages
     * @throws IllegalArgumentException if a checkpoint was not taken prior to
     *             the rollback request.
     */
    public void rollback(final MMTTheoremSet mmtTheoremSet,
        final Messages messages, final boolean auditMessages)
    {

        if (!checkpointInitialized)
            throw new IllegalArgumentException(new LangException(
                LangConstants.ERRMSG_SEQ_ASSIGNER_ROLLBACK_STATE));

        boolean[] wasLogHypInsertedArray;
        boolean[] wasLogHypAppendedArray;
        int[] assignedLogHypSeq;
        int assignedTheoremSeq;
        LogHyp[] logHypArray;
        boolean wasTheoremInserted;
        boolean wasTheoremAppended;
        Theorem theorem;

        for (final TheoremStmtGroup t : mmtTheoremSet) {
            if (!t.getIsTheoremNew())
                continue;

            wasLogHypInsertedArray = t.getWasLogHypInsertedArray();
            wasLogHypAppendedArray = t.getWasLogHypAppendedArray();

            assignedLogHypSeq = t.getAssignedLogHypSeq();
            assignedTheoremSeq = t.getAssignedTheoremSeq();

            logHypArray = t.getLogHypArray();
            wasTheoremInserted = t.getWasTheoremInserted();
            wasTheoremAppended = t.getWasTheoremAppended();
            theorem = t.getTheorem();

            for (int i = 0; i < wasLogHypInsertedArray.length; i++)
                if (wasLogHypInsertedArray[i]) {
                    if (auditMessages)
                        outputRollbackAuditMessage(messages,
                            assignedLogHypSeq[i],
                            LangConstants.ERRMSG_LOGHYP_CAPTION, logHypArray[i],
                            LangConstants.ERRMSG_INSERTED_CAPTION);
                    unassignInsertedSeq(assignedLogHypSeq[i]);
                }

            for (int i = 0; i < wasLogHypAppendedArray.length; i++)
                if (wasLogHypAppendedArray[i])
                    if (auditMessages)
                        outputRollbackAuditMessage(messages,
                            assignedLogHypSeq[i],
                            LangConstants.ERRMSG_LOGHYP_CAPTION, logHypArray[i],
                            LangConstants.ERRMSG_APPENDED_CAPTION);

            if (wasTheoremInserted) {
                if (auditMessages)
                    outputRollbackAuditMessage(messages, assignedTheoremSeq,
                        LangConstants.ERRMSG_THEOREM_CAPTION, theorem,
                        LangConstants.ERRMSG_INSERTED_CAPTION);
                unassignInsertedSeq(assignedTheoremSeq);

            }

            if (wasTheoremAppended)
                if (auditMessages)
                    outputRollbackAuditMessage(messages, assignedTheoremSeq,
                        LangConstants.ERRMSG_THEOREM_CAPTION, theorem,
                        LangConstants.ERRMSG_APPENDED_CAPTION);
        }

        mObjCount = checkpointMObjCount;
        nbrIntervals = checkpointNbrIntervals;

        turnOffCheckpointing();
    }

    private void outputRollbackAuditMessage(final Messages messages,
        final int seq, final String stmtCaption, final Stmt stmt,
        final String updateCaption)
    {
        messages.accumMessage(LangConstants.ERRMSG_SEQ_ASSIGNER_ROLLBACK_AUDIT,
            seq, stmtCaption, stmt == null ? "n/a" : stmt.getLabel(),
            updateCaption);
    }

    private void turnOffCheckpointing() {
        checkpointInitialized = false;
    }

    private void unassignInsertedSeq(final int seq) {

        final BitSet bitSet = getBitSet(convertSeqToIntervalNumber(seq));

        if (bitSet != null) {
            final int bit = seq % intervalSize;
            if (bit != 0)
                bitSet.set(bit, false);
        }
    }

    private Integer convertSeqToIntervalNumber(final int seq) {

        return Integer.valueOf(seq / intervalSize);
    }

    private BitSet getBitSet(final Integer intervalNumber) {

        return intervalTbl.get(intervalNumber);
    }

    private void putBitSet(final Integer intervalNumber, final BitSet bitSet) {

        intervalTbl.put(intervalNumber, bitSet);
    }

    private BitSet initBitSet() {

        final BitSet bitSet = new BitSet(intervalSize);

        bitSet.set(0, true);

        return bitSet;
    }

}
