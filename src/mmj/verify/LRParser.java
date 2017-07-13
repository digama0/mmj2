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

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import org.json.JSONArray;
import org.json.JSONObject;

import mmj.lang.*;
import mmj.pa.*;

/**
 * LR Parser
 */
public class LRParser implements GrammaticalParser {
    private static final String PFX = "~LRParser.";

    private SessionStore store;
    private Setting<Integer> grammarHash;

    private final Grammar grammar;

    private Setting<Map<String, Integer>> startStatesSetting;
    private Setting<List<ParseTableRow>> rowsSetting;

    private Map<String, Integer> startStates;
    private List<ParseTableRow> rows;

    private final List<ParseSet> sets = new ArrayList<>();
    private final Map<ParseSet, Integer> setLookup = new HashMap<>();
    private final Map<Cnst, List<NotationRule>> rulesByTyp = new HashMap<>();
    private final Map<Cnst, Set<Cnst>> nontermClosure = new HashMap<>();
    private final Map<Cnst, Set<Cnst>> initialByTyp = new HashMap<>();
    private final Map<Cnst, Set<Cnst>> followByTyp = new HashMap<>();
    private final Deque<Integer> stateQueue = new ArrayDeque<>();
    private final Deque<Integer> conflicts = new ArrayDeque<>();
    private final List<Set<Integer>> backtrack = new ArrayList<>();
    private final Map<ParseTree, NotationRule> newRules = new HashMap<>();

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

    @SuppressWarnings("unchecked")
    @Override
    public void addSettings(final SessionStore store) {
        this.store = store;
        // As a crude lock on the grammar hash, only allow setting to and from 0
        grammarHash = store.addSetting(PFX + "grammarHash", 0)
            .addListener((o, value) -> o == 0 || value == 0);

        // Doing some type abuse here: since a JSONObject extends
        // Map<String, Object> we can treat it like a Map<String, Integer> as
        // long as nothing sneaks in
        final Map<String, Integer> startDef = (Map<String, Integer>)(Object)new JSONObject();
        startStatesSetting = store
            .addSetting(PFX + "startStates", startDef, Serializer.identity())
            .addListener((o, value) -> o == startDef || value == startDef);

        final List<ParseTableRow> rowDef = new ArrayList<>();
        rowsSetting = store.new ListSetting<>(PFX + "rows", rowDef,
            ParseTableRow.serializer(grammar))
                .addListener((o, value) -> o == rowDef || value == rowDef);
    }

