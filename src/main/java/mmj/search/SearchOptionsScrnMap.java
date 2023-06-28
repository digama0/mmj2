//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchOptionsScrnMap.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.awt.Color;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.JLabel;

public class SearchOptionsScrnMap {

    public SearchOptionsScrnMap(final Font font,
        final SearchOptionsButtonHandler searchOptionsButtonHandler,
        final String[] as, final String[][] as1, final String[][] as2,
        final boolean flag, final SearchMgr searchMgr, final SearchArgs args)
    {
        scrnMapButton = new SearchOptionsJButton[12];
        searchDataLabel = new JLabel(
            SearchOptionsConstants.SEARCH_DATA_LABEL_TEXT);
        textSeparatorsLabel = new JLabel(
            SearchOptionsConstants.TEXT_SEPARATORS_LABEL_TEXT);
        orSeparatorScrnMap = new OrSeparatorScrnMap();
        orSeparatorLabel = orSeparatorScrnMap.createJLabel();
        singleQuoteScrnMap = new SingleQuoteScrnMap();
        singleQuoteLabel = singleQuoteScrnMap.createJLabel();
        doubleQuoteScrnMap = new DoubleQuoteScrnMap();
        doubleQuoteLabel = doubleQuoteScrnMap.createJLabel();
        inWhatScrnMap = new InWhatScrnMap[4];
        partScrnMap = new PartScrnMap[4];
        formatScrnMap = new FormatScrnMap[4];
        operScrnMap = new OperScrnMap[4];
        forWhatScrnMap = new ForWhatScrnMap[4];
        boolScrnMap = new BoolScrnMap[4];
        searchControlsLabel = new JLabel(
            SearchOptionsConstants.SEARCH_CONTROLS_LABEL_TEXT);
        exclLabelsScrnMap = new ExclLabelsScrnMap();
        exclLabelsLabel = exclLabelsScrnMap.createJLabel();
        exclusionLabel = new JLabel(SearchOptionsConstants.EXCLUSION_LABEL_TEXT);
        extSearchLabel = new JLabel(
            SearchOptionsConstants.EXT_SEARCH_LABEL_TEXT);
        outputLabel = new JLabel(SearchOptionsConstants.OUTPUT_LABEL_TEXT);
        minProofRefsScrnMap = new MinProofRefsScrnMap();
        minProofRefsLabel = minProofRefsScrnMap.createJLabel();
        resultsCheckedScrnMap = new ResultsCheckedScrnMap();
        resultsCheckedLabel = resultsCheckedScrnMap.createJLabel();
        maxTimeScrnMap = new MaxTimeScrnMap();
        maxTimeLabel = maxTimeScrnMap.createJLabel();
        minHypsScrnMap = new MinHypsScrnMap();
        minHypsLabel = minHypsScrnMap.createJLabel();
        maxExtResultsScrnMap = new MaxExtResultsScrnMap();
        maxExtResultsLabel = maxExtResultsScrnMap.createJLabel();
        substitutionsScrnMap = new SubstitutionsScrnMap();
        substitutionsLabel = substitutionsScrnMap.createJLabel();
        maxHypsScrnMap = new MaxHypsScrnMap();
        maxHypsLabel = maxHypsScrnMap.createJLabel();
        maxIncompHypsScrnMap = new MaxIncompHypsScrnMap();
        maxIncompHypsLabel = maxIncompHypsScrnMap.createJLabel();
        commentsScrnMap = new CommentsScrnMap();
        commentsLabel = commentsScrnMap.createJLabel();
        maxResultsScrnMap = new MaxResultsScrnMap();
        maxResultsLabel = maxResultsScrnMap.createJLabel();
        prevStepsCheckedScrnMap = new PrevStepsCheckedScrnMap();
        prevStepsCheckedLabel = prevStepsCheckedScrnMap.createJLabel();
        autoSelectScrnMap = new AutoSelectScrnMap();
        autoSelectLabel = autoSelectScrnMap.createJLabel();
        chapSecHierarchyScrnMap = new ChapSecHierarchyScrnMap();
        chapSecHierarchyLabel = chapSecHierarchyScrnMap.createJLabel();
        reuseDerivStepsScrnMap = new ReuseDerivStepsScrnMap();
        reuseDerivStepsLabel = reuseDerivStepsScrnMap.createJLabel();
        statsScrnMap = new StatsScrnMap();
        statsLabel = statsScrnMap.createJLabel();
        outputSortScrnMap = new OutputSortScrnMap();
        outputSortLabel = outputSortScrnMap.createJLabel();
        searchOptionsFont = font;
        this.searchOptionsButtonHandler = searchOptionsButtonHandler;
        chapValues = as;
        secValues = as1;
        forWhatPriorValues = as2;
        stepSearchMode = flag;
        initScrnMap();
        if (searchMgr != null && args != null)
            downloadToScrnMap(args, searchMgr, false);
    }

