//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * GMFFExporter.java  0.01 11/01/2011
 *
 * Version 0.01:
 * Nov-01-2011: new.
 */

package mmj.gmff;

import java.util.HashMap;
import java.util.Map;

import mmj.lang.*;

/**
 * {@code GMFFExporter} is the base class for creating export files.
 * <p>
 * There can be multiple Exporters in use at one time but the key is Export Type
 * (e.g. "html", "althtml", etc.) and only one Exporter of a given Export Type
 * can be active at one time.
 * <p>
 * The Export Type key is used to bring together and consolidate the various
 * RunParm settings at GMFF initialization time.
 * <p>
 * What this all boils down to is that there cannot be two different Models in
 * use for, say, Export Type "html" in a single execution of mmj2. However, the
 * Export Type code is arbitrary and no functionality is hard-coded based on
 * Export Type. So it is possible to define another Export Type such as "html2"
 * which and use say, Model B, at the same time that Export Type "html" uses
 * Model A -- you would just need to define all of the relevant parameters,
 * including the Export Directory (so that the two exports don't overwrite each
 * other.)
 * <p>
 * {@code GMFFExporter} also contains a Map holding a cache of Model Files --
 * {@code modelFileCacheMap} for the given Export Type. The purpose is to avoid
 * re-reading the model files over and over again.
 * <p>
 * {@code GMFFExporter} also contains a number of utility functions which are
 * common to the different model exporters, such as {@code ModelAExporter},
 * which is an extension of {@code GMFFExporter}.
 */
public abstract class GMFFExporter {

    GMFFManager gmffManager;
    GMFFExportParms gmffExportParms;
    GMFFUserTextEscapes gmffUserTextEscapes;
    GMFFExporterTypesetDefs gmffExporterTypesetDefs;
    char[][] escapeSubstitutions;
    Map<String, String> modelFileCacheMap;

    /**
     * A factory for generating GMFFExporters according to Model Id.
     * <p>
     * Right now only Model A is defined, so this code looks peculiar.
     * <p>
     * Note: The Exporter's {@code gmffExporterTypesetDefs} are loaded at
     * GMFFInitialize time, and are left null when the Exporter is initially
     * constructed.
     *
     * @param gmffManager The {@code GMFFManager} instance.
     * @param gmffExportParms Export Parms for the output {@code GMFFExporter}
     * @param gmffUserTextEscapes Text Escapes for the output
     *            {@code GMFFExporter}
     * @return the newly created {@code GMFFExporter}
     * @throws GMFFException if an error occurred
     */
    public static GMFFExporter ConstructModelExporter(
        final GMFFManager gmffManager, final GMFFExportParms gmffExportParms,
        final GMFFUserTextEscapes gmffUserTextEscapes) throws GMFFException
    {

        GMFFExporter gmffExporter;
        if (gmffExportParms.modelId.equals(GMFFConstants.MODEL_A))
            gmffExporter = new ModelAExporter(gmffManager, gmffExportParms,
                gmffUserTextEscapes);
        else
            throw new GMFFException(GMFFConstants.ERRMSG_INVALID_MODEL_ID_ERROR,
                gmffExportParms.modelId, gmffExportParms.exportType);
        return gmffExporter;
    }

