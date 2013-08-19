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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

public class SearchResultsPopupMenuListener extends MouseAdapter {

    public SearchResultsPopupMenuListener(final JPopupMenu jpopupmenu) {
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
