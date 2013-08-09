// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOptionsJCheckBox.java

package mmj.search;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

// Referenced classes of package mmj.search:
//            SearchOptionsScrnMapField, SearchOptionsConstants, SearchOptionsFieldAttr, SearchArgs,
//            SearchArgsField, SearchMgr

public abstract class SearchOptionsJCheckBox extends JCheckBox implements
    SearchOptionsScrnMapField
{

    public SearchOptionsJCheckBox(final int i, final String s) {
        defaultValue = null;
        fieldId = i;
        set(s);
        setDefaultToCurrentValue();
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        jLabel = new JLabel(SearchOptionsConstants.FIELD_ATTR[i].label);
        jLabel.setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        jLabel.setLabelFor(this);
    }

    public SearchOptionsJCheckBox(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    @Override
    public void setEnabled(final boolean flag) {
        super.setEnabled(flag);
        jLabel.setEnabled(flag);
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
        setSelected(Boolean.parseBoolean(s));
    }

    public String get() {
        return Boolean.toString(isSelected());
    }

    public JLabel createJLabel() {
        return jLabel;
    }

    @Override
    public boolean requestFocusInWindow(final boolean flag) {
        return super.requestFocusInWindow(flag);
    }

    protected final int fieldId;
    protected final JLabel jLabel;
    protected String defaultValue;
}