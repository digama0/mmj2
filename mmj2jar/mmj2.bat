rem @echo off

rem Invoke mmj2 and use a more "conventional" CLI.
rem For example, support option flags so that people can use the same
rem command instead of rewriting it each time, and support filenames
rem as parameters.

rem Usage:
rem mmj2 [ -f RUNCOMMANDS ] [ -d METAMATH_DATABASE_FILE ] [ FILE ]
rem If FILE is a .mmp file, open that .mmp file when starting out.
rem If FiLE is a .mm file, open that database and use a "default" .mmp file.

rem Note: If RUNCOMMANDS are given, we don't open the .mmp file ourselves
rem and we merely set the METAMATH_DATABASE_FILE directory.

rem If METAMATH_DATABASE_FILE given, then we use it if present otherwise
rem we search for it.
rem If METAMATH_DATABASE_FILE is not given, we try to load the default
rem (the last one used), and if there is no last one, we search for 'set.mm'.
rem We store the "last one opened" in $HOME/.config/mmj2/database
rem (Windows "!LOCALAPPDATA!\mmj2\database").
rem See: https://unix.stackexchange.com/questions/312988/
rem understanding-home-configuration-file-locations-config-and-local-sha

rem Uses various environment variables:
rem JAVA: path to executable for running Java .jar files
rem METAMATH_DATABASE_FILE:  Metamath database file to load (default: last one)
rem METAMATH_DB_PATH: List of directories to look for set.mm
rem MMJ2_JAR: mmj2.jar path (default: this_script_directory + "/mmj2.jar")
rem MMJ2_MINIMUM_MEMORY: Memory minimum (default '128M')
rem MMJ2_MAXIMUM_MEMORY: Memory minimum (default '1280M')

rem This file was created by hand-converting "mmj2" to "mmj2.bat"; try to
rem keep them in sync. A few notes:
rem - "::" is NOT the same as "rem", it interprets some things!

