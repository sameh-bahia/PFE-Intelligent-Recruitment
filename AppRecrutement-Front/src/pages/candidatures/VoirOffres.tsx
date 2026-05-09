import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Briefcase, MapPin, DollarSign, Building2, ArrowRight } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

export default function VoirOffres() {
  const [offres, setOffres] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const userName = localStorage.getItem('userName') || 'Candidat';

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
                  <div className="flex items-center gap-2 text-sm text-gray-500">
                    <Building2 className="w-4 h-4" />
                    {offre.recruteur?.nomEntreprise || 'Non spécifié'}
                  </div>
                </div>
                <Link
                  to={`/dashboard/candidat/offres/${offre.id}/postuler`}
                  className="ml-6 px-6 py-3 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors font-medium flex items-center gap-2 group-hover:shadow-lg"
                >
                  Postuler
                  <ArrowRight className="w-4 h-4" />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </MainLayout>
  );
}
