# ============================================================
# FICHIER : test_extraction.py
# DESCRIPTION : Script de test pour valider les corrections d'extraction CV
# LOCALISATION : D:\PFE\AppRecrutement-CV-Service\test_extraction.py
# FONCTION : Teste l'extraction avec un CV tunisien typique
# ============================================================

import sys
import os
sys.path.append(os.path.join(os.path.dirname(__file__), 'nlp'))
from model import load_model, extract_entities

# CV de test typique tunisien
TEST_CV = """
JEAN DUPONT
Développeur Full Stack
Tél: +216 XX XXX XXX | Email: jean.dupont@email.com

EXPÉRIENCES PROFESSIONNELLES

Développeur React Native | ESPRIT | 2023 — 2024
- Développement d'applications mobiles avec React Native et Flutter
- Intégration d'APIs REST et GraphQL
- Utilisation de Git et CI/CD

Stagiaire Développeur Web | Tunisie Telecom | 2022 — 2023
- Développement de sites web avec JavaScript et HTML/CSS
- Maintenance de bases de données PostgreSQL
- Gestion de projet avec Jira et Trello

FORMATION & DIPLÔMES

Cycle d'Ingénieur en Informatique | ESPRIT | 2021 — 2024
Baccalauréat Sciences | Lycée de Tunis | 2020

COMPÉTENCES TECHNIQUES

Langages: Java, Python, JavaScript, C#
Frameworks: Spring Boot, React, Angular, Django
Bases de données: PostgreSQL, MySQL, MongoDB
DevOps: Docker, Kubernetes, Git, CI/CD
Cloud: AWS, Azure, GCP
Outils: Jira, Trello, Confluence, Figma

LANGUES & SOFT SKILLS

Français: Courant
Anglais: Professionnel
Arabe: Natif
"""

def test_extraction():
    """Teste l'extraction avec le CV de test"""
    print("=" * 80)
    print("TEST D'EXTRACTION CV - APPROCHE HYBRIDE")
    print("=" * 80)
    
    # Charger le modèle
    print("\n[1] Chargement du modèle spaCy...")
    nlp = load_model()
    print(f"✅ Modèle chargé avec succès")
    
    # Extraire les entités
    print("\n[2] Extraction des entités du CV...")
    result = extract_entities(nlp, TEST_CV)
    
    # Afficher les résultats
    print("\n" + "=" * 80)
    print("RÉSULTATS DE L'EXTRACTION")
    print("=" * 80)
    
    print(f"\n📊 COMPÉTENCES ({len(result['competences'])})")
    for comp in result['competences']:
        print(f"  - {comp['text']}")
    
    print(f"\n💼 EXPÉRIENCES ({len(result['experiences'])})")
    for exp in result['experiences']:
        print(f"  - {exp['titrePoste']} | {exp['entreprise']} | {exp['dateDebut']} - {exp['dateFin']}")
    
    print(f"\n🎓 FORMATIONS ({len(result['formations'])})")
    for form in result['formations']:
        print(f"  - {form['diplome']} | {form['etablissement']} | {form['anneeObtention']}")
    
    print(f"\n📚 NIVEAU D'ÉTUDE: {result['niveauEtude']}")
    
    # Validation
    print("\n" + "=" * 80)
    print("VALIDATION")
    print("=" * 80)
    
    competences_count = len(result['competences'])
    experiences_count = len(result['experiences'])
    formations_count = len(result['formations'])
    
    if competences_count >= 10:
        print(f"✅ Compétences: {competences_count} (OK)")
    else:
        print(f"❌ Compétences: {competences_count} (Insuffisant)")
    
    if experiences_count >= 1:
        print(f"✅ Expériences: {experiences_count} (OK)")
    else:
        print(f"❌ Expériences: {experiences_count} (Aucune)")
    
    if formations_count >= 1:
        print(f"✅ Formations: {formations_count} (OK)")
    else:
        print(f"❌ Formations: {formations_count} (Aucune)")
    
    if result['niveauEtude'] in ['INGENIEUR', 'MASTER', 'LICENCE']:
        print(f"✅ Niveau d'étude: {result['niveauEtude']} (OK)")
    else:
        print(f"⚠️  Niveau d'étude: {result['niveauEtude']}")
    
    print("\n" + "=" * 80)
    print("TEST TERMINÉ")
    print("=" * 80)

if __name__ == "__main__":
    test_extraction()
