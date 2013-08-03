//********************************************************************/
//* Copyright (C) 2006                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/**
 *  ProofCompression.java 0.01 04/01/2006
 *
 */

package mmj.lang;

import java.util.*;

/**
 *  ProofCompression provides Compression and Decompression
 *  Services for Metamath proofs as described in Metamath(dot)pdf
 *  at metamath(dot)org.
 */
public class ProofCompression {

    private   boolean           usedYet;


    //*******************************************
    //all following variables are work items used
    //within a single execution but are stored
    //globally to eliminate call parameters.
    //*******************************************

    private   String            theoremLabel;  //for error msgs

    /*
     *  mandHyp contains the Theorem's MandFrame.hypArray
     */
    private   Hyp[]             mandHyp;

    /*
     *  optHyp contains the Theorem's OptFrame.optHypArray
     */
    private   Hyp[]             optHyp;


    /*
     *  otherHyp contains the VarHyp entries inside
     *  the parenthesized portion of a compressed
     *  proof (they occur before the Assrt entries in
     *  the parentheses.) There may be zero
     *  otherHyp entries.
     */
    private   int               otherHypCnt;
    private   int               otherHypMax;
    private   VarHyp[]          otherHyp;


    /*
     *  otherAssrt contains the Assrt entries inside
     *  the parenthesized portion of a compressed
     *  proof (they occur after the VarHyp entries in
     *  the parentheses.) There may be zero
     *  otherAssrt entries.
     */
    private   int               otherAssrtCnt;
    private   int               otherAssrtMax;
    private   Assrt[]           otherAssrt;


    /*  repeatedSubproof is an array of indexes
     *  pointing to a step[i], which is the last
     *  step in a subproof that is repeated later
     *  in the proof. The start index of the
     *  subproof is computed as:
     *
     *      startIndex = i - subproofLength[i] + 1.
     *
     */
    private   int               repeatedSubproofCnt;
    private   int               repeatedSubproofMax;
    private   int[]             repeatedSubproof;


    /*  step and subproofLength are parallel arrays,
     *  where subproofLength[i] contains the length
     *  of the subproof at step[i].
     *
     *  step[i] may be null, which is the case when
     *  the input proof step = "?".
     *
     *  There must always be at least one proof step,
     *  even if it is just a "?".
     */
    private   int               stepCnt;
    private   int               stepMax;
    private   Stmt[]            step;


    /*  subproofLength and step are parallel arrays,
     *  where subproofLength[i] contains the length
     *  of the subproof at step[i].
     *
     *  If step[i] == null, subproofLength[i] = 1.
     *
     *  When step[i].isHyp(), subproofLength[i] = 1.
     *
     *  When step[i].isAssrt(), subproofLength[i] =
     *
     *      1 plus the sum of subproofLength for n
     *      prior subproofLength entries,
     *
     *      where n =
     *
     *      ((Assrt)step[i]).getMandFrame().hypArray.length
     *      [that is the source data, but is actually
     *      computed using other code...].
     *
     *      The prior subproofLength entry indexes, "P", are
     *      determined working backwards:
     *
     *          P(n)     = i - 1
     *                   = subproofLength for nth
     *                     mand hyp of step[i]
     *
     *          P(n - 1) = P(n) - subproofLength[P(n)]
     *                   =  subproofLength for (n - 1)th
     *                      mand hyp of step[i]
     *
     *
     *          P(n - 2) = P(n - 1) - subproofLength[P(n - 1)]
     *                   =  subproofLength for (n - 2)th
     *                      mand hyp of step[i]
     *
     *          ...etc.
     *
     *  There must always be at least one proof step,
     *  even if it is just a "?".
     */
    private   int[]             subproofLength; //is parallel array!


    //*******************************************


    /**
     *  Constructor - default.
     */
    public ProofCompression() {

        //don't allocate the stacks until activity requested.
    }

