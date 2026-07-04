package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Admin;
import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.repository.OffreRepository;
import com.AppRecrutement.AppRecrutement.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private OffreRepository offreRepository;

    @GetMapping
    public List<Admin> getAllAdmins() {
        return adminService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Admin> getAdminById(@PathVariable Long id) {
        return adminService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Admin createAdmin(@RequestBody Admin admin) {
        return adminService.save(admin);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Admin> updateAdmin(@PathVariable Long id, @RequestBody Admin admin) {
        if (adminService.findById(id).isPresent()) {
            admin.setId(id);
            return ResponseEntity.ok(adminService.save(admin));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        if (adminService.findById(id).isPresent()) {
            adminService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Endpoint pour récupérer les statistiques globales de la plateforme
     * @return Map contenant le nombre de candidats, recruteurs et offres actives
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalCandidats", candidatRepository.count());
        stats.put("totalRecruteurs", recruteurRepository.count());
        stats.put("totalOffresActives", offreRepository.countByEstOuverteTrue());
        return ResponseEntity.ok(stats);
    }

    /**
     * Endpoint pour récupérer tous les utilisateurs (candidats et recruteurs)
     * @return Liste de tous les utilisateurs avec leurs informations
     */
    @GetMapping("/utilisateurs")
    public ResponseEntity<List<Map<String, Object>>> getAllUtilisateurs() {
        List<Map<String, Object>> utilisateurs = new java.util.ArrayList<>();
        
        // Ajouter les candidats
        for (Candidat candidat : candidatRepository.findAll()) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", candidat.getId());
            user.put("email", candidat.getEmail());
            user.put("nom", candidat.getNom());
            user.put("prenom", candidat.getPrenom());
            user.put("role", "CANDIDAT");
            user.put("enabled", candidat.getEnabled());
            user.put("dateInscription", candidat.getDateInscription());
            utilisateurs.add(user);
        }
        
        // Ajouter les recruteurs
        for (Recruteur recruteur : recruteurRepository.findAll()) {
            Map<String, Object> user = new HashMap<>();
            user.put("id", recruteur.getId());
            user.put("email", recruteur.getEmail());
            user.put("nom", recruteur.getNom());
            user.put("prenom", recruteur.getPrenom());
            user.put("role", "RECRUTEUR");
            user.put("nomEntreprise", recruteur.getNomEntreprise());
            user.put("enabled", recruteur.getEnabled());
            user.put("dateInscription", recruteur.getDateInscription());
            utilisateurs.add(user);
        }
        
        return ResponseEntity.ok(utilisateurs);
    }

    /**
     * Endpoint pour bloquer/débloquer un utilisateur
     * @param id Identifiant de l'utilisateur
     * @param payload Map contenant le nouveau statut enabled
     * @return Utilisateur mis à jour ou 404 si non trouvé
     */
    @PutMapping("/utilisateurs/{id}/statut")
    public ResponseEntity<Map<String, Object>> updateUtilisateurStatut(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Boolean enabled = payload.get("enabled");
        if (enabled == null) {
            return ResponseEntity.badRequest().build();
        }

        // Chercher d'abord dans les candidats
        java.util.Optional<Candidat> candidatOpt = candidatRepository.findById(id);
        if (candidatOpt.isPresent()) {
            Candidat candidat = candidatOpt.get();
            candidat.setEnabled(enabled);
            candidatRepository.save(candidat);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", candidat.getId());
            response.put("email", candidat.getEmail());
            response.put("role", "CANDIDAT");
            response.put("enabled", candidat.getEnabled());
            return ResponseEntity.ok(response);
        }

        // Chercher dans les recruteurs
        java.util.Optional<Recruteur> recruteurOpt = recruteurRepository.findById(id);
        if (recruteurOpt.isPresent()) {
            Recruteur recruteur = recruteurOpt.get();
            recruteur.setEnabled(enabled);
            recruteurRepository.save(recruteur);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", recruteur.getId());
            response.put("email", recruteur.getEmail());
            response.put("role", "RECRUTEUR");
            response.put("enabled", recruteur.getEnabled());
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.notFound().build();
    }
}
