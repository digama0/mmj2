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

package mmj.pa;

import java.io.*;
import java.util.*;

import javax.script.*;

import mmj.lang.LangException;
import mmj.util.BatchFramework;
import mmj.util.UtilConstants;

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
public class MacroManager {

    private File macroFolder;
    private String macroExtension;
    private ScriptEngineFactory factory;
    private ScriptEngine engine;
    private File prepMacro;
    private final BatchFramework batchFramework;
    private final Map<CallbackType, Runnable> callbacks;

    /**
     * Constructor with BatchFramework for access to environment.
     *
     * @param batchFramework for access to environment.
     */
    public MacroManager(final BatchFramework batchFramework) {
        this.batchFramework = batchFramework;
        callbacks = new HashMap<CallbackType, Runnable>();
    }

    /**
     * Sets the folder in which to find macros.
     *
     * @param folder the folder
     */
    public void setMacroFolder(final File folder) {
        macroFolder = folder;
    }

    public void setMacroLanguage(final String language,
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
     * Set a hook before RunMacro invocations.
     *
     * @param prep The macro to run before every call to RunMacro
     * @throws FileNotFoundException if the macro could not be found
     */
    public void setPrepMacro(final String prep) throws FileNotFoundException {
        prepMacro = getMacroFile(prep);
    }

    /**
     * Get a named variable's value.
     *
     * @param key the variable name
     * @return The variable value
     */
    public Object get(final String key) {
        return getEngine().get(key);
    }

    /**
     * Set a named variable's value.
     *
     * @param key the variable name
     * @param value The variable value
     */
    public void set(final String key, final Object value) {
        getEngine().put(key, value);
    }

    /**
     * Run a macro given an argument list.
     *
     * @param mode The method in which this macro is getting called (for
     *            distinguishing which variables are set)
     * @param args The argument list; {@code args[0]} is the macro name.
     * @throws IllegalArgumentException if there are any errors
     */
    public synchronized void runMacro(final ExecutionMode mode,
        final String[] args) throws IllegalArgumentException
    {
        getEngine();
        try {
            set("executionMode", mode);
            set("args", args);
            if (prepMacro == null)
                try {
                    prepMacro = getMacroFile(
                        UtilConstants.RUNPARM_OPTION_PREP_MACRO);
                } catch (final FileNotFoundException e) {}
            if (prepMacro != null)
                engine.eval(new FileReader(prepMacro));
            runMacroRaw(args[0]);
        } catch (final ScriptException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                "Error in PrepMacro:\n" + e.getMessage(), e);
        } catch (final FileNotFoundException e) {}
    }

    /**
     * Sets the callback for a given type of event. This function is intended to
     * be called by macros. Only one callback is supported for each type, so
     * macros should handle the organization of multiple event handlers.
     *
     * @param c The type of callback (event trigger)
     * @param r An interface to override with a (javascript) function
     */
    public void setCallback(final CallbackType c, final Runnable r) {
        callbacks.put(c, r);
    }

    /**
     * Run a macro callback.
     *
     * @param c The type of callback (event trigger)
     */
    public synchronized void runCallback(final CallbackType c) {
        final Runnable r = callbacks.get(c);
        if (r != null)
            try {
                r.run();
            } catch (final Throwable e) {
                e.printStackTrace();
                batchFramework.outputBoss.getMessages().accumErrorMessage(
                    "Error in callback " + c + ":\n" + e.getMessage(), e);
            }
    }

    /**
     * Run a macro given a macro statement.
     *
     * @param macroStmt The macro statement (from e.g. a proof worksheet)
     * @throws IllegalArgumentException if there are any errors
     */
    public synchronized void runMacro(final MacroStmt macroStmt)
        throws IllegalArgumentException
    {
        getEngine().put("macroStmt", macroStmt);
        runMacro(ExecutionMode.WORKSHEET_PARSE,
            macroStmt.stmtText.substring(3).trim().split("\\s+"));
    }

    /**
     * Run a macro with the given name.
     *
     * @param name Name of the macro, looked up in macros/ folder
     * @throws IllegalArgumentException if an error occurred
     */
    public synchronized void runMacroRaw(final String name)
        throws IllegalArgumentException
    {
        try {
            engine.eval(new FileReader(getMacroFile(name)));
        } catch (final FileNotFoundException e) {
            throw new IllegalArgumentException(e);
        } catch (final ScriptException e) {
            e.printStackTrace();
            throw new IllegalArgumentException(
                "Error in macro " + name + ":\n" + e.getMessage(), e);
        }
    }

    /**
     * Execute a piece of script code.
     *
     * @param code A string of code to execute.
     * @return the return value of the evaluation
     * @throws ScriptException if an error occurred
     */
    public synchronized Object evalRaw(final String code)
        throws ScriptException
    {
        return engine.eval(code);
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
                set("batchFramework", batchFramework);
                engine.eval(new FileReader(getMacroFile(initMacro)));
            } catch (final FileNotFoundException e) {

            } catch (final ScriptException e) {
                throw new IllegalArgumentException(e);
            }
        }
        return engine;
    }

    private File getMacroFile(String fileNameIn) throws FileNotFoundException {
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
        if (!f.exists())
            throw new FileNotFoundException(fileNameIn);
        return f;
    }

    public enum ExecutionMode {
        RUNPARM, WORKSHEET_PARSE
    }

    public enum CallbackType {
        BUILD_GUI, PREPROCESS, BEFORE_PARSE, WORKSHEET_PARSE, AFTER_LOCAL_REFS,
        AFTER_PARSE, PARSE_FAILED, AFTER_REFORMAT, AFTER_RENUMBER,
        AFTER_UNIFY_REFS, AFTER_UNIFY_EMPTY, AFTER_UNIFY_AUTO, AFTER_UNIFY
    }
}
