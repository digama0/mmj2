
PUSHD c:\mmj2\doc\mmj2Service

:STEP1
javac @TjavacTCompilePaths.txt @TSvcCallbackCalleeClasses.txt
IF ERRORLEVEL 1 GOTO :OUCH

:STEP2
java -Xincgc -Xms64M -Xmx128M -classpath .;c:\mmj2\doc\mmj2Service;c:\mmj2\classes mmj.util.BatchMMJ2 doc\mmj2Service\TSvcCallbackCalleeRunParms.txt Y c:\mmj2 c:\mmj2\data c:\mmj2\doc Y
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
