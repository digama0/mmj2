//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * ProofCompression.java 0.01 04/01/2006
 */

package mmj.lang;

import java.util.*;

import mmj.lang.ParseTree.RPNStep;
import mmj.mmio.BlockList;

/**
 * ProofCompression provides Compression and Decompression Services for Metamath
 * proofs as described in Metamath(dot)pdf at metamath(dot)org.
 */
public class ProofCompression {

    private boolean usedYet;

    // *******************************************
    // all following variables are work items used
    // within a single execution but are stored
    // globally to eliminate call parameters.
    // *******************************************

    private String theoremLabel; // for error msgs

    /**
     * mandHyp contains the Theorem's MandFrame.hypArray
     */
    private Hyp[] mandHyp;

    /**
     * optHyp contains the Theorem's OptFrame.optHypArray
     */
    private Hyp[] optHyp;

    /**
     * otherStmt contains the VarHyp and Assrt entries inside the parenthesized
     * portion of a compressed proof. There may be zero otherStmt entries.
     */
    private List<Stmt> otherStmt;

    /**
     * step contains the output list of RPN steps from the compressed proof
     */
    private List<RPNStep> step;

    // *******************************************

    /**
     * Constructor - default.
     */
    public ProofCompression() {

        // don't allocate the stacks until activity requested.
    }

    /**
     * Decompress a single proof.
     *
     * @param theoremLabel Theorem's label, used in error messages that may be
     *            generated during processing.
     * @param seq the sequence number of the theorem
     * @param stmtTbl Stmt lookup map for translating labels into Stmt object
     *            references.
     * @param mandHypArray The theorem's MandFrame.hypArray.
     * @param optHypArray The theorem's OptFrame.optHypArray.
     * @param otherRefList List of String containing labels of Stmt's provided
     *            in the parenthesized portion of a compressed proof.
     * @param proofBlockList List of String containing the compressed portion of
     *            the proof.
     * @return RPNStep array containing decompressed (but still "packed")
     *         Metamath RPN proof.
     * @throws LangException if an error occurred
     */
    public RPNStep[] decompress(final String theoremLabel, final int seq,
        final Map<String, Stmt> stmtTbl, final Hyp[] mandHypArray,
        final Hyp[] optHypArray, final List<String> otherRefList,
        final BlockList proofBlockList) throws LangException
    {
        this.theoremLabel = theoremLabel; // for error msgs

        if (!usedYet) {
            initArrays();
            usedYet = true;
        }

        mandHyp = mandHypArray;
        optHyp = optHypArray;

        loadOtherRefArrays(stmtTbl, otherRefList, seq);

        loadSteps(proofBlockList);

        return constructProofArray();
    }

    private void loadOtherRefArrays(final Map<String, Stmt> stmtTbl,
        final List<String> otherRefList, final int seq) throws LangException
    {

        otherStmt.clear();

        int iterationNbr = 0;
        for (final String otherLabel : otherRefList) {
            iterationNbr++;
            final Stmt other = stmtTbl.get(otherLabel);
            if (other == null)
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_NOTFND, theoremLabel,
                    iterationNbr, otherLabel);
            if (other.getSeq() >= seq)
                throw new LangException(
                    LangConstants.ERRMSG_FORWARD_PROOF_STEP_LABEL, otherLabel);

            if (isProofStepInFrame(other, mandHyp))
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_MAND, theoremLabel,
                    iterationNbr, otherLabel);

            /**
             * this is a little "tricky" -- "active" applies only to global
             * hypotheses or when the source file is being loaded.
             */
            if (!other.isActive() && !isProofStepInExtendedFrame(other))
                throw new LangException(
                    LangConstants.ERRMSG_PROOF_STEP_HYP_INACTIVE, otherLabel);

