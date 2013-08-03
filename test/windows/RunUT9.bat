ECHO ON

:STEP9001
CALL %2\test\windows\RunBatchTest.bat %1 UT9001.txt N %2\data %2\data %2\data

:STEP9002
CALL %2\test\windows\RunBatchTest.bat %1 UT9002.txt N %2\data %2\data %2\data

:STEP9003
CALL %2\test\windows\RunBatchTest.bat %1 UT9003.txt N %2\data %2\data %2\data

GOTO :ENDIT

:ENDIT:
GOTO :EOF
 
