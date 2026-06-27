package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Competence;
import com.AppRecrutement.AppRecrutement.model.NiveauEtude;
import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.model.SousDomaineIT;
import com.AppRecrutement.AppRecrutement.model.TypeOffre;
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
        // Le candidat n'a pas de champ domaine, on retourne toutes les offres
        return offreService.findAll();
    }

    /**
     * Récupère les offres filtrées par type d'offre.
     * @param typeOffre Type d'offre (STAGE, EMPLOI, ALTERNANCE, FREELANCE)
     * @return Liste des offres du type spécifié
     */
    @GetMapping("/type/{typeOffre}")
    public List<Offre> getOffresParType(@PathVariable String typeOffre) {
        try {
            TypeOffre type = TypeOffre.valueOf(typeOffre.toUpperCase());
            return offreRepository.findByTypeOffre(type);
        } catch (IllegalArgumentException e) {
            return offreService.findAll();
        }
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
        try {
            System.out.println("=== DEBUG CREATE OFFRE ===");
            System.out.println("Payload: " + payload);
            
            String email = authentication.getName();
            Recruteur recruteur = recruteurRepository.findByEmail(email);
            if (recruteur == null) {
                throw new RuntimeException("Recruteur non trouvé");
            }

            Offre offre = new Offre();
            offre.setTitre((String) payload.get("titre"));
            offre.setDescription((String) payload.get("description"));
            offre.setSalaire((String) payload.get("salaire"));
            offre.setLieu((String) payload.get("lieu"));
            offre.setDomaine("IT"); // Forcé à IT
            offre.setRecruteur(recruteur);
            offre.setEstOuverte(true);

            // Convertir le type d'offre String en enum
            String typeOffreStr = (String) payload.get("typeOffre");
            System.out.println("typeOffreStr: " + typeOffreStr);
            if (typeOffreStr != null && !typeOffreStr.isEmpty()) {
                offre.setTypeOffre(TypeOffre.valueOf(typeOffreStr.toUpperCase()));
            }

            // Convertir le sous-domaine IT String en enum
            String sousDomaineITStr = (String) payload.get("sousDomaineIT");
            System.out.println("sousDomaineITStr: " + sousDomaineITStr);
            if (sousDomaineITStr != null && !sousDomaineITStr.isEmpty()) {
                offre.setSousDomaineIT(SousDomaineIT.valueOf(sousDomaineITStr.toUpperCase()));
            }

            // Convertir le niveau d'étude requis String en enum
            String niveauEtudeStr = (String) payload.get("niveauEtudeRequis");
            System.out.println("niveauEtudeStr: " + niveauEtudeStr);
            if (niveauEtudeStr != null && !niveauEtudeStr.isEmpty()) {
                offre.setNiveauEtudeRequis(NiveauEtude.valueOf(niveauEtudeStr.toUpperCase()));
            }

            System.out.println("Offre avant sauvegarde: " + offre);
            
            // Gérer les compétences
            String competencesStr = (String) payload.get("competences");
            System.out.println("=== DEBUG CREATE OFFRE COMPETENCES ===");
            System.out.println("Competences reçues: " + competencesStr);
            
            if (competencesStr != null && !competencesStr.trim().isEmpty()) {
                List<Competence> competences = new ArrayList<>();
                String[] competenceNames = competencesStr.split(",");
                for (String competenceName : competenceNames) {
                    String trimmedName = competenceName.trim();
                    if (!trimmedName.isEmpty()) {
                        System.out.println("Traitement compétence: " + trimmedName);
                        // Vérifier si la compétence existe déjà
                        java.util.Optional<Competence> existingCompetence = competenceRepository.findByNom(trimmedName);
                        Competence competence;
                        if (existingCompetence.isPresent()) {
                            competence = existingCompetence.get();
                            System.out.println("Compétence existante: " + competence.getNom());
                        } else {
                            competence = new Competence(trimmedName, "TECHNIQUE");
                            competence = competenceRepository.save(competence);
                            System.out.println("Nouvelle compétence créée: " + competence.getNom());
                        }
                        competences.add(competence);
                    }
                }
                offre.setCompetences(competences);
            }
            
            // Sauvegarder l'offre (cascade gère la table de jointure)
            Offre savedOffre = offreService.save(offre);
            System.out.println("Offre sauvegardée avec " + (savedOffre.getCompetences() != null ? savedOffre.getCompetences().size() : 0) + " compétences");

        return savedOffre;
        } catch (Exception e) {
            System.out.println("=== ERREUR CREATE OFFRE ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de l'offre: " + e.getMessage(), e);
        }
    }

    /**
     * Met à jour une offre existante.
     * @param id Identifiant de l'offre à mettre à jour
     * @param payload Map contenant les nouvelles données de l'offre
     * @return Offre mise à jour ou 404 si non trouvée
     */
    @PutMapping("/{id}")
    public ResponseEntity<Offre> updateOffre(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        try {
            java.util.Optional<Offre> offreOpt = offreService.findById(id);
            if (offreOpt.isPresent()) {
                Offre offre = offreOpt.get();

                // Mettre à jour les champs de base
                if (payload.containsKey("titre")) {
                    offre.setTitre((String) payload.get("titre"));
                }
                if (payload.containsKey("description")) {
                    offre.setDescription((String) payload.get("description"));
                }
                if (payload.containsKey("salaire")) {
                    offre.setSalaire((String) payload.get("salaire"));
                }
                if (payload.containsKey("lieu")) {
                    offre.setLieu((String) payload.get("lieu"));
                }
                // Le domaine reste forcé à "IT"
                offre.setDomaine("IT");

                // Mettre à jour le type d'offre
                String typeOffreStr = (String) payload.get("typeOffre");
                if (typeOffreStr != null && !typeOffreStr.isEmpty()) {
                    offre.setTypeOffre(TypeOffre.valueOf(typeOffreStr.toUpperCase()));
                }

                // Mettre à jour le sous-domaine IT
                String sousDomaineITStr = (String) payload.get("sousDomaineIT");
                if (sousDomaineITStr != null && !sousDomaineITStr.isEmpty()) {
                    offre.setSousDomaineIT(SousDomaineIT.valueOf(sousDomaineITStr.toUpperCase()));
                }

                // Mettre à jour le niveau d'étude requis
                String niveauEtudeStr = (String) payload.get("niveauEtudeRequis");
                if (niveauEtudeStr != null && !niveauEtudeStr.isEmpty()) {
                    offre.setNiveauEtudeRequis(NiveauEtude.valueOf(niveauEtudeStr.toUpperCase()));
                }

                // Gérer les compétences si présentes dans le payload
                String competencesStr = (String) payload.get("competences");
                System.out.println("=== DEBUG UPDATE OFFRE COMPETENCES ===");
                System.out.println("Competences reçues: " + competencesStr);
                
                if (competencesStr != null && !competencesStr.trim().isEmpty()) {
                    // Créer ou récupérer les compétences et les associer à l'offre
                    List<Competence> newCompetences = new ArrayList<>();
                    String[] competenceNames = competencesStr.split(",");
                    for (String competenceName : competenceNames) {
                        String trimmedName = competenceName.trim();
                        if (!trimmedName.isEmpty()) {
                            System.out.println("Traitement compétence: " + trimmedName);
                            java.util.Optional<Competence> existingCompetence = competenceRepository.findByNom(trimmedName);
                            Competence competence;
                            if (existingCompetence.isPresent()) {
                                competence = existingCompetence.get();
                                System.out.println("Compétence existante: " + competence.getNom());
                            } else {
                                competence = new Competence(trimmedName, "TECHNIQUE");
                                competence = competenceRepository.save(competence);
                                System.out.println("Nouvelle compétence créée: " + competence.getNom());
                            }
                            newCompetences.add(competence);
                        }
                    }
                    // Remplacer les compétences de l'offre (cascade gère la table de jointure)
                    offre.setCompetences(newCompetences);
                } else {
                    // Si aucune compétence n'est fournie, vider la liste
                    offre.setCompetences(new ArrayList<>());
                }

                // Sauvegarder l'offre (cascade sauvegarde automatiquement les compétences dans la table de jointure)
                Offre updatedOffre = offreService.save(offre);
                System.out.println("Offre mise à jour avec " + (updatedOffre.getCompetences() != null ? updatedOffre.getCompetences().size() : 0) + " compétences");

                return ResponseEntity.ok(updatedOffre);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.out.println("=== ERREUR UPDATE OFFRE ===");
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la modification de l'offre: " + e.getMessage(), e);
        }
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
