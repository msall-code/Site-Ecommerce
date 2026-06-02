package com.maket.maket_backend.modules.identity.dto;

import lombok.Data;
import com.maket.maket_backend.modules.identity.model.Role; // Assure-toi d'importer ton Enum Role

@Data
public class UserRegistrationDTO {
    private String keycloakId; // <--- AJOUTE CECI
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
}