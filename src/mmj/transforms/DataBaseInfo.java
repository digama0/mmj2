package mmj.transforms;

import java.util.List;

import mmj.lang.*;
import mmj.verify.VerifyProofs;

public class DataBaseInfo {

    /** This field is true if this object was initialized */
    private boolean isInit = false;

    private final boolean dbg = true;

    protected TrOutput output;

    /** It is necessary for formula construction */
    VerifyProofs verifyProofs;

    /** The information about equivalence rules */
    protected EquivalenceInfo eqInfo = new EquivalenceInfo();

    protected ImplicationInfo implInfo = new ImplicationInfo();

    /** The information about replace rules */
    protected ReplaceInfo replInfo = new ReplaceInfo();

    /** The information about closure rules */
    protected ClosureInfo clInfo = new ClosureInfo();

    protected AssociativeInfo assocInfo = new AssociativeInfo();

    protected CommutativeInfo comInfo = new CommutativeInfo();

    /** The symbol like |- in set.mm */
    protected Cnst provableLogicStmtTyp;

    /** Empty default constructor */
    public DataBaseInfo() {}

    // ----------------------------

    public void prepareAutomaticTransformations(final List<Assrt> assrtList,
        final Cnst provableLogicStmtTyp, final Messages messages,
        final VerifyProofs verifyProofs)
    {
        isInit = true;
        output = new TrOutput(messages);
        this.verifyProofs = verifyProofs;
        this.provableLogicStmtTyp = provableLogicStmtTyp;

        eqInfo.initMe(assrtList, output, dbg);

        clInfo.initMe(assrtList, output, dbg);

        implInfo.initMe(eqInfo, assrtList, output, dbg);

        replInfo.initMe(eqInfo, assrtList, output, dbg);

        assocInfo.initMe(eqInfo, clInfo, replInfo, assrtList, output, dbg);

        comInfo.initMe(eqInfo, assrtList, output, dbg);
    }

    public boolean isInit() {
        return isInit;
    }
}
