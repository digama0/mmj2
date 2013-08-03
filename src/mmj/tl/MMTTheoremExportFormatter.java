//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 *  MMTTheoremExportFormatter.java  0.01 08/01/2008
 *
 *  Version 0.01:
 *      --> new.
 */

package mmj.tl;
import java.util.*;
import mmj.lang.*;
import mmj.pa.*;
import mmj.mmio.MMIOConstants;

/**
 *  Converts a thing into a list Metamath-formatted file lines.
 *  <p>
 *  The output Metamath .mm file text lines are formatted according
 *  to the Theorem Loader Preferences.
 *  <p>
 *  Note: output lines do not contain newlines.
 *  <p>
 *  Note: this code handles Theorems and ProofWorksheets, and
 *  in theory there could be subclasses involved, but it seemed
 *  ok to just put all of the formatting code in one spot here
 *  as a quick and dirty bit of coding.
 */
public class MMTTheoremExportFormatter {

    private TlPreferences   tlPreferences;

    private int             indentAmt;
    private int             rightCol;
    private boolean         storeFormulasAsIs;

    private LinkedList      list;

    private boolean         needScopeLines;

    /**
     *  Basic constructor.
     *  <p>
     *  @param tlPreferences TlPreferences object.
     */
    public MMTTheoremExportFormatter(TlPreferences tlPreferences) {

        this.tlPreferences        = tlPreferences;
    }

    /**
     *  Converts a Theorem in the LogicalSystem into a list of
     *  StringBuffer lines formatted into Metamath format.
     *  <p>
     *  @param theorem Theorem in the Logical System.
     *  @return LinkedList of StringBuffer objects each containing
     *         one line of text in Metamath-format (without newlines.)
     */
    public LinkedList buildStringBufferLineList(Theorem theorem) {
        init(theorem);

        if (needScopeLines) {
            outputBeginScope();
        }

        if (theorem.getMandFrame().djVarsArray.length > 0    ||
            theorem.getOptFrame().optDjVarsArray.length > 0) {
            outputDjVarsLines(theorem);
        }

        if (theorem.getLogHypArrayLength() > 0) {
            outputLogHypLines(theorem);
        }

        String description        = theorem.getDescription();
        if (description != null &&
            description.length() > 0) {
            outputDescription(description);
        }

        outputConclusionLine(theorem);

        outputProofLine(theorem);

        if (needScopeLines) {
            outputEndScope();
        }

        return list;
    }

    /**
     *  Converts a ProofWorksheet into a list of
     *  StringBuffer lines formatted into Metamath format.
     *  <p>
     *  @param proofWorksheet ProofWorksheet object
     *  @return LinkedList of StringBuffer objects each containing
     *         one line of text in Metamath-format (without newlines.)
     *  @throws TheoremLoaderException if the ProofWorksheet is
     *             null or is not unified.
     */
    public LinkedList buildStringBufferLineList(
                            ProofWorksheet proofWorksheet)
                                throws TheoremLoaderException {

        if (proofWorksheet                         == null ||
            proofWorksheet.getGeneratedProofStmt() == null) {
            throw new TheoremLoaderException(
                TlConstants.
                    ERRMSG_EXPORT_FORMAT_PROOF_WORKSHEET_ERR_1);
        }

        init(proofWorksheet);

        if (needScopeLines) {
            outputBeginScope();
        }


        // we output the ProofWorksheet's $d lines regardless
        // of whether the theorem is new.
        DistinctVariablesStmt[] dvStmtArray
                                  =
            proofWorksheet.getDvStmtArray();

        if (dvStmtArray != null &&
            dvStmtArray.length > 0) {
            outputDjVarsLines(dvStmtArray);
        }


        // now logical hypotheses
        if (proofWorksheet.isNewTheorem()) {
            if (proofWorksheet.getHypStepCnt() > 0) {
                outputLogHypLines(proofWorksheet);
            }
        }
        else {
            Theorem theorem       = proofWorksheet.getTheorem();
            if (theorem.getLogHypArrayLength() > 0) {
                if (storeFormulasAsIs) {
                    outputLogHypLines(theorem);
                }
                else {
                    outputLogHypLines(theorem,
                                      proofWorksheet);
                }
            }
        }


        // we output the ProofWorksheet's description regardless
        // of whether the theorem is new.
        String description        = null;
        Iterator iterator         =
            proofWorksheet.getProofWorkStmtListIterator();
        while (iterator.hasNext()) {
            ProofWorkStmt proofWorkStmt
                                  = (ProofWorkStmt)iterator.next();
            if (proofWorkStmt instanceof CommentStmt) {
                description       =
                    proofWorkStmt.
                        getStmtText().
                            toString().
                                substring(1); // erase "*" at start
                break;
            }
        }
        if (description != null) {
            outputDescription(description);
        }


        // now the conclusion formula + start of proof token
        if (proofWorksheet.isNewTheorem() ||
            storeFormulasAsIs) {
            outputConclusionLine(proofWorksheet);
        }
        else {
            outputConclusionLine(proofWorksheet.getTheorem());
        }


        // we output the ProofWorksheet's proof regardless
        // of whether the theorem is new.
        outputProofLine(proofWorksheet);

        if (needScopeLines) {
            outputEndScope();
        }

        return list;
    }

