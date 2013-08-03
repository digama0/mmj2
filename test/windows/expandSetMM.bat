REM ====================================================================
REM .bat file to run metamath.exe and create
REM an expanded version of set.mm -- with
REM *de-compressed* proofs.
REM
REM     Input  c:\metamath\set.mm     (as-is from metamath.org)
REM     Output c:\metamath\expset.mm  (de-comressed proofs)
REM     Output c:\metamath\expset.txt (a log)
REM                                 
REM ====================================================================
cd c:\metamath
metamath.exe "r set.mm" "v proof *" "sa p *" "w s expset.mm" "exit" >> expset.txt
