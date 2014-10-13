package mmj.oth;

public class TypeOp {
    Name n;

    public TypeOp(final Name n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return n.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof TypeOp && n.equals(((TypeOp)obj).n);
    }
}