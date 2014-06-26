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
    public final EquivalenceInfo eqInfo = new EquivalenceInfo();

    public final ImplicationInfo implInfo = new ImplicationInfo();

    /** The information about replace rules */
    public final ReplaceInfo replInfo = new ReplaceInfo();

    /** The information about closure rules */
    public final ClosureInfo clInfo = new ClosureInfo();

    public final AssociativeInfo assocInfo = new AssociativeInfo();

    public final CommutativeInfo comInfo = new CommutativeInfo();

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

    /**
     * This function is needed for debug
     * 
     * @param node the input node
     * @return the corresponding formula
     */
    protected Formula getFormula(final ParseNode node) {
        final ParseTree tree = new ParseTree(node);
        final Formula generatedFormula = verifyProofs.convertRPNToFormula(
            tree.convertToRPN(), "tree"); // TODO: use constant
        return generatedFormula;
    }
}
