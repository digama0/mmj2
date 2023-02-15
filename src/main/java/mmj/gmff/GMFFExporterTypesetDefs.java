//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFExporterTypesetDefs.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.*;

import mmj.lang.Messages;

/**
 * {@code GMFFExporterTypesetDefs} holds the typesetting definitions for a
 * single typesetting definition keyword.
 * <p>
 * It is basically just a data structure with some attached utility functions on
 * the data elements.
 * <p>
 * It contains the {@code typesetDefKeyword} which is used to extract
 * typesetting information from a Metamath $t comment statement and a
 * {@code Map} which holds symbol strings (the Map key) and the corresponding
 * typesetting data (Map value, stored as String.)
 * <p>
 * The typesetting definitions are loaded by {@code TypesetDefCommentParser} and
 * the {@code GMFFExporterTypesetDefs} objects are loaded into
 * {@code GMFFExporters} by {@code GMFFManager}
 * <p>
 * One reason for separating out these data elements into a class of their own
 * is that in theory multiple Exporters could use a single set of typesetting
 * definitions. That is, just because the {@code typesetDefKeyword} is "htmldef"
 * that doesn't mean that only Export Type "html" can use those definitions.
 * Another Export Type called "html2" could be defined, perhaps using Model B to
 * structure its export data.
 * <p>
 */
public class GMFFExporterTypesetDefs {
    public String typesetDefKeyword;
    public Map<String, String> typesetDefMap;

    /**
     * Standard constructor.
     *
     * @param typesetDefKeyword the keyword for extracts from the Metamath $t
     *            comment statement.
     * @param initMapSize the initial size of the Map, which should be about the
     *            same size as the mmj2 Symbol table; every Sym should have a
     *            typesetting definition.
     */
    public GMFFExporterTypesetDefs(final String typesetDefKeyword,
        final int initMapSize)
    {

        this.typesetDefKeyword = typesetDefKeyword;
        typesetDefMap = new HashMap<>(initMapSize);
    }

    /**
     * Converts to Audit Report string for testing purposes.
     *
     * @return String containing the relevant fields.
     */
    public String generateAuditReportText() {
        return String.format(GMFFConstants.EXPORTER_AUDIT_REPORT,
            typesetDefKeyword, typesetDefMap.size());
    }

    /**
     * Prints the typesetting definitions and keyword to the Messages object.
     *
     * @param messages The Messages object.
     */
    public void printTypesetDefs(final Messages messages) {
        final StringBuilder sb = new StringBuilder(
            GMFFConstants.METAMATH_DOLLAR_T_BUFFER_SIZE);

        final Set<String> set = typesetDefMap.keySet();
        final List<String> arrayList = new ArrayList<>(set);
        Collections.sort(arrayList);
        for (final String sym : arrayList)
            sb.append(String.format(GMFFConstants.ERRMSG_TYPESET_DEFS_AUDIT_2,
                sym, typesetDefMap.get(sym)));
        messages.accumMessage(GMFFConstants.ERRMSG_TYPESET_DEFS_AUDIT,
            typesetDefKeyword, sb);
    }
}
