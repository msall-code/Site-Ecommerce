package com.maket.maket_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class PersistenceConfig {
    // Configuration pour l'audit automatique (createdAt, updatedAt)
}