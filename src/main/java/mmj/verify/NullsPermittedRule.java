//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * NullsPermittedRule.java  0.02 08/28/2005
 */

package mmj.verify;

import mmj.lang.*;

/**
 * A Nulls Permitted Rule is a Grammar Rules that says that a certain type of
 * grammatical expression may be null, (may have zero-length symbol sequences).
 * <p>
 * In Metamath terms, a Nulls Permitted Rule can be derived from a {@code $a}
 * statement whose Formula has length equal to 1.
 * <p>
 * For example: {@code wffNullOK $a wff $.} states that wff-type variables and
 * expressions may be empty or "null".
 * <p>
 * Nulls Permitted Rules can also be derived from other Grammar Rules, including
 * Type Conversion Rules and Notation Rules. Things get complicated very quickly
 * when using Nulls! Look at this example:
 * <p>
 * <ol>
 * <li>{@code $c NUMBER INTEGER $.}
 * <li>{@code $v x y z i j k $.}
 * <li>{@code NX $f NUMBER x $.}
 * <li>{@code Ii $f INTEGER i $.}
 * <li>{@code NullOkINTEGER $a INTEGER $.}
 * <li>{@code ConvINTEGER $a NUMBER i $.}
 * </ol>
 * <p>
 * What that says is that we have two grammatical Type Codes, "NUMBER" and
 * "INTEGER", with NUMBER variables x, y, z and INTEGER variables i, j, and k.
 * And it says that INTEGER has Nulls Permitted, and that every INTEGER is a
 * NUMBER. But it implies -- generates -- NUMBER has Nulls Permitted also! A
 * Nulls Permitted Rule is derived from Type Conversion statement "ConvInteger"
 * using statement "NullOkINTEGER".
 * <p>
 * Similar derivations occur with Notation Rules when Nulls and Type Conversions
 * are involved. Very tricky stuff!
 * 
 * @see <a href="../../CreatingGrammarRulesFromSyntaxAxioms.html">
 *      CreatingGrammarRulesFromSyntaxAxioms.html</a>
 * @see <a href="../../ConsolidatedListOfGrammarValidations.html">
 *      ConsolidatedListOfGrammarValidations.html</a>
 * @see <a href="../../BasicsOfSyntaxAxiomsAndTypes.html">
 *      BasicsOfSyntaxAxiomsAndTypes.html</a>
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class NullsPermittedRule extends GrammarRule {

    /**
     * Default constructor.
     */
    public NullsPermittedRule() {
        super();
    }

    /**
     * Constructor -- default GrammarRule for base Syntax Axioms, which means no
     * parameter "transformations".
     * 
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     */
    public NullsPermittedRule(final Grammar grammar, final Axiom baseSyntaxAxiom)
    {
        super(grammar, baseSyntaxAxiom);
    }

    /**
     * Nulls Permitted Rule builder for base Syntax Axioms, which means no
     * parameter "transformations".
     * 
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     * @return new NullsPermittedRule object.
     */
    public static NullsPermittedRule buildBaseRule(final Grammar grammar,
        final Axiom baseSyntaxAxiom)
    {

        final NullsPermittedRule nullsPermittedRule = new NullsPermittedRule(
            grammar, baseSyntaxAxiom);
        nullsPermittedRule.setIsBaseRule(true);
        return nullsPermittedRule;
    }

    /**
     * Build new NullsPermittedRule using an existing GrammarRule which is being
     * "cloned" and modified by substituting the paramTransformationTree from a
     * NullsPermittedRule for one of the existing rule's variable hypothesis
     * paramTransformationTree nodes.
     * <p>
     * Note that the output paramTransformationTree is built using a deep clone
     * of the existing rules paramTransformationTree but the substituting tree
     * does not need cloning BECAUSE when the GrammaticalParser builds a
     * ParseTree for an expression, *it* does a full cloning operation; this, of
     * course assumes that once a GrammarRule is created and fully populated
     * that it is never modified (changing the grammar means rebuilding it, in
     * other words.)
     * <p>
     * Example, a TypeConversionRule is morphed into a NullsPermittedRule by
     * substituting the NullsPermitted paramTransformationTree for the
     * TypeConversionRule's variable hypothesis.
     * 
     * @param grammar The Grammar.
     * @param oldGrammarRule rule being "cloned".
     * @param matchIndex index to paramVarHypNode array indicating which VarHyp
     *            is being substituted.
     * @param nullsPermittedRule the modifying rule.
     */
    public NullsPermittedRule(final Grammar grammar,
        final GrammarRule oldGrammarRule, final int matchIndex,
        final NullsPermittedRule nullsPermittedRule)
    {
        super(grammar);

        final ParseTree substParseTransformationTree = nullsPermittedRule.paramTransformationTree;
        final int substMaxSeqNbr = nullsPermittedRule.getMaxSeqNbr();

        if (oldGrammarRule.getMaxSeqNbr() > substMaxSeqNbr)
            setMaxSeqNbr(oldGrammarRule.getMaxSeqNbr());
        else
            setMaxSeqNbr(substMaxSeqNbr);

        setNbrHypParamsUsed(0);
        paramVarHypNode = new ParseNode[oldGrammarRule.paramVarHypNode.length];

        paramTransformationTree = oldGrammarRule.paramTransformationTree
            .deepCloneWNodeSub(oldGrammarRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());
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
        final Cnst ruleTyp = getGrammarRuleTyp();
        for (final NullsPermittedRule dupCheck : grammar
            .getNullsPermittedGRList())
            if (ruleTyp == dupCheck.getGrammarRuleTyp())
                return dupCheck;
        return null;
    }

    /**
     * Add Nulls Permitted rule format expression to the system "repository".
     * 
     * @param grammar The Grammar object (Mr Big).
     * @param ruleFormatExprIn the expression to add.
     */
    @Override
    public void addToGrammar(final Grammar grammar,
        final Cnst[] ruleFormatExprIn)
    {
        setRuleFormatExpr(ruleFormatExprIn);
        grammar.nullsPermittedGRListAdd(this);
        getGrammarRuleTyp().setNullsPermittedGR(this);
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
     * <p>
     * NullsPermittedRule.deriveAdditionalRules -- The new NullsPermittedRule
     * may generate variants of of NullsPermittedRules, TypeConversionRules and
     * NotationRules. A NullsPermittedRule can be substituted *into* any
     * matching hypothesis, and the resulting GrammarRule can morph!
     * 
     * @param grammar The Grammar.
     */
    @Override
    public void deriveAdditionalRules(final Grammar grammar) {
        for (final TypeConversionRule typeConversionRule : grammar
            .getTypeConversionGRList())
            typeConversionRule.deriveRulesUsingNullsPermitted(grammar, this);

        for (final NotationRule notationRule : grammar.getNotationGRSet())
            notationRule.deriveRulesUsingNullsPermitted(grammar, this);
    }

    /**
     * Returns the ruleFormatExpr for the Nulls Permitted Rule, simulating
     * retrieving it from the Grammar Rule Tree/Forest.
     * <p>
     * In reality, the ruleFormatExpr for a Nulls Permitted Rule is an
     * zero-length sequence of symbol...
     * 
     * @return ruleFormatExpr for the Nulls Permitted Rule.
     */
    @Override
    public Cnst[] getForestRuleExpr() {
        final Cnst[] expr = new Cnst[0];
        return expr;
    }
}
