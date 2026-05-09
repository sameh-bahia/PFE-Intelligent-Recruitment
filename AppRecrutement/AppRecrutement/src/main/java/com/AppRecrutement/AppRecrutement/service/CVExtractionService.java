// ============================================================
// FICHIER : CVExtractionService.java
// DESCRIPTION : Service Spring Boot pour l'extraction de texte des CVs et l'appel au service Python IA
// LOCALISATION : D:\PFE\AppRecrutement\AppRecrutement\src\main\java\com\AppRecrutement\AppRecrutement\service\CVExtractionService.java
// FONCTION : 
//   1. Extrait le texte brut des fichiers PDF/DOCX avec Apache PDFBox et Apache POI
//   2. Envoie le texte au service Python FastAPI via HTTP POST
//   3. Reçoit les entités extraites (compétences, expériences, formations)
// ============================================================

package com.AppRecrutement.AppRecrutement.service;

// Importations pour l'extraction de texte depuis PDF
import org.apache.pdfbox.pdmodel.PDDocument;  // Classe pour charger un document PDF
import org.apache.pdfbox.text.PDFTextStripper;  // Classe pour extraire le texte d'un PDF

// Importations pour l'extraction de texte depuis DOCX
import org.apache.poi.xwpf.usermodel.XWPFDocument;  // Classe pour charger un document Word
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;  // Classe pour extraire le texte d'un Word

// Importations Spring pour les appels HTTP
import org.springframework.http.*;  // Classes pour les requêtes/réponses HTTP
import org.springframework.stereotype.Service;  // Annotation pour marquer comme service Spring
import org.springframework.web.client.RestTemplate;  // Classe pour les appels HTTP REST

// Importations Java standard
import java.io.File;  // Classe pour manipuler les fichiers
import java.io.FileInputStream;  // Classe pour lire les fichiers
import java.io.IOException;  // Classe pour les exceptions d'entrée/sortie
import java.util.List;  // Interface pour les listes
import java.util.Map;  // Interface pour les maps

/**
 * Service pour l'extraction de texte depuis les CVs et l'appel au service Python d'IA.
 * 
 * RESPONSABILITÉS :
 * - Extraire le texte brut des fichiers PDF et DOCX
 * - Appeler le service Python FastAPI pour l'extraction d'entités avec IA
 * - Retourner les entités extraites (compétences, expériences, formations)
 * 
 * UTILISATION :
 * Ce service est injecté dans CVController et appelé lors de l'upload d'un CV.
 */
@Service
public class CVExtractionService {

    // ============================================================
    // CONFIGURATION
    // URL du service Python FastAPI (doit être démarré indépendamment)
    // Port par défaut : 8000
    // Endpoint : /extract
    // ============================================================
    private static final String PYTHON_SERVICE_URL = "http://localhost:8000/extract";
    
    // RestTemplate pour les appels HTTP vers le service Python
    private final RestTemplate restTemplate;

    /**
     * Constructeur : Initialise le RestTemplate pour les appels HTTP.
     */
    public CVExtractionService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Extrait le texte brut d'un fichier CV (PDF ou DOCX).
     * 
     * PROCESSUS :
     * 1. Détermine le type de fichier (PDF ou DOCX)
     * 2. Appelle la méthode d'extraction appropriée
     * 3. Retourne le texte extrait
     * 
     * @param filePath Chemin complet du fichier CV sur le serveur
     * @return Texte brut extrait du CV
     * @throws IOException Si le fichier ne peut pas être lu ou si le format n'est pas supporté
     */
    public String extractTextFromFile(String filePath) throws IOException {
        File file = new File(filePath);
        String fileName = file.getName().toLowerCase();

        // Vérification du type de fichier et appel de la méthode d'extraction appropriée
        if (fileName.endsWith(".pdf")) {
            return extractTextFromPDF(file);  // Extraction depuis PDF avec Apache PDFBox
        } else if (fileName.endsWith(".docx") || fileName.endsWith(".doc")) {
            return extractTextFromDOCX(file);  // Extraction depuis DOCX avec Apache POI
        } else {
            throw new IOException("Format de fichier non supporté. Seuls PDF et DOCX sont supportés.");
        }
    }

