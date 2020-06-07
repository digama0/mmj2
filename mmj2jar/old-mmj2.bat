@echo off
SET /P "JAVA=" || SET "JAVA=java"
SET /P "METAMATH_DB_DIR=" || SET "%HOME%\set.mm"
%JAVA% -Xms128M -Xmx1280M -jar mmj2.jar RunParms.txt Y "" %METAMATH_DB_DIR% ""
