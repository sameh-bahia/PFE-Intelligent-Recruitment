package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.dto.OffreWithQuizDTO;
import com.AppRecrutement.AppRecrutement.model.*;
import com.AppRecrutement.AppRecrutement.repository.OffreRepository;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.repository.CompetenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OffreService {

    @Autowired
    private OffreRepository offreRepository;

    @Autowired
    private RecruteurRepository recruteurRepository;

    @Autowired
    private CompetenceRepository competenceRepository;

    public List<Offre> findAll() {
        return offreRepository.findAll();
    }

    public Optional<Offre> findById(Long id) {
        return offreRepository.findById(id);
    }

    public Offre save(Offre offre) {
        return offreRepository.save(offre);
    }

    @Transactional
    public void deleteById(Long id) {
        offreRepository.deleteById(id);
    }

    // Méthode ajoutée pour récupérer les offres d'un recruteur spécifique
    // Utilisée pour que chaque recruteur ne voit que ses propres offres
    public List<Offre> findByRecruteurId(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId);
    }

    /**
     * Créer une offre avec son quiz complet (questions + options) en une seule transaction
     * @param dto Le DTO contenant l'offre et le quiz imbriqué
     * @param recruteurEmail L'email du recruteur connecté
     * @return L'offre créée avec son quiz
     */
    @Transactional
    public Offre createOffreWithQuiz(OffreWithQuizDTO dto, String recruteurEmail) {
        // 1. Créer l'offre
        Offre offre = new Offre();
        offre.setTitre(dto.getTitre());
        offre.setDescription(dto.getDescription());
        offre.setLieu(dto.getLieu());
        offre.setTypeOffre(TypeOffre.valueOf(dto.getTypeOffre()));
        offre.setSousDomaineIT(SousDomaineIT.valueOf(dto.getSousDomaineIT()));
        offre.setNiveauEtudeRequis(NiveauEtude.valueOf(dto.getNiveauEtudeRequis()));
        offre.setSalaire(dto.getSalaire());
        offre.setDomaine(dto.getDomaine() != null ? dto.getDomaine() : "IT");
        offre.setEstOuverte(true);

        // Récupérer le recruteur
        Recruteur recruteur = recruteurRepository.findByEmail(recruteurEmail);
        if (recruteur == null) {
            throw new RuntimeException("Recruteur non trouvé");
        }
        offre.setRecruteur(recruteur);

        // 2. Créer le quiz si présent
        if (dto.getQuiz() != null) {
            Quiz quiz = new Quiz();
            quiz.setTitre(dto.getQuiz().getTitre());
            quiz.setDureeMinutes(dto.getQuiz().getDureeMinutes());

            // 3. Créer les questions et options
            if (dto.getQuiz().getQuestions() != null) {
                for (OffreWithQuizDTO.QuizDTO.QuestionDTO questionDTO : dto.getQuiz().getQuestions()) {
                    Question question = new Question();
                    question.setEnonce(questionDTO.getEnonce());
                    question.setPoints(questionDTO.getPoints());

                    // Créer les options
                    if (questionDTO.getOptions() != null) {
                        for (OffreWithQuizDTO.QuizDTO.QuestionDTO.OptionDTO optionDTO : questionDTO.getOptions()) {
                            OptionReponse option = new OptionReponse();
                            option.setTexte(optionDTO.getTexte());
                            option.setCorrect(optionDTO.getIsCorrect());
                            question.addOption(option);
                        }
                    }

                    quiz.addQuestion(question);
                }
            }

            // Lier le quiz à l'offre
            offre.setQuiz(quiz);
            quiz.setOffre(offre);
        }

        // 4. Gérer les compétences
        if (dto.getCompetences() != null) {
            List<Competence> competences = new java.util.ArrayList<>();
            for (String competenceNom : dto.getCompetences()) {
                Competence competence = competenceRepository.findByNom(competenceNom)
                        .orElseGet(() -> {
                            Competence newCompetence = new Competence();
                            newCompetence.setNom(competenceNom);
                            return competenceRepository.save(newCompetence);
                        });
                competences.add(competence);
            }
            offre.setCompetences(competences);
        }

        // 5. Sauvegarder tout (cascade gère le reste)
        return offreRepository.save(offre);
    }

}
