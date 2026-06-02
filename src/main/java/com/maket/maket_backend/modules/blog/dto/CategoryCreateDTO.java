package com.maket.maket_backend.modules.blog.dto;

import lombok.Data;

@Data
public class CategoryCreateDTO {
    private String name;
    private Long parentId; // INDISPENSABLE pour les sous-catégories
    
    private boolean requiresGender;
    private boolean requiresSizeText;
    private boolean requiresShoeSize;
    private boolean requiresPrescription;
    private boolean isFoodDelivery;
    private boolean requiresColdChain;
    private boolean isAgeRestricted;
    private boolean isSoldByWeight;
}