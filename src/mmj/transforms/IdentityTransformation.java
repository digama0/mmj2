package mmj.transforms;

import mmj.lang.ParseNode;

/** No transformation at all =) */
class IdentityTransformation extends Transformation {

    public IdentityTransformation(final TransformationManager trManager,
        final ParseNode originalNode)
    {
        super(trManager, originalNode);
    }

    @Override
    public ParseNode getCanonicalNode(final WorksheetInfo info) {
        return originalNode;
    }

    @Override
    public GenProofStepStmt transformMeToTarget(final Transformation target,
        final WorksheetInfo info)
    {
        assert target.originalNode.isDeepDup(originalNode);
        return null; // nothing to do
    }
}
