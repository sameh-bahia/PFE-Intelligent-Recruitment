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

    @Column(columnDefinition = "TEXT")
    private String lettreMotivation;

    private Double scoreCompatibilite;

    private Integer scoreQuiz;

    @Column(name = "date_quiz")
    private java.time.LocalDateTime dateQuiz;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCandidature statut;

    private Date dateEntretien;
    private String typeEntretien; // "EN_LIGNE" ou "PRESENTIEL"
    
    /**
     * Lien de l'entretien (Google Meet ou autre)
     * 
     * Ce champ stocke le lien de la visioconférence généré automatiquement.
     * Format Google Meet : https://meet.google.com/xxx-yyyy-zzz
     * 
     * CHOIX TECHNIQUE : Réutilisation du champ existant
     * - Ce champ existant est utilisé pour stocker le lien Google Meet
     * - Évite de créer un nouveau champ spécifique
     * - Compatible avec les entretiens en ligne et présentiel
     * 
     * LOGIQUE MÉTIER : Génération automatique
     * - Le lien est généré par MeetService lors de l'acceptation
     * - Le recruteur peut cliquer sur le lien pour rejoindre l'entretien
     * - Le candidat voit le lien dans son dashboard
     */
    private String lienEntretien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidat_id")
    private Candidat candidat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offre_id")
    private Offre offre;

    @JsonIgnore
    @OneToMany(mappedBy = "candidature", cascade = CascadeType.ALL)
    private List<Recommandation> recommandations;

    /**
     * Constructeur par défaut généré par Lombok (@NoArgsConstructor)
     * Initialise automatiquement le statut à EN_ATTENTE et la date de postulation
     * 
     * NOTE : L'initialisation des valeurs par défaut se fait via @PrePersist
     * dans une méthode annotée pour garantir l'exécution avant la sauvegarde en base
     */
    @PrePersist
    protected void onCreate() {
        if (this.statut == null) {
            this.statut = StatutCandidature.EN_ATTENTE;
        }
        if (this.datePostulation == null) {
            this.datePostulation = new Date();
        }
    }

    public Double calculerScore() {
        return 0.0;
    }

    public void envoyerNotification() {
    }
}
