# mmj2

01-Nov-2011 Release

```java
//********************************************************************/
 */  Copyright (C) 2005 thru 2011
 */ //* MEL O'CAT : x178g243 at yahoo.com
 */ //* License terms: GNU General Public License Version 2
 */ //* or any later version
 */ //********************************************************************/
//*4567890123456 (71-character line to adjust editor window) 23456789*/ 
```

The mmj2.zip download is available at: <http://us2.metamath.org:8888/ocat/mmj2/>
The links on this page point to documents contained in the unzipped "`mmj2`" directory.  

## General Documentation:

- [README](README.md) - Information about mmj2, its status, acknowledgements, etc.
- [INSTALL](INSTALL.md) - Detailed instructions for installing mmj2 and taking the next step...
- [Quick Start](quickstart.md) - How to install and start-up mmj2 for the first time  
- [PA User Guide](doc/PAUserGuide/Start.html) - Proof Assistant User Guide.
- [LICENSE](LICENSE.TXT) - GNU GENERAL PUBLIC LICENSE Version 2, June 1991  
- [SunJavaTutorialLicense](SunJavaTutorialLicense.md) - Sun Microsystems license requirements (included because
    mmj.pa.ProofAsstGUI.java has several snippets of code that are very similar, if not identical
    to snippets of code in the Java Tutorial.)
- [CHGLOG.TXT](CHGLOG.TXT) - Log of mmj2 changes, sequenced by software release.  

## User Documentation:

-   [PA User Guide](doc/PAUserGuide/Start.html) - Proof
    Assistant User Guide. Assumes mmj2 INSTALL.TXT has been completed
    and that the user is able to start the mmj2 Proof Assistant GUI
    screen.
-   [mmj2jar\\mmj2PATutorial.bat](mmj2jar/mmj2PATutorial.bat) -
    Interactive tutorial that uses the mmj2 Proof Assistant and a set of
    valid Proof Worksheets containing instructional text.
-   [data\\runparm\\windows\\AnnotatedRunParms.txt](data%5Crunparm%5Cwindows%5CAnnotatedRunParms.txt) -
    This is a valid RunParm file which contains extensive RunParm
    Comment lines that describe every RunParm command defined in mmj2.
    RunParms are commands that either alter user preference settings or
    provide the programs the names of input and output folders/files,
    (like "set.mm"), and which initiate processing, such as initiation
    of the Proof Assistant GUI program. The file name of a RunParm file
    is passed on the command line to the mmj2jar.jar file to run mmj2.
-   [mmj2jar\\RunParmsComplete.txt](mmj2jar/RunParms.txt) - Is
    functionally equivalent to RunParms.txt but contains every possible
    RunParm command either with default settings or "asterisked out"
    (whereas RunParms.txt contains only the necessary minimum.)  
-   [doc\\ProofAssistantGUIQuickHOWTO.html](doc/ProofAssistantGUIQuickHOWTO.html) -
    Brief overview on the use of the Proof Assistant GUI program.  
-   doc\\ProofAssistantTutorial.html - A brief explanation of how to use
    the Proof Assistant Tutorial which consists of commented Proof
    Worksheet files for interactive self-study use within the Proof
    Assistant GUI program.  
-   [doc\\ProofAssistantGUIDetailedInfo.html](doc/ProofAssistantGUIDetailedInfo.html) -
    An in-depth reference documente reviewing of the nitty-gritty
    details of the Proof Assistant GUI screen, its fields and validation
    edits. This is not a pleasant document to read, but it may help
    clarify certain points about what is going on...  
-   [doc\\ProofAsstGUICursorHandling.html](doc/ProofAsstGUICursorHandling.html) -
    A brief explanation of how the Proof Assistant positions the cursor
    in various circumstances. This is another document written in
    "legalese" that you will most likely feel is difficult, poorly
    written and unpleasant :)
-   [doc\\TheoremLoaderOverview.html](doc/TheoremLoaderOverview.html) -
    Information about the new (1-Aug-2008 release) Theorem Loader
    enhancement (plus some info about the new "mmj2 Service" feature!)  
