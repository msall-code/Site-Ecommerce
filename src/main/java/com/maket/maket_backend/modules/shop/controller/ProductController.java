package com.maket.maket_backend.modules.shop.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maket.maket_backend.modules.shop.dto.ProductRequestDTO;
import com.maket.maket_backend.modules.shop.dto.ProductResponseDTO;
import com.maket.maket_backend.modules.shop.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@CrossOrigin(origins = "http://127.0.0.1:5500") // L'adresse de ton front
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping("/add")
    @PreAuthorize("hasRole('vendeur')")
    public ResponseEntity<ProductResponseDTO> addProduct(
            @RequestBody ProductRequestDTO dto,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(productService.addProductSecure(dto, jwt.getSubject()));
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasRole('vendeur')")
    public ResponseEntity<List<ProductResponseDTO>> getMyProducts(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(productService.getProductsBySellerKeycloakId(jwt.getSubject()));
    }

    @GetMapping("/catalog")
    public ResponseEntity<List<ProductResponseDTO>> getCatalog() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
}