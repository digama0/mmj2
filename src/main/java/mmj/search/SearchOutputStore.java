//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOutputStore.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.*;

import mmj.lang.Assrt;

public class SearchOutputStore {

    private final int maxResults;
    private int cntResults = 0;
    private final int outputSortNbr;
    private final List<SearchSelectionItem> storeList = new LinkedList<>();

    public SearchOutputStore(final int maxResults, final int outputSortNbr) {
        this.maxResults = maxResults;
        this.outputSortNbr = outputSortNbr;
    }

    public void loadSearchOutput(final SearchOutput searchOutput,
        final String step, final boolean full)
    {
        final String[] selection = new String[1];
        if (full)
            selection[0] = SearchConstants.SEARCH_OUTPUT_LIST_MORE_LITERAL;
        else
            selection[0] = SearchConstants.SEARCH_OUTPUT_LIST_END_LITERAL;
        add(null, selection, -1);
        final Comparator<SearchSelectionItem> sort = SearchSelectionItem.OUTPUT_SORTS[outputSortNbr];
        if (sort != null)
            Collections.sort(storeList, sort);
        searchOutput.sortedAssrtResultsList = new ArrayList<>(
            storeList.size());
        searchOutput.sortedAssrtScoreArray = new int[storeList.size()];
        searchOutput.selectionArray = new String[storeList.size()];
        int j = 0;
        for (final SearchSelectionItem item : storeList) {
            searchOutput.sortedAssrtResultsList.add(item.assrt);
            searchOutput.sortedAssrtScoreArray[j] = item.score;
            final StringBuilder sb = new StringBuilder("<html>");
            String delim = "";
            for (final String element : item.selection) {
                sb.append(delim).append(
                    element.replace("&", "&amp;").replace("<", "&lt;")
                        .replace(">", "&gt;"));
                delim = "<br/>";
            }
            searchOutput.selectionArray[j++] = sb.append("</html>").toString();
        }

        searchOutput.step = step;
    }
    public boolean add(final Assrt assrt, final String[] selection,
        final int score)
    {
        storeList.add(new SearchSelectionItem(assrt, selection, score));
        cntResults++;
        return isFull();
    }

    public boolean isFull() {
        return cntResults >= maxResults;
    }
}
