import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
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

      // Stocker le token
      localStorage.setItem('token', token);
      localStorage.setItem('role', role);

      // Rediriger selon le rôle
      if (role === 'ROLE_CANDIDAT') {
        navigate('/dashboard/candidat');
      } else if (role === 'ROLE_RECRUTEUR') {
        navigate('/dashboard/recruteur');
      } else {
        navigate('/dashboard/candidat'); // Par défaut
      }
    } catch (err) {
      setError('Email ou mot de passe incorrect');
      console.error('Login error:', err);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      <div className="max-w-md w-full space-y-8 p-8 bg-white rounded-lg shadow">
        <div>
          <h2 className="text-3xl font-bold text-center text-gray-900">
            Connexion
          </h2>
        </div>
        {error && (
          <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded">
            {error}
          </div>
        )}
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          <div>
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email
            </label>
            <input
              id="email"
              name="email"
              type="email"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
          <div>
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Mot de passe
            </label>
            <input
              id="password"
              name="password"
              type="password"
              required
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              className="mt-1 block w-full px-3 py-2 border border-gray-300 rounded-md shadow-sm focus:outline-none focus:ring-indigo-500 focus:border-indigo-500"
            />
          </div>
          <button
            type="submit"
            className="w-full flex justify-center py-2 px-4 border border-transparent rounded-md shadow-sm text-sm font-medium text-white bg-indigo-600 hover:bg-indigo-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          >
            Se connecter
          </button>
        </form>
        <div className="text-center space-y-2">
          <p className="text-sm text-gray-600">
            Pas encore inscrit ?{' '}
            <Link to="/register/candidat" className="text-indigo-600 hover:text-indigo-500 font-medium">
              Candidat
            </Link>
            {' '}ou{' '}
            <Link to="/register/recruteur" className="text-indigo-600 hover:text-indigo-500 font-medium">
              Recruteur
            </Link>
          </p>
        </div>
      </div>
    </div>
  );
}
