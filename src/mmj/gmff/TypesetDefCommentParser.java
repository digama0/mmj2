//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * TypesetDefCommentParser.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.*;

import mmj.gmff.GMFFConstants.ParseLocContext;
import mmj.lang.Messages;
import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.MMJException;

/**
 * {@code TypesetDefCommentParser} parses, validates and loads {@code Map}
 * collections with typesetting data from Metamath Comment statements identified
 * by a {@code $t} as the first token after the Comment ID token {@code $(}.
 * <p>
 * Almost all necessary specifications for processing Metamath typesetting
 * information is contained in the {@code Metamath.pdf} book, with the following
 * exceptions:
 * <ul>
 * <li>Asterisk-Slash C-style comments may be present and are to be treated as
 * whitespace following the {@code  $t} token up to the terminating {@code $)}
 * token. (Double-Slash C-style comments are not treated as comments.)
 * <li>Information in the {@code $t} Comment statements is of the form: '
 * <i>keyword</i> <i>xxxStuffxxx</i>;'.
 * <li>GMFF is interested only in symbol typesetting definitions identified by
 * keywords {@code htmldef}, {@code althtmldef}, and {@code latexdef} -- these
 * keywords are input parameters, not hardcoded -- but other definitions may be
 * added in the future and GMFF must be able to correctly bypass these
 * definitions.
 * <li>For unknown and ignored definition types GMFF still needs to parse their
 * quoted strings and C-style comments in order to bypass them. That is because
 * quoted strings may contain what appear to be C-style comments and C-style
 * comments may contain what appear to be quoted strings -- as well as
 * semicolons.
 * </ul>
 * <p>
 * Note that the input Metamath Comment statements processed by
 * {@code TypesetDefCommentParser} are stripped of their delimiting {@code $(}
 * and {@code $)} tokens prior to input. This is a result of the way that mmj2
 * parses input files and stores Metamath Comments in {@code mmj.mmio.SrcStmt}.
 * <p>
 * {@code TypesetDefCommentParser} is coded as a separate class in order to
 * provide modularity, not because there are separate instances of it.
 * {@code GMFFManager} instantiates it and then calls
 * {@code doIt(String typesetDefComment)} for each Metamath {@code $t} Comment
 * statement to be processed.
 * <p>
 * Another curious/strange aspect of processing is that mmj2 accumulates
 * Metamath typesetting Comment statements in the {@code GMFFManager} via
 * {@code mmj.mmio.Systemizer} method {@code loadComment()} but the typesetting
 * Comments are not processed unless and until the user or a RunParm command
 * invokes GMFF's typsetting services -- at which point {@code GMFFManager}
 * invokes {@code TypesetDefCommentParser}.
 */
public class TypesetDefCommentParser {

    Messages messages;
    String[] typesetDefKeyword;
    List<Map<String, String>> typesetDefMap;

    // global variables for current $t parse
    String comment; // the Metamath $t comment stripped of $( and $)
    int currIndex;
    int maxIndex;
    int defErrorIndex;
    String currKeyword;
    String currSym;
    String currReplacement;

    /**
     * The only constructor.
     *
     * @param exporterTypesetDefsList <code>List<code> of
     *            {@code GMFFExporterTypesetDefs} which are to be selected for
     *            processing and loaded with data.
     * @param messages The mmj2 <code> Messages object.
     * @throws GMFFException if an error occurred
     */
    public TypesetDefCommentParser(
        final List<GMFFExporterTypesetDefs> exporterTypesetDefsList,
        final Messages messages) throws GMFFException
    {

        this.messages = messages;

        typesetDefKeyword = new String[exporterTypesetDefsList.size()];
        typesetDefMap = new ArrayList<>(exporterTypesetDefsList.size());

        int i = 0;
        for (final GMFFExporterTypesetDefs d : exporterTypesetDefsList) {
            typesetDefKeyword[i++] = d.typesetDefKeyword;
            typesetDefMap.add(d.typesetDefMap);
        }
    }

    /**
     * Parses, validates and loads {@code Map} collections with symbol
     * typesetting definitions with keywords matching the constructor
     * {@code exporterTypesetDefsList} list.
     *
     * @param typesetDefComment Metamath {@code $t} Comment statement stripped
     *            of its {@code $(} beginning and {@code $)} ending tokens.
     * @throws GMFFException if parse or validation errors encountered.
     */
    public void doIt(final String typesetDefComment) throws GMFFException {

        comment = typesetDefComment;

        currIndex = 0;
        defErrorIndex = currIndex;
        maxIndex = typesetDefComment.length();

        // NOTE: each of routines below leaves currIndex
        // pointing to the next non-whitespace/
        // non-comment char following whatever it was
        // trying to get or bypass -- or currIndex
        // will be set to maxIndex (or greater.)

        validateDollarTToken();

        mainLoop: while (currIndex < maxIndex) {

            initCurrDefVars();
            defErrorIndex = currIndex;

            getCurrKeyword();
            if (currIndex >= maxIndex)
                triggerErrorPrematureEndOfDef();

            for (int i = 0; i < typesetDefKeyword.length; i++)
                if (currKeyword.equals(typesetDefKeyword[i])) {

                    loadCurrTypesetDef();

                    storeInTypesetDefMap(i, currSym, currReplacement);
                    continue mainLoop;
                }
            bypassOtherDef();
        }
        return;
    }

    private void initCurrDefVars() {
        currKeyword = "";
        currSym = "";
        currReplacement = "";
    }

    /*
     * Confirm "$t" is first non-whitespace of $t Comment
     * statement (the "$(" and "$)" are not present in the comment).
    * - bypass whitespace/comments following $t
    * - upon exit, currIndex must point to non-whitespace,
    *   non-comment character OR maxIndex!
     */
    private void validateDollarTToken() throws GMFFException {

        bypassWhitespace();

        if (currIndex >= maxIndex || !comment
            .startsWith(MMIOConstants.TYPESETTING_COMMENT_ID_STRING, currIndex))
            triggerErrorMissingDollarTToken();

        currIndex += MMIOConstants.TYPESETTING_COMMENT_ID_STRING.length();

        bypassWhitespaceAndComments();
    }

    /*
    * Load keyword into currKeyword.
     * - accum one char at a time
    * - trigger invalid keyword error if quote,
    *   single quote, plus symbol, semicolon, asterisk
    *   not preceded by slash, or slash not followed
    *   by asterisk found -- or if end result is
    *   empty string.
    * - terminate accumulation when whitespace
    *   or c-comment start or end of file reached.
    * - if keyword invalid throw exception.
    * - bypass whitespace/comments after keyword.
    * - upon exit, currIndex must point to non-whitespace,
    *   non-comment character OR maxIndex!
    */
    private void getCurrKeyword() throws GMFFException {

        final StringBuilder sb = new StringBuilder();
        char nextChar;

        while (currIndex < maxIndex) {

            nextChar = comment.charAt(currIndex);

            // look for end of keyword
            if (Character.isWhitespace(nextChar) || comment.startsWith(
                MMIOConstants.TYPESETTING_C_COMMENT_START, currIndex))
                break;

            // look for bogus keyword
            if (nextChar == MMIOConstants.TYPESETTING_SINGLE_QUOTE_CHAR
                || nextChar == MMIOConstants.TYPESETTING_DOUBLE_QUOTE_CHAR
                || nextChar == MMIOConstants.TYPESETTING_PLUS_CHAR
                || nextChar == MMIOConstants.TYPESETTING_SLASH_CHAR
                || nextChar == MMIOConstants.TYPESETTING_ASTERISK_CHAR
                || nextChar == MMIOConstants.TYPESETTING_SEMICOLON_CHAR)
                triggerErrorInvalidKeywordChar(nextChar, sb);

            sb.append(nextChar);
            currIndex++;
        }

        if (sb.length() == 0)
            triggerErrorKeywordEmptyString();

        currKeyword = sb.toString();
        bypassWhitespaceAndComments();
    }

