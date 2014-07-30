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

import mmj.gmff.GMFFConstants;
import mmj.mmio.MMIOConstants;
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
     * THEOREM_LOADER_DJ_VARS_OPTION_NO_UPDATE = "NoUpdate"
     */
    public static final String THEOREM_LOADER_DJ_VARS_OPTION_NO_UPDATE = "NoUpdate";

    /**
     * THEOREM_LOADER_DJ_VARS_OPTION_MERGE = "Merge"
     */
    public static final String THEOREM_LOADER_DJ_VARS_OPTION_MERGE = "Merge";

    /**
     * THEOREM_LOADER_DJ_VARS_OPTION_MERGE = "Replace"
     */
    public static final String THEOREM_LOADER_DJ_VARS_OPTION_REPLACE = "Replace";

    /**
     * THEOREM_LOADER_DJ_VARS_OPTION_DEFAULT = "NoUpdate"
     */
    public static final String THEOREM_LOADER_DJ_VARS_OPTION_DEFAULT = TlConstants.THEOREM_LOADER_DJ_VARS_OPTION_NO_UPDATE;

    /**
     * THEOREM_LOADER_AUDIT_MESSAGES_DEFAULT = "Yes"
     */
    public static final String THEOREM_LOADER_AUDIT_MESSAGES_DEFAULT = TlConstants.SYNONYM_TRUE_1;

    /**
     * THEOREM_LOADER_STORE_FORMULAS_ASIS_DEFAULT = "yes"
     */
    public static final String THEOREM_LOADER_STORE_FORMULAS_ASIS_DEFAULT = TlConstants.SYNONYM_TRUE_1;

    /**
     * THEOREM_LOADER_STORE_MM_INDENT_AMT_DEFAULT = 2
     */
    public static final String THEOREM_LOADER_STORE_MM_INDENT_AMT_DEFAULT = "2";

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
    public static final String THEOREM_LOADER_STORE_MM_RIGHT_COL_DEFAULT = "79";

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

    public static final String ERRMSG_MMT_FOLDER_NAME_BLANK_1 = "E-TL-0101 .mmt Folder Name is blank or an empty string.";

    public static final String ERRMSG_NOT_A_MMT_FOLDER_1 = "E-TL-0102 .mmt Folder Name ";
    public static final String ERRMSG_NOT_A_MMT_FOLDER_2 = " \n exists but is not a folder/directory (is a file?).";

    public static final String ERRMSG_MMT_FOLDER_NOTFND_1 = "E-TL-0103 .mmt Folder Name ";
    public static final String ERRMSG_MMT_FOLDER_NOTFND_2 = " invalid.\n No such folder (or file) found!";

    public static final String ERRMSG_MMT_FOLDER_MISC_ERROR_1 = "E-TL-0104 .mmt Folder Name ";
    public static final String ERRMSG_MMT_FOLDER_MISC_ERROR_2 = " is supposed to be a folder.\n However, an error was"
        + " encountered while using the specified name." + " \n Message = ";

    public static final String ERRMSG_MMT_FOLDER_UNSPECIFIED_1 = "E-TL-0105 .mmt Folder Name not yet specified. "
        + " This can be done using RunParm \n 'TheoremLoaderMMTFolder,'"
        + " and via the Proof Assistant GUI TheoremLoader menu.";

    public static final String ERRMSG_MMT_FOLDER_READ_ERROR_1 = "E-TL-0106 .mmt Folder Name ";
    public static final String ERRMSG_MMT_FOLDER_READ_ERROR_2 = "\n -- unable to read theorems from .mmt Folder because"
        + " of unknown I/O error or it is (now) not a directory.";

    public static final String ERRMSG_MMT_FOLDER_FILE_NULL_1 = "E-TL-0107 .mmt Folder file parameter null or missing.";

    // ----------------------------------------------------------
    // Messages from MMTTheoremFile.java
    // ----------------------------------------------------------

    public static final String ERRMSG_MMT_THEOREM_LABEL_BLANK_1 = "E-TL-0201 .mmt Theorem label is blank or an empty string.";

    public static final String ERRMSG_MMT_THEOREM_NOT_A_FILE_1 = "E-TL-0202 .mmt Theorem File Name ";
    public static final String ERRMSG_MMT_THEOREM_NOT_A_FILE_2 = " exists but is not the name of a file"
        + " (is folder/directory?)";

    public static final String ERRMSG_MMT_THEOREM_NOTFND_1 = "E-TL-0203 .mmt Theorem File Name ";
    public static final String ERRMSG_MMT_THEOREM_NOTFND_2 = " invalid. No such file found!";

    public static final String ERRMSG_MMT_THEOREM_FILE_MISC_ERROR_1 = "E-TL-0204 .mmt Theorem File Name ";
    public static final String ERRMSG_MMT_THEOREM_FILE_MISC_ERROR_2 = " is supposed to be a file. However, an error was"
        + " encountered while using the specified name." + " \n Message = ";

    public static final String ERRMSG_MMT_THEOREM_FILE_NOTFND_1 = "E-TL-0205 .mmt Theorem File Name ";
    public static final String ERRMSG_MMT_THEOREM_FILE_NOTFND_2 = " invalid. No such file found!";

    public static final String ERRMSG_MMT_THEOREM_FILE_IO_ERROR_1ST_READ_1 = "E-TL-0206 I/O Exception on 1st read of .mmt Theorem File = ";
    public static final String ERRMSG_MMT_THEOREM_FILE_IO_ERROR_1ST_READ_2 = ". Detailed I/O Exception Message follows: ";

    public static final String ERRMSG_MMT_THEOREM_FILE_TYPE_BOGUS_1 = "E-TL-0207 .mmt Theorem File Name invalid. The file"
        + " type (i.e. suffix) must equal '.mmt'. Input file = ";

    public static final String ERRMSG_MMT_THEOREM_WRITE_IO_ERROR_1 = "E-TL-0208 .mmt Theorem File Name ";
    public static final String ERRMSG_MMT_THEOREM_WRITE_IO_ERROR_2 = " was to be stored in the .mmt Folder. However, an I/O"
        + " error was encountered during the output process."
        + ". Detailed I/O error Message follows: ";

    public static final String ERRMSG_MMT_THEOREM_CLOSE_IO_ERROR_1 = "E-TL-0209 .mmt Theorem File Name ";
    public static final String ERRMSG_MMT_THEOREM_CLOSE_IO_ERROR_2 = " was to be stored in the .mmt Folder. However, an I/O"
        + " error was encountered during the close() operation."
        + ". Detailed I/O error Message follows: ";

    // ----------------------------------------------------------
    // Messages from TheoremStmtGroup.java
    // ----------------------------------------------------------

    public static final String ERRMSG_MMT_THEOREM_FILE_IO_ERROR_1 = "E-TL-0301 I/O Exception on read of .mmt Theorem File = ";
    public static final String ERRMSG_MMT_THEOREM_FILE_IO_ERROR_2 = ". Detailed I/O Exception Message follows: ";

    public static final String ERRMSG_MMT_THEOREM_FILE_BAD_KEYWORD_1 = "E-TL-0302 Invalid Metamath Keyword read in file = ";
    public static final String ERRMSG_MMT_THEOREM_FILE_BAD_KEYWORD_2 = ". The Theorem Loader accepts only ${, $}, $e, $p and $d"
        + " Metamath Keywords. The Keyword value read was $";

    public static final String ERRMSG_BEGIN_SCOPE_MUST_BE_FIRST_1 = "E-TL-0303 A Begin Scope ('${') Metamath statement,"
        + " if present, must be the first Metamath statement"
        + " in a .mmt Theorem File read by the Theorem Loader."
        + " Input file name = ";

    public static final String ERRMSG_END_SCOPE_MUST_BE_LAST_1 = "E-TL-0304 An End Scope ('$}') Metamath statement,"
        + " if present, must be the last Metamath statement"
        + " in a .mmt Theorem File read by the Theorem Loader."
        + " Input file name = ";

    public static final String ERRMSG_BEGIN_SCOPE_MISSING_1_1 = "E-TL-0305 An End Scope ('$}') Metamath statement"
        + " was read which was not preceded by a Begin Scope ('${')"
        + " Metamath statement. Input file name = ";

    public static final String ERRMSG_EXTRA_THEOREM_STMT_1 = "E-TL-0306 More than one Theorem ('$p') Metamath statement,"
        + " was read. Only one Theorem may be present in a .mmt"
        + " Theorem File read by the Theorem Loader. Input file" + " name = ";

    public static final String ERRMSG_THEOREM_LABEL_MISMATCH_1 = "E-TL-0307 Input Metamath Theorem ('$p') statement label = ";
    public static final String ERRMSG_THEOREM_LABEL_MISMATCH_2 = " in input .mmt Theorem File does not match the"
        + " input file name (e.g. theorem 'syl' must be"
        + " input in a file named 'syl.mmt'). Input file name = ";

    public static final String ERRMSG_THEOREM_LABEL_HYP_DUP_1 = "E-TL-0308 Input Metamath Theorem ('$p') statement label = ";
    public static final String ERRMSG_THEOREM_LABEL_HYP_DUP_2 = " is the same as one of the input Logical Hypothesis ('$e')"
        + " labels! Input file name = ";

    public static final String ERRMSG_THEOREM_FILE_THEOREM_MISSING_1 = "E-TL-0309 Input .mmt Theorem File read which does not"
        + " contain a Metamath Theorem ('$p') statement."
        + " Input file name = ";

    public static final String ERRMSG_THEOREM_LOG_HYP_SEQ_ERR_1 = "E-TL-0310 A Logical Hypothesis ('$e') Metamath statement,"
        + " if present, must be input prior to the Theorem ('$p')"
        + " Metamath statement. Input file name = ";

    public static final String ERRMSG_THEOREM_DV_SEQ_ERR_1 = "E-TL-0311 A Distinct Variables ('$d') Metamath statement,"
        + " if present, must be input prior to the Theorem ('$p')"
        + " Metamath statement. Input file name = ";

    public static final String ERRMSG_LOG_HYP_LABEL_HYP_DUP_1 = "E-TL-0312 Input Metamath Logical Hypothesis ('$e') "
        + " statement label = ";
    public static final String ERRMSG_LOG_HYP_LABEL_HYP_DUP_2 = " is the same as another of the input Logical Hypothesis"
        + " statement labels! Input file name = ";

    public static final String ERRMSG_END_SCOPE_MISSING_2_1 = "E-TL-0313 An Begin Scope ('${') Metamath statement"
        + " was read which was not followed by an End Scope ('$}')"
        + " Metamath statement. Input file name = ";

    public static final String ERRMSG_BEGIN_END_SCOPE_PAIR_MISSING_3_1 = "E-TL-0314 A Begin Scope ('${') and End Scope ('$}') pair"
        + " of Metamath statements is required because the input"
        + " .mmt Theorem File contains one or more Distinct Variable"
        + " ('$d') and/or Logical Hypothesis ('$e') Metamath"
        + " statements. Input file name = ";

    public static final String ERRMSG_MMT_THEOREM_FILE_BOGUS_KEYWORD_1 = "E-TL-0315 Unrecognized Metamath Keyword read in file = ";
    public static final String ERRMSG_MMT_THEOREM_FILE_BOGUS_KEYWORD_2 = ". The Theorem Loader accepts only ${, $}, $e, $p and $d"
        + " Metamath Keywords. The Keyword value read was $";

    public static final String ERRMSG_SRC_STMT_SYM_NOTFND_1 = "E-TL-0316 Symbol in Metamath statement not found in the"
        + " Logical System Symbol Table. Symbol = ";
    public static final String ERRMSG_SRC_STMT_SYM_NOTFND_2 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_SRC_STMT_SYM_NOTFND_3 = " in input file = ";

    public static final String ERRMSG_LOG_HYP_STMT_MISMATCH_1 = "E-TL-0317 The label in a Logical Hypothesis ('$e') statement"
        + " in the input .mmt Theorem File matches a statement"
        + " in the Logical System Stmt Table which is not a Logical"
        + " Hypothesis! Input label = ";
    public static final String ERRMSG_LOG_HYP_STMT_MISMATCH_2 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_LOG_HYP_STMT_MISMATCH_3 = " in input file = ";

    public static final String ERRMSG_LOG_HYP_FORMULA_MISMATCH_1 = "E-TL-0318 Formula in Logical Hypothesis ('$e') statement in"
        + " input .mmt Theorem File does not match the existing"
        + " formula in the Logical System. Label = ";
    public static final String ERRMSG_LOG_HYP_FORMULA_MISMATCH_2 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_LOG_HYP_FORMULA_MISMATCH_3 = " in input file = ";

    public static final String ERRMSG_THEOREM_PROOF_COMPRESSED_1 = "E-TL-0319 Theorem proof ('$=') is in Metamath compressed"
        + " format. Input .mmt Theorem files for the Theorem"
        + " Loader must be in uncompressed format. Input file" + " name = ";

    public static final String ERRMSG_THEOREM_STMT_MISMATCH_1 = "E-TL-0320 The label in a Theorem statement ('$p') in"
        + " the input .mmt Theorem File matches a statement"
        + " in the Logical System Stmt Table which is not a"
        + " Theorem! Input label = ";
    public static final String ERRMSG_THEOREM_STMT_MISMATCH_2 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_THEOREM_STMT_MISMATCH_3 = " in input file = ";

    public static final String ERRMSG_THEOREM_FORMULA_MISMATCH_1 = "E-TL-0321 Formula in Theorem statement ('$p') in"
        + " input .mmt Theorem File does not match the existing"
        + " formula in the Logical System. Label = ";
    public static final String ERRMSG_THEOREM_FORMULA_MISMATCH_2 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_THEOREM_FORMULA_MISMATCH_3 = " in input file = ";

    public static final String ERRMSG_PROOF_LABEL_ERR_1 = "E-TL-0322 Proof step (label) in Theorem statement ('$p') in"
        + " input .mmt Theorem File is invalid. The referenced"
        + " statement is not present in the Logical System"
        + " statement table, or in the list of new Theorems"
        + " being added by the Theorem Loader. Nor does it refer"
        + " to one of the theorem's logical hypotheses. The"
        + " invalid proof step label = ";
    public static final String ERRMSG_PROOF_LABEL_ERR_2 = " in proof of theorem = ";
    public static final String ERRMSG_PROOF_LABEL_ERR_3 = " in input file = ";

    public static final String ERRMSG_LOG_HYPS_DONT_MATCH_1 = "E-TL-0323 The Logical Hypotheses ('$e') in the"
        + " the input .mmt Theorem File do not match the"
        + " Logical Hypotheses in the Logical System Stmt Table"
        + " for the Theorem ('$p'). Input Theorem label = ";;
    public static final String ERRMSG_LOG_HYPS_DONT_MATCH_2 = ". The error (Theorem) is located in Metamath statement"
        + " number";
    public static final String ERRMSG_LOG_HYPS_DONT_MATCH_3 = " in input file = ";

    public static final String ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_1 = "E-TL-0324 Updated proof is invalid because it refers"
        + " to a theorem whose sequence number is greater than"
        + " its own. This may be the result of the referenced"
        + " theorem being appended to the end of the Logical"
        + " System instead of being inserted into a numbering"
        + " gap (either the gap was full, the theorem was"
        + " incomplete, or an earlier theorem was appended which"
        + " caused a chain reaction of appends.) Theorem = ";
    public static final String ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_2 = " Seq = ";
    public static final String ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_3 = " Referenced theorem = ";
    public static final String ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_4 = " Seq = ";
    public static final String ERRMSG_USED_THEOREM_SEQ_TOO_HIGH_5 = ". Input .mmt Theorem file = ";

    public static final String ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_1 = "E-TL-0325 Updated proof is invalid because it refers"
        + " to a label whose sequence number is greater than"
        + " its own. Theorem = ";
    public static final String ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_2 = " Seq = ";
    public static final String ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_3 = " Referenced label = ";
    public static final String ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_4 = " Seq = ";
    public static final String ERRMSG_PROOF_LABEL_SEQ_TOO_HIGH_5 = ". Input .mmt Theorem file = ";

    public static final String ERRMSG_MMT_STMT_PARSE_ERR_1 = "E-TL-0326 Syntax parse error (see previous message)"
        + " in statement label ";
    public static final String ERRMSG_MMT_STMT_PARSE_ERR_2 = ". Error in Metamath statement number ";
    public static final String ERRMSG_MMT_STMT_PARSE_ERR_3 = " in MMT Theorem File ";

    public static final String ERRMSG_NEW_THEOREM_OLD_LOG_HYP_1 = "E-TL-0327 A new .mmt theorem's logical hypotheses must"
        + " also be new; they cannot be shared." + " Theorem label = ";
    public static final String ERRMSG_NEW_THEOREM_OLD_LOG_HYP_2 = ", Metamath statement number ";
    public static final String ERRMSG_NEW_THEOREM_OLD_LOG_HYP_3 = " in MMT Theorem File ";

    public static final String ERRMSG_DJ_VAR_SYM_NOT_A_VAR_1 = "E-TL-0328 Symbol in Distinct Variable ('$d') Metamath"
        + " statement is not declared as a Variable ('$v') in the"
        + " Logical System Symbol Table. Symbol = ";
    public static final String ERRMSG_DJ_VAR_SYM_NOT_A_VAR_2 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_DJ_VAR_SYM_NOT_A_VAR_3 = " in input file = ";

    public static final String ERRMSG_HYP_ADDED_TWICE_ERR_1 = "E-TL-0329 A new Theorem's logical hypothesis could"
        + " not be added to the Logical System statement table"
        + " because it was already in the table! This indicates"
        + " that the same Metamath statement label was used"
        + " on logical hypothesis statements ('$e') of two"
        + " different theorems. Theorem label = ";
    public static final String ERRMSG_HYP_ADDED_TWICE_ERR_2 = ". Logical hypothesis label = ";
    public static final String ERRMSG_HYP_ADDED_TWICE_ERR_3 = " Input .mmt Theorem File = ";

    public static final String ERRMSG_MMT_TYP_CD_NOT_VALID_1 = "E-TL-0330 The Type Code of the input Metamath statement"
        + " = ";
    public static final String ERRMSG_MMT_TYP_CD_NOT_VALID_2 = " is not equal to the .mm file 'provable assertion' type."
        + " The input statement label = ";
    public static final String ERRMSG_MMT_TYP_CD_NOT_VALID_3 = ". The error is located in Metamath statement number ";
    public static final String ERRMSG_MMT_TYP_CD_NOT_VALID_4 = " in input file = ";

    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_1 = "I-TL-0331";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_2a = " Theorem";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_2b = " LogHyp";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_3 = " added. Label = ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_4 = " MObj.seq = ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_5a = " (appended). ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_5b = " (inserted). ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_6 = " maxExistingMObjRef.id/label = ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_7 = " maxExistingMObjRef.seq = ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_ADD_8 = " BookManager Ch.Sect.MObjNbr = ";

    public static final String ERRMSG_AUDIT_MSG_THEOREM_UPD_1 = "I-TL-0332 Theorem updated. Label = ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_UPD_2 = " MObj.seq = ";
    public static final String ERRMSG_AUDIT_MSG_THEOREM_UPD_3 = " BookManager Ch.Sect.MObjNbr = ";

    // ----------------------------------------------------------
    // Messages from MMTTheoremSet.java
    // ----------------------------------------------------------

    public static final String ERRMSG_DUP_MMT_THEOREM_1 = "E-TL-0401 Duplicate .mmt theorem labels found."
        + ".mmt Theorem File 1 name = ";
    public static final String ERRMSG_DUP_MMT_THEOREM_2 = ". .mmt Theorem File 2 name = ";

    public static final String ERRMSG_UPDATE_FAILURE_1 = "E-TL-0402 Error found during update of LogicalSystem."
        + " Updates in-progress have been rolled back."
        + " Detailed error message follows: \n";

    public static final String ERRMSG_CYCLIC_REF_ERROR_1 = "E-TL-0403 Cyclic or forward proof references are "
        + "indicated - the queue of MMT Theorem files"
        + " waiting for update processing is halted."
        + " Forward references can occur if the gaps between"
        + " statement numbers in the Logical System are"
        + " full and an inserted statement had to be"
        + " appended instead. The theorems in the Wait Queue"
        + " are listed below. Check the contents of the"
        + " MMT Theorem Folder and its files.";

    // ----------------------------------------------------------
    // Messages from mmj.util.TheoremLoaderBoss.java
    // ----------------------------------------------------------

    public static final String ERRMSG_INVALID_DJ_VARS_OPTION_1 = "E-TL-0501 Invalid input for Theorem Loader Audit"
        + " Messages. Input = ";
    public static final String ERRMSG_INVALID_DJ_VARS_OPTION_2 = ".\n Valid choices are:"
        + " 'NoUpdate'," + " 'Merge', and" + " 'Replace'.";

    public static final String ERRMSG_INVALID_AUDIT_MESSAGES_1 = "E-TL-0502 Invalid input for Theorem Loader Dj Vars"
        + " Option. Input = ";
    public static final String ERRMSG_INVALID_AUDIT_MESSAGES_2 = ".\n Valid choices are:"
        + " 'Yes', 'True' or 'On', and" + " 'No', 'False' or 'Off'";

    public static final String ERRMSG_INVALID_STORE_FORMULAS_ASIS_1 = "E-TL-0503 Invalid input for Theorem Loader Store"
        + " Formulas AsIs Option. Input = ";
    public static final String ERRMSG_INVALID_STORE_FORMULAS_ASIS_2 = ".\n Valid choices are:"
        + " 'Yes', 'True' or 'On', and" + " 'No', 'False' or 'Off'";

    public static final String ERRMSG_INVALID_STORE_MM_INDENT_AMT_1 = "E-TL-0504 Invalid input for Theorem Loader Store MM"
        + " Indent Amt Option. Input = ";
    public static final String ERRMSG_INVALID_STORE_MM_INDENT_AMT_2 = ".\n Valid choices are:"
        + " 0 through 9.";

    public static final String ERRMSG_INVALID_STORE_MM_RIGHT_COL_1 = "E-TL-0505 Invalid input for Theorem Loader Store MM"
        + " Right Col Option. Input = ";
    public static final String ERRMSG_INVALID_STORE_MM_RIGHT_COL_2 = ".\n Valid choices are:"
        + " 70 through 9999.";

    // ----------------------------------------------------------
    // Messages from mmj.tl.MMTTheoremExportFormatter.java
    // ----------------------------------------------------------

    public static final String ERRMSG_EXPORT_FORMAT_PROOF_WORKSHEET_ERR_1 = "E-TL-0601 Theorem Loader cannot convert and store"
        + " a missing (null) or un-unifiable Proof Worksheet."
        + " in the .mmt Folder";

    public static final String ERRMSG_HYP_MISSING_FOR_EXPORTED_PROOF_WORKSHEET_1 = "A-TL-0602"
        + " Fatal error (BUG!) The proof worksheet is"
        + " supposedly valid and unified but it"
        + " fails to contain one of the logical"
        + " hypotheses for the (existing) theorem!" + " Missing label = ";

    public static final String ERRMSG_EXPORT_FORMAT_PROOF_WORKSHEET_ERR_2_1 = "E-TL-0603 Theorem Loader cannot convert and store"
        + " a missing (null) or un-unifiable Proof Worksheet."
        + " in the .mmt Folder. Theorem label = ";

    // ----------------------------------------------------------
    // Messages from mmj.tl.TheoremLoader.java
    // ----------------------------------------------------------

    public static final String ERRMSG_THEOREM_LOADER_TEXT_UNIFY_ERROR_1 = "E-TL-0701 ProofWorksheet unification was attempted"
        + " by TheoremLoader. Unification errors were reported" + " for ";
    public static final String ERRMSG_THEOREM_LOADER_TEXT_UNIFY_ERROR_2 = ". Unification messages are provided in the Messages"
        + " (below). The input filename or data source id was ";

    // ----------------------------------------------------------
    // Messages from mmj.tl.StoreInLogSysAndMMTFolderTLRequest.java
    // ----------------------------------------------------------

    public static final String ERRMSG_STORE_IN_LOG_SYS_AND_MMT_FOLDER_NO_MSGS = "I-TL-0801 Theorem stored in LogSys and MMT Folder";

    // ----------------------------------------------------------
    // Messages from mmj.tl.StoreInMMTFolderTLRequest.java
    // ----------------------------------------------------------

    public static final String ERRMSG_STORE_IN_MMT_FOLDER_NO_MSGS = "I-TL-0901 Theorem stored in MMT Folder";
}
