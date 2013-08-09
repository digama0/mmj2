// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SearchDataLines.java

package mmj.search;

import mmj.lang.Assrt;

// Referenced classes of package mmj.search:
//            SearchDataGetter, SearchDataLine, CompiledSearchArgs

public class SearchDataLines {

    public SearchDataLines(final CompiledSearchArgs csa) {
        getter = null;
        line = null;
        getter = new SearchDataGetter();
        line = new SearchDataLine[4];
        for (int i = 0; i < line.length; i++)
            if (csa.searchForWhat[i].equals(""))
                line[i] = null;
            else
                line[i] = SearchDataLine.createSearchDataLine(
                    csa, i, getter);

    }

    public boolean evaluate(final Assrt assrt,
        final CompiledSearchArgs csa)
    {
        getter.initForNextSearch(assrt);
        boolean flag = false;
        for (int j = 0; j < line.length; j++) {
            if (line[j] == null)
                continue;
            final int i = line[j].evaluate(csa);
            if (i == 0)
                continue;
            if (i > 0) {
                flag = true;
                if (!line[j].isBoolSetToAnd())
                    break;
                continue;
            }
            flag = false;
            if (line[j].isBoolSetToAnd())
                break;
        }

        return flag;
    }

    SearchDataGetter getter;
    SearchDataLine[] line;
}