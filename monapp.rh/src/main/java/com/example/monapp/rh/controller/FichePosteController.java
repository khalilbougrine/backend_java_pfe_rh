package com.example.monapp.rh.controller;

import com.example.monapp.rh.model.FichePoste;
import com.example.monapp.rh.service.interfaces.FichePosteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/postes")
@CrossOrigin(origins = "*")
public class FichePosteController {

    @Autowired
    private FichePosteService service;

    @PostMapping
    public FichePoste create(@RequestBody FichePoste fiche) {
        return service.create(fiche);
    }

    @GetMapping
    public List<FichePoste> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public FichePoste getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public FichePoste update(@PathVariable Long id, @RequestBody FichePoste fiche) {
        return service.update(id, fiche);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFichePoste(@RequestParam("file") MultipartFile file) {
        try {
            String nomFichier = file.getOriginalFilename();
            String typeFichier = file.getContentType();

            service.saveFileOnly(nomFichier, typeFichier);

            return ResponseEntity.ok("Fiche de poste enregistrée.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }
}
