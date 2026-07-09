package com.AppRecrutement.AppRecrutement.dto;

import java.util.Date;

public class CandidatureDTO {
    private Long id;
    private Date datePostulation;
    private String lettreMotivation;
    private Double scoreCompatibilite;
    private Double scoreRelatif;
    private Integer scoreQuiz;
    private String statut;
    private String lienEntretien;
    private OffreSimpleDTO offre;
    private CandidatSimpleDTO candidat;

    public CandidatureDTO() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDatePostulation() {
        return datePostulation;
    }

    public void setDatePostulation(Date datePostulation) {
        this.datePostulation = datePostulation;
    }

    public String getLettreMotivation() {
        return lettreMotivation;
    }

    public void setLettreMotivation(String lettreMotivation) {
        this.lettreMotivation = lettreMotivation;
    }

    public Double getScoreCompatibilite() {
        return scoreCompatibilite;
    }

    public void setScoreCompatibilite(Double scoreCompatibilite) {
        this.scoreCompatibilite = scoreCompatibilite;
    }

    public Double getScoreRelatif() {
        return scoreRelatif;
    }

    public void setScoreRelatif(Double scoreRelatif) {
        this.scoreRelatif = scoreRelatif;
    }

    public Integer getScoreQuiz() {
        return scoreQuiz;
    }

    public void setScoreQuiz(Integer scoreQuiz) {
        this.scoreQuiz = scoreQuiz;
    }

    public String getLienEntretien() {
        return lienEntretien;
    }

    public void setLienEntretien(String lienEntretien) {
        this.lienEntretien = lienEntretien;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public OffreSimpleDTO getOffre() {
        return offre;
    }

    public void setOffre(OffreSimpleDTO offre) {
        this.offre = offre;
    }

    public CandidatSimpleDTO getCandidat() {
        return candidat;
    }

    public void setCandidat(CandidatSimpleDTO candidat) {
        this.candidat = candidat;
    }
}
