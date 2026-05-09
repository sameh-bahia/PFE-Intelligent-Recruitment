import { useState, useEffect, useRef } from 'react';
import { User, Camera, Calendar, MapPin, Building, Briefcase, Edit } from 'lucide-react';
import api from '@/lib/api';
import MainLayout from '@/components/layout/MainLayout';

interface Recruteur {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  nomEntreprise: string;
  poste: string;
  photoProfil: string;
  dateNaissance: string;
  adresse: string;
  lieuTravailPrecedent: string;
  entreprisePrecedente: string;
}

export default function MonProfilRecruteur() {
  const [recruteur, setRecruteur] = useState<Recruteur | null>(null);
  const [loading, setLoading] = useState(true);
  const [editing, setEditing] = useState(false);
  const [error, setError] = useState('');
  const [formData, setFormData] = useState<Partial<Recruteur>>({});
  const [photoFile, setPhotoFile] = useState<File | null>(null);
  const [uploadingPhoto, setUploadingPhoto] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    fetchProfil();
  }, []);

  const fetchProfil = async () => {
    try {
      const response = await api.get('/recruteurs/mon-profil');
      setRecruteur(response.data);
      setFormData(response.data);
      setLoading(false);
    } catch (err) {
      setError('Erreur lors du chargement du profil');
      setLoading(false);
      console.error('Error fetching profile:', err);
    }
  };

  const handleSave = async () => {
    try {
      await api.put(`/recruteurs/${recruteur?.id}`, formData);
      setRecruteur({ ...recruteur, ...formData } as Recruteur);
      setEditing(false);
    } catch (err) {
      setError('Erreur lors de la mise à jour du profil');
      console.error('Error updating profile:', err);
    }
  };

  const handleCancel = () => {
    setFormData(recruteur || {});
    setEditing(false);
  };

  const formatDate = (dateString: string) => {
    if (!dateString) return 'Non renseignée';
    const date = new Date(dateString);
    if (isNaN(date.getTime())) return 'Date invalide';
    return date.toLocaleDateString('fr-FR', { day: '2-digit', month: '2-digit', year: 'numeric' });
  };

  const handlePhotoChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files && e.target.files[0]) {
      setPhotoFile(e.target.files[0]);
      handlePhotoUpload(e.target.files[0]);
    }
  };

  const handlePhotoUpload = async (file: File) => {
    setUploadingPhoto(true);
    try {
      const formData = new FormData();
      formData.append('file', file);

      // Pour l'instant, on simule l'upload en utilisant une URL temporaire
      const reader = new FileReader();
      reader.onloadend = () => {
        setFormData({ ...formData, photoProfil: reader.result as string });
        setRecruteur({ ...recruteur!, photoProfil: reader.result as string });
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
      <MainLayout role="RECRUTEUR" userName="Recruteur">
        <div className="flex items-center justify-center h-64">
          <div className="text-gray-600">Chargement...</div>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout role="RECRUTEUR" userName={recruteur?.nom + ' ' + recruteur?.prenom || 'Recruteur'}>
      <div className="mb-8">
        <h1 className="text-3xl font-bold text-[#1E293B]">Mon Profil</h1>
        <p className="text-gray-600 mt-2">Gérez vos informations personnelles et professionnelles</p>
      </div>

      {error && (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-xl mb-6">
          {error}
        </div>
      )}

      {recruteur && (
        <div className="space-y-6">
          {/* Photo de Profil et Informations de base */}
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

            <div className="flex items-start gap-3 mb-6">
              <div className="relative">
                <div className="w-32 h-32 bg-[#3B82F6]/10 rounded-full flex items-center justify-center overflow-hidden">
                  {recruteur.photoProfil ? (
                    <img
                      src={recruteur.photoProfil}
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
                {editing ? (
                  <div className="space-y-2">
                    <input
                      type="text"
                      value={formData.photoProfil || ''}
                      onChange={(e) => setFormData({ ...formData, photoProfil: e.target.value })}
                      placeholder="URL de la photo de profil"
                      className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg"
                    />
                  </div>
                ) : (
                  <div>
                    <h3 className="text-3xl font-bold text-[#1E293B] leading-none">
                      {recruteur.nom} {recruteur.prenom}
                    </h3>
                    <p className="text-gray-600 text-base mt-2">{recruteur.poste}</p>
                  </div>
                )}
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
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.nom}</p>
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
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.prenom}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Email</label>
                <p className="text-[#1E293B] text-base font-medium">{recruteur.email}</p>
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
                  <p className="text-[#1E293B] text-base font-medium">{formatDate(recruteur.dateNaissance)}</p>
                )}
              </div>
              <div className="md:col-span-2">
                <label className="block text-base font-medium text-gray-600 mb-1">Adresse</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.adresse || ''}
                    onChange={(e) => setFormData({ ...formData, adresse: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.adresse || 'Non renseignée'}</p>
                )}
              </div>
            </div>
          </div>

          {/* Informations Professionnelles */}
          <div className="bg-white rounded-xl shadow-sm p-6 border border-[#E2E8F0]">
            <h2 className="text-xl font-semibold text-[#1E293B] mb-4 flex items-center gap-2">
              <Briefcase className="w-5 h-5 text-[#10B981]" />
              Informations Professionnelles
            </h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Entreprise actuelle</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.nomEntreprise || ''}
                    onChange={(e) => setFormData({ ...formData, nomEntreprise: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.nomEntreprise}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1">Poste actuel</label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.poste || ''}
                    onChange={(e) => setFormData({ ...formData, poste: e.target.value })}
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.poste}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1 flex items-center gap-1">
                  <MapPin className="w-4 h-4" />
                  Lieu de travail précédent
                </label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.lieuTravailPrecedent || ''}
                    onChange={(e) => setFormData({ ...formData, lieuTravailPrecedent: e.target.value })}
                    placeholder="Ex: Paris, France"
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.lieuTravailPrecedent || 'Non renseigné'}</p>
                )}
              </div>
              <div>
                <label className="block text-base font-medium text-gray-600 mb-1 flex items-center gap-1">
                  <Building className="w-4 h-4" />
                  Entreprise précédente
                </label>
                {editing ? (
                  <input
                    type="text"
                    value={formData.entreprisePrecedente || ''}
                    onChange={(e) => setFormData({ ...formData, entreprisePrecedente: e.target.value })}
                    placeholder="Ex: Google"
                    className="w-full px-3 py-2 border border-[#E2E8F0] rounded-lg text-base"
                  />
                ) : (
                  <p className="text-[#1E293B] text-base font-medium">{recruteur.entreprisePrecedente || 'Non renseignée'}</p>
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
        </div>
      )}
    </MainLayout>
  );
}
