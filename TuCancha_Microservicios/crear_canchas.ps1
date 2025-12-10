# ============================================================
# SCRIPT PARA CREAR 4 CANCHAS DE FÚTBOL VÍA API
# Ejecutar en PowerShell después de iniciar el microservicio de Canchas
# ============================================================

$baseUrl = "http://localhost:8081/api/canchas"

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Creando 4 canchas de fútbol..." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan
Write-Host ""

# Verificar que el microservicio esté corriendo
try {
    $response = Invoke-WebRequest -Uri "$baseUrl" -Method GET -UseBasicParsing -TimeoutSec 5
    Write-Host "[OK] Microservicio de Canchas está corriendo" -ForegroundColor Green
} catch {
    Write-Host "[ERROR] El microservicio de Canchas NO está corriendo en el puerto 8081" -ForegroundColor Red
    Write-Host "Por favor inicia el microservicio primero:" -ForegroundColor Yellow
    Write-Host "  cd Canchas" -ForegroundColor Yellow
    Write-Host "  mvnw spring-boot:run" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Cancha 1
Write-Host "Creando Cancha Fútbol 1..." -ForegroundColor Yellow
$cancha1 = @{
    nombre = "Cancha Fútbol 1"
    descripcion = "Cancha de fútbol 11 con césped sintético de última generación. Iluminación LED para partidos nocturnos. Incluye vestuarios y duchas."
    tipo = "Fútbol"
    precioPorHora = 45000.00
    direccion = "Av. Principal 1234"
    ciudad = "Santiago"
    activa = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $cancha1 -ContentType "application/json"
    Write-Host "  [OK] Cancha Fútbol 1 creada (ID: $($response.id))" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] No se pudo crear Cancha Fútbol 1: $($_.Exception.Message)" -ForegroundColor Red
}

# Cancha 2
Write-Host "Creando Cancha Fútbol 2..." -ForegroundColor Yellow
$cancha2 = @{
    nombre = "Cancha Fútbol 2"
    descripcion = "Cancha de fútbol 7 ideal para partidos rápidos. Superficie de césped natural. Perfecta para grupos pequeños."
    tipo = "Fútbol"
    precioPorHora = 35000.00
    direccion = "Calle Los Olivos 567"
    ciudad = "Santiago"
    activa = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $cancha2 -ContentType "application/json"
    Write-Host "  [OK] Cancha Fútbol 2 creada (ID: $($response.id))" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] No se pudo crear Cancha Fútbol 2: $($_.Exception.Message)" -ForegroundColor Red
}

# Cancha 3
Write-Host "Creando Cancha Fútbol 3..." -ForegroundColor Yellow
$cancha3 = @{
    nombre = "Cancha Fútbol 3"
    descripcion = "Cancha de fútbol 11 profesional con graderías. Ideal para torneos y eventos deportivos. Incluye sistema de sonido."
    tipo = "Fútbol"
    precioPorHora = 55000.00
    direccion = "Av. Deportiva 890"
    ciudad = "Providencia"
    activa = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $cancha3 -ContentType "application/json"
    Write-Host "  [OK] Cancha Fútbol 3 creada (ID: $($response.id))" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] No se pudo crear Cancha Fútbol 3: $($_.Exception.Message)" -ForegroundColor Red
}

# Cancha 4
Write-Host "Creando Cancha Fútbol 4..." -ForegroundColor Yellow
$cancha4 = @{
    nombre = "Cancha Fútbol 4"
    descripcion = "Cancha de fútbol 5 techada. Perfecta para jugar en cualquier clima. Incluye iluminación y vestuarios modernos."
    tipo = "Fútbol"
    precioPorHora = 40000.00
    direccion = "Calle Deportes 321"
    ciudad = "Las Condes"
    activa = $true
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $baseUrl -Method POST -Body $cancha4 -ContentType "application/json"
    Write-Host "  [OK] Cancha Fútbol 4 creada (ID: $($response.id))" -ForegroundColor Green
} catch {
    Write-Host "  [ERROR] No se pudo crear Cancha Fútbol 4: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "================================================" -ForegroundColor Cyan
Write-Host "Verificando canchas creadas..." -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

try {
    $canchas = Invoke-RestMethod -Uri "$baseUrl/activas" -Method GET
    Write-Host "Total de canchas activas: $($canchas.Count)" -ForegroundColor Green
    foreach ($cancha in $canchas) {
        Write-Host "  - $($cancha.nombre) (ID: $($cancha.id)) - $($cancha.tipo) - $($cancha.precioPorHora) CLP/hora" -ForegroundColor White
    }
} catch {
    Write-Host "[ERROR] No se pudieron obtener las canchas: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "¡Listo! Las canchas están creadas." -ForegroundColor Green
Write-Host "Puedes verificar en: http://localhost:8081/api/canchas" -ForegroundColor Cyan
