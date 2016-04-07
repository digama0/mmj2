//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * BottomUpParser.java  0.02 08/26/2005
 */

package mmj.verify;

import java.util.List;

import mmj.lang.*;

/**
 * Bottom Up Parser, too slow and stupid for set.mm use.
 *
 * @see <a href="http://www.cs.vu.nl/%7Edick/PTAPG.html"> Parsing Techniques --
 *      A Practical Guide</a>
 * @see <a href="EarleyParser.html">EarleyParser</a>
 */
public class BottomUpParser implements GrammaticalParser {

    private int retryCnt = -1;

    // *******************************************
    // all following variables are work items used
    // within a single execution but are stored
    // globally to avoid having to pass around
    // a bazillion call paramaters.
    // *******************************************

    private int parseCnt = 0;

    private int pStackIndex;
    private int pStackMax;
    private int pStackHighwater;
    private Cnst[][] pStackRFE;
    private int[] pStackRFECnt;
    private int[] pStackFromIndex;
    private int[] pStackThruIndex;
    private NotationRule[] pStackNotationRule;
    private boolean[] pStackIsMatch;
    private boolean[] pStackIsGimme;
    private ParseNodeHolder[] rewrittenExpr;
    private ParseNodeHolder[] paramArray;

    private final Grammar grammar;
    private int maxFormulaLength;

    private ParseTree[] parseTreeArray;
    private Cnst formulaTyp;
    private ParseNodeHolder[] parseNodeHolderExpr;
    private int highestSeq;

    private int governor;
    private int governorLimit;

    /**
     * Construct using reference to Grammar and a parameter signifying the
     * maximum length of a formula in the database.
     *
     * @param grammarIn Grammar object
     * @param maxFormulaLengthIn gives us a hint about what to expect.
     */
    public BottomUpParser(final Grammar grammarIn,
        final int maxFormulaLengthIn)
    {
        grammar = grammarIn;
        maxFormulaLength = maxFormulaLengthIn;
        if (maxFormulaLength < 100)
            maxFormulaLength = 100;
    }

    /**
     * BottomUpParser - returns 'n' = the number of ParseTree objects generated
     * for the input formula and stored in parseTreeArray.
     * <p>
     * The user can control whether or not the first successful parse is
     * returned by passing in an array of length = 1.
     *
     * @param parseTreeArrayIn -- holds generated ParseTrees, therefore, length
     *            must be greater than 0. The user can control whether or not
     *            the first successful parse is returned by passing in an array
     *            of length = 1. If the array length is greater than 1 the
     *            function attempts to fill the array with alternate parses
     *            (which if found, would indicate grammatical ambiguity.)
     *            Returned ParseTree objects are stored in array order -- 0, 1,
     *            2, ... -- and the contents of unused array entries is
     *            unspecified. If more than one ParseTree is returned, the
     *            returned ParseTrees are guaranteed to be different (no
     *            duplicate ParseTrees are returned!)
     * @param formulaTypIn -- Cnst Type Code of the Formula to be parsed. Note
     *            that first symbol is the formula's Type Code. The Type Code is
     *            used only when parsing a null expression (length zero), in
     *            which case a Nulls Permitted Rule is sought.
     * @param parseNodeHolderExprIn - Formula's Expression, preloaded into a
     *            ParseNodeHolder[] (see
     *            Formula.getParseNodeHolderExpr(mandVarHypArray).
     * @param highestSeqIn -- restricts the parse to statements with a sequence
     *            number less than or equal to highestSeq. Set this to
     *            Integer.MAX_VALUE to enable grammatical parsing using all
     *            available rules.
     * @return int -- specifies the number of ParseTree objects stored into
     *         parseTreeArray. A number greater than 1 indicates grammatical
     *         ambiguity, by definition, since there is more than one way to
     *         parse the formula.
     */
    public int parseExpr(final ParseTree[] parseTreeArrayIn,
        final Cnst formulaTypIn, final ParseNodeHolder[] parseNodeHolderExprIn,
        final int highestSeqIn) throws VerifyException
    {
        parseTreeArray = parseTreeArrayIn;
        formulaTyp = formulaTypIn;
        parseNodeHolderExpr = parseNodeHolderExprIn;
        highestSeq = highestSeqIn;

        parseCnt = 0;

        if (parseNodeHolderExpr.length == 0)
            return BottomUpParserSpecialCase1();

        if (parseNodeHolderExpr.length == 1
            && !(parseNodeHolderExpr[0].mObj instanceof Cnst))
            return BottomUpParserSpecialCase2();

        boolean needToRetry = true;
        reInitArrays(0);
        while (needToRetry)
            try {
                parseCnt = 0;
                parse();
                needToRetry = false;
            } catch (final ArrayIndexOutOfBoundsException e) {
                if (++retryCnt > GrammarConstants.MAX_PARSE_RETRIES)
                    throw new IllegalStateException(new VerifyException(
                        GrammarConstants.ERRMSG_MAX_RETRIES_EXCEEDED,
                        GrammarConstants.MAX_PARSE_RETRIES));
                grammar.getMessages().accumException(new VerifyException(e,
                    GrammarConstants.ERRMSG_RETRY_TO_BE_INITIATED, e));
                reInitArrays(retryCnt);
            }

        return parseCnt;
    }

