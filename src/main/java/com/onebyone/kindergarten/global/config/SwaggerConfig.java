package com.onebyone.kindergarten.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

  @Value("${swagger.description}")
  private String description;

  @Value("${swagger.server-url}")
  private String serverUrl;

  @Bean
  public OpenAPI openAPI() {
    return new OpenAPI()
        .info(new Info().title("원바원 API").version("v1").description(description))
        .addServersItem(new Server().url(serverUrl))
        .addSecurityItem(new SecurityRequirement().addList("JWT Authentication"))
        .components(
            new Components()
                .addSecuritySchemes(
                    "JWT Authentication",
                    new SecurityScheme()
                        .name("JWT Authentication")
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .in(SecurityScheme.In.HEADER)));
  }
}
