package com.maket.maket_backend.modules.order.model;

public enum OrderStatus {
    PENDING,   // En attente de paiement
    PAID,      // Payé, prêt à être préparé par le vendeur
    SHIPPED,   // En cours de livraison (pris par un Rider)
    DELIVERED, // Livré (après le double scan)
    CANCELLED  // Annulé
}