package com.AppRecrutement.AppRecrutement.dto;

import java.util.Map;

/**
 * DTO ReponseQuizDTO - Représente les réponses d'un candidat à un quiz
 * 
 * Ce DTO est utilisé pour recevoir les réponses du candidat lors de la soumission.
 * Il contient l'ID de la candidature, les réponses (questionId -> optionId) et
 * le temps écoulé pour la validation anti-triche.
 * 
 * LOGIQUE MÉTIER : Calcul du score
 * - Le backend utilise ce DTO pour calculer le score en comparant les réponses
 *   avec les options correctes stockées en base de données
 * - Le calcul se fait côté backend pour éviter la triche
 * 
 * SÉCURITÉ : Validation du temps
 * - Le champ "tempsEcouleSecondes" est utilisé pour valider que le candidat
 *   n'a pas dépassé le temps autorisé (+30s de tolérance)
 * - Voir QuizService.soumettreQuiz() pour la logique de validation
 * 
 * CHOIX TECHNIQUE : Map pour les réponses
 * - Map<Long, Long> : QuestionId -> OptionId
 * - Permet une association simple et efficace entre questions et réponses
 * - Facilite le calcul du score par itération
 */
public class ReponseQuizDTO {
    private Long candidatureId;
    private Map<Long, Long> reponses; // QuestionId -> OptionId
    private Long tempsEcouleSecondes;

    public ReponseQuizDTO() {
    }

    public ReponseQuizDTO(Long candidatureId, Map<Long, Long> reponses, Long tempsEcouleSecondes) {
        this.candidatureId = candidatureId;
        this.reponses = reponses;
        this.tempsEcouleSecondes = tempsEcouleSecondes;
    }

    public Long getCandidatureId() {
        return candidatureId;
    }

    public void setCandidatureId(Long candidatureId) {
        this.candidatureId = candidatureId;
    }

    public Map<Long, Long> getReponses() {
        return reponses;
    }

    public void setReponses(Map<Long, Long> reponses) {
        this.reponses = reponses;
    }

    public Long getTempsEcouleSecondes() {
        return tempsEcouleSecondes;
    }

    public void setTempsEcouleSecondes(Long tempsEcouleSecondes) {
        this.tempsEcouleSecondes = tempsEcouleSecondes;
    }
}