    public void updateForWhatHistory() {
        if (forWhatScrnMap != null)
            for (final ForWhatScrnMap element : forWhatScrnMap)
                element.searchRequested();
    }

    public void setSearchOptionsFont(final Font font) {
        searchOptionsFont = font;
        for (final SearchOptionsScrnMapField element : scrnMapField)
            element.setSearchOptionsFont(font);

        for (final JLabel element : scrnMapLabel)
            element.setFont(font);

        for (final SearchOptionsJButton element : scrnMapButton)
            element.setFont(font);

    }

    public Box getSearchOptionsBox() {
        return searchOptionsBox;
    }

    public void uploadFromScrnMap(final SearchArgs args) {
        uploadFromScrnMap(args, false);
    }

    public void uploadFromScrnMap(final SearchArgs args, final boolean flag) {
        for (final SearchOptionsScrnMapField element : scrnMapField)
            if (!flag
                || SearchOptionsConstants.FIELD_ATTR[element.getFieldId()].isSearchControl)
                element.uploadFromScrnMap(args);

    }

    public void downloadToScrnMap(final SearchArgs args,
        final SearchMgr searchMgr, final boolean flag)
    {
        for (int i = 0; i < scrnMapField.length; i++)
            if (!flag
                || !SearchOptionsConstants.FIELD_ATTR[scrnMapField[i]
                    .getFieldId()].isSearchControl)
                scrnMapField[i].downloadToScrnMap(args, searchMgr);

    }

    public String[][] getForWhatPriorValues() {
        final String[][] as = new String[4][];
        for (int i = 0; i < 4; i++)
            as[i] = forWhatScrnMap[i].getForWhatPriorValues();

        return as;
    }

    public void updateForWhatPriorValues(final String[][] as) {
        forWhatPriorValues = as;
        for (int i = 0; i < 4; i++)
            forWhatScrnMap[i].updateForWhatPriorValues(as[i]);

    }

    public void positionCursor(final int i, final int j) {
        for (final SearchOptionsScrnMapField element : scrnMapField)
            if (element.getFieldId() == i) {
                element.requestFocusInWindow(false);
                element.positionCursor(j);
                return;
            }

    }

    public void setStepSearchFieldsEnabled(final boolean flag) {
        extSearchLabel.setEnabled(flag);
        resultsCheckedScrnMap.setEnabled(flag);
        maxExtResultsScrnMap.setEnabled(flag);
        maxIncompHypsScrnMap.setEnabled(flag);
        prevStepsCheckedScrnMap.setEnabled(flag);
        reuseDerivStepsScrnMap.setEnabled(flag);
        substitutionsScrnMap.setEnabled(flag);
        autoSelectScrnMap.setEnabled(flag);
    }

    public void resetAllDefaults() {
        resetSearchOptionScrnMapDefaults(true, true);
    }

    public void resetSearchControlDefaults() {
        resetSearchOptionScrnMapDefaults(true, false);
    }

    public void resetSearchDataDefaults() {
        resetSearchOptionScrnMapDefaults(false, true);
    }

    public void resetSearchOptionScrnMapDefaults(final boolean flag,
        final boolean flag1)
    {
        for (final SearchOptionsScrnMapField element : scrnMapField) {
            final int i = element.getFieldId();
            if (SearchOptionsConstants.FIELD_ATTR[i].isSearchControl ? flag
                : flag1)
                element.resetToDefaultValue();
        }

    }

    public void setDefaultsToCurrentValues() {
        for (final SearchOptionsScrnMapField element : scrnMapField)
            element.setDefaultToCurrentValue();

    }

