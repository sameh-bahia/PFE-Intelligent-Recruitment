package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Recommandation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommandationRepository extends JpaRepository<Recommandation, Long> {
    List<Recommandation> findByCandidatureId(Long candidatureId);
}
