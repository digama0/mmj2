$( PTAPGc7s1p148: test mm file
   translated from
   Dick Grune and Ceriel J.H. Jacobs,
   "Parsing Techniques - A Practical Guide"
   [ http://www.cs.vu.nl/~dick/PTAPG.html  ]

$)

$c |- wff             $.
$c S E Q F A + -      $.

$v s e f q a a1 a2 a3 $.

Vs  $f  S s  $.
Ve  $f  E e  $.
Vf  $f  F f  $.
Vq  $f  Q q  $.
Va  $f  A a  $.
Va1 $f  A a1 $.
Va2 $f  A a2 $.
Va3 $f  A a3 $.

R1ConveToS $a  S e      $.
R2BinOp    $a  E e q f  $.
R3ConvfToE $a  E f      $.
R4ConvaToF $a  F a      $.
R5OPPLUS   $a  Q +      $.
R6OPMINUS  $a  Q -      $.
 
T1 $p E  a - a1 + a2       $= ? $.
T2 $p E  a - a1 - a2       $= ? $.
T3 $p E  a + a1 - a2 + a3  $= ? $.


