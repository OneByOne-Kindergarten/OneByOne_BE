package com.onebyone.kindergarten.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/favicon.ico", "/static/**", "/admin/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(3600); // 1시간 캐싱
    }
} 