    /**
     * BottomUpParserSpecialCase1 -- Expression length = 0 occurs on Nulls
     * Permitted Syntax Axioms or, in theory, on a Logical Hypothesis, Logical
     * Axiom or Theorem, statements beginning with a valid Theorem TypeCode such
     * as "|-".
     * <p>
     * In both cases, the parse should return a Nulls Permitted result, but the
     * question is, for which Type Code? Answer: if the parsed Formula Type Code
     * is a Provable Logic Statement Type code, then return a Nulls Permitted
     * parse for one of the Logical Statement Type Codes (and if 2 results are
     * possible then that is an ambiguity); otherwise, if the parsed Formula
     * Type Code is not a Provable Logic Statement Type Code, then return a
     * Nulls Permitted parse result for any of the non-Provable Logic Statement
     * Type Codes, which includes the Logical Statement Type Codes (eg. if
     * Formula Type Code is "wff" then if there is a Nulls Permitted Rule for
     * "wff", return that, else no parse is possible.)
     *
     * @return {@code parseCnt}
     */
    private int BottomUpParserSpecialCase1() {

        final ParseNodeHolder[] paramArray = new ParseNodeHolder[0];
        NullsPermittedRule gR;

        if (formulaTyp.isProvableLogicStmtTyp()) {
            final Cnst[] logicStmtTypArray = grammar.getLogicStmtTypArray();
            for (final Cnst element : logicStmtTypArray) {
                gR = element.getNullsPermittedGR();
                if (gR != null && gR.getMaxSeqNbr() <= highestSeq) {
                    parseTreeArray[parseCnt++] = new ParseTree(
                        gR.buildGrammaticalParseNode(paramArray));
                    if (parseCnt >= parseTreeArray.length)
                        break;
                }
            }
        }
        else {
            final List<NullsPermittedRule> nullsPermittedGRList = grammar
                .getNullsPermittedGRList();
            final int i = nullsPermittedGRList.indexOf(formulaTyp);
            if (i != -1) {
                gR = nullsPermittedGRList.get(i);
                if (gR.getMaxSeqNbr() <= highestSeq)
                    parseTreeArray[parseCnt++] = new ParseTree(
                        gR.buildGrammaticalParseNode(paramArray));
            }
        }
        return parseCnt;
    }

    /**
     * BottomUpParserSpecialCase2 -- Special Case 2) Expression with Length = 1
     * containing just a Variable occurs on Type Conversion Syntax Axioms, on
     * Variable Hypothesis Statements and on Logical Statements (see ax-mp in
     * set(dot)mm).
     * <p>
     * If the parsed Formula's Type Code is a Provable Logic Statement Type Code
     * then the parse result should be just a Variable Hypothesis parse tree --
     * otherwise, the parse result should be a Type Conversion parse tree (NOTE:
     * when parsing a Metamath database in its entirety, we do not send variable
     * hypotheses through the parser, therefore we ignore that scenario in this
     * special case.)
     *
     * @return {@code parseCnt}
     */
    private int BottomUpParserSpecialCase2() {

        TypeConversionRule gR;

        if (formulaTyp.isProvableLogicStmtTyp())
            parseTreeArray[parseCnt++] = new ParseTree(
                parseNodeHolderExpr[0].parseNode);
        else {
            gR = formulaTyp.findFromTypConversionRule(
                ((VarHyp)parseNodeHolderExpr[0].mObj).getTyp());
            if (gR != null && gR.getMaxSeqNbr() <= highestSeq)
                parseTreeArray[parseCnt++] = new ParseTree(
                    gR.buildGrammaticalParseNode(parseNodeHolderExpr));
        }
        return parseCnt;
    }

