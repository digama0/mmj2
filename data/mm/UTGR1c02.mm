$c |- wff $. $( <--need these for Grammar Constructor, OR
                specify RunParmFile param entries:
                    ProvableLogicStmtType,|-
                    LogicStmtType,wff
             $)



$c c14 $.
$c c24 $.
$c c34 $.

wff14 $a wff c14 $.
wff24 $a wff c24 $.
wff34 $a wff c34 $.
wff1424 $a wff c24 c34 $.

$( 2011-06-17 : ensure parseable! $)
ax14b $a |- c14 $.

$( 2011-06-17 : ensure parseable! $)
ax24b $a |- c24 $.

$( 2011-06-17 : ensure parseable! $)
ax34b $a |- c34 $.


$( 2011-06-17 : ensure parseable! $)
ax2434b $a |- c24 c34 $.

$( E-GR-0014 No valid grammatical Parse Tree found for"
           + " expression. Label =";
             ". Parse terminated in formula at symbol position "; $)
ax14 $a |- c14 c14 $.


$c c15 $.
wff15 $a wff c15 c15 $.

$( 2011-06-17 : ensure parseable! $)
ax15b $a |- c15 c15 $.

$( E-GR-0015 No valid grammatical Parse Tree found for"
        + " expression. Label ="; $)
ax15 $a |- c15 c15 c15 $.


