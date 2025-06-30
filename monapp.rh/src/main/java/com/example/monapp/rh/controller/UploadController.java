package com.example.monapp.rh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UploadController {

    // Dossier absolu dans ton projet
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + File.separator + "uploads";

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();

        try {
            // Créer le dossier s’il n’existe pas
            File uploadDir = new File(UPLOAD_DIR);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Enregistrer le fichier
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);

            System.out.println("✅ Fichier sauvegardé dans : " + dest.getAbsolutePath());
            return ResponseEntity.ok("Fichier " + fileName + " enregistré !");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Erreur lors de l'enregistrement du fichier.");
        }
    }
}
