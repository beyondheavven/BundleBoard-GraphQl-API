package com.source.bundleboard.config;

import com.source.bundleboard.config.properties.AppProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;


@Configuration
@RequiredArgsConstructor
public class CorsConfig {

    private final AppProperties appProperties;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(appProperties.getAllowedOrigins());

        configuration.setAllowedMethods(appProperties.getAllowedMethods());

        configuration.setAllowedHeaders(appProperties.getAllowedHeaders());

        configuration.setExposedHeaders(appProperties.getExposedHeaders());

        configuration.setAllowCredentials(appProperties.isAllowCredentials());

        configuration.setMaxAge(appProperties.getMaxAge());

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
