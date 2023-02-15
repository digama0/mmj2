//********************************************************************/
//* Copyright (C) 2016  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-byteacter line to adjust editor window) 23456789*/

/*
 * FolTranslator.java  0.02 08/23/2005
 */

package mmj.setmm;

import static mmj.setmm.SetMMConstants.*;

import java.util.*;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.setmm.LFTerm.*;
import mmj.setmm.LFType.LFArrow;
import mmj.setmm.LFType.LFPi;

/**
 * Translate Metamath statements and theorems into LF/HOL.
 */
public class FolTranslator {
    public final ProofAsst pa;
    private final SetMMConstants sc;
    public final Map<Axiom, byte[][]> boundVars;
    public final Map<Axiom, Axiom> syntaxToDefn;
    public final Map<Stmt, String> alignments;

    /**
     * Gets the bound variable data for a given syntax axiom. This is a list of
     * {@code byte[]} for each variable in the constructor, in database order.
     * The non-set variables are null, and the set variable entries are a list
     * over the variables again.
     * <p>
     * The {@code byte} value at this index is {@code 1} if there are bound
     * occurrences of the set variable in the other variable, and {@code 2} if
     * there are free occurrences of the set variable. If both occur (i.e. the
     * set variable occurs both bound and free, such as in df-sb) it is set to
     * {@code 3}, and {@code 0} if neither occurs (which should not happen
     * unless the set variable is not used in the definition at all).
     * <p>
     * The value of the array at two set variables is not meaningful and can be
     * anything, except that a variable is considered to appear free w.r.t
     * itself (i.e. {@code (boundVars[i][i] & 2) != 0} when it appears free in
     * the definition.
     *
     * @param syntax the syntax axiom
     * @return the bound variable data
     * @throws SetMMException if bound variable data is not available for the
     *             syntax
     */
    public byte[][] getBoundVars(final Axiom syntax) throws SetMMException {
        byte[][] ret = boundVars.get(syntax);
        if (ret == null) {
            for (final VarHyp h : syntax.getMandVarHypArray())
                if (h.getTyp() == sc.SET)
                    throw new SetMMException(ERRMSG_NEW_AXIOM, syntax);
            boundVars.put(syntax,
                ret = new byte[syntax.getMandVarHypArray().length][]);
        }
        return ret;
    }

    private void processBoundVars(final Axiom defn) throws SetMMException {
        final ParseNode root = defn.getExprParseTree().getRoot();
        if (root.stmt != sc.WB && root.stmt != sc.WCEQ)
            return;
        if (!(root.child[0].stmt instanceof Axiom))
            return;
        final TreeSet<VarHyp> set = new TreeSet<>(MObj.SEQ);
        for (final ParseNode child : root.child[0].child)
            if (!(child.stmt instanceof VarHyp) || !set.add((VarHyp)child.stmt))
                return;
        final VarHyp[] parameters = set.toArray(new VarHyp[set.size()]);
        final byte[][] ret = new byte[parameters.length][];

        for (int i = 0; i < parameters.length; i++)
            if (parameters[i].getTyp() == sc.SET)
                processBoundVars(defn, parameters, parameters[i],
                    ret[i] = new byte[parameters.length], root.child[1]);

        final Axiom ax = (Axiom)root.child[0].stmt;
        boundVars.put(ax, ret);
        syntaxToDefn.put(ax, defn);
    }

    private void processBoundVars(final Axiom defn, final VarHyp[] parameters,
        final VarHyp x, final byte[] out, final ParseNode node)
        throws SetMMException
    {
        if (node.stmt instanceof VarHyp) {
            for (int i = 0; i < parameters.length; i++)
                if (parameters[i] == node.stmt) {
                    out[i] |= 2;
                    return;
                }
            return;
        }
        if (!(node.stmt instanceof Axiom))
            throw new SetMMException(ERRMSG_THM_IN_SYNTAX, node.stmt);
        final Axiom ax = (Axiom)node.stmt;
        int setIndex = -1;
        for (int i = 0; i < node.child.length; i++)
            if (node.child[i].stmt == x) {
                if (setIndex != -1)
                    throw new SetMMException(ERRMSG_REUSE_SET_IN_DEFN, defn, x);
                setIndex = i;
            }
        if (setIndex == -1)
            for (final ParseNode child : node.child)
                processBoundVars(defn, parameters, x, out, child);
        else {
            final byte[] bv = getBoundVars(ax)[setIndex];
            for (int i = 0; i < bv.length; i++) {
                if ((bv[i] & 1) != 0) {
                    final byte[] inner = new byte[out.length];
                    processBoundVars(defn, parameters, x, inner, node.child[i]);
                    for (int j = 0; j < out.length; j++)
                        out[j] |= inner[j] != 0 ? 1 : 0;
                }
                if ((bv[i] & 2) != 0)
                    processBoundVars(defn, parameters, x, out, node.child[i]);
            }
        }
    }

