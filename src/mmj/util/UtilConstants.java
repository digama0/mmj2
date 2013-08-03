//********************************************************************/
//* Copyright (C) 2005-2011                                          */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                         */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  UtilConstants.java  0.11 11/01/2011
 *
 *  Nov-26-2005:
 *      -->fix comment(s).RunParmFile lines at 1. Doh.
 *  Dec-09-2005
 *      -->Add "RunProofAsstGUI" RunParm
 *      -->Add "ProofAsstFontSize" RunParm and related constants
 *      -->Add "ProofAsstProofFolder" RunParm
 *      -->Add "RecheckProofAsstUsingProofVerifier"
 *      -->Add "LoadEndpointStmtNbr" RunParm
 *      -->Add "LoadEndpointStmtLabel" RunParm
 *  Jan-15-2006
 *      -->Add "ProofAsstFormulaLeftCol"     RunParm
 *      -->Add "ProofAsstFormulaRightCol"    RunParm
 *      -->Add "ProofAsstRPNProofLeftCol"    RunParm
 *      -->Add "ProofAsstRPNProofRightCol"   RunParm
 *      -->Add "ProofAsstExportToFile"       RunParm
 *      -->Add "ProofAsstBatchTest"          RunParm
 *      -->Add "ProofAsstUnifySearchExclude" RunParm
 *      --> Doc fix: output System Error and Output file
 *          options do NOT support the "append" option,
 *          just "new" and "update".
 *
 *  Version 0.04:
 *      -->Add "ProofAsstMaxUnifyAlternates" RunParm
 *      -->Add "ProofAsstDummyVarPrefix"     RunParm
 *
 *  Sep-02-2006:
 *      --> Add TMFF stuff.
 *      --> Add Java RunTime version checking stuff.
 *      --> Add "ProofAsstStartupProofWorksheet" RunParm
 *  Oct-21-2006:
 *      --> Add "ProofAsstDefaultFileNameSuffix" RunParm
 *  Oct-27-2006:
 *      --> Add "LoadProofs" RunParm
 *
 *  Version 0.05 06/01/2007
 *      -->Add "OutputVerbosity" RunParm so that the
 *         unpopular printing of input RunParm lines
 *         can be stopped if desired.
 *      -->Add "ProofAsstDjVarsSoftErrors " RunParm.
 *
 *  Version 0.06 08/01/2007
 *      -->Add WorkVarBoss stuff.
 *      -->Removed Dummy Var stuff (deprecated).
 *      -->Add AsciiRetest/NoAsciiRetest option
 *
 *  Version 0.07 11/01/2007
 *      -->Add "TMFFAltFormat"                RunParm
 *         Add "TMFFUseIndent"                RunParm
 *         Add "TMFFAltIndent"                RunParm
 *         Add "ProofAsstTextRows"            RunParm
 *         Add "ProofAsstErrorMessageRows"    RunParm
 *         Add "ProofAsstErrorMessageColumns" RunParm
 *         Add "ProofAsstTextAtTop"           RunParm
 *         and associated messages, constants, etc.
 *
 *  Varsion 0.08 - 02/01/2008
 *      - Add "ProofAsstIncompleteStepCursor"        RunParm
 *      - Add "ProofAsstOutputCursorInstrumentation" RunParm
 *      - Add "ProofAsstAutoReformat"                RunParm
 *
 *  Varsion 0.09 - 03/01/2008
 *      - Add "StepSelectorMaxResults"               RunParm
 *      - Add "StepSelectorShowSubstitutions"        RunParm
 *      - Add "StepSelectorDialogPaneWidth"          RunParm
 *      - Add "StepSelectorDialogPaneHeight"         RunParm
 *      - Remove Unify+Get Hints feature, deprecate
 *        hint-related RunParms
 *      - Remove "ProofAsstMaxUnifyAlternates"       RunParm
 *      - Add "StepSelectorBatchTest"                RunParm
 *      - Add "PreprocessRequestBatchTest"           RunParm
 *
 *  Varsion 0.10 - 08/01/2008
 *      - Add "SvcFolder"                            RunParm
 *      - Add "SvcCallbackClass"                     RunParm
 *      - Add "SvcArg"                               RunParm
 *      - Add "SvcCall"                              RunParm
 *      - Added new error messages for LogicalSystemBoss
 *        when processing ProvableLogicStmtType and
 *        LogicStmtType RunParms
 *      - Add "BookManagerEnabled"                   RunParm
 *      - Add new Commands in OutputBoss:
 *            "PrintBookManagerChapters"             RunParm
 *            "PrintBookManagerSections"             RunParm
 *            "PrintBookManagerSectionDetails"       RunParm
 *      - Add new constants for Dump.java for printing
 *        BookManager data.
 *      - Add "SeqAssignerIntervalSize"              RunParm
 *      - Add "SeqAssignerIntervalTblInitialSize"    RunParm
 *      - Added abort message for new MergeSortedArrayLists.java
 *        utility: "ERRMSG_MERGE_SORTED_LISTS_DUP_ERROR_1"
 *      - Add new "TheoremLoaderBoss" with
 *             "TheoremLoaderMMTFolder"              RunParm
 *             "TheoremLoaderDjVarsOption"           RunParm
 *             "TheoremLoaderAuditMessages"          RunParm
 *             "LoadTheoremsFromMMTFolder"           RunParm
 *             "ExtractTheoremToMMTFolder"           RunParm
 *      - Added new RunParm for ProofAsst
 *             "ProofAsstAssrtListFreespace"         RunParm
 *
 *  Version 0.11 - Nov-01-2011:
 *      _ Added GMFF stuff.
 *      _ Modified for MMJ2 Paths Enhancement
 *      _ Added code for MMJ2FailPopupWindow
 *      _ Set: MAX_STATEMENT_PRINT_COUNT_DEFAULT = 9999 )was 99999)
 *
 */

package mmj.util;

/**
 * (Most) Constants used in mmj.util classes
 *
 *  <p>
 *  There are two primary types of constants: parameters that
 *  are "hardcoded" which affect/control processing, and
 *  error/info messages.
 *  <p>
 *  Each mmj message begins with a code, such as this:<br>
 *  <code>
 *  E-LA-0007<br>
 *  <p>
 *  where the format of the code is "X-YY-9999"<br>
 *
 *  <b>X</b>     : error level
 *  <ul>
 *      <li>E = Error
 *      <li>I = Information
 *      <li>A = Abort (processing terminates, usually a bug).
 *  </ul><br>
 *  <br>
 *
 *  <b>YY</b>    : source code
 *  <ul>
 *      <li>GM = mmj.gmff package (see mmj.gmff.GMFFConstants)
 *      <li>GR = mmj.verify.Grammar and related code
 *               (see mmj.verify.GrammarConstants)
 *      <li>IO = mmj.mmio package (see mmj.mmio.MMIOConstants)
 *      <li>LA = mmj.lang package (see mmj.lang.GMFFConstants)
 *      <li>PA = mmj.pa package (proof assistant)
 *               (see mmj.pa.PaConstants)
 *      <li>PR = mmj.verify.VerifyProof and related code
 *               (see mmj.verify.ProofConstants)
 *      <li>TL = mmj.tl package (Theorem Loader).
 *      <li>TM = mmj.tmff.AlignColumn and related code
 *      <li>UT = mmj.util package.
 *               (see mmj.util.UtilConstants)
 *  </ul><br>
 *  <br>
 *  <b>9999</b>   : sequential number within the source code, 0001
 *       through 9999.
 *
 *  </code>
 *  <p>
 *  <code>
 *  =============================================================
 *  </code>
 *  <p>
 *  <b>RunParmFile parameter Names (1st field on a RunParmFile
 *  line).</b>
 *  <p>
 *  The only mandatory RunParm is RUNPARM_LOAD_FILE, and
 *  even that is not required if nothing much is desired :)
 *  <p>
 *  All other RunParms have defaults or invoke processing
 *  that is optional. For example proof verification is
 *  done only if RUNPARM_VERIFY_PROOF is entered.
 *  <p>
 *  There are "state" variables in BatchMMJ's handling
 *  of RunParms. Some situations are dealt with
 *  automatically while others result in an error.
 *  <p>
 *  A RunParm error, either in the parm values or the
 *  "state" (combination of RunParms) terminates
 *  processing immediately (BatchMMJ will not continue
 *  with subsequent commands after a bogosity,)
 *  <p>
 *  It is simplest to sequence RunParmFile lines with the
 *  non-executable commands first. These are the settings
 *  that modify subsequent processing and stay in effect
 *  until superceded.
 *  <p>
 *  <code>
 *  "Executable" RunParms:<br>
 *
 *      000000000011111111112<br>
 *      012345678901234567890...<br>
 *      ----------------------------<br>
 *      Clear<br>
 *      GarbageCollection<br>
 *      LoadFile<br>
 *      LoadTheoremsFromMMTFolder<br>
 *      VerifyProof<br>
 *      VerifyParse<br>
 *      Parse<br>
 *      InitializeGrammar<br>
 *      PrintSyntaxDetails<br>
 *      PrintStatementDetails<br>
 *      PrintBookManagerChapters<br>
 *      PrintBookManagerSections<br>
 *      PrintBookManagerSectionDetails<br>
 *      ProofAsstExportToFile<br>
 *      ProofAsstBatchTest<br>
 *      StepSelectorBatchTest<br>
 *      PreprocessRequestBatchTest<br>
 *      RunProofAsstGUI<br>
 *      SvcCall<br>
 *      ExtractTheoremToMMTFolder<br>

 *<br>
 *  Example #1 RunParmFile to load 1 file, verify proofs, edit<br>
 *  grammar, parse, print syntax and statement details, and
 *  print BookManager data:<br>
 *<br>
 *      000000000011111111112<br>
 *      012345678901234567890...<br>
 *      MaxStatementPrintCount,9999<br>
 *      Caption,Example #1<br>
 *      MaxErrorMessages,500<br>
 *      MaxInfoMessages,500<br>
 *      LoadFile,c:\metamath\expset.mm<br>
 *      VerifyProof,*<br>
 *      Parse,*<br>
 *      PrintSyntaxDetails<br>
 *      PrintStatementDetails,*<br>
 *      PrintBookManagerChapters<br>
 *      PrintBookManagerSections<br>
 *      PrintBookManagerSectionDetails,*<br>
 *<br>
 *  Example #2 RunParmFile doing the exact same thing except<br>
 *  this time:<br>
 *      - specifying the default values AND<br>
 *      - sending the output to files AND<br>
 *      - specify Load Limits for the input .mm file(s) to
 *        stop loading Metamath statements after a given
 *        number of statements and/or a given statement
 *        label is reached.
 *      - loading an extra .mm file on top of the old one! AND<br>
 *      - specify TheoremLoader stuff!
 *      - doing a belt-and-suspenders double-check of the parse<br>
 *        RPN's!<br>
 *        with<br>
 *      - Set Proof Asst parms, export the input theorems' proofs,<br>
 *        read that file back in as a test, and then
 *      - Trigger the ProofAsstGUI
 *      - generous use of blank comment lines for readability!<br>
 *      - and THEN we clear and load a different file!<br>
 *<br>
 *      000000000011111111112<br>
 *      012345678901234567890...<br>
 *<br>
 *      OutputVerbosity,9999
 *       CommentLine: Example #2 - default charsets="" and<br>
 *                    new/update parameter<br>
 *      SystemErrorFile,c:\my\mmjSyserrTest001.txt,new,""<br>
 *      SystemOutputFile,c:\my\mmjSysoutTest001.txt,new,""<br>
 *<br>
 *      MaxErrorMessages,500<br>
 *      MaxInfoMessages,500<br>
 *<br>
 *      SymbolTableInitialSize,600<br>
 *      StatementTableInitialSize,30000<br>
 *<br>
 *      SeqAssignerIntervalSize,100<br>
 *      SeqAssignerIntervalTblInitialSize,100<br>
 *<br>
 *      LoadEndpointStmtNbr,5000<br>
 *      LoadEndpointStmtLabel,FermatsLastTheorem<br>
 *      LoadComments,yes<br>
 *      LoadProofs,yes<br>
 *<br>
 *      ProvableLogicStmtType,|-<br>
 *      LogicStmtType,wff<br>
 *<br>
 *      BookManagerEnabled,yes<b>
 *<br>
 *      GrammarAmbiguityEdits,basic<br>
 *      StatementAmbiguityEdits,basic<br>
 *<br>
 *      MaxStatementPrintCount,9999<br>
 *      Caption,Example #2<br>
 *<br>
 *      LoadFile,c:\metamath\expset.mm<br>
 *      LoadFile,c:\metamath\expset2.mm<br>
 *<br>
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
 *<br>
 *      VerifyProof,*<br>
 *      Parse,*<br>
 *<br>
 *      VerifyParse,*<br>
 *<br>
 * ===TMFF stuff follows===
 *<br>
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
 *<br>
 * Note: "Unformatted" and Format 0 are hardcoded --
 *       they cannot be redefined via RunParms.
 *<br>
 *TMFFDefineScheme,Unformatted,Unformatted
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
 *<br>
 *      TMFFUseFormat,3
 *      TMFFAltFormat,7
 *      TMFFUseIndent,0
 *      TMFFAltIndent,1
 *<br>
 *      PrintSyntaxDetails<br>
 *      PrintStatementDetails,*<br>
 *      PrintBookManagerChapters<br>
 *      PrintBookManagerSections<br>
 *      PrintBookManagerSectionDetails,*<br>
 *<br>
 *      ProofAsstFontSize,14
 *      ProofAsstFontBold,yes
 *      ProofAsstFontFamily,Monospaced
 *      ProofAsstForegroundColorRGB,0,0,0
 *      ProofAsstBackgroundColorRGB,255,255,255
 *<br>
 *      ProofAsstFormulaLeftCol,20
 *      ProofAsstFormulaRightCol,79
 *      ProofAsstTextColumns,80
 *      ProofAsstTextRows,21
 *      ProofAsstErrorMessageRows,4
 *      ProofAsstErrorMessageColumns,80
 *      ProofAsstTextAtTop,yes
 *      ProofAsstIncompleteStepCursor,Last

 *      ProofAsstRPNProofLeftCol,6
 *      ProofAsstRPNProofRightCol,79
 *<br>
 *      ProofAsstOutputCursorInstrumentation,no
 *      ProofAsstAutoReformat,yes
 *      ProofAsstProofFolder,c:\my\proofs
 *      RecheckProofAsstUsingProofVerifier,yes
 *      ProofAsstUndoRedoEnabled,yes
 *      ProofAsstUnifySearchExclude,biigb,xxxid
 *      ProofAsstExportToFile,*,c:\my\export.mmp,new,un-unified,Randomized,Print
 *      ProofAsstBatchTest,*,c:\my\export.mmp,un-unified,NotRandomized,NoPrint<br>
 *      StepSelectorBatchTest,c:\my\export.mmp,50,0<br>
 *      PreprocessRequestBatchTest,c:\my\export.mmp,EraseAndRederiveFormulas
 *<br>
 *      ProofAsstStartupProofWorksheet,c:\mmj2\data\mmp\PATutorial\Page101.mmp
 *<br>
 *      StepSelectorMaxResults,50
 *      StepSelectorShowSubstitutions,yes
 *      StepSelectorDialogPaneWidth,720
 *      StepSelectorDialogPaneHeight,440
 *<br
 *      ProofAsstAssrtListFreespace,5
 *<br>
 *      RunProofAsstGUI<br>
 *<br>
 *       Comment: now load & process another .mm file!!!!<br>
 *      clear
 *      GarbageCollection
 *      LoadFile,c:\metamath\exppeano.mm
 *      VerifyProof,*
 *      PrintSyntaxDetails
 *      PrintStatementDetails,*
 *<br>
 *       Comment: now load another .mm and make SvcCallback!!!!<br>
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
 *  <br>
 *  </code>
 *  <p>
 *  <code>
 *  =============================================================<br>
 *  ----> RunParm Default Values. Some options have defaults<br>
 *        and allowable values defined elsewhere. See:<br>
 *               mmj.mmio.MMIOConstants.java<br>
 *               mmj.lang.LangConstants.java<br>
 *               mmj.verify.GrammarConstants.java<br>
 *               mmj.verify.ProofConstants.java<br>
 *               mmj.pa.PaConstants.java<br>
 *               mmj.tl.TlConstants.java<br>
 *<br>
 *  =============================================================<br>
 *  </code>
 */
public class UtilConstants {

    public static final String JAVA_VERSION_PROPERTY_NAME
                                  = "java.version";

    public static final int    JAVA_VERSION_MMJ2_MAJ
                                  = 1;

    public static final int    JAVA_VERSION_MMJ2_MIN
                                  = 5;

    public static final String JAVA_VERSION_MMJ2_RUNTIME_ERROR_MSG =
        "BatchMMJ2 requires Java RunTime Environment Version "
        + "1.5 or higher. Version running now = ";

    // ----------------------------------------------------------
    // Constants mmj.util.RunParmFile
    // ----------------------------------------------------------
	public static final String RUN_PARM_FILE_REPORT_LINE_1
								= "  [1] runParmFile  = ";
//  public static final String RUN_PARM_FILE_REPORT_LINE_2
//								= "\n";

    // ----------------------------------------------------------
    // Constants mmj.util.Paths
    // ----------------------------------------------------------
	public static final String MMJ2_PATH_REPORT_LINE_1
								= "\n  [3] mmj2Path     = ";
	public static final String METAMATH_PATH_REPORT_LINE_2
								= "  [4] metamathPath = ";
	public static final String SVC_PATH_REPORT_LINE_3
								= "  [5] svcPath      = ";
	public static final String PATH_REPORT_E_G_CAPTION_1
			                    = " (e.g. ";
	public static final String PATH_REPORT_E_G_CAPTION_2
			                    = ")";
	public static final String PATH_REPORT_EXAMPLE_FILE_NAME
								= "YourFile.xyz";

    // ----------------------------------------------------------
    // Constants mmj.util.CommandLineArguments
    // ----------------------------------------------------------
	public static final int    RUNPARM_FILE_NAME_ARGUMENT_INDEX
	                            = 0;

