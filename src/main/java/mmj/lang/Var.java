//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * Var.java  0.07 08/01/2008
 *
 * Dec 31, 2005:
 *     -->
 *
 * Version 0.04:
 *     --> Added "dummy" var feature
 *
 * Version 0.05:
 * Oct-12-2006: - added StmtTbl to constructor and modified to
 *                conform to Metamath.pdf spec change of 6-24-2006
 *                prohibiting Stmt label and Sym id namespace
 *                collisions.
 *
 * Version 0.06:
 * Aug-1-2007: - removed dummy var stuff.
 *              - add accumVarListBySeq()
 *
 * Version 0.07:
 * Aug-1-2008: - Added verifyVarDef() for Theorem Loader stuff.
 */

package mmj.lang;

import java.util.List;
import java.util.Map;

/**
 * Var holds a declared Metamath variable symbol.
 * <p>
 * A Metamath variable can be defined more than once if the $v statements occur
 * within a Scope; outside of that scope the Var is "inactive" and cannot be
 * referenced by subsequent Formula's. However, the "id" -- variable String --
 * cannot have been previously defined as that of a Cnst, and Cnst's must be
 * defined in global scope. (In practice, Var's are defined globally because
 * they have no meaning without a VarHyp -- a Var is just a "thing" with an
 * identifying character String, just like a Cnst.)
 * <p>
 * Curiously, Var receives less coverage in mmj than Cnst. But perhaps that is
 * explained by the fact that to be used in a Formula's Expression, a Var must
 * not only be "active" (in scope), but there must be an active VarHyp for the
 * Var, assigning it a Type Code (and Type Codes are {@code Cnst}s).
 * <p>
 * For convenience, a reference to the activeVarHyp for a Var is stored in var.
 * <p>
 * FYI, variables used in Syntax Axioms must be in "global scope", and they
 * always have an activeVarHyp. mmj does not do anything with this factoid, but
 * the potential exists to maintain a list of activeVarHyp's and Var's by Type
 * Code... and the list would never be null for those Type Codes that are part
 * of the grammar.
 *
 * @see <a href="../../MetamathERNotes.html"> Nomenclature and
 *      Entity-Relationship Notes</a>
 */
public class Var extends Sym {
    private boolean active;
    private VarHyp activeVarHyp;

    /**
     * Adds a new "active" Var to LogicalSystem.
     * <p>
     * If the Var does not already exist in symTbl, it is added, otherwise, the
     * existing Var is returned.
     * <p>
     * If the Var symbol duplicates a Cnst symbol or if the Var is already
     * "active", a LangException is thrown.
     *
     * @param seq MObj.seq number (discarded if Var already exists.)
     * @param symTbl Map containing all {@code Sym}s.
     * @param stmtTbl Map containing all {@code Stmt}s.
     * @param id String value (id) of the Var symbol.
     * @return the new or already existing Var.
     * @throws LangException if the Var symbol duplicates an existing Cnst
     *             symbol or if the Var is already "active" -- or if the Var
     *             symbol duplicates a Stmt label.
     */
    public static Var declareVar(final int seq, final Map<String, Sym> symTbl,
        final Map<String, Stmt> stmtTbl, final String id) throws LangException
    {

        final Sym v = symTbl.get(id);
        Var var;
        if (v == null) {
            if (stmtTbl.containsKey(id))
                throw new LangException(
                    LangConstants.ERRMSG_SYM_ID_DUP_OF_STMT_LABEL, id);
            var = new Var(seq, id, true); // true = "active"
            symTbl.put(id, var);
        }
        else {
            if (!(v instanceof Var))
                throw new LangException(
                    LangConstants.ERRMSG_VAR_IS_DUP_OF_CNST_SYM, id);
            var = (Var)v;
            if (var.isActive())
                throw new LangException(
                    LangConstants.ERRMSG_VAR_IS_ALREADY_ACTIVE, id);
            if (stmtTbl.containsKey(id))
                throw new LangException(
                    LangConstants.ERRMSG_SYM_ID_DUP_OF_STMT_LABEL, id);
            var.setActive(true);
        }
        return var;

    }

    /**
     * Construct using sequence number and id string.
     *
     * @param seq MObj.seq number
     * @param id Sym id string
     * @param active true if this var is "active"
     * @throws LangException if id is empty
     */
    protected Var(final int seq, final String id, final boolean active)
        throws LangException
    {
        super(seq, id);
        setActive(active);
        setActiveVarHyp(null);
    }

    /**
     * Construct using sequence number and id string.
     *
     * @param seq MObj.seq number
     * @param symTbl Symbol Table
     * @param stmtTbl Statement Table
     * @param id Sym id string
     * @param active true if this var is "active"
     * @throws LangException if Sym.id duplicates the id of another Sym (Cnst or
     *             Var).
     */
    public Var(final int seq, final Map<?, ?> symTbl, final Map<?, ?> stmtTbl,
        final String id, final boolean active) throws LangException
    {
        super(seq, symTbl, stmtTbl, id);
        setActive(active);
        setActiveVarHyp(null);

    }

    /**
     * Marks a Var as "active" or "inactive".
     *
     * @param active set Sym {@code true} or {@code false}.
     */
    public void setActive(final boolean active) {
        this.active = active;
    }

