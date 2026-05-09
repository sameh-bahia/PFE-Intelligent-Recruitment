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
import VoirOffres from './pages/candidatures/VoirOffres';
import PostulerOffre from './pages/candidatures/PostulerOffre';
import ListeCandidatures from './pages/candidatures/ListeCandidatures';
import MonProfilCandidat from './pages/profil/MonProfilCandidat';
import MonProfilRecruteur from './pages/profil/MonProfilRecruteur';

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
        <Route path="/dashboard/recruteur/offres/modifier/:id" element={<ModifierOffre />} />
        <Route path="/dashboard/recruteur/candidatures" element={<CandidaturesRecues />} />
        <Route path="/dashboard/recruteur/profil" element={<MonProfilRecruteur />} />
        <Route path="/dashboard/candidat/offres" element={<VoirOffres />} />
        <Route path="/dashboard/candidat/offres/:id/postuler" element={<PostulerOffre />} />
        <Route path="/dashboard/candidat/candidatures" element={<ListeCandidatures />} />
        <Route path="/dashboard/candidat/profil" element={<MonProfilCandidat />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
