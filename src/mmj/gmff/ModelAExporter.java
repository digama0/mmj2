//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ModelAExporter.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

/**
 * {@code ModelAExporter} is an extension of {@code GMFFExporter} which
 * implements export of Proof Worksheets using Model A.
 */
public class ModelAExporter extends GMFFExporter {

    /**
     * The only constructor.
     *
     * @param gmffManager The {@code GMFFManager} object
     * @param gmffExportParms The {@code GMFFExportParms} for for this exporter.
     * @param gmffUserTextEscapes The {@code gmffUserTextEscapes} for this
     *            exporter.
     */
    public ModelAExporter(final GMFFManager gmffManager,
        final GMFFExportParms gmffExportParms,
        final GMFFUserTextEscapes gmffUserTextEscapes)
    {

        super(gmffManager, gmffExportParms, gmffUserTextEscapes);
    }

    /**
     * Exports a Proof Worksheet in Model A format and returns a confirmation
     * message showing the absolute path of the output file.
     * <p>
     * Model A uses the {@code MinProofWorksheet} class instead of the standard
     * mmj2 <code>ProofWorksheet.
     * <p>
     * If the loaded Proof Worksheet has {@code structuralErrors} the export is
     * not attempted (error message accumed in the {@code Messages} object. Note
     * that teh MinProofWorksheet class has extremely lenient standards about
     * {@code structuralErrors}.
     *
     * @param p {@code ProofWorksheetCache} object containing the proof to be
     *            exported.
     * @param appendFileName File Name minus File Type of append file if the
     *            regular file name is to be overridden (see documentation of
     *            appendFileNames in GMFFDoc\GMFFRunParms.txt).
     * @return Confirmation message of the successful export showing the
     *         absolute path of the output file -- or {@code null} if the export
     *         failed (error messages are accumed in the {@code Messages}
     *         object.)
     */
    @Override
    public GMFFException exportProofWorksheet(final ProofWorksheetCache p,
        final String appendFileName)
    {
        try {
            final MinProofWorksheet w = p
                .loadMinProofWorksheet(gmffManager.getMessages());

            if (!w.getStructuralErrors()) {
                if (w.getMinProofWorkStmtList().isEmpty())
                    throw new GMFFException(
                        GMFFConstants.ERRMSG_BUILD_EMPTY_OR_INVALID_WORKSHEET_ERROR,
                        w.getTheoremLabel());

                final StringBuilder exportText = buildModelAExportText(w);

                return outputToExportFile(exportText, appendFileName,
                    w.getTheoremLabel());
            }
        } catch (final GMFFException e) {
            gmffManager.getMessages().accumException(e);
        }
        return null;
    }

    private StringBuilder buildModelAExportText(final MinProofWorksheet w)
        throws GMFFException
    {

        final StringBuilder exportBuffer = new StringBuilder(
            GMFFConstants.EXPORT_BUFFER_DEFAULT_SIZE);

        appendMandatoryModelFile(exportBuffer, GMFFConstants.MODEL_A_FILE0_NAME,
            w.getTheoremLabel());

        for (final MinProofWorkStmt s : w.getMinProofWorkStmtList())
            s.buildModelAExport(this, exportBuffer);

        appendMandatoryModelFile(exportBuffer, GMFFConstants.MODEL_A_FILE2_NAME,
            w.getTheoremLabel());

        return exportBuffer;
    }

}
