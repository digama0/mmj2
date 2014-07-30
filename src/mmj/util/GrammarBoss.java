//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * GrammarBoss.java  0.04 08/01/2008
 *
 * Nov-26-2005:
 *     -->fix comment(s).RunParmFile lines at 1. Doh.
 *
 * Dec-14-2005: change parse to return ParseTree
 *              instead of RPN -- will store
 *              tree instead of RPN to optimize
 *              ProofAsst processing.
 *
 * Version 0.05 08/01/2008
 *     -->Moved processing of ProvableLogicStmtType
 *        and                 LogicStmtType
 *        to LogicalSystemBoss.
 *     -->Modified to update LogicalSystem with the grammar
 *        (SyntaxVerifier) after the "Parse" RunParm so that
 *        if new theorems are added via TheoremLoader, then
 *        LogicalSystem will know it needs to parse the
 *        new statements (as opposed to waiting for the
 *        inevitable "Parse,*" RunParm later.)
 *     -->Totally initialize after "LoadFile" RunParm,
 *        just like "Clear".
 */

package mmj.util;

import java.io.FileNotFoundException;
import java.io.IOException;

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.mmio.MMIOException;
import mmj.verify.Grammar;
import mmj.verify.GrammarConstants;

/**
 * Responsible for building and using Grammar.
 * <ul>
 * <li>If non-executable parm, validate, store and "consume"
 * <li>If LoadFile command, mark Grammar as "not initialized" but do not
 * "consume" the command.
 * <li>If InitializeGrammar command, invoke Grammar's initialize method, get
 * error status, print and clear messages and consume.
 * <li>Use grammar.getGrammarInitialized() to check if grammar is "initialized"
 * -- and then store the result (we will not "set" the grammarInitialized flag
 * in the Grammar object, but keep track of it circumstances outside of Grammar
 * may require re-initialization.)
 * <li>If Parse parm, initialize grammar if "not initialized" (regardless of the
 * fact that the user may have input an Initialize RunParm followed by a Parse).
 * Then if initialize successful, process the parse parm, print-and-clear
 * messages and "consume" parm.
 * <li>Remember that Messages, LogicalSystem and other objects may have changed.
 * Don't worry about whether or not file is loaded, the LogicalSystemBoss will
 * throw an exception if attempt is made to retrieve LogicalSystem if it is not
 * loaded and error free.
 * <li>If clear, set grammar, etc. to null.
 * </ul>
 */
public class GrammarBoss extends Boss {

    protected Grammar grammar;
    protected boolean grammarInitialized;
    protected boolean allStatementsParsedSuccessfully;

//PATCH 2008-08-01 MOVED TO LogicalSystemBoss
//  protected String         provableLogicStmtTypeParm;
//  protected String         logicStmtTypeParm;
//END-PATCH

    protected Boolean grammarAmbiguityParm;
    protected Boolean statementAmbiguityParm;

    /**
     * Constructor with BatchFramework for access to environment.
     * 
     * @param batchFramework for access to environment.
     */
    public GrammarBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     * Returns true if Grammar initialized successfully.
     * 
     * @return true if Grammar initialized successfully.
     */
    public boolean getGrammarInitialized() {
        return grammarInitialized;
    }

    /**
     * Returns true if all statements parsed successfully.
     * 
     * @return true if all statements parsed successfully.
     */
    public boolean getAllStatementsParsedSuccessfully() {
        return allStatementsParsedSuccessfully;
    }

