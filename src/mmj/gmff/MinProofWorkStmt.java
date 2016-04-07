//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MinProofWorkStmt.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.List;

import mmj.pa.PaConstants;

/**
 * General object representing a statement in a MinProofWorksheet.
 */
public abstract class MinProofWorkStmt {

    /**
     * Reference to parent MinProofWorksheet for convenience.
     */
    MinProofWorksheet w;

    /**
     * Array of Array of Strings, each of which is either Metamath whitespace or
     * a Metamath token.
     * <p>
     * The outer array represents a line of a Proof Worksheet and the inner
     * array the "chunks" which comprise a single line.
     */
    String[][] stmtLineChunks;

    /**
     * Standard MinProofWorkStmt constructor.
     *
     * @param w {@code MinProofWorksheet} of which this statement is a part.
     * @param slc Array of Array of String representing the lines and "chunks"
     *            making up the {@code MinProofWorkStmt}.
     */
    public MinProofWorkStmt(final MinProofWorksheet w, final String[][] slc) {

        this.w = w;
        stmtLineChunks = slc;
    }

    /**
     * {@code constructStmt} builds a {@code MinProofWorkStmt} from an List of
     * an List of String chunks which are either Metamath whitespace or Metamath
     * tokens.
     * <p>
     * The first line of a statement identifies the type of Proof Worksheet
     * statement. {@code constructStmt} validates those simply in order to be
     * able to determine which type of Proof Worksheet statement must be
     * constructed. In addition it checks to see if the starting portion--up to
     * theorem label-- is valid, and then it extracts the theorem label and
     * stores it in the input minProofWorksheet object. <b> Errors result in
     * accum'ing of error message(s) in the {@code Messages} as well as setting
     * of {@code structuralError} in the input {@code MinProofWorksheet} object.
     *
     * @param minProofWorksheet GMFF version of {@code ProofWorksheet}
     * @param lineList List of List of String chunks representing the text of a
     *            {@code ProofWorksheet} broken into Metamath whitespace and
     *            tokens grouped by line of the {@code ProofWorksheet}.
     * @return MinProofWorkStmt returns a specific type of
     *         {@code MinProofWorkStmt} according to the first token of the
     *         first line of the statement text.
     * @throws IllegalArgumentException if lineList is empty or if the first
     *             line has no {@code String} chunks.
     */
    public static MinProofWorkStmt constructStmt(
        final MinProofWorksheet minProofWorksheet,
        final List<List<String>> lineList)
    {

        final String[][] slc = new String[lineList.size()][];
        int row = 0;
        for (final List<String> line : lineList) {
            final String[] lc = new String[line.size()];
            int col = 0;
            for (final String s : line)
                lc[col++] = s;
            slc[row++] = lc;
        }

        if (slc.length == 0 || slc[0].length == 0) {
            minProofWorksheet.setStructuralErrors(true);
            throw new IllegalArgumentException(
                new GMFFException(GMFFConstants.ERRMSG_INVALID_LINE_LIST_ERROR,
                    minProofWorksheet.getTheoremLabel(),
                    minProofWorksheet.getLineCnt() - lineList.size() + 1));
        }

        final String startChunk = slc[0][0].toUpperCase();
        try {
            if (startChunk.length() > 0
                && Character.isDigit(startChunk.charAt(0))
                || startChunk.startsWith(PaConstants.QED_STEP_NBR_CAPS))
                return new MinDerivationStep(minProofWorksheet, slc);
            if (startChunk
                .startsWith(PaConstants.HYP_STEP_PREFIX.toUpperCase()))
                return new MinHypothesisStep(minProofWorksheet, slc);
            if (startChunk.equals(PaConstants.COMMENT_STMT_TOKEN_PREFIX))
                return new MinCommentStmt(minProofWorksheet, slc);
            if (startChunk.equals(PaConstants.HEADER_STMT_TOKEN))
                return new MinHeaderStmt(minProofWorksheet, slc);
            if (startChunk.equals(
                PaConstants.DISTINCT_VARIABLES_STMT_TOKEN.toUpperCase()))
                return new MinDistinctVariablesStmt(minProofWorksheet, slc);
            if (startChunk.equals(PaConstants.GENERATED_PROOF_STMT_TOKEN))
                return new MinGeneratedProofStmt(minProofWorksheet, slc);
            if (startChunk.equals(PaConstants.FOOTER_STMT_TOKEN))
                return new MinFooterStmt(minProofWorksheet, slc);
            minProofWorksheet.triggerBogusStmtLineStart(startChunk,
                minProofWorksheet.getLineCnt() - lineList.size() + 1);
        } catch (final IllegalArgumentException e) {
            minProofWorksheet.triggerConstructorError(e,
                minProofWorksheet.getLineCnt() - lineList.size() + 1);

        }
        return null;
    }

