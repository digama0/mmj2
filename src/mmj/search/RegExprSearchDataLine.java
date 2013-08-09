// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RegExprSearchDataLine.java

package mmj.search;

// Referenced classes of package mmj.search:
//            SearchDataLine, CompiledSearchArgs, SearchDataGetter, SearchOutput, 
//            QuotedSearchTerm

public class RegExprSearchDataLine extends SearchDataLine {

    public RegExprSearchDataLine(final CompiledSearchArgs csa,
        final int i, final SearchDataGetter searchDataGetter)
    {
        super(csa, i, searchDataGetter);
        if (csa.searchOutput.searchReturnCode == 0)
            compileSearchTermTextToRegex(csa);
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
                if (quotedSearchTerm.pattern.matcher(element).matches())
                    return true;

        }
        else
            for (final String element : assrtDataStringArray)
                if (quotedSearchTerm.pattern.matcher(element).find(0))
                    return true;
        return false;
    }
}