import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import Logo from '@/components/Logo';
import api from '@/lib/api';

export default function Login() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const response = await api.post('/auth/login', { email, password });
      const { token, role } = response.data;

      localStorage.setItem('token', token);
      localStorage.setItem('role', role);

      if (role === 'ROLE_CANDIDAT') {
        navigate('/dashboard/candidat');
      } else if (role === 'ROLE_RECRUTEUR') {
        navigate('/dashboard/recruteur');
      } else {
        navigate('/dashboard/candidat');
      }
    } catch (err) {
      setError('Email ou mot de passe incorrect');
      console.error('Login error:', err);
    }
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
            <div>
              <h1 className="text-7xl font-bold text-white mb-8 tracking-tight">
                BIENVENUE
              </h1>
              <p className="text-white text-xl leading-relaxed text-white/70">
                Connecter les talents aux opportunités de demain.
              </p>
            </div>
          </div>
          <div className="mt-auto">
            <p className="text-white text-sm">
              © 2024 Linkia
            </p>
          </div>
        </div>

        {/* Right side - Sign in form */}
        <div className="w-1/2 bg-[#F8FAFC] p-16 flex flex-col justify-center">
          <div className="max-w-md mx-auto w-full">
            <h2 className="text-5xl font-bold text-[#1E293B] mb-3">
              Se connecter
            </h2>
            <p className="text-gray-600 text-lg mb-10">
              Entrez vos informations pour accéder à votre compte
            </p>

            {error && (
              <div role="alert" className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6 text-lg">
                {error}
              </div>
            )}

            <form className="space-y-10" onSubmit={handleSubmit}>
              <div>
                <label htmlFor="email" className="block text-lg font-semibold text-[#1E293B] mb-4">
                  Email
                </label>
                <input
                  id="email"
                  name="email"
                  type="email"
                  autoComplete="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border-2 border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:shadow-lg focus:shadow-[#3B82F6]/20 transition-all duration-300 text-lg"
                  placeholder="votre@email.com"
                />
              </div>

              <div>
                <label htmlFor="password" className="block text-lg font-semibold text-[#1E293B] mb-4">
                  Mot de passe
                </label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  autoComplete="current-password"
                  required
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="w-full px-5 py-4 h-14 bg-white/70 backdrop-blur-sm border-2 border-gray-200 rounded-xl focus:outline-none focus:border-[#3B82F6] focus:shadow-lg focus:shadow-[#3B82F6]/20 transition-all duration-300 text-lg"
                  placeholder="••••••••"
                />
              </div>

              <div className="flex items-center justify-between">
                <label className="flex items-center cursor-pointer">
                  <input type="checkbox" className="w-5 h-5 rounded border-gray-300 text-[#3B82F6] focus:ring-[#3B82F6]" />
                  <span className="ml-3 text-lg text-gray-600">Remember me</span>
                </label>
                <Link to="#" className="text-lg text-[#3B82F6] hover:text-[#2563EB] font-semibold">
                  Forgot password?
                </Link>
              </div>

              <button
                type="submit"
                className="w-full h-14 px-4 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all font-bold text-xl shadow-lg shadow-[#3B82F6]/30"
              >
                Connexion
              </button>
            </form>

            <div className="mt-10 text-center">
              <p className="text-lg text-gray-600">
                Pas encore inscrit ?{' '}
                <Link to="/register/candidat" className="text-[#3B82F6] hover:text-[#2563EB] font-bold">
                  Candidat
                </Link>
                {' '}ou{' '}
                <Link to="/register/recruteur" className="text-[#3B82F6] hover:text-[#2563EB] font-bold">
                  Recruteur
                </Link>
              </p>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
}
