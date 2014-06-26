package mmj.transforms;

import java.util.*;

import mmj.lang.*;

public class ClosureInfo extends DBInfo {
    /**
     * The list of closure lows: A e. CC & B e. CC => (A + B) e. CC
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> set of possible properties ( _ e. CC in
     * the example). There could be many properties ( {" _ e. CC" , "_ e. RR" }
     * for example ).
     */
    private final Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>> closureRuleMap;

    public ClosureInfo(final List<Assrt> assrtList, final TrOutput output,
        final boolean dbg)
    {
        super(output, dbg);

        closureRuleMap = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>>();
        for (final Assrt assrt : assrtList)
            findClosureRules(assrt);
    }

    private static final ParseNode endMarker = new ParseNode();

    private static ParseNode getCorrespondingNodeRec(final ParseNode template,
        final ParseNode input)
    {
        if (template == PropertyTemplate.templateReplace)
            return input;
        if (template.getStmt() != input.getStmt())
            return null;

        ParseNode retNode = endMarker;

        for (int i = 0; i < input.getChild().length; i++) {
            final ParseNode res = getCorrespondingNodeRec(
                template.getChild()[i], input.getChild()[i]);
            if (res == null)
                return null;
            if (res != endMarker)
                retNode = res;
        }

        return retNode;
    }

    private static ParseNode getCorrespondingNode(final ParseNode template,
        final ParseNode input)
    {
        final ParseNode res = getCorrespondingNodeRec(template, input);
        if (res == endMarker)
            return null;
        return res;
    }

    /**
     * Replaces the variable var for null in the template
     * 
     * @param template the future template
     * @param var the variable which should be replaced for null
     * @return the number of replace operations
     */
    private static int prepareTemplate(final ParseNode template,
        final VarHyp var)
    {
        final ParseNode[] children = template.getChild();
        int res = 0;
        for (int i = 0; i < children.length; i++)
            if (children[i].getStmt() == var) {
                children[i] = PropertyTemplate.templateReplace; // indicate
                                                                // entry point
                res++;
            }
            else
                res += prepareTemplate(children[i], var);
        return res;
    }

    public static ParseNode createTemplateNodeFromHyp(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length == 0)
            return null;

        if (logHyps.length != varHypArray.length)
            return null;

        final VarHyp[] vars0 = logHyps[0].getMandVarHypArray();
        if (vars0.length != 1)
            return null;

        final VarHyp[] hypToVarHypMap = new VarHyp[logHyps.length];
        hypToVarHypMap[0] = vars0[0];

        // do not consider rules like |- ph & |- ps => |- ( ph <-> ps )
        if (logHyps[0].getExprParseTree().getRoot().getStmt() == vars0[0])
            return null;

        // Here we need deep clone because next we will modify result
        final ParseNode template = logHyps[0].getExprParseTree().getRoot()
            .deepClone();
        final int varNumEntrance = prepareTemplate(template, vars0[0]);
        if (varNumEntrance != 1)
            return null;

