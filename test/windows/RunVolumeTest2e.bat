REM Volume Test 2e is the same as Volume Test 2
REM except that its batch test RunParm uses the
REM DeriveFormulas option with AsciiRetest and
REM ProofAsstAutoReformat,no. 
REM
REM Also important are RunParms:
REM     
REM     ProofAsstDjVarsSoftErrors,GenerateReplacements
REM     RecheckProofAsstUsingProofVerifier,yes
 
erase c:\mmj2\data\result\VT2eOut20111101y.txt  
 
c:\mmj2\test\windows\RunVT2e.bat c:\mmj2\mmj2jar c:\mmj2 20111101y > c:\mmj2\data\result\VT2eOut20111101y.txt
