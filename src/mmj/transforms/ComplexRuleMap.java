//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.ParseNode;
import mmj.lang.Stmt;

/**
 * This class implements complex rule map. It is maps from: Statement (some
 * operation, like (A F B), binary function), constant substitution (+ for
 * example), closure property ( e. CC ) -> data (usually, assert or list of
 * asserts).
 * <p>
 * TODO: improve the performance of constant substitution search
 *
 * @param <Data> stored data
 */
public abstract class ComplexRuleMap<Data> {
    protected final Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Data>>> map;

    public ComplexRuleMap() {
        map = new HashMap<>();
    }

    public Data getData(final Stmt stmt, final ConstSubst constSubst,
        final PropertyTemplate template)
    {
        final Map<ConstSubst, Map<PropertyTemplate, Data>> substMap = map
            .get(stmt);
        if (substMap == null)
            return null;

        final Map<PropertyTemplate, Data> propertyMap = substMap
            .get(constSubst);

        if (propertyMap == null)
            return null;

        final Data data = propertyMap.get(template);
        return data;
    }

    /**
     * Adds new data and returns it or returns the old version
     *
     * @param stmt the statement
     * @param constSubst the constant map
     * @param template the template
     * @param data the data
     * @return old version or the new
     */
    public Data addData(final Stmt stmt, final ConstSubst constSubst,
        final PropertyTemplate template, final Data data)
    {
        Map<ConstSubst, Map<PropertyTemplate, Data>> substMap = map.get(stmt);
        if (substMap == null) {
            substMap = new HashMap<>();
            map.put(stmt, substMap);
        }

        Map<PropertyTemplate, Data> propertyMap = substMap.get(constSubst);
        if (propertyMap == null) {
            propertyMap = new HashMap<>();
            substMap.put(constSubst, propertyMap);
        }

        final Data oldData = propertyMap.get(template);
        if (oldData != null)
            return oldData; // some duplicate

        propertyMap.put(template, data);
        return data;
    }

    public interface ComplexRuleVisitor<Data, ResType> {
        /**
         * The core callback
         *
         * @param node the input node
         * @param info the work sheet info
         * @param constSubst appropriate constant substitution
         * @param varIndexes the indexes in the node children with variables
         * @param propertyMap The map: template -> data
         * @return If this function returns non-failure value the the visiting
         *         process will be terminated and this result will be returned.
         */
        ResType visit(final ParseNode node, final WorksheetInfo info,
            final ConstSubst constSubst, final int[] varIndexes,
            final Map<PropertyTemplate, Data> propertyMap);

        /**
         * This value indicates that we should continue search. Also the value
         * will be returned if nothing will be founded.
         *
         * @return The constant for failure-value indication
         */
        ResType failValue();
    }

    /**
     * Visits all appropriate constant substitutions for the node. Returns after
     * the first non-failure visitor result value.
     *
     * @param node the input node
     * @param info the work sheet info
     * @param visitor the callback for core work
     * @param <ResType> the type of visitor result
     * @return the visitor result
     */
    public final <ResType> ResType visitGenStmts(final ParseNode node,
        final WorksheetInfo info,
        final ComplexRuleVisitor<Data, ResType> visitor)
    {
        final Stmt stmt = node.stmt;
        final Map<ConstSubst, Map<PropertyTemplate, Data>> constSubstMap = map
            .get(stmt);

        final ResType failValue = visitor.failValue();

        if (constSubstMap == null)
            return failValue;

        final ParseNode[] constMap = TrUtil.collectConstSubst(node);

        // TODO: here we use trivial search stub!
        for (final Entry<ConstSubst, Map<PropertyTemplate, Data>> elem : constSubstMap
            .entrySet())
        {
            final ConstSubst constSubst = elem.getKey();

            final int[] varIndexes = TrUtil.checkConstSubstAndGetVarPositions(
                constSubst, constMap);

            if (varIndexes == null)
                continue;

            final Map<PropertyTemplate, Data> propertyMap = elem.getValue();

            final ResType res = visitor.visit(node, info, constSubst,
                varIndexes, propertyMap);

            if (res != failValue && !res.equals(failValue))
                return res;
        }

        return failValue;
    }
}
