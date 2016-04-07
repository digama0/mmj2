//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFManager.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.io.File;
import java.util.*;

import mmj.lang.*;
import mmj.mmio.MMIOConstants;
import mmj.pa.ProofAsst;
import mmj.pa.ProofAsstException;

/**
 * Serves as a central data store for GMFF work in progress and as the primary
 * interface for access to GMFF services.
 * <p>
 * One thing that is different about {@code GMFFManager} than other "manager"
 * type classes in mmj2 is that the {@code GMFFManager} object is instantiated
 * by the {@code LogicalSystemBoss} in the {@code util} package rather than the
 * {@code GMFFBoss}. This is because Metamath $t Comment statement(s) are
 * accumulated during the {@code LoadFile} process -- though not parsed at that
 * time. Hence, a reference to the {@code GMFFManager} instance is stored in
 * {@code LogicalSystem}.
 * <p>
 * Another thing that is different than typical mmj2 processing is that the GMFF
 * RunParms which establish settings for parameters are not validated when the
 * RunParms are initially read. Nothing in GMFF happens until the first time the
 * user -- or a command-style GMFF RunParm -- requests that GMFF typeset
 * something. This functionality matches the way Metamath works and also saves
 * users who have no interest in using GMFF from aggravation if there are errors
 * in the GMFF-related inputs. A side-effect of delayed validation of GMFF
 * RunParms is additional complexity. {@code GMFFManager} deals with that
 * complexity in {@code initialization()} where all of the cached RunParms are
 * validated and merged with default settings for parameters.
 */
public class GMFFManager {

    private boolean gmffInitialized = false;

    private final File filePath;

    private final Messages messages;

    private Map<String, Sym> symTbl = null;

    private final List<String> typesetDefinitionsCache;

    private int nbrTypesetDefinitionsProcessedSoFar = 0;

    // via RunParmGMFFExportParms or wherever
    private final List<GMFFExportParms> inputGMFFExportParmsList;

    private final List<GMFFUserTextEscapes> inputGMFFUserTextEscapesList;

    private GMFFUserExportChoice inputGMFFUserExportChoice;

    // contains defaults merged with inputGMFFExportParmsList
    // contains at most one entry for each exportType!
    private List<GMFFExportParms> exportParmsList;

    // list of exporters constructed from final contents of
    // exportParmsList and userTextEscapesList
    private List<GMFFExporter> gmffExporterList;

    // - contains defaults merged with inputGMFFUserTextEscapesList
    // - contains at most one entry for each exportType!
    // - list built using gmffExporterListas a key -- invalid
    // if user text escapes list's export type not present in
    // exportParmsList.
    private List<GMFFUserTextEscapes> userTextEscapesList;

    // loaded using cached Metamath typesetting defintion
    // comment records.
    private final List<GMFFExporterTypesetDefs> exporterTypesetDefsList;

    // loaded with default merged with inputGMFFUserExportChoice
    private GMFFUserExportChoice gmffUserExportChoice;

    // loaded with Exporters from gmffExporterList according
    // to inputGMFFUserExportChoice
    private GMFFExporter[] selectedExporters;

    /**
     * Standard constructor.
     * <p>
     * Called by {@code LogicalSystemBoss} when the first {@code LoadFile}
     * RunParm is executed.
     * <p>
     * Sets up GMFF data structures but does not load them. Sets
     * {@code gmffInitialized} to {@code  false} to trigger initialization when
     * the first GMFF service request is received.
     *
     * @param filePath path for building directories.
     * @param messages The Messages object.
     */
    public GMFFManager(final File filePath, final Messages messages) {

        gmffInitialized = false;

        this.filePath = filePath;
        this.messages = messages;

        typesetDefinitionsCache = new ArrayList<>(1);

        nbrTypesetDefinitionsProcessedSoFar = 0;

        inputGMFFExportParmsList = new ArrayList<>(
            GMFFConstants.DEFAULT_EXPORT_PARMS.length);

        inputGMFFUserTextEscapesList = new ArrayList<>(
            GMFFConstants.DEFAULT_EXPORT_PARMS.length);
        inputGMFFUserExportChoice = null;

        exporterTypesetDefsList = new ArrayList<>(
            GMFFConstants.DEFAULT_EXPORT_PARMS.length);
    }

    /**
     * Calls {@code initialize()}, generates an audit report of GMFF RunParms
     * and default settings,mand if requested, prints the typesetting
     * definitions obtained from the input Metamath file(s) $t Comment
     * statements.
     * <p>
     * This function is called by GMFFBoss in response to a
     * {@code GMFFInitialize} RunParm.
     *
     * @param printTypesettingDefinitions prints data from Metamath $t Comments
     *            for which there are {@code GMFFExportParms} with matching
     *            {@code typesetDefs} (we don't load data from the $t's unless
     *            it is needed.)
     * @throws GMFFException if an error occurred
     */
    public void gmffInitialize(final boolean printTypesettingDefinitions)
        throws GMFFException
    {

        initialization();

        generateInitializationAuditReport();

        if (printTypesettingDefinitions)
            generateTypesettingDefinitionsReport();
    }

