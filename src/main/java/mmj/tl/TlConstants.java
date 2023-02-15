//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * TlConstants.java  0.02 11/01/2011
 *
 * Version 0.01
 * -- new.
 *
 * Version 0.02 - Nov-01-2011:  comment update.
 */

package mmj.tl;

import static mmj.pa.ErrorCode.of;

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
import mmj.pa.ErrorCode;
import mmj.pa.PaConstants;
import mmj.transforms.TrConstants;
import mmj.util.UtilConstants;
import mmj.verify.GrammarConstants;
import mmj.verify.ProofConstants;

/**
 * (Most) Constants used in mmj.tl classes
 * <p>
 * There are two primary types of constants: parameters that are "hardcoded"
 * which affect/control processing, and error/info messages.
 * <p>
 * Each mmj message begins with a code, such as this:
 * <p>
 * <code>E-LA-0007</code>
 * <p>
 * where the format of the code is {@code X-YY-9999}<br>
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
 */
public class TlConstants {

    /**
     * SYNONYM_TRUE_1, 2 and 3 = "true", "on" and "yes".
     */
    public static final String SYNONYM_TRUE_1 = "true";
    public static final String SYNONYM_TRUE_2 = "on";
    public static final String SYNONYM_TRUE_3 = "yes";

    /**
     * SYNONYM_FALSE_1, 2 and 3 = "false", "off" and "no".
     */
    public static final String SYNONYM_FALSE_1 = "false";
    public static final String SYNONYM_FALSE_2 = "off";
    public static final String SYNONYM_FALSE_3 = "no";

    // ----------------------------------------------------------
    // Constants from MMTTheoremFile.java
    // ----------------------------------------------------------

    /**
     * MMTFileFilter valid file name suffix ".mmt".
     */
    public static final String FILE_SUFFIX_MMT = ".mmt";

    /**
     * MMTTheoremFile output file buffer size = 4096.
     */
    public static final int FILE_WRITER_BUFFER_SIZE = 4096;

    // ----------------------------------------------------------
    // Constants from TheoremStmtGroup.java
    // ----------------------------------------------------------

    /**
     * DEFAULT_DV_SRC_STMT_LIST_SIZE ArrayList initial size = 3.
     */
    public static final int DEFAULT_DV_SRC_STMT_LIST_SIZE = 3;

    /**
     * DEFAULT_LOG_HYP_STMT_LIST_SIZE ArrayList initial size = 3.
     */
    public static final int DEFAULT_LOG_HYP_SRC_STMT_LIST_SIZE = 3;

    // ----------------------------------------------------------
    // Constants from TlPreferences.java
    // ----------------------------------------------------------

    /**
     * THEOREM_LOADER_DJ_VARS_OPTION_DEFAULT = "NoUpdate"
     */
    public static final DjVarsOption THEOREM_LOADER_DJ_VARS_OPTION_DEFAULT = DjVarsOption.NoUpdate;

    public enum DjVarsOption {
        NoUpdate, Merge, Replace
    }

    /**
     * THEOREM_LOADER_AUDIT_MESSAGES_DEFAULT = "Yes"
     */
    public static final boolean THEOREM_LOADER_AUDIT_MESSAGES_DEFAULT = true;

    /**
     * THEOREM_LOADER_STORE_FORMULAS_ASIS_DEFAULT = "yes"
     */
    public static final boolean THEOREM_LOADER_STORE_FORMULAS_ASIS_DEFAULT = true;

    /**
     * THEOREM_LOADER_STORE_MM_INDENT_AMT_DEFAULT = 2
     */
    public static final int THEOREM_LOADER_STORE_MM_INDENT_AMT_DEFAULT = 2;

    /**
     * THEOREM_LOADER_STORE_MM_INDENT_AMT_MIN = 0
     */
    public static final int THEOREM_LOADER_STORE_MM_INDENT_AMT_MIN = 0;

    /**
     * THEOREM_LOADER_STORE_MM_INDENT_AMT_MAX = 9
     */
    public static final int THEOREM_LOADER_STORE_MM_INDENT_AMT_MAX = 9;

    /**
     * THEOREM_LOADER_STORE_MM_RIGHT_COL_DEFAULT = 79
     */
    public static final int THEOREM_LOADER_STORE_MM_RIGHT_COL_DEFAULT = 79;

    /**
     * THEOREM_LOADER_STORE_MM_RIGHT_COL_MIN = 70
     */
    public static final int THEOREM_LOADER_STORE_MM_RIGHT_COL_MIN = 70;

