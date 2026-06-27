package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.repository.OffreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OffreService {

    @Autowired
    private OffreRepository offreRepository;

    public List<Offre> findAll() {
        return offreRepository.findAll();
    }

    public Optional<Offre> findById(Long id) {
        return offreRepository.findById(id);
    }

    public Offre save(Offre offre) {
        return offreRepository.save(offre);
    }

    @Transactional
    public void deleteById(Long id) {
        offreRepository.deleteById(id);
    }

    // Méthode ajoutée pour récupérer les offres d'un recruteur spécifique
    // Utilisée pour que chaque recruteur ne voit que ses propres offres
    public List<Offre> findByRecruteurId(Long recruteurId) {
        return offreRepository.findByRecruteurId(recruteurId);
    }

}
