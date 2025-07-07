package com.example.monapp.rh.service.classes;

import com.example.monapp.rh.model.FicheCandidat;
import com.example.monapp.rh.repository.FicheCandidatRepository;
import com.example.monapp.rh.service.interfaces.FicheCandidatService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

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

    @Override
    public FicheCandidat insertFromMap(Map<String, Object> data) {
        FicheCandidat fiche = new FicheCandidat();

        fiche.setName((String) data.get("name"));
        fiche.setEmail((String) data.get("email"));
        fiche.setPhone((String) data.get("phone"));
        fiche.setAddress((String) data.get("address"));
        fiche.setBirthdate((String) data.get("birthdate"));
        fiche.setLinkedin((String) data.get("linkedin"));
        fiche.setGithub((String) data.get("github"));
        fiche.setResumeTitle((String) data.get("resume_title"));
        fiche.setProfil((String) data.get("profil"));

        fiche.setSkills(toJson(data.get("skills")));
        fiche.setEducation(toJson(data.get("education")));
        fiche.setExperience(toJson(data.get("experience")));
        fiche.setProjects(toJson(data.get("projects")));
        fiche.setCertifications(toJson(data.get("certifications")));
        fiche.setLanguages(toJson(data.get("languages")));
        fiche.setInterests(toJson(data.get("interests")));

        fiche.setImage(Boolean.TRUE.equals(data.get("image")));
        fiche.setNomFichier((String) data.get("nom_fichier"));
        fiche.setTypeFichier((String) data.get("type_fichier"));

// ✅ Ajout du champ image_url si présent dans le JSON
        fiche.setImageUrl((String) data.get("image_url"));


        return repository.save(fiche);
    }

    private String toJson(Object value) {
        try {
            return new ObjectMapper().writeValueAsString(value);
        } catch (Exception e) {
            return "[]";
        }
    }

}