    /**
     *  Decompress a single proof.
     *  <p>
     *  @param theoremLabel Theorem's label, used in error messages
     *                      that may be generated during processing.
     *
     *  @param stmtTbl      Stmt lookup map for translating labels
     *                      into Stmt object references.
     *
     *  @param mandHypArray The theorem's MandFrame.hypArray.
     *
     *  @param optHypArray  The theorem's OptFrame.optHypArray.
     *
     *  @param otherRefList List of String containing labels
     *                      of Stmt's provided in the parenthesized
     *                      portion of a compressed proof.
     *
     *  @param proofBlockList List of String containing
     *                        the compressed portion of the
     *                        proof.
     *
     *
     *  @return Stmt array containing decompressed Metamath RPN
     *          proof.
     */
    public Stmt[] decompress(String    theoremLabel,
                             int       seq,
                             Map       stmtTbl,
                             Hyp[]     mandHypArray,
                             Hyp[]     optHypArray,
                             List      otherRefList,
                             List      proofBlockList)
                                 throws LangException {

        this.theoremLabel         = theoremLabel; //for error msgs

        if (!usedYet) {
            initArrays();
            usedYet               = true;
        }

        mandHyp                   = mandHypArray;
        optHyp                    = optHypArray;

        loadOtherRefArrays(stmtTbl,
                           otherRefList,
                           seq);

        Iterator blockIterator    = proofBlockList.iterator();
        if (blockIterator.hasNext()) {
            loadSteps(blockIterator);
        }
        else {
            throw new LangException(
                LangConstants.ERRMSG_COMPRESS_NO_PROOF_BLOCKS_1
                + theoremLabel
                + LangConstants.ERRMSG_COMPRESS_NO_PROOF_BLOCKS_2);
        }

        return constructProofArray();
    }

    private void loadOtherRefArrays(Map  stmtTbl,
                                    List otherRefList,
                                    int  seq)
                                 throws LangException {

        otherHypCnt               = 0;
        otherAssrtCnt             = 0;

        if (otherRefList.size() > otherAssrtMax) {
           otherAssrtMax          = otherRefList.size() * 2;
           otherAssrt             = new Assrt[otherAssrtMax];
        }

        Iterator otherRefIterator = otherRefList.iterator();
        String otherLabel;
        Stmt   otherStmt;
        int    iterationNbr       = 0;
        while (otherRefIterator.hasNext()) {
            ++iterationNbr;
            otherLabel            = (String)otherRefIterator.next();
            otherStmt             = (Stmt)stmtTbl.get(otherLabel);
            if (otherStmt == null) {
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_NOTFND_1
                    + theoremLabel
                    + LangConstants.ERRMSG_COMPRESS_OTHER_NOTFND_2
                    + iterationNbr
                    + LangConstants.ERRMSG_COMPRESS_OTHER_NOTFND_3
                    + otherLabel);
            }
            if (otherStmt.getSeq() >= seq) {
                throw new LangException(
                    LangConstants.ERRMSG_FORWARD_PROOF_STEP_LABEL
                    + otherLabel);
            }

            if (otherStmt.isAssrt()) {
                otherAssrt[otherAssrtCnt++]
                                  = ((Assrt)otherStmt);
                continue;
            }

            if (!otherStmt.isVarHyp()) {
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_OTHER_BOGUS_1
                    + theoremLabel
                    + LangConstants.ERRMSG_COMPRESS_OTHER_BOGUS_2
                    + iterationNbr
                    + LangConstants.ERRMSG_COMPRESS_OTHER_BOGUS_3
                    + otherLabel);
            }

            /**
             *  this is a little "tricky" -- "active" applies
             *  only to global hypotheses or when the source
             *  file is being loaded.
             */
            if (!otherStmt.isActive()
                &&
                !isProofStepInExtendedFrame(otherStmt)) {
                throw new LangException(
                    LangConstants.
                        ERRMSG_PROOF_STEP_HYP_INACTIVE
                    + otherLabel);
            }

