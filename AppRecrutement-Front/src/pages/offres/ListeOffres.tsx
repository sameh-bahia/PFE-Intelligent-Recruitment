import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Briefcase, Users, TrendingUp, Edit, Trash2, Plus } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Offre {
  id: number;
  titre: string;
  description: string;
  typeContrat: string;
  salaire: string;
  lieu: string;
  datePublication: string;
  statut?: string;
  candidats?: number;
}

export default function ListeOffres() {
  const [offres, setOffres] = useState<Offre[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    fetchOffres();
  }, []);

  // Changement: Remplacement de '/offres' par '/offres/mes-offres'
  // Pourquoi: Avant, l'API retournait TOUTES les offres de la BD
  // Maintenant, l'endpoint filtre par recruteur connecté via token JWT
  // Résultat: Chaque recruteur ne voit que ses propres offres
  const fetchOffres = async () => {
    try {
      const response = await api.get('/offres/mes-offres');
      setOffres(response.data);
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement des offres');
      setLoading(false);
      console.error('Error fetching offres:', err);
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm('Êtes-vous sûr de vouloir supprimer cette offre ?')) {
      return;
    }

    try {
      await api.delete(`/offres/${id}`);
      setOffres(offres.filter(offre => offre.id !== id));
    } catch (err) {
      setError('Erreur lors de la suppression de l\'offre');
      console.error('Error deleting offre:', err);
    }
  };

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Date invalide';
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const getStatutBadge = (statut?: string) => {
    const status = statut || 'Active';
    if (status === 'Active') {
      return (
        <span className="px-3 py-1 rounded-full text-xs font-medium bg-green-100 text-green-800">
          Active
        </span>
      );
    } else {
      return (
        <span className="px-3 py-1 rounded-full text-xs font-medium bg-red-100 text-red-800">
          Expirée
        </span>
      );
    }
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
        <h1 className="text-3xl font-bold text-[#1E293B]">Mes Offres</h1>
        <p className="text-gray-600 mt-2">Gérez vos offres d'emploi</p>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Offres</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">{offres.length}</p>
            </div>
            <div className="p-3 bg-[#3B82F6]/10 rounded-xl">
              <Briefcase className="w-8 h-8 text-[#3B82F6]" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Candidats</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">
                {offres.reduce((sum, offre) => sum + (offre.candidats || 0), 0)}
              </p>
            </div>
            <div className="p-3 bg-[#10B981]/10 rounded-xl">
              <Users className="w-8 h-8 text-[#10B981]" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Matchs récents</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">12</p>
            </div>
            <div className="p-3 bg-[#F59E0B]/10 rounded-xl">
              <TrendingUp className="w-8 h-8 text-[#F59E0B]" />
            </div>
          </div>
        </div>
      </div>

      <div className="flex justify-between items-center mb-6">
        <div></div>
        <Link
          to="/dashboard/recruteur/offres/creer"
          className="bg-[#3B82F6] text-white px-4 py-2 rounded-lg hover:bg-[#2563EB] transition-colors flex items-center gap-2"
        >
          <Plus className="w-5 h-5" />
          Créer une offre
        </Link>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
          {error}
        </div>
      )}

      {offres.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center border border-[#E2E8F0]">
          <Briefcase className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <p className="text-gray-600 mb-4">Aucune offre pour le moment.</p>
          <Link
            to="/dashboard/recruteur/offres/creer"
            className="text-[#3B82F6] hover:text-[#2563EB] font-medium"
          >
            Créer votre première offre
          </Link>
        </div>
      ) : (
        <div className="bg-white rounded-xl shadow-sm overflow-hidden border border-[#E2E8F0]">
          <table className="min-w-full divide-y divide-gray-200">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Titre
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Type de contrat
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Salaire
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Lieu
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Statut
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Candidats
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Date de publication
                </th>
                <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white divide-y divide-gray-200">
              {offres.map((offre) => (
                <tr key={offre.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium text-[#1E293B]">
                    {offre.titre}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {offre.typeContrat}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {offre.salaire}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {offre.lieu}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    {getStatutBadge(offre.statut)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {offre.candidats || 0}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {formatDate(offre.datePublication)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div className="flex space-x-3">
                      <Link
                        to={`/dashboard/recruteur/offres/modifier/${offre.id}`}
                        className="text-[#3B82F6] hover:text-[#2563EB] transition-colors"
                        title="Modifier"
                      >
                        <Edit className="w-5 h-5" />
                      </Link>
                      <button
                        onClick={() => handleDelete(offre.id)}
                        className="text-red-600 hover:text-red-700 transition-colors"
                        title="Supprimer"
                      >
                        <Trash2 className="w-5 h-5" />
                      </button>
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
