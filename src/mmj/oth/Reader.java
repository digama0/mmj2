package mmj.oth;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import mmj.lang.LogicalSystem;

public class Reader {
//    public static Const EQUALS = new Const(new Name("HOLLight.="));
//    public static TypeOp FUN = new TypeOp(new Name("HOLLight.fun"));
//    public static OpType BOOL = new OpType(Collections.<Type> emptyList(),
//        new TypeOp(new Name("HOLLight.bool")));
    public static Const EQUALS = new Const(new Name(OTConstants.EQUALS));
    public static TypeOp FUN = new TypeOp(new Name(OTConstants.FUN));
    public static OpType BOOL = new OpType(Collections.<Type> emptyList(),
        new TypeOp(new Name(OTConstants.BOOL)));
    public static Name VAR_A = new Name(OTConstants.VAR_A);
    public static Name VAR_R = new Name(OTConstants.VAR_R);

    public static void main(final String[] args) {
        try {
            new Reader(args[0], null, null);
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public Reader(final String file, final int[] selThms,
        final LogicalSystem logicalSystem) throws FileNotFoundException
    {
        if (selThms != null && selThms.length == 0)
            return;
        int nextThm = 0;
        final Interpreter interp = new Interpreter(logicalSystem);
        final Scanner sc = new Scanner(new File(file));
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
                if (cmd.charAt(0) == '#')
                    System.out.println("Comment: " + cmd);
                else if (cmd.charAt(0) == '"') {
                    final Name n = new Name(cmd.substring(1, cmd.length() - 1));
                    stack.push(n);
//                    System.out.println(n);
                }
                else if (cmd.equals("absTerm")) {
                    final Term b = (Term)stack.pop();
                    final Var x = (Var)stack.pop();
                    final AbsTerm t = new AbsTerm(x, b);
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals("absThm")) {
                    final Thm th = (Thm)stack.pop();
                    final Var v = (Var)stack.pop();
                    assert ((ConstTerm)((AppTerm)((AppTerm)th.t).f).f).c.n
                        .equals(EQUALS);
                    final Term t = ((AppTerm)((AppTerm)th.t).f).x;
                    final Term u = ((AppTerm)th.t).x;

                    thm = new Thm(line, cmd, Arrays.asList(th), th.assum,
                        ConstTerm.eq(new AbsTerm(v, t), new AbsTerm(v, u)));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("assume")) {
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
                else if (cmd.equals("appTerm")) {
                    final Term x = (Term)stack.pop();
                    final Term f = (Term)stack.pop();
                    final OpType ty = (OpType)f.getType();
                    if (!(ty.op.equals(FUN) && ty.args.size() == 2 && ty.args
                        .get(0).equals(x.getType())))
                        throw new IllegalStateException(
                            "function application (" + f + " " + x
                                + ") has mismatched type");
                    final AppTerm t = new AppTerm(f, x);
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals("appThm")) {
                    final Thm t1 = (Thm)stack.pop();
                    final Thm t2 = (Thm)stack.pop();
                    assert ((ConstTerm)((AppTerm)((AppTerm)t1.t).f).f).c.n
                        .equals(EQUALS);
                    assert ((ConstTerm)((AppTerm)((AppTerm)t2.t).f).f).c.n
                        .equals(EQUALS);
                    final Term f = ((AppTerm)((AppTerm)t2.t).f).x;
                    final Term g = ((AppTerm)t2.t).x;
                    final Term x = ((AppTerm)((AppTerm)t1.t).f).x;
                    final Term y = ((AppTerm)t1.t).x;

                    thm = new Thm(line, cmd, Arrays.asList(t1, t2), union(
                        t1.assum, t2.assum), ConstTerm.eq(new AppTerm(f, x),
                        new AppTerm(g, y)));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("axiom")) {
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
                else if (cmd.equals("betaConv")) {
                    final AppTerm t = (AppTerm)stack.pop();
                    final AbsTerm a = (AbsTerm)t.f;
                    thm = new Thm(line, cmd, Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST, ConstTerm.eq(t, a.b
                            .subst(Arrays.asList(
                                (List<List<Object>>)Collections.EMPTY_LIST,
                                Arrays.asList(Arrays.asList(a.x, t.x))))));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("cons")) {
                    final List<Object> old = (List<Object>)stack.pop();
                    final List<Object> l = new ArrayList<Object>();
                    l.add(stack.pop());
                    l.addAll(old);
                    stack.push(l);
//                    System.out.println(l);
                }
                else if (cmd.equals("const")) {
                    final Const c = new Const((Name)stack.pop());
                    stack.push(c);
//                    System.out.println(c);
                }
                else if (cmd.equals("constTerm")) {
                    final ConstTerm t = new ConstTerm((Type)stack.pop(),
                        (Const)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals("deductAntisym")) {
                    final Thm t2 = (Thm)stack.pop();
                    final Thm t1 = (Thm)stack.pop();

                    thm = new Thm(line, cmd, Arrays.asList(t1, t2), union(
                        exclude(t1.assum, t2.t), exclude(t2.assum, t1.t)),
                        ConstTerm.eq(t1.t, t2.t));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("def")) {
                    final Integer i = (Integer)stack.pop();
                    final Object x = stack.peek();
                    dict.put(i, x);
//                    System.out.println(i + " -> " + x);
                }
                else if (cmd.equals("defineConst")) {
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
                        Collections.EMPTY_LIST, ConstTerm.eq(new ConstTerm(ty,
                            c), t));
                    stack.push(c);
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("defineTypeOp")) {
                    final Thm thl = (Thm)stack.pop();
                    final AppTerm t = (AppTerm)thl.t;
                    final List<Name> names = (List<Name>)stack.pop();
                    final Const rep = new Const((Name)stack.pop());
                    final Const abs = new Const((Name)stack.pop());
                    final TypeOp op = new TypeOp((Name)stack.pop());
                    stack.push(op);
                    stack.push(abs);
                    stack.push(rep);
                    if (!t.f.freeVars().isEmpty())
                        throw new IllegalStateException("type definition "
                            + t.f + " has free vars");
                    for (final VarType v : t.getSubTypeVars()) {
                        int i = 0;
                        for (final Name n : names)
                            if (n.equals(v.n))
                                i++;
                        if (i != 1)
                            throw new IllegalStateException(
                                "incorrect type def list");
                    }
                    final List<Type> vars = new ArrayList<Type>();
                    for (final Name n : names)
                        vars.add(new VarType(n));
                    final Type tOld = t.x.getType();
                    final Type tNew = new OpType(vars, op);
                    final ConstTerm abst = new ConstTerm(OpType.to(tOld, tNew),
                        abs);
                    final ConstTerm rept = new ConstTerm(OpType.to(tNew, tOld),
                        rep);

                    final VarTerm dummy = new VarTerm(new Var(tNew, VAR_A));
                    thm = new Thm(line, cmd, Arrays.asList(thl),
                        Collections.EMPTY_LIST, ConstTerm.eq(new AppTerm(abst,
                            new AppTerm(rept, dummy)), dummy));
                    stack.push(thm);

                    final VarTerm dummy2 = new VarTerm(new Var(tOld, VAR_R));
                    thm2 = new Thm(line, cmd + "2", Arrays.asList(thl),
                        Collections.EMPTY_LIST, ConstTerm.eq(new AppTerm(t.f,
                            dummy2), ConstTerm.eq(new AppTerm(rept,
                            new AppTerm(abst, dummy2)), dummy2)));
                    stack.push(thm2);
                    System.out.println(thm);
                    System.out.println(thm2);
                }
                else if (cmd.equals("eqMp")) {
                    if (line == 64411)
                        hashCode();
                    final Thm t1 = (Thm)stack.pop();
                    final Thm t2 = (Thm)stack.pop();
                    assert ((AppTerm)((AppTerm)t2.t).f).f.toString()
                        .equals("=");
                    final Term p = ((AppTerm)((AppTerm)t2.t).f).x;
                    final Term q = ((AppTerm)t2.t).x;
                    thm = new Thm(line, cmd, Arrays.asList(t1, t2), union(
                        t1.assum, t2.assum), q);
                    if (!p.equals(t1.t))
                        throw new IllegalStateException(
                            "terms not alpha-equiv:\n " + t1 + "\n " + t2
                                + "\n" + thm);

                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("nil"))
                    stack.push(new ArrayList<Type>());
//                    System.out.println("[]");
                else if (cmd.equals("opType")) {
                    final OpType t = new OpType((List<Type>)stack.pop(),
                        (TypeOp)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals("pop"))
                    // System.out.println("pop " + stack.pop());
                    stack.pop();
                else if (cmd.equals("ref")) {
                    final Integer i = (Integer)stack.pop();
                    final Object x = dict.get(i);
                    stack.push(x);
//                    System.out.println("dict[" + i + "] = " + x);
                }
                else if (cmd.equals("refl")) {
                    final Term t = (Term)stack.pop();
                    thm = new Thm(line, cmd, Collections.EMPTY_LIST,
                        Collections.EMPTY_LIST, ConstTerm.eq(t, t));
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("remove")) {
                    final Integer i = (Integer)stack.pop();
                    final Object x = dict.remove(i);
                    stack.push(x);
//                    System.out.println("dict[" + i + "] -> " + x);
                }
                else if (cmd.equals("subst")) {
                    final Thm old = (Thm)stack.pop();
                    thm = old
                        .subst(line, (List<List<List<Object>>>)stack.pop());
                    stack.push(thm);
                    System.out.println(thm);
                }
                else if (cmd.equals("thm")) {
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
                else if (cmd.equals("typeOp")) {
                    final TypeOp t = new TypeOp((Name)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals("var")) {
                    final Var v = new Var((Type)stack.pop(), (Name)stack.pop());
                    stack.push(v);
//                    System.out.println(v);
                }
                else if (cmd.equals("varTerm")) {
                    final VarTerm t = new VarTerm((Var)stack.pop());
                    stack.push(t);
//                    System.out.println(t);
                }
                else if (cmd.equals("varType")) {
                    final VarType v = new VarType((Name)stack.pop());
                    stack.push(v);
//                    System.out.println(v);
                }
                else if (cmd.matches("0|[-]?[1-9][0-9]*")) {
                    final Integer i = Integer.valueOf(cmd);
                    stack.push(i);
//                    System.out.println(i);
                }
                else
                    throw new IllegalStateException("unknown: " + cmd);
                if (selThms == null) {
                    interp.addThm(thm);
                    interp.addThm(thm2);
                }
                else if (line == selThms[nextThm]) {
                    interp.addThm(thm);
                    interp.addThm(thm2);
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
