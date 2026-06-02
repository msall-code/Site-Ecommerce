package com.maket.maket_backend.modules.shop.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "sellers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String storeName;

    private String description;
    private String address;
    
    @Column(unique = true)
    private String keycloakId; // Lien avec le module identity
}