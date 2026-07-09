import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Briefcase, ArrowLeft, Send } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

export default function LettreMotivationApresQuiz() {
  const { offreId } = useParams<{ offreId: string }>();
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    lettreMotivation: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    // Vérifier si les réponses du quiz sont stockées
    const quizReponses = localStorage.getItem('quizReponses');
    if (!quizReponses) {
      setError('Aucune réponse de quiz trouvée. Veuillez repasser le quiz.');
    }
  }, []);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      const quizReponses = localStorage.getItem('quizReponses');
      if (!quizReponses) {
        throw new Error('Aucune réponse de quiz trouvée');
      }

      const parsedQuizReponses = JSON.parse(quizReponses);

      // Envoyer la candidature avec lettre de motivation
      const candidatureResponse = await api.post('/candidatures', {
        offreId: offreId,
        lettreMotivation: formData.lettreMotivation
      });

      const candidatureId = candidatureResponse.data.id;

      // Envoyer les réponses du quiz
      await api.post(`/quiz/${parsedQuizReponses.quizId}/soumettre`, {
        candidatureId: candidatureId,
        reponses: parsedQuizReponses.reponses,
        tempsEcouleSecondes: parsedQuizReponses.tempsEcouleSecondes
      });

      // Nettoyer localStorage
      localStorage.removeItem('quizReponses');

      // Rediriger vers les candidatures
      navigate('/dashboard/candidat/candidatures');
    } catch (err) {
      setError('Erreur lors de la soumission de la candidature');
      console.error('Error submitting candidature:', err);
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <MainLayout role="CANDIDAT" userName="Candidat">
      <div className="mb-8">
        <Link
          to="/dashboard/candidat/offres"
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
            <h1 className="text-3xl font-bold text-[#1E293B]">Lettre de motivation</h1>
            <p className="text-gray-600 mt-1">Complétez votre candidature</p>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          <div>
            <label htmlFor="lettreMotivation" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Lettre de motivation
            </label>
            <textarea
              id="lettreMotivation"
              name="lettreMotivation"
              required
              rows={8}
              value={formData.lettreMotivation}
              onChange={handleChange}
              placeholder="Expliquez pourquoi vous êtes le candidat idéal pour ce poste..."
              className="w-full px-5 py-4 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            />
          </div>

          <div className="flex justify-end gap-4 pt-6">
            <Link
              to="/dashboard/candidat/offres"
              className="px-6 py-3 border border-gray-200 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors font-medium"
            >
              Annuler
            </Link>
            <button
              type="submit"
              disabled={loading}
              className="px-6 py-3 h-14 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] transition-colors font-bold text-lg flex items-center gap-2 disabled:bg-gray-300 disabled:cursor-not-allowed"
            >
              {loading ? 'Envoi en cours...' : (
                <>
                  <Send className="w-5 h-5" />
                  Envoyer la candidature
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </MainLayout>
  );
}
