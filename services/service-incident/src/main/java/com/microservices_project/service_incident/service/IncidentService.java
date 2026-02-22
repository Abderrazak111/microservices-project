package com.microservices_project.service_incident.service;

import com.microservices_project.service_incident.model.Incident;
import com.microservices_project.service_incident.model.Statut;
import com.microservices_project.service_incident.repository.IncidentRepository;
import com.microservices_project.service_incident.repository.StatutRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncidentService {

    private final IncidentRepository incidentRepository;
    private final StatutRepository statutRepository;

    public IncidentService(IncidentRepository incidentRepository, StatutRepository statutRepository) {
        this.incidentRepository = incidentRepository;
        this.statutRepository = statutRepository;
    }

    public List<Incident> getAll() {
        return incidentRepository.findAll();
    }

    public Incident getById(Long id) {
        return incidentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));
    }

    public Incident create(Incident incident) {
        Statut statutDefaut = statutRepository.findByCode("NOUVEAU")
                .orElseThrow(() -> new RuntimeException("Statut par défaut introuvable"));
        incident.setStatut(statutDefaut);
        return incidentRepository.save(incident);
    }

    public Incident update(Long id, Incident incident) {
        Incident existing = getById(id);
        existing.setTitre(incident.getTitre());
        existing.setDescription(incident.getDescription());
        existing.setStatut(incident.getStatut());
        existing.setPriorite(incident.getPriorite());
        existing.setCaptureUrl(incident.getCaptureUrl());
        return incidentRepository.save(existing);
    }

    public void delete(Long id) {
        incidentRepository.deleteById(id);
    }
}

