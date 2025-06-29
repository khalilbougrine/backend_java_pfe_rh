package com.example.monapp.rh.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UploadController {

    @PostMapping("/upload")
    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file) {
        System.out.println("Fichier reçu : " + file.getOriginalFilename());
        return ResponseEntity.ok("Fichier " + file.getOriginalFilename() + " reçu !");
    }
}
