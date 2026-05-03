package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Recommandation;
import com.AppRecrutement.AppRecrutement.service.RecommandationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recommandations")
public class RecommandationController {

    @Autowired
    private RecommandationService recommandationService;

    @GetMapping
    public List<Recommandation> getAllRecommandations() {
        return recommandationService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recommandation> getRecommandationById(@PathVariable Long id) {
        return recommandationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recommandation createRecommandation(@RequestBody Recommandation recommandation) {
        return recommandationService.save(recommandation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recommandation> updateRecommandation(@PathVariable Long id, @RequestBody Recommandation recommandation) {
        if (recommandationService.findById(id).isPresent()) {
            recommandation.setId(id);
            return ResponseEntity.ok(recommandationService.save(recommandation));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecommandation(@PathVariable Long id) {
        if (recommandationService.findById(id).isPresent()) {
            recommandationService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
