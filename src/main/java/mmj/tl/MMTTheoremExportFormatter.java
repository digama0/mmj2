//********************************************************************/
//* Copyright (C) 2008                                               */
//* MEL O'CAT  mmj2 (via) planetmath (dot) org                       */
//* License terms: GNU General Public License Version 2              */
//*                or any later version                              */
//********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/

/*
 * MMTTheoremExportFormatter.java  0.01 08/01/2008
 *
 * Version 0.01:
 *     --> new.
 */

package mmj.tl;

import java.util.LinkedList;
import java.util.List;

import mmj.lang.*;
import mmj.lang.ParseTree.RPNStep;
import mmj.mmio.MMIOConstants;
import mmj.pa.*;

/**
 * Converts a thing into a list Metamath-formatted file lines.
 * <p>
 * The output Metamath .mm file text lines are formatted according to the
 * Theorem Loader Preferences.
 * <p>
 * Note: output lines do not contain newlines.
 * <p>
 * Note: this code handles Theorems and ProofWorksheets, and in theory there
 * could be subclasses involved, but it seemed ok to just put all of the
 * formatting code in one spot here as a quick and dirty bit of coding.
 */
public class MMTTheoremExportFormatter {

    private final TlPreferences tlPreferences;

    private int indentAmt;
    private int rightCol;
    private boolean storeFormulasAsIs;

    private List<StringBuilder> list;

    private boolean needScopeLines;

    /**
     * Basic constructor.
     *
     * @param tlPreferences TlPreferences object.
     */
    public MMTTheoremExportFormatter(final TlPreferences tlPreferences) {

        this.tlPreferences = tlPreferences;
    }

    /**
     * Converts a Theorem in the LogicalSystem into a list of StringBuilder
     * lines formatted into Metamath format.
     *
     * @param theorem Theorem in the Logical System.
     * @return LinkedList of StringBuilder objects each containing one line of
     *         text in Metamath-format (without newlines.)
     */
    public List<StringBuilder> buildStringBuilderLineList(
        final Theorem theorem)
    {
        init(theorem);

        if (needScopeLines)
            outputBeginScope();

        if (theorem.getMandFrame().djVarsArray.length > 0
            || theorem.getOptFrame().djVarsArray.length > 0)
            outputDjVarsLines(theorem);

        if (theorem.getLogHypArrayLength() > 0)
            outputLogHypLines(theorem);

        final String description = theorem.getDescription();
        if (description != null && description.length() > 0)
            outputDescription(description);

        outputConclusionLine(theorem);

        outputProofLine(theorem);

        if (needScopeLines)
            outputEndScope();

        return list;
    }

    /**
     * Converts a ProofWorksheet into a list of StringBuilder lines formatted
     * into Metamath format.
     *
     * @param proofWorksheet ProofWorksheet object
     * @return LinkedList of StringBuilder objects each containing one line of
     *         text in Metamath-format (without newlines.)
     * @throws TheoremLoaderException if the ProofWorksheet is null or is not
     *             unified.
     */
    public List<StringBuilder> buildStringBuilderLineList(
        final ProofWorksheet proofWorksheet) throws TheoremLoaderException
    {

        if (proofWorksheet == null
            || proofWorksheet.getGeneratedProofStmt() == null)
            throw new TheoremLoaderException(
                TlConstants.ERRMSG_EXPORT_FORMAT_PROOF_WORKSHEET_ERR);

        init(proofWorksheet);

        if (needScopeLines)
            outputBeginScope();

        // we output the ProofWorksheet's $d lines regardless
        // of whether the theorem is new.
        final DistinctVariablesStmt[] dvStmtArray = proofWorksheet
            .getDvStmtArray();

        if (dvStmtArray != null && dvStmtArray.length > 0)
            outputDjVarsLines(dvStmtArray);

        // now logical hypotheses
        if (proofWorksheet.isNewTheorem()) {
            if (proofWorksheet.getHypStepCnt() > 0)
                outputLogHypLines(proofWorksheet);
        }
        else {
            final Theorem theorem = proofWorksheet.getTheorem();
            if (theorem.getLogHypArrayLength() > 0)
                if (storeFormulasAsIs)
                    outputLogHypLines(theorem);
                else
                    outputLogHypLines(theorem, proofWorksheet);
        }

        // we output the ProofWorksheet's description regardless
        // of whether the theorem is new.
        String description = null;
        for (final ProofWorkStmt proofWorkStmt : proofWorksheet
            .getProofWorkStmtList())
            if (proofWorkStmt instanceof CommentStmt) {
                description = proofWorkStmt.getStmtText().toString()
                    .substring(1); // erase "*" at start
                break;
            }
        if (description != null)
            outputDescription(description);

        // now the conclusion formula + start of proof token
        if (proofWorksheet.isNewTheorem() || storeFormulasAsIs)
            outputConclusionLine(proofWorksheet);
        else
            outputConclusionLine(proofWorksheet.getTheorem());

        // we output the ProofWorksheet's proof regardless
        // of whether the theorem is new.
        outputProofLine(proofWorksheet);

        if (needScopeLines)
            outputEndScope();

        return list;
    }

