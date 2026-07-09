import { Navigate } from 'react-router-dom';

/**
 * Composant ProtectedRoute - Protection des routes par rôle
 * 
 * Ce composant est un wrapper pour protéger les routes selon le rôle de l'utilisateur.
 * Il vérifie que l'utilisateur est authentifié et a le rôle requis.
 * 
 * FONCTIONNALITÉS :
 * - Vérifier si l'utilisateur est authentifié (userRole dans localStorage)
 * - Vérifier si l'utilisateur a le rôle requis (allowedRoles)
 * - Rediriger vers /login si non authentifié
 * - Rediriger vers / si rôle non autorisé
 * 
 * SÉCURITÉ : Protection côté frontend
 * - Cette protection est une première ligne de défense
 * - La sécurité réelle doit être implémentée côté backend (authentification JWT)
 * - Ce composant empêche l'accès UI mais ne remplace pas la sécurité backend
 * 
 * CHOIX TECHNIQUE : localStorage pour le rôle
 * - Le rôle est stocké dans localStorage après connexion
 * - Permet une vérification rapide côté frontend
 * - Doit être synchronisé avec le backend (JWT token)
 * 
 * UTILISATION :
 * <ProtectedRoute allowedRoles={['RECRUTEUR']}>
 *   <CreerQuiz />
 * </ProtectedRoute>
 */

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles: string[];
}

export default function ProtectedRoute({ children, allowedRoles }: ProtectedRouteProps) {
  // Récupérer le rôle de l'utilisateur depuis localStorage
  const userRole = localStorage.getItem('userRole');
  
  console.log('ProtectedRoute - userRole:', userRole);
  console.log('ProtectedRoute - allowedRoles:', allowedRoles);
  console.log('ProtectedRoute - localStorage token:', localStorage.getItem('token'));

  // Si l'utilisateur n'est pas authentifié, rediriger vers login
  if (!userRole) {
    console.log('ProtectedRoute - Pas de userRole, redirection vers login');
    return <Navigate to="/login" replace />;
  }

  // Normaliser le rôle : retirer le préfixe ROLE_ si présent
  const normalizedUserRole = userRole.replace('ROLE_', '');
  const normalizedAllowedRoles = allowedRoles.map(role => role.replace('ROLE_', ''));
  
  console.log('ProtectedRoute - normalizedUserRole:', normalizedUserRole);
  console.log('ProtectedRoute - normalizedAllowedRoles:', normalizedAllowedRoles);

  // Si l'utilisateur n'a pas le rôle requis, rediriger vers home
  if (!normalizedAllowedRoles.includes(normalizedUserRole)) {
    console.log('ProtectedRoute - Rôle non autorisé, redirection vers home');
    return <Navigate to="/" replace />;
  }

  // Si tout est OK, afficher le composant enfant
  console.log('ProtectedRoute - Accès autorisé');
  return <>{children}</>;
}
