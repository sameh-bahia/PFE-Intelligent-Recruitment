package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.dto.QuizCandidatDTO;
import com.AppRecrutement.AppRecrutement.dto.ReponseQuizDTO;
import com.AppRecrutement.AppRecrutement.dto.ResultatQuizDTO;
import com.AppRecrutement.AppRecrutement.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur QuizSubmissionController - API REST pour la soumission des quiz par les candidats
 * 
 * Ce contrôleur expose les endpoints pour les candidats :
 * - Récupérer un quiz (SANS isCorrect)
 * - Soumettre les réponses et obtenir le score
 * 
 * SÉCURITÉ : Endpoints pour candidats uniquement
 * - GET /api/quiz/{id}/candidat utilise QuizCandidatDTO (sans isCorrect)
 * - POST /api/quiz/{id}/soumettre valide le temps et calcule le score côté backend
 * - La sécurité par rôle doit être implémentée au niveau de l'authentification
 * 
 * SÉCURITÉ : Séparation des contrôleurs
 * - QuizController : pour les recruteurs (CRUD quiz)
 * - QuizSubmissionController : pour les candidats (récupération + soumission)
 * - Cela permet une séparation claire des responsabilités et de la sécurité
 */
@RestController
@RequestMapping("/api/quiz")
public class QuizSubmissionController {

    @Autowired
    private QuizService quizService;

    /**
     * Récupérer un quiz pour le candidat (SANS isCorrect)
     * 
     * Endpoint utilisé par le frontend PasserQuiz pour afficher le quiz au candidat.
     * Utilise QuizCandidatDTO qui n'inclut PAS le champ isCorrect.
     * 
     * SÉCURITÉ : Ce endpoint est sécurisé
     * - Utilise QuizCandidatDTO (options sans isCorrect)
     * - Les candidats ne peuvent pas voir les réponses correctes
     * - Même avec DevTools, le champ isCorrect n'est pas présent dans la réponse
     * 
     * @param id L'ID du quiz à récupérer
     * @return QuizCandidatDTO avec les options SANS isCorrect
     */
    @GetMapping("/{id}/candidat")
    public ResponseEntity<QuizCandidatDTO> getQuizForCandidat(@PathVariable Long id) {
        QuizCandidatDTO quizDTO = quizService.getQuizForCandidat(id);
        return ResponseEntity.ok(quizDTO);
    }

    /**
     * Soumettre les réponses du candidat
     * 
     * Endpoint utilisé par le frontend PasserQuiz pour soumettre les réponses.
     * Le service QuizService valide le temps (anti-triche) et calcule le score.
     * Le score est stocké dans l'entité Candidature.
     * 
     * SÉCURITÉ : Validation anti-triche
     * - Le temps écoulé est validé côté backend (+30s tolérance)
     * - Le score est calculé en comparant avec les options correctes en base
     * - Empêche la triche par manipulation du frontend
     * 
     * @param id L'ID du quiz
     * @param reponseQuizDTO Les réponses du candidat et le temps écoulé
     * @return ResultatQuizDTO avec le score obtenu, le score total et le pourcentage
     */
    @PostMapping("/{id}/soumettre")
    public ResponseEntity<ResultatQuizDTO> soumettreQuiz(
            @PathVariable Long id,
            @RequestBody ReponseQuizDTO reponseQuizDTO
    ) {
        ResultatQuizDTO resultat = quizService.soumettreQuiz(id, reponseQuizDTO);
        return ResponseEntity.ok(resultat);
    }
}
