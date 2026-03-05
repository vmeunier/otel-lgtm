@echo off
REM Script Batch pour demarrer tous les microservices

echo =====================================
echo Demarrage des microservices e-commerce
echo =====================================
echo.
set SCRIPT_DIR=%~dp0

set PROG_ARGS=-Dspring-boot.run.arguments="--spring.config.additional-location=%SCRIPT_DIR%\..\dev\common\logging.properties"

echo Demarrage de Inventory Service sur le port 8083...
start "Inventory Service (8083)" cmd /k "cd /d "%SCRIPT_DIR%inventory-service" && echo Demarrage de Inventory Service... && mvn -am compile spring-boot:run %PROG_ARGS%"
timeout /t 2 /nobreak >nul

echo Demarrage de Cart Service sur le port 8085...&
start "Cart Service (8085)" cmd /k "cd /d "%SCRIPT_DIR%cart-service" && echo Demarrage de Cart Service... && mvn -am compile spring-boot:run %PROG_ARGS%"
timeout /t 2 /nobreak >nul

echo Demarrage de Payment Service sur le port 8082...
start "Payment Service (8082)" cmd /k "cd /d "%SCRIPT_DIR%payment-service" && echo Demarrage de Payment Service... && mvn -am compile spring-boot:run %PROG_ARGS%"
timeout /t 2 /nobreak >nul

echo Demarrage de Order Service sur le port 8084...
start "Order Service (8084)" cmd /k "cd /d "%SCRIPT_DIR%order-service" && echo Demarrage de Order Service... && mvn -am compile spring-boot:run %PROG_ARGS%"
timeout /t 2 /nobreak >nul

echo Demarrage du frontend sur le port 8080...
start "FrontEnd (8080)" cmd /k "cd /d "%SCRIPT_DIR%../frontend" && echo Demarrage du frontend... && npm run dev"
timeout /t 2 /nobreak >nul

echo.
echo Tous les services sont en cours de demarrage...
echo.
echo URLs des services:
echo   - Inventory Service: http://localhost:8083
echo   - Cart Service:      http://localhost:8085
echo   - Payment Service:   http://localhost:8082
echo   - Order Service:     http://localhost:8084
echo.
echo Consoles H2:
echo   - Inventory: http://localhost:8083/h2-console
echo   - Cart:      http://localhost:8085/h2-console
echo   - Payment:   http://localhost:8082/h2-console
echo   - Order:     http://localhost:8084/h2-console
echo.
echo URL du front-end React: http://localhost:8080
echo.
pause

