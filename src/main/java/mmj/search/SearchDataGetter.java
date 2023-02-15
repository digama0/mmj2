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

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;

public class SearchDataGetter {

    public SearchDataGetter() {
        assrt = null;
        assrtFormulaString = null;
        logHypFormulaString = null;
        assrtCommentString = null;
        assrtLabelString = null;
        logHypLabelString = null;
        labelRPNString = null;
        assrtFormulaTree = null;
        logHypFormulaTree = null;
        assrtFormulaVarHyp = null;
        logHypFormulaVarHyp = null;
    }

    public boolean assrtHasLogHyps() {
        final LogHyp[] alogHyp = assrt.getLogHypArray();
        return alogHyp.length > 0;
    }

    public void initForNextSearch(final Assrt assrt1) {
        assrt = assrt1;
        logHypFormulaString = null;
        assrtFormulaString = null;
        assrtCommentString = null;
        logHypLabelString = null;
        assrtLabelString = null;
        labelRPNString = null;
        assrtFormulaTree = null;
        logHypFormulaTree = null;
        assrtFormulaVarHyp = null;
        logHypFormulaVarHyp = null;
    }

    public String[] getFormulasAssrtDataStringArray(final boolean flag,
        final boolean flag1, final boolean flag2)
    {
        if (flag || flag2)
            if (flag1)
                return getComboFormulasStringArray();
            else
                return getConclFormulasStringArray();
        if (flag1)
            return getLogHypFormulasStringArray();
        else
            return new String[0];
    }

    private String[] getComboFormulasStringArray() {
        final int i = assrt.getLogHypArrayLength() + 1;
        final String[] as = new String[i];
        int j = 0;
        final String[] as1 = getLogHypFormulaString();
        for (final String element : as1)
            as[j++] = element;

        as[j++] = getAssrtFormulaString();
        return as;
    }

    private String[] getConclFormulasStringArray() {
        final String[] as = new String[1];
        as[0] = getAssrtFormulaString();
        return as;
    }

    private String[] getLogHypFormulasStringArray() {
        return getLogHypFormulaString();
    }

    private String[] getLogHypFormulaString() {
        if (logHypFormulaString == null) {
            final LogHyp[] alogHyp = assrt.getLogHypArray();
            logHypFormulaString = new String[alogHyp.length];
            for (int i = 0; i < alogHyp.length; i++)
                logHypFormulaString[i] = alogHyp[i].getFormula().toString();

        }
        return logHypFormulaString;
    }

    private String getAssrtFormulaString() {
        if (assrtFormulaString == null)
            assrtFormulaString = assrt.getFormula().toString() + " ";
        return assrtFormulaString;
    }

    public String[] getCommentsAssrtDataStringArray(final boolean flag,
        final boolean flag1, final boolean flag2)
    {
        final String[] as = new String[1];
        as[0] = getAssrtCommentString();
        return as;
    }

    private String getAssrtCommentString() {
        if (assrtCommentString == null)
            assrtCommentString = assrt.getDescriptionForSearch().toLowerCase();
        return assrtCommentString;
    }

    public String[] getLabelsAssrtDataStringArray(final boolean flag,
        final boolean flag1, final boolean flag2)
    {
        if (flag || flag2)
            if (flag1)
                return getComboLabelsStringArray();
            else
                return getConclLabelsStringArray();
        if (flag1)
            return getLogHypLabelsStringArray();
        else
            return new String[0];
    }

    private String[] getComboLabelsStringArray() {
        final int i = assrt.getLogHypArrayLength() + 1;
        final String[] as = new String[i];
        int j = 0;
        final String[] as1 = getLogHypLabelString();
        for (final String element : as1)
            as[j++] = element;

        as[j++] = getAssrtLabelString();
        return as;
    }

    private String[] getConclLabelsStringArray() {
        final String[] as = new String[1];
        as[0] = getAssrtLabelString();
        return as;
    }

    private String[] getLogHypLabelsStringArray() {
        return getLogHypLabelString();
    }

    private String[] getLogHypLabelString() {
        if (logHypLabelString == null) {
            final LogHyp[] alogHyp = assrt.getLogHypArray();
            logHypLabelString = new String[alogHyp.length];
            for (int i = 0; i < alogHyp.length; i++)
                logHypLabelString[i] = alogHyp[i].getLabel();

        }
        return logHypLabelString;
    }

    private String getAssrtLabelString() {
        if (assrtLabelString == null)
            assrtLabelString = assrt.getLabel();
        return assrtLabelString;
    }

