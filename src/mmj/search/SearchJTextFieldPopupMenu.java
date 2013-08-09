// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchJTextFieldPopupMenu.java

package mmj.search;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

public class SearchJTextFieldPopupMenu extends JPopupMenu {

    public SearchJTextFieldPopupMenu() {
        JMenuItem item = new JMenuItem(new DefaultEditorKit.CutAction());
        item.setText(SearchOptionsConstants.GUI_EDIT_MENU_CUT_ITEM_TEXT);
        add(item);
        item = new JMenuItem(new DefaultEditorKit.CopyAction());
        item.setText(SearchOptionsConstants.GUI_EDIT_MENU_CUT_ITEM_TEXT);
        add(item);
        item = new JMenuItem(new DefaultEditorKit.PasteAction());
        item.setText(SearchOptionsConstants.GUI_EDIT_MENU_PASTE_ITEM_TEXT);
        add(item);
    }
}