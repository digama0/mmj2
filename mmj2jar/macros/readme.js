/**
 * Manual and Demonstration for mmj2 Macro Framework
 * -------------------------------------------------
 * 
 * The macro framework in mmj2 is designed to provide a means to perform extra
 * behaviors specific to a certain .mm file, or extra "unify hooks" for
 * custom use.  In order to write the most complicated scripts, knowledge of
 * the mmj2 structure will be needed, but for more basic tasks the existing
 * collection of macros should (hopefully) get you the majority of the way.
 * 
 * These scripts are written in Javascript, and more specifically Nashorn,
 * which is the default Javascript interpreter bundled with JDK8+. If you use
 * JDK 6 or 7 Rhino is used instead, and although it should still work it has
 * not been tested.
 * 
 * There are (currently) two main modes of execution: by RunParm, or by a $m
 * macro statement. A RunParm execution is performed by a RunParm line such as
 * 
 *   RunMacro,echo,hello world
 * 
 * which will output 'hello world' to the console. This looks for a file
 * macros/echo.js containing the code. Since it is not precompiled, this file
 * can be changed at any time - a program restart is not necessary. (However,
 * init.js is only run once at the start of the program, so any changes to it
 * will not be reflected until the program is restarted.)
 * 
 * There are three other RunParms which control macro execution:
 * 
 *   MacroFolder,macros
 *   MacroLanguage,js,js
 *   RunMacroInitialization,init
 *   
 * The MacroFolder refers to a subfolder of the mmj2jar folder (or wherever you
 * keep the mmj2.jar file) in which to look for macros. The MacroLanguage
 * affects the language in which scripts are written - this can be any
 * scripting language your Java installation supports, but you probably don't
 * want to change this since you will have to write your own version of init.js.
 * Changing RunMacroInitialization allows you to give init.js a different name,
 * if you want.
 * 
 * A macro can also be run from the main screen, by inserting a $m line anywhere
 * into the worksheet, for example:
 * 
 * $( <MM> <PROOF_ASST> THEOREM=syllogism LOC_AFTER=
 * 
 * h              |- ( ph -> ps ) 
 * h              |- ( ps -> ch ) 
 * $m echo hello world
 * !              |- ( ph -> ( ps -> ch ) ) 
 * !              |- ( ( ph -> ps ) -> ( ph -> ch ) ) 
 * !qed           |- ( ph -> ch ) 
 * 
 * $)
 * 
 * A macro is run as soon as the line containing the macro is parsed. Several
 * callbacks exist to delay execution of macro processing until unification
 * reaches some desired state (see post()).
 * 
 * Global variables and functions:
 * 
 * batchFramework, messages, grammar, proofAsst, logicalSystem, verifyProofs,
 * macroManager, proofAsstPreferences:
 * These are main components of the mmj2 system which are passed around; you can
 * use these to get to most relevant variables.
 * 
 * proofWorksheet: Only available in $m invocations. Refers to the proof
 * worksheet currently under construction.
 * 
 * args: The array of arguments to the macro. For example, in either of the
 * invocations
 * 
 *   RunMacro,test,1,2,3
 *   $m test 1 2 3
 * 
 * the args array is set to ['test','1','2','3']. Note that args[0] is the name
 * of the macro itself.
 * 
 * argsRaw: Only available in $m invocations. Refers to the complete macro line
 * together with any continuations (indented lines after the $m line), with
 * whitespace intact.
 * 
 * macroStmt: Only available in $m invocations. The MacroStmt object
 * corresponding to this macro invocation.
 * 
 * setPrepMacro(String name):
 * init.js sets this to prep.js, which you probably don't want to change. This
 * macro is called immediately before a RunMacro or $m invocation, but not a
 * call to runMacro().
 * 
 * runMacro(String name):
 * Execute another macro, with the given name. This is equivalent to a RunParm
 * or $m execution, but the prep macro is not called (see setPrepMacro()).
 * 
 * eval(String code):
 * Same as standard javascript eval: execute the given code as its own macro.
 * See also eval.js, which allows for writing JS directly into a worksheet, i.e.
 * "$m eval log('hello world')".
 * 
 * log(String text):
 * Print text to the console, for RunMacro invocations, or the message box in
 * $m invocations. Use print() instead to always print to the console.
 * 
 * setKeyCommand(String key, function f):
 * Execute a callback when a key is pressed. The "key" string here is the same
 * format as KeyStroke.getKeyStroke(), and includes such things as "ctrl K" or
 * "alt shift T".
 * 
 * unify():
 * Unify a proof worksheet. Same as ctrl-U. Useful in conjunction with
 * post(AFTER_UNIFY, f) for more exotic unification actions.
 * 
 * post(CallbackType type, function f):
 * Call the function f when the given callback event occurs. Use this when you
 * need more complete information on the worksheet state from later in the
 * lifecycle. For example:
 * 
 *   post(CallbackType.WORKSHEET_PARSE, function() { log('parse finished') });
 * 
 * The function is only performed once.
 * The available callback types, arranged roughly in chronological order:
 * BUILD_GUI: (For init.js) After the GUI is built and the proofAsstGUI
 *   variable is valid
 * BEFORE_PARSE: Before parsing is started
 * [$m calls happen here, during parsing of the statement loop] 
 * WORKSHEET_PARSE: Once the statement loop finishes and all proof statements
 *   are created
 * AFTER_LOCAL_REFS: After localref worksheet editing is complete
 * AFTER_PARSE: After the worksheet parser is done
 * AFTER_REFORMAT: After a reformat action is done (only called for reformat
 *   actions)
 * AFTER_RENUMBER: After renumbering is done (only called if renumbering is
 *   requested)
 * AFTER_UNIFY_REFS: After unifying steps with user-filled-in refs
 * AFTER_UNIFY_EMPTY: After unifying steps with empty refs
 * AFTER_UNIFY_AUTO: After unifying auto steps
 * AFTER_UNIFY: After all unification is complete
 * 
 * 
 * Macros:
 * init.js: The initialization macro. This is called during environment setup,
 * just before any RunMacro or $m is called for the first time. The existing
 * implementation defines a number of functions for usage by other macros, and
 * you can add your own initialization to this file.
 * 
 * prep.js: The preparation macro. init.js sets this as the macro to be called
 * each time before a RunMacro or $m is called (unlike init.js, which is only
 * called once). Use this to set up variables or functions that depend on the
 * individual run but are otherwise macro-generic.
 * 
 * echo.js: A simple demonstration macro, which echoes back the arguments to the
 * console if called as a RunParm, or to the message area if used as a $m
 * worksheet macro. Ex:
 * 
 *   RunMacro,echo,hello world
 *   $m echo hello world
 * 
 * definitionCheck.js: A set.mm definition checker. Intended to be used as a
 * RunParm macro. Drop-in replacement for SetMMDefinitionsCheckWithExclusions.
 * Ex:
 * 
 *   RunMacro,definitionCheck,ax-*,df-bi,df-clab,df-cleq,df-clel,df-sbc
 * 
 * eval.js: Intended as a $m macro. Evals its arguments as a script, which
 * allows for writing macros directly into a proof worksheet. Ex:
 * 
 *   $m eval log('hello world')
 * 
 * Multiline scripts are possible, as long as all the lines are indented by
 * at least one space (so that they are recognized as a continuation of the
 * macro line).
 */