package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    List<Competence> findByCategorie(String categorie);
    List<Competence> findByNomContaining(String nom);
    java.util.Optional<Competence> findByNom(String nom);
    
    // Supprimer toutes les relations CV-Compétence pour un CV donné
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM cv_competence WHERE cv_id = :cvId", nativeQuery = true)
    void deleteByCvId(@Param("cvId") Long cvId);
}
