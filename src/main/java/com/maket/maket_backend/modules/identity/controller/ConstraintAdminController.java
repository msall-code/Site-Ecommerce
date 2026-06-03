package com.maket.maket_backend.modules.identity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Constraint;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/constraints") // Route appelée par ton JS
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8099")
@PreAuthorize("hasRole('admin')")
public class ConstraintAdminController {
    private final ConstraintService constraintService;

    // IMPORTANT : C'est ce GET qui manque et cause l'erreur 405
    @GetMapping
    public ResponseEntity<List<Constraint>> getAllConstraints() {
        // Appelle ton service qui fait un findAll() sur tes modèles de contraintes
        return ResponseEntity.ok(constraintService.findAll());
    }

    @PostMapping
    public ResponseEntity<Constraint> createConstraint(@RequestBody ConstraintDTO dto) {
        return ResponseEntity.ok(constraintService.save(dto));
    }
}