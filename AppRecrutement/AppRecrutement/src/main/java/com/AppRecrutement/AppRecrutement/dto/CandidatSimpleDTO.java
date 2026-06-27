package com.AppRecrutement.AppRecrutement.dto;

public class CandidatSimpleDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String titreProfil;
    private Long cvId;

    public CandidatSimpleDTO() {}

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTitreProfil() {
        return titreProfil;
    }

    public void setTitreProfil(String titreProfil) {
        this.titreProfil = titreProfil;
    }

    public Long getCvId() {
        return cvId;
    }

    public void setCvId(Long cvId) {
        this.cvId = cvId;
    }
}
