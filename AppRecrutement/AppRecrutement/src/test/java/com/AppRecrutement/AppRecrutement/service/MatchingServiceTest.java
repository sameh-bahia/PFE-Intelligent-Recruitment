package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.*;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.CompetenceRepository;
import com.AppRecrutement.AppRecrutement.repository.OffreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private CandidatRepository candidatRepository;

    @Mock
    private OffreRepository offreRepository;

    @Mock
    private CompetenceRepository competenceRepository;

    @InjectMocks
    private MatchingService matchingService;

    private Candidat candidat;
    private Offre offre;
    private CV cv;
    private Competence competence1, competence2, competence3;

    @BeforeEach
    void setUp() {
        // Créer des compétences de test
        competence1 = new Competence();
        competence1.setId(1L);
        competence1.setNom("java");
        competence1.setCategorie("TECHNIQUE");

        competence2 = new Competence();
        competence2.setId(2L);
        competence2.setNom("python");
        competence2.setCategorie("TECHNIQUE");

        competence3 = new Competence();
        competence3.setId(3L);
        competence3.setNom("sql");
        competence3.setCategorie("TECHNIQUE");

        // Créer un CV de test
        cv = new CV();
        cv.setId(1L);
        cv.setCompetences(new ArrayList<>(Arrays.asList(competence1, competence2, competence3)));

        // Créer un candidat de test
        candidat = new Candidat();
        candidat.setId(1L);
        candidat.setNom("Test");
        candidat.setEmail("test@test.com");
        candidat.setNiveauEtude(NiveauEtude.INGENIEUR);
        candidat.setCv(cv);

        // Créer une offre de test
        offre = new Offre();
        offre.setId(1L);
        offre.setTitre("Test Offre");
        offre.setTypeOffre(TypeOffre.EMPLOI);
        offre.setNiveauEtudeRequis(NiveauEtude.INGENIEUR);
        offre.setCompetences(new ArrayList<>(Arrays.asList(competence1, competence2, competence3)));
    }

    @Test
    void testCalculateMatchingScore_100Percent_AllSkillsMatch() {
        // Given: Candidat et Offre avec les mêmes compétences
        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (100%)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_0Percent_NoSkillsMatch() {
        // Given: Candidat et Offre avec des compétences différentes
        Competence compCandidat = new Competence();
        compCandidat.setId(4L);
        compCandidat.setNom("react");

        Competence compOffre = new Competence();
        compOffre.setId(5L);
        compOffre.setNom("angular");

        cv.setCompetences(new ArrayList<>(Arrays.asList(compCandidat)));
        offre.setCompetences(new ArrayList<>(Arrays.asList(compOffre)));

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 0.0 (0%)
        assertEquals(0.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_50Percent_HalfSkillsMatch() {
        // Given: Candidat avec 2 compétences, Offre avec 4 compétences (2 en commun)
        Competence compCandidat1 = new Competence();
        compCandidat1.setId(1L);
        compCandidat1.setNom("java");

        Competence compCandidat2 = new Competence();
        compCandidat2.setId(2L);
        compCandidat2.setNom("python");

        Competence compOffre1 = new Competence();
        compOffre1.setId(1L);
        compOffre1.setNom("java");

        Competence compOffre2 = new Competence();
        compOffre2.setId(2L);
        compOffre2.setNom("python");

        Competence compOffre3 = new Competence();
        compOffre3.setId(3L);
        compOffre3.setNom("sql");

        Competence compOffre4 = new Competence();
        compOffre4.setId(4L);
        compOffre4.setNom("git");

        cv.setCompetences(new ArrayList<>(Arrays.asList(compCandidat1, compCandidat2)));
        offre.setCompetences(new ArrayList<>(Arrays.asList(compOffre1, compOffre2, compOffre3, compOffre4)));

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 0.5 (50%)
        assertEquals(0.5, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_CaseInsensitive() {
        // Given: Compétences avec différentes casses
        Competence compCandidat = new Competence();
        compCandidat.setId(1L);
        compCandidat.setNom("JAVA");

        Competence compOffre = new Competence();
        compOffre.setId(2L);
        compOffre.setNom("java");

        cv.setCompetences(new ArrayList<>(Arrays.asList(compCandidat)));
        offre.setCompetences(new ArrayList<>(Arrays.asList(compOffre)));

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (match insensible à la casse)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_PartialMatch() {
        // Given: Compétence candidat contient une partie de la compétence offre
        Competence compCandidat = new Competence();
        compCandidat.setId(1L);
        compCandidat.setNom("spring");

        Competence compOffre = new Competence();
        compOffre.setId(2L);
        compOffre.setNom("spring boot");

        cv.setCompetences(new ArrayList<>(Arrays.asList(compCandidat)));
        offre.setCompetences(new ArrayList<>(Arrays.asList(compOffre)));

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (match partiel)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_Eligible_SameLevel() {
        // Given: Candidat et Offre avec le même niveau
        candidat.setNiveauEtude(NiveauEtude.INGENIEUR);
        offre.setNiveauEtudeRequis(NiveauEtude.INGENIEUR);
        offre.setTypeOffre(TypeOffre.EMPLOI);

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (éligible)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_Eligible_HigherLevel() {
        // Given: Candidat avec niveau supérieur à l'offre
        candidat.setNiveauEtude(NiveauEtude.DOCTORAT);
        offre.setNiveauEtudeRequis(NiveauEtude.INGENIEUR);
        offre.setTypeOffre(TypeOffre.EMPLOI);

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (éligible)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_NotEligible_LowerLevel_Emploi() {
        // Given: Candidat avec niveau inférieur pour un EMPLOI
        candidat.setNiveauEtude(NiveauEtude.LICENCE);
        offre.setNiveauEtudeRequis(NiveauEtude.INGENIEUR);
        offre.setTypeOffre(TypeOffre.EMPLOI);

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 0.0 (non éligible)
        assertEquals(0.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_Eligible_Stage_LowerLevel() {
        // Given: Candidat avec niveau inférieur pour un STAGE
        candidat.setNiveauEtude(NiveauEtude.LICENCE);
        offre.setNiveauEtudeRequis(NiveauEtude.INGENIEUR);
        offre.setTypeOffre(TypeOffre.STAGE);

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (éligible - stage accepte niveau inférieur)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_Eligible_NullCandidatLevel() {
        // Given: Candidat sans niveau d'étude défini
        candidat.setNiveauEtude(null);
        offre.setNiveauEtudeRequis(NiveauEtude.INGENIEUR);
        offre.setTypeOffre(TypeOffre.EMPLOI);

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (éligible - calcul basé sur compétences uniquement)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_Eligible_NullOffreLevel() {
        // Given: Offre sans niveau requis
        candidat.setNiveauEtude(NiveauEtude.LICENCE);
        offre.setNiveauEtudeRequis(null);
        offre.setTypeOffre(TypeOffre.EMPLOI);

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (éligible)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_EmptyCompetences() {
        // Given: Candidat et Offre sans compétences
        cv.setCompetences(new ArrayList<>());
        offre.setCompetences(new ArrayList<>());

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 0.0
        assertEquals(0.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_CandidatHasMoreSkills() {
        // Given: Candidat a plus de compétences que l'offre
        Competence compExtra = new Competence();
        compExtra.setId(4L);
        compExtra.setNom("git");

        cv.setCompetences(new ArrayList<>(Arrays.asList(competence1, competence2, competence3, compExtra)));
        offre.setCompetences(new ArrayList<>(Arrays.asList(competence1, competence2, competence3)));

        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.of(offre));

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 1.0 (toutes les compétences de l'offre sont couvertes)
        assertEquals(1.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_NullCandidat() {
        // Given: Candidat null
        when(candidatRepository.findById(1L)).thenReturn(Optional.empty());

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 0.0
        assertEquals(0.0, score, 0.01);
    }

    @Test
    void testCalculateMatchingScore_NullOffre() {
        // Given: Offre null
        when(candidatRepository.findById(1L)).thenReturn(Optional.of(candidat));
        when(offreRepository.findById(1L)).thenReturn(Optional.empty());

        // When: Calculer le score
        double score = matchingService.calculateMatchingScore(1L, 1L);

        // Then: Score doit être 0.0
        assertEquals(0.0, score, 0.01);
    }
}
