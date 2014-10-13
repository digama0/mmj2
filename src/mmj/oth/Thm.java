package mmj.oth;

import java.util.*;

public class Thm {
    public final String ref;
    public final List<Thm> hyp;
    public List<Term> assum;
    public Term t;
    public int num;

    public Thm(final int num, final String ref, final List<Thm> hyp,
        final List<Term> assum, final Term t)
    {
        this.ref = ref;
        this.hyp = hyp;
        this.assum = assum;
        this.t = t;
        this.num = num;
    }

    @Override
    public boolean equals(final Object obj) {
        final Thm orig = (Thm)obj;
        final Map<Var, Var> subst = new HashMap<Var, Var>();
        if (assum.size() != orig.assum.size())
            return false;
        final int i = 0;
        for (final Term t2 : assum)
            if (!t2.alphaEquiv(orig.assum.get(i), subst))
                return false;
        return t.alphaEquiv(orig.t, subst);
    }
    public Thm subst(final int num, final List<List<List<Object>>> subst) {
        final List<Term> assum2 = new ArrayList<Term>();
        for (final Term t2 : assum)
            assum2.add(t2.subst(subst));
        final Thm th = new Thm(num, "subst", Arrays.asList(this), assum2,
            t.subst(subst));
        return th.equals(this) ? this : th;
    }
    @Override
    public String toString() {
        String s = "" + num + ":", delim = "";
        for (final Thm h : hyp) {
            s += delim + h.num;
            delim = ",";
        }
        s += ":" + ref;
        while (s.length() <= 30)
            s += " ";
        return s + " " + assum + " |- " + t;
    }
}
