package mmj.oth;

import java.util.*;

public class VarType implements Type {
    Name n;

    public VarType(final Name n) {
        this.n = n;
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

    public Set<VarType> getTypeVars() {
        final Set<VarType> s = new HashSet<VarType>();
        s.add(this);
        return s;
    }

    public Type subst(final List<List<List<Object>>> subst) {
        for (final List<Object> pr : subst.get(0))
            if (n.equals(pr.get(0)))
                return (Type)pr.get(1);
        return this;
    }
}
