//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * SvcCallback.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     --> new.
 */

package mmj.svc;

import java.io.File;
import java.util.Map;

import mmj.lang.*;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstPreferences;
import mmj.tl.TheoremLoader;
import mmj.tl.TlPreferences;
import mmj.util.*;
import mmj.verify.Grammar;
import mmj.verify.VerifyProofs;

/**
 * Interface for using mmj2 as a service.
 * <p>
 */
public interface SvcCallback {

    /**
     * Method go is called by mmj2 after initialization and a RunParm command is
     * encountered triggering a SvcCallback.
     * <p>
     * The "mmj2 Service" feature adds a new "svc" package to mmj2 and provides
     * a foundation for other, non-mmj2 code to access Metamath data and mmj2
     * facilities. The primary use envisioned is extracting Metamath data, as
     * the mmj2 Service feature provides easy access to the critical ingredient:
     * formula parse trees!
     * <p>
     * Access to mmj2 facilities is made available for "caller" and "callee"
     * programs. In both scenarios the BatchMMJ2 program is used to initialize
     * mmj2 with a loaded .mm file which is validated, parsed, etc. Callers
     * actually call BatchMMJ2 passing a "SvcCallback" object which is called by
     * mmj2 once initialization is complete (when the "SvcCall" RunParm is
     * processed.) "Callee" programs simply write a class which implements the
     * SvcCallback interface -- just as "caller" programs do -- but instead of
     * directly calling BatchMMJ2, the user specifies the name of their
     * SvcCallback- implementing class via a "SvcCallback" RunParm.
     * <p>
     * Whether accessing mmj2 as a "caller" or "callee", complete access to
     * mmj2's main facilities is provided in the SvcCallback.go interface
     * method. The "go()" method is passed references to the biggie mmj2
     * objects, including ProofAsst, Grammar, VerifyProofs, etc.
     * <p>
     * Within the SvcCallback.go() method the user-code can execute calls to
     * mmj2 methods but must single-thread the accesses as mmj2 is not, in
     * general, written for total multi- threaded access throughout (some code
     * could be multi-threaded but not all.)
     * <p>
     * When finished accessing the mmj2 Services, the user-code simply needs to
     * execute a "return" from the SvcCallback.go() method.
     * <p>
     * New RunParms added to mmj2 for use with BatchMMJ2 (see mmj.util.SvcBoss)
     * are SvcFolder, SvcCallbackClass, SvcArg, and SvcCall. The SrvArg RunParm
     * provides a way for the user-code to obtain Key-Value parameter pairs via
     * the input RunParms file; they are loaded by BatchMMJ2 (with minimal
     * validation) into a HashMap and passed via the SvcCallback.go() method.
     * <p>
     * More information about these new RunParms is provided in the
     * mmj2jar\AnnotatedRunParms.txt documentation file.
     * <p>
     * Review sample code and (w/"compile and go" .bat jobs) in
     * 
     * <pre>
     *     doc\mmj2Service
     * </pre>
     * 
     * Sample "callee" and "caller" mode programs are at:
     * 
     * <pre>
     *     \doc\mmj2Service\TSvcCallbackCallee.java
     *     \doc\mmj2Service\TSvcCallbackCaller.java
     * </pre>
     * 
     * Note: In "callee" mode, if a "fatal" or severe error is encountered,
     * triggering an IllegalArgumentException will instantly terminate the mmj2
     * process.
     * <p>
     * Likewise, in "caller" mode "fatal" errors can result during start-up,
     * typically resulting in IllegalArgumentException errors being thrown, and
     * then caught by {@link BatchFramework} and resulting ultimately in a
     * non-zero return-code from
     * {@linkplain BatchMMJ2#generateSvcCallback(String[],SvcCallback)}. So...if
     * the "caller" mode program gets a non-zero returncode from the
     * BatchMMJ2.generateSvcCallback() call then that means that either a) mmj2
     * had a "fatal" error during start-up, or b) that the SvcCallback was
     * triggered, and one of the user programs threw an exception, such as
     * IllegalArgumentException, which flowed back to mmj2 and caused the
     * non-zero return-code.
     * 
     * @param messages mmj.lang.Messages object.
     * @param outputBoss mmj.util.OutputBoss
     * @param logicalSystem mmj.lang.LogicalSystem
     * @param verifyProofs mmj.verify.VerifyProofs
     * @param grammar mmj.verify.Grammar
     * @param workVarManager mmj.lang.WorkVarManager
     * @param proofAsstPreferences mmj.pa.ProofAsstPreferences
     * @param proofAsst mmj.pa.ProofAsst
     * @param tlPreferences mmj.tl.TlPreferences
     * @param theoremLoader mmj.tl.TheoremLoader
     * @param svcFolder home Folder for use by Svc (specified via RunParm).
     * @param svcArgs Map of Svc key/value pair arguments (specified via
     *            RunParm).
     */
    void go(Messages messages, OutputBoss outputBoss,
        LogicalSystem logicalSystem, VerifyProofs verifyProofs,
        Grammar grammar, WorkVarManager workVarManager,
        ProofAsstPreferences proofAsstPreferences, ProofAsst proofAsst,
        TlPreferences tlPreferences, TheoremLoader theoremLoader,
        File svcFolder, Map<String, String> svcArgs);
}
