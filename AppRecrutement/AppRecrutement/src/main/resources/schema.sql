-- Supprimer la colonne type_contrat de la table offre
ALTER TABLE offre DROP COLUMN IF EXISTS type_contrat;

-- Supprimer la colonne domaine de la table candidat
ALTER TABLE candidat DROP COLUMN IF EXISTS domaine;
