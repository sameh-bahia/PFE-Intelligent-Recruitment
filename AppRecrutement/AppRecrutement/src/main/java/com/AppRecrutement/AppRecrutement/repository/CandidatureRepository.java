package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Candidature;
import com.AppRecrutement.AppRecrutement.model.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByCandidatId(Long candidatId);
    List<Candidature> findByOffreId(Long offreId);
    List<Candidature> findByStatut(StatutCandidature statut);
    List<Candidature> findByOffreIdAndCandidatId(Long offreId, Long candidatId);
}
