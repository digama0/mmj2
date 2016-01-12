//*****************************************************************************/
//* Copyright (C) 2014                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import mmj.lang.ParseNode;
import mmj.pa.ProofStepStmt;

/**
 * This class describes generalized proof step statement. In simple cases it is
 * simple proof step statement. But it can be used also to describe statements
 * with implications prefix. Such statements have form "ph -> core" where ph is
 * a prefix which could be used in transformations. The main goal is to support
 * auto-transformations for cases like next example:
 * <p>
 * <code>
 * ph -> x in RR
 * ph -> y in RR
 * x + y = z
 * so y + x = z
 * </code>
 * <p>
 * Note: simple proof statements also could have form "ph -> core".
 */
public class GenProofStepStmt {
    /** This proof step includes implication prefix (if it is used). */
    private final ProofStepStmt step;
    /**
     * It is implication prefix. It is null when this general step is a simple
     * proof step.
     */
    private final ParseNode prefix;

    public GenProofStepStmt(final ProofStepStmt step, final ParseNode prefix) {
        super();
        this.step = step;
        this.prefix = prefix;
    }

    public boolean hasPrefix() {
        return prefix != null;
    }

    public final ParseNode getPrefix() {
        assert prefix != null;
        return prefix;
    }

    public final ParseNode getPrefixOrNull() {
        return prefix;
    }

    public ParseNode getCore() {
        final ParseNode root = step.formulaParseTree.getRoot();
        if (prefix == null)
            return root;
        else {
            assert root.child.length == 2;
            return root.child[1];
        }
    }

    ProofStepStmt getSimpleStep() {
        assert !hasPrefix();
        return step;
    }

    ProofStepStmt getImplicationStep() {
        assert hasPrefix();
        return step;
    }

    ProofStepStmt getAnyStep() {
        return step;
    }
}