    private void init(Theorem theorem) {

        init();

        if (theorem.getLogHypArrayLength() > 0               ||
            theorem.getMandFrame().djVarsArray.length > 0    ||
            theorem.getOptFrame().optDjVarsArray.length > 0) {
            needScopeLines        = true;
        }
    }

    private void init(ProofWorksheet proofWorksheet) {

        init();

        DistinctVariablesStmt[] dvStmtArray
                                  =
            proofWorksheet.getDvStmtArray();

        if (dvStmtArray != null &&
            dvStmtArray.length > 0) {
            needScopeLines        = true;
        }
        else {
            if (proofWorksheet.isNewTheorem()) {
                if (proofWorksheet.getHypStepCnt() > 0) {
                    needScopeLines
                                  = true;
                }
            }
            else {
                if (proofWorksheet.
                        getTheorem().
                            getLogHypArrayLength() > 0) {
                    needScopeLines
                                  = true;
                }
            }
        }
    }

    private void init() {
        list                      = new LinkedList();

        needScopeLines            = false;

        indentAmt                 =
            tlPreferences.getStoreMMIndentAmt();
        rightCol                  =
            tlPreferences.getStoreMMRightCol();
        storeFormulasAsIs         =
            tlPreferences.getStoreFormulasAsIs();
    }

    private void outputBeginScope() {
        StringBuffer sb           = startNewLine(indentAmt);
        sb.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
        sb.append(MMIOConstants.MM_BEGIN_SCOPE_KEYWORD_CHAR);
        list.add(sb);
    }

    private void outputEndScope() {

        StringBuffer sb           = startNewLine(indentAmt);
        sb.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
        sb.append(MMIOConstants.MM_END_SCOPE_KEYWORD_CHAR);
        list.add(sb);
    }

    private void outputLogHypLines(Theorem theorem) {

        LogHyp[] array            = theorem.getLogHypArray();
        for (int i = 0; i < array.length; i++) {
            outputOneLogHypsLines(array[i]);
        }
    }

