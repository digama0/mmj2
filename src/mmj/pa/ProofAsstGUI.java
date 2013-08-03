//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  ProofAsstGUI.java  0.10 11/01/2011
 *
 *  Version 0.02:
 *  ==> Add renumber feature
 *
 *  09-Sep-2006 - Version 0.03 - TMFF enhancement.
 *
 *  Version 0.04 06/01/2007
 *  ==> misc.
 *
 *  Version 0.05 08/01/2007
 *  ==> Modified to not rebuild the RequestMessagesGUI frame
 *      each time. The user should position the screen and
 *      resize it so that it is visible underneath (or above)
 *      the ProofAsstGUI screen -- or just Alt-Tab to view
 *      any messages.
 *
 *  Version 0.06 09/11/2007
 *  ==> Bug fix -> set foreground/background at initialization.
 *  ==> Modify setProofTextAreaCursorPos(ProofWorksheet w) to
 *      compute the column number of the ProofAsstCursor's
 *      fieldId.
 *  ==> Added stuff for new "Set Indent" and
 *      "Reformat Proof: Swap Alt" menu items.
 *
 *  Version 0.07 02/01/2008
 *  ==> Add "accelerator" key definitions for
 *          Edit/Increase Font Size = Ctrl + "="
 *          Edit/Decrease Font Size = Ctrl + "-"
 *          Edit/Reformat Proof     = Ctrl + "R"
 *      Note: Ctrl + "+" seems to require Ctrl-Shift + "+",
 *            so in practice we code for Ctrl + "=", since
 *            "=" and "+" are most often on the same physical
 *            key and "=" is the unshifted glyph.
 *      Note: These Ctrl-Plus/Ctrl-Minus commands to increase/
 *            decrease font size are familiar to users of
 *            the Mozilla browser...
 *  ==> Fix bug: Edit/Decrease Font Size now checks for
 *            minimum font size allowed (8) and does not
 *            allow further reductions (a request to go from
 *            8 to 6 is treated as a change from 8 to 8.) This
 *            bug manifested as 'Exception in thread
 *            "AWT-EventQueue-0" java.lang.ArithmeticException:
 *            / by zero at javax.swing.text.PlainView.paint(
 *            Unknown Source)'. Also added similar range checking
 *            for Edit/Increase Font Size.
 *  ==> Modify request processing for unify and tmffReformat
 *      to pass offset of caret plus one as "inputCaretPos"
 *      for use in later caret positioning.
 *  ==> Tweak: Do not reformat when format number or indent
 *             amount is changed. This allows for single step
 *             reformatting -- but requires that the user
 *             manually initiate reformatting after changing
 *             format number or indent amount.
 *  ==> Add "Reformat Step" and "Reformat Step: Swap Alt" to
 *      popup menu. Then modified tmffReformat-related stuff
 *      to pass the boolean "inputCursorStep" to the standard
 *      reformatting procedure(s) so that the request can be
 *      handled using the regular, all-steps logic.
 *  ==> Turn "Greetings, friend" literal into PaConstants
 *      constant, PROOF_ASST_GUI_STARTUP_MSG.
 *  ==> Add "Incomplete Step Cursor" Edit menu item.
 *
 *  Version 0.08 03/01/2008
 *  ==> Add StepSelectorSearch to Unify menu
 *  ==> Add "callback" function for use by StepSelectionDialog,
 *          proofAsstGUI.unifyWithStepSelectorChoice()
 *  ==> Add Unify + Rederive to Unify menu
 *  ==> Eliminate Unify + Get Hints from Unify Menu
 *
 *  Version 0.09 08/01/2008
 *  ==> Add TheoremLoader stuff.
 *
 *  Version 0.10 - Nov-01-2011:  comment update.
 *  ==> Add File Menu Export Via GMFF item
 *  ==> Modified to use Cursor dontScroll flag
 *  ==> Added Ctrl keys for File menu items:
 *      - fileOpenItem     = Ctrl-P
 *      - fileGetProofItem = Ctrl-G
 */

package mmj.pa;

import mmj.pa.*;
import mmj.lang.Assrt;
import mmj.lang.Theorem;
import mmj.lang.Messages;
import mmj.tmff.TMFFException;
import mmj.tmff.TMFFConstants;
import mmj.tl.*;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import javax.swing.undo.*;

/**
 *  The <code>ProofAsstGUI</code> class is the main user
 *  interface for the mmj2 Proof Assistant feature.
 *  <p>
 *  A proof is represented in the GUI as a single text
 *  area, and the GUI knows nothing about the contents
 *  inside; all work on the proof is done elsewhere via
 *  mmj.pa.ProofAsst.java.
 *  <p>
 *  Note: ProofAsstGUI is single-threaded in the ProofAsst
 *  process which is triggered in BatchMMJ2. The RunParm
 *  that triggers ProofAsstGUI does not terminate until
 *  ProofAsstGUI terminates.
 *  <p>
 *  The main issues dealt with in the GUI have to do with
 *  doing all of the screen updating code on the Java
 *  event thread. Unification is performed using a separate
 *  thread which "calls back" to ProofAsstGUI when/if the
 *  Unificatin process is complete. (As of February 2006,
 *  the longest theorem unification computation is around
 *  1/2 second.)
 */
public class ProofAsstGUI {

    // save constructor parms: proofAsst, proofAsstPreferences
    private ProofAsst               proofAsst;
    private ProofAsstPreferences
                                    proofAsstPreferences;

    private TheoremLoader           theoremLoader;
    private TlPreferences           tlPreferences;

    private JFrame                  mainFrame;

    private JTextArea               proofTextArea;

    private JScrollPane             proofTextScrollPane;

    private JSplitPane              myPane;
    private JTextArea               proofMessageArea;
    private JScrollPane             proofMessageScrollPane;

    private String                  proofTheoremLabel = "";

    private ProofTextChanged        proofTextChanged;
    private int                     nbrTimesSavedSinceNew;

    private UndoManager             undoManager;
    private ProofAsstGUIUndoableEditListener
            proofAsstGUIUndoableEditListener;
    private JMenuItem               editUndoItem;
    private JMenuItem               editRedoItem;

    private JFileChooser            fileChooser;
    private String                  screenTitle;

    private JFileChooser            mmtFolderChooser;

    private JMenuItem               cancelRequestItem;

    private JMenuItem               fontStyleBoldItem;
    private JMenuItem               fontStylePlainItem;

    private JPopupMenu              popupMenu;

    private RequestThreadStuff
                                    requestThreadStuff;

    private Font                    proofFont;

    private RequestMessagesGUI      requestMessagesGUI;

    private StepSelectorDialog      stepSelectorDialog;
    private ProofAsstGUI            proofAsstGUI;

    /**
     *  Sequence number of Proof Worksheet theorem.
     *  <p>
     *  Set to MObj.seq if proof theorem already exists.
     *  Otherwise, set to LOC_AFTER stmt sequnce + 1
     *  if LOC_AFTER input (else Integer.MAX_VALUE).
     */
    private int currProofMaxSeq   = Integer.MAX_VALUE;

    /**
     *  Get sequence number of Proof Worksheet theorem.
     *  <p>
     *  Equals MObj.seq if proof theorem already exists.
     *  Otherwise, set to LOC_AFTER stmt sequnce + 1
     *  if LOC_AFTER input (else Integer.MAX_VALUE).
     *
     *  @return currProofMaxSeq number of Proof Worksheet theorem.
     */
    public int getCurrProofMaxSeq() {
        return currProofMaxSeq;
    }

    /**
     *  Set sequence number of Proof Worksheet theorem.
     *  <p>
     *  Equals MObj.seq if proof theorem already exists.
     *  Otherwise, set to LOC_AFTER stmt sequnce + 1
     *  if LOC_AFTER input (else Integer.MAX_VALUE).
     *
     *  @param currProofMaxSeq number of Proof Worksheet theorem.
     */
    public void setCurrProofMaxSeq(int currProofMaxSeq) {
        this.currProofMaxSeq      = currProofMaxSeq;
    }

    /**
     *  Default constructor used only in test mode when
     *  ProofAsstGUI invoked directly from command line.
     */
    public ProofAsstGUI() {
        proofAsst                 = null;
        proofAsstPreferences      = new ProofAsstPreferences();
        proofAsstGUI              = this;

        buildFileChooser(
            new File(
                    PaConstants.SAMPLE_PROOF_LABEL
                    + proofAsstPreferences.getDefaultFileNameSuffix()));

        updateScreenTitle(fileChooser.getSelectedFile());

        buildGUI(PaConstants.SAMPLE_PROOF_TEXT);
    }

    /**
     *  Normal constructor for setting up ProofAsstGUI.
     *
     *  @param proofAsst ProofAsst object
     *  @param proofAsstPreferences variable settings
     *  @param theoremLoader mmj.tl.TheoremLoader object
     */
    public ProofAsstGUI(ProofAsst            proofAsst,
                        ProofAsstPreferences proofAsstPreferences,
                        TheoremLoader        theoremLoader) {

        this.proofAsst            = proofAsst;
        this.proofAsstPreferences = proofAsstPreferences;

        this.theoremLoader        = theoremLoader;
        tlPreferences             = theoremLoader.getTlPreferences();
        buildMMTFolderChooser();

        proofAsstGUI              = this;

        File startupProofWorksheetFile
                                  =
            proofAsstPreferences.getStartupProofWorksheetFile();


        if (startupProofWorksheetFile == null) {
            if (proofAsstPreferences.getProofFolder() != null) {
                buildFileChooser(
                    new File(
                        proofAsstPreferences.getProofFolder(),
                        PaConstants.SAMPLE_PROOF_LABEL
                        + proofAsstPreferences.
                            getDefaultFileNameSuffix()));
            }
            else {
                buildFileChooser(
                    new File(
                        PaConstants.SAMPLE_PROOF_LABEL
                        + proofAsstPreferences.
                            getDefaultFileNameSuffix()));
            }
            updateScreenTitle(fileChooser.getSelectedFile());
            buildGUI(PaConstants.SAMPLE_PROOF_TEXT);
        }
        else {
            buildFileChooser(
                startupProofWorksheetFile);
            updateScreenTitle(fileChooser.getSelectedFile());
            buildGUI(
                readProofTextFromFile(
                    startupProofWorksheetFile));
        }
    }

    public void unifyWithStepSelectorChoice(
                            StepRequest stepRequest) {

        startUnificationAction(false, // no renum
                               null,  // no preprocess request
                               stepRequest, // s/b SELECTOR_CHOICE
                               null); // no TL Request
    }

    private void buildGUI(String newProofText) {

        displayRequestMessagesGUI(
            PaConstants.PROOF_ASST_GUI_STARTUP_MSG);

        JFrame.setDefaultLookAndFeelDecorated(true);

        mainFrame                 = buildMainFrame();

        JMenuBar menuBar          = buildProofMenuBar();

        mainFrame.setJMenuBar(menuBar);

        popupMenu                 = buildPopupMenu();

        myPane                    =
            new JSplitPane(JSplitPane.VERTICAL_SPLIT);

        if (proofAsstPreferences.getTextAtTop()) {
            buildGUIProofTextStuff(newProofText);
            buildGUIMessageTextStuff();
        }
        else {
            buildGUIMessageTextStuff();
            buildGUIProofTextStuff(newProofText);
        }

        mainFrame.getContentPane().add(myPane);

    }

