//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  GMFFExporterTypesetDefs.java  0.01 11/01/2011
 *
 *  Version 0.01:
 *  Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.*;

import mmj.lang.Messages;
import mmj.mmio.MMIOConstants;

/**
 *  <code>GMFFExporterTypesetDefs</code> holds the typesetting
 *  definitions for a single typesetting definition
 *  keyword.
 *  <p>
 *  It is basically just a data structure with some
 *  attached utility functions on the data elements.
 *  <p>
 *  It contains the <code>typesetDefKeyword</code>
 *  which is used to extract typesetting information
 *  from a Metamath $t comment statement and a
 *  <code>Map</code> which holds symbol strings (the
 *  Map key) and the corresponding typesetting data
 *  (Map value, stored as String.)
 *  <p>
 *  The typesetting definitions are loaded by
 *  <code>TypesetDefCommentParser</code> and the
 *  <code>GMFFExporterTypesetDefs</code> objects
 *  are loaded into <code>GMFFExporters</code> by
 *  <code>GMFFManager</code>
 *  <p>
 *  One reason for separating out these data elements
 *  into a class of their own is that in theory
 *  multiple Exporters could use a single set of
 *  typesetting definitions. That is, just because
 *  the <code>typesetDefKeyword</code> is "htmldef"
 *  that doesn't mean that only Export Type "html"
 *  can use those definitions. Another Export Type
 *  called "html2" could be defined, perhaps using
 *  Model B to structure its export data.
 *  <p>
 */
public class GMFFExporterTypesetDefs {
    public String typesetDefKeyword;
    public Map<String, String> typesetDefMap;

    /**
     *  Standard constructor.
     *
     *  @param typesetDefKeyword the keyword for extracts
     *           from the Metamath $t comment statement.
     *  @param initMapSize the initial size of the Map,
     *           which should be about the same size as
     *           the mmj2 Symbol table; every Sym should
     *           have a typesetting definition.
     */
    public GMFFExporterTypesetDefs(final String typesetDefKeyword,
        final int initMapSize)
    {

        this.typesetDefKeyword = typesetDefKeyword;
        typesetDefMap = new HashMap<String, String>(initMapSize);
    }

    /**
     *  Converts to Audit Report string for testing
     *  purposes.
     *  <p>
     *  @return String containing the relevant fields.
     */
    public String generateAuditReportText() {
        final String s = new String(
            GMFFConstants.INITIALIZATION_AUDIT_REPORT_6_TD_2
                + typesetDefKeyword
                + GMFFConstants.INITIALIZATION_AUDIT_REPORT_6_TD_3
                + typesetDefMap.size());
        return s;
    }

    /**
     *  Prints the typesetting definitions and keyword to
     *  the Messages object.
     *  <p>
     *  @param messages The Messages object.
     */
    public void printTypesetDefs(final Messages messages) {
        final StringBuilder sb = new StringBuilder(
            GMFFConstants.METAMATH_DOLLAR_T_BUFFER_SIZE);
        sb.append(GMFFConstants.ERRMSG_TYPESET_DEFS_AUDIT_1);
        sb.append(typesetDefKeyword);
        sb.append(MMIOConstants.NEW_LINE_CHAR);

        final Set<String> set = typesetDefMap.keySet();
        final List<String> arrayList = new ArrayList<String>(set.size());
        arrayList.addAll(set);
        Collections.sort(arrayList);
        for (final String sym : arrayList) {
            sb.append(GMFFConstants.ERRMSG_TYPESET_DEFS_AUDIT_2);
            sb.append(sym);
            final String repl = typesetDefMap.get(sym);
            sb.append(GMFFConstants.ERRMSG_TYPESET_DEFS_AUDIT_3);
            sb.append(repl);
            sb.append(MMIOConstants.NEW_LINE_CHAR);
        }
        messages.accumInfoMessage(sb.toString());
    }
}
