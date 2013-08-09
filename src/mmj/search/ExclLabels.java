// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3)
// Source File Name:   ExclLabels.java

package mmj.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.*;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

// Referenced classes of package mmj.search:
//            SearchArgsTextField, CompiledSearchArgs, SearchMgr, SearchOutput

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
        final String s = get().trim();
        csa.searchExclLabels = s;
        final Pattern pattern = Pattern.compile(",");
        final Pattern pattern1 = Pattern.compile(Pattern.quote("$$"));
        final Pattern pattern2 = Pattern.compile(Pattern.quote("$*"));
        final Pattern pattern3 = Pattern.compile(Pattern.quote("*"));
        final Pattern pattern4 = Pattern.compile(Pattern.quote("$?"));
        final Pattern pattern5 = Pattern.compile(Pattern.quote("?"));
        final Pattern pattern6 = Pattern.compile(Pattern.quote("."));
        final Pattern pattern7 = Pattern.compile("\\s");
        final String[] as = pattern.split(s);
        final List<Pattern> arraylist = new ArrayList<Pattern>(as.length);
        label0: for (int i = 0; i < as.length; i++) {
            final String s1 = as[i].trim();
            if (s1.length() == 0)
                continue;
            final String[] as1 = pattern7.split(s1);
            for (int k = 0; k < as1.length; k++) {
                final String s2 = as1[k].trim();
                if (s2.length() == 0)
                    continue;
                final Matcher matcher = pattern1.matcher(s2);
                if (matcher.find()) {
                    storeArgError(searchOutput, s2,
                        SearchConstants.ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR,
                        "");
                    break label0;
                }
                String s3 = pattern2.matcher(s2).replaceAll("*");
                s3 = pattern4.matcher(s3).replaceAll("?");
                s3 = pattern6.matcher(s3).replaceAll("\\.");
                s3 = pattern3.matcher(s3).replaceAll(".*");
                s3 = pattern5.matcher(s3).replaceAll(".?");
                Pattern pattern8;
                try {
                    pattern8 = Pattern.compile(s3);
                } catch (final PatternSyntaxException patternsyntaxexception) {
                    storeArgError(
                        searchOutput,
                        s3,
                        SearchConstants.ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR2,
                        SearchConstants.ERRMSG_EXCL_LABELS_SPECIFIER_BAD_ERROR2_2
                            + patternsyntaxexception.getMessage());
                    break label0;
                }
                arraylist.add(pattern8);
            }

        }

        final Pattern[] apattern = new Pattern[arraylist.size()];
        for (int j = 0; j < apattern.length; j++)
            apattern[j] = arraylist.get(j);

        csa.searchExclLabelsPattern = apattern;
    }
}