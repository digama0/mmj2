// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ThruChapScrnMap.java

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, ThruSecScrnMap, FromChapScrnMap

public class ThruChapScrnMap extends SearchOptionsJComboBox implements
    ActionListener
{

    public ThruChapScrnMap(final String[] as,
        final ThruSecScrnMap thruSecScrnMap)
    {
        super(45, as);
        fromChapScrnMap = null;
        chap = "\n";
        chapId = -1;
        chapValues = as;
        this.thruSecScrnMap = thruSecScrnMap;
        addActionListener(this);
        actionPerformed(null);
    }

    public void setFromChapScrnMap(final FromChapScrnMap fromChapScrnMap) {
        this.fromChapScrnMap = fromChapScrnMap;
    }

    public void fromChapIdUpdated(final int i) {
        if (!chap.equals(chapValues[0]) && i > chapId)
            setSelectedItem(chapValues[0]);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null) {
            final int i = computeChapId(s);
            if (chapId != i || !s.equals(chap))
                chapUpdate(s, i);
        }
    }

    private int computeChapId(final String s) {
        for (int i = 0; i < chapValues.length; i++)
            if (s.equals(chapValues[i]))
                return i;

        throw new IllegalArgumentException(
            SearchOptionsConstants.ERRMSG_THRU_CHAP_SEL_INVALID_1 + s);
    }

    private void chapUpdate(final String s, final int i) {
        chap = s;
        chapId = i;
        thruSecScrnMap.chapIdUpdated(i);
        if (!s.equals(chapValues[0]) && fromChapScrnMap != null)
            fromChapScrnMap.thruChapIdUpdated(i);
    }

    private final String[] chapValues;
    ThruSecScrnMap thruSecScrnMap;
    FromChapScrnMap fromChapScrnMap;
    String chap;
    private int chapId;
}
