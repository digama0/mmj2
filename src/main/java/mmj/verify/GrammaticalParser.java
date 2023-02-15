//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * GrammaticalParser.java 0.02 06/26/2005
 */

package mmj.verify;

import mmj.lang.*;
import mmj.pa.SessionStore;

/**
 * GrammaticalParser is an interface established so that the Grammar class can
 * use different parsers (even though the choice of parser is presently
 * hard-coded into Grammar).
 */
public interface GrammaticalParser {

    /**
     * Parse (syntactical analysis) of a single Expression.
     *
     * @param parseTreeArrayIn Array of ParseTree to be filled in with completed
     *            ParseTree objects by the parser.
     * @param formulaTypIn Type Code that the Expression must parse *to*, AKA
     *            "Start Type".
     * @param parseNodeHolderExprIn Expression to be parsed which has already
     *            had its variables "pre-parsed", replacing them with their
     *            corresponding VarHyp's.
     * @param highestSeqIn greatest MObj.seq number that can be used to parse
     *            the expression (a Metamath Stmt should not refer to later
     *            statements, but ambiguity checking may involve attempting a
     *            parse using all grammar rules to see if the later rules
     *            introduce ambiguity.)
     * @return number of ParseTree's returned in parseTreeArrayIn.
     * @throws VerifyException if an error occurred
     */
    public int parseExpr(ParseTree[] parseTreeArrayIn, Cnst formulaTypIn,
        ParseNodeHolder[] parseNodeHolderExprIn, int highestSeqIn)
            throws VerifyException;

    /**
     * (Optionally) add some settings to the global storage to improve load
     * times.
     *
     * @param store The setting storage
     */
    default void addSettings(final SessionStore store) {}
}
