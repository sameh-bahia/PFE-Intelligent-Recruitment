# ============================================================
# FICHIER : test_model.py
# DESCRIPTION : Script pour tester le modèle spaCy entraîné
# LOCALISATION : D:\PFE\AppRecrutement-CV-Service\test_model.py
# FONCTION : Charge le modèle entraîné et teste l'extraction d'entités
# ============================================================

import sys
import os
import json

# Ajouter le dossier courant au chemin
sys.path.append(os.path.dirname(__file__))
from nlp.model import load_model, extract_entities

# Charger les CVs depuis le dataset d'entraînement
TRAINING_DATA_FILE = "data/training_data.json"

def load_training_cvs():
    """
    Charge les CVs depuis le fichier JSON d'entraînement.
    Retourne une liste de CVs avec leurs annotations.
    """
    with open(TRAINING_DATA_FILE, 'r', encoding='utf-8') as f:
        return json.load(f)

def test_model():
    """
    Fonction principale qui :
    1. Charge le modèle spaCy entraîné
    2. Charge les CVs du dataset
    3. Teste chaque CV
    4. Affiche les entités extraites
    """
    print("=" * 60)
    print("TEST DU MODÈLE SPACY ENTRAÎNÉ AVEC CVs DU DATASET")
    print("=" * 60)
    
    # Étape 1 : Charger le modèle
    print("\n[1/3] Chargement du modèle...")
    try:
        nlp = load_model()
        print("[OK] Modèle chargé avec succès")
    except Exception as e:
        print(f"[ERREUR] Erreur lors du chargement du modèle : {e}")
        return
    
    # Étape 2 : Charger les CVs du dataset
    print("\n[2/3] Chargement des CVs du dataset...")
    try:
        training_cvs = load_training_cvs()
        print(f"[OK] {len(training_cvs)} CVs chargés")
    except Exception as e:
        print(f"[ERREUR] Erreur lors du chargement des CVs : {e}")
        return
    
    # Étape 3 : Tester chaque CV
    print("\n[3/3] Test des CVs...")
    print("=" * 60)
    
    total_entities = 0
    for i, cv_data in enumerate(training_cvs, 1):
        text = cv_data["text"]
        expected_entities = cv_data["entities"]
        
        print(f"\n--- CV {i} ---")
        print(f"Texte (premiers 100 caractères) : {text[:100]}...")
        print(f"Entités attendues : {len(expected_entities)}")
        
        try:
            extracted_entities = extract_entities(nlp, text)
            print(f"Entités extraites : {len(extracted_entities)}")
            
            if len(extracted_entities) > 0:
                print("\nEntités extraites :")
                for ent in extracted_entities:
                    print(f"  - {ent['label']}: {ent['text']}")
            else:
                print("[ATTENTION] Aucune entité extraite !")
            
            total_entities += len(extracted_entities)
        except Exception as e:
            print(f"[ERREUR] Erreur lors de l'extraction : {e}")
    
    # Résumé
    print("\n" + "=" * 60)
    print("RÉSUMÉ DU TEST")
    print("=" * 60)
    print(f"Nombre de CVs testés : {len(training_cvs)}")
    print(f"Total d'entités extraites : {total_entities}")
    print(f"Moyenne par CV : {total_entities / len(training_cvs):.1f}")
    print("\nTest terminé !")
    print("=" * 60)

if __name__ == "__main__":
    test_model()
