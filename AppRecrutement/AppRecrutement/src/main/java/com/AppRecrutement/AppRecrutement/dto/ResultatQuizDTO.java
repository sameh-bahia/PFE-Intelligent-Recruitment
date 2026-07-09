package com.AppRecrutement.AppRecrutement.dto;

/**
 * DTO ResultatQuizDTO - Représente le résultat d'un quiz
 * 
 * Ce DTO est utilisé pour renvoyer le score obtenu par un candidat après
 * la soumission d'un quiz. Il contient le score obtenu, le score total et
 * le pourcentage de réussite.
 * 
 * LOGIQUE MÉTIER : Calcul du pourcentage
 * - Le pourcentage est calculé automatiquement dans le constructeur
 * - Formule : (scoreObtenu / scoreTotal) * 100
 * - Évite les divisions par zéro avec une vérification
 * 
 * CHOIX TECHNIQUE : Calcul dans le constructeur
 * - Le pourcentage est calculé une seule fois à la création
 * - Évite les erreurs de calcul répétitif
 * - Garantit la cohérence des données
 * 
 * UTILISATION : Affichage frontend
 * - Le frontend utilise ce DTO pour afficher les résultats au candidat
 * - Le backend stocke également le score dans l'entité Candidature
 */
public class ResultatQuizDTO {
    private Integer scoreObtenu;
    private Integer scoreTotal;
    private Double pourcentage;

    public ResultatQuizDTO() {
    }

    public ResultatQuizDTO(Integer scoreObtenu, Integer scoreTotal) {
        this.scoreObtenu = scoreObtenu;
        this.scoreTotal = scoreTotal;
        this.pourcentage = scoreTotal > 0 ? (scoreObtenu * 100.0 / scoreTotal) : 0.0;
    }

    public Integer getScoreObtenu() {
        return scoreObtenu;
    }

    public void setScoreObtenu(Integer scoreObtenu) {
        this.scoreObtenu = scoreObtenu;
    }

    public Integer getScoreTotal() {
        return scoreTotal;
    }

    public void setScoreTotal(Integer scoreTotal) {
        this.scoreTotal = scoreTotal;
    }

    public Double getPourcentage() {
        return pourcentage;
    }

    public void setPourcentage(Double pourcentage) {
        this.pourcentage = pourcentage;
    }
}
