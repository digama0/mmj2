package mmj.transforms;

import mmj.lang.ParseNode;

/** The template for some property. Usually it has form "var e. set" */
public class PropertyTemplate {
    /** The place in the template which could be replaced */
    public static final ParseNode templateReplace = new ParseNode();

    /** template could be null */
    protected final ParseNode templNode;

    public PropertyTemplate(final ParseNode template) {
        this.templNode = template;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof PropertyTemplate))
            return false;
        final PropertyTemplate that = (PropertyTemplate)obj;
        if (templNode == that.templNode)
            return true;

        if (templNode == null || that.templNode == null)
            return false;

        return templNode.isDeepDup(that.templNode);
    }

    @Override
    public int hashCode() {
        if (templNode != null)
            return templNode.deepHashCode();
        else
            return 0;
    }

    public boolean isEmpty() {
        return templNode == null;
    }

    public ParseNode subst(final ParseNode substNode) {
        return templNode.deepCloneWNodeSub(PropertyTemplate.templateReplace,
            substNode.deepClone());
    }
}
