package com.AppRecrutement.AppRecrutement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.util.List;

@Entity
@DiscriminatorValue("RECRUTEUR")
public class Recruteur extends Utilisateur {

    private String nomEntreprise;
    private String poste;

    @JsonIgnore
    @OneToMany(mappedBy = "recruteur", cascade = CascadeType.ALL)
    private List<Offre> offres;

    public Recruteur() {
    }

    public Recruteur(String email, String motDePasse, String nom, String prenom, Role role, String nomEntreprise, String poste) {
        setEmail(email);
        setMotDePasse(motDePasse);
        setNom(nom);
        setPrenom(prenom);
        setRole(role);
        this.nomEntreprise = nomEntreprise;
        this.poste = poste;
    }

    public void publierOffre(Offre offre) {
        offre.setRecruteur(this);
        this.offres.add(offre);
    }

    public List<Candidature> consulterCandidatures(Offre offre) {
        return offre.getCandidatures();
    }

    public void deciderCandidature(Candidature c, StatutCandidature s) {
        c.setStatut(s);
    }

    public String getNomEntreprise() {
        return nomEntreprise;
    }

    public void setNomEntreprise(String nomEntreprise) {
        this.nomEntreprise = nomEntreprise;
    }

    public String getPoste() {
        return poste;
    }

    public void setPoste(String poste) {
        this.poste = poste;
    }

    public List<Offre> getOffres() {
        return offres;
    }

    public void setOffres(List<Offre> offres) {
        this.offres = offres;
    }
}
