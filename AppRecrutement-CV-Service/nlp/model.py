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
    # Ajouté dans tous les cas (modèle entraîné ou de base)
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
        {"label": "COMPETENCE", "pattern": [{"LOWER": "gestion"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "supply"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "chain"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "négociation"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "fournisseurs"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "optimisation"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "erp"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "sap"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "management"}]},
        {"label": "COMPETENCE", "pattern": [{"LOWER": "transport"}]},
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
    Extrait les sections du CV (Expériences, Formations, Compétences) en utilisant des regex.
    
    Args:
        text: Texte complet du CV
        
    Returns:
        Dictionnaire avec les sections 'experiences', 'formations' et 'competences'
    """
    sections = {
        "experiences": "",
        "formations": "",
        "competences": ""
    }
    
    print(f"[DEBUG] ===== EXTRACT CV SECTIONS =====")
    print(f"[DEBUG] Longueur du texte complet: {len(text)}")
    
    # Trouver toutes les sections avec leurs positions
    # Patterns avec support des accents et caractères spéciaux
    section_patterns = [
        # Expériences
        (r'EXPÉRIENCES?\s+PROFESSIONNELLES?', 'experiences'),  # EXPÉRIENCES PROFESSIONNELLES
        (r'EXPÉRIENCE\s+PROFESSIONNELLE', 'experiences'),  # EXPÉRIENCE PROFESSIONNELLE
        (r'EXPÉRIENCE\s+&\s+PROJETS', 'experiences'),  # EXPÉRIENCE & PROJETS
        (r'EXPÉRIENCES?\s+&\s+STAGES', 'experiences'),  # EXPÉRIENCES & STAGES
        (r'EXPÉRIENCE', 'experiences'),  # EXPÉRIENCE (fallback)
        (r'PROJETS?\s+ACADÉMIQUES?', 'experiences'),  # PROJETS ACADÉMIQUES
        # Formations
        (r'FORMATION\s+&\s+DIPLÔMES', 'formations'),  # FORMATION & DIPLÔMES
        (r'FORMATION\s+ET\s+DIPLOMES', 'formations'),  # FORMATION ET DIPLOMES
        (r'FORMATION\s+ACADÉMIQUE', 'formations'),  # FORMATION ACADÉMIQUE
        (r'EDUCATION', 'formations'),  # EDUCATION
        (r'CURSUS', 'formations'),  # CURSUS
        (r'FORMATION', 'formations'),  # FORMATION (fallback - doit être après les plus spécifiques)
        # Compétences
        (r'COMPÉTENCES\s+TECHNIQUES', 'competences'),  # COMPÉTENCES TECHNIQUES
        (r'COMPETENCES\s+CLES', 'competences'),  # COMPETENCES CLES
        (r'COMPÉTENCES', 'competences'),  # COMPÉTENCES (fallback)
        (r'LANGUES\s+&\s+SOFT\s+SKILLS', 'competences'),  # LANGUES & SOFT SKILLS
        (r'LANGUES', 'competences'),  # LANGUES
    ]
    
    sections_with_pos = []
    
    # Chercher les patterns dans le texte
    for pattern, section_name in section_patterns:
        match = re.search(pattern, text, re.IGNORECASE)
        if match:
            # Vérifier si cette section n'est pas déjà trouvée (éviter les doublons)
            already_found = any(pos[2] == section_name for pos in sections_with_pos)
            if not already_found:
                sections_with_pos.append((match.start(), match.end(), section_name))
                print(f"[DEBUG] Section {section_name} trouvée à position {match.start()}: '{match.group(0)}'")
    
    # Si aucune section trouvée avec les patterns spécifiques, essayer les patterns génériques
    if not sections_with_pos:
        generic_patterns = [
            (r'EXPÉRIENCE|EXPERIENCE|WORK\s+EXPERIENCE', 'experiences'),
            (r'EDUCATION|FORMATION|DIPLÔMES|DIPLOMES', 'formations'),
            (r'COMPÉTENCES|COMPETENCES|SKILLS', 'competences')
        ]
        for pattern, section_name in generic_patterns:
            match = re.search(pattern, text, re.IGNORECASE)
            if match:
                already_found = any(pos[2] == section_name for pos in sections_with_pos)
                if not already_found:
                    sections_with_pos.append((match.start(), match.end(), section_name))
                    print(f"[DEBUG] Section {section_name} trouvée (générique) à position {match.start()}: '{match.group(0)}'")
    
    # Trier par position de début
    sections_with_pos.sort(key=lambda x: x[0])
    
    print(f"[DEBUG] Sections trouvées: {[(pos[2], pos[0]) for pos in sections_with_pos]}")
    
    # Extraire le texte entre les sections
    for i, (start, end, section_name) in enumerate(sections_with_pos):
        # Trouver la fin de cette section (début de la section suivante ou fin du texte)
        if i + 1 < len(sections_with_pos):
            next_start = sections_with_pos[i + 1][0]
            section_text = text[start:next_start].strip()
        else:
            section_text = text[start:].strip()
        
        # Enlever le titre de section du début
        # Gérer différents types de retours à la ligne (\n, \r\n, \r)
        lines = re.split(r'\r?\n|\r', section_text)
        if lines and len(lines) > 1:
            # La première ligne est le titre, on la retire
            section_text = '\n'.join(lines[1:]).strip()
        elif lines and len(lines) == 1:
            # Si une seule ligne, essayer d'enlever le pattern du titre
            section_text = re.sub(r'^[A-ZÉÀÂÔÙ\s&\-]+(?:\r?\n|\r|$)', '', section_text, flags=re.IGNORECASE).strip()
        
        sections[section_name] = section_text
        print(f"[DEBUG] Section {section_name} extraite: {len(section_text)} caractères")
        print(f"[DEBUG] Section {section_name} PREMIERES 200 CARACTERES: {section_text[:200]}")
        
        # Exclure la section LANGUAGES de la section formations
        if section_name == 'formations' and "LANGUAGES" in section_text:
            sections["formations"] = section_text.split("LANGUAGES")[0].strip()
            print(f"[DEBUG] Section LANGUAGES exclue des formations")
    
    print(f"[DEBUG] Longueur section expériences: {len(sections['experiences'])}")
    print(f"[DEBUG] Longueur section formations: {len(sections['formations'])}")
    print(f"[DEBUG] Longueur section compétences: {len(sections['competences'])}")
    print(f"[DEBUG] ===== FIN EXTRACT CV SECTIONS =====")
    
    return sections

def parse_experiences(experience_text: str) -> List[Dict]:
    """
    Parse la section expériences pour extraire les expériences individuelles.
    Version robuste avec try-except global et look-ahead pour les dates sur lignes suivantes.
    
    Args:
        experience_text: Texte de la section expériences
        
    Returns:
        Liste d'expériences avec titrePoste, entreprise, dateDebut, dateFin, description
    """
    try:
        print(f"[DEBUG] ===== PARSE EXPERIENCES =====")
        print(f"[DEBUG] Longueur texte: {len(experience_text)}")
        print(f"[DEBUG] Texte expériences à parser: {experience_text[:500]}...")
        
        if not experience_text:
            return []
        
        experiences = []
        
        # Fonction pour normaliser (comparaison sans accents)
        def normalize(text):
            text = text.upper()
            text = text.replace('É', 'E').replace('È', 'E').replace('Ê', 'E')
            text = text.replace('À', 'A').replace('Â', 'A')
            text = text.replace('Ô', 'O').replace('Ù', 'U')
            text = text.replace('(', ' ').replace(')', ' ')
            text = text.replace('-', ' ').replace('—', ' ')
            return ' '.join(text.split())
        
        # Titres de section à ignorer (normalisés)
        section_patterns = [
            'EXPERIENCES PROFESSIONNELLES', 'EXPERIENCE PROFESSIONNELLE',
            'PROJETS ACADEMIQUES', 'FORMATION', 'COMPETENCES',
            'EXPERIENCES & PROJETS', 'EXPERIENCES & PROJETS'
        ]
        
        # Regex pour détecter les dates (plages ou années isolées)
        # Accepte: 2023-2024, 2023 2024, 2023–2024, Mois YYYY - Mois YYYY
        date_regex = re.compile(
            r'(\d{4}|(?:Janvier|Février|Mars|Avril|Mai|Juin|Juillet|Août|Septembre|Octobre|Novembre|Décembre)\s+\d{4}|\d{2}/\d{2}/\d{4})\s+[-—]?\s*(\d{4}|Présent|Present|(?:Janvier|Février|Mars|Avril|Mai|Juin|Juillet|Août|Septembre|Octobre|Novembre|Décembre)\s+\d{4}|\d{2}/\d{2}/\d{4})|(?<!\d)(\d{4})(?!\d)',
            re.IGNORECASE
        )
        
        # Fonction pour extraire l'année d'une date
        def extract_year(date_str):
            year_match = re.search(r'\d{4}', date_str)
            if year_match:
                return year_match.group(0)
            return None
        
        # Fonction pour chercher une date dans les lignes suivantes (look-ahead)
        def find_date_in_next_lines(lines, start_index, max_lookahead=3):
            for offset in range(1, max_lookahead + 1):
                if start_index + offset < len(lines):
                    next_line = lines[start_index + offset].strip()
                    if next_line and len(next_line) >= 3:
                        date_match = date_regex.search(next_line)
                        if date_match:
                            return date_match, offset
            return None, None
        
        # Diviser en lignes
        lines = experience_text.split('\n')
        current_poste = None
        current_entreprise = None
        current_date_debut = None
        current_date_fin = None
        current_desc = ""
        processed_indices = set()  # Pour éviter de traiter les lignes de date deux fois
        
        for i, line in enumerate(lines):
            line = line.strip()
            if not line or len(line) < 3:
                continue
            
            if i in processed_indices:
                continue
            
            # Ignorer les titres de section
            normalized = normalize(line)
            is_section = any(pattern in normalized for pattern in section_patterns)
            if is_section:
                print(f"[DEBUG] Ignoré (titre): '{line[:50]}...'")
                continue
            
            # Chercher une date dans la ligne courante
            year_match = date_regex.search(line)
            
            # Cas 1: Ligne avec date et poste sur la même ligne
            if year_match and not current_poste:
                # Sauvegarder l'expérience précédente
                if current_poste:
                    exp = {
                        "titrePoste": current_poste,
                        "entreprise": current_entreprise or "",
                        "dateDebut": current_date_debut,
                        "dateFin": current_date_fin,
                        "description": current_desc.strip()
                    }
                    print(f"[DEBUG] Expérience sauvegardée: {exp['titrePoste'][:40]}...")
                    experiences.append(exp)
                
                # Réinitialiser
                current_desc = ""
                current_entreprise = None
                current_date_debut = None
                current_date_fin = None
                
                # Extraire le poste
                line_without_date = line.replace(year_match.group(0), '').strip(' -—|')
                current_poste = line_without_date if line_without_date else "Poste non spécifié"
                
                # Extraire les dates
                if year_match.lastindex and year_match.lastindex >= 3 and year_match.group(3):
                    isolated_year = year_match.group(3)
                    current_date_debut = f"{isolated_year}-01-01"
                    current_date_fin = f"{isolated_year}-01-01"
                else:
                    start_date = extract_year(year_match.group(1))
                    end_date = extract_year(year_match.group(2))
                    if start_date:
                        current_date_debut = f"{start_date}-01-01"
                    if end_date and end_date.upper() not in ['PRÉSENT', 'PRESENT']:
                        current_date_fin = f"{end_date}-01-01"
                
                print(f"[DEBUG] Nouveau poste avec date: '{current_poste[:40]}...' ({current_date_debut} - {current_date_fin})")
            
            # Cas 2: Ligne qui ressemble à un poste/entreprise SANS date -> chercher date en avant
            elif not year_match and not current_poste:
                # Détecter si c'est un poste (contient des mots-clés de poste)
                poste_keywords = ['développeur', 'ingénieur', 'analyste', 'manager', 'consultant', 'stage', 'intern', 'assistant', 'chef', 'lead', 'architecte', 'react', 'java', 'python', 'developer', 'engineer']
                is_poste = any(keyword in normalized for keyword in poste_keywords)
                
                if is_poste or len(line) < 60:  # Si court, probablement un poste ou entreprise
                    # Chercher la date dans les lignes suivantes
                    future_date_match, offset = find_date_in_next_lines(lines, i)
                    
                    if future_date_match:
                        # Sauvegarder l'expérience précédente
                        if current_poste:
                            exp = {
                                "titrePoste": current_poste,
                                "entreprise": current_entreprise or "",
                                "dateDebut": current_date_debut,
                                "dateFin": current_date_fin,
                                "description": current_desc.strip()
                            }
                            print(f"[DEBUG] Expérience sauvegardée: {exp['titrePoste'][:40]}...")
                            experiences.append(exp)
                        
                        # Réinitialiser
                        current_desc = ""
                        current_entreprise = None
                        current_date_debut = None
                        current_date_fin = None
                        
                        current_poste = line
                        
                        # Extraire les dates de la ligne future
                        if future_date_match.lastindex and future_date_match.lastindex >= 3 and future_date_match.group(3):
                            isolated_year = future_date_match.group(3)
                            current_date_debut = f"{isolated_year}-01-01"
                            current_date_fin = f"{isolated_year}-01-01"
                        else:
                            start_date = extract_year(future_date_match.group(1))
                            end_date = extract_year(future_date_match.group(2))
                            if start_date:
                                current_date_debut = f"{start_date}-01-01"
                            if end_date and end_date.upper() not in ['PRÉSENT', 'PRESENT']:
                                current_date_fin = f"{end_date}-01-01"
                        
                        # Marquer la ligne de date comme traitée
                        processed_indices.add(i + offset)
                        print(f"[DEBUG] Nouveau poste avec date (look-ahead): '{current_poste[:40]}...' ({current_date_debut} - {current_date_fin})")
                    else:
                        # Pas de date trouvée, considérer comme poste sans date
                        current_poste = line
                        print(f"[DEBUG] Nouveau poste sans date: '{current_poste[:40]}...'")
            
            # Cas 3: Ligne entreprise (si on a un poste mais pas encore d'entreprise)
            elif current_poste and not current_entreprise and len(line) < 60:
                if not line.startswith('-') and not line.startswith('•'):
                    current_entreprise = line
                    print(f"[DEBUG] Entreprise: '{current_entreprise[:40]}...'")
                else:
                    current_desc += line + "\n"
            
            # Cas 4: Ligne avec date sur ligne avec puce (associer au poste courant)
            elif year_match and current_poste and (line.startswith('•') or line.startswith('-')):
                if not current_date_debut:
                    exp = {
                        "titrePoste": current_poste,
                        "entreprise": current_entreprise or "",
                        "dateDebut": current_date_debut,
                        "dateFin": current_date_fin,
                        "description": current_desc.strip()
                    }
                    print(f"[DEBUG] Expérience sauvegardée (sans dates): {exp['titrePoste'][:40]}...")
                    experiences.append(exp)
                    current_desc = ""
                    current_entreprise = None
                
                if year_match.lastindex and year_match.lastindex >= 3 and year_match.group(3):
                    isolated_year = year_match.group(3)
                    current_date_debut = f"{isolated_year}-01-01"
                    current_date_fin = f"{isolated_year}-01-01"
                else:
                    start_date = extract_year(year_match.group(1))
                    end_date = extract_year(year_match.group(2))
                    if start_date:
                        current_date_debut = f"{start_date}-01-01"
                    if end_date and end_date.upper() not in ['PRÉSENT', 'PRESENT']:
                        current_date_fin = f"{end_date}-01-01"
                
                print(f"[DEBUG] Date associée au poste: {current_date_debut} - {current_date_fin}")
            
            # Cas 5: Format "Projet : ..." ou "Stage : ..."
            elif ':' in line and not year_match and ('PROJET' in normalized or 'STAGE' in normalized):
                if current_poste:
                    exp = {
                        "titrePoste": current_poste,
                        "entreprise": current_entreprise or "",
                        "dateDebut": current_date_debut,
                        "dateFin": current_date_fin,
                        "description": current_desc.strip()
                    }
                    print(f"[DEBUG] Expérience sauvegardée: {exp['titrePoste'][:40]}...")
                    experiences.append(exp)
                
                current_desc = ""
                current_date_debut = None
                current_date_fin = None
                current_entreprise = None
                current_poste = line.strip()
                print(f"[DEBUG] Nouveau projet/stage: '{current_poste[:40]}...'")
            
            # Cas 6: Description
            elif current_poste:
                current_desc += line + "\n"
        
        # Sauvegarder la dernière expérience
        if current_poste:
            exp = {
                "titrePoste": current_poste,
                "entreprise": current_entreprise or "",
                "dateDebut": current_date_debut,
                "dateFin": current_date_fin,
                "description": current_desc.strip()
            }
            print(f"[DEBUG] Dernière expérience: {exp['titrePoste'][:40]}...")
            experiences.append(exp)
        
        print(f"[DEBUG] Total expériences: {len(experiences)}")
        return experiences
        
    except Exception as e:
        print(f"[ERROR] Erreur dans parse_experiences: {e}")
        import traceback
        traceback.print_exc()
        return []

def parse_competences(competence_text: str) -> List[str]:
    """
    Parse la section compétences pour extraire les compétences individuelles.
    Gère les formats avec catégories (Back-End, Front-End, etc.)
    
    Args:
        competence_text: Texte de la section compétences
        
    Returns:
        Liste de compétences (strings)
    """
    if not competence_text:
        return []
    
    competences = []
    
    print(f"[DEBUG] Texte compétences à parser: {competence_text[:300]}...")
    
    # Catégories et mots à ignorer
    ignore_words = {
        'COMPETENCES', 'TECHNIQUES', 'SKILLS', 'CLES', 'LANGUES', 'SOFT',
        'BACK-END', 'FRONT-END', 'BASES', 'CLOUD', 'DEVOPS', 'OUTILS',
        'Frameworks', 'Langages', 'Web', 'Backend', 'Frontend',
        'ET', 'DE', 'DES', 'DU', 'LA', 'LE', 'LES', 'EN', 'ET', 'OU',
        'UNE', 'UN', 'DANS', 'POUR', 'AVEC', 'PAR', 'SUR', 'AU', 'AUX',
        'CE', 'CET', 'CES', 'SA', 'SES', 'MON', 'MA', 'MES', 'NOTRE',
        'NOS', 'VOTRE', 'VOS', 'LEUR', 'LEURS', 'QUE', 'QUI', 'DONT',
        'MASTER', 'PROBLEMES', 'PROBLÈMES', 'TECHNOLOGIES', 'DONNEES', 'DONNÉES', 'RESOLUTION', 'RÉSOLUTION',
        'INTEGRER', 'INTÉGRER', 'CHERCHE', 'INTERETS', 'INTÉRÊTS', 'TRAVAIL', 'CERTIFIED',
        'EQUIPE', 'ÉQUIPE', 'STACK', 'PASSIONNEE', 'PASSIONNÉE', 'DYNAMIQUE', 'ACTIONS',
        'ADAPTABILITE', 'ADAPTABILITÉ', 'APPRENTISSAGE', 'FULL', 'COURANT', 'TEMPS',
        'PROFIL', 'PROFESSIONNEL', 'PROJETS', 'DEVELOPPEUSE', 'DÉVELOPPEUSE', 'METHODES', 'MÉTHODES',
        'COMMUNICATION', 'EXPERTISE', 'PRIORISATION', 'CONTRIBUER',
        'INNOVANTS', 'NATIF', 'CERTIFICATION', 'LIBS', 'DAPPLICATIONS', 'DAPPLICATIONS',
        'EMERGENTES', 'ÉMERGENTES', 'CERTIFICATIONS', 'IA', 'BLOCKCHAIN',
        'MOBILES', 'SOURCE', 'ANS', 'DEXPERIENCE', 'DEXPÉRIENCE', 'TOEIC', 'DEQUIPE', 'DÉQUIPE',
        'GESTION', 'OPEN', 'PRACTITIONER', 'NATIVE', 'REST', 'WEB'
    }
    
    # Diviser en lignes
    lines = competence_text.split('\n')
    
    for line in lines:
        line = line.strip()
        if not line or len(line) < 2:
            continue
        
        # Ignorer les lignes qui sont des catégories (se terminent par :)
        if line.endswith(':') or line.endswith(' :'):
            print(f"[DEBUG] Ligne ignorée (catégorie): '{line}'")
            continue
        
        # Détecter et gérer le format "Catégorie: Compétence1, Compétence2"
        if ':' in line:
            # Extraire uniquement la partie après ":"
            line = line.split(':', 1)[1].strip()
            print(f"[DEBUG] Ligne avec ':' nettoyée: '{line}'")
        
        # Diviser par des séparateurs (virgules, /, espaces multiples)
        parts = re.split(r'[,/]|\s{2,}', line)
        
        for part in parts:
            comp = part.strip()
            # Nettoyer plus agressivement
            comp = re.sub(r'^[:\.\s\(\)\[\]]+', '', comp)
            comp = re.sub(r'[:\.\s\(\)\[\]]+$', '', comp)  # Retirer ponctuation à la fin
            comp = re.sub(r'[^\w\s\-\+\.#]', '', comp)  # Garder seulement alphanumériques et quelques caractères techniques
            comp = comp.strip()  # Nettoyer après les regex
            
            # Vérifier si c'est valide
            if comp and len(comp) > 2:
                # Diviser les compétences composées par des espaces simples
                # mais garder les compétences composées connues (ex: spring boot, data science)
                known_compound_skills = {'spring boot', 'data science', 'machine learning',
                                        'deep learning', 'natural language', 'artificial intelligence',
                                        'full stack', 'front end', 'back end', 'dev ops'}
                comp_lower = comp.lower()

                if comp_lower not in known_compound_skills and ' ' in comp:
                    # Diviser en compétences individuelles
                    sub_parts = comp.split()
                    for sub_comp in sub_parts:
                        sub_comp = sub_comp.strip()
                        # Retirer le point à la fin s'il y en a un
                        sub_comp = sub_comp.rstrip('.')
                        if sub_comp and len(sub_comp) > 2:
                            sub_comp_upper = sub_comp.upper()
                            if sub_comp_upper not in ignore_words:
                                if sub_comp not in competences:
                                    competences.append(sub_comp)
                                    print(f"[DEBUG] Compétence trouvée (divisée): '{sub_comp}'")
                            else:
                                print(f"[DEBUG] Compétence ignorée (dans ignore_words): '{sub_comp}'")
                else:
                    # Garder telle quelle
                    comp = comp.rstrip('.')  # Retirer le point à la fin
                    comp_upper = comp.upper()
                    if comp_upper not in ignore_words:
                        if comp not in competences:
                            competences.append(comp)
                            print(f"[DEBUG] Compétence trouvée: '{comp}'")
                    else:
                        print(f"[DEBUG] Compétence ignorée (dans ignore_words): '{comp}'")
    
    print(f"[DEBUG] Total compétences extraites: {len(competences)}")
    return competences

def extract_niveau_etude(formations: List[Dict]) -> str:
    """
    Extrait le niveau d'étude le plus élevé à partir de la liste des formations.
    
    Args:
        formations: Liste des formations extraites du CV
        
    Returns:
        Niveau d'étude (DOCTORAT, INGENIEUR, MASTER, LICENCE, DUT_BTS, BAC, SANS_EXIGENCE)
    """
    if not formations:
        return "SANS_EXIGENCE"
    
    # Mapping des mots-clés vers les niveaux d'étude
    niveau_mapping = {
        "doctorat": "DOCTORAT",
        "phd": "DOCTORAT",
        "ingénieur": "INGENIEUR",
        "engineer": "INGENIEUR",
        "master": "MASTER",
        "m2": "MASTER",
        "licence": "LICENCE",
        "bachelor": "LICENCE",
        "dut": "DUT_BTS",
        "bts": "DUT_BTS",
        "bac": "BAC",
        "baccalauréat": "BAC"
    }
    
    # Ordre de priorité (du plus élevé au plus bas)
    # SANS_EXIGENCE en dernier pour éviter l'erreur "not in list"
    priorite = ["DOCTORAT", "INGENIEUR", "MASTER", "LICENCE", "DUT_BTS", "BAC", "SANS_EXIGENCE"]
    
    niveau_max = "SANS_EXIGENCE"
    
    for formation in formations:
        diplome = formation.get("diplome", "").lower()
        
        # Chercher le niveau d'étude dans le diplôme
        for mot_cle, niveau in niveau_mapping.items():
            if mot_cle in diplome:
                # Si ce niveau est plus élevé que le niveau actuel, le mettre à jour
                if priorite.index(niveau) < priorite.index(niveau_max):
                    niveau_max = niveau
                break
    
    print(f"[DEBUG] Niveau d'étude extrait: {niveau_max}")
    return niveau_max

def parse_formations(formation_text: str) -> List[Dict]:
    """
    Parse la section formations pour extraire les formations complètes.
    
    Format attendu:
    - Diplôme avec date (ex: Cycle d'Ingénieur 2023 — 2026)
    - Établissement (ex: ESPRIT)
    
    Args:
        formation_text: Texte de la section formations
        
    Returns:
        Liste de formations avec diplome, etablissement, specialite, anneeObtention
    """
    if not formation_text:
        return []
    
    formations = []
    print(f"[DEBUG] Parsing formations: {formation_text[:200]}...")
    
    # Mots-clés d'établissements tunisiens
    establishment_keywords = [
        'ESPRIT', 'Faculté', 'Faculte', 'Institut', 'École', 'Ecole', 'Université',
        'ISG', 'IHEC', 'ENIT', 'ENSI', 'INSAT', 'ISSAT', 'ISI', 'Supcom',
        'ISET', 'FST', 'ENIG', 'ENIS', 'ENIM', 'ENAU', 'ENIT', 'ENSI',
        'ESST', 'ISBS', 'ISCOM', 'ISGG', 'ISLT', 'IPT', 'ICIT', 'INAT',
        'INB', 'INBS', 'INSAT', 'INSPE', 'INS', 'IPEIT', 'IPEIN', 'IPEIS',
        'IPEIT', 'IPEIN', 'IPEIS', 'IPEIT', 'IPEIN', 'IPEIS', 'IPEIT',
        'Université de Tunis', 'Université de Carthage', 'Université de Sfax',
        'Université de Sousse', 'Université de Gabès', 'Université de Monastir',
        'Université de Jendouba', 'Université de Gafsa', 'Université de Kairouan'
    ]
    
    # Lignes à ignorer (titres)
    ignore_patterns = ['FORMATION', 'EDUCATION', 'DIPLOMES', 'CURSUS']
    
    lines = re.split(r'\r?\n|\r', formation_text)
    pending_diplome = None
    pending_annee = None
    
    for i, line in enumerate(lines):
        line = line.strip()
        if not line or len(line) < 3:
            continue

        # Ignorer les titres
        line_upper = line.upper()
        if any(pattern in line_upper for pattern in ignore_patterns):
            print(f"[DEBUG] Ignoré (titre): '{line[:40]}...'")
            continue

        # Vérifier si c'est une ligne avec date
        year_match = re.search(r'(\d{4})\s*[-—]\s*(\d{4}|Présent|En cours)', line, re.IGNORECASE)

        # Vérifier si c'est le format "Diplôme | Établissement" (sans date)
        if '|' in line and not year_match:
            parts = [p.strip() for p in line.split('|')]
            if len(parts) >= 2:
                # Format: Diplôme | Établissement ou Spécialisation : ... | ...
                diplome = parts[0]
                etablissement = parts[1] if len(parts) > 1 else ""

                # Vérifier si c'est une ligne de spécialisation
                if 'spécialisation' in diplome.lower() or 'specialisation' in diplome.lower():
                    # Extraire la spécialité et l'établissement
                    specialite = diplome.split(':')[1].strip() if ':' in diplome else diplome
                    # Si on a un diplôme en attente, mettre à jour sa spécialité
                    if pending_diplome:
                        formation = {
                            "diplome": pending_diplome,
                            "etablissement": etablissement,
                            "specialite": specialite,
                            "anneeObtention": pending_annee
                        }
                        formations.append(formation)
                        print(f"[DEBUG] Formation (avec spécialisation): {formation}")
                        pending_diplome = None
                        pending_annee = None
                    else:
                        # Créer une formation avec juste la spécialisation
                        formation = {
                            "diplome": "",
                            "etablissement": etablissement,
                            "specialite": specialite,
                            "anneeObtention": None
                        }
                        formations.append(formation)
                        print(f"[DEBUG] Formation (spécialisation seule): {formation}")
                else:
                    # Format: Diplôme | Établissement
                    # Accepter n'importe quel texte après "|" comme établissement s'il n'est pas vide
                    if etablissement and len(etablissement) > 2:
                        formation = {
                            "diplome": diplome,
                            "etablissement": etablissement,
                            "specialite": "",
                            "anneeObtention": None
                        }
                        formations.append(formation)
                        print(f"[DEBUG] Formation (format | sans date): {formation}")
                        pending_diplome = None
                        pending_annee = None
                    else:
                        pending_diplome = diplome
            else:
                pending_diplome = line
        elif year_match:
            # C'est une ligne avec date
            # Sauvegarder le diplôme précédent s'il existe avec un établissement
            if pending_diplome:
                formation = {
                    "diplome": pending_diplome,
                    "etablissement": "",  # On n'a pas trouvé d'établissement
                    "specialite": "",
                    "anneeObtention": pending_annee
                }
                formations.append(formation)
                print(f"[DEBUG] Formation (sans étab): {formation}")
            
            # Extraire l'année
            start_year = year_match.group(1)
            pending_annee = f"{start_year}-01-01"
            
            # Extraire le texte avant la date
            text_before_date = line.replace(year_match.group(0), '').strip(' -—|')
            
            # Vérifier si le texte contient un établissement (format: Etablissement | Date)
            is_establishment = any(keyword.upper() in text_before_date.upper() for keyword in establishment_keywords)
            
            # Vérifier si c'est le format "Diplôme | Etablissement | Date"
            if '|' in text_before_date:
                # Séparer par |
                parts = [p.strip() for p in text_before_date.split('|')]
                if len(parts) >= 2:
                    # Format: Diplôme | Etablissement
                    pending_diplome = parts[0]
                    establishment = parts[1] if len(parts) > 1 else ""
                    formation = {
                        "diplome": pending_diplome,
                        "etablissement": establishment,
                        "specialite": "",
                        "anneeObtention": pending_annee
                    }
                    formations.append(formation)
                    print(f"[DEBUG] Formation (format |): {formation}")
                    pending_diplome = None
                    pending_annee = None
                else:
                    pending_diplome = text_before_date
                    print(f"[DEBUG] Diplôme trouvé: '{pending_diplome}', année: {pending_annee}")
            elif is_establishment:
                # C'est une ligne "Etablissement | Date"
                # Si on a un diplôme en attente, l'associer
                if pending_diplome:
                    formation = {
                        "diplome": pending_diplome,
                        "etablissement": text_before_date,
                        "specialite": "",
                        "anneeObtention": pending_annee
                    }
                    formations.append(formation)
                    print(f"[DEBUG] Formation (complète): {formation}")
                    pending_diplome = None
                    pending_annee = None
                else:
                    # Établissement sans diplôme - ignorer
                    print(f"[DEBUG] Établissement sans diplôme: '{text_before_date}'")
            else:
                # C'est peut-être un diplôme avec date sur la même ligne
                pending_diplome = text_before_date
                print(f"[DEBUG] Diplôme trouvé: '{pending_diplome}', année: {pending_annee}")
        
        else:
            # Pas de date - vérifier si c'est un établissement
            is_establishment = any(keyword.upper() in line_upper for keyword in establishment_keywords)
            
            if is_establishment and pending_diplome:
                # Associer cet établissement au diplôme en attente
                formation = {
                    "diplome": pending_diplome,
                    "etablissement": line,
                    "specialite": "",
                    "anneeObtention": pending_annee
                }
                formations.append(formation)
                print(f"[DEBUG] Formation (complète): {formation}")
                pending_diplome = None
                pending_annee = None
            elif is_establishment and not pending_diplome:
                # Établissement sans diplôme en attente - ignorer ou créer une formation vide
                print(f"[DEBUG] Établissement sans diplôme en attente: '{line}'")
            elif not is_establishment and not pending_diplome:
                # C'est peut-être un diplôme sans date
                pending_diplome = line
                print(f"[DEBUG] Diplôme sans date: '{pending_diplome}'")
    
    # Sauvegarder le dernier diplôme s'il reste
    if pending_diplome:
        formation = {
            "diplome": pending_diplome,
            "etablissement": "",
            "specialite": "",
            "anneeObtention": pending_annee
        }
        formations.append(formation)
        print(f"[DEBUG] Dernière formation: {formation}")
    
    print(f"[DEBUG] Total formations: {len(formations)}")
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
    print(f"[DEBUG] Section compétences: {sections['competences'][:200]}...")
    
    # ============================================================
    # PARSING DES EXPÉRIENCES ET FORMATIONS
    # ============================================================
    experiences = parse_experiences(sections["experiences"])
    formations = parse_formations(sections["formations"])
    
    print(f"[DEBUG] Expériences extraites: {len(experiences)}")
    print(f"[DEBUG] Formations extraites: {len(formations)}")
    
    # ============================================================
    # PARSING DES COMPÉTENCES (APPROCHE HYBRIDE: spaCy + Regex)
    # ============================================================
    
    # 1. Extraire les compétences avec spaCy EntityRuler depuis le texte complet
    doc = nlp(text)
    spacy_competences = []
    for ent in doc.ents:
        if ent.label_ == "COMPETENCE":
            spacy_competences.append(ent.text.lower())
    
    print(f"[DEBUG] Compétences spaCy EntityRuler: {len(spacy_competences)}")
    
    # 2. Extraire les compétences depuis la section textuelle avec regex
    raw_competences = parse_competences(sections["competences"])
    print(f"[DEBUG] Compétences regex: {len(raw_competences)}")
    
    # 3. Fusionner et dédoublonner les compétences des deux approches
    all_competences = set()
    for comp in spacy_competences:
        all_competences.add(comp)
    for comp in raw_competences:
        all_competences.add(comp.lower())
    
    # Convertir en format attendu par Java
    competences = []
    for comp in all_competences:
        competences.append({
            "text": comp,
            "label": "COMPETENCE",
            "start": 0,
            "end": len(comp)
        })
    
    print(f"[DEBUG] Compétences totales (fusionnées): {len(competences)}")
    
    # ============================================================
    # NETTOYAGE DES DOUBLONS
    # ============================================================
    experiences = remove_duplicates(experiences, "titrePoste")
    formations = remove_duplicates(formations, "diplome")
    competences = remove_duplicates(competences, "text")
    
    print(f"[DEBUG] Expériences après dédoublonnage: {len(experiences)}")
    print(f"[DEBUG] Formations après dédoublonnage: {len(formations)}")
    print(f"[DEBUG] Compétences après dédoublonnage: {len(competences)}")
    
    # ============================================================
    # EXTRACTION DU NIVEAU D'ÉTUDE
    # ============================================================
    niveau_etude = extract_niveau_etude(formations)
    
    # ============================================================
    # LOG DE CONTRÔLE : Afficher ce que Python envoie au Java
    # ============================================================
    print("[DEBUG] === ENVOI AU JAVA ===")
    print(f"[DEBUG] Compétences: {[c['text'] for c in competences]}")
    print(f"[DEBUG] Expériences: {experiences}")
    print(f"[DEBUG] Formations: {formations}")
    print(f"[DEBUG] Niveau d'étude: {niveau_etude}")
    print("[DEBUG] ===================")
    
    return {
        "competences": competences,
        "experiences": experiences,
        "formations": formations,
        "niveauEtude": niveau_etude
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