    /*
     * The standard constructor for {@code GMFFExporter}.
     * <p>
     * Note: The Exporter's {@code gmffExporterTypesetDefs}
     * are loaded at GMFFInitialize time, and are left null
     * when the Exporter is initially constructed.
     * <p>
     * @param gmffManager The {@code GMFFManager} instance.
     * @param gmffExportParms Export Parms for the output
     *				{@code GMFFExporter}
     * @param gmffUserTextEscapes Text Escapes for the output
     *				{@code GMFFExporter}
     */
    public GMFFExporter(final GMFFManager gmffManager,
        final GMFFExportParms gmffExportParms,
        final GMFFUserTextEscapes gmffUserTextEscapes)
    {

        this.gmffManager = gmffManager;
        this.gmffExportParms = gmffExportParms;
        this.gmffUserTextEscapes = gmffUserTextEscapes;
        escapeSubstitutions = new char[256][];
        for (int i = 0; i < escapeSubstitutions.length; i++) {
            escapeSubstitutions[i] = new char[1];
            escapeSubstitutions[i][0] = (char)i;
        }

        for (final EscapePair pair : gmffUserTextEscapes.escapePairList) {

            escapeSubstitutions[pair.num] = new char[pair.replacement.length()];

            for (int i = 0; i < pair.replacement.length(); i++)
                escapeSubstitutions[pair.num][i] = pair.replacement.charAt(i);
        }

        modelFileCacheMap = new HashMap<>(
            GMFFConstants.EXPORTER_MODEL_CACHE_INIT_SIZE);

    }

    /**
     * Abstract method to export a Proof Worksheet according to the pattern of a
     * Model.
     *
     * @param proofWorksheetCache {@code ProofWorksheetCache} object containing
     *            the proof to be exported.
     * @param appendFileName File Name (minus File Type) of append file if the
     *            regular file name is to be overridden (see documentation of
     *            appendFileNames in GMFFDoc\GMFFRunParms.txt).
     * @return Confirmation message of the successful export showing the
     *         absolute path of the output file -- or {@code null} if the export
     *         failed (error messages are accumed in the {@code Messages}
     *         object.)
     */
    public abstract GMFFException exportProofWorksheet(
        ProofWorksheetCache proofWorksheetCache, String appendFileName);

    /**
     * Get function to return the {@code Messages} object.
     *
     * @return the {@code Messages} object.
     */
    public Messages getMessages() {
        return gmffManager.getMessages();
    }

    /**
     * Outputs the contents of a Mandatory Model File to the export buffer
     * throwing an exception if the file is not found.
     *
     * @param exportBuffer {@code StringBuilder} containing the contents of the
     *            export file.
     * @param mandatoryModelFileName the File Name of the Model File within the
     *            Models Directory for this Export Type.
     * @param theoremLabel provided for use in error messages.
     * @throws GMFFException if other errors encountered (such as I/O errors.)
     */
    public void appendMandatoryModelFile(final StringBuilder exportBuffer,
        final String mandatoryModelFileName, final String theoremLabel)
            throws GMFFException
    {

        exportBuffer.append(
            getMandatoryModelFile(mandatoryModelFileName, theoremLabel));
    }

    /**
     * Appends text from a Model File to the output StringBuilder.
     * <p>
     * NOTE: does not escape the text because it is from a Model File and should
     * already be escaped (as needed.)
     *
     * @param exportBuffer {@code StringBuilder} containing the contents of the
     *            export file.
     * @param modelFileText data from the Model File to be appended to the
     *            exportBuffer.
     */
    public void appendModelFileText(final StringBuilder exportBuffer,
        final String modelFileText)
    {

        exportBuffer.append(modelFileText);
    }

    /**
     * If possible typesets the input token and outputs to the buffer.
     * <p>
     * <ul>
     * if token found in typesetting table, appends the replacement text from
     * the table to the export buffer -- and returns. Otherwise...
     * <li>if not found in the typesetting table then escapes it as text and
     * appends it,
     * <li>if the not-found token is a valid, non-WorkVar symbol then an error
     * message is sent about the missing typesetting table entry.
     * </ul>
     * <p>
     * Note: In the situation where the image file (.gif) referenced by the
     * typesetting replacement text is not present in the Export Directory, some
     * browsers display "ALT=" text. The Metamath set.mm $t statement uses the
     * "ALT=" feature to display the ASCII token text. So in this scenario it
     * may appear that the token was not found in the typesetting table, but the
     * absence of an error message indicates that that is not the case.
     *
     * @param exportBuffer {@code StringBuilder} containing the contents of the
     *            export file.
     * @param token the Metamath token to be typeset.
     * @param theoremLabel provided for use in error messages.
     */
    public void typesetAndAppendToken(final StringBuilder exportBuffer,
        final String token, final String theoremLabel)
    {

        final String typesetString = gmffExporterTypesetDefs.typesetDefMap
            .get(token);

        if (typesetString != null) {
            exportBuffer.append(typesetString);
            return;
        }

        escapeAndAppendProofText(exportBuffer, " " + token + " ");

        final Sym sym = gmffManager.getSymTbl().get(token);

        if (sym == null || sym instanceof WorkVar)
            return;

        gmffManager.getMessages().accumMessage(
            GMFFConstants.ERRMSG_TYPESET_DEF_NOT_FOUND_ERROR, theoremLabel,
            token, gmffExporterTypesetDefs.typesetDefKeyword);
    }

