package com.maket.maket_backend.modules.identity.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String keycloakId;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
}