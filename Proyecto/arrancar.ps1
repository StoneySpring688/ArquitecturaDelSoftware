# ============================================================
# Script para arrancar toda la aplicacion SegundUM (Windows)
# ============================================================

function Stop-PortProcess {
    param([int]$port)
    $process = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue | Select-Object -ExpandProperty OwningProcess -Unique
    if ($process) {
        Write-Host "[INFO] Deteniendo proceso en puerto $port (PID: $process)..." -ForegroundColor Yellow
        Stop-Process -Id $process -Force
    }
}

$PROYECTO_DIR = Get-Location

Write-Host "`n=== Paso 0: Limpiando puertos ===" -ForegroundColor Cyan
8080, 8081, 8082, 8090 | ForEach-Object { Stop-PortProcess $_ }

# 1. Setup RabbitMQ
Write-Host "`n=== Paso 1: Configurando infraestructura RabbitMQ ===" -ForegroundColor Cyan
Set-Location "$PROYECTO_DIR\rabbitmq-setup"
mvn -q compile exec:java
if ($LASTEXITCODE -ne 0) { 
    Write-Host "[ERROR] Fallo al configurar RabbitMQ." -ForegroundColor Red
    exit 1 
}
Write-Host "[OK] Infraestructura RabbitMQ lista." -ForegroundColor Green

# 2. Productos
Write-Host "`n=== Paso 2: Arrancando PRODUCTOS (puerto 8080) ===" -ForegroundColor Cyan
Set-Location "$PROYECTO_DIR\Productos"
Start-Process cmd -ArgumentList "/c mvn spring-boot:run & pause" 
Start-Sleep -Seconds 5

# 3. Usuarios
Write-Host "`n=== Paso 3: Arrancando USUARIOS (puerto 8081) ===" -ForegroundColor Cyan
Set-Location "$PROYECTO_DIR\Usuarios"
# Quitamos las comillas simples del nombre de la clase
Start-Process cmd -ArgumentList "/c mvn compile exec:java -Dexec.mainClass=SegundUM.Usuarios.rest.App & pause" 
Start-Sleep -Seconds 5

# 4. Compraventas
Write-Host "`n=== Paso 4: Arrancando COMPRAVENTAS (puerto 8082) ===" -ForegroundColor Cyan
Set-Location "$PROYECTO_DIR\Compraventas"
Start-Process cmd -ArgumentList "/c mvn spring-boot:run & pause" 
Start-Sleep -Seconds 2

# 5. Pasarela
Write-Host "`n=== Paso 5: Arrancando PASARELA (puerto 8090) ===" -ForegroundColor Cyan
Set-Location "$PROYECTO_DIR\pasarela"
Start-Process cmd -ArgumentList "/c mvn spring-boot:run & pause" 

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host " Todos los microservicios han sido lanzados" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green
Write-Host "Las ventanas de cada microservicio permanecerán abiertas incluso si hay errores."
Write-Host "Revisa las ventanas para confirmar que todos digan 'Started' o similar."

Set-Location $PROYECTO_DIR