    /**
     * THEOREM_LOADER_STORE_MM_RIGHT_COL_MAX = 9999
     */
    public static final int THEOREM_LOADER_STORE_MM_RIGHT_COL_MAX = 9999;

    // ----------------------------------------------------------
    // Messages from MMTFolder.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_MMT_FOLDER_NAME_BLANK = of(
        "E-TL-0101 .mmt Folder Name is blank or an empty string.");

    public static final ErrorCode ERRMSG_NOT_A_MMT_FOLDER = of("E-TL-0102",
        ".mmt Folder Name %s\n exists but is not a folder/directory (is a file?).");

    public static final ErrorCode ERRMSG_MMT_FOLDER_NOTFND = of("E-TL-0103",
        ".mmt Folder Name %s invalid.\n No such folder (or file) found!");

    public static final ErrorCode ERRMSG_MMT_FOLDER_MISC_ERROR = of("E-TL-0104",
        ".mmt Folder Name %s is supposed to be a folder.\n However, an error was"
            + " encountered while using the specified name."
            + " \n Message = %s");

    public static final ErrorCode ERRMSG_MMT_FOLDER_UNSPECIFIED = of(
        "E-TL-0105 .mmt Folder Name not yet specified."
            + " This can be done using RunParm \n 'TheoremLoaderMMTFolder,'"
            + " and via the Proof Assistant GUI TheoremLoader menu.");

    public static final ErrorCode ERRMSG_MMT_FOLDER_READ_ERROR = of(
        "E-TL-0106 .mmt Folder Name %s\n -- unable to read theorems from .mmt Folder because"
            + " of unknown I/O error or it is (now) not a directory.");

    public static final ErrorCode ERRMSG_MMT_FOLDER_FILE_NULL = of(
        "E-TL-0107 .mmt Folder file parameter null or missing.");

    // ----------------------------------------------------------
    // Messages from MMTTheoremFile.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_MMT_THEOREM_LABEL_BLANK = of(
        "E-TL-0201 .mmt Theorem label is blank or an empty string.");

    public static final ErrorCode ERRMSG_MMT_THEOREM_NOT_A_FILE = of(
        "E-TL-0202 .mmt Theorem File Name %s exists but is not the name of a file"
            + " (is folder/directory?)");

    public static final ErrorCode ERRMSG_MMT_THEOREM_NOTFND = of(
        "E-TL-0203 .mmt Theorem File Name %s invalid. No such file found!");

    public static final ErrorCode ERRMSG_MMT_THEOREM_FILE_MISC_ERROR = of(
        "E-TL-0204 .mmt Theorem File Name %s is supposed to be a file. However, an error was"
            + " encountered while using the specified name."
            + " \n Message = %s");

    public static final ErrorCode ERRMSG_MMT_THEOREM_FILE_NOTFND = of(
        "E-TL-0205 .mmt Theorem File Name %s invalid. No such file found!");

    public static final ErrorCode ERRMSG_MMT_THEOREM_FILE_TYPE_BOGUS = of(
        "E-TL-0207 .mmt Theorem File Name invalid. The file"
            + " type (i.e. suffix) must equal '.mmt'. Input file = %s");

    public static final ErrorCode ERRMSG_MMT_THEOREM_WRITE_IO_ERROR = of(
        "E-TL-0208 .mmt Theorem File Name %s was to be stored in the .mmt Folder. However, an I/O"
            + " error was encountered during the output process."
            + ". Detailed I/O error Message follows: %s");

    public static final ErrorCode ERRMSG_MMT_THEOREM_CLOSE_IO_ERROR = of(
        "E-TL-0209 .mmt Theorem File Name %s was to be stored in the .mmt Folder. However, an I/O"
            + " error was encountered during the close() operation."
            + ". Detailed I/O error Message follows: %s");

    // ----------------------------------------------------------
    // Messages from TheoremStmtGroup.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_MMT_THEOREM_FILE_IO_ERROR = of(
        "E-TL-0301 I/O Exception on read of .mmt Theorem File = %s."
            + " Detailed I/O Exception Message follows: %s");

    public static final ErrorCode ERRMSG_MMT_THEOREM_FILE_BAD_KEYWORD = of(
        "E-TL-0302 "
            + "Invalid Metamath Keyword read in file %s. The Theorem Loader accepts "
            + "only ${, $}, $e, $p and $d Metamath Keywords. The Keyword value read was %s");

    public static final ErrorCode ERRMSG_BEGIN_SCOPE_MUST_BE_FIRST = of(
        "E-TL-0303 A Begin Scope ('${') Metamath statement,"
            + " if present, must be the first Metamath statement"
            + " in a .mmt Theorem File read by the Theorem Loader."
            + " Input file name = %s");

    public static final ErrorCode ERRMSG_END_SCOPE_MUST_BE_LAST = of(
        "E-TL-0304 An End Scope ('$}') Metamath statement,"
            + " if present, must be the last Metamath statement"
            + " in a .mmt Theorem File read by the Theorem Loader."
            + " Input file name = %s");

    public static final ErrorCode ERRMSG_BEGIN_SCOPE_MISSING = of(
        "E-TL-0305 An End Scope ('$}') Metamath statement"
            + " was read which was not preceded by a Begin Scope ('${')"
            + " Metamath statement. Input file name = %s");

    public static final ErrorCode ERRMSG_EXTRA_THEOREM_STMT = of(
        "E-TL-0306 More than one Theorem ('$p') Metamath statement,"
            + " was read. Only one Theorem may be present in a .mmt"
            + " Theorem File read by the Theorem Loader. Input file"
            + " name = %s");

    public static final ErrorCode ERRMSG_THEOREM_LABEL_MISMATCH = of(
        "E-TL-0307 Input Metamath Theorem ('$p') statement label = %s"
            + " in input .mmt Theorem File does not match the"
            + " input file name (e.g. theorem 'syl' must be"
            + " input in a file named 'syl.mmt'). Input file name = %s");

    public static final ErrorCode ERRMSG_THEOREM_LABEL_HYP_DUP = of(
        "E-TL-0308 Input Metamath Theorem ('$p') statement label = %s"
            + " is the same as one of the input Logical Hypothesis ('$e')"
            + " labels! Input file name = %s");

    public static final ErrorCode ERRMSG_THEOREM_FILE_THEOREM_MISSING = of(
        "E-TL-0309 Input .mmt Theorem File read which does not"
            + " contain a Metamath Theorem ('$p') statement."
            + " Input file name = %s");

    public static final ErrorCode ERRMSG_THEOREM_LOG_HYP_SEQ_ERR = of(
        "E-TL-0310 A Logical Hypothesis ('$e') Metamath statement,"
            + " if present, must be input prior to the Theorem ('$p')"
            + " Metamath statement. Input file name = %s");

    public static final ErrorCode ERRMSG_THEOREM_DV_SEQ_ERR = of(
        "E-TL-0311 A Distinct Variables ('$d') Metamath statement,"
            + " if present, must be input prior to the Theorem ('$p')"
            + " Metamath statement. Input file name = %s");

    public static final ErrorCode ERRMSG_LOG_HYP_LABEL_HYP_DUP = of(
        "E-TL-0312 Input Metamath Logical Hypothesis ('$e') "
            + " statement label = %s is the same as another of the input Logical Hypothesis"
            + " statement labels! Input file name = %s");

    public static final ErrorCode ERRMSG_END_SCOPE_MISSING = of(
        "E-TL-0313 An Begin Scope ('${') Metamath statement"
            + " was read which was not followed by an End Scope ('$}')"
            + " Metamath statement. Input file name = %s");

    public static final ErrorCode ERRMSG_BEGIN_END_SCOPE_PAIR_MISSING = of(
        "E-TL-0314 A Begin Scope ('${') and End Scope ('$}') pair"
            + " of Metamath statements is required because the input"
            + " .mmt Theorem File contains one or more Distinct Variable"
            + " ('$d') and/or Logical Hypothesis ('$e') Metamath"
            + " statements. Input file name = %s");

    public static final ErrorCode ERRMSG_MMT_THEOREM_FILE_BOGUS_KEYWORD = of(
        "E-TL-0315 Unrecognized Metamath Keyword read in file %s. The Theorem Loader "
            + "accepts only ${, $}, $e, $p and $d Metamath Keywords. The Keyword value read was %s");

    public static final ErrorCode ERRMSG_SRC_STMT_SYM_NOTFND = of(
        "E-TL-0316 Symbol in Metamath statement not found in the"
            + " Logical System Symbol Table. Symbol = %s. The error is located"
            + " in Metamath statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_LOG_HYP_STMT_MISMATCH = of(
        "E-TL-0317 The label in a Logical Hypothesis ('$e') statement"
            + " in the input .mmt Theorem File matches a statement"
            + " in the Logical System Stmt Table which is not a Logical"
            + " Hypothesis! Input label = %s. The error is located in Metamath"
            + " statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_LOG_HYP_FORMULA_MISMATCH = of(
        "E-TL-0318 Formula in Logical Hypothesis ('$e') statement in"
            + " input .mmt Theorem File does not match the existing"
            + " formula in the Logical System. Label = %s. The error is located"
            + " in Metamath statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_THEOREM_PROOF_COMPRESSED = of(
        "E-TL-0319 Theorem proof ('$=') is in Metamath compressed"
            + " format. Input .mmt Theorem files for the Theorem"
            + " Loader must be in uncompressed format. Input file"
            + " name = %s");

    public static final ErrorCode ERRMSG_THEOREM_STMT_MISMATCH = of(
        "E-TL-0320 The label in a Theorem statement ('$p') in"
            + " the input .mmt Theorem File matches a statement"
            + " in the Logical System Stmt Table which is not a"
            + " Theorem! Input label = %s. The error is located"
            + " in Metamath statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_THEOREM_FORMULA_MISMATCH = of(
        "E-TL-0321 Formula in Theorem statement ('$p') in"
            + " input .mmt Theorem File does not match the existing"
            + " formula in the Logical System. Label = %s. The error is located"
            + " in Metamath statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_PROOF_LABEL_ERR = of(
        "E-TL-0322 Proof step (label) in Theorem statement ('$p') in"
            + " input .mmt Theorem File is invalid. The referenced"
            + " statement is not present in the Logical System"
            + " statement table, or in the list of new Theorems"
            + " being added by the Theorem Loader. Nor does it refer"
            + " to one of the theorem's logical hypotheses. The"
            + " invalid proof step label = %s in proof of theorem ="
            + " %s in input file = %s");

    public static final ErrorCode ERRMSG_LOG_HYPS_DONT_MATCH = of(
        "E-TL-0323 The Logical Hypotheses ('$e') in the"
            + " the input .mmt Theorem File do not match the"
            + " Logical Hypotheses in the Logical System Stmt Table"
            + " for the Theorem ('$p'). Input Theorem label = %s. The error"
            + " (Theorem) is located in Metamath statement number %d in"
            + " input file = %s");

    public static final ErrorCode ERRMSG_USED_THEOREM_SEQ_TOO_HIGH = of(
        "E-TL-0324 Updated proof is invalid because it refers"
            + " to a theorem whose sequence number is greater than"
            + " its own. This may be the result of the referenced"
            + " theorem being appended to the end of the Logical"
            + " System instead of being inserted into a numbering"
            + " gap (either the gap was full, the theorem was"
            + " incomplete, or an earlier theorem was appended which"
            + " caused a chain reaction of appends.) Theorem = %s Seq = %d"
            + " Referenced theorem = %s Seq = %d. Input .mmt Theorem file = %s");

    public static final ErrorCode ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH = of(
        "E-TL-0325 Updated proof is invalid because it refers"
            + " to a label whose sequence number is greater than"
            + " its own. Theorem = %s Seq = %d"
            + " Referenced theorem = %s Seq = %d. Input .mmt Theorem file = %s");

    public static final ErrorCode ERRMSG_MMT_STMT_PARSE_ERR = of(
        "E-TL-0326 Syntax parse error (see previous message)"
            + " in statement label %s. Error in Metamath statement number"
            + " %d in MMT Theorem File %s");

    public static final ErrorCode ERRMSG_NEW_THEOREM_OLD_LOG_HYP = of(
        "E-TL-0327 A new .mmt theorem's logical hypotheses must"
            + " also be new); they cannot be shared."
            + " Theorem label = %s, Metamath statement number %d in MMT Theorem File %s");

    public static final ErrorCode ERRMSG_DJ_VAR_SYM_NOT_A_VAR = of(
        "E-TL-0328 Symbol in Distinct Variable ('$d') Metamath"
            + " statement is not declared as a Variable ('$v') in the"
            + " Logical System Symbol Table. Symbol = %s. The error is located"
            + " in Metamath statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_HYP_ADDED_TWICE_ERR = of(
        "E-TL-0329 A new Theorem's logical hypothesis could"
            + " not be added to the Logical System statement table"
            + " because it was already in the table! This indicates"
            + " that the same Metamath statement label was used"
            + " on logical hypothesis statements ('$e') of two"
            + " different theorems. Theorem label = %s. Logical hypothesis"
            + " label = %s Input .mmt Theorem File = %s");

    public static final ErrorCode ERRMSG_MMT_TYP_CD_NOT_VALID = of(
        "E-TL-0330 The Type Code of the input Metamath statement"
            + " = %s is not equal to the .mm file 'provable assertion' type."
            + " The input statement label = %s. The error is located in"
            + " Metamath statement number %d in input file = %s");

    public static final ErrorCode ERRMSG_AUDIT_MSG_THEOREM_ADD = of(
        "I-TL-0331 %s added. Label = %s MObj.seq = %s (%s). "
            + " maxExistingMObjRef.id/label = %s maxExistingMObjRef.seq = %s");

    public static final ErrorCode ERRMSG_AUDIT_MSG_THEOREM_UPD = of(
        "I-TL-0332 Theorem updated. Label = %s MObj.seq = %s");

    // ----------------------------------------------------------
    // Messages from MMTTheoremSet.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_DUP_MMT_THEOREM = of(
        "E-TL-0401 Duplicate .mmt theorem labels found."
            + " .mmt Theorem File 1 name = %s."
            + " .mmt Theorem File 2 name = %s.");

    public static final ErrorCode ERRMSG_ROLLBACK = of(
        "E-TL-0402 Error found during update of LogicalSystem."
            + " Updates in-progress have been rolled back."
            + " Detailed error message follows: \n%s");

    public static final ErrorCode ERRMSG_CYCLIC_REF_ERROR = of(
        "E-TL-0403 Cyclic or forward proof references are "
            + "indicated - the queue of MMT Theorem files"
            + " waiting for update processing is halted."
            + " Forward references can occur if the gaps between"
            + " statement numbers in the Logical System are"
            + " full and an inserted statement had to be"
            + " appended instead. The theorems in the Wait Queue"
            + " are listed below. Check the contents of the"
            + " MMT Theorem Folder and its files.\n%s");

    public static final ErrorCode ERRMSG_ROLLBACK_FAILED = of(
        "A-TL-0404 theoremLoaderRollback() failed."
            + " This is an unrecoverable error, probably a bug!"
            + " Manual restart of mmj2 required."
            + " Explanation message identifying original error follows: %s"
            + " Explanation message identifying the rollback error"
            + " follows: %s");

    // ----------------------------------------------------------
    // Messages from mmj.util.TheoremLoaderBoss.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_INVALID_DJ_VARS_OPTION = of(
        "E-TL-0501 "
            + "Invalid input for Theorem Loader Audit Messages. Input = %s.\n"
            + " Valid choices are: 'NoUpdate'," + " 'Merge', and"
            + " 'Replace'.");

    public static final ErrorCode ERRMSG_INVALID_BOOLEAN = of(
        "E-TL-0502 " + "Invalid input = %s.\n Valid choices are:"
            + " 'Yes', 'True' or 'On', and 'No', 'False' or 'Off'");

    // ----------------------------------------------------------
    // Messages from mmj.tl.MMTTheoremExportFormatter.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_EXPORT_FORMAT_PROOF_WORKSHEET_ERR = of(
        "E-TL-0601 Theorem Loader cannot convert and store"
            + " a missing (null) or un-unifiable Proof Worksheet"
            + " in the .mmt Folder.");

    public static final ErrorCode ERRMSG_HYP_MISSING_FOR_EXPORTED_PROOF_WORKSHEET = of(
        "A-TL-0602" + " Fatal error (BUG!) The proof worksheet is"
            + " supposedly valid and unified but it"
            + " fails to contain one of the logical"
            + " hypotheses for the (existing) theorem!"
            + " Missing label = %s");

    // ----------------------------------------------------------
    // Messages from mmj.tl.TheoremLoader.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_THEOREM_LOADER_TEXT_UNIFY_ERROR = of(
        "E-TL-0701 Unification errors were reported for unification attempt"
            + " by TheoremLoader. Unification messages are provided in the Messages"
            + " (below). The input filename or data source id was %s.\n%s");

    // ----------------------------------------------------------
    // Messages from mmj.tl.StoreInLogSysAndMMTFolderTLRequest.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_STORE_IN_LOG_SYS_AND_MMT_FOLDER_OK = of(
        "I-TL-0801 Theorem stored in LogSys and MMT Folder");

    // ----------------------------------------------------------
    // Messages from mmj.tl.StoreInMMTFolderTLRequest.java
    // ----------------------------------------------------------

    public static final ErrorCode ERRMSG_STORE_IN_MMT_FOLDER_OK = of(
        "I-TL-0901 Theorem stored in MMT Folder");
}
