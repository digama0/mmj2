// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   FromChapScrnMap.java

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

// Referenced classes of package mmj.search:
//            SearchOptionsJComboBox, FromSecScrnMap, ThruChapScrnMap

public class FromChapScrnMap extends SearchOptionsJComboBox implements
    ActionListener
{

    public FromChapScrnMap(final String[] as,
        final FromSecScrnMap fromSecScrnMap)
    {
        super(43, as);
        thruChapScrnMap = null;
        chap = "\n";
        chapId = -1;
        chapValues = as;
        this.fromSecScrnMap = fromSecScrnMap;
        addActionListener(this);
        actionPerformed(null);
    }

    public void setThruChapScrnMap(final ThruChapScrnMap thruChapScrnMap) {
        this.thruChapScrnMap = thruChapScrnMap;
    }

    public void thruChapIdUpdated(final int i) {
        if (!chap.equals(chapValues[0]) && i < chapId)
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
            SearchOptionsConstants.ERRMSG_FROM_CHAP_SEL_INVALID_1 + s);
    }

    private void chapUpdate(final String s, final int i) {
        chap = s;
        chapId = i;
        fromSecScrnMap.chapIdUpdated(i);
        if (!s.equals(chapValues[0]) && thruChapScrnMap != null)
            thruChapScrnMap.fromChapIdUpdated(i);
    }

    private final String[] chapValues;
    FromSecScrnMap fromSecScrnMap;
    ThruChapScrnMap thruChapScrnMap;
    String chap;
    private int chapId;
}
