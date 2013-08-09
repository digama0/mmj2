// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchOptionsJButton.java

package mmj.search;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

// Referenced classes of package mmj.search:
//            SearchOptionsConstants, SearchOptionsButtonAttr, SearchOptionsButtonHandler

public class SearchOptionsJButton extends JButton implements ActionListener {

    protected final int buttonId;
    protected final SearchOptionsButtonHandler searchOptionsButtonHandler;

    public SearchOptionsJButton(final int i,
        final SearchOptionsButtonHandler searchOptionsButtonHandler)
    {
        super(SearchOptionsConstants.BUTTON_ATTR[i].label);
        buttonId = i;
        this.searchOptionsButtonHandler = searchOptionsButtonHandler;
        setToolTipText(SearchOptionsConstants.BUTTON_ATTR[i].toolTip);
        setActionCommand(SearchOptionsConstants.BUTTON_ATTR[i].label);
        addActionListener(this);
    }

    public void setSearchOptionsFont(final Font font) {
        setFont(font);
    }

    public void actionPerformed(final ActionEvent e) {
        searchOptionsButtonHandler.searchOptionsButtonPressed(buttonId);
    }
}
