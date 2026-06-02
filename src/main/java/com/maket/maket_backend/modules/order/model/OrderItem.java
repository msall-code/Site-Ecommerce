package com.maket.maket_backend.modules.order.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_items")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId; // Référence au produit du module Shop
    private Integer quantity;
    private Double priceAtPurchase; // On stocke le prix au moment de l'achat (si le prix change plus tard en boutique)
}