    /**
     * Create a new {@link FolTranslator} instance.
     *
     * @param pa The parent {@link ProofAsst} instance.
     * @param sc The parent {@link SetMMConstants} instance (this class only
     *            works on {@code set.mm}).
     * @throws SetMMException If this database does not look like set.mm
     */
    public FolTranslator(final ProofAsst pa, final SetMMConstants sc)
        throws SetMMException
    {
        this.pa = pa;
        this.sc = sc;
        boundVars = new HashMap<>();
        syntaxToDefn = new HashMap<>();
        alignments = new HashMap<>();
        // The axiomatically defined syntax axioms are the base case;
        // other syntax axioms with definitions add to the set
        boundVars.put(sc.WN, new byte[1][]);
        boundVars.put(sc.WI, new byte[2][]);
        boundVars.put(sc.WB, new byte[2][]);
        boundVars.put(sc.WAL, new byte[][]{null, {1, 0}});
        boundVars.put(sc.CV, new byte[][]{{2}});
        boundVars.put(sc.WCEQ, new byte[2][]);
        boundVars.put(sc.WCEL, new byte[2][]);
        boundVars.put(sc.CAB, new byte[][]{null, {1, 0}});

        final Map<String, Stmt> stmts = pa.getLogicalSystem().getStmtTbl();
        alignments.put(sc.WN, "not");
        alignments.put(sc.WI, "impl");
        alignments.put(sc.WB, "equiv");
        alignments.put(sc.WA, "and");
        alignments.put(sc.WO, "or");
        alignments.put(sc.WTRU, "true");
        alignments.put(sc.WAL, "forall");
        alignments.put(sc.WEX, "exists");
        alignments.put(stmts.get("tru"), "trueI");
        alignments.put(stmts.get("pm3.2i"), "andI");
        alignments.put(stmts.get("simpli"), "andEl");
        alignments.put(stmts.get("simpri"), "andEr");
        alignments.put(stmts.get("orci"), "orIl");
        alignments.put(stmts.get("olci"), "orIr");
        alignments.put(stmts.get("impl"), "_impl");

        final TreeSet<Stmt> sorted = new TreeSet<>(MObj.SEQ);
        sorted.addAll(pa.getLogicalSystem().getStmtTbl().values());
        for (final Stmt s : sorted)
            if (s instanceof Axiom && s.getTyp() == sc.DED)
                processBoundVars((Axiom)s);
    }

    public String align(final Stmt stmt) {
        final String s = alignments.get(stmt);
        if (s != null)
            return s;
        return stmt.getLabel().replace('-', '_').replace('.', '_');
    }

    /**
     * Mangles variable names to make them valid identifiers.
     *
     * @param vh Input variable hyp
     * @param type variable type
     * @return output variable
     */
    public LFVar var(final VarHyp vh, final LFType type) {
        final String s = vh.getVar().getId();
        if (s.matches("\\w+"))
            return new LFVar(s, type);
        return new LFVar(align(vh).substring(1), type);
//        final StringBuilder ret = new StringBuilder();
//        for (final byte b : s.getBytes())
//            if (Character.toString((char)b).matches("\\w"))
//                ret.append((char)b);
//            else
//                ret.append("_").append(Integer.toHexString(b));
//        return new LFVar(ret.toString(), type);
    }

