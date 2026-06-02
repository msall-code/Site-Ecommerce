package com.maket.maket_backend.modules.blog.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private String category; // String pour recevoir le nom de la catégorie
    private LocalDateTime createdAt;
}