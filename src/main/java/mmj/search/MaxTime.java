//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * MaxTime.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import mmj.lang.BookManager;
import mmj.lang.Cnst;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.verify.VerifyProofs;

public class MaxTime extends SearchArgsInt {

    public MaxTime() {
        super(30);
    }

    @Override
    public void compile(final CompiledSearchArgs csa,
        final SearchMgr searchMgr, final BookManager bookManager,
        final SearchOutput searchOutput, final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        csa.searchMaxTime = getAndErrorIfNegative(searchOutput);
    }
}
