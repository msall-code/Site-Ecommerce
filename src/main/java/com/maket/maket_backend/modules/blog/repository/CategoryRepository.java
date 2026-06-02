package com.maket.maket_backend.modules.blog.repository;

import com.maket.maket_backend.modules.blog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Récupère uniquement les catégories mères
    List<Category> findByParentIsNull();

    // Récupère les sous-catégories d'une catégorie parente spécifique
    List<Category> findByParentId(Long parentId);
}