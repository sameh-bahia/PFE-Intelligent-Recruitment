package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.Candidature;
import com.AppRecrutement.AppRecrutement.repository.CandidatureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatureService {

    @Autowired
    private CandidatureRepository candidatureRepository;

    public List<Candidature> findAll() {
        return candidatureRepository.findAll();
    }

    public Optional<Candidature> findById(Long id) {
        return candidatureRepository.findById(id);
    }

    public Candidature save(Candidature candidature) {
        return candidatureRepository.save(candidature);
    }

    public void deleteById(Long id) {
        candidatureRepository.deleteById(id);
    }

    public List<Candidature> findByCandidatId(Long candidatId) {
        return candidatureRepository.findByCandidatId(candidatId);
    }

    // Méthode ajoutée pour récupérer les candidatures reçues par un recruteur spécifique
    // Utilisée pour que chaque recruteur ne voit que les candidatures de ses propres offres
    public List<Candidature> findByRecruteurId(Long recruteurId) {
        return candidatureRepository.findByRecruteurId(recruteurId);
    }

}
