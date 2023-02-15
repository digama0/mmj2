//********************************************************************/
//* Copyright (C) 2007                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MacroBoss.java  0.01 11/13/2015
 *
 * Version 0.01: 11/13/2015
 *     - New.
 */

package mmj.util;

import static mmj.util.UtilConstants.*;

import java.io.File;
import java.io.IOException;

import mmj.pa.SessionStore;

/**
 * Manages access to the WorkVarManager resource and processes Work Var
 * RunParms.
 * <p>
 * Even though WorkVar and related classes are defined in the mmj.lang package,
 * WorkVarBoss is coded as a separate "boss" so that Work Vars can be treated as
 * a separate set of resources, separate from Logical System, Proof Assistant,
 * etc. Initially though, WorkVars and friends are to be used with Proof
 * Assistant -- and nowhere else.
 */
public class StoreBoss extends Boss {

    private SessionStore store;

    private boolean manualLoad;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public StoreBoss(final BatchFramework batchFramework) {
        super(batchFramework);

        putCommand(RUNPARM_CLEAR, () -> {
            store = null;
            manualLoad = false;
            return false; // not "consumed"
        });

        putCommand(RUNPARM_SET_SETTINGS_FILE, this::setSettingsFile);

        putCommand(RUNPARM_DISABLE_SETTINGS, this::disableSettings);

        putCommand(RUNPARM_LOAD_SETTINGS, this::loadSettings);

        putCommand(RUNPARM_SAVE_SETTINGS, this::saveSettings);
    }

    /**
     * Fetches a reference to the SessionStore, first initializing it if
     * necessary.
     *
     * @return SessionStore object ready to go.
     */
    public SessionStore getStore() {
        if (store == null) {
            store = new SessionStore();
            store.setMMJ2Path(batchFramework.paths::getMMJ2Path);
        }
        return store;
    }

    /** Turn off settings collection. */
    protected void disableSettings() {
        getStore().setFile(null);
    }

    /**
     * Validate Settings File RunParm.
     */
    protected void setSettingsFile() {
        final File filePath = batchFramework.paths.getMMJ2Path();
        final String fileNameParm = getFileName(1);

        File file = new File(fileNameParm);
        if (filePath != null && !file.isAbsolute())
            file = new File(filePath, fileNameParm);

        if (file.isDirectory())
            throw error(ERRMSG_NOT_A_FILE, file.getAbsolutePath());

        getStore().setFile(file);
    }

    /**
     * Load the settings file unless we have already been told to do so by a
     * LoadSettings RunParm. Used by RunProofAsstGUI so that settings work
     * "out of the box" without any special RunParms.
     */
    protected void autoload() {
        if (!manualLoad)
            loadSettings();
    }

    /**
     * Validate Load Settings RunParm.
     */
    protected void loadSettings() {
        if (runParm.values.length > 0)
            setSettingsFile();

        try {
            getStore().load(true);
        } catch (final IOException e) {
            throw error(e, ERRMSG_LOAD_FAILED, e.getMessage());
        }

        manualLoad = true;
    }

    /**
     * Validate Save Settings RunParm.
     */
    protected void saveSettings() {
        if (runParm.values.length > 0)
            setSettingsFile();

        try {
            getStore().save();
        } catch (final IOException e) {
            throw error(e, ERRMSG_SAVE_FAILED, e.getMessage());
        }
    }
}
