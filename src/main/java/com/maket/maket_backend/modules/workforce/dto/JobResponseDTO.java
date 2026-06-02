package com.maket.maket_backend.modules.workforce.dto;

import com.maket.maket_backend.modules.blog.model.Category;
import lombok.Data;

@Data
public class JobResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Double salary;
    private Category category;
}