    private void initScrnMap() {
        for (int i = 0; i < scrnMapButton.length; i++)
            scrnMapButton[i] = new SearchOptionsJButton(i,
                searchOptionsButtonHandler);

        searchDataLabel.setForeground(Color.BLUE);
        textSeparatorsLabel.setForeground(Color.BLUE);
        for (int j = 0; j < 4; j++) {
            boolScrnMap[j] = new BoolScrnMap(j);
            forWhatScrnMap[j] = new ForWhatScrnMap(j, forWhatPriorValues[j],
                SearchOptionsJComboBox
                    .buildUpdateableComboBoxModel(forWhatPriorValues[j]));
            operScrnMap[j] = new OperScrnMap(
                j,
                SearchOptionsJComboBox
                    .buildUpdateableComboBoxModel(SearchOptionsConstants.FIELD_ATTR[SearchOptionsConstants.OPER_FIELD_ID[j]].fieldId));
            formatScrnMap[j] = new FormatScrnMap(
                j,
                SearchOptionsJComboBox
                    .buildUpdateableComboBoxModel(SearchOptionsConstants.FIELD_ATTR[SearchOptionsConstants.FORMAT_FIELD_ID[j]].fieldId),
                operScrnMap[j]);
            partScrnMap[j] = new PartScrnMap(
                j,
                SearchOptionsJComboBox
                    .buildUpdateableComboBoxModel(SearchOptionsConstants.FIELD_ATTR[SearchOptionsConstants.PART_FIELD_ID[j]].fieldId),
                formatScrnMap[j]);
            inWhatScrnMap[j] = new InWhatScrnMap(j, partScrnMap[j]);
        }

        inWhatLabel = inWhatScrnMap[0].createJLabel();
        partLabel = partScrnMap[0].createJLabel();
        formatLabel = formatScrnMap[0].createJLabel();
        operLabel = operScrnMap[0].createJLabel();
        forWhatLabel = forWhatScrnMap[0].createJLabel();
        boolLabel = boolScrnMap[0].createJLabel();
        searchControlsLabel.setForeground(Color.BLUE);
        exclusionLabel.setForeground(Color.BLUE);
        extSearchLabel.setForeground(Color.BLUE);
        outputLabel.setForeground(Color.BLUE);
        exclusionLabel
            .setToolTipText(SearchOptionsConstants.EXCLUSION_LABEL_TOOL_TIP);
        extSearchLabel
            .setToolTipText(SearchOptionsConstants.EXT_SEARCH_LABEL_TOOL_TIP);
        fromSecScrnMap = new FromSecScrnMap(
            SearchOptionsJComboBox.buildUpdateableComboBoxModel(secValues[0]),
            secValues);
        fromSecLabel = fromSecScrnMap.createJLabel();
        fromChapScrnMap = new FromChapScrnMap(chapValues, fromSecScrnMap);
        fromChapLabel = fromChapScrnMap.createJLabel();
        thruSecScrnMap = new ThruSecScrnMap(
            SearchOptionsJComboBox.buildUpdateableComboBoxModel(secValues[0]),
            secValues);
        thruSecLabel = thruSecScrnMap.createJLabel();
        thruChapScrnMap = new ThruChapScrnMap(chapValues, thruSecScrnMap);
        thruChapLabel = thruChapScrnMap.createJLabel();
        thruSecScrnMap.setThruChapScrnMap(thruChapScrnMap);
        fromSecScrnMap.setFromChapScrnMap(fromChapScrnMap);
        thruSecScrnMap.setThruChapScrnMap(thruChapScrnMap);
        fromChapScrnMap.setThruChapScrnMap(thruChapScrnMap);
        thruChapScrnMap.setFromChapScrnMap(fromChapScrnMap);
        outputLabel
            .setToolTipText(SearchOptionsConstants.OUTPUT_LABEL_TOOL_TIP);
        scrnMapLabel = new JLabel[]{searchDataLabel, textSeparatorsLabel,
                orSeparatorLabel, singleQuoteLabel, doubleQuoteLabel,
                inWhatLabel, partLabel, formatLabel, operLabel, forWhatLabel,
                boolLabel, searchControlsLabel, searchControlsLabel,
                exclusionLabel, extSearchLabel, outputLabel, exclLabelsLabel,
                minProofRefsLabel, resultsCheckedLabel, maxTimeLabel,
                minHypsLabel, maxExtResultsLabel, substitutionsLabel,
                maxHypsLabel, maxIncompHypsLabel, commentsLabel,
                maxResultsLabel, prevStepsCheckedLabel, autoSelectLabel,
                chapSecHierarchyLabel, reuseDerivStepsLabel, statsLabel,
                fromChapLabel, fromSecLabel, thruChapLabel, thruSecLabel,
                outputSortLabel};
        scrnMapField = new SearchOptionsScrnMapField[]{orSeparatorScrnMap,
                singleQuoteScrnMap, doubleQuoteScrnMap, boolScrnMap[0],
                forWhatScrnMap[0], operScrnMap[0], formatScrnMap[0],
                partScrnMap[0], inWhatScrnMap[0], boolScrnMap[1],
                forWhatScrnMap[1], operScrnMap[1], formatScrnMap[1],
                partScrnMap[1], inWhatScrnMap[1], boolScrnMap[2],
                forWhatScrnMap[2], operScrnMap[2], formatScrnMap[2],
                partScrnMap[2], inWhatScrnMap[2], boolScrnMap[3],
                forWhatScrnMap[3], operScrnMap[3], formatScrnMap[3],
                partScrnMap[3], inWhatScrnMap[3], exclLabelsScrnMap,
                minProofRefsScrnMap, resultsCheckedScrnMap, maxTimeScrnMap,
                minHypsScrnMap, maxExtResultsScrnMap, substitutionsScrnMap,
                maxHypsScrnMap, maxIncompHypsScrnMap, commentsScrnMap,
                maxResultsScrnMap, prevStepsCheckedScrnMap, autoSelectScrnMap,
                chapSecHierarchyScrnMap, reuseDerivStepsScrnMap, statsScrnMap,
                fromChapScrnMap, fromSecScrnMap, thruChapScrnMap,
                thruSecScrnMap, outputSortScrnMap};
        setSearchOptionsFont(searchOptionsFont);
        setStepSearchFieldsEnabled(stepSearchMode);
        setDefaultsToCurrentValues();
        final Box box = Box.createHorizontalBox();
        box.add(scrnMapButton[0]);
        box.add(scrnMapButton[1]);
        box.add(scrnMapButton[2]);
        box.add(scrnMapButton[3]);
        box.add(scrnMapButton[4]);
        box.add(scrnMapButton[5]);
        box.add(scrnMapButton[6]);
        box.add(scrnMapButton[7]);
        box.add(scrnMapButton[8]);
        box.add(scrnMapButton[9]);
        box.add(Box.createHorizontalGlue());
        final Box box1 = Box.createHorizontalBox();
        box1.add(searchDataLabel);
        box1.add(scrnMapButton[10]);
        box1.add(textSeparatorsLabel);
        box1.add(orSeparatorLabel);
        box1.add(orSeparatorScrnMap);
        box1.add(singleQuoteLabel);
        box1.add(singleQuoteScrnMap);
        box1.add(doubleQuoteLabel);
        box1.add(doubleQuoteScrnMap);
        final Box box2 = Box.createVerticalBox();
        box2.add(inWhatLabel);
        for (int k = 0; k < 4; k++)
            box2.add(inWhatScrnMap[k]);

        final Box box3 = Box.createVerticalBox();
        box3.add(partLabel);
        for (int l = 0; l < 4; l++)
            box3.add(partScrnMap[l]);

        final Box box4 = Box.createVerticalBox();
        box4.add(formatLabel);
        for (int i1 = 0; i1 < 4; i1++)
            box4.add(formatScrnMap[i1]);

        final Box box5 = Box.createVerticalBox();
        box5.add(operLabel);
        for (int j1 = 0; j1 < 4; j1++)
            box5.add(operScrnMap[j1]);

        final Box box6 = Box.createVerticalBox();
        box6.add(forWhatLabel);
        for (int k1 = 0; k1 < 4; k1++)
            box6.add(forWhatScrnMap[k1]);

        final Box box7 = Box.createVerticalBox();
        box7.add(boolLabel);
        for (int l1 = 0; l1 < 4; l1++)
            box7.add(boolScrnMap[l1]);

        final Box box8 = Box.createHorizontalBox();
        box8.add(searchControlsLabel);
        box8.add(scrnMapButton[11]);
        box8.add(exclLabelsLabel);
        box8.add(exclLabelsScrnMap);
        final Box box9 = Box.createHorizontalBox();
        box9.add(exclusionLabel);
        box9.add(Box.createHorizontalGlue());
        final Box box10 = Box.createHorizontalBox();
        box10.add(minProofRefsLabel);
        box10.add(minProofRefsScrnMap);
        final Box box11 = Box.createHorizontalBox();
        box11.add(minHypsLabel);
        box11.add(minHypsScrnMap);
        final Box box12 = Box.createHorizontalBox();
        box12.add(maxHypsLabel);
        box12.add(maxHypsScrnMap);
        final Box box13 = Box.createHorizontalBox();
        box13.add(maxResultsLabel);
        box13.add(maxResultsScrnMap);
        final Box box14 = Box.createHorizontalBox();
        box14.add(chapSecHierarchyLabel);
        box14.add(chapSecHierarchyScrnMap);
        box14.add(Box.createHorizontalGlue());
        final Box box15 = Box.createVerticalBox();
        box15.add(box9);
        box15.add(box10);
        box15.add(box11);
        box15.add(box12);
        box15.add(box13);
        box15.add(box14);
        final Box box16 = Box.createHorizontalBox();
        box16.add(extSearchLabel);
        box16.add(Box.createHorizontalGlue());
        final Box box17 = Box.createHorizontalBox();
        box17.add(resultsCheckedLabel);
        box17.add(resultsCheckedScrnMap);
        final Box box18 = Box.createHorizontalBox();
        box18.add(maxExtResultsLabel);
        box18.add(maxExtResultsScrnMap);
        final Box box19 = Box.createHorizontalBox();
        box19.add(maxIncompHypsLabel);
        box19.add(maxIncompHypsScrnMap);
        final Box box20 = Box.createHorizontalBox();
        box20.add(prevStepsCheckedLabel);
        box20.add(prevStepsCheckedScrnMap);
        final Box box21 = Box.createHorizontalBox();
        box21.add(reuseDerivStepsLabel);
        box21.add(reuseDerivStepsScrnMap);
        box21.add(Box.createHorizontalGlue());
        final Box box22 = Box.createVerticalBox();
        box22.add(box16);
        box22.add(box17);
        box22.add(box18);
        box22.add(box19);
        box22.add(box20);
        box22.add(box21);
        final Box box23 = Box.createHorizontalBox();
        box23.add(maxTimeLabel);
        box23.add(maxTimeScrnMap);
        final Box box24 = Box.createHorizontalBox();
        box24.add(substitutionsLabel);
        box24.add(substitutionsScrnMap);
        box24.add(Box.createHorizontalGlue());
        final Box box25 = Box.createHorizontalBox();
        box25.add(commentsLabel);
        box25.add(commentsScrnMap);
        box25.add(Box.createHorizontalGlue());
        final Box box26 = Box.createHorizontalBox();
        box26.add(autoSelectLabel);
        box26.add(autoSelectScrnMap);
        box26.add(Box.createHorizontalGlue());
        final Box box27 = Box.createHorizontalBox();
        box27.add(statsLabel);
        box27.add(statsScrnMap);
        box27.add(Box.createHorizontalGlue());
        final Box box28 = Box.createHorizontalBox();
        box28.add(outputLabel);
        box28.add(Box.createHorizontalGlue());
        final Box box29 = Box.createVerticalBox();
        box29.add(box28);
        box29.add(box23);
        box29.add(box24);
        box29.add(box25);
        box29.add(box26);
        box29.add(box27);
        final Box box30 = Box.createHorizontalBox();
        box30.add(fromChapLabel);
        box30.add(fromChapScrnMap);
        final Box box31 = Box.createHorizontalBox();
        box31.add(fromSecLabel);
        box31.add(fromSecScrnMap);
        final Box box32 = Box.createHorizontalBox();
        box32.add(thruChapLabel);
        box32.add(thruChapScrnMap);
        final Box box33 = Box.createHorizontalBox();
        box33.add(thruSecLabel);
        box33.add(thruSecScrnMap);
        final Box box34 = Box.createVerticalBox();
        box34.add(box30);
        box34.add(box31);
        box34.add(box32);
        box34.add(box33);
        final Box box35 = Box.createHorizontalBox();
        box35.add(outputSortLabel);
        box35.add(outputSortScrnMap);
        final Box box36 = Box.createHorizontalBox();
        box36.add(box2);
        box36.add(box3);
        box36.add(box4);
        box36.add(box5);
        box36.add(box6);
        box36.add(box7);
        final Box box37 = Box.createVerticalBox();
        box37.add(box1);
        box37.add(box36);
        final Box box38 = Box.createHorizontalBox();
        box38.add(box15);
        box38.add(box22);
        box38.add(box29);
        final Box box39 = Box.createVerticalBox();
        box39.add(box8);
        box39.add(box38);
        box39.add(box34);
        box39.add(box35);
        searchOptionsBox = Box.createVerticalBox();
        searchOptionsBox.add(box);
        searchOptionsBox.add(box37);
        searchOptionsBox.add(box39);
    }