            loadOtherHyp((VarHyp)otherStmt,
                         iterationNbr);
            continue;
        }
    }

    /**
     *  Checks to see whether or not a proof step is
     *  contained in the Theorem's Extended Frame.
     *  <p>
     *  Cloned this from mmj.lang.Theorem.java.
     *  <p>
     *  First checks to see if the proof step is in the
     *  MandFrame's hypArray. If not it checks the
     *  OptFrame's hypArray
     *
     *  @param proofStep a Statement reference.
     *
     *  @return true if proof step == a Hyp in either the
     *   MandFrame or OptFrame of the Theorem.
     */
    public  boolean isProofStepInExtendedFrame(Stmt proofStep) {
        for (int i = 0; i < mandHyp.length; i++) {
            if (proofStep == mandHyp[i]) {
                return true;
            }
        }
        for (int i = 0; i < optHyp.length; i++) {
            if (proofStep == optHyp[i]) {
                return true;
            }
        }
        return false;
    }

    private void loadOtherHyp(VarHyp otherVarHyp,
                              int    iterationNbr)
                                 throws LangException {

        if (otherAssrtCnt > 0) {
            throw new LangException(
                LangConstants.ERRMSG_COMPRESS_OTHER_VARHYP_POS_1
                + theoremLabel
                + LangConstants.ERRMSG_COMPRESS_OTHER_VARHYP_POS_2
                + iterationNbr
                + LangConstants.ERRMSG_COMPRESS_OTHER_VARHYP_POS_3
                + otherVarHyp.getLabel());
        }

        if (otherHypCnt >= otherHypMax) {
            reallocAndCopyOtherHypArray();
        }

        otherHyp[otherHypCnt++]   = otherVarHyp;
    }

    private void loadSteps(Iterator blockIterator)
                                 throws LangException {
        repeatedSubproofCnt       = 0;
        stepCnt                   = 0;


        String block              = (String)blockIterator.next();
        int    blockLen           = block.length();
        int    blockNbr           = 1;
        int    charIndex          = 0;

        int    decompressNbr      = 0;
        int    prevDecompressNbr  = 0;
        int    workNbr;

        char   nextChar;
        byte   nextCharCode;

        int    nbrOfPos; //these are for computing subproofLength
        int    prevPos;
        int    currPos;

        while (true) {
            if (charIndex >= blockLen) {
                if (!blockIterator.hasNext()) {
                    if (decompressNbr > 0) {
                        throw new LangException(
                            LangConstants.
                                ERRMSG_COMPRESS_PREMATURE_END_1
                            + theoremLabel
                            + LangConstants.
                                ERRMSG_COMPRESS_PREMATURE_END_2);
                    }
                    break;
                }
                charIndex         = 0;
                block             = (String)blockIterator.next();
                blockLen          = block.length();
                ++blockNbr;
            }

            nextChar              = block.charAt(charIndex++);
            if (nextChar >=
                LangConstants.COMPRESS_VALID_CHARS.length) {
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_NOT_ASCII_1
                    + theoremLabel
                    + LangConstants.ERRMSG_COMPRESS_NOT_ASCII_2
                    + blockNbr
                    + LangConstants.ERRMSG_COMPRESS_NOT_ASCII_3
                    + charIndex
                    + LangConstants.ERRMSG_COMPRESS_NOT_ASCII_4
                    + nextChar);
            }

            //translate 'A' to 0, 'B' to 1, etc. (1 is added to
            // 'A' thru 'Z' later -- curiously but effectively :)
            nextCharCode          =
                LangConstants.COMPRESS_VALID_CHARS[nextChar];

            if (nextCharCode ==
                LangConstants.COMPRESS_ERROR_CHAR_VALUE) {
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_BAD_CHAR_1
                    + theoremLabel
                    + LangConstants.ERRMSG_COMPRESS_BAD_CHAR_2
                    + blockNbr
                    + LangConstants.ERRMSG_COMPRESS_BAD_CHAR_3
                    + charIndex
                    + LangConstants.ERRMSG_COMPRESS_BAD_CHAR_4
                    + nextChar);
            }

            if (nextCharCode ==
                LangConstants.COMPRESS_UNKNOWN_CHAR_VALUE) {
                if (decompressNbr > 0) {
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_UNK_1
                        + theoremLabel
                        + LangConstants.ERRMSG_COMPRESS_BAD_UNK_2
                        + blockNbr
                        + LangConstants.ERRMSG_COMPRESS_BAD_UNK_3
                        + charIndex
                        + LangConstants.ERRMSG_COMPRESS_BAD_UNK_4);
                }

                if (stepCnt >= stepMax) {
                    reallocAndCopyStepArray();
                }
                step[stepCnt]     = null;
                subproofLength[stepCnt]
                                  = 1;
                ++stepCnt;
                prevDecompressNbr = 0;
                decompressNbr     = 0;
                continue;
            }

            if (nextCharCode ==
                LangConstants.COMPRESS_REPEAT_CHAR_VALUE) {

//              System.out.println("nextCharCode == repeat char value"
//              + " Theorem=" + theoremLabel
//              + " nextCharCode=" + nextCharCode
//              + " decompressNbr=" + decompressNbr);

                if (decompressNbr > 0) {
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_RPT_1
                        + theoremLabel
                        + LangConstants.ERRMSG_COMPRESS_BAD_RPT_2
                        + blockNbr
                        + LangConstants.ERRMSG_COMPRESS_BAD_RPT_3
                        + charIndex
                        + LangConstants.ERRMSG_COMPRESS_BAD_RPT_4);
                }
                if (prevDecompressNbr == 0) {
                    throw new LangException(
                        LangConstants.ERRMSG_COMPRESS_BAD_RPT2_1
                        + theoremLabel
                        + LangConstants.ERRMSG_COMPRESS_BAD_RPT2_2
                        + blockNbr
                        + LangConstants.ERRMSG_COMPRESS_BAD_RPT2_3
                        + charIndex
                        + LangConstants.ERRMSG_COMPRESS_BAD_RPT2_4);
                }
                if (repeatedSubproofCnt >= repeatedSubproofMax) {
                    reallocAndCopyRepeatedSubproofArray();
                }
                repeatedSubproof[repeatedSubproofCnt]
                                  = stepCnt - 1;
                ++repeatedSubproofCnt;
                prevDecompressNbr = 0;
                decompressNbr     = 0;
                continue;
            }

            if (nextCharCode >=
                LangConstants.COMPRESS_LOW_BASE) {

                decompressNbr     =
                    (decompressNbr *
                        LangConstants.COMPRESS_HIGH_BASE)
                    + nextCharCode;

//              System.out.println("nextCharCode >= low base"
//              + " Theorem=" + theoremLabel
//              + " nextCharCode=" + nextCharCode
//              + " decompressNbr=" + decompressNbr);

                continue;
            }

            //else...
            decompressNbr        += (nextCharCode + 1); //'A' = 1 etc

            /*
             *  Whew! finally...we have decompressed a number!
             *  But what does the number signify?
             *
             *  1 thru mandHyp.length              = mandHyp
             *  mandHyp.length + 1 thru otherHypCnt = otherHyp
             *  otherHypCnt + 1 thru otherAssrtCnt = otherAssrt
             *  otherAssrtCnt + 1 thru repeatedSubproofCnt
             *                                     = repeatedSubproof
             *  otherSubproofCnt + 1 and beyond    = error! bogus!@#
             *
             *  The above mentioned arrays (mandHyp, otherHyp, etc.)
             *  begin indexing at 0, so we need to subtract 1
             *  from "decompressNbr" to index into the arrays,
             *  but "decompressNbr = 0" has special significance --
             *  it means "no previous decompression in progress",
             *  so we'll use "workNbr" to mess around with.
             */

            workNbr               = decompressNbr - 1;

            // ok, do we have a mandHyp array entry?
            // if so, just put it in the proof step array!
            if (workNbr < mandHyp.length) {
                if (stepCnt >= stepMax) {
                    reallocAndCopyStepArray();
                }
                step[stepCnt]     = mandHyp[workNbr];
                subproofLength[stepCnt]
                                  = 1;
                ++stepCnt;
                prevDecompressNbr = decompressNbr;
                decompressNbr     = 0;
                continue;
            }

            // ok, adjust workNbr down into otherHyp range
            workNbr              -= mandHyp.length;

            // ok, do we have a otherHyp array entry?
            // if so, just put it in the proof step array!
            if (workNbr < otherHypCnt) {
                if (stepCnt >= stepMax) {
                    reallocAndCopyStepArray();
                }
                step[stepCnt]     = otherHyp[workNbr];
                subproofLength[stepCnt]
                                  = 1;
                ++stepCnt;
                prevDecompressNbr = decompressNbr;
                decompressNbr     = 0;
                continue;
            }

            // ok, adjust workNbr down into otherAssrt range
            workNbr              -= otherHypCnt;

            // ok, do we have a otherAssrt array entry?
            // if so, put it in the proof step array, but
            // this time we need to compute subproofLength!
            // an Assrt has Hyps in its subproof tree/stack.
            if (workNbr < otherAssrtCnt) {

                if (stepCnt >= stepMax) {
                    reallocAndCopyStepArray();
                }
                step[stepCnt]     = otherAssrt[workNbr];
                subproofLength[stepCnt]
                                  = 1; //init to 1, then add hyp lens

                nbrOfPos          =
                    otherAssrt[workNbr].getMandHypArrayLength();
                prevPos           = stepCnt;

                while (nbrOfPos-- > 0) {
                    currPos       = prevPos - subproofLength[prevPos];

                    if (currPos < 0 ||
                        currPos > stepCnt) {
                        throw new LangException(
                            LangConstants.ERRMSG_COMPRESS_CORRUPT_1
                            + theoremLabel
                            + LangConstants.ERRMSG_COMPRESS_CORRUPT_2
                            + blockNbr
                            + LangConstants.ERRMSG_COMPRESS_CORRUPT_3
                            + charIndex
                            + LangConstants.ERRMSG_COMPRESS_CORRUPT_4);
                    }

                    subproofLength[stepCnt]
                                 += subproofLength[currPos];
                    prevPos         = currPos;
                }

                ++stepCnt;
                prevDecompressNbr = decompressNbr;
                decompressNbr     = 0;
                continue;
            }

            workNbr              -= otherAssrtCnt;

            if (workNbr >= repeatedSubproofCnt) {
                throw new LangException(
                    LangConstants.ERRMSG_COMPRESS_BAD_RPT3_1
                    + theoremLabel
                    + LangConstants.ERRMSG_COMPRESS_BAD_RPT3_2
                    + blockNbr
                    + LangConstants.ERRMSG_COMPRESS_BAD_RPT3_3
                    + charIndex
                    + LangConstants.ERRMSG_COMPRESS_BAD_RPT3_4);
            }

            int endRepeat         = repeatedSubproof[workNbr];
            int startRepeat       = endRepeat
                                    + 1
                                    - subproofLength[endRepeat];

            if ((stepCnt + subproofLength[endRepeat]) >= stepMax) {
                reallocAndCopyStepArray();
            }

            while (startRepeat <= endRepeat) {
                step[stepCnt]     = step[startRepeat];
                subproofLength[stepCnt]
                                  = subproofLength[startRepeat];
                ++startRepeat;
                ++stepCnt;
            }

            prevDecompressNbr     = 0;
            decompressNbr         = 0;
            continue;
        }
    }

    private Stmt[] constructProofArray() {
        Stmt[] proofArray         = new Stmt[stepCnt];
        for (int i = 0; i < stepCnt; i++) {
            proofArray[i]         = step[i];
        }
        return proofArray;
    }

    private void initArrays() {

        otherHypCnt               = 0;
        otherHypMax               =
            LangConstants.COMPRESS_OTHER_HYP_INIT_LEN;
        otherHyp                  = new VarHyp[otherHypMax];

        otherAssrtCnt             = 0;
        otherAssrtMax             =
            LangConstants.COMPRESS_OTHER_ASSRT_INIT_LEN;
        otherAssrt                = new Assrt[otherAssrtMax];

        repeatedSubproofCnt       = 0;
        repeatedSubproofMax       =
            LangConstants.COMPRESS_REPEATED_SUBPROOF_INIT_LEN;
        repeatedSubproof          = new int[repeatedSubproofMax];

        stepCnt                   = 0;
        stepMax                   =
            LangConstants.COMPRESS_STEP_INIT_LEN;
        step                      = new Stmt[stepMax];

        subproofLength            = new int[stepMax]; //parallel array
    }


    private void reallocAndCopyOtherHypArray() {
        otherHypMax              *= 2;
        VarHyp[] temp             = new VarHyp[otherHypMax];
        for (int i = 0; i < otherHypCnt; i++) {
            temp[i]               = otherHyp[i];
        }
        otherHyp                  = temp;
    }

    private void reallocAndCopyStepArray() {
        stepMax                  *= 2;
        Stmt[] tempStep           = new Stmt[stepMax];
        int[]  tempSubproofLength = new int[stepMax];
        for (int i = 0; i < stepCnt; i++) {
            tempStep[i]           = step[i];
            tempSubproofLength[i] = subproofLength[i];
        }
        step                      = tempStep;
        subproofLength            = tempSubproofLength;
    }

    private void reallocAndCopyRepeatedSubproofArray() {
        repeatedSubproofMax      *= 2;
        int[] temp                = new int[repeatedSubproofMax];
        for (int i = 0; i < repeatedSubproofCnt; i++) {
            temp[i]               = repeatedSubproof[i];
        }
        repeatedSubproof          = temp;
    }
}
