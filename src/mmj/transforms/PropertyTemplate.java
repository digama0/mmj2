package mmj.transforms;

import mmj.lang.ParseNode;

/** The template for some property. Usually it has form "var e. set" */
public class PropertyTemplate {
    /** The place in the template which could be replaced */
    public static final ParseNode templateReplace = new ParseNode();

    /** template could be null */
    protected final ParseNode template;

    public PropertyTemplate(final ParseNode template) {
        this.template = template;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof PropertyTemplate))
            return false;
        final PropertyTemplate that = (PropertyTemplate)obj;
        if (template == that.template)
            return true;

        if (template == null || that.template == null)
            return false;

        return template.isDeepDup(that.template);
    }

    @Override
    public int hashCode() {
        if (template != null)
            return template.deepHashCode();
        else
            return 0;
    }

    public boolean isEmpty() {
        return template == null;
    }

    public ParseNode subst(final ParseNode substNode) {
        return template.deepCloneWNodeSub(PropertyTemplate.templateReplace,
            substNode.deepClone());
    }
}
