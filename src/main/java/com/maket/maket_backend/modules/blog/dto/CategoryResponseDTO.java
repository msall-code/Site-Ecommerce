package com.maket.maket_backend.modules.blog.dto;

import lombok.Data;

@Data
public class CategoryResponseDTO {
    private Long id;
    private String name;
    private Boolean requiresGender;
    private Boolean requiresSizeText;
    private Boolean requiresShoeSize;
    private Boolean requiresPrescription;
    private Boolean foodDelivery;    // Changé : isFoodDelivery -> foodDelivery
    private Boolean requiresColdChain;
    private Boolean ageRestricted;   // Changé : isAgeRestricted -> ageRestricted
    private Boolean soldByWeight;    // Changé : isSoldByWeight -> soldByWeight
}