package com.maket.maket_backend.modules.shop.model;

import java.util.ArrayList;
import java.util.List;

import com.maket.maket_backend.modules.blog.model.Category;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    private Double price;
    private Integer stock;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; 

    // --- LA MAGIE EST ICI ---
    // On remplace tous les champs spécifiques par cette liste dynamique
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> attributes = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private Seller seller;

    private String qrCodeData; 
}