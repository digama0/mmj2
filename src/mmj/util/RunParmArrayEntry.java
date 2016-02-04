//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * RunParmArrayEntry.java  0.02 08/24/2005
 */

package mmj.util;

import java.util.*;

import mmj.pa.MMJException;

/**
 * RunParmArrayEntry holds a RunParm "name" string AND an array(0->n) of RunParm
 * "value" strings, OR it holds a "commentLine" (but not both).
 */
public class RunParmArrayEntry implements Comparable<RunParmArrayEntry> {

    /**
     * if commentLine != null then the entire entry is a comment and there is no
     * data in the rest of it. This is the case when the first character of the
     * input line is a blank or the line is empty.
     */
    public String commentLine;

    /**
     * name is the first field on a DelimitedTextParser line.
     * <p>
     * Generally, this would be a keyword.
     */
    public String name;

    public BatchCommand cmd;

    /**
     * values is an array of String corresponding to fields 1 -> n of a
     * DelimitedTextParser.
     * <p>
     * The program knows nothing about the contents of fields except that they
     * are String's, and perhaps empty ("").
     */
    public String[] values;

    private String quoter;
    private String delimiter;

    /**
     * Construct a dummy RunParmArrayEntry with no parameters.
     */
    public RunParmArrayEntry() {}

    /**
     * Construct a RunParmArrayEntry manually using a name and values array.
     * <p>
     * Quoter and Delimiter characters are set to the defaults from
     * UtilConstants:
     * <ul>
     * <li>UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT
     * <li>UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT
     * </ul>
     *
     * @param name the name of the RunParm
     * @param values the arguments to the RunParm
     */
    public RunParmArrayEntry(final String name, final String[] values) {
        this.name = name.trim();
        this.values = values;
        commentLine = null;

        delimiter = String
            .valueOf(UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT);

        quoter = String.valueOf(UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT);

        getCmd();
    }

    private void getCmd() {
        final String nameLower = name.toLowerCase();
        cmd = Arrays.stream(UtilConstants.RUNPARM_LIST)
            .filter(c -> c.nameLower().equals(nameLower)).findAny()
            .orElseThrow(() -> new IllegalArgumentException(new MMJException(
                UtilConstants.ERRMSG_RUNPARM_NAME_INVALID, name)));
        name = cmd.name();

    }

    /**
     * Construct a RunParmArrayEntry using a DelimitedTextParser pre-loaded with
     * text line and delimiter/quoter parms.
     * <p>
     * Checks for comment: if the input line begins with one of the following
     * characters then it is deemd a "comment":
     * <ul>
     * <li>UtilConstants.RUNPARM_COMMENT_CHAR_SPACE
     * <li>UtilConstants.RUNPARM_COMMENT_CHAR_ASTERISK
     * <li>UtilConstants.RUNPARM_COMMENT_CHAR_SLASH
     * </ul>
     * <p>
     * If the line is not a comment but is empty or null, an
     * IllegalArgumentException is thrown. Otherwise, the name and values are
     * extracted using DelimitedTextParser.
     *
     * @param parser pre-loaded DelimitedTextParser object.
     */
    public RunParmArrayEntry(final DelimitedTextParser parser) {

        delimiter = String.valueOf(parser.getParseDelimiter());
        quoter = String.valueOf(parser.getParseQuoter());

        commentLine = parser.getParseString();

        if (commentLine != null) {
            if (commentLine.length() == 0)
                return; // is comment line
            final char pos1 = commentLine.charAt(0);
            if (pos1 == UtilConstants.RUNPARM_COMMENT_CHAR_SPACE
                || pos1 == UtilConstants.RUNPARM_COMMENT_CHAR_ASTERISK
                || pos1 == UtilConstants.RUNPARM_COMMENT_CHAR_SLASH)
                return; // is comment line
            commentLine = null; // is not comment line
        }

        final List<String> arrayList = new ArrayList<>(5);

        String value = null;

        if ((name = parser.nextField()) == null)
            throw new IllegalArgumentException(
                new MMJException(UtilConstants.ERRMSG_PARSER_LINE_EMPTY));

        name = name.trim();
        getCmd();

        while ((value = parser.nextField()) != null)
            arrayList.add(value);

        values = arrayList.toArray(new String[arrayList.size()]);
    }

    /**
     * Compute hashcode for RunParmArrayEntry using name.hashCode().
     * <p>
     * This won't work well with a bunch of comment lines thrown in, or
     * duplicates!
     *
     * @return hashcode equal to name.hashCode().
     */
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * Compute for equality with another RunParmArrayEntry based on name String
     * equals().
     *
     * @return true if obj is a RunParmArrayEntry and name.equals(blah.name),
     *         else false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof RunParmArrayEntry
            && name.equals(((RunParmArrayEntry)obj).name);
    }

    /**
     * Compares RunParmArrayEntry object based on the name.
     *
     * @param obj RunParmArrayEntry object to compare to this.
     * @return returns negative, zero, or a positive int if this
     *         RunParmArrayEntry object is less than, equal to or greater than
     *         the input parameter obj.
     */
    public int compareTo(final RunParmArrayEntry obj) {
        return name.compareTo(obj.name);
    }

    /**
     * Converts RunParmArrayEntry to a String.
     *
     * @return String version of RunParmArrayEntry, which will be in a
     *         normalized form, with field delimiters and quoters placed amongst
     *         name and values fields according to the contents of those fields
     *         (i.e. uses quotes only if necessary).
     */
    @Override
    public String toString() {
        if (commentLine != null)
            return commentLine;

        final StringBuilder sb = new StringBuilder(80);

        if (name == null) {
            sb.append(quoter);
            sb.append(quoter);
        }
        else if (name.indexOf(delimiter) == -1)
            sb.append(name);
        else {
            sb.append(quoter);
            sb.append(name);
            sb.append(quoter);
        }

        if (values != null)
            for (final String value : values) {
                sb.append(delimiter);
                if (value == null) {
                    sb.append(quoter);
                    sb.append(quoter);
                    continue;
                }
                if (value.indexOf(delimiter) == -1) {
                    sb.append(value);
                    continue;
                }
                sb.append(quoter);
                sb.append(value);
                sb.append(quoter);
            }

        return sb.toString();
    }
}
