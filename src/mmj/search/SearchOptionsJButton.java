//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsJButton.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

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
