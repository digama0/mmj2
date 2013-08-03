$c |- wff $. $( <--need these for Grammar Constructor, OR
                specify RunParmFile param entries:
                    ProvableLogicStmtType,|-
                    LogicStmtType,wff
             $)

$( E-GR-0041 Syntax Axiom, label = ";
             " is a Type Conversion Rule that creates a 
             " Type Conversion loop from Type = ";
             ", to Type = "; $)
$c c41a c41b c41c $.
$v v41a v41b v41c $.
vH41a $f c41a v41a $.
vH41b $f c41b v41b $.
vH41c $f c41c v41c $.
conv41c $a c41b v41c $.
conv41b $a c41a v41b $.
conv41a $a c41c v41a $.

$( E-GR-0043 Syntax Axiom, ";
             ", has base Grammar Rule that is a duplicate of another"
           + " Grammar Rule derived from Syntax Axiom, "; $)
$c c43 $.
a43 $a c43 $.
a432 $a c43 $.
$c class set = $.
$v x y A B $.
setX $f set x $.
setY $f set y $.
classA $f class A $.
classB $f class B $.
convSet $a class x $.
wceq $a wff A = B $.
weq $a wff x = y $.  $( weq must come first to precede derived rule! $)

$( E-GR-0044 GrammarRule is a duplicate with a different"
         + " Type Code than the duplicate rule. Label =";
           " Type Code =";
           " Dup Rule Label =";
           " Dup Rule Type Code ="; $)
          
$c *44 T44A T44B T44 $.
$v v44A1 v44A2 v44B1 v44B2 $. 
vH44A1 $f T44A v44A1 $.
vH44A2 $f T44A v44A2 $.
vH44B1 $f T44B v44B1 $.
vH44B2 $f T44B v44B2 $.
Star44A $a T44 *44 v44A1 v44A2 $.
Star44B $a T44B *44 v44B1 v44B2 $. 
conv44A $a T44B v44A1 $.


