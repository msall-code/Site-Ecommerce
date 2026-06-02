package com.maket.maket_backend.modules.delivery.repository;

import com.maket.maket_backend.modules.delivery.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Delivery findByOrderId(Long orderId);
}