package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class CV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cheminFichier;
    private String texteBrut;
    private Date dateUpload;

    @OneToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @JsonIgnore
    @ManyToMany(mappedBy = "cvs")
    private List<Competence> competences;

    public CV() {
        this.dateUpload = new Date();
    }

    public CV(String cheminFichier, String texteBrut) {
        this.cheminFichier = cheminFichier;
        this.texteBrut = texteBrut;
        this.dateUpload = new Date();
    }

    public void extraireInformations() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCheminFichier() {
        return cheminFichier;
    }

    public void setCheminFichier(String cheminFichier) {
        this.cheminFichier = cheminFichier;
    }

    public String getTexteBrut() {
        return texteBrut;
    }

    public void setTexteBrut(String texteBrut) {
        this.texteBrut = texteBrut;
    }

    public Date getDateUpload() {
        return dateUpload;
    }

    public void setDateUpload(Date dateUpload) {
        this.dateUpload = dateUpload;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public List<Competence> getCompetences() {
        return competences;
    }

    public void setCompetences(List<Competence> competences) {
        this.competences = competences;
    }
}
