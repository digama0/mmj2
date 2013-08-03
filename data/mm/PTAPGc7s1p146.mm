$( PTAPGc7s1p146: test mm file
   translated from
   Dick Grune and Ceriel J.H. Jacobs,
   "Parsing Techniques - A Practical Guide"
   [ http://www.cs.vu.nl/~dick/PTAPG.html  ]

Generated messages follow:
I-GR-0046 Info Message: The size of the Syntax Axiom Type 
Code set = 1 is not equal to the size of the Variable 
Hypothesis Type Code set = 3. This indicates that one 
of the Variable Types has no Syntax Axioms, a condition 
termed 'Undefined non-Terminal', and, in theory, may be 
removed from the grammar.

I-GR-0016 Two Grammatical Parse trees found for expression 
(the first found will be used). 
Label =T1 ParseTree[0] = Va1 Va2 Va3 R3 Va Vb R1  
          ParseTree[1] = Va Va1 Va2 R3 Va3 Vb R2 
       
$)

$c |- wff    $.
$c S A B     $.

$v s a b     $.
$v a1 a2 a3  $.
Vs  $f S s   $.
Va  $f A a   $.
Va1 $f A a1  $.
Va2 $f A a2  $.
Va3 $f A a3  $.
Vb  $f B b   $.

R1 $a S a s b    $.
R2 $a S s a b    $.
R3 $a S a1 a2 a3 $.

T1 $p S a a1 a2 a3 b              $= ? $.
T2 $p S a a s a b b b a b a b a b $= ? $.

