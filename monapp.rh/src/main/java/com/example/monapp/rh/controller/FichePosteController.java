package com.example.monapp.rh.controller;

import com.example.monapp.rh.model.FichePoste;
import com.example.monapp.rh.service.interfaces.FichePosteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Map;

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
            // Sauvegarde temporaire
            File tempFile = File.createTempFile("fiche_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);

            // Appel à l'API FastAPI pour l’upload sur MinIO
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            String uploadUrl = "http://127.0.0.1:8000/upload-fiche-poste/?filename=" + file.getOriginalFilename();
            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = new RestTemplate().postForEntity(uploadUrl, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                String nomFichier = file.getOriginalFilename();
                String typeFichier = file.getContentType();
                service.saveFileOnly(nomFichier, typeFichier);
                return ResponseEntity.ok("Fiche de poste enregistrée avec succès.");
            } else {
                return ResponseEntity.status(500).body("Erreur upload MinIO : " + response.getStatusCode());
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur : " + e.getMessage());
        }
    }
}
