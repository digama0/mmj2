//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * UtilConstants.java  0.13 08/11/2011
 *
 * Nov-26-2005:
 *     -->fix comment(s).RunParmFile lines at 1. Doh.
 * Dec-09-2005
 *     -->Add "RunProofAsstGUI" RunParm
 *     -->Add "ProofAsstFontSize" RunParm and related constants
 *     -->Add "ProofAsstProofFolder" RunParm
 *     -->Add "RecheckProofAsstUsingProofVerifier"
 *     -->Add "LoadEndpointStmtNbr" RunParm
 *     -->Add "LoadEndpointStmtLabel" RunParm
 * Jan-15-2006
 *     -->Add "ProofAsstFormulaLeftCol"     RunParm
 *     -->Add "ProofAsstFormulaRightCol"    RunParm
 *     -->Add "ProofAsstRPNProofLeftCol"    RunParm
 *     -->Add "ProofAsstRPNProofRightCol"   RunParm
 *     -->Add "ProofAsstExportToFile"       RunParm
 *     -->Add "ProofAsstBatchTest"          RunParm
 *     -->Add "ProofAsstUnifySearchExclude" RunParm
 *     --> Doc fix: output System Error and Output file
 *         options do NOT support the "append" option,
 *         just "new" and "update".
 *
 * Version 0.04:
 *     -->Add "ProofAsstMaxUnifyAlternates" RunParm
 *     -->Add "ProofAsstDummyVarPrefix"     RunParm
 *
 * Sep-02-2006:
 *     --> Add TMFF stuff.
 *     --> Add Java RunTime version checking stuff.
 *     --> Add "ProofAsstStartupProofWorksheet" RunParm
 * Oct-21-2006:
 *     --> Add "ProofAsstDefaultFileNameSuffix" RunParm
 * Oct-27-2006:
 *     --> Add "LoadProofs" RunParm
 *
 * Version 0.05 06/01/2007
 *     -->Add "OutputVerbosity" RunParm so that the
 *        unpopular printing of input RunParm lines
 *        can be stopped if desired.
 *     -->Add "ProofAsstDjVarsSoftErrors " RunParm.
 *
 * Version 0.06 08/01/2007
 *     -->Add WorkVarBoss stuff.
 *     -->Removed Dummy Var stuff (deprecated).
 *     -->Add AsciiRetest/NoAsciiRetest option
 *
 * Version 0.07 11/01/2007
 *     -->Add "TMFFAltFormat"                RunParm
 *        Add "TMFFUseIndent"                RunParm
 *        Add "TMFFAltIndent"                RunParm
 *        Add "ProofAsstTextRows"            RunParm
 *        Add "ProofAsstErrorMessageRows"    RunParm
 *        Add "ProofAsstErrorMessageColumns" RunParm
 *        Add "ProofAsstTextAtTop"           RunParm
 *        and associated messages, constants, etc.
 *
 * Version 0.08 - 02/01/2008
 *     - Add "ProofAsstIncompleteStepCursor"        RunParm
 *     - Add "ProofAsstOutputCursorInstrumentation" RunParm
 *     - Add "ProofAsstAutoReformat"                RunParm
 *
 * Version 0.09 - 03/01/2008
 *     - Add "StepSelectorMaxResults"               RunParm
 *     - Add "StepSelectorShowSubstitutions"        RunParm
 *     - Add "StepSelectorDialogPaneWidth"          RunParm
 *     - Add "StepSelectorDialogPaneHeight"         RunParm
 *     - Remove Unify+Get Hints feature, deprecate
 *       hint-related RunParms
 *     - Remove "ProofAsstMaxUnifyAlternates"       RunParm
 *     - Add "StepSelectorBatchTest"                RunParm
 *     - Add "PreprocessRequestBatchTest"           RunParm
 *
 * Version 0.10 - 08/01/2008
 *     - Add "SvcFolder"                            RunParm
 *     - Add "SvcCallbackClass"                     RunParm
 *     - Add "SvcArg"                               RunParm
 *     - Add "SvcCall"                              RunParm
 *     - Added new error messages for LogicalSystemBoss
 *       when processing ProvableLogicStmtType and
 *       LogicStmtType RunParms
 *     - Add "BookManagerEnabled"                   RunParm
 *     - Add new Commands in OutputBoss:
 *           "PrintBookManagerChapters"             RunParm
 *           "PrintBookManagerSections"             RunParm
 *           "PrintBookManagerSectionDetails"       RunParm
 *     - Add new constants for Dump.java for printing
 *       BookManager data.
 *     - Add "SeqAssignerIntervalSize"              RunParm
 *     - Add "SeqAssignerIntervalTblInitialSize"    RunParm
 *     - Added abort message for new MergeSortedArrayLists.java
 *       utility: "ERRMSG_MERGE_SORTED_LISTS_DUP_ERROR_1"
 *     - Add new "TheoremLoaderBoss" with
 *            "TheoremLoaderMMTFolder"              RunParm
 *            "TheoremLoaderDjVarsOption"           RunParm
 *            "TheoremLoaderAuditMessages"          RunParm
 *            "LoadTheoremsFromMMTFolder"           RunParm
 *            "ExtractTheoremToMMTFolder"           RunParm
 *     - Added new RunParm for ProofAsst
 *            "ProofAsstAssrtListFreespace"         RunParm
 *
 * Version 0.11 - Nov-01-2011:
 *     - Added GMFF stuff.
 *     - Modified for MMJ2 Paths Enhancement
 *     - Added code for MMJ2FailPopupWindow
 *     - Set: MAX_STATEMENT_PRINT_COUNT_DEFAULT = 9999 (was 99999)
 *
 * Version 0.12 - Aug-01-2013:
 *     - Add "ProofAsstProofFormat"                 RunParm
 *
 * Version 0.13 - Aug-11-2013:
 *     - Add "ProofAsstLookAndFeel"                 RunParm
 *     - Add "ProofAsstMaximized"                   RunParm
 */

package mmj.util;

import static mmj.pa.ErrorCode.of;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.pa.*;
import mmj.pa.MMJException.ErrorContext;
import mmj.pa.MMJException.FormatContext;
import mmj.transforms.TrConstants;
import mmj.verify.*;

/**
 * (Most) Constants used in mmj.util classes
 * <p>
 * There are two primary types of constants: parameters that are "hardcoded"
 * which affect/control processing, and error/info messages.
 * <p>
 * Each mmj message begins with a code, such as this:
 * <p>
 * <code>E-LA-0007</code>
 * <p>
 * where the format of the code is {@code X-YY-9999}
 * <p>
 * <b>{@code X}</b> : error level
 * <ul>
 * <li>{@code E} = Error
 * <li>{@code I} = Information
 * <li>{@code A} = Abort (processing terminates, usually a bug).
 * </ul>
 * <p>
 * <b>{@code YY}</b> : source code
 * <ul>
 * <li>{@code GM} = mmj.gmff package (see {@link GMFFConstants})
 * <li>{@code GR} = mmj.verify.Grammar and related code (see
 * {@link GrammarConstants})
 * <li>{@code IO} = mmj.mmio package (see {@link MMIOConstants})
 * <li>{@code LA} = mmj.lang package (see {@link GMFFConstants})
 * <li>{@code PA} = mmj.pa package (proof assistant) (see {@link PaConstants})
 * <li>{@code PR} = mmj.verify.VerifyProof and related code (see
 * {@link ProofConstants})
 * <li>{@code TL} = mmj.tl package (Theorem Loader).
 * <li>{@code TM} = mmj.tmff.AlignColumn and related code
 * <li>{@code UT} = mmj.util package. (see {@link UtilConstants})
 * <li>{@code TR} = mmj.transforms package (proof assistant) (see
 * {@link TrConstants})
 * </ul>
 * <p>
 * <b>{@code 9999}</b> : sequential number within the source code, 0001 through
 * 9999.
 * <p>
 * {@code
 * =============================================================
 * }
 * <p>
 * <b>RunParmFile parameter Names (1st field on a RunParmFile line).</b>
 * <p>
 * The only mandatory RunParm is RUNPARM_LOAD_FILE, and even that is not
 * required if nothing much is desired :)
 * <p>
 * All other RunParms have defaults or invoke processing that is optional. For
 * example proof verification is done only if RUNPARM_VERIFY_PROOF is entered.
 * <p>
 * There are "state" variables in BatchMMJ's handling of RunParms. Some
 * situations are dealt with automatically while others result in an error.
 * <p>
 * A RunParm error, either in the parm values or the "state" (combination of
 * RunParms) terminates processing immediately (BatchMMJ will not continue with
 * subsequent commands after a bogosity,)
 * <p>
 * It is simplest to sequence RunParmFile lines with the non-executable commands
 * first. These are the settings that modify subsequent processing and stay in
 * effect until superceded.
 * <p>
 *
 * <pre>
 *  "Executable" RunParms:
 *
 *      000000000011111111112
 *      012345678901234567890...
 *      ----------------------------
 *      Clear
 *      GarbageCollection
 *      LoadFile
 *      LoadTheoremsFromMMTFolder
 *      VerifyProof
 *      VerifyParse
 *      Parse
 *      InitializeGrammar
 *      PrintSyntaxDetails
 *      PrintStatementDetails
 *      PrintBookManagerChapters
 *      PrintBookManagerSections
 *      PrintBookManagerSectionDetails
 *      ProofAsstExportToFile
 *      ProofAsstBatchTest
 *      StepSelectorBatchTest
 *      PreprocessRequestBatchTest
 *      RunProofAsstGUI
 *      SvcCall
 *      ExtractTheoremToMMTFolder
 *
 *
 *  Example #1 RunParmFile to load 1 file, verify proofs, edit
 *  grammar, parse, print syntax and statement details, and
 *  print BookManager data:
 *
 *      000000000011111111112
 *      012345678901234567890...
 *      MaxStatementPrintCount,9999
 *      Caption,Example #1
 *      MaxErrorMessages,500
 *      MaxInfoMessages,500
 *      LoadFile,c:\metamath\expset.mm
 *      VerifyProof,*
 *      Parse,*
 *      PrintSyntaxDetails
 *      PrintStatementDetails,*
 *      PrintBookManagerChapters
 *      PrintBookManagerSections
 *      PrintBookManagerSectionDetails,*
 *
 *  Example #2 RunParmFile doing the exact same thing except
 *  this time:
 *      - specifying the default values AND
 *      - sending the output to files AND
 *      - specify Load Limits for the input .mm file(s) to
 *        stop loading Metamath statements after a given
 *        number of statements and/or a given statement
 *        label is reached.
 *      - loading an extra .mm file on top of the old one! AND
 *      - specify TheoremLoader stuff!
 *      - doing a belt-and-suspenders double-check of the parse
 *        RPN's!
 *        with
 *      - Set Proof Asst parms, export the input theorems' proofs,
 *        read that file back in as a test, and then
 *      - Trigger the ProofAsstGUI
 *      - generous use of blank comment lines for readability!
 *      - and THEN we clear and load a different file!
 *
 *      000000000011111111112
 *      012345678901234567890...
 *
 *      OutputVerbosity,9999
 *       CommentLine: Example #2 - default charsets="" and
 *                    new/update parameter
 *      SystemErrorFile,c:\my\mmjSyserrTest001.txt,new,""
 *      SystemOutputFile,c:\my\mmjSysoutTest001.txt,new,""
 *
 *      MaxErrorMessages,500
 *      MaxInfoMessages,500
 *
 *      SymbolTableInitialSize,600
 *      StatementTableInitialSize,30000
 *
 *      SeqAssignerIntervalSize,100
 *      SeqAssignerIntervalTblInitialSize,100
 *
 *      LoadEndpointStmtNbr,5000
 *      LoadEndpointStmtLabel,FermatsLastTheorem
 *      LoadComments,yes
 *      LoadProofs,yes
 *
 *      ProvableLogicStmtType,|-
 *      LogicStmtType,wff
 *
 *      BookManagerEnabled,yes
 *
 *      GrammarAmbiguityEdits,basic
 *      StatementAmbiguityEdits,basic
 *
 *      MaxStatementPrintCount,9999
 *      Caption,Example #2
 *
 *      LoadFile,c:\metamath\expset.mm
 *      LoadFile,c:\metamath\expset2.mm
 *
 *      TheoremLoaderMMTFolder,c:\my\mmtFolder
 *      TheoremLoaderDjVarsOption,Replace
 *      TheoremLoaderAuditMessages,Yes
 *      TheoremLoaderStoreFormulasAsIs,Yes
 *      TheoremLoaderStoreMMIndentAmt,2
 *      TheoremLoaderStoreMMRightCol,79
 *      LoadTheoremsFromMMTFolder,*
 *      UnifyPlusStoreInMMTFolder,syl.mmp
 *      UnifyPlusStoreInLogSysAndMMTFolder,syl.mmp
 *      ExtractTheoremToMMTFolder,syl
 *
 *      VerifyProof,*
 *      Parse,*
 *
 *      VerifyParse,*
 *
 *  ===TMFF stuff follows===
 *
 *      TMFFDefineScheme,AlignVarDepth1,AlignColumn,1,Var,1,Var
 *      TMFFDefineScheme,AlignVarDepth2,AlignColumn,2,Var,1,Var
 *      TMFFDefineScheme,AlignVarDepth3,AlignColumn,3,Var,1,Var
 *      TMFFDefineScheme,AlignVarDepth4,AlignColumn,4,Var,1,Var
 *      TMFFDefineScheme,AlignVarDepth5,AlignColumn,5,Var,1,Var
 *      TMFFDefineScheme,AlignVarDepth99,AlignColumn,99,Var,1,Var
 *      TMFFDefineScheme,Flat,Flat
 *      TMFFDefineScheme,PrefixDepth3,AlignColumn,3,Sym,2,Sym
 *      TMFFDefineScheme,PostfixDepth3,AlignColumn,3,Sym,1,Sym
 *      TMFFDefineScheme,TwoColumnAlignmentDepth1,TwoColumnAlignment,1
 *      TMFFDefineScheme,TwoColumnAlignmentDepth2,TwoColumnAlignment,2
 *      TMFFDefineScheme,TwoColumnAlignmentDepth3,TwoColumnAlignment,3
 *      TMFFDefineScheme,TwoColumnAlignmentDepth4,TwoColumnAlignment,4
 *      TMFFDefineScheme,TwoColumnAlignmentDepth5,TwoColumnAlignment,5
 *      TMFFDefineScheme,TwoColumnAlignmentDepth99,TwoColumnAlignment,99
 *
 *  Note: "Unformatted" and Format 0 are hardcoded --
 *       they cannot be redefined via RunParms.
 *
 * TMFFDefineScheme,Unformatted,Unformatted
 *
 *      TMFFDefineFormat,1,AlignVarDepth1
 *      TMFFDefineFormat,2,AlignVarDepth2
 *      TMFFDefineFormat,3,AlignVarDepth3
 *      TMFFDefineFormat,4,AlignVarDepth4
 *      TMFFDefineFormat,5,AlignVarDepth5
 *      TMFFDefineFormat,6,AlignVarDepth99
 *      TMFFDefineFormat,7,Flat
 *      TMFFDefineFormat,8,PrefixDepth3
 *      TMFFDefineFormat,9,PostfixDepth3
 *      TMFFDefineFormat,10,TwoColumnAlignmentDepth99
 *      TMFFDefineFormat,11,TwoColumnAlignmentDepth1
 *      TMFFDefineFormat,12,TwoColumnAlignmentDepth2
 *      TMFFDefineFormat,13,TwoColumnAlignmentDepth3
 *      TMFFDefineFormat,14,TwoColumnAlignmentDepth4
 *      TMFFDefineFormat,15,TwoColumnAlignmentDepth5
 *
 *      TMFFUseFormat,3
 *      TMFFAltFormat,7
 *      TMFFUseIndent,0
 *      TMFFAltIndent,1
 *
 *      PrintSyntaxDetails
 *      PrintStatementDetails,*
 *      PrintBookManagerChapters
 *      PrintBookManagerSections
 *      PrintBookManagerSectionDetails,*
 *
 *      ProofAsstFontSize,14
 *      ProofAsstFontBold,yes
 *      ProofAsstFontFamily,Monospaced
 *      ProofAsstForegroundColorRGB,0,0,0
 *      ProofAsstBackgroundColorRGB,255,255,255
 *
 *      ProofAsstFormulaLeftCol,20
 *      ProofAsstFormulaRightCol,79
 *      ProofAsstTextColumns,80
 *      ProofAsstTextRows,21
 *      ProofAsstErrorMessageRows,4
 *      ProofAsstErrorMessageColumns,80
 *      ProofAsstTextAtTop,yes
 *      ProofAsstIncompleteStepCursor,Last
 *
 *      ProofAsstRPNProofLeftCol,6
 *      ProofAsstRPNProofRightCol,79
 *
 *      ProofAsstOutputCursorInstrumentation,no
 *      ProofAsstAutoReformat,yes
 *      ProofAsstProofFolder,c:\my\proofs
 *      RecheckProofAsstUsingProofVerifier,yes
 *      ProofAsstUndoRedoEnabled,yes
 *      ProofAsstUnifySearchExclude,biigb,xxxid
 *      ProofAsstExportToFile,*,c:\my\export.mmp,new,un-unified,Randomized,Print
 *      ProofAsstBatchTest,*,c:\my\export.mmp,un-unified,NotRandomized,NoPrint
 *      StepSelectorBatchTest,c:\my\export.mmp,50,0
 *      PreprocessRequestBatchTest,c:\my\export.mmp,EraseAndRederiveFormulas
 *
 *      ProofAsstStartupProofWorksheet,c:\mmj2\data\mmp\PATutorial\Page101.mmp
 *
 *      StepSelectorMaxResults,50
 *      StepSelectorShowSubstitutions,yes
 *      StepSelectorDialogPaneWidth,720
 *      StepSelectorDialogPaneHeight,440
 *
 *      ProofAsstAssrtListFreespace,5
 *
 *      RunProofAsstGUI
 *
 *       Comment: now load & process another .mm file!!!!
 *      clear
 *      GarbageCollection
 *      LoadFile,c:\metamath\exppeano.mm
 *      VerifyProof,*
 *      PrintSyntaxDetails
 *      PrintStatementDetails,*
 *
 *       Comment: now load another .mm and make SvcCallback!!!!
 *      clear
 *      GarbageCollection
 *      LoadFile,c:\metamath\set.mm
 *      VerifyProof,*
 *      Parse,*
 *      SvcFolder,c:\myProduct
 *      SvcCallbackClass,MyProductMMJ2SvcCallback
 *      SvcArg,ZipOutput,yes
 *      SvcArg,OrgFilesBy,chapter
 *      SvcArg,ExportFormat,mmjbert
 *      SvcArg,whatever,whatever
 *      SvcCall
 * </pre>
 * <p>
 *
 * <pre>
 *  =============================================================
 *  ----> RunParm Default Values. Some options have defaults
 *        and allowable values defined elsewhere. See:
 *               mmj.mmio.MMIOConstants.java
 *               mmj.lang.LangConstants.java
 *               mmj.verify.GrammarConstants.java
 *               mmj.verify.ProofConstants.java
 *               mmj.pa.PaConstants.java
 *               mmj.tl.TlConstants.java
 *
 *  =============================================================
 * </pre>
 */
