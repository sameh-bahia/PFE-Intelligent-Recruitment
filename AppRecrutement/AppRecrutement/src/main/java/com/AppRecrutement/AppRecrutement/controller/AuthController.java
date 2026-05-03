package com.AppRecrutement.AppRecrutement.controller;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");

        try {
            Map<String, Object> response = authService.login(email, password);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Email ou mot de passe incorrect");
        }
    }

    @PostMapping("/register/candidat")
    public ResponseEntity<?> registerCandidat(@RequestBody Candidat candidat) {
        try {
            Candidat savedCandidat = authService.registerCandidat(candidat);
            return ResponseEntity.ok(savedCandidat);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'inscription du candidat: " + e.getMessage());
        }
    }

    @PostMapping("/register/recruteur")
    public ResponseEntity<?> registerRecruteur(@RequestBody Recruteur recruteur) {
        try {
            Recruteur savedRecruteur = authService.registerRecruteur(recruteur);
            return ResponseEntity.ok(savedRecruteur);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'inscription du recruteur: " + e.getMessage());
        }
    }
}
