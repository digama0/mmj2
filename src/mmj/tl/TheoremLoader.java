//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TheoremLoader.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     - new.
 */

package mmj.tl;

import java.util.List;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.pa.ProofWorksheet;

/**
 * The Theorem Loader facility's main control module.
 * <p>
 * Note: this class is primarily an entry point and convenience for Theorem
 * Loader users (batch or GUI.)
 */
public class TheoremLoader {

    private final TlPreferences tlPreferences;

    /**
     * Main constructor for TheoremLoader.
     *
     * @param tlPreferences TlPreferences object.
     */
    public TheoremLoader(final TlPreferences tlPreferences) {
        this.tlPreferences = tlPreferences;
    }

    /**
     * Unifies mmj2 Proof Text area and stores the theorem in the Logical System
     * and MMT Folder.
     *
     * @param proofWorksheetText text area holding an mmj2 Proof Worksheet.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param proofAsst ProofAsst object.
     * @param inputProofWorksheetFileName String used for error reporting.
     * @return unified ProofWorksheet object
     * @throws TheoremLoaderException if data errors encountered, including the
     *             case where the ProofWorksheet cannot be unified.
     */
    public ProofWorksheet unifyPlusStoreInLogSysAndMMTFolder(
        final String proofWorksheetText, final LogicalSystem logicalSystem,
        final Messages messages, final ProofAsst proofAsst,
        final String inputProofWorksheetFileName)
            throws TheoremLoaderException
    {

        final ProofWorksheet proofWorksheet = getUnifiedProofWorksheet(
            proofWorksheetText, proofAsst, inputProofWorksheetFileName);

        storeInLogSysAndMMTFolder(proofWorksheet, logicalSystem, messages,
            proofAsst);

        return proofWorksheet;
    }

    /**
     * Unifies mmj2 Proof Text area and stores the theorem in the MMT Folder.
     *
     * @param proofWorksheetText text area holding an mmj2 Proof Worksheet.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param proofAsst ProofAsst object.
     * @param inputProofWorksheetFileName String used for error reporting.
     * @return unified ProofWorksheet object
     * @throws TheoremLoaderException if data errors encountered, including the
     *             case where the ProofWorksheet cannot be unified.
     */
    public ProofWorksheet unifyPlusStoreInMMTFolder(
        final String proofWorksheetText, final LogicalSystem logicalSystem,
        final Messages messages, final ProofAsst proofAsst,
        final String inputProofWorksheetFileName)
            throws TheoremLoaderException
    {

        final ProofWorksheet proofWorksheet = getUnifiedProofWorksheet(
            proofWorksheetText, proofAsst, inputProofWorksheetFileName);

        storeInMMTFolder(proofWorksheet, logicalSystem, messages, proofAsst);

        return proofWorksheet;
    }

    /**
     * Stores a unified ProofWorksheet in the Logical System and the MMT Folder.
     *
     * @param proofWorksheet ProofWorksheet object already successfully unified.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param proofAsst ProofAsst object.
     * @throws TheoremLoaderException if data errors encountered, including the
     *             case where the ProofWorksheet is not already unified.
     */
    public void storeInLogSysAndMMTFolder(final ProofWorksheet proofWorksheet,
        final LogicalSystem logicalSystem, final Messages messages,
        final ProofAsst proofAsst) throws TheoremLoaderException
    {

        storeInMMTFolder(proofWorksheet, logicalSystem, messages, proofAsst);

        loadTheoremsFromMMTFolder(proofWorksheet.getTheoremLabel(),
            logicalSystem, messages);
    }

    /**
     * Stores a unified ProofWorksheet in the MMT Folder.
     *
     * @param proofWorksheet ProofWorksheet object already successfully unified.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param proofAsst ProofAsst object.
     * @throws TheoremLoaderException if data errors encountered, including the
     *             case where the ProofWorksheet is not already unified.
     */
    public void storeInMMTFolder(final ProofWorksheet proofWorksheet,
        final LogicalSystem logicalSystem, final Messages messages,
        final ProofAsst proofAsst) throws TheoremLoaderException
    {

        if (proofWorksheet.getGeneratedProofStmt() == null)
            throw ProofWorksheet.addLabelContext(proofWorksheet,
                new TheoremLoaderException(
                    TlConstants.ERRMSG_EXPORT_FORMAT_PROOF_WORKSHEET_ERR));

        final MMTTheoremExportFormatter mmtTheoremExportFormatter = new MMTTheoremExportFormatter(
            tlPreferences);

        final List<StringBuilder> mmtTheoremLines = mmtTheoremExportFormatter
            .buildStringBuilderLineList(proofWorksheet);

        tlPreferences.mmtFolder.get().storeMMTTheoremFile(
            proofWorksheet.getTheoremLabel(), mmtTheoremLines);

    }

