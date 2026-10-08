@echo off
title Hero Academy - Test Client
echo ========================================================
echo   Iniciando Hero Academy Mod no Minecraft 1.21.1...
echo ========================================================
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
cd /d "%~dp0"
call gradlew.bat runClient
pause
