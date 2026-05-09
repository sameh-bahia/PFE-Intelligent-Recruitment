# GUIDE COMPLET - PARTIE IA : EXTRACTION DE CVs

## OBJECTIF
Créer un service Python FastAPI avec spaCy pour extraire automatiquement les compétences, expériences et formations des CVs (français et anglais).

---

## ÉTAPE 1 : STRUCTURE DU PROJET
**Date :** Première étape
**Action :** Création des dossiers

**Structure créée :**
```
D:\PFE\AppRecrutement-CV-Service\
├── models/          (pour stocker le modèle spaCy entraîné)
├── data/            (pour le dataset d'entraînement)
└── nlp/             (pour les fonctions NLP)
```

**Pourquoi :** Organisation du projet pour séparer les données, le code et les modèles.

---

## ÉTAPE 2 : FICHIER requirements.txt
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\requirements.txt`
**Action :** Création du fichier des dépendances Python

**Contenu :**
```
fastapi        # Framework web pour créer l'API REST
uvicorn        # Serveur ASGI pour exécuter FastAPI
spacy          # Bibliothèque NLP pour l'extraction d'entités
requests       # Bibliothèque HTTP pour tester les endpoints
```

**Pourquoi :** Liste les dépendances nécessaires pour le projet.

**Installation :**
```bash
pip install -r requirements.txt
python -m spacy download fr_core_news_sm
```

---

## ÉTAPE 3 : FICHIER main.py
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\main.py`
**Action :** Création de l'application FastAPI

**Contenu :**
- Importations (FastAPI, Pydantic, spaCy)
- Modèles de données (CVRequest, CVResponse)
- Variable globale pour le modèle spaCy
- Fonction startup_event() pour charger le modèle au démarrage
- Endpoint POST /extract pour recevoir le texte et retourner les entités
- Endpoints de test (/ et /health)

**Pourquoi :** Crée le serveur HTTP qui recevra les CVs depuis Spring Boot.

**Démarrage :**
```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

---

## ÉTAPE 4 : FICHIER nlp/model.py
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\nlp\model.py`
**Action :** Création des fonctions NLP

**Fonctions créées :**

### 1. load_model()
**Rôle :** Charge le modèle spaCy
**Stratégie :**
- Si le modèle entraîné existe dans `models/cv_ner_model/`, on le charge
- Sinon, on charge le modèle français de base (`fr_core_news_sm`)
- Si le modèle français n'est pas installé, on le télécharge automatiquement

### 2. extract_entities(nlp, text)
**Rôle :** Extrait les entités du texte
**Processus :**
- Le texte est passé au modèle spaCy
- Le modèle NER identifie les entités
- Chaque entité est extraite avec son texte, label et positions
- Retourne la liste des entités

### 3. train_model(training_data, output_dir, n_iter)
**Rôle :** Entraîne un modèle spaCy personnalisé
**Processus :**
- Charge le modèle français de base
- Ajoute le composant NER
- Ajoute les labels personnalisés (COMPETENCE, EXPERIENCE, FORMATION)
- Désactive les autres composants
- Entraîne sur N itérations (défaut: 100)
- Sauvegarde le modèle entraîné

### 4. create_training_data_from_json(json_data)
**Rôle :** Convertit le JSON au format spaCy
**Format entrée :** JSON avec `text` et `entities`
**Format sortie :** Liste de tuples (text, annotations)

**Pourquoi :** Contient toutes les fonctions pour charger, entraîner et utiliser le modèle.

---

