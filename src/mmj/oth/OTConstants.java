package mmj.oth;

import java.util.*;
import java.util.Map.Entry;

import mmj.lang.Stmt;

public class OTConstants {
    public static final String EQUALS = "=";
    public static final String FUN = "->";
    public static final String BOOL = "bool";
    public static final String VAR_A = "a";
    public static final String VAR_R = "r";

    public static final char ART_COMMENT_MARK = '#';
    public static final String ART_ABS_TERM = "absTerm";
    public static final String ART_ABS_THM = "absThm";
    public static final String ART_ASSUME = "assume";
    public static final String ART_APP_TERM = "appTerm";
    public static final String ART_APP_THM = "appThm";
    public static final String ART_AXIOM = "axiom";
    public static final String ART_BETA_CONV = "betaConv";
    public static final String ART_CONS = "cons";
    public static final String ART_CONST = "const";
    public static final String ART_CONST_TERM = "constTerm";
    public static final String ART_DEDUCT = "deductAntisym";
    public static final String ART_DEF = "def";
    public static final String ART_DEFINE_CONST = "defineConst";
    public static final String ART_DEFINE_TYPE_OP = "defineTypeOp";
    public static final String ART_DEFINE_TYPE_OP_2 = "defineTypeOp2";
    public static final String ART_EQ_MP = "eqMp";
    public static final String ART_NIL = "nil";
    public static final String ART_OP_TYPE = "opType";
    public static final String ART_POP = "pop";
    public static final String ART_REF = "ref";
    public static final String ART_REFL = "refl";
    public static final String ART_REMOVE = "remove";
    public static final String ART_SUBST = "subst";
    public static final String ART_THM = "thm";
    public static final String ART_TYPE_OP = "typeOp";
    public static final String ART_VAR = "var";
    public static final String ART_VAR_TERM = "varTerm";
    public static final String ART_VAR_TYPE = "varType";
    public static final String ART_DIGIT_REGEX = "0|[-]?[1-9][0-9]*";

    public static final String HOL_PROOF_CNST = "|-";
    public static final String HOL_LOGIC_CNST = "wff";
    public static final String HOL_TYPE_CNST = "type";
    public static final String HOL_TERM_CNST = "term";
    public static final String HOL_VAR_CNST = "var";

    public static final String HOL_TERM_PFX = "k";
    public static final String HOL_PROOF_TERM = "fp";
    public static final String HOL_TYPE_TERM = "ft";
    public static final String HOL_CT_TERM = "kct";
    public static final String HOL_TRUE_TERM = "kt";
    public static final String HOL_APP_TERM = "kc";
    public static final String HOL_ABS_TERM = "kl";
    public static final String HOL_VAR_TERM = "kv";
    public static final String HOL_EQ_TERM = "ke";
    public static final String HOL_OV_TERM = "kov";

    public static final String HOL_BOOL_TYPE = "hb";
    public static final String HOL_FUN_TYPE = "ht";

    public static final String HOL_TYPE_PFX = "w";
    public static final String HOL_APP_TYPE = "wc";
    public static final String HOL_ABS_TYPE = "wl";
    public static final String HOL_VAR_TYPE = "wv";
    public static final String HOL_OV_TYPE = "wov";
    public static final String HOL_CT_TYPE = "wct";
    public static final String HOL_THM_WEQI = "weqi";

    public static final String HOL_THM_JCA = "jca";
    public static final String HOL_THM_TRUD = "trud";
    public static final String HOL_THM_A1I = "a1i";
    public static final String HOL_THM_SYL = "syl";
    public static final String HOL_THM_SYLIB = "sylib";
    public static final String HOL_THM_EQTYPRI = "eqtypri";
    public static final String HOL_THM_MPBI = "mpbi";
    public static final String HOL_THM_EQTRI = "eqtri";
    public static final String HOL_THM_DED = "ded";
    public static final String HOL_THM_DEDI = "dedi";
    public static final String HOL_THM_CEQ12 = "ceq12";
    public static final String HOL_THM_COVTRI = "covtri";
    public static final String HOL_THM_COVTRRI = "covtrri";
    public static final String HOL_THM_LEQ = "leq";
    public static final String HOL_THM_LEQF = "leqf";
    public static final String HOL_THM_BETA = "beta";
    public static final String HOL_THM_CL = "cl";
    public static final String HOL_THM_ADANTL = "adantl";
    public static final String HOL_THM_ADANTR = "adantr";
    public static final String HOL_THM_AX4 = "ax4";
    public static final String HOL_THM_AX4G = "ax4g";
    public static final String HOL_THM_CBVD = "cbvd";
    public static final String HOL_THM_CBVDF = "cbvdf";

    public static final String HOL_DEF_PFX = "df-";

    public static final String SET_PROOF_CNST = "|-";
    public static final String SET_WFF_CNST = "wff";
    public static final String SET_SET_CNST = "set";
    public static final String SET_CLS_CNST = "class";

    public static final String SET_EL_WFF = "wcel";
    public static final String SET_EQ_WFF = "wceq";
    public static final String SET_IM_WFF = "wi";
    public static final String SET_PH_VAR = "wph";
    public static final String SET_CV_CLS = "cv";
    public static final String SET_WT_CLS = "cwt";
    public static final String SET_TW_WFF = "wtw";
    public static final String SET_FV_CLS = "cfv";

    public static final String SET_BOOL_TYPE = "twcld";
    public static final String SET_FV_TYPE = "holwc";

    private static Map<Name, String> map = new HashMap<Name, String>();
    private static Map<String, Name> reverseMap = new HashMap<String, Name>();
    public static Map<String, String> termOverrides = new HashMap<String, String>();
    private static Set<String> binaryOps = new HashSet<String>();
    public static List<String> equalityThms = new ArrayList<String>();
    public static List<String> simpThms = new ArrayList<String>();
    public static List<String> hbThms = new ArrayList<String>();
    public static Set<String> avoidThms = new HashSet<String>();
    public static Map<String, int[][]> boundVarOverrides = new HashMap<String, int[][]>();

    static {
        map.put(new Name("Data.Bool.T"), "1o");
        map.put(new Name("Data.Bool.F"), "(/)");
        map.put(new Name("A"), "al");
        map.put(new Name("B"), "be");
        map.put(new Name("select"), "@");
        map.put(new Name("bool"), "2o");

        for (final Entry<Name, String> e : map.entrySet())
            reverseMap.put(e.getValue(), e.getKey());

        termOverrides.put("c1o", "holwtru");

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

        hbThms.add("ax-17");
        hbThms.add("hbl1");
        hbThms.add("hbct");
        hbThms.add("hbc");
        hbThms.add("hbov");
        hbThms.add("hbl");

        avoidThms.add("ax4");

        boundVarOverrides.put("cbv.2", new int[][]{{0, 2}, {0, 3}});
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
        /*if (n.ns.size() == 1 && n.ns.get(0).equals("dummy")
            && n.s.charAt(0) == 'x')
            return "&V" + n.s.substring(1);*/
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
