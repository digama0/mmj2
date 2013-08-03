$c |- wff $. $( <--need these for Grammar Constructor, OR
                specify RunParmFile param entries:
                    ProvableLogicStmtType,|-
                    LogicStmtType,wff
             $)

$( E-GR-0029 Variable Hypothesis, Label = ";
             ", has Type Code that is defined as a Provable Logic "
             " Statement Type."
             " This Grammar program is presently unable to deal"
             " with meta-metalogical statements of that kind."; $)
             
$v v291 $.
vHv291 $f |- v291 $.

$( E-GR-0030 Syntax Axiom, Label = ";
             ", has Disjoint Variable Restrictions."; $)
$c c301 c302 $.
$v v3011 v3021 $.
${
    vHv3011 $f c301 v3011 $.
    vHv3021 $f c302 v3021 $.
    $d v3011 v3021 $.
    L30 $a c302 v3011 v3021 $.
$}


$( E-GR-0031 Syntax Axiom, Label = ";
             ", has Logical Hypothesis ($e) in scope."; $)
$c c311 c312 $.
$v v3111 v3121 $.
${
    vHv3111 $f c311 v3111 $.
    vHv3121 $f c312 v3121 $.
    lH31 $e c311 v3121 $.
    L31 $a c312 v3111 v3121 $.
$}
             

$( E-GR-0032 Notation Syntax Axiom formula variables do"
             " not match the VarHyp's for the axiom. Note: a"
             " variable may appear only once in a Notation Syntax"
             " Axiom. Stmt Label = ";
             " Formula = "; $)
$c c321 c322 $.
$v v3221 $. 
vH3221 $f c321 v3221 $.
L32 $a c322 v3221 v3221 $.

$( ==> can't get this one to trigger as E-GR-0033! WANTS
       to be a E-GR-0032. Oh well. Leave it. Not a problem... $)
$( E-GR-0033 Notation Syntax Axiom, label = ";
             ",  has Variable ";
            " that occurs more than once in its formula, ="; $)
$c c331 c332 $.
$v v3321 v3322 v3323 $. 
vH3321 $f c331 v3321 $.
vH3322 $f c331 v3322 $.
vH3323 $f c331 v3323 $.
L33 $a c332 v3321 v3322 v3323 v3321 $.


$( E-GR-0034 "Syntax Axiom, label = 
             " contains Cnst = ";
             " that is used elsewhere as a Grammatical Type"
           + " (VarHyp Type, Syntax Axiom Type, Logic Stmt"
           + " Type or Provable Logic Stmt Type)."; $)
$c c341 c342 $.
$v v341 $.
f341 $f c341 v341 $.
a342 $a c342 c341 $.

