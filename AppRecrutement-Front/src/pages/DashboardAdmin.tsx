import { useState, useEffect } from 'react';
import { Users, Briefcase, Building2, Ban, Check, Search } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Utilisateur {
  id: number;
  email: string;
  nom: string;
  prenom: string;
  role: string;
  nomEntreprise?: string;
  enabled: boolean;
  dateInscription: string;
}

interface Stats {
  totalCandidats: number;
  totalRecruteurs: number;
  totalOffresActives: number;
}

export default function DashboardAdmin() {
  const [stats, setStats] = useState<Stats | null>(null);
  const [utilisateurs, setUtilisateurs] = useState<Utilisateur[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [roleFilter, setRoleFilter] = useState<'all' | 'CANDIDAT' | 'RECRUTEUR'>('all');

  useEffect(() => {
    fetchStats();
    fetchUtilisateurs();
  }, []);

  const fetchStats = async () => {
    try {
      const response = await api.get('/admins/stats');
      setStats(response.data);
    } catch (err) {
      setError('Erreur lors du chargement des statistiques');
    }
  };

  const fetchUtilisateurs = async () => {
    try {
      const response = await api.get('/admins/utilisateurs');
      setUtilisateurs(response.data);
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement des utilisateurs');
      setLoading(false);
    }
  };

  const toggleUserStatus = async (userId: number, currentStatus: boolean) => {
    try {
      await api.put(`/admins/utilisateurs/${userId}/statut`, { enabled: !currentStatus });
      setUtilisateurs(utilisateurs.map(user => 
        user.id === userId ? { ...user, enabled: !currentStatus } : user
      ));
    } catch (err) {
      setError('Erreur lors de la mise à jour du statut');
    }
  };

  const filteredUsers = utilisateurs.filter(user => {
    const matchesSearch = 
      user.nom.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.prenom.toLowerCase().includes(searchTerm.toLowerCase()) ||
      user.email.toLowerCase().includes(searchTerm.toLowerCase()) ||
      (user.nomEntreprise && user.nomEntreprise.toLowerCase().includes(searchTerm.toLowerCase()));
    
    const matchesRole = roleFilter === 'all' || user.role === roleFilter;
    
    return matchesSearch && matchesRole;
  });

  const formatDate = (dateString: string) => {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Date invalide';
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  if (loading) {
    return (
      <MainLayout role="ADMIN" userName="Admin">
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Chargement...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="ADMIN" userName="Admin">
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[#1E293B]">Tableau de bord Admin</h1>
        <p className="text-gray-600 mt-2">Gérez la plateforme et les utilisateurs</p>
      </div>

      {/* KPI Cards */}
      {stats && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Candidats</p>
                <p className="text-3xl font-bold text-[#1E293B] mt-2">{stats.totalCandidats}</p>
              </div>
              <div className="p-3 bg-[#3B82F6]/10 rounded-xl">
                <Users className="w-8 h-8 text-[#3B82F6]" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Total Recruteurs</p>
                <p className="text-3xl font-bold text-[#1E293B] mt-2">{stats.totalRecruteurs}</p>
              </div>
              <div className="p-3 bg-[#10B981]/10 rounded-xl">
                <Building2 className="w-8 h-8 text-[#10B981]" />
              </div>
            </div>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm text-gray-600">Offres Actives</p>
                <p className="text-3xl font-bold text-[#1E293B] mt-2">{stats.totalOffresActives}</p>
              </div>
              <div className="p-3 bg-[#F59E0B]/10 rounded-xl">
                <Briefcase className="w-8 h-8 text-[#F59E0B]" />
              </div>
            </div>
          </div>
        </div>
      )}

      {/* Filters */}
      <div className="bg-white rounded-xl shadow-sm p-4 mb-6 border border-[#E2E8F0]">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="flex-1 relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
            <input
              type="text"
              placeholder="Rechercher par nom, email ou entreprise..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="w-full pl-10 pr-4 py-2 border border-gray-200 rounded-lg focus:outline-none focus:border-blue-500"
            />
          </div>
          <div className="flex gap-2">
            <button
              onClick={() => setRoleFilter('all')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                roleFilter === 'all'
                  ? 'bg-[#3B82F6] text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Tous
            </button>
            <button
              onClick={() => setRoleFilter('CANDIDAT')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                roleFilter === 'CANDIDAT'
                  ? 'bg-[#3B82F6] text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Candidats
            </button>
            <button
              onClick={() => setRoleFilter('RECRUTEUR')}
              className={`px-4 py-2 rounded-lg font-medium transition-colors ${
                roleFilter === 'RECRUTEUR'
                  ? 'bg-[#3B82F6] text-white'
                  : 'bg-gray-200 text-gray-700 hover:bg-gray-300'
              }`}
            >
              Recruteurs
            </button>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
          {error}
        </div>
      )}

      {/* Users Table */}
      <div className="bg-white rounded-xl shadow-sm overflow-hidden border border-[#E2E8F0]">
        <table className="min-w-full divide-y divide-gray-200">
          <thead className="bg-gray-50">
            <tr>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Utilisateur
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Email
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Rôle
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Entreprise
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Statut
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Date d'inscription
              </th>
              <th className="px-6 py-4 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                Actions
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-gray-200">
            {filteredUsers.length === 0 ? (
              <tr>
                <td colSpan={7} className="px-6 py-12 text-center text-gray-500">
                  Aucun utilisateur trouvé
                </td>
              </tr>
            ) : (
              filteredUsers.map((user) => (
                <tr key={user.id} className="hover:bg-gray-50 transition-colors">
                  <td className="px-6 py-4 whitespace-nowrap">
                    <div className="flex items-center">
                      <div className="flex-shrink-0 h-10 w-10 bg-[#3B82F6]/10 rounded-full flex items-center justify-center">
                        <span className="text-[#3B82F6] font-semibold">
                          {user.prenom[0]}{user.nom[0]}
                        </span>
                      </div>
                      <div className="ml-4">
                        <div className="text-sm font-medium text-[#1E293B]">
                          {user.prenom} {user.nom}
                        </div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {user.email}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                      user.role === 'CANDIDAT'
                        ? 'bg-[#3B82F6]/10 text-[#3B82F6]'
                        : 'bg-[#10B981]/10 text-[#10B981]'
                    }`}>
                      {user.role}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {user.nomEntreprise || '-'}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm">
                    <span className={`px-3 py-1 rounded-full text-xs font-medium ${
                      user.enabled
                        ? 'bg-green-100 text-green-800'
                        : 'bg-red-100 text-red-800'
                    }`}>
                      {user.enabled ? 'Actif' : 'Bloqué'}
                    </span>
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                    {formatDate(user.dateInscription)}
                  </td>
                  <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <button
                      onClick={() => toggleUserStatus(user.id, user.enabled)}
                      className={`flex items-center gap-2 px-3 py-2 rounded-lg transition-colors ${
                        user.enabled
                          ? 'bg-red-50 text-red-600 hover:bg-red-100'
                          : 'bg-green-50 text-green-600 hover:bg-green-100'
                      }`}
                    >
                      {user.enabled ? (
                        <>
                          <Ban className="w-4 h-4" />
                          Bloquer
                        </>
                      ) : (
                        <>
                          <Check className="w-4 h-4" />
                          Débloquer
                        </>
                      )}
                    </button>
                  </td>
                </tr>
              ))
            )}
          </tbody>
        </table>
      </div>
    </MainLayout>
  );
}
