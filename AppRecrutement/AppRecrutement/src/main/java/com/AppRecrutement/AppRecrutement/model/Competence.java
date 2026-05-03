package com.AppRecrutement.AppRecrutement.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Competence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String categorie;

    @ManyToMany
    @JoinTable(
        name = "cv_competence",
        joinColumns = @JoinColumn(name = "competence_id"),
        inverseJoinColumns = @JoinColumn(name = "cv_id")
    )
    private List<CV> cvs;

    @ManyToMany
    @JoinTable(
        name = "offre_competence",
        joinColumns = @JoinColumn(name = "competence_id"),
        inverseJoinColumns = @JoinColumn(name = "offre_id")
    )
    private List<Offre> offres;

    public Competence() {
    }

    public Competence(String nom, String categorie) {
        this.nom = nom;
        this.categorie = categorie;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public List<CV> getCvs() {
        return cvs;
    }

    public void setCvs(List<CV> cvs) {
        this.cvs = cvs;
    }

    public List<Offre> getOffres() {
        return offres;
    }

    public void setOffres(List<Offre> offres) {
        this.offres = offres;
    }
}
