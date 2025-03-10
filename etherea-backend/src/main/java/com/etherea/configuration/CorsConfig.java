package com.etherea.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // Ajoute plusieurs origines autorisées
        config.addAllowedOrigin("http://localhost:4200");
        config.addAllowedOriginPattern("http://localhost:*");  // En cas de besoin pour toutes les origines localhost

        // Autoriser tous les en-têtes
        config.addAllowedHeader("*");

        // Autoriser toutes les méthodes HTTP
        config.addAllowedMethod("*");

        // Autoriser les cookies
        config.setAllowCredentials(true);

        // Enregistre la configuration pour toutes les routes
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
