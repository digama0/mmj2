package mmj.setmm;

import static mmj.pa.ErrorCode.of;

import mmj.lang.*;
import mmj.pa.*;
import mmj.pa.MMJException.ErrorContext;

public class SetMMConstants {

    public static final ErrorCode ERRMSG_SYM_NOT_EXISTS = of("E-SM-0001",
        "Typecode '%s' not found.");

    public static final ErrorCode ERRMSG_SYM_NOT_TYPECODE = of("E-SM-0002",
        "Symbol '%s' is not a typecode.");

    public static final ErrorCode ERRMSG_AX_NOT_EXISTS = of("E-SM-0003",
        "Axiom '%s' not found. Perhaps this is not set.mm?");

    public static final ErrorCode ERRMSG_STMT_NOT_AX = of("E-SM-0004",
        "Statement '%s' exists but is not an axiom.");

    public static final ErrorCode ERRMSG_AX_WRONG_TYPE = of("E-SM-0005",
        "Statement '%s' exists but has the wrong type.");

    public static final ErrorCode ERRMSG_AX_HAS_DV = of("E-SM-0006",
        "Syntax axiom '%s' should not have disjoint variable conditions.");

    public static final ErrorCode ERRMSG_NEW_AXIOM = of("E-SM-0101",
        "Syntax axiom '%s' is not associated to a definition. Please add it"
            + " to the FolTranslator.java exception list.");

    public static final ErrorCode ERRMSG_THM_IN_SYNTAX = of("E-SM-0102",
        "Theorem '%s' found in syntax proof! Please use only axioms.");

    public static final ErrorCode ERRMSG_REUSE_SET_IN_DEFN = of("E-SM-0103",
        "Definition %s uses variable %s twice in the same syntax node, which"
            + " is not supported by this algorithm.");

    private final ProofAsst pa;
    public final Cnst SET, CLASS, WFF, DED;
    public final Axiom WN, WI, WB, WA, WO, WTRU, WAL, WEX, CV, WCEQ, WCEL, CAB;

    /**
     * Create an instance of {@code SetMMConstants}. This function checks that
     * all typecodes and syntax axioms it provides exist and have the right
     * type.
     *
     * @param pa
     * @throws SetMMException
     */
    public SetMMConstants(final ProofAsst pa) throws SetMMException {
        this.pa = pa;
        SET = typecode("setvar");
        CLASS = typecode("class");
        WFF = typecode("wff");
        DED = typecode("|-");
        WN = syntax("wn", WFF, WFF);
        WI = syntax("wi", WFF, WFF, WFF);
        WB = syntax("wb", WFF, WFF, WFF);
        WA = syntax("wa", WFF, WFF, WFF);
        WO = syntax("wo", WFF, WFF, WFF);
        WTRU = syntax("wtru", WFF);
        WAL = syntax("wal", WFF, WFF, SET);
        WEX = syntax("wex", WFF, WFF, SET);
        CV = syntax("cv", CLASS, SET);
        WCEQ = syntax("wceq", WFF, CLASS, CLASS);
        WCEL = syntax("wcel", WFF, CLASS, CLASS);
        CAB = syntax("cab", CLASS, WFF, SET);
    }

    private Cnst typecode(final String label) throws SetMMException {
        final Sym cnst = pa.getLogicalSystem().getSymTbl().get(label);
        if (cnst == null)
            throw error(ERRMSG_SYM_NOT_EXISTS, label);
        if (cnst instanceof Cnst)
            return (Cnst)cnst;
        throw error(ERRMSG_SYM_NOT_TYPECODE, label);
    }

    private Axiom syntax(final String label, final Cnst out, final Cnst... in)
        throws SetMMException
    {
        final Stmt ax = pa.getLogicalSystem().getStmtTbl().get(label);
        if (ax == null)
            throw error(ERRMSG_AX_NOT_EXISTS, label);
        if (!(ax instanceof Axiom))
            throw error(ERRMSG_STMT_NOT_AX, label);
        if (ax.getTyp() != out)
            throw error(ERRMSG_AX_WRONG_TYPE, label);
        final VarHyp[] vh = ax.getMandVarHypArray();
        if (vh.length != in.length || ax.getMandHypArrayLength() != in.length)
            throw error(ERRMSG_AX_WRONG_TYPE, label);
        for (int i = 0; i < in.length; i++)
            if (vh[i].getTyp() != in[i])
                throw error(ERRMSG_AX_WRONG_TYPE, label);
        if (((Axiom)ax).getMandFrame().djVarsArray.length != 0)
            throw error(ERRMSG_AX_HAS_DV, label);
        return (Axiom)ax;
    }

    private SetMMException error(final ErrorCode code, final Object... args) {
        return MMJException.addContext(new NotSetMMContext(),
            new SetMMException(code, args));
    }

    public static class NotSetMMContext implements ErrorContext {
        @Override
        public String toString() {
            return "Perhaps this is not set.mm?";
        }

        @Override
        public String append(final String msg) {
            return msg + " " + this;
        }
    }
}
