package com.maket.maket_backend.modules.delivery.model;

public enum DeliveryStatus {
    WAITING_FOR_RIDER, // La commande est payée, on cherche un livreur
    IN_TRANSIT,        // Le livreur a scanné chez le vendeur
    DELIVERED          // Le livreur a scanné chez le client
}