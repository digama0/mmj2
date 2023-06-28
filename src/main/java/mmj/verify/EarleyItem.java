//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * EarleyItem.java  0.02 08/26/2005
 */

package mmj.verify;

import mmj.lang.Cnst;

/**
 * EarleyItem is a work item generated as part of the EarleyParse algorithm
 * representing partial or complete satisfaction of a grammar rule by a
 * subsequence of an expression being parsed.
 */
public class EarleyItem {

    /**
     * NotationRule reference used to avoid copying expr symbols over and over
     * again.
     * <p>
     * EarleyItem.rule.ruleNbr is the high order sub-key of an Earley Item.
     * Together, rule.ruleNbr and atIndex make a unique key, which is essential
     * for maintaining *set* Earley Itemsets.
     */
    NotationRule rule;

    /**
     * Index to next Cnst (Sym) in expr, equal to the Earley loop counter, "i".
     * <p>
     * 1 is first, not 0, which is left empty in expr for simplicity.
     * <p>
     * EarleyItem.atIndex is the low-order sub-key of an Earley Item. Together,
     * rule.ruleNbr and atIndex make a unique key, which is essential for
     * maintaining Earley Itemsets.
     */
    int atIndex;

    /**
     * Index of the Earley "dot" (or "gap") used instead of physically moving
     * the dot around in the EarleyItem.
     * <p>
     * A value of 1 means that the dot is *before* the first sym in the rule's
     * expr, while a value equal to expr.length means that the EarleyItem is
     * completed.
     */
    int dotIndex;

    /**
     * If item completed, equals "null", else equal to ruleExpr[dotIndex].
     * <p>
     * Used to avoid repetitious and annoying "hunting trips" into the rule's
     * forest.
     */
    Cnst afterDot;

    /*
     * Compare for equality with another EarleyItem.
     * <p>
     * Equal if and only if Rule and atIndexSym are
     * identical.
     *
     * @param obj EarleyItem to compare to this item.
     *
     * @return returns true if equal, otherwise false.
     */
    @Override
    public boolean equals(final Object obj) {
        return this == obj || obj instanceof EarleyItem
            && rule == ((EarleyItem)obj).rule
            && atIndex == ((EarleyItem)obj).atIndex;
    }

    /**
     * Converts EarleyItem to a string (solely for diagnostic purposes).
     */
    @Override
    public String toString() {
        final StringBuilder s = new StringBuilder();
        s.append(GrammarConstants.ERRMSG_AT_CAPTION);
        s.append(atIndex);
        s.append(GrammarConstants.ERRMSG_DOT_CAPTION);
        s.append(dotIndex);
        s.append(GrammarConstants.ERRMSG_AFTER_DOT_CAPTION);
        if (afterDot == null)
            s.append(" ");
        else
            s.append(afterDot);
        s.append(" ");
        s.append(rule.ruleNbr);
        s.append(GrammarConstants.ERRMSG_COLON_CAPTION);
        s.append(rule.getBaseSyntaxAxiom().getTyp());
        s.append(GrammarConstants.ERRMSG_COLON_CAPTION);
        s.append(rule.getBaseSyntaxAxiom().getLabel());
        s.append(" ");
        s.append(GrammarConstants.ERRMSG_RULE_EXPR_CAPTION);
        s.append(rule.getRuleFormatExprAsString());
        return s.toString();
    }
}