    private Map<VarHyp, Set<VarHyp>> getDependVars(final Assrt stmt)
        throws SetMMException
    {
        final TreeMap<VarHyp, Set<VarHyp>> map = new TreeMap<>(MObj.SEQ);
        for (final Hyp h : stmt.getMandFrame().hypArray)
            if (h instanceof VarHyp)
                map.put((VarHyp)h, new TreeSet<>(MObj.SEQ));
        final ArrayDeque<VarHyp> stack = new ArrayDeque<>();
        for (final Hyp h : stmt.getMandFrame().hypArray)
            if (h instanceof LogHyp)
                scanExpr(map, stack, null, h.getExprParseTree().getRoot());
        scanExpr(map, stack, null, stmt.getExprParseTree().getRoot());
        for (final DjVars dv : stmt.getMandFrame().djVarsArray) {
            VarHyp s, v;
            if (dv.getVarLo().getActiveVarHyp().getTyp() != sc.SET) {
                v = dv.getVarLo().getActiveVarHyp();
                s = dv.getVarHi().getActiveVarHyp();
            }
            else if (dv.getVarHi().getActiveVarHyp().getTyp() != sc.SET) {
                v = dv.getVarHi().getActiveVarHyp();
                s = dv.getVarLo().getActiveVarHyp();
            }
            else
                continue;
            map.get(v).remove(s);
        }
        return map;

    }

    private void scanExpr(final Map<VarHyp, Set<VarHyp>> dependVars,
        final Deque<VarHyp> boundStack, final Set<VarHyp> free,
        final ParseNode node) throws SetMMException
    {
        if (node.stmt instanceof VarHyp) {
            if (free != null) {
                for (final VarHyp v : dependVars.get(node.stmt))
                    if (!boundStack.contains(v))
                        free.add(v);
            }
            else if (node.stmt.getTyp() == sc.SET)
                dependVars.get(node.stmt).add((VarHyp)node.stmt);
            else
                dependVars.get(node.stmt).addAll(boundStack);

            return;
        }
        if (!(node.stmt instanceof Axiom))
            throw new SetMMException(ERRMSG_THM_IN_SYNTAX, node.stmt);
        final Axiom ax = (Axiom)node.stmt;
        final byte[][] bv = getBoundVars(ax);
        for (int i = 0; i < node.child.length; i++)
            if (node.child[i].stmt.getTyp() != sc.SET) {
                final int oldLen = boundStack.size();
                for (int j = 0; j < node.child.length; j++)
                    if (bv[j] != null && (bv[j][i] & 1) != 0)
                        boundStack.push((VarHyp)node.child[j].stmt);
                scanExpr(dependVars, boundStack, free, node.child[i]);
                while (boundStack.size() > oldLen)
                    boundStack.pop();
            }
    }

    public LFType translateAssrt(final Assrt assrt) throws SetMMException {
        if (assrt.getTyp() != sc.DED) {
            final byte[][] bv = getBoundVars((Axiom)assrt);
            LFType t = assrt.getTyp() == sc.WFF ? LFType.PROP : LFType.CLASS;
            final ParseNode node = assrt.getExprParseTree().getRoot();
            for (int i = node.child.length - 1; i >= 0; i--)
                if (bv[i] == null) {
                    LFType ty = node.child[i].stmt.getTyp() == sc.WFF
                        ? LFType.PROP : LFType.CLASS;
                    for (int j = node.child.length - 1; j >= 0; j--)
                        if (bv[j] != null && (bv[j][i] & 1) != 0)
                            ty = new LFArrow(LFType.SET, ty);
                    t = new LFArrow(ty, t);
                }
                else {
                    boolean needsFree = (bv[i][i] & 2) != 0;
                    if (!needsFree)
                        for (int j = 0; j < node.child.length; j++)
                            if (bv[i][j] == 3
                                && node.child[j].stmt.getTyp() != sc.SET)
                            {
                                needsFree = true;
                                break;
                            }
                    if (needsFree)
                        t = new LFArrow(LFType.SET, t);
                }
            return t;
        }
        final Map<VarHyp, Set<VarHyp>> dependVars = getDependVars(assrt);
        LFType t = translateStmt(dependVars, assrt);
        final Hyp[] hypArray = assrt.getMandFrame().hypArray;
        for (int i = hypArray.length - 1; i >= 0; i--)
            if (hypArray[i] instanceof LogHyp)
                t = new LFArrow(translateStmt(dependVars, hypArray[i]), t);
            else if (hypArray[i] instanceof VarHyp
                && hypArray[i].getTyp() != sc.SET)
            {
                LFType ty = hypArray[i].getTyp() == sc.WFF ? LFType.PROP
                    : LFType.CLASS;
                for (@SuppressWarnings("unused")
                final VarHyp v : dependVars.get(hypArray[i]))
                    ty = new LFArrow(LFType.SET, ty);
                t = new LFPi(var((VarHyp)hypArray[i], ty), t);
            }
        return t;
    }

