//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * AuxFrameGUI.java  0.03 11/01/2011
 *
 * Version 0.01: 02/01/2006
 * ==> New
 *
 * Version 0.02: 08/01/2006
 * ==> Added changeFrameText()
 *       and setCursorToStartOfMessageArea()
 *       for use w/RequestMessagesGUI in ProofAsstGUI.
 *
 * Version 0.03: Nov-01-2011
 * ==> Changes buildFrame() from protected to public access
 *     for mmj2 Paths Enhancement (use MMJ2FailPopupWindow).
 */

package mmj.pa;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.text.DefaultEditorKit;

/**
 * A base class to display an auxiliary frame of information that is standalone
 * from a main application, for example a help page or a quick-and-dirty query
 * report.
 * <p>
 * To use it, just build one with the default constructor, then override
 * variables, such as frameText or frameTitle using the "setter" methods. Then
 * buildFrame() and showFrame() -- see main(), as an example.
 */
public class AuxFrameGUI {

    protected ProofAsstPreferences proofAsstPreferences;
    protected JFrame frame;
    protected JTextArea frameTextArea;
    protected JScrollPane frameScrollPane;
    protected String frameTitle = PaConstants.AUX_FRAME_TITLE_DEFAULT;
    protected String frameText = PaConstants.AUX_FRAME_TEXT_DEFAULT;
    protected int frameRows = PaConstants.AUX_FRAME_NBR_ROWS_DEFAULT;
    protected int frameColumns = PaConstants.AUX_FRAME_NBR_COLUMNS_DEFAULT;
    protected String frameFontFamily = PaConstants.AUX_FRAME_FONT_FAMILY;
    protected Font frameFont;
    protected boolean wordWrap = false;
    protected int frameFontSize;

    /**
     * Default constructor.
     */
    public AuxFrameGUI() {
        this(new ProofAsstPreferences());
    }

    /**
     * The standard constructor.
     *
     * @param proofAsstPreferences variable settings
     */
    public AuxFrameGUI(final ProofAsstPreferences proofAsstPreferences) {
        this.proofAsstPreferences = proofAsstPreferences;
        frameFontSize = proofAsstPreferences.fontSize.get();
        frameFont = new Font(frameFontFamily, 1, frameFontSize);
    }

    public void setFrameFont(final Font font) {
        frameFont = font;
    }

    public void setFrameFontSize(final int i) {
        frameFontSize = i;
    }

    public void increaseFontSize() {
        frameFontSize += 2;
        if (frameFontSize > 72)
            frameFontSize = 72;
        final Font font = frameFont.deriveFont((float)frameFontSize);
        frameFont = font;
        frameTextArea.setFont(frameFont);
        frame.pack();
    }

    public void decreaseFontSize() {
        frameFontSize -= 2;
        if (frameFontSize < 8)
            frameFontSize = 8;
        final Font font = frameFont.deriveFont((float)frameFontSize);
        frameFont = font;
        frameTextArea.setFont(frameFont);
        frame.pack();
    }

    /**
     * Make the frame disappear and go away.
     */
    public void dispose() {
        if (frame != null)
            frame.dispose();
    }

    /**
     * Set word wrap on or off.
     *
     * @param wordWrap true or false.
     */
    public void setWrapStyleWord(final boolean wordWrap) {
        this.wordWrap = wordWrap;
    }

    /**
     * Set the title of the frame to be displayed.
     * <p>
     * Setting the title does not update what is already displayed (though we
     * could modify this routine to make that happen, if desired.)
     *
     * @param frameTitle String title to show.
     */
    public void setFrameTitle(final String frameTitle) {
        this.frameTitle = frameTitle;
    }

    /**
     * Modifies the text already displayed in the frame.
     * <p>
     * Setting the text updates the JTextArea text value after invoking
     * setFrameText(frameText);
     *
     * @param frameText String text area to show.
     */
    public void changeFrameText(final String frameText) {
        setFrameText(frameText);
        frameTextArea.setText(frameText);
    }

    /**
     * Set the text to be displayed in the frame when the frame is displayed.
     * <p>
     * A Java Swing "JTextArea" is used to hold the text.
     * <p>
     * Setting the text does not update what is already displayed (though we
     * could modify this routine to make that happen, if desired.)
     *
     * @param frameText String text area to show.
     */
    public void setFrameText(final String frameText) {
        this.frameText = frameText;
    }

