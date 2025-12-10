@echo off
chcp 65001 >nul
title TuCancha - Microservicios

echo ╔════════════════════════════════════════════════════════════════╗
echo ║           TUCANCHA - INICIANDO MICROSERVICIOS                  ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

:: Verificar que MySQL esté corriendo
echo [1/5] Verificando conexión a MySQL...
mysql -u root -e "SELECT 1" >nul 2>&1
if %errorlevel% neq 0 (
    echo [ERROR] MySQL no está corriendo. Por favor inicia Laragon y MySQL primero.
    echo.
    pause
    exit /b 1
)
echo [OK] MySQL está corriendo.
echo.

:: Crear bases de datos si no existen
echo [2/5] Creando bases de datos...
mysql -u root -e "CREATE DATABASE IF NOT EXISTS db_canchas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -e "CREATE DATABASE IF NOT EXISTS db_disponibilidad CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -e "CREATE DATABASE IF NOT EXISTS db_login CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
mysql -u root -e "CREATE DATABASE IF NOT EXISTS db_reservas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
echo [OK] Bases de datos creadas/verificadas.
echo.

:: Iniciar microservicios en ventanas separadas
echo [3/5] Iniciando microservicio CANCHAS (puerto 8081)...
start "TuCancha - Canchas [8081]" cmd /k "cd /d "%~dp0Canchas" && mvnw.cmd spring-boot:run"
timeout /t 3 >nul

echo [4/5] Iniciando microservicio DISPONIBILIDAD (puerto 8082)...
start "TuCancha - Disponibilidad [8082]" cmd /k "cd /d "%~dp0Disponibilidad" && mvnw.cmd spring-boot:run"
timeout /t 3 >nul

echo [4/5] Iniciando microservicio LOGIN (puerto 8083)...
start "TuCancha - Login [8083]" cmd /k "cd /d "%~dp0Login" && mvnw.cmd spring-boot:run"
timeout /t 3 >nul

echo [5/5] Iniciando microservicio RESERVAS (puerto 8084)...
start "TuCancha - Reservas [8084]" cmd /k "cd /d "%~dp0Reservas" && mvnw.cmd spring-boot:run"

echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║              MICROSERVICIOS INICIADOS                          ║
echo ╠════════════════════════════════════════════════════════════════╣
echo ║  Canchas:        http://localhost:8081/swagger-ui.html         ║
echo ║  Disponibilidad: http://localhost:8082/swagger-ui.html         ║
echo ║  Login:          http://localhost:8083/swagger-ui.html         ║
echo ║  Reservas:       http://localhost:8084/swagger-ui.html         ║
echo ╠════════════════════════════════════════════════════════════════╣
echo ║  Espera ~30 segundos para que todos los servicios inicien      ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
echo Presiona cualquier tecla para cerrar esta ventana...
pause >nul
