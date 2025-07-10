package com.example.monapp.rh.controller;

import com.example.monapp.rh.model.FicheCandidat;
import com.example.monapp.rh.repository.FicheCandidatRepository;
import com.example.monapp.rh.service.interfaces.FicheCandidatService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
            // Création d’un fichier temporaire pour le fichier reçu
            File originalFile = File.createTempFile("cv_", "_" + file.getOriginalFilename());
            file.transferTo(originalFile);

            // Vérifie si c’est un PDF
            File imageFile;
            if (file.getOriginalFilename().toLowerCase().endsWith(".pdf")) {
                // 🔁 Convertir PDF en PNG (1ère page uniquement)
                PDDocument document = PDDocument.load(originalFile);
                PDFRenderer renderer = new PDFRenderer(document);
                BufferedImage image = renderer.renderImageWithDPI(0, 300); // page 0, résolution 300dpi
                imageFile = File.createTempFile("converted_", ".png");
                ImageIO.write(image, "png", imageFile);
                document.close();
            } else {
                // Sinon, garder le fichier tel quel (image déjà prête)
                imageFile = originalFile;
            }

            // Appel API IA
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(imageFile));

            HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = new RestTemplate().postForEntity("http://69.62.106.98:6600/cv", request, Map.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                Map<String, Object> result = response.getBody();
                result.put("nom_fichier", file.getOriginalFilename());
                result.put("type_fichier", file.getContentType());

                // Upload vers MinIO
                try {
                    HttpHeaders uploadHeaders = new HttpHeaders();
                    uploadHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
                    MultiValueMap<String, Object> uploadBody = new LinkedMultiValueMap<>();
                    uploadBody.add("file", new FileSystemResource(imageFile));

                    String uploadUrl = "http://127.0.0.1:8000/upload-cv-image/?filename=" + file.getOriginalFilename();
                    HttpEntity<MultiValueMap<String, Object>> uploadRequest = new HttpEntity<>(uploadBody, uploadHeaders);

                    ResponseEntity<Map> uploadResponse =
                            new RestTemplate().postForEntity(uploadUrl, uploadRequest, Map.class);

                    if (uploadResponse.getStatusCode().is2xxSuccessful() && uploadResponse.getBody() != null) {
                        String imageUrl = uploadResponse.getBody().get("url").toString();
                        result.put("image_url", imageUrl);
                    } else {
                        System.err.println("❌ Erreur upload MinIO : " + uploadResponse.getStatusCode());
                    }
                } catch (Exception ex) {
                    System.err.println("❌ Exception upload MinIO : " + ex.getMessage());
                }

                // Insertion en base
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
