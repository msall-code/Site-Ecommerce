package com.maket.maket_backend.modules.shop.repository;

import com.maket.maket_backend.modules.shop.model.Product;
import com.maket.maket_backend.modules.blog.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Cette méthode fait la jointure automatique entre Product et Seller
    List<Product> findBySellerKeycloakId(String keycloakId);

    List<Product> findByCategory(Category category);
}