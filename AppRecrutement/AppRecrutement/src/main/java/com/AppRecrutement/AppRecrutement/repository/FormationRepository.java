package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Formation;
import com.AppRecrutement.AppRecrutement.model.Candidat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {
    List<Formation> findByCandidatId(Long candidatId);
    
    @Transactional
    void deleteByCandidat(Candidat candidat);
}
