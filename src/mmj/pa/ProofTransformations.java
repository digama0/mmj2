//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.pa;

import java.util.*;

import mmj.lang.*;
import mmj.verify.VerifyProofs;

/**
 * This contains information for possible automatic transformations.
 * <p>
 * Canonical form for the parse node is a parse node with sorted
 * commutative/associative transformations.
 * <p>
 * The functions in this class are separated into 4 parts:
 * <ul>
 * <li>Data structures initialization
 * <li>Transformations
 * <li>Canonical form comparison
 * <li>Auxiliary functions
 * </ul>
 */
public class ProofTransformations {
    /** This field is true if this object was initialized */
    private boolean isInit = false;

    Messages messages; // for debug reasons

    /** It is necessary for formula construction */
    VerifyProofs verifyProofs;

    /** The map from type to corresponding equivalence operators */
    private Map<Cnst, Stmt> eqMap;

    /** The list of commutative rules for equivalence operators: A = B => B = A */
    private Map<Stmt, Assrt> eqCommutatives;

    /**
     * The list of transitive rules for equivalence operators:
     * <p>
     * A = B & B = C => A = C
     */
    private Map<Stmt, Assrt> eqTransitivies;

    /**
     * The map from type to equivalence implication rule: A & A <-> B => B.
     * set.mm has only one element: (wff, <->)
     */
    private Map<Cnst, Assrt> eqImplications;

    /** The list of implication operators : A & A -> B => B. */
    private Map<Stmt, Assrt> implOp;

    private Map<Stmt, Map<ConstSubst, Set<PropertyTemplate>>> closureRuleMap;

    /** The list of statements with possible variable replace */
    private Map<Stmt, Assrt[]> replaceOp;

    /** The list of commutative operators */
    private Map<Stmt, Assrt> comOp;

    /**
     * The map: associative operators -> array of 2 elements:
     * <ul>
     * <li>f(f(a, b), c) = f(a, f(b, c))
     * <li>f(a, f(b, c)) = f(f(a, b), c)
     * </ul>
     */
    private Map<Stmt, Assrt[]> assocOp;

    /** The symbol like |- in set.mm */
    private Cnst provableLogicStmtTyp;

    private class ConstSubst {
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

        private int calcHashCode() {
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

    private class PropertyTemplate {
        private final ParseNode template;

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

            return template.isDeepDup(that.template);
        }

        @Override
        public int hashCode() {
            return template.deepHashCode();
        }
    }

    /** Generalized assertion */
    /*
    private class GenAssrt {
        public final Assrt assrt;
        public final ConstSubst constSubst;

        public GenAssrt(final Assrt assrt, final ConstSubst constMap) {
            this.assrt = assrt;
            constSubst = constMap;
        }

        @Override
        public boolean equals(final Object obj) {
            if (!(obj instanceof GenAssrt))
                return false;
            final GenAssrt that = (GenAssrt)obj;
            if (assrt != that.assrt)
                return false;

            if (!constSubst.equals(that.constSubst))
                return false;

            return true;
        }

        @Override
        public int hashCode() {
            return assrt.hashCode() ^ constSubst.hashCode();
        }
    }*/

    /** Empty default constructor */
    public ProofTransformations() {}

    // ----------------------------

    public void prepareAutomaticTransformations(final List<Assrt> assrtList,
        final Cnst provableLogicStmtTyp, final Messages messages,
        final VerifyProofs verifyProofs)
    {
        isInit = true;
        this.messages = messages;
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        eqCommutatives = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findEquivalenceCommutativeRules(assrt);

        eqTransitivies = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findEquivalenceTransitiveRules(assrt);

        filterOnlyEqRules();

        implOp = new HashMap<Stmt, Assrt>();
        eqImplications = new HashMap<Cnst, Assrt>();
        for (final Assrt assrt : assrtList)
            findImplicationRules(assrt);

        replaceOp = new HashMap<Stmt, Assrt[]>();
        for (final Assrt assrt : assrtList)
            findReplaceRules(assrt);

        closureRuleMap = new HashMap<Stmt, Map<ConstSubst, Set<PropertyTemplate>>>();
        for (final Assrt assrt : assrtList)
            findClosureRules(assrt);

        comOp = new HashMap<Stmt, Assrt>();
        for (final Assrt assrt : assrtList)
            findCommutativeRules(assrt);

        assocOp = new HashMap<Stmt, Assrt[]>();
        for (final Assrt assrt : assrtList)
            findAssociativeRules(assrt);
    }

    /**
     * Find commutative equivalence rules, like A = B => B = A
     * <p>
     * 
     * @param assrt the candidate
     */
    private void findEquivalenceCommutativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        final ParseTree hypTree = logHyps[0].getExprParseTree();

        if (varHypArray.length != 2)
            return;

        if (hypTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getRoot().getChild().length != 2)
            return;

        final Stmt stmt = assrtTree.getRoot().getStmt();

        if (hypTree.getRoot().getStmt() != stmt)
            return;