    /**
     * Caches parameters from one RunParm for later validation and use.
     * <p>
     * Also invokes {@code forceReinitialization()} which sets
     * {@code gmffInitialized = false} to force re-initialization of GMFF the
     * next time a service request is made.
     * <p>
     * This function is called by {@code GMFFBoss} in response to a
     * {@code GMFFExportParms} RunParm.
     *
     * @param inputGMFFExportParms data from GMFFExportParms RunParm.
     */
    public void accumInputGMFFExportParms(
        final GMFFExportParms inputGMFFExportParms)
    {

        inputGMFFExportParmsList.add(inputGMFFExportParms);
        forceReinitialization();
    }

    /**
     * Caches parameters from one RunParm for later validation and use.
     * <p>
     * Also invokes {@code forceReinitialization()} which sets
     * {@code gmffInitialized = false} to force re-initialization of GMFF the
     * next time a service request is made.
     * <p>
     * This function is called by {@code GMFFBoss} in response to a
     * {@code GMFFUserTextEscapes} RunParm.
     *
     * @param inputGMFFUserTextEscapes data from GMFFUserTextEscapes RunParm.
     */
    public void accumInputGMFFUserTextEscapesList(
        final GMFFUserTextEscapes inputGMFFUserTextEscapes)
    {

        inputGMFFUserTextEscapesList.add(inputGMFFUserTextEscapes);
        forceReinitialization();
    }

    /**
     * Caches one Metamath $t Comment statement for later validation and use.
     * <p>
     * Also invokes {@code forceReinitialization()} which sets
     * {@code gmffInitialized = false} to force re-initialization of GMFF the
     * next time a service request is made.
     * <p>
     * This function is called by {@code LogicalSystem} during processing of a
     * {@code LoadFile} RunParm.
     *
     * @param comment String Metamath $t Comment statement as stored in
     *            {@code SrcStmt} (the "$(" and "$)" delimiters are removed at
     *            this pointand the first token is "$t").
     */
    public void cacheTypesettingCommentForGMFF(final String comment) {

        typesetDefinitionsCache.add(comment);
        forceReinitialization();
    }

    /**
     * Stores the contents of the {@code GMFFUserExportChoice} from one RunParm
     * for later validation and use.
     * <p>
     * Also invokes {@code forceReinitialization()} which sets
     * {@code gmffInitialized = false} to force re-initialization of GMFF the
     * next time a service request is made.
     * <p>
     * This function is called by {@code GMFFBoss} in response to a
     * {@code GMFFUserExportChoice} RunParm.
     *
     * @param choice from GMFFUserExportChoice RunParm.
     */
    public void setInputGMFFUserExportChoice(
        final GMFFUserExportChoice choice)
    {
        inputGMFFUserExportChoice = choice;
        forceReinitialization();
    }

    /**
     * Sets the {@code symTbl} for use in generating GMFF exports.
     * <p>
     * This function is called by the {@code LogicalSystem} constructor -- which
     * itself is excuted during processing of the first {@code LoadFile}
     * RunParm. {@code symTbl} is itself constructed during construction of
     * {@code LogicalSystem} so this function is necessary even though
     * {@code GMFFManager} is passed as an argument to the {@code LogicalSystem}
     * constructor (a somewhat circular arrangement.)
     * <p>
     * {@code symTbl} is needed because an error message is generated when a
     * symbol to be typeset is not found in the Metamath $t definitions, but
     * only if the symbol string is really a valid symbol (and is not a
     * {@code WorkVar}.) (GMFF not not require that Proof Worksheets be valid,
     * just that the Proof Worksheet format is loosely followed.)
     *
     * @param symTbl The Symbol Table Map from {@code LogicalSystem}
     */
    public void setSymTbl(final Map<String, Sym> symTbl) {
        this.symTbl = symTbl;
    }

    /**
     * Gets the {@code symTbl} for use in generating GMFF exports.
     *
     * @return The Symbol Table Map, {@code symTbl} from {@code LogicalSystem}
     */
    public Map<String, Sym> getSymTbl() {
        return symTbl;
    }

    /**
     * Gets the {@code messages} object.
     *
     * @return The Messages object used to store error and informational
     *         messages during mmj2 processing.
     */
    public Messages getMessages() {
        return messages;
    }

