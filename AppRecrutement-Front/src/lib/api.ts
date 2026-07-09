import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Intercepteur pour ajouter le token JWT si disponible
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    // Ne pas ajouter le token pour les endpoints d'authentification (login/register)
    const isAuthEndpoint = config.url?.includes('/auth/login') || 
                          config.url?.includes('/auth/register');
    
    if (token && !isAuthEndpoint) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Intercepteur pour gérer les erreurs 401 (Unauthorized)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Ne déconnecter que si l'erreur n'est pas une erreur de création/modification
    // Permet de voir l'erreur dans le frontend sans être déconnecté
    if (error.response && error.response.status === 401) {
      console.error('Erreur 401 détectée:', error.config?.url, error.response?.data);
      // Ne déconnecter automatiquement que pour les requêtes de navigation
      // Pas pour les requêtes POST/PUT/DELETE
      const isNavigationRequest = error.config?.method === 'get' || 
                                  error.config?.method === undefined;
      
      if (isNavigationRequest) {
        localStorage.removeItem('token');
        localStorage.removeItem('userRole');
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default api;
