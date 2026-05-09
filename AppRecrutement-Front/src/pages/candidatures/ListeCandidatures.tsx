import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { FileText, Trash2, ArrowRight, Building2, Briefcase, MapPin } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface StatusBadgeProps {
  statut: string;
}

function StatusBadge({ statut }: StatusBadgeProps) {
  const statusConfig: Record<string, { bg: string; text: string; label: string }> = {
    'EN_ATTENTE': { bg: 'bg-[#F59E0B]/10', text: 'text-[#F59E0B]', label: 'En attente' },
    'ACCEPTEE': { bg: 'bg-[#10B981]/10', text: 'text-[#10B981]', label: 'Accepté' },
    'REFUSEE': { bg: 'bg-[#EF4444]/10', text: 'text-[#EF4444]', label: 'Refusé' },
  };

  const config = statusConfig[statut] || { bg: 'bg-gray-100', text: 'text-gray-600', label: statut || 'En attente' };

  return (
    <span className={`px-3 py-1.5 rounded-full text-sm font-medium ${config.bg} ${config.text}`}>
      {config.label}
    </span>
  );
}

export default function ListeCandidatures() {
  const [candidatures, setCandidatures] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const userName = localStorage.getItem('userName') || 'Candidat';

  useEffect(() => {
    const fetchCandidatures = async () => {
      try {
        const response = await api.get('/candidatures/mes-candidatures');
        setCandidatures(response.data);
        setLoading(false);
      } catch (err) {
        setError('Erreur lors du chargement des candidatures');
        setLoading(false);
      }
    };

    fetchCandidatures();
  }, []);

  const handleDelete = async (id: number) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette candidature?')) {
      return;
    }

    try {
      await api.delete(`/candidatures/${id}`);
      setCandidatures(candidatures.filter(c => c.id !== id));
    } catch (err) {
      setError('Erreur lors de la suppression de la candidature');
      console.error('Error deleting candidature:', err);
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
        <h1 className="text-3xl font-bold text-[#1E293B]">Mes Candidatures</h1>
        <p className="text-gray-600 mt-2">Suivez l'évolution de vos candidatures</p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg mb-6">
          {error}
        </div>
      )}

      {candidatures.length === 0 ? (
        <div className="bg-white rounded-2xl shadow-sm p-12 text-center border border-[#E2E8F0]">
          <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-600 text-lg mb-4">Aucune candidature pour le moment.</p>
          <Link
            to="/dashboard/candidat/offres"
            className="inline-flex items-center gap-2 px-6 py-3 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors font-medium"
          >
            Voir les offres disponibles
            <ArrowRight className="w-4 h-4" />
          </Link>
        </div>
      ) : (
        <div className="bg-white rounded-2xl shadow-sm overflow-hidden border border-[#E2E8F0]">
          <table className="min-w-full">
            <thead className="bg-[#F8FAFC] border-b border-[#E2E8F0]">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-[#1E293B] uppercase tracking-wider">
                  Offre
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-[#1E293B] uppercase tracking-wider">
                  Entreprise
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-[#1E293B] uppercase tracking-wider">
                  Statut
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-[#1E293B] uppercase tracking-wider">
                  Date de candidature
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-[#1E293B] uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#E2E8F0]">
              {candidatures.map((candidature, index) => (
                <tr key={candidature.id} className={index % 2 === 0 ? 'bg-white' : 'bg-[#F8FAFC]'}>
                  <td className="px-6 py-4">
                    <div className="text-sm font-medium text-[#1E293B]">
                      {candidature.offre?.titre || 'Non spécifié'}
                    </div>
                    <div className="flex items-center gap-2 text-sm text-gray-500 mt-1">
                      <Briefcase className="w-3 h-3" />
                      {candidature.offre?.typeContrat || ''} • <MapPin className="w-3 h-3" /> {candidature.offre?.lieu || ''}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-2 text-sm text-[#1E293B]">
                      <Building2 className="w-4 h-4 text-gray-400" />
                      {candidature.offre?.recruteur?.nomEntreprise || 'Non spécifié'}
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <StatusBadge statut={candidature.statut} />
                  </td>
                  <td className="px-6 py-4 text-sm text-gray-600">
                    {candidature.datePostulation ? new Date(candidature.datePostulation).toLocaleDateString('fr-FR') : 'Non spécifié'}
                  </td>
                  <td className="px-6 py-4">
                    <button
                      onClick={() => handleDelete(candidature.id)}
                      className="flex items-center gap-2 px-3 py-1.5 text-[#EF4444] hover:bg-[#EF4444]/10 rounded-lg transition-colors font-medium text-sm"
                    >
                      <Trash2 className="w-4 h-4" />
                      Supprimer
                    </button>
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
