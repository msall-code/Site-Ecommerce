package com.maket.maket_backend.modules.shop.service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maket.maket_backend.modules.blog.model.Category;
import com.maket.maket_backend.modules.blog.model.CategoryConstraint;
import com.maket.maket_backend.modules.blog.repository.CategoryRepository;
import com.maket.maket_backend.modules.shop.dto.ProductRequestDTO;
import com.maket.maket_backend.modules.shop.dto.ProductResponseDTO;
import com.maket.maket_backend.modules.shop.model.Product;
import com.maket.maket_backend.modules.shop.model.ProductAttribute;
import com.maket.maket_backend.modules.shop.model.Seller;
import com.maket.maket_backend.modules.shop.repository.ProductRepository;
import com.maket.maket_backend.modules.shop.repository.SellerRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final SellerRepository sellerRepository;
    private final CategoryRepository categoryRepository;
    private final QRCodeService qrCodeService;

    @Transactional
    public ProductResponseDTO addProductSecure(ProductRequestDTO dto, String keycloakId) {
        Seller seller = sellerRepository.findByKeycloakId(Objects.requireNonNull(keycloakId))
                .orElseThrow(() -> new NoSuchElementException("Vendeur non trouvé"));
        return processProductSaving(dto, seller);
    }

    @Transactional
    public ProductResponseDTO addProduct(ProductRequestDTO dto) {
        Long sellerId = Objects.requireNonNull(dto.getSellerId(), "L'ID du vendeur est obligatoire");
        Seller seller = sellerRepository.findById(sellerId)
                .orElseThrow(() -> new NoSuchElementException("Vendeur non trouvé"));
        return processProductSaving(dto, seller);
    }

    private ProductResponseDTO processProductSaving(ProductRequestDTO dto, Seller seller) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setStock(dto.getStock() != null ? dto.getStock() : 1);
        product.setImageUrl(dto.getImageUrl());
        product.setSeller(seller);

        // RÉSOLUTION DE L'UNBOXING : Utilisation d'Optional pour une sécurité totale
        // RÉSOLUTION DÉFINITIVE DE L'UNBOXING
        Long catId = dto.getSubcategoryId();
        if (catId == null) {
            catId = dto.getCategoryId();
        }

        if (catId != null) {
            // En utilisant directement l'objet Long 'catId' ici, 
            // on évite l'expression ternaire qui provoquait l'alerte d'unboxing.
            Category cat = categoryRepository.findById(catId)
                    .orElseThrow(() -> new NoSuchElementException("Catégorie non trouvée"));
            product.setCategory(cat);

            if (cat.getConstraints() != null && !cat.getConstraints().isEmpty()) {
                handleDynamicAttributes(product, cat.getConstraints(), dto);
            }
        }

        Product saved = productRepository.save(product);

        // QR Code
        String qrData = qrCodeService.generateProductQRCode(
            saved.getId(), 
            dto.getLatitude(), 
            dto.getLongitude()
        );
        
        saved.setQrCodeData(qrData);
        return mapToResponseDTO(productRepository.save(saved));
    }

    private void handleDynamicAttributes(Product product, List<CategoryConstraint> constraints, ProductRequestDTO dto) {
        List<ProductAttribute> attributes = new ArrayList<>();
        for (CategoryConstraint constraint : constraints) {
            String value = extractValueFromDto(dto, constraint.getName());

            if (constraint.isRequired() && (value == null || value.isBlank())) {
                throw new IllegalArgumentException("Le champ '" + constraint.getName() + "' est obligatoire.");
            }

            if (value != null) {
                ProductAttribute attr = new ProductAttribute();
                attr.setProduct(product);
                attr.setConstraint(constraint);
                attr.setValue(value);
                attributes.add(attr);
            }
        }
        product.setAttributes(attributes);
    }

    private String extractValueFromDto(ProductRequestDTO dto, String constraintName) {
        String name = constraintName.toLowerCase();
        if (name.contains("sexe") || name.contains("gender")) return dto.getGender();
        if (name.contains("pointure") || name.contains("shoe")) 
            return dto.getShoeSize() != null ? String.valueOf(dto.getShoeSize()) : null;
        if (name.contains("poids") || name.contains("weight")) 
            return dto.getWeight() != null ? String.valueOf(dto.getWeight()) : null;
        return null;
    }

    public List<ProductResponseDTO> getProductsBySellerKeycloakId(String keycloakId) {
        return productRepository.findBySellerKeycloakId(Objects.requireNonNull(keycloakId))
                .stream().map(this::mapToResponseDTO).toList();
    }

    public List<ProductResponseDTO> getProductsByCategory(Category category) {
        return productRepository.findByCategory(Objects.requireNonNull(category))
                .stream().map(this::mapToResponseDTO).toList();
    }

    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToResponseDTO).toList();
    }

    private ProductResponseDTO mapToResponseDTO(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setCategory(product.getCategory() != null ? product.getCategory().getName() : null);
        dto.setQrCodeData(product.getQrCodeData());
        return dto;
    }
}