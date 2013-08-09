// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOptionsJComboBox.java

package mmj.search;

import java.awt.Font;
import java.util.Vector;

import javax.swing.*;

// Referenced classes of package mmj.search:
//            SearchOptionsScrnMapField, SearchOptionsConstants, SearchOptionsFieldAttr, SearchArgs,
//            SearchArgsField, SearchMgr

public abstract class SearchOptionsJComboBox extends JComboBox<String>
    implements SearchOptionsScrnMapField
{

    public static DefaultComboBoxModel<String> buildUpdateableComboBoxModel(
        final int i)
    {
        final Vector<String> vector = new Vector<String>();
        final String[] as = SearchOptionsConstants.FIELD_ATTR[i].comboBoxItems;
        for (final String element : as)
            vector.add(element);

        return new DefaultComboBoxModel<String>(vector);
    }

    public static DefaultComboBoxModel<String> buildUpdateableComboBoxModel(
        final String[] as)
    {
        final Vector<String> vector = new Vector<String>();
        for (final String element : as)
            vector.add(element);

        return new DefaultComboBoxModel<String>(vector);
    }

    public SearchOptionsJComboBox(final int i,
        final ComboBoxModel<String> comboboxmodel)
    {
        super(comboboxmodel);
        defaultValue = null;
        fieldId = i;
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        setSelectedItem(SearchOptionsConstants.FIELD_ATTR[i].defaultText);
        setDefaultToCurrentValue();
    }

    public SearchOptionsJComboBox(final int i, final String[] as, final String s)
    {
        super(as);
        defaultValue = null;
        fieldId = i;
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        setSelectedItem(s);
        setDefaultToCurrentValue();
    }

    public SearchOptionsJComboBox(final int i, final String[] as) {
        this(i, as, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    public SearchOptionsJComboBox(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].comboBoxItems,
            SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    public int getFieldId() {
        return fieldId;
    }

    public void positionCursor(final int i) {}

    public void setSearchOptionsFont(final Font font) {
        setFont(font);
    }

    public void uploadFromScrnMap(final SearchArgs args) {
        args.arg[fieldId].set(get());
    }

    public void downloadToScrnMap(final SearchArgs args,
        final SearchMgr searchMgr)
    {
        set(args.arg[fieldId].get());
    }

    public void setDefaultToCurrentValue() {
        defaultValue = get();
    }

    public void resetToDefaultValue() {
        set(defaultValue);
    }

    public void set(final String s) {
        setSelectedItem(s);
    }

    public String get() {
        return (String)getSelectedItem();
    }

    public JLabel createJLabel() {
        final JLabel jlabel = new JLabel(
            SearchOptionsConstants.FIELD_ATTR[fieldId].label);
        jlabel
            .setToolTipText(SearchOptionsConstants.FIELD_ATTR[fieldId].toolTip);
        jlabel.setLabelFor(this);
        return jlabel;
    }

    @Override
    public boolean requestFocusInWindow(final boolean flag) {
        return super.requestFocusInWindow(flag);
    }

    protected final int fieldId;
    protected String defaultValue;
}