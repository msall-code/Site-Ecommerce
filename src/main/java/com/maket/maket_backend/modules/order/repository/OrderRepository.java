package com.maket.maket_backend.modules.order.repository;

import com.maket.maket_backend.modules.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByClientId(String clientId);
}