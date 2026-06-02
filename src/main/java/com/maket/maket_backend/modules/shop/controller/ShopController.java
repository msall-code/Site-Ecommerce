package com.maket.maket_backend.modules.shop.controller;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maket.maket_backend.modules.blog.model.Category;
import com.maket.maket_backend.modules.blog.repository.CategoryRepository;
import com.maket.maket_backend.modules.shop.dto.ProductRequestDTO;
import com.maket.maket_backend.modules.shop.dto.ProductResponseDTO;
import com.maket.maket_backend.modules.shop.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    @PostMapping("/products")
    public ResponseEntity<ProductResponseDTO> create(@RequestBody ProductRequestDTO dto) {
        return ResponseEntity.ok(productService.addProduct(dto));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductResponseDTO>> getByCategory(@PathVariable Long categoryId) {
        // Correction Null Safety : on s'assure que categoryId n'est pas null
        Long id = Objects.requireNonNull(categoryId, "L'ID de la catégorie ne peut pas être nul");
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Catégorie non trouvée"));
        
        return ResponseEntity.ok(productService.getProductsByCategory(category));
    }
}