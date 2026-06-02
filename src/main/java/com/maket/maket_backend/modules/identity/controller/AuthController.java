package com.maket.maket_backend.modules.identity.controller;

import com.maket.maket_backend.modules.identity.dto.UserRegistrationDTO; // Import correct
import com.maket.maket_backend.modules.identity.service.KeycloakAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")public class AuthController {

    private final KeycloakAdminService keycloakAdminService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody UserRegistrationDTO dto) {
        // On utilise dto.getRole().name() si c'est un Enum, ou juste getRole() si c'est un String
        String userId = keycloakAdminService.createUser(
            dto.getUsername(), 
            dto.getEmail(), 
            "mot_de_passe_temporaire", // À adapter selon ton service
            dto.getRole().toString()
        );

        return ResponseEntity.ok("Utilisateur créé avec succès. ID : " + userId);
    }
}
// NE RIEN METTRE ICI (Pas de classe RegistrationDTO)