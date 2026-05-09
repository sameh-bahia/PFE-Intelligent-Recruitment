package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Représente le CV d'un candidat dans le système.
 * Cette classe stocke les informations sur le fichier CV, le chemin du fichier et la date d'upload.
 * Un CV est lié à un seul candidat (relation OneToOne).
 */
@Entity
public class CV {

    /** Identifiant unique du CV */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Chemin du fichier CV sur le serveur */
    private String cheminFichier;

    /** Date et heure de l'upload du CV */
    private Date dateUpload;

    /** Candidat propriétaire du CV (relation OneToOne avec JsonBackReference pour éviter les boucles de sérialisation) */
    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    /** Liste des compétences extraites du CV */
    @JsonIgnore
    @ManyToMany(mappedBy = "cvs")
    private List<Competence> competences;

    /**
     * Constructeur par défaut qui initialise la date d'upload à la date actuelle.
     */
    public CV() {
        this.dateUpload = new Date();
    }

    /**
     * Constructeur pour créer un CV avec un chemin de fichier.
     * @param cheminFichier Chemin du fichier CV sur le serveur
     */
    public CV(String cheminFichier) {
        this.cheminFichier = cheminFichier;
        this.dateUpload = new Date();
    }

    /**
     * Extrait les informations du CV (compétences, expériences, etc.).
     * Méthode non implémentée pour l'instant.
     */
    public void extraireInformations() {
    }

    /**
     * Récupère l'identifiant du CV.
     * @return Identifiant unique du CV
     */
    public Long getId() {
        return id;
    }

    /**
     * Définit l'identifiant du CV.
     * @param id Nouvel identifiant
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Récupère le chemin du fichier CV.
     * @return Chemin du fichier sur le serveur
     */
    public String getCheminFichier() {
        return cheminFichier;
    }

    /**
     * Définit le chemin du fichier CV.
     * @param cheminFichier Nouveau chemin du fichier
     */
    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }


    /**
     * Récupère la date d'upload du CV.
     * @return Date d'upload
     */
    public Date getDateUpload() {
        return dateUpload;
    }

    /**
     * Définit la date d'upload du CV.
     * @param dateUpload Nouvelle date d'upload
     */
    public void setDateUpload(Date dateUpload) {
        this.dateUpload = dateUpload;
    }

    /**
     * Récupère le candidat propriétaire du CV.
     * @return Candidat associé au CV
     */
    public Candidat getCandidat() {
        return candidat;
    }

    /**
     * Définit le candidat propriétaire du CV.
     * @param candidat Nouveau candidat
     */
    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    /**
     * Récupère la liste des compétences extraites du CV.
     * @return Liste des compétences
     */
    public List<Competence> getCompetences() {
        return competences;
    }

    /**
     * Définit la liste des compétences extraites du CV.
     * @param competences Nouvelle liste de compétences
     */
    public void setCompetences(List<Competence> competences) {
        this.competences = competences;
    }
}