    /**
     * Returns the {@code gmffInitialized} boolean variable.
     *
     * @return true if GMFF already initialized, otherwise false.
     */
    public boolean isGMFFInitialized() {
        return gmffInitialized;
    }

    /**
     * Forces GMFF to re-initialize itself the next time a service request is
     * received by settting {@code gmffInitialized} to {@code false}.
     */
    public void forceReinitialization() {
        gmffInitialized = false;
    }

    /**
     * Exports one or a range of Proof Worksheets of a given file type from a
     * designated directory.
     * <p>
     * This function implements the {@code GMFFExportFromFolder} RunParm
     * command.
     * <p>
     * The sort sequence used to select and output Proof Worksheets is File Name
     * minus File Type.
     * <p>
     * Refer to mmj2\doc\gmffdoc\C:\mmj2jar\GMFFDoc\GMFFRunParms.txt for more
     * info about the parameters on the {@code GMFFExportFromFolder} RunParm.
     *
     * @param inputDirectory The Directory to export Proof Worksheets from
     * @param theoremLabelOrAsterisk Either a theorem label or "*". If theorem
     *            label input then that is used as the starting point, otherwise
     *            processing begins at the first file in the directory.
     * @param inputFileType File Type to select, including the "." (e.g. ".mmp")
     * @param maxNumberToExport Limits the number of exports processed. Must be
     *            greater than zero and less then 2 billionish...
     * @param appendFileNameIn Specifies an append-mode file name to which all
     *            of the exported proofs will be written -- within the folder
     *            specified for each Export Type on the GMFFExportParms RunParm;
     *            overrides the normal name assigned to an export file.
     * @throws GMFFException if errors encountered.
     */
    public void exportFromFolder(final String inputDirectory,
        final String theoremLabelOrAsterisk, final String inputFileType,
        final String maxNumberToExport, final String appendFileNameIn)
            throws GMFFException
    {

        if (!gmffInitialized)
            initialization();

        final String fileType = validateFileType(inputFileType);

        final String labelOrAsterisk = validateTheoremLabelOrAsterisk(
            theoremLabelOrAsterisk);

        final String appendFileName = validateAppendFileName(appendFileNameIn);

        final int max = validateMaxNumberToExport(maxNumberToExport);

        final GMFFFolder inputFolder = new GMFFFolder(filePath, inputDirectory,
            " ");

        if (labelOrAsterisk.equals(GMFFConstants.OPTION_VALUE_ALL) || max > 1) {

            String lowestNamePrefix;
            if (labelOrAsterisk.equals(GMFFConstants.OPTION_VALUE_ALL))
                lowestNamePrefix = "";
            else
                lowestNamePrefix = labelOrAsterisk;

            final File[] fileArray = inputFolder.listFiles(fileType,
                lowestNamePrefix);

            if (fileArray.length == 0) {
                messages.accumMessage(
                    GMFFConstants.ERRMSG_NO_PROOF_FILES_SELECTED_ERROR,
                    inputDirectory, fileType);
                return;
            }

            for (int i = 0; i < fileArray.length && i < max; i++) {

                final String proofWorksheetText = GMFFInputFile.getFileContents(
                    fileArray[i], " ",
                    GMFFConstants.PROOF_WORKSHEET_MESSAGE_DESCRIPTOR,
                    GMFFConstants.PROOF_WORKSHEET_BUFFER_SIZE);

                for (final GMFFException confirm : exportProofWorksheet(
                    proofWorksheetText, appendFileName))
                    messages.accumException(confirm);
            }
            return;
        }

        final String proofWorksheetText = GMFFInputFile.getFileContents(
            inputFolder, labelOrAsterisk + fileType, " ",
            GMFFConstants.PROOF_WORKSHEET_MESSAGE_DESCRIPTOR,
            GMFFConstants.PROOF_WORKSHEET_BUFFER_SIZE);

        for (final GMFFException confirm : exportProofWorksheet(
            proofWorksheetText, appendFileName))
            messages.accumException(confirm);

    }

