//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * LRParser.java  0.01 1/06/2016
 */

package mmj.verify;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.*;

/**
 * LR Parser
 */
public class LRParser implements GrammaticalParser {

    private final Grammar grammar;
    private int notationRuleCnt = -1;

    private final Map<ParseSet, Integer> setLookup = new HashMap<ParseSet, Integer>();
    private final List<ParseSet> sets = new ArrayList<ParseSet>();
    private final List<Map<Cnst, Integer>> transitions = new ArrayList<Map<Cnst, Integer>>();
    private final List<NotationRule> reductions = new ArrayList<NotationRule>();
    private final Map<Cnst, Integer> startStates = new HashMap<Cnst, Integer>();
    private final Map<Cnst, List<NotationRule>> rulesByTyp = new HashMap<Cnst, List<NotationRule>>();
    private final Map<Cnst, Set<Cnst>> nontermClosure = new HashMap<Cnst, Set<Cnst>>();
    private final Map<Cnst, Set<Cnst>> initialByTyp = new HashMap<Cnst, Set<Cnst>>();
    private final Map<Cnst, Set<Cnst>> followByTyp = new HashMap<Cnst, Set<Cnst>>();
    private final Deque<Integer> conflicts = new ArrayDeque<Integer>();
    private final List<Set<Integer>> backtrack = new ArrayList<Set<Integer>>();
    private final Map<ParseTree, NotationRule> newRules = new HashMap<ParseTree, NotationRule>();

    /**
     * Construct using reference to Grammar and a parameter signifying the
     * maximum length of a formula in the database.
     *
     * @param grammarIn Grammar object
     * @param maxFormulaLengthIn gives us a hint about what to expect.
     */
    public LRParser(final Grammar grammarIn, final int maxFormulaLengthIn) {
        grammar = grammarIn;
    }

