package com.AppRecrutement.AppRecrutement.dto;

import java.util.List;

/**
 * DTO pour la création d'une offre avec son quiz complet (questions + options)
 * Permet de créer tout en une seule requête avec cascade
 */
public class OffreWithQuizDTO {
    private String titre;
    private String description;
    private String lieu;
    private String typeOffre;
    private String sousDomaineIT;
    private String niveauEtudeRequis;
    private String salaire;
    private String domaine;
    private List<String> competences;
    
    // Quiz imbriqué
    private QuizDTO quiz;

    public static class QuizDTO {
        private String titre;
        private Integer dureeMinutes;
        private List<QuestionDTO> questions;

        public static class QuestionDTO {
            private String enonce;
            private Integer points;
            private List<OptionDTO> options;

            public static class OptionDTO {
                private String texte;
                private Boolean isCorrect;

                public String getTexte() {
                    return texte;
                }

                public void setTexte(String texte) {
                    this.texte = texte;
                }

                public Boolean getIsCorrect() {
                    return isCorrect;
                }

                public void setIsCorrect(Boolean isCorrect) {
                    this.isCorrect = isCorrect;
                }
            }

            public String getEnonce() {
                return enonce;
            }

            public void setEnonce(String enonce) {
                this.enonce = enonce;
            }

            public Integer getPoints() {
                return points;
            }

            public void setPoints(Integer points) {
                this.points = points;
            }

            public List<OptionDTO> getOptions() {
                return options;
            }

            public void setOptions(List<OptionDTO> options) {
                this.options = options;
            }
        }

        public String getTitre() {
            return titre;
        }

        public void setTitre(String titre) {
            this.titre = titre;
        }

        public Integer getDureeMinutes() {
            return dureeMinutes;
        }

        public void setDureeMinutes(Integer dureeMinutes) {
            this.dureeMinutes = dureeMinutes;
        }

        public List<QuestionDTO> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuestionDTO> questions) {
            this.questions = questions;
        }
    }

    // Getters and Setters
    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(String typeOffre) {
        this.typeOffre = typeOffre;
    }

    public String getSousDomaineIT() {
        return sousDomaineIT;
    }

    public void setSousDomaineIT(String sousDomaineIT) {
        this.sousDomaineIT = sousDomaineIT;
    }

    public String getNiveauEtudeRequis() {
        return niveauEtudeRequis;
    }

    public void setNiveauEtudeRequis(String niveauEtudeRequis) {
        this.niveauEtudeRequis = niveauEtudeRequis;
    }

    public String getSalaire() {
        return salaire;
    }

    public void setSalaire(String salaire) {
        this.salaire = salaire;
    }

    public String getDomaine() {
        return domaine;
    }

    public void setDomaine(String domaine) {
        this.domaine = domaine;
    }

    public List<String> getCompetences() {
        return competences;
    }

    public void setCompetences(List<String> competences) {
        this.competences = competences;
    }

    public QuizDTO getQuiz() {
        return quiz;
    }

    public void setQuiz(QuizDTO quiz) {
        this.quiz = quiz;
    }
}
