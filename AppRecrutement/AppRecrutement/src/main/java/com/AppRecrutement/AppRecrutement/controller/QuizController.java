package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.dto.QuizRecruteurDTO;
import com.AppRecrutement.AppRecrutement.model.OptionReponse;
import com.AppRecrutement.AppRecrutement.model.Question;
import com.AppRecrutement.AppRecrutement.model.Quiz;
import com.AppRecrutement.AppRecrutement.repository.OptionReponseRepository;
import com.AppRecrutement.AppRecrutement.repository.QuestionRepository;
import com.AppRecrutement.AppRecrutement.repository.QuizRepository;
import com.AppRecrutement.AppRecrutement.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur QuizController - API REST pour la gestion des quiz par les recruteurs
 * 
 * Ce contrôleur expose les endpoints CRUD pour les quiz, questions et options.
 * Il est destiné aux recruteurs pour créer et gérer les quiz techniques.
 * 
 * SÉCURITÉ : Endpoints pour recruteurs uniquement
 * - Les endpoints utilisent QuizRecruteurDTO (avec isCorrect)
 * - La sécurité par rôle doit être implémentée au niveau de l'authentification
 * - Voir QuizSubmissionController pour les endpoints candidats
 * 
 * CHOIX TECHNIQUE : Endpoints séparés pour création de questions/options
 * - POST /api/questions : Créer une question indépendamment
 * - POST /api/options : Créer une option indépendamment
 * - Permet au frontend CreerQuiz de construire le quiz progressivement
 */
@RestController
@RequestMapping("/api")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionReponseRepository optionReponseRepository;

    @Autowired
    private QuizRepository quizRepository;

    /**
     * Créer un quiz pour une offre
     * 
     * Endpoint utilisé par le frontend CreerQuiz après avoir créé les questions/options.
     * Lie le quiz à l'offre spécifiée.
     * 
     * @param quiz Le quiz à créer (titre, durée)
     * @param offreId L'ID de l'offre à laquelle lier le quiz
     * @return Le quiz créé avec l'offre liée
     */
    @PostMapping("/quiz/offre/{offreId}")
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz, @PathVariable Long offreId) {
        Quiz createdQuiz = quizService.createQuiz(quiz, offreId);
        return ResponseEntity.ok(createdQuiz);
    }

    /**
     * Créer une question pour un quiz
     * 
     * Endpoint utilisé par le frontend CreerQuiz pour ajouter une question.
     * Accepte quizId directement dans le corps de la requête.
     * 
     * @param questionData Données de la question (énoncé, points, quizId)
     * @return La question créée
     */
    @PostMapping("/questions")
    public ResponseEntity<Question> createQuestion(@RequestBody java.util.Map<String, Object> questionData) {
        // Extraire les données du corps de la requête
        String enonce = (String) questionData.get("enonce");
        Integer points = ((Number) questionData.get("points")).intValue();
        Long quizId = ((Number) questionData.get("quizId")).longValue();
        
        // Récupérer le Quiz par ID
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new RuntimeException("Quiz non trouvé avec l'ID: " + quizId));
        
        // Créer la Question et l'attacher au Quiz
        Question question = new Question(enonce, points);
        question.setQuiz(quiz);
        
        Question createdQuestion = questionRepository.save(question);
        return ResponseEntity.ok(createdQuestion);
    }

    /**
     * Créer une option de réponse pour une question
     * 
     * Endpoint utilisé par le frontend CreerQuiz pour ajouter une option.
     * Accepte questionId directement dans le corps de la requête.
     * 
     * @param optionData Données de l'option (texte, isCorrect, questionId)
     * @return L'option créée
     */
    @PostMapping("/options")
    public ResponseEntity<OptionReponse> createOption(@RequestBody java.util.Map<String, Object> optionData) {
        // Extraire les données du corps de la requête
        String texte = (String) optionData.get("texte");
        Boolean isCorrect = (Boolean) optionData.get("isCorrect");
        
        // Gérer le cas où questionId est null
        Object questionIdObj = optionData.get("questionId");
        if (questionIdObj == null) {
            throw new RuntimeException("questionId est obligatoire");
        }
        Long questionId = ((Number) questionIdObj).longValue();
        
        // Récupérer la Question par ID
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question non trouvée avec l'ID: " + questionId));
        
        // Créer l'OptionReponse et l'attacher à la Question
        OptionReponse option = new OptionReponse(texte, isCorrect);
        option.setQuestion(question);
        
        OptionReponse createdOption = optionReponseRepository.save(option);
        return ResponseEntity.ok(createdOption);
    }

    /**
     * Récupérer un quiz pour le recruteur (AVEC isCorrect)
     * 
     * Endpoint utilisé par le frontend pour afficher un quiz au recruteur.
     * Utilise QuizRecruteurDTO qui inclut le champ isCorrect.
     * 
     * SÉCURITÉ : Ce endpoint ne doit être accessible qu'aux recruteurs
     * 
     * @param id L'ID du quiz à récupérer
     * @return QuizRecruteurDTO avec les options AVEC isCorrect
     */
    @GetMapping("/quiz/{id}/recruteur")
    public ResponseEntity<QuizRecruteurDTO> getQuizForRecruteur(@PathVariable Long id) {
        QuizRecruteurDTO quizDTO = quizService.getQuizForRecruteur(id);
        return ResponseEntity.ok(quizDTO);
    }

    /**
     * Mettre à jour un quiz
     * 
     * Endpoint pour modifier les informations de base d'un quiz (titre, durée).
     * Les questions et options ne sont pas modifiées ici.
     * 
     * @param id L'ID du quiz à mettre à jour
     * @param quizDetails Les nouvelles informations du quiz
     * @return Le quiz mis à jour
     */
    @PutMapping("/quiz/{id}")
    public ResponseEntity<Quiz> updateQuiz(@PathVariable Long id, @RequestBody Quiz quizDetails) {
        Quiz updatedQuiz = quizService.updateQuiz(id, quizDetails);
        return ResponseEntity.ok(updatedQuiz);
    }

    /**
     * Supprimer un quiz
     * 
     * Endpoint pour supprimer un quiz et toutes ses questions/options (cascade).
     * 
     * @param id L'ID du quiz à supprimer
     * @return 204 No Content si la suppression réussit
     */
    @DeleteMapping("/quiz/{id}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }
}
