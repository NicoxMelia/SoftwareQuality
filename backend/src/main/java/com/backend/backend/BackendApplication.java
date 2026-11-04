package com.backend.backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class BackendApplication {

    @Value("${CORS_ALLOWED_ORIGINS:http://localhost:5173,http://terrasense.local}")
    private String allowedOrigins;
    
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.configure().directory("src/main/resources").ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(BackendApplication.class, args);
        System.err.println("Backend iniciado!");
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins(allowedOrigins.split(","))
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("")
                        .allowCredentials(true);
            }
        };

    }
}
