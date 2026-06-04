package com.maket.maket_backend.modules.identity.service;

import java.util.Collections;
import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value; // Import ajouté
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
 
import com.maket.maket_backend.modules.identity.dto.UserRegistrationDTO;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${maket.keycloak.realm}")
    private String realm;

    public List<UserRepresentation> findAllUsers() {
        log.info("Récupération de tous les utilisateurs du realm: {}", realm);
        List<UserRepresentation> users = keycloak.realm(realm).users().list();
        for (UserRepresentation user : users) {
            List<String> roles = keycloak.realm(realm).users().get(user.getId())
                    .roles().realmLevel().listAll().stream()
                    .map(RoleRepresentation::getName)
                    .filter(r -> List.of("admin", "vendeur", "client", "livreur").contains(r.toLowerCase()))
                    .toList();
            
            user.setRealmRoles(roles);
        }
        return users;
    }

    /**
     * Crée un utilisateur complet avec Nom/Prénom et assigne le rôle.
     */
    public String createUser(UserRegistrationDTO dto) {
        log.info("Création de l'utilisateur: {} avec le rôle: {}", dto.getUsername(), dto.getRole());
        
        UserRepresentation user = new UserRepresentation();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName()); // Fixé : Envoi du prénom
        user.setLastName(dto.getLastName());   // Fixé : Envoi du nom
        user.setEnabled(true);
        user.setEmailVerified(true);
        
        // Empêche Keycloak de demander des actions supplémentaires au login
        user.setRequiredActions(Collections.emptyList());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(dto.getPassword());
        cred.setTemporary(false);
        user.setCredentials(Collections.singletonList(cred));

        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                
                // On normalise le rôle en minuscules
                String roleName = (dto.getRole() != null) ? dto.getRole().toLowerCase() : "client";
                this.updateUserRole(userId, roleName); 
                
                return userId;
            } else {
                String errorDetail = response.readEntity(String.class);
                log.error("Erreur création Keycloak : {}", errorDetail);
                throw new ResponseStatusException(HttpStatus.valueOf(response.getStatus()), errorDetail);
            }
        }
    }

    public void setUserEnabled(String userId, boolean enabled) {
        log.info("Changement statut utilisateur {} -> enabled: {}", userId, enabled);
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);
        } catch (Exception e) {
            log.error("Erreur lors de la modification du statut: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erreur mise à jour statut");
        }
    }

    public void updateUserRole(String userId, String roleName) {
        String normalizedRole = roleName.toLowerCase();
        var userResource = keycloak.realm(realm).users().get(userId);
        
        try {
            RoleRepresentation roleRep = keycloak.realm(realm).roles().get(normalizedRole).toRepresentation();
            
            List<RoleRepresentation> existingRoles = userResource.roles().realmLevel().listAll();
            List<RoleRepresentation> rolesToRemove = existingRoles.stream()
                    .filter(r -> List.of("admin", "vendeur", "client", "livreur").contains(r.getName().toLowerCase()))
                    .toList();
            
            if (!rolesToRemove.isEmpty()) {
                userResource.roles().realmLevel().remove(rolesToRemove);
            }

            userResource.roles().realmLevel().add(Collections.singletonList(roleRep));
            log.info("Rôle '{}' assigné à {}", normalizedRole, userId);
        } catch (NotFoundException e) {
            log.error("Le rôle {} n'existe pas dans Keycloak", normalizedRole);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Rôle non trouvé: " + normalizedRole);
        }
    }

    public void deleteUser(String userId) {
        log.warn("Suppression de l'utilisateur ID: {}", userId);
        try (Response response = keycloak.realm(realm).users().delete(userId)) {
            if (response.getStatus() >= 400) {
                throw new ResponseStatusException(HttpStatus.valueOf(response.getStatus()), "Erreur suppression");
            }
        }
    }
}