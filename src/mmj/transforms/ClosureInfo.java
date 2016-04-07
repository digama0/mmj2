//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;
import mmj.transforms.ComplexRuleMap.ComplexRuleVisitor;
import mmj.transforms.ImplicationInfo.ExtractImplResult;

public class ClosureInfo extends DBInfo {

    private final ImplicationInfo implInfo;

    private final ConjunctionInfo conjInfo;

    private static class ClosureComplexRuleMap extends ComplexRuleMap<Assrt> {}

    /**
     * The list of closure lows: A e. CC & B e. CC => (A + B) e. CC
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> set of possible properties ( _ e. CC in
     * the example). There could be many properties ( {" _ e. CC" , "_ e. RR" }
     * for example ).
     */
    private final ClosureComplexRuleMap closureRuleMap = new ClosureComplexRuleMap();

    /**
     * The list of closure lows: ( A e. CC & B e. CC )-> (A + B) e. CC
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> set of possible properties ( _ e. CC in
     * the example). There could be many properties ( {" _ e. CC" , "_ e. RR" }
     * for example ).
     */
    private final ClosureComplexRuleMap implClosureRuleMap = new ClosureComplexRuleMap();

    private final Set<PropertyTemplate> possibleProperties = new HashSet<>();

    /** The information about properties of constants */
    private final Map<PropertyTemplate, Map<ParseNodeHashElem, Assrt>> constInfo;

    /** This constant is needed for internal debug */
    private static final boolean supportConstants = true;

    // ------------------------------------------------------------------------
    // ------------------------Initialization----------------------------------
    // ------------------------------------------------------------------------

    public ClosureInfo(final ImplicationInfo implInfo,
        final ConjunctionInfo conjInfo, final List<Assrt> assrtList,
        final TrOutput output, final boolean dbg)
    {
        super(output, dbg);
        this.implInfo = implInfo;
        this.conjInfo = conjInfo;
        constInfo = new HashMap<>();

        for (final Assrt assrt : assrtList)
            findClosureRules(assrt);

        for (final Assrt assrt : assrtList)
            findImplClosureRules(assrt);

        for (final Assrt assrt : assrtList)
            findClosureRulesForConsts(assrt);

        // debug output
        if (dbg)
            for (final PropertyTemplate template : possibleProperties)
                output.dbgMessage(dbg, TrConstants.ERRMSG_CLOSURE_TRANS,
                    template.toString());
    }

    private static final ParseNode endMarker = new ParseNode();

    private static ParseNode getCorrespondingNodeRec(final ParseNode template,
        final ParseNode input)
    {
        if (template == PropertyTemplate.templateReplace)
            return input;
        if (template.stmt != input.stmt)
            return null;

        ParseNode retNode = endMarker;

        for (int i = 0; i < input.child.length; i++) {
            final ParseNode res = getCorrespondingNodeRec(template.child[i],
                input.child[i]);
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
        final ParseNode[] children = template.child;
        int res = 0;
        for (int i = 0; i < children.length; i++)
            if (children[i].stmt == var) {
                children[i] = PropertyTemplate.templateReplace; // indicate
                                                                // entry point
                res++;
            }
            else
                res += prepareTemplate(children[i], var);
        return res;
    }

    private static ParseNode createTemplateNodeFromHypRoot(
        final ParseNode hypRoot, final VarHyp var)
    {
        // do not consider rules like |- ph & |- ps => |- ( ph <-> ps )
        if (hypRoot.stmt == var)
            return null;

        // Here we need deep clone because next we will modify result
        final ParseNode templNode = hypRoot.deepClone();
        final int varNumEntrance = prepareTemplate(templNode, var);
        if (varNumEntrance != 1)
            return null;

        return templNode;
    }

    private static ParseNode createTemplateNodeFromFirstHyp(final Assrt assrt) {
        final VarHyp[] varHypArray = assrt.getMandVarHypArray();

        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length == 0)
            return null;

        if (logHyps.length != varHypArray.length)
            return null;

        final VarHyp[] vars0 = logHyps[0].getMandVarHypArray();
        if (vars0.length != 1)
            return null;

        final VarHyp var = vars0[0];

        final ParseNode log0Root = logHyps[0].getExprParseTree().getRoot();

        return createTemplateNodeFromHypRoot(log0Root, var);
    }

