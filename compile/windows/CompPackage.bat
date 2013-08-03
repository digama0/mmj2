REM ===================================================================
REM CompPackage compiles a single mmj2 package.
REM 
REM Parameter 1: top directory of mmj2
REM           2: last node of package name 
REM              (i.e. "util" for package "mmj.util"). 
REM
REM Note: the two command line parms for javac give the compiler
REM       the source, destination, classpath and a list of
REM       the .java files to compile -- each package has a
REM       .txt file created manually specifying its component
REM       .java source files -- example:
REM           c:\mmj2\compile\mmj\util\UtilClasses.txt
REM
REM ===================================================================

PUSHD %1

:STEP1:
javac @compile\windows\javacCompilePaths.txt @compile\mmj\%2\%2Classes.txt
IF ERRORLEVEL 1 GOTO :OUCH

GOTO :ENDIT

:OUCH 
ECHO "**************************************"
ECHO "*** CompPackage.bat ERROR ENCOUNTERED!"
ECHO "**************************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF
