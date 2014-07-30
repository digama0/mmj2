/**
 * 
 */
package mmj.util;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * this class is used to generate documentation about batch commands.
 */
public class BatchDocumentationGenerator {

    /**
     * @param args - name of the file that will be filled with documentation.
     */
    public static void main(final String[] args) {
        if (args.length == 1)
            try {
                generateDocumentation(args[0]);
            } catch (final FileNotFoundException e) {
                System.err.print("Error:File not found exception.\nFileName:\""
                    + args[0] + "\"\nException" + e);
                System.exit(-1);
            }
        else {
            System.err
                .print("Error:Invalid number of arguments.\nNeeds:1 Given:"
                    + args.length);
            System.exit(-1);
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
        final BatchCommand commandList[] = UtilConstants.RUNPARM_LIST;

        Arrays.sort(commandList);

        final PrintWriter documentation = new PrintWriter(fileName);

        documentation.append("<html>\n");
        documentation.append("<article>\n");
        documentation.append("<h1>Table of content</h1>\n");

        documentation.append("<ul>");
        for (int index = 0; index < commandList.length; index++) {
            documentation.append("<li>");
            documentation.append("<a href=\"#command" + index);
            documentation.append("\">");
            documentation.append(commandList[index].name());
            documentation.append("</a>\n");
        }
        documentation.append("</ul>");

        documentation.append("<br>");
        documentation.append("<h1>Content</h1>\n");
        documentation.append("<br>");

        for (int index = 0; index < commandList.length; index++) {
            documentation.append("<hr>\n");
            documentation.append("<h3 id=\"command");
            documentation.append(index + "\">");
            documentation.append(commandList[index].name());
            documentation.append("</h3>\n\n");
            documentation.append(commandList[index].documentation());
            documentation.append("\n<br>");
        }

        documentation.append("</article>\n</html>");

        documentation.close();
    }
}