    // The returned template could be empty!
    private static PropertyTemplate createTemplateFromHyp(final Assrt assrt,
        final VarHyp[] hypToVarHypMap)
    {

        final ParseNode templNode = createTemplateNodeFromFirstHyp(assrt);
        if (templNode == null)
            return null;

        final LogHyp[] logHyps = assrt.getLogHypArray();

        for (int i = 1; i < logHyps.length; i++) {
            final VarHyp vari = hypToVarHypMap[i];
            final ParseNode res = getCorrespondingNode(templNode,
                logHyps[i].getExprParseTree().getRoot());
            if (res == null)
                return null;
            if (res.stmt != vari)
                return null;
        }

        final PropertyTemplate template = new PropertyTemplate(templNode);
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

        final VarHyp[] hypToVarHypMap = TrUtil.getHypToVarMap(assrt);
        if (hypToVarHypMap == null)
            return;
        assert hypToVarHypMap.length != 0;

        final ParseNode mainRoot = assrtTree.getRoot();

        final PropertyTemplate template = createTemplateFromHyp(assrt,
            hypToVarHypMap);

        if (template == null)
            return;

        findClosureRulesCore(assrt, mainRoot, template, hypToVarHypMap,
            closureRuleMap);
    }

    private void findImplClosureRules(final Assrt assrt) {
        final ParseTree assrtTree = assrt.getExprParseTree();

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 4)
            return;

        final ResultClosureInfo res = extractImplClosureInfo(assrt);
        if (res == null)
            return;

