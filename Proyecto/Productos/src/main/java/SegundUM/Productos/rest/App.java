package SegundUM.Productos.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${api.url.swaggerui}")
	private String BASE_URI;

	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("=========================================");
		logger.info("=== Microservicio PRODUCTOS iniciado ===");
		logger.info("Go to {} for api documentation", BASE_URI);
		logger.info("=========================================\n");
	}
}
