@echo off
REM Script de démarrage du frontend avec Vite
echo ========================================
echo Demarrage du frontend (Vite + TypeScript)
echo ========================================
echo.

cd /d "%~dp0"

REM Vérifier si node_modules existe
if not exist "node_modules\" (
    echo Installation des dependances...
    call npm install
    echo.
)

echo Demarrage du serveur de developpement Vite...
echo Le serveur sera accessible sur http://localhost:8080
echo.
echo Appuyez sur Ctrl+C pour arreter le serveur
echo.

call npm run dev
