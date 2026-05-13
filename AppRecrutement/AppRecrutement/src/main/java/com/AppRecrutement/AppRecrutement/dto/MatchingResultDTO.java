package com.AppRecrutement.AppRecrutement.dto;

import java.util.Set;

public class MatchingResultDTO {
    private double score;  // Score entre 0 et 1 (0-100%)
    private int scorePercentage;  // Score en pourcentage (0-100)
    private Set<String> commonCompetences;  // Compétences communes
    private Set<String> missingCompetences;  // Compétences manquantes pour atteindre 100%

    public MatchingResultDTO() {
    }

    public MatchingResultDTO(double score, Set<String> commonCompetences, Set<String> missingCompetences) {
        this.score = score;
        this.scorePercentage = (int) (score * 100);
        this.commonCompetences = commonCompetences;
        this.missingCompetences = missingCompetences;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getScorePercentage() {
        return scorePercentage;
    }

    public void setScorePercentage(int scorePercentage) {
        this.scorePercentage = scorePercentage;
    }

    public Set<String> getCommonCompetences() {
        return commonCompetences;
    }

    public void setCommonCompetences(Set<String> commonCompetences) {
        this.commonCompetences = commonCompetences;
    }

    public Set<String> getMissingCompetences() {
        return missingCompetences;
    }

    public void setMissingCompetences(Set<String> missingCompetences) {
        this.missingCompetences = missingCompetences;
    }
}
