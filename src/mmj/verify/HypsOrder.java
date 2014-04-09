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
    /** Canonical correct metamath hypotheses order */
    CorrectOrder {
        @Override
        public void reorder(final String[] hypStep) {
            // do nothing!
        }

    },

    /** Randomized metamath hypotheses order */
    RandomizedOrder {
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

    /** Reverse hypotheses order */
    ReverseOrder {
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

    /** First half - in correct order, second part - in reverse order */
    HalfReverseOrder {
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

    /** Unspecified order which should be used for performance experiments. */
    SomeOrder {
        @Override
        public void reorder(final String[] hypStep) {
            // You can implement some experiment here!
        }
    };

    abstract public void reorder(final String[] hypStep);
}
