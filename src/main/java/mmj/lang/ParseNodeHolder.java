//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ParseNodeHolder.java  0.02 08/23/2005
 */

package mmj.lang;

/**
 * ParseNodeHolder is either really dumb or just kinda dumb.
 * <p>
 * The idea is that an Expression to be parsed can be rewritten as an array of
 * ParseNodeHolders, with the original Sym's stored in ParseNodeHolder.mObj.
 * Then The Var's can be replaced with their VarHyp's, with mObj set to the
 * VarHyp and also stored in parseNode. Subsequently the array of
 * ParseNodeHolders can be rewritten, and rewritten until, at last, every mObj
 * containing a Cnst has been parsed -- and there is only one item in the array
 * whose parseNode is the root of a parse tree.
 * <p>
 * Anyway, ParseNodeHolder is very popular in mmj.verify. Grammar. Many uses!
 * Haha.
 * <p>
 * In EarleyParser the need arose to maintain a linked list of ParseNodeHolders
 * instead of an array, so, what the heck, twinFwd and twinBwd were added (I was
 * so happy, why not!) The problem was that in an ambiguous grammar, many valid
 * parse trees can exist, and you don't know where you will first notice them;
 * they might be found at a bottom sub-tree and then propagate upward. So,
 * infinite parse trees are feasible, thanks to adding linked lists of'
 * ParseNodeHolder. <:-)
 */
public class ParseNodeHolder {
    /**
     * mObj contains either a Cnst, a Var or a Stmt.
     */
    public MObj mObj;

    /**
     * ParseNode associated with mObj, may be null.
     */
    public ParseNode parseNode;

    /**
     * Forward reference in chain of ParseNodeHolders. Used only in rare, local
     * circumstances, such as EarleyParser tree building. When there is only one
     * item in the chain set fwd = bwd = this;
     */
    public ParseNodeHolder fwd;

    /**
     * Backward reference in chain of ParseNodeHolders. Used only in rare, local
     * circumstances, such as EarleyParser tree building. When there is only one
     * item in the chain set fwd = bwd = this;
     */
    public ParseNodeHolder bwd;

    /**
     * Default constructor.
     */
    public ParseNodeHolder() {}

    /**
     * Constructor -- input is a ParseNode, so derive MObj from the input
     * ParseNode.stmt.
     *
     * @param parseNode the input ParseNode
     */
    public ParseNodeHolder(final ParseNode parseNode) {
        mObj = parseNode.stmt;
        this.parseNode = parseNode;
    }

    /**
     * Constructor -- input is Cnst, so no ParseNode available, just load the
     * MObj for use in ParseTree generation...
     *
     * @param cnst the input Cnst
     */
    public ParseNodeHolder(final Cnst cnst) {
        mObj = cnst;
    }

    /**
     * Constructor -- input is VarHyp so create default VarHyp ParseNode.
     *
     * @param varHyp the input VarHyp
     */
    public ParseNodeHolder(final VarHyp varHyp) {
        mObj = varHyp;
        parseNode = new ParseNode(varHyp);
    }

    /**
     * A tedious conversion.
     * <p>
     * Converts to "ruleFormatExpr" -- each item in the Cnst array is a
     * grammaticl Type Code or a Cnst.
     *
     * @param parseNodeHolderExpr array of ParseNodeHolder.
     * @return ruleFormatExpr version of ParseNodeHolder array.
     */
    public static Cnst[] buildRuleFormatExpr(
        final ParseNodeHolder[] parseNodeHolderExpr)
    {
        final Cnst[] ruleFormatExpr = new Cnst[parseNodeHolderExpr.length];
        for (int i = 0; i < ruleFormatExpr.length; i++)
            ruleFormatExpr[i] = parseNodeHolderExpr[i].getCnstOrTyp();
        return ruleFormatExpr;
    }

    /**
     * return Cnst or Type Code of ParseNode.stmt.
     * <p>
     * If mObj is a Cnst, return it, otherwise return the Type Code of
     * parseNode.
     *
     * @return cnst Cnst or Type Code.
     */
    public Cnst getCnstOrTyp() {
        if (mObj instanceof Cnst)
            return (Cnst)mObj;
        return parseNode.stmt.getTyp();
    }

    /**
     * return Cnst or Label of ParseNode.stmt.
     *
     * @return String Cnst.id if mObj is Cnst, else label of
     *         parseNodeHolder.stmt.
     */
    public String getCnstIdOrLabel() {
        if (mObj instanceof Cnst)
            return ((Cnst)mObj).getId();
        return parseNode.stmt.getLabel();
    }

    /**
     * Copy "twin chain" to string for diagnostic use.
     *
     * @return String representing twin chain.
     */
    public String dumpTwinChainToString() {
        final StringBuilder s = new StringBuilder();
        ParseNodeHolder next = this;
        do {
            s.append(next.getCnstIdOrLabel());
            s.append(" ");
            next = next.fwd;
        } while (next != null && next != this);
        return s.toString();
    }

    /**
     * Init twin chain, first node points to itself.
     */
    public void initTwinChain() {
        bwd = fwd = this;
    }

    /**
     * Add a node to a ParseNodeHolder's twin chain.
     * <p>
     * Inserts at the front of the list.
     *
     * @param x the node to add
     */
    public void addToTwinChain(final ParseNodeHolder x) {
        if (fwd == null)
            initTwinChain();
        x.fwd = fwd;
        x.bwd = this;
        x.fwd.bwd = fwd = x;
    }

    @Override
    public String toString() {
        return mObj.toString();
    }

}
