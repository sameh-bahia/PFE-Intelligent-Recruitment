# RÉSUMÉ DE LA SITUATION - PARTIE IA EXTRACTION DE CVs

## OBJECTIF DU PROJET
Créer un service Python FastAPI avec spaCy pour extraire automatiquement les compétences, expériences et formations des CVs (français et anglais), intégré avec une application Spring Boot.

---

## OÙ NOUS EN SOMMES

### Structure du projet
```
D:\PFE\AppRecrutement-CV-Service\
├── models/cv_ner_model/      (modèle spaCy entraîné)
├── data/
│   ├── training_data.json    (dataset d'entraînement)
│   ├── training_data_spacy.py (dataset converti)
│   ├── convert_to_spacy.py   (script de conversion)
│   └── train_model.py        (script d'entraînement)
├── nlp/model.py              (fonctions NLP)
├── main.py                   (application FastAPI)
├── test_model.py             (script de test)
└── ETAPE_IA.md               (guide complet)
```

### Ce qui a été fait
1. ✅ Création de la structure du projet
2. ✅ Création de requirements.txt (fastapi, spacy, uvicorn)
3. ✅ Création de main.py (FastAPI avec endpoint POST /extract)
4. ✅ Création de nlp/model.py (fonctions NLP : load_model, extract_entities, train_model)
5. ✅ Intégration avec Spring Boot (CVExtractionService.java)
6. ✅ Création du dataset initial (5 CVs annotés : 3 FR, 2 EN)
7. ✅ Création du script de conversion (convert_to_spacy.py)
8. ✅ Création du script d'entraînement (train_model.py)
9. ✅ Premier entraînement du modèle (100 itérations, 5 CVs)
10. ✅ Premier test du modèle (résultat : 9% de précision)
11. ✅ Enrichissement du dataset (ajout de 4 CVs → 9 CVs au total)
12. ✅ Re-conversion du dataset
13. ✅ Ré-entraînement du modèle (100 itérations, 9 CVs)
14. ✅ Second test du modèle (résultat : 12.3% de précision)

---

## PROBLÈME ACTUEL

### Résultats des tests

**Test 1 (avec 5 CVs) :**
- Entités attendues : 98
- Entités extraites : 9
- Précision : 9.2%
- Labels reconnus : PERSON uniquement

**Test 2 (avec 9 CVs) :**
- Entités attendues : 179
- Entités extraites : 22
- Précision : 12.3%
- Labels reconnus : PERSON, DATE, ORG (mais PAS COMPETENCE, EXPERIENCE, FORMATION)

### Problème majeur
Le modèle NE reconnaît PAS les labels personnalisés (COMPETENCE, EXPERIENCE, FORMATION). Il ne reconnaît que :
- PERSON (noms)
- DATE (dates)
- ORG (organisations)

Ces labels étaient déjà dans le modèle français de base (`fr_core_news_sm`), donc le modèle n'a pas vraiment appris les nouveaux labels.

### Pourquoi ça ne marche pas ?

**Hypothèse 1 : Dataset trop petit**
- Actuellement : 9 CVs
- Recommandé pour NER personnalisé : 50-100 CVs minimum
- Le modèle n'a pas assez d'exemples pour apprendre les patterns

**Hypothèse 2 : Modèle de base inadapté**
- Nous utilisons `fr_core_news_sm` (modèle français)
- Le dataset contient des CVs en anglais aussi
- Le modèle français ne gère pas bien l'anglais

**Hypothèse 3 : Problème d'alignement des entités**
- Pendant l'entraînement, spaCy a généré des warnings :
  ```
  [W030] Some entities could not be aligned in the text
  ```
- Cela indique que les positions des entités dans le JSON ne correspondent pas exactement au texte tokenisé par spaCy
- Les entités mal alignées sont ignorées pendant l'entraînement

**Hypothèse 4 : Sur-apprentissage**
- Le modèle a appris uniquement à reconnaître ce qui était déjà dans le modèle de base
- Il n'a pas généralisé pour les nouveaux labels

---

## OPTIONS POSSIBLES

### Option A : Enrichir massivement le dataset
**Description :** Ajouter 50-100 CVs annotés au dataset

**Avantages :**
- Plus de données = meilleur apprentissage
- Approche standard pour NER personnalisé
- Peut résoudre le problème de dataset trop petit

**Inconvénients :**
- Très chronophage (annotation manuelle)
- Nécessite beaucoup de temps
- Pas garanti de résoudre le problème si la cause est autre

**Estimation :** 2-3 semaines de travail

---

### Option B : Utiliser un modèle multilingue
**Description :** Remplacer `fr_core_news_sm` par un modèle multilingue comme `xx_ent_wiki_sm`

