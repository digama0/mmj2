//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  Dump.java  0.07 11/01/2011
 *
 *  Sep-25-2005
 *      -> check grammar.getGrammarInitialized()
 *         in addition to "!= null"
 *
 *  Sep-06-2006:
 *      -->Add TMFF stuff.
 *      -->When printing formula, if !isTMFFEnabled()
 *         don't use TMFF --> do it the old toString() way...
 *
 *  Nov-01-2007 Version 0.05
 *      - Add proofLevel 0 parm to tmffPreferences.renderFormula()
 *        call. No indentation of formulas used in Dump.java.
 *
 *  Aug-01-2008 Version 0.06
 *      - Add PrintBookManagerChapters(),
 *            PrintBookManagerSections(), and
 *            PrintBookManagerSectionDetails().
 *
 *  Version 0.07 - Nov-01-2011:  comment update.
 *      --> Add check for valid parse tree
 *          before calling TMFFPreferences.renderFormula()
 *          in dumpStmtProperties() because TMFF requires
 *          real parse trees except in format number 0.
 */

package mmj.util;

import java.util.*;
import java.io.*;
import mmj.lang.*;
import mmj.verify.*;
import mmj.mmio.*;
import mmj.tmff.*;

/**
 *  Dump started out as just testing code that could be
 *  "throwaway". Now a bit of it is used in mmj.Util.BatchMMJ2
 *  to dump/print info from LogicalSystem and Grammar. The
 *  report is ugly but better than nothing. And it is key
 *  for doing parallel testing because almost every key
 *  data element is output to the printer.
 *  <p>
 *  Dump can employ a PrintWriter or send its stuff to
 *  System.out. The PrintWriter can be set at construction
 *  time or later via a "set" function.
 *  <p>
 *  Most of the Dump print routines are invoked by
 *  BatchMMJ2 but some are leftovers from testing.
 *  Dump is ugly but it works.
 */
public class Dump {

    public PrintWriter sysOut     = null;
    public TMFFPreferences tmffPreferences;

    /**
     *  Default constructor which will print to System.out.
     */
    public Dump() {
        setTMFFPreferences(new TMFFPreferences());
    }

    /**
     *  Construct Dump using a PrintWriter for output.
     */
    public Dump(PrintWriter sysOut) {
        this.sysOut               = sysOut;
        setTMFFPreferences(new TMFFPreferences());
    }

    /**
     *  Sets Dump's SysOut to a new PrintWriter, or null
     *  to revert to writing to System.out.
     *
     *  @param sysOut a PrintWriter, or null for System.out output.
     */
    public void setSysOut(PrintWriter sysOut) {
        this.sysOut               = sysOut;
        setTMFFPreferences(new TMFFPreferences());
    }

    /**
     *  Sets Dump's Text Mode Formula Formatting preference parameters
     *  to a new set of values.
     *  <p>
     *  Note: mmj.util.OutputBoss uses this.
     *  <p>
     *  @param tmffPreferences TMFF Preference parameters.
     */
    public void setTMFFPreferences(TMFFPreferences tmffPreferences) {
        this.tmffPreferences      = tmffPreferences;
    }

    /**
     *  Print a line from a StringBuffer.
     *
     *  @param sb StringBuffer line to be printed.
     */
    public void sysOutDumpAPrintLn(StringBuffer sb) {
        sysOutDumpAPrintLn(sb.toString());
    }

    /**
     *  Print a line from a String.
     *
     *  @param s String line to be printed.
     */
    public void sysOutDumpAPrintLn(String s) {
        if (sysOut == null) {
            System.out.println(s);
        }
        else {
            sysOut.println(s);
        }
    }

