//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  GrammarAmbiguity.java  0.02 08/27/2005
 */

package mmj.verify;
import mmj.lang.*;
import java.util.*;

/**
 *  GrammarAmbiguity was separated out from Grammar because
 *  a) Grammar was already getting too long, and b) the
 *  topic of "ambiguity" is large and the code will likely
 *  be a work-in-progress (until someone has a brainstorm).
 *
 *  At present, there are two levels of Grammar Ambiguity
 *  validation: basic and complete (aka "full"). The distinction
 *  between the two levels is somewhat arbitrary, but has to
 *  do with speed: a user does not need to validate her
 *  grammar every time the program is run. So "basic" is just
 *  the basics: are any grammar rules parseable using other
 *  grammar rules? The "complete" ambiguity checking process
 *  for a grammar may well prove to be quite time-consuming,
 *  involving many heuristics and passes through the grammar!
 *
 *  As of August, 2005, the "complete" level is stubbed out,
 *  except for a couple of little things. It will be a high
 *  priority project in the near future.
 *
 *  @see <a href="../../ConsolidatedListOfGrammarValidations.html">
 *       ConsolidatedListOfGrammarValidations.html</a>
 *  @see <a href="../../BasicsOfSyntaxAxiomsAndTypes.html">
 *       BasicsOfSyntaxAxiomsAndTypes.html</a>
 *  @see <a href="../../EssentialAmbiguityExamples.html">
 *       EssentialAmbiguityExamples.html</a>
 *  @see <a href="../../CreatingGrammarRulesFromSyntaxAxioms.html">
 *       CreatingGrammarRulesFromSyntaxAxioms.html</a>
 *  @see <a href="../../MetamathERNotes.html">
 *       Nomenclature and Entity-Relationship Notes</a>
 */
public class GrammarAmbiguity {

    private  Grammar grammar;
    private  boolean doCompleteGrammarAmbiguityEdits;
    private  boolean errorsFound;

    public GrammarAmbiguity(
                Grammar grammar,
                boolean doCompleteGrammarAmbiguityEdits) {
        this.grammar                            = grammar;
        this.doCompleteGrammarAmbiguityEdits    =
             doCompleteGrammarAmbiguityEdits;
        errorsFound                             = false;
    }

    /**
     *  Right now this has just two edits, a warning about
     *  undefined non-terminals and an informational message
     *  that a grammar like peano.mm is unambiguous because
     *  every NotationRule is a "gimme match".
     *
     *  @return false if errors found, true is no errors found.
     */
    public boolean fullAmbiguityEdits() {

        if (grammar.getVarHypTypSet().size() !=
            grammar.getSyntaxAxiomTypSet().size()) {
            grammar.accumInfoMsgInList(
                GrammarConstants.ERRMSG_UNDEF_NON_TERMINAL_1
                    + grammar.getSyntaxAxiomTypSet().size()
                    + GrammarConstants.ERRMSG_UNDEF_NON_TERMINAL_2
                    + grammar.getVarHypTypSet().size()
                    + GrammarConstants.ERRMSG_UNDEF_NON_TERMINAL_3);
        }

        if (grammar.getNotationGRSet().size() ==
            grammar.getNotationGRGimmeMatchCnt()) {
            grammar.accumInfoMsgInList(
                GrammarConstants.ERRMSG_GRAMMAR_UNAMBIGUOUS);
        }

        //stopped here
        return !errorsFound;
    }


