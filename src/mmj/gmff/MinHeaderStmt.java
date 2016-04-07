//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MinHeaderStmt.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import mmj.mmio.Statementizer;
import mmj.pa.PaConstants;

/**
 * Object representing the Header statement on a {@code MinProofWorksheet}.
 * <p>
 * The Header statement is vitally important even on a {@code MinProofWorksheet}
 * , which is validated only a minimal amount. The Header statement provides the
 * proof's theorem label which is used to construct the export file name. So, if
 * the Header is invalid the entire Proof Worksheet is updated with
 * {@code structuralErrors = true}.
 */
public class MinHeaderStmt extends MinProofWorkStmt {

    /**
     * Standard MinHeaderStmt constructor.
     * <p>
     * The first line of the header is validated minimally. <br>
     * <ul>
     * <li>Starting in column 1 the header must equal
     *
     * <pre>
     * $( <MM> <PROOF_ASST> THEOREM=
     * </pre>
     *
     * <li>The theorem label must not equal "" or blank, or "?".
     * <li>The theorem label must consist of only characters which are valid for
     * Metamath labels.
     * <li>The theorem label must not be on the Metamath prohibited label list.
     * </ul>
     * <p>
     * The validation/load code was extracted from
     * {@code mmj.pa.HeaderStmt.load()} and
     * {@code mmj.pa.HeaderStmt.validateTheoremLabel()}.
     *
     * @param w the {@code MinProofworksheet} object which contains the proof
     *            step.
     * @param slc Array of lines each comprised of an Array of {@code String}s
     *            called "chunks", which may be either Metamath whitespace or
     *            Metamath tokens. Hence the acronym "slc" refers to Statement
     *            Line Chunks.
     */
    public MinHeaderStmt(final MinProofWorksheet w, final String[][] slc) {

        super(w, slc);

        validateHeader();
    }

    /**
     * Formats export data for the Header statement according to the
     * {@code Model A} specifications and loads the data into a specified
     * buffer.
     * <p>
     * Model A model files for {@code MinHeaderStmt} objects are "optional",
     * meaning that if any of the model files are not found, the export process
     * is continues normally but Header statements are not output as part of the
     * Proof Worksheet export.
     * <p>
     * Note the quirky handling of {@code modelAHeader1X},
     * {@code modelAHeader3X}and {@code modelAHeader5X}. If one of these model
     * files is empty then no data is output at that location. This quirky
     * feature will generally not be used but is provided for maximum
     * flexibility in creating export format models.
     * <p>
     * Additional information may be found \GMFFDoc\GMFFModels.txt.
     *
     * @param gmffExporter The {@code GMFFExporter} requesting the export data
     *            build.
     * @param exportBuffer The {@code StringBuilder} to which exported data is
     *            to be output.
     * @throws GMFFException if errors are encountered during the export
     *             process.
     */
    @Override
    public void buildModelAExport(final GMFFExporter gmffExporter,
        final StringBuilder exportBuffer) throws GMFFException
    {

        final String modelAHeader0 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER0_NAME);
        if (modelAHeader0 == null)
            return;

