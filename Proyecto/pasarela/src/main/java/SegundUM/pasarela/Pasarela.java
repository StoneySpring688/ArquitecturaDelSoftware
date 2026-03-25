package SegundUM.pasarela;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

// Punto de entrada del API Gateway (pasarela).
// Utiliza Zuul para redirigir peticiones a los microservicios internos.
@SpringBootApplication
@EnableZuulProxy
public class Pasarela implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(Pasarela.class);

	public static void main(String[] args) {
		SpringApplication.run(Pasarela.class, args);
	}

	@Override
	public void run(String... args) {
		logger.info("=========================================");
		logger.info("=== API Gateway PASARELA iniciada ===");
		logger.info("Puerto: 8090");
		logger.info("=========================================");
	}
}
