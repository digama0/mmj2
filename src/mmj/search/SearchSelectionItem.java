// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   SearchSelectionItem.java

package mmj.search;

import java.util.Comparator;

import mmj.lang.Assrt;

public class SearchSelectionItem {

    public SearchSelectionItem(final Assrt assrt1, final String[] as,
        final int i)
    {
        score = 0;
        parseTreeDepth = 0;
        formulaLength = 0;
        popularity = 0;
        nbrHyps = 0;
        mObjSeq = 0;
        label = "";
        assrt = assrt1;
        selection = as;
        score = i;
        if (i != -1) {
            parseTreeDepth = assrt1.getExprParseTree().getMaxDepth();
            formulaLength = assrt1.getFormula().getCnt();
            popularity = assrt1.getNbrProofRefs();
            nbrHyps = assrt1.getLogHypArrayLength();
            mObjSeq = assrt1.getSeq();
            label = assrt1.getLabel();
        }
    }

    Assrt assrt;
    String[] selection;
    int score;
    int parseTreeDepth;
    int formulaLength;
    int popularity;
    int nbrHyps;
    int mObjSeq;
    String label;
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_1 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o2.parseTreeDepth - o1.parseTreeDepth;
            if (i != 0)
                return i;
            i = o2.formulaLength - o1.formulaLength;
            if (i != 0)
                return i;
            i = o2.popularity - o1.popularity;
            if (i != 0)
                return i;
            i = o1.nbrHyps - o2.nbrHyps;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_2 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o2.popularity - o1.popularity;
            if (i != 0)
                return i;
            i = o2.parseTreeDepth - o1.parseTreeDepth;
            if (i != 0)
                return i;
            i = o2.formulaLength - o1.formulaLength;
            if (i != 0)
                return i;
            i = o1.nbrHyps - o2.nbrHyps;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_3 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o1.nbrHyps - o2.nbrHyps;
            if (i != 0)
                return i;
            i = o2.parseTreeDepth - o1.parseTreeDepth;
            if (i != 0)
                return i;
            i = o2.formulaLength - o1.formulaLength;
            if (i != 0)
                return i;
            i = o2.popularity - o1.popularity;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_4 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o1.nbrHyps - o2.nbrHyps;
            if (i != 0)
                return i;
            i = o2.popularity - o1.popularity;
            if (i != 0)
                return i;
            i = o2.parseTreeDepth - o1.parseTreeDepth;
            if (i != 0)
                return i;
            i = o2.formulaLength - o1.formulaLength;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_5 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o2.parseTreeDepth - o1.parseTreeDepth;
            if (i != 0)
                return i;
            i = o2.formulaLength - o1.formulaLength;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_6 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o2.popularity - o1.popularity;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_7 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o1.nbrHyps - o2.nbrHyps;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_8 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            int i = o2.score - o1.score;
            if (i != 0)
                return i;
            i = o1.nbrHyps - o2.nbrHyps;
            if (i != 0)
                return i;
            else
                return o1.mObjSeq - o2.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_9 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            final int i = o2.score - o1.score;
            if (i != 0)
                return i;
            else
                return o2.mObjSeq - o1.mObjSeq;
        }

    };
    public static final Comparator<SearchSelectionItem> OUTPUT_SORT_10 = new Comparator<SearchSelectionItem>()
    {
        public int compare(final SearchSelectionItem o1,
            final SearchSelectionItem o2)
        {
            final int i = o2.score - o1.score;
            if (i != 0)
                return i;
            else
                return o1.label.compareTo(o2.label);
        }

    };

}