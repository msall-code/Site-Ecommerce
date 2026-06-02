package com.maket.maket_backend.modules.delivery.dto;

import lombok.Data;

@Data
public class ScanValidationDTO {
    private String scannedCode; // Le contenu texte du QR Code
    private Long deliveryId;
}