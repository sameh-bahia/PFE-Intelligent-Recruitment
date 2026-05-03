package com.AppRecrutement.AppRecrutement.service;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.model.Role;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import com.AppRecrutement.AppRecrutement.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;

    public AuthService(AuthenticationManager authenticationManager,
                        UserDetailsService userDetailsService,
                        JwtUtil jwtUtil,
                        PasswordEncoder passwordEncoder,
                        CandidatRepository candidatRepository,
                        RecruteurRepository recruteurRepository) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.candidatRepository = candidatRepository;
        this.recruteurRepository = recruteurRepository;
    }

    public Map<String, Object> login(String email, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
        );

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        String token = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("email", userDetails.getUsername());
        response.put("role", userDetails.getAuthorities().iterator().next().getAuthority());

        return response;
    }

    public Candidat registerCandidat(Candidat candidat) {
        candidat.setMotDePasse(passwordEncoder.encode(candidat.getMotDePasse()));
        candidat.setRole(Role.CANDIDAT);
        return candidatRepository.save(candidat);
    }

    public Recruteur registerRecruteur(Recruteur recruteur) {
        recruteur.setMotDePasse(passwordEncoder.encode(recruteur.getMotDePasse()));
        recruteur.setRole(Role.RECRUTEUR);
        return recruteurRepository.save(recruteur);
    }
}
