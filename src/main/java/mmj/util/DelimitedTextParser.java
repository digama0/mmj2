//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * DelimitedTextParser.java  0.02 08/24/2005
 */

package mmj.util;

import java.util.ArrayList;
import java.util.List;

import mmj.pa.MMJException;

/**
 * Class {@code DelimitedTextParser} parses a line of delimited text input as a
 * String.
 * <p>
 * DelimitedTextParser is intended for use with ASCII-delimited text files,
 * which typically contain one line per record and contain text fields delimited
 * by commas (',') and enclosed by quotes ('"'). A string may contain both
 * quoted and unquoted fields, assuming that none of the unquoted fields contain
 * occurrences of the delimiter character. Input text may contain any Unicode
 * characters. Quoting may be disabled if the input data does not contain fields
 * enclosed by quotes. Note that the end of the string is treated as a delimiter
 * (the last field should not be terminated by a delimiter!)
 * <p>
 * Returned output fields consist of the characters between delimiters and
 * quotes (the delimiter and quote characters are not output). Empty fields
 * (adjacent delimiters) are returned as zero-length Strings (example: "a,,b"
 * will return "a", "", then "b").
 * <p>
 * <ol>
 * Parse Rules:
 * <li>A line containing N delimiters contains N + 1 fields by definition.
 * <li>A null string is returned from nextField after the N+1th field has been
 * returned; this indicates End of Line, and a subsequent call to nextField will
 * result in an IllegalArgumentException.</li>
 * <li>If quotes are used for a field then there must be a pair of quotes, left
 * and right, surrounding the field text and each must be adjacent to a
 * delimiter or at the start or end of the line -- if another character occurs
 * between a delimiter and a quote then the quote will be treated as data and
 * returned as part of the parsed field (example: ('A,B"C",D') is a line
 * containing 3 fields, 'A', 'B"C"', and 'D').</li>
 * <li>Text inside paired quotes is returned from nextField, including
 * occurrences of the defined delimiter character; if quoting is disabled then
 * quote characters are always treated as data.</li>
 * <li>An empty string is returned from nextField if there are no characters
 * between delimiters or paired quotes, or if the line itself is an empty string
 * (thus, a line consisting of just a single delimiter character contains two
 * empty-string fields, by definition.)</li>
 * <li>The defined quote and delimiter characters may be changed at any time
 * (enabling parsing if say, the first field on a line is 'quoted' using '|'
 * characters but the remaining fields are quoted using '"' characters,) the
 * calling method
 * </ol>
 */
public class DelimitedTextParser {

    /**
     * The delimiter character in use.
     */
    private char delimiter = UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT;

    /**
     * The quote character in use.
     */
    private char quoter = UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT;

    /**
     * Quoted field parsing enabled flag.
     */
    private boolean quoterEnabled = true;

    /**
     * The line being parsed.
     */
    private String line = null;

    /**
     * Offset of the next character to be parsed.
     */
    private int next = 0;

    /**
     * Offset of the last character to be parsed (line length).
     */
    private int max = -1;

    /**
     * Output field.
     */
    private String fieldOut = null;

    /**
     * End of Line flag.
     */
    private boolean reachedEOL = false;

    /**
     * Default constructor.
     */
    public DelimitedTextParser() {}

    /**
     * Constructs parser for a String using default parse parameters.
     *
     * @param lineIn String to be parsed.
     * @throws IllegalArgumentException if an error occurred
     */
    public DelimitedTextParser(final String lineIn)
        throws IllegalArgumentException
    {
        setParseString(lineIn);
    }

    /**
     * Constructs parser with specified quoter and delimiter parameters (note:
     * quoting is enabled by default).
     *
     * @param fieldDelimiter Delimiter character.
     * @param fieldQuoter Quote character used to enclose fields.
     * @throws NullPointerException if the input String reference is null.
     */
    public DelimitedTextParser(final char fieldDelimiter,
        final char fieldQuoter)
    {
        delimiter = fieldDelimiter;
        quoter = fieldQuoter;
    }

    /**
     * Constructs parser for a String using specified parse parameters (note:
     * quoting is enabled by default).
     *
     * @param lineIn String to be parsed.
     * @param fieldDelimiter Delimiter character.
     * @param fieldQuoter Quote character used to enclose fields.
     * @throws IllegalArgumentException if an error occurred
     * @throws NullPointerException if the input String reference is null.
     */
    public DelimitedTextParser(final String lineIn, final char fieldDelimiter,
        final char fieldQuoter) throws IllegalArgumentException
    {
        delimiter = fieldDelimiter;
        quoter = fieldQuoter;
        setParseString(lineIn);
    }

