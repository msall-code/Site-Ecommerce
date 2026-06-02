package com.maket.maket_backend.modules.identity.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Import pour le logging
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import jakarta.ws.rs.core.Response; 
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j // Génère automatiquement l'objet 'log'
@Service
@RequiredArgsConstructor
public class KeycloakAdminService {

    private final Keycloak keycloak;

    @Value("${maket.keycloak.realm}")
    private String realm;

    public List<UserRepresentation> findAllUsers() {
        log.info("Récupération de la liste de tous les utilisateurs du realm: {}", realm);
        List<UserRepresentation> users = keycloak.realm(realm).users().list();
        for (UserRepresentation user : users) {
            List<String> roles = keycloak.realm(realm).users().get(user.getId())
                    .roles().realmLevel().listAll().stream()
                    .map(RoleRepresentation::getName)
                    .filter(r -> r.equals("admin") || r.equals("vendeur") || r.equals("client") || r.equals("livreur"))
                    .toList();
            
            user.setRealmRoles(roles);
        }
        return users;
    }

    public String createUser(String username, String email, String password, String role) {
        log.info("Tentative de création de l'utilisateur: {} avec le rôle: {}", username, role);
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setAttributes(new HashMap<>());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        user.setCredentials(Collections.singletonList(cred));

        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() == 201) {
                String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
                log.info("Utilisateur créé avec succès dans Keycloak. ID: {}", userId);
                this.updateUserRole(userId, role);
                return userId;
            } else {
                String errorDetail = response.readEntity(String.class);
                log.error("Échec de création utilisateur Keycloak. Statut: {}, Détails: {}", response.getStatus(), errorDetail);
                throw new ResponseStatusException(
                    HttpStatus.valueOf(response.getStatus()), 
                    "Erreur Keycloak : " + errorDetail
                );
            }
        }
    }

    public void setUserEnabled(String userId, boolean enabled) {
        log.info("Changement du statut 'enabled' pour l'utilisateur {} -> {}", userId, enabled);
        try {
            UserResource userResource = keycloak.realm(realm).users().get(userId);
            UserRepresentation user = userResource.toRepresentation();
            user.setEnabled(enabled);
            userResource.update(user);
            log.info("Statut mis à jour avec succès pour l'ID: {}", userId);
        } catch (Exception e) {
            log.error("Erreur lors de la modification du statut de l'utilisateur {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    public void updateUserRole(String userId, String roleName) {
        log.info("Mise à jour du rôle de l'utilisateur {} vers le rôle: {}", userId, roleName);
        var userResource = keycloak.realm(realm).users().get(userId);
        
        List<RoleRepresentation> existingRoles = userResource.roles().realmLevel().listAll();
        List<RoleRepresentation> rolesToRemove = existingRoles.stream()
                .filter(r -> r.getName().equals("admin") || r.getName().equals("vendeur") || r.getName().equals("client") || r.getName().equals("livreur"))
                .toList();
        
        if (!rolesToRemove.isEmpty()) {
            userResource.roles().realmLevel().remove(rolesToRemove);
            log.debug("Anciens rôles supprimés pour l'utilisateur {}", userId);
        }

        RoleRepresentation roleRep = keycloak.realm(realm).roles().get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(roleRep));
        log.info("Nouveau rôle '{}' attribué avec succès à {}", roleName, userId);
    }

    public void deleteUser(String userId) {
        log.warn("Tentative de suppression de l'utilisateur ID: {}", userId);
        try (Response response = keycloak.realm(realm).users().delete(userId)) {
            if (response.getStatus() != 204 && response.getStatus() != 200) {
                log.error("Erreur lors de la suppression. Statut Keycloak: {}", response.getStatus());
                throw new ResponseStatusException(
                    HttpStatus.valueOf(response.getStatus()), 
                    "Impossible de supprimer l'utilisateur dans Keycloak"
                );
            }
            log.info("Utilisateur {} supprimé avec succès", userId);
        }
    }
}