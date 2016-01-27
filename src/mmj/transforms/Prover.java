package mmj.transforms;

import mmj.lang.*;

/**
 * A general interface for specifying reverse provers, which start at the goal
 * and work backwards producing Assrts as they go. The
 * {@link #prove(WorksheetInfo, ParseNode)} method describes one stage of this,
 * with an input goal and output an assrt and a list of subgoals.
 *
 * @author Mario
 */
public interface Prover {

    /**
     * Prove the given expression from scratch. Implementations are expected to
     * return {@code null} if this prover doesn't know how to prove the theorem
     * using its assrt, or the list of hypothesis subgoals that result.
     *
     * @param info The context
     * @param root The goal expression
     * @return An array of hypothesis expressions
     */
    public abstract ProverResult prove(WorksheetInfo info, ParseNode root);

    public static class ProverResult {
        public Assrt assrt;
        public ParseNode[] subst;

        public ProverResult(final Assrt assrt, final ParseNode[] subst) {
            this.assrt = assrt;
            this.subst = subst;
        }

        public int indexOf(final String var) {
            final Hyp[] hypArray = assrt.getMandFrame().hypArray;
            for (int i = 0; i < hypArray.length; i++) {
                if (!(hypArray[i] instanceof VarHyp))
                    return -1; // reached the end of varhyps
                if (((VarHyp)hypArray[i]).getVar().getId().equals(var))
                    return i;
            }
            return -1;
        }

        public ParseNode get(final String var) {
            return subst[indexOf(var)];
        }
        public ParseNode set(final String var, final ParseNode node) {
            return subst[indexOf(var)] = node;
        }
    }

    /**
     * A general interface for specifying reverse provers, which start at the
     * goal and work backwards producing Assrts as they go. The
     * {@link #prove(WorksheetInfo, ParseNode)} method describes one stage of
     * this, with an input goal and output an assrt and a list of subgoals.
     *
     * @author Mario
     */
    public abstract class AssrtProver implements Prover {
        public Assrt assrt;

        public AssrtProver(final Assrt assrt) {
            this.assrt = assrt;
        }

        @Override
        public String toString() {
            return assrt.toString();
        }
    }

    /**
     * An extension to {@link Prover}, where the function is only required to
     * give an Assrt and substitutions to the variables in the Assrt.
     *
     * @author Mario
     */
    public static abstract class HypProver extends AssrtProver {
        public HypProver(final Assrt assrt) {
            super(assrt);
        }

        /**
         * Similar to {@link Prover#prove(WorksheetInfo, ParseNode)}. Prove a
         * goal expression, producing an Assrt and a list of substitutions to
         * variables. The list of variables must be exhaustive in all VarHyps
         * used in the conclusion and its hypotheses; failure to do so will
         * cause an {@link IllegalArgumentException}.
         *
         * @param info The context
         * @param root The goal expression
         * @return An Assrt and array of hypothesis expressions
         */
        public abstract HypProverResult hypProve(WorksheetInfo info,
            ParseNode root);

        @Override
        public ProverResult prove(final WorksheetInfo info,
            final ParseNode root)
        {
            if (info != null
                && assrt.getSeq() >= info.proofWorksheet.getMaxSeq())
                return null;
            final HypProverResult r = hypProve(info, root);
            if (r == null)
                return null;
            final ParseNode[] hyps = new ParseNode[assrt
                .getLogHypArrayLength()];
            int i = 0;
            for (final LogHyp h : assrt.getLogHypArray())
                hyps[i++] = h.getExprParseTree().getRoot()
                    .deepCloneApplyingAssrtSubst(r.vars, r.subst);
            return new ProverResult(assrt, hyps);
        }
    }

    /**
     * Result type for a {@link HypProver}. There is a slight abuse of
     * inheritance here, since the {@code subst} variable means something
     * different for HypProverResult than it does for ProverResult.
     *
     * @author Mario
     */
    public static class HypProverResult extends ProverResult {
        public VarHyp[] vars;

        public HypProverResult(final Assrt assrt, final VarHyp[] vars,
            final ParseNode[] subst)
        {
            super(assrt, subst);
            this.vars = vars;
        }
    }
}
