package com.maket.maket_backend.modules.delivery.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "deliveries")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String riderId; // ID Keycloak du livreur (RIDER)

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    // Codes de sécurité uniques générés à la création
    private String pickupCode;  // Le QR que le vendeur montre au livreur
    private String deliveryCode; // Le QR que le client montre au livreur

    private boolean isPickedUp = false;
    private boolean isDelivered = false;
}