    /**
     * Exports one theorem or a range of theorems from {@code LogicalSystem}.
     * <p>
     * This function implements the {@code GMFFExportTheorem} RunParm command.
     * <p>
     * The sort sequence used to select and output thereoms is {@code MObj.seq}
     * -- that is, order of appearance in the {@code LogicalSystem}.
     * <p>
     * Refer to mmj2\doc\gmffdoc\C:\mmj2jar\GMFFDoc\GMFFRunParms.txt for more
     * info about the parameters on the {@code GMFFExportThereom} RunParm.
     *
     * @param theoremLabelOrAsterisk Either a theorem label or "*". If theorem
     *            label input then that is used as the starting point, otherwise
     *            processing begins at the first file in the directory.
     * @param maxNumberToExport Limits the number of exports processed. Must be
     *            greater than zero and less then 2 billionish...
     * @param appendFileNameIn Specifies an append-mode file name to which all
     *            of the exported proofs will be written -- within the folder
     *            specified for each Export Type on the GMFFExportParms RunParm;
     *            overrides the normal name assigned to an export file.
     * @param proofAsst The {@code ProofAsst} object, used to format Proof
     *            Worksheets from Metamath (RPN) proofs.
     * @throws GMFFException is errors encountered.
     */
    public void exportTheorem(final String theoremLabelOrAsterisk,
        final String maxNumberToExport, final String appendFileNameIn,
        final ProofAsst proofAsst) throws GMFFException
    {

        if (!gmffInitialized)
            initialization();

        final String labelOrAsterisk = validateTheoremLabelOrAsterisk(
            theoremLabelOrAsterisk);

        final String appendFileName = validateAppendFileName(appendFileNameIn);

        final int max = validateMaxNumberToExport(maxNumberToExport);

        if (labelOrAsterisk.equals(GMFFConstants.OPTION_VALUE_ALL) || max > 1) {

            String startTheorem;
            if (labelOrAsterisk.equals(GMFFConstants.OPTION_VALUE_ALL))
                startTheorem = null;
            else
                startTheorem = labelOrAsterisk;

            Iterable<Theorem> iterable;
            try {
                iterable = proofAsst
                    .getSortedSkipSeqTheoremIterable(startTheorem);
            } catch (final ProofAsstException e) {
                messages.accumException(e);
                return;
            }

            int i = 0;
            for (final Theorem theorem : iterable) {
                if (i++ >= max)
                    break;
                gmffExportOneTheorem(theorem, appendFileName, proofAsst);
            }
            if (i == 0) {
                messages.accumMessage(
                    GMFFConstants.ERRMSG_NO_THEOREMS_SELECTED_ERROR,
                    labelOrAsterisk);
                return;
            }
        }
        else
            gmffExportOneTheorem(labelOrAsterisk, appendFileName, proofAsst);

    }
    /**
     * Exports one {@code Theorem} from the {@code LogicalSystem}. loaded
     * <p>
     * This function is called by other functions in {@code GMFFManager} but it
     * would be perfectly valid to call it externally.
     * <p>
     * This function calls {@code ProofAsst.exportOneTheorem} which creates a
     * Proof Worksheet from a Metamath (RPN) proof. If the theorem's proof is
     * incomplete or invalid, or if it contains no assertions, an error message
     * results (and if input argument {@code theorem} is null an
     * {@code IllegalArgumentException} will result ;-)
     *
     * @param theorem {@code Theorem} to be exported.
     * @param appendFileName Specifies an append-mode file name to which
     *            exported proof will be written -- within the folder specified
     *            for each Export Type on the GMFFExportParms RunParm; overrides
     *            the normal name assigned to an export file.
     * @param proofAsst The {@code ProofAsst} object, used to format Proof
     *            Worksheets from Metamath (RPN) proofs.
     * @throws GMFFException is errors encountered.
     */
    public void gmffExportOneTheorem(final Theorem theorem,
        final String appendFileName, final ProofAsst proofAsst)
            throws GMFFException
    {

        if (!gmffInitialized)
            initialization();

        String proofWorksheetText;
        try {
            proofWorksheetText = proofAsst.exportOneTheorem(theorem);
        } catch (final IllegalArgumentException e) {
            messages.accumException(new GMFFException(e,
                GMFFConstants.ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR,
                theorem.getLabel(), e.getMessage()));
            return;
        }

        if (proofWorksheetText == null)
            messages.accumMessage(
                GMFFConstants.ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR,
                theorem.getLabel());
        else
            for (final GMFFException confirm : exportProofWorksheet(
                proofWorksheetText, appendFileName))
                messages.accumException(confirm);
    }

