REM ===================================================================
REM BuildDoc.bat runs the javadoc utility for a single mmj2 class
REM ===================================================================

PUSHD c:\mmj2

:STEP1:
javadoc src\mmj\verify\TypeConversionRule.java @doc\windows\DocPaths.txt @doc\windows\DocOptions.txt
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