    /**
     * Loads a new String into an existing parser and prepares the parse object
     * for parsing the first field of the string.
     *
     * @param lineIn String to be parsed.
     * @throws IllegalArgumentException if an error occurred
     * @throws NullPointerException if the input String reference is null.
     */
    public void setParseString(final String lineIn)
        throws IllegalArgumentException
    {
        if (lineIn == null)
            throw new IllegalArgumentException(new MMJException(
                UtilConstants.ERRMSG_PARSER_INPUT_STRING_NULL));

        line = lineIn;
        next = 0;
        max = lineIn.length();
        reachedEOL = false;
    }

    /**
     * Returns the original line being parsed.
     *
     * @return line String to be parsed.
     */
    public String getParseString() {
        return line;
    }

    /**
     * Enables or disables (turns on/off) quote parsing.
     *
     * @param quoterEnabled Set to true for enabled or false for disabled.
     */
    public void setQuoterEnabled(final boolean quoterEnabled) {
        this.quoterEnabled = quoterEnabled;
    }

    /**
     * Resets the parsing parameters to their default values: delimiter = comma,
     * quoter = quote, quoter is enabled.
     */
    public void resetParseDefaults() {
        delimiter = UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT;
        quoter = UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT;

        quoterEnabled = true;
    }

    /**
     * Sets the parse delimiter to a specified character.
     *
     * @param delimiter the field delimiter character.
     */
    public void setParseDelimiter(final char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Returns the parse delimiter character
     *
     * @return parse delimiter character
     */
    public char getParseDelimiter() {
        return delimiter;
    }

    /**
     * Sets the parse quoter to a specified character.
     *
     * @param quoter character that encloses input fields.
     */
    public void setParseQuoter(final char quoter) {
        this.quoter = quoter;
    }

    /**
     * Returns the parse quote character
     *
     * @return parse quote character
     */
    public char getParseQuoter() {
        return quoter;
    }

    /**
     * Returns the next field from the input String.
     *
     * @return String the next field parsed from the input String or null if
     *         there are no more fields in the String.
     * @throws IllegalArgumentException thrown if the string to be parsed is not
     *             correctly formatted (for example, unmatched quote characters)
     *             or if nextField invoked again after reaching the end of the
     *             line.
     * @throws IllegalStateException if an error occurred
     */
    public String nextField()
        throws IllegalArgumentException, IllegalStateException
    {
        int left = 0;
        int right = 0;
        fieldOut = null;

        if (next < max) {
            if (quoterEnabled && line.charAt(next) == quoter) {
                left = ++next;
                right = line.indexOf(quoter, left);
                if (right == -1)
                    throw new IllegalArgumentException(new MMJException(
                        UtilConstants.ERRMSG_UNMATCHED_QUOTE_CHAR));
                else {
                    fieldOut = line.substring(left, right);
                    next = right + 1;
                    if (next < max)
                        if (line.charAt(next) == delimiter)
                            next++;
                        else
                            throw new IllegalStateException(new MMJException(
                                UtilConstants.ERRMSG_MISSING_DELIM));
                }
            }
            else if (line.charAt(next) == delimiter) {
                left = right = next++; // set up empty string
                fieldOut = line.substring(left, right);
            }
            else { // next char not a delimiter or a quoter
                left = next++;
                right = line.indexOf(delimiter, next);
                if (right == -1)
                    right = max;
                fieldOut = line.substring(left, right);
                next = right + 1;
            }
        }
        else if (next == max && max > 0 && line.charAt(max - 1) == delimiter) {
            fieldOut = "";
            next++;
        }
        else if (reachedEOL)
            throw new IllegalArgumentException(new MMJException(
                UtilConstants.ERRMSG_PARSER_LINE_ALREADY_REACHED));
        else
            reachedEOL = true;
        return fieldOut;
    }

    /**
     * Returns a list of all fields, consuming the entire input String.
     *
     * @return A {@code List<String>} of fields parsed from the input String.
     * @throws IllegalArgumentException thrown if the string to be parsed is not
     *             correctly formatted (for example, unmatched quote characters)
     *             or if parseAll invoked again after reaching the end of the
     *             line.
     * @throws IllegalStateException if an error occurred
     */
    public List<String> parseAll()
        throws IllegalArgumentException, IllegalStateException
    {
        final List<String> list = new ArrayList<>();
        while (true) {
            final String field = nextField();
            if (field == null)
                break;
            else
                list.add(field);
        }
        return list;
    }

    /**
     * Test code -- just run, requires no command line params.
     *
     * @param args not used.
     */
    public static void main(final String[] args) {
        /*    test cases:
                1.  line with 1 field
                    a)  empty line
                    b)  undelimited (' ')
                    c)  delimited empty string  ('""')
                    d)  delimited non-empty string ('" "')
                2.  line with 2 fields
                    a)  line contains only 1 char, a delimiter (',')
                    b)  line contains two empty delimited strings and
                        a delimiter
                        ('"",""')
                    c)  line contains

                    etc...

        */

        /*
          Enclose all testing in error handling code.
        */

        // goofy self-instantiation to build an object
        // so that non-static methods can be easily invoked
        // in the testing code.

        boolean normalEOJ = false;

        final int testCaseCount = 12;
        int testCaseNbr = 0;
        final String[] testCaseString = {"|a|,b,c,,e", // 01
                "", // 02
                " ", // 03
                "|", // 04
                "||", // 05
                "|||", // 06
                "| |", // 07
                ",", // 08
                "||,||", // 09
                "|a|,|b|", // 10
                " a|, b|", // 11
                ",,," // 12
        };

        final char[] testCaseQuoter = {'|', // 01
                '|', // 02
                '|', // 03
                '|', // 04
                '|', // 05
                '|', // 06
                '|', // 07
                '|', // 08
                '|', // 09
                '|', // 10
                '|', // 11
                '|' // 12
        };

        final char[] testCaseDelimiter = {',', // 01
                ',', // 02
                ',', // 03
                ',', // 04
                ',', // 05
                ',', // 06
                ',', // 07
                ',', // 08
                ',', // 09
                ',', // 10
                ',', // 11
                ',' // 12
        };

        try {

            final DelimitedTextParser d = new DelimitedTextParser("|a|,b,c,,e", // line
                                                                                // in
                ',', // delimiter
                '|'); // quote char
            String s;
            int i = 0;
            while (i < 99 && (s = d.nextField()) != null) {
                i++;
                if (s.length() == 0)
                    System.out
                        .println("nextField " + i + " is an empty string");
                else
                    System.out.println("nextField " + i + " " + s);
            }
            for (i = 0, testCaseNbr = 1; testCaseNbr <= testCaseCount; i++, testCaseNbr++)
                runTestCase(d, testCaseNbr, testCaseString[i],
                    testCaseDelimiter[i], testCaseQuoter[i]);
            normalEOJ = true;
        } catch (final Exception e) {
            printThrownDiagnostics(e);
        } catch (final Error e) {
            printThrownDiagnostics(e);
        } finally {
            System.out.println("Reached Finally in main()! "
                + "Flag 'normalEOJ' = " + normalEOJ);
        }
    }

    private static void runTestCase(final DelimitedTextParser parser,
        final int testCaseNbr, final String line, final char delim,
        final char quoter)
    {
        System.out.println("* * * * * Test Case " + testCaseNbr + " * * * * *");
        parser.setParseDelimiter(delim);
        parser.setParseQuoter(quoter);
        parser.setParseString(line);
        System.out.println("line  ='" + line + "'");
        System.out.println("delim ='" + delim + "'");
        System.out.println("quoter='" + quoter + "'");
        int fieldNbr = 0;
        String fieldOut = null;
        try {
            do {
                fieldOut = parser.nextField();
                if (fieldOut == null)
                    System.out.println("End of line reached.");
                else {
                    fieldNbr++;
                    if (fieldOut.length() == 0)
                        System.out
                            .println("Field " + fieldNbr + " is empty string.");
                    else
                        System.out.println(
                            "Field " + fieldNbr + " ='" + fieldOut + "'");
                }
            } while (fieldOut != null);
        } catch (final Exception e) {
            printThrownDiagnostics(e);
        } catch (final Error e) {
            printThrownDiagnostics(e);
        }
    }

    private static void printThrownDiagnostics(final Throwable e) {
        System.out.println("DelimitedTextParser:main caught Exception:"
            + e.getLocalizedMessage());
        e.printStackTrace(System.out);
    }
}
