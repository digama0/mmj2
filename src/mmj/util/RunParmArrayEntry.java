//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  RunParmArrayEntry.java  0.02 08/24/2005
 */

package mmj.util;

import java.util.*;

/**
 *   RunParmArrayEntry holds a RunParm "name" string
 *   AND an array(0->n) of RunParm "value" strings,
 *   OR it holds a "commentLine" (but not both).
 */
public class RunParmArrayEntry
                        implements Comparable {

    /**
     *  if commentLine != null then the entire
     *  entry is a comment and there is no data
     *  in the rest of it. This is the case when
     *  the first character of the input line
     *  is a blank or the line is empty.
     */
    public  String          commentLine;

    /**
     *  name is the first field on a DelimitedTextParser
     *  line.
     *  <p>
     *  Generally, this would be a keyword.
     */
    public  String          name;

    /**
     *  values is an array of String corresponding to
     *  fields 1 -> n of a DelimitedTextParser.
     *  <p>
     *  The program knows nothing about the contents of
     *  fields except that they are String's, and perhaps
     *  empty ("").
     */
    public  String[]        values;

    private String          quoter;
    private String          delimiter;

    /**
     *  Construct a dummy RunParmArrayEntry with no parameters.
     */
    public RunParmArrayEntry() {
    }

    /**
     *  Construct a RunParmArrayEntry manually using a name
     *  and values array.
     *  <p>
     *  Quoter and Delimiter characters are set to the
     *  defaults from UtilConstants:
     *  <ul>
     *  <li>UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT
     *  <li>UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT
     *  </ul>
     */
    public RunParmArrayEntry(String   name,
                             String[] values) {
        this.name                 = name.trim();
        this.values               = values;
        commentLine               = null;

        delimiter                 = String.valueOf(
            UtilConstants.RUNPARM_FIELD_DELIMITER_DEFAULT);

        quoter                    = String.valueOf(
            UtilConstants.RUNPARM_FIELD_QUOTER_DEFAULT);

    }

    /**
     *  Construct a RunParmArrayEntry using a DelimitedTextParser
     *  pre-loaded with text line and delimiter/quoter parms.
     *  <p>
     *  Checks for comment: if the input line begins with one
     *  of the following characters then it is deemd a "comment":
     *  <ul>
     *  <li>UtilConstants.RUNPARM_COMMENT_CHAR_SPACE
     *  <li>UtilConstants.RUNPARM_COMMENT_CHAR_ASTERISK
     *  <li>UtilConstants.RUNPARM_COMMENT_CHAR_SLASH
     *  </ul>
     *  <p>
     *  If the line is not a comment but is empty or null,
     *  an IllegalArgumentException is thrown. Otherwise,
     *  the name and values are extracted using
     *  DelimitedTextParser.
     *  <p>
     *  @param parser pre-loaded DelimitedTextParser object.
     */
    public RunParmArrayEntry(DelimitedTextParser parser) {

        delimiter                 = String.valueOf(
                                        parser.getParseDelimiter());
        quoter                    = String.valueOf(
                                        parser.getParseQuoter());

        commentLine               = parser.getParseString();

        if (commentLine != null) {
            if (commentLine.length()  == 0) {
                return;                  // is comment line
            }
            char pos1 = commentLine.charAt(0);
            if (pos1 == UtilConstants.RUNPARM_COMMENT_CHAR_SPACE
                ||
                pos1 == UtilConstants.RUNPARM_COMMENT_CHAR_ASTERISK
                ||
                pos1 == UtilConstants.RUNPARM_COMMENT_CHAR_SLASH) {
                return;                  // is comment line
            }
            commentLine           = null; // is not comment line
        }

        ArrayList arrayList       = new ArrayList(5);

        String    value           = null;

        if ((name = parser.nextField())
                == null) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_PARSER_LINE_EMPTY);
        }

        name                      = name.trim();

        while ((value = parser.nextField())
                != null) {
            arrayList.add(value);
        }

        values                    = new String[arrayList.size()];
        Iterator iterator         = arrayList.iterator();
        int i                     = 0;
        while (iterator.hasNext()) {
            values[i++] = (String)iterator.next();
        }

    }

    /**
     *  Compute hashcode for RunParmArrayEntry using
     *  name.hashCode().
     *  <p>
     *  This won't work well with a bunch of comment lines
     *  thrown in, or duplicates!
     *
     *  @return hashcode equal to name.hashCode().
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     *  Compute for equality with another RunParmArrayEntry
     *  based on name String equals().
     *
     *  return true if obj is a RunParmArrayEntry and
     *         name.equals(blah.name), else false.
     */
    public boolean equals(Object obj) {
        return (this == obj) ? true
                : !(obj instanceof RunParmArrayEntry) ? false
                        : name.equals(
                        ((RunParmArrayEntry)obj).name);
    }

    /**
     *  Compares RunParmArrayEntry object based on the name.
     *
     *  @param obj RunParmArrayEntry object to compare to this.
     *
     *  @return returns negative, zero, or a positive int
     *  if this RunParmArrayEntry object is less than, equal to
     *  or greater than the input parameter obj.
     */
    public int compareTo(Object obj) {
        return name.compareTo(
                ((RunParmArrayEntry)obj).name);
    }

    /**
     *  Converts RunParmArrayEntry to a String.
     *
     *  @return String version of RunParmArrayEntry, which will
     *          be in a normalized form, with field delimiters
     *          and quoters placed amongst name and values fields
     *          according to the contents of those fields
     *          (i.e. uses quotes only if necessary).
     */
    public String toString() {
        if (commentLine != null) {
            return commentLine;
        }

        StringBuffer sb = new StringBuffer(80);

        if (name == null) {
            sb.append(quoter);
            sb.append(quoter);
        }
        else {
            if (name.indexOf(delimiter) == -1) {
                sb.append(name);
            }
            else {
                sb.append(quoter);
                sb.append(name);
                sb.append(quoter);
            }
        }

        if (values != null) {
            for (int i = 0; i < values.length; i++) {
                sb.append(delimiter);
                if (values[i] == null) {
                    sb.append(quoter);
                    sb.append(quoter);
                    continue;
                }
                if (values[i].indexOf(delimiter) == -1) {
                    sb.append(values[i]);
                    continue;
                }
                sb.append(quoter);
                sb.append(values[i]);
                sb.append(quoter);
            }
        }

        return sb.toString();
    }
}