rem .bat files normally expand variables on when *reading*, not *executing*.
rem This makes "IF" and "FOR" incompatible with use of variables :-(.
rem Enable "delayed" expansion so IF and FOR work, anD Use !...! not %...%.
SETLOCAL
SETLOCAL EnableDelayedExpansion

IF NOT DEFINED JAVA SET JAVA=java
IF NOT DEFINED MMJ2_MINIMUM_MEMORY SET MMJ2_MINIMUM_MEMORY=128M
IF NOT DEFINED MMJ2_MAXIMUM_MEMORY SET MMJ2_MAXIMUM_MEMORY=1280M
rem MMJ2_JAR The actual default is based on the script directory
IF NOT DEFINED METAMATH_DB_PATH SET METAMATH_DB_PATH=.;C:\metamath;c:\set.mm;!HOME!\metamath;!HOME!\set.mm;c:\cygwin64\home\%USERNAME%\metamath;c:\cygwin64\home\%USERNAME%\set.mm
IF NOT DEFINED METAMATH_DB_PATH SET METAMATH_DB_PATH=.;C:\set.mm;C:\metamath;!HOME!\set.mm;!HOME!\metamath;C:\cygwin64\home\%USERNAME%\set.mm;C:\cygwin64\home\%USERNAME%\metamath

SET mm_db=
SET proof_file=
SET mmj2_run_cmds=
SET temporary_file=
SET file_naming_last_db=!HOME!\.config\mmj2\default_database

rem Skip utility functions, go to main line.
GOTO MAIN

rem Utility functions
:fail
ECHO Error: %~1
EXIT /B 1

rem Determine if a file and NOT directory. Return true (0) if so.
:isfile
IF EXIST %~1 (
  rem TODO: Return FALSE if a directory.
  rem   if [ -e "$mm_db" ] && ! [ -d "$mm_db" ] ; then
  EXIT /B 0
)
EXIT /B 1

rem Trying to implement a "search_path" function is an exercise in
rem frustration in Windows, so I gave up and inline the search.

rem End of function definitions
:MAIN

rem Begin main program.

rem Handle options.
rem Someday we should detect unknown options and too many files.
:processargs
SET arg=%1
IF DEFINED arg (
    IF "!arg!" == "-d" (
      SET mm_db=%2
      SHIFT /1
      SHIFT /1
      GOTO processargs
    )
    IF "!arg!" == "-d" (
      SET mmj2_run_cmds=%2
      SHIFT /1
      SHIFT /1
      GOTO processargs
    )
)

rem If a filename outside an option has been provided, determine what it is.

SET arg=%1
IF DEFINED arg (
  IF "!arg:~-3!" == ".mm" (
    SET mm_db=!arg!
  )
  IF "!arg:~-4!" == ".mmp" (
    SET proof_file=!arg!
  )
  rem TODO - error checking:
  rem       if [ -n "$proof_file" ] && ! [ -e "$proof_file" ] ; then
  rem         fail "No such proof file: ${proof_file}"
  rem       fi ;;
  rem     (*) fail "Filename not allowed: $1" ;;
)

rem Find mmj2.jar to run.

IF not "!MMJ2_JAR!" == "" (
  SET mmj2_jarfile=!MMJ2_JAR!
) ELSE (
  SET mmj2_jarfile=%~dp0
  SET mmj2_jarfile=!mmj2_jarfile!\mmj2.jar
)
IF not exist "!mmj2_jarfile!" (
  CALL :fail "Cannot find JAR file !mmj2_jarfile!"
  GOTO END
)

rem Find Metamath database to load
rem If given, we use it if present and search for it if not present.
rem If not given, try to load from default, and if that doesn't work,
IF not "!mm_db!" == "" (
  rem The following determines if mm_db is a *normal* file (not a directory)
  CALL :isfile "!mm_db!"
  IF errorlevel 1 (
    FOR /D %%d IN (!METAMATH_DB_PATH!) DO (
      SET looking_for=%%d\!mm_db!
      IF EXIST !looking_for! (
	rem Found a database!
        SET mm_db=!looking_for!
        GOTO DONE_METAMATH_DB_SEARCH
      )
    )
  )
) ELSE (
  rem No database specifed, use default (last one) else hunt for it
  IF EXIST "!file_naming_last_db!" (
      set /p mm_db=< "!file_naming_last_db!"
      echo "Using MM database !mm_db! - recorded as default in !file_naming_last_db!"
  ) ELSE (
    rem Hunt for metamath database set.mm in METAMATH_DB_PATH
    FOR /D %%d IN (!METAMATH_DB_PATH!) DO (
      SET looking_for=%%d\set.mm
      IF EXIST !looking_for! (
	rem Found a database!
        SET mm_db=!looking_for!
        GOTO DONE_METAMATH_DB_SEARCH
      )
    )
  )
)
:DONE_METAMATH_DB_SEARCH

IF "!mm_db!" == "" (
  CALL :fail "No MM database found"
  GOTO END
)
IF not EXIST "!mm_db!" (
  CALL :fail "MM database does not exist: !mm_db!"
  GOTO END
)

rem Record absolute pathname of database and record for next time.
rem Note the weird %... things to implement basename and dirname, see
rem https://stackoverflow.com/questions/3432851/
rem dos-bat-file-equivalent-to-unix-basename-command
FOR /F %%i in ("!mm_db!") DO SET mm_db=%%~fi
FOR /F %%i in ("!file_naming_last_db!") DO SET file_naming_last_db_dir=%%~dpi
IF NOT EXIST "!file_naming_last_db_dir!" MKDIR !file_naming_last_db_dir!
ECHO !mm_db! > !file_naming_last_db!
FOR /F %%i in ("!mm_db!") DO SET mm_db_basename=%%~nxi
FOR /F %%i in ("!mm_db!") DO SET mm_db_dirname=%%~dpi

rem Auto-create runcommands unless they were given
IF "!mmj2_run_cmds!" == "" (
  rem Create temporary file & create run commands in it
  :uniqLoop
  SET "temporary_file=!tmp!\mmj2cmds-~%RANDOM%.tmp"
  IF EXIST "temporary_file!" goto :uniqLoop

  echo LoadFile,!mm_db_basename! > "!temporary_file!"
  echo VerifyProof,* >> "!temporary_file!"
  echo Parse,* >> "!temporary_file!"
  echo ProofAsstUnifySearchExclude,biigb,xxxid,dummylink >> "!temporary_file!"
  rem Pick a starting directory for .mmp files, preferring proof_file's.
  IF not "!proof_file!" == "" (
    rem Implement proof_folder="$(dirname "$proof_file")"
    FOR %%F IN ("!proof_file!") DO SET proof_folder=%%~dpF
  ) ELSE (
    IF EXIST "myproofs" (
      SET proof_folder=myproofs
    ) ELSE (
      SET proof_folder=.
    )
  )
  echo ProofAsstProofFolder,!proof_folder! >> "!temporary_file!"
  echo TheoremLoaderMMTFolder,!proof_folder! >> "!temporary_file!"

  rem If .mmp file provided, tell mmj2 to open it.
  IF not "!proof_file!" == "" (
    echo ProofAsstStartupProofWorksheet,!proof_file! >> "!temporary_file!"
  )
  echo RunProofAsstGUI >> "!temporary_file!"
  SET mmj2_run_cmds=!temporary_file!
)

rem Actually run mmj2 program!
echo About to run: "!JAVA!" -Xms!MMJ2_MINIMUM_MEMORY! -Xmx!MMJ2_MAXIMUM_MEMORY! -jar "!mmj2_jarfile!" !mmj2_run_cmds! Y "" !mm_db_dirname!

"!JAVA!" -Xms!MMJ2_MINIMUM_MEMORY! -Xmx!MMJ2_MAXIMUM_MEMORY! -jar "!mmj2_jarfile!" !mmj2_run_cmds! Y "" !mm_db_dirname!

rem Erase temporary file if created
IF EXIST !temporary_file! (
  DEL !temporary_file!
)

:END
ENDLOCAL
