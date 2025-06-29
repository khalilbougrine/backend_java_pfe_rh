package com.example.monapp.rh.service.interfaces;

import com.example.monapp.rh.model.FichePoste;

import java.util.List;

public interface FichePosteService {
    FichePoste create(FichePoste fiche);
    List<FichePoste> getAll();
    FichePoste getById(Long id);
    FichePoste update(Long id, FichePoste fiche);
    void delete(Long id);
}
