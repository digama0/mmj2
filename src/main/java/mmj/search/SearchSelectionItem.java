//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * {{file}}.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.Comparator;

import mmj.lang.Assrt;

public class SearchSelectionItem {
    Assrt assrt;
    String[] selection;
    int score;
    int parseTreeDepth = 0;
    int formulaLength = 0;
    int popularity = 0;
    int nbrHyps = 0;
    int mObjSeq = 0;
    String label = "";

    public SearchSelectionItem(final Assrt assrt, final String[] selection,
        final int score)
    {
        this.assrt = assrt;
        this.selection = selection;
        this.score = score;
        if (score != -1) {
            parseTreeDepth = assrt.getExprParseTree().getMaxDepth();
            formulaLength = assrt.getFormula().getCnt();
            popularity = assrt.getNbrProofRefs();
            nbrHyps = assrt.getLogHypArrayLength();
            mObjSeq = assrt.getSeq();
            label = assrt.getLabel();
        }
    }

    @SuppressWarnings("unchecked")
    public static final Comparator<SearchSelectionItem>[] OUTPUT_SORTS = new Comparator[]{
            null, // no sort 0
            // Sort #1: Score(D)/Complexity(D)/Popularity(D)/Nbr Hyps/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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
            },
            // Sort #2: Score(D)/Popularity(D)/Complexity(D)/Nbr Hyps/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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
            },
            // Sort #3: Score(D)/Nbr Hyps/Complexity(D)/Popularity(D)/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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

            },
            // Sort #4: Score(D)/Nbr Hyps/Popularity(D)/Complexity(D)/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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
            },
            // Sort #5: Score(D)/Complexity(D)/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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
            },
            // Sort #6: Score(D)/Popularity(D)/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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

            },
            // Sort #7: Score(D)/Nbr Hyps/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
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
            },
            // Sort #8: Score(D)/Nbr Hyps/MObjSeq
            new Comparator<SearchSelectionItem>() {
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
            },
            // Sort #9: Score(D)/MObjSeq(D)
            new Comparator<SearchSelectionItem>() {
                public int compare(final SearchSelectionItem o1,
                    final SearchSelectionItem o2)
                {
                    final int i = o2.score - o1.score;
                    if (i != 0)
                        return i;
                    else
                        return o2.mObjSeq - o1.mObjSeq;
                }
            },
            // Sort #10: Score(D)/Label
            new Comparator<SearchSelectionItem>() {
                public int compare(final SearchSelectionItem o1,
                    final SearchSelectionItem o2)
                {
                    final int i = o2.score - o1.score;
                    if (i != 0)
                        return i;
                    else
                        return o1.label.compareTo(o2.label);
                }
            }};
}
