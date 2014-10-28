package mmj.oth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import mmj.lang.LangException;
import mmj.lang.WorkVarManager;
import mmj.pa.ProofAsst;

public class ArtReader {
//    public static Const EQUALS = new Const(new Name("HOLLight.="));
//    public static TypeOp FUN = new TypeOp(new Name("HOLLight.fun"));
//    public static OpType BOOL = new OpType(Collections.<Type> emptyList(),
//        new TypeOp(new Name("HOLLight.bool")));
    public static Const EQUALS = new Const(new Name(OTConstants.EQUALS));
    public static TypeOp FUN = new TypeOp(new Name(OTConstants.FUN));
    public static OpType BOOL = OpType.get(Collections.<Type> emptyList(),
        new TypeOp(new Name(OTConstants.BOOL)));
    public static Name VAR_A = new Name(OTConstants.VAR_A);
    public static Name VAR_R = new Name(OTConstants.VAR_R);

    public static void main(final String[] args) {
        try {
            new ArtReader(args[0], null, null, null);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        } catch (final LangException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public ArtReader(final String fileName, final int[] selThms,
        final ProofAsst pa, final WorkVarManager w)
        throws FileNotFoundException, LangException
    {
        if (selThms != null && selThms.length == 0)
            return;
        int nextThm = 0;
        final File file = new File(fileName);
        final Interpreter interp = pa == null ? null : new Interpreter(
            file.getName(), pa.getLogicalSystem(), pa.getVerifyProofs(), w,
            pa.getMessages());
        final Scanner sc = new Scanner(file);
        final List<Thm> assums = new ArrayList<Thm>();
        final List<Thm> thms = new ArrayList<Thm>();
        try {
            final Deque<Object> stack = new ArrayDeque<Object>();
            final Map<Integer, Object> dict = new HashMap<Integer, Object>();
            int line = 0;
            while (sc.hasNextLine()) {
                final String cmd = sc.nextLine();
                line++;
                Thm thm = null, thm2 = null;
//                for (final Object o : stack)
//                    System.out.println("o> " + o);
//                System.out.println("> " + cmd);
                if (cmd.charAt(0) == OTConstants.ART_COMMENT_MARK)
                    System.out.println("Comment: " + cmd);
                else if (cmd.charAt(0) == '"') {
                    final Name n = new Name(cmd.substring(1, cmd.length() - 1));
                    stack.push(n);
//                    System.out.println(n);
                }
                else if (cmd.equals(OTConstants.ART_ABS_TERM)) {
                    final Term b = (Term)stack.pop();
                    final Var x = (Var)stack.pop();
                    final AbsTerm t = AbsTerm.get(x, b);
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals(OTConstants.ART_ABS_THM)) {
                    final Thm th = (Thm)stack.pop();
                    final Var v = (Var)stack.pop();
                    assert th.t.asApp().getF().asApp().getF().asConst()
                        .getConst().equals(EQUALS);
                    final Term t = th.t.asApp().getF().asApp().getX();
                    final Term u = th.t.asApp().getX();

                    thm = new Thm(line, cmd, Arrays.asList(th), th.assum,
                        ConstTerm.eq(AbsTerm.get(v, t), AbsTerm.get(v, u)));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_ASSUME)) {
                    final Term t = (Term)stack.pop();
                    if (!t.getType().equals(BOOL))
                        throw new IllegalStateException(
                            "cannot assume non-boolean term");
                    final List<Term> l = new ArrayList<Term>();
                    l.add(t);
                    thm = new Thm(line, cmd, Collections.EMPTY_LIST, l, t);
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_APP_TERM)) {
                    final Term x = (Term)stack.pop();
                    final Term f = (Term)stack.pop();
                    final OpType ty = f.getType().asOp();
                    if (!(ty.getOp().equals(FUN) && ty.getArgs().size() == 2 && ty
                        .getArgs().get(0).equals(x.getType())))
                        throw new IllegalStateException(
                            "function application (" + f + " " + x
                                + ") has mismatched type");
                    final AppTerm t = AppTerm.get(f, x);
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals(OTConstants.ART_APP_THM)) {
                    final Thm t2 = (Thm)stack.pop();
                    final Thm t1 = (Thm)stack.pop();
                    assert t1.t.asApp().getF().asApp().getF().asConst()
                        .getConst().equals(EQUALS);
                    assert t2.t.asApp().getF().asApp().getF().asConst()
                        .getConst().equals(EQUALS);
                    final Term f = t1.t.asApp().getF().asApp().getX();
                    final Term g = t1.t.asApp().getX();
                    final Term x = t2.t.asApp().getF().asApp().getX();
                    final Term y = t2.t.asApp().getX();

                    thm = new Thm(line, cmd, Arrays.asList(t1, t2), union(
                        t1.assum, t2.assum), ConstTerm.eq(AppTerm.get(f, x),
                        AppTerm.get(g, y)));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_AXIOM)) {
                    final Term t = (Term)stack.pop();
                    final List<Term> assum = (List<Term>)stack.pop();
                    for (final Term a : assum)
                        if (!a.getType().equals(BOOL))
                            throw new IllegalStateException(
                                "cannot assume non-boolean term");
                    if (!t.getType().equals(BOOL))
                        throw new IllegalStateException(
                            "cannot assume non-boolean term");
                    thm = new Thm(line, cmd, Collections.EMPTY_LIST, assum, t);
                    stack.push(thm);
                    assums.add(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_BETA_CONV)) {
                    final AppTerm t = (AppTerm)stack.pop();
                    final AbsTerm a = t.getF().asAbs();
                    thm = new Thm(line, cmd, Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST, ConstTerm.eq(
                            t,
                            a.getB().subst(
                                Arrays.asList(
                                    (List<List<Object>>)Collections.EMPTY_LIST,
                                    Arrays.asList(Arrays.asList(a.getVar(),
                                        t.getX()))))));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_CONS)) {
                    final List<Object> old = (List<Object>)stack.pop();
                    final List<Object> l = new ArrayList<Object>();
                    l.add(stack.pop());
                    l.addAll(old);
                    stack.push(l);
//                    System.out.println(l);
                }
                else if (cmd.equals(OTConstants.ART_CONST)) {
                    final Const c = new Const((Name)stack.pop());
                    stack.push(c);
//                    System.out.println(c);
                }
                else if (cmd.equals(OTConstants.ART_CONST_TERM)) {
                    final ConstTerm t = ConstTerm.get((Type)stack.pop(),
                        (Const)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals(OTConstants.ART_DEDUCT)) {
                    final Thm t2 = (Thm)stack.pop();
                    final Thm t1 = (Thm)stack.pop();

                    thm = new Thm(line, cmd, Arrays.asList(t1, t2), union(
                        exclude(t1.assum, t2.t), exclude(t2.assum, t1.t)),
                        ConstTerm.eq(t1.t, t2.t));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_DEF)) {
                    final Integer i = (Integer)stack.pop();
                    final Object x = stack.peek();
                    dict.put(i, x);
//                    System.out.println(i + " -> " + x);
                }
                else if (cmd.equals(OTConstants.ART_DEFINE_CONST)) {
                    final Term t = (Term)stack.pop();
                    final Const c = new Const((Name)stack.pop());
                    if (!t.freeVars().isEmpty())
                        throw new IllegalStateException(
                            "constant term definition " + c + " = " + t
                                + " has free vars");
                    final Type ty = t.getType();
                    final Set<VarType> typeVars = ty.getTypeVars();
                    for (final VarType v : t.getSubTypeVars())
                        if (!typeVars.contains(v))
                            throw new IllegalStateException(
                                "constant term definition " + c + " = " + t
                                    + " free term variables in its subterms");

                    thm = new Thm(line, cmd, Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST, ConstTerm.eq(
                            ConstTerm.get(ty, c), t));
                    stack.push(c);
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_DEFINE_TYPE_OP)) {
                    final Thm thl = (Thm)stack.pop();
                    final AppTerm t = thl.t.asApp();
                    final List<Name> names = (List<Name>)stack.pop();
                    final Const rep = new Const((Name)stack.pop());
                    final Const abs = new Const((Name)stack.pop());
                    final TypeOp op = new TypeOp((Name)stack.pop());
                    stack.push(op);
                    stack.push(abs);
                    stack.push(rep);
                    if (!t.getF().freeVars().isEmpty())
                        throw new IllegalStateException("type definition "
                            + t.getF() + " has free vars");
                    for (final VarType v : t.getSubTypeVars()) {
                        int i = 0;
                        for (final Name n : names)
                            if (n.equals(v.getName()))
                                i++;
                        if (i != 1)
                            throw new IllegalStateException(
                                "incorrect type def list");
                    }
                    final List<Type> vars = new ArrayList<Type>();
                    for (final Name n : names)
                        vars.add(VarType.get(n));
                    final Type tOld = t.getX().getType();
                    final Type tNew = OpType.get(vars, op);
                    final ConstTerm abst = ConstTerm.get(OpType.to(tOld, tNew),
                        abs);
                    final ConstTerm rept = ConstTerm.get(OpType.to(tNew, tOld),
                        rep);

                    final VarTerm dummy = VarTerm.get(new Var(tNew, VAR_A));
                    thm = new Thm(line, cmd, Arrays.asList(thl),
                        Collections.EMPTY_LIST, ConstTerm.eq(
                            AppTerm.get(abst, AppTerm.get(rept, dummy)), dummy));
                    stack.push(thm);

                    final VarTerm dummy2 = VarTerm.get(new Var(tOld, VAR_R));
                    thm2 = new Thm(line, OTConstants.ART_DEFINE_TYPE_OP_2,
                        Arrays.asList(thl), Collections.EMPTY_LIST,
                        ConstTerm.eq(AppTerm.get(t.getF(), dummy2), ConstTerm
                            .eq(AppTerm.get(rept, AppTerm.get(abst, dummy2)),
                                dummy2)));
                    stack.push(thm2);
                    System.out.println(thm);
                    System.out.println(thm2);
                }
                else if (cmd.equals(OTConstants.ART_EQ_MP)) {
                    final Thm t1 = (Thm)stack.pop();
                    final Thm t2 = (Thm)stack.pop();
                    assert t2.t.asApp().getF().asApp().getF().asConst()
                        .getConst().equals(EQUALS);
                    final Term p = t2.t.asApp().getF().asApp().getX();
                    final Term q = t2.t.asApp().getX();
                    thm = new Thm(line, cmd, Arrays.asList(t1, t2), union(
                        t1.assum, t2.assum), q);
                    if (!p.equals(t1.t))
                        throw new IllegalStateException(
                            "terms not alpha-equiv:\n " + t1 + "\n " + t2
                                + "\n" + thm);

                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_NIL))
                    stack.push(new ArrayList<Type>());
//                    System.out.println("[]");
                else if (cmd.equals(OTConstants.ART_OP_TYPE)) {
                    final OpType t = OpType.get((List<Type>)stack.pop(),
                        (TypeOp)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals(OTConstants.ART_POP))
                    // System.out.println("pop " + stack.pop());
                    stack.pop();
                else if (cmd.equals(OTConstants.ART_REF)) {
                    final Integer i = (Integer)stack.pop();
                    final Object x = dict.get(i);
                    stack.push(x);
//                    System.out.println("dict[" + i + "] = " + x);
                }
                else if (cmd.equals(OTConstants.ART_REFL)) {
                    final Term t = (Term)stack.pop();
                    thm = new Thm(line, cmd, Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST, ConstTerm.eq(t, t));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_REMOVE)) {
                    final Integer i = (Integer)stack.pop();
                    final Object x = dict.remove(i);
                    stack.push(x);
//                    System.out.println("dict[" + i + "] -> " + x);
                }
                else if (cmd.equals(OTConstants.ART_SUBST)) {
                    final Thm old = (Thm)stack.pop();
                    thm = old
                        .subst(line, (List<List<List<Object>>>)stack.pop());
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_THM)) {
                    final Term t = (Term)stack.pop();
                    final List<Term> l = (List<Term>)stack.pop();
                    final Thm orig = (Thm)stack.pop();
                    thm = new Thm(line, cmd, Arrays.asList(orig), l, t);
                    if (!thm.equals(orig))
                        throw new IllegalStateException(
                            "theorem not alpha-equivalent:\n" + orig + "\n"
                                + thm);
                    thms.add(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals(OTConstants.ART_TYPE_OP)) {
                    final TypeOp t = new TypeOp((Name)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals(OTConstants.ART_VAR)) {
                    final Var v = new Var((Type)stack.pop(), (Name)stack.pop());
                    stack.push(v);
//                    System.out.println(v);
                }
                else if (cmd.equals(OTConstants.ART_VAR_TERM)) {
                    final VarTerm t = VarTerm.get((Var)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals(OTConstants.ART_VAR_TYPE)) {
                    final VarType v = VarType.get((Name)stack.pop());
                    stack.push(v);
//                    System.out.println(v);
                }
                else if (cmd.matches(OTConstants.ART_DIGIT_REGEX)) {
                    final Integer i = Integer.valueOf(cmd);
                    stack.push(i);
//                    System.out.println(i);
                }
                else
                    throw new IllegalStateException("unknown: " + cmd);
                if (selThms == null) {
                    if (interp != null) {
                        interp.addThm(thm, true);
                        interp.addThm(thm2, true);
                    }
                }
                else if (line == selThms[nextThm]) {
                    interp.addThm(thm, false);
                    interp.addThm(thm2, false);
                    if (++nextThm == selThms.length)
                        return;
                }
            }
        } finally {
            System.out.println("Assumptions:");
            for (final Thm t : assums)
                System.out.println("  " + t);
            System.out.println("Theorems:");
            for (final Thm t : thms)
                System.out.println("  " + t);
            sc.close();
        }
    }

    List<Term> union(final List<Term> t1, final List<Term> t2) {
        final ArrayList<Term> list = new ArrayList<Term>();
        for (final Term t : t1)
            if (!list.contains(t))
                list.add(t);
        for (final Term t : t2)
            if (!list.contains(t))
                list.add(t);
        return list;
    }

    List<Term> exclude(final List<Term> list, final Term t) {
        final ArrayList<Term> l = new ArrayList<Term>();
        for (final Term tm : list)
            if (!tm.equals(t))
                l.add(tm);
        return l;
    }
}
