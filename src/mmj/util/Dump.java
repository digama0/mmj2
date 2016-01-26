//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * Dump.java  0.07 11/01/2011
 *
 * Sep-25-2005
 *     -> check grammar.getGrammarInitialized()
 *        in addition to "!= null"
 *
 * Sep-06-2006:
 *     -->Add TMFF stuff.
 *     -->When printing formula, if !isTMFFEnabled()
 *        don't use TMFF --> do it the old toString() way...
 *
 * Nov-01-2007 Version 0.05
 *     - Add proofLevel 0 parm to tmffPreferences.renderFormula()
 *       call. No indentation of formulas used in Dump.java.
 *
 * Aug-01-2008 Version 0.06
 *     - Add PrintBookManagerChapters(),
 *           PrintBookManagerSections(), and
 *           PrintBookManagerSectionDetails().
 *
 * Version 0.07 - Nov-01-2011:  comment update.
 *     --> Add check for valid parse tree
 *         before calling TMFFPreferences.renderFormula()
 *         in dumpStmtProperties() because TMFF requires
 *         real parse trees except in format number 0.
 */

package mmj.util;

import java.io.PrintWriter;
import java.util.*;

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.pa.SessionStore;
import mmj.tmff.TMFFPreferences;
import mmj.tmff.TMFFStateParams;
import mmj.verify.*;

/**
 * Dump started out as just testing code that could be "throwaway". Now a bit of
 * it is used in mmj.Util.BatchMMJ2 to dump/print info from LogicalSystem and
 * Grammar. The report is ugly but better than nothing. And it is key for doing
 * parallel testing because almost every key data element is output to the
 * printer.
 * <p>
 * Dump can employ a PrintWriter or send its stuff to System.out. The
 * PrintWriter can be set at construction time or later via a "set" function.
 * <p>
 * Most of the Dump print routines are invoked by BatchMMJ2 but some are
 * leftovers from testing. Dump is ugly but it works.
 */
public class Dump {

    public PrintWriter sysOut = null;
    public TMFFPreferences tmffPreferences;

    /**
     * Default constructor which will print to System.out.
     */
    public Dump() {
        this(null);
    }

    /**
     * Construct Dump using a PrintWriter for output.
     *
     * @param sysOut the PrintWriter
     */
    public Dump(final PrintWriter sysOut) {
        setSysOut(sysOut);
    }

    /**
     * Sets Dump's SysOut to a new PrintWriter, or null to revert to writing to
     * System.out.
     *
     * @param sysOut a PrintWriter, or null for System.out output.
     */
    public void setSysOut(final PrintWriter sysOut) {
        this.sysOut = sysOut;
        setTMFFPreferences(new TMFFPreferences(new SessionStore()));
    }

    /**
     * Sets Dump's Text Mode Formula Formatting preference parameters to a new
     * set of values.
     * <p>
     * Note: mmj.util.OutputBoss uses this.
     *
     * @param tmffPreferences TMFF Preference parameters.
     */
    public void setTMFFPreferences(final TMFFPreferences tmffPreferences) {
        this.tmffPreferences = tmffPreferences;
    }

    /**
     * Print a line from a StringBuilder.
     *
     * @param sb StringBuilder line to be printed.
     */
    public void sysOutDumpAPrintLn(final StringBuilder sb) {
        sysOutDumpAPrintLn(sb.toString());
    }

    /**
     * Print a line from a String.
     *
     * @param s String line to be printed.
     */
    public void sysOutDumpAPrintLn(final String s) {
        if (sysOut == null)
            System.out.println(s);
        else
            sysOut.println(s);
    }

