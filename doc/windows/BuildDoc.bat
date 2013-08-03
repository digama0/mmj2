REM ===================================================================
REM BuildDoc.bat runs the javadoc utility for mmj2
REM ===================================================================

PUSHD c:\mmj2

:STEP0
CALL doc\windows\EraseMMJJavaDoc.bat


:STEP1:
javadoc @doc\windows\DocPackages.txt @doc\windows\DocPaths.txt @doc\windows\DocOptions.txt
IF ERRORLEVEL 1 GOTO :OUCH

GOTO :ENDIT

:OUCH
ECHO "**************************************"
ECHO "*** BuildDoc.bat ERROR ENCOUNTERED!"
ECHO "**************************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF
