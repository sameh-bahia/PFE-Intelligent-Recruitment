package com.AppRecrutement.AppRecrutement.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO QuizCandidatDTO - Version sécurisée pour les candidats
 * 
 * Ce DTO représente un quiz complet envoyé aux candidats.
 * Il utilise QuestionCandidatDTO (qui utilise OptionCandidatDTO) pour garantir
 * que les candidats ne voient JAMAIS les réponses correctes.
 * 
 * SÉCURITÉ : Composition avec QuestionCandidatDTO
 * - Les questions sont de type QuestionCandidatDTO (options sans isCorrect)
 * - Cela garantit une sécurité en cascade : Quiz -> Question -> Option
 * - Le champ "dureeMinutes" est visible pour le timer frontend
 * 
 * CHOIX TECHNIQUE : Séparation des DTOs par rôle
 * - QuizCandidatDTO : pour les candidats (questions sans isCorrect)
 * - QuizRecruteurDTO : pour les recruteurs (questions avec isCorrect)
 * - Pattern de sécurité par composition en cascade
 */
public class QuizCandidatDTO {
    private Long id;
    private String titre;
    private Integer dureeMinutes;
    private LocalDateTime dateCreation;
    private List<QuestionCandidatDTO> questions;

    public QuizCandidatDTO() {
    }

    public QuizCandidatDTO(Long id, String titre, Integer dureeMinutes, LocalDateTime dateCreation, List<QuestionCandidatDTO> questions) {
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

    public List<QuestionCandidatDTO> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuestionCandidatDTO> questions) {
        this.questions = questions;
    }
}