	public static final int    DISPLAY_MMJ2_FAIL_POPUP_WINDOW_ARGUMENT_INDEX
	                            = 1;

	public static final int    MMJ2_PATH_ARGUMENT_INDEX
	                            = 2;

	public static final int    METAMATH_PATH_ARGUMENT_INDEX
	                            = 3;

	public static final int    SVC_PATH_ARGUMENT_INDEX
	                            = 4;

	public static final int    TEST_OPTION_ARGUMENT_INDEX
	                            = 5;

	public static final String RUNPARM_FILE_NAME_ARGUMENT_LITERAL
								= "runParmFileName argument";

	public static final String DISPLAY_MMJ2_FAIL_POPUP_WINDOW_ARGUMENT_LITERAL
								= "displayMMJ2FailPopupWindow argument";

	public static final String MMJ2_PATH_ARGUMENT_LITERAL
								= "mmj2Path argument";

	public static final String METAMATH_PATH_ARGUMENT_LITERAL
								= "metamathPath argument";

	public static final String SVC_PATH_ARGUMENT_LITERAL
								= "svcPath argument";

    public static final boolean DISPLAY_MMJ2_FAIL_POPUP_WINDOW_DEFAULT
    	                        = true;

	public static final String YES_ARGUMENT
	                            = "Y";
	public static final String NO_ARGUMENT
	                            = "N";

	public static final String ERRMSG_COMMAND_LINE_ARGUMENTS_FORMAT
	                            =
		"\nmmj2 Command Line format follows:\n\n"
		+ "java JAVAPARMS -jar mmj2.jar ARG1 ARG2 ARG3 ARG4 ARG5\n\n"
		+ "    where JAVAPARMS = -Xincgc -Xms128M -Xmx256M (you may customize)\n"
		+ "          ARG1      = RunParms File Name (e.g. RunParms.txt)\n"
		+ "          ARG2      = y or n (displayMMJ2FailPopupWindow)\n"
		+ "          ARG3      = mmj2Path (e.g. c:\\mmj2jar)\n"
		+ "          ARG4      = metamathPath (e.g. c:\\metamath)\n"
		+ "          ARG5      = svcPath (e.g. c:\\your\\svc)\n";

	public static final String ARGUMENTS_OPTION_REPORT_LINE_1
								=
		"\nCommandLineArguments.displayArgumentOptionReport():\n";

	public static final String ARGUMENTS_OPTION_REPORT_LINE_2
								=
		"  Command Line Arguments:";

	public static final String ARGUMENTS_OPTION_REPORT_LINE_3A
								=
		"    Arg #";

	public static final String ARGUMENTS_OPTION_REPORT_LINE_3B
								=
		" = ";

	public static final String ARGUMENTS_OPTION_REPORT_LINE_4
								=
		"  [2] displayMMJ2FailPopupWindow \n"
	  + "                   = ";

	public static final String ARGUMENTS_OPTION_REPORT_LINE_5
								=
		"\n***END CommandLineArguments.displayArgumentOptionReport()***\n";


    // ----------------------------------------------------------
    // Constants mmj.util.MMJ2FailPopupWindow
    // ----------------------------------------------------------
	public static final String MMJ2_FAIL_DIALOG_TITLE
	                            = "MMJ2 Fail";

	public static final String MMJ2_FAIL_STARTUP_DIALOG_TITLE
	                            = "MMJ2 Start-up Error";

	public static final String MMJ2_STARTUP_MSG_LIT_1
	                            = "RunParm #";
	public static final String MMJ2_STARTUP_MSG_LIT_2
	                            = " Command = ";
	public static final String MMJ2_STARTUP_MSG_LIT_3
	                            = " Error(s)\n";

	public static final int    LINE_BREAK_MAX_LENGTH
	                            = 75;
	public static final char   NEW_LINE_CHAR
	                            = '\n';

	public static final int    MAX_STARTUP_ERROR_MESSAGES
	                            = 4;

    // ----------------------------------------------------------
    // other stuff :-)
    // ----------------------------------------------------------

    /**
     *  RunParm Line Dump Verbosity
     *
     *  Note: The default OutputVerbosity RunParm value is
     *        9999. Set it to 0 to turn off log-type
     *        output (error messages will still print.)
     *
     *        The default "verbosity" of the printing
     *        of RunParm lines themselves in BatchFramework.java
     *        is 9, which means that they print (<= verbosity).
     */
    public static final int RUNPARM_LINE_DUMP_VERBOSITY
                                  = 9;

    /**
     *  RunParmFile Default field delimiter.
     *
     *  Note: at present there is no defined escape character.
     *        However, the DelimitedTextParser allows
     *        "quoter" characters inside non-quoted fields,
     *        and delimiter characters inside quoted fields.
     *        Surprisingly, this arrangement suffices for
     *        a great many purposes. If need be, a RunParm
     *        can be added to dynamically *change* the
     *        "quoter" and delimiter characters (that is
     *        simpler than modifying DelimitedText Parser...)<
     */
    public static final char RUNPARM_FIELD_DELIMITER_DEFAULT
                                  = ',';

    /**
     *  RunParmFile Default quote character.
     */
    public static final char RUNPARM_FIELD_QUOTER_DEFAULT
                                  = '"';


    /**
     *  RunParmFile Comment Character: space.
     *
     *  A RunParmFile line with space, '*' or '/' in position 1 is
     *  a comment (not executed...also, a line with no characters
     *  is also treated as a comment).
     */
    public static final char RUNPARM_COMMENT_CHAR_SPACE
                                  = ' ';

    /**
     *  RunParmFile Comment Character: '/' (SLASH).
     *
     *  A RunParmFile line with space, '*' or '/' in position 1 is
     *  a comment (not executed...also, a line with no characters
     *  is also treated as a comment).
     */
    public static final char RUNPARM_COMMENT_CHAR_SLASH
                                  = '/';

    /**
     *  RunParmFile Comment Character: ASTERISK.
     *
     *  A RunParmFile line with space, '*' or '/' in position 1 is
     *  a comment (not executed...also, a line with no characters
     *  is also treated as a comment).
     */
    public static final char RUNPARM_COMMENT_CHAR_ASTERISK
                                  = '*';


    // ----------------------------------------------------------
    // ----------------------------------------------------------


    // =========================================================
    // ====> RunParmFile Name Literals
    // =========================================================

    // ----------------------------------------------------------
    // Commands for mmj.util.LogicalSystemBoss.java
    // ----------------------------------------------------------

    /**
     *  ProvableLogicStmtType.
     *  <p><code>
     *  "ProvableLogicStmtType": default is "|-"
     *  </code>
     */
    public static final String RUNPARM_PROVABLE_LOGIC_STMT_TYPE =
        "ProvableLogicStmtType";   //default is "|-"

    /**
     *  LogicStmtType.
     *  <p><code>
     *  "LogicStmtType": default is "wff"
     *  </code>
     */
    public static final String RUNPARM_LOGIC_STMT_TYPE =
        "LogicStmtType";           //default is "wff"


    /**
     *  BookManagerEnabled.
     *  <p><code>
     *  "BookManagerEnabled": default is "yes"
     *  </code>
     */
    public static final String RUNPARM_BOOK_MANAGER_ENABLED =
        "BookManagerEnabled";       //default is "yes"



    // ----------------------------------------------------------
    // Commands for mmj.util.BatchMMJ2.java
    // ----------------------------------------------------------

    /**
     *  Clear.
     *  <p><code>
     * "Clear":  clear loaded/derived mm data (files/grammar,etc)<br>
     *           as well as all RunParm values except for<br>
     *           SystemErrorFile and SystemOutputFile.<br>
     *  </code>
     */
    public static final String RUNPARM_CLEAR =
        "Clear";

    /**
     *  GarbageCollection.
     *  <p><code>
     *  "GarbageCollection": frees up unused memory items.
     *  </code>
     */
    public static final String RUNPARM_JAVA_GARBAGE_COLLECTION =
        "GarbageCollection";


    // ----------------------------------------------------------
    // Commands for mmj.util.OutputBoss.java
    // ----------------------------------------------------------

    /**
     *  SystemErrorFile.
     *  <p><code>
     *  "SystemErrorFile": value1 = filename,<br>
     *<br>
     *                     value2 = new (default) or update.<br>
     *                       The system will NOT touch an existing<br>
     *                       file unless given "update",<br>
     *                       AND if "new" is specified an error is<br>
     *                       reported, halting processing ASAP if<br>
     *                       the file already exists. If the file<br>
     *                       does exist and Update is specified,<br>
     *                       then it is overwritten (not appended),<br>
     *                       but no error is reported for Update<br>
     *                       if the file does not exist.<br>
     *<br>
     *                     value3 = charset. Note: the program<br>
     *                       will not stop you from appending<br>
     *                       a different charset to an existing<br>
     *                       file, thus hopelessly mixing up your<br>
     *                       data, so have fun but be careful!<br>
     *
     * info on charsets<br>
     * file:///C:/Program%20Files/Java/jdk1.5.0_02/docs/api/java/nio/charset/Charset.html<br>
     *<br>
     * Valid charset names on all Java Platforms:<br>
     *      US-ASCII<br>
     *      ISO-8859-1<br>
     *      UTF-8<br>
     *      UTF-16BE<br>
     *      UTF-16LE<br>
     *      UTF-16<br>
     *  </code>
     */
    public static final String RUNPARM_SYSERR_FILE =
        "SystemErrorFile";

    /**
     *  SystemOutputFile.
     *  <p><code>
     *  "SystemOutputFile": value1 = filename,<br>
     *<br>
     *                      value2 = new (default), or<br>
     *                         update<br>
     *<br>
     *                      value3 = charset<br>
     *                         see RUNPARM_SYSERR_FILE comments<br>
     *                         for info on the above value parms!<br>
     *  </code>
     */
    public static final String RUNPARM_SYSOUT_FILE =
        "SystemOutputFile";

    /**
     *  OutputVerbosity
     *  <p><code>
     *  "OutputVerbosity": value1 = integer,<br>
     *<br>
     *   Verbosity = 9999 is the default
     *             =    0 means only print error messages and
     *                  specifically requested output
     *  </code>
     */
    public static final String RUNPARM_OUTPUT_VERBOSITY =
        "OutputVerbosity";

    /**
     *  StartInstrumentationTimer
     *  <p><code>
     *  "StartInstrumentationTimer": value1 = ID String,<br>
     *<br>
     *   ID String = Identifier in output message produced
     *               by StopInstrumentationTimer RunParm --
     *               must match that ID String.
     *  </code>
     */
    public static final String RUNPARM_START_INSTRUMENTATION_TIMER =
        "StartInstrumentationTimer";

    /**
     *  StopInstrumentationTimer
     *  <p><code>
     *  "StopInstrumentationTimer": value1 = ID String,<br>
     *<br>
     *   ID String = Identifier in StartInstrumentationTimer
     *               RunParm -- must match.
     *  </code>
     */
    public static final String RUNPARM_STOP_INSTRUMENTATION_TIMER =
        "StopInstrumentationTimer";


    // ----------------------------------------------------------
    // Commands for mmj.mmio.Systemizer.java
    // ----------------------------------------------------------

    /**
     *  LoadFile.
     *  <p><code>
     *  "LoadFile": value1 = qual/unqual filename (varies by OS!)
     *  </code>
     */
    public static final String RUNPARM_LOAD_FILE =
        "LoadFile";


    /**
     *  LoadEndpointStmtNbr.
     *  <p><code>
     *  "LoadEndpointStmtNbr": value1 = stop after loading given
     *                                  number of statements from
     *                                  input Metamath file(s).
     *                                  Must be greater than zero.
     *  </code>
     */
    public static final String RUNPARM_LOAD_ENDPOINT_STMT_NBR =
        "LoadEndpointStmtNbr";

    /**
     *  LoadEndpointStmtLabel.
     *  <p><code>
     *  "LoadEndpointStmtLabel": value1 = stop after loading given
     *                                  statement label from
     *                                  input Metamath file(s).
     *                                  Must not be blank.
     *  </code>
     */
    public static final String RUNPARM_LOAD_ENDPOINT_STMT_LABEL =
        "LoadEndpointStmtLabel";

    /**
     *  LoadComments
     *  <p><code>
     *  "LoadComments": value1 = yes/no (default = yes)
     *                           load Metamath comments
     *                           into LogicalSystem as Descriptions for
     *                           the MObj's. The comment immediately
     *                           preceding the $p statement is treated
     *                           as the description (must be the statement
     *                           immediately prior to the $p statement.)
     *
     *                           Only Theorem descriptions are loaded
     *                           now -- which is for Proof Assistant --
     *                           but in principle, the rest could be
     *                           loaded, except for $c and $v statements
     *                           which often have the description
     *                           after the declaration.
     *  </code>
     */
    public static final String RUNPARM_LOAD_COMMENTS =
        "LoadComments";

    /**
     *  LoadProofs
     *  <p><code>
     *  "LoadProofs": value1 = yes/no (default = yes)
     *                           load Metamath proofs from input .mm
     *                           file.
     *
     *                           Use "no" to conserve memory and
     *                           shorten start-up time for the Proof
     *                           Assistant.
     *
     *                           If set to "no" then RunParm
     *                           "VerifyProof" will be ignored -- a
     *                           warning message is produced though.
     *  </code>
     */
    public static final String RUNPARM_LOAD_PROOFS =
        "LoadProofs";

    // ----------------------------------------------------------
    // Commands for mmj.lang.Messages.java
    // ----------------------------------------------------------

    /**
     *  MaxErrorMessages.
     *  <p><code>
     *  "MaxErrorMessages": 1 -> 999999999...
     *  </code>
     */
    public static final String RUNPARM_MAX_ERROR_MESSAGES =
        "MaxErrorMessages";

    /**
     *  MaxInfoMessages.
     *  <p><code>
     *  "MaxInfoMessages": 1 -> 999999999...
     *  </code>
     */
    public static final String RUNPARM_MAX_INFO_MESSAGES =
        "MaxInfoMessages";  // 1 -> 999999999...

    // ----------------------------------------------------------
    // Commands for mmj.lang.LogicalSystem.java
    // ----------------------------------------------------------

    /**
     *  SymbolTableInitialSize.
     *  <p><code>
     *  "SymbolTableInitialSize": default = 1500, min = 10
     *  </code>
     */
    public static final String RUNPARM_SYM_TBL_INITIAL_SIZE =
        "SymbolTableInitialSize"; // default = 600, min = 10

    /**
     *  StatementTableInitialSize.
     *  <p><code>
     *  "StatementTableInitialSize": default = 30000, min = 100
     *  </code>
     */
    public static final String RUNPARM_STMT_TBL_INITIAL_SIZE =
        "StatementTableInitialSize"; // default = 45000, min = 100

    // ----------------------------------------------------------
    // Commands for mmj.lang.SeqAssigner.java
    // ----------------------------------------------------------

    /**
     *  SeqAssignerIntervalSize.
     *  <p><code>
     *  "SeqAssignerIntervalSize": default = 100, min = 1,
     *  max = 10000.
     *  </code>
     */
    public static final String RUNPARM_SEQ_ASSIGNER_INTERVAL_SIZE =
        "SeqAssignerIntervalSize"; // default=1000, min=1, max=10000

    /**
     *  SeqAssignerIntervalTblInitialSize.
     *  <p><code>
     *  "SeqAssignerIntervalTblInitialSize": default = 100, min = 10,
     *  max = 10000.
     *  </code>
     */
    public static final String
                RUNPARM_SEQ_ASSIGNER_INTERVAL_TBL_INITIAL_SIZE =
        "SeqAssignerIntervalTblInitialSize"; // default=100,
                                             // min=10, max=10000

    // ----------------------------------------------------------
    // Commands for mmj.verify.Grammar.java
    // ----------------------------------------------------------

    /**
     *  GrammarAmbiguityEdits.
     *  <p><code>
     *  "GrammarAmbiguityEdits": "basic" (default) or "complete"
     *  </code>
     */
    public static final String RUNPARM_GRAMMAR_AMBIGUITY_EDITS =
        "GrammarAmbiguityEdits";   // "basic" (default) or "complete"

    /**
     *  StatementAmbiguityEdits.
     *  <p><code>
     *  "StatementAmbiguityEdits": "basic" (default) or "complete"
     *  </code>
     */
    public static final String RUNPARM_STATEMENT_AMBIGUITY_EDITS =
        "StatementAmbiguityEdits"; // "basic" (default) or "complete"



    // ----------------------------------------------------------
    // Commands for mmj.util.Dump.java
    // ----------------------------------------------------------

    /**
     *  MaxStatementPrintCount.
     *  <p><code>
     *  "MaxStatementPrintCount": 1 -> 9999999999....
     *  </code>
     */
    public static final String RUNPARM_MAX_STATEMENT_PRINT_COUNT =
        "MaxStatementPrintCount";  // 1 -> 9999999999....

    /**
     *  Caption.
     *  <p><code>
     *  "Caption": freeform caption for report output.
     *  </code>
     */
    public static final String RUNPARM_CAPTION =
        "Caption";   // freeform caption for report output.

    /**
     *  PrintSyntaxDetails.
     *  <p><code>
     *  "PrintSyntaxDetails": no options
     *  </code>
     */
    public static final String RUNPARM_PRINT_SYNTAX_DETAILS =
        "PrintSyntaxDetails";      // no options

    /**
     *  PrintStatementDetails.
     *  <p><code>
     *  "PrintStatementDetails": "*" or Stmt.label
     *  </code>
     */
    public static final String RUNPARM_PRINT_STATEMENT_DETAILS =
        "PrintStatementDetails";   // "*" or Stmt.label

    /**
     *  PrintBookManagerChapters
     *  <p><code>
     *  "PrintBookManagerChapters"
     *  </code>
     */
    public static final String
                        RUNPARM_PRINT_BOOK_MANAGER_CHAPTERS
                                  =
        "PrintBookManagerChapters";

    /**
     *  PrintBookManagerSections
     *  <p><code>
     *  "PrintBookManagerSections"
     *  </code>
     */
    public static final String
                        RUNPARM_PRINT_BOOK_MANAGER_SECTIONS
                                  =
        "PrintBookManagerSections";

