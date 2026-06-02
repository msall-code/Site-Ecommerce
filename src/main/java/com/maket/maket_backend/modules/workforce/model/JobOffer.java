package com.maket.maket_backend.modules.workforce.model;

import com.maket.maket_backend.modules.blog.model.Category;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_offers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class JobOffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private Double salary;
    private String employerId; // ID Keycloak du recruteur (souvent un SELLER)

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category; // SANTE, RESTAURATION, etc.

    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}