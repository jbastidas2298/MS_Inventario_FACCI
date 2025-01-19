# MS_Inventario_FACCI
Microservicios de gestion de inventario

--carpetas necesaria
c:archivos


--Crear certificados Firmados
$Cert = Get-ChildItem -Path Cert:\LocalMachine\My | Where-Object { $_.DnsNameList -like "*facci-inventario" }
Export-PfxCertificate -Cert $Cert -FilePath "C:\Users\jbastidas\Documents\facci-inventario\certificado.pfx" -Password (ConvertTo-SecureString -String "realhulk97" -Force -AsPlainText)

New-SelfSignedCertificate -DnsName "facci-inventario" -CertStoreLocation "Cert:\LocalMachine\My" -NotAfter (Get-Date).AddYears(1)

openssl pkcs12 -in C:\Users\jbastidas\Documents\facci-inventario\certificado.pfx -out C:\Users\jbastidas\Documents\facci-inventario\certificado.crt -nokeys
openssl pkcs12 -in C:\Users\jbastidas\Documents\facci-inventario\certificado.pfx -out C:\Users\jbastidas\Documents\facci-inventario\certificado.key -nodes -nocerts

openssl pkcs12 -export -in C:\Users\jbastidas\Documents\facci-inventario\certificado.crt -inkey C:\Users\jbastidas\Documents\facci-inventario\certificado.key -out C:\Users\jbastidas\Documents\facci-inventario\certificado.p12 -name "facci-inventario"



-- Crear .bat para levantar docker y agregarlo en la ruta 
C:\Users\Joel B\AppData\Roaming\Microsoft\Windows\Start Menu\Programs\Startup, 

@echo off
REM Verificar si Docker Desktop ya está en ejecución
tasklist | findstr /i "Docker Desktop.exe" >nul
if %errorlevel% equ 0 (
    echo Docker Desktop ya está en ejecución.
) else (
    echo Iniciando Docker Desktop...
    start "" "C:\Program Files\Docker\Docker\Docker Desktop.exe"

    REM Esperar 60 segundos para permitir que Docker Desktop se inicie
    echo Esperando 60 segundos para que Docker Desktop esté listo...
    ping -n 61 127.0.0.1 >nul
)

REM Verificar si Docker está listo
echo Verificando que Docker esté completamente operativo...
:esperarDocker
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Docker no está listo aún. Esperando 10 segundos más...
    ping -n 11 127.0.0.1 >nul
    goto esperarDocker
)

REM Cambiar a la carpeta del proyecto
cd C:\desarrollo

REM Levantar los contenedores
echo Levantando los contenedores con Docker Compose...
docker compose up --build -d >nul 2>&1