    /**
     * Is Sym active?
     * <p>
     * {@code Cnst}s are always active as they cannot be defined inside a scope
     * level, but a Var defined in a non-global scope level is "inactive"
     * outside of that scope.
     * <p>
     * The question "is Sym 'x' active" has relevance only in relation to a
     * Stmt: only "active" {@code Sym}s can be used in a given {@code Stmt}'s
     * Formula.
     *
     * @return is Sym "active"
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Set the activeVarHyp for a Var (or null).
     * <p>
     * For speed and convenience, an active Var maintains its current VarHyp;
     * this saves having to do a difficult look-up :)
     *
     * @param activeVarHyp or null.
     */
    public void setActiveVarHyp(final VarHyp activeVarHyp) {
        this.activeVarHyp = activeVarHyp;
    }

    /**
     * Return the activeVarHyp for a Var.
     * <p>
     * For speed and convenience, an active Var maintains its current VarHyp;
     * this saves having to do a difficult look-up :)
     *
     * @return Returns {@code Var.activeVarHyp} (may be null).
     */
    public VarHyp getActiveVarHyp() {
        return activeVarHyp;
    }

    /**
     * Fetches the VarHyp for a Var given an VarHyp array.
     * <p>
     * A tedious little routine to help our friend Formula.
     *
     * @see mmj.lang.Formula
     * @param varHypArray array of VarHyp such as one would find in an Assrt or
     *            LogHyp.
     * @return returns matching VarHyp or null if not found.
     */
    public VarHyp getVarHyp(final VarHyp[] varHypArray) {

        // originally coded with getActiveVarHyp() but
        // decided that that could introduce bizarre side effects
        // down the road...ouch! Now just use if varHypArray == null.
        if (varHypArray == null)
            return getActiveVarHyp();

        // VarHyp vH = getActiveVarHyp();
        VarHyp vH = null;
        // if (vH == null) {
        for (final VarHyp element : varHypArray)
            if (this == element.getVar()) {
                vH = element;
                break;
            }
        // }
        return vH;
    }

    /**
     * Fetches the VarHyp for a Var given a Hyp array.
     * <p>
     * A tedious little routine to help our friend Formula.
     *
     * @see mmj.lang.Formula
     * @param hypArray array of Hyp such as one would find in a ScopeFrame.
     * @return returns matching VarHyp or null if not found.
     */
    public VarHyp getVarHyp(final Hyp[] hypArray) {

        VarHyp vH = null;
        for (final Hyp element : hypArray)
            if (element instanceof VarHyp)
                if (this == ((VarHyp)element).getVar()) {
                    vH = (VarHyp)element;
                    break;
                }
        return vH;
    }

    /**
     * Validates a variable's string (id) to make sure it is properly defined
     * and active.
     * <p>
     * If so, a reference to the existing Var is returned. Otherwise,
     * LangException generated!
     *
     * @param symTbl Map of already defined {@code Sym}s.
     * @param varS String identifying a variable.
     * @return Var matching input String.
     * @throws LangException thrown if variable is not defined, is defined as a
     *             constant, or is not active.
     */
    public static Var verifyVarDefAndActive(final Map<String, Sym> symTbl,
        final String varS) throws LangException
    {

        final Sym tblV = symTbl.get(varS);
        if (tblV == null)
            throw new LangException(LangConstants.ERRMSG_STMT_VAR_UNDEF, varS);
        if (!(tblV instanceof Var))
            throw new LangException(
                LangConstants.ERRMSG_STMT_VAR_NOT_DEF_AS_VAR, varS);
        if (!tblV.isActive())
            throw new LangException(LangConstants.ERRMSG_STMT_VAR_NOT_ACTIVE,
                varS);
        return (Var)tblV;
    }

    /**
     * Validates a variable's string (id) to make sure it is properly defined.
     * <p>
     * If so, a reference to the existing Var is returned. Otherwise,
     * LangException generated!
     *
     * @param symTbl Map of already defined {@code Sym}s.
     * @param varS String identifying a variable.
     * @return Var matching input String.
     * @throws LangException thrown if variable is not defined, is defined as a
     *             constant, or is not active.
     */
    public static Var verifyVarDef(final Map<String, Sym> symTbl,
        final String varS) throws LangException
    {

        final Sym tblV = symTbl.get(varS);
        if (tblV == null)
            throw new LangException(LangConstants.ERRMSG_STMT_VAR_UNDEF, varS);
        if (!(tblV instanceof Var))
            throw new LangException(
                LangConstants.ERRMSG_STMT_VAR_NOT_DEF_AS_VAR, varS);

        return (Var)tblV;
    }

    /**
     * Accumulate var (no duplicates), storing it in an array list in order of
     * appearance in the database.
     * <p>
     * Because varList is maintained in database statement sequence order,
     * hypList should either be empty (new) before the call, or already be in
     * that order.
     *
     * @param varList List of Var's, updated here.
     */
    public void accumVarListBySeq(final List<Var> varList) {

        int i = 0;
        final int iEnd = varList.size();
        final int newSeq = seq;
        int existingSeq;

        while (true) {
            if (i < iEnd) {
                existingSeq = varList.get(i).seq;
                if (newSeq < existingSeq)
                    // insert here, at "i"
                    break;
                if (newSeq == existingSeq)
                    // don't add, already here.
                    return;
            }
            else
                // insert at end, which happens to be here at "i"
                break;
            i++;
        }
        varList.add(i, this);
        return;
    }

    /**
     * Searches for this Var in an ArrayList maintained in database input
     * sequence.
     *
     * @param varList List of Var's
     * @return true if found, else false.
     */
    public boolean containedInVarListBySeq(final List<Var> varList) {
        Var v;
        for (int i = 0; i < varList.size(); i++) {
            v = varList.get(i);
            if (seq < v.seq)
                break;
            if (v == this)
                return true;
        }
        return false;
    }
}
