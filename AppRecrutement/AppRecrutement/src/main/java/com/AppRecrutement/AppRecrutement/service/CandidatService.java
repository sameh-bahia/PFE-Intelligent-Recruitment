package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidatService {

    @Autowired
    private CandidatRepository candidatRepository;

    public List<Candidat> findAll() {
        return candidatRepository.findAll();
    }

    public Optional<Candidat> findById(Long id) {
        return candidatRepository.findById(id);
    }

    public Candidat save(Candidat candidat) {
        return candidatRepository.save(candidat);
    }

    public void deleteById(Long id) {
        candidatRepository.deleteById(id);
    }

}
