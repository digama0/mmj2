//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsJIntegerTextField.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField;
import javax.swing.JLabel;

import mmj.pa.ErrorCode;

public class SearchOptionsJIntegerTextField extends JFormattedTextField
    implements SearchOptionsScrnMapField
{

    public static NumberFormat createIntegerFormat(final int i) {
        final NumberFormat numberformat = NumberFormat.getIntegerInstance();
        numberformat.setGroupingUsed(false);
        numberformat.setParseIntegerOnly(true);
        numberformat.setMinimumIntegerDigits(1);
        numberformat
            .setMaximumIntegerDigits(SearchOptionsConstants.FIELD_ATTR[i].columns);
        return numberformat;
    }

    public SearchOptionsJIntegerTextField(final int i) {
        this(createIntegerFormat(i), i,
            SearchOptionsConstants.FIELD_ATTR[i].defaultText,
            SearchOptionsConstants.FIELD_ATTR[i].columns);
    }

    public SearchOptionsJIntegerTextField(final NumberFormat numberformat,
        final int i, final String s, final int j)
    {
        super(numberformat);
        defaultValue = null;
        fieldId = i;
        setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        setColumns(j);
        set(s);
        setDefaultToCurrentValue();
        addMouseListener(new SearchJTextFieldPopupMenuListener(
            new SearchJTextFieldPopupMenu()));
        jLabel = new JLabel(SearchOptionsConstants.FIELD_ATTR[i].label);
        jLabel.setToolTipText(SearchOptionsConstants.FIELD_ATTR[i].toolTip);
        jLabel.setLabelFor(this);
    }

    @Override
    public void setEnabled(final boolean flag) {
        super.setEnabled(flag);
        jLabel.setEnabled(flag);
    }

    public int getFieldId() {
        return fieldId;
    }

    public void positionCursor(final int i) {
        setCaretPosition(i);
    }

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
        try {
            setValue(new Integer(s));
        } catch (final NumberFormatException numberformatexception) {
            throw new IllegalArgumentException(ErrorCode.format(
                SearchOptionsConstants.ERRMSG_ARG_INTEGER_TEXT_INVALID, s,
                fieldId, SearchOptionsConstants.FIELD_ATTR[fieldId].label));
        }
    }

    public String get() {
        return getValue().toString();
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