    public String[] getLabelsRPNAssrtDataStringArray(final boolean flag,
        final boolean flag1, final boolean flag2)
    {
        if (flag2)
            return getLabelsRPNStringArray();
        else
            return new String[0];
    }

    private String[] getLabelsRPNStringArray() {
        final String[] as = new String[1];
        as[0] = getLabelRPNString();
        return as;
    }

    private String getLabelRPNString() {
        if (labelRPNString == null) {
            final RPNStep[] astmt = ((Theorem)assrt).getProof();
            final StringBuffer sb = new StringBuffer(5 * astmt.length);
            String s = "";
            for (final RPNStep element : astmt) {
                sb.append(s);
                sb.append(element);
                s = " ";
            }

            labelRPNString = sb.toString();
        }
        return labelRPNString;
    }

    public VarHyp[][] getFormulasAssrtDataVarHypArray(final boolean flag,
        final boolean flag1, final boolean flag2)
    {
        if (flag || flag2)
            if (flag1)
                return getComboFormulasVarHypArray();
            else
                return getConclFormulasVarHypArray();
        if (flag1)
            return getLogHypFormulasVarHypArray();
        else
            return new VarHyp[0][];
    }

    private VarHyp[][] getComboFormulasVarHypArray() {
        final int i = assrt.getLogHypArrayLength() + 1;
        final VarHyp[][] avarHyp = new VarHyp[i][];
        int j = 0;
        final VarHyp[][] avarHyp1 = getLogHypFormulaVarHyp();
        for (final VarHyp[] element : avarHyp1)
            avarHyp[j++] = element;

        avarHyp[j++] = getAssrtFormulaVarHyp();
        return avarHyp;
    }

    private VarHyp[][] getConclFormulasVarHypArray() {
        final VarHyp[][] avarHyp = new VarHyp[1][];
        avarHyp[0] = getAssrtFormulaVarHyp();
        return avarHyp;
    }

    private VarHyp[][] getLogHypFormulasVarHypArray() {
        return getLogHypFormulaVarHyp();
    }

    public VarHyp[] getAssrtFormulaVarHyp() {
        return assrt.getMandVarHypArray();
    }

    public VarHyp[][] getLogHypFormulaVarHyp() {
        final LogHyp[] alogHyp = assrt.getLogHypArray();
        final VarHyp[][] avarHyp = new VarHyp[alogHyp.length][];
        for (int i = 0; i < avarHyp.length; i++)
            avarHyp[i] = alogHyp[i].getMandVarHypArray();

        return avarHyp;
    }

    public ParseTree[] getFormulasAssrtDataTreeArray(final boolean flag,
        final boolean flag1, final boolean flag2)
    {
        if (flag || flag2)
            if (flag1)
                return getComboFormulasTreeArray();
            else
                return getConclFormulasTreeArray();
        if (flag1)
            return getLogHypFormulasTreeArray();
        else
            return new ParseTree[0];
    }

    private ParseTree[] getComboFormulasTreeArray() {
        final int i = assrt.getLogHypArrayLength() + 1;
        final ParseTree[] aparseTree = new ParseTree[i];
        int j = 0;
        final ParseTree[] aparseTree1 = getLogHypFormulaTree();
        for (final ParseTree element : aparseTree1)
            aparseTree[j++] = element;

        aparseTree[j++] = getAssrtFormulaTree();
        return aparseTree;
    }

    private ParseTree[] getConclFormulasTreeArray() {
        final ParseTree[] aparseTree = new ParseTree[1];
        aparseTree[0] = getAssrtFormulaTree();
        return aparseTree;
    }

    private ParseTree[] getLogHypFormulasTreeArray() {
        return getLogHypFormulaTree();
    }

    private ParseTree[] getLogHypFormulaTree() {
        if (logHypFormulaTree == null) {
            final LogHyp[] alogHyp = assrt.getLogHypArray();
            logHypFormulaTree = new ParseTree[alogHyp.length];
            for (int i = 0; i < alogHyp.length; i++)
                logHypFormulaTree[i] = alogHyp[i].getExprParseTree();

        }
        return logHypFormulaTree;
    }

    private ParseTree getAssrtFormulaTree() {
        if (assrtFormulaTree == null)
            assrtFormulaTree = assrt.getExprParseTree();
        return assrtFormulaTree;
    }

    Assrt assrt;
    String assrtFormulaString;
    String[] logHypFormulaString;
    String assrtCommentString;
    String assrtLabelString;
    String[] logHypLabelString;
    String labelRPNString;
    ParseTree assrtFormulaTree;
    ParseTree[] logHypFormulaTree;
    VarHyp[] assrtFormulaVarHyp;
    VarHyp[][] logHypFormulaVarHyp;
}
