package com.maket.maket_backend.modules.blog.service;

import com.maket.maket_backend.modules.blog.dto.PostRequestDTO;
import com.maket.maket_backend.modules.blog.dto.PostResponseDTO;
import com.maket.maket_backend.modules.blog.model.Category;
import com.maket.maket_backend.modules.blog.model.Post;
import com.maket.maket_backend.modules.blog.repository.CategoryRepository;
import com.maket.maket_backend.modules.blog.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    public PostResponseDTO createPost(PostRequestDTO dto, String authorId) {
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setImageUrl(dto.getImageUrl());
        
        // CORRECTION : Sécurisation de l'ID extrait du DTO
        Long categoryId = Objects.requireNonNull(dto.getCategoryId(), "L'ID de la catégorie est requis");
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NoSuchElementException("Catégorie non trouvée"));
        
        post.setCategory(category);
        post.setAuthorId(authorId);
        return mapToResponseDTO(postRepository.save(post));
    }

    public List<PostResponseDTO> getPostsByCategoryId(Long categoryId) {
        // CORRECTION : Sécurisation du paramètre d'entrée
        Long safeId = Objects.requireNonNull(categoryId, "L'ID de la catégorie ne doit pas être null");
        Category category = categoryRepository.findById(safeId)
                .orElseThrow(() -> new NoSuchElementException("Catégorie non trouvée"));
        return postRepository.findByCategory(category).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private PostResponseDTO mapToResponseDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setImageUrl(post.getImageUrl());
        if (post.getCategory() != null) {
            dto.setCategory(post.getCategory().getName());
        }
        dto.setCreatedAt(post.getCreatedAt());
        return dto;
    }
}