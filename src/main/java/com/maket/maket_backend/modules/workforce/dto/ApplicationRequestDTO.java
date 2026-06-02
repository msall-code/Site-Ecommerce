package com.maket.maket_backend.modules.workforce.dto;

import lombok.Data;

@Data
public class ApplicationRequestDTO {
    private Long jobOfferId;
    private String cvUrl;
    private String message;
}