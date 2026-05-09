# ============================================================
# FICHIER : main.py
# DESCRIPTION : Application FastAPI principale pour le service d'extraction de CV avec IA
# LOCALISATION : D:\PFE\AppRecrutement-CV-Service\main.py
# FONCTION : Expose un endpoint REST pour recevoir le texte d'un CV et retourner les entités extraites
# ============================================================

# Importations des bibliothèques FastAPI
from fastapi import FastAPI, HTTPException  # FastAPI pour créer l'API, HTTPException pour les erreurs
from pydantic import BaseModel  # BaseModel pour définir les modèles de données (request/response)
from typing import List, Dict  # Types pour les listes et dictionnaires
import sys  # sys pour manipuler le chemin Python
import os  # os pour les chemins de fichiers

# ============================================================
# CONFIGURATION DU CHEMIN PYTHON
# Ajout du dossier 'nlp' au chemin Python pour pouvoir importer les fonctions du module model.py
# ============================================================
sys.path.append(os.path.join(os.path.dirname(__file__), 'nlp'))
from model import load_model, extract_entities  # Import des fonctions depuis nlp/model.py

# ============================================================
# CRÉATION DE L'APPLICATION FASTAPI
# title : Nom du service affiché dans la documentation Swagger
# version : Version de l'API
# ============================================================
app = FastAPI(title="CV Extraction Service", version="1.0.0")

# ============================================================
# MODÈLES DE DONNÉES (Pydantic)
# Ces classes définissent la structure des données envoyées/reçues par l'API
# ============================================================

class CVRequest(BaseModel):
    """
    Modèle pour la requête d'extraction de CV.
    Reçoit le texte brut du CV envoyé par Spring Boot.
    """
    text: str  # Texte brut extrait du CV (PDF/DOCX)

class Entity(BaseModel):
    """
    Modèle pour une entité extraite (non utilisé directement mais pour documentation).
    Représente une entité avec sa position dans le texte.
    """
    text: str  # Texte de l'entité (ex: "Java", "Spring Boot")
    label: str  # Type de l'entité (COMPETENCE, EXPERIENCE, FORMATION)
    start: int  # Position de début dans le texte
    end: int  # Position de fin dans le texte

class CVResponse(BaseModel):
    """
    Modèle pour la réponse de l'API.
    Retourne les entités groupées par type.
    """
    competences: List[Dict]  # Liste des compétences extraites
    experiences: List[Dict]  # Liste des expériences extraites
    formations: List[Dict]  # Liste des formations extraites

# ============================================================
# VARIABLE GLOBALE POUR LE MODÈLE spaCy
# nlp_model : Contient le modèle spaCy chargé en mémoire
# Initialisé à None et chargé au démarrage ou à la première requête
# ============================================================
nlp_model = None

# ============================================================
# ÉVÉNEMENT DE DÉMARRAGE
# Cette fonction s'exécute automatiquement quand le serveur démarre
# Charge le modèle spaCy en mémoire pour éviter de le recharger à chaque requête
# ============================================================
@app.on_event("startup")
async def startup_event():
    global nlp_model  # Utilisation de la variable globale
    try:
        nlp_model = load_model()  # Chargement du modèle depuis nlp/model.py
        print("Modèle spaCy chargé avec succès")
    except Exception as e:
        # Si le modèle ne peut pas être chargé au démarrage, on continue quand même
        # Il sera chargé à la première requête
        print(f"Erreur lors du chargement du modèle: {e}")
        print("Le modèle sera chargé à la première demande")

# ============================================================
# ENDPOINT PRINCIPAL : POST /extract
# Reçoit le texte d'un CV et retourne les entités extraites (compétences, expériences, formations)
# Utilisé par Spring Boot via HTTP POST
# ============================================================
@app.post("/extract", response_model=CVResponse)
async def extract_cv(request: CVRequest):
    global nlp_model  # Utilisation de la variable globale
    
    # Si le modèle n'est pas encore chargé, on le charge maintenant
    if nlp_model is None:
        try:
            nlp_model = load_model()
        except Exception as e:
            raise HTTPException(status_code=500, detail=f"Erreur lors du chargement du modèle: {str(e)}")
    
    try:
        # Extraction des entités depuis le texte du CV en utilisant le modèle spaCy amélioré
        # La fonction extract_entities retourne maintenant un dictionnaire avec les 3 catégories
        entities = extract_entities(nlp_model, request.text)
        
        # ============================================================
        # RÉCUPÉRATION DES ENTITÉS PAR TYPE
        # La fonction extract_entities retourne déjà les entités groupées
        # ============================================================
        competences = entities.get("competences", [])  # Compétences techniques
        experiences = entities.get("experiences", [])  # Expériences professionnelles
        formations = entities.get("formations", [])  # Formations et diplômes
        
        # Retour de la réponse structurée
        return CVResponse(
            competences=competences,
            experiences=experiences,
            formations=formations
        )
    except Exception as e:
        # En cas d'erreur lors de l'extraction, on retourne une erreur 500
        raise HTTPException(status_code=500, detail=f"Erreur lors de l'extraction: {str(e)}")

# ============================================================
# ENDPOINT DE TEST : GET /
# Endpoint racine pour vérifier que le service fonctionne
# ============================================================
@app.get("/")
async def root():
    return {"message": "CV Extraction Service - FastAPI + spaCy", "status": "running"}

# ============================================================
# ENDPOINT DE SANTÉ : GET /health
# Permet de vérifier si le service est en ligne et si le modèle est chargé
# Utile pour les monitoring et health checks
# ============================================================
@app.get("/health")
async def health_check():
    return {"status": "healthy", "model_loaded": nlp_model is not None}

# ============================================================
# POINT D'ENTRÉE PRINCIPAL
# Exécuté quand on lance le fichier directement avec: python main.py
# Démarre le serveur Uvicorn sur le port 8000
# ============================================================
if __name__ == "__main__":
    import uvicorn  # Import de uvicorn pour le serveur
    # Démarrage du serveur :
    # - host="0.0.0.0" : Écoute sur toutes les interfaces réseau
    # - port=8000 : Port d'écoute HTTP
    uvicorn.run(app, host="0.0.0.0", port=8000)
