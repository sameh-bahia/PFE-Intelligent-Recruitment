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

        // ============================================================
        // VÉRIFICATION DU TYPE D'OFFRE ET NIVEAU D'ÉTUDE
        // ============================================================
        if (!isEligibleForOffre(candidat, offre)) {
            System.out.println("[DEBUG] Candidat non éligible pour cette offre (type/niveau)");
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

        // Calculer l'intersection avec correspondance partielle
        Set<String> intersection = new HashSet<>();
        for (String offreComp : offreCompetences) {
            for (String candidatComp : candidatCompetences) {
                // Normaliser les chaînes (lowercase + trim) pour ignorer la casse
                String offreCompNorm = offreComp.toLowerCase().trim();
                String candidatCompNorm = candidatComp.toLowerCase().trim();
                
                // Vérifier si la compétence de l'offre est contenue dans celle du candidat
                // ou si la compétence du candidat contient celle de l'offre
                if (candidatCompNorm.contains(offreCompNorm) || offreCompNorm.contains(candidatCompNorm)) {
                    intersection.add(offreComp);
                    System.out.println("[DEBUG] Correspondance trouvée: '" + offreComp + "' dans '" + candidatComp + "'");
                    break;
                }
            }
        }
        System.out.println("[DEBUG] Intersection: " + intersection);

        // Score Jaccard : |A ∩ B| / |B|
        double score = (double) intersection.size() / offreCompetences.size();
        System.out.println("[DEBUG] Score calculé: " + score + " (" + (score * 100) + "%)");

        return score;
    }

    /**
     * Vérifie si un candidat est éligible pour une offre selon le type et le niveau d'étude
     */
    private boolean isEligibleForOffre(Candidat candidat, Offre offre) {
        TypeOffre typeOffre = offre.getTypeOffre();
        NiveauEtude niveauRequis = offre.getNiveauEtudeRequis();
        NiveauEtude niveauCandidat = candidat.getNiveauEtude();

        System.out.println("[DEBUG] Type offre: " + typeOffre + ", Niveau requis: " + niveauRequis + ", Niveau candidat: " + niveauCandidat);

        // Si l'offre n'a pas d'exigence de niveau, tous les candidats sont éligibles
        if (niveauRequis == NiveauEtude.SANS_EXIGENCE) {
            return true;
        }

        // Si le candidat n'a pas de niveau d'étude défini, on permet quand même le calcul basé sur les compétences
        if (niveauCandidat == null) {
            System.out.println("[DEBUG] Candidat sans niveau d'étude défini - calcul basé sur les compétences uniquement");
            return true;
        }

        // Ordre des niveaux (du plus bas au plus élevé)
        List<NiveauEtude> niveaux = Arrays.asList(
            NiveauEtude.BAC,
            NiveauEtude.DUT_BTS,
            NiveauEtude.LICENCE,
            NiveauEtude.MASTER,
            NiveauEtude.INGENIEUR,
            NiveauEtude.DOCTORAT
        );

        int indexCandidat = niveaux.indexOf(niveauCandidat);
        int indexRequis = niveaux.indexOf(niveauRequis);

        // Si le niveau n'est pas dans la liste, on considère comme non éligible
        if (indexCandidat == -1 || indexRequis == -1) {
            return false;
        }

        // Pour STAGE et ALTERNANCE: le candidat doit avoir un niveau inférieur ou égal
        if (typeOffre == TypeOffre.STAGE || typeOffre == TypeOffre.ALTERNANCE) {
            return indexCandidat <= indexRequis;
        }

        // Pour EMPLOI et FREELANCE: le candidat doit avoir un niveau supérieur ou égal
        if (typeOffre == TypeOffre.EMPLOI || typeOffre == TypeOffre.FREELANCE) {
            return indexCandidat >= indexRequis;
        }

        return true;
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
        
        System.out.println("[DEBUG] Candidat compétences (brutes): " + competences);
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
