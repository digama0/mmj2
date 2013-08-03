//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  NullsPermittedRule.java  0.02 08/28/2005
 */

package mmj.verify;
import mmj.lang.*;
import java.util.*;

/**
 *  A Nulls Permitted Rule is a Grammar Rules that says that
 *  a certain type of grammatical expression may be
 *  null, (may have zero-length symbol sequences).
 *  <p>
 *  In Metamath terms, a Nulls Permitted Rule can be derived
 *  from a <code>$a</code> statement whose Formula has length
 *  equal to 1.
 *  <p>
 *  For example: <code>wffNullOK $a wff $.</code> states
 *  that wff-type variables and expressions may be empty
 *  or "null".
 *  <p>
 *  Nulls Permitted Rules can also be derived from other
 *  Grammar Rules, including Type Conversion Rules and
 *  Notation Rules. Things get complicated very quickly
 *  when using Nulls! Look at this example:
 *  <p>
 *  <ol>
 *  <li><code>$c NUMBER INTEGER $.</code>
 *  <li><code>$v x y z i j k $.</code>
 *  <li><code>NX $f NUMBER x $.</code>
 *  <li><code>Ii $f INTEGER i $.</code>
 *  <li><code>NullOkINTEGER $a INTEGER $.</code>
 *  <li><code>ConvINTEGER $a NUMBER i $.</code>
 *  </ol>
 *  <p>
 *  What that says is that we have two grammatical Type Codes,
 *  "NUMBER" and "INTEGER", with NUMBER variables x, y, z
 *  and INTEGER variables i, j, and k. And it says that
 *  INTEGER has Nulls Permitted, and that every INTEGER is a
 *  NUMBER. But it implies -- generates -- NUMBER has Nulls
 *  Permitted also! A Nulls Permitted Rule is derived from Type
 *  Conversion statement "ConvInteger" using statement
 *  "NullOkINTEGER".
 *  <p>
 *  Similar derivations occur with Notation Rules when
 *  Nulls and Type Conversions are involved. Very tricky stuff!
 *
 *  @see <a href="../../CreatingGrammarRulesFromSyntaxAxioms.html">
 *       CreatingGrammarRulesFromSyntaxAxioms.html</a>
 *  @see <a href="../../ConsolidatedListOfGrammarValidations.html">
 *       ConsolidatedListOfGrammarValidations.html</a>
 *  @see <a href="../../BasicsOfSyntaxAxiomsAndTypes.html">
 *       BasicsOfSyntaxAxiomsAndTypes.html</a>
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class NullsPermittedRule extends GrammarRule {

    /**
     *  Default constructor.
     */
    public NullsPermittedRule() {
        super();
    }

    /**
     *  Constructor -- default GrammarRule for base Syntax Axioms,
     *  which means no parameter "transformations".
     *
     *  @param grammar         The Grammar.
     *  @param baseSyntaxAxiom Syntax Axiom.
     */
    public NullsPermittedRule(Grammar grammar,
                              Axiom   baseSyntaxAxiom) {
        super(grammar,
              baseSyntaxAxiom);
    }

    /**
     *  Nulls Permitted Rule builder for base Syntax Axioms,
     *  which means no parameter "transformations".
     *
     *  @param grammar         The Grammar.
     *  @param baseSyntaxAxiom Syntax Axiom.
     *
     *  @return new NullsPermittedRule object.
     */
    public static NullsPermittedRule buildBaseRule(
                           Grammar           grammar,
                           Axiom             baseSyntaxAxiom) {

        NullsPermittedRule nullsPermittedRule =
           new NullsPermittedRule(grammar,
                                  baseSyntaxAxiom);
        nullsPermittedRule.setIsBaseRule(true);
        return nullsPermittedRule;
    }

    /**
     *  Build new NullsPermittedRule using an existing GrammarRule
     *  which is being "cloned" and modified by substituting the
     *  paramTransformationTree from a NullsPermittedRule for
     *  one of the existing rule's variable hypothesis
     *  paramTransformationTree nodes.
     *  <p>
     *  Note that the output
     *  paramTransformationTree is built using a deep clone
     *  of the existing rules paramTransformationTree but the
     *  substituting tree does not need cloning BECAUSE
     *  when the GrammaticalParser builds a ParseTree for
     *  an expression, *it* does a full cloning operation;
     *  this, of course assumes that once a GrammarRule is
     *  created and fully populated that it is never modified
     *  (changing the grammar means rebuilding it, in other
     *  words.)
     *  <p>
     *  Example, a TypeConversionRule is morphed into a
     *  NullsPermittedRule by substituting the NullsPermitted
     *  paramTransformationTree for the TypeConversionRule's
     *  variable hypothesis.
     *
     *  @param grammar            The Grammar.
     *  @param oldGrammarRule     rule being "cloned".
     *  @param matchIndex         index to paramVarHypNode array
     *                              indicating which VarHyp is being
     *                              substituted.
     *  @param nullsPermittedRule the modifying rule.
     */
    public NullsPermittedRule(
                        Grammar            grammar,
                        GrammarRule        oldGrammarRule,
                        int                matchIndex,
                        NullsPermittedRule nullsPermittedRule) {
        super(grammar);

        ParseTree substParseTransformationTree =
                  nullsPermittedRule.paramTransformationTree;
        int       substMaxSeqNbr               =
                  nullsPermittedRule.getMaxSeqNbr();

        if (oldGrammarRule.getMaxSeqNbr() > substMaxSeqNbr) {
            setMaxSeqNbr(oldGrammarRule.getMaxSeqNbr());
        }
        else {
            setMaxSeqNbr(substMaxSeqNbr);
        }

        setNbrHypParamsUsed(0);
        paramVarHypNode  =
            new ParseNode[oldGrammarRule.paramVarHypNode.length];

        paramTransformationTree =
            oldGrammarRule.paramTransformationTree.deepCloneWNodeSub(
                oldGrammarRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());
    }


    /**
     *  Return a duplicate of the input ruleFormatExpr if it
     *  exists, or return null.
     *
     *  @param grammar          The Grammar.
     *  @param ruleFormatExprIn the expression to check.
     *
     *  @return GrammarRule if duplicate found, or null.
     */
    public GrammarRule getDupRule(Grammar grammar,
                             Cnst[]  ruleFormatExprIn) {

        NullsPermittedRule dupCheck;
        Iterator iterator =
            grammar.getNullsPermittedGRList().iterator();
        Cnst ruleTyp = getGrammarRuleTyp();
        while (iterator.hasNext()) {
            dupCheck = (NullsPermittedRule)iterator.next();
            if (ruleTyp ==
                dupCheck.getGrammarRuleTyp()) {
                return dupCheck;
            }
        }
        return null;
    }

    /**
     *  Add Nulls Permitted rule format expression to the system
     *  "repository".
     *
     *  @param grammar          The Grammar object (Mr Big).
     *  @param ruleFormatExprIn the expression to add.
     */
    public void addToGrammar(Grammar grammar,
                             Cnst[]  ruleFormatExprIn) {
        this.setRuleFormatExpr(ruleFormatExprIn);
        grammar.nullsPermittedGRListAdd(this);
        (getGrammarRuleTyp()).setNullsPermittedGR(this);
    }

    /**
     *  deriveAdditionalRules based on the addition of a
     *  new GrammarRule to those already generated and
     *  accepted.
     *  <p>
     *  A PriorityQueue is used to store new derivedGrammarRules
     *  awaiting processing (dup checking, parse checking and
     *  possible addition to the Grammar data.) The Comparator
     *  orders derived rules based on MaxSeqNbr and ruleNbr so
     *  that low Metamath sequence number statements are added
     *  to the Grammar before higher sequenced statements. The
     *  existing rules are stored in separate repositories,
     *  which are scanned, as needed, when a new rule is added;
     *  the order that the repositories are scanned is irrelevant
     *  because the PriorityQueue controls update processing
     *  sequence.
     *  <p>
     *  Note also that a newly derived rule may
     *  generate other derived rules, which in turn trigger other
     *  derivations; this is a "feature" (as well as a scary
     *  opportunity for an infinite loop!) The benefit is that
     *  each pair of rules may be considered separately; when
     *  processing the pair there is no need to worry about
     *  other possible Type Conversion and Null Permitted Rules.
     *  For example, an "A" Type hypothesis in a Notation Rule may
     *  be the target of a "B to A" Type Conversion, but the
     *  fact that there is a "C to B" or "C to A" conversion
     *  can be ignored -- if the "B to A" conversion generates
     *  a variant rule of the original rule, then when *that*
     *  rule comes back through the PriorityQueue, the "C to B"
     *  rule will automatically come into play. This also means
     *  that a "B to A" conversion will combine with a "C to B"
     *  conversion to generate a "C to A" conversion -- eventually.
     *  <p>
     *  NullsPermittedRule.deriveAdditionalRules --
     *  The new NullsPermittedRule may generate variants of
     *  of NullsPermittedRules, TypeConversionRules and
     *  NotationRules. A NullsPermittedRule can be substituted
     *  *into* any matching hypothesis, and the resulting
     *  GrammarRule can morph!
     *
     *  @param grammar The Grammar.
     */
    public void deriveAdditionalRules(Grammar grammar) {
        Iterator iterator =
            grammar.getTypeConversionGRList().iterator();
        TypeConversionRule typeConversionRule;
        while (iterator.hasNext()) {
            typeConversionRule =
                (TypeConversionRule)iterator.next();
            typeConversionRule.deriveRulesUsingNullsPermitted(
                        grammar,
                        this);
        }

        iterator = grammar.getNotationGRSet().iterator();
        NotationRule notationRule;
        while (iterator.hasNext()) {
            notationRule =
                (NotationRule)iterator.next();
            notationRule.deriveRulesUsingNullsPermitted(
                        grammar,
                        this);
        }
    }

    /**
     *  Returns the ruleFormatExpr for the Nulls Permitted Rule,
     *  simulating retrieving it from the Grammar Rule Tree/Forest.
     *  <p>
     *  In reality, the ruleFormatExpr for a Nulls Permitted
     *  Rule is an zero-length sequence of symbol...
     *
     *  @return ruleFormatExpr for the Nulls Permitted Rule.
     */
    public Cnst[] getForestRuleExpr() {
        Cnst[] expr = new Cnst[0];
        return expr;
    }
}
