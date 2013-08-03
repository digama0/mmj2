//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  IncludeFile.java  0.03 11/01/2011
 *
 *  Nov-01-2011 - Version 0.03
 *      -->Modified for mmj2 Paths Enhancement
 */
package mmj.mmio;
import java.io.*;
import java.util.*;

/**
 * Nitty-gritty IncludeFile work switching Tokenizers and
 * keeping a list, checking it twice.
 * <p>
 * Strategy overview:
 * The top level MetaMath source file Tokenizer is never closed
 * as include files are processed, but a nested include file
 * statement will close the outer include file, process the
 * inner and then "restart" the outer include file where it
 * left off. This is slightly crude but is a fair compromise
 * since nested include files are unlikely AND there is
 * probably an extra file handle hanging around, so we don't
 * need to close the top level MetaMath source file.
 */

public class IncludeFile {

    private     String      fileName                = null;
    private     File        fileObject              = null;

    private     long        restartCharsToBypass    = 0;
    private     Tokenizer   tokenizer               = null;
    private     Tokenizer   prevTokenizer           = null;

    /**
     * <p>
     * Switches Statementizer processing to an include file
     * after recording restart information of the previous
     * include file.
     * <p>
     * Note that the first
     * include file entry in <code>fileArrayList</code>
     * stores the <code>Tokenizer</code> of the top level
     * MetaMath source file read; this is used to restore
     * Statementizer processing where it left off after
     * end of file on the include file.
     * <p>
     *
     * @param fileList  ArrayList used to store information
     *        about IncludeFiles. Initialize to empty list
     *        at start of processing:
     *        <code> fileList = new ArrayList();</code>.
     *        and that's all that is necessary.
     *
     * @param f  File object, previously constructed, that
     *        will be used to create a Reader for the new
     *        include file.
     *
     * @param fileName  include string file name from
     *        Metamath file '$[ xx.mm $]' statement.
     *
     * @param statementizer  the Statementizer presently
     *        in use; used here to switch tokenizers.
     *
     * @return returns Tokenizer for the included file
     *        to which the input Statementizer has been
     *        switched.
     *
     * @throws    FileNotFoundException if bogus include file name.
     */
    public static Tokenizer initIncludeFile(
                                ArrayList     fileList,
                                File          f,
                                String        fileName,
                                Statementizer statementizer)
                                    throws FileNotFoundException,
                                           IOException {

        if (!fileList.isEmpty()) {
            IncludeFile prevI     =
                (IncludeFile)fileList.get(fileList.size() - 1);
            prevI.restartCharsToBypass
                                  =
                prevI.tokenizer.getCurrentCharNbr();
            prevI.tokenizer.close();
        }

        IncludeFile i             = new IncludeFile();
        i.fileObject              = f;
        i.fileName                = fileName;
        i.restartCharsToBypass    = 0;
        i.tokenizer               =
            new Tokenizer(
                new BufferedReader(
                    new InputStreamReader(
                        new FileInputStream(f)
                        ),
                    MMIOConstants.READER_BUFFER_SIZE
                    ),
                fileName);
        i.prevTokenizer           = statementizer.setTokenizer(
                                                        i.tokenizer);
        fileList.add(i);

        return i.tokenizer;
    }

    /**
     *  Terminates processing of the current include file,
     *  "pops the stack", and restores the previous include file
     *  for further Statementizer processing.
     *
     *  @param fileList  ArrayList used to store information
     *         about IncludeFiles.
     *
     *  @param statementizer  the Statementizer presently
     *         in use; used here to switch tokenizers.
     *
     *  @return returns Tokenizer to which the Statementizer
     *         has been switched (it will be either the original
     *         top level Tokenizer or an include file tokenizer).
     *
     *  @throws    FileNotFoundException if bogus include file name.
     */
    public static Tokenizer termIncludeFile(
                                ArrayList     fileList,
                                Statementizer statementizer)
                                    throws FileNotFoundException,
                                           IOException {
        Tokenizer retTokenizer;

        if (fileList.isEmpty()) {
            throw new IllegalArgumentException(
                MMIOConstants.ERRMSG_INCLUDE_FILE_ARRAY_EMPTY);
        }

        // closes current file and tokenizer and removes from fileList
        IncludeFile currI         =
                (IncludeFile)fileList.get(fileList.size() - 1);
        currI.tokenizer.close();

        // save previous -- this will be the top level, original,
        // Metamath source tokenizer, still open and ready to go
        // if this is the only remaining include file in fileList.
        retTokenizer              = currI.prevTokenizer;

        fileList.remove(fileList.size() - 1);

        if (fileList.isEmpty()) {
            statementizer.setTokenizer(retTokenizer);
        }
        else {
            /**
             *  otherwise...we are terminating a nested include
             *  file... then recreate its Tokenizer using the
             *  "skipahead" constructor to reposition to the character
             *  where it left off (when the $[ xx.mm $] include
             *  statement was read.)
             */
            currI                 =
                (IncludeFile)fileList.get(fileList.size() - 1);

            currI.tokenizer       =
                new Tokenizer(
                    new BufferedReader(
                        new InputStreamReader(
                            new FileInputStream(currI.fileObject)
                            ),
                        MMIOConstants.READER_BUFFER_SIZE
                        ),
                    currI.fileName,
                    currI.restartCharsToBypass);

            currI.restartCharsToBypass
                                  = 0;
            retTokenizer          = currI.tokenizer;
        }
        return retTokenizer;
    }

}