    private void initialize() {
        for (final Cnst typ : grammar.getVarHypTypSet()) {
            rulesByTyp.put(typ, new ArrayList<NotationRule>());
            nontermClosure.put(typ, new HashSet<Cnst>());
            initialByTyp.put(typ, new HashSet<Cnst>());
            followByTyp.put(typ, new HashSet<Cnst>());
        }
        final Set<NotationRule> notationRules = grammar.getNotationGRSet();
        notationRuleCnt = notationRules.size();
        for (final NotationRule notationRule : notationRules) {
            final Cnst typ = notationRule.getGrammarRuleTyp();
            rulesByTyp.get(typ).add(notationRule);
            if (notationRule.getIsGimmeMatchNbr() != 1) {
                final Cnst head = notationRule.getRuleFormatExprFirst();
                if (head.isVarTyp())
                    nontermClosure.get(typ).add(head);
                else
                    initialByTyp.get(typ).add(head);
            }
        }
        boolean changed;
        do {
            changed = false;
            for (final Entry<Cnst, Set<Cnst>> e : nontermClosure.entrySet())
                for (final Cnst c : new ArrayList<Cnst>(e.getValue()))
                    changed |= e.getValue().addAll(nontermClosure.get(c));
        } while (changed);
        for (final Cnst typ : grammar.getVarHypTypSet())
            for (final Cnst c : nontermClosure.get(typ))
                initialByTyp.get(typ).addAll(initialByTyp.get(c));
        do {
            changed = false;
            for (final NotationRule notationRule : notationRules) {
                final Cnst[] ruleFormatExpr = notationRule.getRuleFormatExpr();
                for (int i = 0; i < ruleFormatExpr.length; i++)
                    if (ruleFormatExpr[i].isVarTyp()) {
                        final Set<Cnst> set = followByTyp
                            .get(ruleFormatExpr[i]);
                        if (i + 1 == ruleFormatExpr.length)
                            changed |= set.addAll(followByTyp
                                .get(notationRule.getGrammarRuleTyp()));
                        else {
                            final Cnst c = ruleFormatExpr[i + 1];
                            changed |= c.isVarTyp()
                                ? set.addAll(followByTyp.get(c)) : set.add(c);
                        }
                    }
            }
        } while (changed);

        for (final Cnst typ : grammar.getVarHypTypSet()) {
            final ParseSet set = new ParseSet();
            set.initials.add(typ);
            makeClosure(set);
            startStates.put(typ, Integer.valueOf(getState(set)));
        }

        while (!conflicts.isEmpty()) {
            final int index = conflicts.pop();
            Set<Integer> backStates = new TreeSet<Integer>();
            backStates.add(index);
            final NotationRule reduce = reductions.get(index);
            for (int i = reduce.getRuleFormatExpr().length; i > 0; i--) {
                final Set<Integer> newStates = new TreeSet<Integer>();
                for (final int j : backStates)
                    newStates.addAll(backtrack.get(j));
                backStates = newStates;
            }
            final Set<Cnst> badTokens = transitions.get(index).keySet();
            for (final int back : backStates) {
                final int fwd = transitions.get(back)
                    .get(reduce.getGrammarRuleTyp());
                boolean bad = false;
                for (final Cnst head : badTokens)
                    bad |= transitions.get(fwd).containsKey(head);
                if (bad) {
                    final ParseSet goal = new ParseSet();
                    final Cnst typ = reduce.getGrammarRuleTyp();
                    final Cnst dir = reduce.getRuleFormatExprFirst();
                    for (final ParseState state : sets.get(back)) {
                        final Cnst head = state.head();
                        if (head.equals(dir) && state.rule != reduce)
                            goal.add(state.advance());
                        else if (head.equals(typ)) {
                            final Cnst[] expr = state.rule.getRuleFormatExpr();
                            int matchIndex = 0;
                            for (int i = 0; i < state.position; i++)
                                if (expr[i].isVarTyp())
                                    matchIndex++;
                            final NotationRule construct = new NotationRule(
                                grammar, state.rule, matchIndex, reduce);
                            NotationRule derivedRule = newRules
                                .get(construct.getParamTransformationTree());
                            if (derivedRule == null) {
                                derivedRule = construct;
                                derivedRule.setRuleFormatExpr(
                                    derivedRule.buildRuleFormatExpr());
                                newRules.put(
                                    derivedRule.getParamTransformationTree(),
                                    derivedRule);
                            }
                            goal.add(ParseState.get(derivedRule,
                                state.position + 1));
                        }
                    }
                    makeClosure(goal);
                    final int goalIndex = getState(goal);
                    transitions.get(back).put(dir, goalIndex);
                    backtrack.get(goalIndex).add(back);
                }
            }
        }

        // Release resources after generation
        // sets.clear();
        rulesByTyp.clear();
        ParseState.clearCache();
    }

    private int getState(final ParseSet set) {
        Integer index = setLookup.get(set);
        if (index == null) {
            index = sets.size();
            sets.add(set);
            setLookup.put(set, index);
            final HashMap<Cnst, Integer> map = new HashMap<Cnst, Integer>();
            transitions.add(map);
            NotationRule reduce = null;
            reductions.add(null);
            backtrack.add(new TreeSet<Integer>());
            for (final ParseState e : set) {
                final Cnst head = e.head();
                if (head == null) {
                    if (reduce != null)
                        grammar.accumInfoMsgInList(
                            "reduce/reduce conflict: " + set + " => \"" + reduce
                                + "\" / \"" + e.rule + "\"");
                    else
                        reduce = e.rule;
                }
                else if (!map.containsKey(head)) {
                    final ParseSet goal = new ParseSet();
                    for (final ParseState state : set)
                        if (head.equals(state.head()))
                            goal.add(state.advance());
                    makeClosure(goal);
                    final int goalIndex = getState(goal);
                    map.put(head, goalIndex);
                    backtrack.get(goalIndex).add(index);
                    if (reduce != null && followByTyp
                        .get(reduce.getGrammarRuleTyp()).contains(head))
                        conflicts.add(index);
                }
            }
            reductions.set(index, reduce);
        }
        return index;
    }