    /**
     * Loads all MMT Theorems in the MMT Folder into the Logical System.
     * <p>
     * Note: the current MMT Folder is obtained from the TlPreferences object.
     *
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @throws TheoremLoaderException if data errors encountered.
     */
    public void loadTheoremsFromMMTFolder(final LogicalSystem logicalSystem,
        final Messages messages) throws TheoremLoaderException
    {

        final MMTTheoremSet mmtTheoremSet = tlPreferences.mmtFolder.get()
            .constructMMTTheoremSet(logicalSystem, messages, tlPreferences);

        mmtTheoremSet.updateLogicalSystem();
    }

    /**
     * Loads one theorem from the MMT Folder into the Logical System.
     * <p>
     * Note: the input theoremLabel is used to construct the file name to be
     * read from the MMT Folder.
     *
     * @param theoremLabel label of the theorem to be loaded.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @throws TheoremLoaderException if data errors encountered.
     */
    public void loadTheoremsFromMMTFolder(final String theoremLabel,
        final LogicalSystem logicalSystem, final Messages messages)
            throws TheoremLoaderException
    {

        final MMTTheoremSet mmtTheoremSet = tlPreferences.mmtFolder.get()
            .constructMMTTheoremSet(theoremLabel, logicalSystem, messages,
                tlPreferences);

        mmtTheoremSet.updateLogicalSystem();
    }

    /**
     * Reads a theorem from the Logical System and writes it to the MMT Folder.
     * System.
     * <p>
     * Note: the theorem Label is used to construct the file name to be written
     * to the MMT Folder.
     *
     * @param theorem Theorem to be written to the MMT Folder.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @throws TheoremLoaderException if data errors encountered.
     */
    public void extractTheoremToMMTFolder(final Theorem theorem,
        final LogicalSystem logicalSystem, final Messages messages)
            throws TheoremLoaderException
    {

        final MMTTheoremExportFormatter mmtTheoremExportFormatter = new MMTTheoremExportFormatter(
            tlPreferences);

        final List<StringBuilder> mmtTheoremLines = mmtTheoremExportFormatter
            .buildStringBuilderLineList(theorem);

        tlPreferences.mmtFolder.get().storeMMTTheoremFile(theorem.getLabel(),
            mmtTheoremLines);
    }

    /**
     * Unifies an mmj2 Proof Text area.
     *
     * @param proofWorksheetText text of a ProofWorksheet.
     * @param proofAsst ProofAsst object
     * @param filenameOrDataSourceId text for diagnostics
     * @return ProofWorksheet if unified successfully.
     * @throws TheoremLoaderException if there is an error in the proof.
     */
    public ProofWorksheet getUnifiedProofWorksheet(
        final String proofWorksheetText, final ProofAsst proofAsst,
        final String filenameOrDataSourceId) throws TheoremLoaderException
    {

        final ProofWorksheet proofWorksheet = proofAsst.unify(false, // renumReq
            false, // convert work vars
            proofWorksheetText, null, // preprocessRequest
            null, // stepRequest
            null, // no TL request
            -1, // inputCursorPos
            true); // printOkMessages

        if (proofWorksheet.getGeneratedProofStmt() == null)
            throw ProofWorksheet.addLabelContext(proofWorksheet,
                new TheoremLoaderException(
                    TlConstants.ERRMSG_THEOREM_LOADER_TEXT_UNIFY_ERROR,
                    filenameOrDataSourceId,
                    proofWorksheet.getOutputMessageText()));

        return proofWorksheet;
    }

    public TlPreferences getTlPreferences() {
        return tlPreferences;
    }

}
