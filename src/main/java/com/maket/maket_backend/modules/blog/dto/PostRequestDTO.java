package com.maket.maket_backend.modules.blog.dto;

import lombok.Data;

@Data
public class PostRequestDTO {
    private String title;
    private String content;
    private String imageUrl;
    private Long categoryId; // Indispensable pour BlogService.java:28
}