    /**
     *  PrintBookManagerSectionDetails.
     *  <p><code>
     *  "PrintBookManagerSectionDetails": "*" or Section Number
     *  </code>
     */
    public static final String
                        RUNPARM_PRINT_BOOK_MANAGER_SECTION_DETAILS
                                  =
        "PrintBookManagerSectionDetails";   // "*" or Section Number


    // ----------------------------------------------------------
    // Commands for mmj.lang.ProofVerifier.java interface
    // ----------------------------------------------------------

    /**
     *  VerifyProof.
     *  <p><code>
     *  "VerifyProof": "*" or Stmt.label
     *  </code>
     */
    public static final String RUNPARM_VERIFY_PROOF =
        "VerifyProof";        // "*" or Stmt.label

    /**
     *  VerifyParse.
     *  <p><code>
     *  "VerifyParse": "*" or Stmt.label
     *  </code>
     */
    public static final String RUNPARM_VERIFY_PARSE =
        "VerifyParse";        // "*" or Stmt.label


    // ----------------------------------------------------------
    // Commands for mmj.lang.SyntaxVerifier.java interface
    // ----------------------------------------------------------

    /**
     *  Parse.
     *  <p><code>
     *  "Parse": "*" or Stmt.label
     *  </code>
     */
    public static final String RUNPARM_PARSE =
        "Parse";              // "*" or Stmt.label

    /**
     *  InitializeGrammar.
     *  <p><code>
     *  "InitializeGrammar": no option values
     *  </code>
     */
    public static final String RUNPARM_INITIALIZE_GRAMMAR =
        "InitializeGrammar";  // no option values


    // ----------------------------------------------------------
    // Commands for mmj.pa.ProofAsst.java interface
    // ----------------------------------------------------------

    /**
     *  ProofAsstDjVarsSoftErrors
     *  <p><code>
     *  "ProofAsstDjVarsSoftErrors":
     *
     *      "Ignore" -- Don't check for missing $d statements
     *      "Report" -- Create missing $d statement error messages.
     *      "GenerateReplacements"
     *               -- Generate complete set of $d statements if
     *                  any omissions are detected
     *      "GenerateDifferences"
     *               -- Generate set of $d statements to add to the
     *                  $d's in the Proof Worksheet and .mm database
     *                  for the theorem.
     *
     *  Optional, default is "GenerateReplacements"
     *
     *  NOTE: Superfluous $d statements are not detected!
     *
     *  </code>
     */
    public static final String
                            RUNPARM_PROOF_ASST_DJ_VARS_SOFT_ERRORS =
        "ProofAsstDjVarsSoftErrors";

    /**
     *  ProofAsstForegroundColorRGB
     *  <p><code>
     *  "ProofAsstForegroundColorRGB":
     *                         "0,0,0" -- black (default)
     *                         thru
     *                         "255,255,255" -- white
     *
     *  Optional, default is "0.0,0" (black)
     *
     *  </code>
     */
    public static final String
            RUNPARM_PROOF_ASST_FOREGROUND_COLOR_RGB =
        "ProofAsstForegroundColorRGB";

    /**
     *  ProofAsstBackgroundColorRGB
     *  <p><code>
     *  "ProofAsstBackgroundColorRGB":
     *                         "255,255,255" -- white (default)
     *                         thru
     *                         "0,0,0" -- black
     *
     *  Optional, default is "255,255,255" (white)
     *
     *  </code>
     */
    public static final String
            RUNPARM_PROOF_ASST_BACKGROUND_COLOR_RGB =
        "ProofAsstBackgroundColorRGB"; //

    /**
     *  ProofAsstFontFamily
     *  <p><code>
     *  "ProofAsstFontFamily": "Monospaced", (the default),
     *                         "Serif",
     *                         "SansSerif",
     *                         "Monospaced",
     *                         "Dialog",
     *                         "DialogInput"...
     *                         etc.
     *  One way to view the list of Font Family Names defined
     *  on a system is to input an invalid Font Family Name
     *  on the ProofAsstFontFamily command -- a list will be
     *  displayed as part of a punitively long error message :)
     *
     *  NOTE!!! Fixed-width fonts such as Monospaced or Courier
     *          are essential for Proof Assistant if you plan
     *          on using the Text Mode Formula Formatting
     *          (TMFF) alignment Methods such as AlignColumn.
     *          TMFF will not align formula symbols properly
     *          when proportional fonts are used!!!
     *
     *  Optional, default is "Monospaced"
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_FONT_FAMILY =
        "ProofAsstFontFamily";  // "Monospaced", "Courier New", etc.

    /**
     *  ProofAsstFontBold
     *  <p><code>
     *  "ProofAsstFontBold": Yes or No
     *
     *  Optional, default is Yes (bold).
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_FONT_BOLD =
        "ProofAsstFontBold";  // yes, no


    /**
     *  ProofAsstFontSize
     *  <p><code>
     *  "ProofAsstFontSize": 8 or 9, 10, 11, 12, 14, 16, 18
     *                       20, 22, 24, 26, 28, 36, 48, 72
     *
     *  Optional, default is 14 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_FONT_SIZE =
        "ProofAsstFontSize";  // 8, 9, ...

    /**
     *  ProofAsstLineWrap
     *  <p><code>
     *  "ProofAsstLineWrap":
     *      equal to 'on'
     *      or       'off'
     *
     *  Controls whether or not text displayed in the proof
     *  window wraps around when the number of columns of
     *  text exceeds ProofAsstTextColumns.
     *
     *  Optional, default is 'off' (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_LINE_WRAP =
        "ProofAsstLineWrap";



    /**
     *  ProofAsstTextColumns
     *  <p><code>
     *  "ProofAsstTextColumns":
     *      greater than 39 and
     *      less than 1000
     *
     *  Controls program formatting, not user-input formulas.
     *  Defines the column width of the window, which can
     *  be greater than or less than the width of the screen
     *  or the formulas! Primary effect seen with LineWrap ON
     *  because intra-formula line breaks are done with spaces
     *  (and because a double newline is needed at end of
     *  formulas for legibility reasons.)
     *
     *  Optional, default is 80 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_TEXT_COLUMNS =
        "ProofAsstTextColumns";

    /**
     *  ProofAsstTextRows
     *  <p><code>
     *  "ProofAsstTextRows":
     *      greater than 1 and
     *      less than 100
     *
     *  Provides a clue to the system about how big to make
     *  the ProofAsstGUI proof text area window.
     *
     *  Optional, default is 21 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_TEXT_ROWS =
        "ProofAsstTextRows";

    /**
     *  ProofAsstTextAtTop
     *  <p><code>
     *  "ProofAsstTextAtTop":
     *      'yes' or 'no' or 'y' or 'n' or 'Y' or 'N'
     *
     *  If 'yes', positions the ProofAsstGUI proof text area
     *  above the error message text area; otherwise, their
     *  positions are reversed (error messages at top).
     *
     *  Optional, default is 'yes' (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_TEXT_AT_TOP =
        "ProofAsstTextAtTop";

    /**
     *  ProofAsstIncompleteStepCursor
     *  <p><code>
     *  "ProofAsstIncompleteStepCursor":
     *      'First', 'Last', or 'AsIs' (not case sensitive).
     *
     *  Pertains to cursor positioning when no unification
     *  errors found and there is at least one incomplete
     *  proof step; 'First' means position cursor to the
     *  first incomplete proof step, etc.
     *
     *  The cursor is positioned to the Ref sub-field within
     *  a proof step.
     *
     *  Optional, default is 'Last' (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String
                    RUNPARM_PROOF_ASST_INCOMPLETE_STEP_CURSOR =
        "ProofAsstIncompleteStepCursor";


    /**
     *  ProofAsstErrorMessageRows
     *  <p><code>
     *  "ProofAsstErrorMessageRows":
     *      greater than 1 and
     *      less than 100
     *
     *  Provides a clue to the system about how big to make
     *  the ProofAsstGUI error message text area window.
     *
     *  Optional, default is 4 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String
        RUNPARM_PROOF_ASST_ERROR_MESSAGE_ROWS =
        "ProofAsstErrorMessageRows";

    /**
     *  ProofAsstErrorMessageColumns
     *  <p><code>
     *  "ProofAsstErrorMessageColumns":
     *      greater than 39 and
     *      less than 1000
     *
     *  Provides a clue to the system about how wide to make
     *  the ProofAsstGUI error message text area window.
     *
     *  Optional, default is 80 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String
        RUNPARM_PROOF_ASST_ERROR_MESSAGE_COLUMNS =
        "ProofAsstErrorMessageColumns";

    /**
     *  ProofAsstFormulaLeftCol
     *  <p><code>
     *  "ProofAsstFormulaLeftCol":
     *      greater than 1 and
     *      less than ProofAsstFormulaRightCol
     *
     *  Controls program formatting, not user-input formulas.
     *
     *  Optional, default is 20 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_FORMULA_LEFT_COL =
        "ProofAsstFormulaLeftCol";

    /**
     *  ProofAsstFormulaRightCol
     *  <p><code>
     *  "ProofAsstFormulaRightCol":
     *      greater than ProofAsstFormulaLeftCol and
     *      less than 9999
     *
     *  Controls program formatting, not user-input formulas.
     *
     *  Optional, default is 79 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_FORMULA_RIGHT_COL =
        "ProofAsstFormulaRightCol";

    /**
     *  ProofAsstRPNProofLeftCol
     *  <p><code>
     *  "ProofAsstRPNProofLeftCol":
     *      greater than 3 and
     *      less than ProofAsstRPNProofRightCol
     *
     *  Controls program formatting of generated proof statements
     *
     *  Optional, default is 6 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_RPN_PROOF_LEFT_COL
        = "ProofAsstRPNProofLeftCol";

    /**
     *  ProofAsstRPNProofRightCol
     *  <p><code>
     *  "ProofAsstRPNProofRightCol":
     *      greater than ProofAsstRPNProofLeftCol and
     *      less than 9999
     *  <p>
     *  Controls program formatting of generated proof statements
     *  <p>
     *  Optional, default is 79 (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_RPN_PROOF_RIGHT_COL
        = "ProofAsstRPNProofRightCol";

    /**
     *  ProofAsstMaxUnifyAlternates DEPRECATED
     */
    public static final String RUNPARM_PROOF_ASST_MAX_UNIFY_ALTERNATES
        = "ProofAsstMaxUnifyAlternates";

    /**
     *  ProofAsstMaxUnifyHints DEPRECATED
     */
    public static final String RUNPARM_PROOF_ASST_MAX_UNIFY_HINTS
        = "ProofAsstMaxUnifyHints";

    /**
     *  ProofAsstUnifyHintsInBatch DEPRECATED
     */
    public static final String RUNPARM_PROOF_ASST_UNIFY_HINTS_IN_BATCH
        = "ProofAsstUnifyHintsInBatch";

    /**
     *  StepSelectorMaxResults
     *  <p>
     *  Limits the number of unifying assertions returned
     *  by the StepSelectorSearch.
     *  <p>
     *  Optional, default is 50 (see mmj.pa.PaConstants.java)
     */
    public static final String RUNPARM_STEP_SELECTOR_MAX_RESULTS
        = "StepSelectorMaxResults";

    /**
     *  StepSelectorShowSubstitutions
     *  <p>
     *  Determines whether or not unifying assertions are shown
     *  as is or with the substitutions required by unification.
     *  <p>
     *  Default is true (see mmj.pa.PaConstants.java)
     */
    public static final String
                            RUNPARM_STEP_SELECTOR_SHOW_SUBSTITUTIONS
        = "StepSelectorShowSubstitutions";

    /**
     *  StepSelectorDialogPaneWidth
     *  <p>
     *  Sets the pixel width of the StepSelectorDialog.
     *  <p>
     *  Optional, default is 720 (see mmj.pa.PaConstants.java)
     */
    public static final String
            RUNPARM_STEP_SELECTOR_DIALOG_PANE_WIDTH
        = "StepSelectorDialogPaneWidth";

    /**
     *  StepSelectorDialogPaneHeight
     *  <p>
     *  Sets the pixel width of the StepSelectorDialog.
     *  <p>
     *  Optional, default is 440 (see mmj.pa.PaConstants.java)
     */
    public static final String
            RUNPARM_STEP_SELECTOR_DIALOG_PANE_HEIGHT
        = "StepSelectorDialogPaneHeight";

    /**
     *  ProofAsstAssrtListFreespace
     *  <p>
     *  Sets the amount of freespace in the ArrayLists used
     *  in the Proof Assistant.
     *  <p>
     *  Optional, default is 5, minimum 0, maximum 1000.
     */
    public static final String
            RUNPARM_PROOF_ASST_ASSRT_LIST_FREESPACE
        = "ProofAsstAssrtListFreespace";

    /**
     *  ProofAsstOutputCursorInstrumentation
     *  <p><code>
     *  "ProofAsstOutputCursorInstrumentation": yes or no.
     *  <p>
     *  Used to generate "instrumentation" info messages
     *  for use in regression testing. OutputCursor
     *  state information is generated by ProofAsst.java
     *  at the end of main functions, such as "unify".
     *  <p>
     *  <p>
     *  Optional, default is no (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String
        RUNPARM_PROOF_ASST_OUTPUT_CURSOR_INSTRUMENTATION
            = "ProofAsstOutputCursorInstrumentation";


    /**
     *  ProofAsstAutoReformat
     *  <p><code>
     *  "ProofAsstAutoReformat": yes or no.
     *  <p>
     *  Specifies whether or not proof step formulas are
     *  automatically reformatted after work variables
     *  are resolved.
     *  <p>
     *  Optional, default is yes (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_AUTO_REFORMAT
            = "ProofAsstAutoReformat";


    /**
     *  ProofAsstUndoRedoEnabled RunParm.
     *  <p><code>
     *  Controls whether or not the Proof Assistant GUI
     *  provides Undo/Redo support.
     *  <p>
     *  Normally this is turned on, but if desired, say
     *  for performance reasons, the user can disable
     *  Undo/Redo at start-up time via RunParm.
     *  <p>
     *  Optional. Default = yes.
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_UNDO_REDO_ENABLED
        = "ProofAsstUndoRedoEnabled";


    /**
     *  ProofAsstDummyVarPrefix
     *  <p><code>
     *  "ProofAsstDummyVarPrefix": length > 0, no embedded blanks
     *  or unprintable characters.
     *
     *  Dummy variables used to display un-determined variable
     *  substitutions are given a prefix string and a number.
     *  For example: $1, $2, etc.
     *
     *  Optional, default is "$ (see mmj.pa.PaConstants.java)
     *
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_DUMMY_VAR_PREFIX
        = "ProofAsstDummyVarPrefix";

    /**
     *  ProofAsstDefaultFileNameSuffix
     *  <p><code>
     *  "ProofAsstDefaultFileNameSuffix": ".txt", ".TXT",
     *                                    ".mmp" or ".MMP"
     *  <p>
     *  Optional. If this RunParm is not provided, the hardcoded
     *  default ".txt" is used as the default for Proof Worksheet
     *  file names.
     *  </code>
     */
    public static final String
            RUNPARM_PROOF_ASST_DEFAULT_FILE_NAME_SUFFIX =
        "ProofAsstDefaultFileNameSuffix";

    /**
     *  ProofAsstProofFolder
     *  <p><code>
     *  "ProofAsstProofFolder": directory name, no "\" at end
     *                       of name. Must exist.
     *  <p>
     *  Optional. If this RunParm is not provided, the user
     *  of ProofAsstGUI is prompted during Save dialogs, and
     *  the folder is remembered for the duration of the
     *  session.
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_PROOF_FOLDER =
        "ProofAsstProofFolder";


    /**
     *  ProofAsstStartupProofWorksheet
     *  <p><code>
     *  "ProofAsstStartupProofWorksheet": name of Proof Worksheet
     *                       file to be displayed at ProofAsstGUI
     *                       startup. Must exist.
     *  <p>
     *  Optional. If this RunParm is not provided, a hardcoded
     *  Proof Worksheet (String) is displayd.
     *  </code>
     */
    public static final String
            RUNPARM_PROOF_ASST_STARTUP_PROOF_WORKSHEET =
        "ProofAsstStartupProofWorksheet";


    /**
     *  RecheckProofAsstUsingProofVerifier
     *  <p><code>
     *  "RecheckProofAsstUsingProofVerifier,yes"
     *   or
     *  "RecheckProofAsstUsingProofVerifier,no"
     *
     *  Optional, default = "no". If equal to "yes", then each
     *  derivation proof step's generated Metamath RPN proof
     *  is double-checked using the full Metamath Proof Engine,
     *  AKA "Proof Verifier". In theory this should be
     *  unnecessary since the Proof Assistant should provide
     *  valid proofs, but it may be useful if question arise,
     *  or if the user has spare CPU cycles and skepticism.
     *
     *  </code>
     */
    public static final
            String  RUNPARM_RECHECK_PROOF_ASST_USING_PROOF_VERIFIER =
        "RecheckProofAsstUsingProofVerifier";


    /**
     *  RunProofAsstGUI
     *  <p><code>
     *  "RunProofAsstGUI": no option values (for now...)
     *  </code>
     */
    public static final String RUNPARM_RUN_PROOF_ASST_GUI =
        "RunProofAsstGUI";  // no option values...for now...

