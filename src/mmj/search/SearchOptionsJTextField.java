// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOptionsJTextField.java

package mmj.search;

import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTextField;

// Referenced classes of package mmj.search:
//            SearchJTextFieldPopupMenuListener, SearchJTextFieldPopupMenu, SearchOptionsScrnMapField, SearchOptionsConstants,
//            SearchOptionsFieldAttr, SearchArgs, SearchArgsField, SearchMgr

public abstract class SearchOptionsJTextField extends JTextField implements
    SearchOptionsScrnMapField
{

    public SearchOptionsJTextField(final int i, final String s, final int j) {
        super(s, j);
        defaultValue = null;
        fieldId = i;
        setDefaultToCurrentValue();
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        addMouseListener(new SearchJTextFieldPopupMenuListener(
            new SearchJTextFieldPopupMenu()));
    }

    public SearchOptionsJTextField(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText,
            SearchOptionsConstants.FIELD_ATTR[i].columns);
    }

    public int getFieldId() {
        return fieldId;
    }

    public void positionCursor(final int i) {
        setCaretPosition(i);
    }

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
        setText(s);
    }

    public String get() {
        return getText();
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