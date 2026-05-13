import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { Briefcase, ArrowLeft, Save } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

export default function CreerOffre() {
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    typeContrat: '',
    salaire: '',
    lieu: '',
    competences: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const response = await api.post('/offres', formData);
      console.log('Offre créée:', response.data);
      navigate('/dashboard/recruteur/offres');
    } catch (err) {
      setError('Erreur lors de la création de l\'offre');
      console.error('Error creating offre:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

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
            <h1 className="text-3xl font-bold text-[#1E293B]">Créer une nouvelle offre</h1>
            <p className="text-gray-600 mt-1">Remplissez les informations pour publier votre offre</p>
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
            <label htmlFor="typeContrat" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Type de contrat
            </label>
            <select
              id="typeContrat"
              name="typeContrat"
              required
              value={formData.typeContrat}
              onChange={handleChange}
              className="w-full px-5 py-4 h-14 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
            >
              <option value="">Sélectionner...</option>
              <option value="CDI">CDI</option>
              <option value="CDD">CDD</option>
              <option value="Stage">Stage</option>
              <option value="Freelance">Freelance</option>
              <option value="Alternance">Alternance</option>
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
            <label htmlFor="competences" className="block text-lg font-semibold text-[#1E293B] mb-3">
              Compétences requises
            </label>
            <textarea
              id="competences"
              name="competences"
              rows={3}
              value={formData.competences}
              onChange={handleChange}
              className="w-full px-5 py-4 bg-white border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
              placeholder="Entrez les compétences requises, séparées par des virgules (ex: Java, Spring, PostgreSQL, Docker)"
            />
            <p className="text-sm text-gray-500 mt-2">
              Séparez les compétences par des virgules
            </p>
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
              Créer l'offre
            </button>
          </div>
        </form>
      </div>
    </MainLayout>
  );
}