    /**
     * Exports one {@code Theorem} from the {@code LogicalSystem}. loaded
     * <p>
     * This function is called by other functions in {@code GMFFManager} but it
     * would be perfectly valid to call it externally.
     * <p>
     * This function calls {@code ProofAsst.exportOneTheorem} which creates a
     * Proof Worksheet from a Metamath (RPN) proof. If the theorem's proof is
     * incomplete or invalid, or if it contains no assertions, an error message
     * results -- and if input argument {@code theoremLabel} is null or invalid
     * an exception is thrown...
     *
     * @param theoremLabel label of {@code Theorem} to be exported.
     * @param appendFileName Specifies an append-mode file name to which
     *            exported proof will be written -- within the folder specified
     *            for each Export Type on the GMFFExportParms RunParm; overrides
     *            the normal name assigned to an export file.
     * @param proofAsst The {@code ProofAsst} object, used to format Proof
     *            Worksheets from Metamath (RPN) proofs.
     * @throws GMFFException is errors encountered.
     */
    public void gmffExportOneTheorem(final String theoremLabel,
        final String appendFileName, final ProofAsst proofAsst)
            throws GMFFException
    {

        if (!gmffInitialized)
            initialization();

        String proofWorksheetText;
        try {
            proofWorksheetText = proofAsst.exportOneTheorem(theoremLabel);
        } catch (final IllegalArgumentException e) {
            messages.accumException(new GMFFException(e,
                GMFFConstants.ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR, theoremLabel,
                e.getMessage()));
            return;
        }

        if (proofWorksheetText == null)
            messages.accumMessage(
                GMFFConstants.ERRMSG_GMFF_THEOREM_EXPORT_PA_ERROR,
                theoremLabel);
        else
            for (final GMFFException confirm : exportProofWorksheet(
                proofWorksheetText, appendFileName))
                messages.accumException(confirm);
    }

    /**
     * Exports a single Proof Worksheet to files in the requested formats.
     * <p>
     * This function is called by {@code ProofAsst} and by various functions in
     * {@code GMFFManager}.
     * <p>
     * The following functions are performed:
     * <p>
     * <ol>
     * <li>Initializes GMFF if necessary.
     * <li>Throws an exception if the parameter settings do not include at least
     * one active export format.
     * <li>Loads the input proofText into a new {@code ProofWorksheetCache}
     * object
     * <li>invokes each active export request passing the
     * {@code ProofWorksheetCache} and accumulating confirmation messages from
     * them in return.
     * <li>returns the accumulated confirmation messages to the caller.
     * </ol>
     *
     * @param proofText String containing text in the format of an mmj2 Proof
     *            Worksheet.
     * @param appendFileName name of a file to which export data should be
     *            appended (in the proper directory for the Export Type), or
     *            {@code null} if GMFF is supposed to generate the name.
     * @return List of confirmation messages about successful export(s) if no
     *         errors occurred.
     * @throws GMFFException if error found.
     */
    public List<GMFFException> exportProofWorksheet(final String proofText,
        final String appendFileName) throws GMFFException
    {

        final List<GMFFException> confirmationMessage = new ArrayList<>(0);

        if (!gmffInitialized)
            initialization();

        if (selectedExporters.length == 0)
            throw new GMFFException(
                GMFFConstants.ERRMSG_NO_EXPORT_TYPES_SELECTED_ERROR);

        final ProofWorksheetCache p = new ProofWorksheetCache(proofText);

        for (final GMFFExporter selectedExporter : selectedExporters) {
            final GMFFException confirm = selectedExporter
                .exportProofWorksheet(p, appendFileName);
            if (confirm != null)
                confirmationMessage.add(confirm);
        }

        return confirmationMessage;
    }

    /**
     * Implements RunParm GMFFParseMMTypesetDefsComment.
     * <p>
     * This function is primarily used for testing. It parses a file containing
     * a single Metamath comment statement -- of the $t variety. Because it is
     * intended for standalone use in testing, it does not require GMFF
     * initialization prior to use, and it does not check for or trigger GMFF
     * initialization.
     * <p>
     * The code is quick and dirty because it is just used for testing.
     * Efficiency not an issue.
     *
     * @param typesetDefKeyword The Metamath $t keyword to select for parsing
     *            (e.g. "htmldef")
     * @param myDirectory The directory containing the Metamath .mm file to
     *            parse.
     * @param myMetamathTypesetCommentFileName File Name in myDirectory to
     *            parse.
     * @param runParmPrintOption if true prints the input, including the
     *            directory, file name, typesetDefKeyword and the entire
     *            Metamath file.
     * @throws GMFFException if errors found.
     */
    public void parseMetamathTypesetComment(final String typesetDefKeyword,
        final String myDirectory, final String myMetamathTypesetCommentFileName,
        final boolean runParmPrintOption) throws GMFFException
    {

        final GMFFFolder myFolder = new GMFFFolder(filePath, myDirectory,
            typesetDefKeyword);

        String mmDollarTComment = GMFFInputFile.getFileContents(myFolder,
            myMetamathTypesetCommentFileName, typesetDefKeyword,
            GMFFConstants.METAMATH_DOLLAR_T_MESSAGE_DESCRIPTOR,
            GMFFConstants.METAMATH_DOLLAR_T_BUFFER_SIZE);

        if (runParmPrintOption)
            messages.accumMessage(
                GMFFConstants.ERRMSG_INPUT_DOLLAR_T_COMMENT_MM_FILE,
                myFolder.getAbsolutePath(), myMetamathTypesetCommentFileName,
                typesetDefKeyword, mmDollarTComment);

        mmDollarTComment = stripMetamathCommentDelimiters(mmDollarTComment);

        final GMFFExporterTypesetDefs myTypesetDefs = new GMFFExporterTypesetDefs(
            typesetDefKeyword, GMFFConstants.METAMATH_DOLLAR_T_MAP_SIZE);

        final List<GMFFExporterTypesetDefs> list = new ArrayList<>(1);

        list.add(myTypesetDefs);

        final TypesetDefCommentParser parser = new TypesetDefCommentParser(list,
            messages);

        parser.doIt(mmDollarTComment);

        myTypesetDefs.printTypesetDefs(messages);
    }