    /**
     *  ProofAsstExportToFile
     *  <p><code>
     *  "ProofAsstExportToFile":<br>
     *                 value1 = filename; absolute or<br>
     *                          relative (to current<br>
     *                          directory or if provided<br>
     *                          the ProofAsstProofFolder,<br>
     *                          which is input via RunParm<br>
     *                          and also during use of<br>
     *                          ProofAsstGUI<br>
     *<br>
     *                 value2 = "*"    - all theorems<br>
     *                          label  - a single theorem<br>
     *                          99999  - a given number of theorems<br>
     *<br>
     *                 value3 = Optional: new (default),<br>
     *                          or update<br>
     *<br>
     *                 value4 = un-unified (default) or<br>
     *                                    unified.<br>
     *<br>
     *                 value5 = Randomized or NotRandomized
     *                          (default).<br>
     *                          Controls order of exported proof<br>
     *                          step logical hypotheses (a testing<br>
     *                          feature).<br>
     *<br>
     *                 value6 = Print or NoPrint (default)<br>
     *                          Print requests copy of Proof <br>
     *                          Worksheet to be sent to the<br>
     *                          SystemOutputFile (or System.out<br>)
     *                          in addition to the export file.<b>
     *<br>
     *                value7 = "DeriveFormulas" or "NoDeriveFormulas"<br>
     *                          (default) or "". If "DeriveFormulas"<br>
     *                          then the exported Proof Worksheets<br>
     *                          are written with blank formulas to<br>
     *                          trigger the Derive Formula feature<br>
     *                          in the Proof Assistant during later<br>
     *                          import. Note that the theorem's<br>
     *                          logical hypotheses and "qed" step<br>
     *                          cannot be derived -- formula is<br>
     *                          always required for these steps,<br>
     *                          so "DeriveFormulas" applies only to<br>
     *                          non-Qed derivation proof steps.<br>


     *<br>
     *  This RunParm is provided for use in high-volume testing.<br>
     *  It exports proofs to a file in the format required<br>
     *  by the Proof Assistant GUI. To import the proof file and<br>
     *  test the Unification function, use RunParm<br>
     *  'ProofAsstBatchTest', specifying the file name.<br>
     *<br>
     *  Option value3 has two variations: un-unified means the<br>
     *  exported derivation proof steps do not have Ref labels,<br>
     *  whereas unified means Ref labels are present.<br>
     *<br>
     *  Note: this feature is not a full export of a Metamath<br>
     *  file as it does not export $d or anything besides theorems<br>
     *  and their logical hypotheses.<br>
     *<br>
     *  Note: a relative filename such as "export.mmp" can be
     *  input or an "absolute" name such as "c:\my\export.mmp".
     *  The "ProofAsstProofFolder", if present, is used with
     *  relative filename. And take care to note that if export
     *  is performed *after* ProofAsstGUI, the ProofAsstProofFolder
     *  may have been changed.
     *  </code>
     */
    public static final String RUNPARM_PROOF_ASST_EXPORT_TO_FILE =
        "ProofAsstExportToFile"; // options: selector, filename,
                                 // file usage, unified/un-unified

    /**
     *  ProofAsstBatchTest
     *  <p><code>
     *  "ProofAsstBatchTest": value1 = selection, either<br>
     *<br>
     *                       "*"    - all theorems<br>
     *                       label  - a single theorem<br>
     *                       99999  - a given number of theorems<br>
     *<br>
     *                       value2 = Optional: <br>
     *                                a file name, either absolute<br>
     *                                or relative (to the current<br>
     *                                directory, or if provided<br>
     *                                the ProofAsstProofFolder,<br>
     *                                which is input via RunParm<br>
     *                                and also during use of<br>
     *                                ProofAsstGUI<.) If no file<br>
     *                                name input, skeleton proofs<br>
     *                                are generated from memory <br>
     *                                (the .mm file loaded :)<br>
     *<br>
     *                 value3 = un-unified (default) or<br>
     *                          unified proof format.<br>
     *<br>
     *                 value4 = Randomized or NotRandomized
     *                          (default).<br>
     *                          Controls order of exported proof<br>
     *                          step logical hypotheses (a testing<br>
     *                          feature).<br>
     *<br>
     *                 value5 = Print or NoPrint (default)<br>
     *                          Print requests copy of Proof <br>
     *                          Worksheet to be sent to the<br>
     *                          SystemOutputFile (or System.out<br>)
     *                          in addition to the export file.
     *<br>
     *                 value6 = "DeriveFormulas" or "NoDeriveFormulas"<br>
     *                          (default) or "". If "DeriveFormulas"<br>
     *                          then the exported Proof Worksheets<br>
     *                          are written with blank formulas to<br>
     *                          trigger the Derive Formula feature<br>
     *                          in the Proof Assistant during later<br>
     *                          import. Note that the theorem's<br>
     *                          logical hypotheses and "qed" step<br>
     *                          cannot be derived -- formula is<br>
     *                          always required for these steps,<br>
     *                          so "DeriveFormulas" applies only to<br>
     *                          non-Qed derivation proof steps.<br>
     *<br>
     *                 value7 = "CompareDJs" or "NoCompareDJs"<br>
     *                           (default) or "".<br>
     *<br>
     *                           See mmj2\data\runparm\windows<br>
     *                           \AnnotatedRunParms.txt for more<br>
     *                           info.<br>
     *<br>
     *                 value8 = "UpdateDJs" or "NoUpdateDJs"<br>
     *                          (default) or "".<br>
     *<br>
     *                           See mmj2\data\runparm\windows<br>
     *                           \AnnotatedRunParms.txt for more<br>
     *                           info.<br>
     *<br>
     *  </code>
     *  <p>
     *  This RunParm is provided for use in high-volume testing.<br>
     *  <p>
     *  RunParm option value2 is input to specify an input
     *  file containing proofs in the format used on the
     *  Proof Assistant GUI screen. This is optional, and
     *  if not provided, the program simulates an input
     *  file using the currently loaded Metamath data
     *  <p>
     *  In "simulation" mode (no input file), the program
     *  exports a proof "to memory", just as it would have
     *  been created for the ProofAsstExportToFile RunParm
     *  (which is why the unified/un-unified and Randomized/
     *  NotRandomized options are provided here also.) The
     *  The export-simulated proof is run through
     *  the Unification process for testing purposes.
     *
     *  RunParm option value1 provides a selection capability,
     *  and this capability works with or without an input
     *  file. Specify "*" to test unification of all proofs,
     *  either in the input file or those loaded into the
     *  system. Specifying a number, for example 99, runs the
     *  test for the first 99 theorems (database sequence if
     *  input file not provided). Finally, specifying a
     *  theorem label runs the test for just that one theorem.
     *
     *  Note: a relative filename such as "export.mmp" can be
     *  input or an "absolute" name such as "c:\my\export.mmp".
     *  The "ProofAsstProofFolder", if present, is used with
     *  relative filename. And take care to note that if export
     *  is performed *after* ProofAsstGUI, the ProofAsstProofFolder
     *  may have been changed.
     */
    public static final String RUNPARM_PROOF_ASST_BATCH_TEST =
        "ProofAsstBatchTest"; // options selection and optional
                              // file name.
    /**
     *  StepSelectorBatchTest
     *  <p><code>
     *  "StepSelectorBatchTest":
     *<br>
     *                 value1 = Mandatory: <br>
     *                          a file name, either absolute<br>
     *                          or relative (to the current<br>
     *                          directory, or if provided<br>
     *                          the ProofAsstProofFolder,<br>
     *                          which is input via RunParm<br>
     *                          and also during use of<br>
     *                          ProofAsstGUI<.)
     *<br>
     *                 value2 = cursor position:<br>
     *                          char offset position in Proof Worksheet<br>
     *<br>
     *                 value3 = selection number<br>
     *                          zero to 99999999.
     *  </code>
     *  <p>
     *  This RunParm is provided for regression testing.<br>
     *  <p>
     *  Specify the cursor position within the Proof Worksheet
     *  and the number to be selected from the StepSelectorDialog
     *  for the request. The program initiates a StepSelectorSearch
     *  and then if there are no errors, selects the chosen item
     *  from the StepSelectorResults and invokes unify().
     *  <p>
     *  The StepSelectorResults are printed, as well as the
     *  ProofWorksheet after unification -- and any messages.
     */
    public static final String RUNPARM_STEP_SELECTOR_BATCH_TEST =
        "StepSelectorBatchTest"; // all options mandatory: filename,
                                 // cursor pos, and selection number.

    /**
     *  PreprocessRequestBatchTest
     *  <p><code>
     *  "PreprocessRequestBatchTest":
     *<br>
     *                 value1 = Mandatory: <br>
     *                          a file name, either absolute<br>
     *                          or relative (to the current<br>
     *                          directory, or if provided<br>
     *                          the ProofAsstProofFolder,<br>
     *                          which is input via RunParm<br>
     *                          and also during use of<br>
     *                          ProofAsstGUI<.)
     *<br>
     *                 value2 = "EraseAndRederiveFormulas" is the only<br>
     *                          valid option at this time.<br>
     *  </code>
     *  <p>
     *  This RunParm is provided for regression testing.<br>
     *  <p>
     *  The Proof Text is printed before and after preprocessing
     *  and unification.
     *  (and
     */
    public static final String RUNPARM_PREPROCESS_REQUEST_BATCH_TEST =
        "PreprocessRequestBatchTest"; // all options mandatory: filename,
                                      // and request name


    /**
     *  ProofAsstUnifySearchExclude
     *  <p><code>
     *  "ProofAsstUnifySearchExclude": options = Assrt labels, comma
     *                                 separated (ex: biigb,xxxid)
     *  </code>
     *  <p>
     *  NOTE: The RunParm validation for these excluded Assrt
     *  labels will be very lenient and will just ignore
     *  labels that are "invalid" or not in the Statment Table.
     *  The reason is that the exclusion list is expected to
     *  be very stable and the new RunParm "LoadEndpointStmtNbr"
     *  allows loading of just a portion of a Metamath file; if
     *  we required perfection in the exclusion list the usability
     *  of LoadEndPointStmtNbr would drop dramatically (see also
     *  LoadEndpointStmtLabel).
     *  <p>
     *  This RunParm instructs ProofUnifier.java to not attempt
     *  to unify the specified assertion labels with any proof
     *  steps -- unless the user specifically enters them on
     *  a proof step.
     *  <p>
     *  The Unification process scans the loaded Metamath file
     *  assertion (LogicalSystem.stmtTbl) in ascending database
     *  sequence and accepts the first match it finds. Generally
     *  that works fine, but in a few cases, such as duplicate
     *  theorems that are present simply because of an alternate
     *  proof, this feature is helpful (though it would possibly
     *  be easier to put biigb after bii and avoid the situation
     *  in the first place.)
     *  <p>
     *  The *problem* of multiple valid unifications for a proof
     *  step may affect a small number of theorems. The list of
     *  alternatives can be obtained by specifically entering
     *  a valid assertion label that does *not* unify -- the program
     *  then provides a message detailing the possible choices.
     *  (The message with alternatives is also produced if there
     *  is a Distinct Variables error on a proof step and there is
     *  no unifying assertion that doesn't have a Distinct Variables
     *  error.) In set.mm p0ex and snex are appear as alternatives
     *  in a few proofs; mulid1 and mulid2 are another example.
     */
    public static final String
                    RUNPARM_PROOF_ASST_UNIFY_SEARCH_EXCLUDE =
        "ProofAsstUnifySearchExclude";  //options = Assrt labels
                                        //comma separated
                                        // (ex. biigb,xxxid)


    // ----------------------------------------------------------
    // Commands for mmj.tmff.Preferences.java interface
    // ----------------------------------------------------------

    /**
     *  TMFFDefineScheme command.
     *  <p>
     *  Defines TMFF Schemes that may be referenced subsequently
     *  in TMFF Formats (TMFFDefineFormat can only refer to
     *  a TMFF Scheme that is already defined.)
     *  <p>
     *  Note: a Scheme can be re-defined in a subsequent RunParm.
     *        This would normally be of use only in a testing
     *        situation.
     *
     *  <p><code>
     *  Parameters:
     *  <ol>
     *    <li>Scheme Name: must be non-blank, unique, not =
     *        "Unformatted". Not case sensitive.
     *    <li>Method Name: = "AlignColumn" or "Flat". Not case
     *        sensitive.
     *    <li>MaxDepth = subtree depth max before triggering break
     *    <li>ByValue = "Var", "Sym", or "Cnst" (AlignColumn only)
     *    <li>AtNbr = 1, 2, or 3 (AlignColumn only)
     *    <li>AtValue = "Var", "Sym" or "Cnst" (AlignColumn only)
     *  </ol>
     *  </code>
     */
    public static final String RUNPARM_TMFF_DEFINE_SCHEME =
        "TMFFDefineScheme";

    /**
     *  TMFFDefineFormat command.
     *  <p>
     *  Defines TMFF Formats that may be referenced subsequently
     *  in the TMFFUseFormat command (TMFFUseFormat can only refer to
     *  a TMFF Scheme that is already defined, which includes
     *  the pre-defined, built-in Formats.)
     *  <p>
     *  Note: a Format can be re-defined in a subsequent RunParm.
     *        This would normally be of use only in a testing
     *        situation.
     *
     *  <p><code>
     *  Parameters:
     *  <ol>
     *    <li>Format Nbr: 1, 2 or 3.
     *    <li>Scheme Name: must be non-blank, unique, not =
     *        "Unformatted". Not case sensitive.
     *  </ol>
     *  </code>
     */
    public static final String RUNPARM_TMFF_DEFINE_FORMAT =
        "TMFFDefineFormat";

    /**
     *  TMFFUseFormat command.
     *  <p>
     *  Specifies which TMFF Format is in use during subsequent
     *  processing.
     *  <p>
     *  Note: multiple TMFFUseFormat commands can be input, but
     *        only one format can be in effect at a single time.
     *  <p>
     *  Note: Format '0' = Unformatted, turn TMFF off/disabled.
     *
     *  <p><code>
     *  Parameters:
     *  <ol>
     *    <li>Format Nbr: 0, 1, 2, 3, etc.
     *  </ol>
     *  </code>
     */
    public static final String RUNPARM_TMFF_USE_FORMAT =
        "TMFFUseFormat";

    /**
     *  TMFFAltFormat command.
     *  <p>
     *  Specifies the alternate TMFF Format to be used when the
     *  ProofAsstGUI Edit/Reformat Proof - Swap Alt menu item
     *  is selected.
     *  <p>
     *
     *  <p><code>
     *  Parameters:
     *  <ol>
     *    <li>Format Nbr: 0, 1, 2, 3, etc.
     *  </ol>
     *  </code>
     */
    public static final String RUNPARM_TMFF_ALT_FORMAT =
        "TMFFAltFormat";

    /**
     *  TMFFUseIndent command.
     *  <p>
     *  Specifies the number of columns to indent a proof step
     *  formula for each level in the proof tree.
     *  <p>
     *
     *  <p><code>
     *  Parameters:
     *  <ol>
     *    <li>Indent Amount: 0, 1, 2, 3, or 4.
     *  </ol>
     *  </code>
     */
    public static final String RUNPARM_TMFF_USE_INDENT =
        "TMFFUseIndent";


    /**
     *  TMFFAltIndent command.
     *  <p>
     *  Specifies the number of columns to indent a proof step
     *  formula for each level in the proof tree.
     *  Specifies the alternate TMFF Indent Amount to be used when the
     *  ProofAsstGUI Edit/Reformat Proof - Swap Alt menu item
     *  is selected.
     *  <p>
     *
     *  <p><code>
     *  Parameters:
     *  <ol>
     *    <li>Alt Indent Amount: 0, 1, 2, 3, or 4.
     *  </ol>
     *  </code>
     */
    public static final String RUNPARM_TMFF_ALT_INDENT =
        "TMFFAltIndent";

    // ----------------------------------------------------------
    // Commands for mmj.util.WorkVarBoss interface to WorkVarManager
    // ----------------------------------------------------------

    /**
     *  DefineWorkVarType command.
     *  <code>
     * : - Optional. May appear anywhere after the "Parse" RunParm<br>
     *               within an input RunParm file, and takes effect<br>
     *               when the next DeclareWorkVars RunParm command is<br>
     *               processed. If not input prior to first use -- the<br>
     *               Proof Assistant -- the default settings are<br>
     *               automatically used.<br>
     *  <br>
     *   - Default = One default DefineWorkVarType RunParm is<br>
     *               generated for each grammatical Type Code<br>.
     *               specifying a prefix of "&x" where "x" is<br>
     *               the first character of the grammatical<br>
     *               type code, converted to lower case if<br>
     *               necessary; 100 work variables are defined<br>
     *               by default for each grammatical type code<br>.
     *  <br>
     *   - Value1 = Grammatical Type Code (e.g. "wff", "class",<br>
     *              "set", etc.) Must be a valid grammatical<br>
     *              Type Code.<br>
     *  <br>
     *   - Value2 = Work Variable Prefix for the grammatical<br>
     *              Type Code. Must generate unique variable and<br>
     *              variable hypothesis names when concatenated<br>
     *              with the Work Variable numerical suffix (1,<br>
     *              2, ..., 11, ..., etc.) Note that Work<br>
     *              Variable Hypothesis labels are generated<br>
     *              automatically and are the same as the Work<br>
     *              Variables. A Work Variable Prefix must<br>
     *              consist solely of valid Metamath math<br>
     *              symbol characters (not "$", for example,
     *              or embedded blanks.)<br>
     *  <br>
     *   - Value3 = Number of Work Variables to be declared for the<br>
     *              grammatical Type Code. Must be greater than 9<br>
     *              and less than 1000 ("stinginess" is recommended to<br>
     *              avoid wasted processing and memory allocations...<br>
     *              but, in the event that the supply of available<br>
     *              Work Variables is exhausted during processing<br>
     *              a pop-up GUI error message will be displayed; the<br>
     *              RunParms will need to be modified and re-input<br>
     *              in a subsequent run...)<br>
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          DefineWorkVarType,wff,&W,100<br>
     *          DefineWorkVarType,set,&S,100<br>
     *          DefineWorkVarType,class,&C,100<br>
     *  </code>
     */
    public static final String RUNPARM_DEFINE_WORK_VAR_TYPE =
        "DefineWorkVarType";


