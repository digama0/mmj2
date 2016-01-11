//*****************************************************************************/
//* Copyright (C) 2013                                                        */
//* ALEXEY MERKULOV  steelart (dot) alex (at) gmail (dot) com                 */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
package mmj.verify;

import java.util.Random;

/**
 * This enumeration is used to indicate the order of theorem hypotheses during
 * (mainly) loading or storing. The order is used in debug goals.
 */
public enum HypsOrder {
    /** Means that logical hypotheses should be left in the original order. */
    Correct {
        @Override
        public void reorder(final String[] hypStep) {
            // do nothing!
        }

    },

    /**
     * Means that logical hypotheses should be randomized on exported proof
     * steps.
     */
    Randomized {
        @Override
        public void reorder(final String[] hypStep) {
            if (hypStep.length < 2)
                return;

            final Random random = new Random(System.nanoTime());

            for (int i = 0; i < hypStep.length; i++) {
                final int swap = random.nextInt(hypStep.length);
                final String s = hypStep[swap];
                hypStep[swap] = hypStep[i];
                hypStep[i] = s;
            }
        }
    },

    /** Means that logical hypotheses should be emitted in reverse order. */
    Reverse {
        @Override
        public void reorder(final String[] hypStep) {
            if (hypStep.length < 2)
                return;

            final int half = hypStep.length / 2;

            for (int i = 0; i < half; i++) {
                final int swap = hypStep.length - i - 1;
                final String s = hypStep[swap];
                hypStep[swap] = hypStep[i];
                hypStep[i] = s;
            }
        }
    },

    /**
     * Means that the first half of logical hypotheses should be emitted in
     * canonical order, but the second part should be emitted in reverse order.
     */
    HalfReverse {
        @Override
        public void reorder(final String[] hypStep) {
            if (hypStep.length < 3)
                return;

            final int revStart = hypStep.length / 2;
            final int half = (hypStep.length - revStart) / 2;

            for (int i = 0; i < half; i++) {
                final int idx = i + revStart;
                final int swap = hypStep.length - i - 1;
                final String s = hypStep[swap];
                hypStep[swap] = hypStep[idx];
                hypStep[idx] = s;
            }
        }
    },

    /**
     * Means that logical hypotheses list should be empty and autocompleted by
     * autocomplete feature.
     * <p>
     * This is not the order, actually. The generated derivation step will not
     * have the hypotheses list - it should be autocompleted!
     */
    Autocomplete {
        @Override
        public void reorder(final String[] hypStep) {
            // This function should not be called!
            throw new UnsupportedOperationException();
        }
    },

    /**
     * Means that the logical hypotheses should be emitted in some order,
     * depending on current debug goals. The order is not fixed and this option
     * should be used for performance experiments.
     */
    SomeOrder {
        @Override
        public void reorder(final String[] hypStep) {
            // You can implement some experiment here!

            // Now it is round shift of hypotheses
            if (hypStep.length < 2)
                return;

            final String first = hypStep[0];
            System.arraycopy(hypStep, 1, hypStep, 0, hypStep.length - 1);
            hypStep[hypStep.length - 1] = first;
        }
    };

    abstract public void reorder(final String[] hypStep);
}
