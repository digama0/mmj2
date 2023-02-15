//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * {{file}}.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

public class SearchOptionsFieldAttr {

    public final int fieldId;
    public final String label;
    public final String toolTip;
    public final int columns;
    public final String defaultText;
    public final boolean isSearchControl;
    public final int rowIndex;
    public final String[] comboBoxItems;

    public SearchOptionsFieldAttr(final int fieldId, final String label,
        final String toolTip, final int columns, final String defaultText,
        final boolean isSearchControl, final int rowIndex,
        final String[] comboBoxItems)
    {
        this.fieldId = fieldId;
        this.label = label;
        this.toolTip = toolTip;
        this.columns = columns;
        this.defaultText = defaultText;
        this.isSearchControl = isSearchControl;
        this.rowIndex = rowIndex;
        this.comboBoxItems = comboBoxItems;
    }
}
