package mmj.oth;

import java.util.*;

public class OpType implements Type {
    List<Type> args;
    TypeOp op;

    public OpType(final List<Type> args, final TypeOp op) {
        this.args = args;
        this.op = op;
    }

    public static OpType to(final Type a, final Type b) {
        return new OpType(Arrays.asList(a, b), Reader.FUN);
    }

    @Override
    public String toString() {
        if (args.isEmpty())
            return op.toString();
        if (args.size() == 2)
            return "(" + args.get(0) + op.toString() + args.get(1) + ")";
        return op.toString() + args;
    }

    public Set<VarType> getTypeVars() {
        final Set<VarType> s = new HashSet<VarType>();
        for (final Type t : args)
            s.addAll(t.getTypeVars());
        return s;
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof OpType && args.equals(((OpType)obj).args)
            && op.equals(((OpType)obj).op);
    }

    public Type subst(final List<List<List<Object>>> subst) {
        final List<Type> l = new ArrayList<Type>();
        for (final Type ty : args)
            l.add(ty.subst(subst));
        return new OpType(l, op);
    }
}