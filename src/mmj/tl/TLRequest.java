//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TLRequest.java  0.01 08/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.tl;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.pa.ProofWorksheet;

/**
 * Implements a user request for a TheoremLoader operation on a ProofWorksheet.
 * <p>
 */
public abstract class TLRequest {

    /**
     * Constructor for TLRequest
     */
    public TLRequest() {}

    /**
     * Implements a user request for a TheoremLoader operation on a
     * ProofWorksheet.
     * 
     * @param theoremLoader TheoremLoader object.
     * @param proofWorksheet ProofWorksheet object.
     * @param logicalSystem LogicalSystem object.
     * @param messages Messages object.
     * @param proofAsst ProofAsst object.
     * @throws TheoremLoaderException if there are any data errors encountered
     *             while performing the requested function.
     */
    public abstract void doIt(TheoremLoader theoremLoader,
        ProofWorksheet proofWorksheet, LogicalSystem logicalSystem,
        Messages messages, ProofAsst proofAsst) throws TheoremLoaderException;
}
