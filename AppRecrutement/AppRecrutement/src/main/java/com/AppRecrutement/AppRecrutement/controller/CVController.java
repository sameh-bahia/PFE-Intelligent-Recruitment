package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.CV;
import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Competence;
import com.AppRecrutement.AppRecrutement.model.Experience;
import com.AppRecrutement.AppRecrutement.model.Formation;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.CompetenceRepository;
import com.AppRecrutement.AppRecrutement.repository.ExperienceRepository;
import com.AppRecrutement.AppRecrutement.repository.FormationRepository;
import com.AppRecrutement.AppRecrutement.service.CVExtractionService;
import com.AppRecrutement.AppRecrutement.service.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Contrôleur REST pour gérer les CVs des candidats.
 * Fournit les endpoints CRUD pour les CVs, l'upload, le téléchargement et la suppression des CVs.
 */
@RestController
@RequestMapping("/api/cvs")
public class CVController {

    @Autowired
    private CVService cvService;

    @Autowired
    private CandidatRepository candidatRepository;

    @Autowired
    private CVExtractionService cvExtractionService;

    @Autowired
    private CompetenceRepository competenceRepository;

    @Autowired
    private ExperienceRepository experienceRepository;

    @Autowired
    private FormationRepository formationRepository;

    /** Répertoire de stockage des fichiers CV uploadés */
    private static final String UPLOAD_DIR = "uploads/cvs/";

