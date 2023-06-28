//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SystemLoader.java  0.06 11/01/2011
 *
 *     --> 10-Dec-2005: add "prematureEOF" param to finalizeEOF()
 *         so that a user-requested termination of the load at
 *         a label or statement number can exit gracefully (need
 *         to exit nested scopes).
 *
 * Version 0.04: 04/01/2006 --
 *     --> added addTheorem() variant with compressed proof parms
 *
 * Version 0.05: 08/01/2008 --
 *     --> Addes addNewChapter(), addNewSection() and
 *         isBookManagerEnabled() for BookManager.java.
 *
 * Version 0.06: Nov-01-2011 --
 *     --> Added cacheTypesettingCommentForGMFF()
 */

package mmj.lang;

import java.util.List;

import mmj.mmio.BlockList;
import mmj.pa.MMJException;

/**
 * Interface for loading Metamath statements into a Logic System.
 * <p>
 * Interface, initially for mmj.lang.LogicalSystem and passed to
 * mmj.mmio.Systemizer. Allows a different Logical System to be substituted. A
 * different use is possible, such as dumping the parsed .mm file statements
 * somewhere else. Systemizer has no need to know anything about LogicalSystem
 * except where to send the data.
 */
public interface SystemLoader {

    /**
     * Add Cnst to Logical System.
     * 
     * @param id Constant's symbol string to be added to the Logical System.
     * @return Cnst added to LogicalSystem.
     * @throws MMJException if duplicate symbol, etc.
     */
    Cnst addCnst(String id) throws MMJException;

    /**
     * Add Var to Logical System.
     * 
     * @param id Var's symbol string to be added to the Logical System.
     * @return Var added to LogicalSystem.
     * @throws MMJException if duplicate symbol, etc.
     */
    Var addVar(String id) throws MMJException;

    /**
     * Add VarHyp to Logical System.
     * 
     * @param labelS String label of variable hypothesis
     * @param typS String Metamath constant character (type code)
     * @param varS String Metamath variable character
     * @return VarHyp added to LogicalSystem.
     * @throws MMJException if duplicate symbol, etc. (see
     *             {@code mmj.lang.LangConstants.java})
     */
    VarHyp addVarHyp(String labelS, String typS, String varS)
        throws MMJException;

    /**
     * Add DjVars (Disjoint Variables Restriction) to Logical System.
     * 
     * @param djVar1S disjoint variable symbol string 1
     * @param djVar2S disjoint variable symbol string 2
     * @return DjVars (pair) added to LogicalSystem.within the current scope,
     *         *or* the existing DjVars object in the current scope.
     * @throws MMJException if duplicate vars, etc. (see
     *             {@code mmj.lang.LangConstants.java})
     */
    DjVars addDjVars(String djVar1S, String djVar2S) throws MMJException;

    /**
     * Add LogHyp (Logical Hypothesis) to Logical System.
     * 
     * @param labelS logical hypothesis label string
     * @param typS logical hypothesis type code (symbol) string
     * @param symList list containing expression symbol strings (zero or more
     *            symbols).
     * @return LogHyp newly constructed LogHyp added to LogicalSystem.
     * @throws MMJException if duplicate label, undefined vars, etc.
     */
    LogHyp addLogHyp(String labelS, String typS, List<String> symList)
        throws MMJException;

    /**
     * Add Axiom to Logical System.
     * 
     * @param labelS axiom label string
     * @param typS axiom type code (symbol) string
     * @param symList list containing axiom expression symbol strings (zero or
     *            more symbols).
     * @return Axiom newly constructed Axiom added to LogicalSystem.
     * @throws MMJException if duplicate label, undefined vars, etc.
     */
    Axiom addAxiom(String labelS, String typS, List<String> symList)
        throws MMJException;

    /**
     * Add Theorem to Logical System.
     * 
     * @param labelS axiom label string
     * @param column the column at which the "$p" line starts
     * @param typS axiom type code (symbol) string
     * @param symList list containing axiom expression symbol strings (zero or
     *            more symbols).
     * @param proofList list containing proof step symbol strings (1 or more
     *            symbols -- which may be "?" if a step is unknown).
     * @param messages for error reporting
     * @return Theorem newly constructed Theorem added to LogicalSystem.
     * @throws MMJException if duplicate label, undefined vars, etc.
     */
    Theorem addTheorem(String labelS, int column, String typS,
        List<String> symList, List<String> proofList, Messages messages)
        throws MMJException;

    /**
     * Add Theorem to Logical System.
     * <p>
     * This variant is invoked when the input contains a compressed proof.
     * 
     * @param labelS axiom label string
     * @param column the column at which the "$p" line starts
     * @param typS axiom type code (symbol) string
     * @param symList list containing axiom expression symbol strings (zero or
     *            more symbols).
     * @param proofList list containing the contents of the parenthesized
     *            portion of a compressed proof (does not include the
     *            parentheses)
     * @param proofBlockList list containing one or more blocks of compressed
     *            proof symbols.
     * @param messages for error reporting
     * @return Theorem newly constructed Theorem added to LogicalSystem.
     * @throws MMJException if duplicate label, undefined vars, etc.
     */
    Theorem addTheorem(String labelS, int column, String typS,
        List<String> symList, List<String> proofList, BlockList proofBlockList,
        Messages messages) throws MMJException;

    /**
     * Begin a new (nested) scope level for the Logical System.
     * 
     * @see mmj.lang.ScopeDef
     */
    void beginScope();

    /**
     * Ends a (nested) scope level for the Logical System.
     * 
     * @see mmj.lang.ScopeDef
     * @throws MMJException if scope is already at the global scope level.
     */
    void endScope() throws MMJException;

    /**
     * EOF processing for Logical System after file loaded.
     * 
     * @param messages Messages object to error reporting.
     * @param prematureEOF signals LoadLimit requested by user has been reached,
     *            so stop loading even if in the middle of a scope level.
     * @throws MMJException if scope is NOT at the global scope level UNLESS
     *             premature EOF signalled. (see
     *             {@code mmj.lang.LangConstants.java})
     */
    void finalizeEOF(Messages messages, boolean prematureEOF)
        throws MMJException;

    /**
     * Is BookManager enabled?
     * <p>
     * If BookManager is enabled then Chapters and Sections will be stored.
     * 
     * @return true if BookManager is enabled, else false.
     */
    boolean isBookManagerEnabled();

    /**
     * Add new Chapter.
     * <p>
     * See mmj.lang.BookManager.java for more info.
     * 
     * @param chapterTitle Title of chapter or blank or empty String.
     */
    void addNewChapter(String chapterTitle);

    /**
     * Add new Section.
     * <p>
     * See mmj.lang.BookManager.java for more info.
     * 
     * @param sectionTitle Title of section or blank or empty String.
     */
    void addNewSection(String sectionTitle);

    /**
     * Cache Metamath comment containing typesetting definitions for use by
     * GMFF.
     * <p>
     * Note: comment is from mmj.mmio.SrcStmt so it does not contain the "$("
     * and "$)" start/end comment id tokens which are standard to Metamath
     * comments.
     * <p>
     * Note: per agreement with Norm, the "$t" token identifying typesetting
     * definitions in a comment is the first non-whitespace token after the "$("
     * in the comment.
     * 
     * @param comment Typesetting definition comment ("$t) from Metamath file
     *            minus the "$(" and "$)" tokens.
     */
    void cacheTypesettingCommentForGMFF(String comment);

}
