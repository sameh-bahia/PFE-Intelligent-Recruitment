package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Entité OptionReponse - Représente une option de réponse pour une question
 * 
 * Chaque option appartient à une question (@ManyToOne) et peut être correcte ou incorrecte.
 * Le champ "isCorrect" est SENSIBLE et ne doit JAMAIS être exposé aux candidats (voir DTOs).
 * 
 * CHOIX TECHNIQUE : Relation @ManyToOne avec Question
 * - Plusieurs options peuvent appartenir à la même question
 * - FetchType.LAZY pour optimiser les performances
 * - nullable=false : une option doit obligatoirement être liée à une question
 * 
 * SÉCURITÉ : Le champ isCorrect est protégé par les DTOs
 * - OptionCandidatDTO n'expose PAS ce champ (sécurité)
 * - OptionRecruteurDTO expose ce champ (pour le recruteur)
 * - Cela empêche les candidats de voir les réponses correctes dans le frontend
 */
@Entity
@Table(name = "options_reponse")
public class OptionReponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Texte de l'option de réponse
     * Ex: "Java", "Python", "JavaScript", "C++"
     */
    @Column(nullable = false)
    private String texte;

    /**
     * Indique si cette option est la réponse correcte
     * IMPORTANT : Ce champ ne doit JAMAIS être envoyé aux candidats (voir DTOs)
     * Utilisé uniquement par le backend pour calculer le score
     */
    @Column(name = "is_correct")
    private boolean isCorrect;

    /**
     * Relation ManyToOne avec Question
     * Une option appartient obligatoirement à une question
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnore
    private Question question;

    public OptionReponse() {
    }

    /**
     * Constructeur avec paramètres
     * @param texte Texte de l'option
     * @param isCorrect Indique si c'est la réponse correcte
     */
    public OptionReponse(String texte, boolean isCorrect) {
        this.texte = texte;
        this.isCorrect = isCorrect;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTexte() {
        return texte;
    }

    public void setTexte(String texte) {
        this.texte = texte;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }
}
