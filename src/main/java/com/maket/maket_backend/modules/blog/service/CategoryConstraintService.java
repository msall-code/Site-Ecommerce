package com.maket.maket_backend.modules.blog.service;

import java.util.List;

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

    // AJOUT : Pour lister toutes les contraintes dans la bibliothèque admin
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

    // Sécurité : on ne cherche la catégorie que si l'ID est présent
    if (dto.getCategoryId() != null) {
        categoryRepository.findById(dto.getCategoryId())
            .ifPresent(constraint::setCategory);
    }

    return constraintRepository.save(constraint);
}

    public List<CategoryConstraint> getConstraintsByCategoryId(Long categoryId) {
        return constraintRepository.findByCategoryId(categoryId);
    }
    
    @Transactional
    public void deleteConstraint(Long id) {
        constraintRepository.deleteById(id);
    }
}