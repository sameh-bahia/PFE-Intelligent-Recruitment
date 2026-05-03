package com.AppRecrutement.AppRecrutement.security;

import com.AppRecrutement.AppRecrutement.model.Candidat;
import com.AppRecrutement.AppRecrutement.model.Recruteur;
import com.AppRecrutement.AppRecrutement.repository.CandidatRepository;
import com.AppRecrutement.AppRecrutement.repository.RecruteurRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final CandidatRepository candidatRepository;
    private final RecruteurRepository recruteurRepository;

    public UserDetailsServiceImpl(CandidatRepository candidatRepository, RecruteurRepository recruteurRepository) {
        this.candidatRepository = candidatRepository;
        this.recruteurRepository = recruteurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Chercher d'abord dans Candidat
        Candidat candidat = candidatRepository.findByEmail(email);
        if (candidat != null) {
            return new User(
                    candidat.getEmail(),
                    candidat.getMotDePasse(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + candidat.getRole()))
            );
        }

        // Chercher ensuite dans Recruteur
        Recruteur recruteur = recruteurRepository.findByEmail(email);
        if (recruteur != null) {
            return new User(
                    recruteur.getEmail(),
                    recruteur.getMotDePasse(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + recruteur.getRole()))
            );
        }

        throw new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email);
    }
}
