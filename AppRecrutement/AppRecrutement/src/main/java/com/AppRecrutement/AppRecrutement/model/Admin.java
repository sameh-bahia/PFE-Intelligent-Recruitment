package com.AppRecrutement.AppRecrutement.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends Utilisateur {

    public Admin() {
    }

    public Admin(String email, String motDePasse, String nom, String prenom, Role role) {
        setEmail(email);
        setMotDePasse(motDePasse);
        setNom(nom);
        setPrenom(prenom);
        setRole(role);
    }

    public void gererUtilisateurs() {
    }

    public void modererOffres() {
    }
}
