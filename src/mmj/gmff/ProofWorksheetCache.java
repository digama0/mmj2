//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ProofWorksheetCache.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import mmj.lang.Messages;
import mmj.pa.ProofWorksheet;

/**
 * <code>ProofWorksheetCache</code is an iterface and holding area for
 * ProofWorksheet text and loaded objects used in call to Export Via GMFF.
 * <p>
 * The idea is that multiple exports may be created for a single ProofWorksheet,
 * so we don't want to have to perform the ProofWorksheet load action twice.
 * Also, the caller doesn't know whether GMFF will want the old, full-featured
 * ProofWorksheet or the new minimalist MinProofWorksheet. So we provide cache
 * areas for both types of Proof Worksheets which can be reused by GMFF if
 * already loaded.
 */
public class ProofWorksheetCache {

    public String proofText;

    public MinProofWorksheet cachedMinProofWorksheet = null;
    public ProofWorksheet cachedProofWorksheet = null;

    /**
     * Constructor for ProofWorksheetCache.
     * <p>
     * This constructor loads ProofText and initializes the Proof Worksheet
     * cache areas to null;
     * 
     * @param proofText String data holding Proof Worksheet text.
     */
    public ProofWorksheetCache(final String proofText) {
        this.proofText = proofText;
    }

    /**
     * Loads the cached {@code MinProofWorksheet} using the cached
     * {@code proofText} if not already cached, and returns the
     * {@code MinProofWorksheet} to the called.
     * 
     * @param messages The {@code Messages} object.
     * @return MinProofWorksheet either cached or newly loaded.
     * @throws GMFFException if errors found during loading.
     */
    public MinProofWorksheet loadMinProofWorksheet(final Messages messages)
        throws GMFFException
    {

        if (cachedMinProofWorksheet == null) {

            cachedMinProofWorksheet = new MinProofWorksheet(messages);

            cachedMinProofWorksheet.load(proofText);
        }

        return cachedMinProofWorksheet;
    }
}
