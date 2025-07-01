package com.example.monapp.rh.controller;

import com.example.monapp.rh.model.FicheCandidat;
import com.example.monapp.rh.service.interfaces.FicheCandidatService;
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
@RequestMapping("/api/fiches")
@CrossOrigin(origins = "*") // Pour l'accès depuis Angular
public class FicheCandidatController {

    @Autowired
    private FicheCandidatService ficheCandidatService;

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

    @PostMapping("/parse-and-save")
    public ResponseEntity<String> handleCvUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Sauvegarde fichier temporaire
            File tempFile = File.createTempFile("cv_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);

            // Appel API IA pour parsing
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(tempFile));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = new RestTemplate().postForEntity("http://69.62.106.98:6600/cv", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = response.getBody();
                result.put("nom_fichier", file.getOriginalFilename());
                result.put("type_fichier", file.getContentType());

                // Appel service Spring pour insertion MySQL
                ficheCandidatService.insertFromMap(result);

                return ResponseEntity.ok("Fichier traité avec succès");
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Erreur API parsing");
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erreur serveur: " + e.getMessage());
        }
    }

}
