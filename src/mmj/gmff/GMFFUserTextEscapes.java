//********************************************************************/
//* Copyright (C) 2011                                               */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  GMFFUserTextEscapes.java  0.01 11/01/2011
 *
 *  Version 0.01:
 *  Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.ArrayList;
import java.util.Comparator;

import mmj.lang.Messages;
import mmj.util.UtilConstants;

/**
 *  GMFFUserTextEscapes holds the parameters from a single
 *  RunParm of the same name.
 *  <p>
 *  It is basically just a data structure with some
 *  attached utility functions on the data elements.
 *  <p>
 *  The reason for creating this class is that GMFF
 *  parameter type RunParms are not validated and
 *  processed until GMFF is initialized, typically
 *  when the user requests an export. So the RunParms
 *  are cached until initialization time.
 *  <p>
 *  The GMFFUserTextEscapes are keyed by exportType
 *  because, in theory, different export types could
 *  have different escape codes.
 */
public class GMFFUserTextEscapes implements Comparable {

    public final String exportType;
    public ArrayList<EscapePair> escapePairList;

    /**
     *  Constructor with ArrayList of Escape Pairs.
     *  <p>
     *  @param exportType key
     *  @param escapePairList ArrayList of <code>EscapePair</code>
     */
    public GMFFUserTextEscapes(final String exportType,
        final ArrayList<EscapePair> escapePairList)
    {

        this.exportType = exportType;
        this.escapePairList = escapePairList;

    }

    /**
     *  Constructor with array of Escape Pairs.
     *  <p>
     *  @param exportType key
     *  @param escapePairs Array of <code>EscapePair</code>
     */
    public GMFFUserTextEscapes(final String exportType,
        final EscapePair[] escapePairs)
    {

        this.exportType = exportType;
        escapePairList = new ArrayList<EscapePair>(escapePairs.length);
        for (final EscapePair escapePair : escapePairs)
            escapePairList.add(escapePair);
    }

    /**
     *  Converts to Audit Report string for testing
     *  purposes.
     *  <p>
     *  @return String containing the relevant fields.
     */
    public String generateAuditReportText() {

        final StringBuffer sb = new StringBuffer();

        sb.append(UtilConstants.RUNPARM_GMFF_USER_TEXT_ESCAPES);
        sb.append(GMFFConstants.AUDIT_REPORT_COMMA);
        sb.append(exportType);
        for (final EscapePair ep : escapePairList) {
            sb.append(GMFFConstants.AUDIT_REPORT_COMMA);
            sb.append(ep.num);
            sb.append(GMFFConstants.AUDIT_REPORT_COMMA);
            sb.append(GMFFConstants.AUDIT_REPORT_DOUBLE_QUOTE);
            sb.append(ep.replacement);
            sb.append(GMFFConstants.AUDIT_REPORT_DOUBLE_QUOTE);
        }
        return sb.toString();
    }

    /**
     *  Validates User Text Escape data.
     *  <p>
     *  For ease of use, validation does not stop at the
     *  first error found. Any errors are accumulated in
     *  the Messages object.
     *  <p>
     *  @param exportParmsList ArrayList of GMFFExportParms
     *             used to validate exportType (must be
     *             used in the Export Parms.)
     *  @param messages The Messages object.
     *  @return true if valid otherwise false.
     */
    public boolean areUserTextEscapesValid(
        final ArrayList<GMFFExportParms> exportParmsList,
        final Messages messages)
    {

        boolean valid = true;

        if (isExportTypeValid(exportParmsList, messages)) {}
        else
            valid = false;

        if (isEscapePairListValid(messages)) {}
        else
            valid = false;

        return valid;
    }

    /**
     *  Validates export type.
     *  <p>
     *  <ul>
     *  <li>Must not be null or an empty string
     *  <li>Must not contain whitespace
     *  <li>Must be defined in the exportParmsList.
     *  <p>
     *  @param exportParmsList ArrayList of GMFFExportParms
     *             used to validate exportType (must be
     *             defined in the Export Parms.)
     *  @param messages The Messages object.
     *  @return true if valid otherwise false.
     */
    public boolean isExportTypeValid(
        final ArrayList<GMFFExportParms> exportParmsList,
        final Messages messages)
    {

        if (GMFFExportParms.isPresentWithNoWhitespace(exportType))
            for (final GMFFExportParms ep : exportParmsList)
                if (ep.exportType.equals(exportType))
                    return true;

        messages
            .accumErrorMessage(GMFFConstants.ERRMSG_ESCAPE_EXPORT_TYPE_BAD_MISSING_1
                + exportType);

        return false;
    }

    /**
     *  Validates each <code>EscapePair</code> in the list.
     *  <p>
     *  Uses <code>EscapePair.validateEscapePair()</code>
     *  to perform the validation. Validates every
     *  EscapePair even if errors are found.
     *  <p>
     *  @param messages The Messages object.
     *  @return true if valid otherwise false.
     */
    public boolean isEscapePairListValid(final Messages messages) {

        boolean valid = true;

        for (final EscapePair ep : escapePairList)
            try {
                ep.validateEscapePair(exportType);

            } catch (final GMFFException e) {
                messages.accumErrorMessage(e.getMessage());
                valid = false;
            }
        return valid;
    }

    /**
     *  converts to String
     *
     *  @return returns GMFFUserTextEscapes.exportType string;
     */
    @Override
    public String toString() {
        return exportType;
    }

    /**
     * Computes hashcode for this GMFFUserTextEscapes
     *
     * @return hashcode for the GMFFUserTextEscapes
     *        (GMFFUserTextEscapes.exportType.hashcode())
     */
    @Override
    public int hashCode() {
        return exportType.hashCode();
    }

    /**
     * Compare for equality with another GMFFUserTextEscapes.
     * <p>
     * Equal if and only if the GMFFUserTextEscapes exportType
     * strings are equal
     * and the obj to be compared to this object is not null
     * and is a GMFFUserTextEscapes as well.
     * <p>
     * @param obj another GMFFUserTextEscapes -- otherwise will return false.
     *
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj ? true
            : !(obj instanceof GMFFUserTextEscapes) ? false : exportType
                .equals(((GMFFUserTextEscapes)obj).exportType);
    }

    /**
     * Compares GMFFUserTextEscapes object based on the
     * primary key, exportType.
     *
     * @param obj GMFFUserTextEscapes object to compare to this GMFFUserTextEscapes
     *
     * @return returns negative, zero, or a positive int
     * if this GMFFUserTextEscapes object is less than, equal to
     * or greater than the input parameter obj.
     *
     */
    @Override
    public int compareTo(final Object obj) {
        return exportType.compareTo(((GMFFUserTextEscapes)obj).exportType);
    }

    /**
     *  EXPORT_TYPE sequences by GMFFUserTextEscapes.exportType.
     */
    static public final Comparator EXPORT_TYPE = new Comparator() {
        @Override
        public int compare(final Object o1, final Object o2) {
            return ((GMFFUserTextEscapes)o1).exportType
                .compareTo(((GMFFUserTextEscapes)o2).exportType);
        }
    };
}