    private LFType translateStmt(final Map<VarHyp, Set<VarHyp>> dependVars,
        final Stmt s) throws SetMMException
    {
        final ParseNode root = s.getExprParseTree().getRoot();
        LFType t = new LFType.LFDed(translateTerm(dependVars, root));
        final ArrayDeque<VarHyp> stack = new ArrayDeque<>();
        final TreeSet<VarHyp> free = new TreeSet<>(MObj.SEQ.reversed());
        scanExpr(dependVars, stack, free, root);
        for (final VarHyp v : free)
            t = new LFPi(var(v, LFType.SET), t);
        return t;
    }

    private LFTerm translateTerm(final Map<VarHyp, Set<VarHyp>> dependVars,
        final ParseNode node) throws SetMMException
    {
        if (node.stmt instanceof VarHyp) {
            if (node.stmt.getTyp() == sc.SET)
                return var((VarHyp)node.stmt, LFType.SET);
            LFType ty = node.stmt.getTyp() == sc.WFF ? LFType.PROP
                : LFType.CLASS;
            final Set<VarHyp> s = dependVars.get(node.stmt);
            if (s == null)
                throw new IllegalArgumentException();
            for (@SuppressWarnings("unused")
            final VarHyp v : s)
                ty = new LFArrow(LFType.SET, ty);
            LFTerm t = var((VarHyp)node.stmt, ty);
            for (final VarHyp v : s)
                t = new LFApply(t, var(v, LFType.SET));
            return t;
        }
        if (!(node.stmt instanceof Axiom))
            throw new SetMMException(ERRMSG_THM_IN_SYNTAX, node.stmt);
        final Axiom ax = (Axiom)node.stmt;
        final byte[][] bv = getBoundVars(ax);
        LFTerm ap = new LFConst(align(ax));
        for (int i = 0; i < node.child.length; i++)
            if (bv[i] == null) {
                LFTerm t = translateTerm(dependVars, node.child[i]);
                for (int j = node.child.length - 1; j >= 0; j--)
                    if (bv[j] != null && (bv[j][i] & 1) != 0)
                        t = LFLambda.reducedLambda(
                            var((VarHyp)node.child[j].stmt, LFType.SET), t);
                ap = new LFApply(ap, t);
            }
            else {
                boolean needsFree = (bv[i][i] & 2) != 0;
                if (!needsFree)
                    for (int j = 0; j < node.child.length; j++)
                        if (bv[i][j] == 3
                            && node.child[j].stmt.getTyp() != sc.SET)
                        {
                            needsFree = true;
                            break;
                        }
                if (needsFree)
                    ap = new LFApply(ap,
                        var((VarHyp)node.child[i].stmt, LFType.SET));
            }
        return ap;
    }

    public LFTerm translateProof(final Assrt assrt) throws SetMMException {
        if (assrt.getTyp() != sc.DED) {
            final Axiom defn = syntaxToDefn.get(assrt);
            if (defn == null)
                return null;

            final Map<VarHyp, Set<VarHyp>> dependVars = getDependVars(assrt);
            final ParseNode root = defn.getExprParseTree().getRoot();
            LFTerm t;
            try {
                t = translateTerm(dependVars, root.child[1]);
            } catch (final IllegalArgumentException e) {
                return null;
            }
            final ParseNode node = root.child[0];
            final byte[][] bv = getBoundVars((Axiom)assrt);
            for (int i = node.child.length - 1; i >= 0; i--)
                if (bv[i] == null) {
                    LFType ty = node.child[i].stmt.getTyp() == sc.WFF
                        ? LFType.PROP : LFType.CLASS;
                    for (int j = node.child.length - 1; j >= 0; j--)
                        if (bv[j] != null && (bv[j][i] & 1) != 0)
                            ty = new LFArrow(LFType.SET, ty);
                    t = new LFLambda(var((VarHyp)node.child[i].stmt, ty), t);
                }
                else {
                    boolean needsFree = (bv[i][i] & 2) != 0;
                    if (!needsFree)
                        for (int j = 0; j < node.child.length; j++)
                            if (bv[i][j] == 3
                                && node.child[j].stmt.getTyp() != sc.SET)
                            {
                                needsFree = true;
                                break;
                            }
                    if (needsFree)
                        t = new LFLambda(
                            var((VarHyp)node.child[i].stmt, LFType.SET), t);
                }
            return t;
        }
        return null;
    }
}
