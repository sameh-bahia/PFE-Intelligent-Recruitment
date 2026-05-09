package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String diplome;
    private String etablissement;
    private String specialite;
    private String anneeObtention;  // Format YYYY-MM-DD

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    @JsonIgnore
    private Candidat candidat;

    public Formation() {
    }

    public Formation(String diplome, String etablissement, String specialite, String anneeObtention) {
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

    public String getAnneeObtention() {
        return anneeObtention;
    }

    public void setAnneeObtention(String anneeObtention) {
        this.anneeObtention = anneeObtention;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }
}
