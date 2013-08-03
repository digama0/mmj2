REM ===================================================================
REM CompMMJ does:
REM     - erases old compiled classes
REM     - compiles each mmj2 package (computers are so fast nowadays
REM       that it is simplest to just recompile everything everytime.)
REM     - creates a jar file for mmj2 
REM ===================================================================

PUSHD c:\mmj2

:STEP0
CALL compile\windows\EraseMMJObjCode.bat 

:STEP1
CALL compile\windows\CompPackage.bat c:\mmj2 mmio
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP2
CALL compile\windows\CompPackage.bat c:\mmj2 lang
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP3
CALL compile\windows\CompPackage.bat c:\mmj2 verify
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP4
CALL compile\windows\CompPackage.bat c:\mmj2 util
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP5
CALL compile\windows\CompPackage.bat c:\mmj2 pa
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP6
CALL compile\windows\CompPackage.bat c:\mmj2 tmff
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP7
CALL compile\windows\CompPackage.bat c:\mmj2 svc
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP8
CALL compile\windows\CompPackage.bat c:\mmj2 tl
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP9
CALL compile\windows\CompPackage.bat c:\mmj2 gmff
REM IF ERRORLEVEL 1 GOTO :OUCH

:STEP99
CALL compile\windows\mkjar.bat c:\mmj2
REM IF ERRORLEVEL 1 GOTO :OUCH


GOTO :ENDIT


:OUCH
ECHO "**********************************" 
ECHO "*** CompMMJ.bat ERROR ENCOUNTERED!"
ECHO "**********************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF
