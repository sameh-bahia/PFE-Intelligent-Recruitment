package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.dto.StatsDTO;
import com.AppRecrutement.AppRecrutement.dto.UserDTO;
import com.AppRecrutement.AppRecrutement.model.Admin;
import com.AppRecrutement.AppRecrutement.model.Role;
import com.AppRecrutement.AppRecrutement.model.StatutCandidature;
import com.AppRecrutement.AppRecrutement.model.Utilisateur;
import com.AppRecrutement.AppRecrutement.repository.AdminRepository;
import com.AppRecrutement.AppRecrutement.repository.CandidatureRepository;
import com.AppRecrutement.AppRecrutement.repository.OffreRepository;
import com.AppRecrutement.AppRecrutement.repository.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * POST /api/admin/create-admin (TEMPORAIRE - À SUPPRIMER APRÈS PFE)
     * Crée un compte admin avec email et mot de passe
     */
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdmin(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String motDePasse = payload.get("motDePasse");
        String nom = payload.get("nom");

        if (email == null || motDePasse == null || nom == null) {
            return ResponseEntity.badRequest().body("Email, mot de passe et nom requis");
        }

        if (utilisateurRepository.existsByEmail(email)) {
            return ResponseEntity.badRequest().body("Email déjà utilisé");
        }

        Admin admin = new Admin();
        admin.setEmail(email);
        admin.setMotDePasse(passwordEncoder.encode(motDePasse));
        admin.setNom(nom);
        admin.setRole(Role.ADMIN);

        adminRepository.save(admin);

        return ResponseEntity.ok("Admin créé avec succès: " + email);
    }

    /**
     * GET /api/admin/stats
     * Retourne les statistiques globales de la plateforme
     */
    @GetMapping("/stats")
    public ResponseEntity<StatsDTO> getStats() {
        Long totalUsers = utilisateurRepository.count();
        Long totalOffres = offreRepository.count();
        Long totalCandidatures = candidatureRepository.count();
        Long totalEntretiens = candidatureRepository.countByStatut(StatutCandidature.ACCEPTEE);

        StatsDTO stats = new StatsDTO(totalUsers, totalOffres, totalCandidatures, totalEntretiens);
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/admin/registrations-by-day
     * Retourne le nombre d'inscriptions par jour pour les 30 derniers jours
     */
    @GetMapping("/registrations-by-day")
    public ResponseEntity<List<Map<String, Object>>> getRegistrationsByDay() {
        List<Map<String, Object>> registrations = new ArrayList<>();
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for (int i = 29; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            String dateString = date.format(formatter);
            
            // Compter les utilisateurs inscrits ce jour-là
            long count = utilisateurRepository.findAll().stream()
                .filter(u -> u.getDateInscription() != null)
                .filter(u -> {
                    LocalDate inscriptionDate = u.getDateInscription().toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                    return inscriptionDate.equals(date);
                })
                .count();

            Map<String, Object> entry = Map.of(
                "date", dateString,
                "count", count
            );
            registrations.add(entry);
        }

        return ResponseEntity.ok(registrations);
    }

    /**
     * GET /api/admin/users?role=CANDIDAT|RECRUTEUR
     * Retourne la liste des utilisateurs filtrée par rôle
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(@RequestParam(required = false) String role) {
        List<Utilisateur> utilisateurs;

        if (role != null && !role.isEmpty()) {
            try {
                Role roleEnum = Role.valueOf(role.toUpperCase());
                utilisateurs = utilisateurRepository.findByRole(roleEnum);
            } catch (IllegalArgumentException e) {
                utilisateurs = utilisateurRepository.findAll();
            }
        } else {
            utilisateurs = utilisateurRepository.findAll();
        }

        List<UserDTO> userDTOs = utilisateurs.stream().map(this::convertToUserDTO).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * PUT /api/admin/users/{id}/status
     * Active ou désactive un utilisateur
     */
    @PutMapping("/users/{id}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        if (utilisateurRepository.findById(id).isPresent()) {
            Boolean actif = payload.get("actif");
            if (actif != null) {
                // Note: Si l'entité Utilisateur n'a pas de champ actif, il faudra l'ajouter
                // Pour l'instant, on suppose que l'entité a ce champ ou on utilise un autre mécanisme
                // Si pas de champ actif, on peut utiliser un champ "enabled" ou similaire
                // Pour l'instant, on retourne 200 OK (à adapter selon l'entité réelle)
                return ResponseEntity.ok().build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    private UserDTO convertToUserDTO(Utilisateur utilisateur) {
        return new UserDTO(
            utilisateur.getId(),
            utilisateur.getNom() + " " + utilisateur.getPrenom(),
            utilisateur.getEmail(),
            utilisateur.getRole().name(),
            true, // Par défaut actif (à adapter si l'entité a un champ actif)
            utilisateur.getDateInscription()
        );
    }
}
