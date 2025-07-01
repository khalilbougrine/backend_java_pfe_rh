package com.example.monapp.rh.service.interfaces;

import com.example.monapp.rh.model.FicheCandidat;

import java.util.List;
import java.util.Map;

public interface FicheCandidatService {
    FicheCandidat createFiche(FicheCandidat fiche);
    List<FicheCandidat> getAllFiches();
    FicheCandidat getFicheById(Long id);
    FicheCandidat updateFiche(Long id, FicheCandidat fiche);
    void deleteFiche(Long id);
    FicheCandidat insertFromMap(Map<String, Object> data);

}
