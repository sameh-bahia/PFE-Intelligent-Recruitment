package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
public class Candidature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date datePostulation;

    private String lettreMotivation;

    private Double scoreCompatibilite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut;

    @ManyToOne
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @ManyToOne
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @JsonIgnore
    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL)
    private List<Recommandation> recommandations;

    public Candidature() {
        this.statut = StatutCandidature.EN_ATTENTE;
        this.datePostulation = new Date();
    }

    public Double calculerScore() {
        return 0.0;
    }

    public void envoyerNotification() {
    }

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

    public StatutCandidature getStatut() {
        return statut;
    }

    public void setStatut(StatutCandidature statut) {
        this.statut = statut;
    }

    public Candidat getCandidat() {
        return candidat;
    }

    public void setCandidat(Candidat candidat) {
        this.candidat = candidat;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public List<Recommandation> getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(List<Recommandation> recommandations) {
        this.recommandations = recommandations;
    }
}
