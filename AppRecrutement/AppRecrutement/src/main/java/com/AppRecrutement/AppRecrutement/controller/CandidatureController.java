package com.AppRecrutement.AppRecrutement.controller;

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

import java.util.List;
import java.util.Map;

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
    public List<Candidature> getMesCandidatures(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        return candidatureService.findByCandidatId(candidat.getId());
    }

    /**
     * Récupère les candidatures reçues par le recruteur connecté.
     * Utilise l'Authentication pour obtenir l'email du recruteur via le token JWT.
     * @param authentication Authentification JWT du recruteur connecté
     * @return Liste des candidatures pour les offres du recruteur
     */
    @GetMapping("/recruteur/candidatures-recues")
    public List<Candidature> getCandidaturesRecues(Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        return candidatureService.findByRecruteurId(recruteur.getId());
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

        return ResponseEntity.ok(candidatureService.save(candidature));
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
    public ResponseEntity<Candidature> updateStatut(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        if (candidatureService.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Candidature candidature = candidatureService.findById(id).get();
        String statutStr = payload.get("statut");
        if (statutStr != null) {
            candidature.setStatut(StatutCandidature.valueOf(statutStr));
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
