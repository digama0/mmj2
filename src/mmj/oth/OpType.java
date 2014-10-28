package mmj.oth;

import java.util.*;

import mmj.lang.Cnst;
import mmj.lang.ParseNode;

public class OpType extends Type {
    private List<Type> args;
    private TypeOp op;

    private OpType() {}
    public static OpType get(final List<Type> args, final TypeOp op) {
        final OpType o = new OpType();
        o.args = args;
        o.op = op;
        return Type.pool.intern(o).asOp();
    }

    public List<Type> getArgs() {
        return args;
    }
    public TypeOp getOp() {
        return op;
    }

    public static OpType to(final Type a, final Type b) {
        return get(Arrays.asList(a, b), ArtReader.FUN);
    }

    @Override
    public String toString() {
        if (args.isEmpty())
            return op.toString();
        if (args.size() == 2)
            return "(" + args.get(0) + op.toString() + args.get(1) + ")";
        return op.toString() + args;
    }

    @Override
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

    @Override
    public Type subst(final List<List<List<Object>>> subst) {
        final List<Type> l = new ArrayList<Type>();
        for (final Type ty : args)
            l.add(ty.subst(subst));
        return get(l, op);
    }

    @Override
    protected ParseNode generateTypeProof(final Interpreter i) {
        final String name = OTConstants.mapConstants(op.n);
        final Cnst c = i.getConstant(name);
        if (c != null) {
            final ParseNode[] child = new ParseNode[args.size()];
            int j = 0;
            for (final Type t : args)
                child[j++] = t.getTypeProof(i);
            return i.c(i.getTypeTerm(c), child);
        }
        throw new UnsupportedOperationException();
    }
}
