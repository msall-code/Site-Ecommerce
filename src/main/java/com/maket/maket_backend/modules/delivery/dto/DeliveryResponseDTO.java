package com.maket.maket_backend.modules.delivery.dto;

import com.maket.maket_backend.modules.delivery.model.DeliveryStatus;
import lombok.Data;

@Data
public class DeliveryResponseDTO {
    private Long id;
    private Long orderId;
    private String riderId;
    private DeliveryStatus status;
    private boolean isPickedUp;
    private boolean isDelivered;
    // On ne renvoie pas les codes secrets ici par sécurité ! 
    // Ils seront validés via le scan.
}