    /*
        Here we are outputting a theorem's logical hypotheses
        but using the proof worksheet formula formatting because
        "storeFormulasAsIs" was specified. The problem is to
        output the hypotheses in database order, which is not
        necessarily going to be the same as the order they
        appear in the ProofWorksheet.
     */
    private void outputLogHypLines(Theorem        theorem,
                                   ProofWorksheet proofWorksheet) {

        HypothesisStep hypothesisStep;

        LogHyp[] logHypArray      = theorem.getLogHypArray();
        for (int i = 0; i < logHypArray.length; i++) {
            hypothesisStep        =
                proofWorksheet.
                    getHypothesisStepFromList(
                        logHypArray[i]);
            if (hypothesisStep == null) {
                throw new IllegalArgumentException(
                    TlConstants.
                        ERRMSG_HYP_MISSING_FOR_EXPORTED_PROOF_WORKSHEET_1
                    + logHypArray[i].getLabel());
            }
            outputOneLogHypsLines(hypothesisStep);
        }
    }

    /*
       The main question here is what order should the
       HypothesisStep statements be output for this new theorem?
       Fortunately, the order is arbitrary and is only crucial to
       other theorems which would use this theorem in a proof
       (the hyp parse node children of a proof step must be in
       database order.) So we output the HypothesisSteps in order
       of appearance in the ProofWorksheet.
     */
    private void outputLogHypLines(ProofWorksheet proofWorksheet) {

        Iterator iterator         =
            proofWorksheet.getProofWorkStmtListIterator();
        HypothesisStep hypothesisStep;
        ProofWorkStmt  w;
        while (iterator.hasNext()) {
            w                     = (ProofWorkStmt)iterator.next();
            if (w.isHypothesisStep()) {
                hypothesisStep    = (HypothesisStep)w;
                outputOneLogHypsLines(hypothesisStep);
            }
        }
    }

    private void outputOneLogHypsLines(
                                HypothesisStep hypothesisStep) {
        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }

        StringBuffer prefix       = startNewLine(leftOffset);
        prefix.append(hypothesisStep.getRefLabel());
        prefix.append(' ');
        prefix.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
        prefix.append(MMIOConstants.MM_LOG_HYP_KEYWORD_CHAR);

        int continuationOffset    = prefix.length() + indentAmt;

        StringBuffer stmtText     = hypothesisStep.getStmtText();
        StringBuffer textArea     =
            new StringBuffer(stmtText.capacity());
        textArea.append(stmtText);

        ProofStepStmt.reviseStepHypRefInStmtTextArea(textArea,
                                                     prefix);

        String s                  = textArea.toString();
        String[] textLines        =
            s.split(MMIOConstants.MM_JAVA_REGEX_NEWLINE);

        for (int i = 0; i < textLines.length - 1 ; i++) {
            if (textLines[i].length() > 0) {
                list.add(new StringBuffer(textLines[i]));
            }
        }

        StringBuffer sb           = new StringBuffer(rightCol);

        String lastLine           = textLines[textLines.length - 1];

        int finalNonWhitespace    = lastLine.length() - 1;
        while (finalNonWhitespace >= 0) {
            if (Character.isWhitespace(
                    lastLine.charAt(finalNonWhitespace))) {
                --finalNonWhitespace;
            }
            else {
                break;
            }
        }
        if (finalNonWhitespace < 0) {
            sb.append(lastLine);
        }
        else {
            int space             = finalNonWhitespace + 1;
            if (space < lastLine.length()) {
                sb.append(lastLine.substring(0,space));
            }
            else {
                sb.append(lastLine);
            }
        }
        sb.append(' ');