    private final SearchOptionsButtonHandler searchOptionsButtonHandler;
    private Box searchOptionsBox;
    private Font searchOptionsFont;
    private final String[] chapValues;
    private final String[][] secValues;
    private String[][] forWhatPriorValues;
    private final boolean stepSearchMode;
    private JLabel[] scrnMapLabel;
    private SearchOptionsScrnMapField[] scrnMapField;
    private final SearchOptionsJButton[] scrnMapButton;
    private final JLabel searchDataLabel;
    private final JLabel textSeparatorsLabel;
    private final OrSeparatorScrnMap orSeparatorScrnMap;
    private final JLabel orSeparatorLabel;
    private final SingleQuoteScrnMap singleQuoteScrnMap;
    private final JLabel singleQuoteLabel;
    private final DoubleQuoteScrnMap doubleQuoteScrnMap;
    private final JLabel doubleQuoteLabel;
    private final InWhatScrnMap[] inWhatScrnMap;
    private JLabel inWhatLabel;
    private final PartScrnMap[] partScrnMap;
    private JLabel partLabel;
    private final FormatScrnMap[] formatScrnMap;
    private JLabel formatLabel;
    private final OperScrnMap[] operScrnMap;
    private JLabel operLabel;
    private final ForWhatScrnMap[] forWhatScrnMap;
    private JLabel forWhatLabel;
    private final BoolScrnMap[] boolScrnMap;
    private JLabel boolLabel;
    private final JLabel searchControlsLabel;
    private final ExclLabelsScrnMap exclLabelsScrnMap;
    private final JLabel exclLabelsLabel;
    private final JLabel exclusionLabel;
    private final JLabel extSearchLabel;
    private final JLabel outputLabel;
    private final MinProofRefsScrnMap minProofRefsScrnMap;
    private final JLabel minProofRefsLabel;
    private final ResultsCheckedScrnMap resultsCheckedScrnMap;
    private final JLabel resultsCheckedLabel;
    private final MaxTimeScrnMap maxTimeScrnMap;
    private final JLabel maxTimeLabel;
    private final MinHypsScrnMap minHypsScrnMap;
    private final JLabel minHypsLabel;
    private final MaxExtResultsScrnMap maxExtResultsScrnMap;
    private final JLabel maxExtResultsLabel;
    private final SubstitutionsScrnMap substitutionsScrnMap;
    private final JLabel substitutionsLabel;
    private final MaxHypsScrnMap maxHypsScrnMap;
    private final JLabel maxHypsLabel;
    private final MaxIncompHypsScrnMap maxIncompHypsScrnMap;
    private final JLabel maxIncompHypsLabel;
    private final CommentsScrnMap commentsScrnMap;
    private final JLabel commentsLabel;
    private final MaxResultsScrnMap maxResultsScrnMap;
    private final JLabel maxResultsLabel;
    private final PrevStepsCheckedScrnMap prevStepsCheckedScrnMap;
    private final JLabel prevStepsCheckedLabel;
    private final AutoSelectScrnMap autoSelectScrnMap;
    private final JLabel autoSelectLabel;
    private final ChapSecHierarchyScrnMap chapSecHierarchyScrnMap;
    private final JLabel chapSecHierarchyLabel;
    private final ReuseDerivStepsScrnMap reuseDerivStepsScrnMap;
    private final JLabel reuseDerivStepsLabel;
    private final StatsScrnMap statsScrnMap;
    private final JLabel statsLabel;
    private FromChapScrnMap fromChapScrnMap;
    private JLabel fromChapLabel;
    private FromSecScrnMap fromSecScrnMap;
    private JLabel fromSecLabel;
    private ThruChapScrnMap thruChapScrnMap;
    private JLabel thruChapLabel;
    private ThruSecScrnMap thruSecScrnMap;
    private JLabel thruSecLabel;
    private final OutputSortScrnMap outputSortScrnMap;
    private final JLabel outputSortLabel;
}
