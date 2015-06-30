@echo off
cls
call mvn -o -B install
cd launch4j-plugin-usage
call mvn -o -B clean package
rem > a.txt
cd target
call main.exe
cd..
cd..
