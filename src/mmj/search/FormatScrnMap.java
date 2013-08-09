// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   FormatScrnMap.java

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, SearchOptionsConstants, OperScrnMap

public class FormatScrnMap extends SearchOptionsJComboBox implements
    ActionListener
{

    public FormatScrnMap(final int i,
        final DefaultComboBoxModel<String> defaultComboBoxModel,
        final OperScrnMap operScrnMap)
    {
        super(SearchOptionsConstants.FORMAT_FIELD_ID[i], defaultComboBoxModel);
        formatType = -1;
        formatId = -1;
        this.operScrnMap = operScrnMap;
        addActionListener(this);
        actionPerformed(null);
    }

    @Override
    public void set(final String s) {
        final int i = computeFormatType(computeFormatId(s));
        if (i != formatType)
            formatTypeUpdate(i);
        setSelectedItem(s);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null) {
            final int i = computeFormatId(s);
            if (i != formatId)
                formatIdUpdate(i);
            final int j = computeFormatType(formatId);
            if (j != formatType)
                formatTypeUpdate(j);
        }
    }

    public void partTypeUpdated(final int i) {
        int j = formatType;
        String[] as;
        if (i == 0) {
            as = SearchOptionsConstants.FORMAT_VALUES_TREE;
            j = 0;
        }
        else {
            as = SearchOptionsConstants.FORMAT_VALUES_NOT_TREE;
            j = 2;
        }
        updateFormatList(j, as);
    }

    private void updateFormatList(final int i, final String[] as) {
        if (i != formatType)
            formatTypeUpdate(i);
        updateComboBoxList(as);
    }

    private int computeFormatId(final String s) {
        for (int i = 0; i < SearchOptionsConstants.FORMAT_VALUES.length; i++)
            if (s.equals(SearchOptionsConstants.FORMAT_VALUES[i]))
                return SearchOptionsConstants.FORMAT_ID[i];

        throw new IllegalArgumentException(
            SearchOptionsConstants.ERRMSG_FORMAT_SEL_INVALID_1 + s);
    }

    private void formatIdUpdate(final int i) {
        formatId = i;
    }

    private void updateComboBoxList(final String[] as) {
        final String s = (String)getSelectedItem();
        removeAllItems();
        String s1 = as[0];
        for (final String element : as) {
            addItem(element);
            if (s.equals(element))
                s1 = element;
        }

        setSelectedItem(s1);
    }

    private void formatTypeUpdate(final int i) {
        formatType = i;
        operScrnMap.formatTypeUpdated(i);
    }

    private int computeFormatType(final int i) {
        return SearchOptionsConstants.FORMAT_TYPE[i];
    }

    private final OperScrnMap operScrnMap;
    private int formatType;
    private int formatId;
}
