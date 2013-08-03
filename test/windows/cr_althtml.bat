REM ============================================================
REM Read a metamath file and create web pages using unicode
REM math characters. The output /althtml goes in the directory
REM where this batch file is executed from -- I am providing
REM this for informational purposes, and you will need to
REM move and/or modify it to suit your needs.
REM ============================================================
metamath "r set.mm" "v proof *" "sh statement * / alt_html" "write theorem_list" "exit" >> cr_althtml.log
