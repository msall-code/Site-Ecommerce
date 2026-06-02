package com.maket.maket_backend.modules.workforce.model;

public enum ApplicationStatus {
    PENDING,    // Candidature envoyée
    REVIEWING,  // En cours d'examen par l'employeur
    ACCEPTED,   // Retenu pour entretien ou embauche
    REJECTED    // Refusé
}