$( PTAPGc7s2p160: test mm file
   translated from
   Dick Grune and Ceriel J.H. Jacobs,
   "Parsing Techniques - A Practical Guide"
   [ http://www.cs.vu.nl/~dick/PTAPG.html  ]
$)

$c |- wff                    $.
$c Ss S A B C D              $.

$v s a b p q                 $.

Vs  $f  S s                  $.
Va  $f  A a                  $.
Vb  $f  B b                  $.
Vc  $f  C p                  $.
Vd  $f  D q                  $.

R1ConvSToSs $a  Ss  s        $.
R1ConvAToS  $a  S   a        $.
R1ConvBToS  $a  S   b        $.
R2ConvCToA  $a  A   p        $.
R3ConvDToB  $a  B   q        $.

A1ABisS     $a  S   a b      $.

T1 $p S  q              $= ? $.
T2 $p S  p              $= ? $.
T3 $p S  p q            $= ? $.
