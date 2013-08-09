// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchResultsFieldAttr.java

package mmj.search;

public class SearchResultsFieldAttr {

    public SearchResultsFieldAttr(final int i, final String s, final String s1,
        final int j, final String s2)
    {
        fieldId = i;
        label = s;
        toolTip = s1;
        columns = j;
        defaultText = s2;
    }

    public final int fieldId;
    public final String label;
    public final String toolTip;
    public final int columns;
    public final String defaultText;
}