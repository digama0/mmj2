//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * TlPreferences.java  0.02 11/01/2011
 *
 * Version 0.01:
 *     - new.
 *
 * Nov-01-2011 - Version 0.02
 *     -->Modified for mmj2 Paths Enhancement
 *        -> added filePath argument to setMMTFolder()
 */

package mmj.tl;

import static mmj.pa.SessionStore.setIntBound;

import java.io.File;

import mmj.lang.LogicalSystem;
import mmj.pa.*;
import mmj.tl.TlConstants.DjVarsOption;

/**
 * Holds user settings/preferences used by the Theorem Loader.
 */
public class TlPreferences {
    private static final String PFX = "TL.";

    /**
     * MMTFolder in use now, which may be pointing to a null File object if not
     * yet specified.
     */
    public Setting<MMTFolder> mmtFolder;

    /** The Dj Vars Option string. */
    public Setting<DjVarsOption> djVarsOption;

    public Setting<Boolean> auditMessages;
    public Setting<Boolean> storeFormulasAsIs;

    public Setting<Integer> storeMMIndentAmt;
    public Setting<Integer> storeMMRightCol;

    private String provableLogicStmtTypeParm;

    /**
     * Constructor for TlPreferences.
     *
     * @param logicalSystem LogicalSystem object.
     * @param store The setting storage
     */
    public TlPreferences(final LogicalSystem logicalSystem,
        final SessionStore store)
    {
        mmtFolder = store.addSetting(PFX + "mmtFolder", new MMTFolder(),
            MMTFolder.serializer(store));

        djVarsOption = store.addSetting(PFX + "djVarsOption",
            TlConstants.THEOREM_LOADER_DJ_VARS_OPTION_DEFAULT);

        auditMessages = store.addSetting(PFX + "auditMessages",
            TlConstants.THEOREM_LOADER_AUDIT_MESSAGES_DEFAULT);

        storeFormulasAsIs = store.addSetting(PFX + "storeFormulasAsIs",
            TlConstants.THEOREM_LOADER_STORE_FORMULAS_ASIS_DEFAULT);

        storeMMIndentAmt = setIntBound(
            store.addSetting(PFX + "storeMMIndentAmt",
                TlConstants.THEOREM_LOADER_STORE_MM_INDENT_AMT_DEFAULT),
            TlConstants.THEOREM_LOADER_STORE_MM_INDENT_AMT_MIN,
            TlConstants.THEOREM_LOADER_STORE_MM_INDENT_AMT_MAX);

        storeMMRightCol = setIntBound(
            store.addSetting(PFX + "storeMMRightCol",
                TlConstants.THEOREM_LOADER_STORE_MM_RIGHT_COL_DEFAULT),
            TlConstants.THEOREM_LOADER_STORE_MM_RIGHT_COL_MIN,
            TlConstants.THEOREM_LOADER_STORE_MM_RIGHT_COL_MAX);

        setProvableLogicStmtTypeParm(
            logicalSystem.getProvableLogicStmtTypeParm());
    }

    /**
     * Get the cached value of the Provable Logic Statement Type string value.
     *
     * @return provableLogicStmtTypeParm.
     */
    public String getProvableLogicStmtTypeParm() {
        return provableLogicStmtTypeParm;
    }

    /**
     * Validate and set the Provable Logic Stmt Type Parm.
     * <p>
     * If valid the provableLogicStmtTypeParm is set. If invalid, no updates are
     * made.
     *
     * @param s Provable Logic Stmt Type Parm string.
     * @return true if Provable Logic Stmt Type Parm. valid, else false.
     */
    public boolean setProvableLogicStmtTypeParm(final String s) {
        if (s == null || s.length() == 0)
            return false; // error

        provableLogicStmtTypeParm = s;

        return true;
    }

    /**
     * Set the MMT Folder using a File object.
     * <p>
     * If valid the mmtFolder is set. If invalid, no updates are made.
     *
     * @param file MMT Folder File object.
     * @return null if no errors, otherwise error message string.
     */
    public MMJException setMMTFolder(final File file) {
        try {
            mmtFolder.setT(new MMTFolder(file));
            return null;
        } catch (final TheoremLoaderException | ProofAsstException e) {
            return e;
        }
    }
}