    private void initialize() {
        startStates = new HashMap<>();
        rows = new ArrayList<>();

        for (final Cnst typ : grammar.getVarHypTypSet()) {
            rulesByTyp.put(typ, new ArrayList<NotationRule>());
            nontermClosure.put(typ, new HashSet<Cnst>());
            initialByTyp.put(typ, new HashSet<Cnst>());
            followByTyp.put(typ, new HashSet<Cnst>());
        }
        for (final NotationRule notationRule : grammar.getNotationGRSet()) {
            final Cnst typ = notationRule.getGrammarRuleTyp();
            if (notationRule.getIsGimmeMatchNbr() != 1) {
                rulesByTyp.get(typ).add(notationRule);
                final Cnst head = notationRule.getRuleFormatExprFirst();
                if (head.isVarTyp())
                    nontermClosure.get(typ).add(head);
                else
                    initialByTyp.get(typ).add(head);
            }
            else
                getClass();
        }
        boolean changed;
        do {
            changed = false;
            for (final Entry<Cnst, Set<Cnst>> e : nontermClosure.entrySet())
                for (final Cnst c : new ArrayList<>(e.getValue()))
                    changed |= e.getValue().addAll(nontermClosure.get(c));
        } while (changed);
        for (final Cnst typ : grammar.getVarHypTypSet())
            for (final Cnst c : nontermClosure.get(typ))
                initialByTyp.get(typ).addAll(initialByTyp.get(c));
        do {
            changed = false;
            for (final NotationRule notationRule : grammar.getNotationGRSet()) {
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
            startStates.put(typ.getId(), getState(set));
        }

        while (!stateQueue.isEmpty())
            developState(stateQueue.pop());

        while (!conflicts.isEmpty()) {
            final int index = conflicts.pop();
            Set<Integer> backStates = new TreeSet<>();
            backStates.add(index);
            final ParseTableRow row = rows.get(index);
            for (int i = row.args; i > 0; i--) {
                final Set<Integer> newStates = new TreeSet<>();
                for (final int j : backStates)
                    newStates.addAll(backtrack.get(j));
                backStates = newStates;
            }
            final Set<String> badTokens = row.transitions.keySet();
            for (final int back : backStates) {
                final ParseTableRow backRow = rows.get(back);
                final int fwd = backRow.getTransition(row.typeCode);
                boolean bad = false;
                for (final String head : badTokens)
                    bad |= rows.get(fwd).transitions.containsKey(head);
                if (bad) {
                    final ParseSet goal = new ParseSet();
                    final Cnst typ = row.reduce.getGrammarRuleTyp();
                    final Cnst dir = row.reduce.getRuleFormatExprFirst();
                    for (final ParseState state : sets.get(back)) {
                        final Cnst head = state.head();
                        if (head.equals(dir) && state.rule != row.reduce)
                            goal.add(state.advance());
                        else if (head.equals(typ)) {
                            final Cnst[] expr = state.rule.getRuleFormatExpr();
                            int matchIndex = 0;
                            for (int i = 0; i < state.position; i++)
                                if (expr[i].isVarTyp())
                                    matchIndex++;
                            final NotationRule construct = new NotationRule(
                                grammar, state.rule, matchIndex, row.reduce);
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
                    backRow.transitions.put(dir.getId(), goalIndex);
                    backtrack.get(goalIndex).add(back);

                    while (!stateQueue.isEmpty())
                        developState(stateQueue.pop());
                }
            }
        }

        // Release resources after generation
        sets.clear();
        setLookup.clear();
        rulesByTyp.clear();
        nontermClosure.clear();
        initialByTyp.clear();
        followByTyp.clear();
        backtrack.clear();
        newRules.clear();
        ParseState.clearCache();

        grammarHash.reset();
        grammarHash.set(grammar.getNotationGRSet().size());
        startStatesSetting.reset();
        startStatesSetting.set(startStates);
        rowsSetting.reset();
        rowsSetting.set(rows);
    }

    private Integer getState(final ParseSet set) {
        Integer index = setLookup.get(set);
        if (index == null) {
            index = sets.size();
            sets.add(set);
            setLookup.put(set, index);
            final ParseTableRow row = new ParseTableRow();
            rows.add(row);
            backtrack.add(new TreeSet<Integer>());
            stateQueue.add(index);
        }
        return index;
    }

    private void developState(final Integer index) {
        System.out.print("*");
        final ParseSet set = sets.get(index);
        final ParseTableRow row = rows.get(index);
        row.setReduction(
            set.reduce(null, (NotationRule reduce, final ParseState e) -> {
                final Cnst head = e.head();
                if (head == null) {
                    if (reduce != null)
                        grammar.getMessages().accumMessage(
                            GrammarConstants.ERRMSG_REDUCE_REDUCE, set, reduce,
                            e.rule);
                    else
                        reduce = e.rule;
                }
                else if (!row.transitions.containsKey(head.getId())) {
                    final ParseSet goal = new ParseSet();
                    set.forEach(state -> {
                        if (head.equals(state.head()))
                            goal.add(state.advance());
                    });
                    makeClosure(goal);
                    final int goalIndex = getState(goal);
                    row.transitions.put(head.getId(), goalIndex);
                    backtrack.get(goalIndex).add(index);
                    if (reduce != null && followByTyp
                        .get(reduce.getGrammarRuleTyp()).contains(head))
                        conflicts.add(index);
                }
                return reduce;
            }));
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

    public void load(final boolean fromFile) {
        if (grammarHash.get().equals(grammar.getNotationGRSet().size())) {
            startStates = startStatesSetting.get();
            rows = rowsSetting.get();
        }
        if (startStates == null || startStates.isEmpty())
            if (fromFile) {
                try {
                    final List<ProofAsstException> errors = new ArrayList<>();
                    store.load(true, errors, grammarHash.key(),
                        startStatesSetting.key(), rowsSetting.key());
                    for (final ProofAsstException e : errors)
                        throw e;
                } catch (final IOException | ProofAsstException e) {
                    grammarHash.reset();
                    startStatesSetting.reset();
                    rowsSetting.reset();
                }
                load(false);
            }
            else
                initialize();
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
        // Special case: single token
        if (parseNodeHolderExprIn.length == 1) {
            final MObj mObj = parseNodeHolderExprIn[0].mObj;
            if (mObj instanceof VarHyp) {
                parseTreeArrayIn[0] = new ParseTree(
                    new ParseNode((VarHyp)mObj));
                return 1;
            }
            NotationRule rule;
            if (mObj instanceof Cnst
                && (rule = ((Cnst)mObj).getLen1CnstNotationRule()) != null)
            {
                parseTreeArrayIn[0] = new ParseTree(
                    new ParseNode(rule.getBaseSyntaxAxiom()));
                return 1;
            }
            return -1;
        }

        // Check notation rules to make sure the grammar has not changed since
        // last initialization
        load(true);
        int index = 0;
        final Deque<Integer> stateStack = new ArrayDeque<>();
        final ArrayDeque<ParseNode> outStack = new ArrayDeque<>();
        Cnst startRuleTyp = formulaTypIn;
        if (formulaTypIn.isProvableLogicStmtTyp())
            if (grammar.getLogicStmtTypArray().length > 0)
                startRuleTyp = grammar.getLogicStmtTypArray()[0];
            else
                throw new IllegalStateException(new VerifyException(
                    GrammarConstants.ERRMSG_START_RULE_TYPE_UNDEF,
                    formulaTypIn));
        stateStack.push(startStates.get(startRuleTyp.getId()));
        while (true) {
            final ParseNodeHolder lookahead = index < parseNodeHolderExprIn.length
                ? parseNodeHolderExprIn[index] : null;
            final int state = stateStack.peek().intValue();
            Integer transition = null;
            final ParseTableRow row = rows.get(state);
            NotationRule gimmeMatch = null;
            if (lookahead != null) {
                Cnst c = lookahead.getCnstOrTyp();
                gimmeMatch = c.getLen1CnstNotationRule();
                if (gimmeMatch != null && gimmeMatch.getIsGimmeMatchNbr() == 1)
                    c = gimmeMatch.getGrammarRuleTyp();
                else
                    gimmeMatch = null;
                transition = row.getTransition(c);
            }
            if (transition != null) {
                if (gimmeMatch != null)
                    outStack
                        .push(new ParseNode(gimmeMatch.getBaseSyntaxAxiom()));
                else if (!(lookahead.mObj instanceof Cnst)) {
                    final VarHyp hyp = lookahead.mObj instanceof Var
                        ? ((Var)lookahead.mObj).getActiveVarHyp()
                        : (VarHyp)lookahead.mObj;
                    outStack.push(new ParseNode(hyp));
                }
                stateStack.push(transition);
                index++;
            }
            else if (row.typeCode != null) {
                outStack.push(row.paramTransformationTree.getRoot()
                    .deepCloneApplyingStackSubst(outStack));
                for (int i = row.args; i > 0; i--)
                    stateStack.pop();
                transition = rows.get(stateStack.peek())
                    .getTransition(row.typeCode);
                if (transition != null)
                    stateStack.push(transition);
                else
                    break;
            }
            else
                return -1 - index;
        }
        if (index < parseNodeHolderExprIn.length)
            return -1 - index;
        parseTreeArrayIn[0] = new ParseTree(outStack.pop());
        return 1;
    }

    private static class ParseState implements Comparable<ParseState> {
        static Map<ParseState, ParseState> cache = new HashMap<>();
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
        Set<ParseState> extra = new TreeSet<>();
        Set<Cnst> initials = new HashSet<>();

        public ParseSet() {}
        public ParseSet(final ParseSet other) {
            extra = new TreeSet<>(other.extra);
            initials = new TreeSet<>(other.initials);
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

        // Don't use, too slow - use forEach() instead
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
        @Override
        public void forEach(final Consumer<? super ParseState> f) {
            for (final ParseState state : extra)
                f.accept(state);
            for (final Cnst c : initials)
                for (final NotationRule rule : rulesByTyp.get(c))
                    f.accept(ParseState.get(rule));
        }
        public <T> T reduce(T accum,
            final BiFunction<T, ? super ParseState, T> f)
        {
            for (final ParseState state : extra)
                accum = f.apply(accum, state);
            for (final Cnst c : initials)
                for (final NotationRule rule : rulesByTyp.get(c))
                    accum = f.apply(accum, ParseState.get(rule));
            return accum;
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
            return (obj instanceof ParseSet)
                    && extra.equals(((ParseSet)obj).extra)
                    && initials.equals(((ParseSet)obj).initials);
        }
    }

    private static class ParseTableRow {
        public JSONObject transitions;
        public String typeCode;
        public ParseTree paramTransformationTree;
        public int args;
        public NotationRule reduce;

        public ParseTableRow() {
            transitions = new JSONObject();
        }

        public Integer getTransition(final Cnst c) {
            return getTransition(c.getId());
        }

        public Integer getTransition(final String s) {
            return (Integer)transitions.opt(s);
        }

        public void setReduction(final NotationRule reduce) {
            if ((this.reduce = reduce) != null) {
                typeCode = reduce.getGrammarRuleTyp().getId();
                paramTransformationTree = reduce.getParamTransformationTree();
                args = reduce.getRuleFormatExpr().length;
            }
        }

        public static Serializer<ParseTableRow> serializer(
            final Grammar grammar)
        {
            final Serializer<ParseTree> ser = ParseTree
                .serializer(grammar.stmtTbl);
            return Serializer.of(o -> {
                final JSONArray map = (JSONArray)o;
                final ParseTableRow row = new ParseTableRow();
                row.transitions = map.optJSONObject(0);
                if (map.length() > 1) {
                    row.typeCode = map.getString(1);
                    row.paramTransformationTree = ser.deserialize(map.get(2));
                    row.args = map.getInt(3);
                }
                return row;
            }, row -> {
                final JSONArray a = new JSONArray().put(row.transitions == null
                    ? JSONObject.NULL : row.transitions);
                return row.typeCode == null ? a
                    : a.put(row.typeCode)
                        .put(ser.serialize(row.paramTransformationTree))
                        .put(row.args);
            });
        }
    }
}
