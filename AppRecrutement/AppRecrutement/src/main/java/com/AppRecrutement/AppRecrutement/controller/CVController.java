package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.CV;
import com.AppRecrutement.AppRecrutement.service.CVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cvs")
public class CVController {

    @Autowired
    private CVService cvService;

    @GetMapping
    public List<CV> getAllCVs() {
        return cvService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CV> getCVById(@PathVariable Long id) {
        return cvService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public CV createCV(@RequestBody CV cv) {
        return cvService.save(cv);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CV> updateCV(@PathVariable Long id, @RequestBody CV cv) {
        if (cvService.findById(id).isPresent()) {
            cv.setId(id);
            return ResponseEntity.ok(cvService.save(cv));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCV(@PathVariable Long id) {
        if (cvService.findById(id).isPresent()) {
            cvService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
