package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.verify.VerifyProofs;

public class DataBaseInfo {

    /** This field is true if this object was initialized */
    private boolean isInit = false;

    private final boolean dbg = true;

    protected TrOutput output;

    /** It is necessary for formula construction */
    VerifyProofs verifyProofs;

    protected EquivalenceInfo eqInfo = new EquivalenceInfo();

    /**
     * The map from type to equivalence implication rule: A & A <-> B => B.
     * set.mm has only one element: (wff, <->)
     */
    private Map<Cnst, Assrt> eqImplications;

    /** The list of implication operators : A & A -> B => B. */
    protected Map<Stmt, Assrt> implOp;

    /**
     * The list of closure lows: A e. CC & B e. CC => (A + B) e. CC
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> set of possible properties ( _ e. CC in
     * the example). There could be many properties ( {" _ e. CC" , "_ e. RR" }
     * for example ).
     */
    protected Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>> closureRuleMap;

    /** The list of statements with possible variable replace */
    protected Map<Stmt, Assrt[]> replaceOp;

    /** The list of commutative operators */
    protected Map<Stmt, Assrt> comOp;

    /**
     * The map: associative operators -> array of 2 elements:
     * <ul>
     * <li>f(f(a, b), c) = f(a, f(b, c))
     * <li>f(a, f(b, c)) = f(f(a, b), c)
     * </ul>
     */
    protected Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>> assocOp;

    /** The symbol like |- in set.mm */
    protected Cnst provableLogicStmtTyp;

    protected class ConstSubst {
        /** This array contains null elements. */
        public final ParseNode[] constMap;
        public final int hash;

        public ConstSubst(final ParseNode[] constMap) {
            this.constMap = constMap;
            hash = calcHashCode();
        }

        public boolean isEmpty() {
            for (final ParseNode node : constMap)
                if (node != null)
                    return false;
            return true;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof ConstSubst))
                return false;
            final ConstSubst that = (ConstSubst)obj;

