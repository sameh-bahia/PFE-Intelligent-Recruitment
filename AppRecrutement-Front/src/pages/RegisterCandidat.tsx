import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Logo from '@/components/Logo';
import { Mail, Lock, User, MapPin, Calendar, Check, X, Eye, EyeOff, Briefcase, Brain, Clock, Target } from 'lucide-react';
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
  const [showPassword, setShowPassword] = useState(false);
  const [fieldErrors, setFieldErrors] = useState<Record<string, string>>({});
  const [fieldValidities, setFieldValidities] = useState<Record<string, boolean>>({});
  const [passwordStrength, setPasswordStrength] = useState(0);
  const [touchedFields, setTouchedFields] = useState<Record<string, boolean>>({});
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

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
    setTouchedFields({ ...touchedFields, [name]: true });
    validateField(name, value);
  };

  const validateField = (name: string, value: string) => {
    let error = '';
    let isValid = false;

    switch (name) {
      case 'email':
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        isValid = emailRegex.test(value);
        error = isValid ? '' : 'Email invalide';
        break;
      case 'motDePasse':
        const strength = calculatePasswordStrength(value);
        setPasswordStrength(strength);
        isValid = strength >= 2;
        error = isValid ? '' : 'Mot de passe trop faible (min 8 caractères, 1 majuscule, 1 chiffre)';
        break;
      case 'nom':
      case 'prenom':
        isValid = value.trim().length >= 2;
        error = isValid ? '' : 'Minimum 2 caractères';
        break;
      case 'telephone':
        // User types only the 8 digits, +216 is added separately
        const digitsOnly = value.replace(/\D/g, '');
        isValid = digitsOnly.length === 8;
        error = isValid ? '' : 'Numéro invalide (8 chiffres requis)';
        break;
      case 'titreProfil':
        isValid = value.trim().length >= 2;
        error = isValid ? '' : 'Minimum 2 caractères';
        break;
      default:
        isValid = true;
    }

    setFieldErrors({ ...fieldErrors, [name]: error });
    setFieldValidities({ ...fieldValidities, [name]: isValid });
  };

  const calculatePasswordStrength = (password: string): number => {
    let strength = 0;
    if (password.length >= 8) strength++;
    if (/[A-Z]/.test(password)) strength++;
    if (/[0-9]/.test(password)) strength++;
    if (/[^A-Za-z0-9]/.test(password)) strength++;
    return strength;
  };

  const getPasswordStrengthColor = () => {
    if (passwordStrength <= 1) return 'bg-red-500';
    if (passwordStrength === 2) return 'bg-orange-500';
    return 'bg-green-500';
  };

  const isFormValid = () => {
    const requiredFields = ['email', 'motDePasse', 'nom', 'prenom', 'telephone', 'titreProfil'];
    return requiredFields.every(field => fieldValidities[field] && formData[field as keyof typeof formData]);
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
            <div className="mb-8">
              <Logo />
            </div>
            <h1 className="text-7xl font-bold text-white mb-8 tracking-tight">
              Créez votre espace candidat
            </h1>
            <p className="text-white text-xl leading-relaxed text-white/70">
              Trouvez l'opportunité qui vous correspond sur Linkia.
            </p>
          </div>

          <div className="mt-12 space-y-6">
            <div className="flex items-start gap-4">
              <div className="bg-green-500/20 p-2 rounded-lg">
                <Brain className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-white font-semibold text-lg">Matching IA intelligent</p>
                <p className="text-white/60 text-sm">Algorithmes avancés pour trouver les offres parfaites</p>
              </div>
            </div>
            <div className="flex items-start gap-4">
              <div className="bg-green-500/20 p-2 rounded-lg">
                <Clock className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-white font-semibold text-lg">Suivi en temps réel</p>
                <p className="text-white/60 text-sm">Notifications instantanées pour vos candidatures</p>
              </div>
            </div>
            <div className="flex items-start gap-4">
              <div className="bg-green-500/20 p-2 rounded-lg">
                <Target className="w-6 h-6 text-green-400" />
              </div>
              <div>
                <p className="text-white font-semibold text-lg">Opportunités ciblées</p>
                <p className="text-white/60 text-sm">Offres filtrées selon votre profil et compétences</p>
              </div>
            </div>
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
                  <div className="relative">
                    <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                    <input
                      id="nom"
                      name="nom"
                      type="text"
                      required
                      value={formData.nom}
                      onChange={handleChange}
                      className={`w-full pl-12 pr-4 py-3 h-12 bg-white border-2 rounded-xl focus:outline-none transition-all text-lg ${
                        touchedFields.nom 
                          ? fieldValidities.nom 
                            ? 'border-green-500' 
                            : 'border-red-500'
                          : 'border-gray-200 focus:border-blue-500'
                      }`}
                      placeholder="Votre nom"
                    />
                    {touchedFields.nom && fieldValidities.nom && (
                      <Check className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-green-500" />
                    )}
                    {touchedFields.nom && !fieldValidities.nom && (
                      <X className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-red-500" />
                    )}
                  </div>
                  {touchedFields.nom && fieldErrors.nom && (
                    <p className="text-red-500 text-sm mt-1">{fieldErrors.nom}</p>
                  )}
                </div>
                <div>
                  <label htmlFor="prenom" className="block text-lg font-semibold text-[#1E293B] mb-3">
                    Prénom
                  </label>
                  <div className="relative">
                    <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                    <input
                      id="prenom"
                      name="prenom"
                      type="text"
                      required
                      value={formData.prenom}
                      onChange={handleChange}
                      className={`w-full pl-12 pr-4 py-3 h-12 bg-white border-2 rounded-xl focus:outline-none transition-all text-lg ${
                        touchedFields.prenom 
                          ? fieldValidities.prenom 
                            ? 'border-green-500' 
                            : 'border-red-500'
                          : 'border-gray-200 focus:border-blue-500'
                      }`}
                      placeholder="Votre prénom"
                    />
                    {touchedFields.prenom && fieldValidities.prenom && (
                      <Check className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-green-500" />
                    )}
                    {touchedFields.prenom && !fieldValidities.prenom && (
                      <X className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-red-500" />
                    )}
                  </div>
                  {touchedFields.prenom && fieldErrors.prenom && (
                    <p className="text-red-500 text-sm mt-1">{fieldErrors.prenom}</p>
                  )}
                </div>
              </div>

              <div>
                <label htmlFor="email" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Email
                </label>
                <div className="relative">
                  <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="email"
                    name="email"
                    type="email"
                    autoComplete="off"
                    required
                    value={formData.email}
                    onChange={handleChange}
                    className={`w-full pl-12 pr-4 py-3 h-12 bg-white border-2 rounded-xl focus:outline-none transition-all text-lg ${
                      touchedFields.email 
                        ? fieldValidities.email 
                          ? 'border-green-500' 
                          : 'border-red-500'
                        : 'border-gray-200 focus:border-blue-500'
                    }`}
                    placeholder="votre@email.com"
                  />
                  {touchedFields.email && fieldValidities.email && (
                    <Check className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-green-500" />
                  )}
                  {touchedFields.email && !fieldValidities.email && (
                    <X className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-red-500" />
                  )}
                </div>
                {touchedFields.email && fieldErrors.email && (
                  <p className="text-red-500 text-sm mt-1">{fieldErrors.email}</p>
                )}
              </div>

              <div>
                <label htmlFor="motDePasse" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Mot de passe
                </label>
                <div className="relative">
                  <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="motDePasse"
                    name="motDePasse"
                    type={showPassword ? 'text' : 'password'}
                    autoComplete="new-password"
                    required
                    value={formData.motDePasse}
                    onChange={handleChange}
                    className={`w-full pl-12 pr-12 py-3 h-12 bg-white border-2 rounded-xl focus:outline-none transition-all text-lg ${
                      touchedFields.motDePasse 
                        ? fieldValidities.motDePasse 
                          ? 'border-green-500' 
                          : 'border-red-500'
                        : 'border-gray-200 focus:border-blue-500'
                    }`}
                    placeholder="••••••••"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600"
                  >
                    {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                  </button>
                </div>
                {formData.motDePasse && (
                  <div className="mt-2">
                    <div className="w-full bg-gray-200 rounded-full h-2">
                      <div
                        className={`h-2 rounded-full transition-all duration-300 ${getPasswordStrengthColor()}`}
                        style={{ width: `${(passwordStrength / 4) * 100}%` }}
                      ></div>
                    </div>
                  </div>
                )}
                {touchedFields.motDePasse && fieldErrors.motDePasse && (
                  <p className="text-red-500 text-sm mt-1">{fieldErrors.motDePasse}</p>
                )}
              </div>

              <div>
                <label htmlFor="telephone" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Téléphone
                </label>
                <div className="relative flex">
                  <span className="inline-flex items-center px-4 py-3 h-12 bg-gray-100 border-2 border-r-0 border-gray-200 rounded-l-xl text-gray-600 font-medium text-lg">
                    +216
                  </span>
                  <input
                    id="telephone"
                    name="telephone"
                    type="tel"
                    required
                    value={formData.telephone}
                    onChange={handleChange}
                    maxLength={11}
                    className={`flex-1 pl-4 pr-4 py-3 h-12 bg-white border-2 rounded-r-xl focus:outline-none transition-all text-lg ${
                      touchedFields.telephone 
                        ? fieldValidities.telephone 
                          ? 'border-green-500' 
                          : 'border-red-500'
                        : 'border-gray-200 focus:border-blue-500'
                    }`}
                    placeholder="XX XXX XXX"
                  />
                  {touchedFields.telephone && fieldValidities.telephone && (
                    <Check className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-green-500" />
                  )}
                  {touchedFields.telephone && !fieldValidities.telephone && (
                    <X className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-red-500" />
                  )}
                </div>
                {touchedFields.telephone && fieldErrors.telephone && (
                  <p className="text-red-500 text-sm mt-1">{fieldErrors.telephone}</p>
                )}
              </div>

              <div>
                <label htmlFor="adresse" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Adresse
                </label>
                <div className="relative">
                  <MapPin className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="adresse"
                    name="adresse"
                    type="text"
                    value={formData.adresse}
                    onChange={handleChange}
                    className="w-full pl-12 pr-4 py-3 h-12 bg-white border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 transition-all text-lg"
                    placeholder="Votre adresse"
                  />
                </div>
              </div>

              <div>
                <label htmlFor="dateNaissance" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Date de naissance
                </label>
                <div className="relative">
                  <Calendar className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="dateNaissance"
                    name="dateNaissance"
                    type="date"
                    value={formData.dateNaissance}
                    onChange={handleChange}
                    className="w-full pl-12 pr-4 py-3 h-12 bg-white border-2 border-gray-200 rounded-xl focus:outline-none focus:border-blue-500 transition-all text-lg"
                  />
                </div>
              </div>

              <div>
                <label htmlFor="titreProfil" className="block text-lg font-semibold text-[#1E293B] mb-3">
                  Titre du profil
                </label>
                <div className="relative">
                  <Briefcase className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
                  <input
                    id="titreProfil"
                    name="titreProfil"
                    type="text"
                    required
                    value={formData.titreProfil}
                    onChange={handleChange}
                    className={`w-full pl-12 pr-4 py-3 h-12 bg-white border-2 rounded-xl focus:outline-none transition-all text-lg ${
                      touchedFields.titreProfil 
                        ? fieldValidities.titreProfil 
                          ? 'border-green-500' 
                          : 'border-red-500'
                        : 'border-gray-200 focus:border-blue-500'
                    }`}
                    placeholder="Ex: Développeur Full Stack"
                  />
                  {touchedFields.titreProfil && fieldValidities.titreProfil && (
                    <Check className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-green-500" />
                  )}
                  {touchedFields.titreProfil && !fieldValidities.titreProfil && (
                    <X className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-red-500" />
                  )}
                </div>
                {touchedFields.titreProfil && fieldErrors.titreProfil && (
                  <p className="text-red-500 text-sm mt-1">{fieldErrors.titreProfil}</p>
                )}
              </div>


              <button
                type="submit"
                disabled={!isFormValid()}
                className={`w-full h-14 px-4 rounded-full transition-all font-bold text-xl shadow-lg ${
                  isFormValid()
                    ? 'bg-[#3B82F6] text-white hover:bg-[#2563EB] shadow-[#3B82F6]/30'
                    : 'bg-gray-300 text-gray-500 cursor-not-allowed'
                }`}
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