    /*
    * Load a Sym's definition:
    * - positioned at entry to a character which
    *   is not whitespace or the start of a comment.
    * - get sym quoted string,
    * - confirm "as" literal exists
    * - get consolidated replacement quoted string
    * - confirm ";" exists
    * - bypass whitespace/comments between elements
    * - bypass whitespace/comments after ";"
    * - if any missing elements or other errors throw exception
    * - upon exit, currIndex must point to non-whitespace,
    *   non-comment character OR maxIndex!
    */
    private void loadCurrTypesetDef() throws GMFFException {

        getCurrSym();
        if (currIndex >= maxIndex)
            triggerErrorPrematureEndOfDef();

        confirmAsLiteralExists();
        if (currIndex >= maxIndex)
            triggerErrorPrematureEndOfDef();

        getConsolidatedReplacementString();
        if (currIndex >= maxIndex)
            triggerErrorPrematureEndOfDef();

        confirmEndOfDefSemiColon();
    }

    /*
    * Get sym being defined:
    * - pull sym chars out of quote-delimited string
    * - error if whitespace betweem quotes
    * - error if sym turns out to be empty string
    * - end quote must match starting quote
    * - upon exit, currIndex must point to non-whitespace,
    *   non-comment character OR maxIndex!
    */
    private void getCurrSym() throws GMFFException {

        currSym = getQuoteDelimitedString();

        if (currSym.length() == 0)
            triggerErrorInvalidSym();
        for (int i = 0; i < currSym.length(); i++)
            if (Character.isWhitespace(currSym.charAt(i)))
                triggerErrorInvalidSym();
        bypassWhitespaceAndComments();
    }

    /*
    * Confirm currIndex points to "as" (without the quotes)
    * _ currIndex should already be positioned to the "a"
    * - generate missing-as error if not!
    * - upon exit, currIndex must point to non-whitespace,
    *   non-comment character OR maxIndex!
    */
    private void confirmAsLiteralExists() throws GMFFException {
        if (!comment.startsWith(MMIOConstants.TYPESETTING_AS_LITERAL,
            currIndex))
            triggerErrorAsLiteralMissing();
        currIndex += MMIOConstants.TYPESETTING_AS_LITERAL.length();

        bypassWhitespaceAndComments();
    }

    /*
    * Get either one quoted string or a group joined by "+"
    * characters:
    * - trigger error if "+" not followed by quoted string
    * - concatenate quoted strings and store in currReplacement
    */
    private void getConsolidatedReplacementString() throws GMFFException {

        final StringBuilder sb = new StringBuilder();
        sb.append(getQuoteDelimitedString());
        bypassWhitespaceAndComments();
        while (currIndex < maxIndex && comment
            .charAt(currIndex) == MMIOConstants.TYPESETTING_PLUS_CHAR)
        {
            currIndex++;
            bypassWhitespaceAndComments();
            if (currIndex >= maxIndex)
                triggerErrorPrematureEndOfDef();
            sb.append(getQuoteDelimitedString());
            bypassWhitespaceAndComments();
        }
        currReplacement = sb.toString();
    }

    /*
     * Trigger error if semicolon not present at currIndex.
     * - then bypass whitespace and comments
     */
    private void confirmEndOfDefSemiColon() throws GMFFException {
        if (comment
            .charAt(currIndex) == MMIOConstants.TYPESETTING_SEMICOLON_CHAR)
        {}
        else
            triggerErrorMissingSemicolon();
        currIndex++;

        bypassWhitespaceAndComments();
    }

    /*
    * Bypass "other" (unknown/no-interest) definition:
    * - bypass quoted strings
    * - bypass characters that are not quoted strings
    *   or comments;
    * - confirm ";" exists
    * - bypass whitespace/comments between elements
    * - bypass whitespace/comments after ";"
    * - if any errors throw exception
    * - upon exit, currIndex must point to non-whitespace,
    *   non-comment character OR maxIndex!
    */
    private void bypassOtherDef() throws GMFFException {
        char nextChar;
        while (currIndex < maxIndex) {

            nextChar = comment.charAt(currIndex);

            if (nextChar == MMIOConstants.TYPESETTING_SEMICOLON_CHAR) {
                currIndex++;
                bypassWhitespaceAndComments();
                return;
            }

            if (nextChar == MMIOConstants.TYPESETTING_SINGLE_QUOTE_CHAR
                || nextChar == MMIOConstants.TYPESETTING_DOUBLE_QUOTE_CHAR)
                getStringInsideDelimiters(nextChar);
            else
                // nextChar is something...not whitespace or comment
                // or quote...just bypass it.
                currIndex++;
            bypassWhitespaceAndComments();
        }
        triggerErrorMissingSemicolon();
    }

