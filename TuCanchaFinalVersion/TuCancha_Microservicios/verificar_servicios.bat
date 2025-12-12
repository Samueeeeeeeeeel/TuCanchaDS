@echo off
chcp 65001 >nul
title TuCancha - Verificar Servicios

echo ╔════════════════════════════════════════════════════════════════╗
echo ║           TUCANCHA - VERIFICANDO SERVICIOS                     ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

:: Verificar MySQL
echo [1] Verificando MySQL...
mysql -u root -e "SHOW DATABASES LIKE 'db_%%';" 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] MySQL no está corriendo o no se puede conectar.
    echo         Asegúrate de iniciar Laragon primero.
) else (
    echo [OK] MySQL funcionando correctamente.
)
echo.

:: Verificar puertos de microservicios
echo [2] Verificando microservicios...
echo.

echo     Puerto 8081 (Canchas):
curl -s -o nul -w "    Estado: %%{http_code}\n" http://localhost:8081/api/canchas 2>nul || echo     [NO RESPONDE] - No está corriendo

echo     Puerto 8082 (Disponibilidad):
curl -s -o nul -w "    Estado: %%{http_code}\n" http://localhost:8082/api/disponibilidad 2>nul || echo     [NO RESPONDE] - No está corriendo

echo     Puerto 8083 (Login):
curl -s -o nul -w "    Estado: %%{http_code}\n" http://localhost:8083/api/usuarios 2>nul || echo     [NO RESPONDE] - No está corriendo

echo     Puerto 8084 (Reservas):
curl -s -o nul -w "    Estado: %%{http_code}\n" http://localhost:8084/api/reservas 2>nul || echo     [NO RESPONDE] - No está corriendo

echo.
echo ════════════════════════════════════════════════════════════════
echo.

:: Mostrar IP para dispositivo físico
echo [3] Tu IP para dispositivo físico Android:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    echo     %%a
)
echo.
echo     Si usas dispositivo físico, actualiza ApiConfig.kt con tu IP.
echo.
echo ════════════════════════════════════════════════════════════════
pause
