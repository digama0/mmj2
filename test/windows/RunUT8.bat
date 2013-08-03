
PUSHD %4\mmj2Service
 
:STEP1
javac @TjavacTCompilePaths.txt @TSvcCallbackCalleeClasses.txt
IF ERRORLEVEL 1 GOTO :OUCH

:STEP2
javac @TjavacTCompilePaths.txt @TSvcCallbackCallerClasses.txt
IF ERRORLEVEL 1 GOTO :OUCH

:STEP3 
java -Xincgc -Xms64M -Xmx128M -classpath .;%4\mmj2Service;c:\mmj2\classes mmj.util.BatchMMJ2 %4\mmj2Service\TSvcCallbackCalleeRunParms.txt Y %2 %3 %4

:STEP4
java -Xincgc -Xms64M -Xmx128M -classpath .;%4\mmj2Service;%1\mmj2.jar TSvcCallbackCaller %4\mmj2Service\TSvcCallbackCallerRunParms.txt Y %2 %3 %4

GOTO :ENDIT

:OUCH
ECHO "****************************************"
ECHO "**** ERROR ENCOUNTERED!                 "
ECHO "****************************************"
GOTO :ENDIT


:ENDIT:
POPD
GOTO :EOF
