$( E-LA-0004 Disjoint Variable statement has duplicate"
             variables! Sym = $)
$v v1 $.
$(
    // Note: E-IO-0019 E-IO-0019 Statement has duplicate tokens.
    //       Statment keyword = $d in mmj.mmio.Statementizer
    //       takes precedence here, so unless a new interface
    //       to LogicalSystem is created we won't be seeing
    //       this message.
$)
$d v1 v1 $.

$( E-LA-0005 Constant on statement not previously declared. Type = $)
a $a a $. 

$( E-LA-0006 Constant symbol already declared but not as a 
             constant. Type = $)
$c c2 $.
$v v2 $.
label2 $e v2 c2 $.

$( E-LA-0007 Undefined symbol used in statement expression."
             Symbol = $)
$c c3 $.
Labelc3 $a c3 v3 $.
        
        
$( E-LA-0008 Inactive (in scope) symbol used in statement"
             expression. Symbol =  $)
$c c4 $.
${ $v v4 $. $}
Labelc4 $a c4 v4 $.
        
$( E-LA-0009 Variable in expression has no active VarHyp. 
             Symbol = v   $)
$c c5 $.
$v v5 $.
label $a c5 v5 $.

$( "E-LA-0010 Label on statement already used -- a duplicate.
              Label = $)
$c c6 c6b $.
Labelc6 $a c6 c6b $.
Labelc6 $a c6b c6 $.

$( E-LA-0012 Declared symbol is duplicate of other variable"
             or constant, sym = $)
$c c7 $.
$c c7 $.
$v v7 $.
$c v7 $. 

$( E-LA-0013 Proof must have at least one step. A
             single "?" will suffice. $)
$c c8 $.
$(
    // Note: "E-IO-0025 Proof must have at least one step 
    //        (which may be a "?" symbol)." from
    //       mmj.mmio.Statementizer.java appears to take
    //       precedence over this, but it is ok that
    //       LogicalSystem doublechecks this.
$)
Labelc8 $p c8 $= $.

$( E-LA-0014 Proof step label not found in Statement table."
             Label = $)
$c c14 $.
Label14 $p c14 $= oops $.
        
$( E-LA-0015 Proof step sequence in database >= this"
             statement! Label =   $)
$c c15.1 c15.2 $.
Label15.1 $p c15.1 $= c15.2 $.
$( 
    //note: E-LA-0014 in mmj.lang.theorem takes precedence over 
    //      E-LA-0015 if the Theorem is being added in file
    //      order. So this double-check may be useful in the
    //      future but doesn't have any effect.
Label15.2 $a c15.2 $.
        
$( E-LA-0016 Proof step refers to inactive (out of scope)
             hypothesis. Symbol = $)
$c c16 $.
$v v16 $.
${
    hv16 $f c16 v16 $.
$}
Label16 $p c16 $= hv16 $.

$( E-LA-0017 Variable symbol duplicates a Constant ($c)
             sym = $)
$c c17 $.
$v c17 $.

$( E-LA-0018 Variable symbol is already active in scope.
        + " Sym = $)
$v v18 $.
$v v18 $.
$v v18.1 $.
${
    $v v18.1   $.
    $v v18.1.1 $.
    ${ 
        $v v18.1.1   $.
        $v v18.1.1.1 $. 
    $}
    $v v18.1.1.1 $.
$}
$v v18.1.1 $.

$( E-LA-0019 Variable in statement not previously declared.
             Var =   $)
$c c19 $.             
Label19 $f c19 v19 $. 

$( E-LA-0020 Variable symbol already declared, but not as
             a variable. Var =  $)
$c c20.1 c20.2 $.
vh20.2 $f c20.1 c20.2 $.

$( E-LA-0021 Variable in statement not active in scope.
        + " Var = $)
$c c21 $.
${
    $v v21 $.
$}
vh21 $f c21 v21 $.

$( E-LA-0022 Variable Hyp. already active for var in new
        + " VarHyp, Label = $)
$c c22 $.
$v v22 $.
vh22.1 $f c22 v22 $.
vh22.2 $f c22 v22 $.
 
