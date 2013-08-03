$( E-IO-0021 Statement token list must begin with
             a constant symbol (type). Statment keyword = $)
label $a $.
 
$( E-IO-0022 A $f statement requires exactly two math symbols. $)
a $f a $.  

$( E-IO-0023 A "$p" statement requires "$=" followed by proof 
             steps. $)
$c c2 $.
$v v2 $.
label $p v2 $.

$( E-IO-0024 Invalid character in proof step. Token read = $) 
$c c3 $.
$v v3 $.
label3 $p c3 v3 $= # $.

$( E-IO-0025 Proof must have at least one step (which
             may be a "?" symbol). $) 
$c c31 $.
$v v31 $.
label31 $p c31 v31 $= $.
