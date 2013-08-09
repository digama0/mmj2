// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   VarHypSubst.java

package mmj.lang;

// Referenced classes of package mmj.lang:
//            VarHyp, ParseNode

public class VarHypSubst {
    public static VarHypSubst END_OF_LIST = new VarHypSubst(null, null);

    public VarHyp targetVarHyp;
    public ParseNode sourceNode;

    public VarHypSubst(final VarHyp varhyp, final ParseNode parsenode) {
        targetVarHyp = varhyp;
        sourceNode = parsenode;
    }
}
