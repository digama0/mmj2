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

import java.io.*;
import java.util.List;

import javax.script.*;

import mmj.lang.LangException;
import mmj.lang.VerifyException;

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
public class MacroBoss extends Boss {

    private File macroFolder;
    private String macroExtension;
    private ScriptEngineFactory factory;
    private ScriptEngine engine;
    private FileReader prepMacro;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public MacroBoss(final BatchFramework batchFramework) {
        super(batchFramework);
    }

    /**
     * Executes a single command from the RunParmFile.
     *
     * @param runParm the RunParmFile line to execute.
     * @return boolean "consumed" indicating that the input runParm should not
     *         be processed again.
     */
    @Override
    public boolean doRunParmCommand(final RunParmArrayEntry runParm)
        throws IllegalArgumentException, VerifyException
    {

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_CLEAR.name()) == 0)
        {
            macroFolder = null;
            return false; // not "consumed"
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_MACRO_FOLDER.name()) == 0)
        {
            setMacroFolder(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_MACRO_LANGUAGE.name()) == 0)
        {
            setMacroLanguage(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_RUN_MACRO_INIT.name()) == 0)
        {
            runMacroInit(runParm);
            return true;
        }

        if (runParm.name.compareToIgnoreCase(
            UtilConstants.RUNPARM_PREP_MACRO.name()) == 0)
        {
            setPrepMacro(runParm);
            return true;
        }

        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_RUN_MACRO.name()) == 0)
        {
            runMacro(runParm);
            return true;
        }

        return false;
    }

    /**
     * Validate Macro Folder RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws IllegalArgumentException if an error occurred
     */
    protected void setMacroFolder(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        macroFolder = editExistingFolderRunParm(
            batchFramework.paths.getMMJ2Path(), runParm,
            UtilConstants.RUNPARM_MACRO_FOLDER.name(), 1);
    }

    /**
     * Validate Macro Language RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws IllegalArgumentException if an error occurred
     */
    protected void setMacroLanguage(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_MACRO_LANGUAGE.name(), 2);
        setMacroLanguage(runParm.values[0], runParm.values[1]);
    }

    protected void setMacroLanguage(final String language,
        final String extension)
    {
        setMacroLanguage(language, extension, LangException
            .format(UtilConstants.ERRMSG_MACRO_LANGUAGE_MISSING_1, language));
    }

    private void setMacroLanguage(final String language, final String extension,
        String errMsg)
    {
        final List<ScriptEngineFactory> engineFactories = new ScriptEngineManager()
            .getEngineFactories();
        for (final ScriptEngineFactory f : engineFactories) {
            final List<String> names = f.getNames();
            if (names.contains(language)) {
                factory = f;
                macroExtension = extension;
                return;
            }
            errMsg += LangException.format(
                UtilConstants.ERRMSG_MACRO_LANGUAGE_MISSING_2,
                f.getEngineName(), names);
        }
        throw new IllegalArgumentException(errMsg);
    }

    /**
     * Validate RunMacro RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     * @throws IllegalArgumentException if an error occurred
     */
    protected void runMacroInit(final RunParmArrayEntry runParm)
        throws VerifyException, IllegalArgumentException
    {
        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_RUN_MACRO.name(),
            1);
        getEngine(runParm.values[0]);
    }

    /**
     * Validate Prep Macro RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws IllegalArgumentException if an error occurred
     */
    protected void setPrepMacro(final RunParmArrayEntry runParm)
        throws IllegalArgumentException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_PREP_MACRO.name(), 1);
        try {
            prepMacro = getMacroReader(runParm.values[0]);
        } catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(
                UtilConstants.ERRMSG_PREP_MACRO_DOES_NOT_EXIST);
        }
    }

    /**
     * Validate RunMacro RunParm.
     *
     * @param runParm run parm parsed into RunParmArrayEntry object
     * @throws VerifyException if an error occurred
     * @throws IllegalArgumentException if an error occurred
     */
    protected void runMacro(final RunParmArrayEntry runParm)
        throws VerifyException, IllegalArgumentException
    {
        editRunParmValuesLength(runParm, UtilConstants.RUNPARM_RUN_MACRO.name(),
            1);
        getEngine();
        try {
            engine.put("args", runParm.values);
            if (prepMacro == null)
                try {
                    prepMacro = getMacroReader(
                        UtilConstants.RUNPARM_OPTION_PREP_MACRO);
                } catch (final FileNotFoundException e) {}
            if (prepMacro != null)
                engine.eval(prepMacro);
            runMacro(runParm.values[0]);
        } catch (final ScriptException e) {
            throw new IllegalArgumentException("Error in PrepMacro", e);
        }
    }

    /**
     * Run a macro with the given name.
     *
     * @param name Name of the macro, looked up in macros/ folder
     * @throws IllegalArgumentException if an error occurred
     */
    public void runMacro(final String name) throws IllegalArgumentException {
        try {
            engine.eval(getMacroReader(name));
        } catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (final ScriptException e) {
            throw new IllegalArgumentException(
                "Error in macro " + name + "\n" + e.getMessage(), e);
        }
    }

    public ScriptEngine getEngine() {
        return getEngine(UtilConstants.RUNPARM_OPTION_INIT_MACRO);
    }
    public ScriptEngine getEngine(final String initMacro) {
        if (engine == null) {
            if (factory == null)
                setMacroLanguage(UtilConstants.RUNPARM_OPTION_MACRO_LANGUAGE,
                    UtilConstants.RUNPARM_OPTION_MACRO_EXTENSION,
                    LangException.format(
                        UtilConstants.ERRMSG_MACRO_LANGUAGE_DEFAULT_MISSING_1,
                        UtilConstants.RUNPARM_OPTION_MACRO_LANGUAGE));
            engine = factory.getScriptEngine();
            try {
                engine.put("batchFramework", batchFramework);
                engine.eval(getMacroReader(initMacro));
            } catch (final FileNotFoundException e) {

            } catch (final ScriptException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return engine;
    }

    private FileReader getMacroReader(String fileNameIn)
        throws FileNotFoundException
    {
        final File filePath = macroFolder == null
            ? batchFramework.paths.getMMJ2Path() : macroFolder;
        if (macroExtension != null && !macroExtension.isEmpty()) {
            final int index = fileNameIn.lastIndexOf('.');
            if (index == -1)
                fileNameIn = fileNameIn + "." + macroExtension;
        }
        File f = new File(fileNameIn);
        if (filePath != null && !f.isAbsolute())
            f = new File(filePath, fileNameIn);
        return new FileReader(f);
    }
}
