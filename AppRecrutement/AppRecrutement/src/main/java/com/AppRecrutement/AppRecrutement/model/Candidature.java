package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
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

    private Date dateEntretien;
    private String typeEntretien; // "EN_LIGNE" ou "PRESENTIEL"
    private String lienEntretien; // Google Meet link ou adresse

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @ManyToOne(fetch = FetchType.LAZY)
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
}
