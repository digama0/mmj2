//********************************************************************/
//* Copyright (C) 2005, 2006                                         */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SrcStmt.java  0.04 11/01/2006
 *
 * Version 0.03:
 *     --> Added proofBlockList for compressed proofs.
 *
 * 11/01/2006 - Version 0.04 -
 *     --> comment SrcStmt does not include "$(" and "$)"
 *         start/end statement tokens.
 */

package mmj.mmio;

import java.util.List;

/**
 * Simple data structure holding "work" components of a MetaMath source
 * statement.
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class SrcStmt {

    /**
     * Every SrcStmt has a sequence number, even a comment.
     * <p>
     * Note: this {@code seq} is not necessarily the same value stored in
     * {@code LogicalSystem} or whatever object implements {@code SystemLoader}.
     * Both are sequential (ha), but LogicalSystem may use a multiplier or
     * transform the sequence number in some other way.
     */
    public int seq = 0;

    /**
     * VarHyp, LogHyp, Theorem and Axiom have labels.
     */
    public String label = null;

    /**
     * The column that a VarHyp, LogHyp, Theorem or Axiom starts at.
     */
    public int column = 0;

    /**
     * Every SrcStmt has a keyword, even a comment (char 1 = '$', always.)
     */
    public String keyword = null;

    /**
     * VarHyp, LogHyp, Theorem and Axiom have typ, which internally corresponds
     * to the first symbol of a Formula externally, is the first symbol
     * following the Metamath statement's keyword ($a, $p, etc.)
     */
    public String typ = null;

    /**
     * symlist is the 2nd through nth symbols in the Metamath source statement.
     * <p>
     * DjVar, Var, Cnst, VarHyp, LogHyp, Theorem and Axiom have symList:
     *
     * <pre>
     *     DjVar:                   at least 2 sym Strings
     *     Var & Cnst:              at least 1
     *     VarHyp:                  exactly 1, guaranteed :)
     *     Axiom, LogHyp & Theorem: zero or more
     * </pre>
     */
    public List<String> symList = null;

    /**
     * Only Theorem statements ($p) have proofList.
     * <p>
     * proofList is guaranteed to have at least one proofStep string (which will
     * be label of a statement... or "?").
     * <p>
     * Note: if proofBlockList not null then proofList contains the labels
     * inside the parenthesized portion of a compressed proof (but not the
     * parentheses.)
     */
    public List<String> proofList = null;

    /**
     * Theorem compressed proof block list.
     * <p>
     * Null value signifies proof is not compressed. Otherwise, contains 1 or
     * more Strings containing Metamath compressed proof symbols (see
     * Metamath.pdf Appendix B).
     */
    public BlockList proofBlockList = null;

    /**
     * Comment statement --
     * <p>
     * This is the entire comment excluding the start/end comment keywords. All
     * other tokens are present, including the intervening whitespace newlines.
     */
    public String comment = null;

    /**
     * Include File Name from "$[" statement.
     * <p>
     * Does not include the $[, $] tokens or the whitespace chunks, just the
     * file name.
     */
    public String includeFileName = null;

    /**
     * Construct using sequence number and MetaMath keyword.
     *
     * @param seqNbr the sequence number of the source statement
     * @param keyword the MetaMath source keyword token
     */
    public SrcStmt(final int seqNbr, final String keyword) {
        seq = seqNbr;
        this.keyword = keyword;
    }
}
