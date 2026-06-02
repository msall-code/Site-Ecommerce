package com.maket.maket_backend.modules.shop.model;

import com.maket.maket_backend.modules.blog.model.CategoryConstraint;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_attributes")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // La valeur saisie par le vendeur (ex: "42", "Rouge", "Homme")
    // On utilise String pour pouvoir tout stocker, on convertira selon le type au besoin
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "constraint_id")
    private CategoryConstraint constraint; 
}