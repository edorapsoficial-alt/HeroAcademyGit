@echo off
title Hero Academy - Compilar Mod JAR
echo ========================================================
echo   Compilando Hero Academy Mod (.jar)...
echo ========================================================
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot"
cd /d "%~dp0"
call gradlew.bat build --no-daemon
echo.
echo ========================================================
echo   Mod compilado com sucesso!
echo   Arquivo gerado: build\libs\heroacademy-1.0.0.jar
echo ========================================================
pause
