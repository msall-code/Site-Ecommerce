package com.maket.maket_backend.modules.identity.dto;

import lombok.Data;

@Data
public class UserLoginDTO {
    // Dans une architecture Keycloak, on l'utilise souvent pour 
    // transporter le token reçu du front afin de l'échanger ou le valider.
    private String accessToken;
}