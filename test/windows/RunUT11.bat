ECHO ON

erase %2\data\gmfftest\althtml\*%3.html
erase %2\data\gmfftest\html\*%3.html
erase %2\data\gmfftest\myproofs\*%3.mmp
erase %2\data\gmfftest\myproofs\*%3.mmt

:STEP1101
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF01.txt N %2\data %2\data %2\data
 
:STEP1102
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF02.txt N %2\data %2\data %2\data

:STEP1103
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF03.txt N %2\data %2\data %2\data

:STEP1104
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF04.txt N %2\data %2\data %2\data
 
:STEP1105
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF05.txt N %2\data %2\data %2\data

:STEP1106
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF06.txt N %2\data %2\data %2\data

:STEP1107
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF07.txt N %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF07.txt X %2\data %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF07.txt N c:\ASDFASDF\java\gmff %2\data %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF07.txt N %2\data c:\ASDFASDF\java\gmff %2\data
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF07.txt N %2\data %2\data c:\ASDFASDF\java\gmff
CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF07.txt N %2\data %2\data %2\data

:STEP1108

CALL %2\test\windows\RunBatchTest.bat %1 UT11GMFF08.txt Y %2\data %2\data %2\data

  
GOTO :ENDIT

:ENDIT:

rename %2\data\gmfftest\althtml\UT11Out.html UT11Out%3.html
rename %2\data\gmfftest\html\UT11Out.html    UT11Out%3.html
rename %2\data\gmfftest\myproofs\export.mmp  export%3.mmp
rename %2\data\gmfftest\myproofs\a2i.mmt     a2i%3.mmt
rename %2\data\gmfftest\myproofs\syl.mmt     syl%3.mmt

GOTO :EOF
 
