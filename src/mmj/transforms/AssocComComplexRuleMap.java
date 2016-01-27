//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.Map;
import java.util.Map.Entry;

import mmj.lang.ParseNode;

public abstract class AssocComComplexRuleMap<Data> extends ComplexRuleMap<Data>
{
    public GeneralizedStmt detectGenStmt(final ParseNode node,
        final WorksheetInfo info)
    {
        final GeneralizedStmt visitGenStmts = visitGenStmts(node, info,
            new ComplexRuleVisitor<Data, GeneralizedStmt>() {
                public GeneralizedStmt visit(final ParseNode node,
                    final WorksheetInfo info, final ConstSubst constSubst,
                    final int[] varIndexes,
                    final Map<PropertyTemplate, Data> propertyMap)
                {
                    // TODO: here we use trivial search stub!
                    for (final Entry<PropertyTemplate, Data> propElem : propertyMap
                        .entrySet())
                    {
                        final PropertyTemplate template = propElem.getKey();
                        final GeneralizedStmt res = detectGenStmtCore(info,
                            node, template, constSubst, varIndexes);
                        if (res != null)
                            return res;
                    }
                    return null;
                }

                public GeneralizedStmt failValue() {
                    return null;
                }
            });
        return visitGenStmts;
    }

    protected abstract GeneralizedStmt detectGenStmtCore(
        final WorksheetInfo info, final ParseNode node,
        PropertyTemplate template, ConstSubst constSubst, int[] varIndexes);

}
