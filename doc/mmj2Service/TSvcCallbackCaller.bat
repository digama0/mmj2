PUSHD c:\mmj2\doc\mmj2Service

:STEP1
javac @TjavacTCompilePaths.txt @TSvcCallbackCallerClasses.txt
IF ERRORLEVEL 1 GOTO :OUCH

:STEP2
java -Xincgc -Xms64M -Xmx128M -classpath .;c:\mmj2\doc\mmj2Service;c:\mmj2\mmj2jar\mmj2.jar TSvcCallbackCaller doc\mmj2Service\TSvcCallbackCallerRunParms.txt Y c:\mmj2 c:\mmj2\data c:\mmj2\doc Y
IF ERRORLEVEL 1 GOTO :OUCH

GOTO :ENDIT


:OUCH
ECHO "****************************************"
ECHO "**** ERROR ENCOUNTERED!                 "
ECHO "****************************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF
 
