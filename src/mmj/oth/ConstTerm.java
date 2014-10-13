package mmj.oth;

import java.util.*;

public class ConstTerm extends Term {
    Const c;
    Type t;

    public ConstTerm(final Type t, final Const c) {
        this.c = c;
        this.t = t;
    }

    public static Term eq(final Type t) {
        return new ConstTerm(OpType.to(t, OpType.to(t, Reader.BOOL)), Reader.EQUALS);
    }

    public static Term eq(final Term a, final Term b) {
        return new AppTerm(new AppTerm(eq(a.getType()), a), b);
    }

    @Override
    public String toString() {
        return c.toString();
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof ConstTerm && c.equals(((ConstTerm)obj).c)
            && t.equals(((ConstTerm)obj).t);
    }

    @Override
    public Set<Var> freeVars() {
        return Collections.emptySet();
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        return t.getTypeVars();
    }

    @Override
    public Type getType() {
        return t;
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        return new ConstTerm(t.subst(subst), c);
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        return strictEquals(t);
    }

    @Override
    Term substPlain(final Map<Var, Var> subst) {
        return this;
    }
}