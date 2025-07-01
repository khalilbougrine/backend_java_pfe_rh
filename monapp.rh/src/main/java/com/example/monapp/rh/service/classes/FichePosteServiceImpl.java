package com.example.monapp.rh.service.classes;

import com.example.monapp.rh.model.FichePoste;
import com.example.monapp.rh.repository.FichePosteRepository;
import com.example.monapp.rh.service.interfaces.FichePosteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import com.example.monapp.rh.service.interfaces.FichePosteService;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Date;
import java.util.List;

@Service
public class FichePosteServiceImpl implements FichePosteService {

    @Autowired
    private FichePosteRepository repository;



    @Override
    public FichePoste create(FichePoste fiche) {
        return repository.save(fiche);
    }

    @Override
    public List<FichePoste> getAll() {
        return repository.findAll();
    }

    @Override
    public FichePoste getById(Long id) {
        return repository.findById(id).orElse(null);
    }



    @Override
    public FichePoste update(Long id, FichePoste fiche) {
        fiche.setId(id);
        return repository.save(fiche);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public FichePoste saveFileOnly(String nomFichier, String typeFichier) {
        FichePoste fiche = new FichePoste();
        fiche.setNomFichier(nomFichier);
        fiche.setTypeFichier(typeFichier);
        fiche.setCreatedAt(new Date());
        return repository.save(fiche); // 🟢 Ici et pas repository.save(...) d’un autre service
    }

}
