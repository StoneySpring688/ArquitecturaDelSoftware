$USUARIO = "stoneyspring688"

echo "--- Iniciando subida masiva a Docker Hub ---"

# Microservicio Productos
echo "Construyendo Productos..."
docker buildx build --platform linux/amd64,linux/arm64 -t "${USUARIO}/segundum-productos:latest" --push ./Productos

# Microservicio Usuarios
echo "Construyendo Usuarios..."
docker buildx build --platform linux/amd64,linux/arm64 -t "${USUARIO}/segundum-usuarios:latest" --push ./Usuarios

# Microservicio Compraventas
echo "Construyendo Compraventas..."
docker buildx build --platform linux/amd64,linux/arm64 -t "${USUARIO}/segundum-compraventas:latest" --push ./Compraventas

# Microservicio Pasarela
echo "Construyendo Pasarela..."
docker buildx build --platform linux/amd64,linux/arm64 -t "${USUARIO}/segundum-pasarela:latest" --push ./pasarela

# Infraestructura personalizada
echo "Construyendo MySQL preconfigurado..."
docker buildx build --platform linux/amd64,linux/arm64 -t "${USUARIO}/segundum-mysql:latest" --push ./mysql-init

echo "Construyendo RabbitMQ preconfigurado..."
docker buildx build --platform linux/amd64,linux/arm64 -f Dockerfile-rabbitmq -t "${USUARIO}/segundum-rabbitmq:latest" --push .

echo "--- ¡Todo en la nube! ---"