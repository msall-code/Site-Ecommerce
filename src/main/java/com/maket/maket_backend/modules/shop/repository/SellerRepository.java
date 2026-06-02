package com.maket.maket_backend.modules.shop.repository;

import com.maket.maket_backend.modules.shop.model.Seller;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    Optional<Seller> findByKeycloakId(String keycloakId);
}