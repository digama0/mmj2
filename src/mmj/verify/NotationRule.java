//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  NotationRule.java  0.02 08/27/2005
 */

package mmj.verify;
import mmj.lang.*;
import java.util.*;

/**
 *  Notation Grammar Rules are the main Grammar Rules used
 *  to arrange symbols representing grammatical types, such
 *  as "wff" and "class".
 *  <p>
 *  NotationRule objects are built from "base" Syntax Axioms,
 *  possibly modified by Type Conversion Rules and/or
 *  Nulls Permitted Rules.
 *  <p>
 *  At this time, only NotationRule objects are stored in
 *  the Grammar Rule Tree/Forests, though it may be that
 *  use of the Tree/Forest structures are not needed (perhaps
 *  an essential use will be found in mmj.verify.GrammarAmbiguity).
 *
 *  @see <a href="../../CreatingGrammarRulesFromSyntaxAxioms.html">
 *       CreatingGrammarRulesFromSyntaxAxioms.html</a>
 *  @see <a href="../../ConsolidatedListOfGrammarValidations.html">
 *       ConsolidatedListOfGrammarValidations.html</a>
 *  @see <a href="../../BasicsOfSyntaxAxiomsAndTypes.html">
 *       BasicsOfSyntaxAxiomsAndTypes.html</a>
 *  @see <a href="../../EssentialAmbiguityExamples.html">
 *       EssentialAmbiguityExamples.html</a>
 *  @see <a href="../../GrammarRuleTreeNotes.html">
 *       GrammarRuleTreeNotes.html</a>
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class NotationRule extends GrammarRule {

    /**
     *  isGimmeMatchNbr: equals 1 if the NotationRule is a
     *  "gimme match", meaning that a match to a subsequence
     *  of an expression can be taken unequivocally and
     *  irrevocably, regardless of the surrounding symbols
     *  (for example, in set.mm "c0" is a gimme match).
     *
     *  Note: -1 and 0 are used in mmj.verify.GrammarAmbiguity
     *        to signify "not a gimmeMatchNbr" in two different
     *        circumstances.
     */
    protected int     isGimmeMatchNbr;

    /**
     *  gRTail: is an object reference pointing to the terminal
     *  node in the Rule Tree/Forest for this grammar rule.
     */
    protected GRNode  gRTail;     //last node of forest for this GR

    /**
     *  Default constructor.
     */
    public NotationRule() {
        super();
    }

    /**
     *  Constructor -- default GrammarRule for base Syntax Axioms,
     *  which means no parameter "transformations".
     *
     *  @param grammar         The Grammar.
     *  @param baseSyntaxAxiom Syntax Axiom.
     */
    public NotationRule(Grammar   grammar,
                        Axiom     baseSyntaxAxiom) {
        super(grammar,
              baseSyntaxAxiom);
        if (baseSyntaxAxiom.getSyntaxAxiomHasUniqueCnst()) {
            if (baseSyntaxAxiom.getFormula().getCnt() == 2) {
                isGimmeMatchNbr = 1;
            }
        }
    }

    /**
     *  NotationRule "base" Syntax Axiom rule builder, which
     *  means no parameter "transformations".
     *
     *  @param grammar         The Grammar.
     *  @param baseSyntaxAxiom Syntax Axiom.
     *
     *  @return new NotationRule.
     */
    public static NotationRule buildBaseRule(
                           Grammar           grammar,
                           Axiom             baseSyntaxAxiom) {
        NotationRule notationRule =
            new NotationRule(grammar,
                             baseSyntaxAxiom);
        notationRule.setIsBaseRule(true);
        return notationRule;

    }



    /**
     *  Construct new NotationRule using a Notation rule
     *  which is being "cloned" and modified by substituting the
     *  paramTransformationTree from a NullsPermittedRule for
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
     *  @param oldNotationRule    rule being "cloned".
     *  @param matchIndex         index to paramVarHypNode array
     *                              indicating which VarHyp is being
     *                              substituted.
     *  @param nullsPermittedRule the modifying rule.
     */
    public NotationRule(Grammar               grammar,
                           NotationRule       oldNotationRule,
                           int                matchIndex,
                           NullsPermittedRule nullsPermittedRule) {
        super(grammar);

        ParseTree substParseTransformationTree =
                        nullsPermittedRule.paramTransformationTree;
        int       substMaxSeqNbr               =
                        nullsPermittedRule.getMaxSeqNbr();

        if (oldNotationRule.maxSeqNbr > substMaxSeqNbr) {
            maxSeqNbr = oldNotationRule.maxSeqNbr;
        }
        else {
            maxSeqNbr = substMaxSeqNbr;
        }

        nbrHypParamsUsed = oldNotationRule.nbrHypParamsUsed - 1;

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
                    GrammarConstants.ERRMSG_NOTATION_VARHYP_NOTFND_1
                    + oldNotationRule.getBaseSyntaxAxiom().getLabel()
                    + GrammarConstants.ERRMSG_NOTATION_VARHYP_NOTFND_2
                    );
            }
        }

        isGimmeMatchNbr = oldNotationRule.isGimmeMatchNbr;
    }


    /**
     *  Build new NotationRule using a Notation rule
     *  which is being "cloned" and modified by substituting the
     *  paramTransformationTree from a TypeConversionRule for
     *  one of the existing rule's variable hypothesis
     *  paramTransformationTree nodes.
     *  <p>
     *  Note that the output
     *  paramTransformationTree is built using a deep clone
     *  of the existing rule's paramTransformationTree, but the
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
     *                            indicating which VarHyp is being
     *                            substituted.
     *  @param typeConversionRule the modifying rule.
     */
    public NotationRule(Grammar            grammar,
                        NotationRule       oldNotationRule,
                        int                matchIndex,
                        TypeConversionRule typeConversionRule) {
        super(grammar);

        ParseTree substParseTransformationTree =
                        typeConversionRule.paramTransformationTree;
        int       substMaxSeqNbr               =
                        typeConversionRule.getMaxSeqNbr();

        if (oldNotationRule.maxSeqNbr > substMaxSeqNbr) {
            maxSeqNbr = oldNotationRule.maxSeqNbr;
        }
        else {
            maxSeqNbr = substMaxSeqNbr;
        }

        nbrHypParamsUsed = oldNotationRule.nbrHypParamsUsed;

        paramTransformationTree =
            oldNotationRule.paramTransformationTree.deepCloneWNodeSub(
                oldNotationRule.paramVarHypNode[matchIndex],
                substParseTransformationTree.getRoot());

        paramVarHypNode =
            new ParseNode[oldNotationRule.paramVarHypNode.length];
        for (int i = 0; i < paramVarHypNode.length; i++) {
            //bypass VarHyps that are already null...
            if (oldNotationRule.paramVarHypNode[i] == null) {
                continue;
            }
            paramVarHypNode[i] =
                paramTransformationTree.findChildVarHypNode(i);
            if (paramVarHypNode[i] == null) {
                throw new IllegalStateException(
                    GrammarConstants.ERRMSG_NOTATION_VARHYP_2_NOTFND_1
                    + oldNotationRule.getBaseSyntaxAxiom().getLabel()
                    +
                    GrammarConstants.ERRMSG_NOTATION_VARHYP_2_NOTFND_2
                    + i
                    +
                    GrammarConstants.ERRMSG_NOTATION_VARHYP_2_NOTFND_3
                    + paramTransformationTree.toString()
                    +
                    GrammarConstants.ERRMSG_NOTATION_VARHYP_2_NOTFND_4
                    );
            }
        }

        isGimmeMatchNbr = oldNotationRule.isGimmeMatchNbr;
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

        GrammarRule dupRule = null;
        GRNode gRNode = (GRForest.findCnstSubSeq(
                            ruleFormatExprIn[0].getGRRoot(),
                            ruleFormatExprIn,
                            0,
                            (ruleFormatExprIn.length - 1)));
        if (gRNode != null) {
            dupRule =  gRNode.elementNotationRule();
        }
        return dupRule;
    }

    /**
     *  Add rule format expression to the Rule Forest.
     *
     *  @param grammar          The Grammar object (Mr Big).
     *  @param ruleFormatExprIn the expression to add.
     */
    public void addToGrammar(Grammar grammar,
                             Cnst[]  ruleFormatExprIn) {
        GRNode tail =
            GRForest.addNotationRule(ruleFormatExprIn,
                                     this);
        if (tail.elementNotationRule() != this) {
            throw new IllegalStateException(
                GrammarConstants.ERRMSG_NOTATION_GRFOREST_DUP_1
                + getBaseSyntaxAxiom().getLabel()
                + GrammarConstants.ERRMSG_NOTATION_GRFOREST_DUP_2);
        }
        setGRTail(tail);
        this.setRuleFormatExpr(ruleFormatExprIn);
        grammar.notationGRSetAdd(this);

        if (ruleFormatExprIn.length == 1 &&
            nbrHypParamsUsed        == 0) {
            ruleFormatExprIn[0].setLen1CnstNotationRule(this);
        }

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
            this.deriveRulesUsingTypeConversion(
                        grammar,
                        typeConversionRule);
        }
    }

    /**
     *  Derives new grammar rules from an existing NotationRule
     *  and a NullsPermittedRule, which will be applied to
     *  matching variable hypothesis nodes in the NotationRule.
     *
     *  @param grammar            The Grammar.
     *  @param nullsPermittedRule the modifying rule.
     */
    public void  deriveRulesUsingNullsPermitted(
                        Grammar               grammar,
                        NullsPermittedRule    nullsPermittedRule) {
        if (nbrHypParamsUsed == 0) {
            return;
        }

        Cnst nullsPermittedRuleTyp =
             nullsPermittedRule.getGrammarRuleTyp();

        int matchIndex = findMatchingVarHypTyp(
                            0,   //start search at beginning
                            nullsPermittedRuleTyp);
        if (matchIndex < 0) {
            return;
        }

        // check to see if formula has zero Cnst symbols
        if (getBaseSyntaxAxiom().getMandVarHypArray().length ==
            (getBaseSyntaxAxiom().getFormula().getCnt() - 1)) {
            if (nbrHypParamsUsed == 1) {
                NullsPermittedRule nPR = new NullsPermittedRule(
                                            grammar,
                                            this,
                                            matchIndex,
                                            nullsPermittedRule);
                grammar.derivedRuleQueueAdd(nPR);
                return;
            }
            if (nbrHypParamsUsed == 2) {
                TypeConversionRule tCR =
                    new TypeConversionRule(grammar,
                                           this,
                                           matchIndex,
                                           nullsPermittedRule);
                /**
                 *  Don't convert a hyp to its own type. On derived
                 *  rules just ignore this artifact.
                 */
                if (tCR.getConvTyp() != tCR.getGrammarRuleTyp()) {
                    grammar.derivedRuleQueueAdd(tCR);
                }
                matchIndex = findMatchingVarHypTyp(
                                            (matchIndex + 1),
                                            nullsPermittedRuleTyp);
                if (matchIndex < 0) {
                    return;
                }
                tCR = new TypeConversionRule(grammar,
                                             this,
                                             matchIndex,
                                             nullsPermittedRule);
                /**
                 *  Don't convert a hyp to its own type. On derived
                 *  rules just ignore this artifact.
                 */
                if (tCR.getConvTyp() != tCR.getGrammarRuleTyp()) {
                    grammar.derivedRuleQueueAdd(tCR);
                }
                return;
            }
        }

        /**
         *  OK, formula does have at least one Cnst symbol or
         *  more than 2 VarHyps in use......., so
         *  a NullsPermitted modification can only generate
         *  other NotationRule(s).
         */
        NotationRule nR;
        while (true) {
            nR = new NotationRule(grammar,
                                  this,
                                  matchIndex,
                                  nullsPermittedRule);
            grammar.derivedRuleQueueAdd(nR);
            matchIndex = findMatchingVarHypTyp((matchIndex + 1),
                                               nullsPermittedRuleTyp);
            if (matchIndex < 0) {
                return;
            }
        }
    }


    /**
     *  Derives new grammar rules from an existing NotationRule
     *  and a TypeConversionRule, which will be applied to
     *  matching variable hypothesis nodes in the NotationRule.
     *  <p>
     *  Note: combinations of var hyps are not considered here,
     *        just one application of the TypeConversionRule
     *        per derived rule. The reason is that the new
     *        derived rule goes into the queue and is then
     *        itself used to create new rules (if any). Eventually
     *        all matching combinations are considered without
     *        the complexity of dealing with them here.
     *
     *  @param grammar            The Grammar.
     *  @param typeConversionRule the modifying rule.
     */
    public void  deriveRulesUsingTypeConversion(
                        Grammar               grammar,
                        TypeConversionRule    typeConversionRule) {
        if (nbrHypParamsUsed == 0) {
            return;
        }

        Cnst typeConversionRuleTyp =
             typeConversionRule.getGrammarRuleTyp();

        NotationRule nR;
        int          matchIndex = 0;
        while (true) {
            matchIndex = findMatchingVarHypTyp(matchIndex,
                                               typeConversionRuleTyp);
            if (matchIndex < 0) {
                return;
            }
            nR = new NotationRule(grammar,
                                  this,
                                  matchIndex,
                                  typeConversionRule);
            grammar.derivedRuleQueueAdd(nR);
            ++matchIndex;
        }
    }


    /**
     *  Returns the ruleFormatExpr for the Notation Rule
     *  after retrieving it from the Grammar Rule Tree/Forest.
     *
     *  @return ruleFormatExpr for the NotationRule.
     */
    public Cnst[] getForestRuleExpr() {

        Cnst[] expr = new Cnst[
                            getBaseSyntaxAxiom().getFormula().getCnt()
                            - 1
                            - (paramVarHypNode.length
                                - nbrHypParamsUsed)];
        int dest = expr.length;
        GRNode next = getGRTail();
        while (next != null) {
            expr[--dest] = (next.elementCnst());
            next = next.elementUpLevel();
        }
        return expr;
    }

    /**
     *  Return gRTail for the NotationRule.
     *
     *  @return gRTail for the NotationRule.
     */
    public GRNode getGRTail() {
        return gRTail;
    }

    /**
     *  Set the gRTail for the NotationRule.
     *
     *  @param gRTail for the NotationRule.
     */
    public void setGRTail(GRNode gRTail) {
        this.gRTail = gRTail;
    }

    /**
     *  Return isGimmeMatchNbr for the NotationRule.
     *
     *  @return isGimmeMatchNbr for the NotationRule.
     */
    public int getIsGimmeMatchNbr() {
        return isGimmeMatchNbr;
    }

    /**
     *  Set isGimmeMatchNbr for the NotationRule.
     *
     *  @param isGimmeMatchNbr for the NotationRule.
     */
    public void setIsGimmeMatchNbr(int isGimmeMatchNbr) {
        this.isGimmeMatchNbr =
             isGimmeMatchNbr;
    }

}

