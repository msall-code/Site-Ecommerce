package com.maket.maket_backend.modules.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class OrderRequestDTO {
    @NotNull(message = "La liste des produits est obligatoire")
    private List<OrderItemRequestDTO> items; // Ajout de la liste

    @NotNull(message = "La latitude de livraison est requise")
    private Double deliveryLatitude;

    @NotNull(message = "La longitude de livraison est requise")
    private Double deliveryLongitude;

    private String deliveryAddress;
}