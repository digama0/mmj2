//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;

/** The template for some property. Usually it has form "var e. set" */
public class PropertyTemplate extends ParseNodeHashElem {
    /** The place in the template which could be replaced */
    public static final ParseNode templateReplace = new ParseNode() {
        @Override
        public String toString() {
            return "Template-stub";
        }
    };

    public PropertyTemplate(final ParseNode template) {
        super(template);
    }

    public ParseNode subst(final ParseNode substNode) {
        return node.deepCloneWNodeSub(PropertyTemplate.templateReplace,
            substNode.deepClone());
    }

    private static class ParseNodeRef {
        ParseNode res;
        boolean ok = true;
    };

    private static void extract(final ParseNodeRef res,
        final ParseNode curTempl, final ParseNode input)
    {
        if (curTempl == templateReplace) {
            if (res.res != null)
                res.ok = false;
            res.res = input;
        }
        else {
            if (curTempl.stmt != input.stmt) {
                res.ok = false;
                return;
            }

            assert input.child.length == curTempl.child.length;

            for (int i = 0; i < input.child.length; i++) {
                extract(res, curTempl.child[i], input.child[i]);
                if (!res.ok)
                    return;
            }
        }
    }

    public ParseNode extractNode(final ParseNode input) {
        final ParseNodeRef res = new ParseNodeRef();
        extract(res, node, input);
        if (res.ok)
            return res.res;
        else
            return null;
    }
}
