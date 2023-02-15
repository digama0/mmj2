//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * StepSelectorDialog.java  0.01 03/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.pa;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import mmj.pa.StepRequest.StepRequestType;

/**
 * StepSelectorDialog is used by ProofAsstGUI to allow the user to choose from a
 * list of unifying assertions for a given proof step.
 */
public class StepSelectorDialog extends JDialog {

    private final StepSelectorResults stepSelectorResults;
    private final ProofAsstGUI proofAsstGUI;
    private final JList<String> stepSelectorDialogList;
    private final StepSelectorDialog stepSelectorDialog;

    public StepSelectorDialog(final Frame proofAsstGUIFrame,
        final StepSelectorResults results, final ProofAsstGUI gui,
        final ProofAsstPreferences proofAsstPreferences, final Font proofFont)
    {

        super(proofAsstGUIFrame, PaConstants.STEP_SELECTOR_DIALOG_TITLE,
            false/*= not modal*/);

        final ActionListener hideListener = new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                setVisible(false);
            }
        };
        getRootPane().registerKeyboardAction(hideListener,
            KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW);

        stepSelectorResults = results;
        proofAsstGUI = gui;
        stepSelectorDialog = this;

        setFont(proofFont);

        final JButton cancelButton = new JButton(
            PaConstants.STEP_SELECTOR_DIALOG_HIDE_BUTTON_CAPTION);
        cancelButton.addActionListener(hideListener);

        final JButton setButton = new JButton(
            PaConstants.STEP_SELECTOR_DIALOG_SET_BUTTON_CAPTION);
        setButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                doSetButtonAction();
            }
        });
        getRootPane().setDefaultButton(setButton);

        stepSelectorDialogList = new JList<>();
        stepSelectorDialogList.setFont(proofFont);
        stepSelectorDialogList.setListData(stepSelectorResults.selectionArray);
        stepSelectorDialogList
            .setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        stepSelectorDialogList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (e.getClickCount() == 2)
                    setButton.doClick();
                if (e.getButton() == MouseEvent.BUTTON2
                    || e.getButton() == MouseEvent.BUTTON3)
                    popupSelectionItem();
            }
        });

        final JScrollPane listScroller = new JScrollPane(
            stepSelectorDialogList);

        final int width = proofAsstPreferences.stepSelectorDialogPaneWidth
            .get();
        final int height = proofAsstPreferences.stepSelectorDialogPaneHeight
            .get();

        listScroller.setPreferredSize(new Dimension(width, height));
        listScroller.setMinimumSize(new Dimension(width, height));
        listScroller.setAlignmentX(LEFT_ALIGNMENT);

        final JPanel listPane = new JPanel();
        listPane.setLayout(new BoxLayout(listPane, BoxLayout.Y_AXIS));

        final JLabel label = new JLabel(
            PaConstants.STEP_SELECTOR_DIALOG_LIST_CAPTION_PREFIX
                + stepSelectorResults.step
                + PaConstants.STEP_SELECTOR_DIALOG_LIST_CAPTION_SUFFIX);
        label.setLabelFor(stepSelectorDialogList);

        listPane.add(label);
        listPane.add(Box.createRigidArea(new Dimension(0, 5)));
        listPane.add(listScroller);
        listPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        final JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        buttonPane.add(Box.createHorizontalGlue());
        buttonPane.add(cancelButton);
        buttonPane.add(Box.createRigidArea(new Dimension(5, 0)));
        buttonPane.add(setButton);

        final Container contentPane = getContentPane();
        contentPane.add(listPane, BorderLayout.CENTER);
        contentPane.add(buttonPane, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }

    private void doSetButtonAction() {

        final int i = stepSelectorDialogList.getSelectedIndex();
        Object choice = null;
        if (i != -1)
            choice = stepSelectorResults.refArray[i];
        setVisible(false);

        proofAsstGUI.unifyWithStepSelectorChoice(new StepRequest(
            StepRequestType.SelectorChoice, stepSelectorResults.step, choice));
    }

    private void popupSelectionItem() {
        final int n = stepSelectorDialogList.getSelectedIndex();
        if (n == -1)
            return;
        final String s = stepSelectorResults.selectionArray[n];

        final StringBuilder sb = new StringBuilder(s.length());
        int col = 0;
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            if (col > 85 && Character.isWhitespace(c)) {
                sb.append('\n');
                col = 0;
            }
            else {
                sb.append(c);
                col++;
            }
        }

        int answer = JOptionPane.YES_OPTION; // default
        try {
            answer = JOptionPane.showConfirmDialog(stepSelectorDialog,
                sb.toString(),
                PaConstants.STEP_SELECTOR_DIALOG_POPUP_SET_BUTTON_CAPTION,
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (answer == JOptionPane.YES_OPTION)
                doSetButtonAction();
        } catch (final HeadlessException e) {}
    }
}
