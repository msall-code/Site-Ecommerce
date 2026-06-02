package com.maket.maket_backend.modules.blog.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maket.maket_backend.modules.blog.model.CategoryConstraint;

@Repository
public interface CategoryConstraintRepository extends JpaRepository<CategoryConstraint, Long> {
    // Permet de récupérer toutes les contraintes d'une catégorie spécifique
    List<CategoryConstraint> findByCategoryId(Long categoryId);
}