ECHO ON

:STEP1 
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c01.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c02.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c03.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c04.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c05.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c06.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c07.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTIO1c08.txt N %2\data %2\data %2\data

CALL %2\test\windows\RunBatchTest.bat %1 UTLA1c01.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTLA1c02.txt N %2\data %2\data %2\data

CALL %2\test\windows\RunBatchTest.bat %1 UTPR1c01.txt N %2\data %2\data %2\data

CALL %2\test\windows\RunBatchTest.bat %1 UTGR1c01.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTGR1c02.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTGR1c03.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTGR1c04.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTGR1c05.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UTGR1c06.txt N %2\data %2\data %2\data
 
CALL %2\test\windows\RunBatchTest.bat %1 PTAPGc7s1p146.txt N %2\data %2\data %2\data

CALL %2\test\windows\RunBatchTest.bat %1 PTAPGc7s1p148.txt N %2\data %2\data %2\data

CALL %2\test\windows\RunBatchTest.bat %1 PTAPGc7s2p160.txt N %2\data %2\data %2\data

GOTO :ENDIT

:ENDIT:
GOTO :EOF
 
