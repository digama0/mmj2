//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/


/**
 *  MinFooterStmt.java  0.01 11/01/2011
 *
 *  Version 0.01:
 *  Nov-01-2011: new.
 */

package mmj.gmff;

/**
 *  General object representing the Footer statement on a
 *  <code>MinProofWorksheet</code>.
 *  <p>
 *  The Footer statement consists of just a "$)" token
 *  beginning in column 1 of the last statement of
 *  a Proof Worksheet. Hence, there is little to typeset...
 *  but we go through the standard process here anyway,
 *  primarly because the user might want to exclude
 *  the Footer from the export.
 */
public class MinFooterStmt extends MinProofWorkStmt {


    /**
     *  Standard MinFooterStmt constructor.
     *  <p>
     *  @param w <code>MinProofWorksheet</code> of
     *				which this statement is a part.
     *  @param slc Array of Array of String representing
     *   			the lines and "chunks" making up the
     *              <code>MinProofWorkStmt</code>.
     */
    public MinFooterStmt(MinProofWorksheet w,
                          String[][]        slc) {

        super(w,
              slc);
    }

    /**
     *  Formats export data for the Footerf statement
     *  according to the <code>Model A</code> specifications
     *  and loads the data into a specified buffer.
     *  <p>
     *  Model A model file(s) for <code>MinGeneratedProofStmt</code> objects
     *  are "optional", meaning that if any of the model files are
     *  not found, the export process is continues normally but
     *  Generated Proof statements are not output as part of the Proof
     *  Worksheet export.
     *  <p>
     *  Additional information may be found \GMFFDoc\GMFFModels.txt.
     *  <p>
     *  @param gmffExporter The <code>GMFFExporter</code> requesting
     *				the export data build.
     *  @param exportBuffer The <code>StringBuffer</code> to which
     *               exported data is to be output.
  	 *  @throws GMFFException if errors are encountered during the
  	 *               export process.
     */
	public void buildModelAExport(
						 	GMFFExporter gmffExporter,
		               	 	StringBuffer exportBuffer)
				            	throws GMFFException {

		String modelAFooter0     =
			gmffExporter.getOptionalModelFile(
				GMFFConstants.MODEL_A_FOOTER0_NAME);
		if (modelAFooter0 == null) {
			return;
		}

		// well all righty then...

		gmffExporter.appendModelFileText(
			exportBuffer,
			modelAFooter0);
	}
}
