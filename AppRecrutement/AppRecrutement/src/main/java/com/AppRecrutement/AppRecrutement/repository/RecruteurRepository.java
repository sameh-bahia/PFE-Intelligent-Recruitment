package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.Recruteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecruteurRepository extends JpaRepository<Recruteur, Long> {
    Recruteur findByEmail(String email);
}
