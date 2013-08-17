$(
#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
    Metamath source file for logic, set theory, numbers, and Hilbert space
#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#

setFirst100.mm - cloned from set.mm - Version of 14-Sep-2006
               - added $t info on Nov-09-2011 (from set.mm)

                             PUBLIC DOMAIN

This file (specifically, the version of this file with the above date)
has been released into the Public Domain per the Creative Commons Public
Domain Dedication. http://creativecommons.org/licenses/publicdomain/

Norman Megill - email: nm(at)alum(dot)mit(dot)edu

$)

$( <MM> <MODULE> <ID>LOGICB</ID> <PREREQ> </PREREQ> </MODULE> </MM> $)
$(
#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
                           Pre-logic
#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
$)

  $( Declare the primitive constant symbols for propositional calculus. $)
  $c ( $.  $( Left parenthesis $)
  $c ) $.  $( Right parenthesis $)
  $c -> $. $( Right arrow (read:  'implies') $)
  $c -. $. $( Right handle (read:  'not') $)
  $c wff $. $( Well-formed formula symbol (read:  'the following symbol
               sequence is a wff') $)
  $c |- $. $( Turnstile (read:  'the following symbol sequence is provable' or
              'a proof exists for') $)

  $( Introduce some variable names we will use to represent well-formed
     formulas (wff's). $)
  $v ph $. $( Greek phi $)
  $v ps $.  $( Greek psi $)
  $v ch $.  $( Greek chi $)
  $v th $.  $( Greek theta $)
  $v ta $.  $( Greek tau $)

  $( Specify some variables that we will use to represent wff's.
     The fact that a variable represents a wff is relevant only to a theorem
     referring to that variable, so we may use $f hypotheses.  The symbol
     ` wff ` specifies that the variable that follows it represents a wff. $)
  $( Let variable ` ph ` be a wff. $)
  wph $f wff ph $.
  $( Let variable ` ps ` be a wff. $)
  wps $f wff ps $.
  $( Let variable ` ch ` be a wff. $)
  wch $f wff ch $.
  $( Let variable ` th ` be a wff. $)
  wth $f wff th $.
  $( Let variable ` ta ` be a wff. $)
  wta $f wff ta $.

$(
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
          Dummy link theorem for assisting proof development
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
$)

  ${
    dummylink.1 $e |- ph $.
    dummylink.2 $e |- ps $.
    $( Dummy link theorem for assisting proof development.  The Metamath
       program's Proof Assistant requires proofs to be developed backwards from
       the conclusion with no gaps, and it has no mechanism that lets the user
       to work on isolated subproofs.  This theorem provides a workaround for
       this limitation.  It can be inserted at any point in a proof to allow an
       independent subproof to be developed on the side, for later as part of
       the final proof.

       Instructions:  (1) Assign this theorem to any unknown step in the proof.
       (2) Continue to develop the main proof from hypothesis dummylink.1,
       which will have a replication of the unknown proof step.  (3) Develop
       the independent subproof backwards from hypothesis dummylink.2.  If
       desired, use a 'let' command to pre-assign the conclusion of the
       independent subproof to dummylink.2.  (3) Use 'improve all' to assign a
       completed subproof to an unknown step in the main proof that matches it.
       (4) After the entire proof is complete, use 'minimize */brief' to clean
       up (discard) all dummylink references.

       (This theorem was designed to assist importing partially completed
       Proof Worksheets from Mel O'Cat's "mmj2" Proof Assistant GUI, but it
       can also be useful on its own.  Interestingly, this "theorem" - or more
       precisely, inference - requires no axioms for its proof.  It serves no
       purpose in a completed proof, since it is always redundant, and can be
       ignored if you are using this database to learn logic.) $)
    dummylink $p |- ph $=
      (  ) C $.
      $( [8-Feb-2006] $) $( [7-Feb-2006] $)
  $}

$(
#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
                           Propositional calculus
#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#
$)

$(
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        Recursively define primitive wff for propositional calculus
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
$)

  $( If ` ph ` is a wff, so is ` -. ph ` or "not ` ph ` ".  Part of the
     recursive definition of a wff (well-formed formula).  In classical logic
     (which is our logic), a wff is interpreted as either true or false.
     So if ` ph ` is true, then ` -. ph ` is false; if ` ph ` is false, then
     ` -. ph ` is true.  Traditionally, Greek letters are used to represent
     wffs, and we follow this convention.  In propositional calculus, we define
     only wffs built up from other wffs, i.e. there is no starting or "atomic"
     wff.  Later, in predicate calculus, we will extend the basic wff
     definition by including atomic wffs ( ~ weq and ~ wel ). $)
  wn $a wff -. ph $.

  $( If ` ph ` and ` ps ` are wff's, so is ` ( ph -> ps ) ` or " ` ph ` implies
     ` ps ` ."  Part of the recursive definition of a wff.  The resulting wff
     is (interpreted as) false when ` ph ` is true and ` ps ` is false; it is
     true otherwise.  (Think of the truth table for an OR gate with input
     ` ph ` connected through an inverter.)  The left-hand wff is called the
     antecedent, and the right-hand wff is called the consequent.  In the case
     of ` ( ph -> ( ps -> ch ) ) ` , the middle ` ps ` may be informally called
     either an antecedent or part of the consequent depending on context. $)
  wi $a wff ( ph -> ps ) $.

$(
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        The axioms of propositional calculus
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
$)

  $(
     Postulate the three axioms of classical propositional calculus.
  $)

  $( Axiom _Simp_.  Axiom A1 of [Margaris] p. 49.  One of the 3 axioms of
     propositional calculus.  The 3 axioms are also given as Definition 2.1
     of [Hamilton] p. 28.  This axiom is called _Simp_ or "the principle of
     simplification" in _Principia Mathematica_ (Theorem *2.02 of
     [WhiteheadRussell] p. 100) because "it enables us to pass from the joint
     assertion of ` ph ` and ` ps ` to the assertion of ` ph ` simply."

     Propositional calculus (axioms ~ ax-1 through ~ ax-3 and rule ~ ax-mp )
     can be thought of as asserting formulas that are universally "true" when
     their variables are replaced by any combination of "true" and "false".
     Propositional calculus was first formalized by Frege in 1879, using as
     his axioms (in addition to rule ~ ax-mp ) the wffs ~ ax-1 , ~ ax-2 ,
     ~ pm2.04 , ~ con3 , ~ nega , and ~ negb .  Around 1930, Lukasiewicz
     simplified the system by eliminating the third (which follows from the
     first two, as you can see by looking at the proof of ~ pm2.04 ) and
     replacing the last three with our ~ ax-3 .  (Thanks to Ted Ulrich
     for this information.)

     The theorems of propositional calculus are also called _tautologies_.
     Tautologies can be proved very simply using truth tables, based on the
     true/false interpretation of propositional calculus.  To do this, we
     assign all possible combinations of true and false to the wff variables
     and verify that the result (using the rules described in ~ wi and ~ wn )
     always evaluates to true.  This is called the _semantic_ approach.  Our
     approach is called the _syntactic_ approach, in which everything is
     derived from axioms.  A metatheorem called the Completeness Theorem for
     Propositional Calculus shows that the two approaches are equivalent and
     even provides an algorithm for automatically generating syntactic proofs
     from a truth table.  Those proofs, however, tend to be long, and the
     much shorter proofs that we show here were found manually. $)
  ax-1 $a |- ( ph -> ( ps -> ph ) ) $.

  $( Axiom _Frege_.  Axiom A2 of [Margaris] p. 49.  One of the 3 axioms of
     propositional calculus.  It distributes an antecedent over two
     consequents.  This axiom was part of Frege's original system and is known
     as _Frege_ in the literature.  It is also proved as Theorem *2.77 of
     [WhiteheadRussell] p. 108.  $)
  ax-2 $a |- ( ( ph -> ( ps -> ch ) ) -> ( ( ph -> ps ) -> ( ph -> ch ) ) ) $.

  $( Axiom _Transp_.  Axiom A3 of [Margaris] p. 49.  One of the 3 axioms of
     propositional calculus.  It swaps or transposes the order of the
     consequents when negation is removed.  An informal example is that the
     statement "if there are no clouds in the sky, it is not raining" implies
     the statement "if it is raining, there are clouds in the sky."  This
     axiom is called _Transp_ or "the principle of transposition" in
     _Principia Mathematica_ (Theorem *2.17 of [WhiteheadRussell] p. 103).  $)
  ax-3 $a |- ( ( -. ph -> -. ps ) -> ( ps -> ph ) ) $.

  $(
     Postulate the modus ponens rule of inference.
  $)

  ${
    $( Minor premise for modus ponens. $)
    min $e |- ph $.
    $( Major premise for modus ponens. $)
    maj $e |- ( ph -> ps ) $.
    $( Rule of Modus Ponens. The postulated inference rule of propositional
       calculus.  See e.g. Rule 1 of [Hamilton] p. 73.  The rule says, "if
       ` ph ` is true, and ` ph ` implies ` ps ` , then ` ps ` must also be
       true."  This rule is sometimes called "detachment", since it detaches
       the minor premise from the major premise. $)
    ax-mp $a |- ps $.
  $}

  $(
     ~ ax-1 , ~ ax-2 , ~ ax-3 , and ~ ax-mp are the complete set of
     postulates for propositional calculus.  Some additional $a statements
     are used later to introduce defined terms.
  $)

$(
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        Logical implication
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
$)

$( The results in this section make use of the first 2 axioms only.  In
   an implication, the wff before the arrow is called the 'antecedent'
   and the wff after the arrow is called the 'consequent.' $)

$( We will use the following descriptive terms very loosely:  A 'theorem'
   usually has no $e hypotheses.  An 'inference' has one or more $e hypotheses.
   A 'deduction' is an inference in which the hypotheses and the result
   share the same antecedent. $)

  ${
    $( Premise for ~ a1i . $)
    a1i.1 $e |- ph $.
    $( Inference derived from axiom ~ ax-1 .  See ~ a1d for an explanation of
       our informal use of the terms "inference" and "deduction". $)
    a1i $p |- ( ps -> ph ) $=
      ( wi ax-1 ax-mp ) ABADCABEF $.
      $( [5-Aug-1993] $)
  $}

  ${
    $( Premise for ~ a2i . $)
    a2i.1 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Inference derived from axiom ~ ax-2 . $)
    a2i $p |- ( ( ph -> ps ) -> ( ph -> ch ) ) $=
      ( wi ax-2 ax-mp ) ABCEEABEACEEDABCFG $.
      $( [5-Aug-1993] $)
  $}

  ${
    $( First of 2 premises for ~ syl . $)
    syl.1 $e |- ( ph -> ps ) $.
    $( Second of 2 premises for ~ syl . $)
    syl.2 $e |- ( ps -> ch ) $.
    $( An inference version of the transitive laws for implication ~ imim2 and
       ~ imim1 , which Russell and Whitehead call "the principle of the
       syllogism...because...the syllogism in Barbara is derived from them"
       (quote after Theorem *2.06 of [WhiteheadRussell] p. 101).

       (A bit of trivia:  this is the most commonly referenced assertion in our
       database.  In second place is ~ ax-mp , followed by ~ visset , ~ bitr ,
       ~ imp , and ~ ex . The Metamath program command 'show usage' shows the
       number of references.)  $)
    syl $p |- ( ph -> ch ) $=
      ( wi a1i a2i ax-mp ) ABFACFDABCBCFAEGHI $.
      $( [5-Aug-1993] $)
  $}

  ${
    $( Premise for ~ com12 . $)
    com12.1 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Inference that swaps (commutes) antecedents in an implication. $)
    com12 $p |- ( ps -> ( ph -> ch ) ) $=
      ( wi ax-1 a2i syl ) BABEACEBAFABCDGH $.
      $( [5-Aug-1993] $)
  $}

  ${
    a1d.1 $e |- ( ph -> ps ) $.
    $( Deduction introducing an embedded antecedent. (The proof was revised by
       Stefan Allan, 20-Mar-06.)

       _Naming convention_:  We often call a theorem a "deduction" and suffix
       its label with "d" whenever the hypotheses and conclusion are each
       prefixed with the same antecedent.  This allows us to use the theorem in
       places where (in traditional textbook formalizations) the standard
       Deduction Theorem would be used; here ` ph ` would be replaced with a
       conjunction ( ~ df-an ) of the hypotheses of the would-be deduction.  By
       contrast, we tend to call the simpler version with no common antecedent
       an "inference" and suffix its label with "i"; compare theorem ~ a1i .
       Finally, a "theorem" would be the form with no hypotheses; in this case
       the "theorem" form would be the original axiom ~ ax-1 . In propositional
       calculus we usually prove the theorem form first without a suffix on its
       label (e.g.  ~ pm2.43 vs.  ~ pm2.43i vs.  ~ pm2.43d ), but (much) later
       we often suffix the theorem form's label with "t" as in ~ negnegt vs.
       ~ negneg , especially when our "weak deduction theorem" ~ dedth is used
       to prove the theorem form from its inference form.  When an inference is
       converted to a theorem by eliminating an "is a set" hypothesis, we
       sometimes suffix the theorem form with "g" (for somewhat misnamed
       "generalized") as in ~ uniex vs.  ~ uniexg . $)
    a1d $p |- ( ph -> ( ch -> ps ) ) $=
      ( wi ax-1 syl ) ABCBEDBCFG $.
      $( [20-Mar-2006] $)  $( [5-Aug-1993] $)
  $}

  ${
    a2d.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    $( Deduction distributing an embedded antecedent. $)
    a2d $p |- ( ph -> ( ( ps -> ch ) -> ( ps -> th ) ) ) $=
      ( wi ax-2 syl ) ABCDFFBCFBDFFEBCDGH $.
      $( [23-Jun-1994] $)
  $}

  $( A closed form of syllogism (see ~ syl ).  Theorem *2.05 of
     [WhiteheadRussell] p. 100. $)
  imim2 $p |- ( ( ph -> ps ) -> ( ( ch -> ph ) -> ( ch -> ps ) ) ) $=
    ( wi ax-1 a2d ) ABDZCABGCEF $.
    $( [5-Aug-1993] $)

  $( A closed form of syllogism (see ~ syl ).  Theorem *2.06 of
     [WhiteheadRussell] p. 100. $)
  imim1 $p |- ( ( ph -> ps ) -> ( ( ps -> ch ) -> ( ph -> ch ) ) ) $=
    ( wi imim2 com12 ) BCDABDACDBCAEF $.
    $( [5-Aug-1993] $)

  ${
    imim1i.1 $e |- ( ph -> ps ) $.
    $( Inference adding common consequents in an implication, thereby
       interchanging the original antecedent and consequent. $)
    imim1i $p |- ( ( ps -> ch ) -> ( ph -> ch ) ) $=
      ( wi imim1 ax-mp ) ABEBCEACEEDABCFG $.
      $( [5-Aug-1993] $)

    $( Inference adding common antecedents in an implication. $)
    imim2i $p |- ( ( ch -> ph ) -> ( ch -> ps ) ) $=
      ( wi a1i a2i ) CABABECDFG $.
      $( [5-Aug-1993] $)
  $}

  ${
    imim12i.1 $e |- ( ph -> ps ) $.
    imim12i.2 $e |- ( ch -> th ) $.
    $( Inference joining two implications. $)
    imim12i $p |- ( ( ps -> ch ) -> ( ph -> th ) ) $=
      ( wi imim2i imim1i syl ) BCGBDGADGCDBFHABDEIJ $.
      $( [5-Aug-1993] $)
  $}

  ${
    3syl.1 $e |- ( ph -> ps ) $.
    3syl.2 $e |- ( ps -> ch ) $.
    3syl.3 $e |- ( ch -> th ) $.
    $( Inference chaining two syllogisms. $)
    3syl $p |- ( ph -> th ) $=
      ( syl ) ACDABCEFHGH $.
      $( [5-Aug-1993] $)
  $}

  ${
    syl5.1 $e |- ( ph -> ( ps -> ch ) ) $.
    syl5.2 $e |- ( th -> ps ) $.
    $( A syllogism rule of inference.  The second premise is used to replace
       the second antecedent of the first premise. $)
    syl5 $p |- ( ph -> ( th -> ch ) ) $=
      ( wi imim1i syl ) ABCGDCGEDBCFHI $.
      $( [5-Aug-1993] $)
  $}

  ${
    syl6.1 $e |- ( ph -> ( ps -> ch ) ) $.
    syl6.2 $e |- ( ch -> th ) $.
    $( A syllogism rule of inference.  The second premise is used to replace
       the consequent of the first premise. $)
    syl6 $p |- ( ph -> ( ps -> th ) ) $=
      ( wi imim2i syl ) ABCGBDGECDBFHI $.
      $( [5-Aug-1993] $)
  $}

  ${
    syl7.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    syl7.2 $e |- ( ta -> ch ) $.
    $( A syllogism rule of inference.  The second premise is used to replace
       the third antecedent of the first premise. $)
    syl7 $p |- ( ph -> ( ps -> ( ta -> th ) ) ) $=
      ( wi imim1i syl6 ) ABCDHEDHFECDGIJ $.
      $( [5-Aug-1993] $)
  $}

  ${
    syl8.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    syl8.2 $e |- ( th -> ta ) $.
    $( A syllogism rule of inference.  The second premise is used to replace
       the consequent of the first premise. $)
    syl8 $p |- ( ph -> ( ps -> ( ch -> ta ) ) ) $=
      ( wi imim2i syl6 ) ABCDHCEHFDECGIJ $.
      $( [1-Aug-1994] $)
  $}

  ${
    imim1d.1 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Deduction adding nested antecedents. $)
    imim2d $p |- ( ph -> ( ( th -> ps ) -> ( th -> ch ) ) ) $=
      ( wi a1d a2d ) ADBCABCFDEGH $.
      $( [5-Aug-1993] $)
  $}

  ${
    syld.1 $e |- ( ph -> ( ps -> ch ) ) $.
    syld.2 $e |- ( ph -> ( ch -> th ) ) $.
    $( Syllogism deduction.  (The proof was shortened by Mel L. O'Cat,
       7-Aug-04.) $)
    syld $p |- ( ph -> ( ps -> th ) ) $=
      ( wi imim2d a2i ax-mp ) ABCGZGABDGZGEAKLACDBFHIJ $.
      $( [9-Aug-2004] $) $( [5-Aug-1993] $)
  $}

  ${
    imim2d.1 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Deduction adding nested consequents. $)
    imim1d $p |- ( ph -> ( ( ch -> th ) -> ( ps -> th ) ) ) $=
      ( wi imim1 syl ) ABCFCDFBDFFEBCDGH $.
      $( [3-Apr-1994] $)
  $}

  ${
    imim12d.1 $e |- ( ph -> ( ps -> ch ) ) $.
    imim12d.2 $e |- ( ph -> ( th -> ta ) ) $.
    $( Deduction combining antecedents and consequents. $)
    imim12d $p |- ( ph -> ( ( ch -> th ) -> ( ps -> ta ) ) ) $=
      ( wi imim1d imim2d syld ) ACDHBDHBEHABCDFIADEBGJK $.
      $( [7-Aug-1994] $)
  $}

  $( Swap antecedents.  Theorem *2.04 of [WhiteheadRussell] p. 100. $)
  pm2.04 $p |- ( ( ph -> ( ps -> ch ) ) -> ( ps -> ( ph -> ch ) ) ) $=
    ( wi ax-2 ax-1 syl5 ) ABCDDABDACDBABCEBAFG $.
    $( [5-Aug-1993] $)

  $( Theorem *2.83 of [WhiteheadRussell] p. 108. $)
  pm2.83 $p |-  ( ( ph -> ( ps -> ch ) ) -> ( ( ph -> ( ch -> th ) ) ->
                ( ph -> ( ps -> th ) ) ) ) $=
    ( wi imim1 imim2i a2d ) ABCEZEACDEZBDEZIJKEABCDFGH $.
    $( [13-Jan-2005] $) $( [3-Jan-2005] $)

  ${
    com3.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    $( Commutation of antecedents.  Swap 2nd and 3rd. $)
    com23 $p |- ( ph -> ( ch -> ( ps -> th ) ) ) $=
      ( wi pm2.04 syl ) ABCDFFCBDFFEBCDGH $.
      $( [5-Aug-1993] $)

    $( Commutation of antecedents.  Swap 1st and 3rd. $)
    com13 $p |- ( ch -> ( ps -> ( ph -> th ) ) ) $=
      ( wi com12 com23 ) BCADFBACDABCDFEGHG $.
      $( [25-Apr-1994] $)

    $( Commutation of antecedents.  Rotate left. $)
    com3l $p |- ( ps -> ( ch -> ( ph -> th ) ) ) $=
      ( com23 com13 ) ACBDABCDEFG $.
      $( [25-Apr-1994] $)

    $( Commutation of antecedents.  Rotate right. $)
    com3r $p |- ( ch -> ( ph -> ( ps -> th ) ) ) $=
      ( com3l ) BCADABCDEFF $.
      $( [25-Apr-1994] $)
  $}

  ${
    com4.1 $e |- ( ph -> ( ps -> ( ch -> ( th -> ta ) ) ) ) $.
    $( Commutation of antecedents. Swap 3rd and 4th. $)
    com34 $p |- ( ph -> ( ps -> ( th -> ( ch -> ta ) ) ) ) $=
      ( wi pm2.04 syl6 ) ABCDEGGDCEGGFCDEHI $.
      $( [25-Apr-1994] $)

    $( Commutation of antecedents. Swap 2nd and 4th. $)
    com24 $p |- ( ph -> ( th -> ( ch -> ( ps -> ta ) ) ) ) $=
      ( wi com34 com23 ) ADBCEABDCEGABCDEFHIH $.
      $( [25-Apr-1994] $)

    $( Commutation of antecedents. Swap 1st and 4th. $)
    com14 $p |- ( th -> ( ps -> ( ch -> ( ph -> ta ) ) ) ) $=
      ( wi com34 com13 ) DBACEABDCEGABCDEFHIH $.
      $( [25-Apr-1994] $)

    $( Commutation of antecedents.  Rotate left.  (The proof
       was shortened by Mel L. O'Cat, 15-Aug-04.) $)
    com4l $p |- ( ps -> ( ch -> ( th -> ( ph -> ta ) ) ) ) $=
      ( wi com14 com3l ) DBCAEGABCDEFHI $.
      $( [16-Aug-2004] $) $( [25-Apr-1994] $)

    $( Commutation of antecedents.  Rotate twice. $)
    com4t $p |- ( ch -> ( th -> ( ph -> ( ps -> ta ) ) ) ) $=
      ( com4l ) BCDAEABCDEFGG $.
      $( [25-Apr-1994] $)

    $( Commutation of antecedents.  Rotate right. $)
    com4r $p |- ( th -> ( ph -> ( ps -> ( ch -> ta ) ) ) ) $=
      ( com4t com4l ) CDABEABCDEFGH $.
      $( [25-Apr-1994] $)
  $}

  ${
    a1dd.1 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Deduction introducing a nested embedded antecedent. $)
    a1dd $p |- ( ph -> ( ps -> ( th -> ch ) ) ) $=
      ( wi a1d com23 ) ADBCABCFDEGH $.
      $( [18-Dec-2004] $) $( [17-Dec-2004] $)
  $}

  ${
    mp2.1 $e |- ph $.
    mp2.2 $e |- ps $.
    mp2.3 $e |- ( ph -> ( ps -> ch ) ) $.
    $( A double modus ponens inference. $)
    mp2 $p |- ch $=
      ( wi ax-mp ) BCEABCGDFHH $.
      $( [5-Apr-1994] $)
  $}

  ${
    mpd.1 $e |- ( ph -> ps ) $.
    mpd.2 $e |- ( ph -> ( ps -> ch ) ) $.
    $( A modus ponens deduction. $)
    mpd $p |- ( ph -> ch ) $=
      ( wi a2i ax-mp ) ABFACFDABCEGH $.
      $( [5-Aug-1993] $)
  $}

  ${
    mpi.1 $e |- ps $.
    mpi.2 $e |- ( ph -> ( ps -> ch ) ) $.
    $( A nested modus ponens inference.  (The proof was shortened by Stefan
       Allan, 20-Mar-06. $)
    mpi $p |- ( ph -> ch ) $=
      ( a1i mpd ) ABCBADFEG $.
      $( [20-Mar-2006] $)  $( [5-Aug-1993] $)
  $}

  ${
    mpii.1 $e |- ch $.
    mpii.2 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    $( A doubly nested modus ponens inference. $)
    mpii $p |- ( ph -> ( ps -> th ) ) $=
      ( wi com23 mpi ) ACBDGEABCDFHI $.
      $( [31-Dec-1993] $)
  $}

  ${
    mpdd.1 $e |- ( ph -> ( ps -> ch ) ) $.
    mpdd.2 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    $( A nested modus ponens deduction. $)
    mpdd $p |- ( ph -> ( ps -> th ) ) $=
      ( wi a2d mpd ) ABCGBDGEABCDFHI $.
      $( [13-Dec-2004] $) $( [12-Dec-2004] $)
  $}

  ${
    mpid.1 $e |- ( ph -> ch ) $.
    mpid.2 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    $( A nested modus ponens deduction. $)
    mpid $p |- ( ph -> ( ps -> th ) ) $=
      ( a1d mpdd ) ABCDACBEGFH $.
      $( [16-Dec-2004] $) $( [14-Dec-2004] $)
  $}

  ${
    mpdi.1 $e |- ( ps -> ch ) $.
    mpdi.2 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    $( A nested modus ponens deduction. $)
    mpdi $p |- ( ph -> ( ps -> th ) ) $=
      ( wi com12 mpid ) BADBACDEABCDGFHIH $.
      $( [18-Apr-2005] $) $( [16-Apr-2005] $)
  $}

  ${
    mpcom.1 $e |- ( ps -> ph ) $.
    mpcom.2 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Modus ponens inference with commutation of antecedents. $)
    mpcom $p |- ( ps -> ch ) $=
      ( com12 mpd ) BACDABCEFG $.
      $( [17-Mar-1996] $)
  $}

  ${
    syldd.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    syldd.2 $e |- ( ph -> ( ps -> ( th -> ta ) ) ) $.
    $( Nested syllogism deduction. $)
    syldd $p |- ( ph -> ( ps -> ( ch -> ta ) ) ) $=
      ( wi imim2 syl6 mpdd ) ABCDHZCEHZFABDEHLMHGDECIJK $.
      $( [13-Dec-2004] $) $( [12-Dec-2004] $)
  $}

  ${
    sylcom.1 $e |- ( ph -> ( ps -> ch ) ) $.
    sylcom.2 $e |- ( ps -> ( ch -> th ) ) $.
    $( Syllogism inference with commutation of antecedents.  (The proof was
       shortened by Mel O'Cat, 2-Feb-06 and shortened further by Stefan Allan,
       23-Feb-06.) $)
    sylcom $p |- ( ph -> ( ps -> th ) ) $=
      ( wi a2i syl ) ABCGBDGEBCDFHI $.
      $( [24-Feb-2006] $) $( [29-Aug-2004] $)
  $}

  ${
    syl5com.2 $e |- ( ph -> ( ps -> ch ) ) $.
    syl5com.1 $e |- ( th -> ps ) $.
    $( Syllogism inference with commuted antecedents. $)
    syl5com $p |- ( th -> ( ph -> ch ) ) $=
      ( a1d sylcom ) DABCDBAFGEH $.
      $( [25-May-2005] $) $( [24-May-2005] $)
  $}

  ${
    syl6com.1 $e |- ( ph -> ( ps -> ch ) ) $.
    syl6com.2 $e |- ( ch -> th ) $.
    $( Syllogism inference with commuted antecedents. $)
    syl6com $p |- ( ps -> ( ph -> th ) ) $=
      ( syl6 com12 ) ABDABCDEFGH $.
      $( [26-May-2005] $) $( [25-May-2005] $)
  $}

  ${
    syli.1 $e |- ( ps -> ( ph -> ch ) ) $.
    syli.2 $e |- ( ch -> ( ph -> th ) ) $.
    $( Syllogism inference with common nested antecedent. $)
    syli $p |- ( ps -> ( ph -> th ) ) $=
      ( com12 sylcom ) BACDECADFGH $.
      $( [5-Nov-2004] $) $( [4-Nov-2004] $)
  $}

  ${
    syl5d.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    syl5d.2 $e |- ( ph -> ( ta -> ch ) ) $.
    $( A nested syllogism deduction.  (The proof was shortened by Josh
       Purinton, 29-Dec-00 and shortened further by Mel O'Cat, 2-Feb-06.) $)
    syl5d $p |- ( ph -> ( ps -> ( ta -> th ) ) ) $=
      ( wi a1d syldd ) ABECDAECHBGIFJ $.
      $( [3-Feb-2006] $) $( [5-Aug-1993] $)
  $}

  ${
    syl6d.1 $e |- ( ph -> ( ps -> ( ch -> th ) ) ) $.
    syl6d.2 $e |- ( ph -> ( th -> ta ) ) $.
    $( A nested syllogism deduction.  (The proof was shortened by Josh
       Purinton, 29-Dec-00 and shortened further by Mel O'Cat, 2-Feb-06.) $)
    syl6d $p |- ( ph -> ( ps -> ( ch -> ta ) ) ) $=
      ( wi a1d syldd ) ABCDEFADEHBGIJ $.
      $( [3-Feb-2006] $) $( [5-Aug-1993] $)
  $}

  ${
    syl9.1 $e |- ( ph -> ( ps -> ch ) ) $.
    syl9.2 $e |- ( th -> ( ch -> ta ) ) $.
    $( A nested syllogism inference with different antecedents.  (The proof
       was shortened by Josh Purinton, 29-Dec-00.) $)
    syl9 $p |- ( ph -> ( th -> ( ps -> ta ) ) ) $=
      ( wi a1i syl5d ) ADCEBDCEHHAGIFJ $.
      $( [5-Aug-1993] $)
  $}

  ${
    syl9r.1 $e |- ( ph -> ( ps -> ch ) ) $.
    syl9r.2 $e |- ( th -> ( ch -> ta ) ) $.
    $( A nested syllogism inference with different antecedents. $)
    syl9r $p |- ( th -> ( ph -> ( ps -> ta ) ) ) $=
      ( wi syl9 com12 ) ADBEHABCDEFGIJ $.
      $( [5-Aug-1993] $)
  $}

  $( Principle of identity.  Theorem *2.08 of [WhiteheadRussell] p. 101.
     For another version of the proof directly from axioms, see ~ id1 .
     (The proof was shortened by Stefan Allan, 20-Mar-06.) $)
  id $p |- ( ph -> ph ) $=
    ( wi ax-1 mpd ) AAABZAAACAECD $.
    $( [20-Mar-2006] $) $( [20-Mar-2006] $)

  $( Principle of identity.  Theorem *2.08 of [WhiteheadRussell] p. 101.  This
     version is proved directly from the axioms for demonstration purposes.
     This proof is identical, step for step, to the proofs of Theorem 1 of
     [Margaris] p. 51 and Example 2.7(a) of [Hamilton] p. 31.  It is also
     "Our first proof" in Hirst and Hirst's _A Primer for Logic and Proof_
     p. 16 (PDF p. 22) at
     ~ http://www.mathsci.appstate.edu/~~jlh/primer/hirst.pdf .
     For a shorter version of the proof that takes advantage of previously
     proved theorems, see ~ id . $)
  id1 $p |- ( ph -> ph ) $=
    ( wi ax-1 ax-2 ax-mp ) AAABZBZFAACAFABBGFBAFCAFADEE $.
    $( [5-Aug-1993] $)

  $( Principle of identity with antecedent. $)
  idd $p |- ( ph -> ( ps -> ps ) ) $=
    ( wi id a1i ) BBCABDE $.
    $( [26-Nov-1995] $)

  $( This theorem, called "Assertion," can be thought of as closed form of
     modus ponens.  Theorem *2.27 of [WhiteheadRussell] p. 104. $)
  pm2.27 $p |- ( ph -> ( ( ph -> ps ) -> ps ) ) $=
    ( wi id com12 ) ABCZABFDE $.
    $( [5-Aug-1993] $)

  $( Absorption of redundant antecedent.  Also called the "Contraction" or
     "Hilbert" axiom.  Theorem *2.43 of [WhiteheadRussell] p. 106.  (The proof
     was shortened by Mel L. O'Cat, 15-Aug-04.) $)
  pm2.43 $p |- ( ( ph -> ( ph -> ps ) ) -> ( ph -> ps ) ) $=
    ( wi pm2.27 a2i ) AABCBABDE $.
    $( [16-Aug-2004] $) $( [5-Aug-1993] $)

  ${
    pm2.43i.1 $e |- ( ph -> ( ph -> ps ) ) $.
    $( Inference absorbing redundant antecedent. $)
    pm2.43i $p |- ( ph -> ps ) $=
      ( wi pm2.43 ax-mp ) AABDZDGCABEF $.
      $( [5-Aug-1993] $)
  $}

  ${
    pm2.43d.1 $e |- ( ph -> ( ps -> ( ps -> ch ) ) ) $.
    $( Deduction absorbing redundant antecedent.  (The proof was shortened by
       Mel O'Cat, 3-Feb-06.) $)
    pm2.43d $p |- ( ph -> ( ps -> ch ) ) $=
      ( idd mpdd ) ABBCABEDF $.
      $( [4-Feb-2006] $) $( [18-Aug-1993] $)
  $}

  ${
    pm2.43a.1 $e |- ( ps -> ( ph -> ( ps -> ch ) ) ) $.
    $( Inference absorbing redundant antecedent.  (The proof was shortened by
       Mel O'Cat, 3-Feb-06.) $)
    pm2.43a $p |- ( ps -> ( ph -> ch ) ) $=
      ( ax-1 mpdd ) BABCBAEDF $.
      $( [4-Feb-2006] $) $( [7-Nov-1995] $)

    $( Inference absorbing redundant antecedent. $)
    pm2.43b $p |- ( ph -> ( ps -> ch ) ) $=
      ( pm2.43a com12 ) BACABCDEF $.
      $( [31-Oct-1995] $)
  $}

  ${
    sylc.1 $e |- ( ph -> ( ps -> ch ) ) $.
    sylc.2 $e |- ( th -> ph ) $.
    sylc.3 $e |- ( th -> ps ) $.
    $( A syllogism inference combined with contraction. $)
    sylc $p |- ( th -> ch ) $=
      ( wi syl mpd ) DBCGDABCHFEIJ $.
      $( [4-May-1994] $)
  $}

  $( Converse of axiom ~ ax-2 .  Theorem *2.86 of [WhiteheadRussell] p. 108. $)
  pm2.86 $p |- ( ( ( ph -> ps ) -> ( ph -> ch ) ) ->
               ( ph -> ( ps -> ch ) ) ) $=
    ( wi ax-1 imim1i com23 ) ABDZACDZDBACBHIBAEFG $.
    $( [25-Apr-1994] $)

  ${
    pm2.86i.1 $e |- ( ( ph -> ps ) -> ( ph -> ch ) ) $.
    $( Inference based on ~ pm2.86 . $)
    pm2.86i $p |- ( ph -> ( ps -> ch ) ) $=
      ( wi pm2.86 ax-mp ) ABEACEEABCEEDABCFG $.
      $( [5-Aug-1993] $)
  $}

  ${
    pm2.86d.1 $e |- ( ph -> ( ( ps -> ch ) -> ( ps -> th ) ) ) $.
    $( Deduction based on ~ pm2.86 . $)
    pm2.86d $p |- ( ph -> ( ps -> ( ch -> th ) ) ) $=
      ( wi pm2.86 syl ) ABCFBDFFBCDFFEBCDGH $.
      $( [29-Jun-1995] $)
  $}

  $( The Linearity Axiom of the infinite-valued sentential logic (L-infinity)
     of Lukasiewicz.   (Contributed by Mel L. O'Cat, 12-Aug-04.) $)
  loolin $p |- ( ( ( ph -> ps ) -> ( ps -> ph ) ) -> ( ps -> ph ) ) $=
    ( wi ax-1 imim1i pm2.43d ) ABCZBACZCBABGHBADEF $.
    $( [14-Aug-2004] $) $( [13-Aug-2004] $)

  $( An alternate for the Linearity Axiom of the infinite-valued sentential
     logic (L-infinity) of Lukasiewicz, due to Barbara Wozniakowska, _Reports
     on Mathematical Logic_ 10, 129-137 (1978).  (Contributed by Mel L. O'Cat,
     8-Aug-04.) $)
  loowoz $p |- ( ( ( ph -> ps ) -> ( ph -> ch ) ) ->
                 ( ( ps -> ph ) -> ( ps -> ch ) ) ) $=
    ( wi ax-1 imim1i a2d ) ABDZACDZDBACBHIBAEFG $.
    $( [9-Aug-2004] $) $( [9-Aug-2004] $)

$(
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
        Logical negation
=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
$)

$( This section makes our first use of the third axiom of propositonal
   calculus. $)

  ${
    a3i.1 $e |- ( -. ph -> -. ps ) $.
    $( Inference rule derived from axiom ~ ax-3 . $)
    a3i $p |- ( ps -> ph ) $=
      ( wn wi ax-3 ax-mp ) ADBDEBAECABFG $.
      $( [5-Aug-1993] $)
  $}

  ${
    a3d.1 $e |- ( ph -> ( -. ps -> -. ch ) ) $.
    $( Deduction derived from axiom ~ ax-3 . $)
    a3d $p |- ( ph -> ( ch -> ps ) ) $=
      ( wn wi ax-3 syl ) ABECEFCBFDBCGH $.
      $( [26-Mar-1995] $)
  $}

  $( From a wff and its negation, anything is true.  Theorem *2.21 of
     [WhiteheadRussell] p. 104.  Also called the Duns Scotus law. $)
  pm2.21 $p |- ( -. ph -> ( ph -> ps ) ) $=
    ( wn ax-1 a3d ) ACZBAFBCDE $.
    $( [5-Aug-1993] $)

  $( Theorem *2.24 of [WhiteheadRussell] p. 104. $)
  pm2.24 $p |-  ( ph -> ( -. ph -> ps ) ) $=
    ( wn pm2.21 com12 ) ACABABDE $.
    $( [6-Jan-2005] $) $( [3-Jan-2005] $)

  ${
    pm2.21i.1 $e |- -. ph $.
    $( A contradiction implies anything.  Inference from ~ pm2.21 . $)
    pm2.21i $p |- ( ph -> ps ) $=
      ( wn a1i a3i ) BAADBDCEF $.
      $( [16-Sep-1993] $)
  $}

  ${
    pm2.21d.1 $e |- ( ph -> -. ps ) $.
    $( A contradiction implies anything.  Deduction from ~ pm2.21 . $)
    pm2.21d $p |- ( ph -> ( ps -> ch ) ) $=
      ( wn a1d a3d ) ACBABECEDFG $.
      $( [10-Feb-1996] $)
  $}

  $( Proof by contradiction.  Theorem *2.18 of [WhiteheadRussell] p. 103.
     Also called the Law of Clavius. $)
  pm2.18 $p |- ( ( -. ph -> ph ) -> ph ) $=
    ( wn wi pm2.21 a2i a3d pm2.43i ) ABZACZAIAIHAIBZAJDEFG $.
    $( [5-Aug-1993] $)

  $( Peirce's axiom.  This odd-looking theorem is the "difference" between
     an intuitionistic system of propositional calculus and a classical system
     and is not accepted by intuitionists.  When Peirce's axiom is added to an
     intuitionistic system, the system becomes equivalent to our classical
     system ~ ax-1 through ~ ax-3 .  A curious fact about this
     theorem is that it requires ~ ax-3 for its proof even though the
     result has no negation connectives in it. $)
  peirce $p |- ( ( ( ph -> ps ) -> ph ) -> ph ) $=
    ( wi wn pm2.21 imim1i pm2.18 syl ) ABCZACADZACAJIAABEFAGH $.
    $( [5-Aug-1993] $)

  $( The Inversion Axiom of the infinite-valued sentential logic (L-infinity)
     of Lukasiewicz.  Using ~ dfor2 , we can see that this essentially
     expresses "disjunction commutes."  Theorem *2.69 of [WhiteheadRussell]
     p. 108. $)
  looinv $p |- ( ( ( ph -> ps ) -> ps ) -> ( ( ps -> ph ) -> ph ) ) $=
    ( wi imim1 peirce syl6 ) ABCZBCBACGACAGBADABEF $.
    $( [20-Aug-2004] $) $( [12-Aug-2004] $)

  $( Double negation.  Theorem *2.14 of [WhiteheadRussell] p. 102.  (The proof
     was shortened by David Harvey, 5-Sep-99.  An even shorter proof found by
     Josh Purinton, 29-Dec-00.) $)
  nega $p |- ( -. -. ph -> ph ) $=
    ( wn wi pm2.21 pm2.18 syl ) ABZBGACAGADAEF $.
    $( [5-Aug-1993] $)

  $( Converse of double negation.  Theorem *2.12 of [WhiteheadRussell]
     p. 101. $)
  negb $p |- ( ph -> -. -. ph ) $=
    ( wn nega a3i ) ABZBAECD $.
    $( [5-Aug-1993] $)

  $( Reductio ad absurdum.  Theorem *2.01 of [WhiteheadRussell] p. 100. $)
  pm2.01 $p |- ( ( ph -> -. ph ) -> -. ph ) $=
    ( wn wi nega imim1i pm2.18 syl ) AABZCHBZHCHIAHADEHFG $.
    $( [18-Aug-1993] $)

  ${
    pm2.01d.1 $e |- ( ph -> ( ps -> -. ps ) ) $.
    $( Deduction based on reductio ad absurdum. $)
    pm2.01d $p |- ( ph -> -. ps ) $=
      ( wn wi pm2.01 syl ) ABBDZEHCBFG $.
      $( [18-Aug-1993] $)
  $}

  $( Contraposition.  Theorem *2.03 of [WhiteheadRussell] p. 100. $)
  con2 $p |- ( ( ph -> -. ps ) -> ( ps -> -. ph ) ) $=
    ( wn wi nega imim1i a3d ) ABCZDACZBICAHAEFG $.
    $( [5-Aug-1993] $)

  ${
    con2d.1 $e |- ( ph -> ( ps -> -. ch ) ) $.
    $( A contraposition deduction. $)
    con2d $p |- ( ph -> ( ch -> -. ps ) ) $=
      ( wn wi con2 syl ) ABCEFCBEFDBCGH $.
      $( [19-Aug-1993] $)
  $}

  $( Contraposition.  Theorem *2.15 of [WhiteheadRussell] p. 102. $)
  con1 $p |- ( ( -. ph -> ps ) -> ( -. ps -> ph ) ) $=
    ( wn wi negb imim2i a3d ) ACZBDABCZBICHBEFG $.
    $( [5-Aug-1993] $)

  ${
    con1d.1 $e |- ( ph -> ( -. ps -> ch ) ) $.
    $( A contraposition deduction. $)
    con1d $p |- ( ph -> ( -. ch -> ps ) ) $=
      ( wn wi con1 syl ) ABECFCEBFDBCGH $.
      $( [5-Aug-1993] $)
  $}

  $( Contraposition.  Theorem *2.16 of [WhiteheadRussell] p. 103. $)
  con3 $p |- ( ( ph -> ps ) -> ( -. ps -> -. ph ) ) $=
    ( wi wn negb imim2i con2d ) ABCABDZBHDABEFG $.
    $( [5-Aug-1993] $)

  ${
    con3d.1 $e |- ( ph -> ( ps -> ch ) ) $.
    $( A contraposition deduction. $)
    con3d $p |- ( ph -> ( -. ch -> -. ps ) ) $=
      ( wi wn con3 syl ) ABCECFBFEDBCGH $.
      $( [5-Aug-1993] $)
  $}

  ${
    con1.a $e |- ( -. ph -> ps ) $.
    $( A contraposition inference. $)
    con1i $p |- ( -. ps -> ph ) $=
      ( wn negb syl a3i ) ABDZADBHDCBEFG $.
      $( [5-Aug-1993] $)
  $}

  ${
    con2.a $e |- ( ph -> -. ps ) $.
    $( A contraposition inference. $)
    con2i $p |- ( ps -> -. ph ) $=
      ( wn nega syl a3i ) ADZBHDABDAECFG $.
      $( [5-Aug-1993] $)
  $}

  ${
    con3.a $e |- ( ph -> ps ) $.
    $( A contraposition inference. $)
    con3i $p |- ( -. ps -> -. ph ) $=
      ( wn nega syl con1i ) ADZBHDABAECFG $.
      $( [5-Aug-1993] $)
  $}

  $( Theorem *2.36 of [WhiteheadRussell] p. 105. $)
  pm2.36 $p |-  ( ( ps -> ch ) -> ( ( -. ph -> ps ) -> ( -. ch -> ph ) ) ) $=
    ( wi wn imim2 con1 syl6 ) BCDAEZBDICDCEADBCIFACGH $.
    $( [8-Jan-2005] $) $( [3-Jan-2005] $)

  $( Theorem *2.37 of [WhiteheadRussell] p. 105. $)
  pm2.37 $p |-  ( ( ps -> ch ) -> ( ( -. ps -> ph ) -> ( -. ph -> ch ) ) ) $=
    ( wn wi con1 imim1d com12 ) BDAEZBCEADZCEIJBCBAFGH $.
    $( [23-Jan-2005] $) $( [3-Jan-2005] $)

  $( Theorem *2.38 of [WhiteheadRussell] p. 105. $)
  pm2.38 $p |-  ( ( ps -> ch ) -> ( ( -. ps -> ph ) -> ( -. ch -> ph ) ) ) $=
    ( wi wn con3 imim1d ) BCDCEBEABCFG $.
    $( [27-Jan-2005] $) $( [3-Jan-2005] $)

  $( Theorem *2.5 of [WhiteheadRussell] p. 107. $)
  pm2.5 $p |-  ( -. ( ph -> ps ) -> ( -. ph -> ps ) ) $=
    ( wi wn pm2.21 con3i pm2.21d ) ABCZDADZBIHABEFG $.
    $( [27-Jan-2005] $) $( [3-Jan-2005] $)

  $( Theorem *2.51 of [WhiteheadRussell] p. 107. $)
  pm2.51 $p |-  ( -. ( ph -> ps ) -> ( ph -> -. ps ) ) $=
    ( wi wn ax-1 con3i a1d ) ABCZDBDABHBAEFG $.
    $( [27-Jan-2005] $) $( [3-Jan-2005] $)

  $( Theorem *2.52 of [WhiteheadRussell] p. 107. $)
  pm2.52 $p |-  ( -. ( ph -> ps ) -> ( -. ph -> -. ps ) ) $=
    ( wi wn ax-1 con3i a1d ) ABCZDBDADBHBAEFG $.
    $( [27-Jan-2005] $) $( [3-Jan-2005] $)

  $( Theorem *2.521 of [WhiteheadRussell] p. 107. $)
  pm2.521 $p |-  ( -. ( ph -> ps ) -> ( ps -> ph ) ) $=
    ( wi wn pm2.52 a3d ) ABCDABABEF $.
    $( [6-Feb-2005] $) $( [3-Jan-2005] $)

  ${
    pm2.21ni.1 $e |- ph $.
    $( Inference related to ~ pm2.21 . $)
    pm2.21ni $p |- ( -. ph -> ps ) $=
      ( wn a1i con1i ) BAABDCEF $.
      $( [20-Aug-2001] $)
  $}

  ${
    pm2.21nd.1 $e |- ( ph -> ps ) $.
    $( Deduction related to ~ pm2.21 . $)
    pm2.21nd $p |- ( ph -> ( -. ps -> ch ) ) $=
      ( wn a1d con1d ) ACBABCEDFG $.
      $( [31-Jan-2006] $) $( [30-Jan-2006] $)
  $}

  ${
    mto.1 $e |- -. ps $.
    mto.2 $e |- ( ph -> ps ) $.
    $( The rule of modus tollens. $)
    mto $p |- -. ph $=
      ( wn con3i ax-mp ) BEAECABDFG $.
      $( [19-Aug-1993] $)
  $}

  ${
    mtoi.1 $e |- -. ch $.
    mtoi.2 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Modus tollens inference. $)
    mtoi $p |- ( ph -> -. ps ) $=
      ( wn con3d mpi ) ACFBFDABCEGH $.
      $( [5-Jul-1994] $)
  $}

  ${
    mtod.1 $e |- ( ph -> -. ch ) $.
    mtod.2 $e |- ( ph -> ( ps -> ch ) ) $.
    $( Modus tollens deduction. $)
    mtod $p |- ( ph -> -. ps ) $=
      ( wn con3d mpd ) ACFBFDABCEGH $.
      $( [3-Apr-1994] $)
  $}

  ${
    mt2.1 $e |- ps $.
    mt2.2 $e |- ( ph -> -. ps ) $.
    $( A rule similar to modus tollens. $)
    mt2 $p |- -. ph $=
      ( wn con2i ax-mp ) BAECABDFG $.
      $( [19-Aug-1993] $)
  $}

  ${
    test.1 $e |- ph $.
    $( Test. $)
    test $p |- ph $=
      ( ax-mp ax-1 id wi ) AAAABAECZAAAFGAADCC $.
      $( [19-Aug-1993] $)
  $}

$( $t

/******* Symbol definitions *******/

htmldef "(" as "<IMG SRC='lp.gif' WIDTH=5 HEIGHT=19 ALT='(' ALIGN=TOP>";
  althtmldef "(" as "(";
  latexdef "(" as "(";
htmldef ")" as "<IMG SRC='rp.gif' WIDTH=5 HEIGHT=19 ALT=')' ALIGN=TOP>";
  althtmldef ")" as ")";
  latexdef ")" as ")";
htmldef "->" as
    " <IMG SRC='to.gif' WIDTH=15 HEIGHT=19 ALT='-&gt;' ALIGN=TOP> ";
  althtmldef "->" as ' &rarr; ';
  latexdef "->" as "\rightarrow";
htmldef "-." as "<IMG SRC='lnot.gif' WIDTH=10 HEIGHT=19 ALT='-.' ALIGN=TOP> ";
  althtmldef "-." as '&not; ';
  latexdef "-." as "\lnot";
htmldef "wff" as
    "<IMG SRC='_wff.gif' WIDTH=24 HEIGHT=19 ALT='wff' ALIGN=TOP> ";
  althtmldef "wff" as '<FONT COLOR="#808080">wff </FONT>'; /* was #00CC00 */
  latexdef "wff" as "{\rm wff}";
htmldef "|-" as
    "<IMG SRC='_vdash.gif' WIDTH=10 HEIGHT=19 ALT='|-' ALIGN=TOP> ";
  althtmldef "|-" as
    '<FONT COLOR="#808080" FACE=sans-serif>&#8866; </FONT>'; /* &vdash; */
    /* Without sans-serif, way too big in FF3 */
  latexdef "|-" as "\vdash";
htmldef "ph" as
    "<IMG SRC='_varphi.gif' WIDTH=11 HEIGHT=19 ALT='ph' ALIGN=TOP>";
  althtmldef "ph" as '<FONT COLOR="#0000FF"><I>&phi;</I></FONT>';
  latexdef "ph" as "\varphi";
htmldef "ps" as "<IMG SRC='_psi.gif' WIDTH=12 HEIGHT=19 ALT='ps' ALIGN=TOP>";
  althtmldef "ps" as '<FONT COLOR="#0000FF"><I>&psi;</I></FONT>';
  latexdef "ps" as "\psi";
htmldef "ch" as "<IMG SRC='_chi.gif' WIDTH=12 HEIGHT=19 ALT='ch' ALIGN=TOP>";
  althtmldef "ch" as '<FONT COLOR="#0000FF"><I>&chi;</I></FONT>';
  latexdef "ch" as "\chi";
htmldef "th" as "<IMG SRC='_theta.gif' WIDTH=8 HEIGHT=19 ALT='th' ALIGN=TOP>";
  althtmldef "th" as '<FONT COLOR="#0000FF"><I>&theta;</I></FONT>';
  latexdef "th" as "\theta";
  
htmldef "ta" as "<IMG SRC='_tau.gif' WIDTH=10 HEIGHT=19 ALT='ta' ALIGN=TOP>";
  althtmldef "ta" as '<FONT COLOR="#0000FF"><I>&tau;</I></FONT>';
  latexdef "ta" as "\tau";
  
htmldef "<->" as " <IMG SRC='leftrightarrow.gif' WIDTH=15 HEIGHT=19 " +
    "ALT='&lt;-&gt;' ALIGN=TOP> ";
  althtmldef "<->" as ' &harr; ';
  latexdef "<->" as "\leftrightarrow";
htmldef "\/" as " <IMG SRC='vee.gif' WIDTH=11 HEIGHT=19 ALT='\/' ALIGN=TOP> ";
  althtmldef "\/" as ' <FONT FACE=sans-serif>&#8897;</FONT> ' ;
    /* was &or; - changed to match font of &and; replacement */
  latexdef "\/" as "\vee";
htmldef "/\" as
    " <IMG SRC='wedge.gif' WIDTH=11 HEIGHT=19 ALT='/\' ALIGN=TOP> ";
  althtmldef "/\" as ' <FONT FACE=sans-serif>&#8896;</FONT> ';
    /* was &and; which is circle in Mozilla on WinXP Pro (but not Home) */
  latexdef "/\" as "\wedge";
htmldef "et" as "<IMG SRC='_eta.gif' WIDTH=9 HEIGHT=19 ALT='et' ALIGN=TOP>";
  althtmldef "et" as '<FONT COLOR="#0000FF"><I>&eta;</I></FONT>';
  latexdef "et" as "\eta";
htmldef "ze" as "<IMG SRC='_zeta.gif' WIDTH=9 HEIGHT=19 ALT='ze' ALIGN=TOP>";
  althtmldef "ze" as '<FONT COLOR="#0000FF"><I>&zeta;</I></FONT>';
  latexdef "ze" as "\zeta";

htmldef "-/\" as
    " <IMG SRC='barwedge.gif' WIDTH=9 HEIGHT=19 ALT='-/\' ALIGN=TOP> ";
  althtmldef "-/\" as ' <FONT FACE=sans-serif>&#8892;</FONT> '; 
    /*althtmldef "-/\" as " &#8892; "; */ /* too-high font bug in FF */
    /* barwedge, U022BC, alias ISOAMSB barwed, ['nand'] */
  latexdef "-/\" as "\barwedge";  

/* End of typesetting definition section */
$)