    /**
     * Generates and outputs to the Messages object an audit report of the final
     * results of GMFF initialization showing the parameters and settings in
     * use.
     */
    public void generateInitializationAuditReport() {
        final StringBuilder sb = new StringBuilder(
            String.format(GMFFConstants.INITIALIZATION_AUDIT_REPORT_2,
                gmffUserExportChoice.generateAuditReportText()));

        for (int i = 0; i < selectedExporters.length; i++)
            sb.append(String.format(GMFFConstants.INITIALIZATION_AUDIT_REPORT_3,
                i,
                selectedExporters[i].gmffExportParms.generateAuditReportText(),
                selectedExporters[i].gmffUserTextEscapes
                    .generateAuditReportText(),
                selectedExporters[i].gmffExporterTypesetDefs
                    .generateAuditReportText()));

        messages.accumMessage(GMFFConstants.INITIALIZATION_AUDIT_REPORT, sb);
    }

    /**
     * Generates and outputs to the Messages object an audit report of the
     * Metamath $t typesetting definitions after parsing of the input Metamath
     * file.
     */
    public void generateTypesettingDefinitionsReport() {

        for (final GMFFExporterTypesetDefs t : exporterTypesetDefsList)
            t.printTypesetDefs(messages);
    }

    /**
     * Initializes GMFF using all cached RunParms, default settings and cached
     * Metamath $t Comment statements.
     * <p>
     * This was surprisingly tricky to get right due to the interrelated nature
     * of the cached data, some of which may be redundant and/or updates to
     * previous inputs. The key data element is Export Type (e.g. "html",
     * "althtml", "latex", etc.) -- it is the key used to match and merge the
     * primary inputs. For example, two GMFFExportParms RunParms with the same
     * Export Type result in one output Exporter (export request), with the last
     * input RunParm overriding the previous inputs.
     * <p>
     * Functions performed, in order:
     * <ol>
     * <li>set {@code gmffInitialized = false} (it will only be set to
     * {@code true} if this entire gauntlet of logic is completed without thrown
     * exceptions.
     * <li>build consolidated list of Export Parms using cached input and
     * default settings.
     * <li>build consolidated list of User Text Escapes using cached input and
     * default settings.
     * <li>build list of enabled, valid {@code Exporter}s.
     * <li>load the Metamath Typeset Def list and update the {@code Exporter}s
     * to point to the defs.
     * <li>validate and load the User Export Choice (either a particular Export
     * Type or "ALL")
     * <li>load final list of Selected (chosen) {@code Exporter}s.
     * <li>set {@code gmffInitialized = true}
     * </ol>
     *
     * @throws GMFFException if an error occurred
     */
    private void initialization() throws GMFFException {
        gmffInitialized = false;

        exportParmsList = loadExportParmsList();

        userTextEscapesList = loadUserTextEscapesList();

        gmffExporterList = loadExporterList();

        updateExporterTypesetDefsList();

        parseMetamathTypesetDefCache();

        gmffUserExportChoice = loadGMFFUserExportChoice();

        selectedExporters = loadSelectedExportersArray();

        gmffInitialized = true;
    }

    /**
     * Builds a list containing the default export parms with input user export
     * parms merged on top.
     * <p>
     * Validates the ExportParms after building the consolidated list.
     *
     * @return the list of GMFFExportParms
     * @throws GMFFException if an error occurred
     */
    private List<GMFFExportParms> loadExportParmsList() throws GMFFException {

        final List<GMFFExportParms> listOut = new ArrayList<>(
            GMFFConstants.DEFAULT_EXPORT_PARMS.length);

        // load defaults:
        for (final GMFFExportParms element : GMFFConstants.DEFAULT_EXPORT_PARMS)
            updateExportParmsList(listOut, element);

        // merge in user input export parms
        for (final GMFFExportParms p : inputGMFFExportParmsList)
            updateExportParmsList(listOut, p);

        validateExportParmsList(listOut);

        return listOut;
    }

    private void updateExportParmsList(final List<GMFFExportParms> listOut,
        final GMFFExportParms t)
    {

        final int j = listOut.indexOf(t);
        if (j == -1)
            listOut.add(t);
        else
            listOut.set(j, t);
    }

