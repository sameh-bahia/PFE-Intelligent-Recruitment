package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Offre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Titre limité à 200 caractères (suffisant pour un titre d'offre)
    @Column(nullable = false, length = 200)
    private String titre;

    // Description en TEXT pour permettre des descriptions longues
    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    // Lieu limité à 200 caractères (ex: "Paris, France" ou "Remote")
    @Column(nullable = false, length = 200)
    private String lieu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeContrat typeContrat;

    @Column(nullable = false)
    private Boolean estOuverte;

    // Salaire limité à 100 caractères (ex: "3000€ - 4500€")
    @Column(length = 100)
    private String salaire;
    private Double salaireMin;
    private Double salaireMax;

    @ManyToOne
    @JoinColumn(name = "recruteur_id")
    private Recruteur recruteur;

    @JsonIgnore
    @OneToMany(mappedBy = "offre", cascade = CascadeType.ALL)
    private List<Candidature> candidatures;

    @JsonIgnore
    @ManyToMany(mappedBy = "offres")
    private List<Competence> competences;

    public Offre() {
        this.estOuverte = true;
    }

    public Offre(String titre, String description, String lieu, TypeContrat typeContrat, Double salaireMin, Double salaireMax) {
        this.titre = titre;
        this.description = description;
        this.lieu = lieu;
        this.typeContrat = typeContrat;
        this.salaireMin = salaireMin;
        this.salaireMax = salaireMax;
        this.estOuverte = true;
    }

    public void fermerOffre() {
        this.estOuverte = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public Boolean getEstOuverte() {
        return estOuverte;
    }

    public void setEstOuverte(Boolean estOuverte) {
        this.estOuverte = estOuverte;
    }

    public String getSalaire() {
        return salaire;
    }

    public void setSalaire(String salaire) {
        this.salaire = salaire;
    }

    public Double getSalaireMin() {
        return salaireMin;
    }

    public void setSalaireMin(Double salaireMin) {
        this.salaireMin = salaireMin;
    }

    public Double getSalaireMax() {
        return salaireMax;
    }

    public void setSalaireMax(Double salaireMax) {
        this.salaireMax = salaireMax;
    }

    public Recruteur getRecruteur() {
        return recruteur;
    }

    public void setRecruteur(Recruteur recruteur) {
        this.recruteur = recruteur;
    }

    public List<Candidature> getCandidatures() {
        return candidatures;
    }

    public void setCandidatures(List<Candidature> candidatures) {
        this.candidatures = candidatures;
    }

    public List<Competence> getCompetences() {
        return competences;
    }

    public void setCompetences(List<Competence> competences) {
        this.competences = competences;
    }
}
