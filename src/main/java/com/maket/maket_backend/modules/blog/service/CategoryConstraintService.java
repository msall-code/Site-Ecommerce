package com.maket.maket_backend.modules.blog.service;

import java.util.Collections;
import java.util.List; // Ajouté

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maket.maket_backend.modules.blog.dto.CategoryConstraintDTO;
import com.maket.maket_backend.modules.blog.model.CategoryConstraint;
import com.maket.maket_backend.modules.blog.repository.CategoryConstraintRepository;
import com.maket.maket_backend.modules.blog.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryConstraintService {

    private final CategoryConstraintRepository constraintRepository;
    private final CategoryRepository categoryRepository;

    public List<CategoryConstraint> findAll() {
        return constraintRepository.findAll();
    }

    @Transactional
    public CategoryConstraint createConstraint(CategoryConstraintDTO dto) {
        CategoryConstraint constraint = new CategoryConstraint();
        constraint.setName(dto.getName());
        constraint.setControlType(dto.getControlType());
        constraint.setPossibleValues(dto.getPossibleValues());
        constraint.setUnit(dto.getUnit());
        constraint.setRequired(dto.isRequired());

        Long catId = dto.getCategoryId();
        if (catId != null) {
            categoryRepository.findById(catId)
                .ifPresent(constraint::setCategory);
        }

        return constraintRepository.save(constraint);
    }

    // CORRECTION ICI : Sécurisation du type pour supprimer le warning ligne 54
    public List<CategoryConstraint> getConstraintsByCategoryId(Long categoryId) {
        if (categoryId == null) {
            return Collections.emptyList();
        }
        return constraintRepository.findByCategoryId(categoryId);
    }
    
    @Transactional
    public void deleteConstraint(Long id) {
        if (id != null) {
            constraintRepository.deleteById(id);
        }
    }
}