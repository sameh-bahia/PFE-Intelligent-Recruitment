"""
Script d'entraînement d'un modèle spaCy NER personnalisé pour l'extraction de compétences
Ce script entraîne un modèle spaCy pour reconnaître l'entité "COMPETENCE" dans les CVs
"""

import spacy
from spacy.training import Example
from spacy.util import minibatch, compounding, filter_spans
import random
import os
from pathlib import Path

# Charger le modèle de base français
print("Chargement du modèle spaCy de base (fr_core_news_sm)...")
try:
    nlp = spacy.load("fr_core_news_sm")
except OSError:
    print("Modèle non trouvé, téléchargement en cours...")
    os.system("python -m spacy download fr_core_news_sm")
    nlp = spacy.load("fr_core_news_sm")

# Ajouter l'étiquette NER "COMPETENCE" si elle n'existe pas
if "ner" not in nlp.pipe_names:
    ner = nlp.add_pipe("ner")
else:
    ner = nlp.get_pipe("ner")

ner.add_label("COMPETENCE")

# Charger la liste des compétences depuis le fichier
def load_competences_list(file_path):
    """Charge la liste des compétences depuis un fichier texte"""
    with open(file_path, 'r', encoding='utf-8') as f:
        competences = []
        for line in f:
            line = line.strip()
            # Ignorer les lignes vides et les commentaires
            if line and not line.startswith('#'):
                competences.append(line)
    return competences

# Charger les compétences
competences_list = load_competences_list("data/competences_list.txt")
print(f"Chargement de {len(competences_list)} compétences depuis competences_list.txt")