    private void parse() throws VerifyException {

        pStackFromIndex[0] = 0;
        pStackThruIndex[0] = 0;
        pStackNotationRule[0] = null;
        pStackIsMatch[0] = false;
        pStackIsGimme[0] = false;

        /**
         * Initial pass turns input parse expression into "rule format"
         * containing Type Codes instead of variable hypotheses.
         */

        Cnst cnst;
        for (int i = 0; i < parseNodeHolderExpr.length; i++)
            if (parseNodeHolderExpr[i].mObj instanceof Cnst) {
                cnst = (Cnst)parseNodeHolderExpr[i].mObj;
                if (cnst.isGrammaticalTyp())
                    throw new VerifyException(
                        GrammarConstants.ERRMSG_EXPR_USES_TYP_AS_CNST,
                        cnst.toString());
                else
                    pStackRFE[0][i] = (Cnst)parseNodeHolderExpr[i].mObj;
            }
            else
                pStackRFE[0][i] = parseNodeHolderExpr[i].parseNode.stmt
                    .getTyp();
        pStackRFECnt[0] = parseNodeHolderExpr.length;
        pStackIndex = 0;

        governor = 0;
        governorLimit = parseNodeHolderExpr.length
            * GrammarConstants.BOTTOM_UP_GOVERNOR_LIMIT;
        if (parseTreeArray.length > 1)
            governorLimit *= GrammarConstants.BOTTOM_UP_GOVERNOR_LIMIT_MAX;

        do {
            if (++governor > governorLimit)
                throw new VerifyException(
                    GrammarConstants.ERRMSG_BU_GOVERNOR_LIMIT_EXCEEDED,
                    governorLimit);

            if (findMatch()) {
                if (pStackRFECnt[pStackIndex] == pStackThruIndex[pStackIndex]
                    - pStackFromIndex[pStackIndex] + 1)
                {
                    final ParseTree pTree = generateParseTree();
                    if (!pTree.isDup(parseCnt, parseTreeArray)) {
                        parseTreeArray[parseCnt++] = pTree;
                        if (parseCnt >= parseTreeArray.length)
                            break;
                    }
                }
                else {
                    applyNextNotationRuleMatch();
                    pStackIndex++;
                }
            }
            else
                pStackIndex--;
        } while (pStackIndex >= 0);

    }

    private boolean findMatch() {

        while (pStackIsGimme[pStackIndex])
            if (--pStackIndex < 0)
                return false;

        if (pStackIsMatch[pStackIndex])
            return findNonGimmeMatch();
        else {
            if (findGimmeMatch())
                return true;
            return findNonGimmeMatch();
        }
    }

    private boolean findNonGimmeMatch() {

        int left;
        int curr;
        GRNode currLevelRoot;
        GRNode foundLevelNode;
        NotationRule foundNotationRule;
        final Cnst[] rfe = pStackRFE[pStackIndex];
        if (!pStackIsMatch[pStackIndex]) {
            left = 0;
            curr = 0;
            currLevelRoot = rfe[0].getGRRoot();
        }
        else {
            left = pStackFromIndex[pStackIndex];
            curr = pStackThruIndex[pStackIndex] + 1;
            currLevelRoot = pStackNotationRule[pStackIndex].getGRTail()
                .elementDownLevelRoot();
        }

        do {
            do {
                if (curr >= pStackRFECnt[pStackIndex] || currLevelRoot == null)
                    break;
                if ((foundLevelNode = currLevelRoot.find(rfe[curr])) == null)
                    break;
                foundNotationRule = foundLevelNode.elementNotationRule();
                if (foundNotationRule != null
                    && foundNotationRule.getIsGimmeMatchNbr() != 1
                    && foundNotationRule.getMaxSeqNbr() <= highestSeq)
                {
                    pStackNotationRule[pStackIndex] = foundNotationRule;
                    pStackFromIndex[pStackIndex] = left;
                    pStackThruIndex[pStackIndex] = curr;
                    pStackIsMatch[pStackIndex] = true;
                    pStackIsGimme[pStackIndex] = false;

                    return true;
                }
                curr++;
                currLevelRoot = foundLevelNode.elementDownLevelRoot();
            } while (true);

            if (++left >= pStackRFECnt[pStackIndex])
                break;
            curr = left;
            currLevelRoot = rfe[curr].getGRRoot();
        } while (true);

        return false;
    }

    private boolean findGimmeMatch() {
        int left;
        int curr;
        GRNode currLevelRoot;
        GRNode foundLevelNode;
        NotationRule foundNotationRule;
        final Cnst[] rfe = pStackRFE[pStackIndex];
        if (!pStackIsMatch[pStackIndex]) {
            left = 0;
            curr = 0;
            currLevelRoot = rfe[0].getGRRoot();
        }
        else {
            left = pStackFromIndex[pStackIndex];
            curr = pStackThruIndex[pStackIndex] + 1;
            currLevelRoot = pStackNotationRule[pStackIndex].getGRTail()
                .elementDownLevelRoot();
        }

        while (true) {
            while (curr < pStackRFECnt[pStackIndex] && currLevelRoot != null
                && (foundLevelNode = currLevelRoot.find(rfe[curr])) != null)
            {
                foundNotationRule = foundLevelNode.elementNotationRule();
                if (foundNotationRule != null
                    && foundNotationRule.getIsGimmeMatchNbr() == 1
                    && foundNotationRule.getMaxSeqNbr() <= highestSeq)
                {

                    pStackNotationRule[pStackIndex] = foundNotationRule;
                    pStackFromIndex[pStackIndex] = left;
                    pStackThruIndex[pStackIndex] = curr;
                    pStackIsMatch[pStackIndex] = true;
                    pStackIsGimme[pStackIndex] = true;
                    return true;
                }
                curr++;
                currLevelRoot = foundLevelNode.elementDownLevelRoot();
            }

            if (++left >= pStackRFECnt[pStackIndex])
                return false;
            curr = left;
            currLevelRoot = rfe[curr].getGRRoot();
        }
    }

