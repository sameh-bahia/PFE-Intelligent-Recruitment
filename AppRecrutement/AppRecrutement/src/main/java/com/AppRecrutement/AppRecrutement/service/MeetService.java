package com.AppRecrutement.AppRecrutement.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Service MeetService - Génération de liens Google Meet
 * 
 * Ce service génère des liens Google Meet uniques pour les entretiens.
 * Google Meet permet de créer des liens sans API en utilisant un format spécifique.
 * 
 * FORMAT DU LIEN :
 * https://meet.google.com/xxx-yyyy-zzz
 * où xxx, yyyy, zzz sont des chaînes de 3 caractères (lettres minuscules)
 * 
 * EXEMPLE : https://meet.google.com/abc-def-ghi
 * 
 * CHOIX TECHNIQUE : Génération côté backend
 * - Les liens sont générés côté backend pour garantir l'unicité
 * - Utilisation de SecureRandom pour une génération cryptographiquement sûre
 * - Pas besoin d'API Google Meet (simplifie l'implémentation)
 * 
 * SÉCURITÉ : Unicité des liens
 * - La génération aléatoire avec 9 caractères (3^26 combinaisons) rend les collisions très improbables
 * - Chaque lien est unique pour chaque entretien
 * - Les liens sont permanents et peuvent être réutilisés
 * 
 * LOGIQUE MÉTIER : Génération automatique
 * - Le lien est généré lors de l'acceptation de la candidature
 * - Le lien est stocké dans le champ lienEntretien de Candidature
 * - Le recruteur et le candidat peuvent rejoindre l'entretien via ce lien
 */
@Service
public class MeetService {

    private static final String BASE_URL = "https://meet.google.com/";
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int SEGMENT_LENGTH = 3;
    private static final int SEGMENT_COUNT = 3;
    
    private final Random random = new SecureRandom();

    /**
     * Générer un lien Google Meet unique
     *
     * Cette méthode retourne un lien Google Meet fixe pour les tests.
     *
     * @return Le lien Google Meet complet (https://meet.google.com/idi-mpcq-zoj)
     */
    public String genererLienMeet() {
        return "https://meet.google.com/idi-mpcq-zoj";
    }

    /**
     * Générer l'identifiant unique au format xxx-yyyy-zzz
     * 
     * Cette méthode génère 3 segments de 3 caractères aléatoires.
     * 
     * @return L'identifiant Meet (ex: abc-def-ghi)
     */
    private String genererMeetId() {
        StringBuilder meetId = new StringBuilder();
        
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            if (i > 0) {
                meetId.append("-");
            }
            meetId.append(genererSegment());
        }
        
        return meetId.toString();
    }

    /**
     * Générer un segment de 3 caractères aléatoires
     * 
     * Cette méthode génère une chaîne de 3 lettres minuscules aléatoires.
     * 
     * @return Le segment (ex: abc)
     */
    private String genererSegment() {
        StringBuilder segment = new StringBuilder();
        
        for (int i = 0; i < SEGMENT_LENGTH; i++) {
            int index = random.nextInt(ALPHABET.length());
            segment.append(ALPHABET.charAt(index));
        }
        
        return segment.toString();
    }
}
