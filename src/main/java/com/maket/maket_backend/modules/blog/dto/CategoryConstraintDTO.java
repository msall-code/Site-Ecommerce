package com.maket.maket_backend.modules.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryConstraintDTO {
    private Long id;
    private String name;
    private String controlType; // NUMBER, TEXT, DROPDOWN, BOOLEAN
    private String possibleValues; // "S,M,L" ou "Homme,Femme"
    private String unit; // "kg", "cm"
    private boolean required;
    private Long categoryId;
}