        return template;
    }
    /**
     * Filters transitive properties to result rules:
     * <p>
     * A e. CC & B e. CC => (A + B) e. CC
     * <p>
     * We filter assertions with next properties:
     * <ul>
     * <li>Hypothesis have the form P(x), P(y), P(z)
     * <li>The assertion has the form P(f(x, y, z, a, b, c))
     * <li>Function f have unique entrance for variables
     * <li>Other f's children a, b, c should be constants
     * </ul>
     * 
     * @param assrt the candidate
     */
    private void findClosureRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length == 0)
            return;

        if (logHyps.length != varHypArray.length)
            return;

        final VarHyp[] vars0 = logHyps[0].getMandVarHypArray();
        if (vars0.length != 1)
            return;

        final VarHyp[] hypToVarHypMap = new VarHyp[logHyps.length];
        hypToVarHypMap[0] = vars0[0];

        // Here we need deep clone because next we will modify result
        final ParseNode template = createTemplateNodeFromHyp(assrt);
        if (template == null)
            return;

        for (int i = 1; i < logHyps.length; i++) {
            final VarHyp[] varsi = logHyps[i].getMandVarHypArray();
            if (varsi.length != 1)
                return;
            final VarHyp vari = varsi[0];

            hypToVarHypMap[i] = vari;
            final ParseNode res = getCorrespondingNode(template, logHyps[i]
                .getExprParseTree().getRoot());
            if (res == null)
                return;
            if (res.getStmt() != vari)
                return;
        }

        final ParseNode root = assrtTree.getRoot();

        final ParseNode res = getCorrespondingNode(template, root);
        if (res == null)
            return;

        final Stmt stmt = res.getStmt();

        final ParseNode[] children = res.getChild();

        final int varToHypMap[] = new int[logHyps.length];
        for (int i = 0; i < varToHypMap.length; i++)
            varToHypMap[i] = -1;

        final ParseNode[] constMap = new ParseNode[children.length];
        int varNum = 0;
        for (int i = 0; i < children.length; i++) {
            final ParseNode child = children[i];
            if (TrUtil.isVarNode(child)) {
                if (varNum >= varToHypMap.length)
                    return;

                int resNum = -1;
                for (int k = 0; k < hypToVarHypMap.length; k++)
                    if (hypToVarHypMap[k] == child.getStmt()) {
                        resNum = k;
                        break;
                    }
                if (resNum == -1)
                    return;

                if (varToHypMap[varNum] != -1)
                    return;

                varToHypMap[varNum] = resNum;
                varNum++;
            }
            else if (TrUtil.isConstNode(child))
                // may we could use fast clone but it is not very important in
                // the loading phase
                constMap[i] = child.deepClone();
            else
                return;
        }

        boolean incorrectOrder = false;

        final int[] hypToVarMap = new int[logHyps.length];
        for (int i = 0; i < varToHypMap.length; i++) {
            if (varToHypMap[i] == -1)
                return;
            hypToVarMap[varToHypMap[i]] = i;
            if (varToHypMap[i] != i)
                incorrectOrder = true;
        }

        // Theoretically we could process incorrect hypothesis order in
        // theorems.
        // But set.mm has no such theorems so lets implement simple case.
        if (incorrectOrder)
            return;

        String hypString = "";
        for (int i = 0; i < logHyps.length; i++) {
            if (i != 0)
                hypString += " & ";
            hypString += logHyps[i].getFormula().toString();
        }

        Map<ConstSubst, Map<PropertyTemplate, Assrt>> assrtMap = closureRuleMap
            .get(stmt);
        if (assrtMap == null) {
            assrtMap = new HashMap<ConstSubst, Map<PropertyTemplate, Assrt>>();
            closureRuleMap.put(stmt, assrtMap);
        }

        final ConstSubst constSubst = new ConstSubst(constMap);

        Map<PropertyTemplate, Assrt> templateSet = assrtMap.get(constSubst);
        if (templateSet == null) {
            templateSet = new HashMap<PropertyTemplate, Assrt>();
            assrtMap.put(constSubst, templateSet);
        }

        final PropertyTemplate tn = new PropertyTemplate(template);

        if (templateSet.containsKey(tn))
            return; // some duplicate

        templateSet.put(tn, assrt);

        output.dbgMessage(dbg,
            "I-DBG transitive to result properties(%b): %s: %s => %s",
            incorrectOrder, assrt, hypString, assrt.getFormula());
    }

    public Assrt getClosureAssert(final GeneralizedStmt genStmt) {
        return getClosureAssert(genStmt.stmt, genStmt.constSubst,
            genStmt.template);
    }

    public Assrt getClosureAssert(final Stmt stmt, final ConstSubst constSubst,
        final PropertyTemplate template)
    {
        final Map<ConstSubst, Map<PropertyTemplate, Assrt>> substMap = closureRuleMap
            .get(stmt);
        if (substMap == null)
            return null;

        final Map<PropertyTemplate, Assrt> propertyMap = substMap
            .get(constSubst);

        if (propertyMap == null)
            return null;

        final Assrt assrt = propertyMap.get(template);
        return assrt;
    }
}
