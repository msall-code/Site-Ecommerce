package com.maket.maket_backend.modules.identity.controller;

import java.util.List;
import java.util.Map;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maket.maket_backend.modules.identity.dto.RoleDTO;
import com.maket.maket_backend.modules.identity.dto.StatusDTO;
import com.maket.maket_backend.modules.identity.service.KeycloakAdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8099")
@PreAuthorize("hasRole('admin')") 
public class AdminController {

    private final KeycloakAdminService keycloakAdminService;
    private static final String MESSAGE_KEY = "message";

    @GetMapping("/users")
    public ResponseEntity<List<UserRepresentation>> getAllUsers() {
        return ResponseEntity.ok(keycloakAdminService.findAllUsers());
    }

    @PostMapping("/users/{id}/role")
    public ResponseEntity<Map<String, String>> changeRole(@PathVariable String id, @RequestBody RoleDTO roleDto) {
        keycloakAdminService.updateUserRole(id, roleDto.getRole());
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Rôle mis à jour"));
    }

    @PostMapping("/users/{id}/status")
    public ResponseEntity<Map<String, String>> toggleStatus(@PathVariable String id, @RequestBody StatusDTO statusDto) {
        keycloakAdminService.setUserEnabled(id, statusDto.isEnabled());
        return ResponseEntity.ok(Map.of(MESSAGE_KEY, "Statut mis à jour"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        keycloakAdminService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}