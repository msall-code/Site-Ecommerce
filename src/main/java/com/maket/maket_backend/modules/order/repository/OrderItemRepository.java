package com.maket.maket_backend.modules.order.repository;

import com.maket.maket_backend.modules.order.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // Tu peux ajouter ici des méthodes de recherche spécifiques si besoin, 
    // par exemple pour lister les items d'un produit spécifique.
}