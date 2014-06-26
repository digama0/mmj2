package mmj.transforms;

import java.util.List;
import java.util.Map;

import mmj.lang.Assrt;
import mmj.lang.Stmt;

public class ClosureInfo extends DBInfo {
    private EquivalenceInfo eqInfo;

    /**
     * The list of closure lows: A e. CC & B e. CC => (A + B) e. CC
     * <p>
     * It is a map: Statement ( ( A F B ) in the example) -> map : constant
     * elements ( + in the example) -> set of possible properties ( _ e. CC in
     * the example). There could be many properties ( {" _ e. CC" , "_ e. RR" }
     * for example ).
     */
    private Map<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>> closureRuleMap;

    public void initMe(final EquivalenceInfo eqInfo,
        final List<Assrt> assrtList, final TrOutput output, final boolean dbg)
    {
        super.initMe(output, dbg);
        this.eqInfo = eqInfo;

        /*
        closureRuleMap = new HashMap<Stmt, Map<ConstSubst, Map<PropertyTemplate, Assrt>>>();
        for (final Assrt assrt : assrtList)
            findClosureRules(assrt);
            */
    }
}