public class UtilConstants {

    public static final String JAVA_VERSION_PROPERTY_NAME = "java.version";

    public static final int JAVA_VERSION_MMJ2_MAJ = 1;

    public static final int JAVA_VERSION_MMJ2_MIN = 5;

    public static final String JAVA_VERSION_MMJ2_RUNTIME_ERROR_MSG = "BatchMMJ2 requires Java RunTime Environment Version "
        + "1.5 or higher. Version running now = ";

    // ----------------------------------------------------------
    // Constants mmj.util.RunParmFile
    // ----------------------------------------------------------
    public static final String RUN_PARM_FILE_REPORT_LINE_1 = "  [1] runParmFile  = ";
//  public static final String RUN_PARM_FILE_REPORT_LINE_2
//								= "\n";

    // ----------------------------------------------------------
    // Constants mmj.util.Paths
    // ----------------------------------------------------------
    public static final String MMJ2_PATH_REPORT_LINE_1 = "\n  [3] mmj2Path     = ";
    public static final String METAMATH_PATH_REPORT_LINE_2 = "  [4] metamathPath = ";
    public static final String SVC_PATH_REPORT_LINE_3 = "  [5] svcPath      = ";
    public static final String PATH_REPORT_E_G_CAPTION_1 = " (e.g. ";
    public static final String PATH_REPORT_E_G_CAPTION_2 = ")";
    public static final String PATH_REPORT_EXAMPLE_FILE_NAME = "YourFile.xyz";

    // ----------------------------------------------------------
    // Constants mmj.util.CommandLineArguments
    // ----------------------------------------------------------
    public static final int RUNPARM_FILE_NAME_ARGUMENT_INDEX = 0;

    public static final int DISPLAY_MMJ2_FAIL_POPUP_WINDOW_ARGUMENT_INDEX = 1;

    public static final int MMJ2_PATH_ARGUMENT_INDEX = 2;

    public static final int METAMATH_PATH_ARGUMENT_INDEX = 3;

    public static final int SVC_PATH_ARGUMENT_INDEX = 4;

    public static final int TEST_OPTION_ARGUMENT_INDEX = 5;

    public static final String RUNPARM_FILE_NAME_ARGUMENT_LITERAL = "runParmFileName argument";

    public static final String DISPLAY_MMJ2_FAIL_POPUP_WINDOW_ARGUMENT_LITERAL = "displayMMJ2FailPopupWindow argument";

    public static final String MMJ2_PATH_ARGUMENT_LITERAL = "mmj2Path argument";

    public static final String METAMATH_PATH_ARGUMENT_LITERAL = "metamathPath argument";

    public static final String SVC_PATH_ARGUMENT_LITERAL = "svcPath argument";

    public static final boolean DISPLAY_MMJ2_FAIL_POPUP_WINDOW_DEFAULT = true;

    public static final String YES_ARGUMENT = "Y";
    public static final String NO_ARGUMENT = "N";

    public static final ErrorContext ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT = new FormatContext(
        "\nmmj2 Command Line format follows:\n"
            + "java JAVAPARMS -jar mmj2.jar ARG1 ARG2 ARG3 ARG4 ARG5\n"
            + "    where JAVAPARMS = -Xincgc -Xms128M -Xmx256M (you may customize)\n"
            + "          ARG1      = RunParms File Name (e.g. RunParms.txt)\n"
            + "          ARG2      = y or n (displayMMJ2FailPopupWindow)\n"
            + "          ARG3      = mmj2Path (e.g. c:\\mmj2jar)\n"
            + "          ARG4      = metamathPath (e.g. c:\\metamath)\n"
            + "          ARG5      = svcPath (e.g. c:\\your\\svc)\n")
    {};

    public static final String ARGUMENTS_OPTION_REPORT_LINE_1 = "\nCommandLineArguments.displayArgumentOptionReport():\n";

    public static final String ARGUMENTS_OPTION_REPORT_LINE_2 = "  Command Line Arguments:";

    public static final String ARGUMENTS_OPTION_REPORT_LINE_3A = "    Arg #";

    public static final String ARGUMENTS_OPTION_REPORT_LINE_3B = " = ";

    public static final String ARGUMENTS_OPTION_REPORT_LINE_4 = "  [2] displayMMJ2FailPopupWindow \n"
        + "                   = ";

    public static final String ARGUMENTS_OPTION_REPORT_LINE_5 = "\n***END CommandLineArguments.displayArgumentOptionReport()***\n";

    // ----------------------------------------------------------
    // Constants mmj.util.MMJ2FailPopupWindow
    // ----------------------------------------------------------
    public static final String MMJ2_FAIL_DIALOG_TITLE = "MMJ2 Fail";

    public static final String MMJ2_FAIL_STARTUP_DIALOG_TITLE = "MMJ2 Start-up Error";

    public static final String MMJ2_STARTUP_MSG_LIT_1 = "RunParm #";
    public static final String MMJ2_STARTUP_MSG_LIT_2 = " Command = ";
    public static final String MMJ2_STARTUP_MSG_LIT_3 = " Error(s)\n";

    public static final int LINE_BREAK_MAX_LENGTH = 75;
    public static final char NEW_LINE_CHAR = '\n';

    public static final int MAX_STARTUP_ERROR_MESSAGES = 4;

    // ----------------------------------------------------------
    // other stuff :-)
    // ----------------------------------------------------------

    /**
     * RunParm Line Dump Verbosity Note: The default OutputVerbosity RunParm
     * value is 9999. Set it to 0 to turn off log-type output (error messages
     * will still print.) The default "verbosity" of the printing of RunParm
     * lines themselves in BatchFramework.java is 9, which means that they print
     * (<= verbosity).
     */
    public static final int RUNPARM_LINE_DUMP_VERBOSITY = 9;

    /**
     * RunParmFile Default field delimiter. Note: at present there is no defined
     * escape character. However, the DelimitedTextParser allows "quoter"
     * characters inside non-quoted fields, and delimiter characters inside
     * quoted fields. Surprisingly, this arrangement suffices for a great many
     * purposes. If need be, a RunParm can be added to dynamically *change* the
     * "quoter" and delimiter characters (that is simpler than modifying
     * DelimitedText Parser...)<
     */
    public static final char RUNPARM_FIELD_DELIMITER_DEFAULT = ',';

    /**
     * RunParmFile Default quote character.
     */
    public static final char RUNPARM_FIELD_QUOTER_DEFAULT = '"';

    /**
     * RunParmFile Comment Character: space. A RunParmFile line with space, '*'
     * or '/' in position 1 is a comment (not executed...also, a line with no
     * characters is also treated as a comment).
     */
    public static final char RUNPARM_COMMENT_CHAR_SPACE = ' ';

    /**
     * RunParmFile Comment Character: '/' (SLASH). A RunParmFile line with
     * space, '*' or '/' in position 1 is a comment (not executed...also, a line
     * with no characters is also treated as a comment).
     */
    public static final char RUNPARM_COMMENT_CHAR_SLASH = '/';

    /**
     * RunParmFile Comment Character: ASTERISK. A RunParmFile line with space,
     * '*' or '/' in position 1 is a comment (not executed...also, a line with
     * no characters is also treated as a comment).
     */
    public static final char RUNPARM_COMMENT_CHAR_ASTERISK = '*';

    // ----------------------------------------------------------
    // ----------------------------------------------------------

    // =========================================================
    // ====> RunParmFile Name Literals
    // =========================================================