    /**
     * Crée le répertoire d'upload s'il n'existe pas.
     */
    static {
        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        } catch (IOException e) {
            throw new RuntimeException("Impossible de créer le dossier de upload", e);
        }
    }

    /**
     * Récupère tous les CVs.
     * @return Liste de tous les CVs
     */
    @GetMapping
    public List<CV> getAllCVs() {
        return cvService.findAll();
    }

    /**
     * Récupère un CV par son ID.
     * @param id Identifiant du CV
     * @return CV trouvé ou 404 si non trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<CV> getCVById(@PathVariable Long id) {
        return cvService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Crée un nouveau CV.
     * @param cv CV à créer
     * @return CV créé
     */
    @PostMapping
    public CV createCV(@RequestBody CV cv) {
        return cvService.save(cv);
    }

    /**
     * Met à jour un CV existant.
     * @param id Identifiant du CV à mettre à jour
     * @param cv Nouvelles données du CV
     * @return CV mis à jour ou 404 si non trouvé
     */
    @PutMapping("/{id}")
    public ResponseEntity<CV> updateCV(@PathVariable Long id, @RequestBody CV cv) {
        if (cvService.findById(id).isPresent()) {
            cv.setId(id);
            return ResponseEntity.ok(cvService.save(cv));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Supprime un CV par son ID.
     * @param id Identifiant du CV à supprimer
     * @return 204 si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCV(@PathVariable Long id) {
        if (cvService.findById(id).isPresent()) {
            cvService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Upload un CV pour le candidat connecté.
     * Si le candidat a déjà un CV, l'ancien fichier est supprimé et remplacé.
     * Le texte est extrait avec PDFBox/Apache POI et envoyé au service Python pour l'extraction IA.
     * @param file Fichier CV à uploader (PDF, DOC, DOCX)
     * @param authentication Authentification JWT du candidat connecté
     * @return CV sauvegardé avec les informations extraites
     */
    @PostMapping("/upload")
    public ResponseEntity<CV> uploadCV(@RequestParam("file") MultipartFile file, Authentication authentication) {
        try {
            String email = authentication.getName();
            Candidat candidat = candidatRepository.findByEmail(email);

            if (candidat == null) {
                return ResponseEntity.notFound().build();
            }

            // Générer un nom unique pour le fichier
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.write(filePath, file.getBytes());

            // Créer ou mettre à jour le CV
            CV cv;
            if (candidat.getCv() != null) {
                cv = candidat.getCv();
                // Supprimer l'ancien fichier
                if (cv.getCheminFichier() != null) {
                    Path oldFilePath = Paths.get(cv.getCheminFichier());
                    if (Files.exists(oldFilePath)) {
                        Files.delete(oldFilePath);
                    }
                }
            } else {
                cv = new CV();
                cv.setCandidat(candidat);
            }

            cv.setCheminFichier(filePath.toString());
            cv.setDateUpload(new java.util.Date());

            // Sauvegarder le CV d'abord
            CV savedCV = cvService.save(cv);

            // Extraire le texte du CV
            try {
                String texteBrut = cvExtractionService.extractTextFromFile(filePath.toString());

                // Appeler le service Python pour l'extraction IA des entités
                Map<String, Object> entities = cvExtractionService.extractEntitiesWithAI(texteBrut);
                
                // Log des entités extraites (pour vérification)
                System.out.println("Entités extraites du CV:");
                System.out.println("Compétences: " + entities.getOrDefault("competences", List.of()));
                System.out.println("Expériences: " + entities.getOrDefault("experiences", List.of()));
                System.out.println("Formations: " + entities.getOrDefault("formations", List.of()));
                System.out.println("Niveau d'étude: " + entities.get("niveauEtude"));

                // Mettre à jour le niveau d'étude du candidat
                Object niveauEtudeObj = entities.get("niveauEtude");
                String niveauEtudeStr = null;
                
                if (niveauEtudeObj instanceof String) {
                    niveauEtudeStr = (String) niveauEtudeObj;
                } else if (niveauEtudeObj instanceof List) {
                    List<?> niveauList = (List<?>) niveauEtudeObj;
                    if (!niveauList.isEmpty()) {
                        niveauEtudeStr = niveauList.get(0).toString();
                    }
                }
                
                if (niveauEtudeStr != null) {
                    try {
                        com.AppRecrutement.AppRecrutement.model.NiveauEtude niveauEtude = 
                            com.AppRecrutement.AppRecrutement.model.NiveauEtude.valueOf(niveauEtudeStr);
                        candidat.setNiveauEtude(niveauEtude);
                        candidatRepository.save(candidat);
                        System.out.println("Niveau d'étude du candidat mis à jour: " + niveauEtude);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Niveau d'étude invalide: " + niveauEtudeStr);
                    }
                }

                // Supprimer les anciennes compétences du CV
                if (savedCV.getCompetences() != null) {
                    savedCV.getCompetences().clear();
                    cvService.save(savedCV);
                }

                // Sauvegarde des compétences extraites
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> competences = (List<Map<String, Object>>) entities.getOrDefault("competences", List.of());
                for (Map<String, Object> competence : competences) {
                    String nom = (String) competence.get("text");
                    // Vérifier si la compétence existe déjà
                    java.util.Optional<Competence> existingCompetenceOpt = competenceRepository.findByNom(nom);
                    Competence competenceToLink;
                    if (existingCompetenceOpt.isEmpty()) {
                        Competence newCompetence = new Competence(nom, "TECHNIQUE");
                        newCompetence = competenceRepository.save(newCompetence);
                        competenceToLink = newCompetence;
                    } else {
                        competenceToLink = existingCompetenceOpt.get();
                    }
                    // Ajouter le CV à la liste des CVs de la compétence (côté propriétaire de la relation)
                    if (competenceToLink.getCvs() == null) {
                        competenceToLink.setCvs(new java.util.ArrayList<>());
                    }
                    if (!competenceToLink.getCvs().contains(savedCV)) {
                        competenceToLink.getCvs().add(savedCV);
                        competenceRepository.save(competenceToLink);
                    }
                }

                // Sauvegarde des expériences extraites
                // Supprimer les anciennes expériences du candidat
                experienceRepository.deleteByCandidat(candidat);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> experiences = (List<Map<String, Object>>) entities.getOrDefault("experiences", List.of());
                for (Map<String, Object> experience : experiences) {
                    String titrePoste = (String) experience.get("titrePoste");
                    String entreprise = (String) experience.get("entreprise");
                    String dateDebut = (String) experience.get("dateDebut");
                    String dateFin = (String) experience.get("dateFin");
                    String description = (String) experience.get("description");
                    
                    Experience newExperience = new Experience();
                    newExperience.setTitrePoste(titrePoste != null ? titrePoste : "Non spécifié");
                    newExperience.setEntreprise(entreprise != null ? entreprise : "Non spécifié");
                    // Tronquer la description à 255 caractères maximum
                    String truncatedDescription = description != null && description.length() > 255 
                        ? description.substring(0, 255) 
                        : description;
                    newExperience.setDescription(truncatedDescription != null ? truncatedDescription : "");
                    newExperience.setCandidat(candidat);
                    
                    // Convertir les dates si présentes
                    if (dateDebut != null) {
                        try {
                            newExperience.setDateDebut(java.sql.Date.valueOf(dateDebut));
                        } catch (Exception e) {
                            System.err.println("Erreur de conversion dateDebut: " + e.getMessage());
                        }
                    }
                    if (dateFin != null) {
                        try {
                            newExperience.setDateFin(java.sql.Date.valueOf(dateFin));
                        } catch (Exception e) {
                            System.err.println("Erreur de conversion dateFin: " + e.getMessage());
                        }
                    }
                    
                    experienceRepository.save(newExperience);
                }

                // Sauvegarde des formations extraites
                // Supprimer les anciennes formations du candidat
                formationRepository.deleteByCandidat(candidat);
                
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> formations = (List<Map<String, Object>>) entities.getOrDefault("formations", List.of());
                for (Map<String, Object> formation : formations) {
                    String diplome = (String) formation.get("diplome");
                    String etablissement = (String) formation.get("etablissement");
                    String specialite = (String) formation.get("specialite");
                    String anneeObtention = (String) formation.get("anneeObtention");
                    
                    Formation newFormation = new Formation();
                    newFormation.setDiplome(diplome != null ? diplome : "Non spécifié");
                    newFormation.setEtablissement(etablissement != null ? etablissement : "Non spécifié");
                    newFormation.setSpecialite(specialite != null ? specialite : "Non spécifié");
                    newFormation.setCandidat(candidat);
                    if (anneeObtention != null) {
                        newFormation.setAnneeObtention(anneeObtention);
                    }
                    formationRepository.save(newFormation);
                }

            } catch (Exception e) {
                // Si l'extraction échoue, on sauvegarde quand même le CV mais sans les entités
                System.err.println("Erreur lors de l'extraction IA du CV: " + e.getMessage());
                e.printStackTrace();
            }
            
            return ResponseEntity.ok(savedCV);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Récupère le CV du candidat connecté.
     * @param authentication Authentification JWT du candidat connecté
     * @return CV du candidat ou 404 si non trouvé
     */
    @GetMapping("/mon-cv")
    public ResponseEntity<CV> getMonCV(Authentication authentication) {
        String email = authentication.getName();
        Candidat candidat = candidatRepository.findByEmail(email);

        if (candidat == null || candidat.getCv() == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(candidat.getCv());
    }

    /**
     * Supprime le CV du candidat connecté.
     * Supprime également le fichier physique du serveur.
     * @param authentication Authentification JWT du candidat connecté
     * @return 204 si supprimé, 404 si non trouvé
     */
    @DeleteMapping("/mon-cv")
    public ResponseEntity<Void> deleteMonCV(Authentication authentication) {
        try {
            String email = authentication.getName();
            Candidat candidat = candidatRepository.findByEmail(email);

            if (candidat == null || candidat.getCv() == null) {
                return ResponseEntity.notFound().build();
            }

            CV cv = candidat.getCv();

            // Supprimer le fichier
            if (cv.getCheminFichier() != null) {
                Path filePath = Paths.get(cv.getCheminFichier());
                if (Files.exists(filePath)) {
                    Files.delete(filePath);
                }
            }

            cvService.deleteById(cv.getId());
            return ResponseEntity.noContent().build();

        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Télécharge un CV par son ID.
     * Le fichier est affiché inline dans le navigateur (PDF/DOC).
     * @param id Identifiant du CV à télécharger
     * @return Contenu binaire du fichier
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadCV(@PathVariable Long id) {
        Optional<CV> cvOptional = cvService.findById(id);
        if (cvOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            CV cv = cvOptional.get();
            Path filePath = Paths.get(cv.getCheminFichier());
            byte[] content = Files.readAllBytes(filePath);

            String fileName = filePath.getFileName().toString();
            String contentType = "application/pdf";
            if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                contentType = "application/msword";
            }

            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=\"" + fileName + "\"")
                    .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                    .body(content);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
