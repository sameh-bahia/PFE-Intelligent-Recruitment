import { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { Clock, FileQuestion, AlertCircle } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

/**
 * Composant PasserQuiz - Interface de passage de quiz pour les candidats
 * 
 * Ce composant permet aux candidats de passer un quiz technique avec un timer.
 * Le quiz est récupéré SANS le champ isCorrect (sécurité).
 * 
 * FONCTIONNALITÉS :
 * - Afficher le quiz avec ses questions et options
 * - Timer décomptant le temps restant
 * - Soumission automatique si le temps est écoulé
 * - Affichage des résultats après soumission
 * 
 * LOGIQUE MÉTIER : Timer anti-triche
 * - Le timer s'exécute côté frontend (affichage)
 * - Le temps écoulé est envoyé au backend pour validation
 * - Le backend vérifie que le temps n'a pas été dépassé (+30s tolérance)
 * - Empêche la triche par manipulation du timer frontend
 * 
 * SÉCURITÉ : Le champ isCorrect est ABSENT
 * - Le quiz est récupéré via GET /api/quiz/{id}/candidat
 * - Ce endpoint utilise QuizCandidatDTO (sans isCorrect)
 * - Les candidats ne peuvent pas voir les réponses correctes
 * - Même avec DevTools, le champ isCorrect n'est pas présent dans la réponse
 * 
 * INTERFACE Option : Notez l'absence du champ isCorrect
 * - Contrairement à CreerQuiz, ce composant n'a pas isCorrect
 * - C'est intentionnel pour la sécurité
 */

interface Option {
  id: number;
  texte: string;
  // IMPORTANT : Pas de champ isCorrect - les candidats ne voient pas les réponses correctes
}

interface Question {
  id: number;
  enonce: string;
  points: number;
  options: Option[];
}

interface Quiz {
  id: number;
  titre: string;
  dureeMinutes: number;
  questions: Question[];
}

export default function PasserQuiz() {
  const { quizId, offreId } = useParams<{ quizId: string; offreId?: string }>();
  const navigate = useNavigate();
  
  const [quiz, setQuiz] = useState<Quiz | null>(null);
  const [reponses, setReponses] = useState<Record<number, number>>({});
  const [tempsRestant, setTempsRestant] = useState<number>(0);
  const [tempsEcoule, setTempsEcoule] = useState<number>(0);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [soumis, setSoumis] = useState(false);

  /**
   * Charger le quiz depuis le backend
   * 
   * Cette fonction récupère le quiz via l'endpoint sécurisé pour les candidats.
   * Le quiz ne contient PAS le champ isCorrect (sécurité).
   * Le timer est initialisé avec la durée du quiz.
   */
  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        // Endpoint sécurisé : utilise QuizCandidatDTO (sans isCorrect)
        const response = await api.get(`/quiz/${quizId}/candidat`);
        setQuiz(response.data);
        const duree = response.data.dureeMinutes * 60;
        setTempsRestant(duree);
        setLoading(false);
      } catch (err) {
        console.error('PasserQuiz - Error fetching quiz:', err);
        setError('Erreur lors du chargement du quiz');
        setLoading(false);
      }
    };

    fetchQuiz();
  }, [quizId]);

  /**
   * Gérer le timer du quiz
   * 
   * Cette fonction décrémente le timer chaque seconde.
   * Si le temps atteint 0, le quiz est soumis automatiquement.
   * Le temps écoulé est tracké pour la validation anti-triche côté backend.
   * 
   * LOGIQUE MÉTIER : Soumission automatique
   * - Si le temps est écoulé, handleSubmit() est appelé automatiquement
   * - Le candidat ne peut pas continuer après la fin du temps
   * 
   * SÉCURITÉ : Validation backend
   * - Le temps écoulé est envoyé au backend
   * - Le backend vérifie que le temps n'a pas été dépassé (+30s tolérance)
   * - Empêche la triche par manipulation du timer frontend
   */
  useEffect(() => {
    if (!loading && tempsRestant > 0 && !soumis) {
      const timer = setInterval(() => {
        setTempsRestant((prev) => prev - 1);
        setTempsEcoule((prev) => prev + 1);
      }, 1000);
      return () => clearInterval(timer);
    } else if (!loading && tempsRestant === 0 && !soumis) {
      // Soumission automatique si le temps est écoulé
      handleSubmit();
    }
  }, [tempsRestant, soumis, loading]);

  /**
   * Enregistrer la réponse du candidat pour une question
   * 
   * @param questionId L'ID de la question
   * @param optionId L'ID de l'option sélectionnée
   */
  const handleReponseChange = (questionId: number, optionId: number) => {
    setReponses({ ...reponses, [questionId]: optionId });
  };

  /**
   * Soumettre les réponses du candidat
   * 
   * Cette fonction envoie les réponses au backend pour calcul du score.
   * Le temps écoulé est envoyé pour validation anti-triche.
   * Le backend calcule le score en comparant avec les options correctes en base.
   * 
   * SÉCURITÉ : Validation anti-triche
   * - Le temps écoulé est envoyé au backend
   * - Le backend vérifie que le temps n'a pas été dépassé (+30s tolérance)
   * - Le score est calculé côté backend (pas côté frontend)
   * 
   * LOGIQUE MÉTIER : Calcul du score
   * - Le backend compare les réponses avec les options correctes
   * - Le score est stocké dans l'entité Candidature
   * - Le résultat est renvoyé au frontend pour affichage
   * 
   * NOUVEAU FLOW : Redirection vers lettre de motivation
   * - Le score n'est PAS affiché au candidat
   * - Les réponses sont stockées pour envoi avec la lettre de motivation
   * - Redirection vers la page lettre de motivation
   */
  const handleSubmit = async () => {
    if (soumis) return;
    
    setSoumis(true);
    setError('');

    try {
      // Stocker les réponses dans localStorage pour envoi avec la lettre de motivation
      localStorage.setItem('quizReponses', JSON.stringify({
        quizId: parseInt(quizId),
        reponses,
        tempsEcouleSecondes: tempsEcoule
      }));
      
      // Rediriger vers la page lettre de motivation
      navigate(`/dashboard/candidat/offres/${offreId}/lettre-motivation`);
    } catch (err) {
      setError('Erreur lors de la soumission du quiz');
      setSoumis(false);
    }
  };

  /**
   * Formater le temps en format MM:SS
   * 
   * @param seconds Le temps en secondes
   * @return Le temps formaté (ex: "15:30")
   */
  const formatTime = (seconds: number) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  if (loading) {
    return (
      <MainLayout role="CANDIDAT" userName="Candidat">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-[#3B82F6] mx-auto mb-4"></div>
            <p className="text-gray-600">Chargement du quiz...</p>
          </div>
        </div>
      </MainLayout>
    );
  }

  if (error && !quiz) {
    return (
      <MainLayout role="CANDIDAT" userName="Candidat">
        <div className="flex items-center justify-center min-h-[400px]">
          <div className="text-center">
            <AlertCircle className="w-16 h-16 text-red-500 mx-auto mb-4" />
            <p className="text-red-600 text-lg">{error}</p>
            <Link
              to="/dashboard/candidat/offres"
              className="mt-4 inline-block text-[#3B82F6] hover:text-[#2563EB]"
            >
              Retour aux offres
            </Link>
          </div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="CANDIDAT" userName="Candidat">
      <div className="mb-8">
        <Link
          to="/dashboard/candidat/offres"
          className="text-[#3B82F6] hover:text-[#2563EB] font-medium inline-flex items-center gap-2"
        >
          ← Retour aux offres
        </Link>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-8 border border-[#E2E8F0]">
        <div className="flex items-center justify-between mb-8">
          <div className="flex items-center gap-4">
            <div className="p-4 bg-[#3B82F6]/10 rounded-xl">
              <FileQuestion className="w-8 h-8 text-[#3B82F6]" />
            </div>
            <div>
              <h1 className="text-3xl font-bold text-[#1E293B]">{quiz?.titre}</h1>
              <p className="text-gray-600 mt-1">Répondez à toutes les questions</p>
            </div>
          </div>
          <div className={`flex items-center gap-2 px-4 py-2 rounded-lg ${tempsRestant < 60 ? 'bg-red-100 text-red-700' : 'bg-blue-100 text-blue-700'}`}>
            <Clock className="w-5 h-5" />
            <span className="font-mono text-xl font-bold">{formatTime(tempsRestant)}</span>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
            {error}
          </div>
        )}

        <div className="space-y-8">
          {quiz?.questions.map((question, index) => (
            <div key={question.id} className="bg-gray-50 rounded-xl p-6 border border-gray-200">
              <div className="flex items-start gap-4 mb-4">
                <div className="w-10 h-10 bg-[#3B82F6] text-white rounded-full flex items-center justify-center font-bold flex-shrink-0">
                  {index + 1}
                </div>
                <div className="flex-1">
                  <p className="text-lg font-medium text-[#1E293B] mb-2">{question.enonce}</p>
                  <span className="text-sm text-gray-500">{question.points} point{question.points > 1 ? 's' : ''}</span>
                </div>
              </div>

              <div className="space-y-3 ml-14">
                {question.options.map((option) => (
                  <label
                    key={option.id}
                    className={`flex items-center gap-3 p-4 rounded-lg border-2 cursor-pointer transition-all ${
                      reponses[question.id] === option.id
                        ? 'border-[#3B82F6] bg-blue-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <input
                      type="radio"
                      name={`question-${question.id}`}
                      value={option.id}
                      checked={reponses[question.id] === option.id}
                      onChange={() => handleReponseChange(question.id, option.id)}
                      className="w-5 h-5 text-[#3B82F6]"
                    />
                    <span className="text-gray-700">{option.texte}</span>
                  </label>
                ))}
              </div>
            </div>
          ))}
        </div>

        <div className="flex justify-between items-center mt-8 pt-6 border-t border-gray-200">
          <p className="text-gray-500">
            {Object.keys(reponses).length} / {quiz?.questions.length} questions répondues
          </p>
          <button
            onClick={handleSubmit}
            disabled={Object.keys(reponses).length !== quiz?.questions.length || soumis}
            className="px-6 py-3 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] transition-colors font-bold text-lg disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            {soumis ? 'Soumission...' : 'Soumettre le quiz'}
          </button>
        </div>
      </div>
    </MainLayout>
  );
}
