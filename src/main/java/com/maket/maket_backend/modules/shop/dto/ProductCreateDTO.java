package com.maket.maket_backend.modules.shop.dto;

import lombok.Data;

@Data
public class ProductCreateDTO {
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private Long categoryId;
    
    // Champs optionnels basés sur la taxonomie
    private String gender;        // Pour requiresGender
    private String sizeText;      // Pour requiresSizeText
    private Integer shoeSize;     // Pour requiresShoeSize
    private String prescriptionUrl; // Pour requiresPrescription
    private Double weight;        // Pour isSoldByWeight
}