    /**
     *  DeclareWorkVars command.
     *  <code>
     *   - Optional. May appear anywhere after the "Parse" RunParm<br>
     *               within an input, and takes effect immediately<br>
     *               (any existing Work Variables are deleted and<br>
     *               a new set is created.)<br>
     *  <br>
     *   - Default = A default DeclareWorkVars RunParm is executed<br>
     *               automatically when first need arises (e.g. at<br>
     *               Proof Assistant start-up), if none have been<br>
     *               input since the last Clear RunParm or the start<br>
     *               of the RunParm file.<br>
     *  <br>
     *   - Value1 = N/A<br>
     *  <br>
     *   - Examples<br>
     *  <br>
     *      *        1         2         3         4<br>
     *      *234567890123456789012345678901234567890<br>
     *      DeclareWorkVars<br>
     *  </code>
     */
    public static final String RUNPARM_DECLARE_WORK_VARS =
        "DeclareWorkVars";


    // ----------------------------------------------------------
    // Commands for mmj.util.SvcBoss interface to SvcCallback
    // ----------------------------------------------------------

    /**
     *  SvcFolder
     *  <p>
     *  <code>
     *  "SvcFolder": directory name, no "\" at end of name.<br>
     *               Must exist and must be a directory.<br>
     *  <br>
     * : - Optional. Must appear prior to the SvcCall RunParm.<br>
     *  <br>
     *   - Default = If not input, output Svc files are directed<br>
     *               to the current directory.<br>
     *  <br>
     *   - Value1 = Directory Name. No "\" or "/" at the end<br>
     *              of name. Must exist and must be the name of<br>
     *              a directory. The separator symbol is OS <br>
     *              dependent (Windows uses "\", *nix/Max = "/").<br>
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          SvcFolder,c:\MyFolder<br>
     *  </code>
     */
    public static final String RUNPARM_SVC_FOLDER =
        "SvcFolder";

    /**
     *  SvcCallbackClass
     *  <p>
     *  <code>
     *  "SvcCallbackClass": Name of class which implements the<br>
     *               mmj.svc.SvcCallback interface in "callee"<br>
     *               mode. Must have a default constructor.<br>
     *  <br>
     *       NOTE: Do not input this RunParm if you are using<br>
     *             SvcCallback in "caller" mode because it will<br>
     *             override the specific instance of your class<br>
     *             which you pass as an argument to<br>
     *             BatchMMJ2.generateSvcCallback()<br>
     *  <br>
     * : - Optional. SvcCallback can be provided via a call to<br>
     *               BatchMMJ2.setSvcCallback().<br>
     *  <br>
     *   - Default = None.<br>
     *  <br>
     *   - Value1 = SvcCallbackClass class name.<br>
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          SvcCallbackClass,c:\MyClass<br>
     *  </code>
     */
    public static final String RUNPARM_SVC_CALLBACK_CLASS =
        "SvcCallbackClass";


    /**
     *  SvcArg
     *  <p>
     *  <code>
     *  "SvcArg": Key/Value Pair loaded into Map which is passed<br>
     *            to SvcCallback.go(). Key/Value pairs are minimally<br>
     *            validated to ensure that each Key is at least<br>
     *            one character long and unique. The Key/Value parm<br>
     *            contents are parsed using the same separator<br>
     *            and delimter characters used for the rest of<br>
     *            the RunParms. Multiple SvcArgs can be input.<br>
     *  <br>
     * : - Optional.
     *  <br>
     *   - Default = None.<br>
     *  <br>
     *   - Value1 = Key. Non-blank string at least one character
     *              in length. Must not be a duplicate of any
     *              other SvcArg key.
     *  <br>
     *   - Value2 = Value. String zero or more characters in
     *              length.
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          SvcArg,OutFilePrefix,exp<br>
     *          SvcArg,OutFileSuffix,zip<br>
     *          SvcArg,ZipOutput,yes<br>
     *  </code>
     */
    public static final String RUNPARM_SVC_ARG =
        "SvcArg";

    /**
     *  SvcCall
     *  <p>
     *  <code>
     *  "SvcCall": Command to perform call to SvcCallback.go().
     *  <br>
     *       - Examples:<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          SvcCall<br>
     *  </code>
     */
    public static final String RUNPARM_SVC_CALL =
        "SvcCall";

    // ----------------------------------------------------------
    // Commands for mmj.util.GMFFBoss interface
    // ----------------------------------------------------------

    /**
     *  GMFFExportParms command.
     *  <code>
     *
     *  _ Optional. Default values are shown above. Modifications<br>
     *    to the defaults as well as additional settings for new<br>
     *    export types are made with this RunParm. Validation is<br>
     *    deferred until GMFF Initialization except for the number<br>
     *    of RunParm parameters -- i.e. use of this RunParm does not<br>
     *    trigger GMFF Initialization.<br>
     *  <br>
     *  _ May appear anywhere after the "LoadFile" RunParm<br>
     *    but preferably the GMFF RunParms -- if used at<br>
     *    all -- appear just prior to starting the Proof<br>
     *    Assistant. For testing purposes, if input Proof<br>
     *    Worksheet files are used and they contain Work<br>
     *    Variables then the GMFF RunParms should appear<br>
     *    after the WorkVar RunParms.<br>
     *  <br>
	 *  _ Value1     = Export Type (Unicode or .gif)<br>
     *    _ defaults: althtml and html<br>
     *    _ Export Type must be unique. It is the key in the export<br>
     *      parms (and text escapes) lists built using default<br>
     *      settings merged with the input RunParms GMFFExportParms<br>
     *      entries.<br>
     *    _ A second GMFFExportParms RunParm with the same Export<br>
     *      Type updates the first.<br>
     *  <br>
	 *  _ Value2     = on/off<br>
	 *    _ default ON<br>
     *    _ ON or OFF to enable/disable this export type.<br>
     *    - Note that by default, both html and althtml are ON.<br>
     *    _ Setting all export types OFF disables GMFF exports.<br>
     *    _ If OFF the rest of the input parameters are not validated<br>
     *      or stored.<br>
     *  <br>
	 *  _ Value3     = Typesetting Definition Keyword in .mm file<br>
     *                (in the $t typesetting comment) for this export.<br>
     *    _ defaults: althtmldef and htmldef (or latex but latex is<br>
     *      not supported by the GMFF Model files provided and only<br>
     *      Model A is coded into the program.)<br>
     *  <br>
     *  _ Value4     = Export Directory.<br>
     *    _ defaults: gmff\althtml and gmff\html<br>
     *    _ Directory where exports are written. Also, gmff\html<br>
     *      contains .gif files for symbols.<br>
     *  <br>
	 *  _ Value5     = export File Type<br>
     *    _ default: .html (.html or .htm might be good choices :-)<br>
     *  <br>
     *  _ Value6     = GMFF Models Directory -- Directory containing<br>
     *                 html fragment files serving as models for exports.<br>
     *    _ defaults: gmff\althtml\models and GMFF\html\models<br>
     *  <br>
     *  _ Value7     = Model Id. Only "A" is valid now.<br>
     *    _ defaults: Model Id."A"<br>
     *  <br>
     *  _ Value8     = Charset Encoding name.<br>
     *    _ default: ISO-8859-1<br>
     *    _ Must match the html fragment for the specified Model Id<br>
     *      which contains the html <head> keyword...but the program<br>
     *      does not validate this! Model A specifies ISO-8859-1<br>
     *      (same as Metamath Proof Explorer).<br>
     *    _ Valid charset encodings on all Java platforms are:<br>
     *      _ US-ASCII<br>
     *      _ ISO-8859-1<br>
     *      _ UTF-8<br>
     *      _ UTF-16BE<br>
     *      _ UTF-16LE<br>
     *      _ UTF-16<br>
     *  <br>
     *   _ Value9 = OutputFileName <br>
     *
     *               Name of output file minus the file type. <br>
     *               Optional. <br>
     *  <br>
     *               _ If not specified the output file name is <br>
     *                 constructed from the proof theorem's label <br>
     *                 + the Export File Type. <br>
     *  <br>
     *                 _ Note! The OutputFileName applies to all <br>
     *                   exports, including those via the <br>
     *                   GMFFExportTheorem and GMFFExportFromFolder <br>
     *                   RunParms in addition to ProofAsstGUI <br>
     *                   export requests. To export to individual <br>
     *                   theorem-named files you must input a <br>
     *                   new GMFEExportTheorems RunParm!!! <br>
     *  <br>
     *               _ If specified must not contain any <br>
     *                 whitespace characters, or '/' or '\' or ':' <br>
     *                 characters (for safety.) <br>
     *  <br>
     *                 _ All/any exported Proof Worksheets will be <br>
     *                   output to the named file suffixed with the <br>
     *                   GMFFExportParms file type -- except that <br>
     *                   the GMFFExportTheorem and <br>
     *                   GMFFExportFromFolder AppendFileName <br>
     *                   parameter overrides the OutputFileName <br>
     *                   parameter on the GMFFExportParms RunParm! <br>
     *  <br>
     *  <br>
     *  _ NOTE: There is nothing in the GMFF program code specific<br>
     *          to html. All html-specific information is external<br>
     *          to the code, and is specified via the GMFF RunParms,<br>
     *          the GMFF \models directory files, and the Metamath<br>
     *          $t typesetting definitions.<br>
     *  <br>
     *          _ Since mmj2 allows you to input more than one<br>
     *            LoadFile RunParm, you could create an extra $t<br>
     *            comment in a second input .mm file and output<br>
     *            export data in whatever format you desire...<br>
     *            the only proviso being that the GMFF code knows<br>
     *            the names of the \models files for Model A. So<br>
     *            either your extra export type must match the<br>
     *            pattern of \models files (with regards to the<br>
     *            parts which are filled in by the code vs. what<br>
     *            is in the fragments), or another model would<br>
     *            need to be added to the GMFF code.<br>
     *  <br>
     *            _ Model A is a "minimalist" version of a webpage<br>
     *              which typesets only proof step formulas plus the<br>
     *              theorem label, which is output as text but is<br>
     *              treated as a variable in the model.)<br>
     *  <br>
     *            _ The one thing you cannot do with this design is<br>
     *              export to a language which is based on the formula<br>
     *              parse trees, for example MathML. Exporting and<br>
     *              typesetting based on parse trees -- as opposed to<br>
     *              formulas comprised of sequences of symbols -- would<br>
     *              require extra code in GMFF.<br>
     *  <br>
     *       - Examples (these are the defaults):<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFExportParms,althtml,ON,althtmldef,gmff\althtml,.html,gmff\althtml\models,A,ISO-8859-1,general<br>
     *          GMFFExportParms,html,ON,htmldef,gmff\html,.html,gmff\html\models,A,ISO-8859-1,general<br>
     *  </code>
     */
    public static final String RUNPARM_GMFF_EXPORT_PARMS =
        "GMFFExportParms";

    /**
     *  GMFFUserTextEscapes command.
     *  <code>
     *  _ Optional:<br>
     *    _ These "escapes" convert certain output text characters<br>
     *      to an alternative character sequence that represents<br>
     *      the escaped text characters in the output language<br>
     *      (e.g. html).
     * <br>
     *      _ Escapes are necessary because certain text characters<br>
     *        which may be used in a Proof Worksheet have special,<br>
     *        non-text significance in html. Characters such as '&',<br>
     *      '>', '<', etc. are used in the html language.<br>
     * <br>
     *      _ The space character is escaped into "&nbsp;" so that<br>
     *        Proof Worksheet text spacing is maintained (otherwise<br>
     *        browsers would collapse or ignore output spaces in<br>
     *        certain situations.)<br>
     * <br>
     *  _ Value1 - Export Type (Unicode or .gif).<br>
     *    _ Defaults: althtml and html<br>
     *    _ Must match the Export Type on one of the GMFFExportParms<br>
     *      RunParms or the default GMFFExportParms<br>
     * <br>
     *  _ ValueN - Decimal number of Metamath ASCII character<br>
     *             to be "escaped" in the output html file.<br>
     *  _ ValueN+1<br>
     *           - Character string to replace escaped character.<br>
     * <br>
     *  _ Default Escape Pairs (for both html and althtml):<br>
     *    _  32 (' ') -> "&nbsp;"<br>
     *    _  34 ('"') -> "&quot;"<br>
     *    _  38 ('&') -> "&amp;"<br>
     *    _  60 ('<') -> "&lt;"<br>
     *    _  62 ('>') -> "&gt;"<br>
     * <br>
     *  _ NOTE: User Text to be "escaped" is whatever text<br>
     *          in the Proof Worksheet is not "typeset" using<br>
     *          the Metamath $t typesetting definitions --<br>
     *          and any mmj2 Proof Worksheet text stored in<br>
     *          a \models directory (e.g. Proof Worksheet Header<br>
     *          text contains both "<" and ">", which are stored<br>
     *          in the \models directory in escaped format<br>
     *          (so it does not need to be escaped again.)<br>
     *  <br>
     *       - Examples (these are the defaults):<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFUserTextEscapes,html,32,"&nbsp;",34,"&quot;",38,"&amp;",60,"&lt;",62,"&gt;" <br>
     *          GMFFUserTextEscapes,althtml,32,"&nbsp;",34,"&quot;",38,"&amp;",60,"&lt;",62,"&gt;" <br>
     */
    public static final String RUNPARM_GMFF_USER_TEXT_ESCAPES =
        "GMFFUserTextEscapes";

    /**
     *  GMFFUserExportChoice command.
     *  <code>
     *  _ Optional:<br>
     *    _ These "escapes" convert certain output text characters<br>
     *      to an alternative character sequence that represents<br>
     *      the escaped text characters in the output language<br>
     *      (e.g. html).<br>
     * <br>
     *  _ Value1 - Export Type (Unicode or .gif).<br>
     *    _ Defaults: althtml and html<br>
     *    _ Must match the Export Type on one of the GMFFExportParms<br>
     *      RunParms or the default GMFFExportParms<br>
     *  <br>
     *       - Examples ("ALL" is the default):<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFUserExportChoice,ALL <br>
     *          GMFFUserExportChoice,html <br>
     *          GMFFUserExportChoice,althtml <br>
     */
    public static final String RUNPARM_GMFF_USER_EXPORT_CHOICE =
        "GMFFUserExportChoice";

    /**
     *  GMFFInitialize command.
     *  <code>
     *  _ Optional. Forces initialization or re-initialization<br>
     *              using whatever GMFF RunParm options, default<br>
     *              settings and Metamath $t typesetting definitions have<br>
     *              been input.<br>
       _ NOTE: GMFFInitialize prints an audit message showing the final set
     *         of parms in effect: selected Exporter ExportParms,
     *         UserTextEscapes and UserExportChoice ... plus
     *         typeset definition symbol counts by def keyword.
     *  <br>
	 *	 _ The audit report is printed only if GMFF initialization is<br>
	 *     successful.<br>
     * <br>
     *   _ May appear anywhere after the "LoadFile" RunParm<br>
     *     but preferably the GMFF RunParms -- if used at<br>
     *     all -- appear just prior to starting the Proof<br>
     *     Assistant. For testing purposes, if input Proof<br>
     *     Worksheet files are used and they contain Work<br>
     *     Variables then the GMFF RunParms should appear<br>
     *     after the WorkVar RunParms.<br>
     *  <br>
	 *	_ If GMFFInitialize is not used then initialization
	 *	  takes place only if/when the first GMFF export is
	 *	  attempted. Reinitialization can occur if one or
	 *	  more additional LoadFile commands have executed
	 *	  since initialization and new Metamath $t typsetting
     *	  definitions have been input. (And of course, the
	 *	  "Clear" RunParms resets all state variables, which
	 *	  would force reinitialization if additional LoadFile
	 *	  commands and GMFF export processing were to occur.)
     *  <br>
	 *  _ Initialization may result in error messages about
	 *    the contents of the input .mm Metamath file's $t
	 *    typesetting commands, as well as any other start-up
	 *    errors from GMFF.)
     *  <br>
     *   - Default = N/A -- GMFF initialization is automatic.<br>
     *  <br>
     * <br>
     *  _ Value1 -  "PrintTypesettingDefinitions" or spaces.
     *    _ Optional
     *    _ Prints the defined symbols and their definitions (replacement
     *      text.)
     *  <br>
     *
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFInitialize<br>
     *          GMFFInitialize,PrintTypesettingDefinitions<br>
     *  </code>
     */
    public static final String RUNPARM_GMFF_INITIALIZE =
        "GMFFInitialize";

    /**
     *  GMFFParseMetamathTypesetComment command.
     *  <code>
     *  _ Optional. Primarily used for testing. Executes<br>
     *              standalone parse of a single Metamath $t comment<br>
     *              (does not affect the state of GMFF or anything<br>
     *              else -- except Messages.)
     *  <br>
     *              NOTE: the input file should contain only<br>
     *              the $t comment!<br>
     *  <br>
     *              May appear anywhere after the "LoadFile" RunParm.<br>
     *              (Although it is "standalone" and affects only the<br>
     *              Messages and GMFFManager objects, the LoadFile<br>
     *              command creates the LogicalSystem object which holds<br>
     *              the GMFFManager object.)<br>
     *  <br>
     *              A dump of the parse results is generated along with<br>
     *              statistics. The dump is in the form of a very long<br>
     *              "info" message.<br>
     *  <br>
     *   - Default = N/A -- used for batch testing.<br>
     *  <br>
     *   _ Value1 = Typesetting Definition Keyword in .mm file<br>
     *               (in the $t typesetting comment) to be selected<br>
     *              for parsing.<br>
     *  <br>
     *   _ Value2 = directory containing MM file<br>
     *  <br>
     *   _ Value3 = Metamath .mm file containing just a $t comment.<br>
     *  <br>
     * <br>
     *   _ Value4 -  "PRINT" or spaces.<br>
     *    _ Optional<br>
     *    _ Prints the input file as well as the parsed symbols and<br>
     *      their definitions (replacement text.)<br>
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFParseMetamathTypesetComment,htmldef,mydirectory,mytypesetdefs.mm<br>
     *          GMFFParseMetamathTypesetComment,htmldef,mydirectory,mytypesetdefs.mm,PRINT<br>
     *  </code>
     */
    public static final String
    		RUNPARM_GMFF_PARSE_METAMATH_TYPESET_COMMENT =
    	"GMFFParseMetamathTypesetComment";

