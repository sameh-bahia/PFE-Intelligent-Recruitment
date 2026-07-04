package com.AppRecrutement.AppRecrutement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
public class Utilisateur
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    private String nom;
    private String prenom;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateInscription;

    @Column(nullable = true)
    private Boolean enabled = true;

    // Cette méthode s'exécute automatiquement avant la création en base
    @PrePersist
    protected void onCreate() {
        dateInscription = new Date();
        enabled = true;
    }
}
