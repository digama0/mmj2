//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchResultsScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Font;

import javax.swing.Box;

public class SearchResultsScrnMap {

    public SearchSelectionScrnMap getSearchSelectionScrnMap() {
        return searchSelectionScrnMap;
    }

    public SearchResultsScrnMap(final Font font,
        final SearchResultsButtonHandler searchResultsButtonHandler,
        final boolean flag, final SearchOutput searchOutput,
        final SearchMgr searchMgr)
    {
        scrnMapButton = new SearchResultsJButton[7];
        searchResultsFont = font;
        this.searchResultsButtonHandler = searchResultsButtonHandler;
        stepSearchMode = flag;
        this.searchMgr = searchMgr;
        initScrnMap();
    }

    public void downloadToScrnMap(final SearchMgr searchMgr) {
        searchSelectionBox.remove(searchSelectionScrnMap);
        searchSelectionScrnMap = new SearchSelectionScrnMap(searchMgr,
            new SearchSelectionJList(searchMgr));
        scrnMapField[0] = searchSelectionScrnMap;
        searchSelectionBox.add(searchSelectionScrnMap);
    }

    public void setSearchResultsFont(final Font font) {
        searchResultsFont = font;
        for (final SearchResultsScrnMapField element : scrnMapField)
            element.setSearchResultsFont(font);

        for (final SearchResultsJButton element : scrnMapButton)
            element.setFont(font);

    }

    public Box getSearchResultsBox() {
        return searchResultsBox;
    }

    public int getSelectedIndex() {
        return searchSelectionScrnMap.getSelectedIndex();
    }

    public void positionCursor(final int i, final int j) {
        for (final SearchResultsScrnMapField element : scrnMapField)
            if (element.getFieldId() == i) {
                element.positionCursor(j);
                return;
            }

    }

    public void setStepSearchFieldsEnabled(final boolean flag) {
        scrnMapButton[0].setEnabled(flag);
    }

    private void initScrnMap() {
        for (int i = 0; i < scrnMapButton.length; i++)
            scrnMapButton[i] = new SearchResultsJButton(i,
                searchResultsButtonHandler);

        searchSelectionScrnMap = new SearchSelectionScrnMap(searchMgr,
            new SearchSelectionJList(searchMgr));
        scrnMapField = new SearchResultsScrnMapField[]{searchSelectionScrnMap};
        setSearchResultsFont(searchResultsFont);
        setStepSearchFieldsEnabled(stepSearchMode);
        buttonBox = Box.createHorizontalBox();
        buttonBox.add(scrnMapButton[0]);
        buttonBox.add(scrnMapButton[1]);
        buttonBox.add(scrnMapButton[2]);
        buttonBox.add(scrnMapButton[3]);
        buttonBox.add(scrnMapButton[4]);
        buttonBox.add(scrnMapButton[5]);
        buttonBox.add(scrnMapButton[6]);
        buttonBox.add(Box.createHorizontalGlue());
        searchSelectionBox = Box.createHorizontalBox();
        searchSelectionBox.add(searchSelectionScrnMap);
        searchResultsBox = Box.createVerticalBox();
        searchResultsBox.add(buttonBox);
        searchResultsBox.add(searchSelectionBox);
    }

    private final SearchResultsButtonHandler searchResultsButtonHandler;
    private Box searchResultsBox;
    private Box buttonBox;
    private Box searchSelectionBox;
    private Font searchResultsFont;
    private final boolean stepSearchMode;
    private final SearchMgr searchMgr;
    private SearchResultsScrnMapField[] scrnMapField;
    private final SearchResultsJButton[] scrnMapButton;
    SearchSelectionScrnMap searchSelectionScrnMap;
}
