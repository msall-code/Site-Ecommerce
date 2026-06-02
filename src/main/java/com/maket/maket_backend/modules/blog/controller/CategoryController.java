package com.maket.maket_backend.modules.blog.controller;

import java.util.List;
import java.util.Objects;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.maket.maket_backend.modules.blog.dto.CategoryCreateDTO;
import com.maket.maket_backend.modules.blog.dto.CategoryResponseDTO;
import com.maket.maket_backend.modules.blog.model.Category;
import com.maket.maket_backend.modules.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin/categories")
@CrossOrigin(origins = "http://localhost:8099")
@RequiredArgsConstructor 
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<CategoryResponseDTO> getCategories(@RequestParam(required = false) Long parentId) {
        List<Category> categories = (parentId == null) 
            ? categoryRepository.findByParentIsNull() 
            : categoryRepository.findByParentId(parentId);

        return categories.stream().map(cat -> {
            CategoryResponseDTO dto = new CategoryResponseDTO();
            dto.setId(cat.getId());
            dto.setName(cat.getName());
            dto.setRequiresGender(cat.getRequiresGender());
            dto.setRequiresSizeText(cat.getRequiresSizeText());
            dto.setRequiresShoeSize(cat.getRequiresShoeSize());
            dto.setRequiresPrescription(cat.getRequiresPrescription());
            dto.setFoodDelivery(cat.getIsFoodDelivery());
            dto.setRequiresColdChain(cat.getRequiresColdChain());
            dto.setAgeRestricted(cat.getIsAgeRestricted());
            dto.setSoldByWeight(cat.getIsSoldByWeight());
            return dto;
        }).toList();
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody CategoryCreateDTO dto) {
        Category category = new Category();
        mapDtoToEntity(dto, category);
        
        if (dto.getParentId() != null) {
            // Sécurisation de l'ID parent
            categoryRepository.findById(Objects.requireNonNull(dto.getParentId()))
                .ifPresent(category::setParent);
        }
        
        return ResponseEntity.ok(categoryRepository.save(category));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody CategoryCreateDTO dto) {
        return categoryRepository.findById(Objects.requireNonNull(id))
            .map(category -> {
                mapDtoToEntity(dto, category);
                if (dto.getParentId() != null) {
                    // Sécurisation ici aussi
                    categoryRepository.findById(Objects.requireNonNull(dto.getParentId()))
                        .ifPresent(category::setParent);
                }
                // Utilisation de Objects.requireNonNull pour la conformité @NonNull
                Category saved = categoryRepository.save(Objects.requireNonNull(category));
                return ResponseEntity.ok(saved);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(Objects.requireNonNull(id));
        return ResponseEntity.noContent().build();
    }

    private void mapDtoToEntity(CategoryCreateDTO dto, Category entity) {
        entity.setName(dto.getName());
        entity.setRequiresGender(dto.isRequiresGender());
        entity.setRequiresSizeText(dto.isRequiresSizeText());
        entity.setRequiresShoeSize(dto.isRequiresShoeSize());
        entity.setRequiresPrescription(dto.isRequiresPrescription());
        entity.setIsFoodDelivery(dto.isFoodDelivery());
        entity.setRequiresColdChain(dto.isRequiresColdChain());
        entity.setIsAgeRestricted(dto.isAgeRestricted());
        entity.setIsSoldByWeight(dto.isSoldByWeight());
    }
}