    /**
     *  GMFFExportFromFolder command.
     *  <code>
     *  _ Optional. Primarily used for testing. Exports Proof Worksheet<br>
     *              file(s) from a given directory using the current<br>
     *              parameter settings (export parms, escapes, etc.)<br>
     *  <br>
     *              May appear anywhere after the "LoadFile" RunParm,<br>
     *              but should appear after Work Var allocations, at<br>
     *              least.<br>
     *  <br>
     *   - Default = N/A -- used for batch testing.<br>
     *  <br>
     *   _ Value1 = directory containing Proof Worksheet files<br>
     *  <br>
     *   _ Value2 = theorem label or "*" (all). If theorem label<br>
     *              input then it is the starting point of the
     *              export process, which will export the Max
     *              Number of files beginning at that label.
     *              If "*" input then the export begins at the
     *              first label. Either way, files are exported
     *              in lexicographic order -- i.e. alphabetically.
     *  <br>
     *   _ Value3 = file type of input Proof Worksheet files<br>
     *              (normally either .mmp or .mmt)<br>
     *  <br>
     *   _ Value4 = Max Number of proofs to export. Required.<br>
     *  <br>
     *   _ Value5 = Append File Name. Name of output file minus<br>
     *              the file type. Optional. If specified must<br>
     *              not contain any whitespace characters, or '/'<br>
     *              or '\' or ':' characters (for safety.) All<br>
     *              exported Proof Worksheets will be appended<br>
     *              to the named file (written at the end instead<br>
     *              of the beginning.) Used for regression testing.<br>
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFExportFromFolder,myproofs,syl,.mmp,1<br>
     *          GMFFExportFromFolder,myproofs,*,.mmt,100<br>
     *          GMFFExportFromFolder,myproofs,a2i,.mmt,5,Test20110915a<br>
     *  </code>
     */
    public static final String
			RUNPARM_GMFF_EXPORT_FROM_FOLDER =
		"GMFFExportFromFolder";

    /**
     *  GMFFExportTheorem command.
     *  <code>
     *  _ Optional. Primarily used for testing. Exports Proof Worksheet<br>
     *              file(s) from the loaded Metamath database using<br>
     *              the current parameter settings (export parms, escapes, etc.)<br>
     *  <br>
     *              May appear anywhere after the "LoadFile" RunParm,<br>
     *              but should appear after Proof Assistant parameters<br>
     *              initialized if the default Proof Assistant settings<br>
     *              are not used.<br>
     *  <br>
     *   - Default = N/A -- used for batch testing.<br>
     *  <br>
     *   _ Value1 = theorem label or "*" (all). If theorem label<br>
     *              input then it is the starting point of the<br>
     *              export process, which will export the Max<br>
     *              Number of files beginning at that label.<br>
     *              If "*" input then the export begins at the<br>
     *              first label. Either way, files are exported<br>
     *              in MObj.seq number -- i.e. by order of appearance<br>
     *              in the loaded Metamath database (LogicalSystem.)<br>
     *  <br>
     *   _ Value2 = Max Number of proofs to export. Required.<br>
     *  <br>
     *   _ Value3 = Append File Name. Name of output file minus<br>
     *              the file type. Optional. If specified must<br>
     *              not contain any whitespace characters, or '/'<br>
     *              or '\' or ':' characters (for safety.) All<br>
     *              exported Proof Worksheets will be appended<br>
     *              to the named file (written at the end instead<br>
     *              of the beginning.) Used for regression testing.<br>
     *  <br>
     *       - Examples:<<br>
     *          *        1         2         3         4<br>
     *          *234567890123456789012345678901234567890<br>
     *          GMFFExportTheorem,syl,1<br>
     *          GMFFExportTheorem,*,100<br>
     *          GMFFExportTheorem,syl,100,Test20110915a<br>
     *  </code>
     */
    public static final String
			RUNPARM_GMFF_EXPORT_THEOREM =
		"GMFFExportTheorem";


    // ----------------------------------------------------------
    // Constants mmj.util.TheoremLoaderBoss
    // ----------------------------------------------------------

    public static final String RUNPARM_THEOREM_LOADER_DJ_VARS_OPTION =
        "TheoremLoaderDjVarsOption";
    public static final String RUNPARM_THEOREM_LOADER_MMT_FOLDER     =
        "TheoremLoaderMMTFolder";
    public static final String RUNPARM_THEOREM_LOADER_AUDIT_MESSAGES =
        "TheoremLoaderAuditMessages";
    public static final String RUNPARM_LOAD_THEOREMS_FROM_MMT_FOLDER =
        "LoadTheoremsFromMMTFolder";
    public static final String RUNPARM_EXTRACT_THEOREM_TO_MMT_FOLDER =
        "ExtractTheoremToMMTFolder";
    public static final String
                RUNPARM_UNIFY_PLUS_STORE_IN_LOG_SYS_AND_MMT_FOLDER =
        "UnifyPlusStoreInLogSysAndMMTFolder";
    public static final String
                RUNPARM_UNIFY_PLUS_STORE_IN_MMT_FOLDER =
        "UnifyPlusStoreInMMTFolder";

    public static final String
                RUNPARM_THEOREM_LOADER_STORE_FORMULAS_ASIS =
        "TheoremLoaderStoreFormulasAsIs";
    public static final String
                RUNPARM_THEOREM_LOADER_STORE_MM_INDENT_AMT =
        "TheoremLoaderStoreMMIndentAmt";
    public static final String
                RUNPARM_THEOREM_LOADER_STORE_MM_RIGHT_COL =
        "TheoremLoaderStoreMMRightCol";

    public static final int THEOREM_LOADER_BOSS_FILE_BUFFER_SIZE
                                  = 32768;

    // =========================================================
    // ====> Option Value Literals for previous RunParm Names
    // =========================================================

    /**
     *  Option "*".
     */
    public static final String RUNPARM_OPTION_VALUE_ALL =
        "*";

    /**
     *  Option "basic" (ambiguity editing level).
     */
    public static final String RUNPARM_OPTION_VALUE_BASIC =
        "basic";    // ambiguity editing level: basic or paranoid

    /**
     *  Option "complete" (ambiguity editing level).
     */
    public static final String RUNPARM_OPTION_VALUE_COMPLETE =
        "complete"; // ambiguity editing level: basic or paranoid

    /**
     *  Option "new" (output file usage).
     */
    public static final String RUNPARM_OPTION_FILE_OUT_NEW =
        "new";

    /**
     *  Option "update" (output file usage).
     */
    public static final String RUNPARM_OPTION_FILE_OUT_UPDATE =
        "update";


    /**
     *  FILE_OUT_USAGE_DEFAULT = "new".
     */
    public static final String OPTION_FILE_OUT_USAGE_DEFAULT
                                  = "new";

    /**
     *  MAX_STATEMENT_PRINT_COUNT_DEFAULT = 9999.
     */
    public static final int MAX_STATEMENT_PRINT_COUNT_DEFAULT
                                  = 9999;

    /**
     *  Option "yes"
     */
    public static final String RUNPARM_OPTION_YES
                                  = "yes";

    /**
     *  Option "yes" abbreviated to "y"
     */
    public static final String RUNPARM_OPTION_YES_ABBREVIATED
                                  = "y";

    /**
     *  Option "no"
     */
    public static final String RUNPARM_OPTION_NO
                                  = "no";

    /**
     *  Option "no" abbreviated to "n"
     */
    public static final String RUNPARM_OPTION_NO_ABBREVIATED
                                  = "n";

    /**
     *  Option "on"
     */
    public static final String RUNPARM_OPTION_ON
                                  = "on";

    /**
     *  Option "off"
     */
    public static final String RUNPARM_OPTION_OFF
                                  = "off";


    /**
     *  Option Value 4 "unified" for ProofAsstExportToFile RunParm
     *  and Option Value 3 for ProofAsstBatchTest.
     *  <p>
     *  Means that Ref (statement labels) should be included on
     *  exported derivation proof steps.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_EXPORT_UNIFIED
                                  = "unified";

    /**
     *  Option Value 4 "un-unified" for ProofAsstExportToFile RunParm
     *  and Option Value 3 for ProofAsstBatchTest.
     *  <p>
     *  Means that Ref (statement labels) should NOT be included on
     *  exported derivation proof steps. This is the default.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_EXPORT_UN_UNIFIED
                                  = "un-unified";

    /**
     *  Option Value 5 "Randomized" for ProofAsstExportToFile
     *  RunParm and Option Value 4 for ProofAsstBatchTest.
     *  <p>
     *  Means that logical hypotheses should be randomized on
     *  exported proof steps.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_RANDOMIZED
                                  = "randomized";

    /**
     *  Option Value 5 "NotRandomized" for ProofAsstExportToFile
     *  RunParm and Option Value 4 for ProofAsstBatchTest.
     *  <p>
     *  Means that logical hypotheses should be not be randomized
     *  on exported proof steps, but left in the original order.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_NOT_RANDOMIZED
                                  = "notrandomized";

    /**
     *  Option Value 6 "Print" for ProofAsstExportToFile
     *  RunParm and Option Value 5 for ProofAsstBatchTest.
     *  <p>
     *  Means that an extra copy of the Proof Worksheet
     *  should be sent to the SystemOutputFile (or
     *  System.out). The print copy sent by ProofAsstBatchTest
     *  is the after-unification version, with the generated
     *  RPN proof, if available.
     *  exported proof steps.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_PRINT
                                  = "Print";

    /**
     *  Option Value 6 "NoPrint" for ProofAsstExportToFile
     *  RunParm and Option Value 5 for ProofAsstBatchTest.
     *  <p>
     *  "NoPrint" is the default.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_NO_PRINT
                                  = "NoPrint";

    /**
     *  Option Value 7 "DeriveFormulas for ProofAsstExportToFile
     *  RunParm and Option Value 6 for ProofAsstBatchTest.
     *  <p>
     *  Exported non-Qed derivation steps are output without
     *  formulas, leaving it up to the Proof Unifier to
     *  "Derive" the formulas.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_DERIVE_FORMULAS
                                  = "DeriveFormulas";

    /**
     *  Option Value 7 "NoDeriveFormulas" for ProofAsstExportToFile
     *  RunParm and Option Value 6 for ProofAsstBatchTest.
     *  <p>
     *  "NoDeriveFormulas" is the default.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_NO_DERIVE_FORMULAS
                                  = "NoDeriveFormulas";

    /**
     *  Option Value 7 for ProofAsstBatchTest.
     *  <p>
     *  Compares generated DjVars pairs after unification
     *  with the input .mm file's for the theorem.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_COMPARE_DJS
                                  = "CompareDJs";

    /**
     *  Option Value 7 for ProofAsstBatchTest.
     *  <p>
     *  "NoCompareDJs" is the default.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_NO_COMPARE_DJS
                                  = "NoCompareDJs";

    /**
     *  Option Value 8 for ProofAsstBatchTest.
     *  <p>
     *  Updates generated DjVars pairs after unification.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_UPDATE_DJS
                                  = "UpdateDJs";

    /**
     *  Option Value 8 for ProofAsstBatchTest.
     *  <p>
     *  "NoUpdateDJs" is the default.
     */
    public static final
        String RUNPARM_OPTION_PROOF_ASST_NO_UPDATE_DJS
                                  = "NoUpdateDJs";

    /**
     *  Option Value 9 for ProofAsstBatchTest.
     *  <p>
     *  Re-unifies the output Proof Worksheet after
     *  unification.
     */
    public static final
        String RUNPARM_OPTION_ASCII_RETEST
                                  = "AsciiRetest";

    /**
     *  Option Value 9 for ProofAsstBatchTest.
     *  <p>
     *  Re-unifies the output Proof Worksheet after
     *  unification. NoAsciiRetest is the default.
     */
    public static final
        String RUNPARM_OPTION_NO_ASCII_RETEST
                                  = "NoAsciiRetest";


    /**
     *  Maximum RGB Color Value = 255
     */
    public static final int RUNPARM_OPTION_MAX_RGB_COLOR
                                  = 255;

    /**
     *  Minimum RGB Color Value = 0
     */
    public static final int RUNPARM_OPTION_MIN_RGB_COLOR
                                  = 0;

    /**
     *  Number of RGB color values = 3
     */
    public static final int RUNPARM_NBR_RGB_COLOR_VALUES
                                  = 3;

    /**
     *  Default value for OutputVerbosity
     */
    public static final int OUTPUT_VERBOSITY_DEFAULT
                                  = 9999;

    /**
     *  Preprocess Request Option for Erase And Rederive Formulas.
     */
    public static final String RUNPARM_OPTION_ERASE_AND_REDERIVE_FORMULAS
                                  =
        "EraseAndRederiveFormulas";


    // ----------------------------------------------------------
    // ----------------------------------------------------------

    // ----------------------------------------------------------
    //  Messages from RunParmFile.java
    // ----------------------------------------------------------

//  OSBSOLETE AS OF MMJ2 PATH ENHANCEMENT
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

    public static final String ERRMSG_RUNPARM_FILE_EMPTY =
        "A-UT-0005 RunParmFile is empty!"
        + " Input file name = ";

    public static final String ERRMSG_RUNPARM_NEXT_AFTER_EOF =
        "A-UT-0006 RunParmFile.next() method invoked after"
        + " end of file!"
        + " Input file name = ";

    public static final String ERRMSG_RUNPARM_FILE_NOT_FOUND_1 =
        "A-UT-0007 RunParmFile not found or SecurityException."
        + " Input file name = ";
    public static final String ERRMSG_RUNPARM_FILE_NOT_FOUND_2 =
        " System message follows: ";


    // ----------------------------------------------------------
    //  Messages from RunParmArrayEntry.java
    // ----------------------------------------------------------

    public static final String ERRMSG_PARSER_LINE_EMPTY =
        "A-UT-0008 No RunParm fields returned by"
        + " DelimitedTextParser. Input line is empty?!";


    // ----------------------------------------------------------
    //  Messages from DelimitedTextParser.java
    // ----------------------------------------------------------

    public static final String ERRMSG_PARSER_INPUT_STRING_NULL =
        "A-UT-0009 DelimitedTextParser input text line"
        + " string is null.";

    public static final String ERRMSG_UNMATCHED_QUOTE_CHAR =
        "A-UT-0010 DelimitedTextParser found unmatched quote"
        + " character in input text line";

    public static final String ERRMSG_MISSING_DELIM =
        "A-UT-0011 DelimitedTextParser input text line field"
        + " has missing delimiter";

    public static final String ERRMSG_PARSER_LINE_ALREADY_REACHED =
        "A-UT-0012 DelimitedTextParser.nextField() method"
        + " called after end of line already reached!";

    // ----------------------------------------------------------
    //  Messages from BatchFramework.java
    // ----------------------------------------------------------

    public static final String ERRMSG_RUNPARM_FILE_BOGUS_1 =
        "\nA-UT-0013 Command Line Arguments invalid --"
        + " or RunParmFile not found, or otherwise invalid."
        + " Message returned by parameter handlers follows:\n";

    public static final String ERRMSG_RUNPARM_COMMENT_CAPTION =
        "**** I-UT-0014 RunParmFile line comment. Line #";

    public static final String ERRMSG_RUNPARM_EXECUTABLE_CAPTION =
        "**** I-UT-0015 Processing RunParmFile Command #";


    public static final String ERRMSG_RUNPARM_NAME_INVALID_1 =
        "A-UT-0016 RunParm name ";
    public static final String ERRMSG_RUNPARM_NAME_INVALID_2 =
        " not recognized! Whassup, cowboy?";

    public static final String ERRMSG_EQUALS_LITERAL =
        " = ";



    // ----------------------------------------------------------
    //  Messages from Boss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_FILE_USAGE_ERR_EXISTS_1 =
        "A-UT-0017 RunParm name ";
    public static final String ERRMSG_FILE_USAGE_ERR_EXISTS_2 =
        " file name parm ";
    public static final String ERRMSG_FILE_USAGE_ERR_EXISTS_3 =
        " already exists but ";
    public static final String ERRMSG_FILE_USAGE_ERR_EXISTS_4 =
        " was specified.";


    public static final String ERRMSG_FILE_UPDATE_NOT_ALLOWED_1 =
        "A-UT-0018 RunParm name ";
    public static final String ERRMSG_FILE_UPDATE_NOT_ALLOWED_2 =
        " file name parm ";
    public static final String ERRMSG_FILE_UPDATE_NOT_ALLOWED_3 =
        " already exists and ";
    public static final String ERRMSG_FILE_UPDATE_NOT_ALLOWED_4 =
        " was specified, but the existing file is a directory,"
        + " or update is not allowed.";


    public static final String ERRMSG_FILE_MISC_ERROR_1 =
        "A-UT-0019 RunParm name ";
    public static final String ERRMSG_FILE_MISC_ERROR_2 =
        " file name parm ";
    public static final String ERRMSG_FILE_MISC_ERROR_3 =
        " is supposed to be a file name. However, a file error was"
        + " encountered with the specified name. Message = ";


    public static final String ERRMSG_FILE_NAME_BLANK_1 =
        "A-UT-0020 RunParm name ";
    public static final String ERRMSG_FILE_NAME_BLANK_2 =
        " value field number ";
    public static final String ERRMSG_FILE_NAME_BLANK_3 =
        " is blank or an empty string. Expecting a file name.";


    public static final String ERRMSG_FILE_USAGE_PARM_UNRECOG_1 =
        "A-UT-0021 RunParm name ";
    public static final String ERRMSG_FILE_USAGE_PARM_UNRECOG_2 =
        " value field number ";
    public static final String ERRMSG_FILE_USAGE_PARM_UNRECOG_3 =
        " must equal '";
    public static final String ERRMSG_FILE_USAGE_PARM_UNRECOG_4 =
        "' or '";
    public static final String ERRMSG_FILE_USAGE_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String ERRMSG_FILE_USAGE_PARM_UNRECOG_6 =
        "'";


    public static final String ERRMSG_FILE_CHARSET_INVALID_1 =
        "A-UT-0022 RunParm name ";
    public static final String ERRMSG_FILE_CHARSET_INVALID_2 =
        " value field number ";
    public static final String ERRMSG_FILE_CHARSET_INVALID_3 =
        " = ";
    public static final String ERRMSG_FILE_CHARSET_INVALID_4 =
        " is not a valid Charset name. Message returned by"
        + " system follows: ";