-   [doc\\TextModeFormulaFormatting.html](doc/TextModeFormulaFormatting.html) -
    A lengthy review of "TMFF", the new Text Mode Formula Formatting
    feature. You will want to read this if you plan to alter the default
    TMFF RunParm settings that define the built-in TMFF Formats and
    formatting Schemes -- or if you are dissatisfied with TMFF and want
    to suggest a new algorithm (TMFF was coded in a way that will, it is
    hoped, facilitate adding new formatting algorithms.)
-   [doc\\GMFFDoc\\\*](doc/GMFFDoc) - A set of folders and files that
    document the GMFF -- Graphics Mode Formula Formatting -- (i.e. html)
    feature.  
-   [doc\\StepUnifier.html](doc/StepUnifier.html) - Somewhat technical,
    but with lots of diagrams and examples, StepUnifier.html explains
    unification from the perspective of mmj2.  
-   [doc\\WorkVariables.html](doc/WorkVariables.html) - Explains the new
    Work Variables used in mmj2!
-   [doc\\StepSelectorSearch.html](doc/StepSelectorSearch.html) -
    Documentation of the new Unify menu item on the Proof Assistant GUI.
-   [doc\\UnifyEraseAndRederiveFormulasFeature.html](doc/UnifyEraseAndRederiveFormulasFeature.html) -
    Documentation of the new Unify menu item on the Proof Assistant
    GUI.  
-   [doc\\BottomUpProving-ByNormMegill.html](doc/BottomUpProving-ByNormMegill.html) -
    Tutorial written by Norm Megill about using mmj2 to emulate the
    Metamath "Solitaire" prover, and in general, how to prove theorems
    using the "Bottom Up" method with mmj2.  

## Technically-Oriented (Programmer stuff) Documentation:

-   [MMJ2DirectoryStructure.txt](doc/MMJ2DirectoryStructure.txt) - The
    hierarchy of directories contained in mmj2.zip.  
-   [RunningTheMMJ2TestSuite.html](doc/RunningTheMMJ2TestSuite.html) -
    Cookbook for running the built-in mmj2 tests used to verify each new
    mmj2 software release.
-   doc\\mmj2Service\\ -- The mmj2 Service feature with samples invoking
    mmj2 using the "mmj2 Service" feature.  
