// definitionCheck - Definition Checker for set.mm definitions
// Mario Carneiro, 14-Nov-2015
//
// Macro type: RunParm
//
// Arguments: Assrt labels, with * wildcard
// This option runs a soundness check on all axioms in the database, except
// those specified in the list. Recommended exclusions are
// ax-*,df-bi,df-clab,df-cleq,df-clel, which will always fail the check.
//
// Example invocation:
// RunMacro,definitionCheck,ax-*,df-bi,df-clab,df-cleq,df-clel

var imports = new JavaImporter(
	Packages.mmj.pa,
	Packages.mmj.lang,
	java.util.regex.Pattern,
	java.util);
with (imports) {
var ParseNodeArr = Java.type("mmj.lang.ParseNode[]");
var boolArr = Java.type("boolean[]");
var boolArrArr = Java.type("boolean[][]");
var provableLogicStmtTyp = grammar.getProvableLogicStmtTypArray()[0];
var boundVars = new HashMap();

var ERRMSG_PA_DEFINITION_FAIL = " Axiom %s has"
    + " failed the definitional soundness check.";

var ERRMSG_PA_DEFINITION_FAIL_1 = "I-PA-0201"
    + ERRMSG_PA_DEFINITION_FAIL + " The root symbol is not = or <->.";

var ERRMSG_PA_DEFINITION_FAIL_2 = "I-PA-0202"
    + ERRMSG_PA_DEFINITION_FAIL + " The previous axiom %s uses the symbol"
    + " being defined in this axiom.";

var ERRMSG_PA_DEFINITION_FAIL_3 = "I-PA-0203"
    + ERRMSG_PA_DEFINITION_FAIL + " Variables %s in the definiendum are"
    + " required NOT to be distinct.";

var ERRMSG_PA_DEFINITION_FAIL_4 = "I-PA-0204"
    + ERRMSG_PA_DEFINITION_FAIL + " All dummy variables in the definiens"
    + " are required to be distinct from each other and from variables in"
    + " the definiendum. The following DJ conditions need to be added:\n%s";

var ERRMSG_PA_DEFINITION_FAIL_5 = "I-PA-0205"
    + ERRMSG_PA_DEFINITION_FAIL + " Non-set dummy variable found, and no"
    + " justification theorem is available.";

var ERRMSG_PA_DEFINITION_FAIL_6 = "I-PA-0206"
    + ERRMSG_PA_DEFINITION_FAIL + " Dummy variables %s are possibly free"
    + " in the definiendum, and no justification is available.";

/**
 * Perform the definition check on the selected definition. Note that this
 * method is intrinsically tied to set.mm's definitions, and makes various
 * references to specific axioms like wceq, wb, and df-sbc, although it can
 * handle arbitrary new definitions under the basic structure of set.mm.
 *
 * @param axiom
 *            the definition to check
 * @return true if the test was passed
 */
function setMMDefinitionsCheck(axiom)
{
    var root = axiom.getExprParseTree().getRoot();
    var rootLabel = root.stmt.label;
    var devnull = new Messages(); // eat all bad proof errors
    var set = logicalSystem.getSymTbl().get("set");
    // Rule 1: New definitions must be introduced using = or <->
    if (!rootLabel.equals("wb") && !rootLabel.equals("wceq")) {
        messages.accumInfoMessage(ERRMSG_PA_DEFINITION_FAIL_1,
            axiom.label);
        return false;
    }

    var success = true;
    var defined = root.child[0].stmt;
    var startSeq = defined.getSeq();
    var endSeq = axiom.getSeq();
    // Rule 2: No axiom introduced before this one is allowed to use the
    // symbol being defined in this definition, and the definition is not
    // allowed to use itself (except once, in the definiendum)
    rule2: for each (var s in logicalSystem.stmtTbl.values())
        if (s instanceof Axiom && s.getSeq() > startSeq
            && s.getSeq() <= endSeq)
        {
        	var first = s == axiom;
            for each (var step in s.getExprRPN())
                if (step.stmt == defined)
                    if (first)
                        first = false;
                    else {
                        messages.accumInfoMessage(
                            ERRMSG_PA_DEFINITION_FAIL_2,
                            axiom.label, s.label);
                        success = false;
                        break rule2;
                    }
        }

    // Collect all variables on the left into parameters, and all
    // variables on the right but not on the left into dummies
    var parameters = new ArrayList();
    var dummies = new ArrayList();
    collectVariables(parameters, dummies, root.child[0]);
    collectVariables(dummies, parameters, root.child[1]);

    var frame = axiom.getMandFrame();
    var taken = new ArrayList();
    // Rule 3: Every variable in the definiens should not be distinct
    var badVars = new TreeSet(DjVars.DV_ORDER);
    for each (var v1 in parameters) {
        taken.add(v1);
        for each (var v2 in parameters)
            if (v1 != v2
                && ScopeFrame.isVarPairInDjArray(frame, v1.getVar(),
                    v2.getVar()))
            {
                badVars.add(v1.getVar());
                badVars.add(v2.getVar());
            }
    }
    if (!badVars.isEmpty()) {
        success = false;
        messages.accumInfoMessage(ERRMSG_PA_DEFINITION_FAIL_3,
            axiom.label, badVars);
    }

    // Rule 4: Every dummy variable in the definiendum should be distinct
    badVars.clear();
    for each (var v1 in dummies) {
        v1.accumVarHypListBySeq(taken);
        for each (var v2 in dummies)
            if (v1 != v2
                && !ScopeFrame.isVarPairInDjArray(frame, v1.getVar(),
                    v2.getVar()))
            {
                badVars.add(v1.getVar());
                badVars.add(v2.getVar());
            }
    }

    var group = new TreeSet(DjVars.DV_ORDER);
    var field = "";
    for each (var v1 in parameters) {
        group.clear();
        for each (var v2 in dummies)
            if (!ScopeFrame.isVarPairInDjArray(frame, v1.getVar(),
                v2.getVar()))
            {
                if (group.isEmpty()) {
                    group.addAll(badVars);
                    group.add(v1.getVar());
                }
                group.add(v2.getVar());
            }
        if (!group.isEmpty()) {
            field += "  " + PaConstants.DISTINCT_VARIABLES_STMT_TOKEN + " ";
            for each (var v in group)
                field += v + " ";
            field += PaConstants.END_PROOF_STMT_TOKEN;
        }
    }
    if (group.isEmpty() && !badVars.isEmpty()) {
        field += "  " + PaConstants.DISTINCT_VARIABLES_STMT_TOKEN + " ";
        for each (var v in badVars)
            field += v + " ";
        field += PaConstants.END_PROOF_STMT_TOKEN;
    }
    if (field.length() != 0) {
        messages.accumInfoMessage(ERRMSG_PA_DEFINITION_FAIL_4,
            axiom.label, field);
        success = false;
    }

    // If there are no dummy variables, no further processing is needed -
    // the test is passed
    if (dummies.isEmpty())
        return success;

    // Generate a 'justification' theorem and see if it unifies with
    // something in the database
    var newRoot = new ParseNode(root.stmt);
    var assignments = new HashMap();
    var w = new ProofWorksheet("dummy",
        proofAsstPreferences, logicalSystem, grammar, devnull);
    w.loadComboFrameAndVarMap();
    for each (var d in dummies)
        assignments.put(d,
            new ParseNode(getUnusedDummyVar(w, taken, d.getTyp())));
    newRoot.child = [root.child[1],
            reassignVariables(assignments, root.child[1])];
    if (justify(w, newRoot))
        return success;

    // Okay, we couldn't directly find a justification theorem. Most later
    // definitions will fall into this category. Our new approach will be
    // to prove that each dummy is not free in the expression, that is,
    // that we can prove ( ph -> A. x ph ) for each dummy variable x.

    // we need this for showing not-free for class terms
    var dummy = new ParseNode(logicalSystem.stmtTbl.get(
        "cv"));
    dummy.child = [new ParseNode(getUnusedDummyVar(w, taken, set))];

    badVars.clear();
    for each (var v in dummies) {
        // Rule 5: every dummy variable should be a set variable,
        // unless there is a justification theorem
        if (v.getTyp() != set) {
            messages.accumInfoMessage(
                ERRMSG_PA_DEFINITION_FAIL_5, axiom.label);
            return false;
        }

        // Rule 6: every dummy variable must be bound
        if (!proveBoundVar(w, boundVars, new ParseNode(v), dummy,
            root.child[1], true)
            && !proveBoundVar(w, boundVars, new ParseNode(v), dummy,
                root.child[1], false))
            badVars.add(v.getVar());
    }
    if (!badVars.isEmpty()) {
        messages.accumInfoMessage(ERRMSG_PA_DEFINITION_FAIL_6,
            axiom.label, badVars);
        success = false;
    }
    return success;
}

/**
 * Add an entry to the boundVars table for this definition. The boundVars table
 * is a map from each definition to an array of arrays of booleans; the first
 * index specifies the index of a set variable (the other indexes are null),
 * while the second index refers to the index of another variable in the
 * definition, which is true if occurrences of the set variable in the other
 * variable are to be considered bound. For example, for df-sum
 * {@code sum_ x e. A B}, there are three variables, and occurrences of
 * {@code x} are bound in {@code B} but not in {@code A}; thus the boundVars
 * table entry for df-sum would be <code>{{true, false, true}, null,
 * null}</code>.
 * 
 * @param axiom
 *            the definition
 */
function labelBoundVars(axiom)
{
	var w = null;
	var dummy = null;
	var set = logicalSystem.getSymTbl().get("set");
	var root = axiom.getExprParseTree().getRoot();
	var defn = root.child[0].child;
    if (boundVars.get(root.child[0].stmt) != null)
        return;
    var val = new boolArrArr(defn.length);
    for (var i = 0; i < defn.length; i++)
        if (defn[i].stmt.getTyp() == set) {
            if (w == null) {
                w = new ProofWorksheet("dummy", proofAsstPreferences,
                    logicalSystem, grammar, new Messages());
                w.loadComboFrameAndVarMap();
                var taken = new ArrayList();
                collectVariables(taken, Collections.emptyList(),
                    root.child[1]);

                dummy = boxToType(
                    new ParseNode(getUnusedDummyVar(w, taken, set)), null,
                    "class");
            }
            val[i] = new boolArr(defn.length);
            var assignments = new HashMap();
            for (var j = 0; j < defn.length; j++) {
                if (!(defn[j].stmt instanceof VarHyp))
                    return; // this definition is too complicated for us
                if (i == j) {
                    val[i][j] = true;
                    continue;
                }
                assignments.clear();
                assignments.put(
                    defn[j].stmt,
                    boxToType(defn[i], boxToType(defn[i], null, "class"),
                        defn[j].stmt.getTyp().getId()));
                val[i][j] = proveBoundVar(w, boundVars, defn[i], dummy,
                	reassignVariables(assignments, root.child[1]), true);
            }
        }
    boundVars.put(root.child[0].stmt, val);
}

function boxToType(node, dummy, goalType)
{
    if (goalType.equals("set"))
        return node;
    var e1 = node;
    if (node.stmt.getTyp().getId().equals("set")) {
        e1 = new ParseNode(logicalSystem.stmtTbl.get("cv"));
        e1.child = [node];
    }
    if (goalType.equals("class"))
        return e1;
    var e2 = e1;
    if (e1.stmt.getTyp().getId().equals("class")) {
        e2 = new ParseNode(logicalSystem.stmtTbl.get("wcel"));
        e2.child = [dummy, e1];
    }
    return e2;
}

function getUnusedDummyVar(w, taken, typ)
{
    for each (var h in w.comboFrame.hypArray)
        if (h instanceof VarHyp && h.getTyp() == typ) {
            if (!h.containedInVarHypListBySeq(taken)) {
                h.accumVarHypListBySeq(taken);
                return h;
            }
        }
    return null;
}

function isBound(w, v, dummy, root)
{
	var expr = boxToType(root, dummy, "wff");
	var wal = new ParseNode(logicalSystem.stmtTbl.get("wal"));
    wal.child = [expr, v];
    var wi = new ParseNode(logicalSystem.stmtTbl.get("wi"));
    wi.child = [expr, wal];
    return justify(w, wi);
}

function proveBoundVar(w, boundVars, v, dummy, root, fast)
{
	var bound = new boolArr(root.child.length);
	var bound2 = new boolArr(bound.length);
	var allBound = true;
    for (var i = 0; i < root.child.length; i++)
        allBound &= bound[i] = bound2[i] = proveBoundVar(w, boundVars,
            v, dummy, root.child[i], fast);
    if (allBound)
        return v.stmt != root.stmt;

    var val = boundVars.get(root.stmt);
    if (val == null) {
        if (!fast)
            return isBound(w, v, dummy, root);

        var proto = root.stmt.getExprParseTree().getRoot();
        val = new boolArrArr(proto.child.length);
        for (var i = 0; i < val.length; i++) {
            if (!proto.child[i].stmt.getTyp().getId().equals("set"))
                continue;

            val[i] = new boolArr(val.length);
            var assignments = new HashMap();
            for (var j = 0; j < val.length; j++)
                if (proto.child[j].stmt instanceof VarHyp) {
                    assignments.clear();
                    assignments.put(
                        proto.child[j].stmt,
                        boxToType(proto.child[i],
                            boxToType(proto.child[i], null, "class"),
                            proto.child[j].stmt.getTyp().getId()));
                    val[i][j] = proveBoundVar(w, boundVars, proto.child[i],
                        dummy, reassignVariables(assignments, proto), false);
                }
        }
        boundVars.put(root.stmt, val);
    }

    for (var i = 0; i < val.length; i++)
        if (val[i] != null && !bound[i])
            for (var j = 0; j < val.length; j++)
                bound2[j] |= val[i][j];

    for (var i = 0; i < val.length; i++)
        if (!bound2[i])
            return !fast && isBound(w, v, dummy, root);
    return true;
}

/**
 * Generate a 'dummy' proof worksheet containing the given expression, and
 * return true if the resulting theorem was successfully proven.
 * 
 * @param w
 *            The ProofWorksheet in which to work
 * @param theorem
 *            the expression to prove
 * @return true if a proof was found
 */
function justify(w, theorem) {
	var tree = new ParseTree(theorem);
	var f = verifyProofs.convertRPNToFormula(tree.convertToRPN(),
        null);
    f.setTyp(provableLogicStmtTyp);
    w.proofWorkStmtList.clear();
    w.proofWorkStmtList.add(w.qedStep = new DerivationStep(w,
        PaConstants.QED_STEP_NBR, [], [], "",
        f, tree, false, false, true, null));
    proofAsst.proofUnifier.unifyAllProofDerivationSteps(w, w.messages, true);
    return w.qedStep.getProofTree() != null
        && w.qedStep.djVarsErrorStatus != PaConstants.DjVarsErrorStatus.Hard;
}

function collectVariables(vars, exclusions, root)
{
    if (root.stmt instanceof VarHyp) {
        if (!root.stmt.containedInVarHypListBySeq(exclusions))
        	root.stmt.accumVarHypListBySeq(vars);
    }
    else
        for each (var child in root.child)
            collectVariables(vars, exclusions, child);
}

function reassignVariables(assignments, root)
{
	var newRoot = null;
    if (root.stmt instanceof VarHyp)
        newRoot = assignments.get(root.stmt);
    if (newRoot == null) {
        newRoot = new ParseNode(root.stmt);
        var child = new ParseNodeArr(root.child.length);
        for (var i = 0; i < root.child.length; i++)
            child[i] = reassignVariables(assignments, root.child[i]);
        newRoot.child = child;
    }
    return newRoot;
}


var exclusions = new Array(args.length-1);
for (var i = 1; i < args.length; i++) {
    exclusions[i-1] = Pattern.compile(Pattern.quote(
        args[i].trim()).replace("*", "\\E.*\\Q"));
}
var definitions = new TreeSet(MObj.SEQ);
bigLoop: for each (var s in logicalSystem.stmtTbl.values()) {
    if (s instanceof Axiom && s.getTyp() == provableLogicStmtTyp) {
        for each (var p in exclusions) {
            if (p.matcher(s.label).matches())
            	continue bigLoop;
        }
        definitions.add(s);
    }
}
for each (var s in definitions) {
    if (setMMDefinitionsCheck(s, messages))
        labelBoundVars(s);
    messages.printAndClearMessages();
}

} // with(imports)
