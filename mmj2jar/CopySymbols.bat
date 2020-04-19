REM: You need to update mmj2's GMFF symbol .gif files whenever metamath's symbols.zip
REM: is updated: 
REM: 1) Download Metamath symbols.zip, 
REM: 2) Unzip to c:\metamath thus creating c:\metamath\symbols
REM: 3) Then run this (change directory names if you installed mmj2 or metamath differently)
REM:
xcopy c:\metamath\symbols\*.gif c:\mmj2\mmj2jar\gmff\html /S /E /A /Y /I
xcopy c:\metamath\symbols\langle.gif c:\mmj2\mmj2jar\gmff\althtml /S /E /A /Y /I
xcopy c:\metamath\symbols\rangle.gif c:\mmj2\mmj2jar\gmff\althtml /S /E /A /Y /I 
