import { useState, useEffect, useRef } from 'react';
import { User, Upload, Calendar, Building, GraduationCap, Briefcase, Camera, Edit, Plus, Trash2 } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Experience {
  id: number;
  titrePoste: string;
  entreprise: string;
  dateDebut: string;
  dateFin: string;
  description: string;
}

interface Formation {
  id: number;
  diplome: string;
  etablissement: string;
  specialite: string;
  anneeObtention: string;  // Format YYYY-MM-DD
}

interface Competence {
  id: number;
  nom: string;
  categorie: string;
}

interface Candidat {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  telephone: string;
  adresse: string;
  dateNaissance: string;
  titreProfil: string;
  photoProfil: string;
  cv?: {
    id: number;
    cheminFichier: string;
    dateUpload: string;
  };
  experiences?: Experience[];
  formations?: Formation[];
}

export default function MonProfilCandidat() {
  const [candidat, setCandidat] = useState<Candidat | null>(null);
  const [loading, setLoading] = useState(true);
  const [uploading, setUploading] = useState(false);
  const [error, setError] = useState('');
  const [file, setFile] = useState<File | null>(null);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const [editing, setEditing] = useState(false);
  const [formData, setFormData] = useState<Partial<Candidat>>({});
  const [competences, setCompetences] = useState<Competence[]>([]);
  const [experiences, setExperiences] = useState<Experience[]>([]);
  const [formations, setFormations] = useState<Formation[]>([]);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    fetchProfil();
    fetchCompetences();
    fetchExperiences();
    fetchFormations();
  }, []);

  const fetchProfil = async () => {
    try {
      const response = await api.get('/candidats/mon-profil');
      setCandidat(response.data);
      setFormData(response.data);
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement du profil');
      setLoading(false);
      console.error('Error fetching profile:', err);
    }
  };

  const fetchCompetences = async () => {
    try {
      const response = await api.get('/candidats/competences');
      setCompetences(response.data);
    } catch (err) {
      console.error('Error fetching competences:', err);
    }
  };

  const fetchExperiences = async () => {
    try {
      const response = await api.get('/candidats/experiences');
      setExperiences(response.data);
    } catch (err) {
      console.error('Error fetching experiences:', err);
    }
  };

  const fetchFormations = async () => {
    try {
      const response = await api.get('/candidats/formations');
      setFormations(response.data);
    } catch (err) {
      console.error('Error fetching formations:', err);
    }
  };

  /**
   * Affiche le CV du candidat dans un nouvel onglet.
   * 
   * Problème corrigé: Le CV s'affichait comme du texte brut au lieu d'un PDF lisible.
   * Solution:
   * 1. Télécharge le CV via l'API avec responseType: 'blob' pour recevoir les données binaires
   * 2. Détermine le type MIME basé sur l'extension du fichier (.pdf ou .doc/.docx)
   * 3. Crée un Blob avec le type MIME correct pour que le navigateur reconnaisse le format
   * 4. Crée un lien temporaire, l'ajoute au DOM, clique dessus pour ouvrir le fichier
   * 5. Supprime le lien et libère l'URL blob pour éviter les fuites de mémoire
   * 
   * @throws Affiche une erreur si le téléchargement ou l'ouverture échoue
   */
  const handleViewCV = async () => {
    try {
      const response = await api.get(`/cvs/download/${candidat.cv.id}`, {
        responseType: 'blob',
      });
      
      // Déterminer le type MIME basé sur l'extension du fichier
      const fileName = candidat.cv.cheminFichier || '';
      let mimeType = 'application/pdf';
      if (fileName.endsWith('.doc') || fileName.endsWith('.docx')) {
        mimeType = 'application/msword';
      }
      
      const blob = new Blob([response.data], { type: mimeType });
      const url = window.URL.createObjectURL(blob);
      
      // Créer un lien temporaire et cliquer dessus
      const link = document.createElement('a');
      link.href = url;
      link.target = '_blank';
      link.style.display = 'none';
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      
      // Libérer l'URL après un délai
      setTimeout(() => window.URL.revokeObjectURL(url), 100);
    } catch (err) {
      setError('Erreur lors de l\'ouverture du CV');
      console.error('Error opening CV:', err);
    }
  };

  const handleSave = async () => {
    try {
      await api.put(`/candidats/${candidat?.id}`, formData);
      setCandidat({ ...candidat, ...formData } as Candidat);
      setEditing(false);
    } catch (err) {
      setError('Erreur lors de la mise à jour du profil');
      console.error('Error updating profile:', err);
    }
  };

  const handleCancel = () => {
    setFormData(candidat || {});
    setEditing(false);
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleUploadCV = async () => {
    console.log('=== handleUploadCV called ===');
    console.log('File:', file);
    
    if (!file) {
      console.log('No file selected, returning');
      return;
    }

    setUploading(true);
    try {
      console.log('Creating FormData...');
      const formData = new FormData();
      formData.append('file', file);
      
      console.log('FormData created, sending request to /cvs/upload...');
      const response = await api.post('/cvs/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      
      console.log('Upload successful:', response);

      setFile(null);
      fetchProfil();
    } catch (err) {
      console.error('Error uploading CV:', err);
      setError('Erreur lors de l\'upload du CV: ' + (err as Error).message);
    } finally {
      setUploading(false);
    }
  };

  const handleDeleteCV = async () => {
    if (!confirm('Êtes-vous sûr de vouloir supprimer votre CV?')) return;

    try {
      await api.delete('/cvs/mon-cv');
      if (candidat) {
        setCandidat({ ...candidat, cv: undefined });
      }
    } catch (err) {
      setError('Erreur lors de la suppression du CV');
      console.error('Error deleting CV:', err);
    }
  };

  const handleChangeCV = () => {
    document.getElementById('cv-upload')?.click();
  };

  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return 'Non renseignée';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Date invalide';
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      handlePhotoUpload(e.target.files[0]);
    }
  };

  const handlePhotoUpload = async (file: File) => {
    setUploadingPhoto(true);
    try {
      // Pour l'instant, on simule l'upload en utilisant une URL temporaire
      const reader = new FileReader();
      reader.onloadend = () => {
        setCandidat({ ...candidat!, photoProfil: reader.result as string });
        setUploadingPhoto(false);
      };
      reader.readAsDataURL(file);
    } catch (err) {
      setError('Erreur lors de l\'upload de la photo');
      setUploadingPhoto(false);
      console.error('Error uploading photo:', err);
    }
  };

  if (loading) {
    return (
      <MainLayout role="CANDIDAT" userName="Candidat">
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Chargement...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="CANDIDAT" userName={candidat?.nom + ' ' + candidat?.prenom || 'Candidat'}>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[#1E293B]">Mon Profil</h1>
        <p className="text-gray-600 mt-2">Gérez vos informations personnelles et votre CV</p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
          {error}
        </div>
      )}

      {candidat && (
        <div className="space-y-6">
          {/* Informations Personnelles */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold text-[#1E293B] flex items-center gap-2">
                <User className="w-5 h-5 text-[#3B82F6]" />
                Informations Personnelles
              </h2>
              {!editing && (
                <button
                  onClick={() => setEditing(true)}
                  className="text-[#3B82F6] hover:text-[#2563EB] transition-colors flex items-center gap-1"
                >
                  <Edit className="w-4 h-4" />
                  Modifier
                </button>
              )}
            </div>

            {/* Photo de Profil et Header */}
            <div className="flex items-start gap-3 mb-6">
              <div className="relative">
                <div className="w-32 h-32 bg-[#3B82F6]/10 rounded-full flex items-center justify-center overflow-hidden">
                  {candidat.photoProfil ? (
                    <img
                      src={candidat.photoProfil}
                      alt="Photo de profil"
                      className="w-32 h-32 rounded-full object-cover"
                    />
                  ) : (
                    <User className="w-16 h-16 text-[#3B82F6]" />
                  )}
                </div>
                <button
                  onClick={() => fileInputRef.current?.click()}
                  className="absolute bottom-0 right-0 bg-[#3B82F6] text-white p-2 rounded-full hover:bg-[#2563EB] transition-colors shadow-lg"
                  title="Changer la photo"
                >
                  {uploadingPhoto ? (
                    <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                  ) : (
                    <Camera className="w-4 h-4" />
                  )}
                </button>
                <input
                  ref={fileInputRef}
                  type="file"
                  accept="image/*"
                  onChange={handlePhotoChange}
                  className="hidden"
                />
              </div>
              <div className="flex-1 pt-2">
                <h3 className="text-3xl font-bold text-[#1E293B] leading-none">
                  {candidat.nom} {candidat.prenom}
                </h3>
                <p className="text-gray-600 text-base mt-2">{candidat.titreProfil || 'Candidat'}</p>
              </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Nom</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.nom || ''}
                    onChange={(e) => setFormData({ ...formData, nom: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{candidat.nom}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Prénom</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.prenom || ''}
                    onChange={(e) => setFormData({ ...formData, prenom: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{candidat.prenom}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Email</label>
                <p className="text-[#1E293B] text-base font-medium">{candidat.email}</p>
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Téléphone</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.telephone || ''}
                    onChange={(e) => setFormData({ ...formData, telephone: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{candidat.telephone || 'Non renseigné'}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Adresse</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.adresse || ''}
                    onChange={(e) => setFormData({ ...formData, adresse: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{candidat.adresse || 'Non renseignée'}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1 flex items-center gap-1">
                  <Calendar className="w-4 h-4" />
                  Date de naissance
                </label>
                {editing ? (
                  <input
                    type="date"
                    value={formData.dateNaissance || ''}
                    onChange={(e) => setFormData({ ...formData, dateNaissance: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{candidat.dateNaissance ? formatDate(candidat.dateNaissance) : 'Non renseignée'}</p>
                )}
              </div>
            </div>
          </div>

          {editing && (
            <div className="flex gap-3 justify-end">
              <button
                onClick={handleCancel}
                className="px-4 py-2 border border-[#E2E8F0] rounded-lg hover:bg-gray-50 transition-colors"
              >
                Annuler
              </button>
              <button
                onClick={handleSave}
                className="px-4 py-2 bg-[#3B82F6] text-white rounded-lg hover:bg-[#2563EB] transition-colors"
              >
                Enregistrer
              </button>
            </div>
          )}

          {/* CV */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <h2 className="text-xl font-semibold text-[#1E293B] mb-4 flex items-center gap-2">
              <Briefcase className="w-5 h-5 text-[#10B981]" />
              Mon CV
            </h2>
            <input
              type="file"
              accept=".pdf,.doc,.docx"
              onChange={handleFileChange}
              className="hidden"
              id="cv-upload"
            />
            {candidat.cv ? (
              <div className="space-y-4">
                <div className="flex items-center justify-between p-4 bg-gray-50 rounded-lg">
                  <div>
                    <p className="font-semibold text-[#1E293B] text-base">CV uploadé</p>
                    <p className="text-sm text-gray-600">Uploadé le {formatDate(candidat.cv.dateUpload)}</p>
                  </div>
                  <div className="flex gap-2">
                    <button
                      onClick={handleViewCV}
                      className="bg-[#10B981] text-white px-4 py-2 rounded-lg hover:bg-[#059669] transition-colors flex items-center gap-2"
                    >
                      <Briefcase className="w-4 h-4" />
                      Voir le CV
                    </button>
                    <button
                      onClick={handleChangeCV}
                      className="bg-[#3B82F6] text-white px-4 py-2 rounded-lg hover:bg-[#2563EB] transition-colors flex items-center gap-2"
                    >
                      <Upload className="w-4 h-4" />
                      Changer le CV
                    </button>
                  </div>
                </div>
                {file && (
                  <div className="border-2 border-dashed border-[#E2E8F0] rounded-lg p-6 text-center">
                    <p className="text-gray-600 mb-4 text-base">Remplacer le CV actuel</p>
                    <p className="text-sm text-gray-600 mb-2">{file.name}</p>
                    <div className="flex gap-2 justify-center">
                      <button
                        onClick={handleUploadCV}
                        disabled={uploading}
                        className="bg-[#10B981] text-white px-4 py-2 rounded-lg hover:bg-[#059669] transition-colors disabled:opacity-50"
                      >
                        {uploading ? 'Upload en cours...' : 'Uploader'}
                      </button>
                      <button
                        onClick={() => setFile(null)}
                        className="px-4 py-2 border border-[#E2E8F0] rounded-lg hover:bg-gray-50 transition-colors"
                      >
                        Annuler
                      </button>
                    </div>
                  </div>
                )}
              </div>
            ) : (
              <div className="border-2 border-dashed border-[#E2E8F0] rounded-lg p-6 text-center">
                <Upload className="w-12 h-12 text-gray-400 mx-auto mb-4" />
                <p className="text-gray-600 mb-4 text-base">Aucun CV uploadé</p>
                <label
                  htmlFor="cv-upload"
                  className="inline-block bg-[#3B82F6] text-white px-4 py-2 rounded-lg cursor-pointer hover:bg-[#2563EB] transition-colors"
                >
                  Choisir un fichier
                </label>
                {file && (
                  <div className="mt-4">
                    <p className="text-sm text-gray-600 mb-2">{file.name}</p>
                    <button
                      onClick={handleUploadCV}
                      disabled={uploading}
                      className="bg-[#10B981] text-white px-4 py-2 rounded-lg hover:bg-[#059669] transition-colors disabled:opacity-50"
                    >
                      {uploading ? 'Upload en cours...' : 'Uploader'}
                    </button>
                    <button
                      onClick={() => setFile(null)}
                      className="ml-2 px-4 py-2 border border-[#E2E8F0] rounded-lg hover:bg-gray-50 transition-colors"
                    >
                      Annuler
                    </button>
                  </div>
                )}
              </div>
            )}
          </div>

          {/* Compétences */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <h2 className="text-xl font-semibold text-[#1E293B] mb-4 flex items-center gap-2">
              <Briefcase className="w-5 h-5 text-[#3B82F6]" />
              Compétences (extraites du CV par IA)
            </h2>
            {competences && competences.length > 0 ? (
              <div className="flex flex-wrap gap-2">
                {competences.map((competence) => (
                  <span key={competence.id} className="px-3 py-1 bg-[#3B82F6]/10 text-[#3B82F6] rounded-full text-sm font-medium">
                    {competence.nom}
                  </span>
                ))}
              </div>
            ) : (
              <p className="text-gray-600 text-base">Aucune compétence extraite. Uploadez un CV pour que l'IA extrait vos compétences.</p>
            )}
          </div>

          {/* Formations */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <h2 className="text-xl font-semibold text-[#1E293B] mb-4 flex items-center gap-2">
              <GraduationCap className="w-5 h-5 text-[#F59E0B]" />
              Formations
            </h2>
            {formations && formations.length > 0 ? (
              <div className="space-y-3">
                {formations.map((formation) => (
                  <div key={formation.id} className="p-4 bg-gray-50 rounded-lg">
                    <p className="font-semibold text-[#1E293B] text-base">{formation.diplome}</p>
                    <p className="text-base text-gray-600">{formation.etablissement}</p>
                    {formation.specialite && <p className="text-sm text-gray-500 mt-1">{formation.specialite}</p>}
                    <p className="text-sm text-gray-500 mt-1">
                      Année d'obtention: {formation.anneeObtention ? formation.anneeObtention.split('-')[0] : 'Non renseignée'}
                    </p>
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600 text-base">Aucune formation renseignée</p>
            )}
          </div>

          {/* Expériences */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <h2 className="text-xl font-semibold text-[#1E293B] mb-4 flex items-center gap-2">
              <Building className="w-5 h-5 text-[#8B5CF6]" />
              Expériences Professionnelles
            </h2>
            {experiences && experiences.length > 0 ? (
              <div className="space-y-3">
                {experiences.map((experience) => (
                  <div key={experience.id} className="p-4 bg-gray-50 rounded-lg">
                    <p className="font-semibold text-[#1E293B] text-base">{experience.titrePoste}</p>
                    <p className="text-base text-gray-600 flex items-center gap-1">
                      <Building className="w-4 h-4" />
                      {experience.entreprise}
                    </p>
                    <p className="text-sm text-gray-500 mt-1">
                      {formatDate(experience.dateDebut)} - {experience.dateFin ? formatDate(experience.dateFin) : 'Présent'}
                    </p>
                    {experience.description && <p className="text-sm text-gray-600 mt-2">{experience.description}</p>}
                  </div>
                ))}
              </div>
            ) : (
              <p className="text-gray-600 text-base">Aucune expérience renseignée</p>
            )}
          </div>
        </div>
      )}
    </MainLayout>
  );
}
