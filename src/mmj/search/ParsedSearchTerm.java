// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ParsedSearchTerm.java

package mmj.search;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;

import mmj.lang.*;
import mmj.mmio.Tokenizer;

// Referenced classes of package mmj.search:
//            SearchMgr

public class ParsedSearchTerm {

    public static ParsedSearchTerm parseStmtUFOText(final String s,
        final int i, final SearchMgr searchMgr)
    {
        final ParsedSearchTerm parsedSearchTerm = new ParsedSearchTerm(s, i,
            searchMgr.getProvableLogicStmtTyp());
        parsedSearchTerm.parseStmt(searchMgr);
        return parsedSearchTerm;
    }

    public static ParsedSearchTerm parseExprUFOText(final String s,
        final int i, final SearchMgr searchMgr)
    {
        final ParsedSearchTerm parsedSearchTerm = new ParsedSearchTerm(s, i,
            null);
        parsedSearchTerm.parseExpr(searchMgr);
        return parsedSearchTerm;
    }

    private ParsedSearchTerm(final String s, final int i, final Cnst cnst) {
        errorMessage = null;
        isParseExpr = false;
        parseTree = null;
        formula = null;
        varHypArray = null;
        ufoText = s;
        maxSeq = i;
        typCd = cnst;
    }

    private void parseExpr(final SearchMgr searchMgr) {
        isParseExpr = true;
        loadSymbols(searchMgr);
        if (errorMessage == null) {
            loadParseTree(searchMgr);
            if (parseTree == null) {
                final Set<?> set = searchMgr.getGrammar()
                    .getSyntaxAxiomTypSet();
                final Iterator<?> iterator = set.iterator();
                boolean flag = false;
                do {
                    if (!iterator.hasNext())
                        break;
                    formula.setTyp((Cnst)iterator.next());
                    loadParseTree(searchMgr);
                    if (parseTree == null)
                        continue;
                    flag = true;
                    break;
                } while (true);
                if (!flag)
                    errorMessage = "Error parsing expression. Could not find matching syntax Type Code.";
            }
        }
    }

    private void parseStmt(final SearchMgr searchMgr) {
        loadSymbols(searchMgr);
        if (errorMessage == null) {
            loadParseTree(searchMgr);
            if (parseTree == null)
                errorMessage = 
                    "Error(?) in parse of search term expression.";
        }
    }

    private void loadParseTree(final SearchMgr searchMgr) {
        parseTree = searchMgr.getGrammar().parseFormulaWithoutSafetyNet(
            formula, varHypArray, maxSeq);
    }

    private void loadSymbols(final SearchMgr searchMgr) {
        final LogicalSystem logicalSystem = searchMgr.getLogicalSystem();
        searchMgr.getWorkVarManager();
        final MandFrame mandFrame = searchMgr.getComboFrame();
        final Map<String, Var> hashmap = mandFrame.getVarMap();
        Tokenizer tokenizer;
        try {
            tokenizer = new Tokenizer(new StringReader(ufoText), "");
        } catch (final IOException ioexception) {
            errorMessage = 
                "Unable to parse expression. Detailed error = "
                    + ioexception.getMessage();
            return;
        }
        final List<VarHyp> arraylist = new ArrayList<VarHyp>();
        final List<Sym> arraylist1 = new ArrayList<Sym>();
        arraylist1.add(searchMgr.getProvableLogicStmtTyp());
        do {
            final StringBuilder sb = new StringBuilder();
            int i;
            try {
                i = tokenizer.getToken(sb, 0);
            } catch (final IOException e) {
                errorMessage = 
                    "Unable to parse expression. Detailed error = "
                        + e.getMessage();
                return;
            }
            if (i <= 0)
                break;
            final String s = sb.toString();
            Sym sym = hashmap.get(s);
            if (sym == null) {
                sym = logicalSystem.getSymTbl().get(s);
                if (sym == null) {
                    errorMessage = 
                        "Invalid symbol in expression. Input token = " + s
                            + " not found in Logical System Symbol Table."
                            + " Note: Work variables not allowed in "
                            + " search terms.";
                    return;
                }
                if (sym.getSeq() >= maxSeq) {
                    errorMessage = 
                        "Invalid symbol in expression. Input token = " + s
                            + " has sequence number >= Search maxSeq";
                    return;
                }
            }
            if (sym.isVar()) {
                final Var var = (Var)sym;
                if (var.getIsWorkVar()) {
                    errorMessage = 
                        "Work variables not allowed in search terms.";
                    return;
                }
                if (var.isActive() && var.getActiveVarHyp() != null)
                    Assrt.accumHypInList(arraylist, var.getActiveVarHyp());
                else {
                    errorMessage = 
                        "Invalid symbol in expression. Input token = " + s
                            + " has does not have an active Var and VarHyp"
                            + " at the search's scope level.";
                    return;
                }
            }
            arraylist1.add(sym);
        } while (true);
        formula = new Formula(arraylist1);
        varHypArray = new VarHyp[arraylist.size()];
        final Iterator<VarHyp> iterator = arraylist.iterator();
        int j = 0;
        while (iterator.hasNext())
            varHypArray[j++] = iterator.next();
    }

    public String ufoText;
    public int maxSeq;
    public Cnst typCd;
    public String errorMessage;
    public boolean isParseExpr;
    public ParseTree parseTree;
    public Formula formula;
    public VarHyp[] varHypArray;
}