    private void init(final Theorem theorem) {

        init();

        if (theorem.getLogHypArrayLength() > 0
            || theorem.getMandFrame().djVarsArray.length > 0
            || theorem.getOptFrame().djVarsArray.length > 0)
            needScopeLines = true;
    }

    private void init(final ProofWorksheet proofWorksheet) {

        init();

        final DistinctVariablesStmt[] dvStmtArray = proofWorksheet
            .getDvStmtArray();

        if (dvStmtArray != null && dvStmtArray.length > 0)
            needScopeLines = true;
        else if (proofWorksheet.isNewTheorem()) {
            if (proofWorksheet.getHypStepCnt() > 0)
                needScopeLines = true;
        }
        else if (proofWorksheet.getTheorem().getLogHypArrayLength() > 0)
            needScopeLines = true;
    }

    private void init() {
        list = new LinkedList<>();

        needScopeLines = false;

        indentAmt = tlPreferences.storeMMIndentAmt.get();
        rightCol = tlPreferences.storeMMRightCol.get();
        storeFormulasAsIs = tlPreferences.storeFormulasAsIs.get();
    }

    private void outputBeginScope() {
        list.add(startNewLine(indentAmt)
            .append(MMIOConstants.MM_BEGIN_SCOPE_KEYWORD));
    }

    private void outputEndScope() {
        list.add(
            startNewLine(indentAmt).append(MMIOConstants.MM_END_SCOPE_KEYWORD));
    }

    private void outputLogHypLines(final Theorem theorem) {

        final LogHyp[] array = theorem.getLogHypArray();
        for (final LogHyp element : array)
            outputOneLogHypsLines(element);
    }

