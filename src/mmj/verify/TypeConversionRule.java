//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  TypeConversionRule.java  0.03 09/25/2005
 *
 * Sep-25-2005:
 *
 *  - mmj.verify.TypeConversionRule#deriveAdditionalRules()
 *    : bug! derivation of new type conversion rules when
 *    adding a type converison rule must check both rules
 *    for hypothesis matches (i.e. rule "B isa A" and rule
 *    "C isa B" requires derived rule "C isa A" regardless
 *    of the order of input of the 1st two rules, so
 *    attempt derivation both ways when 2nd rule is read.)
 *
 */

package mmj.verify;
import mmj.lang.*;
import java.util.*;

/**
 *  A Type Conversion Rule is a Grammar Rules that says that
 *  any grammatical expression of a certain type may be
 *  substituted for any expression of another grammatical Type.
 *  <p>
 *  In Metamath terms, a Type Conversion can be derived
 *  from a <code>$a</code> statement whose Formula has length
 *  equal to 2 and whose 2nd symbol has a grammatical Type
 *  Code that differs from the 1st Symbol (the first symbol
 *  of every Metamath Formula must be -- is always -- a
 *  Constant, and is, in effect a "Type Code").
 *  <p>
 *  The most famous Metamath Type Conversion is "cv", from
 *  set.mm: <code>cv $a class x $.</code>, which states
 *  that every set is a class, that any set variable or
 *  expression with Type "set" can be used as a replacement
 *  for any class variable or expression with Type "class".)
 *  <p>
 *  Type Conversion Rules can also be derived from other
 *  Grammar Rules, including Type Conversion Rules and
 *  Notation Rules. Things get complicated very quickly,
 *  especially when Nulls Permitted Rules are involved.
 *  <p>
 *  An example:
 *  <p>
 *  <ul>
 *  <li><code>$c NUMBER INTEGER POSITIVEINTEGER wff term = $.</code>
 *  <li><code>$v x y i j m n$.</code>
 *  <li><code>Nx $f NUMBER x $.</code>
 *  <li><code>Ny $f NUMBER y $.</code>
 *  <li><code>Ii $f INTEGER i $.</code>
 *  <li><code>Ij $f INTEGER j $.</code>
 *  <li><code>Pm $f POSITIVEINTEGER m $.</code>
 *  <li><code>Pn $f POSITIVEINTEGER n $.</code>
 *  <li><code>NullOkPOSITIVEINTEGER $a POSITIVEINTEGER $.</code>
 *  <li><code>ConvINTEGER $a NUMBER i $.</code>
 *  <li><code>ConvPOSITIVEINTEGER $a INTEGER m $.</code>
 *  <li><code>TermAdd $a term m n $.</code>
 *  <li><code>WffEquals $a wff x = y $.
 *  </ul>
 *  <p>
 *  OK, that very hypothetical example explicitly says that
 *  we have:
 *  <ul>
 *  <li>Type Code "NUMBER" with variables x and y, and variable
 *      hypotheses Nx and Ny;
 *  <li>Type Code "INTEGER" with variables i and j, and variable
 *      hypotheses Ii and Ij;
 *  <li>Type Code "POSITIVEINTEGER" with variables m and n,
 *      and variable hypotheses Pm and Pn.
 *  <li>Nulls Permitted Rule "NullOkPOSITIVEINTEGER";
 *  <li>Type Conversion Rules "ConvINTEGER", from Integer to Number,
 *      and ConvPOSITIVEINTEGER from Positive Integer to Integer;
 *  <li>Notation Rules "TermAdd" (with Type Code "term") and
 *      "WffEquals" (with Type Code "wff).
 *  </ul>
 *  <p>
 *  But we're not done yet, those are only the explicit Grammar
 *  Rules. A number of implicit -- derived -- Grammar Rules
 *  follow:
 *  <p>
 *  <ul>
 *  <li>Derived Type Conversion Rule from POSITIVEINTEGER to
 *      NUMBER;
 *  <li>Derived Nulls Permitted Rules for INTEGER, NUMBER,
 *      and Term;
 *  <li>Derived Notation Rules based on TermAdd for expressions
 *      "m n", "x y", "m i", "m x", "i m", "x m",
 *      "i x", and "x i";
 *  <li>Derived Notation Rules based on WffEquals for expressions
 *      "m = n", "m =", "= m", "i = j", "i =", "= i", "m = i",
 *      "m = x", "i = m", "x = m", "x =" and "= x".
 *  </ul>
 *  <p>
 *  <b>WHEW!</b>
 *  <p>
 *  Part of mmj's grammar validation checks for Type Conversion
 *  loops, such as "a ISA b" + "b ISA c" + "c ISA a". It
 *  also rejects "a ISA a".
 *  <p>
 *  Implicit grammar rules are derived in mmj and made explicit,
 *  so if "a ISA b" and "z ISA a", then mmj will derive "z ISA b".
 *  <p>
 *  Since derived grammar rules may duplicate user-input
 *  Syntax Axioms (the "base" Grammar Rules), mmj reports as
 *  an error a user-input rule that duplicates a derived rule
 *  already in existence. This means that the user should
 *  move her rule towards the beginning of the .mm file to
 *  establish precedence.
 *  <p>
 *  In other words, mmj reports as an error any user-input
 *  grammar rules that have no effect, but silently ignores
 *  any newly derived rules that duplicate user-input rules
 *  (unless there is a Type Code conflict between the two!)
 *  <p>
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
public class TypeConversionRule extends GrammarRule {
/**
 *  convTyp is the "from" or source Typ Cd for The Type
 *  Conversion Rule, which is converted "to" the Rule's
 *  Type Code.
 *  <p>
 *   Note that there is no need to store
 *   the "to" Type code here in the TypeConversionRule
 *   because it can easily be derived from the Type Code
 *   via GrammarRule.getBaseSyntaxAxiom().getTyp() AND from the
 *   root node's Stmt of GrammarRule.paramTransformationTree.
 */
    protected Cnst convTyp;

    /**
     *  Default constructor.
     */
    public TypeConversionRule() {
        super();
    }

    /**
     *  Constructor -- default GrammarRule for base Syntax Axioms,
     *  which means no parameter "transformations".
     *
     *  @param grammar         The Grammar.
     *  @param baseSyntaxAxiom Syntax Axiom.
     */
    public TypeConversionRule(Grammar     grammar,
                              Axiom       baseSyntaxAxiom,
                              Cnst        convTyp) {
        super(grammar,
              baseSyntaxAxiom);
        this.convTyp = convTyp;
    }

    /**
     *  Builds a new TypeConversionRule using a Notation rule
     *  which is being "cloned" and modified by substituting the
     *  paramTransformationTree from a NullsPermittedRule for
     *  one of the existing rule's variable hypothesis
     *  paramTransformationTree nodes.
     *  <p>
     *  (That sounds hairy :)
     *  <p>
     *  Note that the output
     *  paramTransformationTree is built using a deep clone
     *  of the existing rule's paramTransformationTree but the
     *  substituting tree does not need cloning BECAUSE
     *  when the GrammaticalParser builds a ParseTree for
     *  an expression, *it* does a full cloning operation;
     *  this, of course assumes that once a GrammarRule is
     *  created and fully populated that it is never modified
     *  (changing the grammar means rebuilding it, in other
     *  words.)
     *
     *  @param grammar            The Grammar.
     *  @param oldNotationRule    rule being "cloned".
     *  @param matchIndex         index to paramVarHypNode array
     *                              indicating which VarHyp is being
     *                              substituted.
     *  @param nullsPermittedRule the modifying rule.
     */
    public TypeConversionRule(
                        Grammar            grammar,
                        NotationRule       oldNotationRule,
                        int                matchIndex,
                        NullsPermittedRule nullsPermittedRule) {
        super(grammar);

        ParseTree substParseTransformationTree =
                        nullsPermittedRule.paramTransformationTree;
        int       substMaxSeqNbr               =
                        nullsPermittedRule.getMaxSeqNbr();

        if (oldNotationRule.getMaxSeqNbr() > substMaxSeqNbr) {
            setMaxSeqNbr(oldNotationRule.getMaxSeqNbr());
        }
        else {
            setMaxSeqNbr(substMaxSeqNbr);
        }

        setNbrHypParamsUsed(1);

        paramTransformationTree =
            oldNotationRule.paramTransformationTree.deepCloneWNodeSub(
                oldNotationRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());

        paramVarHypNode =
            new ParseNode[oldNotationRule.paramVarHypNode.length];
        for (int i = 0; i < paramVarHypNode.length; i++) {
            //bypass VarHyp being nulled and those already null...
            if (i == matchIndex ||
                oldNotationRule.paramVarHypNode[i] == null) {
                continue;
            }
            paramVarHypNode[i] =
                paramTransformationTree.findChildVarHypNode(i);
            if (paramVarHypNode[i] == null) {
                throw new IllegalStateException(
                    GrammarConstants.ERRMSG_TYPCONV_VARHYP_NOTFND_1
                    + oldNotationRule.getBaseSyntaxAxiom().getLabel()
                    +
                    GrammarConstants.ERRMSG_TYPCONV_VARHYP_NOTFND_2);
            }
            convTyp = paramVarHypNode[i].getStmt().getTyp();
        }
    }

    /**
     *  Builds a new TypeConversionRule using an existing
     *  TypeConversion rule which is being "cloned" and
     *  and modified by substituting the paramTransformationTree
     *  from another TypeConversionRule for
     *  one of the existing rule's variable hypothesis
     *  paramTransformationTree nodes.
     *  <p>
     *  Note that the output
     *  paramTransformationTree is built using a deep clone
     *  of the existing rule's paramTransformationTree but the
     *  substituting tree does not need cloning BECAUSE
     *  when the GrammaticalParser builds a ParseTree for
     *  an expression, *it* does a full cloning operation;
     *  this, of course assumes that once a GrammarRule is
     *  created and fully populated that it is never modified
     *  (changing the grammar means rebuilding it, in other
     *  words.)
     *
     *  @param grammar            The Grammar.
     *  @param oldTCRule          rule being "cloned".
     *  @param matchIndex         index to paramVarHypNode array
     *                              indicating which VarHyp is being
     *                              substituted.
     *  @param typeConversionRule the modifying rule.
     */
    public TypeConversionRule(
                    Grammar            grammar,
                    TypeConversionRule oldTCRule,
                    int                matchIndex,
                    TypeConversionRule typeConversionRule) {
        super(grammar);

        ParseTree substParseTransformationTree =
                  typeConversionRule.paramTransformationTree;
        int       substMaxSeqNbr               =
                  typeConversionRule.getMaxSeqNbr();

        if (oldTCRule.getMaxSeqNbr() > substMaxSeqNbr) {
            setMaxSeqNbr(oldTCRule.getMaxSeqNbr());
        }
        else {
            setMaxSeqNbr(substMaxSeqNbr);
        }

        setNbrHypParamsUsed(1);

        paramTransformationTree =
            oldTCRule.paramTransformationTree.deepCloneWNodeSub(
                oldTCRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());

        paramVarHypNode =
            new ParseNode[oldTCRule.paramVarHypNode.length];
        paramVarHypNode[matchIndex] =
                paramTransformationTree.findChildVarHypNode(matchIndex);
        if (paramVarHypNode[matchIndex] == null) {
            throw new IllegalStateException(
                GrammarConstants.ERRMSG_TYPCONV_2_VARHYP_NOTFND_1
                + oldTCRule.getBaseSyntaxAxiom().getLabel()
                + GrammarConstants.ERRMSG_TYPCONV_2_VARHYP_NOTFND_2);
        }
        convTyp = paramVarHypNode[matchIndex].getStmt().getTyp();
    }

    /**
     *  Type Conversion Rule builder for base Syntax Axioms,
     *  which means no parameter "transformations".
     *
     *  @param grammar             The Grammar.
     *  @param baseSyntaxAxiom     Syntax Axiom.
     *  @param parseNodeHolderExpr Expression in parseNodeHolder
     *                             array.
     *
     *  @return new TypeConversionRule object.
     */
    protected static TypeConversionRule buildBaseRule(
                           Grammar           grammar,
                           Axiom             baseSyntaxAxiom,
                           ParseNodeHolder[] parseNodeHolderExpr) {
        Cnst     toTyp       = baseSyntaxAxiom.getTyp();
        Cnst     fromTyp     =
            parseNodeHolderExpr[0].parseNode.getStmt().getTyp();

        if (TypeConversionRule.isLoop(fromTyp,
                                      toTyp)) {
            grammar.accumErrorMsgInList(
                GrammarConstants.ERRMSG_TYPCONV_AXIOM_LOOP_1
                + baseSyntaxAxiom.getLabel()
                + GrammarConstants.ERRMSG_TYPCONV_AXIOM_LOOP_2
                + fromTyp
                + GrammarConstants.ERRMSG_TYPCONV_AXIOM_LOOP_3
                + toTyp);
            return null;
        }

        TypeConversionRule typeConversionRule =
            new TypeConversionRule(grammar,
                                   baseSyntaxAxiom,
                                   fromTyp);
        typeConversionRule.setIsBaseRule(true);
        return typeConversionRule;

    }

    /**
     *  isLoop determines whether or not a new Type Conversion
     *  from/to pair of Type Codes create a "loop" -- meaning
     *  that "from" converts to "to" and "to" converts to
     *  "from", directly or indirectly.
     *
     *  isLoop relies on Cnst.convFromTypGRArray already being
     *  fully populated with all "from" Type Codes for the
     *  subject Cnst, including direct conversions and
     *  all indirect conversions; this puts the onus on
     *  TypeConversionRule.deriveAdditionalRules to figure
     *  out the complicated stuff.
     *
     *  @param fromTyp the "from" converstion Type Code.
     *  @param toTyp   the "to" converstion Type Code.
     *
     *  @return true if it is a loop, else false.
     */
    public static boolean isLoop(Cnst fromTyp,
                                 Cnst toTyp) {
        boolean isLoop = false;
        if (fromTyp == toTyp
            ||
            fromTyp.findFromTypConversionRule(toTyp) != null) {
            isLoop = true;
        }
        return isLoop;

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

        return getBaseSyntaxAxiom(
                   ).getTyp(
                       ).findFromTypConversionRule(
                           convTyp);

    }

    /**
     *  Add Type Conversion rule format expression to the system
     *  "repository".
     *
     *  @param grammar          The Grammar object (Mr Big).
     *  @param ruleFormatExprIn the expression to add.
     */
    public void addToGrammar(Grammar grammar,
                             Cnst[]  ruleFormatExprIn) {
        this.setRuleFormatExpr(ruleFormatExprIn);
        (getGrammarRuleTyp()).convFromTypGRArrayAdd(this);
        grammar.typeConversionGRListAdd(this);
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
     *
     *  @param grammar The Grammar.
     */
    public void deriveAdditionalRules(Grammar grammar) {
        Iterator iterator =
            grammar.getNullsPermittedGRList().iterator();
        NullsPermittedRule nullsPermittedRule;
        while (iterator.hasNext()) {
            nullsPermittedRule =
                (NullsPermittedRule)iterator.next();
            this.deriveRulesUsingNullsPermitted(
                        grammar,
                        nullsPermittedRule);
        }

        iterator = grammar.getTypeConversionGRList().iterator();
        TypeConversionRule typeConversionRule;
        while (iterator.hasNext()) {
            typeConversionRule =
                (TypeConversionRule)iterator.next();
            typeConversionRule.deriveRulesUsingTypeConversion(
                        grammar,
                        this);
            // 21 September 2005 fix! go other way also!
            this.deriveRulesUsingTypeConversion(
                        grammar,
                        typeConversionRule);
            
        }

        iterator = grammar.getNotationGRSet().iterator();
        NotationRule notationRule;
        while (iterator.hasNext()) {
            notationRule =
                (NotationRule)iterator.next();
            notationRule.deriveRulesUsingTypeConversion(
                        grammar,
                        this);
        }
    }

    /**
     *  Generates a NullsPermittedRule *if* the input
     *  NullsPermittedRule Type Code matches the Type Code
     *  of the TypeConversionRule's VarHyp.
     *  <p>
     *  Note: The form of a TypeConversionRule *expression* is
     *  exactly one variable whose Type Code differs from
     *  that of the rule's Type.
     *
     *  @param grammar The Grammar.
     *  @param nullsPermittedRule the modifying rule.
     */
    protected void deriveRulesUsingNullsPermitted(
                        Grammar               grammar,
                        NullsPermittedRule    nullsPermittedRule) {
        if (getNbrHypParamsUsed() != 1) {
            throw new IllegalStateException(
                GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1_1
                + getNbrHypParamsUsed()
                + GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1_2
                + getBaseSyntaxAxiom().getLabel()
                + GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1_3
                + ruleNbr);
        }

        int matchIndex =
            findMatchingVarHypTyp(
                0,   //start search at beginning
                nullsPermittedRule.getGrammarRuleTyp());
        if (matchIndex >= 0) {
            NullsPermittedRule nPR =
                new NullsPermittedRule(
                    grammar,
                    this,
                    matchIndex,
                    nullsPermittedRule);
            grammar.derivedRuleQueueAdd(nPR);
        }
    }

    /**
     *  Generates a TypeConversionRule *if* the input
     *  TypeConversionRule Type Code matches the Type Code
     *  of this TypeConversionRule's VarHyp.
     *  <p>
     *  Note: The form of a TypeConversionRule *expression* is
     *  exactly one variable whose Type Code differs from
     *  that of the rule's Type.
     *
     *  @param grammar            The Grammar.
     *  @param typeConversionRule the modifying rule.
     */
    protected void deriveRulesUsingTypeConversion(
                        Grammar               grammar,
                        TypeConversionRule    typeConversionRule) {
        if (getNbrHypParamsUsed() != 1) {
            throw new IllegalStateException(
                GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1_1
                + getNbrHypParamsUsed()
                + GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1_2
                + getBaseSyntaxAxiom().getLabel()
                + GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1_3
                + ruleNbr);
        }

        int matchIndex =
            findMatchingVarHypTyp(
                0,   //start search at beginning
                typeConversionRule.getGrammarRuleTyp());
        if (matchIndex >= 0) {
            TypeConversionRule tCR =
                new TypeConversionRule(
                    grammar,
                    this,
                    matchIndex,
                    typeConversionRule);
                /**
                 *  Don't convert a hyp to its own type. On derived
                 *  rules just ignore this artifact.
                 */
            if (tCR.getConvTyp() != tCR.getGrammarRuleTyp()) {
                grammar.derivedRuleQueueAdd(tCR);
            }
        }
    }

    /**
     *  Return convTyp, the "from" part of the Type conversion.
     *
     *  @return  convTyp, the "from" part of the Type conversion.
     */
    public Cnst getConvTyp() {
        return convTyp;
    }

    /**
     *  Returns the ruleFormatExpr for the Type Conversion Rule by
     *  simulating retrieving it from the Grammar Rule Tree/Forest.
     *  <p>
     *  In reality, the ruleFormatExpr for a Type Conversion
     *  Rule is an length = 1 symbol sequence = convTyp.
     *
     *  @return ruleFormatExpr for the Type Conversion Rule.
     */
    public Cnst[] getForestRuleExpr() {
        Cnst[] expr = new Cnst[1];
        expr[0] = getConvTyp();
        return expr;
    }
}