    /**
     * Extrait le texte d'un fichier PDF en utilisant Apache PDFBox.
     * 
     * PROCESSUS :
     * 1. Charge le document PDF avec PDDocument
     * 2. Utilise PDFTextStripper pour extraire tout le texte
     * 3. Retourne le texte brut
     * 
     * @param file Fichier PDF à traiter
     * @return Texte extrait du PDF
     * @throws IOException Si le fichier ne peut pas être lu
     */
    private String extractTextFromPDF(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            // PDFTextStripper est un utilitaire PDFBox pour extraire le texte
            PDFTextStripper textStripper = new PDFTextStripper();
            return textStripper.getText(document);
        }
    }

    /**
     * Extrait le texte d'un fichier DOCX en utilisant Apache POI.
     * 
     * PROCESSUS :
     * 1. Ouvre le fichier avec FileInputStream
     * 2. Charge le document Word avec XWPFDocument
     * 3. Utilise XWPFWordExtractor pour extraire le texte
     * 4. Retourne le texte brut
     * 
     * @param file Fichier DOCX à traiter
     * @return Texte extrait du DOCX
     * @throws IOException Si le fichier ne peut pas être lu
     */
    private String extractTextFromDOCX(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(fis);
             XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
            return extractor.getText();
        }
    }

    /**
     * Appelle le service Python FastAPI pour extraire les entités du CV avec IA.
     * 
     * PROCESSUS :
     * 1. Crée le corps de la requête JSON avec le texte du CV
     * 2. Configure les headers HTTP (Content-Type: application/json)
     * 3. Envoie une requête POST au service Python
     * 4. Reçoit la réponse JSON avec les entités extraites
     * 
     * FORMAT DE REQUÊTE :
     * {
     *   "text": "Texte brut du CV..."
     * }
     * 
     * FORMAT DE RÉPONSE :
     * {
     *   "competences": [{"text": "Java", "label": "COMPETENCE", "start": 0, "end": 4}, ...],
     *   "experiences": [{"text": "Développeur", "label": "EXPERIENCE", ...}, ...],
     *   "formations": [{"text": "Master", "label": "FORMATION", ...}, ...]
     * }
     * 
     * @param cvText Texte brut du CV extrait du fichier PDF/DOCX
     * @return Map contenant 3 listes : compétences, expériences, formations
     * @throws RuntimeException Si l'appel au service Python échoue
     */
    public Map<String, List<Map<String, Object>>> extractEntitiesWithAI(String cvText) {
        try {
            // ============================================================
            // PRÉPARATION DE LA REQUÊTE HTTP
            // On crée un corps JSON avec le texte du CV
            // ============================================================
            Map<String, String> request = Map.of("text", cvText);

            // Configuration des headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);  // Indique que le corps est en JSON

            // Création de l'entité HTTP (headers + corps)
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

            // ============================================================
            // APPEL AU SERVICE PYTHON
            // Envoi d'une requête POST à http://localhost:8000/extract
            // ============================================================
            ResponseEntity<Map> response = restTemplate.postForEntity(
                PYTHON_SERVICE_URL,  // URL du service Python
                entity,              // Corps et headers de la requête
                Map.class             // Type de la réponse attendue
            );

            // ============================================================
            // TRAITEMENT DE LA RÉPONSE
            // Vérification du code HTTP et extraction du corps JSON
            // ============================================================
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                // Cast du corps de la réponse vers le type attendu
                return (Map<String, List<Map<String, Object>>>) response.getBody();
            } else {
                throw new RuntimeException("Erreur lors de l'appel au service Python: " + response.getStatusCode());
            }
        } catch (Exception e) {
            // Gestion des erreurs : réseau, service Python indisponible, etc.
            throw new RuntimeException("Erreur lors de l'extraction avec l'IA: " + e.getMessage(), e);
        }
    }

    /**
     * Extrait le texte et les entités d'un CV en une seule opération.
     * 
     * Cette méthode combine l'extraction de texte et l'appel au service Python
     * pour simplifier l'utilisation dans le contrôleur.
     * 
     * PROCESSUS :
     * 1. Extrait le texte du fichier (PDF/DOCX)
     * 2. Envoie le texte au service Python
     * 3. Retourne le texte + les entités extraites
     * 
     * @param filePath Chemin complet du fichier CV
     * @return Map contenant :
     *         - "texteBrut": String (texte complet du CV)
     *         - "competences": List<Map> (liste des compétences extraites)
     *         - "experiences": List<Map> (liste des expériences extraites)
     *         - "formations": List<Map> (liste des formations extraites)
     * @throws IOException Si le fichier ne peut pas être lu
     */
    public Map<String, Object> extractCVInformation(String filePath) throws IOException {
        // Étape 1 : Extraction du texte du fichier
        String text = extractTextFromFile(filePath);
        
        // Étape 2 : Appel au service Python pour l'extraction d'entités
        Map<String, List<Map<String, Object>>> entities = extractEntitiesWithAI(text);
        
        // Étape 3 : Retour du texte et des entités dans une map structurée
        return Map.of(
            "texteBrut", text,
            "competences", entities.getOrDefault("competences", List.of()),  // Liste vide si pas de compétences
            "experiences", entities.getOrDefault("experiences", List.of()),  // Liste vide si pas d'expériences
            "formations", entities.getOrDefault("formations", List.of())  // Liste vide si pas de formations
        );
    }
}
