package mmj.oth;

public class Const {
    Name n;

    public Const(final Name n) {
        this.n = n;
    }

    @Override
    public String toString() {
        return n.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        return n.equals(((Const)obj).n);
    }
}