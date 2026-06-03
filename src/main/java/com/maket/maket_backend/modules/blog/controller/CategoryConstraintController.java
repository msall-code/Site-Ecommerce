package com.maket.maket_backend.modules.blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class CategoryConstraintController {

    private final CategoryConstraintService constraintService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<CategoryConstraint> addConstraint(@RequestBody CategoryConstraintDTO dto) {
        return ResponseEntity.ok(constraintService.createConstraint(dto));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CategoryConstraint>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(constraintService.getConstraintsByCategoryId(categoryId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteConstraint(@PathVariable Long id) {
        constraintService.deleteConstraint(id);
        return ResponseEntity.noContent().build();
    }
}