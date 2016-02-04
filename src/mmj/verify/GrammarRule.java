//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * GrammarRule.java  0.03 09/25/2005
 *
 * Sep-25-2005: - Change E-GR-0044 to reflect fact that the
 *                duplicate grammar rule is *derived*, and to
 *                show the actual rules involved!
 */

package mmj.verify;

import java.util.Comparator;

import mmj.lang.*;

/**
 * GrammarRule is the superclass of NotationRule, NullsPermittedRule and
 * TypeConversionRule.
 * <p>
 * Whew!!! The thing is a pantsload. I get tired just thinking about all this
 * stuff.
 *
 * @see <a href="../../CreatingGrammarRulesFromSyntaxAxioms.html">
 *      CreatingGrammarRulesFromSyntaxAxioms.html</a>
 * @see <a href="../../ConsolidatedListOfGrammarValidations.html">
 *      ConsolidatedListOfGrammarValidations.html</a>
 * @see <a href="../../BasicsOfSyntaxAxiomsAndTypes.html">
 *      BasicsOfSyntaxAxiomsAndTypes.html</a>
 * @see <a href="../../EssentialAmbiguityExamples.html">
 *      EssentialAmbiguityExamples.html</a>
 * @see <a href="../../GrammarRuleTreeNotes.html"> GrammarRuleTreeNotes.html</a>
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public abstract class GrammarRule implements Comparable<GrammarRule> {

    /**
     * ruleNbr uniquely identifies a GrammarRule.
     * <p>
     * Rule Numbers are sssigned in sequence of rule creation, which corresponds
     * to precedence -- that is, Metamath database sequence and how the Grammar
     * Rules are generated.
     *
     * @see mmj.verify.Grammar#assignNextGrammarRuleNbr()
     */
    protected int ruleNbr;

    /**
     * paramTransformationTree is a ParseTree that is "splice onto" a set of
     * Variable Hypotheses (if any) to produce a new sub-tree when a GrammarRule
     * is matched to a sub-sequence of an expression.
     * <p>
     * A GrammarRule may involve multiple Syntax Axioms including Type
     * Conversions and Nulls Permitted Syntax Axioms. This means that a
     * GrammarRule whose "base" Syntax Axiom has multiple variable hypotheses
     * may, in practice require only a single variable hypothesis -- or none at
     * all, due to Nulls Permitteds.
     * <p>
     * Note: {@code (Axiom)paramTransformationTree.getRoot().getStmt()} ==
     * GrammarRule "baseSyntaxAxiom". Therefore, we are not storing the
     * baseSyntaxAxiom redundantly as a separate element.
     */
    protected ParseTree paramTransformationTree;

    /**
     * paramVarHypNode -- as unbelievable as this sounds -- has object
     * references to the VarHyp ParseNode's in paramTransformationTree, thus
     * eliminating most searches.
     * <p>
     * The paramVarHypNode array has the same size as GrammarRule's base syntax
     * axiom's var hyp array, but may contain null entries since a GrammarRule
     * may require fewer parameters due to Nulls Permitted rule(s).
     */
    protected ParseNode[] paramVarHypNode;

    /**
     * maxSeqNbr is the greatest MObj(dot)seq of any Stmt in
     * paramTransformationTree, and represents precedence since a statement's
     * ParseTree should not refer to any Stmt's with sequence numbers higher
     * than the Stmt's own sequence number.
     */
    protected int maxSeqNbr;

    /**
     * nbrHypParamsUsed == the number of hypotheses actually in use in the
     * paramTransformationTree for the GrammarRule.
     * <p>
     * nbrHypParamsUsed may be less than the number of variable hypotheses for
     * the base Syntax Axiom because the GrammarRule may employ nulls to
     * "flesh out" the hypotheses for the base Syntax Axiom. Also, the number
     * may be zero; this is the case for Constant Syntax Axioms and for Constant
     * type Grammar Rules derived from Syntax Axioms where nulls are permitted.
     */
    protected int nbrHypParamsUsed;

    /**
     * ruleFormatExpr contains the GrammarRule's Expression rewritten where each
     * Var's VarHyp's Type Code replaces the Var or VarHyp (ie "( wff -> wff )"
     * ).
     * <p>
     * This was a late addition to GrammarRule caused by the need of
     * EarleyParser's Predictor and Lookahead to know the first symbol of a
     * GrammarRule. To save further searching, perhaps expensive marches through
     * the Grammar Rule Forest, I decided to add the entire ruleFormatExpr here.
     * Given that the entire expression is now stored in GrammarRule and that
     * there are cheaper ways to find duplicates than traipsing through the
     * Grammar Rule Forest -- and EarleyParser does not require the Forest for
     * its work -- there are few impediments to eliminating the Forest
     * altogether (just need to rework mmj.util.Dump a bit, I think.)
     * <p>
     */
    protected Cnst[] ruleFormatExpr;

    /**
     * ruleHypPos[i] is an index of a VarHyp's position within ruleFormatExpr
     * (helpful in EarleyParser).
     * <p>
     * Array length = nbrHypParamsUsed and is sequenced by hyp position within
     * the ruleFormatExpr.
     */
    protected int[] ruleHypPos;

    /**
     * isBaseRule indicates that the GrammarRule is derived directly from the
     * baseSyntaxAxiom, and is unaffected by other NullsPermitted or
     * TypeConversion GrammarRules.
     * <p>
     * It is here for the eventuality that we'll use an EarleyParser that
     * handles its own NullsPermitted and TypeConversion rules (otherwise, it
     * can say adios).
     */
    protected boolean isBaseRule;

    /**
     * Add rule format expression to the Rule Forest.
     *
     * @param grammar The Grammar object (Mr Big).
     * @param ruleFormatExpr the expression to add.
     */
    public abstract void addToGrammar(Grammar grammar, Cnst[] ruleFormatExpr);

    /**
     * deriveAdditionalRules based on the addition of a new GrammarRule to those
     * already generated and accepted.
     * <p>
     * A PriorityQueue is used to store new derivedGrammarRules awaiting
     * processing (dup checking, parse checking and possible addition to the
     * Grammar data.) The Comparator orders derived rules based on MaxSeqNbr and
     * ruleNbr so that low Metamath sequence number statements are added to the
     * Grammar before higher sequenced statements. The existing rules are stored
     * in separate repositories, which are scanned, as needed, when a new rule
     * is added; the order that the repositories are scanned is irrelevant
     * because the PriorityQueue controls update processing sequence.
     * <p>
     * Note also that a newly derived rule may generate other derived rules,
     * which in turn trigger other derivations; this is a "feature" (as well as
     * a scary opportunity for an infinite loop!) The benefit is that each pair
     * of rules may be considered separately; when processing the pair there is
     * no need to worry about other possible Type Conversion and Null Permitted
     * Rules. For example, an "A" Type hypothesis in a Notation Rule may be the
     * target of a "B to A" Type Conversion, but the fact that there is a
     * "C to B" or "C to A" conversion can be ignored -- if the "B to A"
     * conversion generates a variant rule of the original rule, then when
     * *that* rule comes back through the PriorityQueue, the "C to B" rule will
     * automatically come into play. This also means that a "B to A" conversion
     * will combine with a "C to B" conversion to generate a "C to A" conversion
     * -- eventually.
     *
     * @param grammar The Grammar.
     */
    public abstract void deriveAdditionalRules(Grammar grammar);

    /**
     * Return a duplicate of the input ruleFormatExpr if it exists, or return
     * null.
     *
     * @param grammar The Grammar.
     * @param ruleFormatExpr the expression to add.
     * @return GrammarRule if duplicate found, or null.
     */
    public abstract GrammarRule getDupRule(Grammar grammar,
        Cnst[] ruleFormatExpr);

    /**
     * Return the GrammarRule's ruleFormatExpr from the Rule Forest.
     * <p>
     * This is pretty obsolete now that ruleFormatExpr is stored in GrammarRule,
     * but it is still used in Dump (for now).
     *
     * @return ruleFormatExpr GrammarRule Expression in rule format.
     */
    public abstract Cnst[] getForestRuleExpr();

    /**
     * Default constructor.
     */
    public GrammarRule() {}

    /**
     * Constructor -- create skeleton GrammarRule w/ruleNbr.
     *
     * @param grammar The Grammar.
     */
    public GrammarRule(final Grammar grammar) {

        ruleNbr = grammar.assignNextGrammarRuleNbr();
    }

    /**
     * Constructor -- default GrammarRule for base Syntax Axioms, which means no
     * parameter "transformations".
     *
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     */
    public GrammarRule(final Grammar grammar, final Axiom baseSyntaxAxiom) {

        ruleNbr = grammar.assignNextGrammarRuleNbr();
        maxSeqNbr = baseSyntaxAxiom.getSeq();
        final VarHyp[] varHypArray = baseSyntaxAxiom.getMandVarHypArray();
        nbrHypParamsUsed = varHypArray.length;

        final ParseNode paramTransformationRoot = new ParseNode(
            baseSyntaxAxiom);

        paramTransformationTree = new ParseTree(paramTransformationRoot);

        final ParseNode[] child = new ParseNode[nbrHypParamsUsed];
        paramTransformationRoot.child = child;

        paramVarHypNode = new ParseNode[nbrHypParamsUsed];

        for (int i = 0; i < nbrHypParamsUsed; i++) {
            child[i] = new ParseNode(varHypArray[i]);
            paramVarHypNode[i] = child[i];

        }
    }

    /**
     * RULE_NBR sequences by GrammarRule.ruleNbr
     */
    public static final Comparator<GrammarRule> RULE_NBR = new Comparator<GrammarRule>() {
        public int compare(final GrammarRule o1, final GrammarRule o2) {
            return o1.ruleNbr - o2.ruleNbr;
        }
    };

    /**
     * MAX_SEQ_NBR sequences by GrammarRule.maxSeqNbr & ruleNbr
     */
    public static final Comparator<GrammarRule> MAX_SEQ_NBR = new Comparator<GrammarRule>() {
        public int compare(final GrammarRule o1, final GrammarRule o2) {
            int c = o1.maxSeqNbr - o2.maxSeqNbr;
            if (c == 0)
                c = o1.ruleNbr - o2.ruleNbr;
            return c;
        }
    };

    /**
     * Adds a Syntax Axiom's "base" grammar rule plus all derived grammar rules
     * to the Grammar.
     * <p>
     * This is a major function in GrammarRule. Hairy.
     * <p>
     * Checks for Type Conversion "loop" on Syntax Axiom base Grammar Rule,
     *
     * @param baseSyntaxAxiom Syntax Axiom.
     * @param grammar The Grammar.
     * @return true if rule added successfully, else false.
     */
    public static boolean add(final Axiom baseSyntaxAxiom,
        final Grammar grammar)
    {

        boolean errorsFound = false;

        final ParseNodeHolder[] parseNodeHolderExpr = baseSyntaxAxiom
            .getFormula()
            .getParseNodeHolderExpr(baseSyntaxAxiom.getMandVarHypArray());

        GrammarRule baseRule;
        if ((baseRule = GrammarRule.buildBaseRule(grammar, baseSyntaxAxiom,
            parseNodeHolderExpr)) == null)
            return false;

        Cnst[] rfe = ParseNodeHolder.buildRuleFormatExpr(parseNodeHolderExpr);

        GrammarRule dupRule;
        final Messages messages = grammar.getMessages();
        if ((dupRule = baseRule.getDupRule(grammar, rfe)) != null) {
            messages.accumMessage(GrammarConstants.ERRMSG_BASE_RULE_IS_DUP,
                baseSyntaxAxiom.getLabel(),
                dupRule.getBaseSyntaxAxiom().getLabel());
            return false;
        }

        baseRule.addToGrammar(grammar, rfe);
// we're not implementing this unless absolutely necessary for
// EarleyParser
//      grammar.baseGRSetAdd(baseRule);

        baseRule.deriveAdditionalRules(grammar);

        GrammarRule derivedRule;
        while ((derivedRule = grammar.derivedRuleQueueRead()) != null) {

            rfe = derivedRule.buildRuleFormatExpr();

            /**
             * if derived rule redundant, just ignore it and continue, but if
             * the duplicate rule has a different type code then set
             * "errors found" to indicate an error, and then continue...
             */
            dupRule = derivedRule.getDupRule(grammar, rfe);
            if (dupRule != null) {
                if (dupRule.getBaseSyntaxAxiom().getTyp() != derivedRule
                    .getBaseSyntaxAxiom().getTyp())
                {
                    errorsFound = true;
                    messages.accumMessage(
                        // mod Sep-23-2005 chg msg text
                        GrammarConstants.ERRMSG_DUP_RULE_DIFF_TYP,
                        derivedRule.getBaseSyntaxAxiom().getTyp(),
                        GrammarRule.showRuleFormatExprAsString(rfe),
                        dupRule.getBaseSyntaxAxiom().getTyp(),
                        derivedRule.getBaseSyntaxAxiom(),
                        dupRule.getBaseSyntaxAxiom());

                }
                continue;
            }

            derivedRule.addToGrammar(grammar, rfe);
            derivedRule.deriveAdditionalRules(grammar);
        }
        return !errorsFound;
    }

    private static GrammarRule buildBaseRule(final Grammar grammar,
        final Axiom syntaxAxiom, final ParseNodeHolder[] parseNodeHolderExpr)
    {

        GrammarRule grammarRule;

        if (parseNodeHolderExpr.length == 0)
            grammarRule = NullsPermittedRule.buildBaseRule(grammar,
                syntaxAxiom);
        else if (parseNodeHolderExpr.length == 1
            && !(parseNodeHolderExpr[0].mObj instanceof Cnst))
            grammarRule = TypeConversionRule.buildBaseRule(grammar, syntaxAxiom,
                parseNodeHolderExpr);
        else
            grammarRule = NotationRule.buildBaseRule(grammar, syntaxAxiom);

        return grammarRule;
    }

    /**
     * Computes hashcode for this GrammarRule.
     *
     * @return hashcode for the GrammarRule
     */
    @Override
    public int hashCode() {
        return ruleNbr;
    }

    /**
     * Compares GrammarRule object based on the seq.
     *
     * @param obj GrammarRule object to compare to this GrammarRule
     * @return returns negative, zero, or a positive int if this GrammarRule
     *         object is less than, equal to or greater than the input parameter
     *         obj.
     */
    public int compareTo(final GrammarRule obj) {
        return ruleNbr - obj.ruleNbr;
    }

    /**
     * Compare for equality with another GrammarRule.
     * <p>
     * Equal if and only if the GrammarRule sequence numbers are equal. and the
     * obj to be compared to this object is not null and is a GrammarRule as
     * well.
     *
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof GrammarRule
            && ruleNbr == ((GrammarRule)obj).ruleNbr;
    }

    @Override
    public String toString() {
        String s = getGrammarRuleTyp() + " ->";
        for (final Cnst element : ruleFormatExpr)
            s += " " + element;
        return s;
    }

    /**
     * Return GrammarRule's ruleNbr.
     *
     * @return GrammarRule's ruleNbr.
     */
    public int getRuleNbr() {
        return ruleNbr;
    }

    /**
     * Return GrammarRule's paramTransformationTree.
     *
     * @return GrammarRule's paramTransformationTree.
     */
    public ParseTree getParamTransformationTree() {
        return paramTransformationTree;
    }

    /**
     * Return GrammarRule's paramVarHypNode array.
     *
     * @return GrammarRule's paramVarHypNode array.
     */
    public ParseNode[] getParamVarHypNode() {
        return paramVarHypNode;
    }

    /**
     * Return GrammarRule's maxSeqNbr.
     *
     * @return GrammarRule's maxSeqNbr.
     */
    public int getMaxSeqNbr() {
        return maxSeqNbr;
    }

    /**
     * Set GrammarRule's maxSeqNbr.
     *
     * @param maxSeqNbr GrammarRule's maxSeqNbr.
     */
    public void setMaxSeqNbr(final int maxSeqNbr) {
        this.maxSeqNbr = maxSeqNbr;
    }

    /**
     * Return GrammarRule's nbrHypParamsUsed.
     *
     * @return GrammarRule's nbrHypParamsUsed.
     */
    public int getNbrHypParamsUsed() {
        return nbrHypParamsUsed;
    }

    /**
     * Set GrammarRule's nbrHypParamsUsed.
     *
     * @param nbrHypParamsUsed GrammarRule's nbrHypParamsUsed.
     */
    public void setNbrHypParamsUsed(final int nbrHypParamsUsed) {
        this.nbrHypParamsUsed = nbrHypParamsUsed;
    }

    /**
     * Return GrammarRule's "base" Syntax Axiom.
     *
     * @return GrammarRule's "base" Syntax Axiom.
     */
    public Axiom getBaseSyntaxAxiom() {
        return (Axiom)paramTransformationTree.getRoot().stmt;
    }

    /**
     * Return GrammarRule's "base" Syntax Axiom's varHypReseq array.
     *
     * @return GrammarRule's "base" Syntax Axiom's varHypReseq array.
     */
    public int[] getSyntaxAxiomVarHypReseq() {
        return getBaseSyntaxAxiom().getSyntaxAxiomVarHypReseq();
    }

    /**
     * Return GrammarRule's Type Code.
     *
     * @return GrammarRule's Type Code.
     */
    public Cnst getGrammarRuleTyp() {
        return getBaseSyntaxAxiom().getTyp();
    }

    /**
     * Return GrammarRule's ruleHypPos array.
     *
     * @return GrammarRule's ruleHypPos array.
     */
    public int[] getRuleHypPos() {
        return ruleHypPos;
    }

    /**
     * Return GrammarRule's ruleFormatExpr.
     *
     * @return GrammarRule's ruleFormatExpr.
     */
    public Cnst[] getRuleFormatExpr() {
        return ruleFormatExpr;
    }

    /**
     * Set GrammarRule's ruleFormatExpr.
     *
     * @param rfe GrammarRule's ruleFormatExpr.
     */
    public void setRuleFormatExpr(final Cnst[] rfe) {
        ruleFormatExpr = rfe;
        ruleHypPos = new int[nbrHypParamsUsed];
        int j = 0;
        for (int i = 0; i < ruleFormatExpr.length; i++)
            if (ruleFormatExpr[i].isVarTyp())
                ruleHypPos[j++] = i;
    }

    /**
     * Return GrammarRule's ruleFormatExpr as String of Stmt labels.
     *
     * @return GrammarRule's ruleFormatExpr as String.
     */
    public String getRuleFormatExprAsString() {
        return GrammarRule.showRuleFormatExprAsString(ruleFormatExpr);
    }

    /**
     * Return a GrammarRule's ruleFormatExpr as String of Stmt labels.
     *
     * @param rfe ruleFormatExpr.
     * @return GrammarRule's ruleFormatExpr as String.
     */
    public static String showRuleFormatExprAsString(final Cnst[] rfe) {
        final StringBuilder s = new StringBuilder(rfe.length * 4);
        for (final Cnst element : rfe) {
            s.append(element);
            s.append(" ");
        }
        return s.toString();
    }

    /**
     * Return the first symbol of a GrammarRule's ruleFormatExpr.
     *
     * @return first symbol of a GrammarRule's ruleFormatExpr.
     */
    public Cnst getRuleFormatExprFirst() {
        if (ruleFormatExpr.length > 0)
            return ruleFormatExpr[0];
        else
            return null;
    }

    /**
     * Return the "i-th" symbol of a GrammarRule's ruleFormatExpr.
     *
     * @param i the index
     * @return i-th symbol of a GrammarRule's ruleFormatExpr or null of i is
     *         beyond the end of the expression.
     */
    public Cnst getRuleFormatExprIthSymbol(final int i) {
        if (i > ruleFormatExpr.length)
            return null;
        else
            return ruleFormatExpr[i - 1];
    }

    /**
     * Return GrammarRule's isBaseRule flag.
     *
     * @return GrammarRule's isBaseRule flag.
     */
    public boolean getIsBaseRule() {
        return isBaseRule;
    }

    /**
     * Set GrammarRule's isBaseRule flag.
     *
     * @param isBaseRule flag.
     */
    public void setIsBaseRule(final boolean isBaseRule) {
        this.isBaseRule = isBaseRule;
    }

    /**
     * return parseNodeHolderExpr version of the GrammarRule's expression.
     * <p>
     * The root of a GrammarRule paramTransformationTree is always the
     * baseSyntaxAxiom. Since no Syntax Axiom may be a composite function that
     * is parseable into other Syntax Axiom rules, the only possible
     * substitution into a *grammar rule* variable is 1) another variable via a
     * Type Conversion Rule or 2) null, via a Nulls Permitted Rule. And, since
     * GrammarRule contains the paramVarHypNode array we can directly write out
     * the resulting formula and/or ruleFormatExpr and/or ParseNodeHolderExpr --
     * an element of paramVarHypNode == null *must be* a null substitution, by
     * virtue of the above considerations.
     *
     * @return parseNodeHolder format expression.
     */
    public ParseNodeHolder[] getParseNodeHolderExpr() {
        final Formula f = paramTransformationTree.getRoot().convertToFormula();
        final ParseNodeHolder[] parseNodeHolderExpr = new ParseNodeHolder[f
            .getCnt() - 1];
        for (int i = 0; i < parseNodeHolderExpr.length; i++) {
            final Sym s = f.getSym()[i + 1];
            parseNodeHolderExpr[i] = s instanceof Cnst
                ? new ParseNodeHolder((Cnst)s)
                : new ParseNodeHolder(((Var)s).getActiveVarHyp());
        }
        return parseNodeHolderExpr;
    }

    /**
     * Build ruleFormatExpr version of the GrammarRule's expression.
     * <p>
     * This is a clone of GrammarRule.getParseNodeHolderExpr() with a different
     * output format, essentially.
     * <p>
     * The root of a GrammarRule paramTransformationTree is always the
     * baseSyntaxAxiom. Since no Syntax Axiom may be a composite function that
     * is parseable into other Syntax Axiom rules, the only possible
     * substitution into a *grammar rule* variable is 1) another variable via a
     * Type Conversion Rule or 2) null, via a Nulls Permitted Rule. And, since
     * GrammarRule contains the paramVarHypNode array we can directly write out
     * the resulting formula and/or ruleFormatExpr and/or ParseNodeHolderExpr --
     * an element of paramVarHypNode == null *must be* a null substitution, by
     * virtue of the above considerations.
     *
     * @return ruleFormatExpr expression.
     */
    public Cnst[] buildRuleFormatExpr() {
        final Formula f = paramTransformationTree.getRoot().convertToFormula();
        final Cnst[] rfe = new Cnst[f.getCnt() - 1];
        for (int i = 0; i < rfe.length; i++) {
            final Sym s = f.getSym()[i + 1];
            rfe[i] = s instanceof Cnst ? (Cnst)s
                : ((Var)s).getActiveVarHyp().getTyp();
        }
        return rfe;
    }

    /**
     * Builds a grammatical parse node from the GrammarRule using an input array
     * of parameters to substitute into the rule's variable hypotheses.
     * <p>
     * This is a rude dog.
     * <p>
     * buildGrammaticalParseNode -- pads out the parameter list as needed (for
     * nulls in the rule) and resequences the parameters from the symbol
     * sequence order to the database sequence order used by Metamath for
     * statement hypotheses. Then the parameters are substituted into the
     * GrammarRule's parameterTransformationTree, which is basically a model
     * parse sub-tree for the GrammarRule, requiring only replacement of the
     * GrammarRule's variable hypotheses with the input parameters. Note that
     * the variable hypothesis nodes for a grammar rule are at the bottom of the
     * tree (true for all parse trees), and this means that we are splicing in
     * the parameters at the bottom, building up a larger tree that contains the
     * grammar rule at the top of the tree.
     * <p>
     * NOTE: Originally, the GrammarRule design didn't call for the parameter
     * transformation data to be stored in a parse tree. But, in highly unusual
     * grammars using Nulls Permitted, a Syntax Axiom with two variable
     * hypotheses generates a Type Conversion Rule. With one of the hypotheses =
     * null, a Syntax Axiom containing no constants in the expression and two
     * variables generates a Type Conversion Grammar Rule. So.....we carry
     * around this stinking tree structure that may contain Type Conversions and
     * Nulls Permitted transformations along with variable hypotheses that are
     * to be substituted. In other words, blame Nulls Permitted for this
     * complexificationizing.
     *
     * @param paramArray substitutions for variable hypothesis nodes of the
     *            Grammar Rule's paramTransformationTree.
     * @return the output ParseNode
     */
    public ParseNode buildGrammaticalParseNode(
        final ParseNodeHolder[] paramArray)
    {

        final int[] varHypReseq = getSyntaxAxiomVarHypReseq();

        final ParseNodeHolder[] expandedReseqParam = new ParseNodeHolder[paramVarHypNode.length];

        int src = 0;
        if (varHypReseq == null) {
            for (int i = 0; i < paramVarHypNode.length; i++)
                if (paramVarHypNode[i] != null)
                    expandedReseqParam[i] = paramArray[src++];
        }
        else
            for (int i = 0; i < paramVarHypNode.length; i++)
                if (paramVarHypNode[i] != null)
                    expandedReseqParam[varHypReseq[i]] = paramArray[src++];

        return paramTransformationTree.getRoot()
            .deepCloneWithGrammarHypSubs(paramVarHypNode, expandedReseqParam);

    }

    /**
     * Scans GrammarRule's paramVarHypNode array looking for a match to the
     * input Type Code.
     * <p>
     * This is a key function used in deriving additional rules using a
     * TypeConversionRule or NullsPermittedRule.
     *
     * @param nextSearch starting index of search.
     * @param searchTyp Type Code sought among paramVarHypNodes.
     * @return index of node in paramVarHypNode with matching type or -1 if not
     *         found.
     * @throws IllegalStateException only if there is a severe bug in the code
     *             (which performs a double-check here just to be on the safe
     *             side -- we'd rather have a blow-up than deliver bogus
     *             answers?)
     */
    protected int findMatchingVarHypTyp(int nextSearch, final Cnst searchTyp) {
        for (; nextSearch < paramVarHypNode.length; nextSearch++)
            if (paramVarHypNode[nextSearch] != null) {
                if (!(paramVarHypNode[nextSearch].stmt instanceof VarHyp))
                    throw new IllegalStateException(new VerifyException(
                        GrammarConstants.ERRMSG_BOGUS_PARAM_VARHYP_NODE,
                        nextSearch,
                        paramVarHypNode[nextSearch].stmt.getLabel()));
                if (paramVarHypNode[nextSearch].stmt.getTyp() == searchTyp)
                    return nextSearch;
            }
        return -1;
    }

}
