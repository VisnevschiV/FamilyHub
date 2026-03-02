package com.visnevschi.familyhub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(
                        "http://localhost:3000",  // React default
                        "http://localhost:5173",  // Vite default
                "http://localhost:4200",  // Angular default
                "https://victorious-rock-0dc461310.6.azurestaticapps.net",
                "https://familyhub-gte6cabtbg.net"
                )
            .allowedOriginPatterns("https://*.azurestaticapps.net")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}