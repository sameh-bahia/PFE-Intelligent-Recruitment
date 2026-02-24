package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Candidature;
import com.AppRecrutement.AppRecrutement.model.StatutCandidature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidatureRepository extends JpaRepository<Candidature, Long> {
    List<Candidature> findByCandidatId(Long candidatId);
    List<Candidature> findByOffreId(Long offreId);
    List<Candidature> findByStatut(StatutCandidature statut);
    List<Candidature> findByOffreIdAndCandidatId(Long offreId, Long candidatId);
    //Cette requête SQL/JPA: "Sélectionne toutes les candidatures où l'offre appartient au recruteur avec l'ID donné"
    // HETHIKA ZEDNEHA BECH KOL RECRUTEURE YAL9A LES CONDIDATURES MTE3OU  MCH LIF FEL BD KOL 
    @Query("SELECT c FROM Candidature c JOIN c.offre o WHERE o.recruteur.id = :recruteurId")
    List<Candidature> findByRecruteurId(@Param("recruteurId") Long recruteurId);
}
