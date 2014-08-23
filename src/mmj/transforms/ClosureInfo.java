package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.pa.ProofStepStmt;
import mmj.transforms.ComplexRuleMap.ComplexRuleVisitor;
import mmj.transforms.WorksheetInfo.GenProofStepStmt;

public class ClosureInfo extends DBInfo {

    private final ImplicationInfo implInfo;

    private final ConjunctionInfo conjInfo;

    /**
     * The list of closure lows: A e. CC & B e. CC => (A + B) e. CC
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> set of possible properties ( _ e. CC in
     * the example). There could be many properties ( {" _ e. CC" , "_ e. RR" }
     * for example ).
     */

    private static class ClosureComplexRuleMap extends ComplexRuleMap<Assrt> {}

    private final ClosureComplexRuleMap closureRuleMap = new ClosureComplexRuleMap();

    private final ClosureComplexRuleMap implClosureRuleMap = new ClosureComplexRuleMap();

    private final Set<PropertyTemplate> possibleProperties = new HashSet<PropertyTemplate>();

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
        constInfo = new HashMap<PropertyTemplate, Map<ParseNodeHashElem, Assrt>>();

        for (final Assrt assrt : assrtList)
            findClosureRules(assrt);

        for (final Assrt assrt : assrtList)
            findImplClosureRules(assrt);

        for (final Assrt assrt : assrtList)
            findClosureRulesForConsts(assrt);

        // debug output
        if (dbg)
            for (final PropertyTemplate template : possibleProperties)
                output.dbgMessage(dbg, "I-TR-DBG transitive property: %s",
                    template.toString());
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

    private static ParseNode createTemplateNodeFromHypRoot(
        final ParseNode hypRoot, final VarHyp var)
    {
        // do not consider rules like |- ph & |- ps => |- ( ph <-> ps )
        if (hypRoot.getStmt() == var)
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
            final ParseNode res = getCorrespondingNode(templNode, logHyps[i]
                .getExprParseTree().getRoot());
            if (res == null)
                return null;
            if (res.getStmt() != vari)
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

        findClosureRulesCore(assrt, mainRoot, logHyps.length, template,
            hypToVarHypMap, closureRuleMap);
    }

    private static void findVarsInParseNode(final ParseNode input,
        final Set<VarHyp> res)
    {
        final Stmt stmt = input.getStmt();
        if (stmt instanceof VarHyp)
            res.add((VarHyp)stmt);
        else
            for (final ParseNode child : input.getChild())
                findVarsInParseNode(child, res);
    }

    // Returns the only one variable or null
    private VarHyp findOneVarInParseNode(final ParseNode input) {
        final Set<VarHyp> res = new HashSet<VarHyp>();
        findVarsInParseNode(input, res);
        if (res.size() != 1)
            return null;
        else
            return res.iterator().next();
    }

    private void findImplClosureRules(final Assrt assrt) {
        final LogHyp[] logHyps = assrt.getLogHypArray();
        final ParseTree assrtTree = assrt.getExprParseTree();

        if (logHyps.length != 0)
            return;

        // Maybe depth restriction could be weaken
        if (assrtTree.getMaxDepth() != 4)
            return;

        final ParseNode root = assrtTree.getRoot();

        if (!implInfo.isImplOperator(root.getStmt()))
            return;

        final ParseNode mainRoot = root.getChild()[1];
        final ParseNode hypNodePart = root.getChild()[0];

        final List<ParseNode> hypNodes = conjInfo.separateByAnd(hypNodePart);

        final VarHyp[] hypToVarHypMap = new VarHyp[hypNodes.size()];

        final ParseNode firstHyp = hypNodes.get(0);
        final VarHyp firstVar = findOneVarInParseNode(firstHyp);

        hypToVarHypMap[0] = firstVar;

        if (firstVar == null)
            return;

        final ParseNode templNode = createTemplateNodeFromHypRoot(firstHyp,
            firstVar);
        if (templNode == null)
            return;

        for (int i = 1; i < hypNodes.size(); i++) {
            final ParseNode hyp = hypNodes.get(i);
            final VarHyp var = findOneVarInParseNode(hyp);
            if (var == null)
                return;
            hypToVarHypMap[i] = var;

            final ParseNode res = getCorrespondingNode(templNode, hyp);

            if (res == null)
                return;

            if (res.getStmt() != var)
                return;
        }

        final PropertyTemplate template = new PropertyTemplate(templNode);

        findClosureRulesCore(assrt, mainRoot, hypNodes.size(), template,
            hypToVarHypMap, implClosureRuleMap);
    }

