package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.service.RecruteurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recruteurs")
public class RecruteurController {

    @Autowired
    private RecruteurService recruteurService;

    @GetMapping
    public List<Recruteur> getAllRecruteurs() {
        return recruteurService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recruteur> getRecruteurById(@PathVariable Long id) {
        return recruteurService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Recruteur createRecruteur(@RequestBody Recruteur recruteur) {
        return recruteurService.save(recruteur);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recruteur> updateRecruteur(@PathVariable Long id, @RequestBody Recruteur recruteur) {
        if (recruteurService.findById(id).isPresent()) {
            recruteur.setId(id);
            return ResponseEntity.ok(recruteurService.save(recruteur));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecruteur(@PathVariable Long id) {
        if (recruteurService.findById(id).isPresent()) {
            recruteurService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
