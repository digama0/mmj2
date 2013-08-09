// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchResultsData.java

package mmj.search;

import mmj.lang.Assrt;

public class SearchResultsData {

    public SearchResultsData(final String s, final Assrt[] aassrt,
        final String[] as)
    {
        step = s;
        refArray = aassrt;
        selectionArray = as;
    }

    String step;
    Assrt[] refArray;
    String[] selectionArray;
}