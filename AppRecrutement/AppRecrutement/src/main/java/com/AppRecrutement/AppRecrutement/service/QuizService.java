package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.dto.*;
import com.AppRecrutement.AppRecrutement.model.*;
import com.AppRecrutement.AppRecrutement.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service QuizService - Logique métier pour la gestion des quiz
 * 
 * Ce service contient toute la logique métier liée aux quiz :
 * - Création et modification de quiz
 * - Récupération sécurisée (avec/sans isCorrect selon le rôle)
 * - Calcul du score des candidats
 * - Validation du temps anti-triche
 * 
 * SÉCURITÉ : Séparation des DTOs par rôle
 * - getQuizForCandidat() utilise QuizCandidatDTO (sans isCorrect)
 * - getQuizForRecruteur() utilise QuizRecruteurDTO (avec isCorrect)
 * - Cela garantit que les candidats ne voient jamais les réponses correctes
 * 
 * CHOIX TECHNIQUE : @Transactional
 * - Toutes les méthodes sont transactionnelles pour garantir la cohérence des données
 * - En cas d'erreur, les modifications sont annulées automatiquement
 * 
 * LOGIQUE MÉTIER : Calcul du score
 * - Le score est calculé côté backend en comparant les réponses avec les options correctes
 * - Le score est stocké dans l'entité Candidature pour le dashboard recruteur
 */
