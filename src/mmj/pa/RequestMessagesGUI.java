//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * RequestMessagesGUI.java  0.02 11/01/2006
 *
 * 11-Sep-2006 - Version 0.02 - renamed from UnificatinErrorsGUI
 */
package mmj.pa;

/**
 * Displays error messages from ProofAsst Unification.
 */
public class RequestMessagesGUI extends AuxFrameGUI {

    private final String defaultTitle = PaConstants.REQUEST_MESSAGES_GUI_TITLE_DEFAULT;

    private final String defaultText = PaConstants.REQUEST_MESSAGES_GUI_TEXT_DEFAULT;

    /**
     * Default constructor.
     */
    public RequestMessagesGUI() {
        super();
        setFrameText(defaultText);
        setFrameTitle(defaultTitle);
        setWrapStyleWord(true);
    }

    /**
     * Constructor with error text to display.
     * 
     * @param errorText String to display.
     */
    public RequestMessagesGUI(final String errorText) {
        super();
        setFrameText(errorText);
        setFrameTitle(defaultTitle);
        setWrapStyleWord(true);
    }

    /**
     * Constructor with error text to display using ProofAsstPreferences
     * settings.
     * 
     * @param errorText String to display.
     * @param proofAsstPreferences variable settings
     */
    public RequestMessagesGUI(final String errorText,
        final ProofAsstPreferences proofAsstPreferences)
    {
        super(proofAsstPreferences);
        setFrameText(errorText);
        setFrameTitle(defaultTitle);
        setWrapStyleWord(true);
    }

    /**
     * Testing entry point for use with default settings.
     * 
     * @param args Command line String args array.
     */
    public static void main(final String[] args) {
        final RequestMessagesGUI e = new RequestMessagesGUI();
        e.showFrame(e.buildFrame());
    }
}
