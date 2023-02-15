package mmj.setmm;

import mmj.setmm.LFTerm.LFVar;

public interface LFType {
    String label();
    String asMMT(int prec);

    public static class LFArrow implements LFType {
        public final LFType left, right;

        public LFArrow(final LFType left, final LFType right) {
            this.left = left;
            this.right = right;
        }

        public String label() {
            return "arrow";
        }

        public String asMMT(final int prec) {
            final String s = left.asMMT(2) + " \u2192 " + right.asMMT(1);
            // final String s = left.asMMT(2) + " \u00e2\u0086\u0092 " +
            // right.asMMT(1);
            // final String s = left.asMMT(2) + " -> " + right.asMMT(1);
            return prec > 1 ? "(" + s + ")" : s;
        }
        @Override
        public String toString() {
            return "arrow " + left + " " + right;
        }
    }

    public static class LFPi implements LFType {
        public final LFVar bound;
        public final LFType body;

        public LFPi(final LFVar bound, final LFType body) {
            this.bound = bound;
            this.body = body;
        }

        public String label() {
            return "lambda";
        }

        public String asMMT(final int prec) {
            String delim = "{";
            String s = "";
            LFType t = this;
            while (t instanceof LFPi) {
                s += delim + ((LFPi)t).bound.varDelim() + ":"
                    + ((LFPi)t).bound.type.asMMT(0);
                t = ((LFPi)t).body;
                delim = ",";
            }
            s += "} " + t.asMMT(0);
            return prec > 0 ? "(" + s + ")" : s;
        }
        @Override
        public String toString() {
            return "lambda " + bound + " " + body;
        }
    }

    public static class LFDed implements LFType {
        public final LFTerm expr;

        public LFDed(final LFTerm expr) {
            this.expr = expr;
        }

        public String label() {
            return "ded";
        }

        public String asMMT(final int prec) {
            // return "\u22A2 " + expr.asMMT();
            return "ded " + expr.asMMT(0);
        }
        @Override
        public String toString() {
            return "ded " + expr;
        }
    }

    public static LFType SET = new LFTypeConst("term");
    public static LFType PROP = new LFTypeConst("prop");
    public static LFType CLASS = new LFTypeConst("class");
    // new LFArrow(SET, PROP);

    public static class LFTypeConst implements LFType {
        public final String label;

        public LFTypeConst(final String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }

        public String asMMT(final int prec) {
            return label;
        }
        @Override
        public String toString() {
            return label;
        }
    }
}