    private void makeClosure(final ParseSet set) {
        for (final Cnst head : set.initials)
            set.initials.addAll(nontermClosure.get(head));
        for (final ParseState state : set.extra) {
            final Cnst head = state.head();
            if (head != null && head.isVarTyp() && set.initials.add(head))
                set.initials.addAll(nontermClosure.get(head));
        }
        /*
        final Deque<ParseState> waiting = new ArrayDeque<ParseState>(set);
        final Set<Cnst> addedNonterms = new HashSet<Cnst>();
        while (!waiting.isEmpty()) {
            final ParseState state = waiting.pop();
            final Cnst head = state.head();
            if (head != null && head.isGrammaticalTyp()
                && addedNonterms.add(head))
                for (final NotationRule rule : rulesByTyp.get(head)) {
                    final ParseState to = ParseState.get(rule);
                    if (set.add(to))
                        waiting.add(to);
                }
        }*/
    }

    /**
     * LRParser - returns 'n' = the number of ParseTree objects generated for
     * the input formula and stored in parseTreeArray.
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
        // Special case: single variable
        if (parseNodeHolderExprIn.length == 1
            && parseNodeHolderExprIn[0].mObj instanceof VarHyp)
        {
            parseTreeArrayIn[0] = new ParseTree(
                new ParseNode((VarHyp)parseNodeHolderExprIn[0].mObj));
            return 1;
        }

        // Check notation rules to make sure the grammar has not changed since
        // last initialization
        if (notationRuleCnt != grammar.getNotationGRSet().size())
            initialize();
        int index = 0;
        final Deque<Integer> stateStack = new ArrayDeque<Integer>();
        final ArrayDeque<ParseNode> outStack = new ArrayDeque<ParseNode>();
        Cnst startRuleTyp = formulaTypIn;
        if (formulaTypIn.isProvableLogicStmtTyp())
            if (grammar.getLogicStmtTypArray().length > 0)
                startRuleTyp = grammar.getLogicStmtTypArray()[0];
            else
                throw new IllegalStateException(
                    GrammarConstants.ERRMSG_START_RULE_TYPE_UNDEF_1
                        + formulaTypIn
                        + GrammarConstants.ERRMSG_START_RULE_TYPE_UNDEF_2);
        stateStack.push(startStates.get(startRuleTyp));
        while (true) {
            final ParseNodeHolder lookahead = index < parseNodeHolderExprIn.length
                ? parseNodeHolderExprIn[index] : null;
            final int state = stateStack.peek().intValue();
            Integer transition;
            NotationRule reduce;
            if (lookahead != null && (transition = transitions.get(state)
                .get(lookahead.getCnstOrTyp())) != null)
            {
                if (!(lookahead.mObj instanceof Cnst)) {
                    final VarHyp hyp = lookahead.mObj instanceof Var
                        ? ((Var)lookahead.mObj).getActiveVarHyp()
                        : (VarHyp)lookahead.mObj;
                    outStack.push(new ParseNode(hyp));
                }
                stateStack.push(transition);
                index++;
            }
            else if ((reduce = reductions.get(state)) != null) {
                outStack.push(reduce.getParamTransformationTree().getRoot()
                    .deepCloneApplyingStackSubst(outStack));
                for (int i = reduce.getRuleFormatExpr().length; i > 0; i--)
                    stateStack.pop();
                transition = transitions.get(stateStack.peek())
                    .get(reduce.getGrammarRuleTyp());
                if (transition != null)
                    stateStack.push(transition);
                else
                    break;
            }
            else
                return 1 - index;
        }
        parseTreeArrayIn[0] = new ParseTree(outStack.pop());
        return 1;
    }

    private static class ParseState implements Comparable<ParseState> {
        static Map<ParseState, ParseState> cache = new HashMap<ParseState, ParseState>();
        final NotationRule rule;
        final int position;

        public static void clearCache() {
            cache.clear();
        }

        private ParseState(final NotationRule rule, final int pos) {
            this.rule = rule;
            position = pos;
        }

        public static ParseState get(final NotationRule rule, final int pos) {
            final ParseState state = new ParseState(rule, pos);
            final ParseState cached = cache.get(state);
            if (cached == null) {
                cache.put(state, state);
                return state;
            }
            return cached;
        }

        public static ParseState get(final NotationRule rule) {
            return get(rule, 0);
        }

        public ParseState advance() {
            if (position == rule.getRuleFormatExpr().length)
                return null;
            return new ParseState(rule, position + 1);
        }

        public Cnst head() {
            final Cnst[] expr = rule.getRuleFormatExpr();
            return position < expr.length ? expr[position] : null;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * rule.hashCode() + position;
        }

        @Override
        public boolean equals(final Object obj) {
            return this == obj || obj instanceof ParseState
                && position == ((ParseState)obj).position
                && rule.equals(((ParseState)obj).rule);
        }

        @Override
        public String toString() {
            String s = rule.getGrammarRuleTyp() + " ->";
            final Cnst[] expr = rule.getRuleFormatExpr();
            for (int i = 0; i < expr.length; i++) {
                if (i == position)
                    s += " *";
                s += " " + expr[i];
            }
            if (position == expr.length)
                s += " *";
            return s;
        }

        public int compareTo(final ParseState o) {
            final int c = rule.compareTo(o.rule);
            return c == 0 ? position - o.position : c;
        }
    }

    public class ParseSet implements Collection<ParseState> {
        Set<ParseState> extra = new TreeSet<ParseState>();
        Set<Cnst> initials = new HashSet<Cnst>();

        public ParseSet() {}
        public ParseSet(final ParseSet other) {
            extra = new TreeSet<ParseState>(other.extra);
            initials = new TreeSet<Cnst>(other.initials);
        }

        public int size() {
            extra.size();
            for (final Cnst c : initials)
                rulesByTyp.get(c).size();
            return 0;
        }
        public boolean isEmpty() {
            return extra.isEmpty() && initials.isEmpty();
        }
        public boolean contains(final Object o) {
            if (extra.contains(o))
                return true;
            if (((ParseState)o).position != 0)
                return false;
            for (final Cnst c : initials)
                if (rulesByTyp.get(c).contains(o))
                    return true;
            return false;
        }

        public Iterator<ParseState> iterator() {
            return new Iterator<ParseState>() {
                Iterator<Cnst> iInit = initials.iterator();
                Iterator<ParseState> iExt = extra.iterator();
                Iterator<NotationRule> iRule = null;

                ParseState next = advance();

                private ParseState advance() {
                    if (iExt.hasNext())
                        return iExt.next();
                    else if (iRule != null && iRule.hasNext())
                        return ParseState.get(iRule.next());
                    else {
                        while (iInit.hasNext()) {
                            final List<NotationRule> list = rulesByTyp
                                .get(iInit.next());
                            if (!list.isEmpty()) {
                                iRule = list.iterator();
                                return ParseState.get(iRule.next());
                            }
                        }
                        return null;
                    }
                }

                public ParseState next() {
                    final ParseState old = next;
                    next = advance();
                    return old;
                }

                public boolean hasNext() {
                    return next != null;
                }
            };
        }
        public Object[] toArray() {
            throw new UnsupportedOperationException();
        }
        public <T> T[] toArray(final T[] a) {
            throw new UnsupportedOperationException();
        }
        public boolean add(final ParseState e) {
            return extra.add(e);
        }
        public boolean addCnst(final Cnst c) {
            return initials.add(c);
        }
        public boolean remove(final Object o) {
            throw new UnsupportedOperationException();
        }
        public boolean containsAll(final Collection<?> c) {
            for (final Object o : c)
                if (!contains(o))
                    return false;
            return true;
        }
        public boolean addAll(final Collection<? extends ParseState> c) {
            return extra.addAll(c);
        }
        public boolean removeAll(final Collection<?> c) {
            throw new UnsupportedOperationException();
        }
        public boolean retainAll(final Collection<?> c) {
            throw new UnsupportedOperationException();
        }
        public void clear() {
            extra.clear();
            initials.clear();
        }
        @Override
        public String toString() {
            String delim = "{";
            String s = "";

            for (final ParseState state : extra) {
                s += delim + state;
                delim = ", ";
            }
            for (final Cnst c : initials) {
                s += delim + c + " -> *all";
                delim = ", ";
            }
            return s + "}";
        }
        @Override
        public int hashCode() {
            final int prime = 31;
            return prime * extra.hashCode() + initials.hashCode();
        }
        @Override
        public boolean equals(final Object obj) {
            return extra.equals(((ParseSet)obj).extra)
                && initials.equals(((ParseSet)obj).initials);
        }
    }
}
