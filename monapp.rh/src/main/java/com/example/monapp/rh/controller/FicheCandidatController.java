package com.example.monapp.rh.controller;

import com.example.monapp.rh.model.FicheCandidat;
import com.example.monapp.rh.service.FicheCandidatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fiches")
@CrossOrigin(origins = "*") // Pour l'accès depuis Angular
public class FicheCandidatController {

    @Autowired
    private FicheCandidatService ficheService;

    @PostMapping
    public FicheCandidat createFiche(@RequestBody FicheCandidat fiche) {
        return ficheService.createFiche(fiche);
    }

    @GetMapping
    public List<FicheCandidat> getAllFiches() {
        return ficheService.getAllFiches();
    }

    @GetMapping("/{id}")
    public FicheCandidat getFicheById(@PathVariable Long id) {
        return ficheService.getFicheById(id);
    }

    @PutMapping("/{id}")
    public FicheCandidat updateFiche(@PathVariable Long id, @RequestBody FicheCandidat fiche) {
        return ficheService.updateFiche(id, fiche);
    }

    @DeleteMapping("/{id}")
    public void deleteFiche(@PathVariable Long id) {
        ficheService.deleteFiche(id);
    }
}
