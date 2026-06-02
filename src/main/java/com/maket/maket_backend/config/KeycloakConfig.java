package com.maket.maket_backend.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

//import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

@Configuration
public class KeycloakConfig {

    @Value("${maket.keycloak.server-url}")
    private String serverUrl;

    @Value("${maket.keycloak.admin-cli.username}")
    private String username;

    @Value("${maket.keycloak.admin-cli.password}")
    private String password;

    /**
     * Cette configuration force Jackson à ignorer les champs comme "userProfileMetadata"
     */
    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * On crée un fournisseur personnalisé pour Resteasy (le client Keycloak)
     */
    @Provider
    public static class JacksonObjectMapperProvider implements ContextResolver<ObjectMapper> {
        private final ObjectMapper mapper;

        public JacksonObjectMapperProvider() {
            this.mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }

    @Bean
public Keycloak keycloak() {
    // On crée un client Resteasy qui ignore TOTALEMENT les erreurs de mapping JSON
    ResteasyClientBuilderImpl builder = new ResteasyClientBuilderImpl();
    
    // On force la configuration de Jackson avant même que Keycloak ne l'utilise
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    
    // On enregistre ce mapper spécifiquement pour le client
    builder.register(new ContextResolver<ObjectMapper>() {
        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    });

    return KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm("master")
            .clientId("admin-cli")
            .grantType("password")
            .username(username)
            .password(password)
            .resteasyClient(builder.build())
            .build();
}
}