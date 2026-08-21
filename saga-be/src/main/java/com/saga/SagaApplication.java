package com.saga;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.neo4j.Neo4jReactiveRepositoriesAutoConfiguration;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication(exclude = {
    UserDetailsServiceAutoConfiguration.class,
    Neo4jReactiveDataAutoConfiguration.class,
    Neo4jReactiveRepositoriesAutoConfiguration.class
})
@EnableAsync
public class SagaApplication {

	private static final Logger log = LoggerFactory.getLogger(SagaApplication.class);
	private final Environment env;

	public SagaApplication(Environment env) {
		this.env = env;
	}

	public static void main(String[] args) {
		SpringApplication.run(SagaApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void printSwaggerUrl() {
		String port = env.getProperty("server.port", "8080");
		log.info("\n----------------------------------------------------------\n\t" +
				"Application is running! Access Swagger UI at:\n\t" +
				"http://localhost:{}/swagger-ui.html\n" +
				"----------------------------------------------------------", port);
	}
}