    /**
     *  The Primary Objective of basicAmbiguityEdits is to make
     *  a pass through Grammar's NotationRule Set and attempt
     *  to parse each Notation Grammar Rule using a Max
     *  Sequence number of Integer(dot)MAX_VALUE to absolutely
     *  positively confirm that none of the NotationRules is
     *  parseable using other NotationRules.
     *  <p>
     *  During the initial pass through Grammar's NotationRule
     *  Set, useful information related to the rules is collected
     *  for subsequent use in ambiguity checking. This information
     *  is quite interesting and goes to the heart of the
     *  question of what makes a grammar (un)ambiguous -- embedding
     *  and overlapping notation rules (there are also deep
     *  mysteries to be pursued involving all-constant Notation
     *  Axioms which can embody characteristics of both constant
     *  "literals" and grammatical types, but those are not
     *  dealt with here.)
     *
     *  <p>
     *  Secondary Objective: set each NotationRule.isGimmeMatchNbr to
     *  -1 if rule is *not* a "gimme match" or +1 if it is (zero
     *  indicates that the question has not yet been decided.)
     *  HOWEVER, there must be no intrinsic ambiguity in the
     *  rules themselves -- meaning that no grammar rule can
     *  be parsed using other grammar rules (and no duplicates.)
     *
     *  <p>
     *  Tertiary Objective: if doCompleteGrammarAmbiguityEdits
     *  is set to true then intermediate results from this routine
     *  should be stored and made available to
     *  fullEdits(). Specifically, the non-gimme
     *  match Notation Rules will require deep investigations
     *  of their potential ambiguity and it is desireable that
     *  these results from this routine not be wasted:
     *  <p>
     *      <ul>
     *      <li> Notation Rules that "overlap"
     *      <li> Notation Rules with "embedding".
     *      </ul>
     *  <p>
     *  (these are not mutually exclusive since Rule A may be
     *  very short and be repeated more than once in Rule B.)
     *
     *  <p>
     *  <b>Overlap</b>:
     *  <p>
     *      <ul>
     *      <li> a suffix portion of a rule matches the prefix
     *           portion of another rule, possibly itself.
     *
     *      <li> At an extreme, suffix overlap with prefix would
     *           mean embedding (all of rule A overlaps rule B),
     *           so we make the definition that Overlap means
     *           that "suffix" and "prefix" overlap less than
     *           the entire rule.
     *      </ul>
     *  <br>
     *  <p>
     *  <b>Embedding</b>:
     *  <p>
     *      <ul>
     *      <li> An entire rule is contained within another
     *           rule. This is *not* necessarily an error
     *           and may not be ambiguous; recall that before
     *           getting to this routine the Rules have already
     *           been validated -- they are not duplicates and
     *           cannot be parsed into other rules.
     *
     *      <li> At an extreme the embedded Rule, A, may not be
     *           surrounded by symbols from Rule B; thus,
     *           A matches a prefix of B or A matches
     *           a suffix of B. This is called "recursion",
     *           either "left recursion" or "right recursion",
     *           respectively. But, as we shall see, recursion
     *           precludes a rule from being a "gimme", so
     *           we shall define recursion to be just a form
     *           of Embedding (there are grammatical parsing
     *           implications of recursion, but for now we will
     *           treat Embedding and Recursion as the same
     *           thing.)
     *      </ul>
     *  <br>
     *
     *  <p>
     *  <b>On Recursion</b>:
     *  <p>
     *      <ul>
     *      <li> an entire rule matches the prefix of another
     *           rule -- left recursion -- or the suffix of
     *           another rule -- right recursion.
     *      </ul>
     *
     *  <br>
     *  <p>
     *  <b>More on Gimme Matches</b>:
     *
     *  <p>
     *  For the purposes of identifying a gimme we need to show that
     *  a rule has no overlaps and is neither embedded in nor contains
     *  an embedded rule, and is not recursive. We already know that
     *  it is not a duplicate rule and cannot itself be grammatically
     *  parsed into other rules.)
     *
     *  <p>
     *  <b>More on Recursion -- cannot be Gimme Matches!</b>:
     *
     *  <p>
     *  The fact that a rule is recursive DOES mean
     *  that it is *not* a gimme match. For example, if we
     *  have rules #1: A -> AB and #2: A -> ABB, then #2 is already
     *  in error because it is parseable (into #1(#1(a, b), b)))!
     *  Or consider these other examples:
     *
     *  <p>
     *
     *  <ul>
     *  <li><code>#1: A -> (A) and #2: A -> (A)B .... = not gimmes!</code>
     *  <li><code>#2: A -> A * and #2: A -> A * B ... = not gimmes!</code>
     *  <li><code>#3: A -> * A and #2: A -> * A B ... = not gimmes!</code>
     *  <li><code>#4: A -> * A and #2: A -> * * A ... = error!</code>
     *  <li><code>#5: A -> * A and #2: A -> + * A ... = not gimmes!</code>
     *  </ul>
     *
     *  <br>
     *
     *  <p>
     *  So these *direct* recursion rules are either ambiguous
     *  or errors, but theye are *not* gimme matches in any case.
     *
     *  <p>
     *  Note: *Indirect* recursions are possible. For example:
     *  <p>
     *  <ul>
     *  <code>
     *  <li><code>#1: E -> A *</code>
     *  <li><code>#2: A -> E * B</code>
     *  </ul>
     *  <p>
     *  which, depending on the set of other rules could mean
     *  both #1 and #2 are "gimme" matches. If there is Type
     *  Conversion <code>#3: E -> A</code> then #1 would turn
     *  out to be a non-gimme because a variant of #2 would
     *  have been generated, <code>#2.1: A -> A * B</code>.
     *
     *  @return false if errors found, true is no errors found.
     */
    public boolean basicAmbiguityEdits() {

        boolean             errorsFound    = false;

        Iterator            iterI;
        Iterator            iterJ;

        NotationRule        rI;
        NotationRule        rJ;

        ParseNodeHolder[]   parseNodeHolderExpr;
        Axiom               baseSyntaxAxiom;
        String              errmsg;

        Cnst[]              exprI;
        Cnst[]              exprJ;

        int                 maxPfxI;
        int                 maxSfxJ;
        int                 maxPfx;
        int                 lenPfxI;
        int                 embedPosJ;

        boolean             isOverlapIJ;
        boolean             isEmbedIJ;

        iterI = grammar.getNotationGRSet().iterator();
        loopI: while (iterI.hasNext()) {
            rI = (NotationRule)iterI.next();
            if (rI.getIsGimmeMatchNbr() == 1) {
                continue loopI;
            }

            /**
             *  try to parse Grammar Rule -- should not be
             *  possible
             */
            parseNodeHolderExpr = rI.getParseNodeHolderExpr();
            baseSyntaxAxiom     = rI.getBaseSyntaxAxiom();
            try {
                errmsg =
                    grammar.grammaticalParseSyntaxExpr(
                             baseSyntaxAxiom.getFormula().getTyp(),
                             parseNodeHolderExpr,
                             Integer.MAX_VALUE,
                             baseSyntaxAxiom.getLabel());
                if (errmsg != null) {
                    grammar.accumInfoMsgInList(errmsg);
                }
            }
            catch (VerifyException e) {
                grammar.accumErrorMsgInList(
                    e.getMessage()
                    + GrammarConstants.ERRMSG_LABEL_CAPTION
                    + baseSyntaxAxiom.getLabel());
                errorsFound = true;
            }

            /**
             *  OK, now continue checking for embeds and
             *  overlaps...
             */
            exprI   = rI.getRuleFormatExpr();

            maxPfxI = exprI.length - 1;

            iterJ = grammar.getNotationGRSet().iterator();
            loopJ: while (iterJ.hasNext()) {
                rJ      = (NotationRule)iterJ.next();
                if (rJ.getIsGimmeMatchNbr() == 1) {
                    continue loopJ;
                }
                exprJ   = rJ.getRuleFormatExpr();
                maxSfxJ = exprJ.length - 1;

                maxPfx  = (maxSfxJ < maxPfxI) ? maxSfxJ : maxPfxI;

                /**
                 *  OK, compare all prefixes of I with suffixes of J
                 *  (overlappling) until length of I is reached,
                 *  then compare I with contents of J (embedding)
                 */
                isOverlapIJ = false;
                overlapLoop: for (lenPfxI = 1;
                                  lenPfxI <= maxPfx;
                                  lenPfxI++) {
                    if (doesIPfxOverlapJSfx(exprI,
                                            lenPfxI,
                                            exprJ)) {
                        isOverlapIJ = true;
                        if (!doCompleteGrammarAmbiguityEdits) {
                            break overlapLoop;
                        }
                        recordOverlap(rI,
                                      exprI,
                                      lenPfxI,
                                      rJ,
                                      exprJ);
                    }
                }

                /**
                 *  OK, unless I == J, see if I is embedded in J.
                 *  NOTE: length of I must be < length of J
                 *  because *otherwise* either I is too long to
                 *  be embedded, or if their lengths are equal
                 *  and I is embedded in J, then they would be
                 *  duplicates and we have already established
                 *  that there are no duplicates! So...
                 *  we don't actually have to check for I != J.
                 */
                isEmbedIJ = false;
                if (exprI.length < exprJ.length) {
                    embedLoop: for (embedPosJ =
                                        exprJ.length - exprI.length;
                                    embedPosJ >= 0;
                                    embedPosJ--) {
                        if (isIEmbeddedInJ(exprI,
                                           exprJ,
                                           embedPosJ)) {
                            isEmbedIJ = true;
                            if (!doCompleteGrammarAmbiguityEdits) {
                                break embedLoop;
                            }
                            recordEmbed(rI,
                                        exprI,
                                        rJ,
                                        exprJ,
                                        embedPosJ);
                        }
                    }
                }

                if (isOverlapIJ ||
                    isEmbedIJ)  {
                    rI.setIsGimmeMatchNbr(-1);
                    rJ.setIsGimmeMatchNbr(-1);
                }
            }
        }

        /**
         *  Count the GimmeMatches while updating the
         *  Gimme's in the Notation GR list.
         */
        grammar.setNotationGRGimmeMatchCnt(0);
        iterI = grammar.getNotationGRSet().iterator();
        int gimmeMatchNbr;
        while (iterI.hasNext()) {
            rI            = (NotationRule)iterI.next();
            gimmeMatchNbr = rI.getIsGimmeMatchNbr();
            if (gimmeMatchNbr < 0) {
                // definitely NOT
            }
            else {
                if (gimmeMatchNbr == 1) {
                    grammar.incNotationGRGimmeMatchCnt();
                }
                else {
                    if (!errorsFound) {
                        // by process of elimination...
                        rI.setIsGimmeMatchNbr(1);
                        grammar.incNotationGRGimmeMatchCnt();
                    }
                }
            }
        }

        return !errorsFound;
    }

