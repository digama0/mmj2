// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchSelectionScrnMap.java

package mmj.search;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JScrollPane;

// Referenced classes of package mmj.search:
//            SearchResultsScrnMapField, SearchResultsConstants, SearchResultsFieldAttr, SearchMgr,
//            SearchSelectionJList

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
