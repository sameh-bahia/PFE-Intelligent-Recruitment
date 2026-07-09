import { useState } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { FileQuestion, ArrowLeft, Save, Plus, Trash2 } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

/**
 * Composant CreerQuiz - Interface de création de quiz pour les recruteurs
 * 
 * Ce composant permet aux recruteurs de créer un quiz technique avec des questions
 * et des options de réponse. Le recruteur peut spécifier quelle option est correcte.
 * 
 * FONCTIONNALITÉS :
 * - Créer un quiz avec un titre et une durée
 * - Ajouter dynamiquement des questions
 * - Pour chaque question, ajouter 4 options de réponse
 * - Spécifier l'option correcte pour chaque question
 * - Définir le nombre de points par question
 * 
 * LOGIQUE MÉTIER : Création progressive
 * - Le quiz est créé d'abord (POST /api/quiz/offre/{offreId})
 * - Ensuite, les questions sont créées une par une (POST /api/questions)
 * - Enfin, les options sont créées pour chaque question (POST /api/options)
 * - Cette approche permet une construction flexible du quiz
 * 
 * SÉCURITÉ : Le champ isCorrect est visible ici
 * - Ce composant est réservé aux recruteurs (voir ProtectedRoute)
 * - Les recruteurs peuvent voir et modifier isCorrect
 * - Les candidats ne voient JAMAIS ce champ (voir PasserQuiz.tsx)
 */

interface Option {
  texte: string;
  isCorrect: boolean;
}

interface Question {
  enonce: string;
  points: number;
  options: Option[];
}

