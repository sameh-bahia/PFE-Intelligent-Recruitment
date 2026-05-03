import { useState, useEffect } from 'react';
import { useNavigate, Link, useParams } from 'react-router-dom';
import api from '@/lib/api';

export default function ModifierOffre() {
  const { id } = useParams<{ id: string }>();
  const [formData, setFormData] = useState({
    titre: '',
    description: '',
    typeContrat: '',
    salaire: '',
    lieu: ''
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
        typeContrat: offre.typeContrat,
        salaire: offre.salaire,
        lieu: offre.lieu
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
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-gray-600">Chargement...</div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 py-8">
      <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="mb-6">
          <Link
            to="/dashboard/recruteur/offres"
            className="text-indigo-600 hover:text-indigo-500"
          >
            ← Retour aux offres
          </Link>
        </div>

        <div className="bg-white rounded-lg shadow p-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-6">
            Modifier l'offre
          </h1>

          {error && (
            <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded mb-4">
              {error}
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label htmlFor="titre" className="block text-sm font-medium text-gray-700">
                Titre de l'offre
              </label>
              <input
                id="titre"
                name="titre"
                type="text"
                required
                value={formData.titre}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>

            <div>
              <label htmlFor="description" className="block text-sm font-medium text-gray-700">
                Description
              </label>
              <textarea
                id="description"
                name="description"
                required
                rows={6}
                value={formData.description}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>

            <div>
              <label htmlFor="typeContrat" className="block text-sm font-medium text-gray-700">
                Type de contrat
              </label>
              <select
                id="typeContrat"
                name="typeContrat"
                required
                value={formData.typeContrat}
                onChange={handleChange}
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
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
              <label htmlFor="salaire" className="block text-sm font-medium text-gray-700">
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
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>

            <div>
              <label htmlFor="lieu" className="block text-sm font-medium text-gray-700">
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
                className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
              />
            </div>

            <div className="flex justify-end space-x-4">
              <Link
                to="/dashboard/recruteur/offres"
                className="px-4 py-2 border border-gray-300 rounded-md text-gray-700 hover:bg-gray-50"
              >
                Annuler
              </Link>
              <button
                type="submit"
                className="px-4 py-2 border border-transparent rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
              >
                Modifier l'offre
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
