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

import java.util.*;

import mmj.lang.*;
import mmj.pa.PaConstants;

public class SearchUnifier {

    private final Deque<ParseNode> unifyNodeStack;
    private final Deque<ParseNode> compareNodeStack;
    private int varHypSubstArrayCnt;
    private final VarHypSubst[] varHypSubstArray;

    public SearchUnifier() {
        unifyNodeStack = new ArrayDeque<>();
        compareNodeStack = new ArrayDeque<>();
        varHypSubstArrayCnt = -1;
        varHypSubstArray = new VarHypSubst[PaConstants.UNIFIER_MAX_VAR_HYPS];
        varHypSubstArrayCnt = -1;
        for (int i = 0; i < varHypSubstArray.length; i++)
            varHypSubstArray[i] = new VarHypSubst(null, null);

    }

    public boolean unifyExpr(final int numHyps, final boolean excludeVarHyps,
        final int searchOperChoice, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        switch (searchOperChoice) {
            case SearchOptionsConstants.OPER_VALUE_LE_ID:
                return unifyExprLE(excludeVarHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_LT_ID:
                return unifyExprLT(numHyps, excludeVarHyps, parseTree,
                    parseTree1);

            case SearchOptionsConstants.OPER_VALUE_EQ_ID:
                return unifyExprEQ(numHyps, excludeVarHyps, parseTree,
                    parseTree1);

            case SearchOptionsConstants.OPER_VALUE_EQ_EQ_ID:
                return unifyExprEQEQ(numHyps, excludeVarHyps, parseTree,
                    parseTree1);

            case SearchOptionsConstants.OPER_VALUE_GE_ID:
                return unifyExprGE(excludeVarHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_GT_ID:
                return unifyExprGT(numHyps, excludeVarHyps, parseTree,
                    parseTree1);

            case SearchOptionsConstants.OPER_VALUE_LT_GT_ID:
                return unifyExprLTGT(numHyps, excludeVarHyps, parseTree,
                    parseTree1);
        }
        throw new IllegalArgumentException(
            SearchConstants.ERROR_SEARCH_UNIFIER_OPER_CHOICE_2
                + searchOperChoice);
    }

    public boolean unifyStmt(final int numHyps, final int searchOperChoice,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        switch (searchOperChoice) {
            case SearchOptionsConstants.OPER_VALUE_LE_ID:
                return unifyStmtLE(parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_LT_ID:
                return unifyStmtLT(numHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_EQ_ID:
                return unifyStmtEQ(numHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_EQ_EQ_ID:
                return unifyStmtEQEQ(numHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_GE_ID:
                return unifyStmtGE(numHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_GT_ID:
                return unifyStmtGT(numHyps, parseTree, parseTree1);

            case SearchOptionsConstants.OPER_VALUE_LT_GT_ID:
                return unifyStmtLTGT(numHyps, parseTree, parseTree1);
        }
        throw new IllegalArgumentException(
            SearchConstants.ERROR_SEARCH_UNIFIER_OPER_CHOICE_1
                + searchOperChoice);
    }

    public boolean unifyStmtLE(final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyStmtStandard(parseTree, parseTree1);
    }

    public boolean unifyStmtLT(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree, parseTree1))
            return !checkVarHypSubstEQ(numHyps);
        else
            return false;
    }

    public boolean unifyStmtEQ(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree, parseTree1))
            return checkVarHypSubstEQ(numHyps);
        else
            return false;
    }

    public boolean unifyStmtEQEQ(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree, parseTree1))
            return checkStmtEQEQ(numHyps, parseTree, parseTree1);
        else
            return false;
    }

    public boolean unifyStmtGE(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyStmtStandard(parseTree1, parseTree);
    }

    public boolean unifyStmtGT(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree1, parseTree))
            return !checkVarHypSubstEQ(numHyps);
        else
            return false;
    }

    public boolean unifyStmtLTGT(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyStmtLT(numHyps, parseTree, parseTree1)
            || unifyStmtGT(numHyps, parseTree, parseTree1);
    }

    public boolean unifyStmtStandard(final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        varHypSubstArrayCnt = -1;
        if (!checkLevelAndDepth(parseTree, parseTree1))
            return false;
        varHypSubstArrayCnt = parseTree.getRoot().unifyWithSubtree(
            parseTree1.getRoot(), unifyNodeStack, compareNodeStack,
            varHypSubstArray);
        return varHypSubstArrayCnt >= 0;
    }

    public boolean checkLevelAndDepth(final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        final String s = parseTree.getLevelOneTwo();
        if (s.length() > 0) {
            if (!s.equals(parseTree1.getLevelOneTwo()))
                return false;
            final Stmt stmt = parseTree.getRoot().stmt;
            if (stmt != parseTree1.getRoot().stmt && !(stmt instanceof VarHyp))
                return false;
        }
        return parseTree.getMaxDepth() <= parseTree1.getMaxDepth()
            || parseTree1.getMaxDepth() <= 0;
    }

    private boolean checkStmtEQEQ(final int numHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (checkVarHypSubstEQ(numHyps))
            return parseTree.getRoot().isDeepDup(parseTree1.getRoot());
        else
            return false;
    }

    public boolean unifyExprLE(final boolean notVarHyp,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(notVarHyp); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return true;
        }

        return false;
    }

    public boolean unifyExprLT(final int numHyps, final boolean excludeVarHyps,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(excludeVarHyps); subtreeiterator
                .hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return !checkVarHypSubstEQ(numHyps);
        }

        return false;
    }

    public boolean unifyExprEQ(final int numHyps, final boolean flag,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return checkVarHypSubstEQ(numHyps);
        }

        return false;
    }

    public boolean unifyExprEQEQ(final int numHyps,
        final boolean excludeVarHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(excludeVarHyps); subtreeiterator
                .hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return checkExprEQEQ(numHyps, parseNode, parseTree1.getRoot());
        }

        return false;
    }

    public boolean unifyExprGE(final boolean excludeVarHyps,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(excludeVarHyps); subtreeiterator
                .hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseTree1.getRoot(), parseNode))
                return true;
        }

        return false;
    }

    public boolean unifyExprGT(final int numHyps, final boolean excludeVarHyps,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(excludeVarHyps); subtreeiterator
                .hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseTree1.getRoot(), parseNode))
                return !checkVarHypSubstEQ(numHyps);
        }

        return false;
    }

    public boolean unifyExprLTGT(final int numHyps,
        final boolean excludeVarHyps, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyExprLT(numHyps, excludeVarHyps, parseTree, parseTree1)
            || unifyExprGT(numHyps, excludeVarHyps, parseTree, parseTree1);
    }

    public boolean unifyExprStandard(final ParseNode parseNode,
        final ParseNode parseNode1)
    {
        varHypSubstArrayCnt = parseNode.unifyWithSubtree(parseNode1,
            unifyNodeStack, compareNodeStack, varHypSubstArray);
        return varHypSubstArrayCnt >= 0;
    }

    private boolean checkExprEQEQ(final int numHyps, final ParseNode parseNode,
        final ParseNode parseNode1)
    {
        if (checkVarHypSubstEQ(numHyps))
            return parseNode.isDeepDup(parseNode1);
        else
            return false;
    }

    private boolean checkVarHypSubstEQ(final int numHyps) {
        if (varHypSubstArrayCnt != numHyps)
            return false;
        final List<VarHyp> arraylist = new ArrayList<>(
            varHypSubstArrayCnt + 1);
        for (int j = 0; j < varHypSubstArrayCnt; j++) {
            if (!(varHypSubstArray[j].sourceNode.stmt instanceof VarHyp))
                return false;
            Assrt.accumHypInList(arraylist,
                (VarHyp)varHypSubstArray[j].sourceNode.stmt);
        }

        return arraylist.size() == numHyps;
    }
}
