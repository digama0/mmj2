ECHO ON

:STEP1101
CALL %2\test\windows\RunBatchTest.bat %1 Sample001.txt N %2\data %2\data %2\data
 
GOTO :ENDIT

:ENDIT:

GOTO :EOF
 
