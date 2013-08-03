ECHO ON

erase %2\data\result\UT4dump150.mmp
erase %2\data\result\UT4dump150%3.mmp

:STEP1
CALL %2\test\windows\RunBatchTest.bat %1 UT4001.txt N %2\data %2\data %2\data

GOTO :ENDIT

:ENDIT:
rename %2\data\result\UT4dump150.mmp UT4dump150%3.mmp 

GOTO :EOF

 
