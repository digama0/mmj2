//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchResultsJButton.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

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
