// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchError.java

package mmj.search;

// Referenced classes of package mmj.search:
//            SearchMgr

public class SearchError {

    public SearchError() {
        returnCode = 0;
        searchArgFieldId = -1;
        message = "";
    }

    public SearchError(final int i, final int j, final String s) {
        returnCode = i;
        searchArgFieldId = j;
        message = SearchMgr.reformatMessage(s);
    }

    int returnCode;
    int searchArgFieldId;
    String message;
}