package com.maket.maket_backend.modules.blog.controller;

import com.maket.maket_backend.modules.blog.dto.PostRequestDTO;
import com.maket.maket_backend.modules.blog.dto.PostResponseDTO;
import com.maket.maket_backend.modules.blog.model.Category;
import com.maket.maket_backend.modules.blog.service.BlogService;
import com.maket.maket_backend.modules.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        // Correction : findAll() remplace values() de l'Enum
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping("/create")
    public ResponseEntity<PostResponseDTO> create(@RequestBody PostRequestDTO dto, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(blogService.createPost(dto, jwt.getSubject()));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<PostResponseDTO>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(blogService.getPostsByCategoryId(categoryId));
    }
}