        if (hypTree.getRoot().getChild()[0].getStmt() != assrtTree.getRoot()
            .getChild()[1].getStmt())
            return;

        if (hypTree.getRoot().getChild()[1].getStmt() != assrtTree.getRoot()
            .getChild()[0].getStmt())
            return;

        messages.accumInfoMessage(
            "I-DBG Equivalence commutative assrt: %s: %s", assrt,
            assrt.getFormula());

        if (!eqCommutatives.containsKey(stmt))
            eqCommutatives.put(stmt, assrt);
    }

    /**
     * Find transitive equivalence rules, like A = B & B = C => A = C
     * <p>
     * 
     * @param assrt the candidate
     */
    private void findEquivalenceTransitiveRules(final Assrt assrt) {
        final VarHyp[] mandVarHypArray = assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 2)
            return;

        final ParseTree hypTree1 = logHyps[0].getExprParseTree();
        final ParseTree hypTree2 = logHyps[1].getExprParseTree();

        if (mandVarHypArray.length != 2)
            return;

        if (hypTree1.getMaxDepth() != 2 || hypTree2.getMaxDepth() != 2)
            return;

        if (assrtTree.getMaxDepth() != 2)
            return;

        if (assrtTree.getRoot().getChild().length != 2)
            return;

        final Stmt stmt = assrtTree.getRoot().getStmt();

        if (hypTree1.getRoot().getStmt() != stmt)
            return;

        if (hypTree2.getRoot().getStmt() != stmt)
            return;

        // check for 'A' in 'A = B & B = C => A = C'
        if (hypTree1.getRoot().getChild()[0].getStmt() != assrtTree.getRoot()
            .getChild()[0].getStmt())
            return;

        // check for 'B' in 'A = B & B = C'
        if (hypTree1.getRoot().getChild()[1].getStmt() != hypTree2.getRoot()
            .getChild()[0].getStmt())
            return;

        // check for 'C' in 'A = B & B = C => A = C'
        if (hypTree2.getRoot().getChild()[1].getStmt() != assrtTree.getRoot()
            .getChild()[1].getStmt())
            return;

