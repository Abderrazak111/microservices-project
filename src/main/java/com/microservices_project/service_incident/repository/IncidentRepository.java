package com.microservices_project.service_incident.repository;

import com.microservices_project.service_incident.model.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
}