        final String modelAHeader1X = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER1X_NAME);
        if (modelAHeader1X == null)
            return;
        final String modelAHeader2 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER2_NAME);
        if (modelAHeader2 == null)
            return;
        final String modelAHeader3X = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER3X_NAME);
        if (modelAHeader3X == null)
            return;
        final String modelAHeader4 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER4_NAME);
        if (modelAHeader4 == null)
            return;
        final String modelAHeader5X = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER5X_NAME);
        if (modelAHeader5X == null)
            return;
        final String modelAHeader6 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER6_NAME);
        if (modelAHeader6 == null)
            return;
        final String modelAHeader7 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_HEADER7_NAME);
        if (modelAHeader7 == null)
            return;

        // well all righty then...

        gmffExporter.appendModelFileText(exportBuffer, modelAHeader0);

        if (modelAHeader1X.length() > 0)
            gmffExporter.escapeAndAppendProofText(exportBuffer,
                w.getTheoremLabel());

        gmffExporter.appendModelFileText(exportBuffer, modelAHeader2);

        if (modelAHeader3X.length() > 0)
            gmffExporter.escapeAndAppendProofText(exportBuffer,
                w.getTheoremLabel());

        gmffExporter.appendModelFileText(exportBuffer, modelAHeader4);

        if (modelAHeader5X.length() > 0)
            gmffExporter.escapeAndAppendProofText(exportBuffer,
                w.getTheoremLabel());

        gmffExporter.appendModelFileText(exportBuffer, modelAHeader6);

        gmffExporter.appendModelFileText(exportBuffer, modelAHeader7);
    }

    /**
     * Performs the minimal set of validation checks to ensure that the theorem
     * label can be used to generate a file name by the GMFF Extract process.
     * <p>
     * <ul>
     * <li>Starting in column 1 the header must equal
     *
     * <pre>
     * $( <MM> <PROOF_ASST> THEOREM=
     * </pre>
     *
     * <li>The theorem label must not equal "" or blank, or "?".
     * <li>The theorem label must consist of only characters which are valid for
     * Metamath labels.
     * <li>The theorem label must not be on the Metamath prohibited label list.
     * </ul>
     * <p>
     * The validation/load code was extracted from
     * <code>mmj.pa.HeaderStmt.load()
     * </pre>
     * and <code>mmj.pa.HeaderStmt.validateTheoremLabel()
     * </pre>
     * . <br>
     *
     * @return true if theorem label valid, otherwise false.
     */
    public boolean isTheoremLabelMinimallyValid() {
        final String theoremLabel = w.getTheoremLabel();

        return theoremLabel != null && !theoremLabel.isEmpty()
            && !theoremLabel.equals(PaConstants.DEFAULT_STMT_LABEL)
            && Statementizer.isValidLabel(theoremLabel);
    }

    /**
     * Checks to make sure that the Header contains the proper literals in the
     * right sequence and that the theoremLabel is syntactically valid and is
     * not on the Metamath Prohibited Label list.
     * <p>
     * If an error is found, the {@code minProofWorksheet}
     * <codetriggerInvalidTheoremLabel()</code> is called to mark the Proof
     * Worksheet with {@code structuralErrors} and to accumulate an error
     * message.
     */
    private void validateHeader() {
        try {
            if (stmtLineChunks[0][PaConstants.HEADER_MM_TOKEN_CHUNK_INDEX]
                .equals(PaConstants.HEADER_MM_TOKEN)

                && stmtLineChunks[0][PaConstants.HEADER_PROOF_ASST_TOKEN_CHUNK_INDEX]
                    .equals(PaConstants.HEADER_PROOF_ASST_TOKEN)

                && stmtLineChunks[0][PaConstants.HEADER_THEOREM_EQUAL_PREFIX_CHUNK_INDEX]
                    .startsWith(PaConstants.HEADER_THEOREM_EQUAL_PREFIX)

                && stmtLineChunks[0][PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX_CHUNK_INDEX]
                    .startsWith(PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX))
            {

                w.setTheoremLabel(
                    stmtLineChunks[0][PaConstants.HEADER_THEOREM_EQUAL_PREFIX_CHUNK_INDEX]
                        .substring(
                            PaConstants.HEADER_THEOREM_EQUAL_PREFIX.length())
                        .trim());
                w.setLocAfter(
                    stmtLineChunks[0][PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX_CHUNK_INDEX]
                        .substring(
                            PaConstants.HEADER_LOC_AFTER_EQUAL_PREFIX.length())
                        .trim());

                if (!isTheoremLabelMinimallyValid())
                    w.triggerInvalidTheoremLabel(w.getTheoremLabel(),
                        w.getLineCnt() - stmtLineChunks.length + 1);
                return;
            }
        } catch (final IndexOutOfBoundsException e) {}

        w.triggerInvalidHeaderConstants(
            w.getLineCnt() - stmtLineChunks.length + 1);
    }
}
