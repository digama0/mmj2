//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * ExclLabels.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

public class ExclLabels extends SearchArgsTextField {

    public ExclLabels() {
        super(27);
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        final String text = get().trim();
        csa.searchExclLabels = text;
        final Pattern comma = Pattern.compile(",");
        final Pattern dollar = Pattern.compile(Pattern.quote("$$"));
        final Pattern asterisk = Pattern.compile(Pattern.quote("$*"));
        final Pattern asteriskShort = Pattern.compile(Pattern.quote("*"));
        final Pattern question = Pattern.compile(Pattern.quote("$?"));
        final Pattern questionShort = Pattern.compile(Pattern.quote("?"));
        final Pattern dot = Pattern.compile(Pattern.quote("."));
        final Pattern whitespace = Pattern.compile("\\s");
        final String[] labels = comma.split(text);
        final List<Pattern> patterns = new ArrayList<>(labels.length);
        loopI: for (int i = 0; i < labels.length; i++) {
            final String label = labels[i].trim();
            if (label.length() == 0)
                continue;
            final String[] words = whitespace.split(label);
            for (int k = 0; k < words.length; k++) {
                String word = words[k].trim();
                if (word.length() == 0)
                    continue;
                final Matcher m = dollar.matcher(word);
                if (m.find()) {
                    storeArgError(searchOutput, word,
                        SearchConstants.ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR,
                        "");
                    break loopI;
                }
                word = asterisk.matcher(word).replaceAll("*");
                word = question.matcher(word).replaceAll("?");
                word = dot.matcher(word).replaceAll("\\.");
                word = asteriskShort.matcher(word).replaceAll(".*");
                word = questionShort.matcher(word).replaceAll(".?");
                try {
                    patterns.add(Pattern.compile(word));
                } catch (final PatternSyntaxException e) {
                    storeArgError(
                        searchOutput,
                        word,
                        SearchConstants.ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR2,
                        SearchConstants.ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR2_2
                            + e.getMessage());
                    break loopI;
                }
            }

        }

        csa.searchExclLabelsPattern = patterns.toArray(new Pattern[patterns
            .size()]);
    }
}
