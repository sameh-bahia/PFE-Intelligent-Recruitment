package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecruteurService {

    @Autowired
    private RecruteurRepository recruteurRepository;

    public List<Recruteur> findAll() {
        return recruteurRepository.findAll();
    }

    public Optional<Recruteur> findById(Long id) {
        return recruteurRepository.findById(id);
    }

    public Recruteur save(Recruteur recruteur) {
        return recruteurRepository.save(recruteur);
    }

    public void deleteById(Long id) {
        recruteurRepository.deleteById(id);
    }

}
