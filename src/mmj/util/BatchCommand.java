/**
 *
 */
package mmj.util;

/**
 * <p>
 * This class contain batch command name and documentation of it and it's
 * options. It's created to unite command name and it's documentation.
 */
public class BatchCommand implements Comparable<BatchCommand> {

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
     * Getter of name string
     *
     * @return name string
     */
    public final String nameLower() {
        return name.toLowerCase();
    }

    @Override
    public int hashCode() {
        return nameLower().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof BatchCommand) &&
                nameLower().equals(((BatchCommand)obj).nameLower());
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

    /**
     * implementation of Comparable compareTo function. This function compares
     * names of BatchCommands so the can be sorted in alphabetic order.
     *
     * @param b - what to compare to.
     * @return difference
     */
    public int compareTo(final BatchCommand b) {
        if (b == this)
            return 0;
        return nameLower().compareTo(b.nameLower());
    }
}
