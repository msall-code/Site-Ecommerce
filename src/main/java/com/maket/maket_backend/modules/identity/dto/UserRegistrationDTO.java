package com.maket.maket_backend.modules.identity.dto;

import lombok.Data;

@Data
public class UserRegistrationDTO {
    private String keycloakId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private String role;
    
    // Ajoute ces champs pour correspondre à ton JSON JavaScript
    private String storeName;
    private String address;
    private String vehicle;
}
