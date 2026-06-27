package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

/**
 * Représente un candidat dans le système de recrutement.
 * Cette classe étend Utilisateur et ajoute des propriétés spécifiques aux candidats.
 * Un candidat peut avoir un CV, des expériences professionnelles, des formations et des candidatures.
 */
@Entity
@DiscriminatorValue("CANDIDAT")
@Getter
@Setter
@NoArgsConstructor
public class Candidat extends Utilisateur {

    /** Numéro de téléphone du candidat */
    private String telephone;

    /** Adresse physique du candidat */
    private String adresse;

    /** Date de naissance du candidat */
    private Date dateNaissance;

    /** Titre professionnel ou profil du candidat (ex: Développeur Full Stack) */
    private String titreProfil;

    /** Niveau d'étude du candidat */
    @Enumerated(EnumType.STRING)
    private NiveauEtude niveauEtude;

    /** Liste des expériences professionnelles du candidat */
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Experience> experiences;

    /** Liste des formations académiques du candidat */
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Formation> formations;

    /** CV du candidat (relation OneToOne avec JsonManagedReference pour éviter les boucles de sérialisation) */
    @JsonManagedReference
    @OneToOne(mappedBy = "candidat", cascade = CascadeType.ALL)
    private CV cv;

    /** Liste des candidatures envoyées par le candidat */
    @JsonIgnore
    @OneToMany(mappedBy = "candidat", cascade = CascadeType.ALL)
    private List<Candidature> candidatures;

    /**
     * Constructeur complet pour créer un candidat avec toutes les informations de base.
     * @param email Email du candidat
     * @param motDePasse Mot de passe du candidat
     * @param nom Nom du candidat
     * @param prenom Prénom du candidat
     * @param role Rôle de l'utilisateur (CANDIDAT)
     * @param telephone Numéro de téléphone
     * @param adresse Adresse physique
     * @param dateNaissance Date de naissance
     * @param titreProfil Titre professionnel
     */
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

    /**
     * Télécharge le CV du candidat (méthode non implémentée).
     */
    public void telechargerCV() {
    }

    /**
     * Consulte la liste des offres disponibles.
     * @return Liste des offres (non implémenté)
     */
    public List<Offre> consulterOffres() {
        return null;
    }

    /**
     * Crée une nouvelle candidature pour une offre d'emploi.
     * @param offre L'offre à laquelle le candidat postule
     * @param lettre La lettre de motivation
     * @return La candidature créée
     */
    public Candidature postuler(Offre offre, String lettre) {
        Candidature candidature = new Candidature();
        candidature.setOffre(offre);
        candidature.setCandidat(this);
        candidature.setLettreMotivation(lettre);
        candidature.setDatePostulation(new Date());
        candidature.setStatut(StatutCandidature.EN_ATTENTE);
        return candidature;
    }
}
