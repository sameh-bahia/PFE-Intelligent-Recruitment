package com.AppRecrutement.AppRecrutement.repository;

import com.AppRecrutement.AppRecrutement.model.OptionReponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionReponseRepository extends JpaRepository<OptionReponse, Long> {
    List<OptionReponse> findByQuestionId(Long questionId);
}