    /**
     * Formats export data for the Proof Worksheet statement according to the
     * {@code Model A} specifications and loads the data into a specified
     * buffer.
     * <p>
     * Export data is based on model files which vary according to the Model Id
     * and Proof Worksheet statement type. In general, model files contain
     * fragments of export data, such as html fragments -- but a model file may
     * also represent a placeholder for insertion of formatted text from the
     * Proof Worksheet. Model files are also categorized as Required or
     * Optional. Required model files, if not found when read by the program
     * trigger an error message and prevent export of the entire proof. Optional
     * model files if not found when read simply prevent export of part of the
     * proof -- for example, Comments -- and no error messages are generated.
     * Placeholder model files, whether Required or Optional, which exist but
     * contain no data indicate to the program that proof text is not to be
     * output at this location in the export text.
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
    public abstract void buildModelAExport(GMFFExporter gmffExporter,
        StringBuilder exportBuffer) throws GMFFException;

    /**
     * Utility function to typeset and reformat a portion of a {@code String}
     * array containing Metamath tokens representing at least a portion of a
     * formula.
     * <p>
     * There are two types of "chunks" to be typeset: whitespace chunks and
     * non-whitespace chunks. Non-whitespace chunks are presumed to be Metamath
     * ASCII symbols which are converted into export data (e.g. html) using the
     * Metamath $t comment statement data. Whitespace chunks are cleaned so that
     * they contain only spaces, and in addition, are shortened by one character
     * for readability.
     *
     * @param gmffExporter The {@code GMFFExporter} requesting the export data
     *            build.
     * @param exportBuffer The {@code StringBuilder} to which exported data is
     *            to be output.
     * @param lineChunks {@code String} array containing Metamath tokens or
     *            whitespace.
     * @param startOfFormulaSymbols index into {@code lineChunks} indicating the
     *            beginning of the typesetting operation (which proceeds through
     *            the end of the array -- effectively we are just bypassing an
     *            initial part of the array.)
     */
    public void typesetFormulaSymbols(final GMFFExporter gmffExporter,
        final StringBuilder exportBuffer, final String[] lineChunks,
        final int startOfFormulaSymbols)
    {

        String chunk;

        for (int t = startOfFormulaSymbols; t < lineChunks.length; t++) {

            chunk = lineChunks[t];
            if (chunk.length() == 0)
                continue;

            if (!isChunkWhitespace(chunk)) {
                gmffExporter.typesetAndAppendToken(exportBuffer, chunk,
                    w.getTheoremLabel());
                continue;
            }

            if (chunk.length() > 1) {
                final StringBuilder sb = new StringBuilder(chunk.length() - 1);
                for (int z = 1; z < chunk.length(); z++)
                    sb.append(" ");
                gmffExporter.escapeAndAppendProofText(exportBuffer,
                    sb.toString());
            }
        }
    }

    /**
     * Converts whitespace chunks in line of the {@code MinProofWorkStmt} to
     * spaces.
     * <p>
     * By the time a Proof Worksheet statement gets to this part of the code,
     * the only non-space whitespace character is Tab -- and on the Proof
     * Assistant GUI Tab characters equate to one space. To be really safe, all
     * whitespace chunk characters are converted to spaces.
     *
     * @param lineIndex index of line in {@code stmtLineChunks} to be "cleaned".
     * @return String containing all chunks in the line concatenated with
     *         whitespace chunks converted to spaces.
     */
    public String getCleanedLineString(final int lineIndex) {

        return getCleanedLineString(lineIndex, 0,
            stmtLineChunks[lineIndex].length);

    }

    /**
     * Converts whitespace chunks in part of a line of the
     * {@code MinProofWorkStmt} to spaces.
     * <p>
     * By the time a Proof Worksheet statement gets to this part of the code,
     * the only non-space whitespace character is Tab -- and on the Proof
     * Assistant GUI Tab characters equate to one space. To be really safe, all
     * whitespace chunk characters are converted to spaces.
     *
     * @param lineIndex index of line in {@code stmtLineChunks} to be "cleaned".
     * @param fromChunkIndex index of first chunk in the line to be "cleaned".
     * @param toChunkIndex exclusive endpoint (i.e. not thru) of chunk cleanup
     *            operation :-)
     * @return String containing all designated chunks in the line concatenated
     *         with whitespace chunks converted to spaces.
     */
    public String getCleanedLineString(final int lineIndex,
        final int fromChunkIndex, final int toChunkIndex)
    {

        final StringBuilder sb = new StringBuilder(100);
        final String[] lineChunk = stmtLineChunks[lineIndex];
        String chunk;
        for (int i = fromChunkIndex; i < toChunkIndex; i++) {
            chunk = lineChunk[i];
            if (chunk.length() > 0)
                if (isChunkWhitespace(chunk))
                    for (int j = 0; j < chunk.length(); j++)
                        sb.append(" ");
                else
                    sb.append(chunk);
        }

        return sb.toString();
    }

    protected boolean isChunkWhitespace(final String s) {
        for (int i = 0; i < s.length(); i++)
            if (!Character.isWhitespace(s.charAt(i)))
                return false;
        return true;
    }
}
