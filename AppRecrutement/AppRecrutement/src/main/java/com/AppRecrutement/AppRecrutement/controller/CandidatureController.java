package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.dto.CandidatSimpleDTO;
import com.AppRecrutement.AppRecrutement.dto.CandidatureDTO;
import com.AppRecrutement.AppRecrutement.dto.MatchingResultDTO;
import com.AppRecrutement.AppRecrutement.dto.OffreSimpleDTO;
import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Candidature;
import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.model.StatutCandidature;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.service.CandidatureService;
import com.AppRecrutement.AppRecrutement.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour gérer les candidatures aux offres d'emploi.
 * Fournit les endpoints CRUD pour les candidatures, les candidatures du candidat connecté,
 * et les candidatures reçues par le recruteur connecté.
 */
@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    @Autowired
    private CandidatureService candidatureService;

    @Autowired
    private OffreService offreService;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private com.AppRecrutement.AppRecrutement.service.MatchingService matchingService;

    @Autowired
    private com.AppRecrutement.AppRecrutement.service.MailService mailService;

    /**
     * Récupère toutes les candidatures.
     * @return Liste de toutes les candidatures
     */
    @GetMapping
    public List<Candidature> getAllCandidatures() {
        return candidatureService.findAll();
    }

    /**
     * Récupère les candidatures du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return Liste des candidatures du candidat
     */
    @GetMapping("/mes-candidatures")
    public List<CandidatureDTO> getMesCandidatures(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        List<Candidature> candidatures = candidatureService.findByCandidatId(candidat.getId());
        return candidatures.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private CandidatureDTO convertToDTO(Candidature candidature) {
        CandidatureDTO dto = new CandidatureDTO();
        dto.setId(candidature.getId());
        dto.setDatePostulation(candidature.getDatePostulation());
        dto.setLettreMotivation(candidature.getLettreMotivation());
        dto.setScoreCompatibilite(candidature.getScoreCompatibilite());
        System.out.println("=== DEBUG CONVERT TO DTO ===");
        System.out.println("Candidature ID: " + candidature.getId());
        System.out.println("Score from entity: " + candidature.getScoreCompatibilite());
        System.out.println("Score in DTO: " + dto.getScoreCompatibilite());
        dto.setStatut(candidature.getStatut() != null ? candidature.getStatut().name() : null);

        if (candidature.getOffre() != null) {
            OffreSimpleDTO offreDTO = new OffreSimpleDTO();
            offreDTO.setId(candidature.getOffre().getId());
            offreDTO.setTitre(candidature.getOffre().getTitre());
            offreDTO.setDescription(candidature.getOffre().getDescription());
            offreDTO.setLieu(candidature.getOffre().getLieu());
            offreDTO.setSalaire(candidature.getOffre().getSalaire());
            offreDTO.setDomaine(candidature.getOffre().getDomaine());
            offreDTO.setTypeOffre(candidature.getOffre().getTypeOffre() != null ? candidature.getOffre().getTypeOffre().name() : null);
            offreDTO.setSousDomaineIT(candidature.getOffre().getSousDomaineIT() != null ? candidature.getOffre().getSousDomaineIT().name() : null);
            offreDTO.setNiveauEtudeRequis(candidature.getOffre().getNiveauEtudeRequis() != null ? candidature.getOffre().getNiveauEtudeRequis().name() : null);
            if (candidature.getOffre().getRecruteur() != null) {
                offreDTO.setNomEntreprise(candidature.getOffre().getRecruteur().getNomEntreprise());
                offreDTO.setPosteRecruteur(candidature.getOffre().getRecruteur().getPoste());
            }
            dto.setOffre(offreDTO);
        }

        if (candidature.getCandidat() != null) {
            CandidatSimpleDTO candidatDTO = new CandidatSimpleDTO();
            candidatDTO.setId(candidature.getCandidat().getId());
            candidatDTO.setNom(candidature.getCandidat().getNom());
            candidatDTO.setPrenom(candidature.getCandidat().getPrenom());
            candidatDTO.setEmail(candidature.getCandidat().getEmail());
            candidatDTO.setTitreProfil(candidature.getCandidat().getTitreProfil());
            if (candidature.getCandidat().getCv() != null) {
                candidatDTO.setCvId(candidature.getCandidat().getCv().getId());
            }
            dto.setCandidat(candidatDTO);
        }

        return dto;
    }

    /**
     * Calcule les scores relatifs pour les candidatures du recruteur.
     * Le score relatif est normalisé par offre: le meilleur candidat pour chaque offre a 100%.
     * @param candidatures Liste des candidatures
     * @return Liste des DTOs avec scores relatifs calculés
     */
    private List<CandidatureDTO> calculateRelativeScores(List<Candidature> candidatures) {
        // Grouper les candidatures par offre
        Map<Long, List<Candidature>> candidaturesParOffre = candidatures.stream()
                .collect(Collectors.groupingBy(c -> c.getOffre().getId()));

        // Pour chaque offre, trouver le score maximum et calculer les scores relatifs
        Map<Long, Double> maxScoresParOffre = new HashMap<>();
        for (Map.Entry<Long, List<Candidature>> entry : candidaturesParOffre.entrySet()) {
            Double maxScore = entry.getValue().stream()
                    .map(c -> c.getScoreCompatibilite() != null ? c.getScoreCompatibilite() : 0.0)
                    .max(Double::compareTo)
                    .orElse(0.0);
            maxScoresParOffre.put(entry.getKey(), maxScore);
            System.out.println("=== DEBUG RELATIVE SCORE ===");
            System.out.println("Offre ID: " + entry.getKey() + ", Max Score: " + maxScore);
        }

        // Convertir en DTOs et calculer les scores relatifs
        List<CandidatureDTO> dtos = new ArrayList<>();
        for (Candidature candidature : candidatures) {
            CandidatureDTO dto = convertToDTO(candidature);
            Long offreId = candidature.getOffre().getId();
            Double maxScore = maxScoresParOffre.get(offreId);
            
            if (maxScore != null && maxScore > 0) {
                Double scoreAbsolu = candidature.getScoreCompatibilite() != null ? candidature.getScoreCompatibilite() : 0.0;
                Double scoreRelatif = (scoreAbsolu / maxScore);
                dto.setScoreRelatif(scoreRelatif);
                System.out.println("Candidature ID: " + candidature.getId() + 
                                 ", Score Absolu: " + scoreAbsolu + 
                                 ", Score Relatif: " + scoreRelatif);
            } else {
                dto.setScoreRelatif(0.0);
            }
            
            dtos.add(dto);
        }

        return dtos;
    }

    /**
     * Récupère les candidatures reçues par le recruteur connecté, triées par score de compatibilité.
     * Utilise l'Authentication pour obtenir l'email du recruteur via le token JWT.
     * @param authentication Authentification JWT du recruteur connecté
     * @return Liste des candidatures pour les offres du recruteur, triées par score décroissant
     */
    @GetMapping("/recruteur/candidatures-recues/triees")
    public List<CandidatureDTO> getCandidaturesRecuesTriees(Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        List<Candidature> candidatures = candidatureService.findByRecruteurId(recruteur.getId());

        // Calculer les scores relatifs par offre
        List<CandidatureDTO> dtos = calculateRelativeScores(candidatures);

        // Trier par score de compatibilité décroissant
        dtos.sort((d1, d2) -> {
            Double score1 = d1.getScoreCompatibilite() != null ? d1.getScoreCompatibilite() : 0.0;
            Double score2 = d2.getScoreCompatibilite() != null ? d2.getScoreCompatibilite() : 0.0;
            return score2.compareTo(score1);
        });

        return dtos;
    }

    /**
     * Récupère les candidatures reçues par le recruteur connecté.
     * Utilise l'Authentication pour obtenir l'email du recruteur via le token JWT.
     * @param authentication Authentification JWT du recruteur connecté
     * @return Liste des candidatures pour les offres du recruteur
     */
    @GetMapping("/recruteur/candidatures-recues")
    public List<CandidatureDTO> getCandidaturesRecues(Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        List<Candidature> candidatures = candidatureService.findByRecruteurId(recruteur.getId());
        return calculateRelativeScores(candidatures);
    }

    /**
     * Récupère une candidature par son ID.
     * @param id Identifiant de la candidature
     * @return Candidature trouvée ou 404 si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable Long id) {
        return candidatureService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Calcule le score de matching entre le candidat connecté et une offre
     * Permet au candidat de voir son score avant de postuler
     * @param offreId ID de l'offre
     * @param authentication Authentification JWT du candidat connecté
     * @return Résultat du matching (score, compétences communes, compétences manquantes)
     */
    @GetMapping("/calculate-score/{offreId}")
    public ResponseEntity<?> calculateScore(@PathVariable Long offreId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            throw new RuntimeException("Candidat non trouvé");
        }

        // Vérifier si le candidat a un CV
        if (candidat.getCv() == null) {
            return ResponseEntity.badRequest().body("Vous devez d'abord créer votre CV avant de voir votre score de matching.");
        }

        offreService.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));

        MatchingResultDTO result = matchingService.calculateMatchingResult(candidat.getId(), offreId);
        return ResponseEntity.ok(result);
    }

    /**
     * Crée une nouvelle candidature pour une offre.
     * La candidature est automatiquement associée au candidat connecté.
     * @param payload Map contenant lettreMotivation et offreId
     * @param authentication Authentification JWT du candidat connecté
     * @return Candidature créée
     */
    @PostMapping
    public ResponseEntity<Candidature> createCandidature(@RequestBody Map<String, Object> payload, Authentication authentication) {
        Candidature candidature = new Candidature();
        candidature.setLettreMotivation((String) payload.get("lettreMotivation"));
        candidature.setStatut(StatutCandidature.EN_ATTENTE);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            throw new RuntimeException("Candidat non trouvé");
        }
        candidature.setCandidat(candidat);

        Object offreIdObj = payload.get("offreId");
        Long offreId;
        if (offreIdObj instanceof String) {
            offreId = Long.parseLong((String) offreIdObj);
        } else if (offreIdObj instanceof Number) {
            offreId = ((Number) offreIdObj).longValue();
        } else {
            throw new RuntimeException("offreId must be a number or string");
        }
        Offre offre = offreService.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));
        candidature.setOffre(offre);

        // Calculer automatiquement le score de matching
        System.out.println("=== DEBUG CREATE CANDIDATURE ===");
        System.out.println("Candidat ID: " + candidat.getId());
        System.out.println("Offre ID: " + offreId);
        System.out.println("Candidat CV: " + (candidat.getCv() != null ? "Present" : "NULL"));
        
        double score = matchingService.calculateMatchingScore(candidat.getId(), offreId);
        System.out.println("Score calculé: " + score);
        
        candidature.setScoreCompatibilite(score);
        System.out.println("Score set in candidature: " + candidature.getScoreCompatibilite());

        Candidature savedCandidature = candidatureService.save(candidature);
        System.out.println("Score after save: " + savedCandidature.getScoreCompatibilite());

        return ResponseEntity.ok(savedCandidature);
    }

    /**
     * Met à jour une candidature existante.
     * @param id Identifiant de la candidature à mettre à jour
     * @param candidature Nouvelles données de la candidature
     * @return Candidature mise à jour ou 404 si non trouvée
     */
    @PutMapping("/{id}")
    public ResponseEntity<Candidature> updateCandidature(@PathVariable Long id, @RequestBody Candidature candidature) {
        if (candidatureService.findById(id).isPresent()) {
            candidature.setId(id);
            return ResponseEntity.ok(candidatureService.save(candidature));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Met à jour uniquement le statut d'une candidature.
     * Permet au recruteur d'accepter ou refuser une candidature.
     * @param id Identifiant de la candidature
     * @param payload Map contenant le nouveau statut
     * @return Candidature mise à jour ou 404 si non trouvée
     */
    @PutMapping("/{id}/statut")
    public ResponseEntity<Candidature> updateStatut(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        if (candidatureService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Candidature candidature = candidatureService.findById(id).get();
        String statutStr = (String) payload.get("statut");
        if (statutStr != null) {
            candidature.setStatut(StatutCandidature.valueOf(statutStr));
            
            // Si accepté avec détails d'entretien
            if (statutStr.equals("ACCEPTEE") && payload.containsKey("dateEntretien")) {
                String dateEntretienStr = (String) payload.get("dateEntretien");
                String typeEntretien = (String) payload.get("typeEntretien");
                String lienEntretien = (String) payload.get("lienEntretien");
                
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    Date dateEntretien = sdf.parse(dateEntretienStr);
                    candidature.setDateEntretien(dateEntretien);
                    candidature.setTypeEntretien(typeEntretien);
                    candidature.setLienEntretien(lienEntretien);
                    
                    // Envoyer l'email de convocation
                    if (candidature.getCandidat() != null && candidature.getOffre() != null) {
                        String nomEntreprise = candidature.getOffre().getRecruteur() != null 
                            ? candidature.getOffre().getRecruteur().getNomEntreprise() 
                            : "Notre entreprise";
                        
                        mailService.sendInterviewInvitation(
                            candidature.getCandidat().getEmail(),
                            candidature.getCandidat().getNom(),
                            candidature.getOffre().getTitre(),
                            dateEntretien,
                            typeEntretien,
                            lienEntretien,
                            nomEntreprise
                        );
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(candidatureService.save(candidature));
        }
        return ResponseEntity.badRequest().build();
    }

    /**
     * Supprime une candidature par son ID.
     * @param id Identifiant de la candidature à supprimer
     * @return 204 si supprimée, 404 si non trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable Long id) {
        if (candidatureService.findById(id).isPresent()) {
            candidatureService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
