REM ===================================================================
REM EraseMMJJavaDoc does:
REM     - erases mmj javadoc created by BuildDoc.bat
REM ===================================================================

PUSHD c:\mmj2

:STEP1
erase /Q /S doc\mmj\*.*
erase /Q /S doc\resources\*.*
erase /Q /S doc\src-html\*.*
erase doc\package-list
erase doc\overview-frame.html
erase doc\constant-values.html
erase doc\overview-summary.html
erase doc\index-all.html
erase doc\index.html
erase doc\help-doc.html
erase doc\deprecated-list.html
erase doc\allclasses-noframe.html
erase doc\allclasses-frame.html
erase doc\overview-tree.html
erase doc\serialized-form.html
erase doc\stylesheet.css

GOTO :ENDIT

:OUCH
ECHO "******************************************" 
ECHO "*** EraseMMJJavaDoc.bat ERROR ENCOUNTERED!"
ECHO "******************************************"
GOTO :ENDIT
 

:ENDIT:
POPD
GOTO :EOF