    /**
     * Executes a single command from the RunParmFile.
     * 
     * @param runParm the RunParmFile line to execute.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException
    {

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()) == 0)
        {
            grammar = null;
            grammarInitialized = false;
            allStatementsParsedSuccessfully = false;
//PATCH 2008-08-01 MOVED TO LogicalSystemBoss
//          provableLogicStmtTypeParm
//                            = null;
//          logicStmtTypeParm = null;
//END-PATCH 2008-08-01 MOVED TO LogicalSystemBoss
            grammarAmbiguityParm = null;
            statementAmbiguityParm = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(UtilConstants.RUNPARM_LOAD_FILE
            .name()) == 0)
        {
            grammar = null;
            grammarInitialized = false;
            allStatementsParsedSuccessfully = false;
            grammarAmbiguityParm = null;
            statementAmbiguityParm = null;
            return false; // not "consumed"
        }

//PATCH 2008-08-01: MOVE TO LogicalSystemBoss
//      if (runParm.name.compareToIgnoreCase(
//          UtilConstants.RUNPARM_PROVABLE_LOGIC_STMT_TYPE)
//          == 0) {
//          editProvableLogicStmtType(runParm);
//          return true; // "consumed"
//      }
//
//      if (runParm.name.compareToIgnoreCase(
//          UtilConstants.RUNPARM_LOGIC_STMT_TYPE)
//          == 0) {
//          editLogicStmtType(runParm);
//          return true; // "consumed"
//      }
//END-PATCH 2008-08-01

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GRAMMAR_AMBIGUITY_EDITS
                .name()) == 0)
        {
            editGrammarAmbiguityEdits(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_STATEMENT_AMBIGUITY_EDITS
                .name()) == 0)
        {
            editStatementAmbiguityEdits(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_INITIALIZE_GRAMMAR
                .name()) == 0)
        {
            doInitializeGrammar(runParm);
            return true; // "consumed"
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_PARSE.name()) == 0)
        {
            doParse(runParm);
            return true; // "consumed"
        }

        return false;
    }

    /**
     * Fetch a Grammar object, building it if necessary from previously input
     * RunParms.
     * <p>
     * NOTE: The returned Grammar is "ready to go" but may not have been
     * "initialized", which means grammar validation, etc. The reason that
     * grammar is not initialized here is that a previous attempt to
     * "initialize" may have failed due to grammar errors, so to re-do it here
     * would result in doubled-up error messages. The Initialize Grammar RunParm
     * Command should be used prior to PrintSyntaxDetails if a
     * "load and print syntax" is desired.
     * 
     * @return Grammar object, ready to go.
     */
    public Grammar getGrammar() {

        if (grammar != null)
            return grammar;

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

//PATCH 2008-08-01 moved parms to LogicalSystemBoss
//      if (provableLogicStmtTypeParm == null) {
//          pTyp                  =
//        GrammarConstants.DEFAULT_PROVABLE_LOGIC_STMT_TYP_CODES;
//      }
//      else {
//          pTyp                  = new String[1];
//          pTyp[0]               = provableLogicStmtTypeParm;
//      }
//
//      String[] lTyp;
//      if (logicStmtTypeParm == null) {
//          lTyp                  =
//        GrammarConstants.DEFAULT_LOGIC_STMT_TYP_CODES;
//      }
//      else {
//          lTyp                  = new String[1];
//          lTyp[0]               = logicStmtTypeParm;
//      }

        final String[] pTyp = new String[1];
        pTyp[0] = logicalSystem.getProvableLogicStmtTypeParm();
        final String[] lTyp = new String[1];
        lTyp[0] = logicalSystem.getLogicStmtTypeParm();

//END-PATCH 2008-08-01

        boolean gComplete;
        if (grammarAmbiguityParm == null)
            gComplete = GrammarConstants.DEFAULT_COMPLETE_GRAMMAR_AMBIG_EDITS;
        else
            gComplete = grammarAmbiguityParm.booleanValue();

        boolean sComplete;
        if (statementAmbiguityParm == null)
            sComplete = GrammarConstants.DEFAULT_COMPLETE_STATEMENT_AMBIG_EDITS;
        else
            sComplete = statementAmbiguityParm.booleanValue();

        grammar = new Grammar(pTyp, lTyp, gComplete, sComplete);

        return grammar;
    }