        if (sb.length() +
            MMIOConstants.MM_END_STMT_KEYWORD.length()
                >
            rightCol) {
            list.add(sb);
            sb                    = startNewLine(continuationOffset);
        }
        sb.append(MMIOConstants.MM_END_STMT_KEYWORD);
        list.add(sb);
    }

    private void outputOneLogHypsLines(LogHyp logHyp) {

        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }

        StringBuffer sb           = startNewLine(leftOffset);

        sb.append(logHyp.getLabel());
        sb.append(' ');

        sb.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
        sb.append(MMIOConstants.MM_LOG_HYP_KEYWORD_CHAR);
        sb.append(' ');

        int continuationOffset    = sb.length();

        Formula formula           = logHyp.getFormula();

        sb                        =
            formula.toStringBufferLineList(
                list,
                sb,
                continuationOffset + 1,
                rightCol,
                MMIOConstants.MM_END_STMT_KEYWORD);

        list.add(sb);

    }

    private void outputDescription(String description) {

        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }

        StringBuffer sb           = startNewLine(leftOffset);

        sb.append(MMIOConstants.MM_START_COMMENT_KEYWORD);
        sb.append(' ');

        int col                   = sb.length();
        int continuationOffset    = col;

        // split description using "\\s" whitespace regular expression.
        String[] tokenArray       =
            description.split(MMIOConstants.MM_JAVA_REGEX_WHITESPACE);

        String token;
        int    i                  = -1;
        while (++i < tokenArray.length) {
            if (tokenArray[i].length() == 0) {
                continue;
            }
            token                 = tokenArray[i];
            if (token.equals(
                MMIOConstants.MM_LABEL_IN_COMMENT_ESCAPE_STRING)) { // "~"
                if (i < tokenArray.length - 1) {
                    token         =
                        new String(tokenArray[i]
                                   + " "
                                   + tokenArray[++i]);
                }
            }

            col                  += token.length();
            if (col > rightCol) {
                list.add(sb);
                sb                = startNewLine(continuationOffset);
                col               = continuationOffset +
                                    token.length();
            }
            sb.append(token);
            if (col < rightCol) {
                sb.append(' ');
                ++col;
            }
        }

        if (col + MMIOConstants.MM_END_COMMENT_KEYWORD.length() >
            rightCol) {

            list.add(sb);
            sb                    = startNewLine(continuationOffset);
        }

        sb.append(MMIOConstants.MM_END_COMMENT_KEYWORD);
        list.add(sb);
    }

    private void outputConclusionLine(Theorem theorem) {

        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }

        StringBuffer sb           = startNewLine(leftOffset);

        sb.append(theorem.getLabel());
        sb.append(' ');

        sb.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
        sb.append(MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD_CHAR);
        sb.append(' ');

        int continuationOffset    = sb.length();

        Formula formula           = theorem.getFormula();

        sb                        =
            formula.toStringBufferLineList(
                list,
                sb,
                continuationOffset + 1,
                rightCol,
                MMIOConstants.MM_START_PROOF_KEYWORD);

        list.add(sb);
    }

    private void outputConclusionLine(
                                ProofWorksheet proofWorksheet) {
        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }

        StringBuffer prefix       = startNewLine(leftOffset);
        prefix.append(proofWorksheet.getTheoremLabel());
        prefix.append(' ');
        prefix.append(MMIOConstants.MM_KEYWORD_1ST_CHAR);
        prefix.append(MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD_CHAR);

        int continuationOffset    = prefix.length() + indentAmt;

        StringBuffer stmtText     =
            proofWorksheet.getQedStep().getStmtText();
        StringBuffer textArea     =
            new StringBuffer(stmtText.capacity());
        textArea.append(stmtText);

        ProofStepStmt.reviseStepHypRefInStmtTextArea(textArea,
                                                     prefix);

        String s                  = textArea.toString();
        String[] textLines        =
            s.split(MMIOConstants.MM_JAVA_REGEX_NEWLINE);

        for (int i = 0; i < textLines.length - 1 ; i++) {
            if (textLines[i].length() > 0) {
                list.add(new StringBuffer(textLines[i]));
            }
        }

        StringBuffer sb           = new StringBuffer(rightCol);

        String lastLine           = textLines[textLines.length - 1];

        int finalNonWhitespace      = lastLine.length() - 1;
        while (finalNonWhitespace >= 0) {
            if (Character.isWhitespace(
                    lastLine.charAt(finalNonWhitespace))) {
                --finalNonWhitespace;
            }
            else {
                break;
            }
        }
        if (finalNonWhitespace < 0) {
            sb.append(lastLine);
        }
        else {
            int space             = finalNonWhitespace + 1;
            if (space < lastLine.length()) {
                sb.append(lastLine.substring(0,space));
            }
            else {
                sb.append(lastLine);
            }
        }
        sb.append(' ');

        if (sb.length() +
            MMIOConstants.MM_START_PROOF_KEYWORD.length()
                >
            rightCol) {
            list.add(sb);
            sb                    = startNewLine(continuationOffset);
        }
        sb.append(MMIOConstants.MM_START_PROOF_KEYWORD );
        list.add(sb);
    }

    private void outputProofLine(Theorem theorem) {
        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }
        leftOffset               += indentAmt;

        StringBuffer sb           = startNewLine(leftOffset);

        outputProof(theorem.getProof(),
                    leftOffset,
                    rightCol);
    }

    private void outputProofLine(ProofWorksheet proofWorksheet) {

        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }
        leftOffset               += indentAmt;

        StringBuffer sb           = startNewLine(leftOffset);

        outputProof(proofWorksheet.getQedStepProofRPN(),
                    leftOffset,
                    rightCol);
    }

    private void outputProof(Stmt[]       proof,
                             int          left,
                             int          right) {

        StringBuffer sb           = startNewLine(left);

        String       stepLabel;
        int          col          = left;
        for (int i = 0; i < proof.length; i++) {

            if (proof[i] == null) {
                stepLabel         = MMIOConstants.MISSING_PROOF_STEP;
            }
            else {
                stepLabel         = proof[i].getLabel();
            }

            col                  += stepLabel.length();
            if (col > right) {
                list.add(sb);
                sb                = startNewLine(left);
                col               = left
                                    + stepLabel.length();
            }

            sb.append(stepLabel);

            if (col < right) {
                sb.append(' ');
                ++col;
            }
            else {
                list.add(sb);
                sb                = startNewLine(left);
                col               = left;
            }
        }

        if (col + MMIOConstants.MM_END_STMT_KEYWORD.length() >
            right) {

            list.add(sb);
            sb                    = startNewLine(left);
        }

        sb.append(MMIOConstants.MM_END_STMT_KEYWORD);

        list.add(sb);
    }

    private void outputDjVarsLines(Theorem theorem) {

        LinkedList djStmtTextList =
            DjVars.
                buildMetamathDjVarsStatementList(
                    theorem);

        outputDjVarsLines(djStmtTextList);
    }

    private void outputDjVarsLines(
                    DistinctVariablesStmt[] distinctVariableStmtArray) {

        LinkedList djStmtTextList =
            DjVars.
                buildMetamathDjVarsStatementList(
                    distinctVariableStmtArray);

        outputDjVarsLines(djStmtTextList);
    }

    private void outputDjVarsLines(LinkedList djStmtTextList) {

        int leftOffset            = indentAmt;
        if (needScopeLines) {
            leftOffset           += indentAmt;
        }

        StringBuffer sb           = startNewLine(leftOffset);
        int col                   = leftOffset;

        // if a single $d statement exceeds the line length
        // let it go beyond? is it worth fooling with? no...
        Iterator     i            = djStmtTextList.iterator();
        StringBuffer dsb;
        while (i.hasNext()) {
            dsb                   = (StringBuffer)i.next();
            col                  += dsb.length();
            if (col > rightCol) {
                list.add(sb);
                sb                = startNewLine(leftOffset);
                col               = leftOffset +
                                    dsb.length();
            }
            sb.append(dsb.toString());

            if (col < rightCol) {
                sb.append(' ');
                ++col;
            }
        }

        list.add(sb);
    }

    private StringBuffer startNewLine(int n) {

        StringBuffer sb           = new StringBuffer(rightCol);
        indent(sb,
               n);
        return sb;
    }

    private void indent(StringBuffer sb,
                        int          n) {
        while (n-- > 0) {
            sb.append(' ');
        }
    }
}
