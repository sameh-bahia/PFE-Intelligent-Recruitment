# ============================================================
# FICHIER : train_model.py
# DESCRIPTION : Script pour entraîner le modèle spaCy sur le dataset multilingue
# LOCALISATION : D:\PFE\AppRecrutement-CV-Service\data\train_model.py
# FONCTION : Charge les données converties et entraîne le modèle spaCy
# ============================================================

import sys
import os

# Ajouter le dossier parent au chemin pour importer nlp.model
sys.path.append(os.path.join(os.path.dirname(__file__), '..'))
from nlp.model import train_model

# Chemin vers le fichier de données converties
TRAINING_DATA_FILE = "data/training_data_spacy.py"
# Chemin de sortie pour le modèle entraîné (Où sauvegarder le modèle)
OUTPUT_DIR = os.path.join(os.path.dirname(os.path.dirname(__file__)), "models", "cv_ner_model")
# Nombre d'itérations d'entraînement
N_ITER = 100

#Cette fonction charge les données d'entraînement depuis le fichier Python généré
def load_training_data():
    """
    Charge les données d'entraînement depuis le fichier Python généré.
    
    Returns:
        TRAINING_DATA : Liste de tuples (text, annotations) au format spaCy
    """
    print(f"Chargement des données d'entraînement depuis : {TRAINING_DATA_FILE}")
    
    try:
        # Importer dynamiquement le fichier de données (Le fichier training_data_spacy.py est généré automatiquement
        # On ne peut pas l'importer normalement avec import
        # L'import dynamique permet de charger n'importe quel fichier Python)
        import importlib.util
        spec = importlib.util.spec_from_file_location("training_data", TRAINING_DATA_FILE)
        training_module = importlib.util.module_from_spec(spec)
        spec.loader.exec_module(training_module)
        
        # Récupérer la variable TRAINING_DATA
        training_data = training_module.TRAINING_DATA
        print(f"✓ {len(training_data)} CVs chargés")
        return training_data
    except FileNotFoundError:
        print(f"✗ Erreur : Le fichier {TRAINING_DATA_FILE} n'existe pas")
        print("  Exécutez d'abord : python data/convert_to_spacy.py")
        return None
    except Exception as e:
        print(f"✗ Erreur lors du chargement : {e}")
        return None

def main():
    """
    Fonction principale qui :
    1. Charge les données d'entraînement
    2. Entraîne le modèle spaCy
    3. Sauvegarde le modèle entraîné
    """
    print("=" * 60)
    print("ENTRAÎNEMENT DU MODÈLE SPACY POUR EXTRACTION DE CV")
    print("=" * 60)
    
    # Étape 1 : Charger les données d'entraînement
    print(f"\n[1/2] Chargement des données d'entraînement...")
    training_data = load_training_data()
    if training_data is None:
        return
    
    # Étape 2 : Entraîner le modèle
    print(f"\n[2/2] Entraînement du modèle spaCy...")
    print(f"  - Itérations : {N_ITER}")
    print(f"  - Répertoire de sortie : {OUTPUT_DIR}")
    print(f"  - Labels : COMPETENCE, EXPERIENCE, FORMATION")
    print("\nDébut de l'entraînement...\n")
    
    try:
        train_model(
            training_data=training_data, #Appelle la fonction train_model()
            output_dir=OUTPUT_DIR, # le répertoire de sortie
            n_iter=N_ITER # le nombre d'itérations
        )
        print("\n" + "=" * 60)
        print("ENTRAÎNEMENT TERMINÉ AVEC SUCCÈS")
        print("=" * 60)
        print(f"\nModèle sauvegardé dans : {OUTPUT_DIR}")
        print("\nLe modèle sera automatiquement chargé par le service FastAPI.")
    except Exception as e:
        print(f"\n✗ Erreur lors de l'entraînement : {e}")
        print("\nVérifiez que spaCy est installé :")
        print("  pip install spacy")
        print("  python -m spacy download fr_core_news_sm")

if __name__ == "__main__":
    main()
