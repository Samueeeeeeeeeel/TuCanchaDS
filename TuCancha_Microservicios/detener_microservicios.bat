@echo off
chcp 65001 >nul
title TuCancha - Detener Microservicios

echo ╔════════════════════════════════════════════════════════════════╗
echo ║           TUCANCHA - DETENIENDO MICROSERVICIOS                 ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

:: Matar procesos Java de Spring Boot en los puertos específicos
echo Deteniendo microservicios...

:: Encontrar y matar procesos en los puertos 8081, 8082, 8083, 8084
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8081"') do taskkill /F /PID %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8082"') do taskkill /F /PID %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8083"') do taskkill /F /PID %%a 2>nul
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8084"') do taskkill /F /PID %%a 2>nul

echo.
echo [OK] Microservicios detenidos.
echo.
pause
