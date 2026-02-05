@echo off
REM Script pour lancer les tests de charge Gatling

echo ========================================
echo   Tests de Charge Gatling - E-commerce
echo ========================================
echo.

REM Vérifier que les services sont démarrés
echo [1/3] Verification des services...
echo.
echo Assurez-vous que les services suivants sont demarres :
echo   - Inventory Service (port 8083)
echo   - Cart Service (port 8085)
echo   - Payment Service (port 8082)
echo   - Order Service (port 8084)
echo.

pause

echo.
echo [2/3] Lancement des tests de charge...
echo.
echo Configuration :
echo   - Duree : 20 minutes
echo   - Utilisateurs : 5-10 simultanes
echo   - Scenarios : Catalogue, Panier, Commande
echo.

REM Aller dans le dossier load-tests
cd load-tests

REM Lancer Gatling avec Maven
echo [3/3] Execution de Gatling...
echo.
call mvn gatling:test

echo.
echo ========================================
echo   Tests de charge termines !
echo ========================================
echo.
echo Les rapports sont disponibles dans :
echo   load-tests\target\gatling\
echo.
echo Ouvrez le fichier index.html dans votre navigateur pour voir les resultats.
echo.

pause
