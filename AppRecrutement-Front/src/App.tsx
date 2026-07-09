import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Login from './pages/Login';
import RegisterCandidat from './pages/RegisterCandidat';
import RegisterRecruteur from './pages/RegisterRecruteur';
import DashboardCandidat from './pages/DashboardCandidat';
import DashboardRecruteur from './pages/DashboardRecruteur';
import ListeOffres from './pages/offres/ListeOffres';
import CreerOffre from './pages/offres/CreerOffre';
import ModifierOffre from './pages/offres/ModifierOffre';
import CandidaturesRecues from './pages/offres/CandidaturesRecues';
import CreerQuiz from './pages/offres/CreerQuiz';
import PasserQuiz from './pages/offres/PasserQuiz';
import VoirOffres from './pages/candidatures/VoirOffres';
import PostulerOffre from './pages/candidatures/PostulerOffre';
import CompleterCandidature from './pages/candidatures/CompleterCandidature';
import LettreMotivationApresQuiz from './pages/candidatures/LettreMotivationApresQuiz';
import ListeCandidatures from './pages/candidatures/ListeCandidatures';
import MonProfilCandidat from './pages/profil/MonProfilCandidat';
import MonProfilRecruteur from './pages/profil/MonProfilRecruteur';
import AdminDashboard from './pages/admin/AdminDashboard';
import UsersManagement from './pages/admin/UsersManagement';
import ProtectedRoute from './components/auth/ProtectedRoute';
import DashboardLayout from './components/layout/DashboardLayout';

/**
 * Composant App - Configuration des routes React Router
 * 
 * Ce composant définit toutes les routes de l'application.
 * Les routes quiz sont protégées par rôle via ProtectedRoute.
 * 
 * ROUTES QUIZ (AJOUTÉES POUR LA FONCTIONNALITÉ QUIZ) :
 * - /dashboard/recruteur/offres/:offreId/creer-quiz : Création quiz (protégée RECRUTEUR)
 * - /dashboard/candidat/quiz/:quizId/candidature/:candidatureId : Passage quiz (protégée CANDIDAT)
 * 
 * SÉCURITÉ : Protection par rôle
 * - Les routes quiz utilisent ProtectedRoute pour vérifier le rôle
 * - CreerQuiz : accessible uniquement aux recruteurs
 * - PasserQuiz : accessible uniquement aux candidats
 * - Empêche l'accès non autorisé aux pages quiz
 * 
 * CHOIX TECHNIQUE : Paramètres de route dynamiques
 * - :offreId : ID de l'offre pour créer le quiz
 * - :quizId : ID du quiz pour le passer
 * - :candidatureId : ID de la candidature pour lier le score
 * - Permet une navigation flexible entre les pages
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/login" element={<Login />} />
        <Route path="/register/candidat" element={<RegisterCandidat />} />
        <Route path="/register/recruteur" element={<RegisterRecruteur />} />
        <Route path="/dashboard/candidat" element={<DashboardCandidat />} />
        <Route path="/dashboard/recruteur" element={<DashboardRecruteur />} />
        <Route path="/dashboard/recruteur/offres" element={<ListeOffres />} />
        <Route path="/dashboard/recruteur/offres/creer" element={<CreerOffre />} />
        
        {/* Route création quiz - PROTÉGÉE RECRUTEUR */}
        <Route path="/dashboard/recruteur/offres/:offreId/creer-quiz" element={
          <ProtectedRoute allowedRoles={['RECRUTEUR']}>
            <CreerQuiz />
          </ProtectedRoute>
        } />
        
        <Route path="/dashboard/recruteur/offres/modifier/:id" element={<ModifierOffre />} />
        <Route path="/dashboard/recruteur/candidatures" element={<CandidaturesRecues />} />
        <Route path="/dashboard/recruteur/profil" element={<MonProfilRecruteur />} />
        <Route path="/dashboard/candidat/offres" element={<VoirOffres />} />
        <Route path="/dashboard/candidat/offres/:id/postuler" element={<PostulerOffre />} />
        
        {/* Route passage quiz - PROTÉGÉE CANDIDAT */}
        <Route path="/dashboard/candidat/quiz/:quizId/offre/:offreId" element={
          <ProtectedRoute allowedRoles={['CANDIDAT']}>
            <PasserQuiz />
          </ProtectedRoute>
        } />
        
        {/* Route lettre motivation après quiz - PROTÉGÉE CANDIDAT */}
        <Route path="/dashboard/candidat/offres/:offreId/lettre-motivation" element={
          <ProtectedRoute allowedRoles={['CANDIDAT']}>
            <LettreMotivationApresQuiz />
          </ProtectedRoute>
        } />
        
        {/* Route complétion candidature - PROTÉGÉE CANDIDAT */}
        <Route path="/dashboard/candidat/candidatures/:candidatureId/completer" element={
          <ProtectedRoute allowedRoles={['CANDIDAT']}>
            <CompleterCandidature />
          </ProtectedRoute>
        } />
        
        <Route path="/dashboard/candidat/candidatures" element={<ListeCandidatures />} />
        <Route path="/dashboard/candidat/profil" element={<MonProfilCandidat />} />
        
        {/* Routes Admin - PROTÉGÉES ADMIN */}
        <Route path="/admin/dashboard" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout role="ADMIN">
              <AdminDashboard />
            </DashboardLayout>
          </ProtectedRoute>
        } />
        
        <Route path="/admin/users" element={
          <ProtectedRoute allowedRoles={['ADMIN']}>
            <DashboardLayout role="ADMIN">
              <UsersManagement />
            </DashboardLayout>
          </ProtectedRoute>
        } />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
