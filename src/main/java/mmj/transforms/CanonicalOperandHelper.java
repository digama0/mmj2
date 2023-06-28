//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.ArrayList;
import java.util.List;

import mmj.lang.ParseNode;

public class CanonicalOperandHelper {
    private final ParseNode originalNode;
    private final GeneralizedStmt genStmt;
    public final List<ParseNode> operandList = new ArrayList<>();

    public CanonicalOperandHelper(final ParseNode originalNode,
        final GeneralizedStmt genStmt)
    {
        super();
        this.originalNode = originalNode;
        this.genStmt = genStmt;
    }

    private void collectOperandList(final ParseNode curNode,
        final AssocTree curStruct)
    {
        if (curStruct.size == 1) {
            operandList.add(curNode);
            return;
        }

        if (curNode.stmt != originalNode.stmt)
            assert curNode.stmt == originalNode.stmt;

        for (int i = 0; i < 2; i++) {
            final ParseNode child = curNode.child[genStmt.varIndexes[i]];
            collectOperandList(child, curStruct.subTrees[i]);
        }
    }

    public void collectOperandList(final AssocTree structure) {
        collectOperandList(originalNode, structure);
    }

    public void convertOperandsToCanonical(
        final TransformationManager trManager, final WorksheetInfo info)
    {
        for (int i = 0; i < operandList.size(); i++)
            operandList.set(i,
                trManager.getCanonicalForm(operandList.get(i), info));
    }

    public ParseNode constructCanonical() {
        if (operandList.isEmpty())
            operandList.toString();

        ParseNode res = operandList.get(0);

        for (int i = 1; i < operandList.size(); i++)
            res = TrUtil.createGenBinaryNode(genStmt, res, operandList.get(i));
        return res;
    }
}