## ÉTAPE 5 : GUIDE D'ANNOTATION
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\data\README.md`
**Action :** Création du guide pour annoter les CVs

**Contenu :**
- Format des données spaCy
- Labels à utiliser (COMPETENCE, EXPERIENCE, FORMATION)
- Étapes pour annoter
- Exemple d'annotation
- Conseils

**Pourquoi :** Guide pour savoir comment annoter les CVs pour l'entraînement.

---

## ÉTAPE 6 : INTÉGRATION SPRING BOOT
**Fichiers modifiés :**
- `CVExtractionService.java` : Service pour extraire le texte et appeler le service Python
- `CVController.java` : Modifié pour utiliser le service d'extraction

**Actions :**
1. Création de `CVExtractionService.java` :
   - Extrait le texte des PDF/DOCX avec Apache PDFBox et Apache POI
   - Appelle le service Python FastAPI via HTTP POST
   - Retourne les entités extraites

2. Modification de `CVController.java` :
   - Injection de `CVExtractionService`
   - Dans `uploadCV()` : extrait le texte, appelle le service Python, log les entités

**Pourquoi :** Intègre le service Python IA dans l'application Spring Boot.

---

## ÉTAPE 7 : CRÉATION DU DATASET (Solution 3 - Modèle multilingue)
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\data\training_data.json`
**Action :** Création du dataset d'entraînement

**Contenu :** 5 CVs annotés (3 en français, 2 en anglais)

**Format :**
```json
{
  "text": "Texte complet du CV...",
  "entities": [
    [0, 12, "PERSON"],        // Nom
    [15, 29, "EXPERIENCE"],   // Poste
    [67, 79, "ORG"],          // Entreprise
    [80, 90, "DATE"],         // Date
    [154, 172, "COMPETENCE"], // Compétence
    [282, 313, "FORMATION"]   // Formation
  ]
}
```

**Comment annoter :**
1. Écrire le texte complet du CV
2. Identifier les entités (PERSON, EXPERIENCE, COMPETENCE, FORMATION, ORG, DATE)
3. Noter les positions (début et fin) de chaque entité
4. Créer le fichier JSON avec le format ci-dessus

**Pourquoi :** Dataset pour entraîner le modèle à reconnaître les entités en français et anglais.

---

## ÉTAPE 8 : SCRIPT DE CONVERSION
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\data\convert_to_spacy.py`
**Action :** Création du script de conversion JSON vers spaCy

**Contenu :**
- Importations (json, sys, os)
- Chemins (training_data.json, training_data_spacy.py)
- Fonction main() qui :
  1. Charge le fichier JSON
  2. Convertit au format spaCy avec `create_training_data_from_json()`
  3. Sauvegarde dans training_data_spacy.py

**Exécution :**
```bash
python data/convert_to_spacy.py
```

**Pourquoi :** Convertit le format JSON lisible pour les humains au format spaCy pour l'entraînement.

---

## ÉTAPE 9 : SCRIPT D'ENTRAÎNEMENT
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\data\train_model.py`
**Action :** Création du script d'entraînement

**Contenu :**
- Importations (sys, os, train_model)
- Chemins (training_data_spacy.py, models/cv_ner_model)
- Paramètres (N_ITER = 100)
- Fonction load_training_data() :
  - Import dynamique du fichier training_data_spacy.py
  - Récupère la variable TRAINING_DATA
- Fonction main() :
  1. Charge les données d'entraînement
  2. Appelle train_model() avec les données
  3. Affiche le résultat

**Exécution :**
```bash
python data/train_model.py
```

**Pourquoi :** Automatise le processus d'entraînement du modèle.

---

## ÉTAPE 10 : EXÉCUTION DE L'ENTRAÎNEMENT
**Actions :**
1. Installation de spaCy et du modèle français :
   ```bash
   python -m spacy download fr_core_news_sm
   ```

2. Conversion du JSON au format spaCy :
   ```bash
   python data/convert_to_spacy.py
   ```
   **Résultat :** Création de `training_data_spacy.py` avec 5 CVs convertis

3. Entraînement du modèle :
   ```bash
   python data/train_model.py
   ```
   **Résultat :** 
   - 100 itérations d'entraînement
   - Pertes diminuées de 193.5 à 0.000003
   - Modèle sauvegardé dans `models/cv_ner_model/`

