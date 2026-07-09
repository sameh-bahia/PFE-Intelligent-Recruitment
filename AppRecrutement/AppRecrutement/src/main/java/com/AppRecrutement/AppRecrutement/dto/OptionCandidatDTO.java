package com.AppRecrutement.AppRecrutement.dto;

/**
 * DTO OptionCandidatDTO - Version sécurisée pour les candidats
 * 
 * Ce DTO est utilisé pour envoyer les options de réponse aux candidats SANS révéler
 * quelle option est correcte. C'est CRUCIAL pour la sécurité du système de quiz.
 * 
 * SÉCURITÉ : Le champ "isCorrect" est INTENTIONNELLEMENT ABSENT
 * - Empêche les candidats de voir les réponses correctes dans le frontend
 * - Le calcul du score se fait uniquement côté backend (QuizService)
 * - Même si un candidat inspecte le réseau/DevTools, il ne voit pas isCorrect
 * 
 * CHOIX TECHNIQUE : Séparation des DTOs par rôle
 * - OptionCandidatDTO : pour les candidats (sans isCorrect)
 * - OptionRecruteurDTO : pour les recruteurs (avec isCorrect)
 * - Pattern de sécurité par obscurcissement de données sensibles
 */
public class OptionCandidatDTO {
    private Long id;
    private String texte;

    public OptionCandidatDTO() {
    }

    public OptionCandidatDTO(Long id, String texte) {
        this.id = id;
        this.texte = texte;
    }

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
}
