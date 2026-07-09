package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Question - Représente une question d'un quiz
 * 
 * Chaque question appartient à un quiz (@ManyToOne) et contient plusieurs options de réponse (@OneToMany).
 * Le champ "points" permet de pondérer les questions pour le calcul du score final.
 * 
 * CHOIX TECHNIQUE : Relation @ManyToOne avec Quiz
 * - Plusieurs questions peuvent appartenir au même quiz
 * - FetchType.LAZY pour optimiser les performances
 * - @JoinColumn avec nullable=false : une question doit obligatoirement être liée à un quiz
 * 
 * CHOIX TECHNIQUE : Cascade.ALL sur options
 * - Permet de gérer les options de réponse automatiquement avec la question
 * - orphanRemoval=true pour nettoyer les options orphelines
 */
@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Énoncé de la question
     * columnDefinition = "TEXT" pour supporter des énoncés longs
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String enonce;

    /**
     * Points attribués à cette question
     * Utilisé pour le calcul du score : score total = somme des points des questions correctes
     * Valeur par défaut : 1 point
     */
    @Column(name = "points")
    private Integer points = 1;

    /**
     * Relation ManyToOne avec Quiz
     * Une question appartient obligatoirement à un quiz
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnore
    private Quiz quiz;

    /**
     * Relation OneToMany avec OptionReponse
     * Une question peut avoir plusieurs options de réponse
     * cascade = CascadeType.ALL : gère automatiquement les options
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionReponse> options = new ArrayList<>();

    public Question() {
    }

    /**
     * Constructeur avec paramètres
     * @param enonce Énoncé de la question
     * @param points Points attribués à cette question
     */
    public Question(String enonce, Integer points) {
        this.enonce = enonce;
        this.points = points;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEnonce() {
        return enonce;
    }

    public void setEnonce(String enonce) {
        this.enonce = enonce;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<OptionReponse> getOptions() {
        return options;
    }

    public void setOptions(List<OptionReponse> options) {
        this.options = options;
    }

    /**
     * Méthode helper pour ajouter une option à la question
     * Maintient la bidirectionnalité de la relation
     * @param option L'option de réponse à ajouter
     */
    public void addOption(OptionReponse option) {
        options.add(option);
        option.setQuestion(this);
    }

    /**
     * Méthode helper pour supprimer une option de la question
     * Maintient la bidirectionnalité de la relation
     * @param option L'option de réponse à supprimer
     */
    public void removeOption(OptionReponse option) {
        options.remove(option);
        option.setQuestion(null);
    }
}