    /*
     * Extract text in between delimiters
     * - position currIndex after end delimiter upon exit
     * - don't bypass whitespace/comments afterwards
     *   simply because the calling routines wanted to
     *   do it themselves :-)
     */
    private String getQuoteDelimitedString() throws GMFFException {
        final char nextChar = comment.charAt(currIndex);
        if (nextChar == MMIOConstants.TYPESETTING_SINGLE_QUOTE_CHAR
            || nextChar == MMIOConstants.TYPESETTING_DOUBLE_QUOTE_CHAR)
        {}
        else
            triggerErrorNotAQuotedString(nextChar);
        return getStringInsideDelimiters(nextChar);
    }

    /*
     * Extract text in between delimiters.
     * - If two consecutive occurrences of delimiter character found
    *   then output one occurrence as text, not a delimiter!
     * - The end delimiter is followed by a) end of string; or
    *   b) anything except an end delimiter character.
     */
    private String getStringInsideDelimiters(final char delimChar)
        throws GMFFException
    {
        final StringBuilder sb = new StringBuilder();
        char currChar;

        // loop through comment extracting text characters
        // until end-delimiter found or end of comment
        while (++currIndex < comment.length()) {
            currChar = comment.charAt(currIndex);
            if (currChar != delimChar) {
                sb.append(currChar);
                continue;
            }

            // ok: found delimiter. see if two in a row
            if (++currIndex < comment.length()) {
                currChar = comment.charAt(currIndex);
                // if two in a row convert the two into one text char
                if (currChar == delimChar) {
                    sb.append(currChar);
                    continue;
                }
                // following char not a delim so we're done
            }
            // - currIndex now positioned after end-delimiter or
            // beyond the end of the comment.
            // - return string inside delimiter!
            return sb.toString(); // end delim found!
        }

        throw getException(GMFFConstants.ERRMSG_MISSING_END_DELIM);
    }

    /**
     * Load sym's typesetting definition into the Map for its keyword.
     * <ul>
     * <li>generate info message if duplicate def for sym
     *
     * @param i the index to store
     * @param currSym The symbol
     * @param currReplacement the replacement
     */
    private void storeInTypesetDefMap(final int i, final String currSym,
        final String currReplacement)
    {
        final Map<String, String> map = typesetDefMap.get(i);
        final String oldReplacement = map.get(currSym);

        if (oldReplacement != null)
            triggerInfoMsgDupSymTypesetDef();
        else
            map.put(currSym, currReplacement);
    }

    /**
     * Bypass whitespace characters and c-style comments:
     * <ul>
     * <li>there may or may not be whitespace and/or comments
     * <li>there may be multiple whitespace/comment sequences
     * <li>currIndex may be >= maxIndex upon entry
     * <li>if Slash-Asterisk found call bypassComment() routine
     * <ul>
     * <li>bypassComment() must confirm Asterisk-Slash exists and that there is
     * not a comment inside of the comment.
     * </ul>
     * <li>upon exit, currIndex must point to non-whitespace, non-comment
     * character OR maxIndex!
     * </ul>
     *
     * @throws GMFFException Shit happens
     */
    private void bypassWhitespaceAndComments() throws GMFFException {
        while (currIndex < maxIndex) {
            bypassWhitespace();
            if (comment.startsWith(MMIOConstants.TYPESETTING_C_COMMENT_START,
                currIndex))
            {
                bypassComment();
                continue;
            }
            break;
        }
    }