---

## ÉTAPE 11 : MODÈLE ENTRAÎNÉ
**Dossier :** `D:\PFE\AppRecrutement-CV-Service\models\cv_ner_model\`

**Contenu :**
- `config.cfg` - Configuration du modèle
- `meta.json` - Métadonnées
- `tokenizer` - Tokeniseur
- `ner/` - **Modèle NER entraîné** (reconnaît COMPETENCE, EXPERIENCE, FORMATION)
- `parser/` - Analyseur syntaxique
- `lemmatizer/` - Lemmatiseur
- `morphologizer/` - Analyseur morphologique
- `tok2vec/` - Conversion mots vers vecteurs
- `vocab/` - Vocabulaire

**Pourquoi :** Le modèle entraîné est maintenant prêt à être utilisé par le service FastAPI.

---

## ÉTAPE 12 : UTILISATION DU MODÈLE
**Fichier :** `D:\PFE\AppRecrutement-CV-Service\main.py`

**Chargement automatique :**
- Dans `load_model()` (nlp/model.py), la fonction vérifie si `models/cv_ner_model/` existe
- Si oui, elle charge le modèle entraîné
- Sinon, elle charge le modèle français de base

**Démarrage du service :**
```bash
cd D:\PFE\AppRecrutement-CV-Service
uvicorn main:app --host 0.0.0.0 --port 8000
```

**Test du service :**
```bash
curl -X POST http://localhost:8000/extract -H "Content-Type: application/json" -d '{"text": "Jean Dupont est développeur Java"}'
```

---

## RÉSUMÉ DU PROCESSUS

### Flux de données :
1. **Candidat** upload un CV (PDF/DOCX) via Spring Boot
2. **Spring Boot** extrait le texte avec PDFBox/Apache POI
3. **Spring Boot** envoie le texte au service Python FastAPI
4. **FastAPI** charge le modèle spaCy entraîné
5. **spaCy** extrait les entités (COMPETENCE, EXPERIENCE, FORMATION)
6. **FastAPI** retourne les entités en JSON
7. **Spring Boot** reçoit les entités et les sauvegarde

### Pourquoi le modèle est multilingue ?
- Le dataset contient des CVs en français ET en anglais
- Le modèle français de base est utilisé comme point de départ
- Le modèle apprend à partir des CVs anglais aussi
- Le modèle NER entraîné reconnaît les mots techniques dans les deux langues

### Points clés à retenir :
1. Le modèle est sauvegardé dans `models/cv_ner_model/`
2. Le service FastAPI charge automatiquement ce modèle
3. Le dataset doit être enrichi avec plus de CVs pour améliorer la précision
4. Le modèle peut être ré-entraîné avec de nouvelles données

---

## COMMANDES UTILES

### Installer les dépendances :
```bash
cd D:\PFE\AppRecrutement-CV-Service
pip install -r requirements.txt
python -m spacy download fr_core_news_sm
```

### Entraîner le modèle :
```bash
python data/convert_to_spacy.py
python data/train_model.py
```

### Démarrer le service :
```bash
uvicorn main:app --host 0.0.0.0 --port 8000
```

### Tester le service :
```bash
curl -X POST http://localhost:8000/extract -H "Content-Type: application/json" -d '{"text": "Votre texte ici"}'
```

### Documentation API :
Ouvrir le navigateur sur : http://localhost:8000/docs

---

## PROCHAINES ÉTAPES (FUTUR)

1. **Enrichir le dataset** : Ajouter plus de CVs annotés (50-100 minimum)
2. **Améliorer la précision** : Entraîner avec plus d'itérations (200-500)
3. **Tester le modèle** : Créer des tests unitaires
4. **Déployer** : Déployer le service sur un serveur
5. **Monitoring** : Ajouter des logs et métriques

---

**Fin du guide - Partie IA**
