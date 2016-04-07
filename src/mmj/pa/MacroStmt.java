//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MacroStmt.java  0.01 11/15/2015
 * Version 0.01: New.
 *
 * MacroStmt represents an inline macro invocation.  By default a macro line is
 * preserved like a comment, causing it to be invoked on every parsing run.
 * <p>
 * Macros begin with "$m" in column 1, and are executed as soon as they are
 * parsed (they can post listeners in order to execute logic later in a later
 * processing stage).
 * <p>
 */

package mmj.pa;

import java.io.*;

import mmj.mmio.MMIOException;

public class MacroStmt extends ProofWorkStmt {

    String macroName;

    /**
     * Default Constructor.
     *
     * @param w the owner ProofWorksheet
     */
    public MacroStmt(final ProofWorksheet w) {
        super(w);
    }

    /**
     * Creates a MacroStmt using an input String.
     * <p>
     * The input String is parsed into lines so that lineCnt can be incremented,
     * and newline characters are re-inserted into the text.
     * <p>
     * The macro text is prefixed by "$m " on the first output line and "  " on
     * subsequent lines.
     *
     * @param w the owner ProofWorksheet
     * @param name The macro name
     * @param args The string of macro arguments, with whitespace preserved
     */
    public MacroStmt(final ProofWorksheet w, final String name,
        final String args)
    {
        super(w);

        macroName = name;

        String linePrefix = PaConstants.MACRO_STMT_TOKEN + " " + name + " ";
        stmtText = new StringBuilder(linePrefix.length() + args.length());

        final String secondLinePrefix = "   ";

        try {
            final LineNumberReader r = new LineNumberReader(
                new StringReader(args));

            String line;
            while ((line = r.readLine()) != null) {
                stmtText.append(linePrefix).append(line.trim()).append("\n");
                lineCnt++;
                linePrefix = secondLinePrefix;
            }
        } catch (final IOException e) {}

        if (lineCnt == 1)
            stmtText.append("\n");
        else
            lineCnt--;
    }

    @Override
    public boolean stmtIsIncomplete() {
        return false;
    }

    /**
     * Function used for cursor positioning.
     *
     * @param fieldId value identify ProofWorkStmt field for cursor positioning,
     *            as defined in PaConstants.FIELD_ID_*.
     * @return column of input fieldId or default value of 1 if there is an
     *         error.
     */
    @Override
    public int computeFieldIdCol(final int fieldId) {
        return 1;
    }

    /**
     * Reformats Derivation Step using TMFF.
     */
    @Override
    public void tmffReformat() {}

    @Override
    public String load(final String firstToken)
        throws IOException, MMIOException, ProofAsstException
    {
        final int currLineNbr = (int)w.proofTextTokenizer.getCurrentLineNbr();

        stmtText = new StringBuilder();

        macroName = loadStmtTextGetRequiredToken(firstToken);

        final String nextT = loadAllStmtTextGetNextStmt(macroName);

        updateLineCntUsingTokenizer(currLineNbr, nextT);
        return nextT;
    }
}