    /**
     * Appends non-typeset text to the output buffer after escaping the text.
     *
     * @param exportBuffer {@code StringBuilder} containing the contents of the
     *            export file.
     * @param proofText output text from the proof worksheet to be escaped and
     *            appended to the output buffer.
     */
    public void escapeAndAppendProofText(final StringBuilder exportBuffer,
        final String proofText)
    {
        for (int i = 0; i < proofText.length(); i++)
            exportBuffer.append(escapeSubstitutions[proofText.charAt(i)]);
    }

    /**
     * Returns a String containing the contents of a mandatory Model File.
     * <p>
     * Invokes the routine to read the Model File from the cache and if not
     * found -- because it is a Mandatory Model File -- throws the
     * {@code GMFFMandatoryModelNotFoundException} exception.
     *
     * @param mandatoryModelFileName the File Name of the Model File within the
     *            Models Directory for this Export Type.
     * @param theoremLabel provided for use in error messages.
     * @return String containing the contents of the Model File.
     * @throws GMFFException if other errors encountered (such as I/O errors.)
     */
    public String getMandatoryModelFile(final String mandatoryModelFileName,
        final String theoremLabel) throws GMFFException
    {
        String mandatoryString = null;
        try {
            mandatoryString = readModelFile(mandatoryModelFileName);
        } catch (final GMFFException e) {
            throw e.code == GMFFConstants.ERRMSG_INPUT_FILE_NOT_FOUND
                ? new GMFFException(e,
                    GMFFConstants.ERRMSG_MANDATORY_MODEL_NOT_FOUND_ERROR,
                    theoremLabel, gmffExportParms.exportType,
                    mandatoryModelFileName, e.getMessage())
                : e;
        }

        return mandatoryString;
    }

    /**
     * Returns a String containing the contents of an optional Model File.
     * <p>
     * Invokes the routine to read the Model File from the cache and if not
     * found -- because it is an Optional Model File -- simply returns
     * {@code null}.
     *
     * @param optionalModelFileName the File Name of the Model File within the
     *            Models Directory for this Export Type.
     * @return String containing the contents of the Model File.
     * @throws GMFFException if an error occurred
     */
    public String getOptionalModelFile(final String optionalModelFileName)
        throws GMFFException
    {
        try {
            return readModelFile(optionalModelFileName);
        } catch (final GMFFException e) {
            if (e.code == GMFFConstants.ERRMSG_INPUT_FILE_NOT_FOUND)
                return null;
            throw e;
        }
    }

    /**
     * Returns the cached model file contents or if not cached already reads the
     * model file from disk and caches it.
     * <p>
     * Returns the file contents as a String, or throws an exception if not
     * found.
     *
     * @param modelFileName the File Name of the Model File within the Models
     *            Directory for this Export Type.
     * @return String containing the contents of the Model File.
     * @throws GMFFException if other errors encountered (such as I/O errors.)
     */
    public String readModelFile(final String modelFileName)
        throws GMFFException
    {

        String modelFileContents = modelFileCacheMap.get(modelFileName);

        if (modelFileContents == null) {

            final GMFFInputFile modelFile = new GMFFInputFile(
                gmffExportParms.modelsFolder, modelFileName,
                gmffExportParms.exportType,
                GMFFConstants.MODEL_ERROR_MESSAGE_DESCRIPTOR,
                GMFFConstants.DEFAULT_MODEL_FILE_BUFFER_SIZE);

            modelFileContents = modelFile.loadContentsToString();

            modelFileCacheMap.put(modelFileName, modelFileContents);

        }

        return modelFileContents;
    }

