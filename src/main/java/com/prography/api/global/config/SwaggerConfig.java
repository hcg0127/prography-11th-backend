package com.prography.api.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		Info info = new Info()
			.title("Prography 11th backend assignmentAPI")
			.description("Custom OpenAPI")
			.version("1.0.0");

		return new OpenAPI()
			.addServersItem(new Server().url("/"))
			.info(info);
	}
}