@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionReponseRepository optionReponseRepository;

    @Autowired
    private CandidatureRepository candidatureRepository;

    @Autowired
    private OffreRepository offreRepository;

    /**
     * Créer un quiz pour une offre
     * 
     * Cette méthode lie un quiz à une offre existante.
     * La relation @OneToOne est établie automatiquement.
     * 
     * @param quiz Le quiz à créer
     * @param offreId L'ID de l'offre à laquelle lier le quiz
     * @return Le quiz créé avec l'offre liée
     * @throws RuntimeException si l'offre n'est pas trouvée
     */
    public Quiz createQuiz(Quiz quiz, Long offreId) {
        Offre offre = offreRepository.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre non trouvée"));
        quiz.setOffre(offre);
        offre.setQuiz(quiz); // Important : configurer les deux côtés de la relation bidirectionnelle
        offreRepository.save(offre); // Sauvegarder l'offre pour mettre à jour quiz_id
        return quizRepository.save(quiz);
    }

    /**
     * Récupérer un quiz pour le candidat (SANS isCorrect)
     * 
     * Cette méthode est SÉCURISÉE : elle utilise QuizCandidatDTO qui ne contient
     * pas le champ isCorrect. Les candidats ne peuvent pas voir les réponses correctes.
     * 
     * @param quizId L'ID du quiz à récupérer
     * @return QuizCandidatDTO avec les options SANS isCorrect
     * @throws RuntimeException si le quiz n'est pas trouvé
     */
    public QuizCandidatDTO getQuizForCandidat(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        return convertToCandidatDTO(quiz);
    }

    /**
     * Récupérer un quiz pour le recruteur (AVEC isCorrect)
     * 
     * Cette méthode utilise QuizRecruteurDTO qui contient le champ isCorrect.
     * Les recruteurs peuvent voir les réponses correctes pour gérer les quiz.
     * 
     * IMPORTANT : Cette méthode ne doit JAMAIS être appelée pour les candidats
     * 
     * @param quizId L'ID du quiz à récupérer
     * @return QuizRecruteurDTO avec les options AVEC isCorrect
     * @throws RuntimeException si le quiz n'est pas trouvé
     */
    public QuizRecruteurDTO getQuizForRecruteur(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        return convertToRecruteurDTO(quiz);
    }

    /**
     * Soumettre les réponses du candidat et calculer le score
     * 
     * Cette méthode effectue plusieurs opérations critiques :
     * 1. Valide le temps écoulé (anti-triche avec tolérance de 30s)
     * 2. Calcule le score en comparant les réponses avec les options correctes
     * 3. Met à jour la candidature avec le score et la date du quiz
     * 
     * LOGIQUE MÉTIER : Calcul du score
     * - Pour chaque réponse, on vérifie si l'option sélectionnée est correcte
     * - Si correcte, on ajoute les points de la question au score
     * - Le score total est la somme des points de toutes les questions
     * 
     * SÉCURITÉ : Validation du temps
     * - Le temps envoyé par le frontend est validé côté backend
     * - Tolérance de 30 secondes pour gérer les latences réseau
     * - Si le temps est dépassé, la soumission est rejetée
     * 
     * @param quizId L'ID du quiz
     * @param reponseQuizDTO Les réponses du candidat et le temps écoulé
     * @return ResultatQuizDTO avec le score obtenu, le score total et le pourcentage
     * @throws RuntimeException si le quiz, une question, une option ou la candidature n'est pas trouvée
     * @throws RuntimeException si le temps est dépassé
     */
    public ResultatQuizDTO soumettreQuiz(Long quizId, ReponseQuizDTO reponseQuizDTO) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

        // Vérifier le temps (anti-triche)
        validerTemps(quiz, reponseQuizDTO.getTempsEcouleSecondes());

        // Calculer le score
        int totalPoints = 0;
        int pointsObtenus = 0;

        for (Map.Entry<Long, Long> entry : reponseQuizDTO.getReponses().entrySet()) {
            Long questionId = entry.getKey();
            Long optionId = entry.getValue();

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new RuntimeException("Question non trouvée"));
            OptionReponse optionSelectionnee = optionReponseRepository.findById(optionId)
                    .orElseThrow(() -> new RuntimeException("Option non trouvée"));

            totalPoints += question.getPoints();

            // Vérifier si l'option sélectionnée est correcte
            if (optionSelectionnee.isCorrect()) {
                pointsObtenus += question.getPoints();
            }
        }

        // Mettre à jour la candidature avec le score
        Candidature candidature = candidatureRepository.findById(reponseQuizDTO.getCandidatureId())
                .orElseThrow(() -> new RuntimeException("Candidature non trouvée"));
        candidature.setScoreQuiz(pointsObtenus);
        candidature.setDateQuiz(java.time.LocalDateTime.now());
        candidatureRepository.save(candidature);

        return new ResultatQuizDTO(pointsObtenus, totalPoints);
    }

    /**
     * Valider le temps écoulé (anti-triche)
     * 
     * Cette méthode vérifie que le candidat n'a pas dépassé le temps autorisé.
     * Une tolérance de 30 secondes est appliquée pour gérer les latences réseau.
     * 
     * LOGIQUE MÉTIER : Tolérance de 30 secondes
     * - Permet de gérer les lenteurs réseau et les délais de soumission
     * - Empêche la triche tout en étant raisonnable pour les candidats
     * 
     * @param quiz Le quiz avec la durée autorisée
     * @param tempsEcouleSecondes Le temps écoulé envoyé par le frontend
     * @throws RuntimeException si le temps est dépassé
     */
    private void validerTemps(Quiz quiz, Long tempsEcouleSecondes) {
        if (quiz.getDureeMinutes() != null) {
            Integer dureeAutoriseeSecondes = quiz.getDureeMinutes() * 60;
            Integer tolerance = 30; // 30 secondes de tolérance pour la latence réseau

            if (tempsEcouleSecondes > dureeAutoriseeSecondes + tolerance) {
                throw new RuntimeException("Temps écoulé dépassé");
            }
        }
    }

    /**
     * Convertir Quiz en QuizCandidatDTO (SANS isCorrect)
     * 
     * Cette méthode convertit l'entité Quiz en DTO sécurisé pour les candidats.
     * Elle utilise convertQuestionToCandidatDTO pour garantir que les options
     * ne contiennent pas le champ isCorrect.
     * 
     * SÉCURITÉ : Conversion sécurisée
     * - Utilise QuestionCandidatDTO (options sans isCorrect)
     * - Garantit que les candidats ne voient jamais les réponses correctes
     * 
     * @param quiz L'entité Quiz à convertir
     * @return QuizCandidatDTO sécurisé
     */
    private QuizCandidatDTO convertToCandidatDTO(Quiz quiz) {
        List<QuestionCandidatDTO> questionDTOs = quiz.getQuestions().stream()
                .map(this::convertQuestionToCandidatDTO)
                .collect(Collectors.toList());

        return new QuizCandidatDTO(
                quiz.getId(),
                quiz.getTitre(),
                quiz.getDureeMinutes(),
                quiz.getDateCreation(),
                questionDTOs
        );
    }

    /**
     * Convertir Question en QuestionCandidatDTO (SANS isCorrect)
     * 
     * Cette méthode convertit une question en DTO sécurisé pour les candidats.
     * Les options sont converties en OptionCandidatDTO (sans isCorrect).
     * 
     * SÉCURITÉ : Conversion sécurisée des options
     * - Utilise OptionCandidatDTO (sans isCorrect)
     * - Le champ isCorrect de l'entité n'est PAS transféré
     * 
     * @param question L'entité Question à convertir
     * @return QuestionCandidatDTO sécurisé
     */
    private QuestionCandidatDTO convertQuestionToCandidatDTO(Question question) {
        List<OptionCandidatDTO> optionDTOs = question.getOptions().stream()
                .map(option -> new OptionCandidatDTO(option.getId(), option.getTexte()))
                .collect(Collectors.toList());

        return new QuestionCandidatDTO(
                question.getId(),
                question.getEnonce(),
                question.getPoints(),
                optionDTOs
        );
    }

    /**
     * Convertir Quiz en QuizRecruteurDTO (AVEC isCorrect)
     * 
     * Cette méthode convertit l'entité Quiz en DTO complet pour les recruteurs.
     * Elle utilise convertQuestionToRecruteurDTO pour inclure le champ isCorrect.
     * 
     * SÉCURITÉ : Conversion complète
     * - Utilise QuestionRecruteurDTO (options avec isCorrect)
     * - Ce DTO ne doit JAMAIS être envoyé aux candidats
     * 
     * @param quiz L'entité Quiz à convertir
     * @return QuizRecruteurDTO complet
     */
    private QuizRecruteurDTO convertToRecruteurDTO(Quiz quiz) {
        List<QuestionRecruteurDTO> questionDTOs = quiz.getQuestions().stream()
                .map(this::convertQuestionToRecruteurDTO)
                .collect(Collectors.toList());

        return new QuizRecruteurDTO(
                quiz.getId(),
                quiz.getTitre(),
                quiz.getDureeMinutes(),
                quiz.getDateCreation(),
                questionDTOs
        );
    }

    /**
     * Convertir Question en QuestionRecruteurDTO (AVEC isCorrect)
     * 
     * Cette méthode convertit une question en DTO complet pour les recruteurs.
     * Les options sont converties en OptionRecruteurDTO (avec isCorrect).
     * 
     * SÉCURITÉ : Conversion complète des options
     * - Utilise OptionRecruteurDTO (avec isCorrect)
     * - Le champ isCorrect de l'entité EST transféré
     * 
     * @param question L'entité Question à convertir
     * @return QuestionRecruteurDTO complet
     */
    private QuestionRecruteurDTO convertQuestionToRecruteurDTO(Question question) {
        List<OptionRecruteurDTO> optionDTOs = question.getOptions().stream()
                .map(option -> new OptionRecruteurDTO(option.getId(), option.getTexte(), option.isCorrect()))
                .collect(Collectors.toList());

        return new QuestionRecruteurDTO(
                question.getId(),
                question.getEnonce(),
                question.getPoints(),
                optionDTOs
        );
    }

    /**
     * Supprimer un quiz
     * 
     * Cette méthode supprime un quiz de la base de données.
     * Grâce à cascade.ALL, les questions et options sont supprimées automatiquement.
     * 
     * @param quizId L'ID du quiz à supprimer
     * @throws RuntimeException si le quiz n'est pas trouvé
     */
    public void deleteQuiz(Long quizId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));
        quizRepository.delete(quiz);
    }

    /**
     * Mettre à jour un quiz
     * 
     * Cette méthode met à jour les informations de base d'un quiz
     * (titre et durée). Les questions et options ne sont pas modifiées ici.
     * 
     * @param quizId L'ID du quiz à mettre à jour
     * @param quizDetails Les nouvelles informations du quiz
     * @return Le quiz mis à jour
     * @throws RuntimeException si le quiz n'est pas trouvé
     */
    public Quiz updateQuiz(Long quizId, Quiz quizDetails) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé"));

        quiz.setTitre(quizDetails.getTitre());
        quiz.setDureeMinutes(quizDetails.getDureeMinutes());

        return quizRepository.save(quiz);
    }
}