    /**
     *  "printSyntaxDetails" is used by BatchMMJ2 to print
     *  all syntax-related information about a LogicalSystem
     *  and its Grammar.
     *
     *  @param caption identifying caption for the report.
     *  @param logicalSystem a LogicalSystem object.
     *  @param grammar a Grammar object derived from the Logical
     *         System.
     */
    public void printSyntaxDetails(
                    String        caption,
                    LogicalSystem logicalSystem,
                    Grammar       grammar) {


        dumpLogSysCounts(1,
                         UtilConstants.DUMP_LOGSYS_COUNTS
                             + caption,
                         logicalSystem.getSymTbl(),
                         logicalSystem.getStmtTbl());

        TreeSet provableLogicStmtTypSet = new TreeSet(MObj.SEQ);
        if (grammar != null &&
            grammar.getGrammarInitialized()) {
            Cnst[] provableLogicStmtTypArray =
                grammar.getProvableLogicStmtTypArray();
            for (int i = 0;
                 i < provableLogicStmtTypArray.length;
                 i++) {
                provableLogicStmtTypSet.add(
                    provableLogicStmtTypArray[i]);
            }
            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1,
                  UtilConstants.DUMP_PROVABLE_TYP_SET
                    + caption,
                  provableLogicStmtTypSet);


            TreeSet logicStmtTypSet = new TreeSet(MObj.SEQ);
            Cnst[] logicStmtTypArray =
                grammar.getLogicStmtTypArray();
            for (int i = 0;
                 i < logicStmtTypArray.length;
                 i++) {
                logicStmtTypSet.add(
                    logicStmtTypArray[i]);
            }
            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1,
                       UtilConstants.DUMP_PROVABLE_TYP_SET
                           + caption,
                       logicStmtTypSet);


            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1,
                       UtilConstants.DUMP_VARHYP_TYP_SET
                           + caption,
                       grammar.getVarHypTypSet());

            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1,
                       UtilConstants.DUMP_SYNTAX_AXIOM_TYP_SET
                           + caption,
                        grammar.getSyntaxAxiomTypSet());

            sysOutDumpAPrintLn(" ");
            dumpSymTbl(1,
                       UtilConstants.DUMP_NULLS_PERMITTED_TYP_SET
                           + caption,
                       grammar.getNullsPermittedTypSet());

        }
        sysOutDumpAPrintLn(" ");
        Collection symTblValues   =
            logicalSystem.getSymTbl().values();
        dumpSymTbl(1,
                   UtilConstants.DUMP_LOGSYS_SYM_TBL
                        + caption,
                   symTblValues);

        if (grammar != null &&
            grammar.getGrammarInitialized()) {

            sysOutDumpAPrintLn(" ");
            dumpGrammarRuleCollection(
                          UtilConstants.DUMP_GRAMMAR_RULE_MAX_PRINT,
                          1,
                          UtilConstants.DUMP_NULLS_PERMITTED_LIST,
                          grammar.getNullsPermittedGRList());

            sysOutDumpAPrintLn(" ");
            dumpGrammarRuleCollection(
                          UtilConstants.DUMP_GRAMMAR_RULE_MAX_PRINT,
                          1,
                          UtilConstants.DUMP_TYPE_CONVERSION_LIST,
                          grammar.getTypeConversionGRList());

            sysOutDumpAPrintLn(" ");
            dumpGrammarRuleCollection(
                          UtilConstants.DUMP_GRAMMAR_RULE_MAX_PRINT,
                          1,
                          UtilConstants.DUMP_NOTATION_LIST,
                          grammar.getNotationGRSet());

            sysOutDumpAPrintLn(" ");
            dumpTheGrammar(1,
                           grammar.getVarHypTypSet(),
                           symTblValues);
        }

    }

    /**
     *  "printOneStatementDetails" is used by BatchMMJ2 to print
     *  information about a single Stmt.
     *
     *  @param stmt a Stmt object.
     */
    public void printOneStatementDetails(Stmt stmt) {
        dumpOneStmt(1,       //indentNbr
                    stmt);
    }

    /**
     *  "printStatementDetails" is used by BatchMMJ2 to print
     *  Metamath Statement details, up to the limit imposed
     *  by maxStatementPrintCountParm.
     *
     *  @param caption identifying caption for the report.
     *  @param stmtTbl Statement Table (Map)
     *  @param maxStatementPrintCountParm max number of Stmt's
     *         to print.
     */
    public void printStatementDetails(
                    String        caption,
                    Map           stmtTbl,
                    int           maxStatementPrintCountParm) {

        sysOutDumpAPrintLn(" ");
        dumpStmtTbl(maxStatementPrintCountParm,  //maxDumpCnt
                    1,                           //indentNbr
                    UtilConstants.DUMP_LOGSYS_STMT_TBL
                        + caption,
                    stmtTbl.values());

    }

    /**
     *  "printBookManagerChapters" is used by BatchMMJ2 to print
     *  BookManager Chapter information.
     *
     *  This function is used primarily as a way to generate
     *  test output but the RunParm "PrintBookManagerChapters"
     *  may be useful for mmj2 users.
     *
     *  @param caption identifying caption for the report.
     *  @param bookManager the BookManager in use.
     */
    public void printBookManagerChapters(
                    String        caption,
                    BookManager   bookManager) {

        sysOutDumpAPrintLn(" ");
        dumpBookManagerChapters(
                        1, //indentNbr
                        caption,
                        bookManager.getChapterList().iterator());
    }

    /**
     *  "printBookManagerSections" is used by BatchMMJ2 to print
     *  BookManager Section information.
     *
     *  This function is used primarily as a way to generate
     *  test output but the RunParm "PrintBookManagerSections"
     *  may be useful for mmj2 users.
     *
     *  @param caption identifying caption for the report.
     *  @param bookManager the BookManager in use.
     */
    public void printBookManagerSections(
                    String        caption,
                    BookManager   bookManager) {

        sysOutDumpAPrintLn(" ");
        dumpBookManagerSections(
                        1, //indentNbr
                        caption,
                        bookManager.getSectionList().iterator());
    }

    /**
     *  "printBookManagerSectionDetails" is used by BatchMMJ2 to print
     *  BookManager Section, MObj and Chapter information.
     *
     *  This function is used primarily as a way to generate
     *  test output but the RunParm "PrintBookManagerSectionDetails"
     *  may be useful for mmj2 users.
     *
     *  @param runParm contains RunParm name and values.
     *  @param logicalSystem the LogicalSystem in use.
     *  @param bookManager the BookManager in use.
     *  @param section the Section to be printed or null if
     *         all Sections are to be printed.
     */
    public void printBookManagerSectionDetails(
                    RunParmArrayEntry   runParm,
                    LogicalSystem       logicalSystem,
                    BookManager         bookManager,
                    Section             section) {

        sysOutDumpAPrintLn(" ");

        dumpBookManagerSectionDetails(
                1, //indentNbr
                runParm,
                bookManager,
                bookManager.getSectionMObjIterator(logicalSystem),
                section);
    }

    public String[]  keyArray = new String[100];
    public int       keyArrayCount = 0;

    public static final String[] indentTbl = {
        "  ",
        "    ",
        "      ",
        "        ",
        "          ",
        "            ",
        "              ",
        "                ",
        "                  ",
        "                    ",
        "                      ",
        "                        ",
        "                          ",
        "                            ",
        "                              ",
        "                                ",
        "                                  ",
        "                                    ",
        "                                      ",
        "                                        ",
        "                                          ",
        "                                            ",
        "                                              ",
        "                                                ",
        "                                                  ",
        "                                                    ",
        "                                                      ",
        "                                                        ",
        "                                                          ",
        "                                                            ",
        "                                                              "
    };

    public void dumpLogSys(int           indentNbr,
                           String        caption,
                           LogicalSystem logSys) {
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_LOGICAL_SYSTEM
                           + caption
                           + UtilConstants.DUMP_START);

        Collection symTbl  = (logSys.getSymTbl()).values();
        Collection stmtTbl = (logSys.getStmtTbl()).values();

        dumpLogSysCounts(indentNbr + 1,
                         caption,
                         symTbl,
                         stmtTbl);
        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_LOGICAL_SYSTEM
                           + caption
                           + UtilConstants.DUMP_END);
    }

    public void dumpLogSysCounts(int        indentNbr,
                                 String     caption,
                                 Map        symTbl,
                                 Map        stmtTbl) {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + " "
                           + caption
                           + UtilConstants.DUMP_SYM_TBL_SIZE
                           + symTbl.size());

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + " "
                           + caption
                           + UtilConstants.DUMP_STMT_TBL_SIZE
                           + stmtTbl.size());

    }


    public void dumpLogSysCounts(int        indentNbr,
                                 String     caption,
                                 Collection symTbl,
                                 Collection stmtTbl) {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + " "
                           + caption
                           + UtilConstants.DUMP_SYM_TBL_SIZE
                           + symTbl.size());

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + " "
                           + caption
                           + UtilConstants.DUMP_STMT_TBL_SIZE
                           + stmtTbl.size());

    }

    public void dumpSymTbl(int        indentNbr,
                           String     caption,
                           Collection symTbl) {
        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_SYM_TBL
                           + caption);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_SYM_TBL_UNDERSCORE
                           );

        if (symTbl == null) {
            sysOutDumpAPrintLn(indentTbl[indentNbr]
                               +
                               UtilConstants.DUMP_SYM_TBL_IS_EMPTY);
            return;
        }

        TreeSet   symSet = new TreeSet(MObj.SEQ);
        symSet.addAll(symTbl);

        Sym      sym;
        Iterator iC = symSet.iterator();
        while (iC.hasNext()) {
            sym             = (Sym)iC.next();
            if (sym.isVar()) {
                dumpSymVarFull(indentNbr,
                               (Var)sym);
            }
            else {
                dumpSymCnstFull(indentNbr,
                               (Cnst)sym);
            }
        }
    }

    public void dumpStmtTbl(int       indentNbr,
                           String     caption,
                           Collection stmtTbl) {
        dumpStmtTbl(Integer.MAX_VALUE,
                    indentNbr,
                    caption,
                    stmtTbl);
    }

    public void dumpStmtTbl(int       maxDumpCnt,
                            int       indentNbr,
                            String     caption,
                            Collection stmtTbl) {
        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_STMT_TBL
                           + caption
                           + UtilConstants.DUMP_OF_FIRST
                           + maxDumpCnt);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        if (stmtTbl == null) {
            sysOutDumpAPrintLn(indentTbl[indentNbr]
                               +
                               UtilConstants.DUMP_STMT_TBL_IS_EMPTY);
            return;
        }

        TreeSet   stmtSet = new TreeSet(MObj.SEQ);
        stmtSet.addAll(stmtTbl);

        Stmt      stmt;
        int       dumpCnt = 0;
        Iterator  iC = stmtSet.iterator();
        while (iC.hasNext() &&
               dumpCnt++ < maxDumpCnt) {
            stmt             = (Stmt)iC.next();
            dumpOneStmt(indentNbr,
                        stmt);
        }
    }

    public void dumpOneStmt(int    indentNbr,
                            Stmt   stmt) {
        sysOutDumpAPrintLn(indentTbl[indentNbr]);

        if (stmt instanceof Theorem) {
            dumpStmtTheoremFull(indentNbr,
                                (Theorem)stmt);
            return;
        }
        if (stmt instanceof Axiom) {
            dumpStmtAxiomFull(indentNbr,
                                (Axiom)stmt);
            return;
        }
        if (stmt instanceof LogHyp) {
            dumpStmtLogHypFull(indentNbr,
                                (LogHyp)stmt);
            return;
        }
        if (stmt instanceof VarHyp) {
            dumpStmtVarHypFull(indentNbr,
                               (VarHyp)stmt);
            return;
        }
        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_DUMP_STMT_UNRECOG_1
            + stmt.getLabel());
    }


    public void dumpStmtTheoremFull(int          indentNbr,
                                    Theorem      theorem) {

        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_THEOREM);

        ++indentNbr;

        dumpStmtProperties(indentNbr,
                           sb,
                           theorem);
        dumpAssrtProperties(indentNbr,
                            theorem);

        sb = new StringBuffer();
        OptFrame optFrame = theorem.getOptFrame();
        sb.append(indentTbl[indentNbr]);
        sb.append(
            UtilConstants.DUMP_OPT_FRAME_HYP_ARRAY);
        char comma = ' ';
        for (int i = 0; i < optFrame.optHypArray.length; i++) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(optFrame.optHypArray[i].getLabel());
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);


        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_OPT_FRAME_DJ_VARS);
        comma = ' ';
        for (int i = 0; i < optFrame.optDjVarsArray.length; i++) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(optFrame.optDjVarsArray[i].getVarLo());
            sb.append(" ");
            sb.append(optFrame.optDjVarsArray[i].getVarHi());
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);


        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_PROOF);
        Stmt[] proof = theorem.getProof();
        for (int i = 0; i < proof.length; i++) {
            if (proof[i] == null) {
                sb.append(UtilConstants.DUMP_PROOF_MISSING_STEP);
            }
            else {
                sb.append(proof[i].getLabel());
                sb.append(" ");
            }
        }
        sysOutDumpAPrintLn(sb);

        sysOutDumpAPrintLn(" ");

    }


    public void dumpStmtAxiomFull(int          indentNbr,
                                  Axiom        axiom) {

        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_AXIOM);

        ++indentNbr;

        dumpStmtProperties(indentNbr,
                           sb,
                           axiom);
        dumpAssrtProperties(indentNbr,
                            axiom);

        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);

        int[] syntaxAxiomVarHypReseq =
              axiom.getSyntaxAxiomVarHypReseq();
        if (syntaxAxiomVarHypReseq != null) {
            sb.append(UtilConstants.DUMP_VARHYP_RESEQ);
            char comma = ' ';
            for (int i = 0; i < syntaxAxiomVarHypReseq.length; i++) {
                sb.append(comma);
                comma = UtilConstants.DUMP_COMMA;
                sb.append(syntaxAxiomVarHypReseq[i]);
            }
            sb.append(UtilConstants.DUMP_END_BRACKET);
        }

        if (axiom.getSyntaxAxiomHasUniqueCnst()) {
            sb.append(UtilConstants.DUMP_AXIOM_UNIQUE_CNST);
        }

        sysOutDumpAPrintLn(sb);

        sysOutDumpAPrintLn(" ");

    }


    public void dumpStmtLogHypFull(int           indentNbr,
                                   LogHyp       logHyp) {

        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_LOGHYP);

        ++indentNbr;

        dumpStmtProperties(indentNbr,
                           sb,
                           logHyp);
        sysOutDumpAPrintLn(" ");


    }


    public void dumpStmtVarHypFull(int          indentNbr,
                                   VarHyp       varHyp) {

        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_VARHYP);

        ++indentNbr;

        dumpStmtProperties(indentNbr,
                           sb,
                           varHyp);
        sysOutDumpAPrintLn(" ");


    }

    public void dumpAssrtProperties(int indentNbr,
                                    Assrt assrt) {
        StringBuffer sb = new StringBuffer();
        MandFrame mandFrame = assrt.getMandFrame();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_MAND_FRAME_HYP_ARRAY);
        char comma = ' ';
        for (int i = 0; i < mandFrame.hypArray.length; i++) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(mandFrame.hypArray[i].getLabel());
            sb.append(" ");
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_MAND_FRAME_DJ_VARS);
        comma = ' ';
        for (int i = 0; i < mandFrame.djVarsArray.length; i++) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(mandFrame.djVarsArray[i].getVarLo());
            sb.append(" ");
            sb.append(mandFrame.djVarsArray[i].getVarHi());
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

    }


    public void dumpStmtProperties(int          indentNbr,
                                   StringBuffer sb,
                                   Stmt         stmt) {

        sb.append(stmt.getLabel());

        sb.append(UtilConstants.DUMP_TYP);
        sb.append(stmt.getTyp());

        if (stmt.isActive()) {
            sb.append(UtilConstants.DUMP_IS_ACTIVE);
        }

        if (stmt.isAssrt()) {
            sb.append(UtilConstants.DUMP_IS_ASSRT);
        }

        if (stmt.isHyp()) {
            sb.append(UtilConstants.DUMP_IS_HYP);
        }

        if (stmt.isCnst()) {
            sb.append(UtilConstants.DUMP_IS_CNST);
        }

        sb.append(UtilConstants.DUMP_MAND_VARHYP_ARRAY);
        VarHyp[] varHypArray = stmt.getMandVarHypArray();
        char comma = ' ';
        for (int i = 0; i < varHypArray.length; i++) {
            sb.append(comma);
            comma = UtilConstants.DUMP_COMMA;
            sb.append(varHypArray[i].getLabel());
            sb.append(" ");
        }
        sb.append(UtilConstants.DUMP_END_BRACKET);
        sysOutDumpAPrintLn(sb);

        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_FORMULA);


        if (tmffPreferences.isTMFFEnabled()
            &&
            (stmt.getExprParseTree() != null)
            &&
            (stmt.getExprParseTree().getRoot().getStmt() != stmt)) {
            // ok!
            TMFFStateParams tmffSP
                                  =
                new TMFFStateParams(sb,
                                    sb.length(),
                                    tmffPreferences);
            tmffSP.setLeftmostColNbr(sb.length() + 2); //local override!
            tmffPreferences.renderFormula(tmffSP,
                                          stmt.getExprParseTree(),
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

        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_EXPR_RPN);
        Stmt[] exprRPN = stmt.getExprRPN();
        if (exprRPN != null) {
            for (int i = 0; i < exprRPN.length; i++) {
                sb.append(exprRPN[i].getLabel());
                sb.append(" ");
            }
        }
        sysOutDumpAPrintLn(sb);
    }


    public void dumpSymVarFull(int indentNbr,
                               Var var) {
        VarHyp       varHyp;
        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_VAR);
        sb.append(var.getId());

        if (var.isActive()) {
            sb.append(UtilConstants.DUMP_IS_ACTIVE);
        }

        varHyp = var.getActiveVarHyp();
        if (varHyp != null) {
            sb.append(UtilConstants.DUMP_ACTIVE_VARHYP);
            sb.append(varHyp.getLabel());
        }

        sysOutDumpAPrintLn(sb);
    }


    public void dumpSymCnstFull(int          indentNbr,
                                Cnst         cnst) {
        StringBuffer sb = new  StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_CNST);

        sb.append(cnst.getId());

        if (cnst.getIsVarTyp()) {
            sb.append(UtilConstants.DUMP_IS_VAR_TYP);
        }

        if (cnst.getIsGrammaticalTyp()) {
            sb.append(UtilConstants.DUMP_IS_GRAMMATICAL_TYP);
        }
        if (cnst.getIsProvableLogicStmtTyp()) {
            sb.append(UtilConstants.DUMP_IS_PROVABLE_TYP);
        }
        if (cnst.getIsLogicStmtTyp()) {
            sb.append(UtilConstants.DUMP_IS_LOGIC_TYP);
        }
        if (cnst.getIsSyntaxAxiomTyp()) {
            sb.append(UtilConstants.DUMP_IS_SYNTAX_AXIOM_TYP);
        }

        if (cnst.getLen1CnstNotationRule() != null) {
            sb.append(UtilConstants.DUMP_LEN1_CNST_RULE_NBR
                      + cnst.getLen1CnstNotationRule(
                                ).getRuleNbr()
                      + UtilConstants.DUMP_LEN1_CNST_AXIOM
                      + cnst.getLen1CnstNotationRule(
                          ).getBaseSyntaxAxiom().getLabel());
        }

        Collection c = cnst.getEarleyFIRST();
        if (c != null) {
            Iterator f = c.iterator();
            sb.append(UtilConstants.DUMP_EARLEY_FIRST);
            while (f.hasNext()) {
                sb.append(f.next());
                sb.append(" ");
            }
            sb.append(UtilConstants.DUMP_END_BRACKET);
        }

        sysOutDumpAPrintLn(sb);

    }

    public void dumpGrammarRuleCollection(
                            int        indentNbr,
                            String     caption,
                            Collection grammarRuleCollection) {

        dumpGrammarRuleCollection(Integer.MAX_VALUE,
                                  indentNbr,
                                  caption,
                                  grammarRuleCollection);
    }

    public void dumpGrammarRuleCollection(
                        int        maxDumpCnt,
                        int        indentNbr,
                        String     caption,
                        Collection grammarRuleCollection) {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_RULE_COLLECTION
                           + caption
                           + UtilConstants.DUMP_OF_FIRST
                           + maxDumpCnt);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                     + UtilConstants.DUMP_RULE_COLLECTION_UNDERSCORE);

        if (grammarRuleCollection == null ||
            grammarRuleCollection.size() == 0) {
            sysOutDumpAPrintLn(indentTbl[indentNbr]
                     + UtilConstants.DUMP_RULE_COLLECTION_IS_EMPTY);
            return;
        }

        TreeSet   gRSet = new TreeSet(GrammarRule.RULE_NBR);
        gRSet.addAll(grammarRuleCollection);

        GrammarRule grammarRule;
        Iterator iterator = gRSet.iterator();
        while (iterator.hasNext()) {
            grammarRule = (GrammarRule)iterator.next();
            dumpGrammarRuleFull(indentNbr,
                                grammarRule);
        }
    }

    public void dumpGrammarRuleFull(int indentNbr,
                                    GrammarRule grammarRule) {
        StringBuffer sb = new  StringBuffer();

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
        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_PARAM_TREE_AS_RPN);
        Stmt[] pTTRPN =
            grammarRule.getParamTransformationTree().convertToRPN();
        for (int i = 0; i < pTTRPN.length; i++) {
            sb.append(pTTRPN[i].getLabel());
            sb.append(" ");
        }

        sysOutDumpAPrintLn(sb);
        sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_PARAM_VARHYP_NODE_ARRAY);
        ParseNode[] pVHN =
            grammarRule.getParamVarHypNode();
        for (int i = 0; i < pVHN.length; i++) {
            sb.append(UtilConstants.DUMP_START_BRACKET);
            sb.append(i);
            sb.append(UtilConstants.DUMP_END_BRACKET);
            if (pVHN[i] == null) {
                sb.append(" ");
            }
            else {
                sb.append(pVHN[i].getStmt().getLabel());
                sb.append(" ");
            }
        }

        sysOutDumpAPrintLn(sb);

        if (grammarRule instanceof NullsPermittedRule) {
            dumpNullsPermittedRuleProperties(
                                 indentNbr,
                                 (NullsPermittedRule)grammarRule);
        }
        if (grammarRule instanceof TypeConversionRule) {
            dumpTypeConversionRuleProperties(
                                 indentNbr,
                                 (TypeConversionRule)grammarRule);
        }
        if (grammarRule instanceof NotationRule) {
            dumpNotationRuleProperties(
                                 indentNbr,
                                 (NotationRule)grammarRule);
        }
        sysOutDumpAPrintLn(" ");


    }

    public void dumpNotationRuleProperties(
                                     int indentNbr,
                                     NotationRule notationRule) {
        StringBuffer sb = new  StringBuffer();
        String       s;
        int          padit;

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_NOTATION_RULE);

        s     = notationRule.getBaseSyntaxAxiom().getLabel();
        padit = UtilConstants.DUMP_NOTATION_LABEL_PADIT - s.length();
        sb.append(s);
        for (int i = 0; i < padit; i++) {
            sb.append(' ');
        }

        sb.append(UtilConstants.DUMP_RULE_COLON);

        s = (new Integer(notationRule.getRuleNbr())).toString();
        padit = UtilConstants.DUMP_NOTATION_RULE_NBR_PADIT
                - s.length();
        for (int i = 0; i < padit; i++) {
            sb.append('0');
        }
        sb.append(s);

        sb.append(UtilConstants.DUMP_RULE_COLON);

        s = notationRule.getGrammarRuleTyp().getId();
        padit = UtilConstants.DUMP_NOTATION_RULE_TYP_PADIT
                - s.length();
        sb.append(s);
        for (int i = 0; i < padit; i++) {
            sb.append(' ');
        }

        sb.append(UtilConstants.DUMP_GRAMMAR_RULE_REPLACEMENT_SYMBOL);

        Cnst[] ruleFormatExpr = notationRule.getForestRuleExpr();
        for (int i = 0; i < ruleFormatExpr.length; i++) {
            sb.append(ruleFormatExpr[i]);
            sb.append(" ");
        }

        sysOutDumpAPrintLn(sb);

        sb = new  StringBuffer();
        sb.append(indentTbl[indentNbr]);


        sb.append(UtilConstants.DUMP_IS_GIMME_MATCH_NBR);
        sb.append(notationRule.getIsGimmeMatchNbr());
        sysOutDumpAPrintLn(sb);
    }

    public void dumpTypeConversionRuleProperties(
                             int indentNbr,
                             TypeConversionRule typeConversionRule) {
        StringBuffer sb = new  StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_TYPE_CONVERSION_RULE);
        sb.append(typeConversionRule.getGrammarRuleTyp());
        sb.append(UtilConstants.DUMP_RIGHT_ARROW);
        sb.append(typeConversionRule.getConvTyp());

        sysOutDumpAPrintLn(sb);

    }

    public void dumpNullsPermittedRuleProperties(
                             int indentNbr,
                             NullsPermittedRule nullsPermittedRule) {
        StringBuffer sb = new  StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_NULLS_PERMITTED_RULE);
        sb.append(nullsPermittedRule.getGrammarRuleTyp());
        sb.append(UtilConstants.DUMP_RIGHT_ARROW);

        sysOutDumpAPrintLn(sb);

    }

    public void dumpTheGrammar(
                             int        indentNbr,
                             Collection grammarTypSet,
                             Collection symTbl) {

        TreeSet   cnstWithRules = new TreeSet(Sym.ID);

        Iterator  symIterator   = symTbl.iterator();
        Cnst cnst;
        Sym  sym;
        while (symIterator.hasNext()) {
            sym = (Sym)symIterator.next();
            if (!sym.isCnst()) {
                continue;
            }
            cnst = (Cnst)sym;
            if (cnst.getGRRoot() != null) {
                cnstWithRules.add(cnst);
            }
        }

        StringBuffer sb = new StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_THE_GRAMMAR);
        sysOutDumpAPrintLn(sb);

        sb = new  StringBuffer();
        sb.append(indentTbl[indentNbr]);
        sb.append(UtilConstants.DUMP_THE_GRAMMAR_UNDERSCORE);
        sysOutDumpAPrintLn(sb);

        if (grammarTypSet.size() == 0 ||
            cnstWithRules.size() == 0) {
            sb.append(UtilConstants.DUMP_THE_GRAMMAR_IS_EMPTY);
        }

        sb = new  StringBuffer();
        sb.append(indentTbl[indentNbr + 1]);

        Cnst[]      ruleFormatExpr;

        String      startRuleLit  =
            UtilConstants.DUMP_GRAMMAR_RULE_REPLACEMENT_SYMBOL;

        String      continueRuleLit
                                  =
            UtilConstants.DUMP_RULE_CONTINUATION_LIT;

        String      currLit;
        int         typIndentLength;
        Iterator    ruleIterator;
        ArrayList   grammarTypRulesList;
        GrammarRule g;
        Iterator    cnstRuleIterator;
        Collection  cnstRuleCollection;
        Cnst        grammarTyp;
        Iterator    cnstIterator;
        Iterator    typIterator     = grammarTypSet.iterator();
        while (typIterator.hasNext()) {
            grammarTyp = (Cnst)typIterator.next();
            grammarTypRulesList = new ArrayList();
            cnstIterator = cnstWithRules.iterator();
            while (cnstIterator.hasNext()) {
                cnst = (Cnst)cnstIterator.next();
                cnstRuleCollection =
                    GRForest.getRuleCollection(cnst.getGRRoot());
                cnstRuleIterator = cnstRuleCollection.iterator();
                while (cnstRuleIterator.hasNext()) {
                    g = (GrammarRule)cnstRuleIterator.next();
                    if (g.getGrammarRuleTyp() == grammarTyp) {
                        grammarTypRulesList.add(g);
                    }
                }
            }
            if (grammarTypRulesList.size() == 0) {
                continue;
            }

            sb = new  StringBuffer();
            sb.append(indentTbl[indentNbr + 1]);
            sb.append(grammarTyp);
            currLit         = startRuleLit;
            typIndentLength = grammarTyp.getId().length();
            ruleIterator = grammarTypRulesList.iterator();
            while (ruleIterator.hasNext()) {
                g = (GrammarRule)ruleIterator.next();
                sb.append(currLit);
                ruleFormatExpr = g.getForestRuleExpr();
                for (int i = 0; i < ruleFormatExpr.length; i++) {
                    sb.append(ruleFormatExpr[i]);
                    sb.append(" ");
                }
                sysOutDumpAPrintLn(sb);
                sb = new  StringBuffer();
                sb.append(indentTbl[indentNbr + 1]);
                for (int i = 0; i < typIndentLength; i++) {
                    sb.append(" ");
                }
                currLit = continueRuleLit;
            }
            sysOutDumpAPrintLn(" ");
        }
    }

    private void dumpBookManagerSectionDetails(
                                 int                indentNbr,
                                 RunParmArrayEntry  runParm,
                                 BookManager        bookManager,
                                 Iterator           iterator,
                                 Section            selectedSection) {

        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + runParm.name
                           + ","
                           + runParm.values[0]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        int     prevChapterNbr    = -1;
        int     prevSectionNbr    = -1;
        int     nbrDetailsPrinted = 0;

        Chapter chapter;
        Section section;
        int     chapterNbr;
        int     sectionNbr;

        MObj    mObj;
        while (iterator.hasNext()) {

            mObj                  = (MObj)iterator.next();
            sectionNbr            = mObj.getSectionNbr();
            if (selectedSection != null) {
                if (sectionNbr > selectedSection.getSectionNbr()) {
                    return;
                }
                if (sectionNbr < selectedSection.getSectionNbr()) {
                    continue;
                }
            }
            section               =
                bookManager.getSection(sectionNbr);
            chapter               =
                bookManager.getChapterForSectionNbr(sectionNbr);
            chapterNbr            = chapter.getChapterNbr();

            if (chapterNbr != prevChapterNbr) {
                dumpOneBookManagerChapter(indentNbr,
                                          chapter);
                prevChapterNbr    = chapterNbr;
            }

            if (sectionNbr != prevSectionNbr) {
                dumpOneBookManagerSection(indentNbr + 1,
                                          section);
                prevSectionNbr    = sectionNbr;
            }

            dumpOneBookManagerSectionDetail(indentNbr + 2,
                                            mObj);
            ++nbrDetailsPrinted;
        }

    }

    private void dumpBookManagerChapters(int      indentNbr,
                                         String   caption,
                                         Iterator iterator) {


        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + caption);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        while (iterator.hasNext()) {
            dumpOneBookManagerChapter(indentNbr,
                                      (Chapter)iterator.next());
        }
    }

    private void dumpBookManagerSections(int      indentNbr,
                                         String   caption,
                                         Iterator iterator) {


        sysOutDumpAPrintLn(indentTbl[indentNbr]);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + caption);
        sysOutDumpAPrintLn(indentTbl[indentNbr]
                           + UtilConstants.DUMP_STMT_TBL_UNDERSCORE);

        while (iterator.hasNext()) {
            dumpOneBookManagerSection(indentNbr,
                                      (Section)iterator.next());
        }
    }

    private void dumpOneBookManagerChapter(int      indentNbr,
                                           Chapter  chapter) {
        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(chapter.toString());
        sysOutDumpAPrintLn(sb);
    }

    private void dumpOneBookManagerSection(int      indentNbr,
                                           Section  section) {
        StringBuffer sb = new StringBuffer();

        sb.append(indentTbl[indentNbr]);
        sb.append(section.toString());
        sysOutDumpAPrintLn(sb);
    }

    private void dumpOneBookManagerSectionDetail
                                    (int    indentNbr,
                                     MObj   mObj) {
        StringBuffer sb           = new StringBuffer();

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

        Stmt stmt             = (Stmt)mObj;

        if (mObj instanceof VarHyp) {
            sb.append(UtilConstants.DUMP_BM_VARHYP);
        }
        else {
            if (mObj instanceof LogHyp) {
                sb.append(UtilConstants.DUMP_BM_LOGHYP);
            }
            else {
                if (mObj instanceof Axiom) {
                    sb.append(UtilConstants.DUMP_BM_AXIOM);
                }
                else {
                    if (mObj instanceof Theorem) {
                        sb.append(UtilConstants.DUMP_BM_THEOREM);
                    }
                    else {
                        sb.append(UtilConstants.DUMP_BM_UNKNOWN);
                    }
                }
            }
        }

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
