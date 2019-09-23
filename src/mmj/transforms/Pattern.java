package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.pa.MMJException;
import mmj.pa.ProofAsst;
import mmj.verify.GrammarConstants;

public class Pattern {
    public final ParseNode root;
    private final VarHyp[] varHypArray;
    private final Deque<ParseNode> unifyNodeStack = new ArrayDeque<>();
    private final Deque<ParseNode> compareNodeStack = new ArrayDeque<>();

    /**
     * Parse a string like "wff x e. A" into its ParseNode representation, in
     * this case "[[vx]; cv, cA]; wcel".
     *
     * @param proofAsst The context
     * @param fmla The formula string
     * @throws MMJException if the formula string could not be parsed
     */
    public Pattern(final ProofAsst proofAsst, final String fmla)
        throws MMJException
    {
        final LogicalSystem logicalSystem = proofAsst.getLogicalSystem();
        final Map<String, Sym> symTbl = logicalSystem.getSymTbl();
        final String[] parse = fmla.split(" ");
        final ArrayList<Sym> symList = new ArrayList<>(parse.length + 1);
        for (final String s : parse) {
            final Sym sym = symTbl.get(s);
            if (sym == null)
                throw new MMJException(GrammarConstants.ERRMSG_PARSE_FAILED);
            symList.add(sym);
        }
        root = proofAsst.getGrammar()
            .parseFormula(proofAsst.getMessages(), symTbl,
                logicalSystem.getStmtTbl(), new Formula(symList), null,
                Integer.MAX_VALUE, null)
            .getRoot();
        final List<VarHyp> list = new ArrayList<>();
        root.accumVarHypUsedListBySeq(list);
        varHypArray = list.toArray(new VarHyp[list.size()]);
    }

    public Map<String, ParseNode> match(final ParseNode expr) {
        final ParseNode[] subst = root.unifyWithSubtree(expr, varHypArray,
            unifyNodeStack, compareNodeStack);
        if (subst == null)
            return null;
        final Map<String, ParseNode> result = new HashMap<>();
        for (int i = 0; i < subst.length; i++)
            result.put(varHypArray[i].getVar().getId(), subst[i]);
        return result;
    }

}
