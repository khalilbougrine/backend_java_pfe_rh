package com.example.monapp.rh.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "fiche_poste")
public class FichePoste {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titrePoste;
    private String description;

    @Column(columnDefinition = "JSON")
    private String competencesRequises;

    @Column(columnDefinition = "JSON")
    private String languesRequises;

    @Column(columnDefinition = "JSON")
    private String formationsRequises;

    private String lieu;
    private String typeContrat;
    private String niveauExperience;

    @Column(columnDefinition = "TEXT")
    private String autresInformations;


    private String nomFichier;
    private String typeFichier;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public String getNomFichier() {
        return nomFichier;
    }

    public void setNomFichier(String nomFichier) {
        this.nomFichier = nomFichier;
    }

    public String getTypeFichier() {
        return typeFichier;
    }

    public void setTypeFichier(String typeFichier) {
        this.typeFichier = typeFichier;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitrePoste() {
        return titrePoste;
    }

    public void setTitrePoste(String titrePoste) {
        this.titrePoste = titrePoste;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompetencesRequises() {
        return competencesRequises;
    }

    public void setCompetencesRequises(String competencesRequises) {
        this.competencesRequises = competencesRequises;
    }

    public String getLanguesRequises() {
        return languesRequises;
    }

    public void setLanguesRequises(String languesRequises) {
        this.languesRequises = languesRequises;
    }

    public String getFormationsRequises() {
        return formationsRequises;
    }

    public void setFormationsRequises(String formationsRequises) {
        this.formationsRequises = formationsRequises;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public String getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(String typeContrat) {
        this.typeContrat = typeContrat;
    }

    public String getNiveauExperience() {
        return niveauExperience;
    }

    public void setNiveauExperience(String niveauExperience) {
        this.niveauExperience = niveauExperience;
    }

    public String getAutresInformations() {
        return autresInformations;
    }

    public void setAutresInformations(String autresInformations) {
        this.autresInformations = autresInformations;
    }
}
