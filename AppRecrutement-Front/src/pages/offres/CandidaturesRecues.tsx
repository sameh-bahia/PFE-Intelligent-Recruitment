import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, Briefcase, Check, X } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Candidature {
  id: number;
  offre: {
    id: number;
    titre: string;
  };
  candidat: {
    id: number;
    nom: string;
    email: string;
  };
  statut: string;
  dateCandidature: string;
}

export default function CandidaturesRecues() {
  const [candidatures, setCandidatures] = useState<Candidature[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchCandidatures();
  }, []);

  // Changement: Remplacement de '/candidatures' par '/candidatures/recruteur/candidatures-recues'
  // Pourquoi: Avant, l'API retournait TOUTES les candidatures de la BD
  // Maintenant, l'endpoint filtre par recruteur connecté via token JWT
  // Résultat: Chaque recruteur ne voit que les candidatures de ses propres offres
  const fetchCandidatures = async () => {
    try {
      const response = await api.get('/candidatures/recruteur/candidatures-recues');
      setCandidatures(response.data);
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement des candidatures');
      setLoading(false);
      console.error('Error fetching candidatures:', err);
    }
  };

  // Changement: Utilisation des valeurs d'enum correctes (sans accents)
  // Problème résolu: Le frontend envoyait 'ACCEPTÉE' et 'REFUSÉE' (avec accents)
  // mais l'enum backend utilise 'ACCEPTEE' et 'REFUSEE' (sans accents)
  const handleAccept = async (id: number) => {
    try {
      await api.put(`/candidatures/${id}/statut`, { statut: 'ACCEPTEE' });
      setCandidatures(candidatures.map(c =>
        c.id === id ? { ...c, statut: 'ACCEPTEE' } : c
      ));
    } catch (err) {
      setError('Erreur lors de l\'acceptation');
      console.error('Error accepting candidature:', err);
    }
  };

  const handleReject = async (id: number) => {
    try {
      await api.put(`/candidatures/${id}/statut`, { statut: 'REFUSEE' });
      setCandidatures(candidatures.map(c =>
        c.id === id ? { ...c, statut: 'REFUSEE' } : c
      ));
    } catch (err) {
      setError('Erreur lors du refus');
      console.error('Error rejecting candidature:', err);
    }
  };

  // Changement: Utilisation des valeurs d'enum correctes (sans accents)
  // Problème résolu: Le frontend comparait avec 'ACCEPTÉE' et 'REFUSÉE' (avec accents)
  // mais l'enum backend retourne 'ACCEPTEE' et 'REFUSEE' (sans accents)
  const getStatutBadge = (statut: string) => {
    switch (statut) {
      case 'ACCEPTEE':
        return (
          <span className="px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
            Acceptée
          </span>
        );
      case 'REFUSEE':
        return (
          <span className="px-3 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
            Refusée
          </span>
        );
      default:
        return (
          <span className="px-3 py-1 rounded-full text-xs font-medium bg-yellow-100 text-yellow-800">
            En attente
          </span>
        );
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Date invalide';
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  if (loading) {
    return (
      <MainLayout role="RECRUTEUR" userName="Recruteur">
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Chargement...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="RECRUTEUR" userName="Recruteur">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[#1E293B]">Candidatures Reçues</h1>
        <p className="text-gray-600 mt-2">Consultez et gérez les candidatures pour vos offres</p>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Candidatures</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">{candidatures.length}</p>
            </div>
            <div className="p-3 bg-[#3B82F6]/10 rounded-xl">
              <Users className="w-8 h-8 text-[#3B82F6]" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">En attente</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">
                {candidatures.filter(c => c.statut === 'EN_ATTENTE' || c.statut === null).length}
              </p>
            </div>
            <div className="p-3 bg-[#F59E0B]/10 rounded-xl">
              <Briefcase className="w-8 h-8 text-[#F59E0B]" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Acceptées</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">
                {candidatures.filter(c => c.statut === 'ACCEPTEE').length}
              </p>
            </div>
            <div className="p-3 bg-[#10B981]/10 rounded-xl">
              <Check className="w-8 h-8 text-[#10B981]" />
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
          {error}
        </div>
      )}

      {candidatures.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center border border-[#E2E8F0]">
          <Users className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <p className="text-gray-600 mb-4">Aucune candidature reçue pour le moment.</p>
          <Link
            to="/dashboard/recruteur/offres"
            className="text-[#3B82F6] hover:text-[#2563EB] font-medium"
          >
            Voir vos offres
          </Link>
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm overflow-hidden border border-[#E2E8F0]">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Candidat
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Offre
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Statut
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Date
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {candidatures.map((candidature) => (
                <tr key={candidature.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div>
                      <p className="text-sm font-medium text-[#1E293B]">{candidature.candidat.nom}</p>
                      <p className="text-sm text-gray-600">{candidature.candidat.email}</p>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {candidature.offre.titre}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {getStatutBadge(candidature.statut)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {formatDate(candidature.dateCandidature)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex space-x-2">
                      {candidature.statut !== 'ACCEPTEE' && (
                        <button
                          onClick={() => handleAccept(candidature.id)}
                          className="text-[#10B981] hover:text-[#059669] transition-colors"
                          title="Accepter"
                        >
                          <Check className="w-5 h-5" />
                        </button>
                      )}
                      {candidature.statut !== 'REFUSEE' && (
                        <button
                          onClick={() => handleReject(candidature.id)}
                          className="text-red-600 hover:text-red-700 transition-colors"
                          title="Refuser"
                        >
                          <X className="w-5 h-5" />
                        </button>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </MainLayout>
  );
}
