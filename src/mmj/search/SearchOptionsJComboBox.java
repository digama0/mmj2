//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsJComboBox.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;
import java.util.Vector;

import javax.swing.*;

public abstract class SearchOptionsJComboBox extends JComboBox<String>
    implements SearchOptionsScrnMapField
{

    public static DefaultComboBoxModel<String> buildUpdateableComboBoxModel(
        final int i)
    {
        final Vector<String> vector = new Vector<>();
        final String[] as = SearchOptionsConstants.FIELD_ATTR[i].comboBoxItems;
        for (final String element : as)
            vector.add(element);

        return new DefaultComboBoxModel<>(vector);
    }

    public static DefaultComboBoxModel<String> buildUpdateableComboBoxModel(
        final String[] as)
    {
        final Vector<String> vector = new Vector<>();
        for (final String element : as)
            vector.add(element);

        return new DefaultComboBoxModel<>(vector);
    }

    public SearchOptionsJComboBox(final int i,
        final ComboBoxModel<String> comboboxmodel)
    {
        super(comboboxmodel);
        defaultValue = null;
        fieldId = i;
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        setSelectedItem(SearchOptionsConstants.FIELD_ATTR[i].defaultText);
        setDefaultToCurrentValue();
    }

    public SearchOptionsJComboBox(final int i, final String[] as,
        final String s)
    {
        super(as);
        defaultValue = null;
        fieldId = i;
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        setSelectedItem(s);
        setDefaultToCurrentValue();
    }

    public SearchOptionsJComboBox(final int i, final String[] as) {
        this(i, as, SearchOptionsConstants.FIELD_ATTR[i].defaultText);
    }

    public SearchOptionsJComboBox(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].comboBoxItems,
            SearchOptionsConstants.FIELD_ATTR[i].defaultText);
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
        setSelectedItem(s);
    }

    public String get() {
        return (String)getSelectedItem();
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
