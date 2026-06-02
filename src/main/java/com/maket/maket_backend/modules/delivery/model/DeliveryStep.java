package com.maket.maket_backend.modules.delivery.model;

public enum DeliveryStep {
    WAITING_FOR_PICKUP, // Le livreur doit scanner chez le vendeur
    WAITING_FOR_CLIENT, // Le livreur doit scanner chez le client
    COMPLETED           // Tout est fini
}