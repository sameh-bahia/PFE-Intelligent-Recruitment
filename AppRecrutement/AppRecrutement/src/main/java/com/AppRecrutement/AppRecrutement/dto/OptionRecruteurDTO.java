package com.AppRecrutement.AppRecrutement.dto;

/**
 * DTO OptionRecruteurDTO - Version complète pour les recruteurs
 * 
 * Ce DTO est utilisé pour envoyer les options de réponse aux recruteurs AVEC
 * l'information sur quelle option est correcte. Les recruteurs ont besoin de
 * voir cette information pour créer et modifier les quiz.
 * 
 * SÉCURITÉ : Ce DTO ne doit JAMAIS être envoyé aux candidats
 * - Le champ "isCorrect" est présent car les recruteurs sont autorisés à le voir
 * - Le backend doit s'assurer que ce DTO n'est utilisé que pour les recruteurs
 * - Voir QuizService.getQuizForRecruteur() pour l'utilisation correcte
 * 
 * CHOIX TECHNIQUE : Séparation des DTOs par rôle
 * - OptionRecruteurDTO : pour les recruteurs (avec isCorrect)
 * - OptionCandidatDTO : pour les candidats (sans isCorrect)
 * - Pattern de sécurité par séparation des données selon le rôle
 */
public class OptionRecruteurDTO {
    private Long id;
    private String texte;
    private boolean isCorrect;

    public OptionRecruteurDTO() {
    }

    public OptionRecruteurDTO(Long id, String texte, boolean isCorrect) {
        this.id = id;
        this.texte = texte;
        this.isCorrect = isCorrect;
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

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }
}