-   [MetamathERNotes.html](doc/MetamathERNotes.html) - This document
    provides some very useful information about the nomenclature used in
    the mmj2 programs as well as the various objects ("Entities") and
    their Relationships used to construct the "LogicalSystem" from an
    input .mm database. This may be of some use to the general reader as
    it may be that error messages containing abbreviations such as "Sym"
    will be more comprehensible after reading this document. What this
    document mainly provides is the vital facts such as the two types of
    Sym (Cnst and Var), the two types of Stmt (Hyp and Assrt), the two
    types of Hyp (VarHyp and LogHyp), and so on. (This is the "Rosetta
    Stone" for deciphering the mmj2.lang java code :)  
-   [mmjProofVerification.html](doc/mmjProofVerification.html) - This
    document restates the Metamath Proof Verification algorithm
    (excluding $d 's) and provides two fully worked-out examples showing
    the contents of the Proof Work and Hypothesis stacks at each point
    during the proofs. (It is interesting to note that the proof details
    of even the simplest theorem, "`a1i`", are mind-bogglingly tedious
    and intricate -- virtually impossible to read -- and yet the most
    complex Metamath proofs require a Proof Work stack thousands of
    items deep!!!)  
-   [BasicsOfSyntaxAxiomsAndTypes.html](doc/BasicsOfSyntaxAxiomsAndTypes.html) -
    Introductory information about Metamath Syntax Axioms and Type
    Codes.  
-   [SyntaxAxiomRulesAndConventions.html](doc/SyntaxAxiomRulesAndConventions.html) -
    More about Syntax Axioms plus "The (Previously) Unwritten Rules for
    Syntax Axioms".  
-   [ExampleMetamathDatabase.html](doc/ExampleMetamathDatabase.html) -
    Extensively annotated Metamath file showing syntax declarations.  
-   [ExampleMetamathGrammar.html](doc/ExampleMetamathGrammar.html) - An
    attempt to illustrate a way to create a grammar from the Example
    Metamath Database taking into account the interaction of Type
    Conversion Syntax Axioms with the Notation Syntax Axioms.  
-   [ConsolidatedListOfGrammarValidations.html](doc/ConsolidatedListOfGrammarValidations.html) -
    Grammar validation (edits) performed by mmj2 (see
    `mmj.verify.Grammar.java`).  
-   [EssentialAmbiguityExamples.html](doc/EssentialAmbiguityExamples.html) -
    An interesting exercise in working out ways to create ambiguous
    grammars in Metamath .mm files. Note: mmj2 has very limited
    ambiguity checking now -- that is one of the "stubs" left for later
    due to the great scope and difficulty of the task versus the
    marginal rewards of this effort (how many people actually invent new
    .mm databases? See `mmj.verify.GrammarAmbiguity.java` for more
    information.)  
-   [CreatingGrammarRulesFromSyntaxAxioms.html](doc/CreatingGrammarRulesFromSyntaxAxioms.html) -
    A very involved description of the methods used to generate a
    Grammar from Metamath Syntax Axioms with particular attention
    devoted to Nulls Permitted and Type Conversion Syntax Axioms (and
    combinations thereof).  
-   [GrammarRuleTreeNotes.html](doc/GrammarRuleTreeNotes.html) - This
    document discusses a method used to build a tree structure
    containing all grammar rules for a Metamath database. (Note: use of
    the Earley Parser used now eliminates the need for the "Grammar Rule
    Tree" except for finding duplicate Grammar Rules -- and that could
    be done using a java TreeSet, which would eliminate a fair amount of
    code without appreciably slowing `mmj2.verify.Grammar.java`.)  
-   [EarleyParseFunctionAlgorithm.html](doc/EarleyParseFunctionAlgorithm.html) -
    Code level documentation about `mmj.verify.EarleyParse.java` --
    good, but incomplete, was written before the code (specifically,
    pPredictorTyp and pBringForwardTyp are not discussed in this
    document, but are mentioned in the "javadoc" comments in
    `mmj.verify.EarleyParse.java`.)  
-   [EarleyTreeBuilder.html](doc/EarleyTreeBuilder.html) - An attempt to
    explain the way that a Syntax Tree is constructed from an Earley
    "Completed Item Sets" data structure. Both tasks are tricky, the
    'splaining and the doing. See also
    `mmj.verify.EarleyParse.buildTrees()`. One tricky aspect of the tree
    building operation is that mmj2 allows for production of an
    unlimited number of parse trees when used with an ambiguous grammar
    -- and that complicates the algorithm (it was coded twice -- the
    rewrite happened after an initial recursive method was found to be
    valid but totally incomprehensible to the original programmer.)  
-   [EarleyParseCompletedItemSetsExample.html](doc/EarleyParseCompletedItemSetsExample.html) -
    Documents the contents of an Earley CompletedItemSet for a
    hypothetical formula.  
-   [ProofAssistant-Unification.txt](doc/ProofAssistant-Unification.txt) -
    Notes about Unification for mmj2 and various approaches to the
    problem. Written before the code. Contains tree diagram illustrating
    the geometric approach to understanding unification. This is an
    interesting topic that is worthy of more investigation. For one
    thing, the search strategy employed now by mmj2 will benefit from a
    "rethink" as the size of set.mm doubles and redoubles; a million
    theorem database would likely increase elapsed time for unification
    from 1/10th of a second to perhaps 10 seconds! Thus, an alternative,
    such as a two dimensional search algorithm willbe needed.
-   [UnificationNotes20060104.txt](doc/UnificationNotes20060104.txt) -
    Contains a description of the "signature" metrics dynamically
    computed about theorems and their logical hypotheses. Together these
    "signatures" enable the mmj2 Unification search algorithm to "fast
    fail" (reject) candidate assertions for Unification with a given
    proof step. Instead of having to compare two syntax trees node by
    node for every proof step and database assertion pair, we compare
    the two computed signatures -- which are analogous to hash totals,
    for example. Only candidate assertions that pass the "fast fail"
    comparison tests need to go through the slow and tedious
    node-by-node syntax tree comparison process.
-   [PAFeasibility01RootTotals.txt](doc/PAFeasibility01RootTotals.txt) -
    Statistics from set.mm as of June, 2005 showing the distribution of
    Syntax Axioms in set.mm syntax tree root nodes.  
