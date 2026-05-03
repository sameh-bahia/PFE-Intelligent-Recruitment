package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.Recommandation;
import com.AppRecrutement.AppRecrutement.repository.RecommandationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecommandationService {

    @Autowired
    private RecommandationRepository recommandationRepository;

    public List<Recommandation> findAll() {
        return recommandationRepository.findAll();
    }

    public Optional<Recommandation> findById(Long id) {
        return recommandationRepository.findById(id);
    }

    public Recommandation save(Recommandation recommandation) {
        return recommandationRepository.save(recommandation);
    }

    public void deleteById(Long id) {
        recommandationRepository.deleteById(id);
    }

}
