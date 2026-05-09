# ============================================================
# FICHIER : convert_to_spacy.py
# DESCRIPTION : Script pour convertir le JSON de données d'entraînement au format spaCy
# LOCALISATION : D:\PFE\AppRecrutement-CV-Service\data\convert_to_spacy.py
# FONCTION : Lit training_data.json et le convertit au format spaCy pour l'entraînement
# ============================================================

import json  # Pour lire le fichier JSON
import sys  # Pour manipuler les chemins
import os  # Pour manipuler les chemins

# Ajouter le dossier parent au chemin pour importer nlp.model
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))
from nlp.model import create_training_data_from_json

# Chemin vers le fichier JSON d'entraînement
TRAINING_DATA_JSON = "data/training_data.json"
# Chemin de sortie pour les données converties
OUTPUT_FILE = "data/training_data_spacy.py"

def main():
    """
    Fonction principale qui :
    1. Lit le fichier JSON de données d'entraînement
    2. Convertit les données au format spaCy
    3. Sauvegarde les données converties dans un fichier Python
    """
    print("=" * 60)
    print("CONVERSION DES DONNÉES D'ENTRAÎNEMENT JSON VERS FORMAT SPACY")
    print("=" * 60)
    
    # Étape 1 : Charger le fichier JSON
    print(f"\n[1/3] Chargement du fichier JSON : {TRAINING_DATA_JSON}")
    try:
        with open(TRAINING_DATA_JSON, 'r', encoding='utf-8') as f:
            json_data = json.load(f)
        print(f"✓ {len(json_data)} CVs chargés avec succès")
    except FileNotFoundError:
        print(f"✗ Erreur : Le fichier {TRAINING_DATA_JSON} n'existe pas")
        return
    except json.JSONDecodeError:
        print(f"✗ Erreur : Le fichier {TRAINING_DATA_JSON} n'est pas un JSON valide")
        return
    
    # Étape 2 : Convertir au format spaCy
    print(f"\n[2/3] Conversion au format spaCy...")
    try:
        training_data = create_training_data_from_json(json_data)
        print(f"✓ Conversion réussie")
    except Exception as e:
        print(f"✗ Erreur lors de la conversion : {e}")
        return
    
    # Étape 3 : Sauvegarder les données converties
    print(f"\n[3/3] Sauvegarde des données converties dans : {OUTPUT_FILE}")
    try:
        with open(OUTPUT_FILE, 'w', encoding='utf-8') as f:
            f.write("# Données d'entraînement au format spaCy\n")
            f.write("# Généré automatiquement par convert_to_spacy.py\n\n")
            f.write("TRAINING_DATA = [\n")
            for i, (text, annotations) in enumerate(training_data):
                f.write(f"    ({repr(text)}, {annotations})")
                if i < len(training_data) - 1:
                    f.write(",\n")
                else:
                    f.write("\n")
            f.write("]\n")
        print(f"✓ Données sauvegardées avec succès")
    except Exception as e:
        print(f"✗ Erreur lors de la sauvegarde : {e}")
        return
    
    print("\n" + "=" * 60)
    print("CONVERSION TERMINÉE AVEC SUCCÈS")
    print("=" * 60)
    print(f"\nFichier de sortie : {OUTPUT_FILE}")
    print(f"Nombre de CVs : {len(training_data)}")
    print("\nVous pouvez maintenant utiliser ce fichier pour entraîner le modèle.")

if __name__ == "__main__":
    main()