    private   boolean doesIPfxOverlapJSfx(Cnst[] exprI,
                                       int    lenPfxI,
                                       Cnst[] exprJ) {
        for (int posJ = exprJ.length - lenPfxI,
                 posI = 0;
             posI < lenPfxI;
             posI++, posJ++) {
            if (exprI[posI] != exprJ[posJ]) {
                return false;
            }
        }
        return true;
    }

    private   boolean isIEmbeddedInJ(Cnst[] exprI,
                                  Cnst[] exprJ,
                                  int    embedPosJ) {
        for (int posI = 0; posI < exprI.length; posI++, embedPosJ++) {
            if (exprI[posI] != exprJ[embedPosJ]) {
                return false;
            }
        }
        return true;
    }

    /**
     *  recordOverlap --
     *  Record fact that prefix with length "lenPfx" of rule
     *  "rI" overlaps suffix of rule "rJ".
     *  Note: rI and rJ may be the same rule!
     *
     */
    private   void recordOverlap(NotationRule rI,
                                 Cnst[]       exprI,
                                 int          lenPfx,
                                 NotationRule rJ,
                                 Cnst[]       exprJ) {
        //stopped here
    }


    private   void recordEmbed(NotationRule rI,
                               Cnst[]       exprI,
                               NotationRule rJ,
                               Cnst[]       exprJ,
                               int          embedPosJ) {
        //stopped here
    }


}
