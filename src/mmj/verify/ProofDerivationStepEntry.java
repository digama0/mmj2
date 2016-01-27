//********************************************************************/
//* Copyright (C) 2006  MEL O'CAT  mmj2 (via) planetmath (dot) org   */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofDerivationStepEntry.java 0.03 11/01/2007
 *
 * 16-Jan-2006 Created for interface between
 *             mmj.verify.VerifyProofs.getProofDerivationSteps()
 *                 and
 *             mmj.pa.ProofAsst.exportToFile().
 *
 * 06-Sep-2006 Added formulaParseTree for use in TMFF.
 *
 * Nov-01-2007 Version 0.06
 * - add proofLevel for TMFF useIndent
 * - add static computeProofLevels() method to compute all
 *   proofLevels for an ArrayList (proof) of
 *   ProofDerivationStepEntry.
 */

package mmj.verify;

import java.util.List;

import mmj.lang.Formula;
import mmj.lang.ParseTree;

/**
 * Proof Derivation Step Entry is a simple data structure used to hold a
 * non-syntax axiom assertion proof step (i.e. a derivation step.)
 * <p>
 * We use mmj.verify.VerifyProofs to generate the ProofDerivationStepEntry
 * objects for a theorem because VerifyProofs already has the complete mechanism
 * for dealing with a Metamath proof and generating the formula resulting from
 * each derivation step.
 */
public class ProofDerivationStepEntry {

    /**
     * isHyp indicates if this step is one of the Theorem's logical hypothesis
     * steps.
     */
    public boolean isHyp;

    /**
     * stepNbr is an integer ranging from 1 through n - 1, where 'n' is the last
     * derivation step of the proof -- and the final step number is "qed".
     */
    public String step;

    /**
     * hypStepNbr contains the stepNbr's of previous steps that are to be used
     * as hypotheses for the Ref Assrt in the current step. There may be 0, 1,
     * ... n hyps.
     */
    public String[] hypStep;

    /**
     * refLabel is the label of the Assrt used to justify the step, and which
     * creates the derivation of the step's formula.
     * <p>
     * refLabel is always present on hypothesis steps because a Theorem's proof
     * may not refer to the hypotheses in database sequence and so there could
     * be ambiguity about which step is referring to which hypothesis.
     */
    public String refLabel;

    /**
     * formula is the formula resulting from the derivation using the Ref and
     * its hypotheses. Except for the hypothesis steps and the final step, the
     * formulas are intermediate results that are not seen -- except here and in
     * a Proof Assistant ProofWorksheet, which is what the
     * ProofDerivationStepEntry will be used to generate.
     */
    public Formula formula;

    /**
     * formulaParseTree is here for use in TMFF, Text Mode Formula Formatting.
     * <p>
     * formulaParseTree of the formula, or null if either the parse failed or
     * has not yet been attempted (VerifyProofs.getProofDerivationSteps() and
     * VerifyProofs.loadProofDerivStepList() will load the parse tree for the
     * theorem's formula and its logical hypotheses, but the proof step parse
     * trees are not available there -- so ProofWorksheet will fill in the
     * missing parses.)
     */
    public ParseTree formulaParseTree;

    /**
     * Level number of the step within the proof where the qed step has level 0
     * and each hypothesis is one level higher.
     */
    public int proofLevel;

    public boolean isAutoStep;

    @Override
    public String toString() {
        String s = (isHyp ? "h" : "") + step + ":";
        String delim = "";
        for (final String hyp : hypStep) {
            s += delim + hyp;
            delim = ",";
        }
        return s + ":" + refLabel + " " + formula;
    }

    /**
     * Compute proof level numbers for a proof stored in a
     * ProofDerivationStepEntry ArrayList.
     * 
     * @param pList ProofDerivationStepEntry ArrayList
     */
    public static void computeProofLevels(
        final List<ProofDerivationStepEntry> pList)
    {

        ProofDerivationStepEntry d;
        ProofDerivationStepEntry h;
        Object o;

        try { // very defensive programming against possible future
              // code changes elsewhere...approaching farce :-)

            for (int i = pList.size() - 1; i > 0; i--) {

                d = pList.get(i);

                for (final String element : d.hypStep) {

                    o = pList.get(Integer.parseInt(element) - 1);
                    if (o != null) {

                        h = (ProofDerivationStepEntry)o;

                        if (h.step.equals(element)) {

                            if (h.proofLevel == 0)
                                h.proofLevel = d.proofLevel + 1;

                            continue;
                        }
                    }
                    throw new IllegalArgumentException("");
                }
            }
        } catch (final Exception e) {
            computeProofLevelsSlowly(pList);
        }
    }

    /**
     * Compute proof level numbers for a proof stored in a
     * ProofDerivationStepEntry ArrayList.
     * <p>
     * This routine is coded because there may be incomplete or invalid proofs
     * that the normal routine cannot handle...and yet, 99.99% of the time we
     * are dealing with complete, valid proofs so it is deemed preferable to try
     * to do the computation the quick way first and see what happens.
     * 
     * @param pList ProofDerivationStepEntry ArrayList
     */
    public static void computeProofLevelsSlowly(
        final List<ProofDerivationStepEntry> pList)
    {

        ProofDerivationStepEntry d;
        ProofDerivationStepEntry h;

        for (int i = 0; i < pList.size(); i++)
            pList.get(i).proofLevel = 0;

        for (int i = pList.size() - 1; i > 0; i--) {
            d = pList.get(i);

            loopJ: for (int j = 0; j < d.hypStep.length; j++)
                for (int k = i - 1; k >= 0; k--) {

                    h = pList.get(k);

                    if (d.hypStep[j].equals(h.step)) {

                        if (h.proofLevel == 0)
                            h.proofLevel = d.proofLevel + 1;

                        continue loopJ;
                    }
                }
        }
    }
}
