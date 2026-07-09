package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Quiz - Représente un quiz technique associé à une offre d'emploi
 * 
 * Cette entité est au cœur de la fonctionnalité de test des candidats.
 * Chaque quiz est lié à une seule offre (@OneToOne) et contient plusieurs questions (@OneToMany).
 * 
 * CHOIX TECHNIQUE : Relation @OneToOne avec Offre
 * - Chaque offre peut avoir au plus un quiz (optionnel)
 * - FetchType.LAZY pour optimiser les performances (ne charger le quiz que si nécessaire)
 * 
 * CHOIX TECHNIQUE : Cascade.ALL sur questions
 * - Permet de créer/supprimer automatiquement les questions lors de la création/suppression du quiz
 * - orphanRemoval=true supprime les questions orphelines si elles ne sont plus liées au quiz
 */
@Entity
@Table(name = "quiz")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    @Column(name = "duree_minutes")
    private Integer dureeMinutes;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    /**
     * Relation OneToOne avec Offre
     * mappedBy = "quiz" indique que la propriété de jointure est dans l'entité Offre
     * FetchType.LAZY : le quiz n'est chargé que si on y accède explicitement
     * @JsonIgnore : évite la sérialisation circulaire Quiz -> Offre -> Quiz
     */
    @JsonIgnore
    @OneToOne(mappedBy = "quiz", fetch = FetchType.LAZY)
    private Offre offre;

    /**
     * Relation OneToMany avec Question
     * cascade = CascadeType.ALL : opérations (CRUD) propagées aux questions
     * orphanRemoval = true : suppression automatique des questions orphelines
     */
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();

    /**
     * Constructeur par défaut
     * Initialise automatiquement la date de création à l'instant présent
     */
    public Quiz() {
        this.dateCreation = LocalDateTime.now();
    }

    /**
     * Constructeur avec paramètres
     * @param titre Titre du quiz
     * @param dureeMinutes Durée du quiz en minutes (utilisé pour le timer anti-triche)
     */
    public Quiz(String titre, Integer dureeMinutes) {
        this.titre = titre;
        this.dureeMinutes = dureeMinutes;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public Integer getDureeMinutes() {
        return dureeMinutes;
    }

    public void setDureeMinutes(Integer dureeMinutes) {
        this.dureeMinutes = dureeMinutes;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Offre getOffre() {
        return offre;
    }

    public void setOffre(Offre offre) {
        this.offre = offre;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    /**
     * Méthode helper pour ajouter une question au quiz
     * Maintient la bidirectionnalité de la relation
     * @param question La question à ajouter
     */
    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    /**
     * Méthode helper pour supprimer une question du quiz
     * Maintient la bidirectionnalité de la relation
     * @param question La question à supprimer
     */
    public void removeQuestion(Question question) {
        questions.remove(question);
        question.setQuiz(null);
    }
}