# Générer des données d'entraînement synthétiques
def generate_training_data(competences, num_samples=1000):
    """
    Génère des données d'entraînement synthétiques en créant des phrases
    qui contiennent les compétences dans différents contextes.
    Génère des phrases avec 1, 2 ou 3 compétences pour apprendre les séparateurs.
    """
    training_data = []
    
    # Contextes pour une seule compétence
    single_contexts = [
        "Je maîtrise {comp}.",
        "Compétences : {comp}.",
        "Technologies : {comp}.",
        "J'ai utilisé {comp} dans mon projet.",
        "Expérience avec {comp}.",
        "Connaissance en {comp}.",
        "Skills : {comp}.",
        "Stack technique : {comp}.",
        "Framework : {comp}.",
        "Langage : {comp}.",
        "Base de données : {comp}.",
        "Outil : {comp}.",
        "Plateforme : {comp}.",
        "Cloud : {comp}.",
        "DevOps : {comp}.",
        "Backend : {comp}.",
        "Frontend : {comp}.",
        "Fullstack : {comp}.",
        "Data Science : {comp}.",
        "Machine Learning : {comp}.",
        "IA : {comp}.",
        "NLP : {comp}.",
        "Développement : {comp}.",
        "Programmation : {comp}.",
        "Software : {comp}.",
        "Application : {comp}.",
        "Web : {comp}.",
        "Mobile : {comp}.",
        "API : {comp}.",
        "Microservices : {comp}.",
        "Architecture : {comp}.",
        "Design : {comp}.",
        "Testing : {comp}.",
        "CI/CD : {comp}.",
        "Monitoring : {comp}.",
        "Logging : {comp}.",
        "Security : {comp}.",
        "Authentication : {comp}.",
        "Authorization : {comp}.",
        "Database : {comp}.",
        "Storage : {comp}.",
        "Caching : {comp}.",
        "Messaging : {comp}.",
        "Search : {comp}.",
        "Analytics : {comp}.",
        "Visualization : {comp}.",
        "Reporting : {comp}.",
        "Documentation : {comp}.",
        "Version Control : {comp}.",
        "Collaboration : {comp}.",
        "Project Management : {comp}.",
        "Agile : {comp}.",
        "Scrum : {comp}.",
        "Kanban : {comp}.",
    ]
    
    # Contextes pour deux compétences (avec différents séparateurs)
    dual_contexts = [
        "Je maîtrise {comp1} et {comp2}.",
        "Compétences : {comp1}, {comp2}.",
        "Technologies : {comp1} et {comp2}.",
        "J'ai utilisé {comp1} et {comp2} dans mon projet.",
        "Expérience avec {comp1}, {comp2}.",
        "Connaissance en {comp1} et {comp2}.",
        "Skills : {comp1}, {comp2}.",
        "Stack technique : {comp1} et {comp2}.",
        "Framework : {comp1}, {comp2}.",
        "Langages : {comp1} et {comp2}.",
        "Bases de données : {comp1}, {comp2}.",
        "Outils : {comp1} et {comp2}.",
        "Plateformes : {comp1}, {comp2}.",
        "Cloud : {comp1} et {comp2}.",
        "DevOps : {comp1}, {comp2}.",
        "Backend : {comp1} et {comp2}.",
        "Frontend : {comp1}, {comp2}.",
        "Fullstack : {comp1} et {comp2}.",
        "Data Science : {comp1}, {comp2}.",
        "Machine Learning : {comp1} et {comp2}.",
        "IA : {comp1}, {comp2}.",
        "NLP : {comp1} et {comp2}.",
        "Développement : {comp1}, {comp2}.",
        "Programmation : {comp1} et {comp2}.",
        "Software : {comp1}, {comp2}.",
        "Application : {comp1} et {comp2}.",
        "Web : {comp1}, {comp2}.",
        "Mobile : {comp1}, {comp2}.",
        "API : {comp1} et {comp2}.",
        "Microservices : {comp1}, {comp2}.",
        "Architecture : {comp1} et {comp2}.",
        "Design : {comp1}, {comp2}.",
        "Testing : {comp1}, {comp2}.",
        "CI/CD : {comp1} et {comp2}.",
        "Monitoring : {comp1}, {comp2}.",
        "Logging : {comp1}, {comp2}.",
        "Security : {comp1} et {comp2}.",
        "Authentication : {comp1}, {comp2}.",
        "Authorization : {comp1}, {comp2}.",
        "Database : {comp1}, {comp2}.",
        "Storage : {comp1}, {comp2}.",
        "Caching : {comp1}, {comp2}.",
        "Messaging : {comp1}, {comp2}.",
        "Search : {comp1}, {comp2}.",
        "Analytics : {comp1}, {comp2}.",
        "Visualization : {comp1}, {comp2}.",
        "Reporting : {comp1}, {comp2}.",
        "Documentation : {comp1}, {comp2}.",
        "Version Control : {comp1}, {comp2}.",
        "Collaboration : {comp1}, {comp2}.",
        "Project Management : {comp1}, {comp2}.",
        "Agile : {comp1}, {comp2}.",
        "Scrum : {comp1}, {comp2}.",
        "Kanban : {comp1}, {comp2}.",
    ]
    
    # Contextes pour trois compétences
    triple_contexts = [
        "Je maîtrise {comp1}, {comp2} et {comp3}.",
        "Compétences : {comp1}, {comp2}, {comp3}.",
        "Technologies : {comp1}, {comp2} et {comp3}.",
        "J'ai utilisé {comp1}, {comp2} et {comp3} dans mon projet.",
        "Expérience avec {comp1}, {comp2}, {comp3}.",
        "Connaissance en {comp1}, {comp2} et {comp3}.",
        "Skills : {comp1}, {comp2}, {comp3}.",
        "Stack technique : {comp1}, {comp2} et {comp3}.",
        "Framework : {comp1}, {comp2}, {comp3}.",
        "Langages : {comp1}, {comp2} et {comp3}.",
        "Bases de données : {comp1}, {comp2}, {comp3}.",
        "Outils : {comp1}, {comp2} et {comp3}.",
        "Plateformes : {comp1}, {comp2}, {comp3}.",
        "Cloud : {comp1}, {comp2} et {comp3}.",
        "DevOps : {comp1}, {comp2}, {comp3}.",
        "Backend : {comp1}, {comp2} et {comp3}.",
        "Frontend : {comp1}, {comp2}, {comp3}.",
        "Fullstack : {comp1}, {comp2} et {comp3}.",
        "Data Science : {comp1}, {comp2}, {comp3}.",
        "Machine Learning : {comp1}, {comp2} et {comp3}.",
        "IA : {comp1}, {comp2}, {comp3}.",
        "NLP : {comp1}, {comp2} et {comp3}.",
        "Développement : {comp1}, {comp2}, {comp3}.",
        "Programmation : {comp1}, {comp2} et {comp3}.",
        "Software : {comp1}, {comp2}, {comp3}.",
        "Application : {comp1}, {comp2} et {comp3}.",
        "Web : {comp1}, {comp2}, {comp3}.",
        "Mobile : {comp1}, {comp2} et {comp3}.",
        "API : {comp1}, {comp2}, {comp3}.",
        "Microservices : {comp1}, {comp2} et {comp3}.",
        "Architecture : {comp1}, {comp2}, {comp3}.",
        "Design : {comp1}, {comp2} et {comp3}.",
        "Testing : {comp1}, {comp2}, {comp3}.",
        "CI/CD : {comp1}, {comp2} et {comp3}.",
        "Monitoring : {comp1}, {comp2}, {comp3}.",
        "Logging : {comp1}, {comp2} et {comp3}.",
        "Security : {comp1}, {comp2}, {comp3}.",
        "Authentication : {comp1}, {comp2} et {comp3}.",
        "Authorization : {comp1}, {comp2}, {comp3}.",
        "Database : {comp1}, {comp2}, {comp3}.",
        "Storage : {comp1}, {comp2} et {comp3}.",
        "Caching : {comp1}, {comp2}, {comp3}.",
        "Messaging : {comp1}, {comp2} et {comp3}.",
        "Search : {comp1}, {comp2}, {comp3}.",
        "Analytics : {comp1}, {comp2} et {comp3}.",
        "Visualization : {comp1}, {comp2}, {comp3}.",
        "Reporting : {comp1}, {comp2} et {comp3}.",
        "Documentation : {comp1}, {comp2} et {comp3}.",
        "Version Control : {comp1}, {comp2}, {comp3}.",
        "Collaboration : {comp1}, {comp2} et {comp3}.",
        "Project Management : {comp1}, {comp2}, {comp3}.",
        "Agile : {comp1}, {comp2} et {comp3}.",
        "Scrum : {comp1}, {comp2}, {comp3}.",
        "Kanban : {comp1}, {comp2} et {comp3}.",
    ]
    
    # Contextes pour compétences sur une même ligne sans séparateurs (cas réel des CV)
    line_contexts = [
        "{comp1} {comp2} {comp3}",
        "{comp1} {comp2}",
        "{comp1} {comp2} {comp3}",
        "{comp1} {comp2}",
    ]
    
    # Contextes pour compétences avec versions (ex: Java 11, Spring Boot 2.7)
    version_contexts = [
        "{comp} 11",
        "{comp} 17",
        "{comp} 2.7",
        "{comp} 3.0",
        "{comp} 8",
        "{comp} LTS",
        "{comp} 1.8",
        "{comp} 5",
    ]
    
    # Exemples négatifs (phrases sans compétences)
    negative_examples = [
        "Je cherche un emploi en développement.",
        "Projet de fin d'études réalisé en équipe.",
        "J'ai travaillé dans une grande entreprise.",
        "Mon objectif est de progresser.",
        "J'aime le travail en équipe.",
        "Je suis motivé et dynamique.",
        "J'ai une bonne communication.",
        "Je suis à l'écoute des clients.",
        "Je gère mon temps efficacement.",
        "Je suis adaptable et flexible.",
        "J'ai un esprit d'équipe.",
        "Je suis rigoureux dans mon travail.",
        "Je respecte les délais.",
        "Je suis autonome dans mes tâches.",
        "J'ai une bonne organisation.",
        "Je suis curieux et apprends vite.",
        "Je suis créatif et innovant.",
        "J'ai une bonne résistance au stress.",
        "Je suis ponctuel et assidu.",
        "J'ai une bonne présentation.",
        "Je suis diplomate et diplomate.",
        "J'ai une bonne écriture.",
        "Je suis à l'aise à l'oral.",
        "J'ai une bonne mémoire.",
        "Je suis analytique et logique.",
        "J'ai une bonne vision d'ensemble.",
        "Je suis orienté résultat.",
        "J'ai un bon sens de l'organisation.",
        "Je suis proactif et réactif.",
        "J'ai une bonne capacité d'analyse.",
        "Je suis persévérant et tenace.",
        "J'ai une bonne éthique de travail.",
        "Je suis respectueux et respectueuse.",
        "J'ai une bonne intégration.",
        "Je suis sociable et ouvert.",
        "J'ai une bonne posture.",
        "Je suis confiant et confiante.",
        "J'ai une bonne attitude.",
        "Je suis positif et positive.",
        "J'ai une bonne énergie.",
        "Je suis passionné et passionnée.",
        "J'ai une bonne motivation.",
        "Je suis ambitieux et ambitieuse.",
        "J'ai une bonne ambition.",
        "Je suis déterminé et déterminée.",
        "J'ai une bonne détermination.",
        "Je suis engagé et engagée.",
        "J'ai un bon engagement.",
        "Je suis loyal et loyale.",
        "J'ai une bonne loyauté.",
        "Je suis honnête et honnête.",
        "J'ai une bonne honnêteté.",
        "Je suis intègre et intègre.",
        "J'ai une bonne intégrité.",
        "Je suis transparent et transparente.",
        "J'ai une bonne transparence.",
        "Je suis sincère et sincère.",
        "J'ai une bonne sincérité.",
        "Je suis authentique et authentique.",
        "J'ai une bonne authenticité.",
        "Je suis humble et humble.",
        "J'ai une bonne humilité.",
        "Je suis modeste et modeste.",
        "J'ai une bonne modestie.",
        "Je suis respectueux et respectueuse.",
        "J'ai un bon respect.",
        "Je suis tolérant et tolérante.",
        "J'ai une bonne tolérance.",
        "Je suis patient et patiente.",
        "J'ai une bonne patience.",
        "Je suis calme et calme.",
        "J'ai une bonne calme.",
        "Je suis serein et sereine.",
        "J'ai une bonne sérénité.",
        "Je suis zen et zen.",
        "J'ai une bonne zen.",
        "Je suis équilibré et équilibrée.",
        "J'ai un bon équilibre.",
        "Je suis stable et stable.",
        "J'ai une bonne stabilité.",
        "Je suis constant et constante.",
        "J'ai une bonne constance.",
        "Je suis régulier et régulière.",
        "J'ai une bonne régularité.",
        "Je suis cohérent et cohérente.",
        "J'ai une bonne cohérence.",
        "Je suis logique et logique.",
        "J'ai une bonne logique.",
        "Je suis rationnel et rationnelle.",
        "J'ai une bonne rationalité.",
        "Je suis pragmatique et pragmatique.",
        "J'ai une bonne pragmatisme.",
        "Je suis réaliste et réaliste.",
        "J'ai une bonne réalisme.",
        "Je suis concret et concrète.",
        "J'ai une bonne concrétisation.",
        "Je suis pratique et pratique.",
        "J'ai une bonne pratique.",
        "Je suis opérationnel et opérationnelle.",
        "J'ai une bonne opérationnalité.",
        "Je suis fonctionnel et fonctionnelle.",
        "J'ai une bonne fonctionnalité.",
        "Je suis efficace et efficace.",
        "J'ai une bonne efficacité.",
        "Je suis efficient et efficiente.",
        "J'ai une bonne efficience.",
        "Je suis productif et productive.",
        "J'ai une bonne productivité.",
        "Je suis performant et performante.",
        "J'ai une bonne performance.",
        "Je suis compétent et compétente.",
        "J'ai une bonne compétence.",
        "Je suis qualifié et qualifiée.",
        "J'ai une bonne qualification.",
        "Je suis expert et experte.",
        "J'ai une bonne expertise.",
        "Je suis spécialiste et spécialiste.",
        "J'ai une bonne spécialisation.",
        "Je suis professionnel et professionnelle.",
        "J'ai une bonne professionnalisme.",
        "Je suis expérimenté et expérimentée.",
        "J'ai une bonne expérience.",
        "Je suis senior et senior.",
        "J'ai une bonne ancienneté.",
        "Je suis junior et junior.",
        "J'ai une bonne juniorité.",
        "Je suis débutant et débutante.",
        "J'ai une bonne débutance.",
        "Je suis stagiaire et stagiaire.",
        "J'ai une bonne stage.",
        "Je suis apprenti et apprentie.",
        "J'ai une bonne apprentissage.",
        "Je suis étudiant et étudiante.",
        "J'ai une bonne étude.",
        "Je suis élève et élève.",
        "J'ai une bonne école.",
        "Je suis professeur et professeure.",
        "J'ai une bonne enseignement.",
        "Je suis formateur et formatrice.",
        "J'ai une bonne formation.",
        "Je suis coach et coach.",
        "J'ai une bonne coaching.",
        "Je suis mentor et mentore.",
        "J'ai une bonne mentorat.",
        "Je suis manager et manager.",
        "J'ai une bonne management.",
        "Je suis leader et leader.",
        "J'ai une bonne leadership.",
        "Je suis directeur et directrice.",
        "J'ai une bonne direction.",
        "Je suis chef et chef.",
        "J'ai une bonne chef.",
        "Je suis responsable et responsable.",
        "J'ai une bonne responsabilité.",
        "Je suis cadre et cadre.",
        "J'ai une bonne cadre.",
        "Je suis employé et employée.",
        "J'ai une bonne emploi.",
        "Je suis salarié et salariée.",
        "J'ai une bonne salaire.",
        "Je suis travailleur et travailleuse.",
        "J'ai une bonne travail.",
        "Je suis collaborateur et collaboratrice.",
        "J'ai une bonne collaboration.",
        "Je suis partenaire et partenaire.",
        "J'ai une bonne partenariat.",
        "Je suis client et cliente.",
        "J'ai une bonne client.",
        "Je suis fournisseur et fournisseuse.",
        "J'ai une bonne fournisseur.",
        "Je suis prestataire et prestataire.",
        "J'ai une bonne prestation.",
        "Je suis sous-traitant et sous-traitante.",
        "J'ai une bonne sous-traitance.",
        "Je suis consultant et consultante.",
        "J'ai une bonne consultation.",
        "Je suis auditeur et auditrice.",
        "J'ai une bonne audit.",
        "Je suis contrôleur et contrôleuse.",
        "J'ai une bonne contrôle.",
        "Je suis inspecteur et inspectrice.",
        "J'ai une bonne inspection.",
        "Je suis analyste et analyste.",
        "J'ai une bonne analyse.",
        "Je suis expert et experte.",
        "J'ai une bonne expertise.",
        "Je suis spécialiste et spécialiste.",
        "J'ai une bonne spécialité.",
        "Je suis technicien et technicienne.",
        "J'ai une bonne technique.",
        "Je suis ingénieur et ingénieure.",
        "J'ai une bonne ingénierie.",
        "Je suis architecte et architecte.",
        "J'ai une bonne architecture.",
        "Je suis designer et designer.",
        "J'ai une bonne design.",
        "Je suis développeur et développeuse.",
        "J'ai une bonne développement.",
        "Je suis programmeur et programmeuse.",
        "J'ai une bonne programmation.",
        "Je suis codé et codée.",
        "J'ai une bonne code.",
        "Je suis testeur et testeuse.",
        "J'ai une bonne test.",
        "Je suis validateur et validatrice.",
        "J'ai une bonne validation.",
        "Je suis vérificateur et vérificatrice.",
        "J'ai une bonne vérification.",
        "Je suis documentaliste et documentaliste.",
        "J'ai une bonne documentation.",
        "Je suis rédacteur et rédactrice.",
        "J'ai une bonne rédaction.",
        "Je suis éditeur et éditrice.",
        "J'ai une bonne édition.",
        "Je suis traducteur et traductrice.",
        "J'ai une bonne traduction.",
        "Je suis interprète et interprète.",
        "J'ai une bonne interprétation.",
        "Je suis communicant et communicante.",
        "J'ai une bonne communication.",
        "Je suis commercial et commerciale.",
        "J'ai une bonne commercial.",
        "Je suis vendeur et vendeuse.",
        "J'ai une bonne vente.",
        "Je suis négociateur et négociatrice.",
        "J'ai une bonne négociation.",
        "Je suis acheteur et acheteuse.",
        "J'ai une bonne achat.",
        "Je suis logisticien et logisticienne.",
        "J'ai une bonne logistique.",
        "Je suis transporteur et transporteuse.",
        "J'ai une bonne transport.",
        "Je suis livreur et livreuse.",
        "J'ai une bonne livraison.",
        "Je suis stockeur et stockeuse.",
        "J'ai une bonne stock.",
        "Je suis entreposeur et entreposeuse.",
        "J'ai une bonne entreposage.",
        "Je suis manutentionnaire et manutentionnaire.",
        "J'ai une bonne manutention.",
        "Je suis magasinier et magasinière.",
        "J'ai une bonne magasin.",
        "Je suis caissier et caissière.",
        "J'ai une bonne caisse.",
        "Je suis hôte et hôtesse.",
        "J'ai une bonne hôte.",
        "Je suis réceptionniste et réceptionniste.",
        "J'ai une bonne réception.",
        "Je suis standardiste et standardiste.",
        "J'ai une bonne standard.",
        "Je suis secrétaire et secrétaire.",
        "J'ai une bonne secrétariat.",
        "Je suis assistant et assistante.",
        "J'ai une bonne assistance.",
        "Je suis adjoint et adjointe.",
        "J'ai une bonne adjoint.",
        "Je suis stagiaire et stagiaire.",
        "J'ai une bonne stage.",
        "Je suis alternant et alternante.",
        "J'ai une bonne alternance.",
        "Je suis apprenti et apprentie.",
        "J'ai une bonne apprentissage.",
        "Je suis bénévole et bénévole.",
        "J'ai une bonne bénévolat.",
        "Je suis volontaire et volontaire.",
        "J'ai une bonne volontariat.",
        "Je suis associé et associée.",
        "J'ai une bonne association.",
        "Je suis membre et membre.",
        "J'ai une bonne membre.",
        "Je suis adhérent et adhérente.",
        "J'ai une bonne adhésion.",
        "Je suis abonné et abonnée.",
        "J'ai une bonne abonnement.",
        "Je suis client et cliente.",
        "J'ai une bonne client.",
        "Je suis usager et usagère.",
        "J'ai une bonne usage.",
        "Je suis utilisateur et utilisatrice.",
        "J'ai une bonne utilisation.",
        "Je suis consommateur et consommatrice.",
        "J'ai une bonne consommation.",
        "Je suis acheteur et acheteuse.",
        "J'ai une bonne achat.",
        "Je suis prospect et prospect.",
        "J'ai une bonne prospect.",
        "Je suis lead et lead.",
        "J'ai une bonne lead.",
        "Je suis contact et contact.",
        "J'ai une bonne contact.",
        "Je suis relation et relation.",
        "J'ai une bonne relation.",
        "Je suis réseau et réseau.",
        "J'ai une bonne réseau.",
        "Je suis connexion et connexion.",
        "J'ai une bonne connexion.",
        "Je suis lien et lien.",
        "J'ai une bonne lien.",
        "Je suis lien et lien.",
        "J'ai une bonne lien.",
    ]
    
    # Générer des exemples pour chaque compétence
    samples_per_comp = num_samples // len(competences)
    
    for comp in competences:
        # 40% single, 20% dual, 15% triple, 15% line, 10% version
        for i in range(samples_per_comp):
            rand = random.random()
            
            if rand < 0.4:
                # Single competence
                context = random.choice(single_contexts)
                text = context.format(comp=comp)
                start = text.find(comp)
                end = start + len(comp)
                entities = [(start, end, "COMPETENCE")]
                training_data.append((text, {"entities": entities}))
                
            elif rand < 0.6:
                # Dual competences
                comp2 = random.choice([c for c in competences if c != comp])
                context = random.choice(dual_contexts)
                text = context.format(comp1=comp, comp2=comp2)
                
                # Trouver les positions exactes
                start1 = text.find(comp)
                end1 = start1 + len(comp)
                start2 = text.find(comp2)
                end2 = start2 + len(comp2)
                
                entities = [(start1, end1, "COMPETENCE"), (start2, end2, "COMPETENCE")]
                training_data.append((text, {"entities": entities}))
                
            elif rand < 0.75:
                # Triple competences
                comp2 = random.choice([c for c in competences if c != comp])
                comp3 = random.choice([c for c in competences if c != comp and c != comp2])
                context = random.choice(triple_contexts)
                text = context.format(comp1=comp, comp2=comp2, comp3=comp3)
                
                # Trouver les positions exactes
                start1 = text.find(comp)
                end1 = start1 + len(comp)
                start2 = text.find(comp2)
                end2 = start2 + len(comp2)
                start3 = text.find(comp3)
                end3 = start3 + len(comp3)
                
                entities = [(start1, end1, "COMPETENCE"), (start2, end2, "COMPETENCE"), (start3, end3, "COMPETENCE")]
                training_data.append((text, {"entities": entities}))
                
            elif rand < 0.9:
                # Line context (compétences sur une même ligne sans séparateurs)
                comp2 = random.choice([c for c in competences if c != comp])
                comp3 = random.choice([c for c in competences if c != comp and c != comp2])
                context = random.choice(line_contexts)
                text = context.format(comp1=comp, comp2=comp2, comp3=comp3)
                
                # Trouver les positions exactes
                start1 = text.find(comp)
                end1 = start1 + len(comp)
                start2 = text.find(comp2)
                end2 = start2 + len(comp2)
                start3 = text.find(comp3)
                end3 = start3 + len(comp3)
                
                entities = [(start1, end1, "COMPETENCE"), (start2, end2, "COMPETENCE"), (start3, end3, "COMPETENCE")]
                training_data.append((text, {"entities": entities}))
                
            else:
                # Version context (compétences avec versions)
                context = random.choice(version_contexts)
                text = context.format(comp=comp)
                start = text.find(comp)
                end = start + len(comp)
                entities = [(start, end, "COMPETENCE")]
                training_data.append((text, {"entities": entities}))
    
    # Ajouter des exemples négatifs (sans entités COMPETENCE)
    for neg_text in negative_examples:
        training_data.append((neg_text, {"entities": []}))
    
    # Mélanger les données
    random.shuffle(training_data)
    
    return training_data

