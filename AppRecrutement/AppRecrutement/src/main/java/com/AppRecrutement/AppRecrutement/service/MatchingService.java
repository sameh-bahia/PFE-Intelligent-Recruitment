package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.dto.MatchingResultDTO;
import com.AppRecrutement.AppRecrutement.model.*;
import com.AppRecrutement.AppRecrutement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    /**
     * Calcule le score de matching entre un candidat et une offre
     * Utilise l'algorithme de Jaccard : |A ∩ B| / |B|
     * où A = compétences du candidat, B = compétences requises
     * 
     * @param candidatId ID du candidat
     * @param offreId ID de l'offre
     * @return Score entre 0 et 1 (0-100%)
     */
    public double calculateMatchingScore(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId).orElse(null);
        Offre offre = offreRepository.findById(offreId).orElse(null);

        if (candidat == null || offre == null) {
            System.out.println("[DEBUG] calculateMatchingScore: Candidat ou Offre null");
            return 0.0;
        }

        // Récupérer les compétences du candidat via son CV
        Set<String> candidatCompetences = getCandidatCompetences(candidat);
        System.out.println("[DEBUG] Candidat competences: " + candidatCompetences);
        
        // Récupérer les compétences requises de l'offre
        Set<String> offreCompetences = getOffreCompetences(offre);
        System.out.println("[DEBUG] Offre competences: " + offreCompetences);

        // Si l'offre n'a pas de compétences requises, score = 100%
        if (offreCompetences.isEmpty()) {
            System.out.println("[DEBUG] Offre n'a pas de compétences, score = 100%");
            return 1.0;
        }

        // Si le candidat n'a pas de compétences, score = 0%
        if (candidatCompetences.isEmpty()) {
            System.out.println("[DEBUG] Candidat n'a pas de compétences, score = 0%");
            return 0.0;
        }

        // Calculer l'intersection
        Set<String> intersection = new HashSet<>(candidatCompetences);
        intersection.retainAll(offreCompetences);
        System.out.println("[DEBUG] Intersection: " + intersection);

        // Score Jaccard : |A ∩ B| / |B|
        double score = (double) intersection.size() / offreCompetences.size();
        System.out.println("[DEBUG] Score calculé: " + score + " (" + (score * 100) + "%)");

        return score;
    }

    /**
     * Récupère les compétences d'un candidat via son CV
     */
    private Set<String> getCandidatCompetences(Candidat candidat) {
        Set<String> competences = new HashSet<>();
        
        if (candidat.getCv() != null && candidat.getCv().getCompetences() != null) {
            competences = candidat.getCv().getCompetences().stream()
                    .map(Competence::getNom)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }
        
        return competences;
    }

    /**
     * Récupère les compétences requises d'une offre
     */
    private Set<String> getOffreCompetences(Offre offre) {
        Set<String> competences = new HashSet<>();
        
        if (offre.getCompetences() != null) {
            competences = offre.getCompetences().stream()
                    .map(Competence::getNom)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        }
        
        return competences;
    }

    /**
     * Identifie les compétences manquantes du candidat pour atteindre 100%
     */
    public Set<String> getMissingCompetences(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId).orElse(null);
        Offre offre = offreRepository.findById(offreId).orElse(null);

        if (candidat == null || offre == null) {
            return new HashSet<>();
        }

        Set<String> candidatCompetences = getCandidatCompetences(candidat);
        Set<String> offreCompetences = getOffreCompetences(offre);

        // Compétences requises mais non possédées par le candidat
        Set<String> missing = new HashSet<>(offreCompetences);
        missing.removeAll(candidatCompetences);

        return missing;
    }

    /**
     * Met à jour le score de compatibilité d'une candidature
     */
    public void updateCandidatureScore(Long candidatureId) {
        Candidature candidature = candidatureRepository.findById(candidatureId).orElse(null);
        
        if (candidature != null) {
            double score = calculateMatchingScore(
                    candidature.getCandidat().getId(),
                    candidature.getOffre().getId()
            );
            candidature.setScoreCompatibilite(score);
            candidatureRepository.save(candidature);
        }
    }

    /**
     * Calcule le résultat complet du matching (score, compétences communes, compétences manquantes)
     */
    public MatchingResultDTO calculateMatchingResult(Long candidatId, Long offreId) {
        Candidat candidat = candidatRepository.findById(candidatId).orElse(null);
        Offre offre = offreRepository.findById(offreId).orElse(null);

        if (candidat == null || offre == null) {
            return new MatchingResultDTO(0.0, new HashSet<>(), new HashSet<>());
        }

        Set<String> candidatCompetences = getCandidatCompetences(candidat);
        Set<String> offreCompetences = getOffreCompetences(offre);

        // Si l'offre n'a pas de compétences requises, score = 100%
        if (offreCompetences.isEmpty()) {
            return new MatchingResultDTO(1.0, new HashSet<>(), new HashSet<>());
        }

        // Si le candidat n'a pas de compétences, score = 0%
        if (candidatCompetences.isEmpty()) {
            return new MatchingResultDTO(0.0, new HashSet<>(), offreCompetences);
        }

        // Calculer l'intersection
        Set<String> common = new HashSet<>(candidatCompetences);
        common.retainAll(offreCompetences);

        // Compétences manquantes
        Set<String> missing = new HashSet<>(offreCompetences);
        missing.removeAll(candidatCompetences);

        // Score Jaccard : |A ∩ B| / |B|
        double score = (double) common.size() / offreCompetences.size();
        
        return new MatchingResultDTO(score, common, missing);
    }
}
