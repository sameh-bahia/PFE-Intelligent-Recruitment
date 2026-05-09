package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les offres d'emploi.
 * Fournit les endpoints CRUD pour les offres et un endpoint pour récupérer les offres du recruteur connecté.
 */
@RestController
@RequestMapping("/api/offres")
public class OffreController {

    @Autowired
    private OffreService offreService;

    @Autowired
    private RecruteurRepository recruteurRepository;

    /**
     * Récupère toutes les offres d'emploi.
     * @return Liste de toutes les offres
     */
    @GetMapping
    public List<Offre> getAllOffres() {
        return offreService.findAll();
    }

    /**
     * Récupère les offres du recruteur connecté.
     * Utilise l'Authentication pour obtenir l'email du recruteur via le token JWT.
     * @param authentication Authentification JWT du recruteur connecté
     * @return Liste des offres du recruteur
     */
    @GetMapping("/mes-offres")
    public List<Offre> getMesOffres(Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        return offreService.findByRecruteurId(recruteur.getId());
    }

    /**
     * Récupère une offre par son ID.
     * @param id Identifiant de l'offre
     * @return Offre trouvée ou 404 si non trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Offre> getOffreById(@PathVariable Long id) {
        return offreService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée une nouvelle offre d'emploi.
     * L'offre est automatiquement associée au recruteur connecté.
     * @param offre Offre à créer
     * @param authentication Authentification JWT du recruteur connecté
     * @return Offre créée
     */
    @PostMapping
    public Offre createOffre(@RequestBody Offre offre, Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        if (recruteur == null) {
            throw new RuntimeException("Recruteur non trouvé");
        }
        offre.setRecruteur(recruteur);
        return offreService.save(offre);
    }

    /**
     * Met à jour une offre existante.
     * @param id Identifiant de l'offre à mettre à jour
     * @param offre Nouvelles données de l'offre
     * @return Offre mise à jour ou 404 si non trouvée
     */
    @PutMapping("/{id}")
    public ResponseEntity<Offre> updateOffre(@PathVariable Long id, @RequestBody Offre offre) {
        if (offreService.findById(id).isPresent()) {
            offre.setId(id);
            return ResponseEntity.ok(offreService.save(offre));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Supprime une offre par son ID.
     * @param id Identifiant de l'offre à supprimer
     * @return 204 si supprimée, 404 si non trouvée
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffre(@PathVariable Long id) {
        if (offreService.findById(id).isPresent()) {
            offreService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
