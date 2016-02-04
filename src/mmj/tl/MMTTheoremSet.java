//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MMTTheoremSet.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     --> new.
 */

package mmj.tl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import mmj.lang.*;
import mmj.pa.MMJException;

/**
 * MMTTheoremSet represents a set of MMTTheoremStmtGroup objects to be loaded
 * into the Logical System.
 */
public class MMTTheoremSet implements Iterable<TheoremStmtGroup> {

    private final LogicalSystem logicalSystem;
    private final Messages messages;
    private final TlPreferences tlPreferences;

    private final Map<String, TheoremStmtGroup> theoremStmtGroupTbl;

    private int nbrOfAdds;

    /**
     * Constructs the MMTTheoremSet using an array of Files, which may be
     * obtained from the MMTFolder.
     * <p>
     * Validation of the input theorems is performed.
     *
     * @param fileArray array of Files designating MMTTheoremFiles to be loaded
     *            into the MMTTheoremSet.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param tlPreferences TlPreferences object.
     * @throws TheoremLoaderException if there are any errors reading the input
     *             files or if there are validation errors.
     */
    public MMTTheoremSet(final File[] fileArray,
        final LogicalSystem logicalSystem, final Messages messages,
        final TlPreferences tlPreferences) throws TheoremLoaderException
    {

        this.logicalSystem = logicalSystem;
        this.messages = messages;
        this.tlPreferences = tlPreferences;

        theoremStmtGroupTbl = buildTheoremStmtGroupTbl(fileArray.length);

        MMTTheoremFile mmtTheoremFile;

        nbrOfAdds = 0;
        for (final File element : fileArray) {

            mmtTheoremFile = new MMTTheoremFile(element);

            final TheoremStmtGroup t = new TheoremStmtGroup(mmtTheoremFile,
                logicalSystem, messages, tlPreferences);

            putToTheoremStmtGroupTbl(t);

            if (t.getIsTheoremNew())
                nbrOfAdds++;
        }

        preUpdateRelationalEdits();
    }

    /**
     * Constructs the MMTTheoremSet using a single input MMTTheoremFile.
     * <p>
     * Validation of the input theorem is performed.
     *
     * @param mmtTheoremFile MMTTheoremFile to be loaded into the MMTTheoremSet.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param tlPreferences TlPreferences object.
     * @throws TheoremLoaderException if there are any errors reading the input
     *             files or if there are validation errors.
     */
    public MMTTheoremSet(final MMTTheoremFile mmtTheoremFile,
        final LogicalSystem logicalSystem, final Messages messages,
        final TlPreferences tlPreferences) throws TheoremLoaderException
    {

        this.logicalSystem = logicalSystem;
        this.messages = messages;
        this.tlPreferences = tlPreferences;

        theoremStmtGroupTbl = buildTheoremStmtGroupTbl(1);

        final TheoremStmtGroup t = new TheoremStmtGroup(mmtTheoremFile,
            logicalSystem, messages, tlPreferences);

        putToTheoremStmtGroupTbl(t);

        if (t.getIsTheoremNew())
            nbrOfAdds = 1;

        preUpdateRelationalEdits();
    }

    /**
     * Returns a List of the TheoremStmtGroups in the MMTTheoremSet which were
     * added to the LogicalSystem during the load process.
     * <p>
     * Note: this method is called by mmj.lang.BookManager.java during
     * "commit()" processing at the end of the TheoremLoader load process.
     *
     * @param comparator Comparator for TheoremStmtGroup.
     * @return List of added TheoremStmtGroup objects sorted using the input
     *         Comparator.
     */
    public List<TheoremStmtGroup> buildSortedListOfAdds(
        final Comparator<? super TheoremStmtGroup> comparator)
    {

        final List<TheoremStmtGroup> outList = new ArrayList<>(nbrOfAdds);

        for (final TheoremStmtGroup t : this)
            if (t.getIsTheoremNew())
                outList.add(t);

        Collections.sort(outList, comparator);

        return outList;
    }