# Générer les données d'entraînement
print("Génération des données d'entraînement...")
training_data = generate_training_data(competences_list, num_samples=2000)
print(f"Génération de {len(training_data)} exemples d'entraînement")

# Afficher quelques exemples
print("\nExemples de données d'entraînement:")
for i in range(min(5, len(training_data))):
    text, entities = training_data[i]
    print(f"Texte: {text}")
    print(f"Entités: {entities['entities']}")
    print()

# Convertir les données au format spaCy
print("Conversion des données au format spaCy...")
examples = []
for text, annotations in training_data:
    doc = nlp.make_doc(text)
    
    # Filtrer les entités qui se chevauchent
    if annotations["entities"]:
        # Convertir les tuples en Span objects pour filter_spans
        spans = []
        for start, end, label in annotations["entities"]:
            span = doc.char_span(start, end, label=label)
            if span is not None:
                spans.append(span)
        
        # Filtrer les spans qui se chevauchent
        filtered_spans = filter_spans(spans)
        
        # Recréer les annotations avec les spans filtrés
        annotations["entities"] = [(span.start_char, span.end_char, span.label_) for span in filtered_spans]
    
    example = Example.from_dict(doc, annotations)
    examples.append(example)

# Diviser en train et validation
split = int(len(examples) * 0.8)
train_examples = examples[:split]
dev_examples = examples[split:]

