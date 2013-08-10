//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SearchSelectionJList.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.HeadlessException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JList;
import javax.swing.JOptionPane;

public class SearchSelectionJList extends JList<String> {

    public SearchSelectionJList(final SearchMgr searchMgr) {
        this.searchMgr = searchMgr;
        setFont(searchMgr.getSearchResultsFont());
        setListData(searchMgr.getSearchOutput().selectionArray);
        setSelectionMode(0);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent mouseevent) {
                if (mouseevent.getClickCount() == 2)
                    doubleClicked();
                if (mouseevent.getButton() == 2 || mouseevent.getButton() == 3)
                    popupSelectionItem();
            }
        });
    }

    private void doubleClicked() {
        searchMgr.execApplySearchSelection(getSelectedIndex());
    }

    private void popupSelectionItem() {
        final boolean flag = searchMgr.getStepSearchMode();
        final int i = getSelectedIndex();
        if (i == -1)
            return;
        final String s = searchMgr.getSearchOutput().selectionArray[i];
        String s1;
        byte byte0;
        byte byte1;
        if (flag) {
            s1 = SearchResultsConstants.SEARCH_RESULTS_POPUP_APPLY_BUTTON_CAPTION;
            byte0 = 3;
            byte1 = 0;
        }
        else {
            s1 = SearchResultsConstants.SEARCH_RESULTS_POPUP_SELECTION_CAPTION;
            byte0 = 1;
            byte1 = -1;
        }
        try {
            final int j = JOptionPane.showConfirmDialog(null,
                SearchMgr.reformatMessage(s), s1, byte1, byte0);
            if (j == 0 && flag)
                searchMgr.execApplySearchSelection(i);
        } catch (final HeadlessException headlessexception) {}
    }

    SearchMgr searchMgr;

}
