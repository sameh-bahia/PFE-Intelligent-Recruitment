package com.AppRecrutement.AppRecrutement.dto;

import java.util.List;

/**
 * DTO QuestionRecruteurDTO - Version complète pour les recruteurs
 * 
 * Ce DTO représente une question envoyée aux recruteurs avec ses options.
 * Il utilise OptionRecruteurDTO (avec isCorrect) pour permettre aux recruteurs
 * de gérer les quiz (création, modification, consultation).
 * 
 * SÉCURITÉ : Composition avec OptionRecruteurDTO
 * - Les options sont de type OptionRecruteurDTO (avec isCorrect)
 * - Ce DTO ne doit JAMAIS être envoyé aux candidats
 * - Voir QuizService.getQuizForRecruteur() pour l'utilisation correcte
 * 
 * CHOIX TECHNIQUE : Séparation des DTOs par rôle
 * - QuestionRecruteurDTO : pour les recruteurs (options avec isCorrect)
 * - QuestionCandidatDTO : pour les candidats (options sans isCorrect)
 * - Pattern de sécurité par composition de DTOs selon le rôle
 */
public class QuestionRecruteurDTO {
    private Long id;
    private String enonce;
    private Integer points;
    private List<OptionRecruteurDTO> options;

    public QuestionRecruteurDTO() {
    }

    public QuestionRecruteurDTO(Long id, String enonce, Integer points, List<OptionRecruteurDTO> options) {
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

    public List<OptionRecruteurDTO> getOptions() {
        return options;
    }

    public void setOptions(List<OptionRecruteurDTO> options) {
        this.options = options;
    }
}
