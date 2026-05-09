# ============================================================
# FICHIER : nlp/model.py
# DESCRIPTION : Module NLP pour la gestion du modèle spaCy et l'extraction d'entités
# LOCALISATION : D:\PFE\AppRecrutement-CV-Service\nlp\model.py
# FONCTION : Charge, entraîne et utilise le modèle spaCy pour extraire les entités des CVs
# ============================================================

# Importations des bibliothèques spaCy
import spacy  # Bibliothèque NLP principale
from spacy.training import Example  # Classe pour les exemples d'entraînement spaCy
import os  # os pour les chemins de fichiers
from typing import List, Dict  # Types pour les listes et dictionnaires
import re  # regex pour le parsing avancé
from datetime import datetime  # pour la normalisation des dates

# ============================================================
# CONFIGURATION DU CHEMIN DU MODÈLE
# MODEL_PATH : Chemin où le modèle entraîné sera sauvegardé/chargé
# Structure : D:\PFE\AppRecrutement-CV-Service\models\cv_ner_model
# ============================================================
MODEL_PATH = os.path.join(os.path.dirname(os.path.dirname(__file__)), "models", "cv_ner_model")

def load_model():
    """
    Charge le modèle spaCy pour l'extraction d'entités depuis les CVs.
    
    STRATÉGIE DE CHARGEMENT :
    1. Si un modèle entraîné existe dans models/cv_ner_model, on le charge
    2. Sinon, on charge le modèle multilingue de base (xx_ent_wiki_sm)
    3. On ajoute toujours les patterns EntityRuler pour les compétences
    4. Si le modèle multilingue n'est pas installé, on le télécharge automatiquement
    
    Returns:
        nlp: Objet spaCy chargé avec le modèle NER et les patterns
    """
    # Vérifier si un modèle entraîné existe déjà
    if os.path.exists(MODEL_PATH):
        print(f"Chargement du modèle entraîné depuis {MODEL_PATH}")
        nlp = spacy.load(MODEL_PATH)  # Charger le modèle personnalisé entraîné
    else:
        print("Modèle entraîné non trouvé, chargement du modèle multilingue de base")
        try:
            # Essayer de charger le modèle multilingue pré-entraîné
            nlp = spacy.load("xx_ent_wiki_sm")
        except OSError:
            # Si le modèle n'est pas installé, le télécharger automatiquement
            print("Modèle multilingue non installé, installation en cours...")
            import subprocess
            subprocess.run(["python", "-m", "spacy", "download", "xx_ent_wiki_sm"])
            nlp = spacy.load("xx_ent_wiki_sm")
        
        # ============================================================
        # AJOUT DES PATTERNS ENTITYRULER POUR LES COMPÉTENCES
        # Même avec le modèle de base, on ajoute les patterns pour extraire les compétences
        # ============================================================
        print("Ajout des patterns EntityRuler pour les compétences...")
        
        competence_patterns = [
            {"label": "COMPETENCE", "pattern": [{"LOWER": "java"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "python"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "spring"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "spring boot"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "hibernate"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "django"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "flask"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "react"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "angular"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "javascript"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "sql"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "postgresql"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "mysql"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "mongodb"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "docker"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "kubernetes"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "k8s"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "git"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "ci/cd"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "devops"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "c#"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": ".net"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "asp.net"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "azure"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "aws"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "gcp"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "microservices"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "rest"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "graphql"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "agile"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "scrum"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "kanban"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "jira"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "trello"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "confluence"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "machine learning"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "deep learning"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "tensorflow"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "pytorch"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "keras"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "pandas"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "numpy"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "tableau"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "power bi"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "excel"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "figma"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "sketch"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "adobe xd"}]},
            # Compétences Logistique et Supply Chain
            {"label": "COMPETENCE", "pattern": [{"LOWER": "gestion de stock"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "supply chain"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "négociation fournisseurs"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "optimisation des flux"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "erp"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "sap"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "management d'équipe"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "transport international"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "logistique"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "approvisionnement"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "planification"}]},
            {"label": "COMPETENCE", "pattern": [{"LOWER": "audit"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Gestion"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Supply"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Chain"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Négociation"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Fournisseurs"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Optimisation"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "ERP"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "SAP"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Management"}]},
            {"label": "COMPETENCE", "pattern": [{"TEXT": "Transport"}]},
        ]
        
        # Ajouter l'EntityRuler au pipeline AVANT le NER
        if "entity_ruler" not in nlp.pipe_names:
            ruler = nlp.add_pipe("entity_ruler", before="ner")
            ruler.add_patterns(competence_patterns)
            print(f"[OK] EntityRuler ajouté avec {len(competence_patterns)} patterns de compétences")
        else:
            print("[INFO] EntityRuler existe déjà")
    
    return nlp

def normalize_date_range(date_str: str) -> Dict[str, str]:
    """
    Normalise une plage de dates (ex: "2024-2025") en date de début valide.
    
    Args:
        date_str: Chaîne de caractères représentant une plage de dates
        
    Returns:
        Dictionnaire avec 'dateDebut' au format ISO (YYYY-MM-DD) et 'dateFin' à None
    """
    if not date_str:
        return {"dateDebut": None, "dateFin": None}
    
    # Pattern pour détecter les années (ex: "2024-2025", "2024 - 2025", "2024-2025 (In progress)")
    year_pattern = r'(\d{4})\s*-\s*(\d{4})'
    match = re.search(year_pattern, date_str)
    
    if match:
        start_year = int(match.group(1))
        # Envoyer uniquement l'année de début au format YYYY-MM-DD
        date_debut = f"{start_year}-01-01"
        
        return {"dateDebut": date_debut, "dateFin": None}
    
    # Pattern pour une seule année
    year_single_pattern = r'(\d{4})'
    match_single = re.search(year_single_pattern, date_str)
    
    if match_single:
        year = int(match_single.group(1))
        return {"dateDebut": f"{year}-01-01", "dateFin": None}
    
    return {"dateDebut": None, "dateFin": None}

def extract_cv_sections(text: str) -> Dict[str, str]:
    """
    Extrait les sections du CV (Expériences, Formations) en utilisant des regex.
    
    Args:
        text: Texte complet du CV
        
    Returns:
        Dictionnaire avec les sections 'experiences' et 'formations'
    """
    sections = {
        "experiences": "",
        "formations": ""
    }
    
    print(f"[DEBUG] ===== EXTRACT CV SECTIONS =====")
    print(f"[DEBUG] Longueur du texte complet: {len(text)}")
    
    # Trouver la position de "PROFESSIONAL EXPERIENCE" et "EDUCATION"
    experience_match = re.search(r'PROFESSIONAL EXPERIENCE', text, re.IGNORECASE)
    education_match = re.search(r'EDUCATION', text, re.IGNORECASE)
    
    # Afficher le contexte autour de PROFESSIONAL EXPERIENCE
    if experience_match:
        start_context = max(0, experience_match.start() - 100)
        end_context = min(len(text), experience_match.end() + 200)
        print(f"[DEBUG] Contexte autour de PROFESSIONAL EXPERIENCE: '{text[start_context:end_context]}'")
    
    print(f"[DEBUG] PROFESSIONAL EXPERIENCE trouvé: {experience_match is not None}")
    print(f"[DEBUG] EDUCATION trouvé: {education_match is not None}")
    
    if experience_match and education_match:
        # Extraire le texte entre PROFESSIONAL EXPERIENCE et EDUCATION
        start_exp = experience_match.end()
        end_exp = education_match.start()
        print(f"[DEBUG] Position PROFESSIONAL EXPERIENCE end: {start_exp}")
        print(f"[DEBUG] Position EDUCATION start: {end_exp}")
        print(f"[DEBUG] Texte entre positions: '{text[start_exp:end_exp]}'")
        sections["experiences"] = text[start_exp:end_exp].strip()
        
        # Extraire le texte après EDUCATION jusqu'à la fin
        start_edu = education_match.end()
        print(f"[DEBUG] Position EDUCATION end: {start_edu}")
        sections["formations"] = text[start_edu:].strip()
        
        print(f"[DEBUG] Section expériences trouvée (PROFESSIONAL EXPERIENCE -> EDUCATION)")
        print(f"[DEBUG] Section formations trouvée (EDUCATION -> fin)")
        
        # Exclure la section LANGUAGES de la section formations
        if "LANGUAGES" in sections["formations"]:
            sections["formations"] = sections["formations"].split("LANGUAGES")[0].strip()
            print(f"[DEBUG] Section LANGUAGES exclue des formations")
    elif experience_match:
        # Seulement PROFESSIONAL EXPERIENCE trouvé
        start_exp = experience_match.end()
        sections["experiences"] = text[start_exp:].strip()
        print(f"[DEBUG] Section expériences trouvée (PROFESSIONAL EXPERIENCE -> fin)")
    elif education_match:
        # Seulement EDUCATION trouvé
        start_edu = education_match.end()
        sections["formations"] = text[start_edu:].strip()
        print(f"[DEBUG] Section formations trouvée (EDUCATION -> fin)")
        
        # Exclure la section LANGUAGES de la section formations
        if "LANGUAGES" in sections["formations"]:
            sections["formations"] = sections["formations"].split("LANGUAGES")[0].strip()
            print(f"[DEBUG] Section LANGUAGES exclue des formations")
    
    # Exclure la section LANGUAGES de la section formations (cas général)
    if "formations" in sections and "LANGUAGES" in sections["formations"]:
        sections["formations"] = sections["formations"].split("LANGUAGES")[0].strip()
        print(f"[DEBUG] Section LANGUAGES exclue des formations (cas général)")
    
    print(f"[DEBUG] Longueur section expériences: {len(sections['experiences'])}")
    print(f"[DEBUG] Longueur section formations: {len(sections['formations'])}")
    print(f"[DEBUG] ===== FIN EXTRACT CV SECTIONS =====")
    
    return sections

def parse_experiences(experience_text: str) -> List[Dict]:
    """
    Parse la section expériences pour extraire les expériences complètes.
    
    Args:
        experience_text: Texte de la section expériences
        
    Returns:
        Liste d'expériences avec titrePoste, entreprise, dateDebut, dateFin, description
    """
    print(f"[DEBUG] ===== PARSE EXPERIENCES =====")
    print(f"[DEBUG] Longueur texte expériences: {len(experience_text)}")
    
    if not experience_text:
        print(f"[DEBUG] Texte expériences vide")
        return []
    
    experiences = []
    
    print(f"[DEBUG] Texte expériences à parser: {experience_text[:200]}...")
    
    # Diviser le texte en blocs (séparés par des lignes vides ou des points •)
    blocks = re.split(r'\n\s*\n|\n•\s*', experience_text)
    
    # Si un seul bloc et qu'il contient des bullet points, utiliser une logique plus robuste
    if len(blocks) == 1 and '-' in experience_text:
        lines = experience_text.split('\n')
        current_block = []
        blocks = []
        in_description = False
        
        for line in lines:
            line = line.strip()
            if not line:
                continue
            # Si la ligne commence par un tiret, c'est une description
            if line.startswith('-'):
                in_description = True
                current_block.append(line)
            # Si la ligne ne commence pas par un tiret et qu'on était dans une description
            elif in_description and current_block:
                # Sauvegarder le bloc actuel et commencer un nouveau
                if len(current_block) >= 2:
                    blocks.append('\n'.join(current_block))
                current_block = [line]
                in_description = False
            else:
                current_block.append(line)
        
        # Ajouter le dernier bloc
        if current_block and len(current_block) >= 2:
            blocks.append('\n'.join(current_block))
    
    print(f"[DEBUG] Nombre de blocs expériences: {len(blocks)}")
    
    for i, block in enumerate(blocks):
        block = block.strip()
        print(f"[DEBUG] Bloc {i}: {block[:100]}...")
        
        if not block or len(block) < 10:
            print(f"[DEBUG] Bloc {i} ignoré (vide ou trop court)")
            continue
        
        # Extraire l'expérience
        experience = {
            "titrePoste": "",
            "entreprise": "",
            "dateDebut": None,
            "dateFin": None,
            "description": ""
        }
        
        # Extraire le titre du poste (première ligne)
        lines = block.split('\n')
        if lines:
            # Première ligne: titre du poste
            experience["titrePoste"] = lines[0].strip()
            print(f"[DEBUG] Expérience - Titre: '{experience['titrePoste']}'")
            
            # Deuxième ligne: entreprise et dates
            if len(lines) > 1:
                second_line = lines[1].strip()
                print(f"[DEBUG] Expérience - Deuxième ligne: '{second_line}'")
                # Extraire l'entreprise (avant le "|")
                if '|' in second_line:
                    parts = second_line.split('|')
                    experience["entreprise"] = parts[0].strip()
                    print(f"[DEBUG] Expérience - Entreprise: '{experience['entreprise']}'")
                    # Extraire les dates (après le "|")
                    date_match = re.search(r'(\d{4})\s*-\s*(\d{4}|Présent)', parts[1])
                    if date_match:
                        start_year = date_match.group(1)
                        end_year = date_match.group(2)
                        experience["dateDebut"] = f"{start_year}-01-01"
                        if end_year == "Présent":
                            experience["dateFin"] = None
                        else:
                            experience["dateFin"] = f"{end_year}-01-01"
        
        # Extraire les dates
        date_pattern = r'(\d{4}\s*-\s*\d{4})'
        date_match = re.search(date_pattern, block)
        if date_match:
            date_str = date_match.group(0)
            normalized = normalize_date_range(date_str)
            experience["dateDebut"] = normalized["dateDebut"]
            experience["dateFin"] = normalized["dateFin"]
        
        # Description (reste du texte après la première ligne)
        if len(lines) > 1:
            experience["description"] = '\n'.join(lines[1:]).strip()
        
        print(f"[DEBUG] Expérience extraite: titre={experience['titrePoste']}, entreprise={experience['entreprise']}")
        
        # Filtrer les expériences invalides (trop courtes ou sans titre)
        if experience["titrePoste"] and len(experience["titrePoste"]) > 3:
            experiences.append(experience)
    
    return experiences

def parse_formations(formation_text: str) -> List[Dict]:
    """
    Parse la section formations pour extraire les formations complètes.
    
    Args:
        formation_text: Texte de la section formations
        
    Returns:
        Liste de formations avec diplome, etablissement, specialite, anneeObtention
    """
    if not formation_text:
        return []
    
    formations = []
    
    print(f"[DEBUG] Texte formations à parser: {formation_text[:200]}...")
    
    # Diviser le texte en blocs (séparés par des lignes vides ou des points •)
    blocks = re.split(r'\n\s*\n|\n•\s*', formation_text)
    
    # Si un seul bloc, utiliser une logique plus robuste basée sur les mots-clés d'établissement
    if len(blocks) == 1:
        lines = formation_text.split('\n')
        current_block = []
        blocks = []
        in_establishment = False
        
        for line in lines:
            line = line.strip()
            if not line:
                continue
            # Si la ligne contient des mots-clés d'établissement, c'est l'établissement
            if any(keyword in line for keyword in ['Institut', 'École', 'Ecole', 'Université', 'ISG', 'IHEC', 'ENIT', 'ENSI']):
                in_establishment = True
                current_block.append(line)
            # Si la ligne ne contient pas ces mots-clés et qu'on était dans un établissement
            elif in_establishment and current_block:
                # Sauvegarder le bloc actuel et commencer un nouveau
                if len(current_block) >= 2:
                    blocks.append('\n'.join(current_block))
                current_block = [line]
                in_establishment = False
            else:
                current_block.append(line)
        
        # Ajouter le dernier bloc
        if current_block and len(current_block) >= 2:
            blocks.append('\n'.join(current_block))
    
    print(f"[DEBUG] Nombre de blocs formations: {len(blocks)}")
    
    for i, block in enumerate(blocks):
        block = block.strip()
        print(f"[DEBUG] Bloc {i}: {block[:100]}...")
        
        # Ignorer les blocs qui commencent par LANGUAGES ou d'autres sections non pertinentes
        if "LANGUAGES" in block[:20]:
            print(f"[DEBUG] Bloc {i} ignoré (section LANGUAGES)")
            continue
        
        if not block or len(block) < 10:
            continue
        
        formation = {
            "diplome": "",
            "etablissement": "",
            "specialite": "",
            "anneeObtention": None  # Sera au format YYYY-MM-DD
        }
        
        lines = block.split('\n')
        
        # Première ligne: diplôme
        if lines:
            first_line = lines[0].strip()
            print(f"[DEBUG] Formation - Première ligne: '{first_line}'")
            # Toute la première ligne est le diplôme
            formation["diplome"] = first_line
            print(f"[DEBUG] Formation - Diplôme: '{formation['diplome']}'")
        
        # Deuxième ligne: établissement et année
        if len(lines) > 1:
            second_line = lines[1].strip()
            print(f"[DEBUG] Formation - Deuxième ligne: '{second_line}'")
            # Extraire l'établissement (avant le "|")
            if '|' in second_line:
                parts = second_line.split('|')
                formation["etablissement"] = parts[0].strip()
                # L'année est après le "|"
                year_match = re.search(r'(\d{4})', parts[1])
                if year_match:
                    year = int(year_match.group(1))
                    formation["anneeObtention"] = f"{year}-01-01"  # Format YYYY-MM-DD
            else:
                # Pas de "|", toute la ligne est l'établissement
                formation["etablissement"] = second_line
                # Chercher l'année dans la ligne
                year_match = re.search(r'(\d{4})', second_line)
                if year_match:
                    year = int(year_match.group(1))
                    formation["anneeObtention"] = f"{year}-01-01"  # Format YYYY-MM-DD
            print(f"[DEBUG] Formation - Établissement: '{formation['etablissement']}'")
        
        # Extraire l'année si pas encore trouvée
        if formation["anneeObtention"] is None:
            year_pattern = r'(\d{4})'
            year_match = re.search(year_pattern, block)
            if year_match:
                year = int(year_match.group(1))
                formation["anneeObtention"] = f"{year}-01-01"  # Format YYYY-MM-DD
        
        print(f"[DEBUG] Formation extraite: diplome={formation['diplome']}, etablissement={formation['etablissement']}, annee={formation['anneeObtention']}")
        
        # Filtrer les formations invalides
        if formation["diplome"] and len(formation["diplome"]) > 3:
            formations.append(formation)
    
    return formations

def remove_duplicates(items: List[Dict], key_field: str) -> List[Dict]:
    """
    Supprime les doublons d'une liste basée sur un champ clé.
    
    Args:
        items: Liste de dictionnaires
        key_field: Champ utilisé pour identifier les doublons
        
    Returns:
        Liste sans doublons
    """
    seen = set()
    unique_items = []
    
    for item in items:
        key = item.get(key_field, "").lower().strip()
        if key and key not in seen:
            seen.add(key)
            unique_items.append(item)
    
    return unique_items

def extract_entities(nlp, text: str) -> Dict[str, List[Dict]]:
    """
    Extrait les entités (COMPETENCE, EXPERIENCE, FORMATION) du texte du CV.
    
    PROCESSUS D'EXTRACTION AMÉLIORÉ :
    1. Extrait les sections du CV (Expériences, Formations) en utilisant des regex
    2. Parse ces sections pour extraire les expériences et formations complètes
    3. Utilise le modèle spaCy pour extraire les compétences (mots-clés techniques)
    4. Nettoie les doublons
    5. Normalise les dates
    
    Args:
        nlp: Modèle spaCy chargé (retourné par load_model())
        text: Texte brut du CV extrait du PDF/DOCX
        
    Returns:
        Dictionnaire contenant :
        - competences: Liste des compétences (mots-clés techniques)
        - experiences: Liste des expériences complètes avec détails
        - formations: Liste des formations complètes avec détails
    """
    # ============================================================
    # EXTRACTION DES SECTIONS DU CV
    # ============================================================
    print("[DEBUG] Début de l'extraction des sections du CV")
    sections = extract_cv_sections(text)
    print(f"[DEBUG] Section expériences: {sections['experiences'][:200]}...")
    print(f"[DEBUG] Section formations: {sections['formations'][:200]}...")
    
    # ============================================================
    # PARSING DES EXPÉRIENCES ET FORMATIONS
    # ============================================================
    experiences = parse_experiences(sections["experiences"])
    formations = parse_formations(sections["formations"])
    
    print(f"[DEBUG] Expériences extraites: {len(experiences)}")
    print(f"[DEBUG] Formations extraites: {len(formations)}")
    
    # ============================================================
    # NETTOYAGE DES DOUBLONS
    # ============================================================
    experiences = remove_duplicates(experiences, "titrePoste")
    formations = remove_duplicates(formations, "diplome")
    
    print(f"[DEBUG] Expériences après dédoublonnage: {len(experiences)}")
    print(f"[DEBUG] Formations après dédoublonnage: {len(formations)}")
    
    # ============================================================
    # EXTRACTION DES COMPÉTENCES AVEC SPACY
    # ============================================================
    doc = nlp(text)
    competences = []
    
    # Parcourir toutes les entités détectées par le modèle
    for ent in doc.ents:
        if ent.label_ == 'COMPETENCE':
            competences.append({
                "text": ent.text,
                "label": ent.label_,
                "start": ent.start_char,
                "end": ent.end_char
            })
    
    # Nettoyer les doublons de compétences
    competences = remove_duplicates(competences, "text")
    
    print(f"[DEBUG] Compétences extraites: {len(competences)}")
    
    # ============================================================
    # LOG DE CONTRÔLE : Afficher ce que Python envoie au Java
    # ============================================================
    print("[DEBUG] === ENVOI AU JAVA ===")
    print(f"[DEBUG] Compétences: {[c['text'] for c in competences]}")
    print(f"[DEBUG] Expériences: {experiences}")
    print(f"[DEBUG] Formations: {formations}")
    print("[DEBUG] ===================")
    
    return {
        "competences": competences,
        "experiences": experiences,
        "formations": formations
    }

def train_model(training_data: List[Dict], output_dir: str = None, n_iter: int = 100):
    """
    Entraîne un modèle spaCy NER personnalisé pour l'extraction d'entités depuis les CVs.
    
    PROCESSUS D'ENTRAÎNEMENT :
    1. Charge le modèle français de base comme point de départ
    2. Ajoute le composant NER au pipeline spaCy
    3. Ajoute les labels personnalisés (COMPETENCE, EXPERIENCE, FORMATION)
    4. Désactive les autres composants du pipeline pour n'entraîner que le NER
    5. Entraîne le modèle sur les données annotées
    6. Sauvegarde le modèle entraîné sur disque
    
    Args:
        training_data: Liste de données d'entraînement au format spaCy
                       Format : [(texte, {"entities": [(start, end, label), ...]}), ...]
        output_dir: Répertoire de sortie pour le modèle entraîné (défaut: models/cv_ner_model)
        n_iter: Nombre d'itérations d'entraînement (défaut: 100)
    """
    if output_dir is None:
        output_dir = MODEL_PATH
    
    # Créer le répertoire de sortie s'il n'existe pas
    os.makedirs(output_dir, exist_ok=True)
    
    # Charger le modèle multilingue de base comme point de départ
    print("Chargement du modèle multilingue de base...")
    nlp = spacy.load("xx_ent_wiki_sm")
    
    # ============================================================
    # AJOUT DE L'ENTITYRULER (APPROCHE HYBRIDE)
    # L'EntityRuler utilise des règles basées sur des patterns pour reconnaître
    # les entités avant que le NER ne s'exécute. Cela aide le modèle à mieux
    # reconnaître nos labels personnalisés (COMPETENCE, FORMATION).
    # ============================================================
    print("Ajout de l'EntityRuler avec des patterns pour COMPETENCE et FORMATION...")
    
    # Créer les patterns pour COMPETENCE (mots-clés techniques)
    competence_patterns = [
        {"label": "COMPETENCE", "pattern": [{"LOWER": "java"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "python"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "spring"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "spring boot"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "hibernate"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "django"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "flask"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "react"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "angular"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "javascript"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "sql"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "postgresql"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "mysql"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "mongodb"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "docker"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "kubernetes"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "k8s"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "git"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "ci/cd"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "devops"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "c#"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": ".net"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "asp.net"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "azure"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "aws"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "gcp"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "microservices"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "rest"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "graphql"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "agile"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "scrum"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "kanban"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "jira"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "trello"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "confluence"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "machine learning"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "deep learning"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "tensorflow"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "pytorch"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "keras"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "pandas"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "numpy"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "tableau"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "power bi"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "excel"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "figma"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "sketch"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "adobe xd"}]},
    ]
    
    # Créer les patterns pour EXPERIENCE (postes et rôles)
    experience_patterns = [
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "développeur"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "developer"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "software engineer"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "data scientist"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "data analyst"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "machine learning engineer"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "architecte logiciel"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "software architect"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "chef de projet"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "project manager"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "product manager"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "ux designer"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "ui designer"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "full stack"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "frontend"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "backend"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "devops"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "ingénieur"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "engineer"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "analyste"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "analyst"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "consultant"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "consultant"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "senior"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "junior"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "lead"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "manager"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "directeur"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "director"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "stagiaire"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "intern"}]},
        {"label": "EXPERIENCE", "pattern": [{"LOWER": "freelance"}]},
    ]
    
    # Créer les patterns pour FORMATION (diplômes et certifications)
    formation_patterns = [
        {"label": "FORMATION", "pattern": [{"LOWER": "master"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "licence"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "bachelor"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "phd"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "doctorat"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "ingénieur"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "engineer"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "diplôme"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "diploma"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "certificat"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "certificate"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "dut"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "iut"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "mba"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "msc"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "bsc"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "bootcamp"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "formation"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "education"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "université"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "university"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "école"}]},
        {"label": "FORMATION", "pattern": [{"LOWER": "school"}]},
    ]
    
    # Combiner tous les patterns
    all_patterns = competence_patterns + experience_patterns + formation_patterns
    
    # Ajouter l'EntityRuler au pipeline AVANT le NER
    if "entity_ruler" not in nlp.pipe_names:
        # Ajouter l'EntityRuler avant le NER
        ruler = nlp.add_pipe("entity_ruler", before="ner")
        ruler.add_patterns(all_patterns)
        print(f"[OK] EntityRuler ajouté avec {len(all_patterns)} patterns")
    else:
        print("[INFO] EntityRuler existe déjà")
    
    # ============================================================
    # CONFIGURATION DU PIPELINE NER
    # Le pipeline spaCy contient plusieurs composants (tagger, parser, ner, etc.)
    # On s'assure que le composant NER est présent
    # ============================================================
    if "ner" not in nlp.pipe_names:
        # Si NER n'existe pas, on l'ajoute au pipeline
        ner = nlp.add_pipe("ner")
    else:
        # Si NER existe déjà, on le récupère
        ner = nlp.get_pipe("ner")
    
    # ============================================================
    # AJOUT DES LABELS PERSONNALISÉS
    # On parcourt les données d'entraînement pour extraire tous les labels uniques
    # et on les ajoute au modèle NER
    # ============================================================
    for _, annotations in training_data:
        for ent in annotations.get("entities"):
            ner.add_label(ent[2])  # ent[2] est le label (COMPETENCE, EXPERIENCE, FORMATION)
    
    # ============================================================
    # CONFIGURATION DE L'ENTRAÎNEMENT
    # On désactive tous les composants sauf NER pour n'entraîner que le NER
    # Cela accélère l'entraînement et évite de perturber les autres composants
    # ============================================================
    pipe_exceptions = ["ner"]  # Composants à garder actifs
    other_pipes = [pipe for pipe in nlp.pipe_names if pipe not in pipe_exceptions]
    
    # Entraîner uniquement le NER
    with nlp.disable_pipes(*other_pipes):
        optimizer = nlp.begin_training()  # Initialiser l'optimiseur
        
        print(f"Début de l'entraînement pour {n_iter} itérations...")
        for itn in range(n_iter):
            losses = {}  # Dictionnaire pour stocker les pertes
            for text, annotations in training_data:
                # Créer un exemple d'entraînement au format spaCy v3
                example = Example.from_dict(nlp.make_doc(text), annotations)
                # Mettre à jour le modèle avec cet exemple
                nlp.update([example], sgd=optimizer, losses=losses)
            print(f"Iteration {itn + 1}/{n_iter}, Losses: {losses}")
    
    # ============================================================
    # SAUVEGARDE DU MODÈLE
    # Le modèle entraîné est sauvegardé sur disque pour être réutilisé
    # ============================================================
    print(f"Sauvegarde du modèle dans {output_dir}")
    nlp.to_disk(output_dir)  # Sauvegarder le modèle complet
    print("Modèle sauvegardé avec succès")

def create_training_data_from_json(json_data: List[Dict]) -> List[Dict]:
    """
    Convertit des données JSON au format spaCy pour l'entraînement.
    
    FORMAT D'ENTRÉE (JSON) :
    [
        {
            "text": "Jean Dupont est développeur Java",
            "entities": [[0, 12, "PERSON"], [24, 33, "COMPETENCE"]]
        }
    ]
    
    FORMAT DE SORTIE (spaCy) :
    [
        ("Jean Dupont est développeur Java", {"entities": [(0, 12, "PERSON"), (24, 33, "COMPETENCE")]})
    ]
    
    Args:
        json_data: Liste de dictionnaires avec 'text' et 'entities'
        
    Returns:
        Liste de tuples (text, annotations) au format spaCy pour l'entraînement
    """
    training_data = []
    for item in json_data:
        text = item["text"]  # Texte du CV
        entities = item["entities"]  # Liste des entités annotées
        # Créer le tuple au format spaCy
        training_data.append((text, {"entities": entities}))
    
    return training_data
