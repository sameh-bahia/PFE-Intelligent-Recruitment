package com.AppRecrutement.AppRecrutement.dto;

import java.time.LocalDateTime;

public class OffreSimpleDTO {
    private Long id;
    private String titre;
    private String description;
    private String lieu;
    private String salaire;
    private String domaine;
    private String typeOffre;
    private String sousDomaineIT;
    private String niveauEtudeRequis;
    private String nomEntreprise;
    private String posteRecruteur;

    public OffreSimpleDTO() {}

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

    public String getSalaire() {
        return salaire;
    }

    public void setSalaire(String salaire) {
        this.salaire = salaire;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public String getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(String typeOffre) {
        this.typeOffre = typeOffre;
    }

    public String getSousDomaineIT() {
        return sousDomaineIT;
    }

    public void setSousDomaineIT(String sousDomaineIT) {
        this.sousDomaineIT = sousDomaineIT;
    }

    public String getNiveauEtudeRequis() {
        return niveauEtudeRequis;
    }

    public void setNiveauEtudeRequis(String niveauEtudeRequis) {
        this.niveauEtudeRequis = niveauEtudeRequis;
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getPosteRecruteur() {
        return posteRecruteur;
    }

    public void setPosteRecruteur(String posteRecruteur) {
        this.posteRecruteur = posteRecruteur;
    }
}
