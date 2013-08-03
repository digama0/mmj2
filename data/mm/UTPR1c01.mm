$( E-PR-0004 VerifyProof: proof incomplete. $)
$c c4 $.
Label4 $p c4 $= ? $.

$( E-PR-0005 VerifyProof: proof has zero steps. $)
$c c5 $.
$(
    ==>E-PR-0005 is overridden when a file is input
       by 
       E-IO-0025 Proof must have at least one step...
       so this double-check doesn't hurt but...
$)
$( Label5 $p c5 $= $. comment this out so file load has 0 errors $)

$( E-PR-0006 VerifyProof: stack has more than one
             entry at end of proof! $)
$c c6.1 c6.2 $.
Label6.2 $a c6.1 c6.2 $.
Label6 $p c6.1 c6.2 $= Label6.2 Label6.2 $.
             
$( E-PR-0007 VerifyProof: assertion to be proved not
             = final stack entry: $)
$c c7.1 c7.2 $.
Label7.1 $a c7.1 $.
Label7.2 $a c7.1 c7.2 $.
Label7 $p c7.1 c7.2 $= Label7.1 $.

$( E-PR-0008 VerifyProof: proof stack item type
             not = hypothesis type: $)
$c c8.1 c8.2 $.
$v v8.1 v8.2 $.
vH8.1 $f c8.1 v8.1 $.
vH8.2 $f c8.2 v8.2 $.
Label8.1 $a c8.1 v8.1 $.
Label8.2 $a c8.2 v8.1 $.
Label8 $p c8.1 v8.1 $= vH8.2 Label8.2 $.
             
$( E-PR-0009 VerifyProof: DjVars restriction violated!
             Substitution (to) variables match: $)
$c c9.1 c9.2 $.
$v v9.11 v9.21 v9.12 v9.22 $.
vH9.11 $f c9.1 v9.11 $.
vH9.12 $f c9.1 v9.12 $.
vH9.21 $f c9.2 v9.21 $.
vH9.22 $f c9.2 v9.22 $. 
$d v9.11 v9.12 $.
Label9.1 $a c9.1 v9.11 v9.12 $.
Label9.2 $a c9.1 v9.21 $.
Label9 $p c9.1 v9.21 v9.21  
       $= vH9.21 Label9.2 vH9.21 Label9.2 Label9.1 $.
             
$( E-PR-0010 VerifyProof: Substitution (to) vars subject
             to DjVars restriction by proof step but
             not listed as DjVars in theorem to be proved: $)
$c c10.1 c10.2 $.
$v v10.11 v10.21 v10.12 v10.22 $.
vH10.11 $f c10.1 v10.11 $.
vH10.12 $f c10.1 v10.12 $.
vH10.21 $f c10.2 v10.21 $.
vH10.22 $f c10.2 v10.22 $. 
$d v10.11 v10.12 $.
Label10.1 $a c10.1 v10.11 v10.12 $.
Label10.2 $a c10.1 v10.21 $.
Label10 $p c10.1 v10.21 v10.22  
       $= vH10.21 Label10.2 vH10.22 Label10.2 Label10.1 $.

$( E-PR-0011 Verify Proof: invalid substitution, stack
             and subst-hypothesis not equal! $)
$c c11.1 c11.2 $.
$v v11.11 v11.12 v11.21 v11.22 $.
vH11.11 $f c11.1 v11.11 $.
vH11.12 $f c11.1 v11.12 $.
vH11.21 $f c11.2 v11.21 $.
vH11.22 $f c11.2 v11.22 $. 
${ 
    eLabel11.1 $e c11.1 v11.11 $.
    aLabel11.1 $a c11.1 v11.11 v11.12 $.
$}
${  
    eLabel11.2 $e c11.1 v11.11 $.
    pLabel11.2 $p c11.1 v11.11 v11.12   
       $= vH11.11 vH11.12 vH11.11 eLabel11.2 aLabel11.1 $.   
    $( $= vH11.11 vH11.12 eLabel11.2 aLabel11.1 $. <==valid proof $)
$}



