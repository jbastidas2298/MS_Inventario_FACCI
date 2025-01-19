@echo off
REM Verificar si Docker Desktop ya está en ejecución
tasklist | findstr /i "Docker Desktop.exe" >nul
if %errorlevel% equ 0 (
    echo Docker Desktop ya está en ejecución.
) else (
    echo Iniciando Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"
    timeout /t 60 /nobreak
)

REM Esperar hasta que Docker esté completamente operativo
echo Esperando que Docker esté listo...
:esperarDocker
docker info >nul 2>&1
if %errorlevel% neq 0 (
    timeout /t 10 /nobreak
    goto esperarDocker
)

REM Cambiar a la carpeta del proyecto
cd C:\desarrollo

REM Levantar los contenedores
docker compose up --build -d

REM Registro del proceso
echo "Docker y contenedores levantados en %date% %time%" >> C:\desarrollo\log_docker_startup.txt