    /**
     * Loads a {@code MinProofWorksheet} object using the cached
     * {@code proofText} if the cache does not already contain a loaded instance
     * of the {@code MinProofWorksheet}.
     *
     * @param p the ProofWorksheetCache object.
     * @throws GMFFException if an error occurred
     */
    protected void loadMinProofWorksheet(final ProofWorksheetCache p)
        throws GMFFException
    {

        if (p.cachedMinProofWorksheet == null) {

            p.cachedMinProofWorksheet = new MinProofWorksheet(getMessages());

            p.cachedMinProofWorksheet.load(p.proofText);
        }
    }

    /**
     * Writes the exported text to an output file.
     * <p>
     * Note: the name of the output file is generated here and it is a bit
     * tricky. There is a hierarchy of possibilities:
     * <ul>
     * <li>if {@code appendFileName} is not null then that is the name used, and
     * the file is opened in "append mode", meaning the output is written to the
     * end of the file if the file already exists.
     * <li>otherwise, if the Export Parms specify an output file name then that
     * is the name used.
     * <li>otherwise, the {@code theoremLabel} is used as the file name.
     * </ul>
     * <p>
     * Finally, to note, the output file name's File Type is appended to the
     * file name computed above, in every case.
     *
     * @param exportText the contents to be written out.
     * @param appendFileName File Name (minus File Type) of append file if the
     *            regular file name is to be overridden (see documentation of
     *            appendFileNames in GMFFDoc\GMFFRunParms.txt).
     * @param theoremLabel the label of the theorem whose proof is being
     *            exported.
     * @return Confirmation message of the successful export showing the
     *         absolute path of the output file.
     * @throws GMFFException if the output operation fails.
     */
    protected GMFFException outputToExportFile(final StringBuilder exportText,
        final String appendFileName, final String theoremLabel)
            throws GMFFException
    {

        String exportFileNamePrefix;
        boolean append = false;

        if (appendFileName == null) {

            if (gmffExportParms.outputFileName == null)
                exportFileNamePrefix = theoremLabel;
            else
                exportFileNamePrefix = gmffExportParms.outputFileName;
        }
        else {
            exportFileNamePrefix = appendFileName;
            append = true;
        }

        final String exportFileName = exportFileNamePrefix
            + gmffExportParms.exportFileType;

        final String exportFileAbsolutePathname = writeExportFile(
            exportFileName, exportText, append);

        return new GMFFException(GMFFConstants.ERRMSG_EXPORT_CONFIRMATION,
            theoremLabel, exportFileAbsolutePathname);
    }

    /**
     * Writes the export text to the specified file and returns a String
     * containing the absolute path of the output file.
     * <p>
     * Note that here is where the last of the Export Parms come into play,
     * including the Export Directory and the Charset Encoding name. Export Type
     * is used in error messages, just to be helpful.
     * <p>
     * The actual I/O operation is handled by {@code  GMFFExportfile}.
     *
     * @param exportFileName the output file, including file type.
     * @param exportBuffer the output text data.
     * @param append true if file is to be opened in "append mode", otherwise
     *            false.
     * @return String containing the absolute path of the export file.
     * @throws GMFFException if the output operation fails.
     */
    protected String writeExportFile(final String exportFileName,
        final StringBuilder exportBuffer, final boolean append)
            throws GMFFException
    {

        final GMFFExportFile exportFile = new GMFFExportFile(
            gmffExportParms.exportFolder, exportFileName,
            gmffExportParms.charsetEncoding, gmffExportParms.exportType,
            append);

        final String absolutePathname = exportFile.getAbsolutePath();

        exportFile.writeFileContents(exportBuffer);

        return absolutePathname;
    }
}
