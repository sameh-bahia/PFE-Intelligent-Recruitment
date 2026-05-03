package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@DiscriminatorValue("CANDIDAT")
public class Candidat extends Utilisateur {

    private String telephone;
    private String adresse;
    private Date dateNaissance;
    private String titreProfil;

    @JsonIgnore
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Experience> experiences;

    @JsonIgnore
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Formation> formations;

    @JsonIgnore
    @OneToOne(mappedBy = "candidat", cascade = CascadeType.ALL)
    private CV cv;

    @JsonIgnore
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Candidature> candidatures;

    public Candidat() {
    }

    public Candidat(String email, String motDePasse, String nom, String prenom, Role role, String telephone, String adresse, Date dateNaissance, String titreProfil) {
        setEmail(email);
        setMotDePasse(motDePasse);
        setNom(nom);
        setPrenom(prenom);
        setRole(role);
        this.telephone = telephone;
        this.adresse = adresse;
        this.dateNaissance = dateNaissance;
        this.titreProfil = titreProfil;
    }

    public void telechargerCV() {
    }

    public List<Offre> consulterOffres() {
        return null;
    }

    public Candidature postuler(Offre offre, String lettre) {
        Candidature candidature = new Candidature();
        candidature.setOffre(offre);
        candidature.setCandidat(this);
        candidature.setLettreMotivation(lettre);
        candidature.setDatePostulation(new Date());
        candidature.setStatut(StatutCandidature.EN_ATTENTE);
        return candidature;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public Date getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(Date dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getTitreProfil() {
        return titreProfil;
    }

    public void setTitreProfil(String titreProfil) {
        this.titreProfil = titreProfil;
    }

    public List<Experience> getExperiences() {
        return experiences;
    }

    public void setExperiences(List<Experience> experiences) {
        this.experiences = experiences;
    }

    public List<Formation> getFormations() {
        return formations;
    }

    public void setFormations(List<Formation> formations) {
        this.formations = formations;
    }

    public CV getCv() {
        return cv;
    }

    public void setCv(CV cv) {
        this.cv = cv;
    }

    public List<Candidature> getCandidatures() {
        return candidatures;
    }

    public void setCandidatures(List<Candidature> candidatures) {
        this.candidatures = candidatures;
    }
}