    /**
     * Returns a List of Theorems in the MMTTheoremSet which were added to the
     * LogicalSystem during the load process.
     * <p>
     * Note: this method is called by mmj.pa.ProofAsst.java during "commit()"
     * processing at the end of the TheoremLoader load process.
     *
     * @param comparator Comparator for mmj.lang.Assrt.
     * @return List of added Theorem objects sorted using the input Comparator.
     */
    public List<Theorem> buildSortedAssrtListOfAdds(
        final Comparator<? super Theorem> comparator)
    {
        final List<Theorem> outList = new ArrayList<>(nbrOfAdds);

        for (final TheoremStmtGroup t : this)
            if (t.getIsTheoremNew())
                outList.add(t.getTheorem());

        Collections.sort(outList, comparator);

        return outList;
    }

    /**
     * Returns an Iterator over TheoremStmtGroup objects contained in the
     * MMTTheoremSet theoremStmtGroupTbl.
     * <p>
     * Note: this is used in LogicalSystem and SeqAssigner during rollback()
     * processing (fyi).
     *
     * @return Iterator over TheoremStmtGroup objects contained in the
     *         MMTTheoremSet theoremStmtGroupTbl.
     */
    public Iterator<TheoremStmtGroup> iterator() {
        return theoremStmtGroupTbl.values().iterator();
    }

    /**
     * Updates the LogicalSystem using the contents of the MMTTheoremSet.
     * <p>
     * If errors are encountered during the update,
     * logicalSystem.theoremLoaderRollback() is called to reverse any previous
     * updates.
     * <p>
     * Likewise, if no errors are encountered during the update,
     * logicalSystem.theoremLoaderCommit() is called to finalize the updates.
     * <p>
     * FYI, this is called by TheoremLoader.
     *
     * @throws TheoremLoaderException if any errors are encountered during the
     *             update process.
     * @throws IllegalArgumentException if either rollback or commit of the
     *             updates fail.
     */
    public void updateLogicalSystem() throws TheoremLoaderException {

        final Deque<TheoremStmtGroup> readyQueue = new LinkedList<>();
        final List<TheoremStmtGroup> waitingList = new LinkedList<>();

        for (final TheoremStmtGroup x : this)
            x.queueForUpdates(readyQueue, waitingList);

        TheoremStmtGroup readyTheoremStmtGroup;

        logicalSystem.seqAssigner.turnOnCheckpointing();

        try {
            while (true) {

                if (readyQueue.isEmpty())
                    if (waitingList.isEmpty())
                        break;
                    else
                        throw new TheoremLoaderException(
                            TlConstants.ERRMSG_CYCLIC_REF_ERROR,
                            waitingList.stream()
                                .map(TheoremStmtGroup::getTheoremLabel)
                                .collect(Collectors.toList()));

                readyTheoremStmtGroup = readyQueue.removeFirst();

                readyTheoremStmtGroup.updateLogicalSystem(logicalSystem,
                    messages, tlPreferences);

                readyTheoremStmtGroup.queueDependentsForUpdate(readyQueue,
                    waitingList);
            }
        }

        catch (final MMJException e) {
            logicalSystem.theoremLoaderRollback(this, e, messages,
                tlPreferences.auditMessages.get());
        }

        // this can fail with IllegalArgumentException
        // indicating unrecoverable error.
        logicalSystem.theoremLoaderCommit(this);

    }

    private Map<String, TheoremStmtGroup> buildTheoremStmtGroupTbl(
        final int n)
    {
        return new HashMap<>(n * 4 / 3 + 2);
    }

    private void putToTheoremStmtGroupTbl(final TheoremStmtGroup t)
        throws TheoremLoaderException
    {

        final TheoremStmtGroup dupTheoremStmtGroup = theoremStmtGroupTbl
            .put(t.getTheoremLabel(), t);

        if (dupTheoremStmtGroup != null)
            throw new TheoremLoaderException(TlConstants.ERRMSG_DUP_MMT_THEOREM,
                t.getSourceFileName(), dupTheoremStmtGroup.getSourceFileName());
    }

    private void preUpdateRelationalEdits() throws TheoremLoaderException {
        for (final TheoremStmtGroup t : this) {
            t.validateTheoremSrcStmtProofLabels(logicalSystem,
                theoremStmtGroupTbl);
            t.initializeMustAppend();

        }
    }
}
