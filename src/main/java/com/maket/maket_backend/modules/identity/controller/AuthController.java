package com.maket.maket_backend.modules.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maket.maket_backend.modules.identity.dto.UserRegistrationDTO;
import com.maket.maket_backend.modules.identity.service.KeycloakAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Plus flexible pour le développement
@Slf4j 
public class AuthController {

    private final KeycloakAdminService keycloakAdminService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody UserRegistrationDTO dto) {
        log.info("Tentative d'inscription pour l'utilisateur : {}", dto.getUsername());

        try {
            // Appel du service avec le DTO complet (gère maintenant nom/prénom/rôle)
            String userId = keycloakAdminService.createUser(dto);

            return ResponseEntity.ok("Utilisateur créé avec succès. ID : " + userId);
            
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription de {}", dto.getUsername(), e);
            // On renvoie le message d'erreur précis (ex: "User exists")
            String message = (e instanceof org.springframework.web.server.ResponseStatusException) 
                             ? ((org.springframework.web.server.ResponseStatusException) e).getReason() 
                             : e.getMessage();
            return ResponseEntity.status(500).body("Erreur lors de l'inscription : " + message);
        }
    }
}