        findClosureRulesCore(assrt, res.main, res.template, res.hypToVarHypMap,
            implClosureRuleMap);
    }

    public class ResultClosureInfo {
        /** The root of main part (after ->) */
        public final ParseNode main;
        /** The used template (e.g. x e. CC) */
        public final PropertyTemplate template;
        /** Array of used variables */
        public final VarHyp[] hypToVarHypMap;

        public ResultClosureInfo(final ParseNode main,
            final PropertyTemplate template, final VarHyp[] hypToVarHypMap)
        {
            this.main = main;
            this.template = template;
            this.hypToVarHypMap = hypToVarHypMap;
        }
    }

    /**
     * Parses rules in implication form
     *
     * @param assrt input assertion
     * @return result information
     */
    public ResultClosureInfo extractImplClosureInfo(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 0)
            return null;

        final ParseNode root = assrtTree.getRoot();

        if (!implInfo.isImplOperator(root.stmt))
            return null;

        final ParseNode main = root.child[1];
        final ParseNode hypNodePart = root.child[0];

        final List<ParseNode> hypNodes = conjInfo.separateByAnd(hypNodePart);

        final VarHyp[] hypToVarHypMap = new VarHyp[hypNodes.size()];

        final ParseNode firstHyp = hypNodes.get(0);
        final VarHyp firstVar = TrUtil.findOneVarInParseNode(firstHyp);

        hypToVarHypMap[0] = firstVar;

        if (firstVar == null)
            return null;

        final ParseNode templNode = createTemplateNodeFromHypRoot(firstHyp,
            firstVar);
        if (templNode == null)
            return null;

        for (int i = 1; i < hypNodes.size(); i++) {
            final ParseNode hyp = hypNodes.get(i);
            final VarHyp var = TrUtil.findOneVarInParseNode(hyp);
            if (var == null)
                return null;
            hypToVarHypMap[i] = var;

            final ParseNode res = getCorrespondingNode(templNode, hyp);

            if (res == null)
                return null;

            if (res.stmt != var)
                return null;
        }

        final PropertyTemplate template = new PropertyTemplate(templNode);

        return new ResultClosureInfo(main, template, hypToVarHypMap);
    }

    private void findClosureRulesCore(final Assrt assrt,
        final ParseNode mainRoot, final PropertyTemplate template,
        final VarHyp[] hypToVarHypMap, final ClosureComplexRuleMap resMap)
    {
        final ParseNode res = getCorrespondingNode(template.node, mainRoot);
        if (res == null)
            return;

        final int hypNum = hypToVarHypMap.length;
        final Stmt stmt = res.stmt;

        final ParseNode[] children = res.child;

        final int varToHypMap[] = new int[hypNum];
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
                    if (hypToVarHypMap[k] == child.stmt) {
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

        final int[] hypToVarMap = new int[hypNum];
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

        final ConstSubst constSubst = new ConstSubst(constMap);

        final Assrt addRes = resMap.addData(stmt, constSubst, template, assrt);
        if (addRes != assrt)
            return;

        possibleProperties.add(template);

        output.dbgMessage(dbg, TrConstants.ERRMSG_TRANS_TO_RESULT,
            incorrectOrder, assrt, assrt.getFormula());
    }

    private void findClosureRulesForConsts(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 0)
            return;

        final ParseNode root = assrtTree.getRoot();

        if (!TrUtil.isConstNode(root))
            return;

        for (final PropertyTemplate template : possibleProperties) {
            final ParseNode core = template.extractNode(root);
            if (core != null) {
                /*
                if (core.toString().equals("c1")
                    && template.toString().equals(
                        "node { [Template-stub, cc]; wcel }"))
                    core.toString();
                */
                Map<ParseNodeHashElem, Assrt> cMap = constInfo.get(template);
                if (cMap == null) {
                    cMap = new HashMap<>();
                    constInfo.put(template, cMap);
                }

                final ParseNodeHashElem key = new ParseNodeHashElem(core);

                if (cMap.containsKey(key))
                    continue;

                cMap.put(key, assrt);

                output.dbgMessage(dbg, TrConstants.ERRMSG_TRANS_TO_CONST, core,
                    assrt, assrt.getFormula());
            }
        }
    }

    // ------------------------------------------------------------------------
    // ----------------------------Detection-----------------------------------
    // ------------------------------------------------------------------------

    public static class TemplDetectRes {
        public final VarHyp[] hypToVarHypMap;
        public final PropertyTemplate template;

        public TemplDetectRes(final VarHyp[] hypToVarHypMap,
            final PropertyTemplate template)
        {
            this.hypToVarHypMap = hypToVarHypMap;
            this.template = template;
        }
    }

    public static TemplDetectRes getTemplateAndVarHyps(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length == 0)
            return new TemplDetectRes(null, new PropertyTemplate(null));

        final VarHyp[] hypToVarHypMap = TrUtil.getHypToVarMap(assrt);
        if (hypToVarHypMap == null)
            return null;
        final PropertyTemplate template = createTemplateFromHyp(assrt,
            hypToVarHypMap);
        if (template == null)
            return null;
        return new TemplDetectRes(hypToVarHypMap, template);
    }

    public static PropertyTemplate createTemplateFromHyp(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        if (logHyps.length == 0)
            return new PropertyTemplate(null);

        final VarHyp[] hypToVarHypMap = TrUtil.getHypToVarMap(assrt);
        if (hypToVarHypMap == null)
            return null;
        return createTemplateFromHyp(assrt, hypToVarHypMap);
    }

    // ------------------------------------------------------------------------
    // ----------------------------Transformations-----------------------------
    // ------------------------------------------------------------------------

    /**
     * Creates closure rule applying generalized statement for the the node
     * recursively. For example, suppose we have |- A e. CC. The this function
     * for the input " _ e. CC" and "( sin ` A )" will find "|- A e. CC" and
     * then will generate "|- ( sin ` A ) e. CC"
     *
     * @param info the work sheet info
     * @param template template (" _ e. CC" in the example)
     * @param node the input node ("( sin ` A )" in the example)
     * @param finishStatement true if the result statement is a target statement
     * @param searchWithPrefix true if we could search closure rules with
     *            implication prefix
     * @return top-level closure statement ("|- ( sin ` A ) e. CC" in the
     *         example)
     */
    public GenProofStepStmt closureProperty(final WorksheetInfo info,
        final PropertyTemplate template, final ParseNode node,
        final boolean finishStatement, final boolean searchWithPrefix)
    {
        final ParseNode stepNode = template.subst(node);

        if (finishStatement)
            if (info.hasImplPrefix())
                assert info.derivStep.formulaParseTree.getRoot()
                    .isDeepDup(TrUtil.createBinaryNode(info.implStatement,
                        info.implPrefix, stepNode));
            else
                assert info.derivStep.formulaParseTree.getRoot()
                    .isDeepDup(stepNode);

        final ProofStepStmt res1 = info.getProofStepStmt(stepNode);
        if (res1 != null)
            return new GenProofStepStmt(res1, null);

        if (searchWithPrefix && info.hasImplPrefix()) {
            final ParseNode implForm = info.applyImplPrefix(stepNode);
            final ProofStepStmt stmtWithImpl = info.getProofStepStmt(implForm);
            if (stmtWithImpl != null)
                return new GenProofStepStmt(stmtWithImpl, info.implPrefix);
        }

        final ClosureResult clRes = getClosurePossibility(info, node, template,
            searchWithPrefix);

        switch (clRes) {
            case CONSTANT_RULE: {
                // We should not be here if we doesn't support constant property
                // auto completion
                assert supportConstants;

                // So it should be constant
                assert TrUtil.isConstNode(node);

                final Map<ParseNodeHashElem, Assrt> cMap = constInfo
                    .get(template);

                assert cMap != null;

                final Assrt assrt = cMap.get(new ParseNodeHashElem(node));

                assert assrt != null;

                final ParseNode root = assrt.getExprParseTree().getRoot();

                final ProofStepStmt res = info.getOrCreateProofStepStmt(root,
                    new ProofStepStmt[0], assrt);

                assert res != null;
                return new GenProofStepStmt(res, null);
            }
            case SIMPLE_RULE: {
                // We found possible f(A) & f(B) => f(g(A,B)) rule
                final CreateClosureVisitor visitor = new CreateClosureVisitor() {
                    public ClosureComplexRuleMap getMap() {
                        return closureRuleMap;
                    }

                    public GenProofStepStmt createClosureStep(
                        final GenProofStepStmt[] genHyps, final Assrt assrt)
                    {
                        final ProofStepStmt[] hyps = TrUtil
                            .convertGenToSimpleProofSteps(genHyps);

                        if (!finishStatement) {
                            // It is not the last one step, we should generate
                            // f(g(A,B)) step
                            final ProofStepStmt r = info
                                .getOrCreateProofStepStmt(stepNode, hyps,
                                    assrt);
                            return new GenProofStepStmt(r, null);
                        }
                        else if (info.hasImplPrefix()) {
                            // It is the last one step and he has form
                            // ph->f(g(A,B)), so we should generate
                            // f(g(A,B)) step and then use ph->f(g(A,B)) target
                            // derivation step
                            final ProofStepStmt r = info
                                .getOrCreateProofStepStmt(stepNode, hyps,
                                    assrt);
                            implInfo.finishStubRule(info, r);
                            return new GenProofStepStmt(info.derivStep, null);
                        }
                        else {
                            // It is the last one step, and we should use
                            // f(g(A,B)) target derivation step
                            info.finishDerivationStep(hyps, assrt);
                            return new GenProofStepStmt(info.derivStep, null);
                        }
                    }
                };

                final GenProofStepStmt res = closureVisitorEntrance(info,
                    template, node, visitor, false);

                assert res != null;
                return res;
            }
            case SIMPLE_RULE_IMPL:
            case USED_PREFIX_RULE: {
                // We found possible f(A) /\ f(B) -> f(g(A,B)) rule
                final CreateClosureVisitor visitor = new CreateClosureVisitor() {
                    public ClosureComplexRuleMap getMap() {
                        return implClosureRuleMap;
                    }

                    public GenProofStepStmt createClosureStep(
                        final GenProofStepStmt[] hyps, final Assrt assrt)
                    {
                        final ParseNode assrtRoot = assrt.getExprParseTree()
                            .getRoot();
                        final ParseNode hypsPartPattern = assrtRoot.child[0];
                        final ParseNode implRes = stepNode;

                        // Precondition f(A) /\ f(B) step (or ph->(f(A) /\ f(B))
                        // )
                        final GenProofStepStmt hypGenStep = conjInfo
                            .concatenateInTheSamePattern(hyps, hypsPartPattern,
                                info);
                        if (hypGenStep.hasPrefix())
                            assert clRes == ClosureResult.USED_PREFIX_RULE;

                        if (!finishStatement) {
                            assert hypGenStep != null;
                            // It is not the last one step

                            return implInfo.applyHyp(info, hypGenStep, stepNode,
                                assrt);
                        }
                        else {
                            assert hypGenStep != null;
                            // It is the last one step, and we should use
                            // target derivation step
                            if (!hypGenStep.hasPrefix()) {
                                // The precondition has simple f(A) /\ f(B) form
                                if (info.hasImplPrefix()) {
                                    // The target derivation step has
                                    // ph->f(g(A,B))
                                    // form
                                    final ProofStepStmt r = implInfo
                                        .applyImplicationRule(info,
                                            hypGenStep.getSimpleStep(), implRes,
                                            assrt);
                                    implInfo.finishStubRule(info, r);
                                    return new GenProofStepStmt(info.derivStep,
                                        null);
                                }
                                else {
                                    // And target derivation step has simple
                                    // f(g(A,B)) form
                                    implInfo.finishWithImplication(info,
                                        hypGenStep.getSimpleStep(), implRes,
                                        assrt);
                                    return new GenProofStepStmt(info.derivStep,
                                        null);
                                }
                            }
                            else {
                                // The precondition has implication
                                // ph->(f(A)/\f(B))
                                // form and the target derivation step has
                                // ph->f(g(A,B)) form.
                                implInfo.finishTransitiveRule(info,
                                    hypGenStep.getImplicationStep(), implRes,
                                    assrt);
                                return new GenProofStepStmt(info.derivStep,
                                    hypGenStep.getPrefix());
                            }
                        }
                    }
                };
                final GenProofStepStmt res = closureVisitorEntrance(info,
                    template, node, visitor, searchWithPrefix);

                assert res != null;
                return res;
            }
            case NO_CLOSURE_RULE:
                throw new IllegalStateException(
                    "Unchecked call of this function");
            default:
                throw new IllegalStateException("Unknown constant: " + clRes);
        }
    }

    private interface CreateClosureVisitor {
        public ClosureComplexRuleMap getMap();
        public GenProofStepStmt createClosureStep(final GenProofStepStmt[] hyps,
            final Assrt assrt);
    }

    private GenProofStepStmt closureVisitorEntrance(final WorksheetInfo info,
        final PropertyTemplate template, final ParseNode node,
        final CreateClosureVisitor visitor, final boolean searchWithPrefix)
    {
        final GenProofStepStmt res = visitor.getMap().visitGenStmts(node, info,
            new ComplexRuleVisitor<Assrt, GenProofStepStmt>()
        {
                public GenProofStepStmt visit(final ParseNode node,
                    final WorksheetInfo info, final ConstSubst constSubst,
                    final int[] varIndexes,
                    final Map<PropertyTemplate, Assrt> propertyMap)
            {
                    final Assrt assrt = propertyMap.get(template);
                    if (assrt == null)
                        return null;
                    final GenProofStepStmt[] hyps = new GenProofStepStmt[varIndexes.length];
                    for (int i = 0; i < varIndexes.length; i++) {
                        final int n = varIndexes[i];
                        // Variable position
                        final ParseNode child = node.child[n];
                        assert child != null;

                        if (!getClosurePossibility(info, child, template,
                            searchWithPrefix).hasClosure)
                            return null;

                        final GenProofStepStmt childRes = closureProperty(info,
                            template, child, false, searchWithPrefix);
                        if (childRes == null)
                            return null;

                        hyps[i] = childRes;
                    }
                    final GenProofStepStmt r = visitor.createClosureStep(hyps,
                        assrt);
                    return r;
                }

                public GenProofStepStmt failValue() {
                    return null;
                }
            });

        assert res != null;
        return res;
    }

    // -------------
    public static enum ClosureResult {
        NO_CLOSURE_RULE(false, 1),

        USED_PREFIX_RULE(true, 2),

        SIMPLE_RULE(true, 3),

        // Almost the same as SIMPLE_RULE
        // The only difference is in the search transformation algorithm
        SIMPLE_RULE_IMPL(true, 3),

        CONSTANT_RULE(true, 4);

        boolean hasClosure;
        int num;

        private ClosureResult(final boolean hasClosure, final int num) {
            this.hasClosure = hasClosure;
            this.num = num;
        }
    }

    static public ClosureResult mergeSearchResults(
        final ClosureResult preferred, final ClosureResult other)
    {
        if (preferred.num <= other.num)
            return preferred;
        else
            return other;
    }

    private ClosureResult getClosurePossibilityInternal(
        final WorksheetInfo info, final ParseNode node,
        final PropertyTemplate template, final ClosureComplexRuleMap map,
        final boolean searchWithPrefix, final ClosureResult simpleRes)
    {
        final ClosureResult res = map.visitGenStmts(node, info,
            new ComplexRuleVisitor<Assrt, ClosureResult>()
        {
                public ClosureResult visit(final ParseNode node,
                    final WorksheetInfo info, final ConstSubst constSubst,
                    final int[] varIndexes,
                    final Map<PropertyTemplate, Assrt> propertyMap)
            {
                    if (!propertyMap.containsKey(template))
                        return ClosureResult.NO_CLOSURE_RULE;

                    ClosureResult childResMerge = simpleRes;
                    for (int i = 0; i < node.child.length; i++)
                        if (constSubst.constMap[i] == null) {
                            // Variable position
                            final ParseNode child = node.child[i];
                            final ClosureResult propRes = getClosurePossibility(
                                info, child, template, searchWithPrefix);
                            if (!propRes.hasClosure)
                                return ClosureResult.NO_CLOSURE_RULE;
                            childResMerge = mergeSearchResults(childResMerge,
                                propRes);
                        }

                    return childResMerge;
                }

                public ClosureResult failValue() {
                    return ClosureResult.NO_CLOSURE_RULE;
                }
            });
        return res;
    }

    public ClosureResult getClosurePossibility(final WorksheetInfo info,
        final ParseNode node, final PropertyTemplate template,
        final boolean searchWithPrefix)
    {
        final ParseNode substProp = template.subst(node);
        final ProofStepStmt stmt = info.getProofStepStmt(substProp);
        if (stmt != null)
            return ClosureResult.SIMPLE_RULE;

        if (supportConstants)
            if (TrUtil.isConstNode(node)) {
                final Map<ParseNodeHashElem, Assrt> cMap = constInfo
                    .get(template);
                if (cMap != null
                    && cMap.containsKey(new ParseNodeHashElem(node)))
                    return ClosureResult.CONSTANT_RULE;
            }

        final ClosureResult simple = getClosurePossibilityInternal(info, node,
            template, closureRuleMap, false, ClosureResult.SIMPLE_RULE);
        assert simple == ClosureResult.SIMPLE_RULE
            || simple == ClosureResult.NO_CLOSURE_RULE;
        if (simple.hasClosure)
            return simple;

        final ClosureResult implRes = getClosurePossibilityInternal(info, node,
            template, implClosureRuleMap, searchWithPrefix,
            ClosureResult.SIMPLE_RULE_IMPL);
        if (implRes.hasClosure)
            return implRes;

        if (searchWithPrefix && info.hasImplPrefix()) {
            final ParseNode implForm = info.applyImplPrefix(substProp);
            final ProofStepStmt stmtWithImpl = info.getProofStepStmt(implForm);
            if (stmtWithImpl != null)
                return ClosureResult.USED_PREFIX_RULE;
        }

        return ClosureResult.NO_CLOSURE_RULE;
    }
    // ------------------------------------------------------------------------
    // ---------------------------Transformations------------------------------
    // ------------------------------------------------------------------------

    public boolean performClosureTransformationInternal(
        final WorksheetInfo info, final ParseNode input)
    {
        // TODO: optimize it!!!
        for (final PropertyTemplate template : possibleProperties) {
            final ParseNode core = template.extractNode(input);
            if (core == null)
                continue;
            if (getClosurePossibility(info, core, template, true).hasClosure) {
                closureProperty(info, template, core, true, true);
                return true;
            }
        }
        return false;
    }

    public boolean performClosureTransformation(final WorksheetInfo info) {
        final ParseNode root = info.derivStep.formulaParseTree.getRoot();

        if (performClosureTransformationInternal(info, root))
            return true;

        final ExtractImplResult extrImplRes = implInfo
            .extractPrefixAndGetImplPart(info);

        if (extrImplRes != null) {
            info.setImplicationPrefix(extrImplRes.implPrefix,
                extrImplRes.implStatement);
            if (performClosureTransformationInternal(info, extrImplRes.core))
                return true;
        }

        return false;
    }

    // ------------------------------------------------------------------------
    // ------------------------------Getters-----------------------------------
    // ------------------------------------------------------------------------

    public boolean hasClosureAssert(final Stmt stmt,
        final ConstSubst constSubst, final PropertyTemplate template)
    {
        final Assrt assrt = closureRuleMap.getData(stmt, constSubst, template);

        if (assrt != null)
            return true;

        final Assrt assrtImpl = implClosureRuleMap.getData(stmt, constSubst,
            template);

        return assrtImpl != null;
    }
}
