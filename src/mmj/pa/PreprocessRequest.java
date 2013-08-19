//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * PreprocessRequest.java  0.01 03/01/2008
 *
 * Version 0.01:
 * ==> New.
 */

package mmj.pa;

/**
 * PreprocessRequest implements a user request for an editing operation prior to
 * unification on a Proof Worksheet's text area.
 */
public abstract class PreprocessRequest {

    public abstract String doIt(String proofTextArea) throws ProofAsstException;

}