    /*
        Here we are outputting a theorem's logical hypotheses
        but using the proof worksheet formula formatting because
        "storeFormulasAsIs" was specified. The problem is to
        output the hypotheses in database order, which is not
        necessarily going to be the same as the order they
        appear in the ProofWorksheet.
     */
    private void outputLogHypLines(final Theorem theorem,
        final ProofWorksheet proofWorksheet)
    {

        HypothesisStep hypothesisStep;

        final LogHyp[] logHypArray = theorem.getLogHypArray();
        for (final LogHyp element : logHypArray) {
            hypothesisStep = proofWorksheet.getHypothesisStepFromList(element);
            if (hypothesisStep == null)
                throw new IllegalArgumentException(new TheoremLoaderException(
                    TlConstants.ERRMSG_HYP_MISSING_FOR_EXPORTED_PROOF_WORKSHEET,
                    element.getLabel()));
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
    private void outputLogHypLines(final ProofWorksheet proofWorksheet) {

        for (final ProofWorkStmt w : proofWorksheet.getProofWorkStmtList())
            if (w instanceof HypothesisStep)
                outputOneLogHypsLines((HypothesisStep)w);
    }

    private void outputOneLogHypsLines(final HypothesisStep hypothesisStep) {
        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;

        final StringBuilder prefix = startNewLine(leftOffset);
        prefix.append(hypothesisStep.getRefLabel());
        prefix.append(' ');
        prefix.append(MMIOConstants.MM_LOG_HYP_KEYWORD);

        final int continuationOffset = prefix.length() + indentAmt;

        final StringBuilder stmtText = hypothesisStep.getStmtText();
        final StringBuilder textArea = new StringBuilder(stmtText.capacity());
        textArea.append(stmtText);

        ProofStepStmt.reviseStepHypRefInStmtTextArea(textArea, prefix);

        final String s = textArea.toString();
        final String[] textLines = s.split("\\n");

        for (int i = 0; i < textLines.length - 1; i++)
            if (textLines[i].length() > 0)
                list.add(new StringBuilder(textLines[i]));

        StringBuilder sb = new StringBuilder(rightCol);

        final String lastLine = textLines[textLines.length - 1];

        int finalNonWhitespace = lastLine.length() - 1;
        while (finalNonWhitespace >= 0)
            if (Character.isWhitespace(lastLine.charAt(finalNonWhitespace)))
                finalNonWhitespace--;
            else
                break;
        if (finalNonWhitespace < 0)
            sb.append(lastLine);
        else {
            final int space = finalNonWhitespace + 1;
            if (space < lastLine.length())
                sb.append(lastLine.substring(0, space));
            else
                sb.append(lastLine);
        }
        sb.append(' ');

        if (sb.length()
            + MMIOConstants.MM_END_STMT_KEYWORD.length() > rightCol)
        {
            list.add(sb);
            sb = startNewLine(continuationOffset);
        }
        sb.append(MMIOConstants.MM_END_STMT_KEYWORD);
        list.add(sb);
    }

    private void outputOneLogHypsLines(final LogHyp logHyp) {

        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;

        StringBuilder sb = startNewLine(leftOffset);

        sb.append(logHyp.getLabel());
        sb.append(' ');

        sb.append(MMIOConstants.MM_LOG_HYP_KEYWORD);
        sb.append(' ');

        final int continuationOffset = sb.length();

        final Formula formula = logHyp.getFormula();

        sb = formula.toStringBuilderLineList(list, sb, continuationOffset + 1,
            rightCol, MMIOConstants.MM_END_STMT_KEYWORD);

        list.add(sb);

    }

    private void outputDescription(final String description) {

        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;

        StringBuilder sb = startNewLine(leftOffset);

        sb.append(MMIOConstants.MM_BEGIN_COMMENT_KEYWORD);
        sb.append(' ');

        int col = sb.length();
        final int continuationOffset = col;

        // split description using "\\s" whitespace regular expression.
        final String[] tokenArray = description.split("\\s");

        String token;
        int i = -1;
        while (++i < tokenArray.length) {
            if (tokenArray[i].length() == 0)
                continue;
            token = tokenArray[i];
            if (token.equals(MMIOConstants.MM_LABEL_IN_COMMENT_ESCAPE_STRING))
                if (i < tokenArray.length - 1)
                    token = tokenArray[i] + " " + tokenArray[++i];

            col += token.length();
            if (col > rightCol) {
                list.add(sb);
                sb = startNewLine(continuationOffset);
                col = continuationOffset + token.length();
            }
            sb.append(token);
            if (col < rightCol) {
                sb.append(' ');
                col++;
            }
        }

        if (col + MMIOConstants.MM_END_COMMENT_KEYWORD.length() > rightCol) {

            list.add(sb);
            sb = startNewLine(continuationOffset);
        }

        sb.append(MMIOConstants.MM_END_COMMENT_KEYWORD);
        list.add(sb);
    }

    private void outputConclusionLine(final Theorem theorem) {

        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;

        StringBuilder sb = startNewLine(leftOffset);

        sb.append(theorem.getLabel());
        sb.append(' ');

        sb.append(MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD);
        sb.append(' ');

        final int continuationOffset = sb.length();

        final Formula formula = theorem.getFormula();

        sb = formula.toStringBuilderLineList(list, sb, continuationOffset + 1,
            rightCol, MMIOConstants.MM_START_PROOF_KEYWORD);

        list.add(sb);
    }

    private void outputConclusionLine(final ProofWorksheet proofWorksheet) {
        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;

        final StringBuilder prefix = startNewLine(leftOffset);
        prefix.append(proofWorksheet.getTheoremLabel());
        prefix.append(' ');
        prefix.append(MMIOConstants.MM_PROVABLE_ASSRT_KEYWORD);

        final int continuationOffset = prefix.length() + indentAmt;

        final StringBuilder stmtText = proofWorksheet.getQedStep()
            .getStmtText();
        final StringBuilder textArea = new StringBuilder(stmtText.capacity());
        textArea.append(stmtText);

        ProofStepStmt.reviseStepHypRefInStmtTextArea(textArea, prefix);

        final String s = textArea.toString();
        final String[] textLines = s.split("\\n");

        for (int i = 0; i < textLines.length - 1; i++)
            if (textLines[i].length() > 0)
                list.add(new StringBuilder(textLines[i]));

        StringBuilder sb = new StringBuilder(rightCol);

        final String lastLine = textLines[textLines.length - 1];

        int finalNonWhitespace = lastLine.length() - 1;
        while (finalNonWhitespace >= 0)
            if (Character.isWhitespace(lastLine.charAt(finalNonWhitespace)))
                finalNonWhitespace--;
            else
                break;
        if (finalNonWhitespace < 0)
            sb.append(lastLine);
        else {
            final int space = finalNonWhitespace + 1;
            if (space < lastLine.length())
                sb.append(lastLine.substring(0, space));
            else
                sb.append(lastLine);
        }
        sb.append(' ');

        if (sb.length()
            + MMIOConstants.MM_START_PROOF_KEYWORD.length() > rightCol)
        {
            list.add(sb);
            sb = startNewLine(continuationOffset);
        }
        sb.append(MMIOConstants.MM_START_PROOF_KEYWORD);
        list.add(sb);
    }

    private void outputProofLine(final Theorem theorem) {
        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;
        leftOffset += indentAmt;

        startNewLine(leftOffset);

        final RPNStep[] s = theorem.getProof();
        final RPNStep[] rpn = new RPNStep[s.length];
        for (int i = 0; i < s.length; i++)
            if (s[i] != null)
                rpn[i] = s[i];
        outputProof(rpn, leftOffset, rightCol);
    }

    private void outputProofLine(final ProofWorksheet proofWorksheet) {

        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;
        leftOffset += indentAmt;

        startNewLine(leftOffset);

        outputProof(proofWorksheet.getQedStepProofRPN(), leftOffset, rightCol);
    }

    private void outputProof(final RPNStep[] proof, final int left,
        final int right)
    {

        StringBuilder sb = startNewLine(left);

        String stepLabel;
        int col = left;
        for (final RPNStep element : proof) {
            if (element.backRef > 0)
                stepLabel = Integer.toString(element.backRef);
            else {
                stepLabel = element.stmt == null
                    ? MMIOConstants.MISSING_PROOF_STEP
                    : element.stmt.getLabel();
                if (element.backRef < 0)
                    stepLabel = -element.backRef + ":" + stepLabel;
            }

            col += stepLabel.length();
            if (col > right) {
                list.add(sb);
                sb = startNewLine(left);
                col = left + stepLabel.length();
            }

            sb.append(stepLabel);

            if (col < right) {
                sb.append(' ');
                col++;
            }
            else {
                list.add(sb);
                sb = startNewLine(left);
                col = left;
            }
        }

        if (col + MMIOConstants.MM_END_STMT_KEYWORD.length() > right) {

            list.add(sb);
            sb = startNewLine(left);
        }

        sb.append(MMIOConstants.MM_END_STMT_KEYWORD);

        list.add(sb);
    }
    private void outputDjVarsLines(final Theorem theorem) {

        final List<StringBuilder> djStmtTextList = DjVars
            .buildMetamathDjVarsStatementList(theorem);

        outputDjVarsLines(djStmtTextList);
    }

    private void outputDjVarsLines(
        final DistinctVariablesStmt[] distinctVariableStmtArray)
    {

        final List<StringBuilder> djStmtTextList = DjVars
            .buildMetamathDjVarsStatementList(distinctVariableStmtArray);

        outputDjVarsLines(djStmtTextList);
    }

    private void outputDjVarsLines(final List<StringBuilder> djStmtTextList) {

        int leftOffset = indentAmt;
        if (needScopeLines)
            leftOffset += indentAmt;

        StringBuilder sb = startNewLine(leftOffset);
        int col = leftOffset;

        // if a single $d statement exceeds the line length
        // let it go beyond? is it worth fooling with? no...
        for (final StringBuilder dsb : djStmtTextList) {
            col += dsb.length();
            if (col > rightCol) {
                list.add(sb);
                sb = startNewLine(leftOffset);
                col = leftOffset + dsb.length();
            }
            sb.append(dsb.toString());

            if (col < rightCol) {
                sb.append(' ');
                col++;
            }
        }

        list.add(sb);
    }

    private StringBuilder startNewLine(final int n) {

        final StringBuilder sb = new StringBuilder(rightCol);
        indent(sb, n);
        return sb;
    }

    private void indent(final StringBuilder sb, int n) {
        while (n-- > 0)
            sb.append(' ');
    }
}