        messages.accumInfoMessage("I-DBG Equivalence transitive assrt: %s: %s",
            assrt, assrt.getFormula());
        if (!eqTransitivies.containsKey(stmt))
            eqTransitivies.put(stmt, assrt);
    }

    /**
     * We found candidates for equivalence from commutative and transitive
     * sides. Now compare results and remove unsuitable!
     */
    private void filterOnlyEqRules() {
        while (true) {
            boolean changed = false;

            for (final Stmt eq : eqTransitivies.keySet())
                if (!eqCommutatives.containsKey(eq)) {
                    eqTransitivies.remove(eq);
                    changed = true;
                    break;
                }

            for (final Stmt eq : eqCommutatives.keySet())
                if (!eqTransitivies.containsKey(eq)) {
                    eqCommutatives.remove(eq);
                    changed = true;
                    break;
                }

            if (!changed)
                break;
        }

        // Debug output:
        for (final Stmt eq : eqTransitivies.keySet())
            messages.accumInfoMessage("I-DBG Equivalence rules: %s: %s and %s",
                eq, eqCommutatives.get(eq).getFormula(), eqTransitivies.get(eq)
                    .getFormula());

        // Create the reverse map:
        eqMap = new HashMap<Cnst, Stmt>();

        for (final Stmt eq : eqCommutatives.keySet()) {
            final Assrt assrt = eqCommutatives.get(eq);

            final ParseTree assrtTree = assrt.getExprParseTree();
            final Cnst type = assrtTree.getRoot().getChild()[0].getStmt()
                .getTyp();
            eqMap.put(type, eq);

            messages.accumInfoMessage("I-DBG Type equivalence map: %s: %s",
                type, eq);
        }
    }

    private boolean isEquivalence(final Stmt stmt) {
        return eqCommutatives.containsKey(stmt);
    }

    /**
     * Filters implication rules, like A & A -> B => B. Some of implications
     * could be equivalence operators (<-> in set.mm).
     * 
     * @param assrt the candidate
     */
    private void findImplicationRules(final Assrt assrt) {
        // TODO: adds the support of assrts like addcomi
        final ParseTree assrtTree = assrt.getExprParseTree();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length != 2)
            return;

        if (assrtTree.getMaxDepth() != 1)
            return;

        final ParseNode resNode = assrtTree.getRoot();

        if (!isVarNode(resNode))
            return;

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();
        final ParseNode log1Root = logHyps[1].getExprParseTree().getRoot();

        ParseNode preHyp;
        ParseNode implHyp;
        if (isVarNode(log0Root)) {
            preHyp = log0Root;
            implHyp = log1Root;
        }
        else if (isVarNode(log1Root)) {
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

        messages.accumInfoMessage("I-DBG implication assrt: %s: %s", assrt,
            assrt.getFormula());
        implOp.put(stmt, assrt);

        if (!isEquivalence(stmt))
            return;

        final Cnst type = resNode.getStmt().getTyp();

        if (eqImplications.containsKey(type))
            return;

        messages.accumInfoMessage("I-DBG implication equal assrt: %s: %s",
            type, assrt);

        eqImplications.put(type, assrt);
    }

    /**
     * Filters replace rules, like A = B => g(A) = g(B)
     * 
     * @param assrt the candidate
     */
    private void findReplaceRules(final Assrt assrt) {
        assrt.getMandVarHypArray();
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 1)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 3)
            return;

        if (eqCommutatives.get(assrtTree.getRoot().getStmt()) == null)
            return;

        final LogHyp testHyp = logHyps[0];

        final ParseTree hypTree = testHyp.getExprParseTree();

        if (eqCommutatives.get(hypTree.getRoot().getStmt()) == null)
            return;

        final ParseNode[] hypSubTrees = hypTree.getRoot().getChild();

        assert hypSubTrees.length == 2 : "It should be the equivalence rule!";

        if (!isVarNode(hypSubTrees[0]) || !isVarNode(hypSubTrees[1]))
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

        messages.accumInfoMessage("I-DBG Replace assrts: %s: %s", assrt,
            assrt.getFormula());
    }

    private boolean isFullReplaceStatement(final Stmt stmt) {
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
    private void findCommutativeRules(final Assrt assrt) {
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

        if (!isEquivalence(assrtTree.getRoot().getStmt()))
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

        messages.accumInfoMessage("I-DBG commutative assrts: %s: %s", assrt,
            assrt.getFormula());
        comOp.put(stmt, assrt);
    }

    // -----------------------------------------------------------
    // -----------------------------------------------------------
    // -----------------------------------------------------------

    private static final ParseNode endMarker = new ParseNode();

    private static ParseNode getCorrespondingNodeRec(final ParseNode template,
        final ParseNode input)
    {
        if (template == null)
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
                children[i] = null; // indicate entry point
                res++;
            }
            else
                res += prepareTemplate(children[i], var);
        return res;
    }

    private ParseNode createTemplateFromHyp(final Assrt assrt) {
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
            if (isVarNode(child)) {
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
            else if (isConstNode(child))
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

        Map<ConstSubst, Set<PropertyTemplate>> assrtMap = closureRuleMap
            .get(stmt);
        if (assrtMap == null) {
            assrtMap = new HashMap<ConstSubst, Set<PropertyTemplate>>();
            closureRuleMap.put(stmt, assrtMap);
        }

        final ConstSubst constSubst = new ConstSubst(constMap);

        Set<PropertyTemplate> templateSet = assrtMap.get(constSubst);
        if (templateSet == null) {
            templateSet = new HashSet<PropertyTemplate>();
            assrtMap.put(constSubst, templateSet);
        }

        final PropertyTemplate tn = new PropertyTemplate(template);

        if (templateSet.contains(tn))
            return; // some duplicate

        templateSet.add(tn);

        messages.accumInfoMessage(
            "I-DBG transitive to result properties(%b): %s: %s => %s",
            incorrectOrder, assrt, hypString, assrt.getFormula());
    }

    private boolean isTheSameConstMap(final ParseNode candidate,
        final ParseNode[] constMap)
    {
        if (candidate.getChild().length != constMap.length)
            return false;

        for (int i = 0; i < constMap.length; i++) {
            final ParseNode child = candidate.getChild()[i];
            if (isConstNode(child)) {
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
    private void findAssociativeRules(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (assrt.getLabel().equals("addassi"))
            assrt.toString();

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

        if (!isEquivalence(assrtTree.getRoot().getStmt()))
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
            if (isConstNode(leftChildren[i]))
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
            final Map<ConstSubst, Set<PropertyTemplate>> substMap = closureRuleMap
                .get(stmt);

            if (substMap == null)
                return;

            final Set<PropertyTemplate> templateSet = substMap.get(constSubst);
            if (templateSet == null)
                return;

            if (!templateSet.contains(new PropertyTemplate(templNode)))
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

            if (!isVarNode(leftChildren[n]))
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
                messages.accumInfoMessage("I-DBG found commutative assrts "
                    + "but it has problems with replace: %s: %s", assrt,
                    assrt.getFormula());
                return;
            }

            if (!constSubst.isEmpty()) {
                messages.accumInfoMessage(
                    "I-DBG temporary associative assrts: %d. %s: %s", i, assrt,
                    assrt.getFormula());
                return;
            }

            Assrt[] assoc = assocOp.get(stmt);

            if (assoc == null) {
                assoc = new Assrt[2];
                assocOp.put(stmt, assoc);
            }

            if (assoc[i] != null)
                continue;

            messages.accumInfoMessage("I-DBG associative assrts: %d. %s: %s",
                i, assrt, assrt.getFormula());
            assoc[i] = assrt;
            return;
        }
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    /**
     * The transformation of {@link Transformation#originalNode} to any equal
     * node.
     */
    private abstract class Transformation {
        /** The original node */
        public final ParseNode originalNode;

        public Transformation(final ParseNode originalNode) {
            this.originalNode = originalNode;
        }

        /**
         * @return the canonical node for {@link Transformation#originalNode}
         */
        abstract public ParseNode getCanonicalNode();

        /**
         * This function should construct derivation step sequence from this
         * {@link Transformation#originalNode} to target's
         * {@link Transformation#originalNode}
         * 
         * @param target the target transformation
         * @param proofWorksheet the proof work sheet
         * @param derivStep 'this' derivation step
         * @return the proof step which confirms that this
         *         {@link Transformation#originalNode} is equal to target
         *         {@link Transformation#originalNode}. Could returns null it
         *         this and target are equal.
         */
        public abstract ProofStepStmt transformMeToTarget(
            final Transformation target, final ProofWorksheet proofWorksheet,
            final DerivationStep derivStep);

        /**
         * This function checks maybe we should not do anything! We should
         * perform transformations if this function returns derivStep.
         * 
         * @param target the target transformation
         * @param proofWorksheet the proof work sheet
         * @param derivStep 'this' derivation step
         * @return null (if target equals to this), statement (if it is already
         *         exists) or derivStep if we should perform some
         *         transformations!
         */
        protected ProofStepStmt checkTransformationNecessary(
            final Transformation target, final ProofWorksheet proofWorksheet,
            final DerivationStep derivStep)
        {

            if (originalNode.isDeepDup(target.originalNode))
                return null; // nothing to transform!

            final Stmt equalStmt = eqMap.get(originalNode.getStmt().getTyp());

            // Create node g(A, B, C) = g(A', B', C')
            final ParseNode stepNode = createBinaryNode(equalStmt,
                originalNode, target.originalNode);

            final ProofStepStmt stepTr = getOrCreateProofStepStmt(
                proofWorksheet, derivStep, stepNode, null, null);

            if (stepTr != null)
                return stepTr;

            return derivStep; // there is more complex case!
        }

        // use it only for debug!
        @Override
        public String toString() {
            final Formula f = getFormula(originalNode);
            return f.toString();
        }
    }

    /** No transformation at all =) */
    private class IdentityTransformation extends Transformation {

        public IdentityTransformation(final ParseNode originalNode) {
            super(originalNode);
        }

        @Override
        public ParseNode getCanonicalNode() {
            return originalNode;
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
        {
            assert target.originalNode.isDeepDup(originalNode);
            return null; // nothing to do
        }
    }

    /**
     * The replace transformation: we could transform children of corresponding
     * node and replace them with its canonical form (or vice versa).
     */
    private class ReplaceTransformation extends Transformation {
        public ReplaceTransformation(final ParseNode originalNode) {
            super(originalNode);
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
        {
            assert target instanceof ReplaceTransformation;
            final ReplaceTransformation trgt = (ReplaceTransformation)target;

            final ProofStepStmt simpleRes = checkTransformationNecessary(
                target, proofWorksheet, derivStep);
            if (simpleRes != derivStep)
                return simpleRes;

            // result transformation statement
            ProofStepStmt resStmt = null;

            // the current transformation result
            ParseNode resNode = originalNode;

            final Assrt[] replAsserts = replaceOp.get(originalNode.getStmt());

            for (int i = 0; i < originalNode.getChild().length; i++) {
                if (replAsserts[i] == null)
                    continue;
                // We replaced previous children and now we should transform ith
                // child B

                final ParseNode child = originalNode.getChild()[i];
                final ParseNode targetChild = target.originalNode.getChild()[i];

                assert replAsserts[i].getLogHypArrayLength() == 1;

                // get the root symbol from g(A', B, C) = g(A', B', C)
                // in set.mm it will be = or <->
                final Stmt equalStmt = replAsserts[i].getExprParseTree()
                    .getRoot().getStmt();

                // the symbol should be equivalence operator
                assert isEquivalence(equalStmt);

                // transform ith child
                // the result should be some B = B' statement
                final ProofStepStmt childTrStmt = createTransformation(child)
                    .transformMeToTarget(createTransformation(targetChild),
                        proofWorksheet, derivStep);
                if (childTrStmt == null)
                    continue; // noting to do

                // get the node B = B'
                final ParseNode childTrRoot = childTrStmt.formulaParseTree
                    .getRoot();

                // transformation should be transformation:
                // check result childTrStmt statement
                assert isEquivalence(childTrRoot.getStmt());
                assert childTrRoot.getChild().length == 2;
                assert childTrRoot.getChild()[0].isDeepDup(originalNode
                    .getChild()[i]);
                assert childTrRoot.getChild()[1].isDeepDup(trgt.originalNode
                    .getChild()[i]);

                // Create statement d:childTrStmt:replAssert
                // |- g(A', B, C) = g(A', B', C)
                final ProofStepStmt stepTr = createReplaceStep(proofWorksheet,
                    derivStep, resNode, i, trgt.originalNode.getChild()[i],
                    childTrStmt);
                resNode = stepTr.formulaParseTree.getRoot().getChild()[1];

                // resStmt now have the form g(A, B, C) = g(A', B, C)
                // So create transitive:
                // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
                // => g(A, B, C) = g(A', B', C)
                // Create statement d:resStmt,stepTr:transitive
                // |- g(A, B, C) = g(A', B', C)
                resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                    resStmt, stepTr);
            }

            assert resStmt != null;

            assert resStmt.formulaParseTree.getRoot().getChild()[0]
                .isDeepDup(originalNode);
            assert resStmt.formulaParseTree.getRoot().getChild()[1]
                .isDeepDup(target.originalNode);

            return resStmt;
        }

        @Override
        public ParseNode getCanonicalNode() {
            final Assrt[] replAsserts = replaceOp.get(originalNode.getStmt());
            final ParseNode resNode = originalNode.cloneWithoutChildren();

            for (int i = 0; i < resNode.getChild().length; i++) {
                if (replAsserts[i] == null)
                    continue;
                resNode.getChild()[i] = getCanonicalForm(originalNode
                    .getChild()[i]);
            }

            return resNode;
        }
    }

    private static class AssocTree {
        final int size;
        final AssocTree[] subTrees;

        AssocTree() {
            size = 1;
            subTrees = null;
        }

        AssocTree(final AssocTree left, final AssocTree right) {
            subTrees = new AssocTree[]{left, right};
            size = left.size + right.size;
        }

        AssocTree(final int from, final AssocTree left, final AssocTree right) {
            assert from == 0 || from == 1;
            subTrees = from == 0 ? new AssocTree[]{left, right}
                : new AssocTree[]{right, left};
            size = left.size + right.size;
        }

        AssocTree duplicate() {
            if (subTrees != null)
                return new AssocTree(subTrees[0].duplicate(),
                    subTrees[1].duplicate());
            else
                return new AssocTree();
        }

        @Override
        public String toString() {
            String res = "";
            res += size;

            if (subTrees != null)
                res += "[" + subTrees[0].toString() + ","
                    + subTrees[1].toString() + "]";

            return res;
        }
    }

    private static AssocTree createAssocTree(final ParseNode originalNode,
        final Stmt assocStmt)
    {
        final Stmt stmt = originalNode.getStmt();
        final ParseNode[] childrent = originalNode.getChild();
        if (stmt != assocStmt)
            return new AssocTree();
        return new AssocTree(createAssocTree(childrent[0], assocStmt),
            createAssocTree(childrent[1], assocStmt));
    }

    /** Only associative transformations */
    private class AssociativeTransformation extends Transformation {
        final AssocTree structure;

        public AssociativeTransformation(final ParseNode originalNode,
            final AssocTree structure)
        {
            super(originalNode);
            this.structure = structure;
        }

        @Override
        public ProofStepStmt transformMeToTarget(final Transformation target,
            final ProofWorksheet proofWorksheet, final DerivationStep derivStep)
        {
            assert target instanceof AssociativeTransformation;
            final AssociativeTransformation trgt = (AssociativeTransformation)target;

            assert trgt.structure.size == structure.size;

            final ProofStepStmt simpleRes = checkTransformationNecessary(
                target, proofWorksheet, derivStep);
            if (simpleRes != derivStep)
                return simpleRes;

            final Stmt stmt = originalNode.getStmt();

            int from;
            if (structure.subTrees[0].size > trgt.structure.subTrees[0].size)
                from = 0;
            else
                from = 1;
            final int to = (from + 1) % 2;

            final int toSize = trgt.structure.subTrees[to].size;

            AssocTree gAssT = structure.duplicate(); // g node
            ParseNode gNode = originalNode;

            // result transformation statement
            ProofStepStmt resStmt = null;

            while (true) {

                // move subtrees to the other side
                while (true) {
                    // @formatter:off
                    //         |g                  |                 +
                    //        / \                 / \                +
                    //       /   \               /   \               +
                    //     e|     |f           a|     |              +
                    //     / \          ==>          / \             +
                    //    /   \                     /   \            +
                    //  a|     |d                 d|     |f          +
                    // @formatter:on

                    final AssocTree eAssT = gAssT.subTrees[from];
                    final AssocTree fAssT = gAssT.subTrees[to];

                    if (fAssT.size >= toSize)
                        break;

                    final AssocTree aAssT = eAssT.subTrees[from];
                    final AssocTree dAssT = eAssT.subTrees[to];

                    if (dAssT.size + fAssT.size > toSize)
                        break;

                    gAssT = new AssocTree(from, aAssT, new AssocTree(from,
                        dAssT, fAssT));

                    final ParseNode eNode = gNode.getChild()[from];
                    final ParseNode fNode = gNode.getChild()[to];
                    final ParseNode aNode = eNode.getChild()[from];
                    final ParseNode dNode = eNode.getChild()[to];

                    final ParseNode prevNode = gNode;

                    gNode = createAssocBinaryNode(from, stmt, aNode,
                        createAssocBinaryNode(from, stmt, dNode, fNode));

                    // transform to normal direction => 'from'
                    final ProofStepStmt assocTr = createAssociativeStep(
                        proofWorksheet, derivStep, from, prevNode, gNode);

                    resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                        resStmt, assocTr);
                }

                final AssocTree eAssT = gAssT.subTrees[from];
                final AssocTree fAssT = gAssT.subTrees[to];

                if (fAssT.size != toSize) {
                    final AssocTree aAssT = eAssT.subTrees[from];
                    final AssocTree dAssT = eAssT.subTrees[to];
                    final AssocTree bAssT = dAssT.subTrees[from];
                    final AssocTree cAssT = dAssT.subTrees[to];

                    // reconstruct 'from' part
                    // @formatter:off
                    //         |g                  |g            +
                    //        / \                 / \            +
                    //       /   \               /   \           +
                    //     e|     |f          e'|     |f         +
                    //     / \          ==>    / \               +
                    //    /   \               /   \              +
                    //  a|     |d            |     |c            +
                    //        / \           / \                  +
                    //       /   \         /   \                 +
                    //     b|     |c     a|     |b               +
                    // @formatter:on

                    gAssT.subTrees[from] = new AssocTree(from, new AssocTree(
                        from, aAssT, bAssT), cAssT);

                    /*--*/ParseNode eNode = gNode.getChild()[from];
                    final ParseNode fNode = gNode.getChild()[to];
                    final ParseNode aNode = eNode.getChild()[from];
                    final ParseNode dNode = eNode.getChild()[to];
                    final ParseNode bNode = dNode.getChild()[from];
                    final ParseNode cNode = dNode.getChild()[to];

                    final ParseNode prevENode = eNode;
                    eNode = createAssocBinaryNode(from, stmt,
                        createAssocBinaryNode(from, stmt, aNode, bNode), cNode);

                    // transform to other direction => 'to'
                    final ProofStepStmt assocTr = createAssociativeStep(
                        proofWorksheet, derivStep, to, prevENode, eNode);

                    final ParseNode prevGNode = gNode;
                    gNode = createAssocBinaryNode(from, stmt, eNode, fNode);

                    final ProofStepStmt replTr = createReplaceStep(
                        proofWorksheet, derivStep, prevGNode, from, eNode,
                        assocTr);

                    resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                        resStmt, replTr);
                }
                else
                    break;
            }

            assert gAssT.subTrees[0].size == trgt.structure.subTrees[0].size;
            assert gAssT.subTrees[1].size == trgt.structure.subTrees[1].size;

            final Transformation replaceMe = new ReplaceTransformation(gNode);
            final Transformation replaceTarget = new ReplaceTransformation(
                target.originalNode);

            final ProofStepStmt replTrStep = replaceMe.transformMeToTarget(
                replaceTarget, proofWorksheet, derivStep);

            if (replTrStep != null)
                resStmt = transitiveConnectStep(proofWorksheet, derivStep,
                    resStmt, replTrStep);

            return resStmt;
        }

        private ParseNode constructCanonicalForm(ParseNode left,
            final ParseNode cur)
        {
            if (cur.getStmt() != originalNode.getStmt()) {
                final ParseNode leaf = getCanonicalForm(cur);
                if (left == null)
                    return leaf;
                else
                    return createBinaryNode(originalNode.getStmt(), left, leaf);
            }

            for (int i = 0; i < 2; i++)
                left = constructCanonicalForm(left, cur.getChild()[i]);

            return left;
        }

        @Override
        public ParseNode getCanonicalNode() {
            return constructCanonicalForm(null, originalNode);
        }

        @Override
        public String toString() {
            return super.toString() + "\n" + structure;
        }
    }

    private ProofStepStmt createReplaceStep(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final ParseNode prevVersion, final int i, final ParseNode newSubTree,
        final ProofStepStmt childTrStmt)
    {
        final Stmt equalStmt = eqMap.get(prevVersion.getStmt().getTyp());
        final Assrt[] replAsserts = replaceOp.get(prevVersion.getStmt());
        final ParseNode resNode = prevVersion.cloneWithoutChildren();

        // Fill the next child
        // So the new node has form g(A', B', C)
        resNode.getChild()[i] = newSubTree;

        // Create node g(A', B, C) = g(A', B', C)
        final ParseNode stepNode = createBinaryNode(equalStmt, prevVersion,
            resNode);

        // Create statement d:childTrStmt:replAssert
        // |- g(A', B, C) = g(A', B', C)
        final ProofStepStmt stepTr = getOrCreateProofStepStmt(proofWorksheet,
            derivStep, stepNode, new ProofStepStmt[]{childTrStmt},
            replAsserts[i]);
        return stepTr;
    }

    private ProofStepStmt createAssociativeStep(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final int from, final ParseNode prevNode, final ParseNode newNode)
    {
        final Assrt[] assocTr = assocOp.get(prevNode.getStmt());

        final boolean revert;
        final Assrt assocAssrt;
        if (assocTr[from] != null) {
            assocAssrt = assocTr[from];
            revert = false;
        }
        else {
            final int other = (from + 1) % 2;
            assocAssrt = assocTr[other];
            revert = true;
        }
        assert assocAssrt != null;

        final Stmt equalStmt = assocAssrt.getExprParseTree().getRoot()
            .getStmt();

        // Create node f(f(a, b), c) = f(a, f(b, c))
        final ParseNode stepNode = !revert ? createBinaryNode(equalStmt,
            prevNode, newNode) : createBinaryNode(equalStmt, newNode, prevNode);

        ProofStepStmt res = getOrCreateProofStepStmt(proofWorksheet, derivStep,
            stepNode, new ProofStepStmt[]{}, assocAssrt);

        if (revert) {
            final Assrt eqComm = eqCommutatives.get(equalStmt);
            final ParseNode revNode = createBinaryNode(equalStmt, prevNode,
                newNode);

            res = getOrCreateProofStepStmt(proofWorksheet, derivStep, revNode,
                new ProofStepStmt[]{res}, eqComm);
        }

        return res;
    }

    private ProofStepStmt transitiveConnectStep(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final ProofStepStmt prevRes, final ProofStepStmt newRes)
    {
        if (prevRes == null)
            return newRes;

        final ParseNode prevRoot = prevRes.formulaParseTree.getRoot();
        final ParseNode newRoot = newRes.formulaParseTree.getRoot();
        final Stmt equalStmt = prevRoot.getStmt();
        final Assrt transitive = eqTransitivies.get(equalStmt);

        final ParseNode transitiveNode = createBinaryNode(equalStmt,
            prevRoot.getChild()[0], newRoot.getChild()[1]);

        // resStmt now have the form g(A, B, C) = g(A', B, C)
        // So create transitive:
        // g(A, B, C) = g(A', B, C) & g(A', B, C) = g(A', B', C)
        // => g(A, B, C) = g(A', B', C)
        // Create statement d:resStmt,stepTr:transitive
        // |- g(A, B, C) = g(A', B', C)
        final ProofStepStmt resStmt = getOrCreateProofStepStmt(proofWorksheet,
            derivStep, transitiveNode, new ProofStepStmt[]{prevRes, newRes},
            transitive);

        return resStmt;
    }

    private static ParseNode createAssocBinaryNode(final int from,
        final Stmt stmt, final ParseNode left, final ParseNode right)
    {
        if (from == 0)
            return createBinaryNode(stmt, left, right);
        else
            return createBinaryNode(stmt, right, left);
    }

    private static ParseNode createBinaryNode(final Stmt stmt,
        final ParseNode left, final ParseNode right)
    {
        final ParseNode eqRoot = new ParseNode(stmt);
        final ParseNode[] eqChildren = {left, right};
        eqRoot.setChild(eqChildren);
        return eqRoot;
    }

    private Transformation createTransformation(final ParseNode originalNode) {
        final Stmt stmt = originalNode.getStmt();

        final Assrt[] replAsserts = replaceOp.get(stmt);
        // final Assrt comAssert = comOp.get(stmt);

        final boolean isAssoc = assocOp.containsKey(stmt);

        final boolean subTreesCouldBeRepl = replAsserts != null;
        // final boolean comOper = comAssert != null;

        if (!subTreesCouldBeRepl)
            return new IdentityTransformation(originalNode);

        if (isAssoc)
            return new AssociativeTransformation(originalNode, createAssocTree(
                originalNode, stmt));
        else if (subTreesCouldBeRepl)
            return new ReplaceTransformation(originalNode);

        // TODO: make the string constant!
        throw new IllegalStateException(
            "Error in createTransformation() algorithm");
    }

    private ParseNode getCanonicalForm(final ParseNode originalNode) {
        return createTransformation(originalNode).getCanonicalNode();
    }

    /**
     * @param first The one operand
     * @param second The other operand
     * @return -1(less), 0(equal),1(greater)
     */
    @SuppressWarnings("unused")
    private static int compareNodes(final ParseNode first,
        final ParseNode second)
    {
        if (first.getStmt() == second.getStmt()) {
            final int len = first.getChild().length;
            for (int i = 0; i < len; i++) {
                final int res = compareNodes(first.getChild()[i],
                    second.getChild()[i]);
                if (res != 0)
                    return res;
            }

            return 0;
        }
        return first.getStmt().getSeq() < second.getStmt().getSeq() ? -1 : 1;
    }

    // -------------

    /**
     * This function is needed for debug
     * 
     * @param proofStmt the proof statement
     * @return the corresponding canonical formula
     */
    private Formula getCanonicalFormula(final ProofStepStmt proofStmt) {
        return getFormula(proofStmt.getCanonicalForm());
    }

    /**
     * This function is needed for debug
     * 
     * @param node the input node
     * @return the corresponding formula
     */
    private Formula getFormula(final ParseNode node) {
        final ParseTree tree = new ParseTree(node);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        return generatedFormula;
    }

    private ProofStepStmt getOrCreateProofStepStmt(
        final ProofWorksheet proofWorksheet, final DerivationStep derivStep,
        final ParseNode root, final ProofStepStmt[] hyps, final Assrt assrt)
    {
        final ParseTree tree = new ParseTree(root);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        generatedFormula.setTyp(provableLogicStmtTyp);

        final ProofStepStmt findMatchingStepFormula = proofWorksheet
            .findMatchingStepFormula(generatedFormula, derivStep);

        if (findMatchingStepFormula != null)
            return findMatchingStepFormula;

        if (hyps == null || assrt == null)
            return null;

        assert assrt.getLogHypArray().length == hyps.length;

        final String[] steps = new String[hyps.length];
        for (int i = 0; i < hyps.length; i++)
            steps[i] = hyps[i].getStep();

        final DerivationStep d = proofWorksheet.addDerivStep(derivStep, hyps,
            steps, assrt.getLabel(), generatedFormula, tree,
            Collections.<WorkVar> emptyList());
        return d;
    }

    // ---------------------

    private Assrt performTransformation(final ProofWorksheet proofWorksheet,
        final DerivationStep derivStep, final ProofStepStmt source)
    {
        final Transformation dsTr = createTransformation(derivStep.formulaParseTree
            .getRoot());
        final Transformation tr = createTransformation(source.formulaParseTree
            .getRoot());

        final ProofStepStmt eqResult = tr.transformMeToTarget(dsTr,
            proofWorksheet, derivStep);
        eqResult.toString();

        final Cnst type = derivStep.formulaParseTree.getRoot().getStmt()
            .getTyp();

        final Assrt impl = eqImplications.get(type);

        final boolean isNormalOrder = isVarNode(impl.getLogHypArray()[0]
            .getExprParseTree().getRoot());

        final ProofStepStmt[] hypDerivArray = isNormalOrder ? new ProofStepStmt[]{
                source, eqResult}
            : new ProofStepStmt[]{eqResult, source};

        final String[] hypStep = new String[hypDerivArray.length];
        for (int i = 0; i < hypStep.length; i++)
            hypStep[i] = hypDerivArray[i].getStep();

        derivStep.setHypList(hypDerivArray);
        derivStep.setHypStepList(hypStep);

        return impl;
    }

    /**
     * Tries to unify the derivation step by some automatic transformations
     * 
     * @param proofWorksheet the proof work sheet
     * @param derivStep the derivation step
     * @return true if it founds possible unification
     */
    public Assrt tryToFindTransformations(final ProofWorksheet proofWorksheet,
        final DerivationStep derivStep)
    {
        if (!isInit)
            return null;

        if (!eqImplications.containsKey(derivStep.formulaParseTree.getRoot()
            .getStmt().getTyp()))
            return null;

        final Transformation dsTr = createTransformation(derivStep.formulaParseTree
            .getRoot());
        final ParseNode dsCanonicalForm = dsTr.getCanonicalNode();
        derivStep.setCanonicalForm(dsCanonicalForm);

        messages.accumInfoMessage("I-DBG Step %s has canonical form: %s",
            derivStep, getCanonicalFormula(derivStep));

        for (final ProofWorkStmt proofWorkStmtObject : proofWorksheet
            .getProofWorkStmtList())
        {

            if (proofWorkStmtObject == derivStep)
                break;

            if (!(proofWorkStmtObject instanceof ProofStepStmt))
                continue;

            final ProofStepStmt candidate = (ProofStepStmt)proofWorkStmtObject;

            if (candidate.getCanonicalForm() == null) {
                candidate
                    .setCanonicalForm(getCanonicalForm(candidate.formulaParseTree
                        .getRoot()));

                messages.accumInfoMessage(
                    "I-DBG Step %s has canonical form: %s", candidate,
                    getCanonicalFormula(candidate));
            }

            if (derivStep.getCanonicalForm().isDeepDup(
                candidate.getCanonicalForm()))
            {
                messages.accumInfoMessage(
                    "I-DBG found canonical forms correspondance: %s and %s",
                    candidate, derivStep);
                final Assrt res = performTransformation(proofWorksheet,
                    derivStep, candidate);
                return res;
            }
        }
        return null;
    }
    // ------------Auxiliary functions--------------

    private static boolean isVarNode(final ParseNode node) {
        return isVarStmt(node.getStmt());
    }

    private static boolean isVarStmt(final Stmt stmt) {
        return stmt instanceof VarHyp;
    }

    private static boolean isConstNode(final ParseNode node) {
        if (isVarNode(node))
            return false;

        for (final ParseNode child : node.getChild())
            if (!isConstNode(child))
                return false;

        return true;
    }
}
