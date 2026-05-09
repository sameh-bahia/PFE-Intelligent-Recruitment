package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Competence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetenceRepository extends JpaRepository<Competence, Long> {
    List<Competence> findByCategorie(String categorie);
    List<Competence> findByNomContaining(String nom);
    java.util.Optional<Competence> findByNom(String nom);
}
