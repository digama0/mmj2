//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MinDistinctVariablesStmt.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

/**
 * General object representing a Distinct Variables statement on a
 * MinProofWorksheet.
 * <p>
 * A Distinct Variables statement consists of a "$d" in column one of the first
 * line of the statemenet followed by a list of variables. Model A provides for
 * typesetting of the variables as well as maintaining indentation prior to the
 * first variable.
 */
public class MinDistinctVariablesStmt extends MinProofWorkStmt {

    /**
     * Standard MinDistinctVariablesStmt constructor.
     * 
     * @param w {@code MinProofWorksheet} of which this statement is a part.
     * @param slc Array of Array of String representing the lines and "chunks"
     *            making up the {@code MinProofWorkStmt}.
     */
    public MinDistinctVariablesStmt(final MinProofWorksheet w,
        final String[][] slc)
    {

        super(w, slc);
    }

    /**
     * Formats export data for the Distinct Variables statement according to the
     * {@code Model A} specifications and loads the data into a specified
     * buffer.
     * <p>
     * Model A model files for {@code MinDistinctVariablesStmt} objects are
     * "optional", meaning that if any of the model files are not found, the
     * export process is continues normally but Distinct Variables statements
     * are not output as part of the Proof Worksheet export.
     * <p>
     * Export of a Distinct Variables statement involves formatting two things:
     * the part of each line prior to the first variable symbol on that line,
     * and the typesetting and formatting of variable symbol.
     * <p>
     * We go to a lot of trouble to format the pre-variable part of the line
     * because it is important to preserve the indentation -- even though the
     * process of generating typeset symbols necessarily alters the indentations
     * of the individual symbols.
     * <p>
     * Note the quirky handling of {@code modelADistinctVar1X} and
     * {@code modelADistinctVar3X}. If one of these model files is empty then no
     * data is output at that location. This quirky feature will generally not
     * be used but is provided for maximum flexibility in creating export format
     * models.
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

        final String modelADistinctVar0 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_DISTINCTVAR0_NAME);
        if (modelADistinctVar0 == null)
            return;

        final String modelADistinctVar1X = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_DISTINCTVAR1X_NAME);
        if (modelADistinctVar1X == null)
            return;
        final String modelADistinctVar2 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_DISTINCTVAR2_NAME);
        if (modelADistinctVar2 == null)
            return;
        final String modelADistinctVar3X = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_DISTINCTVAR3X_NAME);
        if (modelADistinctVar3X == null)
            return;
        final String modelADistinctVar4 = gmffExporter
            .getOptionalModelFile(GMFFConstants.MODEL_A_DISTINCTVAR4_NAME);
        if (modelADistinctVar4 == null)
            return;

        // well all righty then...

        String[] lineChunks;
        String chunk;

        for (int i = 0; i < stmtLineChunks.length; i++) {

            lineChunks = stmtLineChunks[i];

            gmffExporter.appendModelFileText(exportBuffer, modelADistinctVar0);

            int startOfFormulaSymbols = -1;
            int j;
            for (j = 0; j < lineChunks.length; j++) {
                chunk = lineChunks[j];
                if (chunk.length() > 0 && !isChunkWhitespace(chunk)) {

                    startOfFormulaSymbols = j;
                    break;
                }
            }

            final String linePart1 = getCleanedLineString(i, 0, j);

            if (modelADistinctVar1X.length() > 0)
                gmffExporter.escapeAndAppendProofText(exportBuffer, linePart1);

            gmffExporter.appendModelFileText(exportBuffer, modelADistinctVar2);

            if (startOfFormulaSymbols != -1 && modelADistinctVar3X.length() > 0)
                typesetFormulaSymbols(gmffExporter, exportBuffer, lineChunks,
                    startOfFormulaSymbols);

            gmffExporter.appendModelFileText(exportBuffer, modelADistinctVar4);
        }
    }
}