    /**
     * Executes the InitializeGrammar command, prints any messages, etc.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     * @throws IOException if an error occurred
     * @throws VerifyException if an error occurred
     */
    public void doInitializeGrammar(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {
        initializeGrammar();
        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    /**
     * Executes the Parse command, prints any messages, etc.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     * @throws IOException if an error occurred
     * @throws VerifyException if an error occurred
     */
    public void doParse(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, IOException, VerifyException
    {

        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_PARSE.name(), 1);

        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Messages messages = batchFramework.outputBoss.getMessages();

        final Grammar grammar = getGrammar();

        if (!grammarInitialized) {
            grammar.setGrammarInitializedFalse();
            initializeGrammar();
            if (!grammarInitialized) {
                batchFramework.outputBoss.printAndClearMessages();
                return;
            }
        }

        final String optionValue = runParm.values[0].trim();
        if (optionValue.compareTo(UtilConstants.RUNPARM_OPTION_VALUE_ALL) == 0)
        {
            grammar.parseAllFormulas(messages, logicalSystem.getSymTbl(),
                logicalSystem.getStmtTbl());
            if (messages.getErrorMessageCnt() == 0)
                allStatementsParsedSuccessfully = true;
            else
                allStatementsParsedSuccessfully = false;
        }
        else {
            final Stmt stmt = editRunParmValueStmt(optionValue,
                UtilConstants.RUNPARM_PARSE.name(), logicalSystem);

//          Stmt[] exprRPN    =
            final ParseTree parseTree = grammar.parseOneStmt(messages,
                logicalSystem.getSymTbl(), logicalSystem.getStmtTbl(), stmt);
//          if (exprRPN != null) {
            if (parseTree != null) {
                final RPNStep[] exprRPN = parseTree.convertToRPN();
                final StringBuilder sb = new StringBuilder();
                sb.append(UtilConstants.ERRMSG_PARSE_RPN_1);
                sb.append(stmt.getLabel());
                sb.append(UtilConstants.ERRMSG_PARSE_RPN_2);
                for (final RPNStep element : exprRPN) {
                    sb.append(element);
                    sb.append(" ");
                }
                messages.accumInfoMessage(sb.toString());
            }
            if (messages.getErrorMessageCnt() != 0)
                allStatementsParsedSuccessfully = false;
        }

        logicalSystem.setSyntaxVerifier(grammar);

        batchFramework.outputBoss.printAndClearMessages();
    }

    /**
     * An initializeGrammar subroutine.
     */
    protected void initializeGrammar() {

        allStatementsParsedSuccessfully = false;
        final LogicalSystem logicalSystem = batchFramework.logicalSystemBoss
            .getLogicalSystem();

        final Messages messages = batchFramework.outputBoss.getMessages();

        final Grammar grammar = getGrammar();
        grammarInitialized = grammar.initializeGrammar(messages,
            logicalSystem.getSymTbl(), logicalSystem.getStmtTbl());
    }

//PATCH 2008-08-01 move to LogicalSystemBoss
//  /**
///  * Validate Provable Logic Statement Type Runparm.
//   *
//   * @param runParm RunParmFile line.
//   */
//  protected void editProvableLogicStmtType(
//                     RunParmArrayEntry runParm)
//          throws IllegalArgumentException {
//      editRunParmValuesLength(
//               runParm,
//               UtilConstants.RUNPARM_PROVABLE_LOGIC_STMT_TYPE,
//               1);
//      provableLogicStmtTypeParm
//                            = runParm.values[0].trim();
//
//  }
//
//  /**
//   * Validate Logic Statement Type Runparm.
//   *
//   * @param runParm RunParmFile line.
//   */
//  protected void editLogicStmtType(
//                     RunParmArrayEntry runParm)
//          throws IllegalArgumentException {
//      editRunParmValuesLength(
//               runParm,
//               UtilConstants.RUNPARM_LOGIC_STMT_TYPE,
//               1);
//      logicStmtTypeParm     = runParm.values[0].trim();
//
//  }
//END-PATCH 2008-08-01 move to LogicalSystemBoss

    /**
     * Validate Grammar Ambiguity Edits Runparm.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editGrammarAmbiguityEdits(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_GRAMMAR_AMBIGUITY_EDITS.name(), 1);
        grammarAmbiguityParm = editAmbiguityParm(runParm.values[0].trim(),
            UtilConstants.RUNPARM_GRAMMAR_AMBIGUITY_EDITS.name());
    }

    /**
     * Validate Statement Ambiguity Edits Runparm.
     * 
     * @param runParm RunParmFile line.
     * @throws IllegalArgumentException if an error occurred
     */
    protected void editStatementAmbiguityEdits(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_STATEMENT_AMBIGUITY_EDITS.name(), 1);
        statementAmbiguityParm = editAmbiguityParm(runParm.values[0].trim(),
            UtilConstants.RUNPARM_STATEMENT_AMBIGUITY_EDITS.name());
    }

    /**
     * Validate an Ambiguity Edits Runparm.
     * 
     * @param ambiguityParm String, "basic" or "complete".
     * @param valueCaption Caption for RunParm value.
     * @return true signifies Complete, false signifies Basic.
     * @throws IllegalArgumentException if an error occurred
     */
    protected Boolean editAmbiguityParm(final String ambiguityParm,
        final String valueCaption) throws IllegalArgumentException
    {

        if (ambiguityParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_VALUE_BASIC) == 0)
            return new Boolean(false);
        if (ambiguityParm
            .compareToIgnoreCase(UtilConstants.RUNPARM_OPTION_VALUE_COMPLETE) == 0)
            return new Boolean(true);

        throw new IllegalArgumentException(
            UtilConstants.ERRMSG_AMBIG_EDIT_LEVEL_INVALID_1 + valueCaption
                + UtilConstants.ERRMSG_AMBIG_EDIT_LEVEL_INVALID_2
                + UtilConstants.RUNPARM_OPTION_VALUE_BASIC
                + UtilConstants.ERRMSG_AMBIG_EDIT_LEVEL_INVALID_3
                + UtilConstants.RUNPARM_OPTION_VALUE_COMPLETE
                + UtilConstants.ERRMSG_AMBIG_EDIT_LEVEL_INVALID_4);
    }
}
