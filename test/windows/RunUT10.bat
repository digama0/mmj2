ECHO ON

:STEP10001
CALL %2\test\windows\RunBatchTest.bat %1 UT10001.txt N %2\data %2\data %2\data

:STEP10002
CALL %2\test\windows\RunBatchTest.bat %1 UT10002.txt N %2\data %2\data %2\data

:STEP10003
CALL %2\test\windows\RunBatchTest.bat %1 UT10003.txt N %2\data %2\data %2\data

:STEP10021
CALL %2\test\windows\RunBatchTest.bat %1 UT10021.txt N %2\data %2\data %2\data

:STEP10022
CALL %2\test\windows\RunBatchTest.bat %1 UT10022.txt N %2\data %2\data %2\data

:STEP10023
CALL %2\test\windows\RunBatchTest.bat %1 UT10023.txt N %2\data %2\data %2\data

:STEP10031
CALL %2\test\windows\RunBatchTest.bat %1 UT10031.txt N %2\data %2\data %2\data

:STEP10032
CALL %2\test\windows\RunBatchTest.bat %1 UT10032.txt N %2\data %2\data %2\data

:STEP10033
CALL %2\test\windows\RunBatchTest.bat %1 UT10033.txt N %2\data %2\data %2\data

:STEP10041
CALL %2\test\windows\RunBatchTest.bat %1 UT10041.txt N %2\data %2\data %2\data

:STEP10042
CALL %2\test\windows\RunBatchTest.bat %1 UT10042.txt N %2\data %2\data %2\data

:STEP10043 
CALL %2\test\windows\RunBatchTest.bat %1 UT10043.txt N %2\data %2\data %2\data

:STEP10051
CALL %2\test\windows\RunBatchTest.bat %1 UT10051.txt N %2\data %2\data %2\data

:STEP10052
CALL %2\test\windows\RunBatchTest.bat %1 UT10052.txt N %2\data %2\data %2\data

:STEP10053
CALL %2\test\windows\RunBatchTest.bat %1 UT10053.txt N %2\data %2\data %2\data

:STEP19100
CALL %2\test\windows\RunBatchTest.bat %1 UT19100.txt N %2\data %2\data %2\data

TYPE %2\data\mmt\temp\csbima12g.mmt
TYPE %2\data\mmt\testin\t191\csbima12g.mmt
TYPE %2\data\mmt\testout\t191\csbima12g.mmt
TYPE %2\data\mmt\testout\t192\csbima12g.mmt
ERASE %2\data\mmt\temp\csbima12g.mmt
ERASE %2\data\mmt\testout\t191\csbima12g.mmt
ERASE %2\data\mmt\testout\t192\csbima12g.mmt

GOTO :ENDIT

:ENDIT:
GOTO :EOF
 
