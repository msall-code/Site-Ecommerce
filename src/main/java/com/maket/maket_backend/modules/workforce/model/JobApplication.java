package com.maket.maket_backend.modules.workforce.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_applications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class JobApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long jobOfferId;
    private String candidateId; // ID Keycloak du postulant
    
    private String cvUrl; // Lien vers le CV (Firebase/S3)
    private String message; // Lettre de motivation courte

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

    private LocalDateTime appliedAt;

    @PrePersist
    protected void onCreate() {
        this.appliedAt = LocalDateTime.now();
        this.status = ApplicationStatus.PENDING;
    }
}