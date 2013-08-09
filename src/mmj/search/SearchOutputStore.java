// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOutputStore.java

package mmj.search;

import java.util.*;

import mmj.lang.Assrt;

// Referenced classes of package mmj.search:
//            SearchSelectionItem, SearchOutput

public class SearchOutputStore {

    public SearchOutputStore(final int i, final int j) {
        maxResults = i;
        outputSortNbr = j;
        storeList = new LinkedList<SearchSelectionItem>();
        cntResults = 0;
        totalNbrLines = 0;
    }

    public void loadSearchOutput(final SearchOutput searchOutput,
        final String s, final boolean flag)
    {
        final String[] as = new String[1];
        if (flag)
            as[0] = SearchConstants.SEARCH_OUTPUT_LIST_MORE_LITERAL;
        else
            as[0] = SearchConstants.SEARCH_OUTPUT_LIST_END_LITERAL;
        add(null, as, -1);
        Comparator<SearchSelectionItem> comparator = null;
        switch (outputSortNbr) {
            case 1: // '\001'
                comparator = SearchSelectionItem.OUTPUT_SORT_1;
                break;

            case 2: // '\002'
                comparator = SearchSelectionItem.OUTPUT_SORT_2;
                break;

            case 3: // '\003'
                comparator = SearchSelectionItem.OUTPUT_SORT_3;
                break;

            case 4: // '\004'
                comparator = SearchSelectionItem.OUTPUT_SORT_4;
                break;

            case 5: // '\005'
                comparator = SearchSelectionItem.OUTPUT_SORT_5;
                break;

            case 6: // '\006'
                comparator = SearchSelectionItem.OUTPUT_SORT_6;
                break;

            case 7: // '\007'
                comparator = SearchSelectionItem.OUTPUT_SORT_7;
                break;

            case 8: // '\b'
                comparator = SearchSelectionItem.OUTPUT_SORT_8;
                break;

            case 9: // '\t'
                comparator = SearchSelectionItem.OUTPUT_SORT_9;
                break;

            case 10: // '\n'
                comparator = SearchSelectionItem.OUTPUT_SORT_10;
                break;
        }
        if (comparator != null)
            Collections.sort(storeList, comparator);
        final List<Assrt> arraylist = new ArrayList<Assrt>(storeList.size());
        final int ai[] = new int[storeList.size()];
        final int ai1[] = new int[totalNbrLines];
        final String[] as1 = new String[totalNbrLines];
        final ListIterator<SearchSelectionItem> listiterator = storeList
            .listIterator();
        int i = 0;
        for (int j = 0; listiterator.hasNext(); j++) {
            final SearchSelectionItem searchSelectionItem = listiterator.next();
            arraylist.add(searchSelectionItem.assrt);
            ai[j] = searchSelectionItem.score;
            for (final String element : searchSelectionItem.selection) {
                ai1[i] = j;
                as1[i] = element;
                i++;
            }

        }

        searchOutput.step = s;
        searchOutput.sortedAssrtResultsList = arraylist;
        searchOutput.sortedAssrtScoreArray = ai;
        searchOutput.refIndexArray = ai1;
        searchOutput.selectionArray = as1;
    }

    public boolean add(final Assrt assrt, final String[] as, final int i) {
        storeList.add(new SearchSelectionItem(assrt, as, i));
        cntResults++;
        totalNbrLines += as.length;
        return isFull();
    }

    public boolean isFull() {
        return cntResults >= maxResults;
    }

    private final int maxResults;
    private int cntResults;
    private int totalNbrLines;
    private final int outputSortNbr;
    private final LinkedList<SearchSelectionItem> storeList;
}