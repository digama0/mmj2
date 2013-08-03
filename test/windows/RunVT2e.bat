ECHO ON

:STEP1 
CALL %2\test\windows\RunBatchTest.bat %1 VT2ePA002.txt N %2\data %2\data %2\data
IF ERRORLEVEL 1 GOTO :OUCH

GOTO :ENDIT
 
:OUCH 
ECHO "*********************************************" 
ECHO "*** RunVT2.bat ERROR ENCOUNTERED!"
ECHO "*********************************************"
GOTO :ENDIT

:ENDIT:
GOTO :EOF
