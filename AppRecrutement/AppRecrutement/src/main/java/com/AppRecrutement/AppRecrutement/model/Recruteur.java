package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

/**
 * Représente un recruteur dans le système de recrutement.
 * Cette classe étend Utilisateur et ajoute des propriétés spécifiques aux recruteurs.
 * Un recruteur peut publier des offres d'emploi et consulter les candidatures reçues.
 */
@Entity
@DiscriminatorValue("RECRUTEUR")
public class Recruteur extends Utilisateur {

    /** Nom de l'entreprise du recruteur */
    private String nomEntreprise;

    /** Poste du recruteur dans l'entreprise (ex: Responsable RH) */
    private String poste;

    /** URL de la photo de profil du recruteur */
    private String photoProfil;

    /** Date de naissance du recruteur */
    private String dateNaissance;

    /** Adresse du recruteur */
    private String adresse;

    /** Lieu de travail précédent du recruteur */
    private String lieuTravailPrecedent;

    /** Entreprise précédente du recruteur */
    private String entreprisePrecedente;

    /** Liste des offres d'emploi publiées par le recruteur */
    @JsonIgnore
    @OneToMany(mappedBy = "recruteur", cascade = CascadeType.ALL)
    private List<Offre> offres;

    /**
     * Constructeur par défaut pour JPA/Hibernate.
     */
    public Recruteur() {
    }

    /**
     * Constructeur pour créer un recruteur avec les informations de base.
     * @param email Email du recruteur
     * @param motDePasse Mot de passe du recruteur
     * @param nom Nom du recruteur
     * @param prenom Prénom du recruteur
     * @param role Rôle de l'utilisateur (RECRUTEUR)
     * @param nomEntreprise Nom de l'entreprise
     * @param poste Poste du recruteur
     */
    public Recruteur(String email, String motDePasse, String nom, String prenom, Role role, String nomEntreprise, String poste) {
        setEmail(email);
        setMotDePasse(motDePasse);
        setNom(nom);
        setPrenom(prenom);
        setRole(role);
        this.nomEntreprise = nomEntreprise;
        this.poste = poste;
    }

    /**
     * Publie une nouvelle offre d'emploi.
     * @param offre L'offre à publier
     */
    public void publierOffre(Offre offre) {
        offre.setRecruteur(this);
        this.offres.add(offre);
    }

    /**
     * Consulte les candidatures reçues pour une offre spécifique.
     * @param offre L'offre dont on veut consulter les candidatures
     * @return Liste des candidatures pour cette offre
     */
    public List<Candidature> consulterCandidatures(Offre offre) {
        return offre.getCandidatures();
    }

    /**
     * Décide du statut d'une candidature (accepter, rejeter, mettre en attente).
     * @param c La candidature à traiter
     * @param s Le nouveau statut de la candidature
     */
    public void deciderCandidature(Candidature c, StatutCandidature s) {
        c.setStatut(s);
    }

    /**
     * Récupère le nom de l'entreprise.
     * @return Nom de l'entreprise
     */
    public String getNomEntreprise() {
        return nomEntreprise;
    }

    /**
     * Définit le nom de l'entreprise.
     * @param nomEntreprise Nouveau nom de l'entreprise
     */
    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    /**
     * Récupère le poste du recruteur.
     * @return Poste du recruteur
     */
    public String getPoste() {
        return poste;
    }

    /**
     * Définit le poste du recruteur.
     * @param poste Nouveau poste du recruteur
     */
    public void setPoste(String poste) {
        this.poste = poste;
    }

    /**
     * Récupère la liste des offres publiées.
     * @return Liste des offres
     */
    public List<Offre> getOffres() {
        return offres;
    }

    /**
     * Définit la liste des offres publiées.
     * @param offres Nouvelle liste des offres
     */
    public void setOffres(List<Offre> offres) {
        this.offres = offres;
    }

    /**
     * Récupère l'URL de la photo de profil.
     * @return URL de la photo de profil
     */
    public String getPhotoProfil() {
        return photoProfil;
    }

    /**
     * Définit l'URL de la photo de profil.
     * @param photoProfil Nouvelle URL de la photo de profil
     */
    public void setPhotoProfil(String photoProfil) {
        this.photoProfil = photoProfil;
    }

    /**
     * Récupère la date de naissance.
     * @return Date de naissance
     */
    public String getDateNaissance() {
        return dateNaissance;
    }

    /**
     * Définit la date de naissance.
     * @param dateNaissance Nouvelle date de naissance
     */
    public void setDateNaissance(String dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    /**
     * Récupère l'adresse.
     * @return Adresse
     */
    public String getAdresse() {
        return adresse;
    }

    /**
     * Définit l'adresse.
     * @param adresse Nouvelle adresse
     */
    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    /**
     * Récupère le lieu de travail précédent.
     * @return Lieu de travail précédent
     */
    public String getLieuTravailPrecedent() {
        return lieuTravailPrecedent;
    }

    /**
     * Définit le lieu de travail précédent.
     * @param lieuTravailPrecedent Nouveau lieu de travail précédent
     */
    public void setLieuTravailPrecedent(String lieuTravailPrecedent) {
        this.lieuTravailPrecedent = lieuTravailPrecedent;
    }

    /**
     * Récupère l'entreprise précédente.
     * @return Entreprise précédente
     */
    public String getEntreprisePrecedente() {
        return entreprisePrecedente;
    }

    /**
     * Définit l'entreprise précédente.
     * @param entreprisePrecedente Nouvelle entreprise précédente
     */
    public void setEntreprisePrecedente(String entreprisePrecedente) {
        this.entreprisePrecedente = entreprisePrecedente;
    }
}
