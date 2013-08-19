//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFFileFilter.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.io.File;
import java.io.FileFilter;

/**
 * GMFFFileFilter is used by the GMFFFolder to select Proof Worksheet files for
 * export from a GMFFFolder.
 * <p>
 * It is used by GMFFFolder but is called by Java via the File method
 * listFiles(fileFilter). call.
 */
public class GMFFFileFilter implements FileFilter {

    String fileSuffix;
    String lowestNamePrefix;

    /**
     * GMFF Constructor for GMFFFileFilter allowing specification of selection
     * criteria for files in a directory.
     * 
     * @param fileSuffix dot followed by file type (e.g. ".mmp"). Selected files
     *            must have matching file suffix.
     * @param lowestNamePrefix Selected files must have names >=
     *            lowestNamePrefix, ignoring case.
     */
    public GMFFFileFilter(final String fileSuffix, final String lowestNamePrefix)
    {
        super();
        this.fileSuffix = fileSuffix.toLowerCase();
        this.lowestNamePrefix = lowestNamePrefix;
    }

    /**
     * FileFilter for MMT Folders.
     * <p>
     * Return true if:
     * <ul>
     * <li>entry in directory is readable file, and
     * <li>has matching file suffix, ignoring case (e.g. ".mmp"), and
     * <li>{@code (namePrefix.compareToIgnoreCase( lowestNamePrefix) >= 0)}
     * </ul>
     * 
     * @param pathname entry in directory.
     * @return true if selected.
     */
    public boolean accept(final File pathname) {
        if (pathname.isFile() && pathname.canRead()) {

            final String name = pathname.getName();

            if (name.toLowerCase().endsWith(fileSuffix)) {

                final String namePrefix = name.substring(0, name.length()
                    - fileSuffix.length());

                if (namePrefix.compareToIgnoreCase(lowestNamePrefix) >= 0)
                    return true;
            }
        }
        return false;
    }
}
