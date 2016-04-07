//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofWorksheetParser.java  0.03 03/01/2008
 *
 * Feb-1-2008 Version 0.02:
 *     - add inputCursorPos argument to next() method.
 *
 * Mar-1-2008 Version 0.03:
 *     - add stepRequest argument to next() method.
 */

package mmj.pa;

import java.io.*;

import mmj.lang.LogicalSystem;
import mmj.lang.Messages;
import mmj.mmio.MMIOException;
import mmj.mmio.Tokenizer;
import mmj.verify.Grammar;

/**
 * ProofWorksheetParser handles the details of iteration through 1 or more
 * ProofWorksheets input via String, File or Reader.
 * <p>
 * The big shortcoming that it has right now, which may not be big depending on
 * your plans, is that an input ProofWorksheet with structural errors or other
 * severe errors, throws an exception and leaves the input stream stuck in the
 * middle of the worksheet tokens. An enhancement might be to add a routine to
 * chunk through the input stream and bypass tokens until a proof worksheet
 * "Footer" is found, and then grab the *next* token, which could be used to
 * initiate and parse the following ProofWorksheet. However, at this time,
 * batches of ProofWorksheets are used only in testing, so no energy has been
 * expended in bypassing an errored ProofWorksheet's remaining tokens.
 */
public class ProofWorksheetParser implements Closeable {

    // global variables stored here for mere convenience
    private final Reader proofTextReader;
    private final ProofAsstPreferences proofAsstPreferences;
    private final LogicalSystem logicalSystem;
    private final Grammar grammar;
    private final Messages messages;
    private final MacroManager macroManager;

    private String nextToken;
    private final Tokenizer proofTextTokenizer;

    /**
     * Constructor.
     *
     * @param proofTextReader Reader of ProofWorksheet tokens
     * @param proofTextSource Comment for debugging/testing
     * @param proofAsstPreferences variable settings
     * @param logicalSystem the loaded Metamath data
     * @param grammar the mmj.verify.Grammar object
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @param macroManager the mmj.pa.MacroManager object
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     */
    public ProofWorksheetParser(final Reader proofTextReader,
        final String proofTextSource,
        final ProofAsstPreferences proofAsstPreferences,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final Messages messages, final MacroManager macroManager)
            throws IOException, MMIOException
    {

        this.proofTextReader = proofTextReader;
        this.proofAsstPreferences = proofAsstPreferences;
        this.logicalSystem = logicalSystem;
        this.grammar = grammar;
        this.messages = messages;
        this.macroManager = macroManager;

        proofTextTokenizer = new Tokenizer(proofTextReader, proofTextSource);

        final StringBuilder strBuf = new StringBuilder();
        final int offset = 0;
        proofTextTokenizer.getToken(strBuf, offset);
        nextToken = strBuf.toString();
    }

    /**
     * Constructor.
     *
     * @param proofText String containing ProofWorksheet tokens
     * @param proofTextSource Comment for debugging/testing
     * @param proofAsstPreferences variable settings
     * @param logicalSystem the loaded Metamath data
     * @param grammar the mmj.verify.Grammar object
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @param macroManager the mmj.pa.MacroManager object
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     */
    public ProofWorksheetParser(final String proofText,
        final String proofTextSource,
        final ProofAsstPreferences proofAsstPreferences,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final Messages messages, final MacroManager macroManager)
            throws IOException, MMIOException
    {
        this(new StringReader(proofText), proofTextSource, proofAsstPreferences,
            logicalSystem, grammar, messages, macroManager);
    }

    /**
     * Constructor.
     *
     * @param proofFile File object specifying ProofWorksheet token file for
     *            input.
     * @param proofTextSource Comment for debugging/testing
     * @param proofAsstPreferences variable settings
     * @param logicalSystem the loaded Metamath data
     * @param grammar the mmj.verify.Grammar object
     * @param messages the mmj.lang.Messages object used to store error and
     *            informational messages.
     * @param macroManager the mmj.pa.MacroManager object
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     */
    public ProofWorksheetParser(final File proofFile,
        final String proofTextSource,
        final ProofAsstPreferences proofAsstPreferences,
        final LogicalSystem logicalSystem, final Grammar grammar,
        final Messages messages, final MacroManager macroManager)
            throws IOException, MMIOException
    {

        this(new BufferedReader(new FileReader(proofFile)), proofTextSource,
            proofAsstPreferences, logicalSystem, grammar, messages,
            macroManager);
    }

    @Override
    public void close() throws IOException {
        if (proofTextReader != null)
            proofTextReader.close();
    }

    /**
     * Checks to see if another ProofWorksheet is available.
     *
     * @return true if at least one more ProofWorksheet token exists to be
     *         processed.
     */
    public boolean hasNext() {
        if (nextToken.length() > 0)
            return true;
        else
            return false;
    }

    /**
     * Returns the next ProofWorksheet from the input source for situations when
     * input cursor position not available.
     *
     * @return ProofWorksheet or throws an exception!
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    public ProofWorksheet next()
        throws IOException, MMIOException, ProofAsstException
    {
        return next(-1, null);
    }

    /**
     * Returns the next ProofWorksheet from the input source.
     *
     * @param inputCursorPos offset plus one of Caret in Proof TextArea;
     * @param stepRequest may be null, or StepSelector Search or Choice request
     *            and will be loaded into the ProofWorksheet.
     * @return ProofWorksheet or throws an exception!
     * @throws IOException if an error occurred
     * @throws MMIOException if an error occurred
     * @throws ProofAsstException if an error occurred
     */
    public ProofWorksheet next(final int inputCursorPos,
        final StepRequest stepRequest)
            throws IOException, MMIOException, ProofAsstException
    {

        final ProofWorksheet proofWorksheet = new ProofWorksheet(
            proofTextTokenizer, proofAsstPreferences, logicalSystem, grammar,
            messages, macroManager);

        /*
         * loadWorksheet() returns next token *after* this
         * worksheet, if any -- for use when processing a
         */
        nextToken = proofWorksheet.loadWorksheet(nextToken, inputCursorPos,
            stepRequest);

        return proofWorksheet;
    }

}
