//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ForWhatScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;

public class ForWhatScrnMap extends SearchOptionsJComboBox {

    public ForWhatScrnMap(final int i, final String[] as,
        final DefaultComboBoxModel<String> defaultcomboboxmodel)
    {
        super(SearchOptionsConstants.FOR_WHAT_FIELD_ID[i],
            defaultcomboboxmodel);
        setEditable(true);
        setEditor(new BasicComboBoxEditor());
        setSelectedItem(SearchOptionsConstants.FIELD_ATTR[fieldId].defaultText);
        setDefaultToCurrentValue();
        setBorder(BorderFactory
            .createLineBorder(SearchOptionsConstants.FOR_WHAT_BORDER_COLOR, 3));
        ((JTextField)getEditor().getEditorComponent())
            .addMouseListener(new SearchJTextFieldPopupMenuListener(
                new SearchJTextFieldPopupMenu()));
        ((JTextField)getEditor().getEditorComponent())
            .setToolTipText(SearchOptionsConstants.FIELD_ATTR[fieldId].toolTip);
    }

    public void searchRequested() {
        String s = (String)getSelectedItem();
        if (s == null)
            return;
        s = s.trim();
        if (s.equals(""))
            return;
        int i = getItemCount();
        int j = 0;
        do {
            if (j >= i)
                break;
            if (s.equals(getItemAt(j).trim())) {
                removeItemAt(j);
                break;
            }
            j++;
        } while (true);
        insertItemAt(s, 0);
        i = getItemCount();
        if (9 < i)
            removeItemAt(i - 1);
        setSelectedItem(s);
    }

    public void updateForWhatPriorValues(final String[] as) {
        updateComboBoxList(as);
    }

    public String[] getForWhatPriorValues() {
        final int i = getItemCount();
        final String[] as = new String[i];
        for (int j = 0; j < i; j++)
            as[j] = getItemAt(j).trim();

        return as;
    }

    private void updateComboBoxList(final String[] as) {
        final String s = (String)getSelectedItem();
        removeAllItems();
        for (final String element : as)
            addItem(element);

        setSelectedItem(s);
    }
}
