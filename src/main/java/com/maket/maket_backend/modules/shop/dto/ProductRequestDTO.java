package com.maket.maket_backend.modules.shop.dto;

import lombok.Data;

@Data
public class ProductRequestDTO {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private Long categoryId;
    private Long subcategoryId; // <--- AJOUTE CETTE LIGNE
    private Long sellerId; 

    // Champs de taxonomie
    private String gender;
    private String sizeText;
    private Integer shoeSize;
    private String prescriptionUrl;
    private Double weight;

    // GPS
    private Double latitude;
    private Double longitude;
}