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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.DefaultEditorKit;

public class SearchJTextFieldPopupMenu extends JPopupMenu {

    public SearchJTextFieldPopupMenu() {
        JMenuItem item = new JMenuItem(new DefaultEditorKit.CutAction());
        item.setText(SearchOptionsConstants.GUI_EDIT_MENU_CUT_ITEM_TEXT);
        add(item);
        item = new JMenuItem(new DefaultEditorKit.CopyAction());
        item.setText(SearchOptionsConstants.GUI_EDIT_MENU_COPY_ITEM_TEXT);
        add(item);
        item = new JMenuItem(new DefaultEditorKit.PasteAction());
        item.setText(SearchOptionsConstants.GUI_EDIT_MENU_PASTE_ITEM_TEXT);
        add(item);
    }
}
