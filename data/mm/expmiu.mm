$( miu.mm  4-Dec-04 $)

$(

                      PUBLIC DOMAIN DEDICATION

This file is placed in the public domain per the Creative Commons Public
Domain Dedication. http://creativecommons.org/licenses/publicdomain/

Norman Megill - email: nm at alum.mit.edu

$)

$( The MIU-system:  A simple formal system $)

$( Note:  This formal system is unusual in that it allows empty wffs.
To work with a proof, you must type SET EMPTY_SUBSTITUTION ON before
using the PROVE command.  By default this is OFF in order to reduce the
number of ambiguous unification possibilities that have to be selected
during the construction of a proof.  $)

$(
Hofstadter's MIU-system is a simple example of a formal
system that illustrates some concepts of Metamath.  See
Douglas R. Hofstadter, "G\"{o}del, Escher, Bach:  An Eternal
Golden Braid" (Vintage Books, New York, 1979), pp. 33ff. for
a description of the MIU-system.

The system has 3 constant symbols, M, I, and U.  The sole
axiom of the system is MI. There are 4 rules:
     Rule I:  If you possess a string whose last letter is I,
     you can add on a U at the end.
     Rule II:  Suppose you have Mx.  Then you may add Mxx to
     your collection.
     Rule III:  If III occurs in one of the strings in your
     collection, you may make a new string with U in place
     of III.
     Rule IV:  If UU occurs inside one of your strings, you
     can drop it.
Unfortunately, Rules III and IV do not have unique results:
strings could have more than one occurrence of III or UU.
This requires that we introduce the concept of an "MIU
well-formed formula" or wff, which allows us to construct
unique symbol sequences to which Rules III and IV can be
applied.
$)

$( First, we declare the constant symbols of the language.
Note that we need two symbols to distinguish the assertion
that a sequence is a wff from the assertion that it is a
theorem; we have arbitrarily chosen "wff" and "|-". $)
      $c M I U |- wff $. $( Declare constants $)

$( Next, we declare some variables. $)
     $v x y $.

$( Throughout our theory, we shall assume that these
variables represent wffs. $)
 wx   $f wff x $.
 wy   $f wff y $.

$( Define MIU-wffs.  We allow the empty sequence to be a
wff. $)

$( The empty sequence is a wff. $)
 we   $a wff $.
$( "M" after any wff is a wff. $)
 wM   $a wff x M $.
$( "I" after any wff is a wff. $)
 wI   $a wff x I $.
$( "U" after any wff is a wff. $)
 wU   $a wff x U $.
$( If "x" and "y" are wffs, so is "x y".  (Added per suggestion of
   Mel O'Cat 4-Dec-04.) $)
 wxy  $a wff x y $. 

$( Assert the axiom. $)
 ax   $a |- M I $.

$( Assert the rules. $)
 ${
   Ia   $e |- x I $.
$( Given any theorem ending with "I", it remains a theorem
if "U" is added after it. $)
   I    $a |- x I U $.
 $}
 ${
IIa  $e |- M x $.
$( Given any theorem starting with "M", it remains a theorem
if the part after the "M" is added again after it. $)
   II   $a |- M x x $.
 $}
 ${
   IIIa $e |- x I I I y $.
$( Given any theorem with "III" in the middle, it remains a
theorem if the "III" is replace with "U". $)
   III  $a |- x U y $.
 $}
 ${
   IVa  $e |- x U U y $.
$( Given any theorem with "UU" in the middle, it remains a
theorem if the "UU" is deleted. $)
   IV   $a |- x y $.
  $}

$( Now we prove the theorem MUIIU.  You may be interested in
comparing this proof with that of Hofstadter (pp. 35 - 36).
$)
 theorem1  $p |- M U I I U $=
      we wM wU wI we wI wU we wU wI wU we wM we wI wU we wM
      wI wI wI we wI wI we wI ax II II I III II IV $.
