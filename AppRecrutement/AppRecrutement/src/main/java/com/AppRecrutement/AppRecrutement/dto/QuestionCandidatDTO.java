package com.AppRecrutement.AppRecrutement.dto;

import java.util.List;

/**
 * DTO QuestionCandidatDTO - Version sécurisée pour les candidats
 * 
 * Ce DTO représente une question envoyée aux candidats avec ses options.
 * Il utilise OptionCandidatDTO (sans isCorrect) pour garantir la sécurité.
 * 
 * SÉCURITÉ : Composition avec OptionCandidatDTO
 * - Les options sont de type OptionCandidatDTO (sans isCorrect)
 - Cela garantit que même indirectement, les candidats ne voient pas les réponses correctes
 * - Le champ "points" est visible pour informer le candidat de l'importance de la question
 * 
 * CHOIX TECHNIQUE : Séparation des DTOs par rôle
 * - QuestionCandidatDTO : pour les candidats (options sans isCorrect)
 * - QuestionRecruteurDTO : pour les recruteurs (options avec isCorrect)
 * - Pattern de sécurité par composition de DTOs sécurisés
 */
public class QuestionCandidatDTO {
    private Long id;
    private String enonce;
    private Integer points;
    private List<OptionCandidatDTO> options;

    public QuestionCandidatDTO() {
    }

    public QuestionCandidatDTO(Long id, String enonce, Integer points, List<OptionCandidatDTO> options) {
        this.id = id;
        this.enonce = enonce;
        this.points = points;
        this.options = options;
    }

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

    public List<OptionCandidatDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionCandidatDTO> options) {
        this.options = options;
    }
}
