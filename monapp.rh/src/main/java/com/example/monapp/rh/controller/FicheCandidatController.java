package com.example.monapp.rh.controller;

import com.example.monapp.rh.model.FicheCandidat;
import com.example.monapp.rh.repository.FicheCandidatRepository;
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
@CrossOrigin(origins = "*")
public class FicheCandidatController {

    @Autowired
    private FicheCandidatService ficheCandidatService;

    @Autowired
    private FicheCandidatRepository ficheCandidatRepository;

    @PostMapping
    public FicheCandidat createFiche(@RequestBody FicheCandidat fiche) {
        return ficheCandidatService.createFiche(fiche);
    }

    @GetMapping
    public List<FicheCandidat> getAllFiches() {
        List<FicheCandidat> fiches = ficheCandidatRepository.findAll();

        for (FicheCandidat fiche : fiches) {
            if (fiche.getNomFichier() != null && !fiche.getNomFichier().isEmpty()) {
                fiche.setImageUrl("http://localhost:9001/cv-images/" + fiche.getNomFichier());
            }
        }

        return fiches;
    }

    @GetMapping("/{id}")
    public FicheCandidat getFicheById(@PathVariable Long id) {
        return ficheCandidatService.getFicheById(id);
    }

    @PutMapping("/{id}")
    public FicheCandidat updateFiche(@PathVariable Long id, @RequestBody FicheCandidat fiche) {
        return ficheCandidatService.updateFiche(id, fiche);
    }

    @DeleteMapping("/{id}")
    public void deleteFiche(@PathVariable Long id) {
        ficheCandidatService.deleteFiche(id);
    }

    @PostMapping("/parse-and-save")
    public ResponseEntity<String> handleCvUpload(@RequestParam("file") MultipartFile file) {
        try {
            // Sauvegarde temporaire
            File tempFile = File.createTempFile("cv_", "_" + file.getOriginalFilename());
            file.transferTo(tempFile);

            // Appel API IA (http://69.62.106.98:6600/cv)
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

                // 👉 Upload image sur MinIO via l'API FastAPI
                try {
                    HttpHeaders uploadHeaders = new HttpHeaders();
                    uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                    MultiValueMap<String, Object> uploadBody = new LinkedMultiValueMap<>();
                    uploadBody.add("file", new FileSystemResource(tempFile));

                    HttpEntity<MultiValueMap<String, Object>> uploadRequest =
                            new HttpEntity<>(uploadBody, uploadHeaders);

                    // ✅ Ajout du paramètre filename dans l’URL
                    String uploadUrl = "http://127.0.0.1:8000/upload-cv-image/?filename=" + file.getOriginalFilename();

                    ResponseEntity<Map> uploadResponse =
                            new RestTemplate().postForEntity(uploadUrl, uploadRequest, Map.class);

                    if (uploadResponse.getStatusCode().is2xxSuccessful() && uploadResponse.getBody() != null) {
                        String imageUrl = uploadResponse.getBody().get("url").toString(); // clé = "url" dans ta FastAPI
                        result.put("image_url", imageUrl);
                    } else {
                        System.err.println("❌ Erreur upload MinIO : " + uploadResponse.getStatusCode());
                    }
                } catch (Exception ex) {
                    System.err.println("❌ Exception upload MinIO : " + ex.getMessage());
                }

                // Insère dans MySQL
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
