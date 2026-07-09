package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Date de création de l'offre
    @Column(name = "date_creation")
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Titre limité à 200 caractères (suffisant pour un titre d'offre)
    @Column(nullable = false, length = 200)
    private String titre;

    // Description en TEXT pour permettre des descriptions longues
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Lieu limité à 200 caractères (ex: "Paris, France" ou "Remote")
    @Column(nullable = false, length = 200)
    private String lieu;


    @Column(nullable = false)
    private Boolean estOuverte = true;

    // Salaire limité à 100 caractères (ex: "3000€ - 4500€")
    @Column(length = 100)
    private String salaire;
    private Double salaireMin;
    private Double salaireMax;

    // Domaine de l'offre (forcé à IT)
    @Column(length = 50, nullable = false)
    private String domaine = "IT";

    // Type d'offre (EMPLOI, STAGE, ALTERNANCE, FREELANCE)
    @Enumerated(EnumType.STRING)
    private TypeOffre typeOffre;

    // Sous-domaine IT (DEVELOPPEMENT, DATA_SCIENCE, DEVOPS, CYBERSECURITE, GESTION_PROJET, QA)
    @Enumerated(EnumType.STRING)
    private SousDomaineIT sousDomaineIT;

    // Niveau d'étude requis pour l'offre
    @Enumerated(EnumType.STRING)
    private NiveauEtude niveauEtudeRequis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruteur_id")
    private Recruteur recruteur;

    @JsonIgnore
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Candidature> candidatures;

    @JsonIgnoreProperties("offres")
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "offre_competence",
        joinColumns = @JoinColumn(name = "offre_id"),
        inverseJoinColumns = @JoinColumn(name = "competence_id")
    )
    private List<Competence> competences;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    public Offre(String titre, String description, String lieu, TypeOffre typeOffre, SousDomaineIT sousDomaineIT, Double salaireMin, Double salaireMax, NiveauEtude niveauEtudeRequis) {
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.typeOffre = typeOffre;
        this.sousDomaineIT = sousDomaineIT;
        this.salaireMin = salaireMin;
        this.salaireMax = salaireMax;
        this.niveauEtudeRequis = niveauEtudeRequis;
        this.estOuverte = true;
        this.domaine = "IT";
    }

    public void fermerOffre() {
        this.estOuverte = false;
    }
}
