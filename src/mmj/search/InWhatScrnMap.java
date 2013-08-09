// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   InWhatScrnMap.java

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, SearchOptionsConstants, PartScrnMap

public class InWhatScrnMap extends SearchOptionsJComboBox implements
    ActionListener
{

    public InWhatScrnMap(final int i, final PartScrnMap partScrnMap) {
        super(SearchOptionsConstants.IN_WHAT_FIELD_ID[i]);
        inWhatType = -1;
        this.partScrnMap = partScrnMap;
        addActionListener(this);
        actionPerformed(null);
    }

    @Override
    public void set(final String s) {
        final int i = computeInWhatType(s);
        if (i != inWhatType)
            inWhatTypeUpdate(i);
        setSelectedItem(s);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null) {
            final int i = computeInWhatType(s);
            if (i != inWhatType)
                inWhatTypeUpdate(i);
        }
    }

    private int computeInWhatType(final String s) {
        for (int i = 0; i < SearchOptionsConstants.IN_WHAT_VALUES.length; i++)
            if (s.equals(SearchOptionsConstants.IN_WHAT_VALUES[i]))
                return SearchOptionsConstants.IN_WHAT_TYPE[i];

        throw new IllegalArgumentException(
            SearchOptionsConstants.ERRMSG_IN_WHAT_SEL_INVALID_1 + s);
    }

    private void inWhatTypeUpdate(final int i) {
        inWhatType = i;
        partScrnMap.inWhatTypeUpdated(i);
    }

    private final PartScrnMap partScrnMap;
    private int inWhatType;
}
