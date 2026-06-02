package com.maket.maket_backend.modules.identity.repository;

import com.maket.maket_backend.modules.identity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByKeycloakId(String keycloakId);
}