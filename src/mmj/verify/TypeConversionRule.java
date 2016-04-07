//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TypeConversionRule.java  0.03 09/25/2005
 *
 * Sep-25-2005:
 *
 * - mmj.verify.TypeConversionRule#deriveAdditionalRules()
 *   : bug! derivation of new type conversion rules when
 *   adding a type converison rule must check both rules
 *   for hypothesis matches (i.e. rule "B isa A" and rule
 *   "C isa B" requires derived rule "C isa A" regardless
 *   of the order of input of the 1st two rules, so
 *   attempt derivation both ways when 2nd rule is read.)
 */

package mmj.verify;

import mmj.lang.*;

/**
 * A Type Conversion Rule is a Grammar Rules that says that any grammatical
 * expression of a certain type may be substituted for any expression of another
 * grammatical Type.
 * <p>
 * In Metamath terms, a Type Conversion can be derived from a {@code $a}
 * statement whose Formula has length equal to 2 and whose 2nd symbol has a
 * grammatical Type Code that differs from the 1st Symbol (the first symbol of
 * every Metamath Formula must be -- is always -- a Constant, and is, in effect
 * a "Type Code").
 * <p>
 * The most famous Metamath Type Conversion is "cv", from set.mm:
 * {@code cv $a class x $.}, which states that every set is a class, that any
 * set variable or expression with Type "set" can be used as a replacement for
 * any class variable or expression with Type "class".)
 * <p>
 * Type Conversion Rules can also be derived from other Grammar Rules, including
 * Type Conversion Rules and Notation Rules. Things get complicated very
 * quickly, especially when Nulls Permitted Rules are involved.
 * <p>
 * An example:
 * <p>
 * <ul>
 * <li>{@code $c NUMBER INTEGER POSITIVEINTEGER wff term = $.}
 * <li>{@code $v x y i j m n$.}
 * <li>{@code Nx $f NUMBER x $.}
 * <li>{@code Ny $f NUMBER y $.}
 * <li>{@code Ii $f INTEGER i $.}
 * <li>{@code Ij $f INTEGER j $.}
 * <li>{@code Pm $f POSITIVEINTEGER m $.}
 * <li>{@code Pn $f POSITIVEINTEGER n $.}
 * <li>{@code NullOkPOSITIVEINTEGER $a POSITIVEINTEGER $.}
 * <li>{@code ConvINTEGER $a NUMBER i $.}
 * <li>{@code ConvPOSITIVEINTEGER $a INTEGER m $.}
 * <li>{@code TermAdd $a term m n $.}
 * <li><code>WffEquals $a wff x = y $.
 * </ul>
 * <p>
 * OK, that very hypothetical example explicitly says that we have:
 * <ul>
 * <li>Type Code "NUMBER" with variables x and y, and variable hypotheses Nx and
 * Ny;
 * <li>Type Code "INTEGER" with variables i and j, and variable hypotheses Ii
 * and Ij;
 * <li>Type Code "POSITIVEINTEGER" with variables m and n, and variable
 * hypotheses Pm and Pn.
 * <li>Nulls Permitted Rule "NullOkPOSITIVEINTEGER";
 * <li>Type Conversion Rules "ConvINTEGER", from Integer to Number, and
 * ConvPOSITIVEINTEGER from Positive Integer to Integer;
 * <li>Notation Rules "TermAdd" (with Type Code "term") and "WffEquals" (with
 * Type Code "wff).
 * </ul>
 * <p>
 * But we're not done yet, those are only the explicit Grammar Rules. A number
 * of implicit -- derived -- Grammar Rules follow:
 * <p>
 * <ul>
 * <li>Derived Type Conversion Rule from POSITIVEINTEGER to NUMBER;
 * <li>Derived Nulls Permitted Rules for INTEGER, NUMBER, and Term;
 * <li>Derived Notation Rules based on TermAdd for expressions "m n", "x y",
 * "m i", "m x", "i m", "x m", "i x", and "x i";
 * <li>Derived Notation Rules based on WffEquals for expressions "m = n", "m =",
 * "= m", "i = j", "i =", "= i", "m = i", "m = x", "i = m", "x = m", "x =" and
 * "= x".
 * </ul>
 * <p>
 * <b>WHEW!</b>
 * <p>
 * Part of mmj's grammar validation checks for Type Conversion loops, such as
 * "a ISA b" + "b ISA c" + "c ISA a". It also rejects "a ISA a".
 * <p>
 * Implicit grammar rules are derived in mmj and made explicit, so if "a ISA b"
 * and "z ISA a", then mmj will derive "z ISA b".
 * <p>
 * Since derived grammar rules may duplicate user-input Syntax Axioms (the
 * "base" Grammar Rules), mmj reports as an error a user-input rule that
 * duplicates a derived rule already in existence. This means that the user
 * should move her rule towards the beginning of the .mm file to establish
 * precedence.
 * <p>
 * In other words, mmj reports as an error any user-input grammar rules that
 * have no effect, but silently ignores any newly derived rules that duplicate
 * user-input rules (unless there is a Type Code conflict between the two!)
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
public class TypeConversionRule extends GrammarRule {
    /**
     * convTyp is the "from" or source Typ Cd for The Type Conversion Rule,
     * which is converted "to" the Rule's Type Code.
     * <p>
     * Note that there is no need to store the "to" Type code here in the
     * TypeConversionRule because it can easily be derived from the Type Code
     * via GrammarRule.getBaseSyntaxAxiom().getTyp() AND from the root node's
     * Stmt of GrammarRule.paramTransformationTree.
     */
    protected Cnst convTyp;

    /**
     * Default constructor.
     */
    public TypeConversionRule() {
        super();
    }

    /**
     * Constructor -- default GrammarRule for base Syntax Axioms, which means no
     * parameter "transformations".
     *
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     * @param convTyp the "from" type code of this conversion
     */
    public TypeConversionRule(final Grammar grammar,
        final Axiom baseSyntaxAxiom, final Cnst convTyp)
    {
        super(grammar, baseSyntaxAxiom);
        this.convTyp = convTyp;
    }

    /**
     * Builds a new TypeConversionRule using a Notation rule which is being
     * "cloned" and modified by substituting the paramTransformationTree from a
     * NullsPermittedRule for one of the existing rule's variable hypothesis
     * paramTransformationTree nodes.
     * <p>
     * (That sounds hairy :)
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
     * @param nullsPermittedRule the modifying rule.
     */
    public TypeConversionRule(final Grammar grammar,
        final NotationRule oldNotationRule, final int matchIndex,
        final NullsPermittedRule nullsPermittedRule)
    {
        super(grammar);

        final ParseTree substParseTransformationTree = nullsPermittedRule.paramTransformationTree;
        final int substMaxSeqNbr = nullsPermittedRule.getMaxSeqNbr();

        if (oldNotationRule.getMaxSeqNbr() > substMaxSeqNbr)
            setMaxSeqNbr(oldNotationRule.getMaxSeqNbr());
        else
            setMaxSeqNbr(substMaxSeqNbr);

        setNbrHypParamsUsed(1);

        paramTransformationTree = oldNotationRule.paramTransformationTree
            .deepCloneWNodeSub(oldNotationRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());

        paramVarHypNode = new ParseNode[oldNotationRule.paramVarHypNode.length];
        for (int i = 0; i < paramVarHypNode.length; i++) {
            // bypass VarHyp being nulled and those already null...
            if (i == matchIndex || oldNotationRule.paramVarHypNode[i] == null)
                continue;
            paramVarHypNode[i] = paramTransformationTree.findChildVarHypNode(i);
            if (paramVarHypNode[i] == null)
                throw new IllegalStateException(new VerifyException(
                    GrammarConstants.ERRMSG_TYPCONV_VARHYP_NOTFND,
                    oldNotationRule.getBaseSyntaxAxiom().getLabel()));
            convTyp = paramVarHypNode[i].stmt.getTyp();
        }
    }

    /**
     * Builds a new TypeConversionRule using an existing TypeConversion rule
     * which is being "cloned" and and modified by substituting the
     * paramTransformationTree from another TypeConversionRule for one of the
     * existing rule's variable hypothesis paramTransformationTree nodes.
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
     * @param oldTCRule rule being "cloned".
     * @param matchIndex index to paramVarHypNode array indicating which VarHyp
     *            is being substituted.
     * @param typeConversionRule the modifying rule.
     */
    public TypeConversionRule(final Grammar grammar,
        final TypeConversionRule oldTCRule, final int matchIndex,
        final TypeConversionRule typeConversionRule)
    {
        super(grammar);

        final ParseTree substParseTransformationTree = typeConversionRule.paramTransformationTree;
        final int substMaxSeqNbr = typeConversionRule.getMaxSeqNbr();

        if (oldTCRule.getMaxSeqNbr() > substMaxSeqNbr)
            setMaxSeqNbr(oldTCRule.getMaxSeqNbr());
        else
            setMaxSeqNbr(substMaxSeqNbr);

        setNbrHypParamsUsed(1);

        paramTransformationTree = oldTCRule.paramTransformationTree
            .deepCloneWNodeSub(oldTCRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());

        paramVarHypNode = new ParseNode[oldTCRule.paramVarHypNode.length];
        paramVarHypNode[matchIndex] = paramTransformationTree
            .findChildVarHypNode(matchIndex);
        if (paramVarHypNode[matchIndex] == null)
            throw new IllegalStateException(new VerifyException(
                GrammarConstants.ERRMSG_TYPCONV_2_VARHYP_NOTFND,
                oldTCRule.getBaseSyntaxAxiom().getLabel()));
        convTyp = paramVarHypNode[matchIndex].stmt.getTyp();
    }

    /**
     * Type Conversion Rule builder for base Syntax Axioms, which means no
     * parameter "transformations".
     *
     * @param grammar The Grammar.
     * @param baseSyntaxAxiom Syntax Axiom.
     * @param parseNodeHolderExpr Expression in parseNodeHolder array.
     * @return new TypeConversionRule object.
     */
    protected static TypeConversionRule buildBaseRule(final Grammar grammar,
        final Axiom baseSyntaxAxiom,
        final ParseNodeHolder[] parseNodeHolderExpr)
    {
        final Cnst toTyp = baseSyntaxAxiom.getTyp();
        final Cnst fromTyp = parseNodeHolderExpr[0].parseNode.stmt.getTyp();

        if (TypeConversionRule.isLoop(fromTyp, toTyp)) {
            grammar.getMessages().accumMessage(
                GrammarConstants.ERRMSG_TYPCONV_AXIOM_LOOP,
                baseSyntaxAxiom.getLabel(), fromTyp, toTyp);
            return null;
        }

        final TypeConversionRule typeConversionRule = new TypeConversionRule(
            grammar, baseSyntaxAxiom, fromTyp);
        typeConversionRule.setIsBaseRule(true);
        return typeConversionRule;

    }

    /**
     * isLoop determines whether or not a new Type Conversion from/to pair of
     * Type Codes create a "loop" -- meaning that "from" converts to "to" and
     * "to" converts to "from", directly or indirectly.
     * <p>
     * isLoop relies on Cnst.convFromTypGRArray already being fully populated
     * with all "from" Type Codes for the subject Cnst, including direct
     * conversions and all indirect conversions; this puts the onus on
     * TypeConversionRule.deriveAdditionalRules to figure out the complicated
     * stuff.
     *
     * @param fromTyp the "from" converstion Type Code.
     * @param toTyp the "to" converstion Type Code.
     * @return true if it is a loop, else false.
     */
    public static boolean isLoop(final Cnst fromTyp, final Cnst toTyp) {
        boolean isLoop = false;
        if (fromTyp == toTyp
            || fromTyp.findFromTypConversionRule(toTyp) != null)
            isLoop = true;
        return isLoop;

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

        return getBaseSyntaxAxiom().getTyp().findFromTypConversionRule(convTyp);

    }

    /**
     * Add Type Conversion rule format expression to the system "repository".
     *
     * @param grammar The Grammar object (Mr Big).
     * @param ruleFormatExprIn the expression to add.
     */
    @Override
    public void addToGrammar(final Grammar grammar,
        final Cnst[] ruleFormatExprIn)
    {
        setRuleFormatExpr(ruleFormatExprIn);
        getGrammarRuleTyp().convFromTypGRArrayAdd(this);
        grammar.typeConversionGRListAdd(this);
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
        {
            typeConversionRule.deriveRulesUsingTypeConversion(grammar, this);
            // 21 September 2005 fix! go other way also!
            deriveRulesUsingTypeConversion(grammar, typeConversionRule);

        }

        for (final NotationRule notationRule : grammar.getNotationGRSet())
            notationRule.deriveRulesUsingTypeConversion(grammar, this);
    }
    /**
     * Generates a NullsPermittedRule *if* the input NullsPermittedRule Type
     * Code matches the Type Code of the TypeConversionRule's VarHyp.
     * <p>
     * Note: The form of a TypeConversionRule *expression* is exactly one
     * variable whose Type Code differs from that of the rule's Type.
     *
     * @param grammar The Grammar.
     * @param nullsPermittedRule the modifying rule.
     */
    protected void deriveRulesUsingNullsPermitted(final Grammar grammar,
        final NullsPermittedRule nullsPermittedRule)
    {
        if (getNbrHypParamsUsed() != 1)
            throw new IllegalStateException(
                new VerifyException(GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1,
                    getNbrHypParamsUsed(), getBaseSyntaxAxiom().getLabel(),
                    ruleNbr));

        final int matchIndex = findMatchingVarHypTyp(0, // start search at
                                                        // beginning
            nullsPermittedRule.getGrammarRuleTyp());
        if (matchIndex >= 0) {
            final NullsPermittedRule nPR = new NullsPermittedRule(grammar, this,
                matchIndex, nullsPermittedRule);
            grammar.derivedRuleQueueAdd(nPR);
        }
    }

    /**
     * Generates a TypeConversionRule *if* the input TypeConversionRule Type
     * Code matches the Type Code of this TypeConversionRule's VarHyp.
     * <p>
     * Note: The form of a TypeConversionRule *expression* is exactly one
     * variable whose Type Code differs from that of the rule's Type.
     *
     * @param grammar The Grammar.
     * @param typeConversionRule the modifying rule.
     */
    protected void deriveRulesUsingTypeConversion(final Grammar grammar,
        final TypeConversionRule typeConversionRule)
    {
        if (getNbrHypParamsUsed() != 1)
            throw new IllegalStateException(
                new VerifyException(GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1,
                    getNbrHypParamsUsed(), getBaseSyntaxAxiom().getLabel(),
                    GrammarConstants.ERRMSG_TYPCONV_NBRHYP_NE_1, ruleNbr));

        final int matchIndex = findMatchingVarHypTyp(0, // start search at
                                                        // beginning
            typeConversionRule.getGrammarRuleTyp());
        if (matchIndex >= 0) {
            final TypeConversionRule tCR = new TypeConversionRule(grammar, this,
                matchIndex, typeConversionRule);
            /**
             * Don't convert a hyp to its own type. On derived rules just ignore
             * this artifact.
             */
            if (tCR.getConvTyp() != tCR.getGrammarRuleTyp())
                grammar.derivedRuleQueueAdd(tCR);
        }
    }

    /**
     * Return convTyp, the "from" part of the Type conversion.
     *
     * @return convTyp, the "from" part of the Type conversion.
     */
    public Cnst getConvTyp() {
        return convTyp;
    }

    /**
     * Returns the ruleFormatExpr for the Type Conversion Rule by simulating
     * retrieving it from the Grammar Rule Tree/Forest.
     * <p>
     * In reality, the ruleFormatExpr for a Type Conversion Rule is an length =
     * 1 symbol sequence = convTyp.
     *
     * @return ruleFormatExpr for the Type Conversion Rule.
     */
    @Override
    public Cnst[] getForestRuleExpr() {
        final Cnst[] expr = new Cnst[1];
        expr[0] = getConvTyp();
        return expr;
    }
}
