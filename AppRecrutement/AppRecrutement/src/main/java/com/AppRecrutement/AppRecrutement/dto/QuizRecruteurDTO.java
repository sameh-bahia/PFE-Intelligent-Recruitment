package com.AppRecrutement.AppRecrutement.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO QuizRecruteurDTO - Version complète pour les recruteurs
 * 
 * Ce DTO représente un quiz complet envoyé aux recruteurs.
 * Il utilise QuestionRecruteurDTO (qui utilise OptionRecruteurDTO) pour permettre
 * aux recruteurs de voir et gérer toutes les informations du quiz.
 * 
 * SÉCURITÉ : Composition avec QuestionRecruteurDTO
 * - Les questions sont de type QuestionRecruteurDTO (options avec isCorrect)
 * - Ce DTO ne doit JAMAIS être envoyé aux candidats
 * - Voir QuizService.getQuizForRecruteur() pour l'utilisation correcte
 * 
 * CHOIX TECHNIQUE : Séparation des DTOs par rôle
 * - QuizRecruteurDTO : pour les recruteurs (questions avec isCorrect)
 * - QuizCandidatDTO : pour les candidats (questions sans isCorrect)
 * - Pattern de sécurité par composition en cascade selon le rôle
 */
public class QuizRecruteurDTO {
    private Long id;
    private String titre;
    private Integer dureeMinutes;
    private LocalDateTime dateCreation;
    private List<QuestionRecruteurDTO> questions;

    public QuizRecruteurDTO() {
    }

    public QuizRecruteurDTO(Long id, String titre, Integer dureeMinutes, LocalDateTime dateCreation, List<QuestionRecruteurDTO> questions) {
        this.id = id;
        this.titre = titre;
        this.dureeMinutes = dureeMinutes;
        this.dateCreation = dateCreation;
        this.questions = questions;
    }

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

    public List<QuestionRecruteurDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionRecruteurDTO> questions) {
        this.questions = questions;
    }
}
