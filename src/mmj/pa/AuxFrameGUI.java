//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  AuxFrameGUI.java  0.03 11/01/2011
 *
 *  Version 0.01: 02/01/2006
 *  ==> New
 *
 *  Version 0.02: 08/01/2006
 *  ==> Added changeFrameText()
 *        and setCursorToStartOfMessageArea()
 *        for use w/RequestMessagesGUI in ProofAsstGUI.
 *
 *  Version 0.03: Nov-01-2011
 *  ==> Changes buildFrame() from protected to public access
 *      for mmj2 Paths Enhancement (use MMJ2FailPopupWindow).
 */

package mmj.pa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;

/**
 *  A base class to display an auxiliary frame of information
 *  that is standalone from a main application, for example
 *  a help page or a quick-and-dirty query report.
 *
 *  To use it, just build one with the default constructor,
 *  then override variables, such as frameText or frameTitle
 *  using the "setter" methods. Then buildFrame() and
 *  showFrame() -- see main(), as an example.
 */
public class AuxFrameGUI {

    protected  ProofAsstPreferences
                         proofAsstPreferences;

    protected  JFrame    frame;

    protected  JTextArea frameTextArea;

    protected  JScrollPane
                         frameScrollPane;

    protected  String    frameTitle
                                  =
        PaConstants.AUX_FRAME_TITLE_DEFAULT;

    protected  String    frameText
                                  =
        PaConstants.AUX_FRAME_TEXT_DEFAULT;

    protected  int       frameRows
                                  =
        PaConstants.AUX_FRAME_NBR_ROWS_DEFAULT;

    protected  int       frameColumns
                                  =
        PaConstants.AUX_FRAME_NBR_COLUMNS_DEFAULT;

    protected  String    frameFontFamily
                                  =
        PaConstants.AUX_FRAME_FONT_FAMILY;

    protected  Font      frameFont;

    protected  boolean   wordWrap = false;

    /**
     *  Default constructor.
     */
    public AuxFrameGUI() {
        proofAsstPreferences      =
            new ProofAsstPreferences();
    }

    /**
     *  The standard constructor.
     *
     *  @param proofAsstPreferences variable settings
     */
    public AuxFrameGUI(
                ProofAsstPreferences proofAsstPreferences) {
        this.proofAsstPreferences = proofAsstPreferences;
    }

    /**
     *  Make the frame disappear and go away.
     */
    public void dispose() {
        if (frame != null) {
            frame.dispose();
        }
    }

    /**
     *  Set word wrap on or off.
     *
     *  @param wordWrap true or false.
     */
    public void setWrapStyleWord(boolean wordWrap) {
        this.wordWrap             = true;
    }

    /**
     *  Set the title of the frame to be displayed.
     *
     *  Setting the title does not update what is already
     *  displayed (though we could modify this routine
     *  to make that happen, if desired.)
     *
     *  @param frameTitle String title to show.
     */
    public void setFrameTitle(String frameTitle) {
        this.frameTitle = frameTitle;
    }

    /**
     *  Modifies the text already displayed in the frame.
     *
     *  Setting the text updates the JTextArea text
     *  value after invoking setFrameText(frameText);
     *
     *  @param frameText String text area to show.
     */
    public void changeFrameText(String frameText) {
        setFrameText(frameText);
        frameTextArea.setText(frameText);
    }


    /**
     *  Set the text to be displayed in the frame when
     *  the frame is displayed.
     *
     *  A Java Swing "JTextArea" is used to hold the text.
     *
     *  Setting the text does not update what is already
     *  displayed (though we could modify this routine
     *  to make that happen, if desired.)
     *
     *  @param frameText String text area to show.
     */
    public void setFrameText(String frameText) {
        this.frameText = frameText;
    }

    /**
     *  Positions the input caret to start of message text
     *  area and scrolls viewport to ensure that the start
     *  of the message text area is visible.
     *
     *  This is called only after updates to an existing
     *  AuxFrameGUI screen. It is automatically invoked
     *  during the initial display sequence of events..
     */
    public void setCursorToStartOfMessageArea() {

        try {

            frameTextArea.setCaretPosition(0);

            JViewport v           = frameScrollPane.getViewport();

            v.scrollRectToVisible(
                new Rectangle(0,      // x
                              0,      // y
                              1,      // width
                              1));    // height
        }
        catch (Exception e) {
            //ignore, don't care, did our best.
        }
    }

    /**
     *  Get the Frame.
     *
     *  @return      JFrame frame.
     */
    public JFrame getFrame() {
        return frame;
    }

	/**
	 *  Builds the JFrame with default settings.
	 */
    public JFrame buildFrame() {

        Font   frameFont          =
                    new Font(frameFontFamily,
                             Font.BOLD,
                             proofAsstPreferences.getFontSize());

        frame                     = new JFrame(frameTitle);

        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        frameTextArea             = new JTextArea(frameText,
                                                  frameRows,
                                                  frameColumns);

        frameTextArea.setFont(frameFont);
        frameTextArea.setLineWrap(true);
        frameTextArea.setWrapStyleWord(wordWrap);
        frameTextArea.setEditable(true);

        JScrollPane frameScrollPane
                                  = new JScrollPane(frameTextArea);

        frameScrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(frameScrollPane);

        JMenuBar menuBar          = new JMenuBar();
        menuBar.add(buildEditMenu());
        frame.setJMenuBar(menuBar);

        return frame;
    }

    private JMenu buildEditMenu() {

        JMenu editMenu            = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);


        JMenuItem cutItem         =
            new JMenuItem(
                new DefaultEditorKit.CutAction());
        cutItem.setText("Cut");
        cutItem.setMnemonic(KeyEvent.VK_T);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        editMenu.add(cutItem);


        JMenuItem copyItem         =
            new JMenuItem(
                new DefaultEditorKit.CopyAction());
        copyItem.setText("Copy");
        copyItem.setMnemonic(KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        editMenu.add(copyItem);



        JMenuItem pasteItem         =
            new JMenuItem(
                new DefaultEditorKit.PasteAction());
        pasteItem.setText("Paste");
        pasteItem.setMnemonic(KeyEvent.VK_P);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        editMenu.add(pasteItem);


        return editMenu;



    }

    /**
     *  Inner class used to display the Frame later on the
     *  event queue thread.
     *
     *  We construct the thing and establish it to
     *  be displayed on the event queue thread using
     *  "EventQueue.invokeLater()".
     */
    protected static class FrameShower implements Runnable {
        JFrame f;

        /**
         *  Constructor with Frame input.
         *
         *  We construct this thing and establish it to
         *  be displayed on the event queue thread.
         *
         *  @param showFrame is the Frame to be displayed
         */
        public FrameShower(JFrame showFrame) {
            f                     = showFrame;
        }

        /**
         *  Run code to display the frame on the event queue thread.
         */
        public void run() {
            f.pack();
            f.setVisible(true);
        }
    }

    /**
     *  Show the frame later on the event queue thread.
     *
     *  We construct the thing and establish it to
     *  be displayed on the event queue thread using
     *  "EventQueue.invokeLater()".
     *
     *  @param jFrame is the Frame to be displayed
     */
    public void showFrame(JFrame jFrame) {
        Runnable     runner       =
            new FrameShower(jFrame);

        EventQueue.invokeLater(runner);
    }

    /**
     *  main entry point for testing using defaults.
     *
     *  @param args Command line argument String array (not used).
     */
    public static void main(String[] args) {
        AuxFrameGUI aux           = new AuxFrameGUI();
        aux.showFrame(aux.buildFrame());
    }
}
