package com.maket.maket_backend.modules.identity.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String keycloakId; // Le "sub" (subject) envoyé par Keycloak

    private String email;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;
}