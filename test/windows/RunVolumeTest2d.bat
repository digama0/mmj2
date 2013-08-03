REM Volume Test 2d is the same as Volume Test 2
REM except that its batch test RunParm uses the
REM DeriveFormulas option with AsciiRetest. Also
REM important are RunParms:
REM     ProofAsstDjVarsSoftErrors,GenerateReplacements
REM     RecheckProofAsstUsingProofVerifier,yes
REM

erase c:\mmj2\data\result\VT2dOut20111101y.txt  
 
c:\mmj2\test\windows\RunVT2d.bat c:\mmj2\mmj2jar c:\mmj2 20111101y > c:\mmj2\data\result\VT2dOut20111101y.txt
