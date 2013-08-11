//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MinCommentStmt.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

/**
 * General object representing a Comment statement on a
 * {@code MinProofWorksheet}.
 * <p>
 * Proof Worksheet Comment statements consist of just an "*" in column one of
 * the first line of the statement followed by text characters -- which must be
 * valid Metamath characters but are not otherwise validated.
 * <p>
 * GMFF typesets nothing on the Comment statement, everything is output as just
 * "escaped" text.
 */
public class MinCommentStmt extends MinProofWorkStmt {

    /**
     * Standard MinCommentStmt constructor.
     * 
     * @param w the {@code MinProofworksheet} object which contains the proof
     *            step.
     * @param slc Array of lines each comprised of an Array of {@code String}s
     *            called "chunks", which may be either Metamath whitespace or
     *            Metamath tokens. Hence the acronym "slc" refers to Statement
     *            Line Chunks.
     */
    public MinCommentStmt(final MinProofWorksheet w, final String[][] slc) {

        super(w, slc);
    }

    /**
     * Formats export data for the Comment statement according to the
     * {@code Model A} specifications and loads the data into a specified
     * buffer.
     * <p>
     * Model A model files for {@code MinCommentStmt} objects are "optional",
     * meaning that if any of the model files are not found, the export process
     * is continues normally but Generated Proof statements are not output as
     * part of the Proof Worksheet export.
     * <p>
     * Note the quirky handling of {@code modelAGenComment1X}. If this model
     * file is empty then no data is output at that location. This quirky
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

        final String modelAComment0 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_COMMENT0_NAME);
        if (modelAComment0 == null)
            return;

        final String modelAComment1X = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_COMMENT1X_NAME);
        if (modelAComment1X == null)
            return;
        final String modelAComment2 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_COMMENT2_NAME);
        if (modelAComment2 == null)
            return;

        // well all righty then...

        for (int i = 0; i < stmtLineChunks.length; i++) {

            gmffExporter.appendModelFileText(exportBuffer, modelAComment0);

            if (modelAComment1X.length() > 0)
                gmffExporter.escapeAndAppendProofText(exportBuffer,
                    getCleanedLineString(i));

            gmffExporter.appendModelFileText(exportBuffer, modelAComment2);
        }
    }
}