    public static final String ERRMSG_FILE_CHARSET_UNSUPPORTED_1 =
        "A-UT-0023 RunParm name ";
    public static final String ERRMSG_FILE_CHARSET_UNSUPPORTED_2 =
        " value field number ";
    public static final String ERRMSG_FILE_CHARSET_UNSUPPORTED_3 =
        " = ";
    public static final String ERRMSG_FILE_CHARSET_UNSUPPORTED_4 =
        " is a valid Charset name but is not"
        + " supported by your Java system environment.";

    public static final String ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS_1 =
        "A-UT-0024 RunParm name ";
    public static final String ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS_2 =
        " must have at least ";
    public static final String ERRMSG_RUNPARM_NOT_ENOUGH_FIELDS_3 =
        " value fields";

    public static final String ERRMSG_RUNPARM_NBR_FORMAT_ERROR_1 =
        "A-UT-0025 RunParm name ";
    public static final String ERRMSG_RUNPARM_NBR_FORMAT_ERROR_2 =
        " value is formatted incorrectly. Should be"
        + " a simple integer number. Parse message follows: ";

    public static final String ERRMSG_RUNPARM_NBR_LE_ZERO_1 =
        "A-UT-0026 RunParm name ";
    public static final String ERRMSG_RUNPARM_NBR_LE_ZERO_2 =
        " value must be a simple integer number"
        + " greater than 0. Found input value = ";

    public static final String ERRMSG_RUNPARM_STMT_NOT_THEOREM_1 =
        "A-UT-0027 RunParm name ";
    public static final String ERRMSG_RUNPARM_STMT_NOT_THEOREM_2 =
        " value = ";
    public static final String ERRMSG_RUNPARM_STMT_NOT_THEOREM_3 =
        " is the label of a Stmt, but is not the label of a theorem."
        + " Therefore, VerifyProof cannot be performed.";

    public static final String ERRMSG_RUNPARM_STMT_LABEL_BLANK_1 =
        "A-UT-0028 RunParm name ";
    public static final String ERRMSG_RUNPARM_STMT_LABEL_BLANK_2 =
        " value is blank. A valid Stmt label is required.";

    public static final String ERRMSG_RUNPARM_STMT_LABEL_NOTFND_1 =
        "A-UT-0029 RunParm name ";
    public static final String ERRMSG_RUNPARM_STMT_LABEL_NOTFND_2 =
        " value = ";
    public static final String ERRMSG_RUNPARM_STMT_LABEL_NOTFND_3 =
        " is not a valid Stmt label in the"
        + " LogicalSystem that is presently loaded.";

    public static final String ERRMSG_FOLDER_NAME_BLANK_1 =
        "A-UT-0101 RunParm name ";
    public static final String ERRMSG_FOLDER_NAME_BLANK_2 =
        " value field number ";
    public static final String ERRMSG_FOLDER_NAME_BLANK_3 =
        " is blank or an empty string. Expecting a FOLDER name.";


    public static final String ERRMSG_NOT_A_FOLDER_1 =
        "A-UT-0102 RunParm name ";
    public static final String ERRMSG_NOT_A_FOLDER_2 =
        " folder name parm ";
    public static final String ERRMSG_NOT_A_FOLDER_3 =
        " exists, but is not a folder/directory (is a file?).";

    public static final String ERRMSG_FOLDER_NOTFND_1 =
        "A-UT-0103 RunParm name ";
    public static final String ERRMSG_FOLDER_NOTFND_2 =
        " folder name parm ";
    public static final String ERRMSG_FOLDER_NOTFND_3 =
        " invalid. No such folder (or file) found!";

    public static final String ERRMSG_FOLDER_MISC_ERROR_1 =
        "A-UT-0104 RunParm name ";
    public static final String ERRMSG_FOLDER_MISC_ERROR_2 =
        " folder name parm ";
    public static final String ERRMSG_FOLDER_MISC_ERROR_3 =
        " is supposed to be a folder. However, an error was"
        + " encountered while using the specified name. Message = ";

    public static final String ERRMSG_RECHECK_PA_1 =
        "A-UT-0105 RunParm name ";
    public static final String ERRMSG_RECHECK_PA_2 =
        " value not equal to 'yes' or 'no'.";

    public static final String ERRMSG_BAD_ON_OFF_PARM_1 =
        "A-UT-0106 RunParm name ";
    public static final String ERRMSG_BAD_ON_OFF_PARM_2 =
        " value not equal to 'on' or 'off'.";


    public static final String ERRMSG_FILE_NOTFND_1 =
        "A-UT-0107 RunParm name ";
    public static final String ERRMSG_FILE_NOTFND_2 =
        " file name parm ";
    public static final String ERRMSG_FILE_NOTFND_3 =
        " not found.";

    public static final String ERRMSG_FILE_READ_NOT_ALLOWED_1 =
        "A-UT-0108 RunParm name ";
    public static final String ERRMSG_FILE_READ_NOT_ALLOWED_2 =
        " file name parm ";
    public static final String ERRMSG_FILE_READ_NOT_ALLOWED_3 =
        " exists, but the existing file is a directory or"
        + " read access is not allowed.";

    public static final String
                    ERRMSG_RUNPARM_NONBLANK_PRINT_STR_BAD_1 =
        "A-UT-0109 RunParm name ";
    public static final String
                    ERRMSG_RUNPARM_NONBLANK_PRINT_STR_BAD_2 =
        " value invalid. Must be a printable string of length 1 or"
        + " more consisting only of non-blank printable characters"
        + " (7-bit ASCII only, just like Metamath.)";

    public static final String ERRMSG_RUNPARM_RGB_RANGE_1 =
        "A-UT-0110 RunParm name ";
    public static final String ERRMSG_RUNPARM_RGB_RANGE_2 =
        " value less than minimum RGB value ";
    public static final String ERRMSG_RUNPARM_RGB_RANGE_3 =
        ", or greater than maximum RGB value ";
    public static final String ERRMSG_RUNPARM_RGB_RANGE_4 =
        ". Input RGB number = ";

    public static final String ERRMSG_NOT_A_FILE_1 =
        "A-UT-0111 RunParm name ";
    public static final String ERRMSG_NOT_A_FILE_2 =
        " file name parm ";
    public static final String ERRMSG_NOT_A_FILE_3 =
        " exists, but is a folder/directory, not a file!";

    public static final String ERRMSG_BAD_FILE_NAME_SUFFIX_1 =
        "A-UT-0112 RunParm name ";
    public static final String ERRMSG_BAD_FILE_NAME_SUFFIX_2 =
        " file name parm ";
    public static final String ERRMSG_BAD_FILE_NAME_SUFFIX_3 =
        " must equal '.txt', '.TXT', '.mmp' or '.MMP'.";

    public static final String ERRMSG_RUNPARM_NBR_LT_ZERO_1 =
        "A-UT-0113 RunParm name ";
    public static final String ERRMSG_RUNPARM_NBR_LT_ZERO_2 =
        " value must be a simple integer number"
        + " greater than or equal to 0. Found input value = ";


    // ----------------------------------------------------------
    //  Messages from GrammarBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_AMBIG_EDIT_LEVEL_INVALID_1 =
        "A-UT-0030 RunParm name ";
    public static final String ERRMSG_AMBIG_EDIT_LEVEL_INVALID_2 =
        " value is blank or invalid. Must equal '";
    public static final String ERRMSG_AMBIG_EDIT_LEVEL_INVALID_3 =
        "' or '";
    public static final String ERRMSG_AMBIG_EDIT_LEVEL_INVALID_4 =
        "'";

    public static final String ERRMSG_PARSE_RPN_1 =
        "I-UT-0031 Parse RPN for Statement ";
    public static final String ERRMSG_PARSE_RPN_2 =
        " = ";

    // ----------------------------------------------------------
    //  Messages from LogicalSystemBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_MM_FILE_NOT_LOADED_1 =
        "A-UT-0032 Cannot complete current RunParmFile request"
        + " because either, a) the previous ";
    public static final String ERRMSG_MM_FILE_NOT_LOADED_2 =
        " RunParm processing detected errors in"
        + " the input Metamath file; or b) a ";
    public static final String ERRMSG_MM_FILE_NOT_LOADED_3 =
        " RunParm must be input before the"
        + " current RunParmFile line."
        + "\nReview previous error messages to find the error.";

    public static final String ERRMSG_LOAD_ENDPOINT_LABEL_BLANK =
        " A-UT-0201 RunParm LoadEndpointStmtLabel has blank"
        + " label. Delete/Comment out the RunParm or specify a"
        + " bogus Statement Label, such as Z999ZZZZZ if you do not"
        + " wish to limit the load of Metamath statements.";

    public static final String ERRMSG_PROVABLE_TYP_CD_BOGUS_1 =
        "A-UT-0202 ProvableLogicStmtType invalid. Is blank"
        + " or is zero-length string";

    public static final String ERRMSG_LOGIC_TYP_CD_BOGUS_1 =
        "A-UT-0203 LogicStmtType invalid. Is blank"
        + " or is zero-length string";

    public static final String ERRMSG_BOOK_MANAGER_ALREADY_EXISTS_1 =
        "A-UT-0204 BookManager already constructed."
        + " The 'BookManagerEnabled' RunParm must be located before"
        + " the 'LoadFile' RunParm command and the enabled/disabled"
        + " status cannot be changed after LoadFile is executed!";

    // ----------------------------------------------------------
    //  Messages from Dump.java
    // ----------------------------------------------------------

    public static final String ERRMSG_DUMP_STMT_UNRECOG_1 =
        "A-UT-0033 Uh oh! dumpStmtTbl() does not recognize"
        + " this Stmt type! Stmt label = ";


    // ----------------------------------------------------------
    //  Messages from OutputBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_SYSOUT_PRINT_WRITER_IO_ERROR_1 =
        "A-UT-0034 OutputBoss found IO error on sysOut PrintWriter.";

    public static final String ERRMSG_SYSERR_PRINT_WRITER_IO_ERROR_1 =
        "A-UT-0035 OutputBoss found IO error on sysErr PrintWriter.";

    public static final String ERRMSG_BOOK_MANAGER_NOT_ENABLED_1 =
        "A-UT-1203 OutputBoss found BookManager not enabled when"
        + " processing RunParm command ";
    public static final String ERRMSG_BOOK_MANAGER_NOT_ENABLED_2 =
        ". Use RunParm command 'BookManagerEnabled,yes' prior"
        + " to the LoadFile RunParm to enable the BookManager.";

    public static final String
                        ERRMSG_BOOK_MANAGER_SECTION_NBR_NOT_FOUND_1
                                  =
        "A-UT-1204 BookManager Section Number ";
    public static final String
                        ERRMSG_BOOK_MANAGER_SECTION_NBR_NOT_FOUND_2
                                  =
        " not found when processing RunParm command ";


    // ----------------------------------------------------------
    //  Messages from ProofAsstBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_PA_REQUIRES_GRAMMAR_INIT =
        "E-UT-0036 ProofAsstBoss could not load ProofAsst as"
        + " requested. RunParms requiring the ProofAsst should follow"
        + " successful 'Load' and 'Parse,*' commands. It is required"
        + " that a .mm file be loaded, that the Grammar be"
        + " successfully initialized (no errors), and that"
        + " all Metamath statements be grammatically parsed"
        + " prior to running the ProofAsst (for use in"
        + " unification)."
        + "\nFor more information, see:"
        + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt."
        + "\nReview previous error messages to find the error.";



    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_FONT_SZ_RANGE_ERR_1 =
        "A-UT-0037 ProofAsstFontSize RunParm must be between ";
    public static final String ERRMSG_RUNPARM_FONT_SZ_RANGE_ERR_2 =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_PA_FLC_RANGE_ERR_1 =
        "A-UT-0038 ProofAsstFormulaLeftCol RunParm must be between ";
    public static final String ERRMSG_RUNPARM_PA_FLC_RANGE_ERR_2 =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_PA_FRC_RANGE_ERR_1 =
        "A-UT-0039 ProofAsstFormulaRightCol RunParm must be between ";
    public static final String ERRMSG_RUNPARM_PA_FRC_RANGE_ERR_2 =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_PA_RLC_RANGE_ERR_1 =
        "A-UT-0040 ProofAsstRPNProofLeftCol RunParm must be between ";
    public static final String ERRMSG_RUNPARM_PA_RLC_RANGE_ERR_2 =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_PA_RRC_RANGE_ERR_1 =
        "A-UT-0041 ProofAsstRPNProofRightCol RunParm must be between ";
    public static final String ERRMSG_RUNPARM_PA_RRC_RANGE_ERR_2 =
        " (inclusive) and ";

    public static final String ERRMSG_SELECTOR_MISSING_1 =
        "A-UT-0042 RunParm name ";
    public static final String ERRMSG_SELECTOR_MISSING_2 =
        " value field number ";
    public static final String ERRMSG_SELECTOR_MISSING_3 =
        ", the 'Selector' option is blank, null or empty";

    public static final String ERRMSG_SELECTOR_NOT_A_STMT_1 =
        "A-UT-0043 RunParm name ";
    public static final String ERRMSG_SELECTOR_NOT_A_STMT_2 =
        " value field number ";
    public static final String ERRMSG_SELECTOR_NOT_A_STMT_3 =
        " with value (statement label) = ";
    public static final String ERRMSG_SELECTOR_NOT_A_STMT_4 =
        " not found in Logical System Statement Table.";

    public static final String ERRMSG_SELECTOR_NOT_A_THEOREM_1 =
        "A-UT-0044 RunParm name ";
    public static final String ERRMSG_SELECTOR_NOT_A_THEOREM_2 =
        " value field number ";
    public static final String ERRMSG_SELECTOR_NOT_A_THEOREM_3 =
        " with value (statement label) = ";
    public static final String ERRMSG_SELECTOR_NOT_A_THEOREM_4 =
        " found in Logical System Statement Table but the"
        + " statement found is not a theorem.";

    public static final String ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_1 =
        "A-UT-0045 RunParm name ";
    public static final String ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_2 =
        " value field number ";
    public static final String ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_3 =
        " must equal '";
    public static final String ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_4 =
        "' or '";
    public static final String ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String ERRMSG_EXPORT_UNIFIED_PARM_UNRECOG_6 =
        "'";

    public static final String
                            ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_1 =
        "A-UT-0046 RunParm name ";
    public static final String
                            ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_2 =
        " value field number ";
    public static final String
                            ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_3 =
        " must equal '";
    public static final String
                            ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_4 =
        "' or '";
    public static final String
                            ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String
                            ERRMSG_EXPORT_RANDOMIZED_PARM_UNRECOG_6 =
        "'";

    public static final String
                            ERRMSG_EXPORT_PRINT_PARM_UNRECOG_1 =
        "A-UT-0047 RunParm name ";
    public static final String
                            ERRMSG_EXPORT_PRINT_PARM_UNRECOG_2 =
        " value field number ";
    public static final String
                            ERRMSG_EXPORT_PRINT_PARM_UNRECOG_3 =
        " must equal '";
    public static final String
                            ERRMSG_EXPORT_PRINT_PARM_UNRECOG_4 =
        "' or '";
    public static final String
                            ERRMSG_EXPORT_PRINT_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String
                            ERRMSG_EXPORT_PRINT_PARM_UNRECOG_6 =
        "'";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_PA_TEXT_COL_RANGE_ERR_1
                                  =
        "A-UT-0048 ProofAsstTextColumns RunParm must be between ";
    public static final String ERRMSG_RUNPARM_PA_TEXT_COL_RANGE_ERR_2 =
        " (inclusive) and ";

    public static final String  PROOF_ASST_FONT_FAMILY_LIST_CAPTION =
        " List of Font Families defined in the system: \n";


    public static final String
        ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_1 =
        "A-UT-0049 RunParm name ";
    public static final String
        ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_2 =
        " value field number ";
    public static final String
        ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_3 =
        " must equal '";
    public static final String
        ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_4 =
        "' or '";
    public static final String
        ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String
        ERRMSG_EXPORT_DERIVE_FORMULAS_PARM_UNRECOG_6 =
        "'";

    public static final String
        ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_1 =
        "A-UT-0050 RunParm name ";
    public static final String
        ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_2 =
        " value field number ";
    public static final String
        ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_3 =
        " must equal '";
    public static final String
        ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_4 =
        "' or '";
    public static final String
        ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String
        ERRMSG_IMPORT_COMPARE_DJS_PARM_UNRECOG_6 =
        "'";

    public static final String
        ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_1 =
        "A-UT-0051 RunParm name ";
    public static final String
        ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_2 =
        " value field number ";
    public static final String
        ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_3 =
        " must equal '";
    public static final String
        ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_4 =
        "' or '";
    public static final String
        ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String
        ERRMSG_IMPORT_UPDATE_DJS_PARM_UNRECOG_6 =
        "'";

    public static final String
        ERRMSG_ASCII_RETEST_PARM_UNRECOG_1 =
        "A-UT-0052 RunParm name ";
    public static final String
        ERRMSG_ASCII_RETEST_PARM_UNRECOG_2 =
        " value field number ";
    public static final String
        ERRMSG_ASCII_RETEST_PARM_UNRECOG_3 =
        " must equal '";
    public static final String
        ERRMSG_ASCII_RETEST_PARM_UNRECOG_4 =
        "' or '";
    public static final String
        ERRMSG_ASCII_RETEST_PARM_UNRECOG_5 =
        "'. Value input was '";
    public static final String
        ERRMSG_ASCII_RETEST_PARM_UNRECOG_6 =
        "'";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String ERRMSG_RUNPARM_PA_TEXT_ROW_RANGE_ERR_1
                                  =
        "A-UT-0053 ProofAsstTextRows RunParm must be between ";
    public static final String ERRMSG_RUNPARM_PA_TEXT_ROW_RANGE_ERR_2 =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String
        ERRMSG_RUNPARM_PA_ERR_MSG_ROW_RANGE_ERR_1
                                  =
      "A-UT-0054 ProofAsstErrorMessageRows RunParm must be between ";
    public static final String
        ERRMSG_RUNPARM_PA_ERR_MSG_ROW_RANGE_ERR_2
                                  =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for min/max values
    public static final String
        ERRMSG_RUNPARM_PA_ERR_MSG_COL_RANGE_ERR_1
                                  =
      "A-UT-0055 ProofAsstErrorMessageColumns RunParm must be between ";
    public static final String
        ERRMSG_RUNPARM_PA_ERR_MSG_COL_RANGE_ERR_2
                                  =
        " (inclusive) and ";

