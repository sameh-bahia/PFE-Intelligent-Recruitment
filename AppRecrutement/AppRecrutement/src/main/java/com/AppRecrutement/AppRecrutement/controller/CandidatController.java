package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.*;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.service.CandidatService;
import com.AppRecrutement.AppRecrutement.repository.ExperienceRepository;
import com.AppRecrutement.AppRecrutement.repository.FormationRepository;
import com.AppRecrutement.AppRecrutement.repository.CompetenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


/**
 * Contrôleur REST pour gérer les candidats.
 * Fournit les endpoints CRUD pour les candidats et un endpoint pour récupérer le profil du candidat connecté.
 */
@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    /**
     * Récupère tous les candidats.
     * @return Liste de tous les candidats
     */
    @GetMapping
    public List<Candidat> getAllCandidats() {
        return candidatService.findAll();
    }

    /**
     * Récupère un candidat par son ID.
     * @param id Identifiant du candidat
     * @return Candidat trouvé ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getCandidatById(@PathVariable Long id) {
        return candidatService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau candidat.
     * @param candidat Candidat à créer
     * @return Candidat créé
     */
    @PostMapping
    public Candidat createCandidat(@RequestBody Candidat candidat) {
        return candidatService.save(candidat);
    }

    /**
     * Met à jour un candidat existant.
     * @param id Identifiant du candidat à mettre à jour
     * @param candidat Nouvelles données du candidat
     * @return Candidat mis à jour ou 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Candidat> updateCandidat(@PathVariable Long id, @RequestBody Candidat candidat) {
        return candidatService.findById(id)
                .map(existingCandidat -> {
                    existingCandidat.setEmail(candidat.getEmail());
                    existingCandidat.setMotDePasse(candidat.getMotDePasse());
                    existingCandidat.setNom(candidat.getNom());
                    existingCandidat.setPrenom(candidat.getPrenom());
                    existingCandidat.setRole(candidat.getRole());
                    existingCandidat.setTelephone(candidat.getTelephone());
                    existingCandidat.setAdresse(candidat.getAdresse());
                    existingCandidat.setDateNaissance(candidat.getDateNaissance());
                    existingCandidat.setTitreProfil(candidat.getTitreProfil());
                    // dateInscription est préservée par @Column(updatable = false)
                    return ResponseEntity.ok(candidatService.save(existingCandidat));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un candidat par son ID.
     * @param id Identifiant du candidat à supprimer
     * @return 204 si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable Long id) {
        if (candidatService.findById(id).isPresent()) {
            candidatService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère le profil du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return Profil du candidat ou 404 si non trouvé
     */
    @GetMapping("/mon-profil")
    public ResponseEntity<Candidat> getMonProfil(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(candidat);
    }

    /**
     * Récupère les expériences du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return Liste des expériences du candidat
     */
    @GetMapping("/experiences")
    public ResponseEntity<List<Experience>> getMesExperiences(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(candidat.getExperiences());
    }

    /**
     * Ajoute une expérience au candidat connecté.
     * @param experience Expérience à ajouter
     * @param authentication Authentification JWT du candidat connecté
     * @return Expérience créée
     */
    @PostMapping("/experiences")
    public ResponseEntity<Experience> addExperience(@RequestBody Experience experience, Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        experience.setCandidat(candidat);
        Experience savedExperience = experienceRepository.save(experience);
        return ResponseEntity.ok(savedExperience);
    }

    /**
     * Modifie une expérience du candidat connecté.
     * @param id Identifiant de l'expérience
     * @param experience Nouvelles données de l'expérience
     * @param authentication Authentification JWT du candidat connecté
     * @return Expérience modifiée ou 404 si non trouvée
     */
    @PutMapping("/experiences/{id}")
    public ResponseEntity<Experience> updateExperience(@PathVariable Long id, @RequestBody Experience experience, Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        
        return experienceRepository.findById(id)
            .map(existingExperience -> {
                if (!existingExperience.getCandidat().getId().equals(candidat.getId())) {
                    return ResponseEntity.status(403).<Experience>build();
                }
                existingExperience.setTitrePoste(experience.getTitrePoste());
                existingExperience.setEntreprise(experience.getEntreprise());
                existingExperience.setDateDebut(experience.getDateDebut());
                existingExperience.setDateFin(experience.getDateFin());
                existingExperience.setDescription(experience.getDescription());
                return ResponseEntity.ok(experienceRepository.save(existingExperience));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime une expérience du candidat connecté.
     * @param id Identifiant de l'expérience
     * @param authentication Authentification JWT du candidat connecté
     * @return 204 si supprimée, 404 si non trouvée
     */
    @DeleteMapping("/experiences/{id}")
    public ResponseEntity<Void> deleteExperience(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        
        return experienceRepository.findById(id)
            .map(experience -> {
                if (!experience.getCandidat().getId().equals(candidat.getId())) {
                    return ResponseEntity.status(403).<Void>build();
                }
                experienceRepository.deleteById(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les formations du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return Liste des formations du candidat
     */
    @GetMapping("/formations")
    public ResponseEntity<List<Formation>> getMesFormations(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(candidat.getFormations());
    }

    /**
     * Ajoute une formation au candidat connecté.
     * @param formation Formation à ajouter
     * @param authentication Authentification JWT du candidat connecté
     * @return Formation créée
     */
    @PostMapping("/formations")
    public ResponseEntity<Formation> addFormation(@RequestBody Formation formation, Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        formation.setCandidat(candidat);
        Formation savedFormation = formationRepository.save(formation);
        return ResponseEntity.ok(savedFormation);
    }

    /**
     * Modifie une formation du candidat connecté.
     * @param id Identifiant de la formation
     * @param formation Nouvelles données de la formation
     * @param authentication Authentification JWT du candidat connecté
     * @return Formation modifiée ou 404 si non trouvée
     */
    @PutMapping("/formations/{id}")
    public ResponseEntity<Formation> updateFormation(@PathVariable Long id, @RequestBody Formation formation, Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        
        return formationRepository.findById(id)
            .map(existingFormation -> {
                if (!existingFormation.getCandidat().getId().equals(candidat.getId())) {
                    return ResponseEntity.status(403).<Formation>build();
                }
                existingFormation.setDiplome(formation.getDiplome());
                existingFormation.setEtablissement(formation.getEtablissement());
                existingFormation.setSpecialite(formation.getSpecialite());
                existingFormation.setAnneeObtention(formation.getAnneeObtention());
                return ResponseEntity.ok(formationRepository.save(existingFormation));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime une formation du candidat connecté.
     * @param id Identifiant de la formation
     * @param authentication Authentification JWT du candidat connecté
     * @return 204 si supprimée, 404 si non trouvée
     */
    @DeleteMapping("/formations/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id, Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null) {
            return ResponseEntity.notFound().build();
        }
        
        return formationRepository.findById(id)
            .map(formation -> {
                if (!formation.getCandidat().getId().equals(candidat.getId())) {
                    return ResponseEntity.status(403).<Void>build();
                }
                formationRepository.deleteById(id);
                return ResponseEntity.noContent().<Void>build();
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Récupère les compétences du CV du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return Liste des compétences du CV
     */
    @GetMapping("/competences")
    public ResponseEntity<List<Competence>> getMesCompetences(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat == null || candidat.getCv() == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(candidat.getCv().getCompetences());
    }
}
