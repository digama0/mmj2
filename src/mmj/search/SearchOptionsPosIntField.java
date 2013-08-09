// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchOptionsPosIntField.java

package mmj.search;

// Referenced classes of package mmj.search:
//            SearchOptionsJTextField, SearchOptionsScrnMapField, SearchOptionsConstants, SearchOptionsFieldAttr

public abstract class SearchOptionsPosIntField extends SearchOptionsJTextField
    implements SearchOptionsScrnMapField
{

    public SearchOptionsPosIntField(final int i, final String s, final int j) {
        super(i, s, j);
    }

    public SearchOptionsPosIntField(final int i) {
        this(i, SearchOptionsConstants.FIELD_ATTR[i].defaultText,
            SearchOptionsConstants.FIELD_ATTR[i].columns);
    }
}