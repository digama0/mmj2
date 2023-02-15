//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.transforms;

import java.util.*;

import mmj.lang.*;
import mmj.transforms.Prover.HypProver;
import mmj.transforms.Prover.HypProverResult;

public class Provers {
    private final static Deque<ParseNode> unifyNodeStack = new ArrayDeque<>();
    private final static Deque<ParseNode> compareNodeStack = new ArrayDeque<>();

    private Provers() {}

    /**
     * A Prover which uses the given assrt whenever it matches the current goal.
     * This is suitable for theorems where there are no extra VarHyps in the $e
     * hypotheses to the theorem that do not show up in the goal theorem.
     * <p>
     * It does not use work variables in the substitution; thus applying the
     * theorem addcni "( A + B ) e. CC" to "( ( 1 + 1 ) + &C2 ) e. CC" will
     * succeed (with A = ( 1 + 1 ) and B = &C2), but will fail against
     * "&C1 e. CC" even though unification is possible.
     *
     * @author Mario
     */
    public static class UseWhenPossible extends HypProver {
        protected VarHyp[] varHypArray;
        private final UseWhenPossibleListener uwpListener;

        public UseWhenPossible(final Assrt assrt) {
            super(assrt);
            varHypArray = assrt.getMandVarHypArray();
            uwpListener = null;
        }

        public UseWhenPossible(final Assrt assrt,
            final UseWhenPossibleListener f)
        {
            super(assrt);
            final List<VarHyp> list = new ArrayList<>();
            for (final Hyp h : assrt.getMandFrame().hypArray)
                if (h instanceof VarHyp)
                    list.add((VarHyp)h);
            varHypArray = list.toArray(new VarHyp[list.size()]);
            uwpListener = f;
        }

        @Override
        public HypProverResult hypProve(final WorksheetInfo info,
            final ParseNode root)
        {
            final ParseNode[] assrtSubst = assrt.getExprParseTree().getRoot()
                .unifyWithSubtree(root, varHypArray, unifyNodeStack,
                    compareNodeStack);
            if (assrtSubst != null) {
                final HypProverResult r = new HypProverResult(assrt,
                    varHypArray, assrtSubst);
                return uwpListener == null
                    || uwpListener.loadExtra(info, root, r) ? r : null;
            }
            return null;
        }
    }

    public static abstract class ArrayProver implements Prover {
        Map<String, Prover> provers = new HashMap<>();

        public void addProver(final AssrtProver prover) {
            provers.put(prover.assrt.getLabel(), prover);
        }

        @Override
        public ProverResult prove(final WorksheetInfo info,
            final ParseNode root)
        {
            final Prover prover = provers.get(selectProver(info, root));
            return prover == null ? null : prover.prove(info, root);
        }

        public abstract String selectProver(WorksheetInfo info, ParseNode root);
    }

    @FunctionalInterface
    public interface UseWhenPossibleListener {
        /**
         * If {@code assrt} has extra variables in the hypotheses (such as
         * filling a goal "A = C" with transitivity "A = B & B = C => A = C", in
         * which the variable B is not present in the conclusion), the initial
         * goal matching will not be sufficient to fill all the steps, and an
         * {@link IllegalArgumentException} will be triggered. To prevent this,
         * subclasses should override this method and fill these extra variables
         * based on domain-specific rules. If this method does nothing, this
         * class will perform the same function as {@link UseWhenPossible}.
         *
         * @param info The context
         * @param root The goal
         * @param r The partially filled result ({@code r.subst} contains nulls,
         *            which should be set by this method)
         * @return True if the match should be accepted
         */
        boolean loadExtra(final WorksheetInfo info, final ParseNode root,
            final HypProverResult r);
    }
}
