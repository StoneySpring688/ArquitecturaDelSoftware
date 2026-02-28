package SegundUM.Productos.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Punto de entrada del microservicio de Productos.
 *
 * Inicia Spring Boot y muestra información de los endpoints disponibles.
 */
@SpringBootApplication(scanBasePackages = "SegundUM.Productos")
@EnableJpaRepositories(basePackages = "SegundUM.Productos.repositorio")
@EntityScan(basePackages = "SegundUM.Productos.dominio")
public class App implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(App.class);
	private static final String BASE_URI = "http://localhost:8080/api/";

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("=========================================");
		logger.info("=== Microservicio PRODUCTOS iniciado ===");
		logger.info("Base URI: {}", BASE_URI);
		logger.info("");
		logger.info("Endpoints de Productos:");
		logger.info("  GET    {}productos/{{id}}                  - Obtener producto por ID", BASE_URI);
		logger.info("  POST   {}productos                        - Alta de producto", BASE_URI);
		logger.info("  PUT    {}productos/{{id}}                  - Modificar producto", BASE_URI);
		logger.info("  DELETE {}productos/{{id}}                  - Eliminar producto", BASE_URI);
		logger.info("  PUT    {}productos/{{id}}/recogida         - Asignar lugar de recogida", BASE_URI);
		logger.info("  PUT    {}productos/{{id}}/visualizaciones  - Registrar visualización", BASE_URI);
		logger.info("  GET    {}productos/buscar                  - Buscar con filtros", BASE_URI);
		logger.info("  GET    {}productos/vendedor/{{vendedorId}} - Productos de un vendedor", BASE_URI);
		logger.info("  GET    {}productos/historial               - Resumen mensual", BASE_URI);
		logger.info("  GET    {}productos/historial/{{email}}     - Resumen mensual por vendedor", BASE_URI);
		logger.info("");
		logger.info("Endpoints de Categorías:");
		logger.info("  GET    {}categorias/{{id}}                 - Obtener categoría por ID", BASE_URI);
		logger.info("  GET    {}categorias/                       - Listar todas las categorías", BASE_URI);
		logger.info("=========================================");
	}
}
