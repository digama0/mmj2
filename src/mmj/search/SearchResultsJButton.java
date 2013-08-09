// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchResultsJButton.java

package mmj.search;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

// Referenced classes of package mmj.search:
//            SearchResultsConstants, SearchResultsButtonAttr, SearchResultsButtonHandler

public class SearchResultsJButton extends JButton implements ActionListener {

    public SearchResultsJButton(final int i,
        final SearchResultsButtonHandler searchResultsButtonHandler)
    {
        super(SearchResultsConstants.BUTTON_ATTR[i].label);
        buttonId = i;
        this.searchResultsButtonHandler = searchResultsButtonHandler;
        setToolTipText(SearchResultsConstants.BUTTON_ATTR[i].toolTip);
        setActionCommand(SearchResultsConstants.BUTTON_ATTR[i].label);
        addActionListener(this);
    }

    public void setSearchResultsFont(final Font font) {
        setFont(font);
    }

    public void actionPerformed(final ActionEvent e) {
        searchResultsButtonHandler.searchResultsButtonPressed(buttonId);
    }

    protected final int buttonId;
    protected final SearchResultsButtonHandler searchResultsButtonHandler;
}