            assert constMap.length == that.constMap.length;
            for (int i = 0; i < constMap.length; i++)
                if (constMap[i] != that.constMap[i]) {
                    if (constMap[i] == null || that.constMap[i] == null)
                        return false;
                    if (!constMap[i].isDeepDup(that.constMap[i]))
                        return false;
                }
            return true;
        }

        protected int calcHashCode() {
            int hash = 0;
            for (final ParseNode node : constMap)
                if (node != null)
                    hash ^= node.deepHashCode();
            return hash;
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }

    /** The place in the template which could be replaced */
    protected static ParseNode templateReplace = new ParseNode();

    /** The template for some property. Usually it has form "var e. set" */
    protected class PropertyTemplate {
        /** template could be null */
        protected final ParseNode template;

        public PropertyTemplate(final ParseNode template) {
            this.template = template;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof PropertyTemplate))
                return false;
            final PropertyTemplate that = (PropertyTemplate)obj;
            if (template == that.template)
                return true;

            if (template == null || that.template == null)
                return false;

            return template.isDeepDup(that.template);
        }

        @Override
        public int hashCode() {
            if (template != null)
                return template.deepHashCode();
            else
                return 0;
        }

        public boolean isEmpty() {
            return template == null;
        }

        public ParseNode subst(final ParseNode substNode) {
            return template.deepCloneWNodeSub(templateReplace,
                substNode.deepClone());
        }
    }

    /** Empty default constructor */
    public DataBaseInfo() {}

    // ----------------------------

    public void prepareAutomaticTransformations(final List<Assrt> assrtList,
        final Cnst provableLogicStmtTyp, final Messages messages,
        final VerifyProofs verifyProofs)
    {
        isInit = true;
        output = new TrOutput(messages);
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        eqInfo.initMe(assrtList, output, dbg);

        implOp = new HashMap<Stmt, Assrt>();
        eqImplications = new HashMap<Cnst, Assrt>();
        for (final Assrt assrt : assrtList)
            findImplicationRules(assrt);

        replaceOp = new HashMap<Stmt, Assrt[]>();
        for (final Assrt assrt : assrtList)
            findReplaceRules(assrt);

        closureRuleMap = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>>();
        for (final Assrt assrt : assrtList)
            findClosureRules(assrt);

        comOp = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findCommutativeRules(assrt);

        assocOp = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt[]>>>();
        for (final Assrt assrt : assrtList)
            findAssociativeRules(assrt);
    }

    /**
     * Filters implication rules, like A & A -> B => B. Some of implications
     * could be equivalence operators (<-> in set.mm).
     * 
     * @param assrt the candidate
     */
    protected void findImplicationRules(final Assrt assrt) {
        // TODO: adds the support of assrts like addcomi
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 2)
            return;

        if (assrtTree.getMaxDepth() != 1)
            return;

        final ParseNode resNode = assrtTree.getRoot();

        if (!TrUtil.isVarNode(resNode))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        final ParseNode log1Root = logHyps[1].getExprParseTree().getRoot();

        ParseNode preHyp;
        ParseNode implHyp;
        if (TrUtil.isVarNode(log0Root)) {
            preHyp = log0Root;
            implHyp = log1Root;
        }
        else if (TrUtil.isVarNode(log1Root)) {
            preHyp = log1Root;
            implHyp = log0Root;
        }
        else
            return;

        if (implHyp.getChild().length != 2)
            return;

        // TODO: the order could be different!

        if (implHyp.getChild()[0].getStmt() != preHyp.getStmt())
            return;

        if (implHyp.getChild()[1].getStmt() != resNode.getStmt())
            return;

        final Stmt stmt = implHyp.getStmt();

        if (implOp.containsKey(stmt))
            return;

        output.dbgMessage(dbg, "I-DBG implication assrt: %s: %s", assrt,
            assrt.getFormula());
        implOp.put(stmt, assrt);

        if (!eqInfo.isEquivalence(stmt))
            return;

        final Cnst type = resNode.getStmt().getTyp();

        if (eqImplications.containsKey(type))
            return;

        output.dbgMessage(dbg, "I-DBG implication equal assrt: %s: %s", type,
            assrt);

        eqImplications.put(type, assrt);
    }

    /**
     * Filters replace rules, like A = B => g(A) = g(B)
     * 
     * @param assrt the candidate
     */
    protected void findReplaceRules(final Assrt assrt) {
        assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 3)
            return;

        if (eqInfo.getEqCommutative(assrtTree.getRoot().getStmt()) == null)
            return;

        final LogHyp testHyp = logHyps[0];

        final ParseTree hypTree = testHyp.getExprParseTree();

        if (eqInfo.getEqCommutative(hypTree.getRoot().getStmt()) == null)
            return;

        final ParseNode[] hypSubTrees = hypTree.getRoot().getChild();

        assert hypSubTrees.length == 2 : "It should be the equivalence rule!";

        if (!TrUtil.isVarNode(hypSubTrees[0])
            || !TrUtil.isVarNode(hypSubTrees[1]))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        assert subTrees.length == 2 : "It should be the equivalence rule!";

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ParseNode[] leftChild = subTrees[0].getChild();
        final ParseNode[] rightChild = subTrees[1].getChild();

        // Fast compare, change if the depth of this assrt statement tree
        // could be more then 3
        int replPos = -1;
        replaceCheck: for (int i = 0; i < leftChild.length; i++)
            if (leftChild[i].getStmt() != rightChild[i].getStmt()) {
                // Another place for replace? It is strange!
                if (replPos != -1)
                    return;

                // We found the replace
                replPos = i;

                // Check that it is actually the swap of two variables
                for (int k = 0; k < 2; k++) {
                    final int m = (k + 1) % 2; // the other index
                    if (leftChild[i].getStmt() == hypSubTrees[k].getStmt()
                        && rightChild[i].getStmt() == hypSubTrees[m].getStmt())
                        continue replaceCheck;
                }

                return;
            }

        Assrt[] repl = replaceOp.get(stmt);

        if (repl == null) {
            repl = new Assrt[subTrees[0].getChild().length];
            replaceOp.put(stmt, repl);
        }

        // it is the first such assrt;
        if (repl[replPos] != null)
            return;

        repl[replPos] = assrt;

        output.dbgMessage(dbg, "I-DBG Replace assrts: %s: %s", assrt,
            assrt.getFormula());
    }

    protected boolean isFullReplaceStatement(final Stmt stmt) {
        final Assrt[] replAssrts = replaceOp.get(stmt);

        if (replAssrts == null)
            return false;

        for (final Assrt assrt : replAssrts)
            if (assrt == null)
                return false;

        return true;
    }

    /**
     * Filters commutative rules, like A + B = B + A
     * 
     * @param assrt the candidate
     */
    protected void findCommutativeRules(final Assrt assrt) {
        // TODO: adds the support of assrts like addcomi
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 0)
            return;

        if (varHypArray.length != 2)
            return;

        if (assrtTree.getMaxDepth() != 3)
            return;

        if (!eqInfo.isEquivalence(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it is the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        if (subTrees[0].getChild().length != 2)
            return;

        if (subTrees[0].getChild()[0].getStmt() != subTrees[1].getChild()[1]
            .getStmt())
            return;

        if (subTrees[0].getChild()[1].getStmt() != subTrees[1].getChild()[0]
            .getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        if (comOp.containsKey(stmt))
            return;

        output.dbgMessage(dbg, "I-DBG commutative assrts: %s: %s", assrt,
            assrt.getFormula());
        comOp.put(stmt, assrt);
    }

    // -----------------------------------------------------------
    // -----------------------------------------------------------
    // -----------------------------------------------------------

    protected static final ParseNode endMarker = new ParseNode();

    protected static ParseNode getCorrespondingNodeRec(
        final ParseNode template, final ParseNode input)
    {
        if (template == templateReplace)
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

    protected static ParseNode getCorrespondingNode(final ParseNode template,
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
    protected static int prepareTemplate(final ParseNode template,
        final VarHyp var)
    {
        final ParseNode[] children = template.getChild();
        int res = 0;
        for (int i = 0; i < children.length; i++)
            if (children[i].getStmt() == var) {
                children[i] = templateReplace; // indicate entry point
                res++;
            }
            else
                res += prepareTemplate(children[i], var);
        return res;
    }

    protected ParseNode createTemplateFromHyp(final Assrt assrt) {
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
    protected void findClosureRules(final Assrt assrt) {
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
        final ParseNode template = createTemplateFromHyp(assrt);
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

    protected boolean isTheSameConstMap(final ParseNode candidate,
        final ParseNode[] constMap)
    {
        if (candidate.getChild().length != constMap.length)
            return false;

        for (int i = 0; i < constMap.length; i++) {
            final ParseNode child = candidate.getChild()[i];
            if (TrUtil.isConstNode(child)) {
                if (constMap[i] == null)
                    return false;
                else if (!constMap[i].isDeepDup(child))
                    return false;
            }
            else if (constMap[i] != null)
                return false;

        }
        return true;
    }

    /**
     * Filters associative rules, like (A + B) + C = A + (B + C)
     * 
     * @param assrt the candidate
     */
    protected void findAssociativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        // if (assrt.getLabel().equals("addassi"))
        // assrt.toString();

        final LogHyp[] logHyps = assrt.getLogHypArray();

        ParseNode templNode = null;
        if (logHyps.length != 0) {
            templNode = createTemplateFromHyp(assrt);
            if (templNode == null)
                return;
        }

        if (varHypArray.length != 3)
            return;

        // if (assrtTree.getMaxDepth() != 4)
        // return;

        if (!eqInfo.isEquivalence(assrtTree.getRoot().getStmt()))
            return;

        final ParseNode[] subTrees = assrtTree.getRoot().getChild();

        // it must be the equivalence rule
        assert subTrees.length == 2;

        if (subTrees[0].getStmt() != subTrees[1].getStmt())
            return;

        final Stmt stmt = subTrees[0].getStmt();

        final ParseNode[] leftChildren = subTrees[0].getChild();
        final ParseNode[] rightChildren = subTrees[1].getChild();

        final ParseNode[] constMap = new ParseNode[leftChildren.length];
        final int[] varPlace = new int[leftChildren.length];
        for (int i = 0; i < varPlace.length; i++)
            varPlace[i] = -1;
        int curVarNum = 0;
        for (int i = 0; i < leftChildren.length; i++)
            if (TrUtil.isConstNode(leftChildren[i]))
                constMap[i] = leftChildren[i];
            else
                varPlace[curVarNum++] = i;

        if (!isTheSameConstMap(subTrees[1], constMap))
            return;

        // the statement contains more that 2 variables
        if (curVarNum != 2)
            return;

        final ConstSubst constSubst = new ConstSubst(constMap);

        if (templNode != null) {
            final Map<ConstSubst, Map<PropertyTemplate, Assrt>> substMap = closureRuleMap
                .get(stmt);

            if (substMap == null)
                return;

            final Map<PropertyTemplate, Assrt> templateSet = substMap
                .get(constSubst);
            if (templateSet == null)
                return;

            if (!templateSet.containsKey(new PropertyTemplate(templNode)))
                return;
        }

        // we need to find one of the 2 patterns:
        // 0) f(f(a, b), c) = f(a, f(b, c))
        // 1) f(a, f(b, c)) = f(f(a, b), c)
        for (int i = 0; i < 2; i++) {
            final int k = varPlace[i];
            final int n = varPlace[(i + 1) % 2];
            if (leftChildren[k].getStmt() != stmt)
                continue;
            if (!isTheSameConstMap(leftChildren[k], constMap))
                continue;

            if (rightChildren[n].getStmt() != stmt)
                continue;

            if (!isTheSameConstMap(rightChildren[n], constMap))
                continue;

            if (!TrUtil.isVarNode(leftChildren[n]))
                continue;

            if (leftChildren[n].getStmt() != rightChildren[n].getChild()[n]
                .getStmt())
                continue;

            if (leftChildren[k].getChild()[k].getStmt() != rightChildren[k]
                .getStmt())
                continue;

            if (leftChildren[k].getChild()[n].getStmt() != rightChildren[n]
                .getChild()[k].getStmt())
                continue;

            if (!isFullReplaceStatement(stmt)) {
                output.dbgMessage(dbg, "I-DBG found commutative assrts "
                    + "but it has problems with replace: %s: %s", assrt,
                    assrt.getFormula());
                return;
            }

            if (!constSubst.isEmpty())
                output.dbgMessage(dbg,
                    "I-DBG temporary associative assrts: %d. %s: %s", i, assrt,
                    assrt.getFormula());
            // return;

            Map<ConstSubst, Map<PropertyTemplate, Assrt[]>> constSubstMap = assocOp
                .get(stmt);

            if (constSubstMap == null) {
                constSubstMap = new HashMap<ConstSubst, Map<PropertyTemplate, Assrt[]>>();
                assocOp.put(stmt, constSubstMap);
            }

            Map<PropertyTemplate, Assrt[]> propertyMap = constSubstMap
                .get(constSubst);
            if (propertyMap == null) {
                propertyMap = new HashMap<PropertyTemplate, Assrt[]>();
                constSubstMap.put(constSubst, propertyMap);
            }

            final PropertyTemplate template = new PropertyTemplate(templNode);
            Assrt[] assoc = propertyMap.get(template);

            if (assoc == null) {
                assoc = new Assrt[2];
                propertyMap.put(template, assoc);
            }
            /*
            Assrt[] assoc = assocOp.get(stmt);

            if (assoc == null) {
                assoc = new Assrt[2];
                assocOp.put(stmt, assoc);
            }
            */

            if (assoc[i] != null)
                continue;

            output.dbgMessage(dbg, "I-DBG associative assrts: %d. %s: %s", i,
                assrt, assrt.getFormula());
            assoc[i] = assrt;
            return;
        }
    }

    // ------------Getters and setters--------------

    public boolean isInit() {
        return isInit;
    }

    public Assrt getEqImplication(final Cnst type) {
        return eqImplications.get(type);
    }
    // protected Map<Stmt, Assrt> eqTransitivies;

}