    /**
     * Positions the input caret to start of message text area and scrolls
     * viewport to ensure that the start of the message text area is visible.
     * <p>
     * This is called only after updates to an existing AuxFrameGUI screen. It
     * is automatically invoked during the initial display sequence of events..
     */
    public void setCursorToStartOfMessageArea() {
        try {
            frameTextArea.setCaretPosition(0);
            final JViewport v = frameScrollPane.getViewport();
            v.scrollRectToVisible(
                new Rectangle(/*x=*/0, /*y=*/0, /*width=*/1, /*height=*/1));
        } catch (final Exception e) {
            // ignore, don't care, did our best.
        }
    }

    /**
     * Get the Frame.
     *
     * @return JFrame frame.
     */
    public JFrame getFrame() {
        return frame;
    }

    /**
     * Builds the JFrame with default settings.
     *
     * @return the new JFrame
     */
    public JFrame buildFrame() {

        frame = new JFrame(frameTitle);

        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.getRootPane().registerKeyboardAction(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                dispose();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        frameTextArea = new JTextArea(frameText, frameRows, frameColumns);

        frameTextArea.setFont(frameFont);
        frameTextArea.setLineWrap(true);
        frameTextArea.setWrapStyleWord(wordWrap);
        frameTextArea.setEditable(true);

        final JScrollPane frameScrollPane = new JScrollPane(frameTextArea);

        frameScrollPane.setVerticalScrollBarPolicy(
            ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        frame.getContentPane().add(frameScrollPane);

        final JMenuBar menuBar = new JMenuBar();
        menuBar.add(buildEditMenu());
        frame.setJMenuBar(menuBar);

        return frame;
    }

    private JMenu buildEditMenu() {

        final JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);

        final JMenuItem cutItem = new JMenuItem(
            new DefaultEditorKit.CutAction());
        cutItem.setText("Cut");
        cutItem.setMnemonic(KeyEvent.VK_T);
        cutItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        editMenu.add(cutItem);

        final JMenuItem copyItem = new JMenuItem(
            new DefaultEditorKit.CopyAction());
        copyItem.setText("Copy");
        copyItem.setMnemonic(KeyEvent.VK_C);
        copyItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        editMenu.add(copyItem);

        final JMenuItem pasteItem = new JMenuItem(
            new DefaultEditorKit.PasteAction());
        pasteItem.setText("Paste");
        pasteItem.setMnemonic(KeyEvent.VK_P);
        pasteItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        editMenu.add(pasteItem);

        final JMenuItem largerFontItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                increaseFontSize();
            }
        });
        largerFontItem.setText("Larger Font Size");
        largerFontItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
        editMenu.add(largerFontItem);
        final JMenuItem smallerFontItem = new JMenuItem(new AbstractAction() {
            public void actionPerformed(final ActionEvent e) {
                decreaseFontSize();
            }
        });
        smallerFontItem.setText("Smaller Font Size");
        smallerFontItem.setAccelerator(
            KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        editMenu.add(smallerFontItem);
        return editMenu;

    }

    /**
     * Inner class used to display the Frame later on the event queue thread.
     * <p>
     * We construct the thing and establish it to be displayed on the event
     * queue thread using "EventQueue.invokeLater()".
     */
    protected static class FrameShower implements Runnable {
        JFrame f;

        /**
         * Constructor with Frame input.
         * <p>
         * We construct this thing and establish it to be displayed on the event
         * queue thread.
         *
         * @param showFrame is the Frame to be displayed
         */
        public FrameShower(final JFrame showFrame) {
            f = showFrame;
        }

        /**
         * Run code to display the frame on the event queue thread.
         */
        public void run() {
            f.pack();
            f.setVisible(true);
        }
    }

    /**
     * Show the frame later on the event queue thread.
     * <p>
     * We construct the thing and establish it to be displayed on the event
     * queue thread using "EventQueue.invokeLater()".
     *
     * @param jFrame is the Frame to be displayed
     */
    public void showFrame(final JFrame jFrame) {
        final Runnable runner = new FrameShower(jFrame);

        EventQueue.invokeLater(runner);
    }

    /**
     * main entry point for testing using defaults.
     *
     * @param args Command line argument String array (not used).
     */
    public static void main(final String[] args) {
        final AuxFrameGUI aux = new AuxFrameGUI();
        aux.showFrame(aux.buildFrame());
    }
}