            otherStmt.add(other);
        }
    }

    /**
     * Checks to see whether or not a proof step is contained in the given
     * Frame.
     *
     * @param proofStep a Statement reference.
     * @param frame the frame to check
     * @return true if proof step == a Hyp in the frame
     */
    public boolean isProofStepInFrame(final Stmt proofStep, final Hyp[] frame) {
        for (final Hyp element : frame)
            if (proofStep == element)
                return true;
        return false;
    }

    /**
     * Checks to see whether or not a proof step is contained in the Theorem's
     * Extended Frame.
     * <p>
     * Cloned this from mmj.lang.Theorem.java.
     * <p>
     * First checks to see if the proof step is in the MandFrame's hypArray. If
     * not it checks the OptFrame's hypArray
     *
     * @param proofStep a Statement reference.
     * @return true if proof step == a Hyp in either the MandFrame or OptFrame
     *         of the Theorem.
     */
    public boolean isProofStepInExtendedFrame(final Stmt proofStep) {
        return isProofStepInFrame(proofStep, mandHyp)
            || isProofStepInFrame(proofStep, optHyp);
    }

    private void loadSteps(final BlockList blockList) throws LangException {
        step.clear();

        int backrefs = 0;
        while (true) {
            int decompressNbr = blockList.getNext(theoremLabel);

            /*
             * We have decompressed a number! But what does the number signify?
             *
             * -1                                 = EOF
             * 0                                  = unknown '?' step
             * 1 thru mandHyp.length              = mandHyp
             * mandHyp.length + 1 thru otherStmtCnt = otherStmt
             * otherStmtCnt + 1 thru repeatedSubproofCnt
             *                                    = repeatedSubproof
             * otherSubproofCnt + 1 and beyond    = error! bogus!@#
             *
             * The above mentioned arrays (mandHyp, otherHyp, etc.)
             * begin indexing at 0, so we need to subtract 1
             * from "decompressNbr" to index into the arrays.
             */

            if (decompressNbr < 0)
                break; // run out of chars
            else if (decompressNbr == 0) { // unknown (?) step
                final RPNStep s = new RPNStep(null);
                if (blockList.marked)
                    s.backRef = -++backrefs;
                step.add(s);
                continue;
            }

            decompressNbr--;

            // ok, do we have a mandHyp array entry?
            // if so, just put it in the proof step array!
            if (decompressNbr < mandHyp.length) {
                final RPNStep s = new RPNStep(mandHyp[decompressNbr]);
                if (blockList.marked)
                    s.backRef = -++backrefs;
                step.add(s);
                continue;
            }

            // ok, adjust workNbr down into otherHyp range
            decompressNbr -= mandHyp.length;

            // ok, do we have a otherStmt array entry?
            // if so, put it in the proof step array, but
            // this time we need to compute subproofLength!
            // an Assrt has Hyps in its subproof tree/stack.
            if (decompressNbr < otherStmt.size()) {
                final RPNStep s = new RPNStep(otherStmt.get(decompressNbr));
                if (blockList.marked)
                    s.backRef = -++backrefs;
                step.add(s);
                continue;
            }

            decompressNbr -= otherStmt.size();

            if (decompressNbr >= backrefs)
                throw new LangException(LangConstants.ERRMSG_COMPRESS_BAD_RPT3,
                    theoremLabel, blockList.getIndex());

            final RPNStep s = new RPNStep(null);
            s.backRef = decompressNbr + 1;
            step.add(s);
        }
    }
    private RPNStep[] constructProofArray() {
        return step.toArray(new RPNStep[step.size()]);
    }

    private void initArrays() {
        otherStmt = new ArrayList<>(LangConstants.COMPRESS_OTHER_STMT_INIT_LEN);
        step = new ArrayList<>(LangConstants.COMPRESS_STEP_INIT_LEN);
    }

    public List<Stmt> compress(final String theoremLabel, final int width,
        final List<Hyp> mandHypArray, final List<VarHyp> optHypArray,
        final RPNStep[] rpnProof, final StringBuilder letters)
    {
        this.theoremLabel = theoremLabel;
        final List<Stmt> parenStmt = new ArrayList<>();
        int linePos = 2;
        final List<Stmt> proofOrdered = new ArrayList<>();
        final List<Integer> proofOrdBackrefs = new ArrayList<>();
        for (final RPNStep s : rpnProof)
            if (s != null && s.backRef <= 0 && s.stmt != null
                && !mandHypArray.contains(s.stmt))
            {
                final int i = proofOrdered.indexOf(s.stmt);
                if (i >= 0)
                    proofOrdBackrefs.set(i, proofOrdBackrefs.get(i) + 1);
                else {
                    proofOrdered.add(s.stmt);
                    proofOrdBackrefs.add(1);
                }
            }
        int hyps = 0;
        for (int i = 0; i < proofOrdered.size(); i++)
            if (proofOrdered.get(i) instanceof Hyp) {
                proofOrdered.add(hyps, proofOrdered.remove(i));
                proofOrdBackrefs.add(hyps++, proofOrdBackrefs.remove(i));
            }
        final int[] values = new int[proofOrdered.size()];
        for (int i = 0; i < values.length; i++)
            values[i] = proofOrdered.get(i).getLabel().length() + 1;
        final PriorityQueue<Integer> sortedByBackrefs = new PriorityQueue<>(
            proofOrdered.size(), new Comparator<Integer>()
        {
                public int compare(final Integer a, final Integer b) {
                    final int i = proofOrdBackrefs.get(b)
                        - proofOrdBackrefs.get(a);
                    return i == 0 ? a - b : i;
                }
            });
        for (int i = 0; i < proofOrdered.size(); i++)
            sortedByBackrefs.add(i);
        int i = mandHypArray.size();
        int cutoff = LangConstants.COMPRESS_LOW_BASE;
        while (cutoff <= i) {
            i -= cutoff;
            cutoff *= LangConstants.COMPRESS_HIGH_BASE;
        }
        Integer pos;
        final List<Integer> lengthBlock = new LinkedList<>();
        while ((pos = sortedByBackrefs.poll()) != null) {
            if (i++ == cutoff) {
                i = 1;
                cutoff *= LangConstants.COMPRESS_HIGH_BASE;
                linePos = processBlock(parenStmt, proofOrdered, values,
                    lengthBlock, width, linePos);
            }
            lengthBlock.add(pos);
        }
        processBlock(parenStmt, proofOrdered, values, lengthBlock, width,
            linePos);
        for (final RPNStep s : rpnProof) {
            if (s == null) {
                letters.append((char)LangConstants.COMPRESS_UNKNOWN_CHAR);
                continue;
            }
            int letter = 0;
            if (s.backRef > 0)
                letter = mandHypArray.size() + parenStmt.size() + s.backRef;
            else {
                int index = mandHypArray.indexOf(s.stmt);
                if (index != -1)
                    letter = index + 1;
                else {
                    index = parenStmt.indexOf(s.stmt);
                    if (index == -1)
                        throw new RuntimeException("shouldn't happen");
                    letter = mandHypArray.size() + index + 1;
                }
            }
            String code = ""
                + (char)LangConstants.COMPRESS_LOW_DIGIT_CHARS[(letter - 1)
                    % LangConstants.COMPRESS_LOW_BASE];
            letter = (letter - 1) / LangConstants.COMPRESS_LOW_BASE;
            while (letter > 0) {
                code = (char)LangConstants.COMPRESS_HIGH_DIGIT_CHARS[(letter
                    - 1) % LangConstants.COMPRESS_HIGH_BASE] + code;
                letter = (letter - 1) / LangConstants.COMPRESS_HIGH_BASE;
            }
            if (s.backRef < 0)
                code += (char)LangConstants.COMPRESS_REPEAT_CHAR;
            letters.append(code);
        }
        return parenStmt;
    }

    private int processBlock(final List<Stmt> parenStmt,
        final List<Stmt> proofOrdered, final int[] values,
        final List<Integer> list, final int width, int linePos)
    {
        Collections.sort(list); // restart with proof order
        while (!list.isEmpty()) {
            boolean noSpace = true;
            for (final Integer p : knapsackFit(list, values, width - linePos)) {
                noSpace = false;
                final Stmt s = proofOrdered.get(p);
                final int l = s.getLabel().length() + 1;
                linePos += l;
                list.remove(p);
                parenStmt.add(s);
            }
            if (noSpace || linePos >= width - 1)
                linePos = 0;
        }
        return linePos;
    }

    private Deque<Integer> knapsackFit(final List<Integer> items,
        final int[] values, final int size)
    {
        final int[][] worth = new int[items.size() + 1][size + 1];
        for (int i = 0; i < items.size(); i++) {
            final int value = values[items.get(i)];
            for (int s = 0; s <= size; s++)
                worth[i + 1][s] = s >= value
                    ? Math.max(worth[i][s], value + worth[i][s - value])
                    : worth[i][s];
        }
        final Deque<Integer> included = new ArrayDeque<>();
        int s = size;
        for (int i = items.size() - 1; i >= 0; i--)
            if (worth[i + 1][s] != worth[i][s]) {
                included.push(items.get(i));
                if ((s -= values[items.get(i)]) == 0)
                    break;
            }
        return included;
    }
}
