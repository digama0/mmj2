//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsJCheckBox.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

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
