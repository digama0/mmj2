REM ===================================================================
REM mkjar does the following:
REM     - erases old mmj2.jar file
REM     - creates the mmj2 jar file from previously compiled packages.
REM     - lists contents of new mmj2 jar file
REM 
REM Parameter 1: top directory of mmj2 (example: c:\mmj2)
REM  
REM ===================================================================

PUSHD %1

:STEP1: 

erase mmj2jar\mmj2.jar

:STEP2:
jar cfm mmj2jar\mmj2.jar compile\windows\MANIFEST.MF @compile\windows\mkjarargs.txt
IF ERRORLEVEL 1 GOTO :OUCH

:STEP3:
jar tvf mmj2jar\mmj2.jar
IF ERRORLEVEL 1 GOTO :OUCH

GOTO :ENDIT

:OUCH
ECHO "********************************"
ECHO "*** mkjar.bat ERROR ENCOUNTERED!"
ECHO "********************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF

