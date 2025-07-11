package com.onebyone.kindergarten.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@OpenAPIDefinition(info = @Info(title = "원바원 API", description = "개발 환경 원바원 API 명세서입니다.", version = "v1"), security = {
        @SecurityRequirement(name = "JWT Authentication")
}, servers = {
        @Server(url = "http://43.201.114.78:8080")
})
@SecurityScheme(name = "JWT Authentication", type = SecuritySchemeType.HTTP, description = "JWT 인증을 위한 헤더. Bearer Authentication", paramName = "Authorization", in = SecuritySchemeIn.HEADER, scheme = "bearer", bearerFormat = "JWT")
@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevSwaggerConfig {

}