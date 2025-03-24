package com.etherea.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Autoriser les origines spécifiques (Angular)
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOriginPattern("http://localhost:*");
        config.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://localhost:*"));


        // Autoriser tous les en-têtes et toutes les méthodes HTTP
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");

        // Autoriser les cookies et les informations d'identification
        config.setAllowCredentials(true);

        // Enregistrer la configuration pour toutes les routes
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}
