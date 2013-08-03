$c |- wff $. $( <--need these for Grammar Constructor, OR
                specify RunParmFile param entries:
                    ProvableLogicStmtType,|-
                    LogicStmtType,wff
             $)

$(  // Sep-18-2005: I-GR-0017 is not triggered with
    // the present configuration of the code because
    // only one parse of grammar rules is attempted,
    // (see mmj.java.verify.GrammarAmbiguity.java).
    // I-GR-0018 is output instead. $)
$( I-GR-0017 "Syntax of 
             " (or grammar rule derived from it indirectly)"
             " is parseable; thus it is a duplicate or"
             " is a composite function. 1st ParseTree = "; $)
$( I-GR-0018 Syntax of 
             " (or grammar rule derived from it indirectly)"
             " is parseable; thus it is a duplicate or"
             " is a composite function. 1st ParseTree = ";
             " 2nd ParseTree = "; $)
$c - $.
$v v181 v182 $.
h181 $f wff v181 $.
h182 $f wff v182 $.
A18a $a wff - v181 $.
A18b $a wff - - v182 $. 
        
