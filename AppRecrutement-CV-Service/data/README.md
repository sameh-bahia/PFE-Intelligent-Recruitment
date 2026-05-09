# Guide d'annotation de CVs pour entraînement spaCy NER

## Objectif
Ce guide explique comment annoter des CVs pour entraîner un modèle spaCy NER capable d'extraire automatiquement :
- **COMPETENCE** : Compétences techniques et professionnelles
- **EXPERIENCE** : Expériences professionnelles
- **FORMATION** : Formations et diplômes

## Format des données d'entraînement

Les données d'entraînement doivent être au format spaCy :

```python
training_data = [
    (
        "Jean Dupont a 5 ans d'expérience en développement Java.",
        {
            "entities": [
                (0, 12, "PERSON"),  # Jean Dupont
                (30, 47, "COMPETENCE")  # développement Java
            ]
        }
    )
]
```

## Labels personnalisés

- **COMPETENCE** : Compétences techniques (Java, Python, Spring Boot, etc.) et compétences professionnelles (gestion de projet, communication, etc.)
- **EXPERIENCE** : Postes, entreprises, durées d'expérience
- **FORMATION** : Diplômes, écoles, universités, certifications

## Étapes pour annoter un CV

### 1. Extraire le texte du CV
Utilisez Apache PDFBox ou un autre outil pour extraire le texte brut du CV PDF.

### 2. Identifier les entités
Parcourez le texte et identifiez toutes les occurrences de :
- Compétences (technologies, outils, méthodologies)
- Expériences (postes, entreprises, dates)
- Formations (diplômes, écoles, dates)

### 3. Noter les positions
Pour chaque entité, notez :
- Le caractère de début (start)
- Le caractère de fin (end)
- Le label (COMPETENCE, EXPERIENCE, FORMATION)

### 4. Créer le format spaCy
Organisez les données au format spaCy comme montré ci-dessus.

## Exemple de CV annoté

### Texte du CV :
```
Jean Dupont
Développeur Full Stack

Expérience :
- Développeur Java chez TechCorp (2020-2023)
- Analyste Programmeur chez DataSoft (2018-2020)

Compétences :
- Java, Spring Boot, Hibernate
- Python, Django
- JavaScript, React

Formation :
- Master Informatique, Université Paris (2018)
- Licence Informatique, Université Lyon (2016)
```

### Format spaCy correspondant :
```python
training_data = [
    (
        "Jean Dupont\nDéveloppeur Full Stack\n\nExpérience :\n- Développeur Java chez TechCorp (2020-2023)\n- Analyste Programmeur chez DataSoft (2018-2020)\n\nCompétences :\n- Java, Spring Boot, Hibernate\n- Python, Django\n- JavaScript, React\n\nFormation :\n- Master Informatique, Université Paris (2018)\n- Licence Informatique, Université Lyon (2016)",
        {
            "entities": [
                (0, 12, "PERSON"),
                (14, 35, "EXPERIENCE"),
                (48, 63, "COMPETENCE"),
                (69, 77, "ORG"),
                (78, 88, "DATE"),
                (92, 112, "EXPERIENCE"),
                (118, 126, "ORG"),
                (127, 137, "DATE"),
                (155, 160, "COMPETENCE"),
                (161, 172, "COMPETENCE"),
                (173, 182, "COMPETENCE"),
                (186, 192, "COMPETENCE"),
                (193, 199, "COMPETENCE"),
                (203, 212, "COMPETENCE"),
                (217, 222, "COMPETENCE"),
                (232, 254, "FORMATION"),
                (255, 273, "ORG"),
                (274, 278, "DATE"),
                (282, 303, "FORMATION"),
                (304, 320, "ORG"),
                (321, 325, "DATE")
            ]
        }
    )
]
```

## Créer un petit dataset d'exemple

### Étape 1 : Collecter 5-10 CVs
Récupérez des CVs variés (différents profils, différents secteurs).

### Étape 2 : Annoter manuellement
Pour chaque CV, créez une entrée dans le format spaCy.

### Étape 3 : Sauvegarder en JSON
Sauvegardez les données annotées dans un fichier JSON :

```json
[
    {
        "text": "Texte complet du CV...",
        "entities": [
            [0, 12, "COMPETENCE"],
            [15, 30, "EXPERIENCE"]
        ]
    }
]
```

### Étape 4 : Convertir au format spaCy
Utilisez la fonction `create_training_data_from_json` de `nlp/model.py` pour convertir.

## Entraîner le modèle

Une fois les données annotées, entraînez le modèle :

```python
from nlp.model import train_model, create_training_data_from_json
import json

# Charger les données annotées
with open("data/training_data.json", "r", encoding="utf-8") as f:
    json_data = json.load(f)

# Convertir au format spaCy
training_data = create_training_data_from_json(json_data)

# Entraîner le modèle
train_model(training_data, n_iter=100)
```

## Conseils

- **Qualité > Quantité** : Mieux vaut 5 CVs bien annotés que 50 mal annotés
- **Cohérence** : Utilisez les mêmes critères pour annoter tous les CVs
- **Variété** : Incluez différents styles de CVs (chronologiques, fonctionnels, etc.)
- **Itératif** : Commencez avec un petit dataset, testez, puis augmentez

## Outils d'annotation

Vous pouvez utiliser des outils comme :
- **spaCy Doctor** : Interface web pour annoter
- **Prodigy** : Outil d'annotation commercial de spaCy
- **Annotation manuelle** : Éditeur de texte avec comptage de caractères
