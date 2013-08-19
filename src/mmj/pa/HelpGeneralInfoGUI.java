//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * HelpGeneralInfoGUI.java  0.01 02/01/2006
 */

package mmj.pa;

/**
 * Displays general information about the Proof Assistant GUI.
 * <p>
 * The information displayed is hardcoded into mmj.pa.PaConstants.java.
 */
public class HelpGeneralInfoGUI extends AuxFrameGUI {

    /**
     * Default constructor.
     */
    public HelpGeneralInfoGUI() {
        super();
        setFrameValues();
    }

    /**
     * Constructor using ProofAsstPreferences settings.
     * 
     * @param proofAsstPreferences variable settings
     */
    public HelpGeneralInfoGUI(final ProofAsstPreferences proofAsstPreferences) {
        super(proofAsstPreferences);
        setFrameValues();
    }

    private void setFrameValues() {
        setFrameText(PaConstants.GENERAL_HELP_INFO_TEXT);
        setFrameTitle(PaConstants.GENERAL_HELP_FRAME_TITLE);
    }

    public static void main(final String[] args) {
        final HelpGeneralInfoGUI h = new HelpGeneralInfoGUI();
        h.showFrame(h.buildFrame());
    }
}
