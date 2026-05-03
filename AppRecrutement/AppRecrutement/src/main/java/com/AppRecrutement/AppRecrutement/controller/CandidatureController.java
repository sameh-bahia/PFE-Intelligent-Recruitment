package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Candidature;
import com.AppRecrutement.AppRecrutement.model.Offre;
import com.AppRecrutement.AppRecrutement.model.StatutCandidature;
import com.AppRecrutement.AppRecrutement.service.CandidatService;
import com.AppRecrutement.AppRecrutement.service.CandidatureService;
import com.AppRecrutement.AppRecrutement.service.OffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/candidatures")
public class CandidatureController {

    @Autowired
    private CandidatureService candidatureService;

    @Autowired
    private CandidatService candidatService;

    @Autowired
    private OffreService offreService;

    @GetMapping
    public List<Candidature> getAllCandidatures() {
        return candidatureService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Candidature> getCandidatureById(@PathVariable Long id) {
        return candidatureService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Candidature> createCandidature(@RequestBody Map<String, Object> payload) {
        Candidature candidature = new Candidature();
        candidature.setLettreMotivation((String) payload.get("lettreMotivation"));
        candidature.setScoreCompatibilite(((Number) payload.get("scoreCompatibilite")).doubleValue());
        candidature.setStatut(StatutCandidature.valueOf((String) payload.get("statut")));

        Map<String, Object> candidatMap = (Map<String, Object>) payload.get("candidat");
        Long candidatId = ((Number) candidatMap.get("id")).longValue();
        Candidat candidat = candidatService.findById(candidatId)
                .orElseThrow(() -> new RuntimeException("Candidat not found"));
        candidature.setCandidat(candidat);

        Map<String, Object> offreMap = (Map<String, Object>) payload.get("offre");
        Long offreId = ((Number) offreMap.get("id")).longValue();
        Offre offre = offreService.findById(offreId)
                .orElseThrow(() -> new RuntimeException("Offre not found"));
        candidature.setOffre(offre);

        return ResponseEntity.ok(candidatureService.save(candidature));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Candidature> updateCandidature(@PathVariable Long id, @RequestBody Candidature candidature) {
        if (candidatureService.findById(id).isPresent()) {
            candidature.setId(id);
            return ResponseEntity.ok(candidatureService.save(candidature));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCandidature(@PathVariable Long id) {
        if (candidatureService.findById(id).isPresent()) {
            candidatureService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
