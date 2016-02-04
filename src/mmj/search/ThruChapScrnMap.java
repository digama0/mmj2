//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ThruChapScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mmj.pa.ErrorCode;

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

        throw new IllegalArgumentException(ErrorCode.format(
            SearchOptionsConstants.ERRMSG_THRU_CHAP_SEL_INVALID, s));
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
