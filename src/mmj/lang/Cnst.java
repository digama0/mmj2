//********************************************************************/
//* Copyright (C) 2005  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * 0.03 11/01/2006
 *
 * Version 0.03:
 *
 * Oct-12-2006: - added StmtTbl to constructor and modified to
 *                conform to Metamath.pdf spec change of 6-24-2006
 *                prohibiting Stmt label and Sym id namespace
 *                collisions.
 *
 * Version 0.04 -- 08/01/2007
 *              - added workVarTypIndex for use in WorkVarManager.
 */

package mmj.lang;

import java.util.*;

import mmj.verify.*;

/**
 * Cnst holds a declared Metamath constant symbol.
 * <p>
 * The class Cnst contains more than just a symbol sequence. Cnst is a very
 * important class in mmj as it is presently used to store information about
 * Type Codes. Although the idea of creating a separate class to store Type
 * Codes has been considered, the convenience and efficiency of hoarding the
 * attributes here has proven irresistable.
 * <p>
 * The downside of storing grammatical attributes in Cnst is that changing
 * mmj.verify.Grammar may mean changes in mmj.lang.Cnst. Also, these attributes
 * are not loaded until the grammar is initialized.
 * <p>
 * The upside, of course, is that once a symbol is in hand, all information
 * about that symbol is at hand, without an extra table look-up.
 *
 * @see mmj.lang.SyntaxVerifier
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class Cnst extends Sym {

    /**
     * workVarTypIndex is an array index into tables ordered by Type Code inside
     * WorkVarManager.
     * <p>
     */
    /* friendly */int workVarTypIndex;

    /**
     * IsVarTyp says that this Cnst is used in a $f VarHyp statement as the Type
     * Code of a variable (i.e. "wff" or "set").
     */
    private boolean isVarTyp;

    /**
     * isProvableLogicStmtTyp says that this Cnst is defined by the user,
     * perhaps by default, as a Type Code used on Provable Logic Statments (and
     * by extension, Logical Axioms.)
     *
     * @see mmj.verify.Grammar
     */
    private boolean isProvableLogicStmtTyp;

    /**
     * isLogicStmtTyp says that this Cnst is defined by the user, perhaps by
     * default, as a Type Code used on Logic Statments (i.e. "wff"s)
     *
     * @see mmj.verify.Grammar
     */
    private boolean isLogicStmtTyp;

    /**
     * isSyntaxAxiomTyp says that this Cnst is used as the Type Code of at least
     * one Syntax Axiom.
     *
     * @see mmj.verify.Grammar
     */
    private boolean isSyntaxAxiomTyp;

    /**
     * isGrammaticalTyp says that this Cnst is a Type Code used on at least one
     * VarHyp or Syntax Axiom, or isLogicStmtTyp() == true or
     * isProvableLogicStmtTyp() == true.
     *
     * @see mmj.verify.Grammar
     */
    private boolean isGrammaticalTyp;

    /**
     * nbrOccInCnstSyntaxAxioms counts how many times the constant symbol
     * appears in the expression portion of Syntax Axioms having no variables
     * (i.e. Named Typed Constants, which have expression length = 1 -- or, the
     * much less popular "Literal" variety, which have expression length > 1.)
     * <p>
     * Note: the term "expression" refers to the portion of a Formula following
     * the Type Code, which is always the first symbol of a Formula.
     */
    private int nbrOccInCnstSyntaxAxioms;

    /**
     * nbrOccInSyntaxAxioms counts how many times a constant appears in the
     * expression portion of Syntax Axioms. If this number equals 1 then the
     * Syntax Axiom in which it appears has special grammatical parsing
     * properties (i.e. the uniqueness is a strong hint to the parser :)
     * <p>
     * If this number is equal to zero that means the constant is defined but is
     * not part of the grammar, and its usage in any other statement is in
     * error. Constants with a value in this field greater than 1 are most
     * likely just punctuation, like "(" and ")"; however, in set.mm the
     * constant "/" is used as punctuation and as a Named Type Constant (see
     * "cdiv", "wsb" and "wsbc".)
     * <p>
     * Note: the term "expression" refers to the portion of a Formula following
     * the Type Code, which is always the first symbol of a Formula.
     */
    private int nbrOccInSyntaxAxioms;

    /**
     * GrammarRule Tree (forest) root node for Grammar rules whose Expression's
     * "ruleFormatExpr" first symbol is this Cnst object. Null if no such tree
     * exists or Grammar is not yet initialized.
     * <p>
     * In other words, for the Cnst "(", gRRoot contains the root node pointing
     * to a forest of GrammarRules whose expression starts with "(". Or, if the
     * expresison begins with a "wff" variable, the forest contains all
     * expressions that begin with a "wff" type variable.
     * <p>
     * By "forest" we mean that not only is there a tree for each Cnst that
     * begins a "ruleFormatExpr", but each sub-expression has its own forest.
     * Thus, there would be a forest of trees whose first two (constant)
     * ruleFormatExpr symbols are "( wff".
     * <p>
     * This whole "grammar rule forest" concept doesn't see much use in
     * mmj.verify.EarleyParser, but was used in mmj.verify.BottomUpParser, and
     * it is still used for detecting duplicate NotationRule expressions.
     *
     * @see mmj.verify.GRForest
     * @see mmj.verify.GRNode
     * @see mmj.verify.GrammarRule
     * @see mmj.verify.NotationRule
     * @see mmj.verify.BottomUpParser
     */
    private GRNode gRRoot;

    /**
     * convFromTypGRArray - Array of Type Conversion GrammarRules, one for each
     * Type Code that can be converted to *this* Type code. Array is null if no
     * Type Conversions exist for the Cnst object (which may or may not be a
     * Type Code.)
     */
    private TypeConversionRule[] convFromTypGRArray;

    /**
     * nullsPermittedGR - set to Nulls Permitted GrammarRule if null values are
     * permitted for variables/expressions with this Type Code, otherwise, if
     * nulls are not permitted, nullsPermittedGR is set to null :0)
     */
    private NullsPermittedRule nullsPermittedGR;

    /**
     * len1CnstNotationRule -- Notation Rule for Syntax Axiom with Formula
     * Expression length of 1 consisting of just a Cnst (example, Cnst "c0" from
     * set.mm would have a reference to c0's NotationRule in the "c0" Cnst,
     * here.)
     */
    private NotationRule len1CnstNotationRule;

    /**
     * earleyRules -- a list of the Grammar Rules with Type Code equal to this
     * Cnst, sorted by GrammarRule.MAX_SEQ_NBR for mmj.verify.earleyParser.
     */
    private List<NotationRule> earleyRules;

    /**
     * earleyFIRST -- a Set of {@code Cnst}s that can possibly begin a
     * "ruleFormatExpr" of this (Cnst) Type.
     * <p>
     * For example, the earleyFIRST set for "wff" contains "(" because there is
     * a grammar rule with Type Code "wff" whose Formula ruleFormatExpr begins
     * with "(".
     * <p>
     * Used by mmj.verify.earleyParser for Prediction Lookahead.
     */
    private Set<Cnst> earleyFIRST;

    /**
     * Construct using sequence number and id string.
     *
     * @param seq MObj.seq number
     * @param id Sym id string
     * @throws LangException if id string is empty
     */
    protected Cnst(final int seq, final String id) throws LangException {
        super(seq, id);
        isVarTyp = false;
        workVarTypIndex = -1;
    }

    /**
     * Construct using sequence number and id string.
     *
     * @param seq MObj.seq number
     * @param symTbl Symbol Table
     * @param stmtTbl Symbol Table
     * @param id Sym id string
     * @throws LangException if Sym.id duplicates the id of another Sym (Cnst or
     *             Var) or Stmt label.
     */
    public Cnst(final int seq, final Map<String, Sym> symTbl,
        final Map<String, Stmt> stmtTbl, final String id) throws LangException
    {
        super(seq, symTbl, stmtTbl, id);
        isVarTyp = false;
        workVarTypIndex = -1;
    }

    /**
     * Is Sym active?
     * <p>
     * {@code Cnst}s are always active as they cannot be defined inside a scope
     * level and must be defined at the global level.
     *
     * @return is Sym "active"
     */
    @Override
    public boolean isActive() {
        return true;
    }

    /**
     * Convenient lookup function to find a TypeConversionRule that will convert
     * the input Type Code to this Type Code.
     *
     * @param typ Type Code to convert from.
     * @return TypeConversionRule to convert from the input Type Code to this
     *         Type Code or null if not found.
     */
    public TypeConversionRule findFromTypConversionRule(final Cnst typ) {
        if (convFromTypGRArray != null)
            for (final TypeConversionRule element : convFromTypGRArray)
                if (element.getConvTyp() == typ)
                    return element;
        return null;
    }

    /**
     * get isVarTyp.
     * <p>
     * IsVarTyp says that this Cnst is used in a $f VarHyp statement as the Type
     * Code of a variable (i.e. "wff" or "set").
     *
     * @return isVarTyp, true or false.
     */
    public boolean isVarTyp() {
        return isVarTyp;
    }

    /**
     * @return true if this is the "set" type, assuming there is a "set" type in
     *         the database, false otherwise.
     */
    public boolean isSetTyp() {
        return getId().equals(LangConstants.CNST_SET_TYPE);
    }

    /**
     * set isVarTyp, true or false.
     * <p>
     * IsVarTyp says that this Cnst is used in a $f VarHyp statement as the Type
     * Code of a variable (i.e. "wff" or "set").
     *
     * @param isVarTyp true or false
     */
    public void setVarTyp(final boolean isVarTyp) {
        this.isVarTyp = isVarTyp;
    }

    /**
     * get isProvableLogicStmtTyp.
     * <p>
     * isProvableLogicStmtTyp says that this Cnst is defined by the user,
     * perhaps by default, as a Type Code used on Provable Logic Statments (and
     * by extension, Logical Axioms.)
     *
     * @return isProvableLogicStmtTyp
     */
    public boolean isProvableLogicStmtTyp() {
        return isProvableLogicStmtTyp;
    }

    /**
     * set isProvableLogicStmtTyp, true or false.
     * <p>
     * isProvableLogicStmtTyp says that this Cnst is defined by the user,
     * perhaps by default, as a Type Code used on Provable Logic Statments (and
     * by extension, Logical Axioms.)
     *
     * @param isProvableLogicStmtTyp the new value
     */
    public void setProvableLogicStmtTyp(final boolean isProvableLogicStmtTyp) {
        this.isProvableLogicStmtTyp = isProvableLogicStmtTyp;
    }

    /**
     * get isLogicStmtTyp.
     * <p>
     * isLogicStmtTyp says that this Cnst is defined by the user, perhaps by
     * default, as a Type Code used on Logic Statments (i.e. "wff"s)
     *
     * @return isLogicStmtTyp
     */
    public boolean isLogicStmtTyp() {
        return isLogicStmtTyp;
    }

    /**
     * set isLogicStmtTyp, true or false.
     * <p>
     * isLogicStmtTyp says that this Cnst is defined by the user, perhaps by
     * default, as a Type Code used on Logic Statments (i.e. "wff"s)
     *
     * @param isLogicStmtTyp the new value
     */
    public void setLogicStmtTyp(final boolean isLogicStmtTyp) {
        this.isLogicStmtTyp = isLogicStmtTyp;
    }

    /**
     * get isSyntaxAxiomTyp.
     * <p>
     * isSyntaxAxiomTyp says that this Cnst is used as the Type Code of at least
     * one Syntax Axiom.
     *
     * @return isSyntaxAxiomTyp
     */
    public boolean isSyntaxAxiomTyp() {
        return isSyntaxAxiomTyp;
    }

    /**
     * set isSyntaxAxiomTyp, true or false.
     * <p>
     * isSyntaxAxiomTyp says that this Cnst is used as the Type Code of at least
     * one Syntax Axiom.
     *
     * @param isSyntaxAxiomTyp the new value
     */
    public void setSyntaxAxiomTyp(final boolean isSyntaxAxiomTyp) {
        this.isSyntaxAxiomTyp = isSyntaxAxiomTyp;
    }

    /**
     * get isGrammaticalTyp.
     * <p>
     * isGrammaticalTyp says that this Cnst is a Type Code used on at least one
     * VarHyp or Syntax Axiom, or isLogicStmtTyp() == true or
     * isProvableLogicStmtTyp() == true.
     *
     * @return isGrammaticalTyp.
     */
    public boolean isGrammaticalTyp() {
        return isGrammaticalTyp;
    }

    /**
     * set isGrammaticalTyp, true or false.
     * <p>
     * isGrammaticalTyp says that this Cnst is a Type Code used on at least one
     * VarHyp or Syntax Axiom, or isLogicStmtTyp() == true or
     * isProvableLogicStmtTyp() == true.
     *
     * @param isGrammaticalTyp the new value
     */
    public void setGrammaticalTyp(final boolean isGrammaticalTyp) {
        this.isGrammaticalTyp = isGrammaticalTyp;
    }

    /**
     * get gRRoot, , the grammar rule forest root for this Cnst.
     * <p>
     * GrammarRule Tree (forest) root node for Grammar rules whose Expression's
     * "ruleFormatExpr" first symbol is this Cnst object. Null if no such tree
     * exists or Grammar is not yet initialized.
     *
     * @return gRRoot, the grammar rule forest root for this Cnst. Null if there
     *         is no forest for this Cnst.
     */
    public GRNode getGRRoot() {
        return gRRoot;
    }

    /**
     * set gRRoot to null or the grammar rule forest root for this Cnst.
     * <p>
     * GrammarRule Tree (forest) root node for Grammar rules whose Expression's
     * "ruleFormatExpr" first symbol is this Cnst object. Null if no such tree
     * exists or Grammar is not yet initialized.
     *
     * @param gRRoot the grammar rule forest root for this Cnst. Null if there
     *            is no forest for this Cnst.
     */
    public void setGRRoot(final GRNode gRRoot) {
        this.gRRoot = gRRoot;
    }

    /**
     * get nbrOccInCnstSyntaxAxioms.
     * <p>
     * nbrOccInCnstSyntaxAxioms counts how many times the constant symbol
     * appears in the expression portion of Syntax Axioms having no variables
     * (i.e. Named Typed Constants, which have expression length = 1 -- or, the
     * much less popular "Literal" variety, which have expression length > 1.)
     * <p>
     * Note: the term "expression" refers to the portion of a Formula following
     * the Type Code, which is always the first symbol of a Formula.
     *
     * @return nbrOccInCnstSyntaxAxioms.
     */
    public int getNbrOccInCnstSyntaxAxioms() {
        return nbrOccInCnstSyntaxAxioms;
    }

    /**
     * Increment nbrOccInCnstSyntaxAxioms by 1.
     */
    public void incNbrOccInCnstSyntaxAxioms() {
        nbrOccInCnstSyntaxAxioms++;
    }

    /**
     * set nbrOccInCnstSyntaxAxioms.
     * <p>
     * nbrOccInCnstSyntaxAxioms counts how many times the constant symbol
     * appears in Syntax Axioms that contain no variables (i.e. Named Typed
     * Constants, which have expression length = 1 -- or, the much less popular
     * "Literal" variety, which have expression length > 1.)
     *
     * @param nbrOccInCnstSyntaxAxioms the new value
     */
    public void setNbrOccInCnstSyntaxAxioms(
        final int nbrOccInCnstSyntaxAxioms)
    {
        this.nbrOccInCnstSyntaxAxioms = nbrOccInCnstSyntaxAxioms;
    }

    /**
     * get nbrOccInSyntaxAxioms.
     * <p>
     * nbrOccInSyntaxAxioms counts how many times a constant appears in the
     * expression portion of Syntax Axioms. If this number equals 1 then the
     * Syntax Axiom in which it appears has special grammatical parsing
     * properties (i.e. the uniqueness is a strong hint to the parser :)
     * <p>
     * If this number is equal to zero that means the constant is defined but is
     * not part of the grammar, and its usage in any other statement is in
     * error. Constants with a value in this field greater than 1 are most
     * likely just punctuation, like "(" and ")"; however, in set.mm the
     * constant "/" is used as punctuation and as a Named Type Constant (see
     * "cdiv", "wsb" and "wsbc".)
     * <p>
     * Note: the term "expression" refers to the portion of a Formula following
     * the Type Code, which is always the first symbol of a Formula.
     *
     * @return nbrOccInSyntaxAxioms.
     */
    public int getNbrOccInSyntaxAxioms() {
        return nbrOccInSyntaxAxioms;
    }

    /**
     * Increment nbrOccInCnstSyntaxAxioms by 1.
     */
    public void incNbrOccInSyntaxAxioms() {
        nbrOccInSyntaxAxioms++;
    }

    /**
     * Set nbrOccInSyntaxAxioms.
     *
     * @param nbrOccInSyntaxAxioms the new value
     */
    public void setNbrOccInSyntaxAxioms(final int nbrOccInSyntaxAxioms) {
        this.nbrOccInSyntaxAxioms = nbrOccInSyntaxAxioms;
    }

    /**
     * get convFromTypGRArray -- null or an array.
     * <p>
     * convFromTypGRArray - Array of Type Conversion GrammarRules, one for each
     * Type Code that can be converted to *this* Type code. Array is null if no
     * Type Conversions exist for the Cnst object (which may or may not be a
     * Type Code), or if the grammar has not yet been (successfully)
     * initialized.
     *
     * @return convFromTypGRArray (may be null).
     */
    public TypeConversionRule[] getConvFromTypGRArray() {
        return convFromTypGRArray;
    }

    /**
     * Set convFromTypGRArray -- null or an array.
     *
     * @param convFromTypGRArray (may be null).
     */
    public void setConvFromTypGRArray(
        final TypeConversionRule[] convFromTypGRArray)
    {
        this.convFromTypGRArray = convFromTypGRArray;
    }

    /**
     * Add a TypeConversionRule to the constant's convFromTypGRArray.
     * <p>
     * Attempts to add a new TypeConversionRule.
     * <p>
     * Rejects duplicates with extreme prejudice (IllegalStateException).
     * <p>
     * Will create the necessary array if needed. Has an unfortunate habit of
     * resizing the array every time a new rule is added. If there were tons of
     * Type Conversion Rules this would be coded differently, but in that case,
     * bigger problems would surface in mmj.verify.Grammar having to do with
     * multitudes of Grammar Rule generated in a combinatorial explosion...sigh.
     *
     * @param r TypeConversionRule to be added.
     * @return index of new Cnst.convFromTypGRArray element
     * @throws IllegalStateException is new rule is a duplicate in the
     *             Cnst.convFromTypGRArray.
     */
    public int convFromTypGRArrayAdd(final TypeConversionRule r) {
        int ruleIndex = 0;
        if (convFromTypGRArray != null) {
            ruleIndex = convFromTypGRArray.length;
            final Cnst typ = r.getBaseSyntaxAxiom().getTyp();
            if (findFromTypConversionRule(r.getConvTyp()) != null)
                throw new IllegalStateException(
                    new LangException(LangConstants.ERRMSG_TYP_CONV_DUP,
                        r.getBaseSyntaxAxiom().getLabel(), typ));
        }

        final TypeConversionRule[] x = new TypeConversionRule[ruleIndex + 1];

        for (int i = 0; i < ruleIndex; i++)
            x[i] = convFromTypGRArray[i];

        x[ruleIndex] = r;
        setConvFromTypGRArray(x);

        return ruleIndex;
    }

    /**
     * get nullsPermittedGR.
     * <p>
     * nullsPermittedGR - set to Nulls Permitted GrammarRule if null values are
     * permitted for variables/expressions with this Type Code, otherwise, if
     * nulls are not permitted, nullsPermittedGR is set to null :0)
     *
     * @return nullsPermittedGR or null if nulls are not permitted for this Type
     *         Code.
     */
    public NullsPermittedRule getNullsPermittedGR() {
        return nullsPermittedGR;
    }

    /**
     * Set nullsPermittedGR.
     * <p>
     * nullsPermittedGR - set to Nulls Permitted GrammarRule if null values are
     * permitted for variables/expressions with this Type Code, otherwise, if
     * nulls are not permitted, nullsPermittedGR is set to null :0)
     *
     * @param nullsPermittedRule or null if nulls are not permitted for this
     *            Type Code.
     */
    public void setNullsPermittedGR(
        final NullsPermittedRule nullsPermittedRule)
    {
        nullsPermittedGR = nullsPermittedRule;
    }

    /**
     * Get Len1CnstNotationRule.
     * <p>
     * len1CnstNotationRule -- Notation Rule for Syntax Axiom with Formula
     * Expression length of 1 consisting of just a Cnst (example, Cnst "c0" from
     * set.mm would have a reference to c0's NotationRule in the "c0" Cnst,
     * here.)
     *
     * @return len1CnstNotationRule or null if there is not a
     *         len1CnstNotationRule for this Cnst.
     */
    public NotationRule getLen1CnstNotationRule() {
        return len1CnstNotationRule;
    }

    /**
     * Set Len1CnstNotationRule.
     * <p>
     * len1CnstNotationRule -- Notation Rule for Syntax Axiom with Formula
     * Expression length of 1 consisting of just a Cnst (example, Cnst "c0" from
     * set.mm would have a reference to c0's NotationRule in the "c0" Cnst,
     * here.)
     *
     * @param len1CnstNotationRule or null if there is not a
     *            len1CnstNotationRule for this Cnst.
     */
    public void setLen1CnstNotationRule(
        final NotationRule len1CnstNotationRule)
    {
        this.len1CnstNotationRule = len1CnstNotationRule;
    }

    /**
     * Get earleyRules.
     * <p>
     * earleyRules -- a list of the Grammar Rules with Type Code equal to this
     * Cnst, sorted by GrammarRule.MAX_SEQ_NBR for mmj.verify.earleyParser.
     *
     * @return earleyRules List for this Type Code or null.
     */
    public List<NotationRule> getEarleyRules() {
        return earleyRules;
    }

    /**
     * Set earleyRules.
     * <p>
     * earleyRules -- a list of the Grammar Rules with Type Code equal to this
     * Cnst, sorted by GrammarRule.MAX_SEQ_NBR for mmj.verify.earleyParser.
     *
     * @param earleyRules List for this Type Code or null.
     */
    public void setEarleyRules(final List<NotationRule> earleyRules) {
        this.earleyRules = earleyRules;
    }

    /**
     * Get earleyFIRST.
     * <p>
     * earleyFIRST -- a Set of {@code Cnst}s that can possibly begin a
     * "ruleFormatExpr" of this (Cnst) Type.
     * <p>
     * For example, the earleyFIRST set for "wff" contains "(" because there is
     * a grammar rule with Type Code "wff" whose Formula ruleFormatExpr begins
     * with "(".
     * <p>
     * Used by mmj.verify.earleyParser for Prediction Lookahead. Sorted by
     * sym.ID.
     *
     * @return earleyFIRST (or null).
     */
    public Set<Cnst> getEarleyFIRST() {
        return earleyFIRST;
    }

    /**
     * Set earleyFIRST.
     * <p>
     * earleyFIRST -- a Set of {@code Cnst}s that can possibly begin a
     * "ruleFormatExpr" of this (Cnst) Type.
     * <p>
     * For example, the earleyFIRST set for "wff" contains "(" because there is
     * a grammar rule with Type Code "wff" whose Formula ruleFormatExpr begins
     * with "(".
     * <p>
     * Used by mmj.verify.earleyParser for Prediction Lookahead. Sorted by
     * sym.ID.
     *
     * @param earleyFIRST (or null).
     */
    public void setEarleyFIRST(final Set<Cnst> earleyFIRST) {
        this.earleyFIRST = earleyFIRST;
    }

    /**
     * Convenient lookup routine used by mmj.verify.EarleyParse "Predictor" to
     * find out whether the next parse symbol in the ruleFormatExpr is the first
     * symbol of any grammar rule with this Type Code.
     *
     * @param exprFIRST the lookup Cnst
     * @return true if {@code earleyFIRST.contains(exprFIRST)}
     */
    public boolean earleyFIRSTContainsSymbol(final Cnst exprFIRST) {
        return earleyFIRST != null && earleyFIRST.contains(exprFIRST);
    }
}
