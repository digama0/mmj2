//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * NotationRule.java  0.02 08/27/2005
 */

package mmj.verify;

import java.util.ArrayList;

import mmj.lang.*;

/**
 * Notation Grammar Rules are the main Grammar Rules used to arrange symbols
 * representing grammatical types, such as "wff" and "class".
 * <p>
 * NotationRule objects are built from "base" Syntax Axioms, possibly modified
 * by Type Conversion Rules and/or Nulls Permitted Rules.
 * <p>
 * At this time, only NotationRule objects are stored in the Grammar Rule
 * Tree/Forests, though it may be that use of the Tree/Forest structures are not
 * needed (perhaps an essential use will be found in
 * mmj.verify.GrammarAmbiguity).
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
public class NotationRule extends GrammarRule {

    /**
     * isGimmeMatchNbr: equals 1 if the NotationRule is a "gimme match", meaning
     * that a match to a subsequence of an expression can be taken unequivocally
     * and irrevocably, regardless of the surrounding symbols (for example, in
     * set.mm "c0" is a gimme match).
     * <p>
     * Note: -1 and 0 are used in mmj.verify.GrammarAmbiguity to signify
     * "not a gimmeMatchNbr" in two different circumstances.
     */
    protected int isGimmeMatchNbr;

    /**
     * gRTail: is an object reference pointing to the terminal node in the Rule
     * Tree/Forest for this grammar rule.
     */
    protected GRNode gRTail; // last node of forest for this GR

    /**
     * Default constructor.
     */
    public NotationRule() {
        super();
    }

    /**
     * Constructor -- default GrammarRule for base Syntax Axioms, which means no
     * parameter "transformations".
     *
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     */
    public NotationRule(final Grammar grammar, final Axiom baseSyntaxAxiom) {
        super(grammar, baseSyntaxAxiom);
        if (baseSyntaxAxiom.getSyntaxAxiomHasUniqueCnst())
            if (baseSyntaxAxiom.getFormula().getCnt() == 2)
                isGimmeMatchNbr = 1;
    }

    /**
     * NotationRule "base" Syntax Axiom rule builder, which means no parameter
     * "transformations".
     *
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     * @return new NotationRule.
     */
    public static NotationRule buildBaseRule(final Grammar grammar,
        final Axiom baseSyntaxAxiom)
    {
        final NotationRule notationRule = new NotationRule(grammar,
            baseSyntaxAxiom);
        notationRule.setIsBaseRule(true);
        return notationRule;

    }

    /**
     * Construct new NotationRule using a Notation rule which is being "cloned"
     * and modified by substituting the paramTransformationTree from a
     * GrammarRule for one of the existing rule's variable hypothesis
     * paramTransformationTree nodes.
     * <p>
     * Note that the output paramTransformationTree is built using a deep clone
     * of the existing rule's paramTransformationTree but the substituting tree
     * does not need cloning BECAUSE when the GrammaticalParser builds a
     * ParseTree for an expression, *it* does a full cloning operation; this, of
     * course assumes that once a GrammarRule is created and fully populated
     * that it is never modified (changing the grammar means rebuilding it, in
     * other words.)
     *
     * @param grammar The Grammar.
     * @param oldNotationRule rule being "cloned".
     * @param matchIndex index to paramVarHypNode array indicating which VarHyp
     *            is being substituted.
     * @param substRule the modifying rule.
     */
    public NotationRule(final Grammar grammar,
        final NotationRule oldNotationRule, final int matchIndex,
        final GrammarRule substRule)
    {
        super(grammar);
        final ParseTree substParseTransformationTree = substRule.paramTransformationTree;
        final int substMaxSeqNbr = substRule.getMaxSeqNbr();

        if (oldNotationRule.maxSeqNbr > substMaxSeqNbr)
            maxSeqNbr = oldNotationRule.maxSeqNbr;
        else
            maxSeqNbr = substMaxSeqNbr;

        nbrHypParamsUsed = oldNotationRule.nbrHypParamsUsed - 1
            + substRule.nbrHypParamsUsed;

        paramTransformationTree = oldNotationRule.paramTransformationTree
            .deepCloneWNodeSub(oldNotationRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());

        if (substRule.nbrHypParamsUsed > 1) {
            new ArrayList<ParseNode>(nbrHypParamsUsed);
            paramVarHypNode = new ParseNode[nbrHypParamsUsed];
            paramTransformationTree.getRoot().accumVarHypArray(paramVarHypNode,
                0);
        }
        else {
            paramVarHypNode = new ParseNode[oldNotationRule.paramVarHypNode.length];
            for (int i = 0; i < paramVarHypNode.length; i++) {
                // bypass VarHyp being nulled and those already null...
                if (oldNotationRule.paramVarHypNode[i] == null
                    || substRule instanceof NullsPermittedRule
                        && i == matchIndex)
                    continue;
                paramVarHypNode[i] = paramTransformationTree
                    .findChildVarHypNode(i);
                if (paramVarHypNode[i] == null)
                    throw new IllegalStateException(new VerifyException(
                        GrammarConstants.ERRMSG_NOTATION_VARHYP_NOTFND,
                        oldNotationRule.getBaseSyntaxAxiom().getLabel(), i,
                        paramTransformationTree.toString()));
            }
        }
        isGimmeMatchNbr = oldNotationRule.isGimmeMatchNbr;
    }

    /**
     * Return a duplicate of the input ruleFormatExpr if it exists, or return
     * null.
     *
     * @param grammar The Grammar.
     * @param ruleFormatExprIn the expression to check.
     * @return GrammarRule if duplicate found, or null.
     */
    @Override
    public GrammarRule getDupRule(final Grammar grammar,
        final Cnst[] ruleFormatExprIn)
    {

        GrammarRule dupRule = null;
        final GRNode gRNode = GRForest.findCnstSubSeq(
            ruleFormatExprIn[0].getGRRoot(), ruleFormatExprIn, 0,
            ruleFormatExprIn.length - 1);
        if (gRNode != null)
            dupRule = gRNode.elementNotationRule();
        return dupRule;
    }

    /**
     * Add rule format expression to the Rule Forest.
     *
     * @param grammar The Grammar object (Mr Big).
     * @param ruleFormatExprIn the expression to add.
     */
    @Override
    public void addToGrammar(final Grammar grammar,
        final Cnst[] ruleFormatExprIn)
    {
        final GRNode tail = GRForest.addNotationRule(ruleFormatExprIn, this);
        if (tail.elementNotationRule() != this)
            throw new IllegalStateException(new VerifyException(
                GrammarConstants.ERRMSG_NOTATION_GRFOREST_DUP,
                getBaseSyntaxAxiom().getLabel()));
        setGRTail(tail);
        setRuleFormatExpr(ruleFormatExprIn);
        grammar.notationGRSetAdd(this);

        if (ruleFormatExprIn.length == 1 && nbrHypParamsUsed == 0)
            ruleFormatExprIn[0].setLen1CnstNotationRule(this);

    }

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
    @Override
    public void deriveAdditionalRules(final Grammar grammar) {
        for (final NullsPermittedRule nullsPermittedRule : grammar
            .getNullsPermittedGRList())
            deriveRulesUsingNullsPermitted(grammar, nullsPermittedRule);

        for (final TypeConversionRule typeConversionRule : grammar
            .getTypeConversionGRList())
            deriveRulesUsingTypeConversion(grammar, typeConversionRule);
    }

    /**
     * Derives new grammar rules from an existing NotationRule and a
     * NullsPermittedRule, which will be applied to matching variable hypothesis
     * nodes in the NotationRule.
     *
     * @param grammar The Grammar.
     * @param nullsPermittedRule the modifying rule.
     */
    public void deriveRulesUsingNullsPermitted(final Grammar grammar,
        final NullsPermittedRule nullsPermittedRule)
    {
        if (nbrHypParamsUsed == 0)
            return;

        final Cnst nullsPermittedRuleTyp = nullsPermittedRule
            .getGrammarRuleTyp();

        int matchIndex = findMatchingVarHypTyp(0, // start search at beginning
            nullsPermittedRuleTyp);
        if (matchIndex < 0)
            return;

        // check to see if formula has zero Cnst symbols
        if (getBaseSyntaxAxiom()
            .getMandVarHypArray().length == getBaseSyntaxAxiom().getFormula()
                .getCnt() - 1)
        {
            if (nbrHypParamsUsed == 1) {
                final NullsPermittedRule nPR = new NullsPermittedRule(grammar,
                    this, matchIndex, nullsPermittedRule);
                grammar.derivedRuleQueueAdd(nPR);
                return;
            }
            if (nbrHypParamsUsed == 2) {
                TypeConversionRule tCR = new TypeConversionRule(grammar, this,
                    matchIndex, nullsPermittedRule);
                /**
                 * Don't convert a hyp to its own type. On derived rules just
                 * ignore this artifact.
                 */
                if (tCR.getConvTyp() != tCR.getGrammarRuleTyp())
                    grammar.derivedRuleQueueAdd(tCR);
                matchIndex = findMatchingVarHypTyp(matchIndex + 1,
                    nullsPermittedRuleTyp);
                if (matchIndex < 0)
                    return;
                tCR = new TypeConversionRule(grammar, this, matchIndex,
                    nullsPermittedRule);
                /**
                 * Don't convert a hyp to its own type. On derived rules just
                 * ignore this artifact.
                 */
                if (tCR.getConvTyp() != tCR.getGrammarRuleTyp())
                    grammar.derivedRuleQueueAdd(tCR);
                return;
            }
        }

        /**
         * OK, formula does have at least one Cnst symbol or more than 2 VarHyps
         * in use......., so a NullsPermitted modification can only generate
         * other NotationRule(s).
         */
        NotationRule nR;
        while (true) {
            nR = new NotationRule(grammar, this, matchIndex,
                nullsPermittedRule);
            grammar.derivedRuleQueueAdd(nR);
            matchIndex = findMatchingVarHypTyp(matchIndex + 1,
                nullsPermittedRuleTyp);
            if (matchIndex < 0)
                return;
        }
    }

    /**
     * Derives new grammar rules from an existing NotationRule and a
     * TypeConversionRule, which will be applied to matching variable hypothesis
     * nodes in the NotationRule.
     * <p>
     * Note: combinations of var hyps are not considered here, just one
     * application of the TypeConversionRule per derived rule. The reason is
     * that the new derived rule goes into the queue and is then itself used to
     * create new rules (if any). Eventually all matching combinations are
     * considered without the complexity of dealing with them here.
     *
     * @param grammar The Grammar.
     * @param typeConversionRule the modifying rule.
     */
    public void deriveRulesUsingTypeConversion(final Grammar grammar,
        final TypeConversionRule typeConversionRule)
    {
        if (nbrHypParamsUsed == 0)
            return;

        final Cnst typeConversionRuleTyp = typeConversionRule
            .getGrammarRuleTyp();

        NotationRule nR;
        int matchIndex = 0;
        while (true) {
            matchIndex = findMatchingVarHypTyp(matchIndex,
                typeConversionRuleTyp);
            if (matchIndex < 0)
                return;
            nR = new NotationRule(grammar, this, matchIndex,
                typeConversionRule);
            grammar.derivedRuleQueueAdd(nR);
            matchIndex++;
        }
    }

    /**
     * Returns the ruleFormatExpr for the Notation Rule after retrieving it from
     * the Grammar Rule Tree/Forest.
     *
     * @return ruleFormatExpr for the NotationRule.
     */
    @Override
    public Cnst[] getForestRuleExpr() {

        final Cnst[] expr = new Cnst[getBaseSyntaxAxiom().getFormula().getCnt()
            - 1 - (paramVarHypNode.length - nbrHypParamsUsed)];
        int dest = expr.length;
        GRNode next = getGRTail();
        while (next != null) {
            expr[--dest] = next.elementCnst();
            next = next.elementUpLevel();
        }
        return expr;
    }

    /**
     * Return gRTail for the NotationRule.
     *
     * @return gRTail for the NotationRule.
     */
    public GRNode getGRTail() {
        return gRTail;
    }

    /**
     * Set the gRTail for the NotationRule.
     *
     * @param gRTail for the NotationRule.
     */
    public void setGRTail(final GRNode gRTail) {
        this.gRTail = gRTail;
    }

    /**
     * Return isGimmeMatchNbr for the NotationRule.
     *
     * @return isGimmeMatchNbr for the NotationRule.
     */
    public int getIsGimmeMatchNbr() {
        return isGimmeMatchNbr;
    }

    /**
     * Set isGimmeMatchNbr for the NotationRule.
     *
     * @param isGimmeMatchNbr for the NotationRule.
     */
    public void setIsGimmeMatchNbr(final int isGimmeMatchNbr) {
        this.isGimmeMatchNbr = isGimmeMatchNbr;
    }

}