**Avantages :**
- Supporte mieux l'anglais et le français
- Peut résoudre le problème de langue
- Plus adapté aux CVs multilingues

**Inconvénients :**
- Modèle plus petit (moins précis)
- Peut ne pas avoir les mêmes performances
- Nécessite de ré-entraîner le modèle

**Estimation :** 1-2 jours de travail

---

### Option C : Corriger l'alignement des entités
**Description :** Utiliser `spacy.training.offsets_to_biluo_tags` pour vérifier et corriger l'alignement des entités

**Avantages :**
- Résout le problème des warnings d'alignement
- Améliore la qualité de l'entraînement
- Relativement rapide

**Inconvénients :**
- Peut ne pas résoudre le problème si la cause est le dataset
- Nécessite de comprendre le format BILUO de spaCy

**Estimation :** 1 jour de travail

---

### Option D : Changer d'approche (basée sur des règles)
**Description :** Utiliser une approche basée sur des règles au lieu de l'apprentissage automatique

**Avantages :**
- Plus rapide à implémenter
- Plus prévisible
- Pas besoin de beaucoup de données

**Inconvénients :**
- Moins flexible
- Moins précis sur des CVs variés
- Maintenance difficile

**Estimation :** 3-5 jours de travail

---

### Option E : Utiliser un modèle pré-entraîné pour CVs
**Description :** Utiliser un modèle spécialement entraîné pour les CVs (ex: spaCy models for resumes)

**Avantages :**
- Modèle déjà entraîné sur des CVs
- Meilleure performance out-of-the-box
- Moins de travail

**Inconvénients :**
- Peut ne pas avoir les labels exacts que nous voulons
- Dépendance externe
- Peut nécessiter une adaptation

**Estimation :** 2-3 jours de travail

---

### Option F : Documenter et arrêter
**Description :** Documenter les résultats actuels et arrêter pour discuter avec l'encadrante

**Avantages :**
- Honnête sur les limites
- Permet de discuter avec l'encadrante
- Évite de perdre du temps

**Inconvénients :**
- Pas de solution fonctionnelle
- Peut décevoir

**Estimation :** 1 jour de travail

---

## QUESTIONS POUR GEMINI/GPT

1. **Quelle est la cause la plus probable du problème ?**
   - Dataset trop petit ?
   - Modèle inadapté ?
   - Problème d'alignement ?
   - Autre ?

2. **Quelle option recommandez-vous ?**
   - Pourquoi ?
   - Quels sont les risques ?

3. **Y a-t-il d'autres options que nous n'avons pas considérées ?**

4. **Comment améliorer l'alignement des entités ?**
   - Est-ce que cela peut résoudre le problème ?
   - Comment le faire correctement ?

5. **Comment créer un dataset de qualité pour NER ?**
   - Combien de CVs sont nécessaires ?
   - Comment annoter correctement ?

6. **Est-ce que spaCy est le bon outil pour ce projet ?**
   - Y a-t-il de meilleures alternatives ?
   - (ex: Hugging Face Transformers, flair, etc.)

---

## INFORMATIONS TECHNIQUES

### Environnement
- Python 3.13
- spaCy 3.8.0
- fr_core_news_sm 3.8.0
- FastAPI
- Windows

### Code actuel
- `nlp/model.py` : Contient les fonctions load_model, extract_entities, train_model
- `train_model.py` : Script d'entraînement avec 100 itérations
- `convert_to_spacy.py` : Script de conversion JSON vers spaCy
- `training_data.json` : Dataset avec 9 CVs annotés

### Format des données
```json
{
  "text": "Texte complet du CV...",
  "entities": [
    [0, 12, "PERSON"],
    [15, 29, "EXPERIENCE"],
    [67, 79, "COMPETENCE"],
    [282, 313, "FORMATION"]
  ]
}
```

### Labels personnalisés
- COMPETENCE : Compétences techniques
- EXPERIENCE : Expériences professionnelles
- FORMATION : Formations académiques

---

## CONTEXTE DU PROJET

C'est un projet de fin d'études (PFE) pour un système de recrutement intelligent. L'objectif est d'extraire automatiquement les informations des CVs pour faciliter le matching candidat-poste.

Le système comprend :
- Backend Spring Boot (Java)
- Frontend React
- Service Python FastAPI (IA)
- Base de données

Le service Python doit extraire les compétences, expériences et formations des CVs uploadés par les candidats.

---

## URGENCE

Le projet doit être terminé bientôt. Nous avons besoin d'une solution fonctionnelle rapidement.

---

**Fin du résumé**
