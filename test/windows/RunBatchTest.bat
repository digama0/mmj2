REM ============================================================
REM RunBatchTest.bat is a subroutine .bat file that
REM invokes mmj2.jar to execute batch regression
REM tests.
REM
REM Parameter 1: directory containing mmj2.jar
REM              (e.g. c:\mmj2\mmj2jar)
REM
REM Parameter 2: RunParm File Name in runparm\windows directory
REM              within parameter 3 path
REM              (e.g. runparms.txt)
REM
REM Parameter 3: mmj2 fail popup window, y or n
REM
REM Parameter 4: directory containing mmj2 batch test data
REM              (e.g. c:\mmj2\data).
REM
REM Parameter 5: directory containing metamath batch test data
REM              (e.g. c:\mmj2\data). 
REM
REM Parameter 6: directory containing svc batch test data
REM              (e.g. c:\mmj2\data). 
REM ============================================================
 
ECHO ON

:STEP1  

java -Xincgc -Xms128M -Xmx256M -jar %1\mmj2.jar runparm\windows\%2 %3 %4 %5 %6
IF ERRORLEVEL 1 GOTO :OUCH
 
GOTO :ENDIT

:OUCH 
ECHO "***************************************" 
ECHO "*** RunBatchTest.bat ERROR ENCOUNTERED!"
ECHO "***************************************"
GOTO :ENDIT 

:ENDIT:
POPD
GOTO :EOF 
