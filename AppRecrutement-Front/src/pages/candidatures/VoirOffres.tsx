import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Briefcase, MapPin, DollarSign, Building2, ArrowRight, TrendingUp, X, Check, AlertCircle } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

export default function VoirOffres() {
  const [offres, setOffres] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const userName = localStorage.getItem('userName') || 'Candidat';
  const [matchingResult, setMatchingResult] = useState<any>(null);
  const [showModal, setShowModal] = useState(false);
  const [calculatingScore, setCalculatingScore] = useState(false);

  useEffect(() => {
    const fetchOffres = async () => {
      try {
        const response = await api.get('/offres');
        setOffres(response.data);
        setLoading(false);
      } catch (err) {
        setError('Erreur lors du chargement des offres');
        setLoading(false);
      }
    };

    fetchOffres();
  }, []);

  const calculateScore = async (offreId: number) => {
    setCalculatingScore(true);
    try {
      const response = await api.get(`/candidatures/calculate-score/${offreId}`);
      setMatchingResult(response.data);
      setShowModal(true);
    } catch (err) {
      setError('Erreur lors du calcul du score');
    } finally {
      setCalculatingScore(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#F8FAFC] flex items-center justify-center">
        <div className="text-gray-600">Chargement...</div>
      </div>
    );
  }

  return (
    <MainLayout role="CANDIDAT" userName={userName}>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[#1E293B]">Offres disponibles</h1>
        <p className="text-gray-600 mt-2">Découvrez les opportunités qui correspondent à votre profil</p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {offres.length === 0 ? (
        <div className="bg-white rounded-2xl shadow-sm p-12 text-center border border-[#E2E8F0]">
          <Briefcase className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600 text-lg">Aucune offre disponible pour le moment.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 gap-6">
          {offres.map((offre) => (
            <div key={offre.id} className="bg-white rounded-2xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 border border-[#E2E8F0] group">
              <div className="flex justify-between items-start">
                <div className="flex-1">
                  <h3 className="text-2xl font-semibold text-[#1E293B] mb-3 group-hover:text-[#3B82F6] transition-colors">
                    {offre.titre}
                  </h3>
                  <p className="text-gray-600 mb-4 line-clamp-3">
                    {offre.description}
                  </p>
                  <div className="flex flex-wrap gap-3 mb-4">
                    <span className="px-3 py-1.5 bg-[#3B82F6]/10 text-[#3B82F6] rounded-full text-sm font-medium flex items-center gap-1">
                      <Briefcase className="w-4 h-4" />
                      {offre.typeContrat}
                    </span>
                    <span className="px-3 py-1.5 bg-[#10B981]/10 text-[#10B981] rounded-full text-sm font-medium flex items-center gap-1">
                      <DollarSign className="w-4 h-4" />
                      {offre.salaire}
                    </span>
                    <span className="px-3 py-1.5 bg-[#334155]/10 text-[#334155] rounded-full text-sm font-medium flex items-center gap-1">
                      <MapPin className="w-4 h-4" />
                      {offre.lieu}
                    </span>
                  </div>
                  {offre.competences && offre.competences.length > 0 && (
                    <div className="mb-4">
                      <p className="text-sm font-medium text-gray-700 mb-2">Compétences requises :</p>
                      <div className="flex flex-wrap gap-2">
                        {offre.competences.map((comp: any, index: number) => (
                          <span key={index} className="px-2 py-1 bg-[#6366F1]/10 text-[#6366F1] rounded-full text-xs font-medium">
                            {comp.nom}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                  <div className="flex items-center gap-2 text-sm text-gray-500">
                    <Building2 className="w-4 h-4" />
                    {offre.recruteur?.nomEntreprise || 'Non spécifié'}
                  </div>
                </div>
                <div className="flex flex-col gap-2 ml-6">
                  <button
                    onClick={() => calculateScore(offre.id)}
                    disabled={calculatingScore}
                    className="px-6 py-3 bg-[#10B981] text-white rounded-lg hover:bg-[#059669] transition-colors font-medium flex items-center gap-2 disabled:opacity-50"
                  >
                    <TrendingUp className="w-4 h-4" />
                    {calculatingScore ? 'Calcul...' : 'Voir mon score'}
                  </button>
                  <Link
                    to={`/dashboard/candidat/offres/${offre.id}/postuler`}
                    className="px-6 py-3 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors font-medium flex items-center gap-2 group-hover:shadow-lg text-center"
                  >
                    Postuler
                    <ArrowRight className="w-4 h-4" />
                  </Link>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
      
      {showModal && matchingResult && (
        <div className="fixed inset-0 bg-black/50 flex items-center justify-center z-50">
          <div className="bg-white rounded-2xl shadow-xl max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
            <div className="p-6">
              <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-[#1E293B]">Résultat du matching</h2>
                <button
                  onClick={() => setShowModal(false)}
                  className="p-2 hover:bg-gray-100 rounded-lg transition-colors"
                >
                  <X className="w-6 h-6 text-gray-500" />
                </button>
              </div>
              
              <div className="mb-6">
                <div className="flex items-center justify-between mb-2">
                  <span className="text-lg font-semibold text-[#1E293B]">Score de compatibilité</span>
                  <span className="text-3xl font-bold text-[#10B981]">{matchingResult.scorePercentage}%</span>
                </div>
                <div className="w-full bg-gray-200 rounded-full h-4">
                  <div
                    className="bg-[#10B981] h-4 rounded-full transition-all duration-500"
                    style={{ width: `${matchingResult.scorePercentage}%` }}
                  ></div>
                </div>
              </div>

              {matchingResult.commonCompetences && matchingResult.commonCompetences.length > 0 && (
                <div className="mb-6">
                  <h3 className="text-lg font-semibold text-[#1E293B] mb-3 flex items-center gap-2">
                    <Check className="w-5 h-5 text-[#10B981]" />
                    Compétences communes
                  </h3>
                  <div className="flex flex-wrap gap-2">
                    {matchingResult.commonCompetences.map((comp: string, index: number) => (
                      <span key={index} className="px-3 py-1.5 bg-[#10B981]/10 text-[#10B981] rounded-full text-sm font-medium">
                        {comp}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              {matchingResult.missingCompetences && matchingResult.missingCompetences.length > 0 && (
                <div className="mb-6">
                  <h3 className="text-lg font-semibold text-[#1E293B] mb-3 flex items-center gap-2">
                    <AlertCircle className="w-5 h-5 text-[#F59E0B]" />
                    Compétences manquantes
                  </h3>
                  <p className="text-sm text-gray-600 mb-3">
                    Il vous manque {matchingResult.missingCompetences.length} compétence(s) pour atteindre 100%
                  </p>
                  <div className="flex flex-wrap gap-2">
                    {matchingResult.missingCompetences.map((comp: string, index: number) => (
                      <span key={index} className="px-3 py-1.5 bg-[#F59E0B]/10 text-[#F59E0B] rounded-full text-sm font-medium">
                        {comp}
                      </span>
                    ))}
                  </div>
                </div>
              )}

              <div className="mb-6 p-4 bg-blue-50 border border-blue-200 rounded-xl">
                <p className="text-sm font-medium text-blue-900 flex items-start gap-2">
                  <AlertCircle className="w-5 h-5 text-blue-600 flex-shrink-0 mt-0.5" />
                  <span>
                    <span className="font-semibold">Recommandation : </span>
                    {matchingResult.scorePercentage === 100 
                      ? 'Votre profil est parfait pour ce poste, postulez vite !'
                      : matchingResult.missingCompetences && matchingResult.missingCompetences.length > 0
                        ? `Pensez à mentionner vos expériences en ${matchingResult.missingCompetences[0]} pour valoriser votre profil.`
                        : 'Continuez à développer vos compétences pour maximiser vos chances.'
                    }
                  </span>
                </p>
              </div>

              <div className="flex justify-end gap-3 mt-8">
                <button
                  onClick={() => setShowModal(false)}
                  className="px-6 py-3 bg-gray-200 text-gray-700 rounded-lg hover:bg-gray-300 transition-colors font-medium"
                >
                  Fermer
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
}
