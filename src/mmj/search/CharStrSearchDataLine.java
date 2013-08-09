// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CharStrSearchDataLine.java

package mmj.search;

// Referenced classes of package mmj.search:
//            SearchDataLine, CompiledSearchArgs, SearchDataGetter, SearchOutput, 
//            QuotedSearchTerm

public class CharStrSearchDataLine extends SearchDataLine {

    public CharStrSearchDataLine(final CompiledSearchArgs csa,
        final int i, final SearchDataGetter searchDataGetter)
    {
        super(csa, i, searchDataGetter);
        if (csa.searchOutput.searchReturnCode == 0
            && searchPartChoice == 1)
            convertSearchTermTextToLowerCase();
    }

    @Override
    public void loadAssrtData() {
        loadAssrtDataStringArray();
    }

    @Override
    public boolean evaluateSearchTerm(final QuotedSearchTerm quotedSearchTerm,
        final CompiledSearchArgs csa)
    {
        if (searchPartChoice == 2) {
            for (final String element : assrtDataStringArray)
                if (element.equals(quotedSearchTerm.text))
                    return true;

        }
        else
            for (final String element : assrtDataStringArray)
                if (element.contains(quotedSearchTerm.text))
                    return true;
        return false;
    }
}