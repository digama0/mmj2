//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SyntaxVerifier.java  0.03 02/01/2006
 *
 * Dec-14-2005: change parse to return ParseTree
 *              instead of RPN -- will store
 *              tree instead of RPN to optimize
 *              ProofAsst processing.
 */

package mmj.lang;

import java.util.Map;

/**
 * Interface to Syntax Verification (aka "Grammar", "grammatical parsing" and
 * "syntactic analysis").
 * <p>
 * Refer to mmj.verify.GrammarConstants.java for information about parse
 * parameters and error messages.
 */
public interface SyntaxVerifier {

    /**
     * Parse a single formula.
     * <p>
     * Note: access to symTbl and stmtTbl is required in case the grammar needs
     * to be re-initialized.
     * <p>
     * Note: <b>highestSeq</b> is similar to the restriction in proof
     * verification: a proof can only refer to previous statements. However,
     * setting this to java.lang.Integer.MAX_VALUE says, parse this formula with
     * the entire grammar -- which ought to result in the same parse, unless the
     * grammar is ambiguous (new Grammar Rules should be "disjoint" from
     * previous ones.)
     * 
     * @param messages Messages object for error/info messages.
     * @param symTbl Symbol Table (Map).
     * @param stmtTbl Statement Table (Map).
     * @param formula Formula to parse.
     * @param varHypArray VarHyp's for the Formula's Var's.
     * @param highestSeq Max MObj.seq that can be referenced.
     * @param defaultStmtForRPN Default Stmt for output RPN/message.
     * @return Array of Stmt containing RPN.
     */
    ParseTree parseFormula(Messages messages,
//  Stmt[] parseFormula(Messages    messages,
        Map<String, Sym> symTbl, Map<String, Stmt> stmtTbl, Formula formula,
        VarHyp[] varHypArray, int highestSeq, Stmt defaultStmtForRPN);

    /**
     * Parse a single Statement.
     * <p>
     * If used with VarHyp or SyntaxAxiom, simply returns stmt.getExprRPN().
     * <p>
     * Note: access to symTbl and stmtTbl is required in case the grammar needs
     * to be re-initialized.
     * 
     * @param messages Messages object for error/info messages.
     * @param symTbl Symbol Table (Map).
     * @param stmtTbl Statement Table (Map).
     * @param stmt Stmt in stmtTbl to parse.
     * @return Array of Stmt containing RPN.
     */
    ParseTree parseOneStmt(Messages messages,
//  Stmt[] parseOneStmt(Messages messages,
        Map<String, Sym> symTbl, Map<String, Stmt> stmtTbl, Stmt stmt);

    /**
     * Parse all Statement Formulas and update stmtTbl with results.
     * 
     * @param messages Messages object for error/info messages.
     * @param symTbl Symbol Table (Map).
     * @param stmtTbl Statement Table (Map).
     */
    void parseAllFormulas(Messages messages, Map<String, Sym> symTbl,
        Map<String, Stmt> stmtTbl);

    /**
     * Initializes the grammar.
     * <p>
     * Normally this is handled automatically. It initializes "the grammar" but
     * does not parse every Stmt in stmtTbl.
     * <p>
     * The intended use of this function would be to initialize the grammar
     * without parsing every statement (parsing every statement in set.mm
     * requires 8ish seconds!)
     * 
     * @param messages Messages object for error/info messages.
     * @param symTbl Symbol Table (Map).
     * @param stmtTbl Statement Table (Map).
     * @return true if grammar initializes successfully, else false.
     */
    boolean initializeGrammar(Messages messages, Map<String, Sym> symTbl,
        Map<String, Stmt> stmtTbl);
}
