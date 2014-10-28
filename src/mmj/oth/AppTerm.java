package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;

public class AppTerm extends Term {
    private Term f;
    private Term x;

    private AppTerm() {}
    public static AppTerm get(final Term f, final Term x) {
        final AppTerm t = new AppTerm();
        t.f = f;
        t.x = x;
        return Term.pool.intern(t).asApp();
    }
    public Term getF() {
        return f;
    }
    public Term getX() {
        return x;
    }

    @Override
    public String toString() {
        if (f instanceof AppTerm)
            return "(" + f.asApp().x + " " + f.asApp().f + " " + x + ")";
        return "(" + f + " " + x + ")";
    }

    @Override
    public boolean strictEquals(final Term obj) {
        return obj instanceof AppTerm && f.strictEquals(obj.asApp().f)
            && x.strictEquals(obj.asApp().x);
    }

    @Override
    public Set<Var> freeVars() {
        final HashSet<Var> s = new HashSet<Var>(f.freeVars());
        s.addAll(x.freeVars());
        return s;
    }

    @Override
    public Set<VarType> getSubTypeVars() {
        final HashSet<VarType> s = new HashSet<VarType>(f.getSubTypeVars());
        s.addAll(x.getSubTypeVars());
        return s;
    }

    @Override
    public Type getType() {
        return f.getType().asOp().getArgs().get(1);
    }

    @Override
    public Term subst(final List<List<List<Object>>> subst) {
        return get(f.subst(subst), x.subst(subst));
    }

    @Override
    public Term substPlain(final Map<Var, Var> subst) {
        return get(f.substPlain(subst), x.substPlain(subst));
    }

    @Override
    public boolean alphaEquiv(final Term t, final Map<Var, Var> subst) {
        return t instanceof AppTerm && f.alphaEquiv(t.asApp().f, subst)
            && x.alphaEquiv(t.asApp().x, subst);
    }

    @Override
    protected void generateTypeProof(final Interpreter i) {
        if (f instanceof AppTerm && OTConstants.isBinaryOp(f.asApp().f)) {
            final ConstTerm op = f.asApp().f.asConst();
            final Term x1 = f.asApp().x;
            final ParseNode ft = op.getTermProof(i);
            final ParseNode x1t = x1.getTermProof(i);
            final ParseNode x2t = x.getTermProof(i);
            termProof = i.c(OTConstants.HOL_OV_TERM, ft, x1t, x2t);
            final List<Type> l = f.getType().asOp().getArgs();
            typeProof = i.c(OTConstants.HOL_OV_TYPE,
                x1.getType().getTypeProof(i), l.get(0).getTypeProof(i), l
                    .get(1).getTypeProof(i), ft, x1t, x2t, op.getTypeProof(i),
                x1.getTypeProof(i), x.getTypeProof(i));
        }
        else {
            final ParseNode ft = f.getTermProof(i);
            final ParseNode xt = x.getTermProof(i);
            termProof = i.c(OTConstants.HOL_APP_TERM, ft, xt);
            final List<Type> l = f.getType().asOp().getArgs();
            typeProof = i.c(OTConstants.HOL_APP_TYPE, l.get(0).getTypeProof(i),
                l.get(1).getTypeProof(i), ft, xt, f.getTypeProof(i),
                x.getTypeProof(i));
        }
    }
}
