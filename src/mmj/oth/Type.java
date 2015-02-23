package mmj.oth;

import java.util.*;

import mmj.lang.ParseNode;
import mmj.lang.VarHyp;
import mmj.util.InternPool;

public abstract class Type {
    protected static InternPool<Type> pool = new InternPool<Type>();

    public OpType asOp() {
        return (OpType)this;
    }
    public VarType asConst() {
        return (VarType)this;
    }
    public VarType asVar() {
        return (VarType)this;
    }

    public abstract Set<VarType> getTypeVars();

    private ParseNode proof = null;

    public ParseNode getTypeProof(final ProofContext p) {
        return proof == null ? proof = generateTypeProof(p) : proof;
    }
    protected abstract ParseNode generateTypeProof(final ProofContext p);

    public abstract Type subst(List<List<List<Object>>> subst);
    public static Type createFromParseNode(final ProofContext p,
        final ParseNode type)
    {
        if (type == null)
            throw new UnsupportedOperationException("Type inference failed");
        if (type.stmt.getLabel().equals(OTConstants.HOL_FUN_TYPE))
            return OpType.get(Arrays.asList(
                createFromParseNode(p, type.child[0]),
                createFromParseNode(p, type.child[1])),
                new TypeOp(OTConstants.mapTypeVar(OTConstants.FUN)));
        if (type.stmt instanceof VarHyp)
            return VarType.get(OTConstants.mapTypeVar(((VarHyp)type.stmt)
                .getVar().getId()));
        if (type.stmt.getFormula().getCnt() == 2)
            return OpType.get(
                Collections.<Type> emptyList(),
                new TypeOp(OTConstants.mapTypeVar(type.stmt.getFormula()
                    .getSym()[1].getId())));
        throw new UnsupportedOperationException("Unknown type constructor");
    }
}
