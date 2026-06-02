package com.maket.maket_backend.modules.shop.dto;

import lombok.Data;

@Data
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String category;
    private String storeName;
    private String qrCodeData;
    private String imageUrl;

    // Champs de taxonomie pour l'affichage client
    private String gender;
    private Integer shoeSize;
    private String sizeText;
    private Double weight;
}