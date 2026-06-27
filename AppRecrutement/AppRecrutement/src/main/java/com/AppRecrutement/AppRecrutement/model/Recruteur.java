package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * Représente un recruteur dans le système de recrutement.
 * Cette classe étend Utilisateur et ajoute des propriétés spécifiques aux recruteurs.
 * Un recruteur peut publier des offres d'emploi et consulter les candidatures reçues.
 */
@Entity
@DiscriminatorValue("RECRUTEUR")
@Getter
@Setter
@NoArgsConstructor
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
}
