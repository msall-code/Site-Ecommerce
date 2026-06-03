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

import com.maket.maket_backend.modules.blog.dto.CategoryConstraintDTO;
import com.maket.maket_backend.modules.blog.model.CategoryConstraint;
import com.maket.maket_backend.modules.blog.service.CategoryConstraintService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/constraints")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8099")
@PreAuthorize("hasRole('admin')")
public class ConstraintAdminController {

    private final CategoryConstraintService constraintService;

    @GetMapping
public ResponseEntity<List<CategoryConstraint>> getAllConstraints() {
    try {
        List<CategoryConstraint> constraints = constraintService.findAll();
        if (constraints == null) return ResponseEntity.ok(List.of());
        return ResponseEntity.ok(constraints);
    } catch (Exception e) {
        // Log l'erreur réelle dans ta console Java pour debug
        return ResponseEntity.status(500).build();
    }
}

    @PostMapping
    public ResponseEntity<CategoryConstraint> createConstraint(@RequestBody CategoryConstraintDTO dto) {
        return ResponseEntity.ok(constraintService.createConstraint(dto));
    }
}