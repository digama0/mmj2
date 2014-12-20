package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;
import mmj.lang.VarHyp;

public class Var {
    private static Map<Name, Map<Type, Name>> rename = new HashMap<Name, Map<Type, Name>>();

    Name n;
    Type t;

    public Var(final Type t, final Name n) {
        this.n = n;
        this.t = t;
    }

    public static Var get(final Type t, final Name n) {
        Map<Type, Name> map = rename.get(n);
        if (map == null)
            rename.put(n, map = new HashMap<Type, Name>());
        if (!map.containsKey(n))
            map.put(t, Name.dummy());
        return new Var(t, n);
    }

    public Var subst(final Map<Var, Var> subst) {
        final Var sub = subst.get(this);
        return sub == null ? this : sub;
    }

    @Override
    public int hashCode() {
        return n.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return n.equals(((Var)obj).n) && t.equals(((Var)obj).t);
    }

    @Override
    public String toString() {
        return n.toString();
    }

    public VarHyp toVarHyp(final Interpreter i) {
        final Name r = rename.get(n).get(t);
        final VarHyp s = i.getTermVar(OTConstants.mapTermVar(r, false));
        return s.getTyp().getId().equals(OTConstants.HOL_VAR_CNST) ? s : i
            .getTermVar(OTConstants.mapTermVar(r, true));
    }

    public static Var createFromVarHyp(final Interpreter i, final VarHyp v,
        final ParseNode type)
    {
        return Var.get(Type.createFromParseNode(i, type),
            OTConstants.mapTypeVar(v.getVar().getId()));
    }

    public static Var getFromName(final Set<Var> list, final Name n) {
        for (final Var v : list)
            if (v.n.equals(n))
                return v;
        return null;
    }
}
