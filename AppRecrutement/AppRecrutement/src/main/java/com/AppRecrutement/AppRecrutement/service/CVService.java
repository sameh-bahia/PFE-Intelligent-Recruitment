package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.CV;
import com.AppRecrutement.AppRecrutement.repository.CVRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CVService {

    @Autowired
    private CVRepository cvRepository;

    public List<CV> findAll() {
        return cvRepository.findAll();
    }

    public Optional<CV> findById(Long id) {
        return cvRepository.findById(id);
    }

    public CV save(CV cv) {
        return cvRepository.save(cv);
    }

    public void deleteById(Long id) {
        cvRepository.deleteById(id);
    }

}
