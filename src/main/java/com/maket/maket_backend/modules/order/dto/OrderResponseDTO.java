package com.maket.maket_backend.modules.order.dto;

import java.time.LocalDateTime;

import com.maket.maket_backend.modules.order.model.OrderStatus;

import lombok.Data;

@Data
public class OrderResponseDTO {
    private Long id;
    private Double totalAmount;
    private Double deliveryFee; // <--- AJOUTE CETTE LIGNE
    private OrderStatus status;
    private LocalDateTime createdAt;
}