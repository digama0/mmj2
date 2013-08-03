REM ===================================================================
REM EraseMMJObjCode does:
REM     - erases old compiled classes in all MMJ packages
REM ===================================================================

PUSHD c:\mmj2

:STEP1
erase /Q classes\mmj\gmff\*.class
erase /Q classes\mmj\lang\*.class
erase /Q classes\mmj\mmio\*.class
erase /Q classes\mmj\pa\*.class
erase /Q classes\mmj\svc\*.class
erase /Q classes\mmj\tl\*.class
erase /Q classes\mmj\tmff\*.class
erase /Q classes\mmj\util\*.class
erase /Q classes\mmj\verify\*.class


GOTO :ENDIT

:OUCH
ECHO "******************************************" 
ECHO "*** EraseMMJObjCode.bat ERROR ENCOUNTERED!"
ECHO "******************************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF
