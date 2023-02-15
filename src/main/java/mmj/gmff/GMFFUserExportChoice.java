//********************************************************************/
//* Copyright (C) 2011                                               */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * GMFFUserExportChoice.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.List;

import mmj.util.UtilConstants;

/**
 * GMFFUserExportChoice holds the parameters from a single RunParm of the same
 * name.
 * <p>
 * It is basically just a data structure with some attached utility functions on
 * the data elements.
 * <p>
 * The reason for creating this class is that GMFF parameter type RunParms are
 * not validated and processed until GMFF is initialized, typically when the
 * user requests an export. So the RunParms are cached until initialization
 * time.
 * <p>
 */
public class GMFFUserExportChoice {

    public String exportTypeOrAll;

    /**
     * The standard constructor for GMFFUserExportChoice.
     *
     * @param exportTypeOrAll String containing either a single Export Type or
     *            the literal "ALL".
     */
    public GMFFUserExportChoice(final String exportTypeOrAll) {

        this.exportTypeOrAll = exportTypeOrAll;
    }

    /**
     * Converts to Audit Report string for testing purposes.
     *
     * @return String containing the relevant fields.
     */
    public String generateAuditReportText() {
        return UtilConstants.RUNPARM_GMFF_USER_EXPORT_CHOICE.name() + ","
            + exportTypeOrAll;
    }

    /**
     * Validates the User Export Choice.
     *
     * @param exportParmsList List of GMFFExportParms used to validate
     *            exportType (must be used in the Export Parms.)
     * @throws GMFFException if User Export Choice invalid.
     */
    public void validateUserExportChoice(
        final List<GMFFExportParms> exportParmsList) throws GMFFException
    {

        if (GMFFExportParms.isPresentWithNoWhitespace(exportTypeOrAll)) {

            if (exportTypeOrAll
                .equalsIgnoreCase(GMFFConstants.USER_EXPORT_CHOICE_ALL))
                return;

            for (final GMFFExportParms ep : exportParmsList)
                if (ep.exportType.equals(exportTypeOrAll))
                    return;
        }

        throw new GMFFException(
            GMFFConstants.ERRMSG_USER_EXPORT_CHOICE_BAD_MISSING,
            exportTypeOrAll);
    }
}
