package com.maket.maket_backend.modules.blog.repository;

import com.maket.maket_backend.modules.blog.model.Post;
import com.maket.maket_backend.modules.blog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCategory(Category category);
    List<Post> findAllByOrderByCreatedAtDesc();
}