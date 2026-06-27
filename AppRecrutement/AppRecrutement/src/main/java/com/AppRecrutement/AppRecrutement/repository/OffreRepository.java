package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.TypeOffre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OffreRepository extends JpaRepository<Offre, Long> {
    List<Offre> findByRecruteurId(Long recruteurId);
    List<Offre> findByEstOuverteTrue();
    List<Offre> findByLieuContaining(String lieu);
    List<Offre> findByDomaine(String domaine);
    List<Offre> findByTypeOffre(TypeOffre typeOffre);
}
