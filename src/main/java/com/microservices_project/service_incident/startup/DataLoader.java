package com.microservices_project.service_incident.startup;

import com.microservices_project.service_incident.model.Statut;
import com.microservices_project.service_incident.repository.StatutRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final StatutRepository statutRepository;

    public DataLoader(StatutRepository statutRepository) {
        this.statutRepository = statutRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Vérifie si la table est vide pour éviter les doublons
        if (statutRepository.count() == 0) {
            statutRepository.save(Statut.builder().code("NOUVEAU").libelle("Nouveau").build());
            statutRepository.save(Statut.builder().code("ASSIGNE").libelle("Assigné").build());
            statutRepository.save(Statut.builder().code("RESOLU").libelle("Résolu").build());
            System.out.println("Statuts initiaux créés !");
        }
    }
}