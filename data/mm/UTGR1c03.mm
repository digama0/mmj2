$c |- wff $. $( <--need these for Grammar Constructor, OR
                specify RunParmFile param entries:
                    ProvableLogicStmtType,|-
                    LogicStmtType,wff
             $)


$( NOTE: This edit requires RunParmFile parm
         "StatementAmbiguityEdits,complete" $)
$( I-GR-0016 Two Grammatical Parse trees found for"
        + " expression (the first found will be used). Label =";
        " ParseTree[0] = ";
        " ParseTree[1] = "; $)
$c c16 $.
$v v161 v162 $.
f161 $f wff v161 $.
f162 $f wff v162 $. 
wff16 $a wff v161 v162 $.
ax16 $a |- v161 v161 v161 v161 v161 $.
