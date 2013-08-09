// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchJTextFieldPopupMenuListener.java

package mmj.search;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class SearchJTextFieldPopupMenuListener extends MouseAdapter {

    public SearchJTextFieldPopupMenuListener(final JPopupMenu jpopupmenu) {
        popupMenu = jpopupmenu;
    }

    @Override
    public void mousePressed(final MouseEvent mouseevent) {
        popupMenuForMouse(mouseevent);
    }

    @Override
    public void mouseReleased(final MouseEvent mouseevent) {
        popupMenuForMouse(mouseevent);
    }

    public void popupMenuForMouse(final MouseEvent mouseevent) {
        if (mouseevent.isPopupTrigger())
            popupMenu.show(mouseevent.getComponent(), mouseevent.getX(),
                mouseevent.getY());
    }

    private final JPopupMenu popupMenu;
}