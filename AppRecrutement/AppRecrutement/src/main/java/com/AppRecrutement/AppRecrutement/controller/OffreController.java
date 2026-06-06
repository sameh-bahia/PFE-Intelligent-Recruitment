package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Competence;
import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.model.TypeContrat;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.CompetenceRepository;
import com.AppRecrutement.AppRecrutement.repository.OffreRepository;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/offres")
public class OffreController {

    @Autowired
    private OffreService offreService;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private OffreRepository offreRepository;

    /**
     * Récupère toutes les offres d'emploi.
     * @return Liste de toutes les offres
     */
    @GetMapping
    public List<Offre> getAllOffres() {
        return offreService.findAll();
    }

    /**
     * Récupère les offres filtrées par domaine du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return Liste des offres du domaine du candidat
     */
    @GetMapping("/par-domaine")
    public List<Offre> getOffresParDomaine(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null || candidat.getDomaine() == null) {
            return offreService.findAll();
        }
        return offreRepository.findByDomaine(candidat.getDomaine());
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
     * @param payload Map contenant les données de l'offre (titre, description, typeContrat, salaire, lieu, competences)
     * @param authentication Authentification JWT du recruteur connecté
     * @return Offre créée
     */
    @PostMapping
    public Offre createOffre(@RequestBody Map<String, Object> payload, Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        if (recruteur == null) {
            throw new RuntimeException("Recruteur non trouvé");
        }

        Offre offre = new Offre();
        offre.setTitre((String) payload.get("titre"));
        offre.setDescription((String) payload.get("description"));
        
        // Convertir le type de contrat String en enum
        String typeContratStr = (String) payload.get("typeContrat");
        if (typeContratStr != null && !typeContratStr.isEmpty()) {
            offre.setTypeContrat(TypeContrat.valueOf(typeContratStr.toUpperCase()));
        }
        
        offre.setSalaire((String) payload.get("salaire"));
        offre.setLieu((String) payload.get("lieu"));
        offre.setDomaine((String) payload.get("domaine"));
        offre.setRecruteur(recruteur);
        offre.setEstOuverte(true);

        // Sauvegarder l'offre d'abord
        Offre savedOffre = offreService.save(offre);

        // Gérer les compétences
        String competencesStr = (String) payload.get("competences");
        if (competencesStr != null && !competencesStr.trim().isEmpty()) {
            List<Competence> competences = new ArrayList<>();
            String[] competenceNames = competencesStr.split(",");
            for (String competenceName : competenceNames) {
                String trimmedName = competenceName.trim();
                if (!trimmedName.isEmpty()) {
                    // Vérifier si la compétence existe déjà
                    java.util.Optional<Competence> existingCompetence = competenceRepository.findByNom(trimmedName);
                    Competence competence;
                    if (existingCompetence.isPresent()) {
                        competence = existingCompetence.get();
                    } else {
                        competence = new Competence(trimmedName, "TECHNIQUE");
                        competence = competenceRepository.save(competence);
                    }
                    // Ajouter l'offre à la liste des offres de la compétence (car Competence est le propriétaire de la relation)
                    if (competence.getOffres() == null) {
                        competence.setOffres(new ArrayList<>());
                    }
                    competence.getOffres().add(savedOffre);
                    competenceRepository.save(competence);
                    competences.add(competence);
                }
            }
            savedOffre.setCompetences(competences);
            offreService.save(savedOffre);
        }

        return savedOffre;
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
     * Met à jour le statut d'une offre (ouvrir/fermer).
     * @param id Identifiant de l'offre à mettre à jour
     * @param payload Map contenant le nouveau statut estOuverte
     * @return Offre mise à jour ou 404 si non trouvée
     */
    @PutMapping("/{id}/statut")
    public ResponseEntity<Offre> updateOffreStatut(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        java.util.Optional<Offre> offreOpt = offreService.findById(id);
        if (offreOpt.isPresent()) {
            Offre offre = offreOpt.get();
            Boolean estOuverte = payload.get("estOuverte");
            if (estOuverte != null) {
                offre.setEstOuverte(estOuverte);
                return ResponseEntity.ok(offreService.save(offre));
            }
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
