/**
 * 
 */
package mmj.util;

/**
 * <p>
 * This class contain batch command name and documentation of it and it's
 * options. It's created to unite command name and it's documentation.
 */
public class BatchCommand {

    private final String name;
    private final String documentation;

    /**
     * Constructor of BatchCommand. Sets documentation to null.
     * 
     * @param name name of the command that will be stored
     */
    BatchCommand(final String name) {
        this.name = name;
        documentation = null;
    }

    /**
     * Constructor of BatchCommand.
     * 
     * @param name name of the command that will be stored
     * @param documentation string with the documentation of this command
     */
    BatchCommand(final String name, final String documentation) {
        this.name = name;
        this.documentation = documentation;
    }

    /**
     * Getter of name string
     * 
     * @return name string
     */
    public final String name() {
        return name;
    }

    /**
     * Getter of documentation string
     * 
     * @return string with documentation
     */
    public final String documentation() {
        return documentation;
    }

    @Override
    public String toString() {
        return name();
    }
}
