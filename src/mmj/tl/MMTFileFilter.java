//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  MMTFileFilter.java  0.01 08/01/2008
 *
 *  Version 0.01:
 *      --> new.
 */

package mmj.tl;
import java.io.FileFilter;
import java.io.File;


/**
 *   MMTFileFilter is used by the Theorem Loader to select
 *   theorem files from the MMT Folder.
 *   <p>
 *   It is used by MMTFolder but is called by Java
 *   via the File method listFiles(fileFilter).
 *   call.
 */
public class MMTFileFilter implements FileFilter {

    /**
     *  FileFilter for MMT Folders.
     *  <p>
     *  @param pathname entry in directory.
     *  @return true if entry in directory has pathname ending
     *          in ".mmt" and is a readable file.
     */
    public boolean accept(File pathname) {
        if (pathname.
                getName().
                    toLowerCase().
                        endsWith(
                            TlConstants.FILE_SUFFIX_MMT)
            &&
            pathname.isFile()
            &&
            pathname.canRead()) {
            return true;
        }
        return false;
    }
}
