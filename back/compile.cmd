@echo off
REM Script Batch pour démarrer tous les microservices

echo =====================================
echo Compilation des microservices e-commerce
echo =====================================
echo.

set SCRIPT_DIR=%~dp0

echo Compilation de tous les modules...
start "Compilation" cmd /k "mvn clean install"
timeout /t 2 /nobreak >nul