    private void applyNextNotationRuleMatch() {

        final int nextEntry = pStackIndex + 1;

        final Cnst[] rfe = pStackRFE[pStackIndex];
        final Cnst[] rewrittenExpr = pStackRFE[nextEntry];

        pStackRFECnt[nextEntry] = pStackRFECnt[pStackIndex]
            - (pStackThruIndex[pStackIndex] - pStackFromIndex[pStackIndex]);
        pStackFromIndex[nextEntry] = 0;
        pStackThruIndex[nextEntry] = 0;
        pStackNotationRule[nextEntry] = null;
        pStackIsMatch[nextEntry] = false;
        pStackIsGimme[nextEntry] = false;

        int src = 0;
        int dest = 0;

        while (src < pStackFromIndex[pStackIndex])
            rewrittenExpr[dest++] = rfe[src++];

        rewrittenExpr[dest++] = pStackNotationRule[pStackIndex]
            .getGrammarRuleTyp();
        src = pStackThruIndex[pStackIndex] + 1;

        while (src < pStackRFECnt[pStackIndex])
            rewrittenExpr[dest++] = rfe[src++];
        return;
    }

    private ParseTree generateParseTree() {

        for (int i = 0; i < parseNodeHolderExpr.length; i++)
            rewrittenExpr[i] = parseNodeHolderExpr[i];

        int from;
        int thru;
        int exprCnt;
        int j;
        int src;
        int dest;
        for (int stackIndex = 0; stackIndex <= pStackIndex; stackIndex++) {
            from = pStackFromIndex[stackIndex];
            thru = pStackThruIndex[stackIndex];
            exprCnt = pStackRFECnt[stackIndex];

            // load var hyps and previously generated parse node
            // holders int paramArray
            j = 0;
            for (int i = from; i <= thru; i++)
                if (!(rewrittenExpr[i].mObj instanceof Cnst))
                    paramArray[j++] = rewrittenExpr[i];

            // store new ParseNodeHolder for the Notation Rule Match
            dest = from;
            rewrittenExpr[dest++] = new ParseNodeHolder(
                pStackNotationRule[stackIndex]
                    .buildGrammaticalParseNode(paramArray));

            // shift left all remaining elements
            src = thru + 1;
            if (src != dest)
                while (src < exprCnt)
                    rewrittenExpr[dest++] = rewrittenExpr[src++];
        }

        // the final rewrittenExpr has one element, and all we
        // want from it is its ParseNode, the root of our new tree.
        return new ParseTree(rewrittenExpr[0].parseNode);
    }

    private void reInitArrays(final int retry) throws VerifyException {
        if (retryCnt == -1) {
            retryCnt = 0;
            initArrays(maxFormulaLength);
            return;
        }
        retryCnt = retry;

        if (pStackIndex > pStackHighwater)
            pStackHighwater = pStackIndex;

        if (retryCnt == 0) {
            pStackIndex = -1;
            return;
        }

        if (pStackMax < GrammarConstants.BOTTOM_UP_STACK_HARD_FAILURE_MAX) {
            pStackMax *= 2;
            if (pStackMax > GrammarConstants.BOTTOM_UP_STACK_HARD_FAILURE_MAX)
                pStackMax = GrammarConstants.BOTTOM_UP_STACK_HARD_FAILURE_MAX;
            initArrays(pStackMax);
        }
        else
            throw new VerifyException(
                GrammarConstants.ERRMSG_BU_PARSE_STACK_OVERFLOW, pStackMax);

    }

    private void initArrays(final int max) {

        pStackIndex = -1;
        pStackMax = max;
        pStackHighwater = 0;
        pStackRFE = new Cnst[pStackMax][];
        for (int i = 0; i < pStackMax; i++)
            pStackRFE[i] = new Cnst[pStackMax];
        pStackRFECnt = new int[pStackMax];
        pStackFromIndex = new int[pStackMax];
        pStackThruIndex = new int[pStackMax];
        pStackNotationRule = new NotationRule[pStackMax];
        pStackIsMatch = new boolean[pStackMax];
        pStackIsGimme = new boolean[pStackMax];

        rewrittenExpr = new ParseNodeHolder[pStackMax];
        paramArray = new ParseNodeHolder[pStackMax];
    }
}
