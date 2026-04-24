package com.microservices_project.service_incident.controller;

import com.microservices_project.service_incident.model.Incident;
import com.microservices_project.service_incident.service.IncidentService;
import com.microservices_project.service_incident.service.MinioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

    private final IncidentService incidentService;
    private final MinioService minioService;

    public IncidentController(IncidentService incidentService, MinioService minioService) {
        this.incidentService = incidentService;
        this.minioService = minioService;
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

    /**
     * Upload a screenshot for an incident — stores it in MinIO and updates captureUrl.
     */
    @PostMapping("/{id}/capture")
    public ResponseEntity<Incident> uploadCapture(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        String objectKey = minioService.uploadCapture(file);
        String presignedUrl = minioService.getPresignedUrl(objectKey);
        Incident updated = incidentService.updateCaptureUrl(id, presignedUrl);
        return ResponseEntity.ok(updated);
    }

    /**
     * Get a fresh pre-signed URL for the incident's capture.
     */
    @GetMapping("/{id}/capture/url")
    public ResponseEntity<String> getCaptureUrl(@PathVariable Long id) {
        Incident incident = incidentService.getById(id);
        if (incident.getCaptureUrl() == null || incident.getCaptureUrl().isBlank()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(incident.getCaptureUrl());
    }
}