    private void buildGUIProofTextStuff(String newProofText) {
        proofTextArea             =
            buildProofTextArea(newProofText);

        proofTextScrollPane       =
            buildProofTextScrollPane(
                proofTextArea);

        proofTextArea.
            addMouseListener(new PopupMenuListener());

        proofTextArea.
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        unifyWithStepSelectorChoice(
                            new StepRequest(
                                PaConstants.
                                    STEP_REQUEST_SELECTOR_SEARCH));

                    }
                }
            });

        myPane.add(proofTextScrollPane);

    }

    private void buildGUIMessageTextStuff() {
        proofMessageArea          =
            buildProofMessageArea(
                PaConstants.PROOF_ASST_GUI_STARTUP_MSG);

        proofMessageScrollPane    =
            buildProofMessageScrollPane(
                proofMessageArea);

        proofMessageArea.addMouseListener(
            new PopupMenuListener());

        myPane.add(proofMessageScrollPane);
    }

    private void updateMainFrameTitle() {
        mainFrame.setTitle(screenTitle);
    }

    private void buildMMTFolderChooser() {
        MMTFolder mmtFolder       = tlPreferences.getMMTFolder();
        if (mmtFolder.getFolderFile() == null) {
            mmtFolderChooser      =
                new JFileChooser();
        }
        else {
            mmtFolderChooser      =
                new JFileChooser(mmtFolder.getFolderFile());
            mmtFolderChooser.
                setSelectedFile(mmtFolder.getFolderFile());
        }

        mmtFolderChooser.
            setFileSelectionMode(
                JFileChooser.DIRECTORIES_ONLY);
        mmtFolderChooser.
            setAcceptAllFileFilterUsed(false);
    }

    private void buildFileChooser(File defaultFile) {

        fileChooser               =
            new JFileChooser(proofAsstPreferences.getProofFolder());

        fileChooser.addChoosableFileFilter(
            new proofAsstFileFilter());

        fileChooser.setAcceptAllFileFilterUsed(false);

        fileChooser.setSelectedFile(defaultFile);
    }

    private void updateFileChooserFileForProofLabel(String label) {
        File prevFile             =
                fileChooser.getSelectedFile();

        String prevParent         = null;

        if (prevFile != null) {
            prevParent            = prevFile.getParent();
        }

        File newFile              =
            new File(prevParent,
                     label
                     + proofAsstPreferences.
                        getDefaultFileNameSuffix());

        fileChooser.setSelectedFile(newFile);

    }


    private class proofAsstFileFilter
                extends javax.swing.filechooser.FileFilter {
        public boolean accept(File file) {
            if (file.isDirectory()) {
                return true;
            }
            String fileName = file.getName();
            if (fileName != null
                &&
                (fileName.endsWith(
                    PaConstants.
                        PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP)
                     ||
                 fileName.endsWith(
                    PaConstants.
                        PA_GUI_FILE_CHOOSER_FILE_SUFFIX_MMP2)
                     ||
                 fileName.endsWith(
                    PaConstants.
                        PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT)
                     ||
                 fileName.endsWith(
                    PaConstants.
                        PA_GUI_FILE_CHOOSER_FILE_SUFFIX_TXT2))) {
                return true;
            }
            return false;
        }
        public String getDescription() {
            return PaConstants.PA_GUI_FILE_CHOOSER_DESCRIPTION;
        }

    }

    private class ProofTextChanged implements DocumentListener {
        boolean changes;
        public ProofTextChanged(boolean changes) {
            this.changes = changes;
        }
        public synchronized boolean getChanges() {
            return changes;
        }
        public synchronized void setChanges(
                                    boolean changes) {
            this.changes = changes;
        }
        public void changedUpdate(DocumentEvent e) {
            setChanges(true);
        }
        public void insertUpdate(DocumentEvent e) {
            setChanges(true);
        }
        public void removeUpdate(DocumentEvent e) {
            setChanges(true);
        }
    }

    private void clearUndoRedoCaches() {
        if (proofAsstPreferences.getUndoRedoEnabled()) {
            undoManager.discardAllEdits();
            updateUndoRedoItems();
        }
    }

    private class ProofAsstGUIUndoableEditListener
                   implements UndoableEditListener {
        public void undoableEditHappened(
                        UndoableEditEvent undoableEditEvent) {
            undoManager.addEdit(
                undoableEditEvent.getEdit());
            updateUndoRedoItems();
        }
    }

    private void updateUndoRedoItems() {

        if (undoManager.canUndo()) {
            editUndoItem.setEnabled(true);
        }
        else {
            editUndoItem.setEnabled(false);
        }

        if (undoManager.canRedo()) {
            editRedoItem.setEnabled(true);
        }
        else {
            editRedoItem.setEnabled(false);
        }
    }


    public JFrame getMainFrame() {
        return mainFrame;
    }
    private String getProofTextAreaText() {
        return proofTextArea.getText();
    }
    private void setProofTextAreaText(String s) {
        proofTextArea.setText(s);
    }

    private void setProofTextAreaCursorPos(
                                ProofWorksheet w,
                                int            proofTextLength) {

        ProofAsstCursor cursor
                                  = w.getProofCursor();

        if (cursor.proofWorkStmt != null) {

            int n                 =
                w.computeProofWorkStmtLineNbr(cursor.proofWorkStmt);

            if (n < 1) {
                //default it
                cursor.caretLine  = 1;
                cursor.caretCol   = 1;
            }
            else {
                cursor.caretLine  = n;
                cursor.caretCol   =
                    cursor.proofWorkStmt.
                        computeFieldIdCol(
                            cursor.fieldId);
            }

            cursor.proofWorkStmt  = null;
            cursor.caretCharNbr   = -1; // just in case
            cursor.scrollToLine   = -1; // just in case
            cursor.scrollToCol    = -1; // just in case
        }

        setProofTextAreaCursorPos(cursor,
                                  proofTextLength);
    }

    private void setProofTextAreaCursorPos(
                            ProofAsstCursor cursor,
                            int             proofTextLength) {
        try {

            int caretPosition     = 0;
            int row               = 0;
            int col               = 0;

            if (cursor.caretCharNbr > 0) {
                caretPosition     = cursor.caretCharNbr - 1;
            }
            else {
                if (cursor.caretLine > 0) {
                    row           = cursor.caretLine - 1;
                    if (cursor.caretCol > 0) {
                        col       = cursor.caretCol - 1;
                    }
                    else {
                        col       = 0;
                    }
                    int offset    =
                        proofTextArea.getLineStartOffset(row);
                    caretPosition = offset + col;
                }
            }

            // just to be safe instead of sorry...
            if (caretPosition >= proofTextLength) {
                caretPosition     = proofTextLength - 1;
            }
            if (caretPosition < 0) {
                caretPosition     = 0;
            }

        	proofTextArea.setCaretPosition(caretPosition);

            JViewport v           =
                proofTextScrollPane.getViewport();

            int vHeight           = v.getView().getHeight();

            row                   = 0;
            col                   = 0;

            if (cursor.scrollToLine != cursor.caretLine
                &&
                cursor.scrollToLine > 0
                &&
                cursor.caretLine > 0) {
                row               = cursor.scrollToLine - 1;
            }
            else {
                if (cursor.caretCharNbr > 0) {
                    row           =
                        proofTextArea.getLineOfOffset(
                            cursor.caretCharNbr - 1);
                }
                else {
                    if (cursor.caretLine > 0) {
                        row       = cursor.caretLine - 1;
                    }
                }
            }

            if (cursor.scrollToCol > 0) {
                col               = cursor.scrollToCol - 1;
            }

            int vPos              =
                (vHeight * row) /
                proofTextArea.getLineCount();

            if (cursor.getDontScroll()) {
				cursor.setDontScroll(false);
			}
			else {
				v.scrollRectToVisible(
					new Rectangle(col,    // x
								  vPos,   // y
								  1,      // width
								  1));    // height
			}
        }
        catch (Exception e) {
            //ignore, don't care, did our best.
        }
    }

    private String getProofTheoremLabel() {
        return proofTheoremLabel;
    }
    private void setProofTheoremLabel(String s) {
        proofTheoremLabel = s;
    }
    private void setNbrTimesSavedSinceNew(int n) {
        nbrTimesSavedSinceNew = n;
    }
    private void incNbrTimesSavedSinceNew() {
        ++nbrTimesSavedSinceNew;
    }
    private int getNbrTimesSavedSinceNew() {
        return nbrTimesSavedSinceNew;
    }

    private void updateScreenTitle() {
        screenTitle               = buildScreenTitle(null);
    }

    private void updateScreenTitle(File file) {
        screenTitle               = buildScreenTitle(file);
    }

    /*
     *  Build title using ProofAsstGUI caption + full path name.
     *  If title length > textColumns - 15
     *      build title using ProofAsstGUI caption + just file name
     *          if title length > textColumns - 15
     *              build title using just file name
     *              if title length > textColumns - 15
     *                  build title using just ProofAsstGUI caption.
     */
    private String buildScreenTitle(File file) {
        int maxLength             =
            proofAsstPreferences.getTextColumns()
            - 15;

        if (file == null ||
            file.getName().length() > maxLength) {
            return PaConstants.PROOF_ASST_FRAME_TITLE;
        }

        StringBuffer s            = new StringBuffer(maxLength);

        s.append(PaConstants.PROOF_ASST_FRAME_TITLE);

        if (appendToScreenTitle(s, " - ") < 0) {
            return s.toString();
        }

        if (appendToScreenTitle(s, file.getPath()) < 0) {
            if (appendToScreenTitle(s, file.getName()) < 0) {
                return file.getName();
            }
        }
        return s.toString();
    }
    private int appendToScreenTitle(StringBuffer s,
                                    String       t) {
        if (t.length() > s.capacity()) {
            return -1;
        }
        s.append(t);
        return 0;
    }

    private JFrame buildMainFrame() {

        JFrame frame              =
            new JFrame(screenTitle);

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if (saveIfAskedBeforeExit(
                            PaConstants.
                                PA_GUI_ACTION_BEFORE_SAVE_EXIT)
                        == JOptionPane.CANCEL_OPTION) {
                        return;
                    }
                    System.exit(0);
                }
            }
        );


        return frame;
    }

    private JTextArea buildProofMessageArea(String text) {
        JTextArea textArea        =
            new JTextArea(
                text,
                proofAsstPreferences.getErrorMessageRows(),
                proofAsstPreferences.getErrorMessageColumns());
        Font   frameFont          =
                    new Font(PaConstants.AUX_FRAME_FONT_FAMILY,
                             Font.BOLD,
                             proofAsstPreferences.getFontSize());

        textArea.setFont(frameFont);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(true);
        return textArea;
    }

    private JScrollPane buildProofMessageScrollPane(
                            JTextArea proofMessageArea) {
        JScrollPane scrollPane    = new JScrollPane(proofMessageArea);
        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        return scrollPane;
    }

    private JTextArea buildProofTextArea(String text) {

        JTextArea textArea        =
                new JTextArea(
                    text,
                    proofAsstPreferences.getTextRows(),
                    proofAsstPreferences.getTextColumns());

        buildProofFont();

        textArea.setFont(proofFont);
        textArea.setLineWrap(proofAsstPreferences.getLineWrap());
        textArea.setCursor(null);  //use arrow instead of thingamabob
        textArea.setTabSize(
            PaConstants.PROOF_TEXT_TAB_LENGTH); //disable it using 1.
        textArea.setForeground(
            proofAsstPreferences.getForegroundColor());
        textArea.setBackground(
            proofAsstPreferences.getBackgroundColor());

        proofTextChanged          = new ProofTextChanged(false);
        (textArea.getDocument()).
            addDocumentListener(
                proofTextChanged);

        if (proofAsstPreferences.getUndoRedoEnabled()) {
            undoManager           =
                new UndoManager();

            proofAsstGUIUndoableEditListener
                                  =
                new ProofAsstGUIUndoableEditListener();

            (textArea.getDocument()).
                addUndoableEditListener(
                    proofAsstGUIUndoableEditListener);
        }

        setNbrTimesSavedSinceNew(0);

        return textArea;
    }

    private void buildProofFont() {
        if (proofAsstPreferences.getFontBold()) {
            proofFont             =
                new Font(proofAsstPreferences.getFontFamily(),
                         Font.BOLD,
                         proofAsstPreferences.getFontSize());
        }
        else {
            proofFont             =
                new Font(proofAsstPreferences.getFontFamily(),
                         Font.PLAIN,
                         proofAsstPreferences.getFontSize());
        }
    }

    private JScrollPane buildProofTextScrollPane(
                            JTextArea proofTextArea) {

        JScrollPane scrollPane    = new JScrollPane(proofTextArea);

        scrollPane.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        return scrollPane;
    }

    private JPopupMenu buildPopupMenu() {
        JPopupMenu m              = new JPopupMenu();

        JMenuItem i;

        i                         =
            new JMenuItem(new DefaultEditorKit.CutAction());
        i.setText(PaConstants.PA_GUI_EDIT_MENU_CUT_ITEM_TEXT);
        m.add(i);

        i                         =
            new JMenuItem(new DefaultEditorKit.CopyAction());
        i.setText(PaConstants.PA_GUI_EDIT_MENU_COPY_ITEM_TEXT);
        m.add(i);

        i                         =
            new JMenuItem(new DefaultEditorKit.PasteAction());
        i.setText(PaConstants.PA_GUI_EDIT_MENU_PASTE_ITEM_TEXT);
        m.add(i);

        i                         =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doTMFFReformatAction(true);
                    }
                });
        i.setText(
            PaConstants.PA_GUI_POPUP_MENU_REFORMAT_STEP_TEXT);
        m.add(i);


        i                         =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doTMFFReformatSwapAltAction(true);
                    }
                });
        i.setText(
            PaConstants.PA_GUI_POPUP_MENU_REFORMAT_SWAP_ALT_STEP_TEXT);
        m.add(i);


        i                         =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        unifyWithStepSelectorChoice(
                            new StepRequest(
                                PaConstants.
                                    STEP_REQUEST_SELECTOR_SEARCH));
                    }
                });
        i.setText(
            PaConstants.
                PA_GUI_UNIFY_MENU_STEP_SELECTOR_SEARCH_ITEM_TEXT);
        m.add(i);

        i                         =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        reshowStepSelectorDialogAction();
                }
            });
        i.setText(
            PaConstants.
                PA_GUI_UNIFY_MENU_RESHOW_STEP_SELECTOR_DIALOG_ITEM_TEXT);
        m.add(i);

        return m;
    }

    private class PopupMenuListener extends MouseAdapter {
        public void mousePressed(MouseEvent e) {
            popupMenuForMouse(e);
        }
        public void mouseReleased(MouseEvent e) {
            popupMenuForMouse(e);
        }
        public void popupMenuForMouse(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popupMenu.show(
                    e.getComponent(),
                    e.getX(),
                    e.getY());
            }
        }
    }

    private JMenuBar buildProofMenuBar() {
        JMenuBar m                = new JMenuBar();
        m.add(buildFileMenu());
        m.add(buildEditMenu());
        m.add(buildCancelMenu());
        m.add(buildUnifyMenu());
        m.add(buildTLMenu());
        m.add(buildGMFFMenu());
        m.add(buildHelpMenu());
        return m;
    }

    private JMenu buildFileMenu() {

        JMenu fileMenu            =
            new JMenu(PaConstants.PA_GUI_FILE_MENU_TITLE);
        fileMenu.setMnemonic(KeyEvent.VK_F);


        JMenuItem fileSaveItem   =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileSaveAction(false);
                    }
                });
        fileSaveItem.setText(
            PaConstants.PA_GUI_FILE_MENU_SAVE_ITEM_TEXT);
        fileSaveItem.setMnemonic(KeyEvent.VK_S);
        fileSaveItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        fileMenu.add(fileSaveItem);


        JMenuItem fileNewItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileNewAction();
                    }
                });
        fileNewItem.setText(
            PaConstants.PA_GUI_FILE_MENU_NEW_ITEM_TEXT);
        fileNewItem.setMnemonic(KeyEvent.VK_N);
        fileMenu.add(fileNewItem);


        JMenuItem fileNewNextItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileNewNextAction();
                    }
                });
        fileNewNextItem.setText(
            PaConstants.PA_GUI_FILE_MENU_NEW_NEXT_ITEM_TEXT);
        fileNewNextItem.setMnemonic(KeyEvent.VK_E);
        fileMenu.add(fileNewNextItem);


        JMenuItem fileOpenItem    =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileOpenAction();
                    }
                });
        fileOpenItem.setText(
            PaConstants.PA_GUI_FILE_MENU_OPEN_ITEM_TEXT);
        fileOpenItem.setMnemonic(KeyEvent.VK_P);
        fileOpenItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_P, ActionEvent.CTRL_MASK));
        fileMenu.add(fileOpenItem);


        JMenuItem fileGetProofItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileGetProofAction();
                    }
                });
        fileGetProofItem.setText(
            PaConstants.PA_GUI_FILE_MENU_GET_PROOF_ITEM_TEXT);
        fileGetProofItem.setMnemonic(KeyEvent.VK_G);
        fileGetProofItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_G, ActionEvent.CTRL_MASK));
        fileMenu.add(fileGetProofItem);


        JMenuItem fileGetFwdProofItem    =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileGetFwdProofAction();
                    }
                });
        fileGetFwdProofItem.setText(
            PaConstants.PA_GUI_FILE_MENU_GET_FWD_PROOF_ITEM_TEXT);
        fileGetFwdProofItem.setMnemonic(KeyEvent.VK_F);
        fileGetFwdProofItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_F, ActionEvent.CTRL_MASK));
        fileMenu.add(fileGetFwdProofItem);


        JMenuItem fileGetBwdProofItem    =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileGetBwdProofAction();
                    }
                });
        fileGetBwdProofItem.setText(
            PaConstants.PA_GUI_FILE_MENU_GET_BWD_PROOF_ITEM_TEXT);
        fileGetBwdProofItem.setMnemonic(KeyEvent.VK_B);
        fileGetBwdProofItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_B, ActionEvent.CTRL_MASK));
        fileMenu.add(fileGetBwdProofItem);



        JMenuItem fileCloseItem   =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileCloseAction();
                    }
                });
        fileCloseItem.setText(
            PaConstants.PA_GUI_FILE_MENU_CLOSE_ITEM_TEXT);
        fileCloseItem.setMnemonic(KeyEvent.VK_L);
        fileMenu.add(fileCloseItem);



        JMenuItem fileSaveAsItem  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileSaveAsAction();
                    }
                });
        fileSaveAsItem.setText(
            PaConstants.PA_GUI_FILE_MENU_SAVE_AS_ITEM_TEXT);
        fileSaveAsItem.setMnemonic(KeyEvent.VK_A);
        fileMenu.add(fileSaveAsItem);


        JMenuItem fileExportViaGMFFItem  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileExportViaGMFFAction();
                    }
                });
        fileExportViaGMFFItem.setText(
            PaConstants.PA_GUI_FILE_MENU_EXPORT_VIA_GMFF_ITEM_TEXT);
        fileExportViaGMFFItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_1, ActionEvent.CTRL_MASK));

        fileMenu.add(fileExportViaGMFFItem);



        JMenuItem fileExitItem    =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileExitAction();
                    }
                });
        fileExitItem.setText(
            PaConstants.PA_GUI_FILE_MENU_EXIT_ITEM_TEXT);
        fileExitItem.setMnemonic(KeyEvent.VK_X);
        fileExitItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        fileMenu.add(fileExitItem);

        return fileMenu;
    }


    private JMenu buildEditMenu() {
        JMenu editMenu            =
            new JMenu(
                PaConstants.PA_GUI_EDIT_MENU_TITLE);
        editMenu.setMnemonic(KeyEvent.VK_E);

        if (proofAsstPreferences.getUndoRedoEnabled()) {
            editUndoItem          =
                new JMenuItem(
                    new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            startRequestAction(
                                new RequestEditUndo());
                        }
                    });
            editUndoItem.setText(
                PaConstants.PA_GUI_EDIT_MENU_UNDO_ITEM_TEXT);
            editUndoItem.setMnemonic(KeyEvent.VK_U);
            editUndoItem.setEnabled(false);
            editUndoItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
            editMenu.add(editUndoItem);

            editRedoItem          =
                new JMenuItem(
                    new AbstractAction() {
                        public void actionPerformed(ActionEvent e) {
                            startRequestAction(
                                new RequestEditRedo());
                        }
                    });
            editRedoItem.setText(
                PaConstants.PA_GUI_EDIT_MENU_REDO_ITEM_TEXT);
            editRedoItem.setMnemonic(KeyEvent.VK_R);
            editRedoItem.setEnabled(false);
            editRedoItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
            editMenu.add(editRedoItem);
        }


        JMenuItem cutItem         =
            new JMenuItem(
                new DefaultEditorKit.CutAction());
        cutItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_CUT_ITEM_TEXT);
        cutItem.setMnemonic(KeyEvent.VK_T);
        cutItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        editMenu.add(cutItem);


        JMenuItem copyItem        =
            new JMenuItem(
                new DefaultEditorKit.CopyAction());
        copyItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_COPY_ITEM_TEXT);
        copyItem.setMnemonic(KeyEvent.VK_C);
        copyItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        editMenu.add(copyItem);


        JMenuItem pasteItem       =
            new JMenuItem(
                new DefaultEditorKit.PasteAction());
        pasteItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_PASTE_ITEM_TEXT);
        pasteItem.setMnemonic(KeyEvent.VK_P);
        pasteItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        editMenu.add(pasteItem);


        JMenuItem setIncompleteStepCursorItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetIncompleteStepCursorItemAction();
                    }
                });
        setIncompleteStepCursorItem.setText(
            PaConstants.
              PA_GUI_EDIT_MENU_SET_INCOMPLETE_STEP_CURSOR_ITEM_TEXT);
        editMenu.add(setIncompleteStepCursorItem);


        JMenuItem setSoftDjErrorItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetSoftDjErrorItemAction();
                    }
                });
        setSoftDjErrorItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_SET_SOFT_DJ_ERROR_ITEM_TEXT);
        editMenu.add(setSoftDjErrorItem);


        JMenuItem setFontFamilyItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetFontFamilyItemAction();
                    }
                });
        setFontFamilyItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_SET_FONT_FAMILY_ITEM_TEXT);
        editMenu.add(setFontFamilyItem);


        fontStyleBoldItem  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        if (!proofAsstPreferences.getFontBold()) {
                            proofAsstPreferences.setFontBold(true);
                            proofFont
                                  = proofFont.deriveFont(Font.BOLD);
                            proofTextArea.setFont(proofFont);
                            fontStyleBoldItem.setEnabled(false);
                            fontStylePlainItem.setEnabled(true);
                        }

                    }
                });
        fontStyleBoldItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_FONT_STYLE_BOLD_ITEM_TEXT);
        if (proofAsstPreferences.getFontBold()) {
            fontStyleBoldItem.setEnabled(false);
        }
        else {
            fontStyleBoldItem.setEnabled(true);
        }
        editMenu.add(fontStyleBoldItem);


        fontStylePlainItem  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        if (proofAsstPreferences.getFontBold()) {
                            proofAsstPreferences.setFontBold(false);
                            proofFont
                                  = proofFont.deriveFont(Font.PLAIN);
                            proofTextArea.setFont(proofFont);
                            fontStylePlainItem.setEnabled(false);
                            fontStyleBoldItem.setEnabled(true);
                        }

                    }
                });
        fontStylePlainItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_FONT_STYLE_PLAIN_ITEM_TEXT);
        if (proofAsstPreferences.getFontBold()) {
            fontStylePlainItem.setEnabled(true);
        }
        else {
            fontStylePlainItem.setEnabled(false);
        }
        editMenu.add(fontStylePlainItem);


        JMenuItem largerFontItem  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        int fontSize
                                  =
                            proofAsstPreferences.getFontSize()
                            +
                            PaConstants.PROOF_ASST_FONT_SIZE_CHG_AMT;

                        //2007-12-06 - FIX BUG
                        if (fontSize >
                            PaConstants.PROOF_ASST_FONT_SIZE_MAX) {
                            fontSize
                                  =
                            PaConstants.PROOF_ASST_FONT_SIZE_MAX;
                        }

                        proofAsstPreferences.setFontSize(fontSize);

                        Font f    = proofFont.deriveFont(
                                        (float)fontSize); // bad Sun!
                        proofFont = f;
                        proofTextArea.setFont(proofFont);

                    }
                });
        largerFontItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_INC_FONT_ITEM_TEXT);
        largerFontItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_EQUALS, ActionEvent.CTRL_MASK));
        editMenu.add(largerFontItem);


        JMenuItem smallerFontItem =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        int fontSize
                                  =
                            proofAsstPreferences.getFontSize()
                            -
                            PaConstants.PROOF_ASST_FONT_SIZE_CHG_AMT;

                        //2007-12-06 - FIX BUG
                        if (fontSize <
                            PaConstants.PROOF_ASST_FONT_SIZE_MIN) {
                            fontSize
                                  =
                            PaConstants.PROOF_ASST_FONT_SIZE_MIN;
                        }

                        proofAsstPreferences.setFontSize(fontSize);

                        Font f    = proofFont.deriveFont(
                                        (float)fontSize); // bad Sun!
                        proofFont = f;
                        proofTextArea.setFont(proofFont);

                    }
                });
        smallerFontItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_DEC_FONT_ITEM_TEXT);
        smallerFontItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_MINUS, ActionEvent.CTRL_MASK));
        editMenu.add(smallerFontItem);

        JMenuItem setForegroundColorItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetForegroundColorItemAction();
                    }
                });
        setForegroundColorItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_SET_FOREGROUND_ITEM_TEXT);
        editMenu.add(setForegroundColorItem);

        JMenuItem setBackgroundColorItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetBackgroundColorItemAction();
                    }
                });
        setBackgroundColorItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_SET_BACKGROUND_ITEM_TEXT);
        editMenu.add(setBackgroundColorItem);


        JMenuItem setFormatNbrItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetFormatNbrItemAction();
                    }
                });
        setFormatNbrItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_SET_FORMAT_NBR_ITEM_TEXT);
        editMenu.add(setFormatNbrItem);

        JMenuItem setIndentItem   =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetIndentItemAction();
                    }
                });
        setIndentItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_SET_INDENT_ITEM_TEXT);
        editMenu.add(setIndentItem);

        JMenuItem reformatItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doTMFFReformatAction(false);
                    }
                });
        reformatItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_REFORMAT_ITEM_TEXT);
        reformatItem.setMnemonic(KeyEvent.VK_R);
        reformatItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        editMenu.add(reformatItem);

        JMenuItem reformatSwapAltItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doTMFFReformatSwapAltAction(false);
                    }
                });
        reformatSwapAltItem.setText(
            PaConstants.PA_GUI_EDIT_MENU_REFORMAT_SWAP_ALT_ITEM_TEXT);
        reformatSwapAltItem.setMnemonic(KeyEvent.VK_O);
        reformatSwapAltItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        editMenu.add(reformatSwapAltItem);

        return editMenu;
    }

    private JMenu buildCancelMenu() {

        JMenu cancelMenu          =
            new JMenu(
                PaConstants.PA_GUI_CANCEL_MENU_TITLE);
        cancelMenu.setMnemonic(KeyEvent.VK_C);


        cancelRequestItem =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        cancelRequestAction();
                    }
                });
        cancelRequestItem.setText(
            PaConstants.PA_GUI_CANCEL_MENU_KILL_ITEM_TEXT);
        cancelRequestItem.setMnemonic(KeyEvent.VK_K);
        cancelRequestItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_K, ActionEvent.CTRL_MASK));
        cancelRequestItem.setEnabled(false);
        cancelMenu.add(cancelRequestItem);

        return cancelMenu;
    }


    private JMenu buildUnifyMenu() {

        JMenu unifyMenu           =
            new JMenu(
                PaConstants.PA_GUI_UNIFY_MENU_TITLE);
        unifyMenu.setMnemonic(KeyEvent.VK_U);


        JMenuItem startUnificationItem
                                   =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        startUnificationAction(
                            false, // no renum
                            null,  // no preprocess request
                            null,  // no Step Request
                            null); // no TL Request

                    }
                });
        startUnificationItem.setText(
            PaConstants.PA_GUI_UNIFY_MENU_START_ITEM_TEXT);
        startUnificationItem.setMnemonic(KeyEvent.VK_U);
        startUnificationItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_U, ActionEvent.CTRL_MASK));
        unifyMenu.add(startUnificationItem);


        JMenuItem startUnifyWRenumItem
                                   =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        startUnificationAction(
                            true,   // yes, renum
                            null,   // no preprocess request
                            null,   // no Step Request
                            null);  // no TL Request
                    }
                });
        startUnifyWRenumItem.setText(
            PaConstants.PA_GUI_UNIFY_MENU_START_UR_ITEM_TEXT);
        startUnifyWRenumItem.setMnemonic(KeyEvent.VK_R);
        unifyMenu.add(startUnifyWRenumItem);

        JMenuItem startUnifyWRederiveItem
                                   =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        startUnificationAction(
                            true,   // yes, renum
                            new EraseWffsPreprocessRequest(),
                            null,   // no Step Request
                            null);  // no TL Request
                    }
                });
        startUnifyWRederiveItem.setText(
            PaConstants.PA_GUI_UNIFY_MENU_REDERIVE_ITEM_TEXT);
        startUnifyWRederiveItem.setMnemonic(KeyEvent.VK_D);
        unifyMenu.add(startUnifyWRederiveItem);

        JMenuItem startUnifyWStepSelectorSearchItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        unifyWithStepSelectorChoice(
                            new StepRequest(
                                PaConstants.
                                    STEP_REQUEST_SELECTOR_SEARCH));
                    }
                });
        startUnifyWStepSelectorSearchItem.setText(
            PaConstants.
                PA_GUI_UNIFY_MENU_STEP_SELECTOR_SEARCH_ITEM_TEXT);
        startUnifyWStepSelectorSearchItem.
            setMnemonic(KeyEvent.VK_S);
        startUnifyWStepSelectorSearchItem.
            setAccelerator(
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_8,
                    ActionEvent.CTRL_MASK));
        unifyMenu.add(startUnifyWStepSelectorSearchItem);

        //clone of startUnificationItem
        JMenuItem reshowStepSelectorDialogItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        reshowStepSelectorDialogAction();
                    }
                });
        reshowStepSelectorDialogItem.setText(
            PaConstants.
              PA_GUI_UNIFY_MENU_RESHOW_STEP_SELECTOR_DIALOG_ITEM_TEXT);
        reshowStepSelectorDialogItem.
            setMnemonic(KeyEvent.VK_D);
        reshowStepSelectorDialogItem.
            setAccelerator(
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_9,
                    ActionEvent.CTRL_MASK));
        unifyMenu.add(reshowStepSelectorDialogItem);

        JMenuItem setMaxResultsItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetMaxResultsItemAction();
                    }
                });
        setMaxResultsItem.setText(
            PaConstants.PA_GUI_UNIFY_MENU_SET_MAX_RESULTS_ITEM_TEXT);
        unifyMenu.add(setMaxResultsItem);

        JMenuItem setShowSubstitutionsItem     =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetShowSubstitutionsItemAction();
                    }
                });
        setShowSubstitutionsItem.setText(
            PaConstants.PA_GUI_UNIFY_MENU_SET_SHOW_SUBST_ITEM_TEXT);
        unifyMenu.add(setShowSubstitutionsItem);


        return unifyMenu;
    }


    private JMenu buildTLMenu() {

        JMenu tlMenu              =
            new JMenu(
                PaConstants.PA_GUI_TL_MENU_TITLE);
        tlMenu.setMnemonic(KeyEvent.VK_T);


        JMenuItem unifyPlusStoreInLogSysAndMMTFolder
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doUnifyPlusStoreInLogSysAndMMTFolderItemAction();
                    }
                });
        unifyPlusStoreInLogSysAndMMTFolder.setText(
            PaConstants.
                PA_GUI_TL_MENU_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER_TEXT);
        tlMenu.add(unifyPlusStoreInLogSysAndMMTFolder);


        JMenuItem unifyPlusStoreInMMTFolderItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doUnifyPlusStoreInMMTFolderItemAction();
                    }
                });
        unifyPlusStoreInMMTFolderItem.setText(
            PaConstants.
                PA_GUI_TL_MENU_UNIFY_PLUS_STORE_IN_MMT_FOLDER_TEXT);
        tlMenu.add(unifyPlusStoreInMMTFolderItem);


        JMenuItem loadTheoremsFromMMTFolderItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doLoadTheoremsFromMMTFolderItemAction();
                    }
                });
        loadTheoremsFromMMTFolderItem.setText(
            PaConstants.
                PA_GUI_TL_MENU_LOAD_THEOREMS_FROM_MMT_FOLDER_TEXT);
        tlMenu.add(loadTheoremsFromMMTFolderItem);


        JMenuItem extractTheoremToMMTFolderItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doExtractTheoremToMMTFolderItemAction();
                    }
                });
        extractTheoremToMMTFolderItem.setText(
            PaConstants.
                PA_GUI_TL_MENU_EXTRACT_THEOREM_TO_MMT_FOLDER_TEXT);
        tlMenu.add(extractTheoremToMMTFolderItem);


        JMenuItem verifyAllProofs
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doVerifyAllProofsItemAction();
                    }
                });
        verifyAllProofs.setText(
            PaConstants.
                PA_GUI_TL_MENU_VERIFY_ALL_PROOFS_TEXT);
        tlMenu.add(verifyAllProofs);


        JMenuItem setTLMMTFolderItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetTLMMTFolderItemAction();
                    }
                });
        setTLMMTFolderItem.setText(
            PaConstants.PA_GUI_TL_MENU_MMT_FOLDER_TEXT);
        tlMenu.add(setTLMMTFolderItem);


        JMenuItem setTLDjVarsOptionItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetTLDjVarsOptionItemAction();
                    }
                });
        setTLDjVarsOptionItem.setText(
            PaConstants.PA_GUI_TL_MENU_DJ_VARS_OPTION_TEXT);
        tlMenu.add(setTLDjVarsOptionItem);


        JMenuItem setTLAuditMessagesItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetTLAuditMessagesItemAction();
                    }
                });
        setTLAuditMessagesItem.setText(
            PaConstants.PA_GUI_TL_MENU_AUDIT_MESSAGES_TEXT);
        tlMenu.add(setTLAuditMessagesItem);


        JMenuItem setTLStoreMMIndentAmtItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetTLStoreMMIndentAmtItemAction();
                    }
                });
        setTLStoreMMIndentAmtItem.setText(
            PaConstants.PA_GUI_TL_MENU_STORE_MM_INDENT_AMT_TEXT);
        tlMenu.add(setTLStoreMMIndentAmtItem);


        JMenuItem setTLStoreMMRightColItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetTLStoreMMRightColItemAction();
                    }
                });
        setTLStoreMMRightColItem.setText(
            PaConstants.PA_GUI_TL_MENU_STORE_MM_RIGHT_COL_TEXT);
        tlMenu.add(setTLStoreMMRightColItem);


        JMenuItem setTLStoreFormulasAsIsItem
                                  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doSetTLStoreFormulasAsIsItemAction();
                    }
                });
        setTLStoreFormulasAsIsItem.setText(
            PaConstants.PA_GUI_TL_MENU_STORE_FORMULAS_AS_IS_TEXT);
        tlMenu.add(setTLStoreFormulasAsIsItem);


        return tlMenu;
    }


    private JMenu buildGMFFMenu() {

        JMenu gmffMenu            =
            new JMenu(
                PaConstants.PA_GUI_GMFF_MENU_TITLE);
        gmffMenu.setMnemonic(KeyEvent.VK_G);

		//this item is a copy of the File Menu item
        JMenuItem fileExportViaGMFFItem  =
            new JMenuItem(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent e) {
                        doFileExportViaGMFFAction();
                    }
                });
        fileExportViaGMFFItem.setText(
            PaConstants.PA_GUI_FILE_MENU_EXPORT_VIA_GMFF_ITEM_TEXT);
        fileExportViaGMFFItem.setAccelerator(KeyStroke.getKeyStroke(
                    KeyEvent.VK_1, ActionEvent.CTRL_MASK));

        gmffMenu.add(fileExportViaGMFFItem);

        return gmffMenu;
    }

    private JMenu buildHelpMenu() {

        JMenu helpMenu            =
            new JMenu(
                PaConstants.PA_GUI_HELP_MENU_TITLE);
        helpMenu.setMnemonic(KeyEvent.VK_H);

        JMenuItem generalInfoItem =
            new JMenuItem(
                PaConstants.PA_GUI_HELP_MENU_GENERAL_ITEM_TEXT);
        generalInfoItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    HelpGeneralInfoGUI h
                                  = new HelpGeneralInfoGUI(
                                            proofAsstPreferences);
                    h.showFrame(h.buildFrame());
                }
            }
        );
        helpMenu.add(generalInfoItem);

        JMenuItem helpAboutItem =
            new JMenuItem(
                PaConstants.PA_GUI_HELP_ABOUT_ITEM_TEXT);
        helpAboutItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Runtime r     = Runtime.getRuntime();
                    r.gc(); //run garbage collector
                    String about  = new String(
                        PaConstants.HELP_ABOUT_TEXT_1

                      + PaConstants.HELP_ABOUT_TEXT_2
                      + r.maxMemory()
                      + PaConstants.PROOF_WORKSHEET_NEW_LINE

                      + PaConstants.HELP_ABOUT_TEXT_3
                      + r.freeMemory()
                      + PaConstants.PROOF_WORKSHEET_NEW_LINE

                      + PaConstants.HELP_ABOUT_TEXT_4
                      + r.totalMemory()
                      + PaConstants.PROOF_WORKSHEET_NEW_LINE);
                    try {
                        JOptionPane.
                            showMessageDialog(
                                getMainFrame(),
                                about,
                                PaConstants.HELP_ABOUT_TITLE,
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    catch (HeadlessException f) {
                    }
                }
            }
        );
        helpMenu.add(helpAboutItem);



        return helpMenu;
    }

    /**
     *  ===============
     *  Edit menu stuff
     *  ===============
     */

    private void doSetFormatNbrItemAction() {
        int oldFormatNbr          =
            proofAsstPreferences.
                getTMFFPreferences().
                    getCurrFormatNbr();

        int newFormatNbr          =
            getNewFormatNbr(oldFormatNbr);

        if (newFormatNbr != oldFormatNbr) {
            if (newFormatNbr < 0) {
                return;
            }
            proofAsstPreferences.
                getTMFFPreferences().
                    setCurrFormatNbr(newFormatNbr);
          //TWEAK: 2008-02-01 -> Do not reformat when
          //                     format number is changed.
          //doTMFFReformatAction(false);
          //END-TWEAK: 2008-02-01
        }
    }

    private void doSetIndentItemAction() {
        int oldIndent             =
            proofAsstPreferences.
                getTMFFPreferences().
                    getUseIndent();

        int newIndent             =
            getNewIndent(oldIndent);

        if (newIndent != oldIndent) {
            if (newIndent < 0) {
                return;
            }
            proofAsstPreferences.
                getTMFFPreferences().
                    setUseIndent(newIndent);
          //TWEAK: 2008-02-01 -> Do not reformat when
          //                     indent amount is changed.
          //doTMFFReformatAction(false);
          //END-TWEAK: 2008-02-01
        }
    }

    private void doSetForegroundColorItemAction() {
        Color oldColor            =
            proofAsstPreferences.getForegroundColor();
        String colorChooserTitle  = new String(
            PaConstants.PA_GUI_EDIT_MENU_SET_FOREGROUND_ITEM_TEXT
            + PaConstants.COLOR_CHOOSE_TITLE_2
            + Integer.toString(oldColor.getRed())
            + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
            + Integer.toString(oldColor.getGreen())
            + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
            + Integer.toString(oldColor.getBlue()));
        Color newColor            =
            getNewColor(oldColor,
                        colorChooserTitle);
        if (!newColor.equals(oldColor)) {
            proofAsstPreferences.setForegroundColor(newColor);
            proofTextArea.setForeground(newColor);
        }
    }

    private void doSetBackgroundColorItemAction() {
        Color oldColor            =
            proofAsstPreferences.getBackgroundColor();
        String colorChooserTitle  = new String(
            PaConstants.PA_GUI_EDIT_MENU_SET_BACKGROUND_ITEM_TEXT
            + PaConstants.COLOR_CHOOSE_TITLE_2
            + Integer.toString(oldColor.getRed())
            + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
            + Integer.toString(oldColor.getGreen())
            + PaConstants.COLOR_CHOOSE_TITLE_SEPARATOR
            + Integer.toString(oldColor.getBlue()));
        Color newColor            =
            getNewColor(oldColor,
                        colorChooserTitle);
        if (!newColor.equals(oldColor)) {
            proofAsstPreferences.setBackgroundColor(newColor);
            proofTextArea.setBackground(newColor);
        }
    }

    private Color getNewColor(Color  oldColor,
                              String title) {
        Color newColor            =
            JColorChooser.showDialog(
                proofTextArea,
                title,
                oldColor);
        if (newColor == null) {
            newColor              = oldColor;
        }
        return newColor;
    }

    private void doSetIncompleteStepCursorItemAction() {

        String newIncompleteStepCursorOption
                                  =
            getNewIncompleteStepCursorOption();

        if (newIncompleteStepCursorOption != null) {
            proofAsstPreferences.
                setIncompleteStepCursor(
                    newIncompleteStepCursorOption);
        }
    }

    private void doSetSoftDjErrorItemAction() {

        String newSoftDjErrorOption
                                  =
            getNewSoftDjErrorOption();

        if (newSoftDjErrorOption != null) {
            proofAsstPreferences.
                setDjVarsSoftErrorsOption(
                    newSoftDjErrorOption);
        }
    }

    private void doSetFontFamilyItemAction() {
        String oldFontFamily      =
            proofAsstPreferences.getFontFamily();

        String newFontFamily      =
            getNewFontFamily(oldFontFamily);

        if (newFontFamily != null &&
            newFontFamily.compareToIgnoreCase(oldFontFamily) != 0) {
            if (newFontFamily.length() > 0) {
                proofAsstPreferences.setFontFamily(newFontFamily);
                buildProofFont();
                proofTextArea.setFont(proofFont);
            }
        }
    }

    private void doTMFFReformatAction(boolean inputCursorStep) {

        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        // note: pass boolean "changes" to reformat task
        //       so that reformat alone does not count
        //       as changes -- after the reformat we'll
        //       set that proofTextChanged status back
        //       to the way it was here!
        startRequestAction(
            new RequestTMFFReformat(
                    inputCursorStep,
                    proofTextChanged.getChanges()));
    }

    private void doTMFFReformatSwapAltAction(boolean inputCursorStep) {

        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        proofAsstPreferences.
            getTMFFPreferences().
                toggleAltFormatAndIndentParms();

        // note: pass boolean "changes" to reformat task
        //       so that reformat alone does not count
        //       as changes -- after the reformat we'll
        //       set that proofTextChanged status back
        //       to the way it was here!
        startRequestAction(
            new RequestTMFFReformat(
                    inputCursorStep,
                    proofTextChanged.getChanges()));
    }

    private int getNewFormatNbr(int oldFormatNbr) {
        int newFormatNbr          = -1;
        String s                  =
            Integer.toString(oldFormatNbr);

        String formatListString   =
            proofAsstPreferences.
                getTMFFPreferences().
                    getFormatListString();

        String origPromptString   =
               formatListString
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + PaConstants.PA_GUI_SET_FORMAT_NBR_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                break promptLoop; //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                newFormatNbr      =
                    proofAsstPreferences.
                        getTMFFPreferences().
                            validateFormatNbrString(s);
                break promptLoop;
            }
            catch (TMFFException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }

        return newFormatNbr;
    }

    private int getNewIndent(int oldIndent) {
        int newIndent          = -1;
        String s                  =
            Integer.toString(oldIndent);

        String origPromptString   =
               PaConstants.PA_GUI_SET_INDENT_PROMPT
               + Integer.toString(
                   TMFFConstants.TMFF_MAX_INDENT);

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                break promptLoop; //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                newIndent      =
                    proofAsstPreferences.
                        getTMFFPreferences().
                            validateIndentString(s);
                break promptLoop;
            }
            catch (TMFFException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }

        return newIndent;
    }

    private String getNewIncompleteStepCursorOption() {

        String s                  =
            proofAsstPreferences.getIncompleteStepCursorOptionNbr();

        String incompleteStepCursorOptionListString
                                  =
            proofAsstPreferences.
                getIncompleteStepCursorOptionListString();

        String origPromptString   =
               PaConstants.PROOF_ASST_INCOMPLETE_STEP_CURSOR_OPTION_LIST
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + incompleteStepCursorOptionListString
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + PaConstants.
                PA_GUI_SET_INCOMPLETE_STEP_CURSOR_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                return s; //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                return(
                    proofAsstPreferences.
                        validateIncompleteStepCursorOptionNbr(
                            s));
            }
            catch (ProofAsstException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }
    }

    private String getNewSoftDjErrorOption() {

        String s                  =
            proofAsstPreferences.getDjVarsSoftErrorsOptionNbr();

        String softDjErrorOptionListString
                                  =
            proofAsstPreferences.
                getSoftDjErrorOptionListString();

        String origPromptString   =
               PaConstants.PROOF_ASST_SOFT_DJ_ERROR_OPTION_LIST
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + softDjErrorOptionListString
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + PaConstants.PA_GUI_SET_SOFT_DJ_ERROR_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                return s; //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                return(
                    proofAsstPreferences.
                        validateDjVarsSoftErrorsOptionNbr(
                            s));
            }
            catch (ProofAsstException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }
    }


    private String getNewFontFamily(String oldFontFamily) {

        String s                  = oldFontFamily;

        String fontListString     =
            proofAsstPreferences.getFontListString();

        String origPromptString   =
               PaConstants.PROOF_ASST_FONT_FAMILY_LIST
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + fontListString
               + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
               + PaConstants.PA_GUI_SET_FONT_FAMILY_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                break promptLoop; //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                s                 =
                    proofAsstPreferences.
                        validateFontFamily(s);
                break promptLoop;
            }
            catch (ProofAsstException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }

        return s;
    }

    /**
     *  ===============
     *  File menu stuff
     *  ===============
     */

    private void doFileExitAction() {
        if (saveIfAskedBeforeExit(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_EXIT)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }
        System.exit(0);
    }

    private void doFileCloseAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_CLOSE)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        setProofTextAreaText("");

        startRequestAction(
            new RequestUpdateMainFrameTitle(null));

        clearUndoRedoCaches();
        proofTextChanged.setChanges(false);
        setNbrTimesSavedSinceNew(0);
        disposeOfOldSelectorDialog();
    }

    private void doFileNewAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        String newTheoremLabel = getNewTheoremLabel();

        startRequestAction(
            new RequestNewProof(
                newTheoremLabel));
    }

    private void doFileNewNextAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        startRequestAction(
            new RequestNewNextProof(
                getCurrProofMaxSeq()));

    }


    private void doFileGetProofAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        Theorem oldTheorem        = getTheorem();

        if (oldTheorem == null) {
            return;
        }

        startRequestAction(
            new RequestGetProof(oldTheorem,
                                true,
                                false));

    }

    private void doFileGetFwdProofAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        startRequestAction(
            new RequestFwdProof(getCurrProofMaxSeq(),
                                true,   // proof unified
                                false)); // hyps Randomized
    }

    private void doFileGetBwdProofAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_NEW)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        startRequestAction(
            new RequestBwdProof(getCurrProofMaxSeq(),
                                true,   // proof unified
                                false)); // hyps Randomized
    }

    private String getNewTheoremLabel() {
        String s;
        while (true) {
            s                     =
                (JOptionPane.showInputDialog(
                    getMainFrame(),
                    PaConstants.
                        PA_GUI_NEW_THEOREM_LABEL_PROMPT));
            if (s == null) {             //cancelled input
                return s;
            }
            s                     = s.trim();
            if (!s.equals("")) {
                return s;
            }
        }
    }


    private Theorem getTheorem() {
        String s                  = new String("");

        String promptString       =
            PaConstants.PA_GUI_GET_THEOREM_LABEL_PROMPT;

        Theorem theorem;

        while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                return null;  //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString       =
                    PaConstants.PA_GUI_GET_THEOREM_LABEL_PROMPT;
                continue;
            }
            theorem               = proofAsst.getTheorem(s);
            if (theorem != null) {
                return theorem;
            }
            promptString          =
                new String(
                    PaConstants.PA_GUI_GET_THEOREM_LABEL_PROMPT_2_1
                    + s
                    + PaConstants.
                        PA_GUI_GET_THEOREM_LABEL_PROMPT_2_2);
        }
    }

    private void doFileOpenAction() {
        if (saveIfAskedBeforeAction(
                PaConstants.PA_GUI_ACTION_BEFORE_SAVE_OPEN)
            ==
            JOptionPane.CANCEL_OPTION) {
            return;
        }

        int    returnVal;
        File   file;
        String s;
        while (true) {
            returnVal             =
                fileChooser.showOpenDialog(
                    getMainFrame());
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file              = fileChooser.getSelectedFile();
                if (file.exists()) {
                    startRequestAction(
                        new RequestFileOpen(file));
                }
                else {
                    if (getYesNoAnswer(
                            PaConstants.ERRMSG_PA_GUI_FILE_NOTFND_1
                             + file.getAbsolutePath()
                             + PaConstants.
                                ERRMSG_PA_GUI_FILE_NOTFND_2)
                        == JOptionPane.YES_OPTION) {
                        continue;
                    }
                }
            }
            break;
        }
    }

    private int saveIfAskedBeforeExit(String actionCaption) {
        int answer                = JOptionPane.NO_OPTION;
        if (proofTextChanged.getChanges()) {
            answer                =
                getYesNoCancelAnswer(
                    PaConstants.ERRMSG_PA_GUI_SAVE_BEFORE_ACTION_1
                    + actionCaption
                    + PaConstants.ERRMSG_PA_GUI_SAVE_BEFORE_ACTION_2);

            if (answer == JOptionPane.YES_OPTION) {
                doFileSaveAction(true); //saving before exit...
            }
        }
        return answer;
    }

    private int saveIfAskedBeforeAction(String actionCaption) {
        int answer                = JOptionPane.NO_OPTION;
        if (proofTextChanged.getChanges()) {
            answer                =
                getYesNoCancelAnswer(
                    PaConstants.ERRMSG_PA_GUI_SAVE_BEFORE_ACTION_1
                    + actionCaption
                    + PaConstants.ERRMSG_PA_GUI_SAVE_BEFORE_ACTION_2);

            if (answer == JOptionPane.YES_OPTION) {
                doFileSaveAction(false);
            }
        }
        return answer;
    }


    private int getYesNoCancelAnswer(String messageAboutIt) {
        int answer                = JOptionPane.YES_OPTION; //default
        try {
            answer                =
                JOptionPane.showConfirmDialog(
                            getMainFrame(),
                            messageAboutIt,
                            PaConstants.PA_GUI_YES_NO_CANCEL_TITLE,
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                            );
        }
        catch (HeadlessException e) {
        }

        // Per Norm, window close should default to cancel!
        if (answer == JOptionPane.CLOSED_OPTION) {
            answer                = JOptionPane.CANCEL_OPTION;
        }
        return answer;
    }

    private String readProofTextFromFile(File file) {
        String newProofText;

        char[]       cBuffer      = new char[1024];

        StringBuffer sb           = new StringBuffer(cBuffer.length);

        int          len          = 0;
        try {
            BufferedReader r      =
                new BufferedReader(
                    new FileReader(
                        file));
            while ((len = r.read(cBuffer, 0, cBuffer.length)) != -1) {
                sb.append(cBuffer, 0, len);
            }
            r.close();
            newProofText          = new String(sb);
        }
        catch (IOException e) {
            newProofText =
                PaConstants.ERRMSG_PA_GUI_READ_PROOF_IO_ERR_1
                + e.getMessage();
        }

        return newProofText;
    }

    private void doFileSaveAction(boolean exitingNow) {

        File file;
        if (getNbrTimesSavedSinceNew() > 0) {
            file = fileChooser.getSelectedFile();
            if (file.exists()) {
                saveOldProofTextFile(file);
                updateMainFrameTitleIfNecessary(exitingNow);
                return;
            }
            else {
                setNbrTimesSavedSinceNew(0); //should not happen
            }
        }

        int returnVal             =
                fileChooser.showSaveDialog(getMainFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file                  = fileChooser.getSelectedFile();
            if (file.exists()) {
                saveOldProofTextFile(file);
            }
            else {
                saveNewProofTextFile(file);
            }
            updateMainFrameTitleIfNecessary(exitingNow);
        }
    }

    private void doFileSaveAsAction() {
        File newFile;

        File oldFile              = fileChooser.getSelectedFile();

        int  returnVal            =
                fileChooser.showSaveDialog(getMainFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            newFile               = fileChooser.getSelectedFile();
            if (newFile.exists()) {
                if (getYesNoAnswer(
                        PaConstants.ERRMSG_PA_GUI_FILE_EXISTS_1
                        + newFile.getAbsolutePath()
                        + PaConstants.ERRMSG_PA_GUI_FILE_EXISTS_2)
                    == JOptionPane.YES_OPTION) {
                    saveOldProofTextFile(newFile);
                }
                else {
                    fileChooser.setSelectedFile(oldFile);
                }
            }
            else {
                saveNewProofTextFile(newFile);
            }
        }
        updateMainFrameTitleIfNecessary(false);

        // this prevents a title and filename update if the
        // user changes the THEOREM= label now...because they
        // used SaveAs we are taking them at their word that
        // this is the file name to use regardless!!!
        setProofTheoremLabel(null); //tricky - avoid title update

    }

    private void updateMainFrameTitleIfNecessary(
                                    boolean exitingNow) {
        if (!exitingNow) {
            String newScreenTitle =
                buildScreenTitle(
                    fileChooser.getSelectedFile());
            if (screenTitle.compareTo(newScreenTitle) != 0) {
                startRequestAction(
                    new RequestUpdateMainFrameTitle());
            }
        }
    }

    private int getYesNoAnswer(String messageAboutIt) {
        int answer                = JOptionPane.YES_OPTION; //default
        try {
            answer                =
                JOptionPane.showConfirmDialog(
                            getMainFrame(),
                            messageAboutIt,
                            PaConstants.PA_GUI_YES_NO_TITLE,
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE
                            );
        }
        catch (HeadlessException e) {
        }
        return answer;
    }

    private void saveNewProofTextFile(File file) {
        try {
            BufferedWriter w      =
                new BufferedWriter(
                    new FileWriter(
                        file));
            String s              = getProofTextAreaText();
            w.write(s, 0, s.length());
            w.close();
        }
        catch (Throwable e) {
            String s              =
                PaConstants.ERRMSG_PA_GUI_SAVE_IO_ERROR_1
               + e.getMessage();

            JOptionPane.showMessageDialog(
                    getMainFrame(),
                    s,
                    PaConstants.PA_GUI_SAVE_NEW_PROOF_TEXT_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }

        proofTextChanged.setChanges(false);
        clearUndoRedoCaches();
        setNbrTimesSavedSinceNew(1);
    }

    private void saveOldProofTextFile(File file) {
        try {
            BufferedWriter w      =
                new BufferedWriter(
                    new FileWriter(
                        file));
            String s              = getProofTextAreaText();
            w.write(s, 0, s.length());
            w.close();
        }
        catch (Throwable e) {
            String s              =
                PaConstants.ERRMSG_PA_GUI_SAVE_IO_ERROR2_1
                    + e.getMessage();
            JOptionPane.showMessageDialog(
                    getMainFrame(),
                    s,
                    PaConstants.PA_GUI_SAVE_OLD_PROOF_TEXT_TITLE,
                    JOptionPane.ERROR_MESSAGE);
        }

        proofTextChanged.setChanges(false);
        clearUndoRedoCaches();
        incNbrTimesSavedSinceNew();

    }

    private void doFileExportViaGMFFAction() {
        startRequestAction(
            new RequestExportViaGMFF());
	}

    // ------------------------------------------------------
    // | Unify menu stuff                                   |
    // ------------------------------------------------------


    private void doSetShowSubstitutionsItemAction() {

        boolean newShowSubstitutions
                                  = getNewShowSubstitutions();

        proofAsstPreferences.
             setStepSelectorShowSubstitutions(
                        newShowSubstitutions);
    }

    private boolean getNewShowSubstitutions() {

        String s                  =
            Boolean.toString(
                proofAsstPreferences.
                    getStepSelectorShowSubstitutions());

        String origPromptString   =
            PaConstants.PA_GUI_SET_SHOW_SUBST_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) { //cancelled input
                return proofAsstPreferences.
                           getStepSelectorShowSubstitutions();
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                return(
                    proofAsstPreferences.
                        validateStepSelectorShowSubstitutions(
                            s));
            }
            catch (IllegalArgumentException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }
    }

    private void doSetMaxResultsItemAction() {

        int newMaxResults         = getNewMaxResults();

        if (newMaxResults != -1) {
            proofAsstPreferences.
                    setStepSelectorMaxResults(
                        newMaxResults);
        }
    }

    private int getNewMaxResults() {

        String s                  =
            Integer.toString(
                proofAsstPreferences.
                    getStepSelectorMaxResults());

        String origPromptString   =
            PaConstants.PA_GUI_SET_MAX_RESULTS_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) {
                return -1; //cancelled input
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            try {
                return(
                    proofAsstPreferences.
                        validateStepSelectorMaxResults(
                            s));
            }
            catch (IllegalArgumentException e) {
                promptString      =
                    origPromptString
                    + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                    + e.getMessage();
            }
        }
    }

    private void startUnificationAction(
                            boolean           renumReq,
                            PreprocessRequest preprocessRequest,
                            StepRequest       stepRequest,
                            TLRequest         tlRequest) {
        RequestUnify request      =
            new RequestUnify(proofTextChanged.getChanges(),
                             renumReq,
                             preprocessRequest,
                             stepRequest,
                             tlRequest);
        startRequestAction(request);
    }

    private void reshowStepSelectorDialogAction() {
        if (stepSelectorDialog != null) {
            stepSelectorDialog.setVisible(true);
        }
    }

    /*
     *  Get rid of this thing if it is still hanging around,
     *  we may need the memory space.
     */
    private void disposeOfOldSelectorDialog() {
        if (stepSelectorDialog != null) {
            stepSelectorDialog.dispose();
        }
    }


    // ------------------------------------------------------
    // | TL menu stuff                                      |
    // ------------------------------------------------------


    private void doUnifyPlusStoreInLogSysAndMMTFolderItemAction() {
        startUnificationAction(
            false, // no renum
            null,  // no preprocess request
            null, //  no step selector request
            new StoreInLogSysAndMMTFolderTLRequest());
    }

    private void doUnifyPlusStoreInMMTFolderItemAction() {
        startUnificationAction(
            false, // no renum
            null,  // no preprocess request
            null, //  no step selector request
            new StoreInMMTFolderTLRequest());
    }

    private void doLoadTheoremsFromMMTFolderItemAction() {
        startRequestAction(
            new RequestLoadTheoremsFromMMTFolder());
    }

    private void doExtractTheoremToMMTFolderItemAction() {
        Theorem theorem           = getTheorem();
        if (theorem != null) {
            startRequestAction(
                new RequestExtractTheoremToMMTFolder(
                        theorem));
        }
    }

    private void doVerifyAllProofsItemAction() {
        startRequestAction(
            new RequestVerifyAllProofs());
    }

    private void doSetTLMMTFolderItemAction() {
        MMTFolder mmtFolder       = getNewMMTFolder();
    }

    private MMTFolder getNewMMTFolder() {

        String title              = "";
        File file                 =
            tlPreferences.getMMTFolder().getFolderFile();
        if (file != null) {
            title                 = file.getAbsolutePath();
        }
        mmtFolderChooser.setDialogTitle(title);

        int    returnVal;
        String s;
        String errMsg;
        while (true) {
            returnVal             =
                mmtFolderChooser.
                    showDialog(
                        getMainFrame(),
                        PaConstants.
                            PA_GUI_SET_TL_MMT_FOLDER_OPTION_PROMPT_1
                        );
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                file              =
                    mmtFolderChooser.getSelectedFile();
                errMsg            =
                    tlPreferences.setMMTFolder(file);
                if (errMsg == null) {
                    break;
                }
                if (getYesNoAnswer(
                        errMsg
                        + PaConstants.
                            PA_GUI_SET_TL_MMT_FOLDER_OPTION_PROMPT_2)
                    == JOptionPane.YES_OPTION) {
                    continue;
                }
            }
            break;
        }
        return tlPreferences.getMMTFolder();
    }

    private void doSetTLDjVarsOptionItemAction() {
        String newDjVarsOption
                                  = getNewTLDjVarsOption();
    }

    private String getNewTLDjVarsOption() {

        String s                  =
            tlPreferences.
                getDjVarsOption();

        String origPromptString   =
            PaConstants.
                PA_GUI_SET_TL_DJ_VARS_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) { //cancelled input
                return tlPreferences.
                           getDjVarsOption();
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            if (tlPreferences.setDjVarsOption(s)) {
                return tlPreferences.
                           getDjVarsOption();
            }
            promptString          =
                origPromptString
                + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                + TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION_1
                + s
                + TlConstants.ERRMSG_INVALID_DJ_VARS_OPTION_2;
        }
    }

    private void doSetTLStoreMMIndentAmtItemAction() {
        int newStoreMMIndentAmt
                                  = getNewTLStoreMMIndentAmt();
    }

    private int getNewTLStoreMMIndentAmt() {

        String s                  =
            Integer.toString(
                tlPreferences.
                    getStoreMMIndentAmt());

        String origPromptString   =
            PaConstants.
                PA_GUI_SET_TL_STORE_MM_INDENT_AMT_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) { //cancelled input
                return tlPreferences.
                           getStoreMMIndentAmt();
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            if (tlPreferences.setStoreMMIndentAmt(s)) {
                return tlPreferences.
                           getStoreMMIndentAmt();
            }
            promptString          =
                origPromptString
                + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                + TlConstants.ERRMSG_INVALID_STORE_MM_INDENT_AMT_1
                + s
                + TlConstants.ERRMSG_INVALID_STORE_MM_INDENT_AMT_2;
        }
    }

    private void doSetTLStoreMMRightColItemAction() {
        int newStoreMMRightCol
                                  = getNewTLStoreMMRightCol();
    }

    private int getNewTLStoreMMRightCol() {

        String s                  =
            Integer.toString(
                tlPreferences.
                    getStoreMMRightCol());

        String origPromptString   =
            PaConstants.
                PA_GUI_SET_TL_STORE_MM_RIGHT_COL_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) { //cancelled input
                return tlPreferences.
                           getStoreMMRightCol();
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            if (tlPreferences.setStoreMMRightCol(s)) {
                return tlPreferences.
                           getStoreMMRightCol();
            }
            promptString          =
                origPromptString
                + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                + TlConstants.ERRMSG_INVALID_STORE_MM_RIGHT_COL_1
                + s
                + TlConstants.ERRMSG_INVALID_STORE_MM_RIGHT_COL_2;
        }
    }

    private void doSetTLStoreFormulasAsIsItemAction() {
        boolean newStoreFormulasAsIs
                                  = getNewTLStoreFormulasAsIs();
    }

    private boolean getNewTLStoreFormulasAsIs() {

        String s                  =
            Boolean.toString(
                tlPreferences.
                    getStoreFormulasAsIs());

        String origPromptString   =
            PaConstants.
                PA_GUI_SET_TL_STORE_FORMULAS_AS_IS_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) { //cancelled input
                return tlPreferences.
                           getStoreFormulasAsIs();
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            if (tlPreferences.setStoreFormulasAsIs(s)) {
                return tlPreferences.
                           getStoreFormulasAsIs();
            }
            promptString          =
                origPromptString
                + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                + TlConstants.ERRMSG_INVALID_STORE_FORMULAS_ASIS_1
                + s
                + TlConstants.ERRMSG_INVALID_STORE_FORMULAS_ASIS_2;
        }
    }

    private void doSetTLAuditMessagesItemAction() {
        boolean newAuditMessages  = getNewTLAuditMessages();
    }

    private boolean getNewTLAuditMessages() {

        String s                  =
            Boolean.toString(
                tlPreferences.
                    getAuditMessages());

        String origPromptString   =
            PaConstants.PA_GUI_SET_TL_AUDIT_MESSAGES_OPTION_PROMPT;

        String promptString       = origPromptString;

        promptLoop: while (true) {
            s                     =
                (JOptionPane.
                    showInputDialog(
                        getMainFrame(),
                        promptString,
                        s)
                );
            if (s == null) { //cancelled input
                return tlPreferences.
                           getAuditMessages();
            }
            s                     = s.trim();
            if (s.equals("")) {
                promptString      = origPromptString;
                continue;
            }
            if (tlPreferences.setAuditMessages(s)) {
                return tlPreferences.
                           getAuditMessages();
            }
            promptString          =
                origPromptString
                + PaConstants.PROOF_WORKSHEET_NEW_LINE_STRING
                + TlConstants.ERRMSG_INVALID_AUDIT_MESSAGES_1
                + s
                + TlConstants.ERRMSG_INVALID_AUDIT_MESSAGES_2;

        }
    }


    // ------------------------------------------------------
    // | Inner classes to make requests for processing      |
    // | that occur on separate threads off of the event    |
    // | loop.                                              |
    // ------------------------------------------------------

    abstract class Request {
        ProofWorksheet w;
        Request() {
        }
        abstract void send();
        abstract void receive();

    }

    class RequestExtractTheoremToMMTFolder extends Request {
        Messages messages;
        Theorem  theorem;
        RequestExtractTheoremToMMTFolder(Theorem theorem) {
            super();
            this.theorem          = theorem;
        }
        void send() {
            messages              =
                proofAsst.extractTheoremToMMTFolder(theorem);
        }
        void receive() {
            String s              =
                ProofWorksheet.getOutputMessageText(messages);
            if (s == null) {
                s                 =
                  PaConstants.
                   ERRMSG_PA_GUI_EXTRACT_THEOREMS_TO_MMT_FOLDER_NO_MSGS;
            }
            displayRequestMessages(s);
        }
    }

    class RequestLoadTheoremsFromMMTFolder extends Request {
        Messages messages;
        RequestLoadTheoremsFromMMTFolder() {
            super();
        }
        void send() {
            messages              =
                proofAsst.loadTheoremsFromMMTFolder();
        }
        void receive() {
            String s              =
                ProofWorksheet.getOutputMessageText(messages);
            if (s == null) {
                s                 =
                  PaConstants.
                   ERRMSG_PA_GUI_LOAD_THEOREMS_FROM_MMT_FOLDER_NO_MSGS;
            }
            displayRequestMessages(s);
        }
    }

    class RequestExportViaGMFF extends Request {
        Messages messages;
        RequestExportViaGMFF() {
            super();
        }
        void send() {
            messages              =
                proofAsst.exportViaGMFF(
					getProofTextAreaText());
        }
        void receive() {
            String s              =
                ProofWorksheet.getOutputMessageText(messages);
            if (s == null) {
                s                 =
                  PaConstants.
                   ERRMSG_PA_GUI_EXPORT_VIA_GMFF_NO_MSGS;
            }
            displayRequestMessages(s);
        }
    }


    class RequestVerifyAllProofs extends Request {
        Messages messages;
        RequestVerifyAllProofs() {
            super();
        }
        void send() {
            messages              =
                proofAsst.verifyAllProofs();
        }
        void receive() {
            String s              =
                ProofWorksheet.getOutputMessageText(messages);
            if (s == null) {
                s                 =
                    PaConstants.
                        ERRMSG_PA_GUI_VERIFY_ALL_PROOFS_NO_MSGS;
            }
            displayRequestMessages(s);
        }
    }

    class RequestUpdateMainFrameTitle extends Request {
        File newFile;
        RequestUpdateMainFrameTitle() {
            super();
            newFile               = fileChooser.getSelectedFile();
        }
        RequestUpdateMainFrameTitle(File f) {
            super();
            newFile               = f;
        }
        void send() {
        }
        void receive() {
            updateScreenTitle(newFile);
            updateMainFrameTitle();
        }
    }

    class RequestEditUndo extends Request {
        RequestEditUndo() {
            super();
        }
        void send() {
        }
        void receive() {
            try {
                undoManager.undo();
                updateUndoRedoItems();
            }
            catch(CannotUndoException e) {
                displayRequestMessages(
                    e.getMessage());
            }
        }
    }

    class RequestEditRedo extends Request {
        RequestEditRedo() {
            super();
        }
        void send() {
        }
        void receive() {
            try {
                undoManager.redo();
                updateUndoRedoItems();
            }
            catch(CannotRedoException e) {
                displayRequestMessages(
                    e.getMessage());
            }
        }
    }

    class RequestUnify extends Request {
        boolean             renumReq;
        PreprocessRequest   preprocessRequest;
        StepRequest         stepRequest;
        TLRequest           tlRequest;
        boolean             textChangedBeforeUnify;
        RequestUnify(boolean            textChangedBeforeUnify,
                     boolean            renumReq,
                     PreprocessRequest  preprocessRequest,
                     StepRequest        stepRequest,
                     TLRequest          tlRequest) {
            super();
            this.textChangedBeforeUnify
                                  =
                 textChangedBeforeUnify;
            this.renumReq         = renumReq;
            this.preprocessRequest
                                  = preprocessRequest;
            this.stepRequest      = stepRequest;
            this.tlRequest        = tlRequest;
        }
        void send() {
            w                     =
                proofAsst.
                    unify(renumReq,
                          getProofTextAreaText(),
                          preprocessRequest,
                          stepRequest,
                          tlRequest,
                          proofTextArea.getCaretPosition() + 1);

        }
        void receive() {
            if (w.stepSelectorResults != null) {
                disposeOfOldSelectorDialog();
                stepSelectorDialog =
                    new StepSelectorDialog(
                            mainFrame,
                            w.stepSelectorResults,
                            proofAsstGUI,
                            proofAsstPreferences,
                            proofFont);
            }
            else {
                displayProofWorksheet(w);
            }
            proofTextChanged.setChanges(
                textChangedBeforeUnify);
        }
    }

    class RequestNewProof extends Request {
        String newTheoremLabel;
        RequestNewProof(String newTheoremLabel) {
            super();
            this.newTheoremLabel = newTheoremLabel;
        }
        void send() {
            w                     =
                proofAsst.startNewProof(newTheoremLabel);
        }
        void receive(){
            setProofTheoremLabel(""); //tricky - force title update
            displayProofWorksheet(w);

            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            setNbrTimesSavedSinceNew(0);
            disposeOfOldSelectorDialog();
        }
    }


    class RequestNewNextProof extends Request {
        int     currProofMaxSeq;

        RequestNewNextProof(int currProofMaxSeq) {
            super();
            this.currProofMaxSeq  = currProofMaxSeq;
        }
        void send() {
            w                     =
                proofAsst.startNewNextProof(currProofMaxSeq);
        }
        void receive() {
            setProofTheoremLabel(""); //tricky - force title update
            displayProofWorksheet(w);

            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            setNbrTimesSavedSinceNew(0);
            disposeOfOldSelectorDialog();
        }
    }

    class RequestFileOpen extends Request {
        File   selectedFile;
        String s;

        RequestFileOpen(File selectedFile) {

            super();
            this.selectedFile     = selectedFile;
        }
        void send() {
            s                     =
                readProofTextFromFile(selectedFile);
        }
        void receive() {
            setProofTextAreaText(s);

            setProofTheoremLabel(null); //tricky - avoid title update
            updateScreenTitle(fileChooser.getSelectedFile());
            updateMainFrameTitle();

            clearUndoRedoCaches();

            setProofTextAreaCursorPos(
                ProofAsstCursor.
                    makeProofStartCursor(),
                s.length());

            proofTextChanged.setChanges(false);
            setNbrTimesSavedSinceNew(1);
            disposeOfOldSelectorDialog();
        }
    }

    class RequestGetProof extends Request {
        Theorem oldTheorem;
        boolean proofUnified;
        boolean hypsRandomized;

        RequestGetProof(Theorem oldTheorem,
                        boolean proofUnified,
                        boolean hypsRandomized) {
            super();
            this.oldTheorem       = oldTheorem;
            this.proofUnified     = proofUnified;
            this.hypsRandomized   = hypsRandomized;
        }
        void send() {
            w                     =
                proofAsst.getExistingProof(oldTheorem,
                                           proofUnified,
                                           hypsRandomized);
        }
        void receive() {
            setProofTheoremLabel(""); //tricky - force title update
            displayProofWorksheet(w);
            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            setNbrTimesSavedSinceNew(0);
            disposeOfOldSelectorDialog();
        }
    }

    class RequestFwdProof extends Request {
        int     currProofMaxSeq;
        boolean proofUnified;
        boolean hypsRandomized;

        RequestFwdProof(int     currProofMaxSeq,
                        boolean proofUnified,
                        boolean hypsRandomized) {
            super();
            this.currProofMaxSeq  = currProofMaxSeq;
            this.proofUnified     = proofUnified;
            this.hypsRandomized   = hypsRandomized;
        }
        void send() {
            w                     =
                proofAsst.getNextProof(currProofMaxSeq,
                                       proofUnified,
                                       hypsRandomized);
        }
        void receive() {
            setProofTheoremLabel(""); //tricky - force title update
            displayProofWorksheet(w);
            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            setNbrTimesSavedSinceNew(0);
            disposeOfOldSelectorDialog();
        }
    }

    class RequestBwdProof extends Request {
        int     currProofMaxSeq;
        boolean proofUnified;
        boolean hypsRandomized;

        RequestBwdProof(int     currProofMaxSeq,
                        boolean proofUnified,
                        boolean hypsRandomized) {
            super();
            this.currProofMaxSeq  = currProofMaxSeq;
            this.proofUnified     = proofUnified;
            this.hypsRandomized   = hypsRandomized;
        }
        void send() {
            w                     =
                proofAsst.getPreviousProof(getCurrProofMaxSeq(),
                                           true,   // proof unified
                                           false); // hyps Randomized
        }
        void receive() {
            setProofTheoremLabel(""); //tricky - force title update
            displayProofWorksheet(w);
            clearUndoRedoCaches();
            proofTextChanged.setChanges(false);
            setNbrTimesSavedSinceNew(0);
            disposeOfOldSelectorDialog();
        }
    }

    class RequestTMFFReformat extends Request {
        boolean inputCursorStep;
        boolean textChangedBeforeReformat;

        RequestTMFFReformat(boolean inputCursorStep,
                            boolean textChangedBeforeReformat) {
            super();
            this.inputCursorStep  = inputCursorStep;
            this.textChangedBeforeReformat
                                  =
                 textChangedBeforeReformat;
        }
        void send() {
            w                     =
                proofAsst.
                    tmffReformat(
                        inputCursorStep,
                        getProofTextAreaText(),
                        proofTextArea.getCaretPosition() + 1);
        }

        void receive(){
            displayProofWorksheet(w);
            proofTextChanged.setChanges(
                textChangedBeforeReformat);
        }
    }

    // ------------------------------------------------------
    // | Inner classes to manage thread requests            |
    // | that occur on separate threads off of the event    |
    // | loop.                                              |
    // ------------------------------------------------------

    public class RequestThreadStuff {

        Request request;

        Runnable displayRequestResults;

        Runnable sendRequest;

        /**
         *  Thread used for Unification of proof.
         */
        public Thread requestThread;

        /**
         *  Set the Thread value in RequestThreadStuff object.
         *
         *  @param t Thread used in RequestThreadStuff
         */
        public synchronized void setRequestThread(Thread t) {
            requestThread = t;
        }

        /**
         *  Get the Thread value in RequestThreadStuff object.
         *
         *  @return      Thread used in RequestThreadStuff
         */
        public synchronized Thread getRequestThread() {
            return requestThread;
        }


        /**
         *  Start the Thread used in the RequestThreadStuff object.
         */
        public void startRequestThread() {
            getRequestThread().start();
        }

        /**
         *  Cancel the Thread used in the RequestThreadStuff object
         *  if it exists (not null).
         */
        public void cancelRequestThread() {
            Thread requestThread = getRequestThread();
            if (requestThread != null) {
                requestThread.interrupt();
            }
        }

        /**
         *  Constructor.
         *
         *  Builds object for sending processing request
         *  and receiving the finished results.
         *
         *  @param r Request object reference
         */
         public RequestThreadStuff(Request r) {

            request               = r;

            displayRequestResults = new Runnable() {
                public void run() {
                    try {
                        request.receive();
                    }
                    finally {
                        tidyUpRequestStuff();
                    }
                }
            };

            sendRequest           = new Runnable() {
                public void run() {
                    try {
                        request.send();
                        EventQueue.invokeLater(
                            displayRequestResults);
                    }
                    finally {
                        setRequestThread(null);
                    }
                }
            };

            setRequestThread(
                new Thread(sendRequest));
        }
    }

    private synchronized RequestThreadStuff
                      getRequestThreadStuff() {
        return requestThreadStuff;
    }

    private synchronized void setRequestThreadStuff(
                                 RequestThreadStuff x) {
        requestThreadStuff        = x;
    }

    private synchronized void startRequestAction(Request r) {
        if (getRequestThreadStuff() == null) {

            setRequestThreadStuff(
                new RequestThreadStuff(r));

            getRequestThreadStuff().startRequestThread();

            cancelRequestItem.setEnabled(true);
            getMainFrame().
                setCursor(
                    Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR));
            proofTextArea.
                setCursor(
                    Cursor.getPredefinedCursor(
                        Cursor.WAIT_CURSOR));
        }
    }

    private synchronized void cancelRequestAction() {
        RequestThreadStuff k;
        if ((k = getRequestThreadStuff()) != null) {
            k.cancelRequestThread();
            tidyUpRequestStuff();
        }
    }

    private void tidyUpRequestStuff() {
        setRequestThreadStuff(null);
        cancelRequestItem.setEnabled(false);
        getMainFrame().setCursor(null);
        proofTextArea.setCursor(null);
    }

    private void displayProofWorksheet(ProofWorksheet w) {

        // keep this number for browsing forward and back!
        setCurrProofMaxSeq(w.getMaxSeq());

        String s                  = w.getOutputProofText();
        int    proofTextLength    = Integer.MAX_VALUE;
        if (s != null) { //no structural errors...
            setProofTextAreaText(s);
            proofTextLength       = s.length();
        }

        s                         = w.getTheoremLabel();
        if (s != null &&
            getProofTheoremLabel() != null) {
            if (s.compareToIgnoreCase(getProofTheoremLabel())
                != 0) {
                updateFileChooserFileForProofLabel(s);
                updateScreenTitle(fileChooser.getSelectedFile());
                updateMainFrameTitle();
                setProofTheoremLabel(s);
                setNbrTimesSavedSinceNew(0);
            }
        }

        setProofTextAreaCursorPos(w,
                                  proofTextLength);

        s                         = w.getOutputMessageText();
        displayRequestMessages(s);

    }

    private void displayRequestMessages(String s) {

        String messages;
        if (s == null) {
            messages              =
                PaConstants.ERRMSG_NO_MESSAGES_MSG_1;
        }
        else {
            messages              = s;
        }

        proofMessageArea.setText(s);
        setCursorToStartOfMessageArea();

        displayRequestMessagesGUI(messages);

    }

    private void displayRequestMessagesGUI(String messages) {

        RequestMessagesGUI u      = getRequestMessagesGUI();
        if (u == null) {
            if (messages == null) {
                return;
            }

            u                     =
                new RequestMessagesGUI(messages,
                                       proofAsstPreferences);
            setRequestMessagesGUI(u);
            u.showFrame(u.buildFrame());
        }
        else {
            u.changeFrameText(messages);
            u.setCursorToStartOfMessageArea();
        }
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

            proofMessageArea.setCaretPosition(0);

            JViewport v           =
                proofMessageScrollPane.getViewport();

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

    private synchronized void
                setRequestMessagesGUI(RequestMessagesGUI u) {

        this.requestMessagesGUI = u;
    }
    private synchronized RequestMessagesGUI
                getRequestMessagesGUI() {

        return requestMessagesGUI;
    }
    private synchronized void
                disposeOfRequestMessagesGUI() {

        RequestMessagesGUI u    = getRequestMessagesGUI();
        if (u != null) {
            u.dispose();
        }
    }



    /**
     *  ==============================
     *  GUI Frame Infrastructure stuff
     *  ==============================
     */

    private static class FrameShower implements Runnable {
        JFrame f;
        public FrameShower(JFrame frame) {
            f = frame;
        }
        public void run() {
            f.pack();
            f.setVisible(true);
        }
    }

    /**
     *  Show the GUI's main frame (screen).
     */
    public void showMainFrame() {
        showFrame(getMainFrame());
    }

    private void showFrame(JFrame jFrame) {
        Runnable     runner       =
            new FrameShower(jFrame);

        EventQueue.invokeLater(runner);
    }

    /**
     *  Test code to invoke GUI from command line.
     *
     *  @param args String array holding command line parms
     */
    public static void main(String[] args) {
        ProofAsstGUI proofAsstGUI = new ProofAsstGUI();
        proofAsstGUI.showMainFrame();
    }
}
