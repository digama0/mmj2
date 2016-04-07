//*****************************************************************************/
//* Copyright (C) 2005-2013                                                   */
//* MEL O'CAT  X178G243 (at) yahoo (dot) com                                  */
//* License terms: GNU General Public License Version 2                       */
//*                or any later version                                       */
//*****************************************************************************/
//*456789012345678 (80-character line to adjust editor window) 456789012345678*/

/*
 * SearchArgs.java  0.01 20/09/2012
 *
 * Version 0.01:
 * Aug-09-2013: new from decompilation.
 */

package mmj.search;

import java.util.List;

import mmj.lang.*;
import mmj.pa.*;
import mmj.pa.StepRequest.StepRequestType;
import mmj.verify.VerifyProofs;

public class SearchArgs {
    List<Assrt> sortedAssrtSearchList = null;
    ProofWorksheet proofWorksheet;
    StepRequest stepRequest;
    boolean stepSearchMode;
    DerivationStep stepSearchStmt;
    Stmt searchReferenceStmt;
    int searchMaxSeq;
    String theoremLabel;
    String locAfterLabel;
    String generalSearchStmtLabel;
    ScopeFrame comboFrame;
    OrSeparator orSeparator = new OrSeparator();
    SingleQuote singleQuote = new SingleQuote();
    DoubleQuote doubleQuote = new DoubleQuote();
    InWhat[] inWhat = {new InWhat(0), new InWhat(1), new InWhat(2),
            new InWhat(3)};
    Part[] part = {new Part(0), new Part(1), new Part(2), new Part(3)};
    Format[] format = {new Format(0), new Format(1), new Format(2),
            new Format(3)};
    Oper[] oper = {new Oper(0), new Oper(1), new Oper(2), new Oper(3)};
    ForWhat[] forWhat = {new ForWhat(0), new ForWhat(1), new ForWhat(2),
            new ForWhat(3)};
    Bool[] bool = {new Bool(0), new Bool(1), new Bool(2), new Bool(3)};
    ExclLabels exclLabels = new ExclLabels();
    MinProofRefs minProofRefs = new MinProofRefs();
    ResultsChecked resultsChecked = new ResultsChecked();
    MaxTime maxTime = new MaxTime();
    MinHyps minHyps = new MinHyps();
    MaxExtResults maxExtResults = new MaxExtResults();
    Substitutions substitutions = new Substitutions();
    MaxHyps maxHyps = new MaxHyps();
    MaxIncompHyps maxIncompHyps = new MaxIncompHyps();
    Comments comments = new Comments();
    MaxResults maxResults = new MaxResults();
    PrevStepsChecked prevStepsChecked = new PrevStepsChecked();
    AutoSelect autoSelect = new AutoSelect();
    ReuseDerivSteps reuseDerivSteps = new ReuseDerivSteps();
    Stats stats = new Stats();
    ChapSecHierarchy chapSecHierarchy = new ChapSecHierarchy();
    FromChap fromChap = new FromChap();
    FromSec fromSec = new FromSec();
    ThruChap thruChap = new ThruChap();
    ThruSec thruSec = new ThruSec();
    OutputSort outputSort = new OutputSort();
    SearchArgsField[] arg = new SearchArgsField[]{orSeparator, singleQuote,
            doubleQuote, inWhat[0], part[0], format[0], oper[0], forWhat[0],
            bool[0], inWhat[1], part[1], format[1], oper[1], forWhat[1],
            bool[1], inWhat[2], part[2], format[2], oper[2], forWhat[2],
            bool[2], inWhat[3], part[3], format[3], oper[3], forWhat[3],
            bool[3], exclLabels, minProofRefs, resultsChecked, maxTime, minHyps,
            maxExtResults, substitutions, maxHyps, maxIncompHyps, comments,
            maxResults, prevStepsChecked, autoSelect, chapSecHierarchy,
            reuseDerivSteps, stats, fromChap, fromSec, thruChap, thruSec,
            outputSort};

    public SearchArgs() {
        initSearchArgsToDefaults();
    }

    public CompiledSearchArgs compile(final SearchMgr searchMgr,
        final BookManager bookManager, final SearchOutput searchOutput,
        final ProofAsst proofAsst,
        final ProofAsstPreferences proofAsstPreferences,
        final VerifyProofs verifyProofs, final Cnst cnst)
    {
        return new CompiledSearchArgs(this, searchMgr, bookManager,
            searchOutput, proofAsst, proofAsstPreferences, verifyProofs, cnst);
    }

    void initSearchArgsToDefaults() {
        initSearchKeys();
        for (final SearchArgsField element : arg)
            element.set(SearchOptionsConstants.FIELD_ATTR[element
                .getFieldId()].defaultText);

    }

    void initSearchKeys() {
        proofWorksheet = null;
        stepRequest = null;
        stepSearchMode = false;
        stepSearchStmt = null;
        searchReferenceStmt = null;
        searchMaxSeq = Integer.MAX_VALUE;
        theoremLabel = null;
        locAfterLabel = null;
        generalSearchStmtLabel = null;
        comboFrame = null;
    }

    void initSearchKeys(final LogicalSystem logicalSystem) {
        initSearchKeys();
        loadDefaultComboFrame(logicalSystem);
    }

    void loadSearchKeys(final Stmt stmt, final LogicalSystem logicalSystem) {
        initSearchKeys(logicalSystem);
        if (stmt != null) {
            generalSearchStmtLabel = stmt.getLabel();
            searchMaxSeq = stmt.getSeq();
            searchReferenceStmt = stmt;
            loadComboFrame(stmt, logicalSystem);
        }
    }

    void loadSearchKeys(final ProofWorksheet w,
        final LogicalSystem logicalSystem)
    {
        initSearchKeys(logicalSystem);
        if (w == null)
            return;
        proofWorksheet = w;
        searchReferenceStmt = w.getTheorem();
        if (searchReferenceStmt == null) {
            if (locAfterLabel != null && !locAfterLabel.equals("")
                && !locAfterLabel.equals(PaConstants.DEFAULT_STMT_LABEL))
                searchReferenceStmt = logicalSystem.getStmtTbl()
                    .get(locAfterLabel);
        }
        else
            loadComboFrame(searchReferenceStmt, logicalSystem);
        theoremLabel = w.getTheoremLabel();
        locAfterLabel = w.getLocAfterLabel();
        searchMaxSeq = w.getMaxSeq();
        stepRequest = w.getStepRequest();
        if (stepRequest == null)
            return;
        if (stepRequest.type == StepRequestType.StepSearch
            || stepRequest.type == StepRequestType.SearchOptions
                && stepRequest.param1 != null)
        {
            stepSearchMode = true;
            stepSearchStmt = (DerivationStep)stepRequest.param1;
        }
    }

    void loadComboFrame(final Stmt stmt, final LogicalSystem logicalSystem) {
        if (stmt instanceof Assrt)
            comboFrame = ((Assrt)stmt).getMandFrame();
        else
            loadDefaultComboFrame(logicalSystem);
    }

    void loadDefaultComboFrame(final LogicalSystem logicalSystem) {
        comboFrame = new ScopeFrame(logicalSystem.getScopeDefList().get(0),
            searchMaxSeq);
    }
}
