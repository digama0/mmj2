REM Volume Test 2c is the same as Volume Test 2
REM except that its batch test RunParm has the
REM CompareDJs and UpdateDJs options to recalculate
REM the mandatory DjVars restrictions for each
REM theorem in the input set.mm file and store
REM the results back in memory as the pass through
REM the file is completed (thus resulting in 
REM a compounding effect where superfluous DjVars
REM early in the .mm file are eliminated and
REM additional input $d statements are found
REM to be superfluous too as a result of the
REM prior updates. And, after the DjVars processing,
REM the VerifyProofs is rerun to confirm that
REM the proofs are still valid. 
PUSHD c:\mmj2\data\result
   
erase c:\mmj2\data\result\VT2cOut20111101y.txt  
 
c:\mmj2\test\windows\RunVT2c.bat c:\mmj2\mmj2jar c:\mmj2 20111101y > c:\mmj2\data\result\VT2cOut20111101y.txt