    private void findClosureRulesCore(final Assrt assrt,
        final ParseNode mainRoot, final int hypNum,
        final PropertyTemplate template, final VarHyp[] hypToVarHypMap,
        final ClosureComplexRuleMap resMap)
    {
        final ParseNode res = getCorrespondingNode(template.node, mainRoot);
        if (res == null)
            return;

        final Stmt stmt = res.getStmt();

        final ParseNode[] children = res.getChild();

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

        output.dbgMessage(dbg,
            "I-TR-DBG transitive to result properties(%b): %s: %s",
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
                    cMap = new HashMap<ParseNodeHashElem, Assrt>();
                    constInfo.put(template, cMap);
                }

                final ParseNodeHashElem key = new ParseNodeHashElem(core);

                if (cMap.containsKey(key))
                    continue;

                cMap.put(key, assrt);

                output.dbgMessage(dbg,
                    "I-TR-DBG transitive rule for constant %s: %s: %s", core,
                    assrt, assrt.getFormula());
            }
        }
    }
    // ------------------------------------------------------------------------
    // ----------------------------Detection-----------------------------------
    // ------------------------------------------------------------------------

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
     * @return top-level closure statement ("|- ( sin ` A ) e. CC" in the
     *         example)
     */
    public ProofStepStmt closureProperty(final WorksheetInfo info,
        final PropertyTemplate template, final ParseNode node)
    {
        return closurePropertyCommon(info, template, node, false, true).step;
    }

    public GenProofStepStmt closurePropertyCommon(final WorksheetInfo info,
        final PropertyTemplate template, final ParseNode node,
        final boolean finishStatement, final boolean searchWithPrefix)
    {
        final ParseNode stepNode = template.subst(node);

        final int startCounter = debugCounter;
        debugCounter++;

        if (startCounter == 19)
            toString();

        if (finishStatement)
            if (info.hasImplPrefix())
                assert info.derivStep.formulaParseTree.getRoot().isDeepDup(
                    TrUtil.createBinaryNode(info.implStatement,
                        info.implPrefix, stepNode));
            else
                assert info.derivStep.formulaParseTree.getRoot().isDeepDup(
                    stepNode);

        final ProofStepStmt res1 = info.getProofStepStmt(stepNode);
        if (res1 != null)
            return new GenProofStepStmt(res1, null);

        if (searchWithPrefix && info.hasImplPrefix()) {
            final ParseNode implForm = info.applyImplPrefix(stepNode);
            final ProofStepStmt stmtWithImpl = info.getProofStepStmt(implForm);
            if (stmtWithImpl != null)
                return new GenProofStepStmt(stmtWithImpl, info.implPrefix);
        }

        if (hasClosurePropertyInternal(info, node, template, closureRuleMap,
            false))
        {
            final CreateClosureVisitor visitor = new CreateClosureVisitor() {
                public ClosureComplexRuleMap getMap() {
                    return closureRuleMap;
                }

                public GenProofStepStmt createClosureStep(
                    final GenProofStepStmt[] genHyps, final Assrt assrt)
                {
                    final ProofStepStmt[] hyps = new ProofStepStmt[genHyps.length];

                    for (int i = 0; i < genHyps.length; i++) {
                        assert genHyps[i].prefix == null;
                        hyps[i] = genHyps[i].step;
                    }

                    if (!finishStatement) {
                        if (info.hasImplPrefix()) {
                            final ProofStepStmt r = info
                                .getOrCreateProofStepStmt(stepNode, hyps, assrt);
                            implInfo.finishStubRule(info, r);
                            return new GenProofStepStmt(info.derivStep, null);
                        }
                        else {
                            final ProofStepStmt r = info
                                .getOrCreateProofStepStmt(stepNode, hyps, assrt);
                            return new GenProofStepStmt(r, null);
                        }
                    }
                    else {
                        info.finishDerivationStep(hyps, assrt);
                        return new GenProofStepStmt(info.derivStep, null);
                    }
                }
            };

            final GenProofStepStmt res = closureVisitorEntrance(info, template,
                node, visitor, false);

            assert res != null;
            return res;
        }
        else if (hasClosurePropertyInternal(info, node, template,
            implClosureRuleMap, searchWithPrefix))
        {
            final CreateClosureVisitor visitor = new CreateClosureVisitor() {
                public ClosureComplexRuleMap getMap() {
                    return implClosureRuleMap;
                }

                public GenProofStepStmt createClosureStep(
                    final GenProofStepStmt[] hyps, final Assrt assrt)
                {
                    final ParseNode assrtRoot = assrt.getExprParseTree()
                        .getRoot();
                    final ParseNode hypsPartPattern = assrtRoot.getChild()[0];
                    final ParseNode implRes = stepNode;

                    final GenProofStepStmt implHyp = conjInfo
                        .conctinateInTheSamePattern(hyps, hypsPartPattern, info);
                    if (!finishStatement) {
                        if (!implHyp.hasPrefix()) {
                            final ProofStepStmt r = implInfo
                                .applyImplicationRule(info, implHyp.step,
                                    implRes, assrt);
                            return new GenProofStepStmt(r, null);
                        }
                        else {
                            final ProofStepStmt r = implInfo
                                .applyTransitiveRule(info, implHyp.step,
                                    implRes, assrt);
                            return new GenProofStepStmt(r, implHyp.prefix);
                        }
                    }
                    else if (!implHyp.hasPrefix()) {
                        if (info.hasImplPrefix()) {
                            final ProofStepStmt r = implInfo
                                .applyImplicationRule(info, implHyp.step,
                                    implRes, assrt);
                            implInfo.finishStubRule(info, r);
                            return new GenProofStepStmt(info.derivStep, null);
                        }
                        else {
                            implInfo.finishWithImplication(info, implHyp.step,
                                implRes, assrt);
                            return new GenProofStepStmt(info.derivStep, null);
                        }
                    }
                    else {
                        implInfo.finishTransitiveRule(info, implHyp.step,
                            implRes, assrt);
                        return new GenProofStepStmt(info.derivStep,
                            implHyp.prefix);
                    }
                }
            };
            final GenProofStepStmt res = closureVisitorEntrance(info, template,
                node, visitor, searchWithPrefix);

            assert res != null;
            return res;
        }
        else {
            // We should not be here if we doesn't support constant property
            // auto completion
            assert supportConstants;

            // So it should be constant
            assert TrUtil.isConstNode(node) : "(-" + startCounter + ","
                + debugCounter + "-)";

            // TODO: DEBUG IT!!!!

            final Map<ParseNodeHashElem, Assrt> cMap = constInfo.get(template);

            assert cMap != null;

            final Assrt assrt = cMap.get(new ParseNodeHashElem(node));

            assert assrt != null;

            final ParseNode root = assrt.getExprParseTree().getRoot();

            final ProofStepStmt res = info.getOrCreateProofStepStmt(root,
                new ProofStepStmt[]{}, assrt);

            assert res != null;
            return new GenProofStepStmt(res, null);
        }
    }

    private interface CreateClosureVisitor {
        public ClosureComplexRuleMap getMap();
        public GenProofStepStmt createClosureStep(
            final GenProofStepStmt[] hyps, final Assrt assrt);
    }

    private GenProofStepStmt closureVisitorEntrance(final WorksheetInfo info,
        final PropertyTemplate template, final ParseNode node,
        final CreateClosureVisitor visitor, final boolean searchWithPrefix)
    {
        final GenProofStepStmt res = visitor.getMap().visitGenStmts(node, info,
            new ComplexRuleVisitor<Assrt, GenProofStepStmt>() {
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
                        final ParseNode child = node.getChild()[n];
                        assert child != null;

                        if (!hasClosureProperty(info, child, template,
                            searchWithPrefix))
                            return null;

                        final GenProofStepStmt childRes = closurePropertyCommon(
                            info, template, child, false, searchWithPrefix);
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

    private int debugCounter = 0;

    private boolean hasClosurePropertyInternal(final WorksheetInfo info,
        final ParseNode node, final PropertyTemplate template,
        final ClosureComplexRuleMap map, final boolean searchWithPrefix)
    {
        final Boolean res = map.visitGenStmts(node, info,
            new ComplexRuleVisitor<Assrt, Boolean>() {
                public Boolean visit(final ParseNode node,
                    final WorksheetInfo info, final ConstSubst constSubst,
                    final int[] varIndexes,
                    final Map<PropertyTemplate, Assrt> propertyMap)
                {
                    if (!propertyMap.containsKey(template))
                        return false;

                    final int prevDbg = debugCounter;

                    if (prevDbg == 7)
                        toString();

                    for (int i = 0; i < node.getChild().length; i++)
                        if (constSubst.constMap[i] == null) {
                            debugCounter++;
                            // Variable position
                            final ParseNode child = node.getChild()[i];
                            if (!hasClosureProperty(info, child, template,
                                searchWithPrefix))
                                return false;
                        }
                    debugCounter++;

                    output
                        .dbgMessage(
                            dbg,
                            "I-TR-DBG (-%d, %d-) Closure property confirmed (map %s): node %s has property %s, assertion %s",
                            prevDbg, debugCounter,
                            map == closureRuleMap ? "simple" : "implication",
                            info.trManager.getFormula(node), template,
                            propertyMap.get(template));

                    return true;
                }

                public Boolean failValue() {
                    return false;
                }
            });
        return res;
    }

    public boolean hasClosureProperty(final WorksheetInfo info,
        final ParseNode node, final PropertyTemplate template,
        final boolean searchWithPrefix)
    {
        final ParseNode substProp = template.subst(node);
        final ProofStepStmt stmt = info.getProofStepStmt(substProp);
        if (stmt != null)
            return true;

        if (searchWithPrefix && info.hasImplPrefix()) {
            final ParseNode implForm = info.applyImplPrefix(substProp);
            final ProofStepStmt stmtWithImpl = info.getProofStepStmt(implForm);
            if (stmtWithImpl != null)
                return true;
        }

        if (hasClosurePropertyInternal(info, node, template,
            implClosureRuleMap, searchWithPrefix))
            return true;

        if (hasClosurePropertyInternal(info, node, template, closureRuleMap,
            false))
            return true;

        if (supportConstants)
            if (TrUtil.isConstNode(node)) {
                final Map<ParseNodeHashElem, Assrt> cMap = constInfo
                    .get(template);
                if (cMap != null
                    && cMap.containsKey(new ParseNodeHashElem(node)))
                    return true;
            }

        return false;
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
            if (hasClosureProperty(info, core, template, true)) {
                closurePropertyCommon(info, template, core, true, true);
                return true;
            }
        }
        return false;
    }

    public boolean performClosureTransformation(final WorksheetInfo info) {
        final ParseNode root = info.derivStep.formulaParseTree.getRoot();

        if (performClosureTransformationInternal(info, root))
            return true;

        final ParseNode core = implInfo.extractPrefixAndGetImplPart(info);

        if (core != null)
            if (performClosureTransformationInternal(info, core))
                return true;

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