    /**
     * Builds a list containing the default text escapes with user input text
     * escapes merged on top.
     * <p>
     * Validates the text escapes after building the consolidated list.
     *
     * @return the list of GMFFUserTextEscapes
     * @throws GMFFException if an error occurred
     */
    private List<GMFFUserTextEscapes> loadUserTextEscapesList()
        throws GMFFException
    {

        final List<GMFFUserTextEscapes> listOut = new ArrayList<>(
            GMFFConstants.DEFAULT_USER_TEXT_ESCAPES.length);

        // load defaults:
        for (final GMFFUserTextEscapes element : GMFFConstants.DEFAULT_USER_TEXT_ESCAPES)
            updateUserTextEscapesList(listOut, element);

        // merge in user input export parms
        for (final GMFFUserTextEscapes u : inputGMFFUserTextEscapesList)
            updateUserTextEscapesList(listOut, u);

        validateUserTextEscapesList(listOut);

        return listOut;
    }

    private void updateUserTextEscapesList(
        final List<GMFFUserTextEscapes> listOut, final GMFFUserTextEscapes t)
    {

        final int j = listOut.indexOf(t);
        if (j == -1)
            listOut.add(t);
        else
            listOut.set(j, t);
    }

    private void validateExportParmsList(final List<GMFFExportParms> list)
        throws GMFFException
    {
        boolean validationErrors = false;

        for (final GMFFExportParms p : list)
            if (!p.areExportParmsValid(filePath, messages))
                validationErrors = true;

        if (validationErrors)
            throw new GMFFException(
                GMFFConstants.ERRMSG_EXPORT_PARMS_LIST_ERROR);
    }

    private void validateUserTextEscapesList(
        final List<GMFFUserTextEscapes> list) throws GMFFException
    {
        boolean validationErrors = false;

        for (final GMFFUserTextEscapes u : list)
            if (!u.areUserTextEscapesValid(exportParmsList, messages))
                validationErrors = true;

        if (validationErrors)
            throw new GMFFException(
                GMFFConstants.ERRMSG_USER_TEXT_ESCAPES_LIST_ERROR);
    }

    private List<GMFFExporter> loadExporterList() throws GMFFException {

        final List<GMFFExporter> x = new ArrayList<>(exportParmsList.size());

        GMFFUserTextEscapes t;
        for (final GMFFExportParms p : exportParmsList) {

            if (!p.onoff.equalsIgnoreCase(GMFFConstants.EXPORT_PARM_ON))
                continue;
            t = null;
            for (final GMFFUserTextEscapes u : userTextEscapesList)
                if (u.exportType.equals(p.exportType)) {
                    t = u;
                    break;
                }
            final GMFFExporter e = GMFFExporter.ConstructModelExporter(this, p,
                t);
            x.add(e);
        }
        return x;
    }

    private void updateExporterTypesetDefsList() {

        GMFFExporter exporter;

        exporterLoop: for (int i = 0; i < gmffExporterList.size(); i++) {
            exporter = gmffExporterList.get(i);

            for (final GMFFExporterTypesetDefs t : exporterTypesetDefsList)
                if (exporter.gmffExportParms.typesetDefKeyword
                    .equals(t.typesetDefKeyword))
                {

                    exporter.gmffExporterTypesetDefs = t;

                    continue exporterLoop;
                }

            // ergo, not found in gmffExporterTypesetDefsList...
            final GMFFExporterTypesetDefs t = new GMFFExporterTypesetDefs(
                exporter.gmffExportParms.typesetDefKeyword, symTbl.size());

            exporter.gmffExporterTypesetDefs = t;

            updateExporterTypesetDefsList(t);

        }
    }

    private void updateExporterTypesetDefsList(
        final GMFFExporterTypesetDefs newT)
    {

        for (final GMFFExporterTypesetDefs oldT : exporterTypesetDefsList)
            if (oldT.typesetDefKeyword.equals(newT.typesetDefKeyword))
                return;
        exporterTypesetDefsList.add(newT);
    }

    private void parseMetamathTypesetDefCache() throws GMFFException {

        final TypesetDefCommentParser parser = new TypesetDefCommentParser(
            exporterTypesetDefsList, messages);

        String typesetDefComment;

        while (nbrTypesetDefinitionsProcessedSoFar < typesetDefinitionsCache
            .size())
        {

            typesetDefComment = typesetDefinitionsCache
                .get(nbrTypesetDefinitionsProcessedSoFar);

            parser.doIt(typesetDefComment);

            nbrTypesetDefinitionsProcessedSoFar++;
        }
    }