    public static final String
                            ERRMSG_PREPROCESS_OPTION_UNRECOG_1
                                  =
      "A-UT-0056 PreprocessRequestBatchTest RunParm Option must be"
      + "'EraseAndRederiveFormulas' at this time (there is only one"
      + " type of PreprocessRequest now.) Input was ";


    // see mmj.pa.PaConstants.java for min/max values
    public static final String
        ERRMSG_RUNPARM_SS_DLG_PANE_WIDTH_ERR_1
                                  =
      "A-UT-0057 StepSelectorDialogPaneWidth RunParm must be between ";

    public static final String
        ERRMSG_RUNPARM_SS_DLG_PANE_WIDTH_ERR_2
                                  =
        " (inclusive) and ";


    // see mmj.pa.PaConstants.java for min/max values
    public static final String
        ERRMSG_RUNPARM_SS_DLG_PANE_HEIGHT_ERR_1
                                  =
      "A-UT-0058 StepSelectorDialogPaneHeight RunParm must be between ";

    public static final String
        ERRMSG_RUNPARM_SS_DLG_PANE_HEIGHT_ERR_2
                                  =
        " (inclusive) and ";

    // see mmj.pa.PaConstants.java for max value
    public static final String
        ERRMSG_RUNPARM_PROOF_ASST_FREESPACE_ERR_1
                                  =
      "A-UT-0059 ProofAsstAssrtListFreespace RunParm must be"
      + " greater than or equal to zero and no greater than ";

    // ----------------------------------------------------------
    //  Messages from TMFFBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_TMFF_REQUIRES_GRAMMAR_INIT =
        "A-UT-0601 TMFFBoss did not create TMFFPreferences, which"
        + " will prevent ProofAsstGUI from running, among other"
        + " things. TMFF requires that all input .mm statements"
        + " be successfully parsed prior to invoking TMFF."
        + " Therefore, TMFF RunParms should follow"
        + " successful 'Load' and 'Parse,*' commands. It is required"
        + " that a .mm file be loaded, that the Grammar be"
        + " successfully initialized (no errors), and that"
        + " all Metamath statements be grammatically parsed"
        + " first!"
        + "\nFor more information, see:"
        + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt."
        + "\nReview previous error messages to find the error.";

    public static final String ERRMSG_RUNPARM_DEFINE_SCHEME_ERR_1 =
        "A-UT-0602 TMFFDefineScheme RunParm Error. Detailed"
        + " error message follows: ";

    public static final String ERRMSG_RUNPARM_DEFINE_FORMAT_ERR_1 =
        "A-UT-0603 TMFFDefineFormat RunParm Error. Detailed"
        + " error message follows: ";

    public static final String ERRMSG_RUNPARM_USE_FORMAT_ERR_1 =
        "A-UT-0604 TMFFUseFormat RunParm Error. Detailed"
        + " error message follows: ";

    public static final String ERRMSG_RUNPARM_ALT_FORMAT_ERR_1 =
        "A-UT-0605 TMFFAltFormat RunParm Error. Detailed"
        + " error message follows: ";

    public static final String ERRMSG_RUNPARM_USE_INDENT_ERR_1 =
        "A-UT-0606 TMFFUseIndent RunParm Error. Detailed"
        + " error message follows: ";

    public static final String ERRMSG_RUNPARM_ALT_INDENT_ERR_1 =
        "A-UT-0607 TMFFAltIndent RunParm Error. Detailed"
        + " error message follows: ";

    // ----------------------------------------------------------
    //  Messages from VerifyProofBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_IGNORING_VERIFY_PROOF_RUNPARM =
        "I-UT-0701 VerifyProof RunParm request ignored because"
        + " 'LoadProofs' RunParm 'no' input.";

    // ----------------------------------------------------------
    //  Messages from WorkVarBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_WV_MGR_REQUIRES_GRAMMAR_INIT =
        "A-UT-0801 WorkVarBoss did not initialize the WorkVarManager"
        + " as requested -- which is necessary for definition and"
        + " declaration of Work Variables, not to mention the"
        + " Proof Assistant itself. It is required"
        + " that a .mm file be loaded, that the Grammar be"
        + " successfully initialized (no errors)"
        + " prior to defining or declaring Work Variables, or"
        + " running the ProofAsstGUI.\nFor more information, see:"
        + " ..\\mmj2\\mmj2jar\\AnnotatedRunParms.txt."
        + "\nReview previous error messages to find the error.";

    // ----------------------------------------------------------
    //  Messages from SvcBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_SVC_CALLBACK_CLASS_INIT_ERROR_1
                                  =
        "A-UT-0901 SvcBoss encountered a problem during the load"
        + " and instantiation of the input SvcCallbackClass name =";

    public static final String ERRMSG_SVC_CALLBACK_CLASS_INIT_ERROR_2
                                  =
        ". The specific error message returned by the Java Runtime"
        + " Environment follows: ";

    public static final String ERRMSG_SVC_CALLBACK_CLASS_CAST_ERROR_1
                                  =
        "A-UT-0902 SvcBoss encountered a problem during the 'cast'"
        + " of the input class object to the SvcCallback interface."
        + " The input SvcCallbackClass name =";

    public static final String ERRMSG_SVC_CALLBACK_CLASS_CAST_ERROR_2
                                  =
        ". The specific error message returned by the Java Runtime"
        + " Environment follows: ";

    public static final String ERRMSG_SVC_ARG_ERROR_1
                                  =
        "A-UT-0903 Input SrvArg invalid. Key value must be"
        + " unique non-blank character string with length > 1."
        + " Input Key parameter = ";

    public static final String ERRMSG_SVC_ARG_ERROR_2
                                  =
        ". Input Value parameter = ";

    public static final String ERRMSG_SVC_CALL_PROOF_ASST_MISSING_1
                                  =
        "A-UT-0904 SvcCall command not completed:"
        + " Unable to initialize ProofAsst object (probably"
        + " because a load, verify or parse RunParm command"
        + " encountered an error. Check previous error"
        + " messages.";

    public static final String
                    ERRMSG_SVC_CALL_THEOREM_LOADER_MISSING_1
                                  =
        "A-UT-0905 SvcCall command not completed:"
        + " Unable to initialize TheoremLoader object."
        + " Check previous error messages (for clues :-)";

    // ----------------------------------------------------------
    //  Messages from MergeSortedArrayLists.java
    // ----------------------------------------------------------

    public static final String ERRMSG_MERGE_SORTED_LISTS_DUP_ERROR_1
                                  =
        "A-UT-1001 An element of the source list = ";
    public static final String ERRMSG_MERGE_SORTED_LISTS_DUP_ERROR_2
                                  =
        " was found in the destination list, and"
        + " a program-abort was requested if this occurred.";


    // ----------------------------------------------------------
    //  Messages from TheoremLoaderBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_1
                                  =
        "A-UT-1101 Error encountered in Theorem Loader RunParm ";

    public static final String ERRMSG_THEOREM_LOADER_RUN_PARM_ERROR_2
                                  =
        ". Explanatory message details follow: ";

    public static final String ERRMSG_THEOREM_LOADER_READER_ERROR_1
                                  =
        "A-UT-1102 IO error encountered reading Proof Worksheet file ";

    public static final String ERRMSG_THEOREM_LOADER_READER_ERROR_2
                                  =
        ". RunParm = ";
    public static final String ERRMSG_THEOREM_LOADER_READER_ERROR_3
                                  =
        ". Detailed IOException Message follows: ";

    // ----------------------------------------------------------
    //  Messages from GMFFBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_GMFF_INITIALIZATION_ERROR_1
                                  =
        "A-UT-1201 Errors encountered during GMFFInitialize RunParm."
        + " GMFF not successfully initialized.";

    public static final String ERRMSG_GMFF_PROOF_ASST_MISSING_1
                                  =
        "A-UT-1202 GMFF command ";
    public static final String ERRMSG_GMFF_PROOF_ASST_MISSING_2
                                  =
		" not completed:"
        + " Unable to initialize ProofAsst object (probably"
        + " because a load, verify or parse RunParm command"
        + " encountered an error. Check previous error"
        + " messages.";

    public static final String ERRMSG_GMFF_RUNPARM_ERROR_1
                                  =
        "E-UT-1203 GMFF command ";
    public static final String ERRMSG_GMFF_RUNPARM_ERROR_2
                                  =
		" encountered a problem. Please review previous"
		+ " messages to diagnose the problem.";


    // ----------------------------------------------------------
    //  Messages from CommandLineArguments.java
    // ----------------------------------------------------------


	public static final String ERRMSG_FAIL_POPUP_WINDOW_ARGUMENT_1
	                            =
        "A-UT-1301 Command Line ";
	public static final String ERRMSG_FAIL_POPUP_WINDOW_ARGUMENT_2
	                            =
        " invalid. Must equal 'y' or 'n'. Found = ";

	public static final String ERRMSG_TEST_OPTION_ARGUMENT_1
	                            =
        "A-UT-1302 Command Line ";
	public static final String ERRMSG_TEST_OPTION_ARGUMENT_2
	                            =
        " invalid. Must equal 'y' or 'n'. Found = ";

    // ----------------------------------------------------------
    //  Messages from Paths.java
    // ----------------------------------------------------------

	public static final String ERRMSG_PATH_INVALID_1
	                            =
        "A-UT-1401 Command Line ";
	public static final String ERRMSG_PATH_INVALID_2
	                            =
        " invalid. Path does not exist or is a file: ";

	public static final String ERRMSG_PATH_SECURITY_ERROR_1
	                            =
        "A-UT-1402 Command Line ";
	public static final String ERRMSG_PATH_SECURITY_ERROR_2
	                            =
        " invalid. SecurityException on path access: ";


    // ----------------------------------------------------------
    //  Dump.java "report" literals.
    // ----------------------------------------------------------

    public static final String DUMP_LOGSYS_COUNTS =
        "LogSysCounts: ";

    public static final String DUMP_PROVABLE_TYP_SET =
        "Grammar.provableLogicStmtTypSet: ";

    public static final String DUMP_LOGIC_TYP_SET =
        "Grammar.logicStmtTypSet: ";

    public static final String DUMP_VARHYP_TYP_SET =
        "Grammar.varHypTypSet: ";

    public static final String DUMP_SYNTAX_AXIOM_TYP_SET =
        "Grammar.syntaxAxiomTypSet: ";

    public static final String DUMP_NULLS_PERMITTED_TYP_SET =
        "Grammar.nullsPermittedTypSet: ";

    public static final String DUMP_LOGSYS_SYM_TBL =
        "LogicalSystem.symTbl: ";

    public static final int DUMP_GRAMMAR_RULE_MAX_PRINT
                                  = 9999;

    public static final String DUMP_NULLS_PERMITTED_LIST =
        "NullsPermitted List: ";

    public static final String DUMP_TYPE_CONVERSION_LIST =
        "TypeConversion List: ";

    public static final String DUMP_NOTATION_LIST =
        "Notation List: ";

    public static final String DUMP_LOGSYS_STMT_TBL =
        "LogicalSystem.stmtTbl: ";

    public static final String DUMP_LOGICAL_SYSTEM =
        "LogicalSystem: ";

    public static final String DUMP_START =
        " Start";

    public static final String DUMP_END =
        " End";

    public static final String DUMP_SYM_TBL_SIZE =
        " symTbl.size()= ";

    public static final String DUMP_STMT_TBL_SIZE =
        " stmtTbl.size()= ";

    public static final String DUMP_SYM_TBL =
        "symTbl:";
    public static final String DUMP_SYM_TBL_UNDERSCORE =
        "=======";
    public static final String DUMP_SYM_TBL_IS_EMPTY =
        "symTbl IS EMPTY!";

    public static final String DUMP_STMT_TBL =
        "stmtTbl:";
    public static final String DUMP_STMT_TBL_UNDERSCORE =
        "========";
    public static final String DUMP_STMT_TBL_IS_EMPTY =
        "stmtTbl IS EMPTY!";

    public static final String DUMP_OF_FIRST =
        " dump of first ";

    public static final String DUMP_THEOREM =
        "Theorem: ";

    public static final String DUMP_OPT_FRAME_HYP_ARRAY =
        "Optframe, optHypArray: [";

    public static final String DUMP_OPT_FRAME_DJ_VARS =
        "           DjVars  : [";

    public static final String DUMP_START_BRACKET =
        "[";
    public static final String DUMP_END_BRACKET =
        "] ";
    public static final char   DUMP_COMMA
                                  = ',';

    public static final String DUMP_PROOF =
        "Proof: ";
    public static final String DUMP_PROOF_MISSING_STEP =
        "? ";

    public static final String DUMP_AXIOM =
        "Axiom: ";

    public static final String DUMP_VARHYP_RESEQ =
        "syntaxAxiomVarHypReseq: [";

    public static final String DUMP_AXIOM_UNIQUE_CNST =
        " syntaxAxiomHasUniqueCnst ";

    public static final String DUMP_LOGHYP =
        "LogHyp: ";

    public static final String DUMP_VARHYP =
        "VarHyp: ";

    public static final String DUMP_MAND_FRAME_HYP_ARRAY =
        "Mandframe, hypArray: [";

    public static final String DUMP_MAND_FRAME_DJ_VARS =
        "           DjVars  : [";


    public static final String DUMP_TYP =
        " Typ: ";

    public static final String DUMP_IS_ACTIVE =
        " isActive";

    public static final String DUMP_IS_ASSRT =
        " isAssrt";

    public static final String DUMP_IS_HYP =
        " isHyp";

    public static final String DUMP_IS_CNST =
        " isCnst";

    public static final String DUMP_MAND_VARHYP_ARRAY =
        " VarHypArray: [";

    public static final String DUMP_FORMULA =
        "Formula:";

    public static final String DUMP_EXPR_RPN =
        "ExprRPN: ";

    public static final String DUMP_VAR =
        "Var: ";

    public static final String DUMP_ACTIVE_VARHYP =
        " activeVarHyp: ";

    public static final String DUMP_CNST =
        "Cnst: ";

    public static final String DUMP_IS_VAR_TYP =
        " isVarTyp";

    public static final String DUMP_IS_GRAMMATICAL_TYP =
        " isGrammaticalTyp";

    public static final String DUMP_IS_PROVABLE_TYP =
        " isProvableLogicStmtTyp";

    public static final String DUMP_IS_LOGIC_TYP =
        " isLogicStmtTyp";

    public static final String DUMP_IS_SYNTAX_AXIOM_TYP =
        " isSyntaxAxiomTyp";

    public static final String DUMP_LEN1_CNST_RULE_NBR =
        " len1CnstNotationRule ruleNbr = ";

    public static final String DUMP_LEN1_CNST_AXIOM =
        " Axiom = ";

    public static final String DUMP_EARLEY_FIRST =
        " EarleyFIRST= [";

    public static final String DUMP_RULE_COLLECTION =
        "GrammarRule Collection:";

    public static final String DUMP_RULE_COLLECTION_UNDERSCORE =
        "=======================";

    public static final String DUMP_RULE_COLLECTION_IS_EMPTY =
        "GrammarRule Collection IS EMPTY!";

    public static final String DUMP_GRAMMAR_RULE =
        "GrammarRule: ";

    public static final String DUMP_RULE_NBR =
        " RuleNbr: ";

    public static final String DUMP_TYPE_CODE =
        " Type Code: ";

    public static final String DUMP_MAX_SEQ_NBR =
        " MaxSeqNbr: ";

    public static final String DUMP_NBR_HYP_PARAMS_USED =
        " NbrHypParamsUsed: ";

    public static final String DUMP_PARAM_TREE_AS_RPN =
        " ParamTransformationTree as RPN: ";

    public static final String DUMP_PARAM_VARHYP_NODE_ARRAY =
        " ParamVarHypNode Array: ";

    public static final String DUMP_NOTATION_RULE =
        "NotationRule: ";

    public static final int    DUMP_NOTATION_LABEL_PADIT
                                  = 7;

    public static final int    DUMP_NOTATION_RULE_NBR_PADIT
                                  = 5;

    public static final int    DUMP_NOTATION_RULE_TYP_PADIT
                                  = 7;

    public static final String DUMP_GRAMMAR_RULE_REPLACEMENT_SYMBOL =
        " =: ";
    public static final String DUMP_RULE_CONTINUATION_LIT =
        "  | ";
    public static final String DUMP_RULE_COLON =
        " : ";

    public static final String DUMP_IS_GIMME_MATCH_NBR =
        " isGimmeMatchNbr = ";

    public static final String DUMP_TYPE_CONVERSION_RULE =
        "TypeConversionRule: ";

    public static final String DUMP_RIGHT_ARROW =
        " -> ";

    public static final String DUMP_NULLS_PERMITTED_RULE =
        "NullsPermittedRule: ";

    public static final String DUMP_THE_GRAMMAR =
        "The Grammar";

    public static final String DUMP_THE_GRAMMAR_UNDERSCORE =
        "===========";

    public static final String DUMP_THE_GRAMMAR_IS_EMPTY =
        "The Grammar is empty?!?";

    /* stuff for dumping BookManager info */

    public static final String DUMP_BM_CNST    = "Cnst ";
    public static final String DUMP_BM_VAR     = "Var ";
    public static final String DUMP_BM_VARHYP  = "VarHyp ";
    public static final String DUMP_BM_LOGHYP  = "LogHyp ";
    public static final String DUMP_BM_AXIOM   = "Axiom ";
    public static final String DUMP_BM_THEOREM = "Theorem ";
    public static final String DUMP_BM_UNKNOWN = "?Stmt? ";
    public static final String DUMP_BM_DOT     = ".";
    public static final String DUMP_BM_EQ_COL  = "=: ";

}










