package com.microservices_project.service_incident.controller;


import com.microservices_project.service_incident.model.Incident;
import com.microservices_project.service_incident.service.IncidentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;

    public IncidentController(IncidentService incidentService) {
        this.incidentService = incidentService;
    }

    @GetMapping
    public List<Incident> getAll() {
        return incidentService.getAll();
    }

    @GetMapping("/{id}")
    public Incident getById(@PathVariable Long id) {
        return incidentService.getById(id);
    }

    @PostMapping
    public Incident create(@RequestBody Incident incident) {
        return incidentService.create(incident);
    }

    @PutMapping("/{id}")
    public Incident update(@PathVariable Long id, @RequestBody Incident incident) {
        return incidentService.update(id, incident);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        incidentService.delete(id);
    }
}

