package com.maket.maket_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(Customizer.withDefaults())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // On ouvre grand les portes pour l'interface utilisateur et les ressources de base
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/login.html",
                    "/subscribe.html",
                    "/favicon.ico",
                    "/Maket.png",
                    "/css/**",
                    "/js/**",
                    "/views/**",
                    "/api/auth/**",
                    "/products/catalog",
                    "/api/blog/categories", // 👈 AJOUTE CETTE LIGNE EXACTE
                    "/error" // Permet à Spring de renvoyer des vraies erreurs 404 au lieu de bloquer en 401
                ).permitAll()
                
                // On protège uniquement les routes API d'administration
                .requestMatchers("/api/admin/**").hasRole("admin")
                
                // Tout le reste demande une authentification Keycloak
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new KeycloakSecurityConfig());
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withIssuerLocation("http://localhost:7080/realms/maket-realm").build();
    }
}