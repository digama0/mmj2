package mmj.oth;

import java.util.*;

public class Name {
    List<String> ns;
    String s;
    static int dummyNum = 1;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (ns == null ? 0 : ns.hashCode());
        result = prime * result + (s == null ? 0 : s.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        final Name other = (Name)obj;
        return ns.equals(other.ns) && s.equals(other.s);
    }

    public Name(final String str) {
        ns = new ArrayList<String>();
        for (final String s : str.split("(?<!\\\\)\\."))
            ns.add(s.replace("\\.", ".").replace("\\\"", "\"")
                .replace("\\\\", "\\"));
        s = ns.remove(ns.size() - 1);
    }

    public Name(final List<String> ns, final String s) {
        this.ns = ns;
        this.s = s;
    }

    public static Name dummy() {
        return new Name(Arrays.asList("dummy"), "x" + dummyNum++);
    }

    @Override
    public String toString() {
        return s;
    }
}