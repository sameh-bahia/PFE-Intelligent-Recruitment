package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.TypeOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByRecruteurId(Long recruteurId);
    List<Offre> findByEstOuverteTrue();
    List<Offre> findByLieuContaining(String lieu);
    List<Offre> findByDomaine(String domaine);
    List<Offre> findByTypeOffre(TypeOffre typeOffre);
    
    @Query("SELECT o FROM Offre o LEFT JOIN FETCH o.quiz q LEFT JOIN FETCH q.questions WHERE o.id = :id")
    Optional<Offre> findByIdWithQuiz(@org.springframework.data.repository.query.Param("id") Long id);
}
