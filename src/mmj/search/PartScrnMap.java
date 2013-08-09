// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   PartScrnMap.java

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, SearchOptionsConstants, FormatScrnMap

public class PartScrnMap extends SearchOptionsJComboBox implements
    ActionListener
{

    public PartScrnMap(final int i,
        final DefaultComboBoxModel<String> defaultComboBoxModel,
        final FormatScrnMap formatScrnMap)
    {
        super(SearchOptionsConstants.PART_FIELD_ID[i], defaultComboBoxModel);
        partType = -1;
        partId = -1;
        this.formatScrnMap = formatScrnMap;
        addActionListener(this);
        actionPerformed(null);
    }

    @Override
    public void set(final String s) {
        final int i = computePartType(computePartId(s));
        if (i != partType)
            partTypeUpdate(i);
        setSelectedItem(s);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null) {
            final int i = computePartId(s);
            if (i != partId)
                partIdUpdate(i);
            final int j = computePartType(partId);
            if (j != partType)
                partTypeUpdate(j);
        }
    }

    public void inWhatTypeUpdated(final int i) {
        int j;
        String[] as;
        if (i == 0) {
            as = SearchOptionsConstants.PART_VALUES_STATEMENT;
            j = 0;
        }
        else if (i == 1) {
            as = SearchOptionsConstants.PART_VALUES_HYP;
            j = 0;
        }
        else {
            as = SearchOptionsConstants.PART_VALUES_SUB_STATEMENT;
            j = 1;
        }
        updatePartList(j, as);
    }

    private void updatePartList(final int i, final String[] as) {
        if (i != partType)
            partTypeUpdate(i);
        updateComboBoxList(as);
    }

    private int computePartId(final String s) {
        for (int i = 0; i < SearchOptionsConstants.PART_VALUES.length; i++)
            if (s.equals(SearchOptionsConstants.PART_VALUES[i]))
                return i;

        throw new IllegalArgumentException(
            SearchOptionsConstants.ERRMSG_PART_SEL_INVALID_1 + s);
    }

    private void partIdUpdate(final int i) {
        partId = i;
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

    private void partTypeUpdate(final int i) {
        partType = i;
        formatScrnMap.partTypeUpdated(i);
    }

    private int computePartType(final int i) {
        return SearchOptionsConstants.PART_TYPE[i];
    }

    private final FormatScrnMap formatScrnMap;
    private int partType;
    private int partId;
}
