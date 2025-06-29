package com.example.monapp.rh.service.classes;

import com.example.monapp.rh.model.FicheCandidat;
import com.example.monapp.rh.repository.FicheCandidatRepository;
import com.example.monapp.rh.service.interfaces.FicheCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FicheCandidatServiceImpl implements FicheCandidatService {

    @Autowired
    private FicheCandidatRepository repository;

    @Override
    public FicheCandidat createFiche(FicheCandidat fiche) {
        return repository.save(fiche);
    }

    @Override
    public List<FicheCandidat> getAllFiches() {
        return repository.findAll();
    }

    @Override
    public FicheCandidat getFicheById(Long id) {
        return repository.findById(id).orElse(null);
    }

    @Override
    public FicheCandidat updateFiche(Long id, FicheCandidat fiche) {
        fiche.setId(id);
        return repository.save(fiche);
    }

    @Override
    public void deleteFiche(Long id) {
        repository.deleteById(id);
    }
}
