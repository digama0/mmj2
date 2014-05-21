package mmj.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import mmj.gmff.GMFFException;
import mmj.lang.TheoremLoaderException;
import mmj.lang.VerifyException;
import mmj.mmio.MMIOException;

public class BatchDocumentationBoss extends Boss {

    public BatchDocumentationBoss(final BatchFramework batchFramework) {
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
        throws IllegalArgumentException, MMIOException, FileNotFoundException,
        IOException, VerifyException, TheoremLoaderException, GMFFException
    {
        if (runParm.name
            .compareToIgnoreCase(UtilConstants.RUNPARM_GENERATE_BATCH_DOCUMENTATION
                .name()) == 0)
        {
            generateDocumentation(runParm);
            return true;
        }
        return false;// not consumed
    }

    private void generateDocumentation(final RunParmArrayEntry runParm)
        throws FileNotFoundException
    {
        editRunParmValuesLength(runParm,
            UtilConstants.RUNPARM_VERIFY_PROOF.name(), 1);

        final PrintWriter documentation = new PrintWriter(runParm.values[0]);

        documentation.append("<html>\n");

        for (final BatchCommand element : UtilConstants.RUNPARM_LIST)
            documentation.append(element.documentation());

        documentation.append("\n</html>");

        documentation.close();
    }
}
