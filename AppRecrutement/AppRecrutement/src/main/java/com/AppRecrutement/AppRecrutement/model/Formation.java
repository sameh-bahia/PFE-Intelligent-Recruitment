package com.AppRecrutement.AppRecrutement.model;

import jakarta.persistence.*;

@Entity
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diplome;
    private String etablissement;
    private String specialite;
    private Integer anneeObtention;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    public Formation() {
    }

    public Formation(String diplome, String etablissement, String specialite, Integer anneeObtention) {
        this.diplome = diplome;
        this.etablissement = etablissement;
        this.specialite = specialite;
        this.anneeObtention = anneeObtention;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiplome() {
        return diplome;
    }

    public void setDiplome(String diplome) {
        this.diplome = diplome;
    }

    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    public Integer getAnneeObtention() {
        return anneeObtention;
    }

    public void setAnneeObtention(Integer anneeObtention) {
        this.anneeObtention = anneeObtention;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }
}
