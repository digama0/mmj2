//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchSelectionScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JScrollPane;

public class SearchSelectionScrnMap extends JScrollPane implements
    SearchResultsScrnMapField
{

    public SearchSelectionScrnMap(final SearchMgr searchMgr,
        final SearchSelectionJList searchSelectionJList)
    {
        super(searchSelectionJList);
        this.searchMgr = searchMgr;
        this.searchSelectionJList = searchSelectionJList;
        setToolTipText(SearchResultsConstants.FIELD_ATTR[fieldId].toolTip);
        setPreferredSize(getNewPreferredSize());
        setAlignmentX(0.0F);
    }

    public synchronized Dimension getNewPreferredSize() {
        return searchMgr.getSearchSelectionPreferredSize();
    }

    public int getFieldId() {
        return fieldId;
    }

    public void positionCursor(final int i) {}

    public void setSearchResultsFont(final Font font) {
        searchSelectionJList.setFont(font);
    }

    public int getSelectedIndex() {
        return searchSelectionJList.getSelectedIndex();
    }

    protected final int fieldId = 0;
    private final SearchMgr searchMgr;
    private final SearchSelectionJList searchSelectionJList;
}