    /**
     * "printSyntaxDetails" is used by BatchMMJ2 to print all syntax-related
     * information about a LogicalSystem and its Grammar.
     *
     * @param caption identifying caption for the report.
     * @param logicalSystem a LogicalSystem object.
     * @param grammar a Grammar object derived from the Logical System.
     */
    public void printSyntaxDetails(final String caption,
        final LogicalSystem logicalSystem, final Grammar grammar)
    {

        dumpLogSysCounts(1, UtilConstants.DUMP_LOGSYS_COUNTS + caption,
            logicalSystem.getSymTbl(), logicalSystem.getStmtTbl());

        final Set<Sym> provableLogicStmtTypSet = new TreeSet<>(MObj.SEQ);
        if (grammar != null && grammar.getGrammarInitialized()) {
            final Cnst[] provableLogicStmtTypArray = grammar
                .getProvableLogicStmtTypArray();
            for (final Cnst element : provableLogicStmtTypArray)
                provableLogicStmtTypSet.add(element);
            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1, UtilConstants.DUMP_PROVABLE_TYP_SET + caption,
                provableLogicStmtTypSet);

            final Set<Cnst> logicStmtTypSet = new TreeSet<>(MObj.SEQ);
            final Cnst[] logicStmtTypArray = grammar.getLogicStmtTypArray();
            for (final Cnst element : logicStmtTypArray)
                logicStmtTypSet.add(element);
            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1, UtilConstants.DUMP_PROVABLE_TYP_SET + caption,
                logicStmtTypSet);

            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1, UtilConstants.DUMP_VARHYP_TYP_SET + caption,
                grammar.getVarHypTypSet());

            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1, UtilConstants.DUMP_SYNTAX_AXIOM_TYP_SET + caption,
                grammar.getSyntaxAxiomTypSet());

            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1, UtilConstants.DUMP_NULLS_PERMITTED_TYP_SET + caption,
                grammar.getNullsPermittedTypSet());

        }
        sysOutDumpAPrintLn(" ");
        final Collection<Sym> symTblValues = logicalSystem.getSymTbl().values();
        dumpSymTbl(1, UtilConstants.DUMP_LOGSYS_SYM_TBL + caption,
            symTblValues);

        if (grammar != null && grammar.getGrammarInitialized()) {

            sysOutDumpAPrintLn(" ");
            dumpGrammarRuleCollection(UtilConstants.DUMP_GRAMMAR_RULE_MAX_PRINT,
                1, UtilConstants.DUMP_NULLS_PERMITTED_LIST,
                grammar.getNullsPermittedGRList());

            sysOutDumpAPrintLn(" ");
            dumpGrammarRuleCollection(UtilConstants.DUMP_GRAMMAR_RULE_MAX_PRINT,
                1, UtilConstants.DUMP_TYPE_CONVERSION_LIST,
                grammar.getTypeConversionGRList());

            sysOutDumpAPrintLn(" ");
            dumpGrammarRuleCollection(UtilConstants.DUMP_GRAMMAR_RULE_MAX_PRINT,
                1, UtilConstants.DUMP_NOTATION_LIST,
                grammar.getNotationGRSet());

            sysOutDumpAPrintLn(" ");
            dumpTheGrammar(1, grammar.getVarHypTypSet(), symTblValues);
        }

    }

    /**
     * "printOneStatementDetails" is used by BatchMMJ2 to print information
     * about a single Stmt.
     *
     * @param stmt a Stmt object.
     */
    public void printOneStatementDetails(final Stmt stmt) {
        dumpOneStmt(1, // indentNbr
            stmt);
    }

    /**
     * "printStatementDetails" is used by BatchMMJ2 to print Metamath Statement
     * details, up to the limit imposed by maxStatementPrintCountParm.
     *
     * @param caption identifying caption for the report.
     * @param stmtTbl Statement Table (Map)
     * @param maxStatementPrintCountParm max number of Stmt's to print.
     */
    public void printStatementDetails(final String caption,
        final Map<String, Stmt> stmtTbl, final int maxStatementPrintCountParm)
    {

        sysOutDumpAPrintLn(" ");
        dumpStmtTbl(maxStatementPrintCountParm, // maxDumpCnt
            1, // indentNbr
            UtilConstants.DUMP_LOGSYS_STMT_TBL + caption, stmtTbl.values());

    }

    /**
     * "printBookManagerChapters" is used by BatchMMJ2 to print BookManager
     * Chapter information.
     * <p>
     * This function is used primarily as a way to generate test output but the
     * RunParm "PrintBookManagerChapters" may be useful for mmj2 users.
     *
     * @param caption identifying caption for the report.
     * @param bookManager the BookManager in use.
     */
    public void printBookManagerChapters(final String caption,
        final BookManager bookManager)
    {

        sysOutDumpAPrintLn(" ");
        dumpBookManagerChapters(1, // indentNbr
            caption, bookManager.getChapterList());
    }

    /**
     * "printBookManagerSections" is used by BatchMMJ2 to print BookManager
     * Section information.
     * <p>
     * This function is used primarily as a way to generate test output but the
     * RunParm "PrintBookManagerSections" may be useful for mmj2 users.
     *
     * @param caption identifying caption for the report.
     * @param bookManager the BookManager in use.
     */
    public void printBookManagerSections(final String caption,
        final BookManager bookManager)
    {

        sysOutDumpAPrintLn(" ");
        dumpBookManagerSections(1, // indentNbr
            caption, bookManager.getSectionList());
    }

    /**
     * "printBookManagerSectionDetails" is used by BatchMMJ2 to print
     * BookManager Section, MObj and Chapter information.
     * <p>
     * This function is used primarily as a way to generate test output but the
     * RunParm "PrintBookManagerSectionDetails" may be useful for mmj2 users.
     *
     * @param runParm contains RunParm name and values.
     * @param logicalSystem the LogicalSystem in use.
     * @param bookManager the BookManager in use.
     * @param section the Section to be printed or null if all Sections are to
     *            be printed.
     */
    public void printBookManagerSectionDetails(final RunParmArrayEntry runParm,
        final LogicalSystem logicalSystem, final BookManager bookManager,
        final Section section)
    {

        sysOutDumpAPrintLn(" ");

        dumpBookManagerSectionDetails(1, // indentNbr
            runParm, bookManager,
            bookManager.getSectionMObjIterable(logicalSystem), section);
    }

    public String[] keyArray = new String[100];
    public int keyArrayCount = 0;

    public static final String[] indentTbl = new String[31];

    static {
        final StringBuilder s = new StringBuilder();
        for (int i = 0; i < 31; i++) {
            s.append("  ");
            indentTbl[i] = s.toString();
        }
    }

    public void dumpLogSys(final int indentNbr, final String caption,
        final LogicalSystem logSys)
    {
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_LOGICAL_SYSTEM + caption
                + UtilConstants.DUMP_START);

        final Collection<Sym> symTbl = logSys.getSymTbl().values();
        final Collection<Stmt> stmtTbl = logSys.getStmtTbl().values();

        dumpLogSysCounts(indentNbr + 1, caption, symTbl, stmtTbl);
        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_LOGICAL_SYSTEM + caption
                + UtilConstants.DUMP_END);
    }

    public void dumpLogSysCounts(final int indentNbr, final String caption,
        final Map<String, Sym> symTbl, final Map<String, Stmt> stmtTbl)
    {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + " " + caption
            + UtilConstants.DUMP_SYM_TBL_SIZE + symTbl.size());

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + " " + caption
            + UtilConstants.DUMP_STMT_TBL_SIZE + stmtTbl.size());

    }

    public void dumpLogSysCounts(final int indentNbr, final String caption,
        final Collection<Sym> symTbl, final Collection<Stmt> stmtTbl)
    {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + " " + caption
            + UtilConstants.DUMP_SYM_TBL_SIZE + symTbl.size());

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + " " + caption
            + UtilConstants.DUMP_STMT_TBL_SIZE + stmtTbl.size());

    }

    public void dumpSymTbl(final int indentNbr, final String caption,
        final Collection<? extends Sym> symTbl)
    {
        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_SYM_TBL + caption);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_SYM_TBL_UNDERSCORE);

        if (symTbl == null) {
            sysOutDumpAPrintLn(
                indentTbl[indentNbr] + UtilConstants.DUMP_SYM_TBL_IS_EMPTY);
            return;
        }

        final Set<Sym> symSet = new TreeSet<>(MObj.SEQ);
        symSet.addAll(symTbl);

        for (final Sym sym : symSet)
            if (sym instanceof Var)
                dumpSymVarFull(indentNbr, (Var)sym);
            else
                dumpSymCnstFull(indentNbr, (Cnst)sym);
    }

    public void dumpStmtTbl(final int indentNbr, final String caption,
        final Collection<? extends Stmt> stmtTbl)
    {
        dumpStmtTbl(Integer.MAX_VALUE, indentNbr, caption, stmtTbl);
    }

    public void dumpStmtTbl(final int maxDumpCnt, final int indentNbr,
        final String caption, final Collection<? extends Stmt> stmtTbl)
    {
        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + UtilConstants.DUMP_STMT_TBL
            + caption + UtilConstants.DUMP_OF_FIRST + maxDumpCnt);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        if (stmtTbl == null) {
            sysOutDumpAPrintLn(
                indentTbl[indentNbr] + UtilConstants.DUMP_STMT_TBL_IS_EMPTY);
            return;
        }

        final Set<Stmt> stmtSet = new TreeSet<>(MObj.SEQ);
        stmtSet.addAll(stmtTbl);

        int dumpCnt = 0;
        for (final Stmt stmt : stmtSet) {
            if (dumpCnt++ >= maxDumpCnt)
                break;
            dumpOneStmt(indentNbr, stmt);
        }
    }

    public void dumpOneStmt(final int indentNbr, final Stmt stmt) {
        sysOutDumpAPrintLn(indentTbl[indentNbr]);

        if (stmt instanceof Theorem) {
            dumpStmtTheoremFull(indentNbr, (Theorem)stmt);
            return;
        }
        if (stmt instanceof Axiom) {
            dumpStmtAxiomFull(indentNbr, (Axiom)stmt);
            return;
        }
        if (stmt instanceof LogHyp) {
            dumpStmtLogHypFull(indentNbr, (LogHyp)stmt);
            return;
        }
        if (stmt instanceof VarHyp) {
            dumpStmtVarHypFull(indentNbr, (VarHyp)stmt);
            return;
        }
        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_DUMP_STMT_UNRECOG_1 + stmt.getLabel());
    }

    public void dumpStmtTheoremFull(int indentNbr, final Theorem theorem) {

        StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_THEOREM);

        indentNbr++;

        dumpStmtProperties(indentNbr, sb, theorem);
        dumpAssrtProperties(indentNbr, theorem);

        sb = new StringBuilder();
        final ScopeFrame optFrame = theorem.getOptFrame();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_OPT_FRAME_HYP_ARRAY);
        char comma = ' ';
        for (final Hyp element : optFrame.hypArray) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(element.getLabel());
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_OPT_FRAME_DJ_VARS);
        comma = ' ';
        for (final DjVars element : optFrame.djVarsArray) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(element.getVarLo());
            sb.append(" ");
            sb.append(element.getVarHi());
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_PROOF);
        final RPNStep[] proof = theorem.getProof();
        for (final RPNStep element : proof)
            if (element == null)
                sb.append(UtilConstants.DUMP_PROOF_MISSING_STEP);
            else {
                sb.append(element);
                sb.append(" ");
            }
        sysOutDumpAPrintLn(sb);

        sysOutDumpAPrintLn(" ");

    }

    public void dumpStmtAxiomFull(int indentNbr, final Axiom axiom) {

        StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_AXIOM);

        indentNbr++;

        dumpStmtProperties(indentNbr, sb, axiom);
        dumpAssrtProperties(indentNbr, axiom);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);

        final int[] syntaxAxiomVarHypReseq = axiom.getSyntaxAxiomVarHypReseq();
        if (syntaxAxiomVarHypReseq != null) {
            sb.append(UtilConstants.DUMP_VARHYP_RESEQ);
            char comma = ' ';
            for (final int element : syntaxAxiomVarHypReseq) {
                sb.append(comma);
                comma = UtilConstants.DUMP_COMMA;
                sb.append(element);
            }
            sb.append(UtilConstants.DUMP_END_BRACKET);
        }

        if (axiom.getSyntaxAxiomHasUniqueCnst())
            sb.append(UtilConstants.DUMP_AXIOM_UNIQUE_CNST);

        sysOutDumpAPrintLn(sb);

        sysOutDumpAPrintLn(" ");

    }

    public void dumpStmtLogHypFull(int indentNbr, final LogHyp logHyp) {

        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_LOGHYP);

        indentNbr++;

        dumpStmtProperties(indentNbr, sb, logHyp);
        sysOutDumpAPrintLn(" ");

    }

    public void dumpStmtVarHypFull(int indentNbr, final VarHyp varHyp) {

        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_VARHYP);

        indentNbr++;

        dumpStmtProperties(indentNbr, sb, varHyp);
        sysOutDumpAPrintLn(" ");

    }

    public void dumpAssrtProperties(final int indentNbr, final Assrt assrt) {
        StringBuilder sb = new StringBuilder();
        final ScopeFrame mandFrame = assrt.getMandFrame();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_MAND_FRAME_HYP_ARRAY);
        char comma = ' ';
        for (final Hyp element : mandFrame.hypArray) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(element.getLabel());
            sb.append(" ");
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_MAND_FRAME_DJ_VARS);
        comma = ' ';
        for (final DjVars element : mandFrame.djVarsArray) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(element.getVarLo());
            sb.append(" ");
            sb.append(element.getVarHi());
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

    }

    public void dumpStmtProperties(final int indentNbr, StringBuilder sb,
        final Stmt stmt)
    {

        sb.append(stmt.getLabel());

        sb.append(UtilConstants.DUMP_TYP);
        sb.append(stmt.getTyp());

        if (stmt.isActive())
            sb.append(UtilConstants.DUMP_IS_ACTIVE);

        if (stmt instanceof Assrt)
            sb.append(UtilConstants.DUMP_IS_ASSRT);

        if (stmt instanceof Hyp)
            sb.append(UtilConstants.DUMP_IS_HYP);

        sb.append(UtilConstants.DUMP_MAND_VARHYP_ARRAY);
        final VarHyp[] varHypArray = stmt.getMandVarHypArray();
        char comma = ' ';
        for (final VarHyp element : varHypArray) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(element.getLabel());
            sb.append(" ");
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_FORMULA);

        if (tmffPreferences.isTMFFEnabled() && stmt.getExprParseTree() != null
            && stmt.getExprParseTree().getRoot().stmt != stmt)
        {
            // ok!
            final TMFFStateParams tmffSP = new TMFFStateParams(sb, sb.length(),
                tmffPreferences);
            tmffSP.leftmostColNbr = sb.length() + 2; // local override!
            tmffPreferences.renderFormula(tmffSP, stmt.getExprParseTree(),
                stmt.getFormula());
        }
        else {
            // do it the old way, for conformity with the old version
            // so we can perform adequate parallel testing :)
            // it actually looks better w/format nbr 0, but...
            sb.append(' ');
            sb.append(stmt.getFormula().toString());
        }

        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_EXPR_RPN);
        final RPNStep[] exprRPN = stmt.getExprRPN();
        if (exprRPN != null)
            for (final RPNStep element : exprRPN) {
                sb.append(element);
                sb.append(" ");
            }
        sysOutDumpAPrintLn(sb);
    }

    public void dumpSymVarFull(final int indentNbr, final Var var) {
        VarHyp varHyp;
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_VAR);
        sb.append(var.getId());

        if (var.isActive())
            sb.append(UtilConstants.DUMP_IS_ACTIVE);

        varHyp = var.getActiveVarHyp();
        if (varHyp != null) {
            sb.append(UtilConstants.DUMP_ACTIVE_VARHYP);
            sb.append(varHyp.getLabel());
        }

        sysOutDumpAPrintLn(sb);
    }

    public void dumpSymCnstFull(final int indentNbr, final Cnst cnst) {
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_CNST);

        sb.append(cnst.getId());

        if (cnst.isVarTyp())
            sb.append(UtilConstants.DUMP_IS_VAR_TYP);

        if (cnst.isGrammaticalTyp())
            sb.append(UtilConstants.DUMP_IS_GRAMMATICAL_TYP);
        if (cnst.isProvableLogicStmtTyp())
            sb.append(UtilConstants.DUMP_IS_PROVABLE_TYP);
        if (cnst.isLogicStmtTyp())
            sb.append(UtilConstants.DUMP_IS_LOGIC_TYP);
        if (cnst.isSyntaxAxiomTyp())
            sb.append(UtilConstants.DUMP_IS_SYNTAX_AXIOM_TYP);

        if (cnst.getLen1CnstNotationRule() != null)
            sb.append(UtilConstants.DUMP_LEN1_CNST_RULE_NBR
                + cnst.getLen1CnstNotationRule().getRuleNbr()
                + UtilConstants.DUMP_LEN1_CNST_AXIOM + cnst
                    .getLen1CnstNotationRule().getBaseSyntaxAxiom().getLabel());

        final Collection<Cnst> coll = cnst.getEarleyFIRST();
        if (coll != null) {
            sb.append(UtilConstants.DUMP_EARLEY_FIRST);
            for (final Cnst c : coll)
                sb.append(c).append(" ");
            sb.append(UtilConstants.DUMP_END_BRACKET);
        }

        sysOutDumpAPrintLn(sb);

    }

    public void dumpGrammarRuleCollection(final int indentNbr,
        final String caption,
        final Collection<GrammarRule> grammarRuleCollection)
    {

        dumpGrammarRuleCollection(Integer.MAX_VALUE, indentNbr, caption,
            grammarRuleCollection);
    }

    public void dumpGrammarRuleCollection(final int maxDumpCnt,
        final int indentNbr, final String caption,
        final Collection<? extends GrammarRule> grammarRuleCollection)
    {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_RULE_COLLECTION + caption
                + UtilConstants.DUMP_OF_FIRST + maxDumpCnt);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
            + UtilConstants.DUMP_RULE_COLLECTION_UNDERSCORE);

        if (grammarRuleCollection == null || grammarRuleCollection.isEmpty()) {
            sysOutDumpAPrintLn(indentTbl[indentNbr]
                + UtilConstants.DUMP_RULE_COLLECTION_IS_EMPTY);
            return;
        }

        final Set<GrammarRule> gRSet = new TreeSet<>(
            GrammarRule.RULE_NBR);
        gRSet.addAll(grammarRuleCollection);

        for (final GrammarRule grammarRule : gRSet)
            dumpGrammarRuleFull(indentNbr, grammarRule);
    }

    public void dumpGrammarRuleFull(final int indentNbr,
        final GrammarRule grammarRule)
    {
        StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_GRAMMAR_RULE);

        sb.append(UtilConstants.DUMP_RULE_NBR);
        sb.append(grammarRule.getRuleNbr());

        sb.append(UtilConstants.DUMP_TYPE_CODE);
        sb.append(grammarRule.getGrammarRuleTyp());

        sb.append(UtilConstants.DUMP_MAX_SEQ_NBR);
        sb.append(grammarRule.getMaxSeqNbr());

        sb.append(UtilConstants.DUMP_NBR_HYP_PARAMS_USED);
        sb.append(grammarRule.getNbrHypParamsUsed());

        sysOutDumpAPrintLn(sb);
        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_PARAM_TREE_AS_RPN);
        final RPNStep[] pTTRPN = grammarRule.getParamTransformationTree()
            .convertToRPN();
        for (final RPNStep element : pTTRPN) {
            sb.append(element);
            sb.append(" ");
        }

        sysOutDumpAPrintLn(sb);
        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_PARAM_VARHYP_NODE_ARRAY);
        final ParseNode[] pVHN = grammarRule.getParamVarHypNode();
        for (int i = 0; i < pVHN.length; i++) {
            sb.append(UtilConstants.DUMP_START_BRACKET);
            sb.append(i);
            sb.append(UtilConstants.DUMP_END_BRACKET);
            if (pVHN[i] == null)
                sb.append(" ");
            else {
                sb.append(pVHN[i].stmt.getLabel());
                sb.append(" ");
            }
        }

        sysOutDumpAPrintLn(sb);

        if (grammarRule instanceof NullsPermittedRule)
            dumpNullsPermittedRuleProperties(indentNbr,
                (NullsPermittedRule)grammarRule);
        if (grammarRule instanceof TypeConversionRule)
            dumpTypeConversionRuleProperties(indentNbr,
                (TypeConversionRule)grammarRule);
        if (grammarRule instanceof NotationRule)
            dumpNotationRuleProperties(indentNbr, (NotationRule)grammarRule);
        sysOutDumpAPrintLn(" ");

    }

    public void dumpNotationRuleProperties(final int indentNbr,
        final NotationRule notationRule)
    {
        StringBuilder sb = new StringBuilder();
        String s;
        int padit;

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_NOTATION_RULE);

        s = notationRule.getBaseSyntaxAxiom().getLabel();
        padit = UtilConstants.DUMP_NOTATION_LABEL_PADIT - s.length();
        sb.append(s);
        for (int i = 0; i < padit; i++)
            sb.append(' ');

        sb.append(UtilConstants.DUMP_RULE_COLON);

        s = Integer.valueOf(notationRule.getRuleNbr()).toString();
        padit = UtilConstants.DUMP_NOTATION_RULE_NBR_PADIT - s.length();
        for (int i = 0; i < padit; i++)
            sb.append('0');
        sb.append(s);

        sb.append(UtilConstants.DUMP_RULE_COLON);

        s = notationRule.getGrammarRuleTyp().getId();
        padit = UtilConstants.DUMP_NOTATION_RULE_TYP_PADIT - s.length();
        sb.append(s);
        for (int i = 0; i < padit; i++)
            sb.append(' ');

        sb.append(UtilConstants.DUMP_GRAMMAR_RULE_REPLACEMENT_SYMBOL);

        final Cnst[] ruleFormatExpr = notationRule.getForestRuleExpr();
        for (final Cnst element : ruleFormatExpr) {
            sb.append(element);
            sb.append(" ");
        }

        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);

        sb.append(UtilConstants.DUMP_IS_GIMME_MATCH_NBR);
        sb.append(notationRule.getIsGimmeMatchNbr());
        sysOutDumpAPrintLn(sb);
    }

    public void dumpTypeConversionRuleProperties(final int indentNbr,
        final TypeConversionRule typeConversionRule)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_TYPE_CONVERSION_RULE);
        sb.append(typeConversionRule.getGrammarRuleTyp());
        sb.append(UtilConstants.DUMP_RIGHT_ARROW);
        sb.append(typeConversionRule.getConvTyp());

        sysOutDumpAPrintLn(sb);

    }

    public void dumpNullsPermittedRuleProperties(final int indentNbr,
        final NullsPermittedRule nullsPermittedRule)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_NULLS_PERMITTED_RULE);
        sb.append(nullsPermittedRule.getGrammarRuleTyp());
        sb.append(UtilConstants.DUMP_RIGHT_ARROW);

        sysOutDumpAPrintLn(sb);

    }

    public void dumpTheGrammar(final int indentNbr,
        final Collection<Cnst> grammarTypSet, final Collection<Sym> symTbl)
    {

        final Set<Cnst> cnstWithRules = new TreeSet<>(Sym.ID);

        for (final Sym sym : symTbl) {
            if (!(sym instanceof Cnst))
                continue;
            final Cnst cnst = (Cnst)sym;
            if (cnst.getGRRoot() != null)
                cnstWithRules.add(cnst);
        }

        StringBuilder sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_THE_GRAMMAR);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_THE_GRAMMAR_UNDERSCORE);
        sysOutDumpAPrintLn(sb);

        if (grammarTypSet.isEmpty() || cnstWithRules.isEmpty())
            sb.append(UtilConstants.DUMP_THE_GRAMMAR_IS_EMPTY);

        sb = new StringBuilder();
        sb.append(indentTbl[indentNbr + 1]);

        final String startRuleLit = UtilConstants.DUMP_GRAMMAR_RULE_REPLACEMENT_SYMBOL;

        final String continueRuleLit = UtilConstants.DUMP_RULE_CONTINUATION_LIT;

        for (final Cnst grammarTyp : grammarTypSet) {
            final List<GrammarRule> grammarTypRulesList = new ArrayList<>();
            for (final Cnst cnst : cnstWithRules)
                for (final NotationRule g : GRForest
                    .getRuleCollection(cnst.getGRRoot()))
                    if (g.getGrammarRuleTyp() == grammarTyp)
                        grammarTypRulesList.add(g);
            if (grammarTypRulesList.isEmpty())
                continue;

            sb = new StringBuilder();
            sb.append(indentTbl[indentNbr + 1]);
            sb.append(grammarTyp);
            String currLit = startRuleLit;
            final int typIndentLength = grammarTyp.getId().length();
            for (final GrammarRule g : grammarTypRulesList) {
                sb.append(currLit);
                final Cnst[] ruleFormatExpr = g.getForestRuleExpr();
                for (final Cnst element : ruleFormatExpr) {
                    sb.append(element);
                    sb.append(" ");
                }
                sysOutDumpAPrintLn(sb);
                sb = new StringBuilder();
                sb.append(indentTbl[indentNbr + 1]);
                for (int i = 0; i < typIndentLength; i++)
                    sb.append(" ");
                currLit = continueRuleLit;
            }
            sysOutDumpAPrintLn(" ");
        }
    }

    private void dumpBookManagerSectionDetails(final int indentNbr,
        final RunParmArrayEntry runParm, final BookManager bookManager,
        final Iterable<MObj> iterable, final Section selectedSection)
    {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + runParm.name + "," + runParm.values[0]);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        int prevChapterNbr = -1;
        int prevSectionNbr = -1;

        for (final MObj mObj : iterable) {
            final int sectionNbr = mObj.getSectionNbr();
            if (selectedSection != null) {
                if (sectionNbr > selectedSection.getSectionNbr())
                    return;
                if (sectionNbr < selectedSection.getSectionNbr())
                    continue;
            }
            final Section section = bookManager.getSection(sectionNbr);
            final Chapter chapter = bookManager
                .getChapterForSectionNbr(sectionNbr);
            final int chapterNbr = chapter.getChapterNbr();

            if (chapterNbr != prevChapterNbr) {
                dumpOneBookManagerChapter(indentNbr, chapter);
                prevChapterNbr = chapterNbr;
            }

            if (sectionNbr != prevSectionNbr) {
                dumpOneBookManagerSection(indentNbr + 1, section);
                prevSectionNbr = sectionNbr;
            }

            dumpOneBookManagerSectionDetail(indentNbr + 2, mObj);
        }

    }

    private void dumpBookManagerChapters(final int indentNbr,
        final String caption, final Collection<Chapter> coll)
    {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + caption);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        for (final Chapter c : coll)
            dumpOneBookManagerChapter(indentNbr, c);
    }

    private void dumpBookManagerSections(final int indentNbr,
        final String caption, final Collection<Section> coll)
    {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr] + caption);
        sysOutDumpAPrintLn(
            indentTbl[indentNbr] + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        for (final Section s : coll)
            dumpOneBookManagerSection(indentNbr, s);
    }

    private void dumpOneBookManagerChapter(final int indentNbr,
        final Chapter chapter)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(chapter.toString());
        sysOutDumpAPrintLn(sb);
    }

    private void dumpOneBookManagerSection(final int indentNbr,
        final Section section)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);
        sb.append(section.toString());
        sysOutDumpAPrintLn(sb);
    }

    private void dumpOneBookManagerSectionDetail(final int indentNbr,
        final MObj mObj)
    {
        final StringBuilder sb = new StringBuilder();

        sb.append(indentTbl[indentNbr]);

        if (mObj instanceof Cnst) {
            sb.append(UtilConstants.DUMP_BM_CNST);
            sb.append(mObj.getSectionNbr());
            sb.append(UtilConstants.DUMP_BM_DOT);
            sb.append(mObj.getSectionMObjNbr());
            sb.append(" ");
            sb.append(((Cnst)mObj).toString());
            sysOutDumpAPrintLn(sb);
            return;
        }

        if (mObj instanceof Var) {
            sb.append(UtilConstants.DUMP_BM_VAR);
            sb.append(mObj.getSectionNbr());
            sb.append(UtilConstants.DUMP_BM_DOT);
            sb.append(mObj.getSectionMObjNbr());
            sb.append(" ");
            sb.append(((Var)mObj).toString());
            sysOutDumpAPrintLn(sb);
            return;
        }

        final Stmt stmt = (Stmt)mObj;

        if (mObj instanceof VarHyp)
            sb.append(UtilConstants.DUMP_BM_VARHYP);
        else if (mObj instanceof LogHyp)
            sb.append(UtilConstants.DUMP_BM_LOGHYP);
        else if (mObj instanceof Axiom)
            sb.append(UtilConstants.DUMP_BM_AXIOM);
        else if (mObj instanceof Theorem)
            sb.append(UtilConstants.DUMP_BM_THEOREM);
        else
            sb.append(UtilConstants.DUMP_BM_UNKNOWN);

        sb.append(mObj.getSectionNbr());
        sb.append(UtilConstants.DUMP_BM_DOT);
        sb.append(mObj.getSectionMObjNbr());
        sb.append(" ");
        sb.append(stmt.getLabel());
        sb.append(" ");
        sb.append(UtilConstants.DUMP_BM_EQ_COL);
        sb.append(stmt.getFormula().toString());
        sysOutDumpAPrintLn(sb);
    }
}
