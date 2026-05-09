import { Link } from 'react-router-dom';
import { Briefcase, Users, Plus, User, ArrowRight } from 'lucide-react';
import MainLayout from '@/components/layout/MainLayout';

export default function DashboardRecruteur() {
  const userName = localStorage.getItem('userName') || 'Recruteur';

  return (
    <MainLayout role="RECRUTEUR" userName={userName}>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[#1E293B]">Tableau de bord Linkia</h1>
        <p className="text-gray-600 mt-2">Bienvenue, {userName}! Gérez vos offres et candidatures.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        <Link
          to="/dashboard/recruteur/offres"
          className="bg-white rounded-xl shadow-sm p-8 hover:shadow-md transition-all duration-300 border border-[#E2E8F0] group"
        >
          <div className="flex items-center gap-6">
            <div className="p-4 bg-[#3B82F6]/10 rounded-xl group-hover:bg-[#3B82F6]/20 transition-colors">
              <Briefcase className="w-8 h-8 text-[#3B82F6]" />
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-semibold text-[#1E293B] group-hover:text-[#3B82F6] transition-colors">
                Mes Offres
              </h2>
              <p className="text-gray-600 mt-1">Gérez vos offres d'emploi</p>
            </div>
            <ArrowRight className="w-6 h-6 text-gray-400 group-hover:text-[#3B82F6] group-hover:translate-x-1 transition-all" />
          </div>
        </Link>

        <Link
          to="/dashboard/recruteur/candidatures"
          className="bg-white rounded-xl shadow-sm p-8 hover:shadow-md transition-all duration-300 border border-[#E2E8F0] group"
        >
          <div className="flex items-center gap-6">
            <div className="p-4 bg-[#10B981]/10 rounded-xl group-hover:bg-[#10B981]/20 transition-colors">
              <Users className="w-8 h-8 text-[#10B981]" />
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-semibold text-[#1E293B] group-hover:text-[#10B981] transition-colors">
                Candidatures Reçues
              </h2>
              <p className="text-gray-600 mt-1">Consultez les candidats</p>
            </div>
            <ArrowRight className="w-6 h-6 text-gray-400 group-hover:text-[#10B981] group-hover:translate-x-1 transition-all" />
          </div>
        </Link>

        <Link
          to="/dashboard/recruteur/offres/creer"
          className="bg-white rounded-xl shadow-sm p-8 hover:shadow-md transition-all duration-300 border border-[#E2E8F0] group"
        >
          <div className="flex items-center gap-6">
            <div className="p-4 bg-[#3B82F6]/10 rounded-xl group-hover:bg-[#3B82F6]/20 transition-colors">
              <Plus className="w-8 h-8 text-[#3B82F6]" />
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-semibold text-[#1E293B] group-hover:text-[#3B82F6] transition-colors">
                Publier une Offre
              </h2>
              <p className="text-gray-600 mt-1">Créez une nouvelle offre</p>
            </div>
            <ArrowRight className="w-6 h-6 text-gray-400 group-hover:text-[#3B82F6] group-hover:translate-x-1 transition-all" />
          </div>
        </Link>

        <Link
          to="/dashboard/recruteur/profil"
          className="bg-white rounded-xl shadow-sm p-8 hover:shadow-md transition-all duration-300 border border-[#E2E8F0] group"
        >
          <div className="flex items-center gap-6">
            <div className="p-4 bg-[#8B5CF6]/10 rounded-xl group-hover:bg-[#8B5CF6]/20 transition-colors">
              <User className="w-8 h-8 text-[#8B5CF6]" />
            </div>
            <div className="flex-1">
              <h2 className="text-2xl font-semibold text-[#1E293B] group-hover:text-[#8B5CF6] transition-colors">
                Mon Profil
              </h2>
              <p className="text-gray-600 mt-1">Gérez vos informations</p>
            </div>
            <ArrowRight className="w-6 h-6 text-gray-400 group-hover:text-[#8B5CF6] group-hover:translate-x-1 transition-all" />
          </div>
        </Link>
      </div>
    </MainLayout>
  );
}
