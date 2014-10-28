package mmj.oth;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.Stmt;

public class OTConstants {
    public static String EQUALS = "=";
    public static String FUN = "->";
    public static String BOOL = "bool";
    public static String VAR_A = "a";
    public static String VAR_R = "r";

    public static char ART_COMMENT_MARK = '#';
    public static String ART_ABS_TERM = "absTerm";
    public static String ART_ABS_THM = "absThm";
    public static String ART_ASSUME = "assume";
    public static String ART_APP_TERM = "appTerm";
    public static String ART_APP_THM = "appThm";
    public static String ART_AXIOM = "axiom";
    public static String ART_BETA_CONV = "betaConv";
    public static String ART_CONS = "cons";
    public static String ART_CONST = "const";
    public static String ART_CONST_TERM = "constTerm";
    public static String ART_DEDUCT = "deductAntisym";
    public static String ART_DEF = "def";
    public static String ART_DEFINE_CONST = "defineConst";
    public static String ART_DEFINE_TYPE_OP = "defineTypeOp";
    public static String ART_DEFINE_TYPE_OP_2 = "defineTypeOp2";
    public static String ART_EQ_MP = "eqMp";
    public static String ART_NIL = "nil";
    public static String ART_OP_TYPE = "opType";
    public static String ART_POP = "pop";
    public static String ART_REF = "ref";
    public static String ART_REFL = "refl";
    public static String ART_REMOVE = "remove";
    public static String ART_SUBST = "subst";
    public static String ART_THM = "thm";
    public static String ART_TYPE_OP = "typeOp";
    public static String ART_VAR = "var";
    public static String ART_VAR_TERM = "varTerm";
    public static String ART_VAR_TYPE = "varType";
    public static String ART_DIGIT_REGEX = "0|[-]?[1-9][0-9]*";

    public static String HOL_PROOF_CNST = "|-";
    public static String HOL_LOGIC_CNST = "wff";
    public static String HOL_TYPE_CNST = "type";
    public static String HOL_TERM_CNST = "term";
    public static String HOL_VAR_CNST = "var";

    public static String HOL_TERM_PFX = "k";
    public static String HOL_PROOF_TERM = "fp";
    public static String HOL_TYPE_TERM = "ft";
    public static String HOL_CT_TERM = "kct";
    public static String HOL_TRUE_TERM = "kt";
    public static String HOL_APP_TERM = "kc";
    public static String HOL_ABS_TERM = "kl";
    public static String HOL_VAR_TERM = "kv";
    public static String HOL_EQ_TERM = "ke";
    public static String HOL_OV_TERM = "kov";

    public static String HOL_BOOL_TYPE = "hb";
    public static String HOL_FUN_TYPE = "ht";

    public static String HOL_TYPE_PFX = "w";
    public static String HOL_APP_TYPE = "wc";
    public static String HOL_ABS_TYPE = "wl";
    public static String HOL_VAR_TYPE = "wv";
    public static String HOL_OV_TYPE = "wov";
    public static String HOL_CT_TYPE = "wct";

    public static String HOL_THM_JCA = "jca";
    public static String HOL_THM_A1I = "a1i";
    public static String HOL_THM_SYLIB = "sylib";
    public static String HOL_THM_EQTYPRI = "eqtypri";
    public static String HOL_THM_MPBI = "mpbi";
    public static String HOL_THM_EQTRI = "eqtri";
    public static String HOL_THM_DED = "ded";
    public static String HOL_THM_DEDI = "dedi";
    public static String HOL_THM_CEQ12 = "ceq12";
    public static String HOL_THM_COVTRI = "covtri";
    public static String HOL_THM_COVTRRI = "covtrri";
    public static String HOL_THM_LEQ = "leq";
    public static String HOL_THM_BETA = "beta";
    public static String HOL_THM_CL = "cl";
    public static String HOL_THM_ADANTL = "adantl";
    public static String HOL_THM_ADANTR = "adantr";

    public static String HOL_DEF_PFX = "df-";

    private static Map<Name, String> map = new HashMap<Name, String>();
    private static Map<String, Name> reverseMap = new HashMap<String, Name>();
    private static Set<String> binaryOps = new HashSet<String>();
    public static List<String> equalityThms = new ArrayList<String>();
    public static List<String> simpThms = new ArrayList<String>();

    static {
        map.put(new Name("Data.Bool.T"), "T.");
        map.put(new Name("Data.Bool.F"), "F.");
        map.put(new Name("A"), "al");
        map.put(new Name("B"), "be");
        map.put(new Name("select"), "@");

        for (final Entry<Name, String> e : map.entrySet())
            reverseMap.put(e.getValue(), e.getKey());

        binaryOps.add("=");
        binaryOps.add("/\\");
        binaryOps.add("\\/");
        binaryOps.add("==>");

        equalityThms.add("eqid");
        equalityThms.add("ceq1");
        equalityThms.add("ceq2");
        equalityThms.add("ceq12");
        equalityThms.add("oveq");
        equalityThms.add("oveq1");
        equalityThms.add("oveq2");
        equalityThms.add("oveq12");
        equalityThms.add("oveq123");
        equalityThms.add("leq");
        equalityThms.add("cbv");

        simpThms.add("id");
        simpThms.add("trud");
        simpThms.add("simpl");
        simpThms.add("simpr");
        simpThms.add("ct1");
        simpThms.add("ct2");
        simpThms.add("jca");
    }

    public static final String mapConstants(final Name n) {
        final String s = map.get(n);
        return s == null ? n.s : s;
    }

    public static final String mapTypeVar(final Name n) {
        final String s = map.get(n);
        return s == null ? "type_" + n.s : s;
    }

    public static final String mapTermVar(final Name n, final boolean force) {
        final String s = map.get(n);
        return s == null ? force ? "term_" + n.s : n.s : s;
    }

    public static final Name mapTypeVar(final String s) {
        final Name n = reverseMap.get(s);
        return n == null ? new Name(s) : n;
    }

    public static final boolean isBinaryOp(final String id) {
        return binaryOps.contains(id);
    }
    public static final boolean isBinaryOp(final Term t) {
        return t instanceof ConstTerm
            && isBinaryOp(mapConstants(t.asConst().getConst().n));
    }

    public static boolean isForceThm(final Stmt s) {
        return false;
    }
}
