package SegundUM.Compraventas.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Punto de entrada del microservicio de Productos.
 *
 * Inicia Spring Boot y muestra información de los endpoints disponibles.
 */
@SpringBootApplication(scanBasePackages = "SegundUM.Compraventas")
@EnableMongoRepositories(basePackages = "SegundUM.Compraventas.repositorio")
@EntityScan(basePackages = "SegundUM.Compraventas.dominio")
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
		logger.info("=== Microservicio Compraventas iniciado ===");
		logger.info("Go to {} for api documentation", BASE_URI);
		logger.info("=========================================\n");
	}
}
