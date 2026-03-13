package sn.sall_boucherie.sall_boucherie.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "produits")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal prix;

    private String imageUrl;

    private boolean disponible = true;

    @Column(nullable = false)
    private Double poidsMini; // poids minimum en kg

}