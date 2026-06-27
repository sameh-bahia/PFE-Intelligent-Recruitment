import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, Briefcase, Check, X, User, Lock, Unlock, Eye } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Candidature {
  id: number;
  offre: {
    id: number;
    titre: string;
    description?: string;
    typeContrat?: string;
    salaire?: string;
    lieu?: string;
    estOuverte?: boolean;
  };
  candidat: {
    id: number;
    nom: string;
    email: string;
    cvId?: number;
  };
  statut: string;
  dateCandidature: string;
  scoreCompatibilite?: number;
  scoreRelatif?: number;
}

interface GroupedCandidatures {
  offre: Candidature['offre'];
  candidatures: Candidature[];
}

export default function CandidaturesRecues() {
  const [candidatures, setCandidatures] = useState<Candidature[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [showAcceptModal, setShowAcceptModal] = useState(false);
  const [selectedCandidature, setSelectedCandidature] = useState<Candidature | null>(null);
  const [interviewDate, setInterviewDate] = useState('');
  const [interviewType, setInterviewType] = useState<'EN_LIGNE' | 'PRESENTIEL'>('EN_LIGNE');
  const [interviewLink, setInterviewLink] = useState('');

  useEffect(() => {
    fetchCandidatures();
  }, []);

  // Changement: Utilisation de l'endpoint trié par score de compatibilité
  // Pourquoi: Les candidats sont automatiquement triés par score décroissant
  // Résultat: Les meilleurs candidats apparaissent en premier
  const fetchCandidatures = async () => {
    try {
      const response = await api.get('/candidatures/recruteur/candidatures-recues/triees');
      setCandidatures(response.data);
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement des candidatures');
      setLoading(false);
      console.error('Error fetching candidatures:', err);
    }
  };

  // Changement: Utilisation des valeurs d'enum correctes (sans accents)
  // Problème résolu: Le frontend envoyait 'ACCEPTÉE' et 'REFUSÉE' (avec accents)
  // mais l'enum backend utilise 'ACCEPTEE' et 'REFUSEE' (sans accents)
  const handleAccept = async (id: number) => {
    const candidature = candidatures.find(c => c.id === id);
    if (candidature) {
      setSelectedCandidature(candidature);
      setShowAcceptModal(true);
    }
  };

  const handleConfirmAccept = async () => {
    if (!selectedCandidature) return;

    try {
      await api.put(`/candidatures/${selectedCandidature.id}/statut`, {
        statut: 'ACCEPTEE',
        dateEntretien: interviewDate,
        typeEntretien: interviewType,
        lienEntretien: interviewLink
      });
      setCandidatures(candidatures.map(c =>
        c.id === selectedCandidature.id ? { ...c, statut: 'ACCEPTEE' } : c
      ));
      setShowAcceptModal(false);
      setSelectedCandidature(null);
      setInterviewDate('');
      setInterviewType('EN_LIGNE');
      setInterviewLink('');
    } catch (err) {
      setError('Erreur lors de l\'acceptation');
      console.error('Error accepting candidature:', err);
    }
  };

  const handleReject = async (id: number) => {
    try {
      await api.put(`/candidatures/${id}/statut`, { statut: 'REFUSEE' });
      setCandidatures(candidatures.map(c =>
        c.id === id ? { ...c, statut: 'REFUSEE' } : c
      ));
    } catch (err) {
      setError('Erreur lors du refus');
      console.error('Error rejecting candidature:', err);
    }
  };

  const handleCloseOffre = async (offreId: number, currentStatus: boolean) => {
    try {
      await api.put(`/offres/${offreId}/statut`, { estOuverte: !currentStatus });
      setCandidatures(candidatures.map(c =>
        c.offre.id === offreId ? { ...c, offre: { ...c.offre, estOuverte: !currentStatus } } : c
      ));
    } catch (err) {
      setError('Erreur lors de la modification du statut de l\'offre');
      console.error('Error closing offre:', err);
    }
  };

  const handleViewCV = async (cvId: number) => {
    try {
      const response = await api.get(`/cvs/download/${cvId}`, {
        responseType: 'blob'
      });
      const blob = new Blob([response.data], { type: 'application/pdf' });
      const url = window.URL.createObjectURL(blob);
      window.open(url, '_blank');
    } catch (err) {
      setError('Erreur lors de l\'ouverture du CV');
      console.error('Error viewing CV:', err);
    }
  };

  // Changement: Utilisation des valeurs d'enum correctes (sans accents)
  // Problème résolu: Le frontend comparait avec 'ACCEPTÉE' et 'REFUSÉE' (avec accents)
  // mais l'enum backend retourne 'ACCEPTEE' et 'REFUSEE' (sans accents)
  const getStatutBadge = (statut: string) => {
    switch (statut) {
      case 'ACCEPTEE':
        return (
          <span className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-semibold bg-green-50 text-green-700 border border-green-200">
            Acceptée
          </span>
        );
      case 'REFUSEE':
        return (
          <span className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-200">
            Refusée
          </span>
        );
      default:
        return (
          <span className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full text-xs font-semibold bg-amber-50 text-amber-700 border border-amber-200">
            <span className="relative flex h-2 w-2">
              <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-amber-400 opacity-75"></span>
              <span className="relative inline-flex rounded-full h-2 w-2 bg-amber-500"></span>
            </span>
            En attente
          </span>
        );
    }
  };

  const getScoreGradient = (score: number) => {
    if (score >= 0.7) {
      return 'linear-gradient(90deg, #10B981, #3B82F6)';
    } else if (score >= 0.5) {
      return 'linear-gradient(90deg, #F59E0B, #10B981)';
    } else {
      return 'linear-gradient(90deg, #EF4444, #F59E0B)';
    }
  };

  const getScoreShadowColor = (score: number) => {
    if (score >= 0.7) {
      return 'rgba(16, 185, 129, 0.5)';
    } else if (score >= 0.5) {
      return 'rgba(245, 158, 11, 0.5)';
    } else {
      return 'rgba(239, 68, 68, 0.5)';
    }
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return 'Date non disponible';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Date invalide';
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  // Grouper les candidatures par offre
  const groupedCandidatures = candidatures.reduce((acc: GroupedCandidatures[], candidature) => {
    const existingOffre = acc.find(group => group.offre.id === candidature.offre.id);
    if (existingOffre) {
      existingOffre.candidatures.push(candidature);
    } else {
      acc.push({
        offre: candidature.offre,
        candidatures: [candidature]
      });
    }
    return acc;
  }, []);

  if (loading) {
    return (
      <MainLayout role="RECRUTEUR" userName="Recruteur">
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Chargement...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="RECRUTEUR" userName="Recruteur">
      <div className="mb-8" style={{ fontFamily: 'Inter, sans-serif' }}>
        <h1 className="text-3xl font-bold text-[#1E293B]">Candidatures Reçues</h1>
        <p className="text-gray-600 mt-2">Consultez et gérez les candidatures pour vos offres</p>
      </div>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8" style={{ fontFamily: 'Inter, sans-serif' }}>
        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Total Candidatures</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">{candidatures.length}</p>
            </div>
            <div className="p-3 bg-[#3B82F6]/10 rounded-xl">
              <Users className="w-8 h-8 text-[#3B82F6]" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">En attente</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">
                {candidatures.filter(c => c.statut === 'EN_ATTENTE' || c.statut === null).length}
              </p>
            </div>
            <div className="p-3 bg-[#F59E0B]/10 rounded-xl">
              <Briefcase className="w-8 h-8 text-[#F59E0B]" />
            </div>
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
          <div className="flex items-center justify-between">
            <div>
              <p className="text-sm text-gray-600">Acceptées</p>
              <p className="text-3xl font-bold text-[#1E293B] mt-2">
                {candidatures.filter(c => c.statut === 'ACCEPTEE').length}
              </p>
            </div>
            <div className="p-3 bg-[#10B981]/10 rounded-xl">
              <Check className="w-8 h-8 text-[#10B981]" />
            </div>
          </div>
        </div>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
          {error}
        </div>
      )}

      {groupedCandidatures.length === 0 ? (
        <div className="bg-white rounded-xl shadow-sm p-12 text-center border border-[#E2E8F0]">
          <Users className="w-16 h-16 text-gray-300 mx-auto mb-4" />
          <p className="text-gray-600 mb-4">Aucune candidature reçue pour le moment.</p>
          <Link
            to="/dashboard/recruteur/offres"
            className="text-[#3B82F6] hover:text-[#2563EB] font-medium"
          >
            Voir vos offres
          </Link>
        </div>
      ) : (
        <div className="space-y-6" style={{ fontFamily: 'Inter, sans-serif' }}>
          {groupedCandidatures.map((group) => (
            <div key={group.offre.id} className="bg-white rounded-xl shadow-sm border border-[#E2E8F0] overflow-hidden">
              <div className="bg-gray-50 px-6 py-4 border-b border-gray-200 flex justify-between items-start">
                <div>
                  <h3 className="text-lg font-semibold text-[#1E293B]">{group.offre.titre}</h3>
                  <p className="text-sm text-gray-600 mt-1">
                    {group.offre.typeContrat} • {group.offre.salaire} • {group.offre.lieu}
                  </p>
                </div>
                <button
                  onClick={() => handleCloseOffre(group.offre.id, group.offre.estOuverte ?? true)}
                  className={`flex items-center gap-2 px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-200 border ${
                    group.offre.estOuverte 
                      ? 'border-gray-300 text-gray-700 hover:bg-gray-50 hover:border-gray-400' 
                      : 'border-green-500 text-green-700 hover:bg-green-50'
                  }`}
                  title={group.offre.estOuverte ? 'Fermer l\'offre' : 'Ouvrir l\'offre'}
                >
                  {group.offre.estOuverte ? <Lock className="w-4 h-4" /> : <Unlock className="w-4 h-4" />}
                  {group.offre.estOuverte ? 'Fermer' : 'Ouvrir'}
                </button>
              </div>
              {group.candidatures.length === 0 ? (
                <div className="p-8 text-center text-gray-500">
                  Aucune candidature pour cette offre
                </div>
              ) : (
                <table className="min-w-full divide-y divide-gray-200">
                  <thead className="bg-gray-50">
                    <tr>
                      <th className="w-64 px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        Candidat
                      </th>
                      <th className="w-32 px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        Score
                      </th>
                      <th className="w-28 px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        Statut
                      </th>
                      <th className="w-36 px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        Date
                      </th>
                      <th className="w-32 px-6 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wider">
                        Actions
                      </th>
                    </tr>
                  </thead>
                  <tbody className="bg-white divide-y divide-gray-200">
                    {group.candidatures.map((candidature) => (
                      <tr key={candidature.id} className="hover:bg-gray-50 hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 cursor-default">
                        <td className="px-6 py-4">
                          <div className="flex items-center gap-3">
                            <div className="flex-shrink-0">
                              <div className="w-10 h-10 rounded-full bg-[#3B82F6]/10 flex items-center justify-center">
                                <User className="w-5 h-5 text-[#3B82F6]" />
                              </div>
                            </div>
                            <div>
                              <p className="text-base font-bold text-[#3B82F6]">{candidature.candidat.nom}</p>
                              <p className="text-sm text-gray-600">{candidature.candidat.email}</p>
                            </div>
                          </div>
                        </td>
                        <td className="w-32 px-6 py-4 whitespace-nowrap text-sm">
                          {candidature.scoreRelatif !== null && candidature.scoreRelatif !== undefined ? (
                            <div className="flex items-center gap-3">
                              <div className="flex-1 bg-gray-100 rounded-full h-2.5 overflow-hidden">
                                <div
                                  className="h-2.5 rounded-full transition-all duration-300"
                                  style={{
                                    width: `${(candidature.scoreRelatif * 100).toFixed(0)}%`,
                                    background: getScoreGradient(candidature.scoreRelatif),
                                    boxShadow: `0 0 8px ${getScoreShadowColor(candidature.scoreRelatif)}`
                                  }}
                                ></div>
                              </div>
                              <span className="font-semibold text-sm" style={{ color: getScoreShadowColor(candidature.scoreRelatif).replace('0.5)', '1)') }}>
                                {(candidature.scoreRelatif * 100).toFixed(0)}%
                              </span>
                            </div>
                          ) : candidature.scoreCompatibilite !== null && candidature.scoreCompatibilite !== undefined ? (
                            <div className="flex items-center gap-3">
                              <div className="flex-1 bg-gray-100 rounded-full h-2.5 overflow-hidden">
                                <div
                                  className="h-2.5 rounded-full transition-all duration-300"
                                  style={{
                                    width: `${(candidature.scoreCompatibilite * 100).toFixed(0)}%`,
                                    background: getScoreGradient(candidature.scoreCompatibilite),
                                    boxShadow: `0 0 8px ${getScoreShadowColor(candidature.scoreCompatibilite)}`
                                  }}
                                ></div>
                              </div>
                              <span className="font-semibold text-sm" style={{ color: getScoreShadowColor(candidature.scoreCompatibilite).replace('0.5)', '1)') }}>
                                {(candidature.scoreCompatibilite * 100).toFixed(0)}%
                              </span>
                            </div>
                          ) : (
                            <span className="text-gray-400">N/A</span>
                          )}
                        </td>
                        <td className="w-28 px-6 py-4 whitespace-nowrap text-sm">
                          {getStatutBadge(candidature.statut)}
                        </td>
                        <td className="w-36 px-6 py-4 whitespace-nowrap text-sm text-gray-600">
                          {formatDate(candidature.dateCandidature)}
                        </td>
                        <td className="w-32 px-6 py-4 whitespace-nowrap text-sm font-medium">
                          <div className="flex space-x-2">
                            {candidature.candidat.cvId && (
                              <button
                                onClick={() => handleViewCV(candidature.candidat.cvId)}
                                className="text-[#3B82F6] hover:text-[#2563EB] transition-colors"
                                title="Voir le CV"
                              >
                                <Eye className="w-5 h-5" />
                              </button>
                            )}
                            {candidature.statut !== 'ACCEPTEE' && (
                              <button
                                onClick={() => handleAccept(candidature.id)}
                                className="text-[#10B981] hover:text-[#059669] transition-colors"
                                title="Accepter"
                              >
                                <Check className="w-5 h-5" />
                              </button>
                            )}
                            {candidature.statut !== 'REFUSEE' && (
                              <button
                                onClick={() => handleReject(candidature.id)}
                                className="text-red-600 hover:text-red-700 transition-colors"
                                title="Refuser"
                              >
                                <X className="w-5 h-5" />
                              </button>
                            )}
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Modal d'acceptation avec détails d'entretien */}
      {showAcceptModal && selectedCandidature && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
          <div className="bg-white rounded-xl shadow-xl p-6 w-full max-w-md mx-4">
            <h2 className="text-xl font-bold text-[#1E293B] mb-4">
              Accepter la candidature de {selectedCandidature.candidat.nom}
            </h2>
            <p className="text-gray-600 mb-4">
              Veuillez renseigner les détails de l'entretien pour le poste de <strong>{selectedCandidature.offre.titre}</strong>
            </p>
            
            <div className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Date et heure de l'entretien *
                </label>
                <input
                  type="datetime-local"
                  value={interviewDate}
                  onChange={(e) => setInterviewDate(e.target.value)}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#3B82F6]"
                  required
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  Type d'entretien *
                </label>
                <select
                  value={interviewType}
                  onChange={(e) => setInterviewType(e.target.value as 'EN_LIGNE' | 'PRESENTIEL')}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#3B82F6]"
                >
                  <option value="EN_LIGNE">En ligne (visioconférence)</option>
                  <option value="PRESENTIEL">Présentiel</option>
                </select>
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {interviewType === 'EN_LIGNE' ? 'Lien Google Meet *' : 'Adresse de la société *'}
                </label>
                <input
                  type="text"
                  value={interviewLink}
                  onChange={(e) => setInterviewLink(e.target.value)}
                  placeholder={interviewType === 'EN_LIGNE' ? 'https://meet.google.com/...' : '123 Rue Example, Ville'}
                  className="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-[#3B82F6]"
                  required
                />
              </div>
            </div>

            <div className="flex justify-end gap-3 mt-6">
              <button
                onClick={() => {
                  setShowAcceptModal(false);
                  setSelectedCandidature(null);
                  setInterviewDate('');
                  setInterviewType('EN_LIGNE');
                  setInterviewLink('');
                }}
                className="px-4 py-2 text-gray-700 hover:text-gray-900 font-medium"
              >
                Annuler
              </button>
              <button
                onClick={handleConfirmAccept}
                className="px-4 py-2 bg-[#10B981] text-white rounded-lg hover:bg-[#059669] font-medium"
              >
                Confirmer et envoyer l'email
              </button>
            </div>
          </div>
        </div>
      )}
    </MainLayout>
  );
}