    private GMFFUserExportChoice loadGMFFUserExportChoice()
        throws GMFFException
    {

        GMFFUserExportChoice userExportChoice;

        if (inputGMFFUserExportChoice == null)
            userExportChoice = GMFFConstants.DEFAULT_USER_EXPORT_CHOICE;
        else
            userExportChoice = inputGMFFUserExportChoice;

        validateUserExportChoice(userExportChoice);

        return userExportChoice;
    }

    private void validateUserExportChoice(
        final GMFFUserExportChoice userExportChoice) throws GMFFException
    {

        userExportChoice.validateUserExportChoice(exportParmsList);
    }

    private GMFFExporter[] loadSelectedExportersArray() {

        int nbrSelected = 0;

        if (gmffUserExportChoice.exportTypeOrAll
            .equalsIgnoreCase(GMFFConstants.USER_EXPORT_CHOICE_ALL))
            nbrSelected = gmffExporterList.size();
        else
            for (final GMFFExporter e : gmffExporterList)
                if (e.gmffExportParms.exportType
                    .equals(gmffUserExportChoice.exportTypeOrAll))
                    nbrSelected++;

        final GMFFExporter[] selected = new GMFFExporter[nbrSelected];

        if (nbrSelected > 0) {

            int i = 0;

            for (final GMFFExporter exporter : gmffExporterList)
                if (gmffUserExportChoice.exportTypeOrAll
                    .equalsIgnoreCase(GMFFConstants.USER_EXPORT_CHOICE_ALL)
                    || gmffUserExportChoice.exportTypeOrAll
                        .equals(exporter.gmffExportParms.exportType))
                    selected[i++] = exporter;
        }

        return selected;
    }

    private String validateFileType(final String fileType)
        throws GMFFException
    {

        if (!GMFFExportParms.isPresentWithNoWhitespace(fileType)
            || fileType.charAt(0) != GMFFConstants.FILE_TYPE_DOT)
            throw new GMFFException(GMFFConstants.ERRMSG_FILE_TYPE_BAD_MISSING,
                fileType);
        return fileType;
    }

    private int validateMaxNumberToExport(final String max)
        throws GMFFException
    {

        if (GMFFExportParms.isPresentWithNoWhitespace(max)) {
            Integer i = null;
            try {
                i = Integer.valueOf(max.trim());
                if (i > 0)
                    return i.intValue();
            } catch (final NumberFormatException e) {}
        }
        throw new GMFFException(
            GMFFConstants.ERRMSG_MAX_NBR_TO_EXPORT_BAD_MISSING, max);
    }

    private String validateAppendFileName(final String appendFileNameIn)
        throws GMFFException
    {
        String appendFileName;

        if (appendFileNameIn == null)
            return null;

        appendFileName = appendFileNameIn.trim();
        if (appendFileName.length() == 0)
            return null;

        if (GMFFExportParms.isPresentWithNoWhitespace(appendFileName)
            && appendFileName
                .indexOf(GMFFConstants.APPEND_FILE_NAME_ERR_CHAR_1) == -1
            && appendFileName
                .indexOf(GMFFConstants.APPEND_FILE_NAME_ERR_CHAR_2) == -1
            && appendFileName
                .indexOf(GMFFConstants.APPEND_FILE_NAME_ERR_CHAR_3) == -1)
        {}
        else
            throw new GMFFException(GMFFConstants.ERRMSG_APPEND_FILE_NAME_ERROR,
                appendFileName);

        return appendFileName;
    }

    private String validateTheoremLabelOrAsterisk(
        final String theoremLabelOrAsterisk) throws GMFFException
    {
        if (!GMFFExportParms.isPresentWithNoWhitespace(theoremLabelOrAsterisk))
            throw new GMFFException(
                GMFFConstants.ERRMSG_LABEL_OR_ASTERISK_BAD_MISSING,
                theoremLabelOrAsterisk);
        return theoremLabelOrAsterisk;
    }

    // removes $( and $) delimiters to match the way Systemizer
    // caches SrcStmt $t comments. this code is for testing so
    // we just brutally abort w/out a fancy message or more
    // careful inspection of the input.
    private String stripMetamathCommentDelimiters(final String mmComment)
        throws GMFFException
    {

        final int startC = mmComment
            .indexOf(MMIOConstants.MM_BEGIN_COMMENT_KEYWORD);

        final int endC = mmComment
            .lastIndexOf(MMIOConstants.MM_END_COMMENT_KEYWORD);

        if (startC == -1 || endC == -1 || startC > endC)
            throw new GMFFException(
                GMFFConstants.ERRMSG_INVALID_METAMATH_TYPESET_COMMENT_ERROR);

        return mmComment.substring(
            startC + MMIOConstants.MM_BEGIN_COMMENT_KEYWORD.length(), endC);
    }
}
