ECHO ON

:STEP1
CALL %2\test\windows\RunBatchTest.bat %1 UT2PA001.txt N %2\data %2\data %2\data

IF ERRORLEVEL 1 GOTO :OUCH

GOTO :ENDIT

:OUCH
ECHO ********************************************* 
ECHO *** RunUT2.bat ERROR ENCOUNTERED!
ECHO *********************************************
GOTO :ENDIT

:ENDIT:
GOTO :EOF
