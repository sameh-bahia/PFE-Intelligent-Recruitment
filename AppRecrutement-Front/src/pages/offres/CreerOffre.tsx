import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Briefcase, ArrowLeft, Save, Plus, Trash2 } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Question {
  enonce: string;
  points: number;
  options: Option[];
}

interface Option {
  texte: string;
  isCorrect: boolean;
}

export default function CreerOffre() {
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    typeOffre: '',
    sousDomaineIT: '',
    niveauEtudeRequis: '',
    salaire: '',
    lieu: '',
    competences: ''
  });
  const [creerQuiz, setCreerQuiz] = useState(false);
  const [quizData, setQuizData] = useState({
    titre: '',
    dureeMinutes: 15
  });
  const [questions, setQuestions] = useState<Question[]>([]);
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const addQuestion = () => {
    setQuestions([...questions, { enonce: '', points: 1, options: [{ texte: '', isCorrect: false }, { texte: '', isCorrect: false }, { texte: '', isCorrect: false }, { texte: '', isCorrect: false }] }]);
  };

  const removeQuestion = (index: number) => {
    setQuestions(questions.filter((_, i) => i !== index));
  };

  const updateQuestion = (index: number, field: keyof Question, value: string | number) => {
    const updated = [...questions];
    updated[index][field] = value as never;
    setQuestions(updated);
  };

  const updateOption = (questionIndex: number, optionIndex: number, field: keyof Option, value: string | boolean) => {
    const updated = [...questions];
    updated[questionIndex].options[optionIndex][field] = value as never;
    setQuestions(updated);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      if (creerQuiz) {
        // Créer l'offre avec le quiz complet en une seule requête
        const payload = {
          ...formData,
          competences: formData.competences.split(',').map(c => c.trim()).filter(c => c),
          quiz: {
            titre: quizData.titre || 'Quiz Technique',
            dureeMinutes: quizData.dureeMinutes,
            questions: questions
          }
        };
        const response = await api.post('/offres/with-quiz', payload);
        console.log('Offre créée avec quiz:', response.data);
        navigate('/dashboard/recruteur/offres');
      } else {
        // Créer l'offre sans quiz
        const payload = {
          ...formData,
          competences: formData.competences.split(',').map(c => c.trim()).filter(c => c)
        };
        const response = await api.post('/offres', payload);
        console.log('Offre créée:', response.data);
        navigate('/dashboard/recruteur/offres');
      }
    } catch (err) {
      setError('Erreur lors de la création de l\'offre');
      console.error('Error creating offre:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <MainLayout role="RECRUTEUR" userName="Recruteur">
      <div className="mb-8">
        <Link
          to="/dashboard/recruteur/offres"
          className="text-[#3B82F6] hover:text-[#2563EB] font-medium inline-flex items-center gap-2"
        >
          <ArrowLeft className="w-5 h-5" />
          Retour aux offres
        </Link>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-8 border border-[#E2E8F0]">
        <div className="flex items-center gap-4 mb-8">
          <div className="p-4 bg-[#3B82F6]/10 rounded-xl">
            <Briefcase className="w-8 h-8 text-[#3B82F6]" />
          </div>
          <div>
            <h1 className="text-3xl font-bold text-[#1E293B]">Créer une nouvelle offre</h1>
            <p className="text-gray-600 mt-1">Remplissez les informations pour publier votre offre</p>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          <div>
            <label htmlFor="titre" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Titre de l'offre
            </label>
            <input
              id="titre"
              name="titre"
              type="text"
              required
              value={formData.titre}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
              placeholder="Ex: Développeur Full Stack Senior"
            />
          </div>

          <div>
            <label htmlFor="description" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              required
              rows={6}
              value={formData.description}
              onChange={handleChange}
              className="w-full px-5 py-4 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
              placeholder="Décrivez le poste, les responsabilités et les compétences requises..."
            />
          </div>

          <div>
            <label htmlFor="typeOffre" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Type d'offre
            </label>
            <select
              id="typeOffre"
              name="typeOffre"
              required
              value={formData.typeOffre}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="EMPLOI">Emploi</option>
              <option value="STAGE">Stage</option>
              <option value="ALTERNANCE">Alternance</option>
              <option value="FREELANCE">Freelance</option>
            </select>
          </div>

          <div>
            <label htmlFor="salaire" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Salaire
            </label>
            <input
              id="salaire"
              name="salaire"
              type="text"
              required
              placeholder="Ex: 3000€ - 4000€"
              value={formData.salaire}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            />
          </div>

          <div>
            <label htmlFor="lieu" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Lieu
            </label>
            <input
              id="lieu"
              name="lieu"
              type="text"
              required
              placeholder="Ex: Paris, Tunis, Remote"
              value={formData.lieu}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            />
          </div>

          <div>
            <label htmlFor="sousDomaineIT" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Sous-domaine IT
            </label>
            <select
              id="sousDomaineIT"
              name="sousDomaineIT"
              required
              value={formData.sousDomaineIT}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="DEVELOPPEMENT">Développement</option>
              <option value="DATA_SCIENCE">Data Science</option>
              <option value="DEVOPS">DevOps</option>
              <option value="CYBERSECURITE">Cybersécurité</option>
              <option value="GESTION_PROJET">Gestion de Projet</option>
              <option value="QA">Quality Assurance</option>
            </select>
          </div>

          <div>
            <label htmlFor="niveauEtudeRequis" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Niveau d'étude requis
            </label>
            <select
              id="niveauEtudeRequis"
              name="niveauEtudeRequis"
              required
              value={formData.niveauEtudeRequis}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="BAC">BAC</option>
              <option value="DUT_BTS">DUT/BTS</option>
              <option value="LICENCE">Licence</option>
              <option value="MASTER">Master</option>
              <option value="INGENIEUR">Ingénieur</option>
              <option value="DOCTORAT">Doctorat</option>
              <option value="SANS_EXIGENCE">Sans exigence</option>
            </select>
          </div>

          <div>
            <label htmlFor="competences" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Compétences requises
            </label>
            <textarea
              id="competences"
              name="competences"
              rows={3}
              value={formData.competences}
              onChange={handleChange}
              className="w-full px-5 py-4 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
              placeholder="Entrez les compétences requises, séparées par des virgules (ex: Java, Spring, PostgreSQL, Docker)"
            />
            <p className="text-sm text-gray-500 mt-2">
              Séparez les compétences par des virgules
            </p>
          </div>

          <div className="flex items-center gap-3 p-4 bg-blue-50 rounded-xl border border-blue-200">
            <input
              type="checkbox"
              id="creerQuiz"
              checked={creerQuiz}
              onChange={(e) => setCreerQuiz(e.target.checked)}
              className="w-5 h-5 text-[#3B82F6] rounded focus:ring-[#3B82F6]"
            />
            <label htmlFor="creerQuiz" className="text-gray-700">
              <span className="font-semibold">Ajouter un quiz technique</span>
              <span className="text-gray-500 text-sm ml-2">- Les candidats devront passer un quiz avant de postuler</span>
            </label>
          </div>

          {creerQuiz && (
            <div className="space-y-6 p-6 bg-gray-50 rounded-xl border border-gray-200">
              <h3 className="text-xl font-bold text-[#1E293B]">Configuration du Quiz</h3>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label htmlFor="quizTitre" className="block text-sm font-semibold text-[#1E293B] mb-2">
                    Titre du quiz
                  </label>
                  <input
                    id="quizTitre"
                    type="text"
                    value={quizData.titre}
                    onChange={(e) => setQuizData({ ...quizData, titre: e.target.value })}
                    className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                    placeholder="Quiz Technique"
                  />
                </div>
                <div>
                  <label htmlFor="quizDuree" className="block text-sm font-semibold text-[#1E293B] mb-2">
                    Durée (minutes)
                  </label>
                  <input
                    id="quizDuree"
                    type="number"
                    value={quizData.dureeMinutes}
                    onChange={(e) => setQuizData({ ...quizData, dureeMinutes: parseInt(e.target.value) || 15 })}
                    className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                    min="1"
                  />
                </div>
              </div>

              <div className="space-y-4">
                <div className="flex items-center justify-between">
                  <h4 className="text-lg font-semibold text-[#1E293B]">Questions</h4>
                  <button
                    type="button"
                    onClick={addQuestion}
                    className="flex items-center gap-2 px-4 py-2 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors"
                  >
                    <Plus className="w-4 h-4" />
                    Ajouter une question
                  </button>
                </div>

                {questions.map((question, qIndex) => (
                  <div key={qIndex} className="p-4 bg-white rounded-xl border border-gray-200 space-y-4">
                    <div className="flex items-center justify-between">
                      <span className="font-semibold text-[#1E293B]">Question {qIndex + 1}</span>
                      <button
                        type="button"
                        onClick={() => removeQuestion(qIndex)}
                        className="text-red-500 hover:text-red-700"
                      >
                        <Trash2 className="w-5 h-5" />
                      </button>
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-[#1E293B] mb-2">
                        Énoncé de la question
                      </label>
                      <textarea
                        value={question.enonce}
                        onChange={(e) => updateQuestion(qIndex, 'enonce', e.target.value)}
                        rows={2}
                        className="w-full px-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                        placeholder="Entrez votre question..."
                      />
                    </div>

                    <div>
                      <label className="block text-sm font-semibold text-[#1E293B] mb-2">
                        Points
                      </label>
                      <input
                        type="number"
                        value={question.points}
                        onChange={(e) => updateQuestion(qIndex, 'points', parseInt(e.target.value) || 1)}
                        className="w-24 px-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                        min="1"
                      />
                    </div>

                    <div className="space-y-2">
                      <label className="block text-sm font-semibold text-[#1E293B] mb-2">
                        Options de réponse
                      </label>
                      {question.options.map((option, oIndex) => (
                        <div key={oIndex} className="flex items-center gap-3">
                          <input
                            type="checkbox"
                            checked={option.isCorrect}
                            onChange={(e) => updateOption(qIndex, oIndex, 'isCorrect', e.target.checked)}
                            className="w-5 h-5 text-[#3B82F6] rounded focus:ring-[#3B82F6]"
                          />
                          <input
                            type="text"
                            value={option.texte}
                            onChange={(e) => updateOption(qIndex, oIndex, 'texte', e.target.value)}
                            className="flex-1 px-4 py-3 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                            placeholder={`Option ${oIndex + 1}`}
                          />
                        </div>
                      ))}
                    </div>
                  </div>
                ))}
              </div>
            </div>
          )}

          <div className="flex justify-end gap-4 pt-6">
            <Link
              to="/dashboard/recruteur/offres"
              className="px-6 py-3 border border-gray-200 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors font-medium"
            >
              Annuler
            </Link>
            <button
              type="submit"
              className="px-6 py-3 h-14 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] transition-colors font-bold text-lg flex items-center gap-2"
            >
              <Save className="w-5 h-5" />
              Créer l'offre
            </button>
          </div>
        </form>
      </div>
    </MainLayout>
  );
}
