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

import static mmj.util.UtilConstants.*;

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.verify.*;

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

    protected boolean grammarAmbiguityParm;
    protected boolean statementAmbiguityParm;

    protected Class<? extends GrammaticalParser> parserPrototype;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public GrammarBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        grammarAmbiguityParm = GrammarConstants.DEFAULT_COMPLETE_GRAMMAR_AMBIG_EDITS;
        statementAmbiguityParm = GrammarConstants.DEFAULT_COMPLETE_STATEMENT_AMBIG_EDITS;

        putCommand(RUNPARM_CLEAR, this::clear);
        putCommand(RUNPARM_LOAD_FILE, this::clear);

//PATCH 2008-08-01: MOVE TO LogicalSystemBoss
//        putCommand(RUNPARM_PROVABLE_LOGIC_STMT_TYPE,
//            this::editProvableLogicStmtType);
//        putCommand(RUNPARM_LOGIC_STMT_TYPE, this::editLogicStmtType);
//END-PATCH 2008-08-01

        putCommand(RUNPARM_GRAMMAR_AMBIGUITY_EDITS,
            this::editGrammarAmbiguityEdits);

        putCommand(RUNPARM_STATEMENT_AMBIGUITY_EDITS,
            this::editStatementAmbiguityEdits);

        putCommand(RUNPARM_SET_PARSER, this::editParser);

        putCommand(RUNPARM_INITIALIZE_GRAMMAR, this::doInitializeGrammar);

        putCommand(RUNPARM_PARSE, this::doParse);
    }

    private boolean clear() {
        grammar = null;
        grammarInitialized = false;
        allStatementsParsedSuccessfully = false;
//PATCH 2008-08-01 MOVED TO LogicalSystemBoss
//          provableLogicStmtTypeParm
//                            = null;
//          logicStmtTypeParm = null;
//END-PATCH 2008-08-01 MOVED TO LogicalSystemBoss
        grammarAmbiguityParm = GrammarConstants.DEFAULT_COMPLETE_GRAMMAR_AMBIG_EDITS;
        statementAmbiguityParm = GrammarConstants.DEFAULT_COMPLETE_STATEMENT_AMBIG_EDITS;
        parserPrototype = GrammarConstants.DEFAULT_PARSER_PROTOTYPE;
        return false; // not "consumed"
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
//END-PATCH 2008-08-01

        final String[] pTyp = new String[]{
                logicalSystem.getProvableLogicStmtTypeParm()};
        final String[] lTyp = new String[]{
                logicalSystem.getLogicStmtTypeParm()};

        try {
            grammar = new Grammar(pTyp, lTyp, grammarAmbiguityParm,
                statementAmbiguityParm, parserPrototype);
        } catch (final VerifyException e) {
            throw error(e);
        }
        grammar.setStore(batchFramework.storeBoss.getStore());

        return grammar;
    }

    /**
     * Set the grammar parser by class name.
     */
    @SuppressWarnings("unchecked")
    public void editParser() {
        try {
            parserPrototype = (Class<? extends GrammaticalParser>)Class
                .forName(get(1));
        } catch (final ClassNotFoundException e) {
            throw error(e, ERRMSG_RUNPARM_PARSER_BAD_CLASS,
                runParm.values[0].trim());
        }
    }

    /**
     * Executes the InitializeGrammar command, prints any messages, etc.
     */
    public void doInitializeGrammar() {
        initializeGrammar();
        batchFramework.outputBoss.printAndClearMessages();
        return;
    }

    /**
     * Executes the Parse command, prints any messages, etc.
     */
    public void doParse() {
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

        if (get(1).equals(RUNPARM_OPTION_VALUE_ALL)) {
            grammar.parseAllFormulas(messages, logicalSystem.getSymTbl(),
                logicalSystem.getStmtTbl());
            allStatementsParsedSuccessfully = messages
                .getErrorMessageCnt() == 0;
        }
        else {
            final Stmt stmt = getStmt(1, logicalSystem);

            final ParseTree parseTree = grammar.parseOneStmt(messages,
                logicalSystem.getSymTbl(), logicalSystem.getStmtTbl(), stmt);
            if (parseTree != null) {
                final RPNStep[] exprRPN = parseTree.convertToRPN();
                final StringBuilder sb = new StringBuilder();
                for (final RPNStep element : exprRPN)
                    sb.append(element).append(" ");
                messages.accumMessage(ERRMSG_PARSE_RPN, stmt, sb);
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
//               RUNPARM_PROVABLE_LOGIC_STMT_TYPE,
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
//               RUNPARM_LOGIC_STMT_TYPE,
//               1);
//      logicStmtTypeParm     = runParm.values[0].trim();
//
//  }
//END-PATCH 2008-08-01 move to LogicalSystemBoss

    /**
     * Validate Grammar Ambiguity Edits Runparm.
     */
    protected void editGrammarAmbiguityEdits() {
        grammarAmbiguityParm = getBoolean(1,
            GrammarConstants.DEFAULT_COMPLETE_GRAMMAR_AMBIG_EDITS,
            RUNPARM_OPTION_VALUE_COMPLETE, RUNPARM_OPTION_VALUE_BASIC);
    }

    /**
     * Validate Statement Ambiguity Edits Runparm.
     */
    protected void editStatementAmbiguityEdits() {
        statementAmbiguityParm = getBoolean(1,
            GrammarConstants.DEFAULT_COMPLETE_STATEMENT_AMBIG_EDITS,
            RUNPARM_OPTION_VALUE_COMPLETE, RUNPARM_OPTION_VALUE_BASIC);
    }
}