    /**
     * Bypass a c-style comment (Slash-Asterisk variety):
     * <ul>
     * <li>upon entry currIndex points to starting "/"
     * <li>must confirm Asterisk-Slash exists prior to end of file
     * <li>must confirm comment does not contain a Slash-Asterisk inside the
     * comment (no nesting of comments).
     * <li>upon exit, currIndex must point to first character after
     * Asterisk-Slash comment end OR OR maxIndex!
     * </ul>
     *
     * @throws GMFFException Shit happens
     */
    private void bypassComment() throws GMFFException {
        currIndex += MMIOConstants.TYPESETTING_C_COMMENT_START.length();

        final int endCommentIndex = comment
            .indexOf(MMIOConstants.TYPESETTING_C_COMMENT_END, currIndex);

        if (endCommentIndex == -1)
            triggerErrorMissingEndComment();

        final int nextStartCommentIndex = comment
            .indexOf(MMIOConstants.TYPESETTING_C_COMMENT_START, currIndex);

        currIndex = endCommentIndex
            + MMIOConstants.TYPESETTING_C_COMMENT_END.length();

        if (nextStartCommentIndex != -1 && nextStartCommentIndex < currIndex)
            triggerErrorNestedComments();
    }

    /**
     * Bypass whitespace characters:
     * <ul>
     * <li>there may be no whitespace characters
     * <li>currIndex may be >= maxIndex upon entry
     * <li>upon exit, currIndex must point to non-whitespace, character OR
     * maxIndex!
     * </ul>
     */
    private void bypassWhitespace() {
        for (; currIndex < maxIndex
            && Character.isWhitespace(comment.charAt(currIndex)); currIndex++);
    }

    private void triggerErrorPrematureEndOfDef() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_PREMATURE_END_OF_DEF);
    }

    private void triggerErrorMissingDollarTToken() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_MISSING_DOLLAR_T_TOKEN);
    }

    private void triggerErrorNestedComments() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_NESTED_COMMENTS);
    }

    private void triggerErrorMissingEndComment() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_MISSING_END_COMMENT);
    }

    private void triggerErrorInvalidKeywordChar(final char nextChar,
        final StringBuilder sb) throws GMFFException
    {
        throw getException(GMFFConstants.ERRMSG_INVALID_KEYWORD_CHAR, nextChar,
            sb);
    }

    private void triggerErrorKeywordEmptyString() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_KEYWORD_EMPTY_STRING);
    }

    private void triggerErrorInvalidSym() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_INVALID_SYM);
    }

    private void triggerErrorAsLiteralMissing() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_AS_LITERAL_MISSING);
    }

    private void triggerErrorMissingSemicolon() throws GMFFException {
        throw getException(GMFFConstants.ERRMSG_MISSING_SEMICOLON);
    }

    private void triggerErrorNotAQuotedString(final char nextChar)
        throws GMFFException
    {
        throw getException(GMFFConstants.ERRMSG_NOT_A_QUOTED_STRING, nextChar);
    }

    private void triggerInfoMsgDupSymTypesetDef() {
        messages.accumException(
            getException(GMFFConstants.ERRMSG_DUP_SYM_TYPESET_DEF));
    }

    /**
     * Returns an exception filled with the location of the parser, containing
     * the line number of defErrorIndex which is set by parsing functions as
     * they proceed just in case there is an error.
     * <ul>
     * <li>Counts Newline characters to determine line number.
     * <li>If doesn't find any Newline characters then looks for Carriage
     * Returns :-)
     * </ul>
     *
     * @param code The error code
     * @param args The formatting arguments
     * @return an exception
     */
    private GMFFException getException(final ErrorCode code,
        final Object... args)
    {

        int errLine = countLines('\n');

        if (errLine < 2)
            errLine = countLines('\r');

        return MMJException.addContext(
            new ParseLocContext(errLine, currKeyword, currSym),
            new GMFFException(code, args));
    }

    private int countLines(final char nextLineChar) {

        int lineNbr = 1;
        int nextLineIndex = 0;

        while (true) {
            final int i = comment.indexOf(nextLineChar, nextLineIndex);
            if (i == -1 || i >= defErrorIndex)
                break;

            lineNbr++;
            nextLineIndex = i + 1;
        }

        return lineNbr;
    }
}
