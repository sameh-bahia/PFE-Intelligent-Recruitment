import { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import { Briefcase, ArrowLeft, Save } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

export default function ModifierOffre() {
  const { id } = useParams<{ id: string }>();
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    typeOffre: '',
    sousDomaineIT: '',
    niveauEtudeRequis: '',
    salaire: '',
    lieu: '',
    competences: ''
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchOffre();
  }, [id]);

  const fetchOffre = async () => {
    try {
      const response = await api.get(`/offres/${id}`);
      const offre = response.data;
      setFormData({
        titre: offre.titre,
        description: offre.description,
        typeOffre: offre.typeOffre,
        sousDomaineIT: offre.sousDomaineIT,
        niveauEtudeRequis: offre.niveauEtudeRequis,
        salaire: offre.salaire,
        lieu: offre.lieu,
        competences: offre.competences ? offre.competences.map((c: any) => c.nom).join(', ') : ''
      });
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement de l\'offre');
      setLoading(false);
      console.error('Error fetching offre:', err);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    console.log('=== DEBUG FORM DATA AVANT ENVOI ===');
    console.log('FormData:', formData);
    console.log('Compétences:', formData.competences);

    try {
      const response = await api.put(`/offres/${id}`, formData);
      console.log('Offre modifiée:', response.data);
      navigate('/dashboard/recruteur/offres');
    } catch (err) {
      setError('Erreur lors de la modification de l\'offre');
      console.error('Error updating offre:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
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
        <Link
          to="/dashboard/recruteur/offres"
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
            <h1 className="text-3xl font-bold text-[#1E293B]">Modifier l'offre</h1>
            <p className="text-gray-600 mt-1">Mettez à jour les informations de votre offre</p>
          </div>
        </div>

        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-8">
          <div>
            <label htmlFor="titre" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Titre de l'offre
            </label>
            <input
              id="titre"
              name="titre"
              type="text"
              required
              value={formData.titre}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
              placeholder="Ex: Développeur Full Stack Senior"
            />
          </div>

          <div>
            <label htmlFor="description" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              required
              rows={6}
              value={formData.description}
              onChange={handleChange}
              className="w-full px-5 py-4 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
              placeholder="Décrivez le poste, les responsabilités et les compétences requises..."
            />
          </div>

          <div>
            <label htmlFor="typeOffre" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Type d'offre
            </label>
            <select
              id="typeOffre"
              name="typeOffre"
              required
              value={formData.typeOffre}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="EMPLOI">Emploi</option>
              <option value="STAGE">Stage</option>
              <option value="ALTERNANCE">Alternance</option>
              <option value="FREELANCE">Freelance</option>
            </select>
          </div>

          <div>
            <label htmlFor="salaire" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Salaire
            </label>
            <input
              id="salaire"
              name="salaire"
              type="text"
              required
              placeholder="Ex: 3000€ - 4000€"
              value={formData.salaire}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            />
          </div>

          <div>
            <label htmlFor="lieu" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Lieu
            </label>
            <input
              id="lieu"
              name="lieu"
              type="text"
              required
              placeholder="Ex: Paris, Tunis, Remote"
              value={formData.lieu}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            />
          </div>

          <div>
            <label htmlFor="sousDomaineIT" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Sous-domaine IT
            </label>
            <select
              id="sousDomaineIT"
              name="sousDomaineIT"
              required
              value={formData.sousDomaineIT}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="DEVELOPPEMENT">Développement</option>
              <option value="DATA_SCIENCE">Data Science</option>
              <option value="DEVOPS">DevOps</option>
              <option value="CYBERSECURITE">Cybersécurité</option>
              <option value="GESTION_PROJET">Gestion de Projet</option>
              <option value="QA">Quality Assurance</option>
            </select>
          </div>

          <div>
            <label htmlFor="niveauEtudeRequis" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Niveau d'étude requis
            </label>
            <select
              id="niveauEtudeRequis"
              name="niveauEtudeRequis"
              required
              value={formData.niveauEtudeRequis}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="BAC">BAC</option>
              <option value="DUT_BTS">DUT/BTS</option>
              <option value="LICENCE">Licence</option>
              <option value="MASTER">Master</option>
              <option value="INGENIEUR">Ingénieur</option>
              <option value="DOCTORAT">Doctorat</option>
              <option value="SANS_EXIGENCE">Sans exigence</option>
            </select>
          </div>

          <div>
            <label htmlFor="competences" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Compétences requises
            </label>
            <input
              id="competences"
              name="competences"
              type="text"
              placeholder="Ex: Java, Spring Boot, Angular (séparées par des virgules)"
              value={formData.competences}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            />
          </div>

          <div className="flex justify-end gap-4 pt-6">
            <Link
              to="/dashboard/recruteur/offres"
              className="px-6 py-3 border border-gray-200 rounded-xl text-gray-700 hover:bg-gray-50 transition-colors font-medium"
            >
              Annuler
            </Link>
            <button
              type="submit"
              className="px-6 py-3 h-14 bg-[#3B82F6] text-white rounded-xl hover:bg-[#2563EB] transition-colors font-bold text-lg flex items-center gap-2"
            >
              <Save className="w-5 h-5" />
              Modifier l'offre
            </button>
          </div>
        </form>
      </div>
    </MainLayout>
  );
}
