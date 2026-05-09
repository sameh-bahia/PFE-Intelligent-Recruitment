import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { User } from 'lucide-react';
import api from '@/lib/api';

export default function RegisterCandidat() {
  const [formData, setFormData] = useState({
    email: '',
    motDePasse: '',
    nom: '',
    prenom: '',
    telephone: '',
    adresse: '',
    dateNaissance: '',
    titreProfil: ''
  });
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const response = await api.post('/auth/register/candidat', formData);
      console.log('Register Candidat success:', response.data);
      navigate('/login');
    } catch (err) {
      setError('Erreur lors de l\'inscription');
      console.error('Register error:', err);
    }
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  return (
    <section className="min-h-screen bg-[#1E293B] flex items-center justify-center p-4 relative overflow-hidden">
      {/* Abstract background shapes */}
      <div className="absolute top-0 left-0 w-96 h-96 bg-[#3B82F6] rounded-full opacity-20 blur-3xl -translate-x-1/2 -translate-y-1/2"></div>
      <div className="absolute bottom-0 right-0 w-96 h-96 bg-[#60A5FA] rounded-full opacity-20 blur-3xl translate-x-1/2 translate-y-1/2"></div>
      <div className="absolute top-1/2 left-1/2 w-64 h-64 bg-[#3B82F6] rounded-full opacity-10 blur-2xl -translate-x-1/2 -translate-y-1/2"></div>

      {/* Split layout card */}
      <div className="relative z-10 w-full max-w-6xl bg-white rounded-3xl shadow-2xl overflow-hidden flex">
        {/* Left side - Welcome */}
        <div className="w-1/2 bg-gradient-to-br from-[#1E293B] to-[#334155] p-16 flex flex-col justify-center">
          <div className="mb-8">
            <div className="bg-[#3B82F6] p-6 rounded-2xl w-fit mb-8">
              <User className="w-12 h-12 text-white" />
            </div>
            <h1 className="text-7xl font-bold text-white mb-8 tracking-tight">
              INSCRIPTION
            </h1>
            <p className="text-white text-xl leading-relaxed text-white/70">
              Rejoignez Linkia et connectez-vous aux opportunités de demain.
            </p>
          </div>
          <div className="mt-auto">
            <p className="text-white text-sm">
              © 2024 Linkia
            </p>
          </div>
        </div>

        {/* Right side - Sign up form */}
        <div className="w-1/2 bg-[#F8FAFC] p-16 flex flex-col justify-center overflow-y-auto">
          <div className="max-w-md mx-auto w-full">
            <h2 className="text-5xl font-bold text-[#1E293B] mb-3">
              Candidat
            </h2>
            <p className="text-gray-600 text-lg mb-10">
              Créez votre compte pour accéder aux offres
            </p>

            {error && (
              <div role="alert" className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 text-lg">
                {error}
              </div>
            )}

            <form className="space-y-6" onSubmit={handleSubmit}>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label htmlFor="nom" className="block text-lg font-semibold text-[#1E293B] mb-3">
                    Nom
                  </label>
                  <input
                    id="nom"
                    name="nom"
                    type="text"
                    required
                    value={formData.nom}
                    onChange={handleChange}
                    className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                    placeholder="Votre nom"
                  />
                </div>
                <div>
                  <label htmlFor="prenom" className="block text-lg font-semibold text-[#1E293B] mb-3">
                    Prénom
                  </label>
                  <input
                    id="prenom"
                    name="prenom"
                    type="text"
                    required
                    value={formData.prenom}
                    onChange={handleChange}
                    className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                    placeholder="Votre prénom"
                  />
                </div>
              </div>

              <div>
                <label htmlFor="email" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Email
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  required
                  value={formData.email}
                  onChange={handleChange}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                  placeholder="votre@email.com"
                />
              </div>

              <div>
                <label htmlFor="motDePasse" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Mot de passe
                </label>
                <input
                  id="motDePasse"
                  name="motDePasse"
                  type="password"
                  required
                  value={formData.motDePasse}
                  onChange={handleChange}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                  placeholder="••••••••"
                />
              </div>

              <div>
                <label htmlFor="telephone" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Téléphone
                </label>
                <input
                  id="telephone"
                  name="telephone"
                  type="tel"
                  value={formData.telephone}
                  onChange={handleChange}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                  placeholder="+216 XX XXX XXX"
                />
              </div>

              <div>
                <label htmlFor="adresse" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Adresse
                </label>
                <input
                  id="adresse"
                  name="adresse"
                  type="text"
                  value={formData.adresse}
                  onChange={handleChange}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                  placeholder="Votre adresse"
                />
              </div>

              <div>
                <label htmlFor="dateNaissance" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Date de naissance
                </label>
                <input
                  id="dateNaissance"
                  name="dateNaissance"
                  type="date"
                  value={formData.dateNaissance}
                  onChange={handleChange}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                />
              </div>

              <div>
                <label htmlFor="titreProfil" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Titre du profil
                </label>
                <input
                  id="titreProfil"
                  name="titreProfil"
                  type="text"
                  value={formData.titreProfil}
                  onChange={handleChange}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:ring-2 focus:ring-[#3B82F6]/20 transition-all text-lg"
                  placeholder="Ex: Développeur Full Stack"
                />
              </div>

              <button
                type="submit"
                className="w-full h-14 px-4 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all font-bold text-xl shadow-lg shadow-[#3B82F6]/30"
              >
                S'inscrire
              </button>
            </form>

            <div className="mt-10 text-center">
              <p className="text-lg text-gray-600">
                Déjà inscrit ?{' '}
                <Link to="/login" className="text-[#3B82F6] hover:text-[#2563EB] font-bold">
                  Se connecter
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
