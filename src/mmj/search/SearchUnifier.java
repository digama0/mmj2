//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * {{file}}.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.ArrayList;
import java.util.List;

import mmj.lang.*;
import mmj.pa.PaConstants;

public class SearchUnifier {

    public SearchUnifier() {
        unifyNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];
        compareNodeStack = new ParseNode[PaConstants.UNIFIER_NODE_STACK_SIZE];
        varHypSubstArrayCnt = -1;
        varHypSubstArray = new VarHypSubst[PaConstants.UNIFIER_MAX_VAR_HYPS];
        varHypSubstArrayCnt = -1;
        for (int i = 0; i < varHypSubstArray.length; i++)
            varHypSubstArray[i] = new VarHypSubst(null, null);

    }

    public boolean unifyExpr(final int i, final boolean flag, final int j,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        switch (j) {
            case 2: // '\002'
                return unifyExprLE(flag, parseTree, parseTree1);

            case 3: // '\003'
                return unifyExprLT(i, flag, parseTree, parseTree1);

            case 4: // '\004'
                return unifyExprEQ(i, flag, parseTree, parseTree1);

            case 5: // '\005'
                return unifyExprEQEQ(i, flag, parseTree, parseTree1);

            case 6: // '\006'
                return unifyExprGE(flag, parseTree, parseTree1);

            case 7: // '\007'
                return unifyExprGT(i, flag, parseTree, parseTree1);

            case 8: // '\b'
                return unifyExprLTGT(i, flag, parseTree, parseTree1);
        }
        throw new IllegalArgumentException(
            SearchConstants.ERROR_SEARCH_UNIFIER_OPER_CHOICE_2 + j);
    }

    public boolean unifyStmt(final int i, final int j,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        switch (j) {
            case 2: // '\002'
                return unifyStmtLE(parseTree, parseTree1);

            case 3: // '\003'
                return unifyStmtLT(i, parseTree, parseTree1);

            case 4: // '\004'
                return unifyStmtEQ(i, parseTree, parseTree1);

            case 5: // '\005'
                return unifyStmtEQEQ(i, parseTree, parseTree1);

            case 6: // '\006'
                return unifyStmtGE(i, parseTree, parseTree1);

            case 7: // '\007'
                return unifyStmtGT(i, parseTree, parseTree1);

            case 8: // '\b'
                return unifyStmtLTGT(i, parseTree, parseTree1);
        }
        throw new IllegalArgumentException(
            SearchConstants.ERROR_SEARCH_UNIFIER_OPER_CHOICE_1 + j);
    }

    public boolean unifyStmtLE(final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyStmtStandard(parseTree, parseTree1);
    }

    public boolean unifyStmtLT(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree, parseTree1))
            return !checkVarHypSubstEQ(i);
        else
            return false;
    }

    public boolean unifyStmtEQ(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree, parseTree1))
            return checkVarHypSubstEQ(i);
        else
            return false;
    }

    public boolean unifyStmtEQEQ(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree, parseTree1))
            return checkStmtEQEQ(i, parseTree, parseTree1);
        else
            return false;
    }

    public boolean unifyStmtGE(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyStmtStandard(parseTree1, parseTree);
    }

    public boolean unifyStmtGT(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (unifyStmtStandard(parseTree1, parseTree))
            return !checkVarHypSubstEQ(i);
        else
            return false;
    }

    public boolean unifyStmtLTGT(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        return unifyStmtLT(i, parseTree, parseTree1)
            || unifyStmtGT(i, parseTree, parseTree1);
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
            final Stmt stmt = parseTree.getRoot().getStmt();
            if (stmt != parseTree1.getRoot().getStmt() && !stmt.isVarHyp())
                return false;
        }
        return parseTree.getMaxDepth() <= parseTree1.getMaxDepth()
            || parseTree1.getMaxDepth() <= 0;
    }

    private boolean checkStmtEQEQ(final int i, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        if (checkVarHypSubstEQ(i))
            return parseTree.getRoot().isDeepDup(parseTree1.getRoot());
        else
            return false;
    }

    public boolean unifyExprLE(final boolean flag, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return true;
        }

        return false;
    }

    public boolean unifyExprLT(final int i, final boolean flag,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return !checkVarHypSubstEQ(i);
        }

        return false;
    }

    public boolean unifyExprEQ(final int i, final boolean flag,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return checkVarHypSubstEQ(i);
        }

        return false;
    }

    public boolean unifyExprEQEQ(final int i, final boolean flag,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseNode, parseTree1.getRoot()))
                return checkExprEQEQ(i, parseNode, parseTree1.getRoot());
        }

        return false;
    }

    public boolean unifyExprGE(final boolean flag, final ParseTree parseTree,
        final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseTree1.getRoot(), parseNode))
                return true;
        }

        return false;
    }

    public boolean unifyExprGT(final int i, final boolean flag,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        for (final mmj.lang.ParseNode.SubTreeIterator subtreeiterator = parseTree
            .getRoot().subTreeIterator(flag); subtreeiterator.hasNext();)
        {
            final ParseNode parseNode = subtreeiterator.next();
            if (unifyExprStandard(parseTree1.getRoot(), parseNode))
                return !checkVarHypSubstEQ(i);
        }

        return false;
    }

    public boolean unifyExprLTGT(final int i, final boolean flag,
        final ParseTree parseTree, final ParseTree parseTree1)
    {
        return unifyExprLT(i, flag, parseTree, parseTree1)
            || unifyExprGT(i, flag, parseTree, parseTree1);
    }

    public boolean unifyExprStandard(final ParseNode parseNode,
        final ParseNode parseNode1)
    {
        varHypSubstArrayCnt = parseNode.unifyWithSubtree(parseNode1,
            unifyNodeStack, compareNodeStack, varHypSubstArray);
        return varHypSubstArrayCnt >= 0;
    }

    private boolean checkExprEQEQ(final int i, final ParseNode parseNode,
        final ParseNode parseNode1)
    {
        if (checkVarHypSubstEQ(i))
            return parseNode.isDeepDup(parseNode1);
        else
            return false;
    }

    private boolean checkVarHypSubstEQ(final int i) {
        if (varHypSubstArrayCnt != i)
            return false;
        final List<VarHyp> arraylist = new ArrayList<VarHyp>(
            varHypSubstArrayCnt + 1);
        for (int j = 0; j < varHypSubstArrayCnt; j++) {
            if (!varHypSubstArray[j].sourceNode.stmt.isVarHyp())
                return false;
            Assrt.accumHypInList(arraylist,
                (VarHyp)varHypSubstArray[j].sourceNode.stmt);
        }

        return arraylist.size() == i;
    }

    private final ParseNode[] unifyNodeStack;
    private final ParseNode[] compareNodeStack;
    private int varHypSubstArrayCnt;
    private final VarHypSubst[] varHypSubstArray;
}