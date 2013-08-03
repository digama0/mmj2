//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  ProofWorksheetParser.java  0.03 03/01/2008
 *
 *  Feb-1-2008 Version 0.02:
 *      - add inputCursorPos argument to next() method.
 *
 *  Mar-1-2008 Version 0.03:
 *      - add stepRequest argument to next() method.
 */

package mmj.pa;

import  java.io.File;
import  java.io.Reader;
import  java.io.StringReader;
import  java.io.FileReader;
import  java.io.BufferedReader;
import  java.io.IOException;
import  mmj.lang.*;
import  mmj.verify.*;
import  mmj.mmio.*;

/**
 *  ProofWorksheetParser handles the details of
 *  iteration through 1 or more ProofWorksheets
 *  input via String, File or Reader.
 *  <p>
 *  The big shortcoming that it has right now,
 *  which may not be big depending on your
 *  plans, is that an input ProofWorksheet
 *  with structural errors or other severe
 *  errors, throws an exception and leaves the
 *  input stream stuck in the middle of the
 *  worksheet tokens. An enhancement might be
 *  to add a routine to chunk through the input
 *  stream and bypass tokens until a proof
 *  worksheet "Footer" is found, and then grab
 *  the *next* token, which could be used to
 *  initiate and parse the following ProofWorksheet.
 *  However, at this time, batches of ProofWorksheets
 *  are used only in testing, so no energy has
 *  been expended in bypassing an errored
 *  ProofWorksheet's remaining tokens.
 */
public class ProofWorksheetParser {

    //global variables stored here for mere convenience
    private     Reader               proofTextReader;
    private     ProofAsstPreferences proofAsstPreferences;
    private     LogicalSystem        logicalSystem;
    private     Grammar              grammar;
    private     Messages             messages;

    private     String               nextToken;
    private     Tokenizer            proofTextTokenizer;

    /**
     *  Constructor.
     *
     *  @param proofTextReader Reader of ProofWorksheet tokens
     *  @param proofTextSource Comment for debugging/testing
     *  @param proofAsstPreferences variable settings
     *  @param logicalSystem the loaded Metamath data
     *  @param grammar the mmj.verify.Grammar object
     *  @param messages the mmj.lang.Messages object used to store
     *                  error and informational messages.
     */
    public ProofWorksheetParser(
                Reader               proofTextReader,
                String               proofTextSource,
                ProofAsstPreferences proofAsstPreferences,
                LogicalSystem        logicalSystem,
                Grammar              grammar,
                Messages             messages)
                            throws IOException,
                                   MMIOError {

        this.proofTextReader      = proofTextReader;
        this.proofAsstPreferences = proofAsstPreferences;
        this.logicalSystem        = logicalSystem;
        this.grammar              = grammar;
        this.messages             = messages;

        proofTextTokenizer        = new Tokenizer(proofTextReader,
                                                  proofTextSource);

        StringBuffer strBuf       = new StringBuffer();
        int          offset       = 0;
        proofTextTokenizer.getToken(strBuf,
                                    offset);
        nextToken                 = new String(strBuf);
    }

    /**
     *  Constructor.
     *
     *  @param proofText String containing ProofWorksheet tokens
     *  @param proofTextSource Comment for debugging/testing
     *  @param proofAsstPreferences variable settings
     *  @param logicalSystem the loaded Metamath data
     *  @param grammar the mmj.verify.Grammar object
     *  @param messages the mmj.lang.Messages object used to store
     *                  error and informational messages.
     */
    public ProofWorksheetParser(
                String               proofText,
                String               proofTextSource,
                ProofAsstPreferences proofAsstPreferences,
                LogicalSystem        logicalSystem,
                Grammar              grammar,
                Messages             messages)
                            throws IOException,
                                   MMIOError {
        this(new StringReader(proofText),
             proofTextSource,
             proofAsstPreferences,
             logicalSystem,
             grammar,
             messages);
    }

    /**
     *  Constructor.
     *
     *  @param proofFile File object specifying ProofWorksheet token
     *                   file for input.
     *  @param proofTextSource Comment for debugging/testing
     *  @param proofAsstPreferences variable settings
     *  @param logicalSystem the loaded Metamath data
     *  @param grammar the mmj.verify.Grammar object
     *  @param messages the mmj.lang.Messages object used to store
     *                  error and informational messages.
     */
     public ProofWorksheetParser(
                File                 proofFile,
                String               proofTextSource,
                ProofAsstPreferences proofAsstPreferences,
                LogicalSystem        logicalSystem,
                Grammar              grammar,
                Messages             messages)
                            throws IOException,
                                   MMIOError {

        this(new BufferedReader(new FileReader(proofFile)),
             proofTextSource,
             proofAsstPreferences,
             logicalSystem,
             grammar,
             messages);
    }

    /**
     *  Closes the ProofWorksheet token reader.
     *  <p>
     *  Use this when finished, it doesn't get called
     *  automatically!
     */
    public void closeReader() {
        if (proofTextReader != null) {
            try {
                proofTextReader.close();
            }
            catch (IOException e) {
            }
        }
    }

    /**
     *  Checks to see if another ProofWorksheet is available.
     *
     *  @return      true if at least one more ProofWorksheet token
     *           exists to be processed.
     */
    public boolean hasNext() {
        if (nextToken.length() > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *  Returns the next ProofWorksheet from the input source
     *  for situations when input cursor position not available.
     *
     *  @return      ProofWorksheet or throws an exception!
     */
    public ProofWorksheet next()
                    throws IOException,
                           MMIOError,
                           ProofAsstException {
        return next(-1,
                    null);
    }

    /**
     *  Returns the next ProofWorksheet from the input source.
     *
     *  @param inputCursorPos offset plus one of Caret in
     *         Proof TextArea;
     *
     *  @param stepRequest may be null, or StepSelector Search or
     *                     Choice request and will be loaded into
     *                     the ProofWorksheet.
     *
     *  @return      ProofWorksheet or throws an exception!
     */
    public ProofWorksheet next(int         inputCursorPos,
                               StepRequest stepRequest)
                    throws IOException,
                           MMIOError,
                           ProofAsstException {

        ProofWorksheet proofWorksheet
                                  =
            new ProofWorksheet(proofTextTokenizer,
                               proofAsstPreferences,
                               logicalSystem,
                               grammar,
                               messages);

        /*
         * loadWorksheet() returns next token *after* this
         * worksheet, if any -- for use when processing a
         */
        nextToken                 =
            proofWorksheet.loadWorksheet(nextToken,
                                         inputCursorPos,
                                         stepRequest);

        return proofWorksheet;
    }

}

