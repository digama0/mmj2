// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ForWhatScrnMap.java

package mmj.search;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxEditor;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, SearchJTextFieldPopupMenuListener, SearchJTextFieldPopupMenu, SearchOptionsConstants,
//            SearchOptionsFieldAttr

public class ForWhatScrnMap extends SearchOptionsJComboBox {

    public ForWhatScrnMap(final int i, final String[] as,
        final DefaultComboBoxModel<String> defaultcomboboxmodel)
    {
        super(SearchOptionsConstants.FOR_WHAT_FIELD_ID[i], defaultcomboboxmodel);
        setEditable(true);
        setEditor(new BasicComboBoxEditor());
        setSelectedItem(SearchOptionsConstants.FIELD_ATTR[fieldId].defaultText);
        setDefaultToCurrentValue();
        setBorder(BorderFactory.createLineBorder(
            SearchOptionsConstants.FOR_WHAT_BORDER_COLOR, 3));
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