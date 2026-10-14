package com.alvraro.papeos_backend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PapeosBackendApplication {

	private static final Logger logger = LoggerFactory.getLogger(PapeosBackendApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(PapeosBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner demo(AlimentoRepository repository) {
		return (args) -> {
			// Save
			repository.save(new Alimento(1, "Manzana"));
			repository.save(new Alimento(2, "Banana"));
			repository.save(new Alimento(3, "Cereza"));

			logger.info("Alimentos findAll():");
			logger.info("-------------------------------");
			for (Alimento alimento : repository.findAll()) {
				logger.info(alimento.toString());
			}
			logger.info("-------------------------------");

			Alimento alimento = repository.findById(1).orElse(null);
			logger.info("Alimento findById(1):");
			logger.info("--------------------------------");
			logger.info(alimento.toString());
			logger.info("-------------------------------");

			logger.info("Alimentos findByNombreContainingIgnoreCase('a'):");
			logger.info("------------------------------------------------------------");
			for (Alimento a : repository.findByNombreContainingIgnoreCase("e")) {
				logger.info(a.toString());
			}
		};
	}
}