export default function CreerQuiz() {
  const { offreId } = useParams<{ offreId: string }>();
  const navigate = useNavigate();
  
  const [quizData, setQuizData] = useState({
    titre: '',
    dureeMinutes: 15
  });
  
  const [questions, setQuestions] = useState<Question[]>([]);
  const [error, setError] = useState('');

  /**
   * Ajouter une nouvelle question au quiz
   * 
   * Cette fonction crée une nouvelle question avec 4 options vides.
   * Les options sont initialisées avec isCorrect = false par défaut.
   * Le recruteur pourra ensuite cocher l'option correcte.
   */
  const addQuestion = () => {
    setQuestions([...questions, {
      enonce: '',
      points: 1,
      options: [
        { texte: '', isCorrect: false },
        { texte: '', isCorrect: false },
        { texte: '', isCorrect: false },
        { texte: '', isCorrect: false }
      ]
    }]);
  };

  /**
   * Supprimer une question du quiz
   * 
   * @param index L'index de la question à supprimer
   */
  const removeQuestion = (index: number) => {
    setQuestions(questions.filter((_, i) => i !== index));
  };

  /**
   * Mettre à jour un champ d'une question
   * 
   * @param index L'index de la question
   * @param field Le champ à modifier ('enonce' ou 'points')
   * @param value La nouvelle valeur
   */
  const updateQuestion = (index: number, field: keyof Question, value: string | number) => {
    const updatedQuestions = [...questions];
    updatedQuestions[index] = { ...updatedQuestions[index], [field]: value };
    setQuestions(updatedQuestions);
  };

  /**
   * Mettre à jour un champ d'une option
   * 
   * @param questionIndex L'index de la question
   * @param optionIndex L'index de l'option
   * @param field Le champ à modifier ('texte' ou 'isCorrect')
   * @param value La nouvelle valeur
   */
  const updateOption = (questionIndex: number, optionIndex: number, field: keyof Option, value: string | boolean) => {
    const updatedQuestions = [...questions];
    updatedQuestions[questionIndex].options[optionIndex] = {
      ...updatedQuestions[questionIndex].options[optionIndex],
      [field]: value
    };
    setQuestions(updatedQuestions);
  };

  /**
   * Soumettre le formulaire et créer le quiz
   * 
   * Cette fonction effectue la création du quiz en 3 étapes :
   * 1. Créer le quiz lié à l'offre (POST /api/quiz/offre/{offreId})
   * 2. Pour chaque question, créer la question (POST /api/questions)
   * 3. Pour chaque option de chaque question, créer l'option (POST /api/options)
   * 
   * LOGIQUE MÉTIER : Création séquentielle
   * - Le quiz doit être créé en premier pour obtenir quizId
   * - Les questions doivent être créées ensuite pour obtenir questionId
   * - Les options sont créées en dernier avec questionId
   * 
   * SÉCURITÉ : Le champ isCorrect est envoyé au backend
   * - Seul le recruteur peut spécifier isCorrect
   * - Le backend stocke cette information en base de données
   * - Les candidats ne verront jamais ce champ
   */
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    if (questions.length === 0) {
      setError('Veuillez ajouter au moins une question');
      return;
    }

    try {
      // Étape 1 : Créer le quiz lié à l'offre
      const quizResponse = await api.post(`/quiz/offre/${offreId}`, quizData);
      const quizId = quizResponse.data.id;

      // Étape 2 : Créer les questions une par une
      for (const question of questions) {
        const questionResponse = await api.post('/questions', {
          enonce: question.enonce,
          points: question.points,
          quizId
        });

        const questionId = questionResponse.data.id;

        // Étape 3 : Créer les options pour cette question
        for (const option of question.options) {
          await api.post('/options', {
            texte: option.texte,
            isCorrect: option.isCorrect,
            questionId
          });
        }
      }

      console.log('Quiz créé avec succès');
      navigate(`/dashboard/recruteur/offres`);
    } catch (err) {
      setError('Erreur lors de la création du quiz');
      console.error('Error creating quiz:', err);
    }
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
            <FileQuestion className="w-8 h-8 text-[#3B82F6]" />
          </div>
          <div>
            <h1 className="text-3xl font-bold text-[#1E293B]">Créer un quiz</h1>
            <p className="text-gray-600 mt-1">Ajoutez des questions pour évaluer les candidats</p>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          <div className="grid grid-cols-2 gap-6">
            <div>
              <label htmlFor="titre" className="block text-lg font-semibold text-[#1E293B] mb-3">
                Titre du quiz
              </label>
              <input
                id="titre"
                name="titre"
                type="text"
                required
                value={quizData.titre}
                onChange={(e) => setQuizData({ ...quizData, titre: e.target.value })}
                className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                placeholder="Ex: Quiz Java Senior"
              />
            </div>

            <div>
              <label htmlFor="dureeMinutes" className="block text-lg font-semibold text-[#1E293B] mb-3">
                Durée (minutes)
              </label>
              <input
                id="dureeMinutes"
                name="dureeMinutes"
                type="number"
                required
                min="1"
                value={quizData.dureeMinutes}
                onChange={(e) => setQuizData({ ...quizData, dureeMinutes: parseInt(e.target.value) })}
                className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                placeholder="Ex: 15"
              />
            </div>
          </div>

          <div className="border-t border-gray-200 pt-8">
            <div className="flex items-center justify-between mb-6">
              <h2 className="text-2xl font-bold text-[#1E293B]">Questions</h2>
              <button
                type="button"
                onClick={addQuestion}
                className="px-4 py-2 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors font-medium flex items-center gap-2"
              >
                <Plus className="w-5 h-5" />
                Ajouter une question
              </button>
            </div>

            {questions.length === 0 ? (
              <div className="text-center py-12 bg-gray-50 rounded-xl border-2 border-dashed border-gray-200">
                <FileQuestion className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-500">Aucune question ajoutée</p>
                <p className="text-sm text-gray-400 mt-1">Cliquez sur "Ajouter une question" pour commencer</p>
              </div>
            ) : (
              <div className="space-y-6">
                {questions.map((question, qIndex) => (
                  <div key={qIndex} className="bg-gray-50 rounded-xl p-6 border border-gray-200">
                    <div className="flex items-center justify-between mb-4">
                      <h3 className="text-lg font-semibold text-[#1E293B]">Question {qIndex + 1}</h3>
                      <button
                        type="button"
                        onClick={() => removeQuestion(qIndex)}
                        className="text-red-500 hover:text-red-700 transition-colors"
                      >
                        <Trash2 className="w-5 h-5" />
                      </button>
                    </div>

                    <div className="space-y-4">
                      <div>
                        <label className="block text-sm font-medium text-[#1E293B] mb-2">
                          Énoncé de la question
                        </label>
                        <textarea
                          value={question.enonce}
                          onChange={(e) => updateQuestion(qIndex, 'enonce', e.target.value)}
                          rows={3}
                          required
                          className="w-full px-4 py-3 bg-white border border-gray-200 rounded-lg focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                          placeholder="Ex: Quelle est la différence entre GET et POST ?"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-[#1E293B] mb-2">
                          Points
                        </label>
                        <input
                          type="number"
                          min="1"
                          value={question.points}
                          onChange={(e) => updateQuestion(qIndex, 'points', parseInt(e.target.value))}
                          required
                          className="w-32 px-4 py-3 bg-white border border-gray-200 rounded-lg focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                        />
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-[#1E293B] mb-2">
                          Options de réponse
                        </label>
                        <div className="space-y-3">
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
                                required
                                className="flex-1 px-4 py-3 bg-white border border-gray-200 rounded-lg focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all"
                                placeholder={`Option ${oIndex + 1}`}
                              />
                              <span className="text-sm text-gray-500">
                                {option.isCorrect ? '✓ Correcte' : ''}
                              </span>
                            </div>
                          ))}
                        </div>
                        <p className="text-sm text-gray-500 mt-2">
                          Cochez la case pour indiquer la réponse correcte
                        </p>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}
          </div>

          <div className="flex justify-end gap-4 pt-6 border-t border-gray-200">
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
              Créer le quiz
            </button>
          </div>
        </form>
      </div>
    </MainLayout>
  );
}
