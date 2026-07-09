import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { Briefcase, ArrowLeft, Send } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

export default function CompleterCandidature() {
  const { candidatureId } = useParams<{ candidatureId: string }>();
  const navigate = useNavigate();
  const [candidature, setCandidature] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [formData, setFormData] = useState({
    lettreMotivation: ''
  });
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchCandidature = async () => {
      try {
        const response = await api.get(`/candidatures/${candidatureId}`);
        setCandidature(response.data);
        // Pré-remplir la lettre de motivation si elle existe déjà
        if (response.data.lettreMotivation) {
          setFormData({ lettreMotivation: response.data.lettreMotivation });
        }
        setLoading(false);
      } catch (err) {
        setError('Erreur lors du chargement de la candidature');
        setLoading(false);
      }
    };

    fetchCandidature();
  }, [candidatureId]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      await api.put(`/candidatures/${candidatureId}`, {
        lettreMotivation: formData.lettreMotivation
      });
      console.log('Candidature complétée avec succès');
      navigate('/dashboard/candidat/candidatures');
    } catch (err) {
      setError('Erreur lors de la mise à jour de la candidature');
      console.error('Error updating candidature:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  if (loading) {
    return (
      <MainLayout role="CANDIDAT" userName="Candidat">
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Chargement...</div>
        </div>
      </MainLayout>
    );
  }

  if (error && !candidature) {
    return (
      <MainLayout role="CANDIDAT" userName="Candidat">
        <div className="flex items-center justify-center h-64">
          <div className="text-red-600">{error}</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="CANDIDAT" userName="Candidat">
      <div className="mb-8">
        <Link
          to="/dashboard/candidat/candidatures"
          className="text-[#3B82F6] hover:text-[#2563EB] font-medium inline-flex items-center gap-2"
        >
          <ArrowLeft className="w-5 h-5" />
          Retour aux candidatures
        </Link>
      </div>

      <div className="bg-white rounded-2xl shadow-sm p-8 border border-[#E2E8F0]">
        <div className="flex items-center gap-4 mb-8">
          <div className="p-4 bg-[#3B82F6]/10 rounded-xl">
            <Briefcase className="w-8 h-8 text-[#3B82F6]" />
          </div>
          <div>
            <h1 className="text-3xl font-bold text-[#1E293B]">Compléter votre candidature</h1>
            <p className="text-gray-600 mt-1">Ajoutez votre lettre de motivation</p>
          </div>
        </div>

        {candidature && candidature.offre && (
          <div className="mb-8 p-6 bg-[#F8FAFC] rounded-xl border border-[#E2E8F0]">
            <h2 className="text-2xl font-bold text-[#1E293B] mb-2">{candidature.offre.titre}</h2>
            <p className="text-gray-600 text-lg">{candidature.offre.typeOffre} • {candidature.offre.salaire} • {candidature.offre.lieu}</p>
          </div>
        )}

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
              to="/dashboard/candidat/candidatures"
              className="px-6 py-3 border border-gray-200 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors font-medium"
            >
              Annuler
            </Link>
            <button
              type="submit"
              className="px-6 py-3 h-14 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] transition-colors font-bold text-lg flex items-center gap-2"
            >
              <Send className="w-5 h-5" />
              Envoyer la candidature
            </button>
          </div>
        </form>
      </div>
    </MainLayout>
  );
}
