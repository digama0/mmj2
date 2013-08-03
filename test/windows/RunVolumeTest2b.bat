REM Volume Test 2b is the same as Volume Test 2
REM except that its RunParms use the new TMFF
REM formatting for exported Proof Worksheets, AND 
REM it uses the "Randomized" RunParm option
REM which randomizes the sequence of Hyp numbers
REM on each Derivation Proof Step (to make things
REM more interesting.)

erase c:\mmj2\data\result\VT2bOut20111101y.txt  
 
c:\mmj2\test\windows\RunVT2b.bat c:\mmj2\mmj2jar c:\mmj2 20111101y > c:\mmj2\data\result\VT2bOut20111101y.txt
  
