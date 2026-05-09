package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.service.RecruteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour gérer les recruteurs.
 * Fournit les endpoints CRUD pour les recruteurs et un endpoint pour récupérer le profil du recruteur connecté.
 */
@RestController
@RequestMapping("/api/recruteurs")
public class RecruteurController {

    @Autowired
    private RecruteurService recruteurService;

    @Autowired
    private RecruteurRepository recruteurRepository;

    /**
     * Récupère tous les recruteurs.
     * @return Liste de tous les recruteurs
     */
    @GetMapping
    public List<Recruteur> getAllRecruteurs() {
        return recruteurService.findAll();
    }

    /**
     * Récupère un recruteur par son ID.
     * @param id Identifiant du recruteur
     * @return Recruteur trouvé ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Recruteur> getRecruteurById(@PathVariable Long id) {
        return recruteurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau recruteur.
     * @param recruteur Recruteur à créer
     * @return Recruteur créé
     */
    @PostMapping
    public Recruteur createRecruteur(@RequestBody Recruteur recruteur) {
        return recruteurService.save(recruteur);
    }

    /**
     * Met à jour un recruteur existant.
     * @param id Identifiant du recruteur à mettre à jour
     * @param recruteur Nouvelles données du recruteur
     * @return Recruteur mis à jour ou 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<Recruteur> updateRecruteur(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        return recruteurService.findById(id)
                .map(existingRecruteur -> {
                    existingRecruteur.setEmail(recruteur.getEmail());
                    existingRecruteur.setMotDePasse(recruteur.getMotDePasse());
                    existingRecruteur.setNom(recruteur.getNom());
                    existingRecruteur.setPrenom(recruteur.getPrenom());
                    existingRecruteur.setRole(recruteur.getRole());
                    existingRecruteur.setNomEntreprise(recruteur.getNomEntreprise());
                    existingRecruteur.setPoste(recruteur.getPoste());
                    existingRecruteur.setPhotoProfil(recruteur.getPhotoProfil());
                    existingRecruteur.setDateNaissance(recruteur.getDateNaissance());
                    existingRecruteur.setAdresse(recruteur.getAdresse());
                    existingRecruteur.setLieuTravailPrecedent(recruteur.getLieuTravailPrecedent());
                    existingRecruteur.setEntreprisePrecedente(recruteur.getEntreprisePrecedente());
                    // dateInscription est préservée par @Column(updatable = false)
                    return ResponseEntity.ok(recruteurService.save(existingRecruteur));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime un recruteur par son ID.
     * @param id Identifiant du recruteur à supprimer
     * @return 204 si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruteur(@PathVariable Long id) {
        if (recruteurService.findById(id).isPresent()) {
            recruteurService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Récupère le profil du recruteur connecté.
     * @param authentication Authentification JWT du recruteur connecté
     * @return Profil du recruteur ou 404 si non trouvé
     */
    @GetMapping("/mon-profil")
    public ResponseEntity<Recruteur> getMonProfil(Authentication authentication) {
        String email = authentication.getName();
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        if (recruteur == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(recruteur);
    }
}