print(f"Exemples d'entraînement: {len(train_examples)}")
print(f"Exemples de validation: {len(dev_examples)}")

# Désactiver les autres pipes pendant l'entraînement
pipe_exceptions = ["ner"]
unaffected_pipes = [pipe for pipe in nlp.pipe_names if pipe not in pipe_exceptions]

# Entraîner le modèle
print("\nDébut de l'entraînement...")
nlp.disable_pipes(*unaffected_pipes)

optimizer = nlp.initialize()
epochs = 30
batch_size = 32

for epoch in range(epochs):
    random.shuffle(train_examples)
    losses = {}
    
    # Mini-batch training
    batches = minibatch(train_examples, size=compounding(4.0, batch_size, 1.001))
    
    for batch in batches:
        nlp.update(
            batch,
            sgd=optimizer,
            losses=losses,
            drop=0.2  # Dropout pour éviter le surapprentissage
        )
    
    # Évaluer sur le set de validation
    with nlp.select_pipes(enable=["ner"]):
        scores = nlp.evaluate(dev_examples)
    
    print(f"Epoch {epoch + 1}/{epochs} - Loss: {losses['ner']:.4f} - F1: {scores['ents_f']:.4f} - Precision: {scores['ents_p']:.4f} - Recall: {scores['ents_r']:.4f}")

# Réactiver tous les pipes
for pipe in unaffected_pipes:
    nlp.enable_pipe(pipe)

# Sauvegarder le modèle
output_dir = Path("models/competence_ner")
output_dir.mkdir(parents=True, exist_ok=True)
nlp.to_disk(output_dir)

print(f"\nModèle entraîné et sauvegardé dans {output_dir}")

# Tester le modèle sur quelques exemples
print("\nTest du modèle entraîné:")
test_sentences = [
    "Je maîtrise Java, Python et Spring Boot.",
    "Compétences : React, Angular, Vue.js.",
    "J'ai utilisé Docker et Kubernetes pour le déploiement.",
    "Expérience avec TensorFlow et PyTorch pour le machine learning.",
    "Technologies : PostgreSQL, MongoDB, Redis.",
]

nlp_loaded = spacy.load(output_dir)
for sentence in test_sentences:
    doc = nlp_loaded(sentence)
    print(f"\nPhrase: {sentence}")
    print("Compétences détectées:")
    for ent in doc.ents:
        if ent.label_ == "COMPETENCE":
            print(f"  - {ent.text} ({ent.label_})")

print("\nEntraînement terminé avec succès!")
