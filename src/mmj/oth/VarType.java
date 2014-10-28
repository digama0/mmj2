package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;

public class VarType extends Type {
    private Name n;

    private VarType() {}
    public static VarType get(final Name n) {
        final VarType v = new VarType();
        v.n = n;
        return Type.pool.intern(v).asVar();
    }

    public Name getName() {
        return n;
    }

    @Override
    public String toString() {
        return n.toString();
    }

    @Override
    public int hashCode() {
        return n.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof VarType && n.equals(((VarType)obj).n);
    }

    @Override
    public Set<VarType> getTypeVars() {
        final Set<VarType> s = new HashSet<VarType>();
        s.add(this);
        return s;
    }

    @Override
    public Type subst(final List<List<List<Object>>> subst) {
        for (final List<Object> pr : subst.get(0))
            if (n.equals(pr.get(0)))
                return (Type)pr.get(1);
        return this;
    }

    @Override
    protected ParseNode generateTypeProof(final Interpreter i) {
        final String name = OTConstants.mapTypeVar(n);
        final mmj.lang.Var v = (mmj.lang.Var)i.getLogicalSystem().getSymTbl()
            .get(name);
        if (v != null)
            return i.c(v.getActiveVarHyp());
        else
            throw new UnsupportedOperationException();
    }
}
