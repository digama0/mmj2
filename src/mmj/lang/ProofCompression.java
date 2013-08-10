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

    /*
     * mandHyp contains the Theorem's MandFrame.hypArray
     */
    private Hyp[] mandHyp;

    /*
     * optHyp contains the Theorem's OptFrame.optHypArray
     */
    private Hyp[] optHyp;

    /*
     * otherHyp contains the VarHyp entries inside
     * the parenthesized portion of a compressed
     * proof (they occur before the Assrt entries in
     * the parentheses.) There may be zero
     * otherHyp entries.
     */
    private int otherHypCnt;
    private int otherHypMax;
    private VarHyp[] otherHyp;

    /*
     * otherAssrt contains the Assrt entries inside
     * the parenthesized portion of a compressed
     * proof (they occur after the VarHyp entries in
     * the parentheses.) There may be zero
     * otherAssrt entries.
     */
    private int otherAssrtCnt;
    private int otherAssrtMax;
    private Assrt[] otherAssrt;

    /**
     * repeatedSubproof is an array of indexes pointing to a step[i], which is
     * the last step in a subproof that is repeated later in the proof. The
     * start index of the subproof is computed as:
     * 
     * <pre>
     * startIndex = i - subproofLength[i] + 1.
     * </pre>
     */
    private int[] repeatedSubproof;
    private int repeatedSubproofCnt;
    private int repeatedSubproofMax;

    /**
     * step and subproofLength are parallel arrays, where subproofLength[i]
     * contains the length of the subproof at step[i].
     * <p>
     * step[i] may be null, which is the case when the input proof step = "?".
     * <p>
     * There must always be at least one proof step, even if it is just a "?".
     */
    private Stmt[] step;
    private int stepCnt;
    private int stepMax;

    /**
     * subproofLength and step are parallel arrays, where subproofLength[i]
     * contains the length of the subproof at step[i].
     * <p>
     * If {@code step[i] == null}, subproofLength[i] = 1.
     * <p>
     * When {@code step[i].isHyp()}, subproofLength[i] = 1.
     * <p>
     * When {@code step[i].isAssrt()}, subproofLength[i] = 1 plus the sum of
     * subproofLength for n prior subproofLength entries, where
     * 
     * <pre>
     * n = ((Assrt)step[i]).getMandFrame().hypArray.length
     * </pre>
     * 
     * [that is the source data, but is actually computed using other code...].
     * <p>
     * The prior subproofLength entry indexes, "P", are determined working
     * backwards:
     * 
     * <pre>
     *         P(n)     = i - 1
     *                  = subproofLength for nth
     *                    mand hyp of step[i]
     * 
     *         P(n - 1) = P(n) - subproofLength[P(n)]
     *                  =  subproofLength for (n - 1)th
     *                     mand hyp of step[i]
     * 
     *         P(n - 2) = P(n - 1) - subproofLength[P(n - 1)]
     *                  =  subproofLength for (n - 2)th
     *                     mand hyp of step[i]
     * 
     *         ...etc.
     * </pre>
     * 
     * There must always be at least one proof step, even if it is just a "?".
     */
    private int[] subproofLength; // is parallel array!

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
     * @return Stmt array containing decompressed Metamath RPN proof.
     * @throws LangException if an error occurred
     */
    public Stmt[] decompress(final String theoremLabel, final int seq,
        final Map<String, Stmt> stmtTbl, final Hyp[] mandHypArray,
        final Hyp[] optHypArray, final List<String> otherRefList,
        final List<String> proofBlockList) throws LangException
    {
        this.theoremLabel = theoremLabel; // for error msgs

        if (!usedYet) {
            initArrays();
            usedYet = true;
        }

        mandHyp = mandHypArray;
        optHyp = optHypArray;

        loadOtherRefArrays(stmtTbl, otherRefList, seq);

        final Iterator<String> blockIterator = proofBlockList.iterator();
        if (blockIterator.hasNext())
            loadSteps(blockIterator);
        else
            throw new LangException(
                LangConstants.ERRMSG_COMPRESS_NO_PROOF_BLOCKS, theoremLabel);

        return constructProofArray();
    }

    private void loadOtherRefArrays(final Map<String, Stmt> stmtTbl,
        final List<String> otherRefList, final int seq) throws LangException
    {

        otherHypCnt = 0;
        otherAssrtCnt = 0;

        if (otherRefList.size() > otherAssrtMax) {
            otherAssrtMax = otherRefList.size() * 2;
            otherAssrt = new Assrt[otherAssrtMax];
        }

        int iterationNbr = 0;
        for (final String otherLabel : otherRefList) {
            ++iterationNbr;
            final Stmt otherStmt = stmtTbl.get(otherLabel);
            if (otherStmt == null)
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_NOTFND, theoremLabel,
                    iterationNbr, otherLabel);
            if (otherStmt.getSeq() >= seq)
                throw new LangException(
                    LangConstants.ERRMSG_FORWARD_PROOF_STEP_LABEL + otherLabel);

            if (otherStmt.isAssrt()) {
                otherAssrt[otherAssrtCnt++] = (Assrt)otherStmt;
                continue;
            }

            if (!otherStmt.isVarHyp())
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_BOGUS, theoremLabel,
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
        for (final Hyp element : mandHyp)
            if (proofStep == element)
                return true;
        for (final Hyp element : optHyp)
            if (proofStep == element)
                return true;
        return false;
    }

    private void loadOtherHyp(final VarHyp otherVarHyp, final int iterationNbr)
        throws LangException
    {

        if (otherAssrtCnt > 0)
            throw new LangException(
                LangConstants.ERRMSG_COMPRESS_OTHER_VARHYP_POS, theoremLabel,
                iterationNbr, otherVarHyp.getLabel());

        if (otherHypCnt >= otherHypMax)
            reallocAndCopyOtherHypArray();

        otherHyp[otherHypCnt++] = otherVarHyp;
    }

    private void loadSteps(final Iterator<String> blockIterator)
        throws LangException
    {
        repeatedSubproofCnt = 0;
        stepCnt = 0;

        String block = blockIterator.next();
        int blockLen = block.length();
        int blockNbr = 1;
        int charIndex = 0;

        int decompressNbr = 0;
        int prevDecompressNbr = 0;
        int workNbr;

        char nextChar;
        byte nextCharCode;

        int nbrOfPos; // these are for computing subproofLength
        int prevPos;
        int currPos;

        while (true) {
            if (charIndex >= blockLen) {
                if (!blockIterator.hasNext()) {
                    if (decompressNbr > 0)
                        throw new LangException(
                            LangConstants.ERRMSG_COMPRESS_PREMATURE_END,
                            theoremLabel);
                    break;
                }
                charIndex = 0;
                block = blockIterator.next();
                blockLen = block.length();
                ++blockNbr;
            }

            nextChar = block.charAt(charIndex++);
            if (nextChar >= LangConstants.COMPRESS_VALID_CHARS.length)
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_NOT_ASCII, theoremLabel,
                    blockNbr, charIndex, nextChar);

            // translate 'A' to 0, 'B' to 1, etc. (1 is added to
            // 'A' thru 'Z' later -- curiously but effectively :)
            nextCharCode = LangConstants.COMPRESS_VALID_CHARS[nextChar];

            if (nextCharCode == LangConstants.COMPRESS_ERROR_CHAR_VALUE)
                throw new LangException(LangConstants.ERRMSG_COMPRESS_BAD_CHAR,
                    theoremLabel, blockNbr, charIndex, nextChar);

            if (nextCharCode == LangConstants.COMPRESS_UNKNOWN_CHAR_VALUE) {
                if (decompressNbr > 0)
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_UNK, theoremLabel,
                        blockNbr, charIndex);

                if (stepCnt >= stepMax)
                    reallocAndCopyStepArray();
                step[stepCnt] = null;
                subproofLength[stepCnt] = 1;
                ++stepCnt;
                prevDecompressNbr = 0;
                decompressNbr = 0;
                continue;
            }

            if (nextCharCode == LangConstants.COMPRESS_REPEAT_CHAR_VALUE) {

//              System.out.println("nextCharCode == repeat char value"
//              + " Theorem=" + theoremLabel
//              + " nextCharCode=" + nextCharCode
//              + " decompressNbr=" + decompressNbr);

                if (decompressNbr > 0)
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_RPT, theoremLabel,
                        blockNbr, charIndex);
                if (prevDecompressNbr == 0)
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_RPT2, theoremLabel,
                        blockNbr, charIndex);
                if (repeatedSubproofCnt >= repeatedSubproofMax)
                    reallocAndCopyRepeatedSubproofArray();
                repeatedSubproof[repeatedSubproofCnt] = stepCnt - 1;
                ++repeatedSubproofCnt;
                prevDecompressNbr = 0;
                decompressNbr = 0;
                continue;
            }

            if (nextCharCode >= LangConstants.COMPRESS_LOW_BASE) {

                decompressNbr = decompressNbr
                    * LangConstants.COMPRESS_HIGH_BASE + nextCharCode;

//              System.out.println("nextCharCode >= low base"
//              + " Theorem=" + theoremLabel
//              + " nextCharCode=" + nextCharCode
//              + " decompressNbr=" + decompressNbr);

                continue;
            }

            // else...
            decompressNbr += nextCharCode + 1; // 'A' = 1 etc

            /*
             * Whew! finally...we have decompressed a number!
             * But what does the number signify?
             *
             * 1 thru mandHyp.length              = mandHyp
             * mandHyp.length + 1 thru otherHypCnt = otherHyp
             * otherHypCnt + 1 thru otherAssrtCnt = otherAssrt
             * otherAssrtCnt + 1 thru repeatedSubproofCnt
             *                                    = repeatedSubproof
             * otherSubproofCnt + 1 and beyond    = error! bogus!@#
             *
             * The above mentioned arrays (mandHyp, otherHyp, etc.)
             * begin indexing at 0, so we need to subtract 1
             * from "decompressNbr" to index into the arrays,
             * but "decompressNbr = 0" has special significance --
             * it means "no previous decompression in progress",
             * so we'll use "workNbr" to mess around with.
             */

            workNbr = decompressNbr - 1;

            // ok, do we have a mandHyp array entry?
            // if so, just put it in the proof step array!
            if (workNbr < mandHyp.length) {
                if (stepCnt >= stepMax)
                    reallocAndCopyStepArray();
                step[stepCnt] = mandHyp[workNbr];
                subproofLength[stepCnt] = 1;
                ++stepCnt;
                prevDecompressNbr = decompressNbr;
                decompressNbr = 0;
                continue;
            }

            // ok, adjust workNbr down into otherHyp range
            workNbr -= mandHyp.length;

            // ok, do we have a otherHyp array entry?
            // if so, just put it in the proof step array!
            if (workNbr < otherHypCnt) {
                if (stepCnt >= stepMax)
                    reallocAndCopyStepArray();
                step[stepCnt] = otherHyp[workNbr];
                subproofLength[stepCnt] = 1;
                ++stepCnt;
                prevDecompressNbr = decompressNbr;
                decompressNbr = 0;
                continue;
            }

            // ok, adjust workNbr down into otherAssrt range
            workNbr -= otherHypCnt;

            // ok, do we have a otherAssrt array entry?
            // if so, put it in the proof step array, but
            // this time we need to compute subproofLength!
            // an Assrt has Hyps in its subproof tree/stack.
            if (workNbr < otherAssrtCnt) {

                if (stepCnt >= stepMax)
                    reallocAndCopyStepArray();
                step[stepCnt] = otherAssrt[workNbr];
                subproofLength[stepCnt] = 1; // init to 1, then add hyp lens

                nbrOfPos = otherAssrt[workNbr].getMandHypArrayLength();
                prevPos = stepCnt;

                while (nbrOfPos-- > 0) {
                    currPos = prevPos - subproofLength[prevPos];

                    if (currPos < 0 || currPos > stepCnt)
                        throw new LangException(
                            LangConstants.ERRMSG_COMPRESS_CORRUPT,
                            theoremLabel, blockNbr, charIndex);

                    subproofLength[stepCnt] += subproofLength[currPos];
                    prevPos = currPos;
                }

                ++stepCnt;
                prevDecompressNbr = decompressNbr;
                decompressNbr = 0;
                continue;
            }

            workNbr -= otherAssrtCnt;

            if (workNbr >= repeatedSubproofCnt)
                throw new LangException(LangConstants.ERRMSG_COMPRESS_BAD_RPT3,
                    theoremLabel, blockNbr, charIndex);

            final int endRepeat = repeatedSubproof[workNbr];
            int startRepeat = endRepeat + 1 - subproofLength[endRepeat];

            if (stepCnt + subproofLength[endRepeat] >= stepMax)
                reallocAndCopyStepArray();

            while (startRepeat <= endRepeat) {
                step[stepCnt] = step[startRepeat];
                subproofLength[stepCnt] = subproofLength[startRepeat];
                ++startRepeat;
                ++stepCnt;
            }

            prevDecompressNbr = 0;
            decompressNbr = 0;
            continue;
        }
    }

    private Stmt[] constructProofArray() {
        final Stmt[] proofArray = new Stmt[stepCnt];
        for (int i = 0; i < stepCnt; i++)
            proofArray[i] = step[i];
        return proofArray;
    }

    private void initArrays() {

        otherHypCnt = 0;
        otherHypMax = LangConstants.COMPRESS_OTHER_HYP_INIT_LEN;
        otherHyp = new VarHyp[otherHypMax];

        otherAssrtCnt = 0;
        otherAssrtMax = LangConstants.COMPRESS_OTHER_ASSRT_INIT_LEN;
        otherAssrt = new Assrt[otherAssrtMax];

        repeatedSubproofCnt = 0;
        repeatedSubproofMax = LangConstants.COMPRESS_REPEATED_SUBPROOF_INIT_LEN;
        repeatedSubproof = new int[repeatedSubproofMax];

        stepCnt = 0;
        stepMax = LangConstants.COMPRESS_STEP_INIT_LEN;
        step = new Stmt[stepMax];

        subproofLength = new int[stepMax]; // parallel array
    }

    private void reallocAndCopyOtherHypArray() {
        otherHypMax *= 2;
        final VarHyp[] temp = new VarHyp[otherHypMax];
        for (int i = 0; i < otherHypCnt; i++)
            temp[i] = otherHyp[i];
        otherHyp = temp;
    }

    private void reallocAndCopyStepArray() {
        stepMax *= 2;
        final Stmt[] tempStep = new Stmt[stepMax];
        final int[] tempSubproofLength = new int[stepMax];
        for (int i = 0; i < stepCnt; i++) {
            tempStep[i] = step[i];
            tempSubproofLength[i] = subproofLength[i];
        }
        step = tempStep;
        subproofLength = tempSubproofLength;
    }

    private void reallocAndCopyRepeatedSubproofArray() {
        repeatedSubproofMax *= 2;
        final int[] temp = new int[repeatedSubproofMax];
        for (int i = 0; i < repeatedSubproofCnt; i++)
            temp[i] = repeatedSubproof[i];
        repeatedSubproof = temp;
    }

    public List<Stmt> compress(final String theoremLabel, final int width,
        final List<Hyp> mandHypArray, final List<Hyp> optHypArray,
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
        final PriorityQueue<Entry<Stmt, Integer>> sortedByBackrefs = new PriorityQueue<Map.Entry<Stmt, Integer>>(
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
