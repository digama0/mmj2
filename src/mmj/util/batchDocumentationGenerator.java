/**
 * 
 */
package mmj.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

/**
 * this class is used to generate documentation about batch commands.
 */
public class batchDocumentationGenerator {

    /**
     * @param args - name of the file that will be filled with documentation.
     */
    public static void main(final String[] args) {
        if (args.length >= 1)
            try {
                generateDocumentation(args[0]);
            } catch (final FileNotFoundException e) {
                return;
            }
    }

    /**
     * this function writes documentation of every command in
     * UtilConstants.RUNPARM_LIST in the file.
     * 
     * @param fileName - name of the file that will be filled with
     *            documentation.
     * @throws FileNotFoundException - is thrown if file is not found
     */
    private static void generateDocumentation(final String fileName)
        throws FileNotFoundException
    {
        final PrintWriter documentation = new PrintWriter(fileName);

        documentation.append("<html>\n");

        for (final BatchCommand element : UtilConstants.RUNPARM_LIST)
            documentation.append(element.documentation());

        documentation.append("\n</html>");

        documentation.close();
    }

}
