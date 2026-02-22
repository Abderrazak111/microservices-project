package com.microservices_project.service_incident.repository;

import com.microservices_project.service_incident.model.Statut;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatutRepository extends JpaRepository<Statut, Long> {
    Optional<Statut> findByCode(String code);
}

