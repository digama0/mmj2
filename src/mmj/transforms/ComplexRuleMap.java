package mmj.transforms;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.ParseNode;
import mmj.lang.Stmt;

/**
 * This class implements complex rule map
 * 
 * @param <Data> stored data
 */
public abstract class ComplexRuleMap<Data> {
    protected final Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Data>>> map;

    public ComplexRuleMap() {
        map = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Data>>>();
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
            substMap = new HashMap<ConstSubst, Map<PropertyTemplate, Data>>();
            map.put(stmt, substMap);
        }

        Map<PropertyTemplate, Data> propertyMap = substMap.get(constSubst);
        if (propertyMap == null) {
            propertyMap = new HashMap<PropertyTemplate, Data>();
            substMap.put(constSubst, propertyMap);
        }

        final Data oldData = propertyMap.get(template);
        if (oldData != null)
            return oldData; // some duplicate

        propertyMap.put(template, data);
        return data;
    }

    public GeneralizedStmt detectGenStmt(final ParseNode node,
        final WorksheetInfo info)
    {
        final Stmt stmt = node.getStmt();
        final Map<ConstSubst, Map<PropertyTemplate, Data>> constSubstMap = map
            .get(stmt);

        if (constSubstMap == null)
            return null;

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

            // TODO: here we use trivial search stub!
            for (final Entry<PropertyTemplate, Data> propElem : propertyMap
                .entrySet())
            {
                final PropertyTemplate template = propElem.getKey();
                final GeneralizedStmt res = detectGenStmtCore(info, node,
                    template, constSubst, varIndexes);
                if (res != null)
                    return res;
            }
        }

        return null;
    }

    public abstract GeneralizedStmt detectGenStmtCore(final WorksheetInfo info,
        final ParseNode node, PropertyTemplate template, ConstSubst constSubst,
        int[] varIndexes);
}
