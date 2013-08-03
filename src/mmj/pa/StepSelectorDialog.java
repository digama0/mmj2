//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  StepSelectorDialog.java  0.01 03/01/2008
 *
 *  Version 0.01:
 *  ==> New.
 */

package mmj.pa;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 *  StepSelectorDialog is used by ProofAsstGUI to allow the
 *  user to choose from a list of unifying assertions for a given
 *  proof step.
 */
public class StepSelectorDialog extends JDialog {

    private final StepSelectorResults   stepSelectorResults;
    private final ProofAsstGUI          proofAsstGUI;
    private       JList                 stepSelectorDialogList;
    private       Font                  proofFont;
    private       StepSelectorDialog    stepSelectorDialog;

    public StepSelectorDialog(
                    Frame                proofAsstGUIFrame,
                    StepSelectorResults  results,
                    ProofAsstGUI         gui,
                    ProofAsstPreferences proofAsstPreferences,
                    Font                 proofFont) {

        super(proofAsstGUIFrame,
              PaConstants.STEP_SELECTOR_DIALOG_TITLE,
              false);  //false = not modal

        this.stepSelectorResults  = results;
        this.proofAsstGUI         = gui;
        this.proofFont            = proofFont;
        stepSelectorDialog        = this;

        setFont(proofFont);

        JButton cancelButton      =
            new JButton(
                PaConstants.
                    STEP_SELECTOR_DIALOG_HIDE_BUTTON_CAPTION);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });


        final JButton setButton   =
            new JButton(
                PaConstants.
                    STEP_SELECTOR_DIALOG_SET_BUTTON_CAPTION);
        setButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doSetButtonAction();
            }
        });
        getRootPane().setDefaultButton(setButton);

        stepSelectorDialogList    =
            new JList();
        stepSelectorDialogList.
            setFont(
                proofFont);
        stepSelectorDialogList.
            setListData(
                stepSelectorResults.
                    selectionArray);
        stepSelectorDialogList.
            setSelectionMode(
                ListSelectionModel.
                    SINGLE_SELECTION);

        stepSelectorDialogList.
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setButton.doClick();
                    }
                    if (e.getButton() == MouseEvent.BUTTON2
                        ||
                        e.getButton() == MouseEvent.BUTTON3) {
                        popupSelectionItem();
                   }
                }
            });

        JScrollPane listScroller   =
            new JScrollPane(stepSelectorDialogList);

        int width                  =
            proofAsstPreferences.getStepSelectorDialogPaneWidth();
        int height                 =
            proofAsstPreferences.getStepSelectorDialogPaneHeight();

        listScroller.
            setPreferredSize(
                new Dimension(
                        width,
                        height
                    ));
        listScroller.
            setMinimumSize(
                new Dimension(
                        width,
                        height
                    ));
        listScroller.
            setAlignmentX(
                LEFT_ALIGNMENT);

        JPanel listPane           = new JPanel();
        listPane.
            setLayout(
            new BoxLayout(
                    listPane,
                    BoxLayout.Y_AXIS));

        JLabel label              =
            new JLabel(
                    PaConstants.
                        STEP_SELECTOR_DIALOG_LIST_CAPTION_PREFIX
                    + stepSelectorResults.step
                    + PaConstants.
                        STEP_SELECTOR_DIALOG_LIST_CAPTION_SUFFIX
                    );
        label.setLabelFor(
            stepSelectorDialogList);

        listPane.add(label);
        listPane.add(
            Box.createRigidArea(
                new Dimension(0,5)));
        listPane.add(
            listScroller);
        listPane.setBorder(
            BorderFactory.
                createEmptyBorder(5,5,5,5));

        JPanel buttonPane         = new JPanel();
        buttonPane.setLayout(
            new BoxLayout(buttonPane,
                          BoxLayout.X_AXIS));
        buttonPane.setBorder(
            BorderFactory.
                createEmptyBorder(0, 5, 5, 5));
        buttonPane.add(
            Box.createHorizontalGlue());
        buttonPane.add(
            cancelButton);
        buttonPane.add(
            Box.createRigidArea(
                new Dimension(5, 0)));
        buttonPane.add(setButton);

        Container contentPane     = getContentPane();
        contentPane.add(listPane,
                        BorderLayout.CENTER);
        contentPane.add(buttonPane,
                        BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    private void doSetButtonAction() {


        int i             =
            stepSelectorDialogList.getSelectedIndex();
        Object choice     = null;
        if (i != -1) {
            choice        =
                stepSelectorResults.refArray[i];
        }
        setVisible(false);

        proofAsstGUI.
            unifyWithStepSelectorChoice(
                new StepRequest(
                    PaConstants.STEP_REQUEST_SELECTOR_CHOICE,
                    stepSelectorResults.step,
                    choice));
    }

    private void popupSelectionItem() {
        int n                     =
            stepSelectorDialogList.getSelectedIndex();
        if (n == -1) {
            return;
        }
        String s                  =
            stepSelectorResults.
                selectionArray[n];

        StringBuffer sb           =
            new StringBuffer(s.length());
        int col                   = 0;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c                     = s.charAt(i);
            if (col > 85 &&
                Character.isWhitespace(c)) {
                sb.append('\n');
                col               = 0;
            }
            else {
                sb.append(c);
                ++col;
            }
        }

        int answer                = JOptionPane.YES_OPTION; //default
        try {
            answer                =
                JOptionPane.showConfirmDialog(
                    stepSelectorDialog,
                    sb.toString(),
                    PaConstants.
                        STEP_SELECTOR_DIALOG_POPUP_SET_BUTTON_CAPTION,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
            if (answer == JOptionPane.YES_OPTION) {
                doSetButtonAction();
            }
        }
        catch (HeadlessException e) {
        }
    }
}
