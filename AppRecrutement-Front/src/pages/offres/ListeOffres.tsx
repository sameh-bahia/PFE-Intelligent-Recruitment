import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Briefcase, Users, TrendingUp, Edit, Trash2, Plus, MapPin, DollarSign, Calendar } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Offre {
  id: number;
  titre: string;
  description: string;
  typeOffre: string;
  sousDomaineIT: string;
  niveauEtudeRequis: string;
  salaire: string;
  lieu: string;
  dateCreation: string;
  statut?: string;
  candidats?: number;
  competences?: Array<{ nom: string; categorie: string }>;
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
        <div className="grid grid-cols-1 gap-6">
          {offres.map((offre) => (
            <div key={offre.id} className="bg-white rounded-2xl shadow-sm p-6 hover:shadow-lg transition-all duration-300 border border-[#E2E8F0] group">
              <div className="flex justify-between items-start">
                <div className="flex-1">
                  <div className="flex items-start justify-between mb-3">
                    <h3 className="text-2xl font-semibold text-[#1E293B] group-hover:text-[#3B82F6] transition-colors">
                      {offre.titre}
                    </h3>
                    {getStatutBadge(offre.statut)}
                  </div>
                  <p className="text-gray-600 mb-4 line-clamp-2">
                    {offre.description}
                  </p>
                  <div className="flex flex-wrap gap-3 mb-4">
                    <span className="px-3 py-1.5 bg-[#3B82F6]/10 text-[#3B82F6] rounded-full text-sm font-medium flex items-center gap-1">
                      <Briefcase className="w-4 h-4" />
                      {offre.typeOffre}
                    </span>
                    <span className="px-3 py-1.5 bg-[#10B981]/10 text-[#10B981] rounded-full text-sm font-medium flex items-center gap-1">
                      <DollarSign className="w-4 h-4" />
                      {offre.salaire}
                    </span>
                    <span className="px-3 py-1.5 bg-[#334155]/10 text-[#334155] rounded-full text-sm font-medium flex items-center gap-1">
                      <MapPin className="w-4 h-4" />
                      {offre.lieu}
                    </span>
                    <span className="px-3 py-1.5 bg-[#F59E0B]/10 text-[#F59E0B] rounded-full text-sm font-medium flex items-center gap-1">
                      <Calendar className="w-4 h-4" />
                      {formatDate(offre.dateCreation)}
                    </span>
                  </div>
                  {offre.competences && offre.competences.length > 0 && (
                    <div className="mb-4">
                      <p className="text-sm font-medium text-gray-700 mb-2">Compétences requises :</p>
                      <div className="flex flex-wrap gap-2">
                        {offre.competences.map((comp, index) => (
                          <span key={index} className="px-2 py-1 bg-[#6366F1]/10 text-[#6366F1] rounded-full text-xs font-medium">
                            {comp.nom}
                          </span>
                        ))}
                      </div>
                    </div>
                  )}
                  <div className="flex items-center gap-4 text-sm text-gray-500">
                    <span className="flex items-center gap-1">
                      <Users className="w-4 h-4" />
                      {offre.candidats || 0} candidat(s)
                    </span>
                    <span className="text-gray-400">•</span>
                    <span>{offre.sousDomaineIT}</span>
                  </div>
                </div>
                <div className="flex flex-col gap-2 ml-6">
                  <Link
                    to={`/dashboard/recruteur/offres/modifier/${offre.id}`}
                    className="px-4 py-2 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors font-medium flex items-center gap-2"
                  >
                    <Edit className="w-4 h-4" />
                    Modifier
                  </Link>
                  <button
                    onClick={() => handleDelete(offre.id)}
                    className="px-4 py-2 bg-red-50 text-red-600 rounded-lg hover:bg-red-100 transition-colors font-medium flex items-center gap-2"
                  >
                    <Trash2 className="w-4 h-4" />
                    Supprimer
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </MainLayout>
  );
}
