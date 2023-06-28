//********************************************************************/
//* Copyright (C) 2005, 2006, 2007, 2008                             */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * TheoremLoaderCommitListener.java  0.01 08/01/2008
 *
 * Version 0.01: 08/01/2008
 *     --> new!
 */

package mmj.tl;

/**
 * Interface for Theorem Loader update commits.
 * <p>
 * This is used by the LogicalSystem to keep track of which objects need to be
 * told that the TheoremLoader has just stored a MMTTheoremSet in the
 * LogicalSystem.
 */
public interface TheoremLoaderCommitListener {

    /**
     * mmj Object wishing notification of a TheoremLoader commit.
     * 
     * @param mmtTheoremSet Set of TheoremStmtGroup updates now committed
     */
    void commit(MMTTheoremSet mmtTheoremSet);

}
