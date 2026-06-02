package com.maket.maket_backend.modules.blog.model;

import jakarta.persistence.Column;
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
@Table(name = "category_constraints")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryConstraint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // Ex: "Pointure EU", "Cible Sexe", "Type de tissu"
    
    // Type de champ pour le Front : "NUMBER", "TEXT", "DROPDOWN", "BOOLEAN"
    private String controlType; 

    // Pour les DROPDOWN : stocke les choix séparés par des virgules 
    // Ex: "Homme,Femme,Unisexe" ou "36,37,38,39,40"
    @Column(columnDefinition = "TEXT")
    private String possibleValues;

    private String unit; // Ex: "kg", "cm", "litres"
    
    private boolean required; // Est-ce que le vendeur doit obligatoirement remplir ce champ ?

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // La catégorie à laquelle on "branche" cette brique
}