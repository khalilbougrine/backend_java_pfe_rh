package com.example.monapp.rh.service;

import com.example.monapp.rh.model.FicheCandidat;

import java.util.List;

public interface FicheCandidatService {
    FicheCandidat createFiche(FicheCandidat fiche);
    List<FicheCandidat> getAllFiches();
    FicheCandidat getFicheById(Long id);
    FicheCandidat updateFiche(Long id, FicheCandidat fiche);
    void deleteFiche(Long id);
}