    // ----------------------------------------------------------
    // Commands for mmj.util.LogicalSystemBoss.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_PROVABLE_LOGIC_STMT_TYPE = new BatchCommand(
        "ProvableLogicStmtType",
        " ProvableLogicStmtType.\n" + " <p>\n" + "<code> \n"
            + " \"ProvableLogicStmtType\": default is \"|-\"\n"
            + " </code></p>\n" + "\n");

    public static final BatchCommand RUNPARM_LOGIC_STMT_TYPE = new BatchCommand(
        "LogicStmtType",
        " LogicStmtType.\n" + " <p>\n" + "<code> \n"
            + " \"LogicStmtType\": default is \"wff\"\n" + " </code></p>\n"
            + "\n");

    public static final BatchCommand RUNPARM_BOOK_MANAGER_ENABLED = new BatchCommand(
        "BookManagerEnabled", // default
        " BookManagerEnabled.\n" + " <p>\n" + " <code> \n"
            + " \"BookManagerEnabled\": default is \"yes\"\n" + " </code>\n"
            + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.BatchMMJ2.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_CLEAR = new BatchCommand("Clear",
        " Clear.\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"Clear\":  clear loaded/derived mm data (files/grammar,etc)\n"
            + "          as well as all RunParm values except for\n"
            + "          SystemErrorFile and SystemOutputFile.\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_JAVA_GARBAGE_COLLECTION = new BatchCommand(
        "GarbageCollection",
        " GarbageCollection.\n" + " <p>\n" + " <code> \n"
            + " \"GarbageCollection\": frees up unused memory items.\n"
            + " </code></p>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.OutputBoss.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_SYSERR_FILE = new BatchCommand(
        "SystemErrorFile",
        " SystemErrorFile.\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"SystemErrorFile\": value1 = filename,\n" + " \n"
            + "                    value2 = new (default) or update.\n"
            + "                      The system will NOT touch an existing\n"
            + "                      file unless given \"update\",\n"
            + "                      AND if \"new\" is specified an error is\n"
            + "                      reported, halting processing ASAP if\n"
            + "                      the file already exists. If the file\n"
            + "                      does exist and Update is specified,\n"
            + "                      then it is overwritten (not appended),\n"
            + "                      but no error is reported for Update\n"
            + "                      if the file does not exist.\n" + " \n"
            + "                    value3 = charset. Note: the program\n"
            + "                      will not stop you from appending\n"
            + "                      a different charset to an existing\n"
            + "                      file, thus hopelessly mixing up your\n"
            + "                      data, so have fun but be careful!\n"
            + " \n" + " info on charsets\n"
            + " file:///C:/Program%20Files/Java/jdk1.5.0_02/docs/api/java/nio/charset/Charset.html\n"
            + " \n" + " Valid charset names on all Java Platforms:\n"
            + "     US-ASCII\n" + "     ISO-8859-1\n" + "     UTF-8\n"
            + "     UTF-16BE\n" + "     UTF-16LE\n" + "     UTF-16\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_SYSOUT_FILE = new BatchCommand(
        "SystemOutputFile",
        " SystemOutputFile.\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"SystemOutputFile\": value1 = filename,\n" + " \n"
            + "                     value2 = new (default), or\n"
            + "                        update\n" + " \n"
            + "                     value3 = charset\n"
            + "                        see RUNPARM_SYSERR_FILE comments\n"
            + "                        for info on the above value parms!\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_OUTPUT_VERBOSITY = new BatchCommand(
        "OutputVerbosity",
        " OutputVerbosity\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"OutputVerbosity\": value1 = integer,\n" + " \n"
            + "  Verbosity = 9999 is the default\n"
            + "            =    0 means only print error messages and\n"
            + "                 specifically requested output\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_START_INSTRUMENTATION_TIMER = new BatchCommand(
        "StartInstrumentationTimer",
        " StartInstrumentationTimer\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"StartInstrumentationTimer\": value1 = ID String,\n" + " \n"
            + "  ID String = Identifier in output message produced\n"
            + "              by StopInstrumentationTimer RunParm --\n"
            + "              must match that ID String.\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_STOP_INSTRUMENTATION_TIMER = new BatchCommand(
        "StopInstrumentationTimer",
        " StopInstrumentationTimer\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"StopInstrumentationTimer\": value1 = ID String,\n" + " \n"
            + "  ID String = Identifier in StartInstrumentationTimer\n"
            + "              RunParm -- must match.\n" + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.mmio.Systemizer.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_LOAD_FILE = new BatchCommand(
        "LoadFile",
        " LoadFile.\n" + " \n" + " <pre>\n"
            + " \"LoadFile\": value1 = qual/unqual filename (varies by OS!)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_LOAD_ENDPOINT_STMT_NBR = new BatchCommand(
        "LoadEndpointStmtNbr",
        " LoadEndpointStmtNbr.\n" + " \n" + " <pre>\n"
            + " \"LoadEndpointStmtNbr\": value1 = stop after loading given\n"
            + "                                 number of statements from\n"
            + "                                 input Metamath file(s).\n"
            + "                                 Must be greater than zero.\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_LOAD_ENDPOINT_STMT_LABEL = new BatchCommand(
        "LoadEndpointStmtLabel",
        " LoadEndpointStmtLabel.\n" + " \n" + " <pre>\n"
            + " \"LoadEndpointStmtLabel\": value1 = stop after loading given\n"
            + "                                 statement label from\n"
            + "                                 input Metamath file(s).\n"
            + "                                 Must not be blank.\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_LOAD_COMMENTS = new BatchCommand(
        "LoadComments",
        " LoadComments\n" + " \n" + " <pre>\n"
            + " \"LoadComments\": value1 = yes/no (default = yes)\n"
            + "                          load Metamath comments\n"
            + "                          into LogicalSystem as Descriptions for\n"
            + "                          the MObj's. The comment immediately\n"
            + "                          preceding the $p statement is treated\n"
            + "                          as the description (must be the statement\n"
            + "                          immediately prior to the $p statement.)\n"
            + " \n"
            + "                          Only Theorem descriptions are loaded\n"
            + "                          now -- which is for Proof Assistant --\n"
            + "                          but in principle, the rest could be\n"
            + "                          loaded, except for $c and $v statements\n"
            + "                          which often have the description\n"
            + "                          after the declaration.\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_LOAD_PROOFS = new BatchCommand(
        "LoadProofs",
        " LoadProofs\n" + " \n" + " <pre>\n"
            + " \"LoadProofs\": value1 = yes/no (default = yes)\n"
            + "                          load Metamath proofs from input .mm\n"
            + "                          file.\n" + " \n"
            + "                          Use \"no\" to conserve memory and\n"
            + "                          shorten start-up time for the Proof\n"
            + "                          Assistant.\n" + " \n"
            + "                          If set to \"no\" then RunParm\n"
            + "                          \"VerifyProof\" will be ignored -- a\n"
            + "                          warning message is produced though.\n"
            + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.lang.Messages.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_MAX_ERROR_MESSAGES = new BatchCommand(
        "MaxErrorMessages",
        " MaxErrorMessages.\n" + " \n" + " <pre>\n"
            + " \"MaxErrorMessages\": 1 -> 999999999...\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_MAX_INFO_MESSAGES = new BatchCommand(
        "MaxInfoMessages", " MaxInfoMessages.\n" + " \n" + " <pre>\n"
            + " \"MaxInfoMessages\": 1 -> 999999999...\n" + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.lang.LogicalSystem.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_SYM_TBL_INITIAL_SIZE = new BatchCommand(
        "SymbolTableInitialSize",
        " SymbolTableInitialSize.\n" + " \n" + " <pre>\n"
            + " \"SymbolTableInitialSize\": default = 1500, min = 10\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_STMT_TBL_INITIAL_SIZE = new BatchCommand(
        "StatementTableInitialSize",
        " StatementTableInitialSize.\n" + " \n" + " <pre>\n"
            + " \"StatementTableInitialSize\": default = 30000, min = 100\n"
            + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.lang.SeqAssigner.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_SEQ_ASSIGNER_INTERVAL_SIZE = new BatchCommand(
        "SeqAssignerIntervalSize",
        " SeqAssignerIntervalSize.\n" + " \n" + " <pre>\n"
            + " \"SeqAssignerIntervalSize\": default = 100, min = 1,\n"
            + " max = 10000.\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE = new BatchCommand(
        "SeqAssignerIntervalTblInitialSize",
        " SeqAssignerIntervalTblInitialSize.\n" + " \n" + " <pre>\n"
            + " \"SeqAssignerIntervalTblInitialSize\": default = 100, min = 10,\n"
            + " max = 10000.\n" + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.verify.Grammar.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_GRAMMAR_AMBIGUITY_EDITS = new BatchCommand(
        "GrammarAmbiguityEdits",
        " GrammarAmbiguityEdits.\n" + " \n" + " <pre>\n"
            + " \"GrammarAmbiguityEdits\": \"basic\" (default) or \"complete\"\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_STATEMENT_AMBIGUITY_EDITS = new BatchCommand(
        "StatementAmbiguityEdits",
        " StatementAmbiguityEdits.\n" + " \n" + " <pre>\n"
            + " \"StatementAmbiguityEdits\": \"basic\" (default) or \"complete\"\n"
            + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.Dump.java
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_MAX_STATEMENT_PRINT_COUNT = new BatchCommand(
        "MaxStatementPrintCount",
        " MaxStatementPrintCount.\n" + " \n" + " <pre>\n"
            + " \"MaxStatementPrintCount\": 1 -> 9999999999....\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_CAPTION = new BatchCommand(
        "Caption",
        " Caption.\n" + " \n" + " <pre>\n"
            + " \"Caption\": freeform caption for report output.\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PRINT_SYNTAX_DETAILS = new BatchCommand(
        "PrintSyntaxDetails", // no
        " PrintSyntaxDetails.\n" + " \n" + " <pre>\n"
            + " \"PrintSyntaxDetails\": no options\n" + " </pre>\n" + "\n");
    // options

    public static final BatchCommand RUNPARM_PRINT_STATEMENT_DETAILS = new BatchCommand(
        "PrintStatementDetails", // "*"
        " PrintStatementDetails.\n" + " \n" + " <pre>\n"
            + " \"PrintStatementDetails\": \"*\" or Stmt.label\n" + " </pre>\n"
            + "\n");
    // or
    // Stmt.label

    public static final BatchCommand RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS = new BatchCommand(
        "PrintBookManagerChapters",
        " PrintBookManagerChapters\n" + " \n" + " <pre>\n"
            + " &quot;PrintBookManagerChapters&quot;\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PRINT_BOOK_MANAGER_SECTIONS = new BatchCommand(
        "PrintBookManagerSections",
        " PrintBookManagerSections\n" + " \n" + " <pre>\n"
            + " &quot;PrintBookManagerSections&quot;\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PRINT_BOOK_MANAGER_SECTION_DETAILS = new BatchCommand(
        "PrintBookManagerSectionDetails", // "*"
        " PrintBookManagerSectionDetails.\n" + " \n" + " <pre>\n"
            + " \"PrintBookManagerSectionDetails\": \"*\" or Section Number\n"
            + " </pre>\n" + "\n");
            // or
            // Section
            // Number

    // ----------------------------------------------------------
    // Commands for mmj.lang.ProofVerifier.java interface
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_VERIFY_PROOF = new BatchCommand(
        "VerifyProof", // "*" or
        " VerifyProof.\n" + " \n" + " <pre>\n"
            + " \"VerifyProof\": \"*\" or Stmt.label\n" + " </pre>\n" + "\n");
    // Stmt.label

    public static final BatchCommand RUNPARM_VERIFY_PARSE = new BatchCommand(
        "VerifyParse", // "*" or
        " VerifyParse.\n" + " \n" + " <pre>\n"
            + " \"VerifyParse\": \"*\" or Stmt.label\n" + " </pre>\n" + "\n");
            // Stmt.label

    // ----------------------------------------------------------
    // Commands for mmj.lang.SyntaxVerifier.java interface
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_SET_PARSER = new BatchCommand(
        "SetParser", // no
        " SetParser.\n" + " \n" + " <pre>\n"
            + " \"SetParser\": fully qualified parser implementation class name.\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PARSE = new BatchCommand("Parse", // "*"
                                                                               // or
                                                                               // Stmt.label
        " Parse.\n" + " \n" + " <pre>\n" + " \"Parse\": \"*\" or Stmt.label\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_INITIALIZE_GRAMMAR = new BatchCommand(
        "InitializeGrammar", // no
        " InitializeGrammar.\n" + " \n" + " <pre>\n"
            + " \"InitializeGrammar\": no option values\n" + " </pre>\n"
            + "\n");
            // option
            // values

    // ----------------------------------------------------------
    // Commands for mmj.pa.ProofAsst.java interface
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_PROOF_ASST_STORE = new BatchCommand(
        "ProofAsstStore",
        " ProofAsstStore\n" + " <p>\n"
            + " <code> \"ProofAsstStore\"</code>: define a file for automatic saving \n"
            + " and loading of preferences.\n</p>" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_LOOK_AND_FEEL = new BatchCommand(
        "ProofAsstLookAndFeel",
        " ProofAsstLookAndFeel\n" + " <p>\n"
            + " <code> \"ProofAsstLookAndFeel\"</code>: choose between any installed looks on\n"
            + " your Java installation. Default is <code> Metal</code>, and available options\n"
            + " on my computer are <code> Metal</code>, <code> Nimbus</code>, <code> CDE/Motif</code>,\n"
            + " <code> Windows</code>, and <code> Windows Classic</code>, although the specific\n"
            + " options depend on your installation. Input an invalid option here to get\n"
            + " a list of available options in the error message.\n</p>"
            + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_DJ_VARS_SOFT_ERRORS = new BatchCommand(
        "ProofAsstDjVarsSoftErrors",
        " ProofAsstDjVarsSoftErrors\n" + " \n" + " <pre>\n"
            + " \"ProofAsstDjVarsSoftErrors\":\n" + " \n"
            + "     \"Ignore\" -- Don't check for missing $d statements\n"
            + "     \"Report\" -- Create missing $d statement error messages.\n"
            + "     \"GenerateReplacements\"\n"
            + "              -- Generate complete set of $d statements if\n"
            + "                 any omissions are detected\n"
            + "     \"GenerateDifferences\"\n"
            + "              -- Generate set of $d statements to add to the\n"
            + "                 $d's in the Proof Worksheet and .mm database\n"
            + "                 for the theorem.\n" + " \n"
            + " Optional, default is \"GenerateReplacements\"\n" + " \n"
            + " NOTE: Superfluous $d statements are not detected!\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_PROOF_FORMAT = new BatchCommand(
        "ProofAsstProofFormat",
        " ProofAsstProofFormat\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"ProofAsstProofFormat\":\n" + " \n"
            + "     \"Normal\" -- Uncompressed RPN proof\n"
            + "     \"Packed\" -- RPN proof with backreferences\n"
            + "     \"Compressed\"\n"
            + "              -- Full compression (with all caps encoding)\n"
            + " \n" + " Optional, default is \"Compressed\"\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_AUTOCOMPLETE_ENABLED = new BatchCommand(
        "ProofAsstAutocompleteEnabled",
        "ProofAsstAutocompleteEnabled\n" + "\n" + "<pre>\n"
            + "\"ProofAsstAutocompleteEnabled\": Yes or No\n" + "\n"
            + "Optional, default is Yes (enabled).\n" + "</pre>\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_DERIVE_AUTOCOMPLETE = new BatchCommand(
        "ProofAsstDeriveAutocomplete",
        "ProofAsstDeriveAutocomplete\n" + "\n" + "<pre>\n"
            + "\"ProofAsstDeriveAutocomplete\": Yes or No\n" + "\n"
            + "Optional, default is No (disabled).\n" + "</pre>\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_HIGHLIGHTING_ENABLED = new BatchCommand(
        "ProofAsstHighlightingEnabled",
        " ProofAsstHighlightingEnabled\n" + " \n" + " <pre>\n"
            + " \"ProofAsstHighlightingEnabled\": Yes or No\n" + " \n"
            + " Optional, default is Yes (enabled).\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_HIGHLIGHTING_STYLE = new BatchCommand(
        "ProofAsstHighlightingStyle",
        " ProofAsstHighlightingStyle\n" + " \n" + " <pre>\n"
            + " \"ProofAsstHighlightingStyle\": type, color RGB, bold yes/no, italic yes/no\n"
            + " </pre>\n" + " \n"
            + " The color RGB should be given as 6 hex digits (i.e. '00FFC0'), and any of\n"
            + " the three fields can be set to 'Inherit' to use the global\n"
            + " color/bold/italic settings. Style types and defaults are:\n"
            + " \n" + " <pre>\n" + " default: inherit,inherit,inherit\n"
            + " comment: 808080,no,yes\n" + " keyword: 808080,yes,inherit\n"
            + " error: FF0000,yes,inherit\n" + " proof: 808080,no,inherit\n"
            + " step: 8A2908,yes,inherit\n" + " hyp: 8A2908,inherit,inherit\n"
            + " ref: 0044DD,yes,inherit\n"
            + " localref: 008800,inherit,inherit\n"
            + " specialstep: B58900,yes,inherit\n"
            + " class: CC33CC,inherit,inherit\n"
            + " set: FF0000,inherit,inherit\n"
            + " wff: 0000FF,inherit,inherit\n"
            + " workvar: 008800,inherit,inherit\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_FOREGROUND_COLOR_RGB = new BatchCommand(
        "ProofAsstForegroundColorRGB",
        " ProofAsstForegroundColorRGB\n" + " \n" + " <pre>\n"
            + " \"ProofAsstForegroundColorRGB\":\n"
            + "                        \"0,0,0\" -- black (default)\n"
            + "                        thru\n"
            + "                        \"255,255,255\" -- white\n" + " \n"
            + " Optional, default is \"0,0,0\" (black)\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_BACKGROUND_COLOR_RGB = new BatchCommand(
        "ProofAsstBackgroundColorRGB", //
        " ProofAsstBackgroundColorRGB\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"ProofAsstBackgroundColorRGB\":\n"
            + "                        \"255,255,255\" -- white (default)\n"
            + "                        thru\n"
            + "                        \"0,0,0\" -- black\n" + " \n"
            + " Optional, default is \"255,255,255\" (white)\n" + " </pre>\n"
            + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_FONT_FAMILY = new BatchCommand(
        "ProofAsstFontFamily", // "Monospaced",
        " ProofAsstFontFamily\n" + " \n" + " <pre>\n"
            + " \"ProofAsstFontFamily\": \"Monospaced\", (the default),\n"
            + "                        \"Serif\",\n"
            + "                        \"SansSerif\",\n"
            + "                        \"Monospaced\",\n"
            + "                        \"Dialog\",\n"
            + "                        \"DialogInput\"...\n"
            + "                        etc.\n"
            + " One way to view the list of Font Family Names defined\n"
            + " on a system is to input an invalid Font Family Name\n"
            + " on the ProofAsstFontFamily command -- a list will be\n"
            + " displayed as part of a punitively long error message :)\n"
            + " \n"
            + " NOTE!!! Fixed-width fonts such as Monospaced or Courier\n"
            + "         are essential for Proof Assistant if you plan\n"
            + "         on using the Text Mode Formula Formatting\n"
            + "         (TMFF) alignment Methods such as AlignColumn.\n"
            + "         TMFF will not align formula symbols properly\n"
            + "         when proportional fonts are used!!!\n" + " \n"
            + " Optional, default is \"Monospaced\"\n" + " </pre>\n" + "\n");
    // "Courier New",
    // etc.

    public static final BatchCommand RUNPARM_PROOF_ASST_FONT_BOLD = new BatchCommand(
        "ProofAsstFontBold", // yes,
        " ProofAsstFontBold\n" + " \n" + " <pre>\n"
            + " \"ProofAsstFontBold\": Yes or No\n" + " \n"
            + " Optional, default is Yes (bold).\n" + " </pre>\n" + "\n");
    // no

    public static final BatchCommand RUNPARM_PROOF_ASST_LINE_SPACING = new BatchCommand(
        "ProofAsstLineSpacing",
        " ProofAsstLineSpacing\n" + " \n" + " <pre>\n"
            + " \"ProofAsstLineSpacing\": float value, default 0\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_FONT_SIZE = new BatchCommand(
        "ProofAsstFontSize", // 8,
        " ProofAsstFontSize\n" + " \n" + " <pre>\n"
            + " \"ProofAsstFontSize\": 8 or 9, 10, 11, 12, 14, 16, 18\n"
            + "                      20, 22, 24, 26, 28, 36, 48, 72\n" + " \n"
            + " Optional, default is 14 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");
    // 9,
    // ...

    public static final BatchCommand RUNPARM_PROOF_ASST_LINE_WRAP = new BatchCommand(
        "ProofAsstLineWrap",
        " ProofAsstLineWrap\n" + " \n" + " <pre>\n"
            + " \"ProofAsstLineWrap\":\n" + "     equal to 'on'\n"
            + "     or       'off'\n" + " \n"
            + " Controls whether or not text displayed in the proof\n"
            + " window wraps around when the number of columns of\n"
            + " text exceeds ProofAsstTextColumns.\n" + " \n"
            + " Optional, default is 'off' (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_TEXT_COLUMNS = new BatchCommand(
        "ProofAsstTextColumns",
        " ProofAsstTextColumns\n" + " \n" + " <pre>\n"
            + " \"ProofAsstTextColumns\":\n" + "     greater than 39 and\n"
            + "     less than 1000\n" + " \n"
            + " Controls program formatting, not user-input formulas.\n"
            + " Defines the column width of the window, which can\n"
            + " be greater than or less than the width of the screen\n"
            + " or the formulas! Primary effect seen with LineWrap ON\n"
            + " because intra-formula line breaks are done with spaces\n"
            + " (and because a double newline is needed at end of\n"
            + " formulas for legibility reasons.)\n" + " \n"
            + " Optional, default is 80 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_TEXT_ROWS = new BatchCommand(
        "ProofAsstTextRows",
        " ProofAsstTextRows\n" + " \n" + " <pre>\n"
            + " \"ProofAsstTextRows\":\n" + "     greater than 1 and\n"
            + "     less than 100\n" + " \n"
            + " Provides a clue to the system about how big to make\n"
            + " the ProofAsstGUI proof text area window.\n" + " \n"
            + " Optional, default is 21 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_MAXIMIZED = new BatchCommand(
        "ProofAsstMaximized",
        " ProofAsstMaximized\n" + " \n" + " <pre>\n"
            + " \"ProofAsstMaximized\":\n"
            + "     'yes' or 'no' or 'y' or 'n' or 'Y' or 'N'\n" + " \n"
            + " If 'yes', maximizes the ProofAsstGUI main window on startup.\n"
            + " \n"
            + " Optional, default is 'no' (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_TEXT_AT_TOP = new BatchCommand(
        "ProofAsstTextAtTop",
        " ProofAsstTextAtTop\n" + " \n" + " <pre>\n"
            + " \"ProofAsstTextAtTop\":\n"
            + "     'yes' or 'no' or 'y' or 'n' or 'Y' or 'N'\n" + " \n"
            + " If 'yes', positions the ProofAsstGUI proof text area\n"
            + " above the error message text area; otherwise, their\n"
            + " positions are reversed (error messages at top).\n" + " \n"
            + " Optional, default is 'yes' (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_INCOMPLETE_STEP_CURSOR = new BatchCommand(
        "ProofAsstIncompleteStepCursor",
        " ProofAsstIncompleteStepCursor\n" + " \n" + " <pre>\n"
            + " \"ProofAsstIncompleteStepCursor\":\n"
            + "     'First', 'Last', or 'AsIs' (not case sensitive).\n" + " \n"
            + " Pertains to cursor positioning when no unification\n"
            + " errors found and there is at least one incomplete\n"
            + " proof step; 'First' means position cursor to the\n"
            + " first incomplete proof step, etc.\n" + " \n"
            + " The cursor is positioned to the Ref sub-field within\n"
            + " a proof step.\n" + " \n"
            + " Optional, default is 'Last' (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_ERROR_MESSAGE_ROWS = new BatchCommand(
        "ProofAsstErrorMessageRows",
        " ProofAsstErrorMessageRows\n" + " \n" + " <pre>\n"
            + " \"ProofAsstErrorMessageRows\":\n" + "     greater than 1 and\n"
            + "     less than 100\n" + " \n"
            + " Provides a clue to the system about how big to make\n"
            + " the ProofAsstGUI error message text area window.\n" + " \n"
            + " Optional, default is 4 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_ERROR_MESSAGE_COLUMNS = new BatchCommand(
        "ProofAsstErrorMessageColumns",
        " ProofAsstErrorMessageColumns\n" + " \n" + " <pre>\n"
            + " \"ProofAsstErrorMessageColumns\":\n"
            + "     greater than 39 and\n" + "     less than 1000\n" + " \n"
            + " Provides a clue to the system about how wide to make\n"
            + " the ProofAsstGUI error message text area window.\n" + " \n"
            + " Optional, default is 80 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_FORMULA_LEFT_COL = new BatchCommand(
        "ProofAsstFormulaLeftCol",
        " ProofAsstFormulaLeftCol\n" + " \n" + " <pre>\n"
            + " \"ProofAsstFormulaLeftCol\":\n" + "     greater than 1 and\n"
            + "     less than ProofAsstFormulaRightCol\n" + " \n"
            + " Controls program formatting, not user-input formulas.\n" + " \n"
            + " Optional, default is 20 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_FORMULA_RIGHT_COL = new BatchCommand(
        "ProofAsstFormulaRightCol",
        " ProofAsstFormulaRightCol\n" + " \n" + " <pre>\n"
            + " \"ProofAsstFormulaRightCol\":\n"
            + "     greater than ProofAsstFormulaLeftCol and\n"
            + "     less than 9999\n" + " \n"
            + " Controls program formatting, not user-input formulas.\n" + " \n"
            + " Optional, default is 79 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_RPN_PROOF_LEFT_COL = new BatchCommand(
        "ProofAsstRPNProofLeftCol",
        " ProofAsstRPNProofLeftCol\n" + " \n" + " <pre>\n"
            + " \"ProofAsstRPNProofLeftCol\":\n" + "     greater than 3 and\n"
            + "     less than ProofAsstRPNProofRightCol\n" + " \n"
            + " Controls program formatting of generated proof statements\n"
            + " \n" + " Optional, default is 6 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_RPN_PROOF_RIGHT_COL = new BatchCommand(
        "ProofAsstRPNProofRightCol",
        " ProofAsstRPNProofRightCol\n" + " \n" + " <pre>\n"
            + " \"ProofAsstRPNProofRightCol\":\n"
            + "     greater than ProofAsstRPNProofLeftCol and\n"
            + "     less than 9999\n" + " \n"
            + " Controls program formatting of generated proof statements\n"
            + " \n" + " Optional, default is 79 (see mmj.pa.PaConstants.java)\n"
            + " </pre>\n" + "\n");

    @Deprecated
    public static final BatchCommand RUNPARM_PROOF_ASST_MAX_UNIFY_ALTERNATES = new BatchCommand(
        "ProofAsstMaxUnifyAlternates",
        " ProofAsstMaxUnifyAlternates DEPRECATED\n" + "\n");

    @Deprecated
    public static final BatchCommand RUNPARM_PROOF_ASST_MAX_UNIFY_HINTS = new BatchCommand(
        "ProofAsstMaxUnifyHints",
        " ProofAsstMaxUnifyHints DEPRECATED\n" + "\n");

    @Deprecated
    public static final BatchCommand RUNPARM_PROOF_ASST_UNIFY_HINTS_IN_BATCH = new BatchCommand(
        "ProofAsstUnifyHintsInBatch",
        " ProofAsstUnifyHintsInBatch DEPRECATED\n" + "\n");

    public static final BatchCommand RUNPARM_STEP_SELECTOR_MAX_RESULTS = new BatchCommand(
        "StepSelectorMaxResults",
        " StepSelectorMaxResults\n" + " <p>\n"
            + " Limits the number of unifying assertions returned by the\n"
            + " StepSelectorSearch.\n" + " <p>\n"
            + " Optional, default is 50 (see mmj.pa.PaConstants.java)\n"
            + "\n");

    public static final BatchCommand RUNPARM_STEP_SELECTOR_SHOW_SUBSTITUTIONS = new BatchCommand(
        "StepSelectorShowSubstitutions",
        " StepSelectorShowSubstitutions\n" + " <p>\n"
            + " Determines whether or not unifying assertions are shown as is or with the\n"
            + " substitutions required by unification.\n" + " <p>\n"
            + " Default is true (see mmj.pa.PaConstants.java)\n" + "\n");

    public static final BatchCommand RUNPARM_STEP_SELECTOR_DIALOG_PANE_WIDTH = new BatchCommand(
        "StepSelectorDialogPaneWidth",
        " StepSelectorDialogPaneWidth\n" + " <p>\n"
            + " Sets the pixel width of the StepSelectorDialog.\n" + " <p>\n"
            + " Optional, default is 720 (see mmj.pa.PaConstants.java)\n"
            + "\n");

    public static final BatchCommand RUNPARM_STEP_SELECTOR_DIALOG_PANE_HEIGHT = new BatchCommand(
        "StepSelectorDialogPaneHeight",
        " StepSelectorDialogPaneHeight\n" + " <p>\n"
            + " Sets the pixel width of the StepSelectorDialog.\n" + " <p>\n"
            + " Optional, default is 440 (see mmj.pa.PaConstants.java)\n"
            + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_ASSRT_LIST_FREESPACE = new BatchCommand(
        "ProofAsstAssrtListFreespace",
        " ProofAsstAssrtListFreespace\n" + " <p>\n"
            + " Sets the amount of freespace in the ArrayLists used in the Proof\n"
            + " Assistant.\n" + " <p>\n"
            + " Optional, default is 5, minimum 0, maximum 1000.\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_OUTPUT_CURSOR_INSTRUMENTATION = new BatchCommand(
        "ProofAsstOutputCursorInstrumentation",
        " ProofAsstOutputCursorInstrumentation\n" + " <p>\n" + " <code>\n"
            + " \"ProofAsstOutputCursorInstrumentation\": yes or no.\n"
            + " <p>\n" + " Used to generate \"instrumentation\" info messages\n"
            + " for use in regression testing. OutputCursor\n"
            + " state information is generated by ProofAsst.java\n"
            + " at the end of main functions, such as \"unify\".\n" + " <p>\n"
            + " <p>\n"
            + " Optional, default is no (see mmj.pa.PaConstants.java)\n" + " \n"
            + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_AUTO_REFORMAT = new BatchCommand(
        "ProofAsstAutoReformat",
        " ProofAsstAutoReformat\n" + " <p>\n" + " <code>\n"
            + " \"ProofAsstAutoReformat\": yes or no.\n" + " <p>\n"
            + " Specifies whether or not proof step formulas are\n"
            + " automatically reformatted after work variables\n"
            + " are resolved.\n" + " <p>\n"
            + " Optional, default is yes (see mmj.pa.PaConstants.java)\n"
            + " \n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_UNDO_REDO_ENABLED = new BatchCommand(
        "ProofAsstUndoRedoEnabled",
        " ProofAsstUndoRedoEnabled RunParm.\n" + " <p>\n" + " <code>\n"
            + " Controls whether or not the Proof Assistant GUI\n"
            + " provides Undo/Redo support.\n" + " <p>\n"
            + " Normally this is turned on, but if desired, say\n"
            + " for performance reasons, the user can disable\n"
            + " Undo/Redo at start-up time via RunParm.\n" + " <p>\n"
            + " Optional. Default = yes.\n" + " </code>\n" + "\n");

    @Deprecated
    public static final BatchCommand RUNPARM_PROOF_ASST_DUMMY_VAR_PREFIX = new BatchCommand(
        "ProofAsstDummyVarPrefix",
        " ProofAsstDummyVarPrefix\n" + " <p>\n" + " <code> \n"
            + " \"ProofAsstDummyVarPrefix\": length > 0, no embedded blanks\n"
            + " or unprintable characters.\n" + " \n"
            + " Dummy variables used to display un-determined variable\n"
            + " substitutions are given a prefix string and a number.\n"
            + " For example: $1, $2, etc.\n" + " \n"
            + " Optional, default is \"$ (see mmj.pa.PaConstants.java)\n"
            + " + \n</code></p>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_DEFAULT_FILE_NAME_SUFFIX = new BatchCommand(
        "ProofAsstDefaultFileNameSuffix",
        " ProofAsstDefaultFileNameSuffix\n" + " <p>\n" + " <code>\n"
            + " \"ProofAsstDefaultFileNameSuffix\": \".txt\", \".TXT\",\n"
            + "                                   \".mmp\" or \".MMP\"\n"
            + " <p>\n"
            + " Optional. If this RunParm is not provided, the hardcoded\n"
            + " default \".txt\" is used as the default for Proof Worksheet\n"
            + " file names.\n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_PROOF_FOLDER = new BatchCommand(
        "ProofAsstProofFolder",
        "*\n" + " ProofAsstProofFolder\n" + " <p>\n" + " <code>\n"
            + " \"ProofAsstProofFolder\": directory name, no \"\\\" at end\n"
            + "                      of name. Must exist.\n" + " <p>\n"
            + " Optional. If this RunParm is not provided, the user\n"
            + " of ProofAsstGUI is prompted during Save dialogs, and\n"
            + " the folder is remembered for the duration of the\n"
            + " session.\n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_STARTUP_PROOF_WORKSHEET = new BatchCommand(
        "ProofAsstStartupProofWorksheet",
        " ProofAsstStartupProofWorksheet\n" + " <p>\n" + " <code>\n"
            + " \"ProofAsstStartupProofWorksheet\": name of Proof Worksheet\n"
            + "                      file to be displayed at ProofAsstGUI\n"
            + "                      startup. Must exist.\n" + " <p>\n"
            + " Optional. If this RunParm is not provided, a hardcoded\n"
            + " Proof Worksheet (String) is displayd.\n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_RECHECK_PROOF_ASST_USING_PROOF_VERIFIER = new BatchCommand(
        "RecheckProofAsstUsingProofVerifier",
        " RecheckProofAsstUsingProofVerifier\n" + " <p>\n" + " <code> \n"
            + " \"RecheckProofAsstUsingProofVerifier,yes\"\n" + "  or\n"
            + " \"RecheckProofAsstUsingProofVerifier,no\"\n" + " \n"
            + " Optional, default = \"no\". If equal to \"yes\", then each\n"
            + " derivation proof step's generated Metamath RPN proof\n"
            + " is double-checked using the full Metamath Proof Engine,\n"
            + " AKA \"Proof Verifier\". In theory this should be\n"
            + " unnecessary since the Proof Assistant should provide\n"
            + " valid proofs, but it may be useful if question arise,\n"
            + " or if the user has spare CPU cycles and skepticism.\n" + "*"
            + " </code></p>\n" + "\n");

    public static final BatchCommand RUNPARM_RUN_PROOF_ASST_GUI = new BatchCommand(
        "RunProofAsstGUI", // no
        " RunProofAsstGUI\n" + " <p>\n" + " <code> \n"
            + " \"RunProofAsstGUI\": no option values (for now...)\n"
            + " </code></p>\n" + "\n");
    // option
    // values...for
    // now...

    public static final BatchCommand RUNPARM_PROOF_ASST_EXPORT_TO_FILE = new BatchCommand(
        "ProofAsstExportToFile", // options:
        " ProofAsstExportToFile\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"ProofAsstExportToFile\":\n"
            + "                value1 = filename; absolute or\n"
            + "                         relative (to current\n"
            + "                         directory or if provided\n"
            + "                         the ProofAsstProofFolder,\n"
            + "                         which is input via RunParm\n"
            + "                         and also during use of\n"
            + "                         ProofAsstGUI\n" + " \n"
            + "                value2 = \"*\"    - all theorems\n"
            + "                         label  - a single theorem\n"
            + "                         99999  - a given number of theorems\n"
            + " \n" + "                value3 = Optional: new (default),\n"
            + "                         or update\n" + " \n"
            + "                value4 = un-unified (default) or\n"
            + "                                   unified.\n" + " \n"
            + "                value5 = \"Correct\" (deprecated \"NotRandomized\",\n"
            + "                         default), \"Randomized\", \"Reverse\" and\n"
            + "                         others (see <code> mmj.verify.HypsOrder</code>).\n"
            + "                         Controls order of exported proof\n"
            + "                         step logical hypotheses (a testing\n"
            + "                         feature).\n" + " \n"
            + "                value6 = Print or NoPrint (default)\n"
            + "                         Print requests copy of Proof \n"
            + "                         Worksheet to be sent to the\n"
            + "                         SystemOutputFile (or System.out)\n"
            + "                         in addition to the export file.\n"
            + " \n"
            + "               value7 = \"DeriveFormulas\" or \"NoDeriveFormulas\"\n"
            + "                         (default) or \"\". If \"DeriveFormulas\"\n"
            + "                         then the exported Proof Worksheets\n"
            + "                         are written with blank formulas to\n"
            + "                         trigger the Derive Formula feature\n"
            + "                         in the Proof Assistant during later\n"
            + "                         import. Note that the theorem's\n"
            + "                         logical hypotheses and \"qed\" step\n"
            + "                         cannot be derived -- formula is\n"
            + "                         always required for these steps,\n"
            + "                         so \"DeriveFormulas\" applies only to\n"
            + "                         non-Qed derivation proof steps.\n"
            + " \n"
            + " This RunParm is provided for use in high-volume testing.\n"
            + " It exports proofs to a file in the format required\n"
            + " by the Proof Assistant GUI. To import the proof file and\n"
            + " test the Unification function, use RunParm\n"
            + " 'ProofAsstBatchTest', specifying the file name.\n" + " \n"
            + " Option value3 has two variations: un-unified means the\n"
            + " exported derivation proof steps do not have Ref labels,\n"
            + " whereas unified means Ref labels are present.\n" + " \n"
            + " Note: this feature is not a full export of a Metamath\n"
            + " file as it does not export $d or anything besides theorems\n"
            + " and their logical hypotheses.\n" + " \n"
            + " Note: a relative filename such as \"export.mmp\" can be\n"
            + " input or an \"absolute\" name such as \"c:\\my\\export.mmp\".\n"
            + " The \"ProofAsstProofFolder\", if present, is used with\n"
            + " relative filename. And take care to note that if export\n"
            + " is performed *after* ProofAsstGUI, the ProofAsstProofFolder\n"
            + " may have been changed.\n" + " </pre></p>\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_OPTIMIZE_THEOREM_SEARCH = new BatchCommand(
        "ProofAsstOptimizeTheoremSearch",
        "Perform the optimizations for theorem search during \"parallel\"\n"
            + "unification.\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_USE_AUTOTRANSFORMATIONS = new BatchCommand(
        "ProofAsstUseAutotransformations",
        "Auto-transformation options (it is temporary option and could be changed any moment):\n <p>"
            + "    value1 = Yes/No (use or do not use auto-transformations)\n <p>"
            + "    value2 = Yes/No (use debug output or do not use it)\n"
            + "    value3 = Yes/No (support implication prefix)\n" + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_BATCH_TEST = new BatchCommand(
        "ProofAsstBatchTest", // options
        " ProofAsstBatchTest\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"ProofAsstBatchTest\": value1 = selection, either\n" + " \n"
            + "                      \"*\"    - all theorems\n"
            + "                      label  - a single theorem\n"
            + "                      99999  - a given number of theorems\n"
            + " \n" + "                      value2 = Optional: \n"
            + "                               a file name, either absolute\n"
            + "                               or relative (to the current\n"
            + "                               directory, or if provided\n"
            + "                               the ProofAsstProofFolder,\n"
            + "                               which is input via RunParm\n"
            + "                               and also during use of\n"
            + "                               ProofAsstGUI<.) If no file\n"
            + "                               name input, skeleton proofs\n"
            + "                               are generated from memory \n"
            + "                               (the .mm file loaded :)\n" + " \n"
            + "                value3 = un-unified (default) or\n"
            + "                         unified proof format.\n" + " \n"
            + "                value4 = \"Correct\" (deprecated \"NotRandomized\",\n"
            + "                         default), \"Randomized\", \"Reverse\" and\n"
            + "                         others (see <code> mmj.verify.HypsOrder</code>).\n"
            + "                         Controls order of exported proof\n"
            + "                         step logical hypotheses (a testing\n"
            + "                         feature).\n" + " \n"
            + "                value5 = Print or NoPrint (default)\n"
            + "                         Print requests copy of Proof \n"
            + "                         Worksheet to be sent to the\n"
            + "                         SystemOutputFile (or System.out)\n"
            + "                         in addition to the export file.\n"
            + " \n"
            + "                value6 = \"DeriveFormulas\" or \"NoDeriveFormulas\"\n"
            + "                         (default) or \"\". If \"DeriveFormulas\"\n"
            + "                         then the exported Proof Worksheets\n"
            + "                         are written with blank formulas to\n"
            + "                         trigger the Derive Formula feature\n"
            + "                         in the Proof Assistant during later\n"
            + "                         import. Note that the theorem's\n"
            + "                         logical hypotheses and \"qed\" step\n"
            + "                         cannot be derived -- formula is\n"
            + "                         always required for these steps,\n"
            + "                         so \"DeriveFormulas\" applies only to\n"
            + "                         non-Qed derivation proof steps.\n"
            + " \n"
            + "                value7 = \"CompareDJs\" or \"NoCompareDJs\"\n"
            + "                          (default) or \"\".\n" + " \n"
            + "                          See mmj2\\data\\runparm\\windows\n"
            + "                          \\AnnotatedRunParms.txt for more\n"
            + "                          info.\n" + " \n"
            + "                value8 = \"UpdateDJs\" or \"NoUpdateDJs\"\n"
            + "                         (default) or \"\".\n" + " \n"
            + "                          See mmj2\\data\\runparm\\windows\n"
            + "                          \\AnnotatedRunParms.txt for more\n"
            + "                          info.\n" + " </pre>\n" + " <p>\n"
            + " This RunParm is provided for use in high-volume testing.\n"
            + " <p>\n"
            + " RunParm option value2 is input to specify an input file containing proofs\n"
            + " in the format used on the Proof Assistant GUI screen. This is optional,\n"
            + " and if not provided, the program simulates an input file using the\n"
            + " currently loaded Metamath data\n" + " <p>\n"
            + " In \"simulation\" mode (no input file), the program exports a proof\n"
            + " \"to memory\", just as it would have been created for the\n"
            + " ProofAsstExportToFile RunParm (which is why the unified/un-unified and\n"
            + " Randomized/ NotRandomized options are provided here also.) The The\n"
            + " export-simulated proof is run through the Unification process for testing\n"
            + " purposes. RunParm option value1 provides a selection capability, and this\n"
            + " capability works with or without an input file. Specify \"*\" to test\n"
            + " unification of all proofs, either in the input file or those loaded into\n"
            + " the system. Specifying a number, for example 99, runs the test for the\n"
            + " first 99 theorems (database sequence if input file not provided).\n"
            + " Finally, specifying a theorem label runs the test for just that one\n"
            + " theorem. Note: a relative filename such as \"export.mmp\" can be input or\n"
            + " an \"absolute\" name such as \"c:\\my\\export.mmp\". The\n"
            + " \"ProofAsstProofFolder\", if present, is used with relative filename. And\n"
            + " take care to note that if export is performed *after* ProofAsstGUI, the\n"
            + " ProofAsstProofFolder may have been changed.</p>\n" + "\n");
    // selection
    // and
    // optional
    // file
    // name.
    public static final BatchCommand RUNPARM_STEP_SELECTOR_BATCH_TEST = new BatchCommand(
        "StepSelectorBatchTest", // all
        " StepSelectorBatchTest\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"StepSelectorBatchTest\":\n" + " \n"
            + "                value1 = Mandatory: \n"
            + "                         a file name, either absolute\n"
            + "                         or relative (to the current\n"
            + "                         directory, or if provided\n"
            + "                         the ProofAsstProofFolder,\n"
            + "                         which is input via RunParm\n"
            + "                         and also during use of\n"
            + "                         ProofAsstGUI<.)\n" + " \n"
            + "                value2 = cursor position:\n"
            + "                         char offset position in Proof Worksheet\n"
            + " \n" + "                value3 = selection number\n"
            + "                         zero to 99999999.\n" + " </pre>\n"
            + " <p>\n" + " This RunParm is provided for regression testing.\n"
            + " <p>\n"
            + " Specify the cursor position within the Proof Worksheet and the number to\n"
            + " be selected from the StepSelectorDialog for the request. The program\n"
            + " initiates a StepSelectorSearch and then if there are no errors, selects\n"
            + " the chosen item from the StepSelectorResults and invokes unify().\n"
            + " <p>\n"
            + " The StepSelectorResults are printed, as well as the ProofWorksheet after\n"
            + " unification -- and any messages.\n" + "\n");

    public static final BatchCommand RUNPARM_PREPROCESS_REQUEST_BATCH_TEST = new BatchCommand(
        "PreprocessRequestBatchTest", // all
        " PreprocessRequestBatchTest\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"PreprocessRequestBatchTest\":\n" + " \n"
            + "                value1 = Mandatory: \n"
            + "                         a file name, either absolute\n"
            + "                         or relative (to the current\n"
            + "                         directory, or if provided\n"
            + "                         the ProofAsstProofFolder,\n"
            + "                         which is input via RunParm\n"
            + "                         and also during use of\n"
            + "                         ProofAsstGUI<.)\n" + " \n"
            + "                value2 = \"EraseAndRederiveFormulas\" is the only\n"
            + "                         valid option at this time.\n"
            + " </pre>\n" + " <p>\n"
            + " This RunParm is provided for regression testing.\n" + " <p>\n"
            + " The Proof Text is printed before and after preprocessing and unification.\n");
    // options
    // mandatory:
    // filename,
    // and
    // request
    // name

    @Deprecated
    public static final BatchCommand RUNPARM_SET_MM_DEFINITIONS_CHECK = new BatchCommand(
        "SetMMDefinitionsCheckWithExclusions",
        " SetMMDefinitionsCheckWithExclusions\n" + " <p>\n" + " <pre> \n"
            + " \"SetMMDefinitionsCheckWithExclusions\":\n"
            + "     options = Assrt labels, comma separated, with * wildcard\n"
            + " </pre>\n" + " <p>\n"
            + " This option runs a soundness check on all axioms in the database, except\n"
            + " those specified in the list. Recommended exclusions are\n"
            + " <code>ax-*,df-bi,df-clab,df-cleq,df-clel</code>, which will always fail the\n"
            + " check.\n" + " <p>\n"
            + " This RunParm option is deprecated; its functionality has been replaced by\n"
            + " <code>RunMacro,definitionCheck,...</code> with the same arguments.\n"
            + "\n");

    public static final BatchCommand RUNPARM_PROOF_ASST_UNIFY_SEARCH_EXCLUDE = new BatchCommand(
        "ProofAsstUnifySearchExclude", // options
        " ProofAsstUnifySearchExclude\n" + " <p>\n" + " <code> \n"
            + " \"ProofAsstUnifySearchExclude\": options = Assrt labels, comma\n"
            + "                                separated (ex: biigb,xxxid)\n"
            + " </code></p>\n" + " <p>\n"
            + " NOTE: The RunParm validation for these excluded Assrt labels will be very\n"
            + " lenient and will just ignore labels that are \"invalid\" or not in the\n"
            + " Statment Table. The reason is that the exclusion list is expected to be\n"
            + " very stable and the new RunParm \"LoadEndpointStmtNbr\" allows loading of\n"
            + " just a portion of a Metamath file; if we required perfection in the\n"
            + " exclusion list the usability of LoadEndPointStmtNbr would drop\n"
            + " dramatically (see also LoadEndpointStmtLabel).\n" + " <p>\n"
            + " This RunParm instructs ProofUnifier.java to not attempt to unify the\n"
            + " specified assertion labels with any proof steps -- unless the user\n"
            + " specifically enters them on a proof step.\n" + " <p>\n"
            + " The Unification process scans the loaded Metamath file assertion\n"
            + " (LogicalSystem.stmtTbl) in ascending database sequence and accepts the\n"
            + " first match it finds. Generally that works fine, but in a few cases, such\n"
            + " as duplicate theorems that are present simply because of an alternate\n"
            + " proof, this feature is helpful (though it would possibly be easier to put\n"
            + " biigb after bii and avoid the situation in the first place.)\n"
            + " <p>\n"
            + " The *problem* of multiple valid unifications for a proof step may affect\n"
            + " a small number of theorems. The list of alternatives can be obtained by\n"
            + " specifically entering a valid assertion label that does *not* unify --\n"
            + " the program then provides a message detailing the possible choices. (The\n"
            + " message with alternatives is also produced if there is a Distinct\n"
            + " Variables error on a proof step and there is no unifying assertion that\n"
            + " doesn't have a Distinct Variables error.) In set.mm p0ex and snex are\n"
            + " appear as alternatives in a few proofs; mulid1 and mulid2 are another\n"
            + " example.</p>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.tmff.Preferences.java interface
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_TMFF_DEFINE_SCHEME = new BatchCommand(
        "TMFFDefineScheme",
        " TMFFDefineScheme command.\n" + " <p>\n"
            + " Defines TMFF Schemes that may be referenced subsequently in TMFF Formats\n"
            + " (TMFFDefineFormat can only refer to a TMFF Scheme that is already\n"
            + " defined.)\n" + " <p>\n"
            + " Note: a Scheme can be re-defined in a subsequent RunParm. This would\n"
            + " normally be of use only in a testing situation.\n" + " <p>\n"
            + " <code>\n" + " Parameters:\n" + " <ol>\n"
            + "   <li>Scheme Name: must be non-blank, unique, not =\n"
            + "       \"Unformatted\". Not case sensitive.\n"
            + "   <li>Method Name: = \"AlignColumn\" or \"Flat\". Not case\n"
            + "       sensitive.\n"
            + "   <li>MaxDepth = subtree depth max before triggering break\n"
            + "   <li>ByValue = \"Var\", \"Sym\", or \"Cnst\" (AlignColumn only)\n"
            + "   <li>AtNbr = 1, 2, or 3 (AlignColumn only)\n"
            + "   <li>AtValue = \"Var\", \"Sym\" or \"Cnst\" (AlignColumn only)\n"
            + " </ol>\n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_TMFF_DEFINE_FORMAT = new BatchCommand(
        "TMFFDefineFormat",
        " TMFFDefineFormat command.\n" + " <p>\n"
            + " Defines TMFF Formats that may be referenced subsequently in the\n"
            + " TMFFUseFormat command (TMFFUseFormat can only refer to a TMFF Scheme that\n"
            + " is already defined, which includes the pre-defined, built-in Formats.)\n"
            + " <p>\n"
            + " Note: a Format can be re-defined in a subsequent RunParm. This would\n"
            + " normally be of use only in a testing situation.\n" + " <p>\n"
            + " <code>\n" + " Parameters:\n" + " <ol>\n"
            + "   <li>Format Nbr: 1, 2 or 3.\n"
            + "   <li>Scheme Name: must be non-blank, unique, not =\n"
            + "       \"Unformatted\". Not case sensitive.\n" + " </ol>\n"
            + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_TMFF_USE_FORMAT = new BatchCommand(
        "TMFFUseFormat",
        " TMFFUseFormat command.\n" + " <p>\n"
            + " Specifies which TMFF Format is in use during subsequent processing.\n"
            + " <p>\n"
            + " Note: multiple TMFFUseFormat commands can be input, but only one format\n"
            + " can be in effect at a single time.\n" + " <p>\n"
            + " Note: Format '0' = Unformatted, turn TMFF off/disabled.\n"
            + " <p>\n" + " <code>\n" + " Parameters:\n" + " <ol>\n"
            + "   <li>Format Nbr: 0, 1, 2, 3, etc.\n" + " </ol>\n"
            + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_TMFF_ALT_FORMAT = new BatchCommand(
        "TMFFAltFormat",
        " TMFFAltFormat command.\n" + " <p>\n"
            + " Specifies the alternate TMFF Format to be used when the ProofAsstGUI\n"
            + " Edit/Reformat Proof - Swap Alt menu item is selected.\n"
            + " <p>\n" + " <p>\n" + " <code>\n" + " Parameters:\n" + " <ol>\n"
            + "   <li>Format Nbr: 0, 1, 2, 3, etc.\n" + " </ol>\n"
            + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_TMFF_USE_INDENT = new BatchCommand(
        "TMFFUseIndent",
        " TMFFUseIndent command.\n" + " <p>\n"
            + " Specifies the number of columns to indent a proof step formula for each\n"
            + " level in the proof tree.\n" + " <p>\n" + " <p>\n" + " <code>\n"
            + " Parameters:\n" + " <ol>\n"
            + "   <li>Indent Amount: 0, 1, 2, 3, or 4.\n" + " </ol>\n"
            + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_TMFF_ALT_INDENT = new BatchCommand(
        "TMFFAltIndent",
        " TMFFAltIndent command.\n" + " <p>\n"
            + " Specifies the number of columns to indent a proof step formula for each\n"
            + " level in the proof tree. Specifies the alternate TMFF Indent Amount to be\n"
            + " used when the ProofAsstGUI Edit/Reformat Proof - Swap Alt menu item is\n"
            + " selected.\n" + " <p>\n" + " <p>\n" + " <code>\n"
            + " Parameters:\n" + " <ol>\n"
            + "   <li>Alt Indent Amount: 0, 1, 2, 3, or 4.\n" + " </ol>\n"
            + " </code>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.WorkVarBoss interface to WorkVarManager
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_DEFINE_WORK_VAR_TYPE = new BatchCommand(
        "DefineWorkVarType",
        " DefineWorkVarType command.\n" + " \n" + " <pre>\n"
            + " : - Optional. May appear anywhere after the \"Parse\" RunParm\n"
            + "              within an input RunParm file, and takes effect\n"
            + "              when the next DeclareWorkVars RunParm command is\n"
            + "              processed. If not input prior to first use -- the\n"
            + "              Proof Assistant -- the default settings are\n"
            + "              automatically used.\n" + " \n"
            + "  - Default = One default DefineWorkVarType RunParm is\n"
            + "              generated for each grammatical Type Code.\n"
            + "              specifying a prefix of \"&x\" where \"x\" is\n"
            + "              the first character of the grammatical\n"
            + "              type code, converted to lower case if\n"
            + "              necessary; 100 work variables are defined\n"
            + "              by default for each grammatical type code.\n"
            + " \n"
            + "  - Value1 = Grammatical Type Code (e.g. \"wff\", \"class\",\n"
            + "             \"set\", etc.) Must be a valid grammatical\n"
            + "             Type Code.\n" + " \n"
            + "  - Value2 = Work Variable Prefix for the grammatical\n"
            + "             Type Code. Must generate unique variable and\n"
            + "             variable hypothesis names when concatenated\n"
            + "             with the Work Variable numerical suffix (1,\n"
            + "             2, ..., 11, ..., etc.) Note that Work\n"
            + "             Variable Hypothesis labels are generated\n"
            + "             automatically and are the same as the Work\n"
            + "             Variables. A Work Variable Prefix must\n"
            + "             consist solely of valid Metamath math\n"
            + "             symbol characters (not \"$\", for example,\n"
            + "             or embedded blanks.)\n" + " \n"
            + "  - Value3 = Number of Work Variables to be declared for the\n"
            + "             grammatical Type Code. Must be greater than 9\n"
            + "             and less than 1000 (\"stinginess\" is recommended to\n"
            + "             avoid wasted processing and memory allocations...\n"
            + "             but, in the event that the supply of available\n"
            + "             Work Variables is exhausted during processing\n"
            + "             a pop-up GUI error message will be displayed; the\n"
            + "             RunParms will need to be modified and re-input\n"
            + "             in a subsequent run...)\n" + " \n"
            + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         DefineWorkVarType,wff,&W,100\n"
            + "         DefineWorkVarType,set,&S,100\n"
            + "         DefineWorkVarType,class,&C,100\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_DECLARE_WORK_VARS = new BatchCommand(
        "DeclareWorkVars",
        " DeclareWorkVars command.\n" + " \n" + " <pre>\n"
            + "  - Optional. May appear anywhere after the \"Parse\" RunParm\n"
            + "              within an input, and takes effect immediately\n"
            + "              (any existing Work Variables are deleted and\n"
            + "              a new set is created.)\n" + " \n"
            + "  - Default = A default DeclareWorkVars RunParm is executed\n"
            + "              automatically when first need arises (e.g. at\n"
            + "              Proof Assistant start-up), if none have been\n"
            + "              input since the last Clear RunParm or the start\n"
            + "              of the RunParm file.\n" + " \n"
            + "  - Value1 = N/A\n" + " \n" + "  - Examples\n" + " \n"
            + "     *       1         2         3         4\n"
            + "     *234567890123456789012345678901234567890\n"
            + "     DeclareWorkVars\n" + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.SvcBoss interface to SvcCallback
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_SVC_FOLDER = new BatchCommand(
        "SvcFolder",
        " SvcFolder\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"SvcFolder\": directory name, no \"\\\" at end of name.\n"
            + "              Must exist and must be a directory.\n" + " \n"
            + " : - Optional. Must appear prior to the SvcCall RunParm.\n"
            + " \n"
            + "  - Default = If not input, output Svc files are directed\n"
            + "              to the current directory.\n" + " \n"
            + "  - Value1 = Directory Name. No \"\\\" or \"/\" at the end\n"
            + "             of name. Must exist and must be the name of\n"
            + "             a directory. The separator symbol is OS \n"
            + "             dependent (Windows uses \"\\\", *nix/Max = \"/\").\n"
            + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         SvcFolder,c:\\MyFolder\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_SVC_CALLBACK_CLASS = new BatchCommand(
        "SvcCallbackClass",
        " SvcCallbackClass\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"SvcCallbackClass\": Name of class which implements the\n"
            + "              mmj.svc.SvcCallback interface in \"callee\"\n"
            + "              mode. Must have a default constructor.\n" + " \n"
            + "      NOTE: Do not input this RunParm if you are using\n"
            + "            SvcCallback in \"caller\" mode because it will\n"
            + "            override the specific instance of your class\n"
            + "            which you pass as an argument to\n"
            + "            BatchMMJ2.generateSvcCallback()\n" + " \n"
            + " : - Optional. SvcCallback can be provided via a call to\n"
            + "              BatchMMJ2.setSvcCallback().\n" + " \n"
            + "  - Default = None.\n" + " \n"
            + "  - Value1 = SvcCallbackClass class name.\n" + " \n"
            + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         SvcCallbackClass,c:\\MyClass\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_SVC_ARG = new BatchCommand(
        "SvcArg",
        " SvcArg\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"SvcArg\": Key/Value Pair loaded into Map which is passed\n"
            + "           to SvcCallback.go(). Key/Value pairs are minimally\n"
            + "           validated to ensure that each Key is at least\n"
            + "           one character long and unique. The Key/Value parm\n"
            + "           contents are parsed using the same separator\n"
            + "           and delimter characters used for the rest of\n"
            + "           the RunParms. Multiple SvcArgs can be input.\n"
            + " \n" + " : - Optional.\n" + " \n" + "  - Default = None.\n"
            + " \n"
            + "  - Value1 = Key. Non-blank string at least one character\n"
            + "             in length. Must not be a duplicate of any\n"
            + "             other SvcArg key.\n" + " \n"
            + "  - Value2 = Value. String zero or more characters in\n"
            + "             length.\n" + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         SvcArg,OutFilePrefix,exp\n"
            + "         SvcArg,OutFileSuffix,zip\n"
            + "         SvcArg,ZipOutput,yes\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_SVC_CALL = new BatchCommand(
        "SvcCall",
        " SvcCall\n" + " <p>\n" + " \n" + " <pre>\n"
            + " \"SvcCall\": Command to perform call to SvcCallback.go().\n"
            + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         SvcCall\n" + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.GMFFBoss interface
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_GMFF_EXPORT_PARMS = new BatchCommand(
        "GMFFExportParms",
        " GMFFExportParms command.\n" + " \n" + " <pre>\n" + " \n"
            + " - Optional. Default values are shown above. Modifications\n"
            + "   to the defaults as well as additional settings for new\n"
            + "   export types are made with this RunParm. Validation is\n"
            + "   deferred until GMFF Initialization except for the number\n"
            + "   of RunParm parameters -- i.e. use of this RunParm does not\n"
            + "   trigger GMFF Initialization.\n" + " \n"
            + " - May appear anywhere after the \"LoadFile\" RunParm\n"
            + "   but preferably the GMFF RunParms -- if used at\n"
            + "   all -- appear just prior to starting the Proof\n"
            + "   Assistant. For testing purposes, if input Proof\n"
            + "   Worksheet files are used and they contain Work\n"
            + "   Variables then the GMFF RunParms should appear\n"
            + "   after the WorkVar RunParms.\n" + " \n"
            + " - Value1     = Export Type (Unicode or .gif)\n"
            + "   - defaults: althtml and html\n"
            + "   - Export Type must be unique. It is the key in the export\n"
            + "     parms (and text escapes) lists built using default\n"
            + "     settings merged with the input RunParms GMFFExportParms\n"
            + "     entries.\n"
            + "   - A second GMFFExportParms RunParm with the same Export\n"
            + "     Type updates the first.\n" + " \n"
            + " - Value2     = on/off\n" + "   - default ON\n"
            + "   - ON or OFF to enable/disable this export type.\n"
            + "   - Note that by default, both html and althtml are ON.\n"
            + "   - Setting all export types OFF disables GMFF exports.\n"
            + "   - If OFF the rest of the input parameters are not validated\n"
            + "     or stored.\n" + " \n"
            + " - Value3     = Typesetting Definition Keyword in .mm file\n"
            + "               (in the $t typesetting comment) for this export.\n"
            + "   - defaults: althtmldef and htmldef (or latex but latex is\n"
            + "     not supported by the GMFF Model files provided and only\n"
            + "     Model A is coded into the program.)\n" + " \n"
            + " - Value4     = Export Directory.\n"
            + "   - defaults: gmff\\althtml and gmff\\html\n"
            + "   - Directory where exports are written. Also, gmff\\html\n"
            + "     contains .gif files for symbols.\n" + " \n"
            + " - Value5     = export File Type\n"
            + "   - default: .html (.html or .htm might be good choices :-)\n"
            + " \n"
            + " - Value6     = GMFF Models Directory -- Directory containing\n"
            + "                html fragment files serving as models for exports.\n"
            + "   - defaults: gmff\\althtml\\models and GMFF\\html\\models\n"
            + " \n" + " - Value7     = Model Id. Only \"A\" is valid now.\n"
            + "   - defaults: Model Id.\"A\"\n" + " \n"
            + " - Value8     = Charset Encoding name.\n"
            + "   - default: ISO-8859-1\n"
            + "   - Must match the html fragment for the specified Model Id\n"
            + "     which contains the html <head> keyword...but the program\n"
            + "     does not validate this! Model A specifies ISO-8859-1\n"
            + "     (same as Metamath Proof Explorer).\n"
            + "   - Valid charset encodings on all Java platforms are:\n"
            + "     - US-ASCII\n" + "     - ISO-8859-1\n" + "     - UTF-8\n"
            + "     - UTF-16BE\n" + "     - UTF-16LE\n" + "     - UTF-16\n"
            + " \n" + "  - Value9 = OutputFileName \n" + " \n"
            + "              Name of output file minus the file type. \n"
            + "              Optional. \n" + " \n"
            + "              - If not specified the output file name is \n"
            + "                constructed from the proof theorem's label \n"
            + "                + the Export File Type. \n" + " \n"
            + "                - Note! The OutputFileName applies to all \n"
            + "                  exports, including those via the \n"
            + "                  GMFFExportTheorem and GMFFExportFromFolder \n"
            + "                  RunParms in addition to ProofAsstGUI \n"
            + "                  export requests. To export to individual \n"
            + "                  theorem-named files you must input a \n"
            + "                  new GMFEExportTheorems RunParm!!! \n" + " \n"
            + "              - If specified must not contain any \n"
            + "                whitespace characters, or '/' or '\\' or ':' \n"
            + "                characters (for safety.) \n" + " \n"
            + "                - All/any exported Proof Worksheets will be \n"
            + "                  output to the named file suffixed with the \n"
            + "                  GMFFExportParms file type -- except that \n"
            + "                  the GMFFExportTheorem and \n"
            + "                  GMFFExportFromFolder AppendFileName \n"
            + "                  parameter overrides the OutputFileName \n"
            + "                  parameter on the GMFFExportParms RunParm! \n"
            + " \n" + " \n"
            + " - NOTE: There is nothing in the GMFF program code specific\n"
            + "         to html. All html-specific information is external\n"
            + "         to the code, and is specified via the GMFF RunParms,\n"
            + "         the GMFF \\models directory files, and the Metamath\n"
            + "         $t typesetting definitions.\n" + " \n"
            + "         - Since mmj2 allows you to input more than one\n"
            + "           LoadFile RunParm, you could create an extra $t\n"
            + "           comment in a second input .mm file and output\n"
            + "           export data in whatever format you desire...\n"
            + "           the only proviso being that the GMFF code knows\n"
            + "           the names of the \\models files for Model A. So\n"
            + "           either your extra export type must match the\n"
            + "           pattern of \\models files (with regards to the\n"
            + "           parts which are filled in by the code vs. what\n"
            + "           is in the fragments), or another model would\n"
            + "           need to be added to the GMFF code.\n" + " \n"
            + "           - Model A is a \"minimalist\" version of a webpage\n"
            + "             which typesets only proof step formulas plus the\n"
            + "             theorem label, which is output as text but is\n"
            + "             treated as a variable in the model.)\n" + " \n"
            + "           - The one thing you cannot do with this design is\n"
            + "             export to a language which is based on the formula\n"
            + "             parse trees, for example MathML. Exporting and\n"
            + "             typesetting based on parse trees -- as opposed to\n"
            + "             formulas comprised of sequences of symbols -- would\n"
            + "             require extra code in GMFF.\n" + " \n"
            + "      - Examples (these are the defaults):\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFExportParms,althtml,ON,althtmldef,gmff\\althtml,.html,gmff\\althtml\\models,A,ISO-8859-1,general\n"
            + "         GMFFExportParms,html,ON,htmldef,gmff\\html,.html,gmff\\html\\models,A,ISO-8859-1,general\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_GMFF_USER_TEXT_ESCAPES = new BatchCommand(
        "GMFFUserTextEscapes",
        " GMFFUserTextEscapes command.\n" + " <pre>\n" + " - Optional:\n"
            + "   - These \"escapes\" convert certain output text characters\n"
            + "     to an alternative character sequence that represents\n"
            + "     the escaped text characters in the output language\n"
            + "     (e.g. html).\n" + " \n"
            + "     - Escapes are necessary because certain text characters\n"
            + "       which may be used in a Proof Worksheet have special,\n"
            + "       non-text significance in html. Characters such as '&',\n"
            + "     '>', '<', etc. are used in the html language.\n" + " \n"
            + "     - The space character is escaped into \"&nbsp;\" so that\n"
            + "       Proof Worksheet text spacing is maintained (otherwise\n"
            + "       browsers would collapse or ignore output spaces in\n"
            + "       certain situations.)\n" + " \n"
            + " - Value1 - Export Type (Unicode or .gif).\n"
            + "   - Defaults: althtml and html\n"
            + "   - Must match the Export Type on one of the GMFFExportParms\n"
            + "     RunParms or the default GMFFExportParms\n" + " \n"
            + " - ValueN - Decimal number of Metamath ASCII character\n"
            + "            to be \"escaped\" in the output html file.\n"
            + " - ValueN+1\n"
            + "          - Character string to replace escaped character.\n"
            + " \n" + " - Default Escape Pairs (for both html and althtml):\n"
            + "   -  32 (' ') -> \"&nbsp;\"\n"
            + "   -  34 ('\"') -> \"&quot;\"\n"
            + "   -  38 ('&') -> \"&amp;\"\n" + "   -  60 ('<') -> \"&lt;\"\n"
            + "   -  62 ('>') -> \"&gt;\"\n" + " \n"
            + " - NOTE: User Text to be \"escaped\" is whatever text\n"
            + "         in the Proof Worksheet is not \"typeset\" using\n"
            + "         the Metamath $t typesetting definitions --\n"
            + "         and any mmj2 Proof Worksheet text stored in\n"
            + "         a \\models directory (e.g. Proof Worksheet Header\n"
            + "         text contains both \"<\" and \">\", which are stored\n"
            + "         in the \\models directory in escaped format\n"
            + "         (so it does not need to be escaped again.)\n" + " \n"
            + "      - Examples (these are the defaults):\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFUserTextEscapes,html,32,\"&nbsp;\",34,\"&quot;\",38,\"&amp;\",60,\"&lt;\",62,\"&gt;\" \n"
            + "         GMFFUserTextEscapes,althtml,32,\"&nbsp;\",34,\"&quot;\",38,\"&amp;\",60,\"&lt;\",62,\"&gt;\"\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_GMFF_USER_EXPORT_CHOICE = new BatchCommand(
        "GMFFUserExportChoice",
        " GMFFUserExportChoice command.\n" + " \n" + " <pre>\n"
            + " - Optional:\n"
            + "   - These \"escapes\" convert certain output text characters\n"
            + "     to an alternative character sequence that represents\n"
            + "     the escaped text characters in the output language\n"
            + "     (e.g. html).\n" + " \n"
            + " - Value1 - Export Type (Unicode or .gif).\n"
            + "   - Defaults: althtml and html\n"
            + "   - Must match the Export Type on one of the GMFFExportParms\n"
            + "     RunParms or the default GMFFExportParms\n" + " \n"
            + "      - Examples (\"ALL\" is the default):\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFUserExportChoice,ALL \n"
            + "         GMFFUserExportChoice,html \n"
            + "         GMFFUserExportChoice,althtml\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_GMFF_INITIALIZE = new BatchCommand(
        "GMFFInitialize",
        " GMFFInitialize command.\n" + " \n" + " <pre>\n"
            + " - Optional. Forces initialization or re-initialization\n"
            + "             using whatever GMFF RunParm options, default\n"
            + "             settings and Metamath $t typesetting definitions have\n"
            + "             been input.\n"
            + "        - NOTE: GMFFInitialize prints an audit message showing the final set\n"
            + "        of parms in effect: selected Exporter ExportParms,\n"
            + "        UserTextEscapes and UserExportChoice ... plus\n"
            + "        typeset definition symbol counts by def keyword.\n"
            + " \n"
            + "  - The audit report is printed only if GMFF initialization is\n"
            + "    successful.\n" + " \n"
            + "  - May appear anywhere after the \"LoadFile\" RunParm\n"
            + "    but preferably the GMFF RunParms -- if used at\n"
            + "    all -- appear just prior to starting the Proof\n"
            + "    Assistant. For testing purposes, if input Proof\n"
            + "    Worksheet files are used and they contain Work\n"
            + "    Variables then the GMFF RunParms should appear\n"
            + "    after the WorkVar RunParms.\n" + " \n"
            + " - If GMFFInitialize is not used then initialization\n"
            + "   takes place only if/when the first GMFF export is\n"
            + "   attempted. Reinitialization can occur if one or\n"
            + "   more additional LoadFile commands have executed\n"
            + "   since initialization and new Metamath $t typsetting\n"
            + "   definitions have been input. (And of course, the\n"
            + "   \"Clear\" RunParms resets all state variables, which\n"
            + "   would force reinitialization if additional LoadFile\n"
            + "   commands and GMFF export processing were to occur.)\n" + " \n"
            + " - Initialization may result in error messages about\n"
            + "   the contents of the input .mm Metamath file's $t\n"
            + "   typesetting commands, as well as any other start-up\n"
            + "   errors from GMFF.)\n" + " \n"
            + "  - Default = N/A -- GMFF initialization is automatic.\n" + " \n"
            + " \n"
            + " - Value1 -  \"PrintTypesettingDefinitions\" or spaces.\n"
            + "   - Optional\n"
            + "   - Prints the defined symbols and their definitions (replacement\n"
            + "     text.)\n" + " \n" + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFInitialize\n"
            + "         GMFFInitialize,PrintTypesettingDefinitions\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_GMFF_PARSE_METAMATH_TYPESET_COMMENT = new BatchCommand(
        "GMFFParseMetamathTypesetComment",
        " GMFFParseMetamathTypesetComment command.\n" + " \n" + " <pre>\n"
            + " - Optional. Primarily used for testing. Executes\n"
            + "             standalone parse of a single Metamath $t comment\n"
            + "             (does not affect the state of GMFF or anything\n"
            + "             else -- except Messages.)\n" + " \n"
            + "             NOTE: the input file should contain only\n"
            + "             the $t comment!\n" + " \n"
            + "             May appear anywhere after the \"LoadFile\" RunParm.\n"
            + "             (Although it is \"standalone\" and affects only the\n"
            + "             Messages and GMFFManager objects, the LoadFile\n"
            + "             command creates the LogicalSystem object which holds\n"
            + "             the GMFFManager object.)\n" + " \n"
            + "             A dump of the parse results is generated along with\n"
            + "             statistics. The dump is in the form of a very long\n"
            + "             \"info\" message.\n" + " \n"
            + "  - Default = N/A -- used for batch testing.\n" + " \n"
            + "  - Value1 = Typesetting Definition Keyword in .mm file\n"
            + "              (in the $t typesetting comment) to be selected\n"
            + "             for parsing.\n" + " \n"
            + "  - Value2 = directory containing MM file\n" + " \n"
            + "  - Value3 = Metamath .mm file containing just a $t comment.\n"
            + " \n" + " \n" + "  - Value4 -  \"PRINT\" or spaces.\n"
            + "   - Optional\n"
            + "   - Prints the input file as well as the parsed symbols and\n"
            + "     their definitions (replacement text.)\n" + " \n"
            + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFParseMetamathTypesetComment,htmldef,mydirectory,mytypesetdefs.mm\n"
            + "         GMFFParseMetamathTypesetComment,htmldef,mydirectory,mytypesetdefs.mm,PRINT\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_GMFF_EXPORT_FROM_FOLDER = new BatchCommand(
        "GMFFExportFromFolder",
        " GMFFExportFromFolder command.\n" + " \n" + " <pre>\n"
            + " - Optional. Primarily used for testing. Exports Proof Worksheet\n"
            + "             file(s) from a given directory using the current\n"
            + "             parameter settings (export parms, escapes, etc.)\n"
            + " \n"
            + "             May appear anywhere after the \"LoadFile\" RunParm,\n"
            + "             but should appear after Work Var allocations, at\n"
            + "             least.\n" + " \n"
            + "  - Default = N/A -- used for batch testing.\n" + " \n"
            + "  - Value1 = directory containing Proof Worksheet files\n"
            + " \n"
            + "  - Value2 = theorem label or \"*\" (all). If theorem label\n"
            + "             input then it is the starting point of the\n"
            + "             export process, which will export the Max\n"
            + "             Number of files beginning at that label.\n"
            + "             If \"*\" input then the export begins at the\n"
            + "             first label. Either way, files are exported\n"
            + "             in lexicographic order -- i.e. alphabetically.\n"
            + " \n" + "  - Value3 = file type of input Proof Worksheet files\n"
            + "             (normally either .mmp or .mmt)\n" + " \n"
            + "  - Value4 = Max Number of proofs to export. Required.\n" + " \n"
            + "  - Value5 = Append File Name. Name of output file minus\n"
            + "             the file type. Optional. If specified must\n"
            + "             not contain any whitespace characters, or '/'\n"
            + "             or '\\' or ':' characters (for safety.) All\n"
            + "             exported Proof Worksheets will be appended\n"
            + "             to the named file (written at the end instead\n"
            + "             of the beginning.) Used for regression testing.\n"
            + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFExportFromFolder,myproofs,syl,.mmp,1\n"
            + "         GMFFExportFromFolder,myproofs,*,.mmt,100\n"
            + "         GMFFExportFromFolder,myproofs,a2i,.mmt,5,Test20110915a\n"
            + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_GMFF_EXPORT_THEOREM = new BatchCommand(
        "GMFFExportTheorem",
        " GMFFExportTheorem command.\n" + " \n" + " <pre>\n"
            + " - Optional. Primarily used for testing. Exports Proof Worksheet\n"
            + "             file(s) from the loaded Metamath database using\n"
            + "             the current parameter settings (export parms, escapes, etc.)\n"
            + " \n"
            + "             May appear anywhere after the \"LoadFile\" RunParm,\n"
            + "             but should appear after Proof Assistant parameters\n"
            + "             initialized if the default Proof Assistant settings\n"
            + "             are not used.\n" + " \n"
            + "  - Default = N/A -- used for batch testing.\n" + " \n"
            + "  - Value1 = theorem label or \"*\" (all). If theorem label\n"
            + "             input then it is the starting point of the\n"
            + "             export process, which will export the Max\n"
            + "             Number of files beginning at that label.\n"
            + "             If \"*\" input then the export begins at the\n"
            + "             first label. Either way, files are exported\n"
            + "             in MObj.seq number -- i.e. by order of appearance\n"
            + "             in the loaded Metamath database (LogicalSystem.)\n"
            + " \n" + "  - Value2 = Max Number of proofs to export. Required.\n"
            + " \n"
            + "  - Value3 = Append File Name. Name of output file minus\n"
            + "             the file type. Optional. If specified must\n"
            + "             not contain any whitespace characters, or '/'\n"
            + "             or '\\' or ':' characters (for safety.) All\n"
            + "             exported Proof Worksheets will be appended\n"
            + "             to the named file (written at the end instead\n"
            + "             of the beginning.) Used for regression testing.\n"
            + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         GMFFExportTheorem,syl,1\n"
            + "         GMFFExportTheorem,*,100\n"
            + "         GMFFExportTheorem,syl,100,Test20110915a\n" + " </pre>\n"
            + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.MacroBoss
    // ----------------------------------------------------------

    /** Default macro enabled state = true */
    public static final boolean RUNPARM_MACRO_ENABLED_DEFAULT = true;

    public static final BatchCommand RUNPARM_MACRO_ENABLED = new BatchCommand(
        "MacrosEnabled",
        "*\n" + " MacrosEnabled\n" + " <p>\n" + " <code>\n"
            + " \"MacrosEnabled\": yes or no.\n" + " <p>\n"
            + " Set macros on or off, default = yes.\n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_MACRO_FOLDER = new BatchCommand(
        "MacroFolder",
        "*\n" + " MacroFolder\n" + " <p>\n" + " <code>\n"
            + " \"MacroFolder\": directory name, no \"\\\" at end\n"
            + "                  of name. Must exist.\n" + " <p>\n"
            + " Optional. Search location for macros. If this\n"
            + " RunParm is not provided, the current directory is used"
            + " instead.\n" + " </code>\n" + "\n");

    public static final BatchCommand RUNPARM_MACRO_LANGUAGE = new BatchCommand(
        "MacroLanguage",
        " MacroLanguage command.\n" + " \n" + " <pre>\n"
            + " Set the language for macro scripts. Valid values depend on\n"
            + " your installation; input an invalid language to see the list\n"
            + " of possibilities.\n" + "\n"
            + "  - Value1 = Language name, default 'js', which corresponds to\n"
            + "             'rhino' on JRE7- and 'nashorn' on JDK8+.\n" + " \n"
            + "  - Value2 = Default file extension, attached to macros with\n"
            + "             no specified extensions, default 'js'.\n" + " \n"
            + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         MacroLanguage,js,js\n"
            + "         MacroLanguage,BeanShell,bsh\n"
            + "         MacroLanguage,jruby,rb\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_RUN_MACRO_INIT = new BatchCommand(
        "RunMacroInitialization",
        " RunMacroInitialization command.\n" + " \n" + " <pre>\n"
            + " Run the given macro for initialization. Will replace the\n"
            + " existing script context if a macro has already been run.\n"
            + " Optional, default is 'init.js', which is run immediately\n"
            + " before a call to RunMacro if this command is not run first.\n"
            + "\n"
            + "  - Value1 = File name.  Path name is relative to the macro/\n"
            + "             directory.\n" + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         RunMacroInitialization,init\n" + " </pre>\n" + "\n");

    public static final BatchCommand RUNPARM_RUN_MACRO = new BatchCommand(
        "RunMacro",
        " RunMacro command.\n" + " \n" + " <pre>\n"
            + " : - Optional. Run a BeanShell macro in the macro/ directory.\n"
            + "               May appear anywhere in the RunParms list.\n"
            + " \n"
            + "  - Value1 = File name.  Path name is relative to the macro/"
            + "             directory.\n" + " \n"
            + "  - ...    = An arbitrary number of additional parameters are\n"
            + "             permitted; they are passed unchanged to the macro.\n"
            + " \n" + "      - Examples:\n"
            + "         *       1         2         3         4\n"
            + "         *234567890123456789012345678901234567890\n"
            + "         RunMacro,echo,Hello World\n" + " </pre>\n" + "\n");

    // ----------------------------------------------------------
    // Commands for mmj.util.StoreBoss
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_SET_SETTINGS_FILE = new BatchCommand(
        "SettingsFile",
        "*\n" + " SettingsFile\n" + " <p>\n"
            + " \"SettingsFile\": file name relative to mmj2 path.\n" + " <p>\n"
            + " Set the file for use by 'LoadSettings' and 'SaveSettings'"
            + " RunParms. Default value is 'store.json'.");

    public static final BatchCommand RUNPARM_DISABLE_SETTINGS = new BatchCommand(
        "DisableSettings",
        "*\n" + " DisableSettings\n" + " <p>\n"
            + " \"DisableSettings\": no options\n" + " <p>\n"
            + " Turn off the saving and loading of settings to the SettingsFile\n"
            + " on startup and shutdown. Use SettingsFile RunParm to re-enable.");

    public static final BatchCommand RUNPARM_LOAD_SETTINGS = new BatchCommand(
        "LoadSettings",
        "*\n" + " LoadSettings\n" + " <p>\n"
            + " \"LoadSettings\": file name relative to mmj2 path.\n" + " <p>\n"
            + " Load settings from the given file if it exists. If no file given,\n"
            + " use the file set by 'SettingsFile,xxx' RunParm.");

    public static final BatchCommand RUNPARM_SAVE_SETTINGS = new BatchCommand(
        "SaveSettings",
        "*\n" + " SaveSettings\n" + " <p>\n"
            + " \"SaveSettings\": file name relative to mmj2 path.\n" + " <p>\n"
            + " Save settings to the given file. If no file given,\n"
            + " use the file set by 'SettingsFile,xxx' RunParm.");

    // ----------------------------------------------------------
    // Constants mmj.util.TheoremLoaderBoss
    // ----------------------------------------------------------

    public static final BatchCommand RUNPARM_THEOREM_LOADER_DJ_VARS_OPTION = new BatchCommand(
        "TheoremLoaderDjVarsOption");
    public static final BatchCommand RUNPARM_THEOREM_LOADER_MMT_FOLDER = new BatchCommand(
        "TheoremLoaderMMTFolder");
    public static final BatchCommand RUNPARM_THEOREM_LOADER_AUDIT_MESSAGES = new BatchCommand(
        "TheoremLoaderAuditMessages");
    public static final BatchCommand RUNPARM_LOAD_THEOREMS_FROM_MMT_FOLDER = new BatchCommand(
        "LoadTheoremsFromMMTFolder");
    public static final BatchCommand RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER = new BatchCommand(
        "ExtractTheoremToMMTFolder");
    public static final BatchCommand RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER = new BatchCommand(
        "UnifyPlusStoreInLogSysAndMMTFolder");
    public static final BatchCommand RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER = new BatchCommand(
        "UnifyPlusStoreInMMTFolder");

    public static final BatchCommand RUNPARM_THEOREM_LOADER_STORE_FORMULAS_ASIS = new BatchCommand(
        "TheoremLoaderStoreFormulasAsIs");
    public static final BatchCommand RUNPARM_THEOREM_LOADER_STORE_MM_INDENT_AMT = new BatchCommand(
        "TheoremLoaderStoreMMIndentAmt");
    public static final BatchCommand RUNPARM_THEOREM_LOADER_STORE_MM_RIGHT_COL = new BatchCommand(
        "TheoremLoaderStoreMMRightCol");

    public static final int THEOREM_LOADER_BOSS_FILE_BUFFER_SIZE = 32768;

    // =========================================================
    // ====> Option Value Literals for previous RunParm Names
    // =========================================================

    /**
     * Option "*".
     */
    public static final String RUNPARM_OPTION_VALUE_ALL = "*";

    /**
     * Option "basic" (ambiguity editing level).
     */
    public static final String RUNPARM_OPTION_VALUE_BASIC = "basic"; // ambiguity
                                                                     // editing
                                                                     // level:
                                                                     // basic or
                                                                     // paranoid

    /**
     * Option "complete" (ambiguity editing level).
     */
    public static final String RUNPARM_OPTION_VALUE_COMPLETE = "complete"; // ambiguity
                                                                           // editing
                                                                           // level:
                                                                           // basic
                                                                           // or
                                                                           // paranoid

    /**
     * Option "new" (output file usage).
     */
    public static final String RUNPARM_OPTION_FILE_OUT_NEW = "new";

    /**
     * Option "update" (output file usage).
     */
    public static final String RUNPARM_OPTION_FILE_OUT_UPDATE = "update";

    /**
     * FILE_OUT_USAGE_DEFAULT = "new".
     */
    public static final String OPTION_FILE_OUT_USAGE_DEFAULT = "new";

    /**
     * MAX_STATEMENT_PRINT_COUNT_DEFAULT = 9999.
     */
    public static final int MAX_STATEMENT_PRINT_COUNT_DEFAULT = 9999;

    /**
     * Option "yes"
     */
    public static final String RUNPARM_OPTION_YES = "yes";

    /**
     * Option "yes" abbreviated to "y"
     */
    public static final String RUNPARM_OPTION_YES_ABBREVIATED = "y";

    /**
     * Option "no"
     */
    public static final String RUNPARM_OPTION_NO = "no";

    /**
     * Option "no" abbreviated to "n"
     */
    public static final String RUNPARM_OPTION_NO_ABBREVIATED = "n";

    /**
     * Option "on"
     */
    public static final String RUNPARM_OPTION_ON = "on";

    /**
     * Option "off"
     */
    public static final String RUNPARM_OPTION_OFF = "off";

    /**
     * Option Value 4 "unified" for ProofAsstExportToFile RunParm and Option
     * Value 3 for ProofAsstBatchTest.
     * <p>
     * Means that Ref (statement labels) should be included on exported
     * derivation proof steps.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_EXPORT_UNIFIED = "unified";

    /**
     * Option Value 4 "un-unified" for ProofAsstExportToFile RunParm and Option
     * Value 3 for ProofAsstBatchTest.
     * <p>
     * Means that Ref (statement labels) should NOT be included on exported
     * derivation proof steps. This is the default.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_EXPORT_UN_UNIFIED = "un-unified";

    /**
     * Option Value 5 "NotRandomized" for ProofAsstExportToFile RunParm and
     * Option Value 4 for ProofAsstBatchTest.
     * <p>
     * This option is deprecated and means the same as {@link HypsOrder#Correct}
     * .
     */
    @Deprecated
    public static final String RUNPARM_OPTION_PROOF_ASST_NOT_RANDOMIZED = "NotRandomized";

    /**
     * Option Value 5 "Correct" for ProofAsstExportToFile RunParm and Option
     * Value 4 for ProofAsstBatchTest.
     * <p>
     * This option has the same meaning as notrandomized above
     * <p>
     * Means that logical hypotheses should be left in the original order.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_CORRECT = "correct";

    /**
     * Option Value 5 "Reverse" for ProofAsstExportToFile RunParm and Option
     * Value 4 for ProofAsstBatchTest.
     * <p>
     * Means that logical hypotheses should be emitted in reverse order.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_REVERSE = "reverse";

    /**
     * Option Value 5 "HalfReverse" for ProofAsstExportToFile RunParm and Option
     * Value 4 for ProofAsstBatchTest.
     * <p>
     * Means that the first half of logical hypotheses should be emitted in
     * canonical order, but the second part should be emitted in reverse order.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_HALF_REVERSE = "halfreverse";

    /**
     * Option Value 5 "Autocomplete" for ProofAsstExportToFile RunParm and
     * Option Value 4 for ProofAsstBatchTest.
     * <p>
     * Means that logical hypotheses list should be empty and autocompleted by
     * autocomplete feature.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_AUTOCOMPLETE = "autocomplete";

    /**
     * Option Value 5 "SomeOrder" for ProofAsstExportToFile RunParm and Option
     * Value 4 for ProofAsstBatchTest.
     * <p>
     * Means that the logical hypotheses should be emitted in some order,
     * depending on current debug goals. The order is not fixed and this option
     * should be used for preformance experements.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_SOME_ORDER = "someorder";

    /**
     * Option Value 6 "Print" for ProofAsstExportToFile RunParm and Option Value
     * 5 for ProofAsstBatchTest.
     * <p>
     * Means that an extra copy of the Proof Worksheet should be sent to the
     * SystemOutputFile (or System.out). The print copy sent by
     * ProofAsstBatchTest is the after-unification version, with the generated
     * RPN proof, if available. exported proof steps.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_PRINT = "Print";

    /**
     * Option Value 7 "DeriveFormulas for ProofAsstExportToFile RunParm and
     * Option Value 6 for ProofAsstBatchTest.
     * <p>
     * Exported non-Qed derivation steps are output without formulas, leaving it
     * up to the Proof Unifier to "Derive" the formulas.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_DERIVE_FORMULAS = "DeriveFormulas";

    /**
     * Option Value 7 for ProofAsstBatchTest.
     * <p>
     * Compares generated DjVars pairs after unification with the input .mm
     * file's for the theorem.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_COMPARE_DJS = "CompareDJs";

    /**
     * Option Value 8 for ProofAsstBatchTest.
     * <p>
     * Updates generated DjVars pairs after unification.
     */
    public static final String RUNPARM_OPTION_PROOF_ASST_UPDATE_DJS = "UpdateDJs";

    /**
     * Option Value 9 for ProofAsstBatchTest.
     * <p>
     * Re-unifies the output Proof Worksheet after unification.
     */
    public static final String RUNPARM_OPTION_ASCII_RETEST = "AsciiRetest";

    /**
     * Option "inherit"
     */
    public static final String RUNPARM_OPTION_INHERIT = "inherit";

    /**
     * Default value for OutputVerbosity
     */
    public static final int OUTPUT_VERBOSITY_DEFAULT = 9999;

    /**
     * Preprocess Request Option for Erase And Rederive Formulas.
     */
    public static final String RUNPARM_OPTION_ERASE_AND_REDERIVE_FORMULAS = "EraseAndRederiveFormulas";

    /**
     * Default macro language 'js' corresponds to 'nashorn' on JDK8 and 'rhino'
     * on older versions.
     */
    public static final String RUNPARM_OPTION_MACRO_LANGUAGE = "js";

    /**
     * Default macro extension.
     */
    public static final String RUNPARM_OPTION_MACRO_EXTENSION = "js";

    /**
     * Default initialization macro.
     */
    public static final String RUNPARM_OPTION_INIT_MACRO = "init";

    /**
     * Default preparation macro.
     */
    public static final String RUNPARM_OPTION_PREP_MACRO = "prep";

    // ----------------------------------------------------------
    // ----------------------------------------------------------

    // ----------------------------------------------------------
    // Messages from RunParmFile.java
    // ----------------------------------------------------------

//  OBSOLETE AS OF MMJ2 PATH ENHANCEMENT
//  public static final String ERRMSG_RUNPARM_ARG1_ERROR =
//      "A-UT-0001 First argument must be RunParmFile"
//      + " filename string";
//
//  public static final String ERRMSG_RUNPARM_ARG2_ERROR =
//      "A-UT-0002 Second argument must be an empty string"
//      + " or a codeSet specification string for the "
//      + " RunParmFile";
//
//  public static final String ERRMSG_RUNPARM_ARG3_ERROR =
//      "A-UT-0003 Third argument must be an empty string"
//      + " or a 1 character string containing the field"
//      + " delimiter character for parsing lines in the"
//      + " RunParmFile file. The default value is ','.";
//
//  public static final String ERRMSG_RUNPARM_ARG4_ERROR =
//      "A-UT-0004 Fourth argument must be an empty string"
//      + " or a 1 character string containing the field"
//      + " quoting character for parsing lines in the"
//      + " RunParmFile file. The default value is '\"'"
//      + " Note: the delimiter and quoting characters"
//      + " must not be equal!";

    public static final ErrorCode ERRMSG_RUNPARM_FILE_EMPTY = of(
        "A-UT-0005 RunParmFile is empty!" + " Input file name = %s");

    public static final ErrorCode ERRMSG_RUNPARM_NEXT_AFTER_EOF = of(
        "A-UT-0006 RunParmFile.next() method invoked after" + " end of file!"
            + " Input file name = ");

    public static final ErrorCode ERRMSG_RUNPARM_FILE_NOT_FOUND = of(
        "A-UT-0007 RunParmFile not found or SecurityException."
            + " Input file name = %s System message follows: %s");

    // ----------------------------------------------------------
    // Messages from RunParmArrayEntry.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_PARSER_LINE_EMPTY = of(
        "A-UT-0008 No RunParm fields returned by"
            + " DelimitedTextParser. Input line is empty?!");

    // ----------------------------------------------------------
    // Messages from DelimitedTextParser.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_PARSER_INPUT_STRING_NULL = of(
        "A-UT-0009 DelimitedTextParser input text line" + " string is null.");

    public static final ErrorCode ERRMSG_UNMATCHED_QUOTE_CHAR = of(
        "A-UT-0010 DelimitedTextParser found unmatched quote"
            + " character in input text line");

    public static final ErrorCode ERRMSG_MISSING_DELIM = of(
        "A-UT-0011 DelimitedTextParser input text line field"
            + " has missing delimiter");

    public static final ErrorCode ERRMSG_PARSER_LINE_ALREADY_REACHED = of(
        "A-UT-0012 DelimitedTextParser.nextField() method"
            + " called after end of line already reached!");

    // ----------------------------------------------------------
    // Messages from BatchFramework.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_RUNPARM_FILE_BOGUS_1 = of(
        "A-UT-0013 Command Line Arguments invalid --"
            + " or RunParmFile not found, or otherwise invalid."
            + " Message returned by parameter handlers follows:\n");

    public static final ErrorCode ERRMSG_RUNPARM_COMMENT_CAPTION = of(
        "I-UT-0014 **** RunParmFile line comment. Line #%d = %s");

    public static final ErrorCode ERRMSG_RUNPARM_EXECUTABLE_CAPTION = of(
        "I-UT-0015 **** Processing RunParmFile Command #%d = %s");

    public static final ErrorCode ERRMSG_RUNPARM_NAME_INVALID = of(
        "A-UT-0016 RunParm name %s not recognized! Whassup, cowboy?");

    // ----------------------------------------------------------
    // Messages from Boss.java
    // ----------------------------------------------------------

    public static class RunParmContext implements ErrorContext {
        public BatchCommand parm;

        public RunParmContext(final BatchCommand parm) {
            this.parm = parm;
        }

        @Override
        public String append(final String msg) {
            return "Step " + parm + ": " + msg;
        }

        public static <T extends MMJException> T addRunParmContext(
            final BatchCommand parm, final T e)
        {
            return parm == null ? e
                : MMJException.addContext(new RunParmContext(parm), e);
        }
    }

    public static final ErrorCode ERRMSG_FILE_USAGE_ERR_EXISTS = of("A-UT-0017 "
        + "File name parm %s already exists but %s was specified.");

    public static final ErrorCode ERRMSG_FILE_UPDATE_NOT_ALLOWED = of(
        "A-UT-0018 " + "File name parm %s already exists and %s was specified,"
            + " but the existing file is a directory, or update is not allowed.");

    public static final ErrorCode ERRMSG_FILE_MISC_ERROR = of("A-UT-0019 "
        + "File name parm %s is supposed to be a file name. However, a file error was"
        + " encountered with the specified name. Message = %s");

    public static final ErrorCode ERRMSG_FILE_NAME_BLANK = of(
        "A-UT-0020 Value field "
            + "number %d is blank or an empty string. Expecting a file name.");

    public static final ErrorCode ERRMSG_FILE_USAGE_PARM_UNRECOG = of(
        "A-UT-0021 "
            + "Value field number %d must equal '%s' or '%s'. Value input was '%s'");

    public static final ErrorCode ERRMSG_FILE_CHARSET_INVALID = of("A-UT-0022 "
        + "Value field number %d = %s is not a valid Charset name. Message "
        + "returned by system follows: %s");

    public static final ErrorCode ERRMSG_FILE_CHARSET_UNSUPPORTED = of(
        "A-UT-0023 "
            + "Value field number %d = %s is a valid Charset name but is not"
            + " supported by your Java system environment.");

    public static final ErrorCode ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS = of(
        "A-UT-0024 " + "RunParm %s must have at least %d value fields");

    public static final ErrorCode ERRMSG_RUNPARM_NBR_FORMAT_ERROR = of(
        "A-UT-0025 " + "Value is formatted incorrectly. Should be"
            + " a simple integer number. Parse message follows: %s");

    public static final ErrorCode ERRMSG_RUNPARM_NBR_LE_ZERO = of(
        "A-UT-0026 Value "
            + "must be a simple integer number greater than 0. Found input value = %d");

    public static final ErrorCode ERRMSG_RUNPARM_STMT_NOT_THEOREM = of(
        "A-UT-0027 "
            + "Value = %s is the label of a Stmt, but is not the label of a theorem."
            + " Therefore, VerifyProof cannot be performed.");

    public static final ErrorCode ERRMSG_RUNPARM_STMT_LABEL_BLANK = of(
        "A-UT-0028 "
            + "Value field number %d is blank. A valid Stmt label is required.");

    public static final ErrorCode ERRMSG_RUNPARM_STMT_LABEL_NOTFND = of(
        "A-UT-0029 " + "Value = %s is not a valid Stmt label in the"
            + " LogicalSystem that is presently loaded.");

    public static final ErrorCode ERRMSG_RUNPARM_FLOAT_FORMAT_ERROR = of(
        "A-UT-0030 " + "Value is formatted incorrectly. Should be"
            + " a floating point number. Parse message follows: %s");

    public static final ErrorCode ERRMSG_FOLDER_NAME_BLANK = of("A-UT-0101 "
        + "Value field number %d is blank or an empty string. Expecting a FOLDER name.");

    public static final ErrorCode ERRMSG_NOT_A_FOLDER = of(
        "A-UT-0102 Folder name "
            + "parm %s exists, but is not a folder/directory (is a file?).");

    public static final ErrorCode ERRMSG_FOLDER_NOTFND = of(
        "A-UT-0103 Folder name "
            + "parm %s invalid. No such folder (or file) found!");

    public static final ErrorCode ERRMSG_FOLDER_MISC_ERROR = of("A-UT-0104 "
        + "Folder name parm %s is supposed to be a folder. However, an error was"
        + " encountered while using the specified name. Message = %s");

    public static final ErrorCode ERRMSG_BAD_YES_NO_PARM = of(
        "A-UT-0105 Value %s not equal to 'yes' or 'no'.");

    public static final ErrorCode ERRMSG_BAD_ON_OFF_PARM = of(
        "A-UT-0106 Value %s not equal to 'on' or 'off'.");

    public static final ErrorCode ERRMSG_FILE_NOTFND = of(
        "A-UT-0107 File name parm %s not found.");

    public static final ErrorCode ERRMSG_FILE_READ_NOT_ALLOWED = of("A-UT-0108 "
        + "File name parm %s exists, but the existing file is a directory or"
        + " read access is not allowed.");

    public static final ErrorCode ERRMSG_RUNPARM_NONBLANK_PRINT_STR_BAD = of(
        "A-UT-0109 "
            + "Value invalid. Must be a printable string of length 1 or"
            + " more consisting only of non-blank printable characters"
            + " (7-bit ASCII only, just like Metamath.)");

    public static final ErrorCode ERRMSG_NOT_A_FILE = of(
        "A-UT-0111 File name parm %s "
            + "exists, but is a folder/directory, not a file!");

    public static final ErrorCode ERRMSG_BAD_FILE_NAME_SUFFIX = of("A-UT-0112 "
        + "File name parm %s must equal '.txt', '.TXT', '.mmp' or '.MMP'.");

    public static final ErrorCode ERRMSG_RUNPARM_NBR_LT_ZERO = of(
        "A-UT-0113 " + "Value must be a simple integer number"
            + " greater than or equal to 0. Found input value = %d");

    public static final ErrorCode ERRMSG_RUNPARM_RGB_FORMAT = of(
        "A-UT-0114 " + "Must have 6 hexadecimal digits format. Input = %s");

    public static final ErrorCode ERRMSG_BOOLEAN_UNRECOG = of("A-UT-0115 "
        + "Value field number %d must equal '%s' or '%s'. Value input was '%s'");

    // ----------------------------------------------------------
    // Messages from GrammarBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_PARSE_RPN = of(
        "I-UT-0031 Parse RPN for Statement %s = %s");

    public static final ErrorCode ERRMSG_RUNPARM_PARSER_BAD_CLASS = of(
        "I-UT-0032 "
            + "Class %s does not exist or is not an implementation of mmj.verify.GrammaticalParser");

    // ----------------------------------------------------------
    // Messages from LogicalSystemBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_MM_FILE_NOT_LOADED = of(
        "A-UT-0200 Cannot "
            + "complete current RunParmFile request because either, a) the previous "
            + "%1$s RunParm processing detected errors in the input Metamath file; or b) "
            + "a %1$s RunParm must be input before the current RunParmFile line."
            + "\nReview previous error messages to find the error.");

    public static final ErrorCode ERRMSG_LOAD_ENDPOINT_LABEL_BLANK = of(
        "A-UT-0201 RunParm has blank"
            + " label. Delete/Comment out the RunParm or specify a"
            + " bogus Statement Label, such as Z999ZZZZZ if you do not"
            + " wish to limit the load of Metamath statements.");

    public static final ErrorCode ERRMSG_PROVABLE_TYP_CD_BOGUS = of("A-UT-0202 "
        + "ProvableLogicStmtType invalid. Is blank or is zero-length string");

    public static final ErrorCode ERRMSG_LOGIC_TYP_CD_BOGUS = of("A-UT-0203 "
        + "LogicStmtType invalid. Is blank or is zero-length string");

    public static final ErrorCode ERRMSG_BOOK_MANAGER_ALREADY_EXISTS = of(
        "A-UT-0204 " + "BookManager already constructed."
            + " The '%1$s' RunParm must be located before"
            + " the '%2$s' RunParm command and the enabled/disabled"
            + " status cannot be changed after %2$s is executed!");

    // ----------------------------------------------------------
    // Messages from Dump.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_DUMP_STMT_UNRECOG_1 = of(
        "A-UT-0033 Uh oh! dumpStmtTbl() does not recognize"
            + " this Stmt type! Stmt label = ");

    // ----------------------------------------------------------
    // Messages from OutputBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_SYSOUT_PRINT_WRITER_IO_ERROR = of(
        "A-UT-0034 OutputBoss found IO error on sysOut PrintWriter.");

    public static final ErrorCode ERRMSG_SYSERR_PRINT_WRITER_IO_ERROR = of(
        "A-UT-0035 OutputBoss found IO error on sysErr PrintWriter.");

    public static final ErrorCode ERRMSG_BOOK_MANAGER_NOT_ENABLED = of(
        "A-UT-0036 "
            + "OutputBoss found BookManager not enabled. Use RunParm command '%s,yes' prior"
            + " to the %s RunParm to enable the BookManager.");

    public static final ErrorCode ERRMSG_BOOK_MANAGER_SECTION_NBR_NOT_FOUND = of(
        "A-UT-0037 BookManager Section Number %d not found");

    // ----------------------------------------------------------
    // Messages from ProofAsstBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_PA_REQUIRES_GRAMMAR_INIT = of(
        "E-UT-0041 ProofAsstBoss could not load ProofAsst as"
            + " requested. RunParms requiring the ProofAsst should follow"
            + " successful 'Load' and 'Parse,*' commands. It is required"
            + " that a .mm file be loaded, that the Grammar be"
            + " successfully initialized (no errors), and that"
            + " all Metamath statements be grammatically parsed"
            + " prior to running the ProofAsst (for use in" + " unification)."
            + "\nFor more information, see:"
            + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt."
            + "\nReview previous error messages to find the error.");

    public static final ErrorCode ERRMSG_SELECTOR_MISSING = of("A-UT-0042 "
        + "Value field number %d, the 'Selector' option is blank, null or empty");

    public static final ErrorCode ERRMSG_SELECTOR_NOT_A_STMT = of("A-UT-0043 "
        + "Value field number %d with value (statement label) = %s not found "
        + "in Logical System Statement Table.");

    public static final ErrorCode ERRMSG_SELECTOR_NOT_A_THEOREM = of(
        "A-UT-0044 "
            + "Value field number %d with value (statement label) = %s found "
            + "in Logical System Statement Table but the statement found is not a theorem.");

    public static final ErrorCode ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG = of(
        "A-UT-0046 "
            + "Value field number %d must equal '%s' (same as deprecated '%s'), "
            + "'%s', '%s', '%s', '%s' or '%s'. Value input was '%s'.");

    public static final String PROOF_ASST_FONT_FAMILY_LIST_CAPTION = " List of Font Families defined in the system: \n";

    public static final ErrorCode ERRMSG_PREPROCESS_OPTION_UNRECOG = of(
        "A-UT-0056",
        "Option must be 'EraseAndRederiveFormulas' at this time (there is only one"
            + " type of PreprocessRequest now.) Input was '%s'.");

    public static final ErrorCode ERRMSG_MISC_IO_ERROR = of("A-UT-0057",
        "Unknown I/O Exception encountered: %s");

    public static final ErrorCode ERRMSG_RUNPARM_PA_STYLE_UNKNOWN = of(
        "A-UT-0060 ProofAsstErrorMessageColumns RunParm must be one of %s");

    // ----------------------------------------------------------
    // Messages from TMFFBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_TMFF_REQUIRES_GRAMMAR_INIT = of(
        "A-UT-0601 TMFFBoss did not create TMFFPreferences, which"
            + " will prevent ProofAsstGUI from running, among other"
            + " things. TMFF requires that all input .mm statements"
            + " be successfully parsed prior to invoking TMFF."
            + " Therefore, TMFF RunParms should follow"
            + " successful 'Load' and 'Parse,*' commands. It is required"
            + " that a .mm file be loaded, that the Grammar be"
            + " successfully initialized (no errors), and that"
            + " all Metamath statements be grammatically parsed" + " first!"
            + "\nFor more information, see:"
            + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt."
            + "\nReview previous error messages to find the error.");

    // ----------------------------------------------------------
    // Messages from VerifyProofBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_IGNORING_VERIFY_PROOF_RUNPARM = of(
        "I-UT-0701 VerifyProof RunParm request ignored because"
            + " 'LoadProofs' RunParm 'no' input.");

    // ----------------------------------------------------------
    // Messages from WorkVarBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_WV_MGR_REQUIRES_GRAMMAR_INIT = of(
        "A-UT-0801 WorkVarBoss did not initialize the WorkVarManager"
            + " as requested -- which is necessary for definition and"
            + " declaration of Work Variables, not to mention the"
            + " Proof Assistant itself. It is required"
            + " that a .mm file be loaded, that the Grammar be"
            + " successfully initialized (no errors)"
            + " prior to defining or declaring Work Variables, or"
            + " running the ProofAsstGUI.\nFor more information, see:"
            + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt."
            + "\nReview previous error messages to find the error.");

    // ----------------------------------------------------------
    // Messages from SvcBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_SVC_CALLBACK_CLASS_INIT_ERROR = of(
        "A-UT-0901 " + "SvcBoss encountered a problem during the load"
            + " and instantiation of the input name = %. The specific error message "
            + "returned by the Java Runtime Environment follows: ");

    public static final ErrorCode ERRMSG_SVC_CALLBACK_CLASS_CAST_ERROR = of(
        "A-UT-0902 " + "SvcBoss encountered a problem during the 'cast'"
            + " of the input class object to the SvcCallback interface."
            + " The input name = %s. The specific error message returned by the Java Runtime"
            + " Environment follows: %s");

    public static final ErrorCode ERRMSG_SVC_ARG_ERROR = of(
        "A-UT-0903 Input SrvArg "
            + "invalid. Key value must be unique non-blank character string with length > 1."
            + " Input Key parameter = %s. Input Value parameter = %s.");

    public static final ErrorCode ERRMSG_SVC_CALL_PROOF_ASST_MISSING = of(
        "A-UT-0904" + " Unable to initialize ProofAsst object (probably"
            + " because a load, verify or parse RunParm command"
            + " encountered an error). Check previous error messages.");

    public static final ErrorCode ERRMSG_SVC_CALL_THEOREM_LOADER_MISSING = of(
        "A-UT-0905" + " Unable to initialize TheoremLoader object."
            + " Check previous error messages (for clues :-)");

    // ----------------------------------------------------------
    // Messages from MergeSortedArrayLists.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_MERGE_SORTED_LISTS_DUP_ERROR = of(
        "A-UT-1001 An element of the source list = %s was found in the destination list, and"
            + " a program-abort was requested if this occurred.");

    // ----------------------------------------------------------
    // Messages from TheoremLoaderBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_THEOREM_LOADER_READER_ERROR = of(
        "A-UT-1102 "
            + "IO error encountered reading Proof Worksheet file %s. Detailed IOException Message follows: %s");

    // ----------------------------------------------------------
    // Messages from GMFFBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_GMFF_INITIALIZATION_ERROR = of(
        "A-UT-1201 " + " GMFF not successfully initialized.");

    public static final ErrorCode ERRMSG_GMFF_PROOF_ASST_MISSING = of(
        "A-UT-1202" + " GMFF command not completed:"
            + " Unable to initialize ProofAsst object (probably"
            + " because a load, verify or parse RunParm command"
            + " encountered an error. Check previous error" + " messages.");

    public static final ErrorCode ERRMSG_GMFF_RUNPARM_ERROR = of("E-UT-1203"
        + " GMFF command encountered a problem. Detailed message: %s");

    // ----------------------------------------------------------
    // Messages from CommandLineArguments.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_FAIL_POPUP_WINDOW_ARGUMENT = of(
        "A-UT-1301 Command Line %s invalid. Must equal 'y' or 'n'. Found = %s%s");

    public static final ErrorCode ERRMSG_TEST_OPTION_ARGUMENT = of(
        "A-UT-1302 Command Line %s invalid. Must equal 'y' or 'n'. Found = %s");

    // ----------------------------------------------------------
    // Messages from Paths.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_PATH_INVALID = of(
        "A-UT-1401 Command Line %s invalid. Path does not exist or is a file: %s");

    public static final ErrorCode ERRMSG_PATH_SECURITY_ERROR = of(
        "A-UT-1402 Command Line %s invalid. SecurityException on path access: %s");

    // ----------------------------------------------------------
    // Messages from MacroBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_MACRO_LANGUAGE_MISSING = of(
        "E-PA-1501" + " MacroLanguage '%s' does not exist.\n%s");
    public static final String ERRMSG_MACRO_LANGUAGE_MISSING_2 = ""
        + "To use %s, set language to one of %s.\n";

    public static final ErrorCode ERRMSG_MACRO_LANGUAGE_DEFAULT_MISSING = of(
        "E-UT-1502"
            + " You attempted to use a macro, but the default Macro language '%s'"
            + " does not exist. Use 'MacroLanguage,xxx' with one of the following"
            + " installed languages:\n");

    public static final ErrorCode ERRMSG_PREP_MACRO_DOES_NOT_EXIST = of(
        "E-UT-1503" + " The given PrepMacro file %s does not exist.");

    public static final ErrorCode ERRMSG_CALLBACK_ERROR = of("E-UT-1504",
        "Error in callback %s:\n%s");

    // ----------------------------------------------------------
    // Messages from StoreBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_LOAD_FAILED = of("E-UT-1601",
        "Loading settings failed. Detailed error message: %s");

    public static final ErrorCode ERRMSG_SAVE_FAILED = of("E-UT-1602",
        "Saving settings failed. Detailed error message: %s");

    // ----------------------------------------------------------
    // Dump.java "report" literals.
    // ----------------------------------------------------------

    public static final String DUMP_LOGSYS_COUNTS = "LogSysCounts: ";

    public static final String DUMP_PROVABLE_TYP_SET = "Grammar.provableLogicStmtTypSet: ";

    public static final String DUMP_LOGIC_TYP_SET = "Grammar.logicStmtTypSet: ";

    public static final String DUMP_VARHYP_TYP_SET = "Grammar.varHypTypSet: ";

    public static final String DUMP_SYNTAX_AXIOM_TYP_SET = "Grammar.syntaxAxiomTypSet: ";

    public static final String DUMP_NULLS_PERMITTED_TYP_SET = "Grammar.nullsPermittedTypSet: ";

    public static final String DUMP_LOGSYS_SYM_TBL = "LogicalSystem.symTbl: ";

    public static final int DUMP_GRAMMAR_RULE_MAX_PRINT = 9999;

    public static final String DUMP_NULLS_PERMITTED_LIST = "NullsPermitted List: ";

    public static final String DUMP_TYPE_CONVERSION_LIST = "TypeConversion List: ";

    public static final String DUMP_NOTATION_LIST = "Notation List: ";

    public static final String DUMP_LOGSYS_STMT_TBL = "LogicalSystem.stmtTbl: ";

    public static final String DUMP_LOGICAL_SYSTEM = "LogicalSystem: ";

    public static final String DUMP_START = " Start";

    public static final String DUMP_END = " End";

    public static final String DUMP_SYM_TBL_SIZE = " symTbl.size()= ";

    public static final String DUMP_STMT_TBL_SIZE = " stmtTbl.size()= ";

    public static final String DUMP_SYM_TBL = "symTbl:";
    public static final String DUMP_SYM_TBL_UNDERSCORE = "=======";
    public static final String DUMP_SYM_TBL_IS_EMPTY = "symTbl IS EMPTY!";

    public static final String DUMP_STMT_TBL = "stmtTbl:";
    public static final String DUMP_STMT_TBL_UNDERSCORE = "========";
    public static final String DUMP_STMT_TBL_IS_EMPTY = "stmtTbl IS EMPTY!";

    public static final String DUMP_OF_FIRST = " dump of first ";

    public static final String DUMP_THEOREM = "Theorem: ";

    public static final String DUMP_OPT_FRAME_HYP_ARRAY = "Optframe, optHypArray: [";

    public static final String DUMP_OPT_FRAME_DJ_VARS = "           DjVars  : [";

    public static final String DUMP_START_BRACKET = "[";
    public static final String DUMP_END_BRACKET = "] ";
    public static final char DUMP_COMMA = ',';

    public static final String DUMP_PROOF = "Proof: ";
    public static final String DUMP_PROOF_MISSING_STEP = "? ";

    public static final String DUMP_AXIOM = "Axiom: ";

    public static final String DUMP_VARHYP_RESEQ = "syntaxAxiomVarHypReseq: [";

    public static final String DUMP_AXIOM_UNIQUE_CNST = " syntaxAxiomHasUniqueCnst ";

    public static final String DUMP_LOGHYP = "LogHyp: ";

    public static final String DUMP_VARHYP = "VarHyp: ";

    public static final String DUMP_MAND_FRAME_HYP_ARRAY = "Mandframe, hypArray: [";

    public static final String DUMP_MAND_FRAME_DJ_VARS = "           DjVars  : [";

    public static final String DUMP_TYP = " Typ: ";

    public static final String DUMP_IS_ACTIVE = " isActive";

    public static final String DUMP_IS_ASSRT = " isAssrt";

    public static final String DUMP_IS_HYP = " isHyp";

    public static final String DUMP_IS_CNST = " isCnst";

    public static final String DUMP_MAND_VARHYP_ARRAY = " VarHypArray: [";

    public static final String DUMP_FORMULA = "Formula:";

    public static final String DUMP_EXPR_RPN = "ExprRPN: ";

    public static final String DUMP_VAR = "Var: ";

    public static final String DUMP_ACTIVE_VARHYP = " activeVarHyp: ";

    public static final String DUMP_CNST = "Cnst: ";

    public static final String DUMP_IS_VAR_TYP = " isVarTyp";

    public static final String DUMP_IS_GRAMMATICAL_TYP = " isGrammaticalTyp";

    public static final String DUMP_IS_PROVABLE_TYP = " isProvableLogicStmtTyp";

    public static final String DUMP_IS_LOGIC_TYP = " isLogicStmtTyp";

    public static final String DUMP_IS_SYNTAX_AXIOM_TYP = " isSyntaxAxiomTyp";

    public static final String DUMP_LEN1_CNST_RULE_NBR = " len1CnstNotationRule ruleNbr = ";

    public static final String DUMP_LEN1_CNST_AXIOM = " Axiom = ";

    public static final String DUMP_EARLEY_FIRST = " EarleyFIRST= [";

    public static final String DUMP_RULE_COLLECTION = "GrammarRule Collection:";

    public static final String DUMP_RULE_COLLECTION_UNDERSCORE = "=======================";

    public static final String DUMP_RULE_COLLECTION_IS_EMPTY = "GrammarRule Collection IS EMPTY!";

    public static final String DUMP_GRAMMAR_RULE = "GrammarRule: ";

    public static final String DUMP_RULE_NBR = " RuleNbr: ";

    public static final String DUMP_TYPE_CODE = " Type Code: ";

    public static final String DUMP_MAX_SEQ_NBR = " MaxSeqNbr: ";

    public static final String DUMP_NBR_HYP_PARAMS_USED = " NbrHypParamsUsed: ";

    public static final String DUMP_PARAM_TREE_AS_RPN = " ParamTransformationTree as RPN: ";

    public static final String DUMP_PARAM_VARHYP_NODE_ARRAY = " ParamVarHypNode Array: ";

    public static final String DUMP_NOTATION_RULE = "NotationRule: ";

    public static final int DUMP_NOTATION_LABEL_PADIT = 7;

    public static final int DUMP_NOTATION_RULE_NBR_PADIT = 5;

    public static final int DUMP_NOTATION_RULE_TYP_PADIT = 7;

    public static final String DUMP_GRAMMAR_RULE_REPLACEMENT_SYMBOL = " =: ";
    public static final String DUMP_RULE_CONTINUATION_LIT = "  | ";
    public static final String DUMP_RULE_COLON = " : ";

    public static final String DUMP_IS_GIMME_MATCH_NBR = " isGimmeMatchNbr = ";

    public static final String DUMP_TYPE_CONVERSION_RULE = "TypeConversionRule: ";

    public static final String DUMP_RIGHT_ARROW = " -> ";

    public static final String DUMP_NULLS_PERMITTED_RULE = "NullsPermittedRule: ";

    public static final String DUMP_THE_GRAMMAR = "The Grammar";

    public static final String DUMP_THE_GRAMMAR_UNDERSCORE = "===========";

    public static final String DUMP_THE_GRAMMAR_IS_EMPTY = "The Grammar is empty?!?";

    /* stuff for dumping BookManager info */

    public static final String DUMP_BM_CNST = "Cnst ";
    public static final String DUMP_BM_VAR = "Var ";
    public static final String DUMP_BM_VARHYP = "VarHyp ";
    public static final String DUMP_BM_LOGHYP = "LogHyp ";
    public static final String DUMP_BM_AXIOM = "Axiom ";
    public static final String DUMP_BM_THEOREM = "Theorem ";
    public static final String DUMP_BM_UNKNOWN = "?Stmt? ";
    public static final String DUMP_BM_DOT = ".";
    public static final String DUMP_BM_EQ_COL = "=: ";

    /**
     * Array of every documented command, obtained by reflection by searching
     * this class for static BatchCommand fields
     */
    public static final BatchCommand[] RUNPARM_LIST = Arrays
        .stream(UtilConstants.class.getDeclaredFields())
        .filter(f -> Modifier.isStatic(f.getModifiers())
            && BatchCommand.class.isAssignableFrom(f.getType()))
        .map(f -> {
            try {
                return (BatchCommand)f.get(null);
            } catch (final Exception e)

        {
                throw new RuntimeException(e);
            }
        }).sorted().toArray(BatchCommand[]::new);
}
