//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * OperScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;

import mmj.pa.ErrorCode;

public class OperScrnMap extends SearchOptionsJComboBox
    implements ActionListener
{

    public OperScrnMap(final int i,
        final DefaultComboBoxModel<String> defaultcomboboxmodel)
    {
        super(SearchOptionsConstants.OPER_FIELD_ID[i], defaultcomboboxmodel);
        operType = -1;
        addActionListener(this);
        actionPerformed(null);
    }

    @Override
    public void set(final String s) {
        final int i = computeOperType(s);
        if (i != operType)
            operTypeUpdate(i);
        setSelectedItem(s);
    }

    @Override
    public void actionPerformed(final ActionEvent actionevent) {
        final String s = (String)getSelectedItem();
        if (s != null) {
            final int i = computeOperType(s);
            if (i != operType)
                operTypeUpdate(i);
        }
    }

    public void formatTypeUpdated(final int i) {
        if (i == 0)
            updateOperList(0, SearchOptionsConstants.OPER_VALUES_TREE);
        else if (i == 1)
            updateOperList(1, SearchOptionsConstants.OPER_VALUES_SUB_TREE);
        else
            updateOperList(2, SearchOptionsConstants.OPER_VALUES_NOT_TREE);
    }

    private void updateOperList(final int i, final String[] as) {
        operTypeUpdate(i);
        updateComboBoxList(as);
    }

    private void operTypeUpdate(final int i) {
        operType = i;
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

    private int computeOperType(final String s) {
        for (int i = 0; i < SearchOptionsConstants.OPER_VALUES.length; i++)
            if (s.equals(SearchOptionsConstants.OPER_VALUES[i]))
                return SearchOptionsConstants.OPER_TYPE[i];

        throw new IllegalArgumentException(ErrorCode
            .format(SearchOptionsConstants.ERRMSG_OPER_SEL_INVALID, s));
    }

    private int operType;
}
