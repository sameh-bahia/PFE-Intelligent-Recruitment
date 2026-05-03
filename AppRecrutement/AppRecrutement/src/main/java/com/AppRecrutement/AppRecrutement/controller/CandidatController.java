package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.service.CandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidats")
public class CandidatController {

    @Autowired
    private CandidatService candidatService;

    @GetMapping
    public List<Candidat> getAllCandidats() {
        return candidatService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidat> getCandidatById(@PathVariable Long id) {
        return candidatService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Candidat createCandidat(@RequestBody Candidat candidat) {
        return candidatService.save(candidat);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidat> updateCandidat(@PathVariable Long id, @RequestBody Candidat candidat) {
        return candidatService.findById(id)
                .map(existingCandidat -> {
                    existingCandidat.setEmail(candidat.getEmail());
                    existingCandidat.setMotDePasse(candidat.getMotDePasse());
                    existingCandidat.setNom(candidat.getNom());
                    existingCandidat.setPrenom(candidat.getPrenom());
                    existingCandidat.setRole(candidat.getRole());
                    existingCandidat.setTelephone(candidat.getTelephone());
                    existingCandidat.setAdresse(candidat.getAdresse());
                    existingCandidat.setDateNaissance(candidat.getDateNaissance());
                    existingCandidat.setTitreProfil(candidat.getTitreProfil());
                    // dateInscription est préservée par @Column(updatable = false)
                    return ResponseEntity.ok(candidatService.save(existingCandidat));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidat(@PathVariable Long id) {
        if (candidatService.findById(id).isPresent()) {
            candidatService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
