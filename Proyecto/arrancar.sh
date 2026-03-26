#!/bin/bash

# ============================================================
# Script para arrancar toda la aplicacion SegundUM
# ============================================================
# Orden de arranque:
#   1. rabbitmq-setup  (crea exchange y colas en CloudAMQP)
#   2. Productos       (Spring Boot, puerto 8080)
#   3. Usuarios        (JAX-RS/Grizzly, puerto 8081)
#   4. Compraventas    (Spring Boot, puerto 8082)
# ============================================================

PROYECTO_DIR="$(cd "$(dirname "$0")" && pwd)"

# Colores para la salida
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # Sin color

log_info()  { echo -e "${CYAN}[INFO]${NC}  $1"; }
log_ok()    { echo -e "${GREEN}[OK]${NC}    $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_warn()  { echo -e "${YELLOW}[WARN]${NC}  $1"; }

# Array para guardar los PIDs de los microservicios
PIDS=()

# Funcion para matar todos los procesos al salir
cleanup() {
    echo ""
    log_warn "Deteniendo todos los microservicios..."
    for pid in "${PIDS[@]}"; do
        if kill -0 "$pid" 2>/dev/null; then
            kill "$pid" 2>/dev/null
            log_info "Proceso $pid detenido"
        fi
    done
    log_ok "Todos los microservicios detenidos."
    exit 0
}

trap cleanup SIGINT SIGTERM

# Liberar puertos por si quedaron procesos anteriores
for port in 8080 8081 8082 8090; do
    fuser -k $port/tcp 2>/dev/null
done

# ============================================================
# 1. Setup RabbitMQ (exchange + colas)
# ============================================================
log_info "=== Paso 1: Configurando infraestructura RabbitMQ ==="

cd "$PROYECTO_DIR/rabbitmq-setup"
mvn -q compile exec:java 2>&1
if [ $? -ne 0 ]; then
    log_error "Fallo al configurar RabbitMQ. Abortando."
    exit 1
fi
log_ok "Infraestructura RabbitMQ lista."
echo ""

# ============================================================
# 2. Arrancar Productos (puerto 8080)
# ============================================================
log_info "=== Paso 2: Arrancando microservicio PRODUCTOS (puerto 8080) ==="

cd "$PROYECTO_DIR/Productos"
mvn -q spring-boot:run &
PIDS+=($!)
log_ok "Productos arrancando en segundo plano (PID: ${PIDS[-1]})"
echo ""

# Esperar un poco para que Productos arranque antes que los demas
sleep 5

# ============================================================
# 3. Arrancar Usuarios (puerto 8081)
# ============================================================
log_info "=== Paso 3: Arrancando microservicio USUARIOS (puerto 8081) ==="

cd "$PROYECTO_DIR/Usuarios"
mvn -q compile exec:java -Dexec.mainClass="SegundUM.Usuarios.rest.App" &
PIDS+=($!)
log_ok "Usuarios arrancando en segundo plano (PID: ${PIDS[-1]})"
echo ""

# Esperar un poco
sleep 5

# ============================================================
# 4. Arrancar Compraventas (puerto 8082)
# ============================================================
log_info "=== Paso 4: Arrancando microservicio COMPRAVENTAS (puerto 8082) ==="

cd "$PROYECTO_DIR/Compraventas"
mvn -q spring-boot:run &
PIDS+=($!)
log_ok "Compraventas arrancando en segundo plano (PID: ${PIDS[-1]})"
echo ""

# ============================================================
# 5. Arrancar Pasarela (puerto 8090)
# ============================================================
log_info "=== Paso 5: Arrancando microservicio PASARELA (puerto 8090) ==="

cd "$PROYECTO_DIR/pasarela"
mvn -q spring-boot:run &
PIDS+=($!)
log_ok "Pasarela arrancando en segundo plano (PID: ${PIDS[-1]})"
echo ""

# ============================================================
# Resumen
# ============================================================
log_ok "=========================================="
log_ok " Todos los microservicios arrancados"
log_ok "=========================================="
echo ""
log_info "Pasarela (Gateway): http://localhost:8090"
log_info "Productos:    http://localhost:8090/productos"
log_info "Usuarios:     http://localhost:8090/usuarios"
log_info "Compraventas: http://localhost:8090/compraventas"
echo ""
log_info "Presiona Ctrl+C para detener todos los microservicios"
echo ""

# Mantener el script vivo esperando a los procesos hijos
wait
