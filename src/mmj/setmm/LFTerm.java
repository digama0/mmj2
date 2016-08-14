package mmj.setmm;

public interface LFTerm {
    String label();
    String asMMT(int prec);

    public static class LFApply implements LFTerm {
        public final LFTerm left, right;

        public LFApply(final LFTerm left, final LFTerm right) {
            this.left = left;
            this.right = right;
        }

        public String label() {
            return "apply";
        }

        public String asMMT(final int prec) {
            final String s = left.asMMT(1) + " "
                + right.asMMT(right instanceof LFLambda && prec > 1 ? 0 : 2);
            return prec > 1 ? "(" + s + ")" : s;
        }
        @Override
        public String toString() {
            return "apply " + left + " " + right;
        }
    }

    public static class LFLambda implements LFTerm {
        public final LFVar bound;
        public final LFTerm body;

        public LFLambda(final LFVar bound, final LFTerm body) {
            this.bound = bound;
            this.body = body;
        }

        public static LFTerm reducedLambda(final LFVar bound,
            final LFTerm body)
        {
            if (body instanceof LFApply) {
                final LFApply ap = (LFApply)body;
                if (ap.right instanceof LFVar
                    && ((LFVar)ap.right).equals(bound))
                    return ap.left;
            }
            return new LFLambda(bound, body);
        }

        public String label() {
            return "lambda";
        }

        public String asMMT(final int prec) {
            String delim = "[";
            String s = "";
            LFTerm t = this;
            while (t instanceof LFLambda) {
                s += delim + ((LFLambda)t).bound.varDelim() + ":"
                    + ((LFLambda)t).bound.type.asMMT(0);
                t = ((LFLambda)t).body;
                delim = ",";
            }
            s += "] " + t.asMMT(0);
            return prec > 0 ? "(" + s + ")" : s;
        }
        @Override
        public String toString() {
            return "lambda " + bound + " " + body;
        }
    }

    public static class LFVar implements LFTerm {
        public final String var;
        public final LFType type;

        public LFVar(final String var, final LFType type) {
            this.var = var;
            this.type = type;
        }

        public String label() {
            return "var";
        }

        public String asMMT(final int prec) {
            return varDelim();
        }

        public String varDelim() {
            String s = var;
            if (var.charAt(0) == '_')
                s = " " + s;
            if (var.charAt(var.length() - 1) == '_')
                s += " ";
            return s;
        }

        @Override
        public String toString() {
            return var + ": " + type;
        }

        @Override
        public boolean equals(final Object obj) {
            return obj instanceof LFVar && var == ((LFVar)obj).var;
        }
    }

    public static LFConst FORALL = new LFConst("forall");
    public static LFConst EXISTS = new LFConst("exists");
    public static LFConst EQUAL = new LFConst("equal");

    public static class LFConst implements LFTerm {
        public final String label;

        public LFConst(final String label) {
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
