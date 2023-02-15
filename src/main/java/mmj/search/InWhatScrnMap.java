//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * InWhatScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import mmj.pa.ErrorCode;

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

        throw new IllegalArgumentException(ErrorCode.format(
            SearchOptionsConstants.ERRMSG_IN_WHAT_SEL_INVALID, s));
    }

    private void inWhatTypeUpdate(final int i) {
        inWhatType = i;
        partScrnMap.inWhatTypeUpdated(i);
    }

    private final PartScrnMap partScrnMap;
    private int inWhatType;
}
