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
import java.util.Map.Entry;

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
     * otherHyp contains the VarHyp entries inside the parenthesized portion of
     * a compressed proof (they occur before the Assrt entries in the
     * parentheses.) There may be zero otherHyp entries.
     */
    private List<VarHyp> otherHyp;

    /**
     * otherAssrt contains the Assrt entries inside the parenthesized portion of
     * a compressed proof (they occur after the VarHyp entries in the
     * parentheses.) There may be zero otherAssrt entries.
     */
    private List<Assrt> otherAssrt;

    /**
     * step contains the output list of RPN steps from the compressed proof
     */
    private List<RPNStep> step;

    /** Messages object for error reporting */
    private Messages messages;

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
     * @param messages for error reporting
     * @return RPNStep array containing decompressed (but still "packed")
     *         Metamath RPN proof.
     * @throws LangException if an error occurred
     */
    public RPNStep[] decompress(final String theoremLabel, final int seq,
        final Map<String, Stmt> stmtTbl, final Hyp[] mandHypArray,
        final Hyp[] optHypArray, final List<String> otherRefList,
        final BlockList proofBlockList, final Messages messages)
        throws LangException
    {
        this.theoremLabel = theoremLabel; // for error msgs

        if (!usedYet) {
            initArrays();
            usedYet = true;
        }

        mandHyp = mandHypArray;
        optHyp = optHypArray;
        this.messages = messages;

        loadOtherRefArrays(stmtTbl, otherRefList, seq);

        loadSteps(proofBlockList);

        return constructProofArray();
    }

    private void loadOtherRefArrays(final Map<String, Stmt> stmtTbl,
        final List<String> otherRefList, final int seq) throws LangException
    {

        otherHyp.clear();
        otherAssrt.clear();

        int iterationNbr = 0;
        for (final String otherLabel : otherRefList) {
            iterationNbr++;
            final Stmt otherStmt = stmtTbl.get(otherLabel);
            if (otherStmt == null)
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_NOTFND, theoremLabel,
                    iterationNbr, otherLabel);
            if (otherStmt.getSeq() >= seq)
                throw new LangException(
                    LangConstants.ERRMSG_FORWARD_PROOF_STEP_LABEL + otherLabel);

            if (otherStmt instanceof Assrt) {
                otherAssrt.add((Assrt)otherStmt);
                continue;
            }

            if (!(otherStmt instanceof VarHyp))
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_BOGUS, theoremLabel,
                    iterationNbr, otherLabel);

            if (isProofStepInFrame(otherStmt, mandHyp))
                messages.accumInfoMessage(
                    LangConstants.ERRMSG_COMPRESS_OTHER_MAND, theoremLabel,
                    iterationNbr, otherLabel);

            /**
             * this is a little "tricky" -- "active" applies only to global
             * hypotheses or when the source file is being loaded.
             */
            if (!otherStmt.isActive() && !isProofStepInExtendedFrame(otherStmt))
                throw new LangException(
                    LangConstants.ERRMSG_PROOF_STEP_HYP_INACTIVE, otherLabel);

            loadOtherHyp((VarHyp)otherStmt, iterationNbr);
            continue;
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

    private void loadOtherHyp(final VarHyp otherVarHyp, final int iterationNbr)
        throws LangException
    {

        if (otherAssrt.size() > 0)
            messages.accumInfoMessage(
                LangConstants.ERRMSG_COMPRESS_OTHER_VARHYP_POS, theoremLabel,
                iterationNbr, otherVarHyp.getLabel());

        otherHyp.add(otherVarHyp);
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
             * mandHyp.length + 1 thru otherHypCnt = otherHyp
             * otherHypCnt + 1 thru otherAssrtCnt = otherAssrt
             * otherAssrtCnt + 1 thru repeatedSubproofCnt
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

            // ok, do we have a otherHyp array entry?
            // if so, just put it in the proof step array!
            if (decompressNbr < otherHyp.size()) {
                final RPNStep s = new RPNStep(otherHyp.get(decompressNbr));
                if (blockList.marked)
                    s.backRef = -++backrefs;
                step.add(s);
                continue;
            }

            // ok, adjust workNbr down into otherAssrt range
            decompressNbr -= otherHyp.size();

            // ok, do we have a otherAssrt array entry?
            // if so, put it in the proof step array, but
            // this time we need to compute subproofLength!
            // an Assrt has Hyps in its subproof tree/stack.
            if (decompressNbr < otherAssrt.size()) {
                final RPNStep s = new RPNStep(otherAssrt.get(decompressNbr));
                if (blockList.marked)
                    s.backRef = -++backrefs;
                step.add(s);
                continue;
            }

            decompressNbr -= otherAssrt.size();

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
        otherHyp = new ArrayList<VarHyp>(
            LangConstants.COMPRESS_OTHER_HYP_INIT_LEN);
        otherAssrt = new ArrayList<Assrt>(
            LangConstants.COMPRESS_OTHER_ASSRT_INIT_LEN);
        step = new ArrayList<RPNStep>(LangConstants.COMPRESS_STEP_INIT_LEN);
    }

    public List<Stmt> compress(final String theoremLabel, final int width,
        final List<Hyp> mandHypArray, final List<VarHyp> optHypArray,
        final RPNStep[] rpnProof, final StringBuilder letters)
    {
        this.theoremLabel = theoremLabel;
        final List<Stmt> parenStmt = new ArrayList<Stmt>();
        int x = 2;
        for (final RPNStep s : rpnProof)
            if (s != null && s.stmt != null && optHypArray.contains(s.stmt)
                && !parenStmt.contains(s.stmt))
            {
                parenStmt.add(s.stmt);
                final int l = s.stmt.getLabel().length() + 1;
                if (x + l > width)
                    x = l;
                else
                    x += l;
            }
        final Map<Stmt, Integer> unsortedMap = new HashMap<Stmt, Integer>();
        for (final RPNStep s : rpnProof)
            if (s != null && s.backRef <= 0 && s.stmt != null
                && !mandHypArray.contains(s.stmt)
                && !parenStmt.contains(s.stmt))
            {
                final Integer i = unsortedMap.get(s.stmt);
                if (i == null)
                    unsortedMap.put(s.stmt, 0);
                else
                    unsortedMap.put(s.stmt, i + 1);
            }
        final PriorityQueue<Entry<Stmt, Integer>> sortedByBackrefs = new PriorityQueue<Entry<Stmt, Integer>>(
            unsortedMap.size(), new Comparator<Entry<Stmt, Integer>>() {
                public int compare(final Entry<Stmt, Integer> e1,
                    final Entry<Stmt, Integer> e2)
                {
                    return e2.getValue() - e1.getValue();
                }
            });
        sortedByBackrefs.addAll(unsortedMap.entrySet());
        int i = mandHypArray.size() + parenStmt.size();
        int cutoff = LangConstants.COMPRESS_LOW_BASE;
        while (cutoff <= i)
            cutoff *= LangConstants.COMPRESS_HIGH_BASE;
        int d = 0;
        final List<List<Stmt>> sortedByLength = new ArrayList<List<Stmt>>();
        sortedByLength.add(new LinkedList<Stmt>());
        Entry<Stmt, Integer> e;
        while ((e = sortedByBackrefs.poll()) != null) {
            if (i++ == cutoff) {
                cutoff *= LangConstants.COMPRESS_HIGH_BASE;
                d++;
                sortedByLength.add(new LinkedList<Stmt>());
            }
            sortedByLength.get(d).add(e.getKey());
        }
        for (final List<Stmt> list : sortedByLength) {
            Collections.sort(list, new Comparator<Stmt>() {
                public int compare(final Stmt s1, final Stmt s2) {
                    return s2.getLabel().length() - s1.getLabel().length();
                }
            });
            while (!list.isEmpty()) {
                boolean found = false;
                if (x + 2 < width) // assume minimum size 2 for label
                    for (final Stmt s : list) {
                        final int l = s.getLabel().length() + 1;
                        if (x + l <= width) {
                            found = true;
                            x += l;
                            list.remove(s);
                            parenStmt.add(s);
                            break;
                        }
                    }
                if (!found) {
                    final Stmt s = list.remove(0);
                    final int l = s.getLabel().length() + 1;
                    if (x + l >= width)
                        x = l;
                    else
                        x += l;
                    parenStmt.add(s);
                }
            }
        }
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
                code = (char)LangConstants.COMPRESS_HIGH_DIGIT_CHARS[(letter - 1)
                    % LangConstants.COMPRESS_HIGH_BASE]
                    + code;
                letter = (letter - 1) / LangConstants.COMPRESS_HIGH_BASE;
            }
            if (s.backRef < 0)
                code += (char)LangConstants.COMPRESS_REPEAT_CHAR;
            letters.append(code);
        }
        return parenStmt;
    }
}
